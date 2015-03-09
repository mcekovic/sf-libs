package org.strangeforest.util;

import java.time.*;

public class StopWatch {

	private long startTime = System.nanoTime();

	public Duration time() {
		return Duration.ofNanos(System.nanoTime() - startTime);
	}

	public Duration reset() {
		long now = System.nanoTime();
		long duration = (now - startTime)/1000000L;
		startTime = now;
		return Duration.ofNanos(duration);
	}
}
