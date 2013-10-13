package test.pool;

import java.util.*;

import org.junit.*;
import org.strangeforest.pool.*;
import org.strangeforest.util.*;

public class ResourcePoolTest {

	@Test
	public void test() throws Exception {
		TestPool pool = new TestPool(2, 2, 5, true);
		pool.setMaxWaitTime(500L);
		pool.setMaxIdleTime(100L);
		pool.setPropertyCycle(200L);
		pool.init();

		PoolableObject obj1 = pool.getObject();
		PoolableObject obj2 = pool.getObject();
		PoolableObject obj3 = pool.getObject();
		PoolableObject obj4 = pool.getObject();
		obj3.close();
		obj4.close();
//		obj4 = null;
//		System.gc();
		Thread.sleep(500L);
		PoolableObject obj5 = pool.getObject();
//		Thread.sleep(100L);
		PoolableObject obj6 = pool.getObject();
		obj6.close();
		printStatistics(pool.getStatistics());
		Assert.assertEquals(pool.getSize(), 4);
		Assert.assertEquals(pool.getStatistics().getBusyCount(), 3);
		Assert.assertEquals(pool.getStatistics().getIdleCount(), 1);
		pool.destroy();
	}

	@Test
	public void testMinIdleCount() throws Exception {
		TestPool pool = new TestPool(2, 2, 5, true);
		pool.setMinIdleCount(1);
		pool.setPropertyCycle(100L);
		pool.init();

		PoolableObject obj1 = pool.getObject();
		PoolableObject obj2 = pool.getObject();
		Assert.assertEquals(pool.getSize(), 2);
		Thread.sleep(200L);
		Assert.assertEquals(pool.getSize(), 3);
		Thread.sleep(200L);
		Assert.assertEquals(pool.getSize(), 3);
		obj1.close();
		obj2.close();
		Assert.assertEquals(pool.getSize(), 3);
		Assert.assertEquals(pool.getStatistics().getIdleCount(), 3);
		pool.destroy();
	}

	@Test
	public void testBusyTimeout() throws Exception {
		final Reference<Boolean> timedOut = new Reference<>(false);
		TestPool pool = new TestPool(2, 2, 5, true);
		pool.setMaxBusyTime(100L);
		pool.setPropertyCycle(50L);
		pool.setLogger(new ResourcePoolLogger() {
			public void logMessage(String message) {}
			public void logError(String message, Throwable th) {
				System.out.println(message);
				timedOut.set(true);
			}
		});
		pool.init();

		PoolableObject obj1 = pool.getObject();
		Thread.sleep(200L);
		obj1.close();
		Assert.assertTrue(timedOut.get());
		pool.destroy();
	}

	private static final int CLIENT_COUNT = 20;

	@Test
	public void testConcurrency() throws Exception {
		TestPool pool = new TestPool(2, 2, 10, false);
		pool.setMaxWaitTime(500L);
		pool.setMaxIdleTime(100L);
		pool.setPropertyCycle(200L);
		pool.init();

		Client[] clients = new Client[CLIENT_COUNT];
		for (int i = 0; i < CLIENT_COUNT; i++) {
			clients[i] = new Client(pool, i);
			clients[i].start();
		}

		for (int i = 0; i < CLIENT_COUNT; i++)
			clients[i].join();

		printStatistics(pool.getStatistics());
		Thread.sleep(1000L);
		printStatistics(pool.getStatistics());
		pool.destroy();
	}

	private class Client extends Thread {

		private TestPool pool;
		private int id;
		private Random rnd;

		private Client(TestPool pool, int id) {
			super();
			this.pool = pool;
			this.id = id;
			rnd = new Random();
		}

		public void run() {
			try {
				System.out.println(id + ":started");
				for (int i = 0; i < 500; i++) {
					try {
						Thread.sleep(rnd.nextInt(1));
					}
					catch (InterruptedException ignored) {}

					PoolableObject obj = pool.getObject();
					try {
						try {
							Thread.sleep(rnd.nextInt(2));
						}
						catch (InterruptedException ignored) {}
					}
					finally {
						obj.close();
					}
				}
				System.out.println(id + ":finished");
			}
			catch (Exception ex) {
				System.out.println(id);
				ex.printStackTrace();
			}
		}
	}

	private static void printStatistics(ResourcePool.Statistics stats) {
		System.out.println("POOL STATISTICS:");
		System.out.println("Size:        " + stats.getSize());
		System.out.println("Busy:        " + stats.getBusyCount());
		System.out.println("Idle:        " + stats.getIdleCount());
		System.out.println("Allocations: " + stats.getAllocationCount());
		System.out.println("Releases:    " + stats.getReleaseCount());
		System.out.println("Gets:        " + stats.getGetCount());
		System.out.println("Returns:     " + stats.getReturnCount());
	}
}
