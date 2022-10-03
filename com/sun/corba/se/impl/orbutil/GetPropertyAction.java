package com.sun.corba.se.impl.orbutil;

import java.security.PrivilegedAction;

public class GetPropertyAction implements PrivilegedAction
{
    private String theProp;
    private String defaultVal;
    
    public GetPropertyAction(final String theProp) {
        this.theProp = theProp;
    }
    
    public GetPropertyAction(final String theProp, final String defaultVal) {
        this.theProp = theProp;
        this.defaultVal = defaultVal;
    }
    
    @Override
    public Object run() {
        final String property = System.getProperty(this.theProp);
        return (property == null) ? this.defaultVal : property;
    }
}
