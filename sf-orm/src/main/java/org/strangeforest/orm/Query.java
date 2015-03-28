package org.strangeforest.orm;

import java.io.*;
import java.util.*;

public class Query implements Serializable {

	private final String name;
	private Object[] params;
	private QueryTransformer transformer;
	private String projection;
	private Integer maxCount;
	private Integer fetchSize;
	private boolean cached = true;

	public Query(String name) {
		super();
		this.name = name;
	}

	public Query(String name, Object... params) {
		super();
		this.name = name;
		this.params = params;
	}

	public String getName() {
		return name;
	}

	public Object[] getParams() {
		return params;
	}

	public boolean hasParams() {
		return params != null && params.length > 0;
	}

	public Object getParam(int index) {
		return params[index];
	}

	public Query withParams(Object... params) {
		this.params = params;
		return this;
	}

	public QueryTransformer getTransformer() {
		return transformer;
	}

	public Query withTransformer(QueryTransformer transformer) {
		this.transformer = transformer;
		return this;
	}

	public String getProjection() {
		return projection;
	}

	public Query withProjection(String projection) {
		this.projection = projection;
		cached = false;
		return this;
	}

	public Integer getMaxCount() {
		return maxCount;
	}

	public Query withMaxCount(int maxCount) {
		this.maxCount = maxCount;
		return this;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public Query withFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
		return this;
	}

	public boolean isCached() {
		return cached;
	}

	public Query cached() {
		cached = true;
		return this;
	}

	public Query notCached() {
		cached = false;
		return this;
	}

	public static <I> String asQueryString(I... values) {
		return asQueryString(Arrays.asList(values));
	}

	public static <I> String asQueryString(List<I> values) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (I value : values) {
			if (first)
				first = false;
			else
				sb.append(',');
			if (value instanceof String || value instanceof Enum) {
				sb.append('\'');
				sb.append(value);
				sb.append('\'');
			}
			else
				sb.append(value);
		}
		return sb.toString();
	}

	
	// Object methods

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Query)) return false;
		Query query = (Query)o;
		return name.equals(query.name) && Arrays.equals(params, query.params) && Objects.equals(transformer, query.transformer) && Objects.equals(maxCount, query.maxCount);
	}

	@Override public int hashCode() {
		int hc = name.hashCode();
		if (params != null)
			hc = 31 * hc + Arrays.hashCode(params);
		if (transformer != null)
			hc = 31 * hc + transformer.hashCode();
		if (maxCount != null)
			hc = 31 * hc + maxCount.hashCode();
		return hc;
	}

	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (params != null)
			sb.append(Arrays.toString(params));
		if (projection != null) {
			sb.append(", projection=");
			sb.append(projection);
		}
		if (maxCount != null) {
			sb.append(", maxCount=");
			sb.append(maxCount);
		}
		return sb.toString();
	}
}
