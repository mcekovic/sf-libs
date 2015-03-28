package org.strangeforest.orm.db;

import java.sql.*;
import java.time.*;

import org.strangeforest.db.*;
import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.*;

public abstract class QueryStatementPreparer {

	public static StatementPreparer createStatementPreparer(final Query query) {
		if (query.hasParams() || query.getMaxCount() != null) {
			return st -> {
				QueryTransformer transformer = query.getTransformer();
				if (transformer instanceof DBQueryTransformer)
					st.setPoolable(((DBQueryTransformer)transformer).isStatementPoolable());
				Integer fetchSize = query.getFetchSize();
				if (fetchSize != null)
					st.setFetchSize(fetchSize);
				int paramIndex = 1;
				if (query.hasParams()) {
					for (Object param : query.getParams())
						setParameter(st, paramIndex++, param);
				}
				Integer maxCount = query.getMaxCount();
				if (maxCount != null)
					st.setInt(paramIndex, maxCount);
			};
		}
		else
			return null;
	}

	public static void setParameter(PreparedStatementHelper st, int index, Object value) throws SQLException {
		if (value instanceof Boolean)
			st.setInt(index, (Boolean)value ? -1 : 0);
		else if (value instanceof Enum)
			st.setString(index, ((Enum)value).name());
		else if (value instanceof java.util.Date)
			st.setTimestamp(index, new Timestamp(((java.util.Date)value).getTime()));
		else if (value instanceof Instant)
			st.setTimestamp(index, new Timestamp(((Instant)value).toEpochMilli()));
		else
			st.setObject(index, value);
	}
}
