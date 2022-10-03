package org.apache.tomcat.dbcp.dbcp2;

public interface BasicDataSourceMXBean extends DataSourceMXBean
{
    @Deprecated
    String getPassword();
}
