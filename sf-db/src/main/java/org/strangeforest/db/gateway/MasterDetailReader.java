package org.strangeforest.db.gateway;

import java.sql.*;

public abstract class MasterDetailReader<M, D> implements ObjectReader<M> {

	private final ObjectReader<M> masterReader;
	private final ObjectReader<D> detailReader;

	private M master;

	protected MasterDetailReader(ObjectReader<M> masterReader, ObjectReader<D> detailReader) {
		super();
		this.masterReader = masterReader;
		this.detailReader = detailReader;
	}

	@Override public M read(ResultSet rs) throws SQLException {
		boolean newMaster = master == null || !isSameMaster(master, rs);
		if (newMaster)
			master = masterReader.read(rs);
		D detail = detailReader.read(rs);
		if (detail != null)
			addDetail(master, detail);
		return newMaster ? master : null;
	}

	protected abstract boolean isSameMaster(M master, ResultSet rs) throws SQLException;
	protected abstract void addDetail(M master, D detail);
}
