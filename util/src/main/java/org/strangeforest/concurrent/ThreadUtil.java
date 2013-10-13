package org.strangeforest.concurrent;

public abstract class ThreadUtil {

	public static Thread startThread(String name, Runnable runnable) {
		Thread thread = new Thread(runnable, name);
		thread.start();
		return thread;
	}

	public static Thread startDaemonThread(String name, Runnable runnable) {
		Thread thread = new Thread(runnable, name);
		thread.setDaemon(true);
		thread.start();
		return thread;
	}

	public static void runInThread(Runnable runnable) {
		new Thread(runnable).start();
	}

	public static void runInThread(String name, Runnable runnable) {
		new Thread(runnable, name).start();
	}
}