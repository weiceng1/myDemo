package com.demo.configuration;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PoolUtilities;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ConnectionBuilder;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.ShardingKeyBuilder;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ConnectionDataSource implements DataSource {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ConnectionDataSource.class);
    private PoolProperties poolProperties;
    private AtomicInteger retryCount = new AtomicInteger(0);
    private static final int MAX_RETRY_COUNT = 3;
    public static final String MYSQL_URL_FORMAT = "jdbc:mysql://%s:3306/%s?useSSL=true&requireSSL=true&enabledTLSProtocols=TLSv1.2&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&connectionCollation=utf8mb4_general_ci&useUnicode=true";

    public ConnectionDataSource(PoolProperties poolProperties) {
        this.poolProperties = poolProperties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        int count = retryCount.incrementAndGet();

        try {
            Connection connection = doConnection();
            retryCount.set(0);
            return connection;
        } catch (SQLException e) {
            if (count > MAX_RETRY_COUNT) {
                logger.error("mysql connection failed, retry count: {}", count);
                throw e;
            }

            logger.info("retry connect mysql, count: {}", count);
            return getConnection();
        }
    }

    private Connection doConnection() throws SQLException {
        String driverURL = poolProperties.getUrl();
        logger.info("connect datasource {}", driverURL);

        Properties prop = PoolUtilities.clone(poolProperties.getDbProperties());
        prop.setProperty(PoolUtilities.PROP_USER, poolProperties.getUsername());
        prop.setProperty(PoolUtilities.PROP_PASSWORD, poolProperties.getPassword());
        Connection connection;
        try {
            connection = DriverManager.getConnection(driverURL, prop);
        } catch (Exception x) {
            logger.debug("Unable to connect to database.", x);
            if (x instanceof SQLException) {
                throw (SQLException) x;
            } else {
                SQLException ex = new SQLException(x.getMessage());
                ex.initCause(x);
                throw ex;
            }
        }
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public ConnectionBuilder createConnectionBuilder() throws SQLException {
        return DataSource.super.createConnectionBuilder();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
        return DataSource.super.createShardingKeyBuilder();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
