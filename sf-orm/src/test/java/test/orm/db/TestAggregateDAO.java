package test.orm.db;

import java.sql.*;
import javax.sql.*;

import test.orm.*;

import org.strangeforest.db.*;
import org.strangeforest.orm.db.*;

import static org.strangeforest.orm.db.ORMDataHelper.*;

public class TestAggregateDAO extends DBEntityDAO<Long, TestAggregate> {

	public TestAggregateDAO(DataSource dataSource) {
		super(TestAggregate.class, new TestAggregateMapper(), dataSource);
	}

	@Override protected void createDetails(TestAggregate aggregate) {
		context.getRepository(TestEntity.class).create(aggregate.getEntities(), TestEntity.QUERY_FOR_AGGREGATE(aggregate.getId()));
	}

	@Override protected void saveDetails(TestAggregate aggregate, TestAggregate oldAggregate) {
		context.getRepository(TestEntity.class).save(aggregate.getEntities(), oldAggregate != null ? oldAggregate.getEntities() : null, TestEntity.QUERY_FOR_AGGREGATE(aggregate.getId()));
	}

	private static final String[] EXCLUDE_FIELDS = new String[] {"entities", _VERSION};

	@Override protected String[] excludeFromShallowEquals() {
		return EXCLUDE_FIELDS;
	}

	private static class TestAggregateMapper implements AutoGenColumnsEntityMapper<Long, TestAggregate> {

		@Override public Long readId(ResultSet rs) throws SQLException {
			return getLong(rs, "IDTSAggregate");
		}

		@Override public TestAggregate read(ResultSet rs) throws SQLException {
			TestAggregate aggregate = new TestAggregate(readId(rs));
			aggregate.setName(getString(rs, "Name"));
			aggregate.setEntityId(getNullableLong(rs, "IDTSEntity"));
			aggregate.setAggregateId(getNullableLong(rs, "IDTSAggregate_Related"));
			return aggregate;
		}

		@Override public void mapId(Long id, PreparedStatementHelper st) throws SQLException {
			setLong(st, "pIDTSAggregate", id);
		}

		@Override public void map(TestAggregate aggregate, PreparedStatementHelper st, boolean forCreate) throws SQLException {
			if (!forCreate)
				mapId(aggregate.getId(), st);
			setString(st, "pName", aggregate.getName());
			setLongRef(st, "pIDTSEntity", aggregate.getEntityRef());
			setLongRef(st, "pIDTSAggregate_Related", aggregate.getAggregateRef());
		}

		@Override public String[] getAutoGenColumns() {
			return new String[] {"IDTSAggregate"};
		}

		@Override public void readAutoGenColumns(ResultSet rs, TestAggregate entity) throws SQLException {
			entity.setId(rs.getLong(1));
		}
	}
}
