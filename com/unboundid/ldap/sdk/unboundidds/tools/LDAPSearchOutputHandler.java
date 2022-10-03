package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
abstract class LDAPSearchOutputHandler
{
    abstract void formatHeader();
    
    abstract void formatSearchResultEntry(final SearchResultEntry p0);
    
    abstract void formatSearchResultReference(final SearchResultReference p0);
    
    abstract void formatResult(final LDAPResult p0);
    
    abstract void formatUnsolicitedNotification(final LDAPConnection p0, final ExtendedResult p1);
}
