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

	private SQLs getSQLs(String sqlsLocation) {
		Class cls = getClass();
		sqlsLocation = sqlsLocation != null ? sqlsLocation : (cls.getSimpleName() + ".sqls");
		InputStream in = cls.getResourceAsStream(sqlsLocation);
		if (in != null)
			return new SQLs(in);
		else
			throw new DBException("Cannot find SQLs: " + sqlsLocation);
	}

	public boolean hasProfile(String profile) {
		return db.hasProfile(profile);
	}

	public void setProfiles(String profiles) {
		db.setProfiles(profiles);
	}
}