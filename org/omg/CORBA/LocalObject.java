package org.omg.CORBA;

import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ServantObject;

public class LocalObject implements Object
{
    private static String reason;
    
    @Override
    public boolean _is_equivalent(final Object object) {
        return this.equals(object);
    }
    
    @Override
    public boolean _non_existent() {
        return false;
    }
    
    @Override
    public int _hash(final int n) {
        return this.hashCode();
    }
    
    @Override
    public boolean _is_a(final String s) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    @Override
    public Object _duplicate() {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    @Override
    public void _release() {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    @Override
    public Request _request(final String s) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    @Override
    public Request _create_request(final Context context, final String s, final NVList list, final NamedValue namedValue) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    @Override
    public Request _create_request(final Context context, final String s, final NVList list, final NamedValue namedValue, final ExceptionList list2, final ContextList list3) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    public Object _get_interface() {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    @Override
    public Object _get_interface_def() {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    public ORB _orb() {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    @Override
    public Policy _get_policy(final int n) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    @Override
    public DomainManager[] _get_domain_managers() {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    @Override
    public Object _set_policy_override(final Policy[] array, final SetOverrideType setOverrideType) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    public boolean _is_local() {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    public ServantObject _servant_preinvoke(final String s, final Class clazz) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    public void _servant_postinvoke(final ServantObject servantObject) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    public OutputStream _request(final String s, final boolean b) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    public InputStream _invoke(final OutputStream outputStream) throws ApplicationException, RemarshalException {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    public void _releaseReply(final InputStream inputStream) {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    public boolean validate_connection() {
        throw new NO_IMPLEMENT(LocalObject.reason);
    }
    
    static {
        LocalObject.reason = "This is a locally constrained object.";
    }
}
