package com.unboundid.ldap.sdk;

import com.unboundid.ldif.LDIFModifyChangeRecord;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ReadOnlyModifyRequest extends ReadOnlyLDAPRequest
{
    String getDN();
    
    List<Modification> getModifications();
    
    ModifyRequest duplicate();
    
    ModifyRequest duplicate(final Control[] p0);
    
    LDIFModifyChangeRecord toLDIFChangeRecord();
    
    String[] toLDIF();
    
    String toLDIFString();
}
