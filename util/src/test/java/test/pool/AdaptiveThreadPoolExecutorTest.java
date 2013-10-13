package test.pool;

import java.util.*;
import java.util.concurrent.*;

import org.junit.*;
import org.strangeforest.pool.*;
import org.strangeforest.util.*;

import static junit.framework.TestCase.*;

public class AdaptiveThreadPoolExecutorTest {

	private static final Expected[] EXPECTED_QUEUE_PREF_BOUNDED = new Expected[] {
		new Expected(0, 0, 0, 0, 0),
		new Expected(1, 1, 0, 0, 1),
		new Expected(2, 1, 1, 0, 1),
		new Expected(3, 1, 1, 1, 1),
		new Expected(4, 1, 1, 2, 1),
		new Expected(5, 1, 2, 2, 2),
		new Expected(5, 1, 2, 2, 2),
		new Expected(5, 3, 2, 0, 2),
		new Expected(5, 5, 0, 0, 2),
		new Expected(6, 6, 0, 0, 2),
		new Expected(6, 6, 0, 0, 0)
	};

	private static final Expected[] EXPECTED_QUEUE_PREF_UNBOUNDED = new Expected[] {
		new Expected(0, 0, 0, 0, 0),
		new Expected(1, 1, 0, 0, 1),
		new Expected(2, 1, 1, 0, 1),
		new Expected(3, 1, 1, 1, 1),
		new Expected(4, 1, 1, 2, 1),
		new Expected(5, 1, 1, 3, 1),
		new Expected(6, 1, 1, 4, 1),
		new Expected(6, 2, 1, 3, 1),
		new Expected(6, 3, 1, 2, 1),
		new Expected(7, 3, 1, 3, 1),
		new Expected(7, 7, 0, 0, 0)
	};

	private static final Expected[] EXPECTED_THREAD_PREF_BOUNDED = new Expected[] {
		new Expected(0, 0, 0, 0, 0),
		new Expected(1, 1, 0, 0, 1),
		new Expected(2, 1, 1, 0, 1),
		new Expected(3, 1, 2, 0, 2),
		new Expected(4, 1, 2, 1, 2),
		new Expected(5, 1, 2, 2, 2),
		new Expected(5, 1, 2, 2, 2),
		new Expected(5, 3, 2, 0, 2),
		new Expected(5, 5, 0, 0, 2),
		new Expected(6, 6, 0, 0, 2),
		new Expected(6, 6, 0, 0, 0)
	};

	private static final Expected[] EXPECTED_THREAD_PREF_UNBOUNDED = new Expected[] {
		new Expected(0, 0, 0, 0, 0),
		new Expected(1, 1, 0, 0, 1),
		new Expected(2, 1, 1, 0, 1),
		new Expected(3, 1, 2, 0, 2),
		new Expected(4, 1, 2, 1, 2),
		new Expected(5, 1, 2, 2, 2),
		new Expected(6, 1, 2, 3, 2),
		new Expected(6, 3, 2, 1, 2),
		new Expected(6, 5, 1, 0, 2),
		new Expected(7, 6, 1, 0, 2),
		new Expected(7, 7, 0, 0, 0)
	};

	private static final Expected[] EXPECTED_SYNCHRONOUS = new Expected[] {
		new Expected(0, 0, 0, 0, 0),
		new Expected(1, 1, 0, 0, 1),
		new Expected(2, 1, 1, 0, 1),
		new Expected(3, 1, 2, 0, 2),
		new Expected(4, 1, 3, 0, 3),
		new Expected(5, 1, 4, 0, 4),
		new Expected(5, 1, 4, 0, 4),
		new Expected(5, 5, 0, 0, 4),
		new Expected(5, 5, 0, 0, 4),
		new Expected(6, 6, 0, 0, 4),
		new Expected(6, 6, 0, 0, 0)
	};

	private static final Expected[] EXPECTED_KEEP_ALIVE = new Expected[] {
		new Expected(0, 0, 0, 0, 0),
		new Expected(3, 3, 0, 0, 1),
		new Expected(3, 3, 0, 0, 0)
	};

	@Test
	public void testQueuePreferenceBounded() throws InterruptedException {
		AdaptiveThreadPoolExecutor executor = new AdaptiveThreadPoolExecutor(1, 2, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(2));
		executor.setQueuePreference();
		testExecutor(executor, EXPECTED_QUEUE_PREF_BOUNDED);
	}

	@Test
	public void testQueuePreferenceUnbounded() throws InterruptedException {
		AdaptiveThreadPoolExecutor executor = new AdaptiveThreadPoolExecutor(1, 2, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
		executor.setQueuePreference();
		testExecutor(executor, EXPECTED_QUEUE_PREF_UNBOUNDED);
	}

	@Test
	public void testQueuePreferenceSync() throws InterruptedException {
		AdaptiveThreadPoolExecutor executor = new AdaptiveThreadPoolExecutor(1, 4, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());
		executor.setQueuePreference();
		testExecutor(executor, EXPECTED_SYNCHRONOUS);
	}

	@Test
	public void testQueuePreferenceKeepAlive() throws InterruptedException {
		AdaptiveThreadPoolExecutor executor = new AdaptiveThreadPoolExecutor(1, 2, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1));
		executor.setQueuePreference();
		testExecutorKeepAlive(executor, EXPECTED_KEEP_ALIVE);
	}

	@Test
	public void testThreadPreferenceBounded() throws InterruptedException {
		ExecutorService executor = new AdaptiveThreadPoolExecutor(1, 2, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(2));
		testExecutor(executor, EXPECTED_THREAD_PREF_BOUNDED);
	}

	@Test
	public void testThreadPreferenceUnbounded() throws InterruptedException {
		ExecutorService executor = new AdaptiveThreadPoolExecutor(1, 2, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
		testExecutor(executor, EXPECTED_THREAD_PREF_UNBOUNDED);
	}

	@Test
	public void testThreadPreferenceSync() throws InterruptedException {
		ExecutorService executor = new ThreadPoolExecutor(1, 4, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());
		testExecutor(executor, EXPECTED_SYNCHRONOUS);
	}

	@Test
	public void testThreadPreferenceKeepAlive() throws InterruptedException {
		ExecutorService executor = new AdaptiveThreadPoolExecutor(1, 2, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1));
		testExecutorKeepAlive(executor, EXPECTED_KEEP_ALIVE);
	}

	private void testExecutor(ExecutorService executor, Expected[] expected) throws InterruptedException {
		long t0 = System.currentTimeMillis();
		expected[0].assertExecutor(executor, t0);

		executor.execute(new WorkingRunnable(1, 0, t0));
		TimeUnit.MILLISECONDS.sleep(20);
		expected[1].assertExecutor(executor, t0);

		executor.execute(new WorkingRunnable(2, 100, t0));
		TimeUnit.MILLISECONDS.sleep(10);
		expected[2].assertExecutor(executor, t0);

		executor.execute(new WorkingRunnable(3, 100, t0));
		TimeUnit.MILLISECONDS.sleep(10);
		expected[3].assertExecutor(executor, t0);

		executor.execute(new WorkingRunnable(4, 100, t0));
		TimeUnit.MILLISECONDS.sleep(10);
		expected[4].assertExecutor(executor, t0);

		executor.execute(new WorkingRunnable(5, 100, t0));
		TimeUnit.MILLISECONDS.sleep(10);
		expected[5].assertExecutor(executor, t0);

		try {
			executor.execute(new WorkingRunnable(6, 100, t0));
		}
		catch (RejectedExecutionException ignored) {}
		expected[6].assertExecutor(executor, t0);

		TimeUnit.MILLISECONDS.sleep(140);
		expected[7].assertExecutor(executor, t0);

		TimeUnit.MILLISECONDS.sleep(70);
		expected[8].assertExecutor(executor, t0);

		executor.execute(new WorkingRunnable(7, 0, t0));
		TimeUnit.MILLISECONDS.sleep(20);
		expected[9].assertExecutor(executor, t0);

		executor.shutdown();
		assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
		expected[10].assertExecutor(executor, t0);
	}

	private void testExecutorKeepAlive(ExecutorService executor, Expected[] expected) throws InterruptedException {
		long t0 = System.currentTimeMillis();
		expected[0].assertExecutor(executor, t0);

		executor.execute(new WorkingRunnable(1, 0, t0));
		executor.execute(new WorkingRunnable(1, 0, t0));
		executor.execute(new WorkingRunnable(1, 0, t0));
		TimeUnit.MILLISECONDS.sleep(1050);
		expected[1].assertExecutor(executor, t0);

		executor.shutdown();
		assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
		expected[2].assertExecutor(executor, t0);
	}

	private static class Expected {

		final long taskCount, completedCount;
		final int activeCount, queueSize, poolSize;

		private Expected(long taskCount, long completedCount, int activeCount, int queueSize, int poolSize) {
			this.taskCount = taskCount;
			this.completedCount = completedCount;
			this.activeCount = activeCount;
			this.queueSize = queueSize;
			this.poolSize = poolSize;
		}

		private void assertExecutor(Executor executor, long t0) {
			System.out.println((System.currentTimeMillis() - t0) + ": Asserting");
			assertEquals(BeanUtil.getProperty(executor, "taskCount"), taskCount);
			assertEquals(BeanUtil.getProperty(executor, "completedTaskCount"), completedCount);
			assertEquals(BeanUtil.getProperty(executor, "activeCount"), activeCount);
			assertEquals(((Queue)BeanUtil.getProperty(executor, "queue")).size(), queueSize);
			assertEquals(BeanUtil.getProperty(executor, "poolSize"), poolSize);
		}
	}

	private class WorkingRunnable implements Runnable {

		private final int id;
		private final int timeout;
		private final long t0;

		public WorkingRunnable(int id, int timeout, long t0) {
			this.id = id;
			this.timeout = timeout;
			this.t0 = t0;
		}

		public void run() {
			System.out.println((System.currentTimeMillis() - t0) + ": Start " + id);
			try {
				TimeUnit.MILLISECONDS.sleep(timeout);
				System.out.println((System.currentTimeMillis() - t0) + ": Stop " + id);
			}
			catch (InterruptedException ex) {
				System.out.println((System.currentTimeMillis() - t0) + ": Interrupted " + id);
			}
		}
	}

	@Test @Ignore
	public void testRandomWorkload() throws InterruptedException {
		int maxPoolSize = 50;
		int corePoolSize = 10;
		int taskCount = 10000;
		AdaptiveThreadPoolExecutor executor = new AdaptiveThreadPoolExecutor(corePoolSize, maxPoolSize, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
//		executor.setQueuePreference();
		final Random rnd = new Random();
		int queueSize = 0;
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < taskCount; i++) {
			Thread.sleep(rnd.nextInt(2));
//			System.out.printf("%d: %d %d %d%n", System.currentTimeMillis()-t0, i, executor.getPoolSize(), executor.getQueue().size());
			executor.execute(new Runnable() {
				public void run() {
					try {
						Thread.sleep(rnd.nextInt(10));
					}
					catch (InterruptedException ignored) {}
				}
			});
			queueSize = Math.max(queueSize, executor.getQueue().size());
		}
		System.out.println("Pool size: " + executor.getPoolSize());
		System.out.println("Peak pool size: " + executor.getPeakPoolSize());
		System.out.println("Queue size: " + executor.getQueue().size());
		System.out.println("Peak queue size: " + queueSize);
		System.out.println("Time per task: " + (System.currentTimeMillis() - t0) * 1.0 / taskCount);
		assertTrue(executor.getPoolSize() >= corePoolSize);
		assertTrue(executor.getPoolSize() <= maxPoolSize);
		TimeUnit.SECONDS.sleep(1);
		assertEquals(executor.getTaskCount(), taskCount);
		assertEquals(executor.getCompletedTaskCount(), taskCount);
		TimeUnit.SECONDS.sleep(2);
		assertEquals(executor.getPoolSize(), corePoolSize);
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.SECONDS);
		assertEquals(executor.getPoolSize(), 0);
	}
}
