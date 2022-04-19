/*
 * Copyright 2014-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.dbflute.utflute.spring;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbflute.jdbc.DataSourceHandler;
import org.dbflute.jdbc.HandlingDataSourceWrapper;
import org.dbflute.jdbc.NotClosingConnectionWrapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * @author jflute
 * @since 0.1.1 (2011/07/25 Monday)
 */
public abstract class ContainerTestCase extends SpringTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The (main) data source for database. (NotNull: after injection) */
    @Resource(name = "dataSource")
    private DataSource _xdataSource; // resolved by name for multiple DB

    // ===================================================================================
    //                                                                         JDBC Helper
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    @Override
    protected DataSource getDataSource() { // user method
        if (_xdataSource == null) {
            return null;
        }
        return wrapSpringTransactionalDataSource(_xdataSource); // as default
    }

    /**
     * Wrap the data source for Spring transaction. (basically for commons-DBCP)
     * @param dataSource The data source provided from DI container. (NotNull)
     * @return The wrapped data source for Spring transaction. (NotNull)
     */
    protected DataSource wrapSpringTransactionalDataSource(DataSource dataSource) {
        // same way as DBFlute does because it may use, e.g. Commons DBCP
        return xcreateHandlingDataSourceWrapper(xcreateSpringTransactionalDataSourceHandler());
    }

    protected SpringTransactionalDataSourceHandler xcreateSpringTransactionalDataSourceHandler() {
        return new SpringTransactionalDataSourceHandler();
    }

    protected HandlingDataSourceWrapper xcreateHandlingDataSourceWrapper(SpringTransactionalDataSourceHandler handler) {
        return new HandlingDataSourceWrapper(_xdataSource, new DataSourceHandler() {
            public Connection getConnection(DataSource dataSource) throws SQLException {
                return handler.getConnection(dataSource);
            }
        });
    }

    protected static class SpringTransactionalDataSourceHandler implements DataSourceHandler {

        public Connection getConnection(DataSource ds) throws SQLException {
            final Connection conn = getConnectionFromUtils(ds);
            if (isConnectionTransactional(conn, ds)) {
                return new NotClosingConnectionWrapper(conn);
            } else {
                return conn;
            }
        }

        public Connection getConnectionFromUtils(DataSource ds) {
            return DataSourceUtils.getConnection(ds);
        }

        public boolean isConnectionTransactional(Connection conn, DataSource ds) {
            return DataSourceUtils.isConnectionTransactional(conn, ds);
        }

        @Override
        public String toString() {
            return "SpringDBCPDataSourceHandler(for Spring and Commons-DBCP)";
        }
    }
}
