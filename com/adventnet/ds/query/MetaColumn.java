package com.adventnet.ds.query;

import com.adventnet.ds.query.util.QueryUtil;
import java.io.Serializable;

public class MetaColumn extends Column implements Serializable, Cloneable
{
    private static final long serialVersionUID = 4187190641488064119L;
    
    public MetaColumn(final String tableName, final String columnName, final int columnIndex, final String type) {
        super(tableName, columnName);
        super.setColumnIndex(columnIndex);
        super.setType(QueryUtil.getJavaSQLType(type));
    }
    
    @Override
    public void setTableAlias(final String tableAlias) {
        throw new UnsupportedOperationException("Table Name cannot be changed for this data-dictionary column " + this.getTableAlias() + "." + this.getColumnName());
    }
    
    @Override
    public void setColumnIndex(final int columnIndex) {
        throw new UnsupportedOperationException("Column Index cannot be changed for this data-dictionary column " + this.getTableAlias() + "." + this.getColumnName());
    }
    
    @Override
    public void setColumnName(final String columnName) {
        throw new UnsupportedOperationException("Column Name cannot be changed for this data-dictionary column " + this.getTableAlias() + "." + this.getColumnName());
    }
    
    @Override
    public void setColumnAlias(final String columnAlias) {
        throw new UnsupportedOperationException("Column Alias cannot be changed for this data-dictionary column " + this.getTableAlias() + "." + this.getColumnName());
    }
}
