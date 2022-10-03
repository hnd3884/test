package com.microsoft.sqlserver.jdbc;

import javax.naming.RefAddr;
import java.lang.reflect.InvocationTargetException;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

public final class SQLServerDataSourceObjectFactory implements ObjectFactory
{
    @Override
    public Object getObjectInstance(final Object ref, final Name name, final Context c, final Hashtable<?, ?> h) throws SQLServerException {
        try {
            final Reference r = (Reference)ref;
            final RefAddr ra = r.get("class");
            if (null == ra) {
                this.throwInvalidDataSourceRefException();
            }
            final String className = (String)ra.getContent();
            if (null == className) {
                this.throwInvalidDataSourceRefException();
            }
            if ("com.microsoft.sqlserver.jdbc.SQLServerDataSource".equals(className) || "com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource".equals(className) || "com.microsoft.sqlserver.jdbc.SQLServerXADataSource".equals(className)) {
                final Class<?> dataSourceClass = Class.forName(className);
                final Object dataSourceClassInstance = dataSourceClass.getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                final SQLServerDataSource ds = (SQLServerDataSource)dataSourceClassInstance;
                ds.initializeFromReference(r);
                return dataSourceClassInstance;
            }
            this.throwInvalidDataSourceRefException();
        }
        catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            this.throwInvalidDataSourceRefException();
        }
        return null;
    }
    
    private void throwInvalidDataSourceRefException() throws SQLServerException {
        SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidDataSourceReference"), null, true);
    }
}
