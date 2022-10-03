package org.omg.CORBA.portable;

import org.omg.CORBA.SetOverrideType;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.Policy;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Context;
import org.omg.CORBA.Request;
import org.omg.CORBA.Object;

public abstract class Delegate
{
    public abstract org.omg.CORBA.Object get_interface_def(final org.omg.CORBA.Object p0);
    
    public abstract org.omg.CORBA.Object duplicate(final org.omg.CORBA.Object p0);
    
    public abstract void release(final org.omg.CORBA.Object p0);
    
    public abstract boolean is_a(final org.omg.CORBA.Object p0, final String p1);
    
    public abstract boolean non_existent(final org.omg.CORBA.Object p0);
    
    public abstract boolean is_equivalent(final org.omg.CORBA.Object p0, final org.omg.CORBA.Object p1);
    
    public abstract int hash(final org.omg.CORBA.Object p0, final int p1);
    
    public abstract Request request(final org.omg.CORBA.Object p0, final String p1);
    
    public abstract Request create_request(final org.omg.CORBA.Object p0, final Context p1, final String p2, final NVList p3, final NamedValue p4);
    
    public abstract Request create_request(final org.omg.CORBA.Object p0, final Context p1, final String p2, final NVList p3, final NamedValue p4, final ExceptionList p5, final ContextList p6);
    
    public ORB orb(final org.omg.CORBA.Object object) {
        throw new NO_IMPLEMENT();
    }
    
    public Policy get_policy(final org.omg.CORBA.Object object, final int n) {
        throw new NO_IMPLEMENT();
    }
    
    public DomainManager[] get_domain_managers(final org.omg.CORBA.Object object) {
        throw new NO_IMPLEMENT();
    }
    
    public org.omg.CORBA.Object set_policy_override(final org.omg.CORBA.Object object, final Policy[] array, final SetOverrideType setOverrideType) {
        throw new NO_IMPLEMENT();
    }
    
    public boolean is_local(final org.omg.CORBA.Object object) {
        return false;
    }
    
    public ServantObject servant_preinvoke(final org.omg.CORBA.Object object, final String s, final Class clazz) {
        return null;
    }
    
    public void servant_postinvoke(final org.omg.CORBA.Object object, final ServantObject servantObject) {
    }
    
    public OutputStream request(final org.omg.CORBA.Object object, final String s, final boolean b) {
        throw new NO_IMPLEMENT();
    }
    
    public InputStream invoke(final org.omg.CORBA.Object object, final OutputStream outputStream) throws ApplicationException, RemarshalException {
        throw new NO_IMPLEMENT();
    }
    
    public void releaseReply(final org.omg.CORBA.Object object, final InputStream inputStream) {
        throw new NO_IMPLEMENT();
    }
    
    public String toString(final org.omg.CORBA.Object object) {
        return object.getClass().getName() + ":" + this.toString();
    }
    
    public int hashCode(final org.omg.CORBA.Object object) {
        return System.identityHashCode(object);
    }
    
    public boolean equals(final org.omg.CORBA.Object object, final Object o) {
        return object == o;
    }
}
