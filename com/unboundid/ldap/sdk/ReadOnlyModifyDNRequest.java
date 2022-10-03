package com.unboundid.ldap.sdk;

import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ReadOnlyModifyDNRequest extends ReadOnlyLDAPRequest
{
    String getDN();
    
    String getNewRDN();
    
    boolean deleteOldRDN();
    
    String getNewSuperiorDN();
    
    ModifyDNRequest duplicate();
    
    ModifyDNRequest duplicate(final Control[] p0);
    
    LDIFModifyDNChangeRecord toLDIFChangeRecord();
    
    String[] toLDIF();
    
    String toLDIFString();
}
