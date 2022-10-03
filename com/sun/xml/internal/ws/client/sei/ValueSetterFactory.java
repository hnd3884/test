package com.sun.xml.internal.ws.client.sei;

import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.model.ParameterImpl;

public abstract class ValueSetterFactory
{
    public static final ValueSetterFactory SYNC;
    public static final ValueSetterFactory NONE;
    public static final ValueSetterFactory SINGLE;
    
    public abstract ValueSetter get(final ParameterImpl p0);
    
    static {
        SYNC = new ValueSetterFactory() {
            @Override
            public ValueSetter get(final ParameterImpl p) {
                return ValueSetter.getSync(p);
            }
        };
        NONE = new ValueSetterFactory() {
            @Override
            public ValueSetter get(final ParameterImpl p) {
                throw new WebServiceException("This shouldn't happen. No response parameters.");
            }
        };
        SINGLE = new ValueSetterFactory() {
            @Override
            public ValueSetter get(final ParameterImpl p) {
                return ValueSetter.SINGLE_VALUE;
            }
        };
    }
    
    public static final class AsyncBeanValueSetterFactory extends ValueSetterFactory
    {
        private Class asyncBean;
        
        public AsyncBeanValueSetterFactory(final Class asyncBean) {
            this.asyncBean = asyncBean;
        }
        
        @Override
        public ValueSetter get(final ParameterImpl p) {
            return new ValueSetter.AsyncBeanValueSetter(p, this.asyncBean);
        }
    }
}
