package com.unboundid.ldif;

import com.unboundid.util.ByteStringBuffer;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface LDIFRecord extends Serializable
{
    String getDN();
    
    DN getParsedDN() throws LDAPException;
    
    String[] toLDIF();
    
    String[] toLDIF(final int p0);
    
    void toLDIF(final ByteStringBuffer p0);
    
    void toLDIF(final ByteStringBuffer p0, final int p1);
    
    String toLDIFString();
    
    String toLDIFString(final int p0);
    
    void toLDIFString(final StringBuilder p0);
    
    void toLDIFString(final StringBuilder p0, final int p1);
    
    String toString();
    
    void toString(final StringBuilder p0);
}
