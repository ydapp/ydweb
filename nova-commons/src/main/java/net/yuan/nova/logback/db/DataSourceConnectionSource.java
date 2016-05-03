package net.yuan.nova.logback.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import ch.qos.logback.core.db.ConnectionSourceBase;
import ch.qos.logback.core.db.dialect.SQLDialectCode;

import net.yuan.nova.commons.SpringUtils;

public class DataSourceConnectionSource extends ConnectionSourceBase {

	private DataSource dataSource;

	@Override
	public void start() {
		if (getDataSource() == null) {
			addWarn("WARNING: No data source specified");
		} else {
			discoverConnectionProperties();
			if (!supportsGetGeneratedKeys() && getSQLDialectCode() == SQLDialectCode.UNKNOWN_DIALECT) {
				addWarn("Connection does not support GetGeneratedKey method and could not discover the dialect.");
			}
		}
		super.start();
	}

	/**
	 * @see ch.qos.logback.commons.db.ConnectionSource#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		if (dataSource == null) {
			addError("WARNING: No data source specified");
			return null;
		}

		if (getUser() == null) {
			return dataSource.getConnection();
		} else {
			return dataSource.getConnection(getUser(), getPassword());
		}
	}

	public DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = createDataSource();
		}
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 创建数据源o
	 * 
	 * @return
	 */
	private DataSource createDataSource() {
		System.err.println("信息：在日志系统中加载应用系统的数据源");
		DataSource dataSource = (DataSource) SpringUtils.getBean("dataSource");
		if (dataSource == null) {
			System.err.println("请在spring配置文件中配置数据源“dataSource”");
		}
		return dataSource;
	}
}
