package org.apache.taglibs.standard.tag.rt.sql;

import org.apache.taglibs.standard.tag.common.sql.QueryTagSupport;

public class QueryTag extends QueryTagSupport
{
    public void setDataSource(final Object dataSource) {
        this.rawDataSource = dataSource;
        this.dataSourceSpecified = true;
    }
    
    public void setStartRow(final int startRow) {
        this.startRow = startRow;
    }
    
    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
        this.maxRowsSpecified = true;
    }
    
    public void setSql(final String sql) {
        this.sql = sql;
    }
}
