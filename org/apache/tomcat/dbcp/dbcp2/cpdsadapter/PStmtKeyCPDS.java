package org.apache.tomcat.dbcp.dbcp2.cpdsadapter;

import org.apache.tomcat.dbcp.dbcp2.PStmtKey;

@Deprecated
public class PStmtKeyCPDS extends PStmtKey
{
    public PStmtKeyCPDS(final String sql) {
        super(sql);
    }
    
    public PStmtKeyCPDS(final String sql, final int autoGeneratedKeys) {
        super(sql, null, autoGeneratedKeys);
    }
    
    public PStmtKeyCPDS(final String sql, final int resultSetType, final int resultSetConcurrency) {
        super(sql, resultSetType, resultSetConcurrency);
    }
    
    public PStmtKeyCPDS(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) {
        super(sql, null, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    public PStmtKeyCPDS(final String sql, final int[] columnIndexes) {
        super(sql, null, columnIndexes);
    }
    
    public PStmtKeyCPDS(final String sql, final String[] columnNames) {
        super(sql, null, columnNames);
    }
}
