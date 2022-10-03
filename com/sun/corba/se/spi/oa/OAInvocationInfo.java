package com.sun.corba.se.spi.oa;

import javax.rmi.CORBA.Tie;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import org.omg.CORBA.portable.ServantObject;

public class OAInvocationInfo extends ServantObject
{
    private Object servantContainer;
    private ObjectAdapter oa;
    private byte[] oid;
    private CookieHolder cookieHolder;
    private String operation;
    private ObjectCopierFactory factory;
    
    public OAInvocationInfo(final ObjectAdapter oa, final byte[] oid) {
        this.oa = oa;
        this.oid = oid;
    }
    
    public OAInvocationInfo(final OAInvocationInfo oaInvocationInfo, final String operation) {
        this.servant = oaInvocationInfo.servant;
        this.servantContainer = oaInvocationInfo.servantContainer;
        this.cookieHolder = oaInvocationInfo.cookieHolder;
        this.oa = oaInvocationInfo.oa;
        this.oid = oaInvocationInfo.oid;
        this.factory = oaInvocationInfo.factory;
        this.operation = operation;
    }
    
    public ObjectAdapter oa() {
        return this.oa;
    }
    
    public byte[] id() {
        return this.oid;
    }
    
    public Object getServantContainer() {
        return this.servantContainer;
    }
    
    public CookieHolder getCookieHolder() {
        if (this.cookieHolder == null) {
            this.cookieHolder = new CookieHolder();
        }
        return this.cookieHolder;
    }
    
    public String getOperation() {
        return this.operation;
    }
    
    public ObjectCopierFactory getCopierFactory() {
        return this.factory;
    }
    
    public void setOperation(final String operation) {
        this.operation = operation;
    }
    
    public void setCopierFactory(final ObjectCopierFactory factory) {
        this.factory = factory;
    }
    
    public void setServant(final Object o) {
        this.servantContainer = o;
        if (o instanceof Tie) {
            this.servant = ((Tie)o).getTarget();
        }
        else {
            this.servant = o;
        }
    }
}
