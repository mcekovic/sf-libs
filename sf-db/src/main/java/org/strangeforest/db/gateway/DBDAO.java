package org.strangeforest.db.gateway;

import java.io.*;
import javax.sql.*;

public abstract class DBDAO {

	protected final DBGateway db;

	protected DBDAO(DataSource dataSource) {
		this(dataSource, null);
	}

	protected DBDAO(DataSource dataSource, String sqlsName) {
		super();
		db = new DBGateway(dataSource, getSQLs(sqlsName));
	}

	private SQLs getSQLs(String sqlsName) {
		Class cls = getClass();
		return new SQLs(cls, sqlsName != null ? sqlsName : (cls.getSimpleName() + ".sqls"));
	}

	public boolean hasProfile(String profile) {
		return db.hasProfile(profile);
	}

	public void setProfiles(String profiles) {
		db.setProfiles(profiles);
	}
}