package com.sun.xml.internal.ws.client.sei;

import javax.xml.ws.Holder;

enum ValueGetter
{
    PLAIN {
        @Override
        Object get(final Object parameter) {
            return parameter;
        }
    }, 
    HOLDER {
        @Override
        Object get(final Object parameter) {
            if (parameter == null) {
                return null;
            }
            return ((Holder)parameter).value;
        }
    };
    
    abstract Object get(final Object p0);
}
