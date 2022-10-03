package org.omg.PortableServer;

import java.lang.reflect.InvocationTargetException;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.PortableServer.portable.Delegate;

public abstract class Servant
{
    private transient Delegate _delegate;
    
    public Servant() {
        this._delegate = null;
    }
    
    public final Delegate _get_delegate() {
        if (this._delegate == null) {
            throw new BAD_INV_ORDER("The Servant has not been associated with an ORB instance");
        }
        return this._delegate;
    }
    
    public final void _set_delegate(final Delegate delegate) {
        this._delegate = delegate;
    }
    
    public final org.omg.CORBA.Object _this_object() {
        return this._get_delegate().this_object(this);
    }
    
    public final org.omg.CORBA.Object _this_object(final ORB orb) {
        try {
            ((org.omg.CORBA_2_3.ORB)orb).set_delegate(this);
        }
        catch (final ClassCastException ex) {
            throw new BAD_PARAM("POA Servant requires an instance of org.omg.CORBA_2_3.ORB");
        }
        return this._this_object();
    }
    
    public final ORB _orb() {
        return this._get_delegate().orb(this);
    }
    
    public final POA _poa() {
        return this._get_delegate().poa(this);
    }
    
    public final byte[] _object_id() {
        return this._get_delegate().object_id(this);
    }
    
    public POA _default_POA() {
        return this._get_delegate().default_POA(this);
    }
    
    public boolean _is_a(final String s) {
        return this._get_delegate().is_a(this, s);
    }
    
    public boolean _non_existent() {
        return this._get_delegate().non_existent(this);
    }
    
    public org.omg.CORBA.Object _get_interface_def() {
        final Delegate get_delegate = this._get_delegate();
        try {
            return get_delegate.get_interface_def(this);
        }
        catch (final AbstractMethodError abstractMethodError) {
            try {
                return (org.omg.CORBA.Object)get_delegate.getClass().getMethod("get_interface", Servant.class).invoke(get_delegate, this);
            }
            catch (final InvocationTargetException ex) {
                final Throwable targetException = ex.getTargetException();
                if (targetException instanceof Error) {
                    throw (Error)targetException;
                }
                if (targetException instanceof RuntimeException) {
                    throw (RuntimeException)targetException;
                }
                throw new NO_IMPLEMENT();
            }
            catch (final RuntimeException ex2) {
                throw ex2;
            }
            catch (final Exception ex3) {
                throw new NO_IMPLEMENT();
            }
        }
    }
    
    public abstract String[] _all_interfaces(final POA p0, final byte[] p1);
}
