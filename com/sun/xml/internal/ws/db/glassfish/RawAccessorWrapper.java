package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.bind.api.RawAccessor;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;

public class RawAccessorWrapper implements PropertyAccessor
{
    private RawAccessor accessor;
    
    public RawAccessorWrapper(final RawAccessor a) {
        this.accessor = a;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.accessor.equals(obj);
    }
    
    @Override
    public Object get(final Object bean) throws DatabindingException {
        try {
            return this.accessor.get(bean);
        }
        catch (final AccessorException e) {
            throw new DatabindingException(e);
        }
    }
    
    @Override
    public int hashCode() {
        return this.accessor.hashCode();
    }
    
    @Override
    public void set(final Object bean, final Object value) throws DatabindingException {
        try {
            this.accessor.set(bean, value);
        }
        catch (final AccessorException e) {
            throw new DatabindingException(e);
        }
    }
    
    @Override
    public String toString() {
        return this.accessor.toString();
    }
}
