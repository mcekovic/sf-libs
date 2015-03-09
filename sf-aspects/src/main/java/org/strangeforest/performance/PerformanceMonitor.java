package org.strangeforest.performance;

import java.util.*;

public class PerformanceMonitor {

	// Factory

	private static PerformanceMonitor instance = new PerformanceMonitor();

	public static PerformanceMonitor instance() {
		return instance;
	}


	// Instance

	private Map<String, PerformanceInfo> infoMap = new HashMap<>();

	public synchronized List<PerformanceInfo> getPerformanceInfos() {
		ArrayList<PerformanceInfo> infos = new ArrayList<>(infoMap.values());
		Collections.sort(infos);
		return infos;
	}

	public synchronized PerformanceInfo getPerformanceInfo(String name) {
		PerformanceInfo info = infoMap.get(name);
		if (info == null) {
			info = new PerformanceInfo(name);
			infoMap.put(name, info);
		}
		return info;
	}

	public synchronized void reset() {
		infoMap.clear();
	}
}
