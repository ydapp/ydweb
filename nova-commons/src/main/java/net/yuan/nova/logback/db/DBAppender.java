package net.yuan.nova.logback.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.db.names.DBNameResolver;
import ch.qos.logback.classic.db.names.DefaultDBNameResolver;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.db.DBAppenderBase;
import ch.qos.logback.core.db.DBHelper;

/**
 *
 * @author zhangshuai
 */
public class DBAppender extends DBAppenderBase<ILoggingEvent> {

	protected Level level;

	protected String insertSQL;

	private DBNameResolver dbNameResolver;

	static final int THREAD_NAME_INDEX = 1;
	static final int LOG_LEVEL_INDEX = 2;
	static final int LOGGER_NAME_INDEX = 3;
	static final int CALLER_FILENAME_INDEX = 4;
	static final int GEN_POS_INDEX = 5;
	static final int COMMENTS_INDEX = 6;
	static final int LOG_COTNENT_INDEX = 7;
	static final int CREATE_DATE_INDEX = 8;

	protected static final StackTraceElement EMPTY_CALLER_DATA = CallerData.naInstance();

	protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void setDbNameResolver(DBNameResolver dbNameResolver) {
		this.dbNameResolver = dbNameResolver;
	}

	@Override
	public void start() {
		this.level = Level.toLevel("WARN");
		if (dbNameResolver == null)
			dbNameResolver = new DefaultDBNameResolver();
		insertSQL = SQLBuilder.buildInsertSysLogSQL(dbNameResolver);
		super.start();
	}

	@Override
	public void append(ILoggingEvent eventObject) {
		Level level = eventObject.getLevel();
		if (level.toInt() < this.level.toInt()) {
			// System.out.println("直接返回，不予入库");
			return;
		}
		Connection connection = null;
		PreparedStatement insertStatement = null;
		try {
			connection = connectionSource.getConnection();
			connection.setAutoCommit(false);

			insertStatement = connection.prepareStatement(getInsertSQL());

			synchronized (this) {
				subAppend(eventObject, connection, insertStatement);
			}

			connection.commit();
		} catch (Throwable sqle) {
			sqle.printStackTrace();
			addError("problem appending event", sqle);
		} finally {
			DBHelper.closeStatement(insertStatement);
			DBHelper.closeConnection(connection);
		}
	}

	@Override
	protected void subAppend(ILoggingEvent event, Connection connection, PreparedStatement insertStatement)
			throws Throwable {

		insertStatement.setString(THREAD_NAME_INDEX, event.getThreadName());
		insertStatement.setString(LOG_LEVEL_INDEX, event.getLevel().toString());
		insertStatement.setString(LOGGER_NAME_INDEX, event.getLoggerName());

		StackTraceElement[] callerDataArray = event.getCallerData();
		StackTraceElement caller = extractFirstCaller(callerDataArray);
		insertStatement.setString(CALLER_FILENAME_INDEX, caller.getFileName());
		insertStatement.setString(GEN_POS_INDEX,
				new StringBuilder().append(caller.getClassName()).append(".").append(caller.getMethodName())
						.append("(").append(Integer.toString(caller.getLineNumber())).append(")").toString());
		// 异常信息
		insertStatement.setString(COMMENTS_INDEX, event.getFormattedMessage());
		IThrowableProxy throwableProxy = event.getThrowableProxy();
		if (throwableProxy != null) {
			insertStatement.setString(LOG_COTNENT_INDEX, getStackTrace(throwableProxy));
		} else {
			insertStatement.setString(LOG_COTNENT_INDEX, "");
		}
		// String dateStr = sdf.format(new Date(event.getTimeStamp()));
		// insertStatement.setString(CREATE_DATE_INDEX, dateStr);
		insertStatement.setTimestamp(CREATE_DATE_INDEX, new java.sql.Timestamp(event.getTimeStamp()));

		// insertStatement.setDate(CREATE_DATE_INDEX, new
		// Date(event.getTimeStamp()));

		// insertStatement.setTimestamp(CREATE_DATE_INDEX, new
		// Timestamp(event.getTimeStamp()));

		// 执行插入
		int updateCount = insertStatement.executeUpdate();
		if (updateCount != 1) {
			addWarn("Failed to insert loggingEvent");
		}
	}

	protected void secondarySubAppend(ILoggingEvent event, Connection connection, long eventId) throws Throwable {

	}

	private StackTraceElement extractFirstCaller(StackTraceElement[] callerDataArray) {
		StackTraceElement caller = EMPTY_CALLER_DATA;
		if (hasAtLeastOneNonNullElement(callerDataArray))
			caller = callerDataArray[0];
		return caller;
	}

	private boolean hasAtLeastOneNonNullElement(StackTraceElement[] callerDataArray) {
		return callerDataArray != null && callerDataArray.length > 0 && callerDataArray[0] != null;
	}

	@Override
	protected Method getGeneratedKeysMethod() {
		return null;
	}

	@Override
	protected String getInsertSQL() {
		return insertSQL;
	}

	private String getStackTrace(IThrowableProxy tp) {
		StringBuilder buf = new StringBuilder();
		while (tp != null) {
			buildExceptionStatement(tp, buf);
			if (buf.length() > 3800) {
				break;
			}
			tp = tp.getCause();
		}
		return buf.toString();
	}

	private void buildExceptionStatement(IThrowableProxy tp, StringBuilder buf) {

		ThrowableProxyUtil.subjoinFirstLine(buf, tp);

		int commonFrames = tp.getCommonFrames();
		StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
		for (int i = 0; i < stepArray.length - commonFrames; i++) {
			buf.append(CoreConstants.TAB);
			ThrowableProxyUtil.subjoinSTEP(buf, stepArray[i]);
			if (buf.length() > 3800) {
				return;
			}
		}

		if (commonFrames > 0) {
			buf.append(CoreConstants.TAB).append("... ").append(commonFrames).append(" common frames omitted");
		}

	}

}