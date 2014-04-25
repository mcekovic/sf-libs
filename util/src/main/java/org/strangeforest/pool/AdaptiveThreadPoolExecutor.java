package org.strangeforest.pool;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

public class AdaptiveThreadPoolExecutor extends AbstractExecutorService {

	private final int corePoolSize;
	private final int maxPoolSize;
	private final BlockingQueue<Runnable> workQueue;
	private final ThreadFactory threadFactory;
	private final RejectedExecutionHandler rejectHandler;
	private volatile int state;
	private List<Worker> workers;
	private volatile long keepAliveTime;
	private volatile boolean threadPreference = true;
	private int pendingWorkers;
	private int peakPoolSize;
	private AtomicInteger taskCount;
	private long completedTaskCount;

	private static final int RUNNING    = 0;
	private static final int SHUTDOWN   = 1;
	private static final int STOP       = 2;
	private static final int TERMINATED = 3;

	public AdaptiveThreadPoolExecutor(int corePoolSize, int maxPoolSize) {
		this(0, corePoolSize, maxPoolSize, 60, TimeUnit.SECONDS, new SynchronousQueue<>(), Executors.defaultThreadFactory(), new AbortPolicy());
	}

	public AdaptiveThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		this(0, corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), new AbortPolicy());
	}

	public AdaptiveThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		this(0, corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory, new AbortPolicy());
	}

	public AdaptiveThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler rejectHandler) {
		this(0, corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectHandler);
	}

	public AdaptiveThreadPoolExecutor(int initPoolSize, int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		this(initPoolSize, corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), new AbortPolicy());
	}

	public AdaptiveThreadPoolExecutor(int initPoolSize, int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler rejectHandler) {
		super();
		this.corePoolSize = corePoolSize;
		this.maxPoolSize = maxPoolSize;
		this.keepAliveTime = unit.toNanos(keepAliveTime);
		this.workQueue = workQueue;
		this.threadFactory = threadFactory;
		this.rejectHandler = rejectHandler;
		workers = new ArrayList<>();
		pendingWorkers = initPoolSize;
		for (int i = 0; i < initPoolSize; i++)
			addWorker(null);
		taskCount = new AtomicInteger();
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public int getMaximumPoolSize() {
		return maxPoolSize;
	}

	public boolean isThreadPreference() {
		return threadPreference;
	}

	public void setThreadPreference(boolean threadPreference) {
		this.threadPreference = threadPreference;
	}

	public void setThreadPreference() {
		setThreadPreference(true);
	}

	public void setQueuePreference() {
		setThreadPreference(false);
	}

	public synchronized int getPoolSize() {
		return workers.size();
	}

	public synchronized int getActiveCount() {
		int count = 0;
		for (Worker worker : workers) {
			if (worker.isActive())
				count++;
		}
		return count;
	}

	public synchronized int getPeakPoolSize() {
		return peakPoolSize;
	}

	public long getTaskCount() {
		return getCompletedTaskCount() + taskCount.get();
	}

	public synchronized long getCompletedTaskCount() {
		long count = completedTaskCount;
		for (Worker worker : workers)
			count += worker.completed;
		return count;
	}

	public BlockingQueue<Runnable> getQueue() {
		return workQueue;
	}

	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return rejectHandler;
	}


	@Override public void execute(Runnable command) {
		if (command == null)
			throw new NullPointerException();
		if (state == RUNNING) {
			if (shouldAddWorkerBeforeEnqueue()) {
				addWorker(command);
				taskCount.incrementAndGet();
				return;
			}
			else if (workQueue.offer(command)) {
				taskCount.incrementAndGet();
				return;
			}
			else if (shouldAddWorker()) {
				addWorker(command);
				taskCount.incrementAndGet();
				return;
			}
		}
		rejectHandler.rejectedExecution(command, this);
	}

	@Override public synchronized void shutdown() {
		if (state < SHUTDOWN)
			state = SHUTDOWN;
		for (Worker worker : workers)
			worker.interruptIfIdle();
		tryTerminate();
	}

	@Override public synchronized List<Runnable> shutdownNow() {
		if (state < STOP)
			state = STOP;
		for (Worker worker : workers)
			worker.interruptNow();
		List<Runnable> tasks = drainQueue();
		tryTerminate();
		return tasks;
	}

	private void tryTerminate() {
		if (state == STOP || state == SHUTDOWN && workers.isEmpty()) {
			state = TERMINATED;
			notifyAll();
		}
	}

	private List<Runnable> drainQueue() {
		List<Runnable> taskList = new ArrayList<>();
		workQueue.drainTo(taskList);
		while (!workQueue.isEmpty()) {
			Iterator<Runnable> it = workQueue.iterator();
			try {
				if (it.hasNext()) {
					Runnable r = it.next();
					if (workQueue.remove(r))
						taskList.add(r);
				}
			}
			catch (ConcurrentModificationException ignored) {}
		}
		return taskList;
	}

	@Override public boolean isShutdown() {
		return state != RUNNING;
	}

	public boolean isTerminating() {
	    return state == SHUTDOWN || state == STOP;
	}

	@Override public boolean isTerminated() {
		return state == TERMINATED;
	}

	@Override public synchronized boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		long t0 = System.currentTimeMillis();
		long millis = unit.toMillis(timeout);
		while (true) {
			if (state == TERMINATED)
				return true;
			long toWait = millis - (System.currentTimeMillis() - t0);
			if (toWait <= 0L)
				return false;
			wait(toWait);
		}
	}

	private synchronized boolean shouldAddWorkerBeforeEnqueue() {
		int workerCount = workers.size() + pendingWorkers;
		return pendingWorker((maxPoolSize == 0 || workerCount < maxPoolSize)
		                  && (threadPreference || workerCount == 0 || workerCount < corePoolSize)
		                  && workerCount <= taskCount.get());
	}

	private synchronized boolean shouldAddWorker() {
		return pendingWorker(maxPoolSize == 0 || workers.size() + pendingWorkers < maxPoolSize);
	}

	private boolean pendingWorker(boolean pending) {
		if (pending)
			pendingWorkers++;
		return pending;
	}

	private void addWorker(Runnable firstTask) {
		Worker worker = new Worker(firstTask);
		Thread thread = threadFactory.newThread(worker);
		if (thread != null) {
			worker.thread = thread;
			doAddWorker(worker);
			thread.start();
		}
	}

	private synchronized void doAddWorker(Worker worker) {
		workers.add(worker);
		pendingWorkers--;
		int workerCount = workers.size();
		if (workerCount > peakPoolSize)
			peakPoolSize = workerCount;
	}

	private final class Worker implements Runnable {

		private Thread thread;
		private final ReentrantLock runLock = new ReentrantLock();
		private Runnable firstTask;
		private volatile long completed;
		private boolean removed;

		private Worker(Runnable firstTask) {
			super();
			this.firstTask = firstTask;
		}

		@Override public void run() {
			try {
				while (!thread.isInterrupted()) {
					Runnable task;
					if (firstTask != null) {
						task = firstTask;
						firstTask = null;
					}
					else
						task = getTask();
					if (task != null)
						runTask(task);
					else if (removeIfNotNeeded(this))
						break;
				}
			}
			catch (InterruptedException ignored) {}
			finally {
				removeWorker(this);
			}
		}

		private void runTask(Runnable task) throws InterruptedException {
			runLock.lockInterruptibly();
			try {
				task.run();
				completed++;
			}
			finally {
				runLock.unlock();
				taskCount.decrementAndGet();
			}
		}

		boolean isActive() {
			return runLock.isLocked();
		}

		void interruptIfIdle() {
			if (runLock.tryLock()) {
				try {
					if (thread != Thread.currentThread())
						thread.interrupt();
				}
				finally {
					runLock.unlock();
				}
			}
		}

		void interruptNow() {
			thread.interrupt();
		}
	}

	private Runnable getTask() throws InterruptedException {
		switch (state) {
			case RUNNING:
				return keepAliveTime > 0L ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) : workQueue.take();
			case SHUTDOWN:
				return workQueue.poll();
			default:
				return null;
		}
	}

	private synchronized boolean removeIfNotNeeded(Worker worker) {
		boolean shouldRemove = shouldRemoveWorker();
		if (shouldRemove)
			removeWorker(worker);
		return shouldRemove;
	}

	private boolean shouldRemoveWorker() {
		switch (state) {
			case RUNNING:
				return workers.size() > corePoolSize && workQueue.isEmpty();
			case SHUTDOWN:
				return workQueue.isEmpty();
			default:
				return true;
		}
	}

	private synchronized void removeWorker(Worker worker) {
		if (!worker.removed)
			doRemoveWorker(worker);
	}

	private void doRemoveWorker(Worker worker) {
		workers.remove(worker);
		completedTaskCount += worker.completed;
		if (workers.isEmpty())
			tryTerminate();
		worker.removed = true;
	}

	public interface RejectedExecutionHandler {
		void rejectedExecution(Runnable command, ExecutorService executor);
	}

	public static class CallerRunsPolicy implements RejectedExecutionHandler {
		@Override public void rejectedExecution(Runnable command, ExecutorService executor) {
			if (!executor.isShutdown())
				command.run();
		}
	}

	public static class AbortPolicy implements RejectedExecutionHandler {
		@Override public void rejectedExecution(Runnable command, ExecutorService executor) {
			throw new RejectedExecutionException();
		}
	}

	public static class DiscardPolicy implements RejectedExecutionHandler {
		@Override public void rejectedExecution(Runnable command, ExecutorService executor) {}
	}
}
