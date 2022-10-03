package com.unboundid.ldap.sdk;

import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ReadOnlySearchRequest extends ReadOnlyLDAPRequest
{
    String getBaseDN();
    
    SearchScope getScope();
    
    DereferencePolicy getDereferencePolicy();
    
    int getSizeLimit();
    
    int getTimeLimitSeconds();
    
    boolean typesOnly();
    
    Filter getFilter();
    
    List<String> getAttributeList();
    
    SearchRequest duplicate();
    
    SearchRequest duplicate(final Control[] p0);
}
