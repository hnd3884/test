package org.ietf.jgss;

public interface GSSCredential extends Cloneable
{
    public static final int INITIATE_AND_ACCEPT = 0;
    public static final int INITIATE_ONLY = 1;
    public static final int ACCEPT_ONLY = 2;
    public static final int DEFAULT_LIFETIME = 0;
    public static final int INDEFINITE_LIFETIME = Integer.MAX_VALUE;
    
    void dispose() throws GSSException;
    
    GSSName getName() throws GSSException;
    
    GSSName getName(final Oid p0) throws GSSException;
    
    int getRemainingLifetime() throws GSSException;
    
    int getRemainingInitLifetime(final Oid p0) throws GSSException;
    
    int getRemainingAcceptLifetime(final Oid p0) throws GSSException;
    
    int getUsage() throws GSSException;
    
    int getUsage(final Oid p0) throws GSSException;
    
    Oid[] getMechs() throws GSSException;
    
    void add(final GSSName p0, final int p1, final int p2, final Oid p3, final int p4) throws GSSException;
    
    boolean equals(final Object p0);
    
    int hashCode();
}
