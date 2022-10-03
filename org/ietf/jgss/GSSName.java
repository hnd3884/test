package org.ietf.jgss;

public interface GSSName
{
    public static final Oid NT_HOSTBASED_SERVICE = Oid.getInstance("1.2.840.113554.1.2.1.4");
    public static final Oid NT_USER_NAME = Oid.getInstance("1.2.840.113554.1.2.1.1");
    public static final Oid NT_MACHINE_UID_NAME = Oid.getInstance("1.2.840.113554.1.2.1.2");
    public static final Oid NT_STRING_UID_NAME = Oid.getInstance("1.2.840.113554.1.2.1.3");
    public static final Oid NT_ANONYMOUS = Oid.getInstance("1.3.6.1.5.6.3");
    public static final Oid NT_EXPORT_NAME = Oid.getInstance("1.3.6.1.5.6.4");
    
    boolean equals(final GSSName p0) throws GSSException;
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    GSSName canonicalize(final Oid p0) throws GSSException;
    
    byte[] export() throws GSSException;
    
    String toString();
    
    Oid getStringNameType() throws GSSException;
    
    boolean isAnonymous();
    
    boolean isMN();
}
