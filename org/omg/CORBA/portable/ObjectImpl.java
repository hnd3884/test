package org.omg.CORBA.portable;

import org.omg.CORBA.SetOverrideType;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.Policy;
import org.omg.CORBA.ORB;
import java.lang.reflect.InvocationTargetException;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Context;
import org.omg.CORBA.Request;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.Object;

public abstract class ObjectImpl implements Object
{
    private transient Delegate __delegate;
    
    public Delegate _get_delegate() {
        if (this.__delegate == null) {
            throw new BAD_OPERATION("The delegate has not been set!");
        }
        return this.__delegate;
    }
    
    public void _set_delegate(final Delegate _delegate) {
        this.__delegate = _delegate;
    }
    
    public abstract String[] _ids();
    
    @Override
    public Object _duplicate() {
        return this._get_delegate().duplicate(this);
    }
    
    @Override
    public void _release() {
        this._get_delegate().release(this);
    }
    
    @Override
    public boolean _is_a(final String s) {
        return this._get_delegate().is_a(this, s);
    }
    
    @Override
    public boolean _is_equivalent(final Object object) {
        return this._get_delegate().is_equivalent(this, object);
    }
    
    @Override
    public boolean _non_existent() {
        return this._get_delegate().non_existent(this);
    }
    
    @Override
    public int _hash(final int n) {
        return this._get_delegate().hash(this, n);
    }
    
    @Override
    public Request _request(final String s) {
        return this._get_delegate().request(this, s);
    }
    
    @Override
    public Request _create_request(final Context context, final String s, final NVList list, final NamedValue namedValue) {
        return this._get_delegate().create_request(this, context, s, list, namedValue);
    }
    
    @Override
    public Request _create_request(final Context context, final String s, final NVList list, final NamedValue namedValue, final ExceptionList list2, final ContextList list3) {
        return this._get_delegate().create_request(this, context, s, list, namedValue, list2, list3);
    }
    
    @Override
    public Object _get_interface_def() {
        final Delegate get_delegate = this._get_delegate();
        try {
            return get_delegate.get_interface_def(this);
        }
        catch (final NO_IMPLEMENT no_IMPLEMENT) {
            try {
                return (Object)get_delegate.getClass().getMethod("get_interface", Object.class).invoke(get_delegate, this);
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
    
    public ORB _orb() {
        return this._get_delegate().orb(this);
    }
    
    @Override
    public Policy _get_policy(final int n) {
        return this._get_delegate().get_policy(this, n);
    }
    
    @Override
    public DomainManager[] _get_domain_managers() {
        return this._get_delegate().get_domain_managers(this);
    }
    
    @Override
    public Object _set_policy_override(final Policy[] array, final SetOverrideType setOverrideType) {
        return this._get_delegate().set_policy_override(this, array, setOverrideType);
    }
    
    public boolean _is_local() {
        return this._get_delegate().is_local(this);
    }
    
    public ServantObject _servant_preinvoke(final String s, final Class clazz) {
        return this._get_delegate().servant_preinvoke(this, s, clazz);
    }
    
    public void _servant_postinvoke(final ServantObject servantObject) {
        this._get_delegate().servant_postinvoke(this, servantObject);
    }
    
    public OutputStream _request(final String s, final boolean b) {
        return this._get_delegate().request(this, s, b);
    }
    
    public InputStream _invoke(final OutputStream outputStream) throws ApplicationException, RemarshalException {
        return this._get_delegate().invoke(this, outputStream);
    }
    
    public void _releaseReply(final InputStream inputStream) {
        this._get_delegate().releaseReply(this, inputStream);
    }
    
    @Override
    public String toString() {
        if (this.__delegate != null) {
            return this.__delegate.toString(this);
        }
        return this.getClass().getName() + ": no delegate set";
    }
    
    @Override
    public int hashCode() {
        if (this.__delegate != null) {
            return this.__delegate.hashCode(this);
        }
        return super.hashCode();
    }
    
    @Override
    public boolean equals(final java.lang.Object o) {
        if (this.__delegate != null) {
            return this.__delegate.equals(this, o);
        }
        return this == o;
    }
}
