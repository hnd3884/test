package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.DN;
import java.util.TreeSet;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SearchResultListener;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class LDAPDeleteSearchListener implements SearchResultListener
{
    private static final long serialVersionUID = 2185398520482379634L;
    private final AtomicReference<ResultCode> returnCode;
    private final LDAPDelete ldapDelete;
    private final String baseDN;
    private final String filter;
    private final TreeSet<DN> dnSet;
    
    LDAPDeleteSearchListener(final LDAPDelete ldapDelete, final TreeSet<DN> dnSet, final String baseDN, final String filter, final AtomicReference<ResultCode> returnCode) {
        this.ldapDelete = ldapDelete;
        this.baseDN = baseDN;
        this.filter = filter;
        this.dnSet = dnSet;
        this.returnCode = returnCode;
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        try {
            this.dnSet.add(searchEntry.getParsedDN());
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            this.ldapDelete.commentToErr(ToolMessages.ERR_LDAPDELETE_SEARCH_LISTENER_CANNOT_PARSE_ENTRY_DN.get(this.baseDN, this.filter, searchEntry.getDN(), StaticUtils.getExceptionMessage(e)));
            this.returnCode.compareAndSet(null, e.getResultCode());
        }
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        this.returnCode.compareAndSet(null, ResultCode.REFERRAL);
        this.ldapDelete.commentToErr(ToolMessages.ERR_LDAPDELETE_SEARCH_LISTENER_REFERENCE.get(this.baseDN, this.filter, String.valueOf(searchReference)));
    }
}
