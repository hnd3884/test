package org.apache.naming;

import java.util.Enumeration;
import javax.naming.RefAddr;
import javax.naming.Reference;

public abstract class AbstractRef extends Reference
{
    private static final long serialVersionUID = 1L;
    
    public AbstractRef(final String className) {
        super(className);
    }
    
    public AbstractRef(final String className, final String factory, final String factoryLocation) {
        super(className, factory, factoryLocation);
    }
    
    @Override
    public final String getFactoryClassName() {
        String factory = super.getFactoryClassName();
        if (factory != null) {
            return factory;
        }
        factory = System.getProperty("java.naming.factory.object");
        if (factory != null) {
            return null;
        }
        return this.getDefaultFactoryClassName();
    }
    
    protected abstract String getDefaultFactoryClassName();
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append("[className=");
        sb.append(this.getClassName());
        sb.append(",factoryClassLocation=");
        sb.append(this.getFactoryClassLocation());
        sb.append(",factoryClassName=");
        sb.append(this.getFactoryClassName());
        final Enumeration<RefAddr> refAddrs = this.getAll();
        while (refAddrs.hasMoreElements()) {
            final RefAddr refAddr = refAddrs.nextElement();
            sb.append(",{type=");
            sb.append(refAddr.getType());
            sb.append(",content=");
            sb.append(refAddr.getContent());
            sb.append('}');
        }
        sb.append(']');
        return sb.toString();
    }
}
