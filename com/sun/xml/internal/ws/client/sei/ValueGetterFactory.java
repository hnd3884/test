package com.sun.xml.internal.ws.client.sei;

import javax.jws.WebParam;
import com.sun.xml.internal.ws.model.ParameterImpl;

abstract class ValueGetterFactory
{
    static final ValueGetterFactory SYNC;
    static final ValueGetterFactory ASYNC;
    
    abstract ValueGetter get(final ParameterImpl p0);
    
    static {
        SYNC = new ValueGetterFactory() {
            @Override
            ValueGetter get(final ParameterImpl p) {
                return (p.getMode() == WebParam.Mode.IN || p.getIndex() == -1) ? ValueGetter.PLAIN : ValueGetter.HOLDER;
            }
        };
        ASYNC = new ValueGetterFactory() {
            @Override
            ValueGetter get(final ParameterImpl p) {
                return ValueGetter.PLAIN;
            }
        };
    }
}
