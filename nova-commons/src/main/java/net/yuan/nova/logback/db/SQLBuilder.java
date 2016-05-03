package net.yuan.nova.logback.db;

import ch.qos.logback.classic.db.names.DBNameResolver;

/**
 * @author zhangshuai
 * @since 2010-03-16
 */
public class SQLBuilder {

	static String buildInsertSysLogSQL(DBNameResolver dbNameResolver) {
		StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
		sqlBuilder.append(dbNameResolver.getTableName(TableName.PISP_SYS_LOG)).append(" (");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.SYS_LOG_ID)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.THREAD_NAME)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.LOG_LEVEL)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.LOGGER_NAME)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CALLER_FILENAME)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.GEN_POS)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.COMMENTS)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.LOG_COTNENT)).append(", ");
		sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CREATE_DATE)).append(") ");
//		sqlBuilder.append("VALUES (SYS_LOG_ID_SEQ.NEXTVAL, ?, ? ,?, ?, ?, ?, ?, to_date(?,'yyyy-mm-dd hh24:mi:ss'))");
		sqlBuilder.append("VALUES (SYS_LOG_ID_SEQ.NEXTVAL, ?, ? ,?, ?, ?, ?, ?, ?)");
		return sqlBuilder.toString();
	}
}
