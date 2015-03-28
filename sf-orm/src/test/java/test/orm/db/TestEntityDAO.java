package test.orm.db;

import java.sql.*;
import javax.sql.*;

import test.orm.*;

import org.strangeforest.db.*;
import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.db.*;

import static org.strangeforest.orm.db.ORMDataHelper.*;

public class TestEntityDAO extends DBEntityDAO<Long, TestEntity> {

	private final DBDetailDAO<Long, TestDetail> detailDAO;

	public TestEntityDAO(DataSource dataSource) {
		super(TestEntity.class, new TestEntityMapper(), dataSource);
		detailDAO = new DBDetailDAO<>(TestDetail.class.getSimpleName(), new TestDetailMapper(), dataSource, this);
		registerSaveOptimizer("Optimized", new OptimizedTestEntityMapper(), new String[] {"name", "details"});
	}

	@Override protected void createDetails(TestEntity entity) {
		detailDAO.create(entity.getId(), entity.getDetails());
	}

	@Override protected void saveDetails(TestEntity entity, TestEntity oldEntity) {
		detailDAO.save(entity.getId(), entity.getDetails(), oldEntity != null ? oldEntity.getDetails() : null);
	}

	@Override protected void deleteDetails(TestEntity entity) {
		detailDAO.delete(entity.getId());
	}

	private static class TestEntityMapper implements AutoGenColumnsEntityMapper<Long, TestEntity> {

		@Override public Long readId(ResultSet rs) throws SQLException {
			return getLong(rs, "IDTSEntity");
		}

		@Override public TestEntity read(ResultSet rs) throws SQLException {
			TestEntity entity = new TestEntity(readId(rs));
			entity.setAggregateId(getLong(rs, "IDTSAggregate"));
			entity.setName(getString(rs, "Name"));
			entity.setDescription(getString(rs, "Description"));
			return entity;
		}

		@Override public void mapId(Long id, PreparedStatementHelper st) throws SQLException {
			setLong(st, "pIDTSEntity", id);
		}

		@Override public void map(TestEntity entity, PreparedStatementHelper st, boolean forCreate) throws SQLException {
			if (!forCreate)
				mapId(entity.getId(), st);
			setLongRef(st, "pIDTSAggregate", entity.getAggregateRef());
			setString(st, "pName", entity.getName());
			setString(st, "pDescription", entity.getDescription());
		}

		@Override public String[] getAutoGenColumns() {
			return new String[] {"IDTSEntity"};
		}

		@Override public void readAutoGenColumns(ResultSet rs, TestEntity entity) throws SQLException {
			entity.setId(rs.getLong(1));
		}
	}

	private static class OptimizedTestEntityMapper extends TestEntityMapper {
		@Override public void map(TestEntity entity, PreparedStatementHelper st, boolean forCreate) throws SQLException {
			mapId(entity.getId(), st);
			setString(st, "pName", entity.getName());
		}
	}

	public static class TestDetailMapper implements ObjectMapper<TestDetail> {

		@Override public TestDetail read(ResultSet rs) throws SQLException {
			TestDetail detail = new TestDetail(getInt(rs, "DetailID"));
			detail.setName(getString(rs, "Name"));
			return detail;
		}

		@Override public void map(TestDetail detail, PreparedStatementHelper st) throws SQLException {
			setInt(st, "pDetailID", detail.getDetailId());
			setString(st, "pName", detail.getName());
		}
	}
}
