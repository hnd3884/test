package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Environment;

public class EnvironmentImpl extends Environment
{
    private Exception _exc;
    
    @Override
    public Exception exception() {
        return this._exc;
    }
    
    @Override
    public void exception(final Exception exc) {
        this._exc = exc;
    }
    
    @Override
    public void clear() {
        this._exc = null;
    }
}
