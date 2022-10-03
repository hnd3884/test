package org.apache.commons.beanutils;

import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.io.Serializable;

public class RowSetDynaClass extends JDBCDynaClass implements DynaClass, Serializable
{
    protected int limit;
    protected List<DynaBean> rows;
    
    public RowSetDynaClass(final ResultSet resultSet) throws SQLException {
        this(resultSet, true, -1);
    }
    
    public RowSetDynaClass(final ResultSet resultSet, final int limit) throws SQLException {
        this(resultSet, true, limit);
    }
    
    public RowSetDynaClass(final ResultSet resultSet, final boolean lowerCase) throws SQLException {
        this(resultSet, lowerCase, -1);
    }
    
    public RowSetDynaClass(final ResultSet resultSet, final boolean lowerCase, final int limit) throws SQLException {
        this(resultSet, lowerCase, limit, false);
    }
    
    public RowSetDynaClass(final ResultSet resultSet, final boolean lowerCase, final boolean useColumnLabel) throws SQLException {
        this(resultSet, lowerCase, -1, useColumnLabel);
    }
    
    public RowSetDynaClass(final ResultSet resultSet, final boolean lowerCase, final int limit, final boolean useColumnLabel) throws SQLException {
        this.limit = -1;
        this.rows = new ArrayList<DynaBean>();
        if (resultSet == null) {
            throw new NullPointerException();
        }
        this.lowerCase = lowerCase;
        this.limit = limit;
        this.setUseColumnLabel(useColumnLabel);
        this.introspect(resultSet);
        this.copy(resultSet);
    }
    
    public List<DynaBean> getRows() {
        return this.rows;
    }
    
    protected void copy(final ResultSet resultSet) throws SQLException {
        int cnt = 0;
        while (resultSet.next() && (this.limit < 0 || cnt++ < this.limit)) {
            final DynaBean bean = this.createDynaBean();
            for (final DynaProperty propertie : this.properties) {
                final String name = propertie.getName();
                final Object value = this.getObject(resultSet, name);
                bean.set(name, value);
            }
            this.rows.add(bean);
        }
    }
    
    protected DynaBean createDynaBean() {
        return new BasicDynaBean(this);
    }
}
