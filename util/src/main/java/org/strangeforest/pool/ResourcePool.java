package org.strangeforest.pool;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>ResourcePool is base abstract class the implements resource (object) pooling.</p>
 * <p>Implementation of ResourceManager is responsible for allocating, releasing and testing resources.
 * Note that ResourceManager methods are never called from within synchronized blocks
 * (except from initPool/destroyPool methods), thus maximizing scalability.
 * <p>ResourcePool has following properties:</p>
 * <ul><li><i>InitialPoolSize</i> - Size of pool after initialization with init() method.</li>
 * <li><i>MinPoolSize</i> - Idle resources will not be released when pool size is equal or less than MinPoolSize.</li>
 * <li><i>MaxPoolSize</i> - Maximum size of pool. When pool reach maximum size no new resources will be allocated. If zero there is no limit on pool size.</li>
 * <li><i>MinIdleCount</i> - Minimum count of idle resources that are kept allocated and in the pool ready to be served.</li>
 * <li><i>MaxPendingCount</i> - Maximum count of resources that are being allocated concurrently. If zero there is no limit.</li>
 * <li><i>CheckTime</i> - If resource is not being checked for more then CheckTime it will be checked by checkResource method before returning to client by getResource method. Negative value indicates checking is disabled.</li>
 * <li><i>MaxWaitTime</i> - When the pool has reached the maximum size, it waits MaxWaitTime for a free resource before throwing a PoolException. Value of 0 indicates wait forever. Negative value indicates that exception is thrown without waiting.</li>
 * <li><i>MaxIdleTime</i> - After being idle more than MaxIdleTime resources will be released and removed from pool (depending on MinPoolSize parameter).</li>
 * <li><i>MaxBusyTime</i> - Resources held by client more than MaxBusyTime will be released and removed from pool. A value of 0 indicates there is no limit.</li>
 * <li><i>MaxLiveTime</i> - Maximum time a resource can live MaxLiveTime after allocation resource will be released. A value of 0 indicates resource will never be released.</li>
 * <li><i>PropertyCycle</i> - Period of maintenance task scheduling.</li></ul>
 */
public class ResourcePool<R> {

	public static final int DEFAULT_INITIAL_POOL_SIZE =     0;
	public static final int DEFAULT_MIN_POOL_SIZE =         1;
	public static final int DEFAULT_MAX_POOL_SIZE =         0;
	public static final int DEFAULT_MIN_IDLE_COUNT =        0;
	public static final int DEFAULT_MAX_PENDING_COUNT =     0;
	public static final long DEFAULT_CHECK_TIME =          60*1000L;
	public static final long DEFAULT_MAX_WAIT_TIME =        0*1000L;
	public static final long DEFAULT_MAX_IDLE_TIME =     5*60*1000L;
	public static final long DEFAULT_MAX_BUSY_TIME =    60*60*1000L;
	public static final long DEFAULT_MAX_LIVE_TIME = 24*60*60*1000L;
	public static final long DEFAULT_PROPERTY_CYCLE =      60*1000L;

	private ResourceManager<R> manager;
	private Deque<PooledResource<R>> idlePool;
	private Map<R, PooledResource<R>> busyPool;
	private Set<PooledResource<R>> dirtyPool;
	private ScheduledFuture housekeeperFuture;
	private ResourcePoolLogger logger;

	private int initialPoolSize, minPoolSize, maxPoolSize, minIdleCount, maxPendingCount;
	private long checkTime, maxWaitTime, maxIdleTime, maxBusyTime, maxLiveTime, propertyCycle;
	private long initTime, peakPoolTime;
	private int peakPoolSize, cntAllocs, cntReleases, cntPending;
	private long totalAllocTime, totalReleaseTime, totalWaitTime;
	private long cntGets, cntReturns, cntChecks;
	private int cntFailedAllocs, cntFailedChecks, cntFailedGets, cntBusyTimeouts;

	public ResourcePool() {
		this(DEFAULT_INITIAL_POOL_SIZE, DEFAULT_MIN_POOL_SIZE, DEFAULT_MAX_POOL_SIZE);
	}

	public ResourcePool(int initialPoolSize, int minPoolSize, int maxPoolSize) {
		this(initialPoolSize, minPoolSize, maxPoolSize, DEFAULT_MIN_IDLE_COUNT, DEFAULT_MAX_PENDING_COUNT,
			  DEFAULT_CHECK_TIME, DEFAULT_MAX_WAIT_TIME, DEFAULT_MAX_IDLE_TIME, DEFAULT_MAX_BUSY_TIME, DEFAULT_MAX_LIVE_TIME, DEFAULT_PROPERTY_CYCLE);
	}

	public ResourcePool(int initialPoolSize, int minPoolSize, int maxPoolSize, int minIdleCount, int maxPendingCount,
	                    long checkTime, long maxWaitTime, long maxIdleTime, long maxBusyTime, long maxLiveTime, long propertyCycle) {
		super();
		this.initialPoolSize = initialPoolSize;
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.minIdleCount = minIdleCount;
		this.maxPendingCount = maxPendingCount;
		this.checkTime = checkTime;
		this.maxWaitTime = maxWaitTime;
		this.maxIdleTime = maxIdleTime;
		this.maxBusyTime = maxBusyTime;
		this.maxLiveTime = maxLiveTime;
		this.propertyCycle = propertyCycle;
	}

	public synchronized ResourceManager<R> getResourceManager() {
		return manager;
	}

	public synchronized void setResourceManager(ResourceManager<R> manager) {
		this.manager = manager;
	}

	/**
	 * Initializes the pool and allocates specified <tt>initialPoolSize</tt> resources.
	 */
	public synchronized void init() {
		checkIfNotInitialized();
		checkResourceManager();
		initTime = System.currentTimeMillis();
		idlePool = new LinkedList<>();
		busyPool = new HashMap<>(maxPoolSize);
		dirtyPool = new HashSet<>();
		scheduleHousekeeper();
		preallocate(initialPoolSize);
	}

	protected void preallocate(int count) {
		for (int i = 0; i < count; i++) {
			synchronized (this) {
				if (maxPoolSize > 0 && getSize() + cntPending >= maxPoolSize)
					return;
				cntPending++;
			}
			long t0 = System.currentTimeMillis();
			R resource;
			try {
				resource = manager.allocateResource();
			}
			catch (Throwable th) {
				synchronized (this) {
					cntPending--;
					cntFailedAllocs++;
				}
				throw th instanceof PoolException ? (PoolException)th : new PoolException("Error allocating resource.", th);
			}
			synchronized (this) {
				long now = System.currentTimeMillis();
				cntPending--;
				cntAllocs++;
				totalAllocTime += now - t0;
				idlePool.addLast(new PooledResource<>(resource));
				int size = getSize();
				if (size > peakPoolSize) {
					peakPoolSize = size;
					peakPoolTime = now;
				}
			}
		}
	}

	/**
	 * Releases all allocated resources and destroys the pool.
	 */
	public synchronized void destroy() {
		if (isInitialized()) {
			cancelHousekeeper();
			releaseResources(false);
			idlePool = null;
			busyPool = null;
			dirtyPool = null;
		}
	}

	private void scheduleHousekeeper() {
		housekeeperFuture = ResourcePoolExecutor.schedule(new Housekeeper(), propertyCycle);
	}

	private void cancelHousekeeper() {
		if (housekeeperFuture != null) {
			ResourcePoolExecutor.cancel(housekeeperFuture);
			housekeeperFuture = null;
		}
	}

	protected boolean isInitialized() {
		return idlePool != null;
	}

	private void checkIfInitialized() {
		if (idlePool == null)
			throw new IllegalStateException("Pool is not initialized.");
	}

	private void checkIfNotInitialized() {
		if (idlePool != null)
			throw new IllegalStateException("Pool is already initialized.");
	}

	private void checkResourceManager() {
		if (manager == null)
			throw new IllegalStateException("ResourceManager is not set.");
	}


	// Resource management

	public R getResource() {
		long t0 = 0L;
		PooledResource<R> pooledRes;
		while (true) {
			boolean needsChecking;
			synchronized (this) {
				checkIfInitialized();
				if (idlePool.isEmpty()) {
					if ((maxPoolSize > 0 && busyPool.size() + cntPending >= maxPoolSize)
					 || (maxPendingCount > 0 && cntPending >= maxPendingCount)) {
						if (t0 == 0L)
							t0 = System.currentTimeMillis();
						long toWait;
						if (maxWaitTime == 0L)
							toWait = 0L;
						else {
							toWait = maxWaitTime-(System.currentTimeMillis()-t0);
							if (toWait <= 0L || maxWaitTime < 0L) {
								cntFailedGets++;
								throw new PoolException("Maximum number of resources already allocated.");
							}
						}
						try {
							wait(toWait);
						}
						catch (InterruptedException ignored) {}
						continue;
					}
					else {
						cntPending++;
						break;
					}
				}
				else {
					pooledRes = idlePool.removeFirst();
					busyPool.put(pooledRes.resource, pooledRes);
					long now = System.currentTimeMillis();
					needsChecking = checkTime >= 0 && pooledRes.checkTime + checkTime <= now;
					if (needsChecking)
						pooledRes.checkTime = now;
					else {
						cntGets++;
						if (t0 != 0L)
							totalWaitTime += now - t0;
					}
					pooledRes.getTime = now;
				}
			}
			if (!needsChecking)
				return pooledRes.resource;
			else {
				if (manager.checkResource(pooledRes.resource)) {
					synchronized (this) {
						cntChecks++;
						cntGets++;
						totalWaitTime += System.currentTimeMillis() - (t0 != 0L ? t0 : pooledRes.checkTime);
					}
					return pooledRes.resource;
				}
				else {
					synchronized (this) {
						cntFailedChecks++;
						busyPool.remove(pooledRes.resource);
						dirtyPool.add(pooledRes);
						notify();
					}
				}
			}
		}
		// Allocate new resource
		R resource;
		long t1 = System.currentTimeMillis();
		try {
			resource = manager.allocateResource();
		}
		catch (Throwable th) {
			synchronized (this) {
				cntPending--;
				cntFailedAllocs++;
				cntFailedGets++;
				notify();
			}
			throw th instanceof PoolException ? (PoolException)th : new PoolException("Error allocating resource.", th);
		}
		pooledRes = new PooledResource<>(resource);
		synchronized (this) {
			long now = System.currentTimeMillis();
			cntPending--;
			cntAllocs++;
			totalAllocTime += now - t1;
			pooledRes.getTime = now;
			busyPool.put(resource, pooledRes);
			int size = getSize();
			if (size > peakPoolSize) {
				peakPoolSize = size;
				peakPoolTime = now;
			}
			cntGets++;
			totalWaitTime += now - (t0 != 0L ? t0 : t1);
			notify();
		}
		return resource;
	}

	public synchronized void returnResource(R returned, boolean toEnd) {
		checkIfInitialized();
		PooledResource<R> pooledRes = busyPool.remove(returned);
		if (pooledRes == null)
			return;
		pooledRes.returnTime = System.currentTimeMillis();
		cntReturns++;
		if (!pooledRes.isDirty) {
			if (toEnd)
				idlePool.addLast(pooledRes);
			else
				idlePool.addFirst(pooledRes);
		}
		else
			dirtyPool.add(pooledRes);
		notify();
	}

	public synchronized void removeResource(R dirty, boolean synchronously) {
		checkIfInitialized();
		PooledResource<R> pooledRes = busyPool.remove(dirty);
		if (pooledRes == null)
			return;
		if (synchronously)
			doReleaseResource(pooledRes);
		else
			dirtyPool.add(pooledRes);
		notify();
	}

	protected List<PooledResource<R>> getPooledResources() {
		List<PooledResource<R>> resources = new ArrayList<>();
		synchronized (this) {
			resources.addAll(busyPool.values());
			resources.addAll(idlePool);
		}
		return resources;
	}

	private void doReleaseResource(PooledResource<R> pooledRes) {
		long t0 = System.currentTimeMillis();
		try {
			manager.releaseResource(pooledRes.resource);
		}
		catch (Throwable ignored) {}
		synchronized (this) {
			cntReleases++;
			totalReleaseTime += System.currentTimeMillis() - t0;
		}
	}

	/**
	 * Returns pool size (number of allocated resources).
	 * @return number of allocated resources.
	 */
	public synchronized int getSize() {
		return idlePool.size() + busyPool.size();
	}

	/**
	 * Returns peak pool size (peak number of allocated resources).
	 * @return peak number of allocated resources.
	 */
	public synchronized int getPeakSize() {
		return peakPoolSize;
	}

	/**
	 * Returns statistics information as a consistent snapshot of current pool state.
	 * @return statistics information.
	 */
	public synchronized Statistics<R> getStatistics() {
		return new Statistics<>(this);
	}

	/**
	 * Resets statistics information.
	 */
	public synchronized void resetStatistics() {
		peakPoolSize = cntAllocs = cntReleases = 0;
		peakPoolTime = totalAllocTime = totalReleaseTime = totalWaitTime = 0L;
		cntGets = cntReturns = cntChecks = 0L;
		cntFailedAllocs = cntFailedChecks = cntFailedGets = cntBusyTimeouts = 0;
	}


	// Housekeeping methods

	private void releaseIdleTimedOutDeadAndDirtyResources() {
		List<PooledResource<R>> forRemoval = new ArrayList<>();
		synchronized (this) {
			checkIfInitialized();
			long now = System.currentTimeMillis();
			for (Iterator<PooledResource<R>> idleResources = idlePool.iterator(); idleResources.hasNext();) {
				PooledResource<R> pooledRes = idleResources.next();
				if (isIdleTimedout(pooledRes, now) || isLiveTimedout(pooledRes, now)) {
					idleResources.remove();
					forRemoval.add(pooledRes);
				}
			}
			for (Iterator<PooledResource<R>> busyResources = busyPool.values().iterator(); busyResources.hasNext(); ) {
				PooledResource<R> pooledRes = busyResources.next();
				if (maxBusyTime > 0 && pooledRes.getTime + maxBusyTime <= now) {
					busyResources.remove();
					forRemoval.add(pooledRes);
					cntBusyTimeouts++;
					notify();
					logError("Resource maxBusyTime violated: " + pooledRes, null);
				}
				else if (maxLiveTime > 0 && pooledRes.allocTime + maxLiveTime <= now)
					pooledRes.isDirty = true;
			}
			forRemoval.addAll(dirtyPool);
			dirtyPool.clear();
		}
		for (PooledResource<R> pooledRes : forRemoval)
			doReleaseResource(pooledRes);
	}

	private void preallocate() {
		int count = 0;
		synchronized (this) {
			checkIfInitialized();
			int idleSize = idlePool.size();
			if (idleSize < minIdleCount)
				count = minIdleCount - idleSize;
		}
		if (count > 0)
			preallocate(count);
	}

	protected void releaseResources(boolean graceful) {
		List<PooledResource<R>> forRemoval = new ArrayList<>();
		synchronized (this) {
			for (Iterator<PooledResource<R>> idleResources = idlePool.iterator(); idleResources.hasNext(); ) {
				PooledResource<R> pooledRes = idleResources.next();
				idleResources.remove();
				forRemoval.add(pooledRes);
			}
			for (Iterator<PooledResource<R>> busyResources = busyPool.values().iterator(); busyResources.hasNext(); ) {
				PooledResource<R> pooledRes = busyResources.next();
				if (graceful)
					pooledRes.isDirty = true;
				else {
					busyResources.remove();
					forRemoval.add(pooledRes);
					notify();
				}
			}
			forRemoval.addAll(dirtyPool);
			dirtyPool.clear();
		}
		for (PooledResource<R> pooledRes : forRemoval)
			doReleaseResource(pooledRes);
	}

	private boolean isLiveTimedout(PooledResource<R> pooledRes, long now) {
		return (maxLiveTime > 0 && pooledRes.allocTime + maxLiveTime <= now);
	}

	private boolean isIdleTimedout(PooledResource<R> pooledRes, long now) {
		return maxIdleTime > 0 && getSize() > minPoolSize && pooledRes.returnTime + maxIdleTime <= now && idlePool.size() > minIdleCount;
	}


	// Logging

	public synchronized ResourcePoolLogger getLogger() {
		return logger;
	}

	public synchronized void setLogger(ResourcePoolLogger logger) {
		this.logger = logger;
	}

	public final synchronized void logMessage(String message) {
		if (logger != null)
			logger.logMessage(message);
	}

	public final synchronized void logError(String message, Throwable th) {
		if (logger != null)
			logger.logError(message, th);
	}


	// Configuration methods

	public synchronized int getInitialPoolSize() {
		return initialPoolSize;
	}

	public void setInitialPoolSize(int initialPoolSize) {
		int count = 0;
		synchronized (this) {
			this.initialPoolSize = initialPoolSize;
			if (isInitialized()) {
				int size = getSize() + cntPending;
				if (size < initialPoolSize)
					count = initialPoolSize - size;
			}
		}
		if (count > 0)
			preallocate(count);
	}

	public synchronized int getMinPoolSize() {
		return minPoolSize;
	}

	public synchronized void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public synchronized int getMaxPoolSize() {
		return maxPoolSize;
	}

	public synchronized void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		if (maxPoolSize < minPoolSize)
			minPoolSize = maxPoolSize;
		notifyAll();
	}

	public synchronized int getMinIdleCount() {
		return minIdleCount;
	}

	public synchronized void setMinIdleCount(int minIdleCount) {
		this.minIdleCount = minIdleCount;
	}

	public synchronized int getMaxPendingCount() {
		return maxPendingCount;
	}

	public synchronized void setMaxPendingCount(int maxPendingCount) {
		this.maxPendingCount = maxPendingCount;
		notifyAll();
	}

	public synchronized long getCheckTime() {
		return checkTime;
	}

	public synchronized void setCheckTime(long checkTime) {
		this.checkTime = checkTime;
	}

	public synchronized long getMaxWaitTime() {
		return maxWaitTime;
	}

	public synchronized void setMaxWaitTime(long maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public synchronized long getMaxIdleTime() {
		return maxIdleTime;
	}

	public synchronized void setMaxIdleTime(long maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	public synchronized long getMaxBusyTime() {
		return maxBusyTime;
	}

	public synchronized void setMaxBusyTime(long maxBusyTime) {
		this.maxBusyTime = maxBusyTime;
	}

	public synchronized long getMaxLiveTime() {
		return maxLiveTime;
	}

	public synchronized void setMaxLiveTime(long maxLiveTime) {
		this.maxLiveTime = maxLiveTime;
	}

	public synchronized long getPropertyCycle() {
		return propertyCycle;
	}

	public synchronized void setPropertyCycle(long propertyCycle) {
		if (propertyCycle != this.propertyCycle) {
			this.propertyCycle = propertyCycle;
			if (isInitialized()) {
				cancelHousekeeper();
				scheduleHousekeeper();
			}
		}
	}


	// Helper classes

	private class Housekeeper implements Runnable {
		@Override public void run() {
			try {
				releaseIdleTimedOutDeadAndDirtyResources();
				preallocate();
			}
			catch (Throwable th) {
				logError("Exception in ResourcePool housekeeper.", th);
			}
		}
	}

	/**
	 * <p>Pooled resource holder.</p>
	 */
	public static final class PooledResource<R> {

		private final R resource;
		private long allocTime, checkTime, getTime, returnTime;
		private boolean isDirty;

		private PooledResource(R resource) {
			super();
			this.resource = resource;
			allocTime = System.currentTimeMillis();
			checkTime = allocTime;
			getTime = allocTime;
			returnTime = allocTime;
		}

		public R resource() {
			return resource;
		}

		private Date allocationTime() {
			return new Date(allocTime);
		}

		private Date lastCheckTime() {
			return checkTime != allocTime ? new Date(checkTime) : null;
		}

		private Date lastGetTime() {
			return getTime != allocTime || returnTime != allocTime ? new Date(getTime) : null;
		}

		private Date lastReturnTime() {
			return returnTime != allocTime ? new Date(returnTime) : null;
		}

		@Override public String toString() {
			return resource.toString();
		}
	}

	/**
	 * <p>Provides resource pool statistics information.</p>
	 */
	public static class Statistics<R> implements Serializable {

		private Date init, peakTime;
		private int size, peak, busy, idle;
		private int allocs, releases, pending;
		private long allocTime, releaseTime, waitTime;
		private long gets, returns, checks;
		private int failedAllocs, failedChecks, failedGets, busyTimeouts;
		private List<ResourceInfo> info;

		protected Statistics(ResourcePool<R> pool) {
			super();
			init = new Date(pool.initTime);
			size = pool.getSize();
			peak = pool.peakPoolSize;
			peakTime = pool.peakPoolTime != 0L ? new Date(pool.peakPoolTime) : null;
			busy = pool.busyPool.size();
			idle = pool.idlePool.size();
			allocs = pool.cntAllocs;
			releases = pool.cntReleases;
			pending = pool.cntPending;
			allocTime = allocs != 0 ? pool.totalAllocTime/allocs : 0L;
			releaseTime = releases != 0 ? pool.totalReleaseTime/releases : 0L;
			gets = pool.cntGets;
			returns = pool.cntReturns;
			checks = pool.cntChecks;
			waitTime = gets != 0L ? pool.totalWaitTime/gets : 0L;
			failedAllocs = pool.cntFailedAllocs;
			failedChecks = pool.cntFailedChecks;
			failedGets = pool.cntFailedGets;
			busyTimeouts = pool.cntBusyTimeouts;

			info = new ArrayList<>(size);
			for (PooledResource<R> pooledRes : pool.busyPool.values())
				info.add(createResourceInfo(pooledRes, true));
			for (PooledResource<R> pooledRes : pool.idlePool)
				info.add(createResourceInfo(pooledRes, false));
		}

		public Date getInitializationTime() {
			return init;
		}

		public int getSize() {
			return size;
		}

		public int getPeakSize() {
			return peak;
		}

		public Date getPeakTime() {
			return peakTime;
		}

		public int getBusyCount() {
			return busy;
		}

		public int getIdleCount() {
			return idle;
		}

		public int getAllocationCount() {
			return allocs;
		}

		public int getReleaseCount() {
			return releases;
		}

		public int getPendingCount() {
			return pending;
		}

		public long getAllocationTime() {
			return allocTime;
		}

		public long getReleaseTime() {
			return releaseTime;
		}

		public long getWaitTime() {
			return waitTime;
		}

		public long getGetCount() {
			return gets;
		}

		public long getReturnCount() {
			return returns;
		}

		public double getEfficiency() {
			return gets > 0 ? ((double)(gets-allocs))/gets : 0.0;
		}

		public long getCheckCount() {
			return checks;
		}

		public double getCheckRatio() {
			long cachedGets = gets - allocs;
			return cachedGets > 0 ? ((double)(checks))/cachedGets : 0.0;
		}

		public int getFailedAllocationCount() {
			return failedAllocs;
		}

		public int getFailedCheckCount() {
			return failedChecks;
		}

		public int getFailedGetCount() {
			return failedGets;
		}

		public int getBusyTimeoutCount() {
			return busyTimeouts;
		}

		/**
		 * Returns <tt>List</tt> of <tt>ResourceInfo</tt> objects.
		 * @return <tt>List</tt> of <tt>ResourceInfo</tt> objects.
		 */
		public List<ResourceInfo> getResourceInfos() {
			return info;
		}

		protected ResourceInfo createResourceInfo(PooledResource<R> pooledRes, boolean isBusy) {
			return new ResourceInfo(pooledRes, isBusy);
		}
	}

	/**
	 * <p>Resource descriptor.</p>
	 */
	public static class ResourceInfo implements Serializable {

		private Date allocTime, checkTime, getTime, returnTime;
		private boolean isBusy;
		private String description;

		protected <R> ResourceInfo(PooledResource<R> pooledRes, boolean isBusy) {
			super();
			allocTime = pooledRes.allocationTime();
			checkTime = pooledRes.lastCheckTime();
			getTime = pooledRes.lastGetTime();
			returnTime = pooledRes.lastReturnTime();
			this.isBusy = isBusy;
			description = pooledRes.toString();
		}

		public Date getAllocationTime() {
			return allocTime;
		}

		public Date getLastCheckTime() {
			return checkTime;
		}

		public Date getLastGetTime() {
			return getTime;
		}

		public Date getLastReturnTime() {
			return returnTime;
		}

		public boolean isBusy() {
			return isBusy;
		}

		public String getDescription() {
			return description;
		}
	}
}
