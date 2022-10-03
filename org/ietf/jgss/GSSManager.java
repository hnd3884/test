package org.ietf.jgss;

import java.security.Provider;
import sun.security.jgss.GSSManagerImpl;

public abstract class GSSManager
{
    public static GSSManager getInstance() {
        return new GSSManagerImpl();
    }
    
    public abstract Oid[] getMechs();
    
    public abstract Oid[] getNamesForMech(final Oid p0) throws GSSException;
    
    public abstract Oid[] getMechsForName(final Oid p0);
    
    public abstract GSSName createName(final String p0, final Oid p1) throws GSSException;
    
    public abstract GSSName createName(final byte[] p0, final Oid p1) throws GSSException;
    
    public abstract GSSName createName(final String p0, final Oid p1, final Oid p2) throws GSSException;
    
    public abstract GSSName createName(final byte[] p0, final Oid p1, final Oid p2) throws GSSException;
    
    public abstract GSSCredential createCredential(final int p0) throws GSSException;
    
    public abstract GSSCredential createCredential(final GSSName p0, final int p1, final Oid p2, final int p3) throws GSSException;
    
    public abstract GSSCredential createCredential(final GSSName p0, final int p1, final Oid[] p2, final int p3) throws GSSException;
    
    public abstract GSSContext createContext(final GSSName p0, final Oid p1, final GSSCredential p2, final int p3) throws GSSException;
    
    public abstract GSSContext createContext(final GSSCredential p0) throws GSSException;
    
    public abstract GSSContext createContext(final byte[] p0) throws GSSException;
    
    public abstract void addProviderAtFront(final Provider p0, final Oid p1) throws GSSException;
    
    public abstract void addProviderAtEnd(final Provider p0, final Oid p1) throws GSSException;
}
