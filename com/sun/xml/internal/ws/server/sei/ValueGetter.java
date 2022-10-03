package com.sun.xml.internal.ws.server.sei;

import javax.xml.ws.Holder;
import javax.jws.WebParam;
import com.sun.xml.internal.ws.model.ParameterImpl;

public enum ValueGetter
{
    PLAIN {
        @Override
        public Object get(final Object parameter) {
            return parameter;
        }
    }, 
    HOLDER {
        @Override
        public Object get(final Object parameter) {
            if (parameter == null) {
                return null;
            }
            return ((Holder)parameter).value;
        }
    };
    
    public abstract Object get(final Object p0);
    
    public static ValueGetter get(final ParameterImpl p) {
        if (p.getMode() == WebParam.Mode.IN || p.getIndex() == -1) {
            return ValueGetter.PLAIN;
        }
        return ValueGetter.HOLDER;
    }
}
