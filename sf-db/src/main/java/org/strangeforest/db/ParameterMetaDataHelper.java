package org.strangeforest.db;

import java.sql.*;

public class ParameterMetaDataHelper implements ParameterMetaData {

	private PreparedStatementHelper st;
	private ParameterMetaData pmd;

	public ParameterMetaDataHelper(PreparedStatementHelper st, ParameterMetaData pmd) {
		super();
		this.st = st;
		this.pmd = pmd;
	}

	// ParameterMetaData methods

	@Override public int getParameterCount() throws SQLException {
		return pmd.getParameterCount();
	}

	@Override public int isNullable(int param) throws SQLException {
		return pmd.isNullable(param);
	}

	@Override public boolean isSigned(int param) throws SQLException {
		return pmd.isSigned(param);
	}

	@Override public int getPrecision(int param) throws SQLException {
		return pmd.getPrecision(param);
	}

	@Override public int getScale(int param) throws SQLException {
		return pmd.getScale(param);
	}

	@Override public int getParameterType(int param) throws SQLException {
		return pmd.getParameterType(param);
	}

	@Override public String getParameterTypeName(int param) throws SQLException {
		return pmd.getParameterTypeName(param);
	}

	@Override public String getParameterClassName(int param) throws SQLException {
		return pmd.getParameterClassName(param);
	}

	@Override public int getParameterMode(int param) throws SQLException {
		return pmd.getParameterMode(param);
	}

	@Override public <T> T unwrap(Class<T> iface) throws SQLException {
		return iface.isInterface() && iface.isInstance(this) ? (T)this : pmd.unwrap(iface);
	}

	@Override public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return (iface.isInterface() && iface.isInstance(this)) || pmd.isWrapperFor(iface);
	}


	// Helper methods

	public int isNullable(String paramName) throws SQLException {
		return pmd.isNullable(st.getParamIndex(paramName));
	}

	public boolean isSigned(String paramName) throws SQLException {
		return pmd.isSigned(st.getParamIndex(paramName));
	}

	public int getPrecision(String paramName) throws SQLException {
		return pmd.getPrecision(st.getParamIndex(paramName));
	}

	public int getScale(String paramName) throws SQLException {
		return pmd.getScale(st.getParamIndex(paramName));
	}

	public int getParameterType(String paramName) throws SQLException {
		return pmd.getParameterType(st.getParamIndex(paramName));
	}

	public String getParameterTypeName(String paramName) throws SQLException {
		return pmd.getParameterTypeName(st.getParamIndex(paramName));
	}

	public String getParameterClassName(String paramName) throws SQLException {
		return pmd.getParameterClassName(st.getParamIndex(paramName));
	}

	public int getParameterMode(String paramName) throws SQLException {
		return pmd.getParameterMode(st.getParamIndex(paramName));
	}
}
