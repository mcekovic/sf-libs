package org.strangeforest.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * ThreadFactory that adds meaningful names for Threads.
 */
public class NamedThreadFactory implements ThreadFactory {

	public static ThreadFactory newThreadFactory(String name) {
		return new NamedThreadFactory(name);
	}

	public static ThreadFactory newSingleThreadFactory(String name) {
		return new NamedThreadFactory(name, false, true);
	}

	private final String name;
	private final boolean daemon;
	private AtomicInteger threadNumber;

	public NamedThreadFactory(String name) {
		this(name, false, false);
	}

	public NamedThreadFactory(String name, boolean daemon, boolean single) {
		super();
		this.name = name;
		this.daemon = daemon;
		if (!single)
			threadNumber = new AtomicInteger(1);
	}

	public String getName() {
		return name;
	}

	public boolean isDaemon() {
		return daemon;
	}

	@Override public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(runnable, getThreadName());
		thread.setDaemon(daemon);
		return thread;
	}

	private String getThreadName() {
		return threadNumber != null ? name + "-" + threadNumber.getAndIncrement() : name;
	}
}
