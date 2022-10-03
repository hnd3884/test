package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Closeable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface FullLDAPInterface extends LDAPInterface, Closeable
{
    void close();
    
    BindResult bind(final String p0, final String p1) throws LDAPException;
    
    BindResult bind(final BindRequest p0) throws LDAPException;
    
    ExtendedResult processExtendedOperation(final String p0) throws LDAPException;
    
    ExtendedResult processExtendedOperation(final String p0, final ASN1OctetString p1) throws LDAPException;
    
    ExtendedResult processExtendedOperation(final ExtendedRequest p0) throws LDAPException;
}
