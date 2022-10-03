package com.sun.corba.se.impl.protocol;

import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Context;
import com.sun.corba.se.impl.corba.RequestImpl;
import org.omg.CORBA.Request;
import org.omg.CORBA.portable.Delegate;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.impl.util.JDKBridge;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.pept.encoding.InputObject;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import com.sun.corba.se.pept.encoding.OutputObject;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import java.util.Iterator;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Object;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;

public class CorbaClientDelegateImpl extends CorbaClientDelegate
{
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private CorbaContactInfoList contactInfoList;
    
    public CorbaClientDelegateImpl(final ORB orb, final CorbaContactInfoList contactInfoList) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
        this.contactInfoList = contactInfoList;
    }
    
    @Override
    public Broker getBroker() {
        return this.orb;
    }
    
    @Override
    public ContactInfoList getContactInfoList() {
        return this.contactInfoList;
    }
    
    @Override
    public OutputStream request(final org.omg.CORBA.Object object, final String s, final boolean b) {
        final ClientInvocationInfo orIncrementInvocationInfo = this.orb.createOrIncrementInvocationInfo();
        Iterator contactInfoListIterator = orIncrementInvocationInfo.getContactInfoListIterator();
        if (contactInfoListIterator == null) {
            contactInfoListIterator = this.contactInfoList.iterator();
            orIncrementInvocationInfo.setContactInfoListIterator(contactInfoListIterator);
        }
        if (!contactInfoListIterator.hasNext()) {
            throw ((CorbaContactInfoListIterator)contactInfoListIterator).getFailureException();
        }
        final CorbaContactInfo corbaContactInfo = contactInfoListIterator.next();
        final ClientRequestDispatcher clientRequestDispatcher = corbaContactInfo.getClientRequestDispatcher();
        orIncrementInvocationInfo.setClientRequestDispatcher(clientRequestDispatcher);
        return (OutputStream)clientRequestDispatcher.beginRequest(object, s, !b, corbaContactInfo);
    }
    
    @Override
    public InputStream invoke(final org.omg.CORBA.Object object, final OutputStream outputStream) throws ApplicationException, RemarshalException {
        return (InputStream)this.getClientRequestDispatcher().marshalingComplete(object, (OutputObject)outputStream);
    }
    
    @Override
    public void releaseReply(final org.omg.CORBA.Object object, final InputStream inputStream) {
        this.getClientRequestDispatcher().endRequest(this.orb, object, (InputObject)inputStream);
        this.orb.releaseOrDecrementInvocationInfo();
    }
    
    private ClientRequestDispatcher getClientRequestDispatcher() {
        return ((CorbaInvocationInfo)this.orb.getInvocationInfo()).getClientRequestDispatcher();
    }
    
    @Override
    public org.omg.CORBA.Object get_interface_def(final org.omg.CORBA.Object object) {
        InputStream invoke = null;
        Object o = null;
        try {
            invoke = this.invoke(null, this.request(null, "_interface", true));
            final org.omg.CORBA.Object read_Object = invoke.read_Object();
            if (!read_Object._is_a("IDL:omg.org/CORBA/InterfaceDef:1.0")) {
                throw this.wrapper.wrongInterfaceDef(CompletionStatus.COMPLETED_MAYBE);
            }
            try {
                o = JDKBridge.loadClass("org.omg.CORBA._InterfaceDefStub").newInstance();
            }
            catch (final Exception ex) {
                throw this.wrapper.noInterfaceDefStub(ex);
            }
            StubAdapter.setDelegate(o, StubAdapter.getDelegate(read_Object));
            return (org.omg.CORBA.Object)o;
        }
        catch (final ApplicationException ex2) {
            throw this.wrapper.applicationExceptionInSpecialMethod(ex2);
        }
        catch (final RemarshalException ex3) {
            return this.get_interface_def(object);
        }
        finally {
            this.releaseReply(null, invoke);
        }
        return (org.omg.CORBA.Object)o;
    }
    
    @Override
    public boolean is_a(final org.omg.CORBA.Object object, final String s) {
        final String[] typeIds = StubAdapter.getTypeIds(object);
        if (s.equals(this.contactInfoList.getTargetIOR().getTypeId())) {
            return true;
        }
        for (int i = 0; i < typeIds.length; ++i) {
            if (s.equals(typeIds[i])) {
                return true;
            }
        }
        InputStream invoke = null;
        try {
            final OutputStream request = this.request(null, "_is_a", true);
            request.write_string(s);
            invoke = this.invoke(null, request);
            return invoke.read_boolean();
        }
        catch (final ApplicationException ex) {
            throw this.wrapper.applicationExceptionInSpecialMethod(ex);
        }
        catch (final RemarshalException ex2) {
            return this.is_a(object, s);
        }
        finally {
            this.releaseReply(null, invoke);
        }
    }
    
    @Override
    public boolean non_existent(final org.omg.CORBA.Object object) {
        InputStream invoke = null;
        try {
            invoke = this.invoke(null, this.request(null, "_non_existent", true));
            return invoke.read_boolean();
        }
        catch (final ApplicationException ex) {
            throw this.wrapper.applicationExceptionInSpecialMethod(ex);
        }
        catch (final RemarshalException ex2) {
            return this.non_existent(object);
        }
        finally {
            this.releaseReply(null, invoke);
        }
    }
    
    @Override
    public org.omg.CORBA.Object duplicate(final org.omg.CORBA.Object object) {
        return object;
    }
    
    @Override
    public void release(final org.omg.CORBA.Object object) {
    }
    
    @Override
    public boolean is_equivalent(final org.omg.CORBA.Object object, final org.omg.CORBA.Object object2) {
        if (object2 == null) {
            return false;
        }
        if (!StubAdapter.isStub(object2)) {
            return false;
        }
        final org.omg.CORBA.portable.Delegate delegate = StubAdapter.getDelegate(object2);
        return delegate != null && (delegate == this || (delegate instanceof CorbaClientDelegateImpl && this.contactInfoList.getTargetIOR().isEquivalent(((CorbaContactInfoList)((CorbaClientDelegateImpl)delegate).getContactInfoList()).getTargetIOR())));
    }
    
    @Override
    public boolean equals(final org.omg.CORBA.Object object, final Object o) {
        if (o == null) {
            return false;
        }
        if (!StubAdapter.isStub(o)) {
            return false;
        }
        final org.omg.CORBA.portable.Delegate delegate = StubAdapter.getDelegate(o);
        return delegate != null && delegate instanceof CorbaClientDelegateImpl && this.contactInfoList.getTargetIOR().equals(((CorbaClientDelegateImpl)delegate).contactInfoList.getTargetIOR());
    }
    
    @Override
    public int hashCode(final org.omg.CORBA.Object object) {
        return this.hashCode();
    }
    
    @Override
    public int hash(final org.omg.CORBA.Object object, final int n) {
        final int hashCode = this.hashCode();
        if (hashCode > n) {
            return 0;
        }
        return hashCode;
    }
    
    @Override
    public Request request(final org.omg.CORBA.Object object, final String s) {
        return new RequestImpl(this.orb, object, null, s, null, null, null, null);
    }
    
    @Override
    public Request create_request(final org.omg.CORBA.Object object, final Context context, final String s, final NVList list, final NamedValue namedValue) {
        return new RequestImpl(this.orb, object, context, s, list, namedValue, null, null);
    }
    
    @Override
    public Request create_request(final org.omg.CORBA.Object object, final Context context, final String s, final NVList list, final NamedValue namedValue, final ExceptionList list2, final ContextList list3) {
        return new RequestImpl(this.orb, object, context, s, list, namedValue, list2, list3);
    }
    
    @Override
    public org.omg.CORBA.ORB orb(final org.omg.CORBA.Object object) {
        return this.orb;
    }
    
    @Override
    public boolean is_local(final org.omg.CORBA.Object object) {
        return this.contactInfoList.getEffectiveTargetIOR().getProfile().isLocal();
    }
    
    @Override
    public ServantObject servant_preinvoke(final org.omg.CORBA.Object object, final String s, final Class clazz) {
        return this.contactInfoList.getLocalClientRequestDispatcher().servant_preinvoke(object, s, clazz);
    }
    
    @Override
    public void servant_postinvoke(final org.omg.CORBA.Object object, final ServantObject servantObject) {
        this.contactInfoList.getLocalClientRequestDispatcher().servant_postinvoke(object, servantObject);
    }
    
    @Override
    public String get_codebase(final org.omg.CORBA.Object object) {
        if (this.contactInfoList.getTargetIOR() != null) {
            return this.contactInfoList.getTargetIOR().getProfile().getCodebase();
        }
        return null;
    }
    
    @Override
    public String toString(final org.omg.CORBA.Object object) {
        return this.contactInfoList.getTargetIOR().stringify();
    }
    
    @Override
    public int hashCode() {
        return this.contactInfoList.hashCode();
    }
}
