package com.mycompany.myapp.tenancy.hibernate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AdamS on 2015-03-12.
 */
public class MyMultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider, ServiceRegistryAwareService {

    private final Logger log = LoggerFactory.getLogger(MyMultiTenantConnectionProviderImpl.class);

    DataSource dataSource;
    private Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();

    public MyMultiTenantConnectionProviderImpl() {
        getSource("main");
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
//        try {
//            connection.createStatement().execute("USE main;");
//        }
//        catch (SQLException e) {
//            log.debug("",e);
//            throw new HibernateException("Could not alter JDBC connection to specified schema [public]", e);
//        }
        connection.close();
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        //return this.dataSource.getConnection();
        log.info("get eny connection return main");
        return getSource("jhipster").getConnection();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        log.info("Tenatd is:" + tenantIdentifier);
//        final Connection connection = getAnyConnection();
//        try {
//            //connection.createStatement().execute("USE " + tenantIdentifier + ";");
//
//        }
//        catch (SQLException e) {
//            log.debug("",e);
//            throw new HibernateException("Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]", e);
//        }
//
//        return connection;
        return getSource(tenantIdentifier).getConnection();
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
//        try {
//            connection.createStatement().execute("USE main");
//        }
//        catch (SQLException e) {
//            log.debug("",e);
//            throw new HibernateException("Could not alter JDBC connection to specified schema [public]", e);
//        }
        log.info("releaseConnection " + tenantIdentifier);
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        Map lSettings = serviceRegistry.getService(ConfigurationService.class).getSettings();
        DataSource localDs =  (DataSource) lSettings.get("hibernate.connection.datasource");
        dataSource = localDs;
    }

    public DataSource getSource(String tentant) {
        if(dataSourceMap.containsKey(tentant)){
            return dataSourceMap.get(tentant);
        } else {
            DataSource ds = dataSource(tentant);
            dataSourceMap.put(tentant,ds);
            return ds;
        }
    }

    public DataSource dataSource(String tentant) {
        log.info("Create Datasource "+tentant);

        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("url", "jdbc:mysql://localhost:3306/"+tentant);
        config.addDataSourceProperty("user", "root");
        config.addDataSourceProperty("password", "");

        //MySQL optimizations, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize",  "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(config);
    }
}
