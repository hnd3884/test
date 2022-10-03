package org.apache.naming.factory;

import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.Reference;
import org.apache.naming.ResourceRef;
import org.apache.naming.StringManager;

public class ResourceFactory extends FactoryBase
{
    private static final StringManager sm;
    
    @Override
    protected boolean isReferenceTypeSupported(final Object obj) {
        return obj instanceof ResourceRef;
    }
    
    @Override
    protected ObjectFactory getDefaultFactory(final Reference ref) throws NamingException {
        ObjectFactory factory = null;
        if (ref.getClassName().equals("javax.sql.DataSource")) {
            final String javaxSqlDataSourceFactoryClassName = System.getProperty("javax.sql.DataSource.Factory", "org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory");
            try {
                factory = (ObjectFactory)Class.forName(javaxSqlDataSourceFactoryClassName).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (final Exception e) {
                final NamingException ex = new NamingException(ResourceFactory.sm.getString("resourceFactory.factoryCreationError"));
                ex.initCause(e);
                throw ex;
            }
        }
        else if (ref.getClassName().equals("javax.mail.Session")) {
            final String javaxMailSessionFactoryClassName = System.getProperty("javax.mail.Session.Factory", "org.apache.naming.factory.MailSessionFactory");
            try {
                factory = (ObjectFactory)Class.forName(javaxMailSessionFactoryClassName).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (final Throwable t) {
                if (t instanceof NamingException) {
                    throw (NamingException)t;
                }
                if (t instanceof ThreadDeath) {
                    throw (ThreadDeath)t;
                }
                if (t instanceof VirtualMachineError) {
                    throw (VirtualMachineError)t;
                }
                final NamingException ex = new NamingException(ResourceFactory.sm.getString("resourceFactory.factoryCreationError"));
                ex.initCause(t);
                throw ex;
            }
        }
        return factory;
    }
    
    @Override
    protected Object getLinked(final Reference ref) {
        return null;
    }
    
    static {
        sm = StringManager.getManager(ResourceFactory.class);
    }
}
