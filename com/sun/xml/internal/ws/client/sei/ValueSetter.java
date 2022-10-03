package com.sun.xml.internal.ws.client.sei;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import javax.xml.ws.Holder;
import com.sun.xml.internal.ws.model.ParameterImpl;

public abstract class ValueSetter
{
    private static final ValueSetter RETURN_VALUE;
    private static final ValueSetter[] POOL;
    static final ValueSetter SINGLE_VALUE;
    
    private ValueSetter() {
    }
    
    abstract Object put(final Object p0, final Object[] p1);
    
    static ValueSetter getSync(final ParameterImpl p) {
        final int idx = p.getIndex();
        if (idx == -1) {
            return ValueSetter.RETURN_VALUE;
        }
        if (idx < ValueSetter.POOL.length) {
            return ValueSetter.POOL[idx];
        }
        return new Param(idx);
    }
    
    static {
        RETURN_VALUE = new ReturnValue();
        POOL = new ValueSetter[16];
        for (int i = 0; i < ValueSetter.POOL.length; ++i) {
            ValueSetter.POOL[i] = new Param(i);
        }
        SINGLE_VALUE = new SingleValue();
    }
    
    private static final class ReturnValue extends ValueSetter
    {
        private ReturnValue() {
            super(null);
        }
        
        @Override
        Object put(final Object obj, final Object[] args) {
            return obj;
        }
    }
    
    static final class Param extends ValueSetter
    {
        private final int idx;
        
        public Param(final int idx) {
            super(null);
            this.idx = idx;
        }
        
        @Override
        Object put(final Object obj, final Object[] args) {
            final Object arg = args[this.idx];
            if (arg != null) {
                assert arg instanceof Holder;
                ((Holder)arg).value = (T)obj;
            }
            return null;
        }
    }
    
    private static final class SingleValue extends ValueSetter
    {
        private SingleValue() {
            super(null);
        }
        
        @Override
        Object put(final Object obj, final Object[] args) {
            args[0] = obj;
            return null;
        }
    }
    
    static final class AsyncBeanValueSetter extends ValueSetter
    {
        private final PropertyAccessor accessor;
        
        AsyncBeanValueSetter(final ParameterImpl p, final Class wrapper) {
            super(null);
            final QName name = p.getName();
            try {
                this.accessor = p.getOwner().getBindingContext().getElementPropertyAccessor((Class<Object>)wrapper, name.getNamespaceURI(), name.getLocalPart());
            }
            catch (final JAXBException e) {
                throw new WebServiceException(wrapper + " do not have a property of the name " + name, e);
            }
        }
        
        @Override
        Object put(final Object obj, final Object[] args) {
            assert args != null;
            assert args.length == 1;
            assert args[0] != null;
            final Object bean = args[0];
            try {
                this.accessor.set(bean, obj);
            }
            catch (final Exception e) {
                throw new WebServiceException(e);
            }
            return null;
        }
    }
}
