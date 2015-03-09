package org.strangeforest.performance;

import java.io.*;

import org.strangeforest.util.*;

public class PerformanceInfo implements Comparable<PerformanceInfo>, Serializable {

	private final String name;
	private long beforeCount;
	private long afterCount;
	private long totalTime;
	private Long minTime;
	private Long maxTime;

	public PerformanceInfo(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public synchronized long getBeforeCount() {
		return beforeCount;
	}

	public synchronized long getAfterCount() {
		return afterCount;
	}

	public synchronized long getTotalTime() {
		return totalTime;
	}

	public synchronized Long getAvgTime() {
		return afterCount != 0L ? totalTime/afterCount : null;
	}

	public synchronized Long getMinTime() {
		return minTime;
	}

	public synchronized Long getMaxTime() {
		return maxTime;
	}

	public synchronized void before() {
		beforeCount++;
	}

	public synchronized void after(long time) {
		afterCount++;
		totalTime += time;
		if (minTime == null || time < minTime)
			minTime = time;
		if (maxTime == null || time > maxTime)
			maxTime = time;
	}

	@Override public int compareTo(PerformanceInfo info) {
		return -ObjectUtil.compare(totalTime, info.getTotalTime());
	}
}
