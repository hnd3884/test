package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.CompletionStatus;
import java.util.EmptyStackException;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import org.omg.PortableServer.Servant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.omg.PortableServer.POA;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.PortableServer.Current;
import org.omg.CORBA.portable.ObjectImpl;

public class POACurrent extends ObjectImpl implements Current
{
    private ORB orb;
    private POASystemException wrapper;
    
    public POACurrent(final ORB orb) {
        this.orb = orb;
        this.wrapper = POASystemException.get(orb, "oa.invocation");
    }
    
    @Override
    public String[] _ids() {
        return new String[] { "IDL:omg.org/PortableServer/Current:1.0" };
    }
    
    @Override
    public POA get_POA() throws NoContext {
        final POA poa = (POA)this.peekThrowNoContext().oa();
        this.throwNoContextIfNull(poa);
        return poa;
    }
    
    @Override
    public byte[] get_object_id() throws NoContext {
        final byte[] id = this.peekThrowNoContext().id();
        this.throwNoContextIfNull(id);
        return id;
    }
    
    public ObjectAdapter getOA() {
        final ObjectAdapter oa = this.peekThrowInternal().oa();
        this.throwInternalIfNull(oa);
        return oa;
    }
    
    public byte[] getObjectId() {
        final byte[] id = this.peekThrowInternal().id();
        this.throwInternalIfNull(id);
        return id;
    }
    
    Servant getServant() {
        return (Servant)this.peekThrowInternal().getServantContainer();
    }
    
    CookieHolder getCookieHolder() {
        final CookieHolder cookieHolder = this.peekThrowInternal().getCookieHolder();
        this.throwInternalIfNull(cookieHolder);
        return cookieHolder;
    }
    
    public String getOperation() {
        final String operation = this.peekThrowInternal().getOperation();
        this.throwInternalIfNull(operation);
        return operation;
    }
    
    void setServant(final Servant servant) {
        this.peekThrowInternal().setServant(servant);
    }
    
    private OAInvocationInfo peekThrowNoContext() throws NoContext {
        OAInvocationInfo peekInvocationInfo;
        try {
            peekInvocationInfo = this.orb.peekInvocationInfo();
        }
        catch (final EmptyStackException ex) {
            throw new NoContext();
        }
        return peekInvocationInfo;
    }
    
    private OAInvocationInfo peekThrowInternal() {
        OAInvocationInfo peekInvocationInfo;
        try {
            peekInvocationInfo = this.orb.peekInvocationInfo();
        }
        catch (final EmptyStackException ex) {
            throw this.wrapper.poacurrentUnbalancedStack(ex);
        }
        return peekInvocationInfo;
    }
    
    private void throwNoContextIfNull(final Object o) throws NoContext {
        if (o == null) {
            throw new NoContext();
        }
    }
    
    private void throwInternalIfNull(final Object o) {
        if (o == null) {
            throw this.wrapper.poacurrentNullField(CompletionStatus.COMPLETED_MAYBE);
        }
    }
}
