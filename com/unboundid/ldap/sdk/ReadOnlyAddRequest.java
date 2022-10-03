package com.unboundid.ldap.sdk;

import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldap.matchingrules.MatchingRule;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ReadOnlyAddRequest extends ReadOnlyLDAPRequest
{
    String getDN();
    
    List<Attribute> getAttributes();
    
    Attribute getAttribute(final String p0);
    
    boolean hasAttribute(final String p0);
    
    boolean hasAttribute(final Attribute p0);
    
    boolean hasAttributeValue(final String p0, final String p1);
    
    boolean hasAttributeValue(final String p0, final String p1, final MatchingRule p2);
    
    boolean hasAttributeValue(final String p0, final byte[] p1);
    
    boolean hasAttributeValue(final String p0, final byte[] p1, final MatchingRule p2);
    
    boolean hasObjectClass(final String p0);
    
    Entry toEntry();
    
    AddRequest duplicate();
    
    AddRequest duplicate(final Control[] p0);
    
    LDIFAddChangeRecord toLDIFChangeRecord();
    
    String[] toLDIF();
    
    String toLDIFString();
}
