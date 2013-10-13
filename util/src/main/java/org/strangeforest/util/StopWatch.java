package org.strangeforest.util;

import org.joda.time.*;

public class StopWatch {

	private long startTime = System.nanoTime();

	public Duration time() {
		return new Duration((System.nanoTime() - startTime)/1000000L);
	}

	public Duration reset() {
		long now = System.nanoTime();
		long duration = (now - startTime)/1000000L;
		startTime = now;
		return new Duration(duration);
	}
}
