package org.strangeforest.concurrent;

import java.util.concurrent.*;

/** FixedRateScheduler is a convenience class for fixed rate scheduling of <i>Runnable</i> tasks which can
 * transparently reschedule the task when scheduling period is changed.
 */
public class FixedRateScheduler {

	private final Runnable runnable;
	private long period;
	private ScheduledExecutorService executor;
	private boolean ownExecutor;
	private String threadName;
	private ScheduledFuture future;

	public FixedRateScheduler(Runnable runnable, long period) {
		this(runnable, period, (String)null);
	}

	public FixedRateScheduler(Runnable runnable, long period, String threadName) {
		super();
		this.runnable = runnable;
		this.period = period;
		this.threadName = threadName;
	}

	public FixedRateScheduler(Runnable runnable, long period, ScheduledExecutorService executor) {
		super();
		this.runnable = runnable;
		this.period = period;
		this.executor = executor;
	}

	public synchronized long getPeriod() {
		return period;
	}

	public synchronized void setPeriod(long period) {
		if (period != this.period) {
			boolean wasScheduled = isScheduled();
			cancel();
			this.period = period;
			if (wasScheduled)
				schedule();
		}
	}

	public synchronized ScheduledExecutorService getExecutor() {
		return executor;
	}

	public synchronized void setExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	public synchronized void schedule() {
		if (period > 0L)
			future = doGetExecutor().scheduleAtFixedRate(runnable, period, period, TimeUnit.MILLISECONDS);
	}

	public synchronized void cancel() {
		if (future != null) {
			future.cancel(false);
			future = null;
		}
	}

	public synchronized boolean isScheduled() {
		return future != null;
	}

	public synchronized void shutdown() {
		cancel();
		if (ownExecutor)
			executor.shutdown();
	}

	private ScheduledExecutorService doGetExecutor() {
		if (executor == null) {
			if (threadName != null)
				executor = Executors.newSingleThreadScheduledExecutor(NamedThreadFactory.newSingleThreadFactory(threadName));
			else
				executor = Executors.newSingleThreadScheduledExecutor();
			ownExecutor = true;
		}
		return executor;
	}
}
