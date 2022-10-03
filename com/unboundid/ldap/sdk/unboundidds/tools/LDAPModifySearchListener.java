package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.DN;
import java.util.Set;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldif.LDIFWriter;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.util.FixedRateBarrier;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SearchResultListener;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class LDAPModifySearchListener implements SearchResultListener
{
    private static final long serialVersionUID = -583082242208798146L;
    private final Filter searchFilter;
    private final FixedRateBarrier rateLimiter;
    private final LDAPConnectionPool connectionPool;
    private final LDAPModify ldapModify;
    private final LDIFModifyChangeRecord sourceChangeRecord;
    private final LDIFWriter rejectWriter;
    private final List<Control> modifyControls;
    private volatile ResultCode resultCode;
    private final Set<DN> processedEntryDNs;
    
    LDAPModifySearchListener(final LDAPModify ldapModify, final LDIFModifyChangeRecord sourceChangeRecord, final Filter searchFilter, final List<Control> modifyControls, final LDAPConnectionPool connectionPool, final FixedRateBarrier rateLimiter, final LDIFWriter rejectWriter, final Set<DN> processedEntryDNs) {
        this.ldapModify = ldapModify;
        this.sourceChangeRecord = sourceChangeRecord;
        this.searchFilter = searchFilter;
        this.modifyControls = modifyControls;
        this.connectionPool = connectionPool;
        this.rateLimiter = rateLimiter;
        this.rejectWriter = rejectWriter;
        this.processedEntryDNs = processedEntryDNs;
        this.resultCode = ResultCode.SUCCESS;
    }
    
    ResultCode getResultCode() {
        return this.resultCode;
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        DN parsedDN = null;
        try {
            parsedDN = searchEntry.getParsedDN();
            if (this.processedEntryDNs.contains(parsedDN)) {
                return;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        if (this.rateLimiter != null) {
            this.rateLimiter.await();
        }
        final LDIFModifyChangeRecord changeRecordFromSearchEntry = new LDIFModifyChangeRecord(searchEntry.getDN(), this.sourceChangeRecord.getModifications(), this.sourceChangeRecord.getControls());
        try {
            final ResultCode rc = this.ldapModify.doModify(changeRecordFromSearchEntry, this.modifyControls, this.connectionPool, null, this.rejectWriter);
            if (rc != ResultCode.SUCCESS && (this.resultCode == ResultCode.SUCCESS || this.resultCode == ResultCode.NO_OPERATION)) {
                this.resultCode = rc;
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (this.resultCode == ResultCode.SUCCESS || this.resultCode == ResultCode.NO_OPERATION) {
                this.resultCode = le.getResultCode();
            }
        }
        if (parsedDN != null) {
            this.processedEntryDNs.add(parsedDN);
        }
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        final StringBuilder urls = new StringBuilder();
        for (final String url : searchReference.getReferralURLs()) {
            if (urls.length() > 0) {
                urls.append(", ");
            }
            urls.append(url);
        }
        final String comment = ToolMessages.ERR_LDAPMODIFY_SEARCH_LISTENER_REFERRAL.get(this.sourceChangeRecord.getDN(), String.valueOf(this.searchFilter), urls.toString());
        this.ldapModify.writeRejectedChange(this.rejectWriter, comment, this.sourceChangeRecord);
    }
}
