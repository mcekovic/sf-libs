package test.orm.db;

import java.sql.*;
import javax.sql.*;

import test.orm.*;

import org.strangeforest.db.*;
import org.strangeforest.orm.db.*;

import static org.strangeforest.orm.db.ORMDataHelper.*;

public class SimpleTestEntityDAO extends DBEntityDAO<Long, SimpleTestEntity> {

	public SimpleTestEntityDAO(DataSource dataSource) {
		super(SimpleTestEntity.class, new SimpleTestEntityMapper(), dataSource);
	}

	private static class SimpleTestEntityMapper implements AutoGenColumnsEntityMapper<Long, SimpleTestEntity> {

		@Override public Long readId(ResultSet rs) throws SQLException {
			return getLong(rs, "IDTSSimpleEntity");
		}

		@Override public SimpleTestEntity read(ResultSet rs) throws SQLException {
			SimpleTestEntity entity = new SimpleTestEntity(readId(rs));
			entity.setName(getString(rs, "Name"));
			return entity;
		}

		@Override public void mapId(Long id, PreparedStatementHelper st) throws SQLException {
			setLong(st, "pIDTSSimpleEntity", id);
		}

		@Override public void map(SimpleTestEntity entity, PreparedStatementHelper st, boolean forCreate) throws SQLException {
			if (!forCreate)
				mapId(entity.getId(), st);
			setString(st, "pName", entity.getName());
		}

		@Override public String[] getAutoGenColumns() {
			return new String[] {"IDTSSimpleEntity"};
		}

		@Override public void readAutoGenColumns(ResultSet rs, SimpleTestEntity entity) throws SQLException {
			entity.setId(rs.getLong(1));
		}
	}
}
