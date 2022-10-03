package org.apache.tomcat.dbcp.dbcp2;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Driver;

class DriverFactory
{
    static Driver createDriver(final BasicDataSource basicDataSource) throws SQLException {
        Driver driverToUse = basicDataSource.getDriver();
        final String driverClassName = basicDataSource.getDriverClassName();
        final ClassLoader driverClassLoader = basicDataSource.getDriverClassLoader();
        final String url = basicDataSource.getUrl();
        if (driverToUse == null) {
            Class<?> driverFromCCL = null;
            if (driverClassName != null) {
                try {
                    try {
                        if (driverClassLoader == null) {
                            driverFromCCL = Class.forName(driverClassName);
                        }
                        else {
                            driverFromCCL = Class.forName(driverClassName, true, driverClassLoader);
                        }
                    }
                    catch (final ClassNotFoundException cnfe) {
                        driverFromCCL = Thread.currentThread().getContextClassLoader().loadClass(driverClassName);
                    }
                }
                catch (final Exception t) {
                    final String message = "Cannot load JDBC driver class '" + driverClassName + "'";
                    basicDataSource.log(message, t);
                    throw new SQLException(message, t);
                }
            }
            try {
                if (driverFromCCL == null) {
                    driverToUse = DriverManager.getDriver(url);
                }
                else {
                    driverToUse = (Driver)driverFromCCL.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                    if (!driverToUse.acceptsURL(url)) {
                        throw new SQLException("No suitable driver", "08001");
                    }
                }
            }
            catch (final Exception t) {
                final String message = "Cannot create JDBC driver of class '" + ((driverClassName != null) ? driverClassName : "") + "' for connect URL '" + url + "'";
                basicDataSource.log(message, t);
                throw new SQLException(message, t);
            }
        }
        return driverToUse;
    }
}
