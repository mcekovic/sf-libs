package org.strangeforest.orm.db;

import java.io.*;
import javax.sql.*;

import org.strangeforest.db.gateway.*;

public class ORMDBDAO {

	protected final ORMDBGateway db;

	protected ORMDBDAO(DataSource dataSource, Class objClass) {
		super();
		db = createDBGateway(dataSource, getSQLs(getClass(), objClass));
	}

	protected ORMDBDAO(DataSource dataSource, Class objClass, Class daoClass) {
		super();
		db = createDBGateway(dataSource, getSQLs(daoClass, objClass));
	}

	protected ORMDBGateway createDBGateway(DataSource dataSource, SQLs sqls) {
		return new ORMDBGateway(dataSource, sqls);
	}

	private SQLs getSQLs(Class daoClass, Class objClass) {
		String sqlsName = objClass.getSimpleName() + ".sqls";
		InputStream in = daoClass.getResourceAsStream(sqlsName);
		if (in != null) {
			try {
				return new SQLs(in);
			}
			finally {
				try {
					in.close();
				}
				catch (IOException ignored) {}
			}
		}
		else
			throw new DBException("Cannot find SQLs: " + sqlsName);
	}

	public boolean hasProfile(String profile) {
		return db.hasProfile(profile);
	}

	public void setProfiles(String profiles) {
		db.setProfiles(profiles);
	}
}
