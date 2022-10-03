package org.apache.tomcat.jdbc.pool;

public class XADataSource extends DataSource implements javax.sql.XADataSource
{
    public XADataSource() {
    }
    
    public XADataSource(final PoolConfiguration poolProperties) {
        super(poolProperties);
    }
}
