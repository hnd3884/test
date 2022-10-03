package com.unboundid.ldap.sdk;

import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ReadOnlyDeleteRequest extends ReadOnlyLDAPRequest
{
    String getDN();
    
    DeleteRequest duplicate();
    
    DeleteRequest duplicate(final Control[] p0);
    
    LDIFDeleteChangeRecord toLDIFChangeRecord();
    
    String[] toLDIF();
    
    String toLDIFString();
}
