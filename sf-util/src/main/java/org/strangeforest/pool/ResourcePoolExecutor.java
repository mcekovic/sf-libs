package org.strangeforest.pool;

import java.util.concurrent.*;

import org.strangeforest.concurrent.*;

abstract class ResourcePoolExecutor {

	private static ScheduledExecutorService executor;
	private static int taskCounter;

	public synchronized static ScheduledFuture<?> schedule(Runnable task, long period) {
		if (executor == null)
			executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Resource Pools Housekeeper", true, true));
		ScheduledFuture<?> schedule = executor.scheduleAtFixedRate(task, period, period, TimeUnit.MILLISECONDS);
		taskCounter++;
		return schedule;
	}

	public synchronized static void cancel(ScheduledFuture<?> schedule) {
		schedule.cancel(true);
		taskCounter--;
		if (taskCounter == 0) {
			executor.shutdown();
			executor = null;
		}
	}
}
