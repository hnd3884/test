package org.apache.commons.beanutils;

import java.util.Iterator;
import java.sql.SQLException;
import java.sql.ResultSet;

public class ResultSetDynaClass extends JDBCDynaClass implements DynaClass
{
    protected ResultSet resultSet;
    
    public ResultSetDynaClass(final ResultSet resultSet) throws SQLException {
        this(resultSet, true);
    }
    
    public ResultSetDynaClass(final ResultSet resultSet, final boolean lowerCase) throws SQLException {
        this(resultSet, lowerCase, false);
    }
    
    public ResultSetDynaClass(final ResultSet resultSet, final boolean lowerCase, final boolean useColumnLabel) throws SQLException {
        this.resultSet = null;
        if (resultSet == null) {
            throw new NullPointerException();
        }
        this.resultSet = resultSet;
        this.lowerCase = lowerCase;
        this.setUseColumnLabel(useColumnLabel);
        this.introspect(resultSet);
    }
    
    public Iterator<DynaBean> iterator() {
        return new ResultSetIterator(this);
    }
    
    public Object getObjectFromResultSet(final String name) throws SQLException {
        return this.getObject(this.getResultSet(), name);
    }
    
    ResultSet getResultSet() {
        return this.resultSet;
    }
    
    @Override
    protected Class<?> loadClass(final String className) throws SQLException {
        try {
            return this.getClass().getClassLoader().loadClass(className);
        }
        catch (final Exception e) {
            throw new SQLException("Cannot load column class '" + className + "': " + e);
        }
    }
}
