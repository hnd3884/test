package com.sun.corba.se.impl.corba;

import org.omg.CORBA.NVList;
import org.omg.CORBA.Any;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Context;

public final class ContextImpl extends Context
{
    private ORB _orb;
    private ORBUtilSystemException wrapper;
    
    public ContextImpl(final ORB orb) {
        this._orb = orb;
        this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)orb, "rpc.presentation");
    }
    
    public ContextImpl(final Context context) {
        throw this.wrapper.contextNotImplemented();
    }
    
    @Override
    public String context_name() {
        throw this.wrapper.contextNotImplemented();
    }
    
    @Override
    public Context parent() {
        throw this.wrapper.contextNotImplemented();
    }
    
    @Override
    public Context create_child(final String s) {
        throw this.wrapper.contextNotImplemented();
    }
    
    @Override
    public void set_one_value(final String s, final Any any) {
        throw this.wrapper.contextNotImplemented();
    }
    
    @Override
    public void set_values(final NVList list) {
        throw this.wrapper.contextNotImplemented();
    }
    
    @Override
    public void delete_values(final String s) {
        throw this.wrapper.contextNotImplemented();
    }
    
    @Override
    public NVList get_values(final String s, final int n, final String s2) {
        throw this.wrapper.contextNotImplemented();
    }
}
