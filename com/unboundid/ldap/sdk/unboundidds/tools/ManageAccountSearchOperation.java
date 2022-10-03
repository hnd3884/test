package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.DN;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.unboundid.ldap.sdk.AsyncRequestID;
import com.unboundid.ldap.sdk.AsyncSearchResultListener;

final class ManageAccountSearchOperation implements AsyncSearchResultListener
{
    private static final long serialVersionUID = 5568681845030018155L;
    private volatile AsyncRequestID asyncRequestID;
    private final AtomicInteger entryCounter;
    private final AtomicInteger referenceCounter;
    private final ConcurrentHashMap<DN, DN> dnsProcessed;
    private final int simplePageSize;
    private final LDAPConnectionPool pool;
    private final ManageAccount manageAccount;
    private final ManageAccountProcessor manageAccountProcessor;
    private final SearchRequest searchRequest;
    
    ManageAccountSearchOperation(final ManageAccount manageAccount, final ManageAccountProcessor manageAccountProcessor, final LDAPConnectionPool pool, final String baseDN, final Filter filter, final int simplePageSize) {
        this.manageAccount = manageAccount;
        this.manageAccountProcessor = manageAccountProcessor;
        this.pool = pool;
        this.simplePageSize = simplePageSize;
        (this.searchRequest = new SearchRequest(this, baseDN, SearchScope.SUB, filter, new String[] { "1.1" })).setResponseTimeoutMillis(3600000L);
        this.dnsProcessed = new ConcurrentHashMap<DN, DN>(StaticUtils.computeMapCapacity(10));
        this.entryCounter = new AtomicInteger(0);
        this.referenceCounter = new AtomicInteger(0);
    }
    
    void doSearch() {
        ASN1OctetString cookie = null;
        while (true) {
            if (this.simplePageSize > 0) {
                this.searchRequest.setControls(new SimplePagedResultsControl(this.simplePageSize, cookie, false));
            }
            final SearchResult searchResult = this.doSearchWithRetry();
            if (searchResult.getResultCode() != ResultCode.SUCCESS) {
                break;
            }
            if (this.simplePageSize <= 0) {
                break;
            }
            try {
                final SimplePagedResultsControl responseControl = SimplePagedResultsControl.get(searchResult);
                if (!responseControl.moreResultsToReturn()) {
                    break;
                }
                cookie = responseControl.getCookie();
            }
            catch (final Exception e) {
                this.manageAccountProcessor.handleMessage(ToolMessages.ERR_MANAGE_ACCT_SEARCH_OP_ERROR_READING_PAGE_RESPONSE.get(String.valueOf(searchResult), String.valueOf(this.searchRequest.getFilter()), StaticUtils.getExceptionMessage(e)), true);
            }
        }
    }
    
    private SearchResult doSearchWithRetry() {
        this.dnsProcessed.clear();
        this.entryCounter.set(0);
        this.referenceCounter.set(0);
        LDAPConnection conn;
        try {
            conn = this.pool.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            final String message = ToolMessages.ERR_MANAGE_ACCT_SEARCH_OP_CANNOT_GET_CONNECTION.get(String.valueOf(this.searchRequest), StaticUtils.getExceptionMessage(le));
            this.manageAccountProcessor.handleMessage(message, true);
            return new SearchResult(this.searchRequest.getLastMessageID(), ResultCode.CONNECT_ERROR, message, null, null, this.entryCounter.get(), this.referenceCounter.get(), null);
        }
        boolean alreadyReleased = false;
        boolean releaseAsDefunct = true;
        try {
            LDAPResult result = null;
            try {
                this.asyncRequestID = conn.asyncSearch(this.searchRequest);
                result = this.asyncRequestID.get();
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                result = le2.toLDAPResult();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            finally {
                this.asyncRequestID = null;
            }
            if (result != null && result.getResultCode().isConnectionUsable()) {
                releaseAsDefunct = false;
                if (result.getResultCode() == ResultCode.SUCCESS) {
                    if (this.simplePageSize > 0) {
                        this.manageAccountProcessor.handleMessage(ToolMessages.INFO_MANAGE_ACCT_SEARCH_OP_SUCCESSFUL_PAGE.get(String.valueOf(this.searchRequest.getFilter()), this.entryCounter.get()), false);
                    }
                    else {
                        this.manageAccountProcessor.handleMessage(ToolMessages.INFO_MANAGE_ACCT_SEARCH_OP_SUCCESSFUL_FULL.get(String.valueOf(this.searchRequest.getFilter()), this.entryCounter.get()), false);
                    }
                }
                else {
                    this.manageAccountProcessor.handleMessage(ToolMessages.ERR_MANAGE_ACCT_SEARCH_OP_FAILED_NO_RETRY.get(String.valueOf(this.searchRequest.getFilter()), result.getResultCode(), result.getDiagnosticMessage()), true);
                }
                if (result instanceof SearchResult) {
                    return (SearchResult)result;
                }
                return new SearchResult(result.getMessageID(), result.getResultCode(), result.getDiagnosticMessage(), result.getMatchedDN(), result.getReferralURLs(), this.entryCounter.get(), this.referenceCounter.get(), result.getResponseControls());
            }
            else {
                this.entryCounter.set(0);
                this.referenceCounter.set(0);
                try {
                    alreadyReleased = true;
                    conn = this.pool.replaceDefunctConnection(conn);
                    alreadyReleased = false;
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    final String message2 = ToolMessages.ERR_MANAGE_ACCT_SEARCH_OP_CANNOT_GET_CONNECTION.get(String.valueOf(this.searchRequest), StaticUtils.getExceptionMessage(le2));
                    this.manageAccountProcessor.handleMessage(message2, true);
                    return new SearchResult(this.searchRequest.getLastMessageID(), ResultCode.CONNECT_ERROR, message2, null, null, this.entryCounter.get(), this.referenceCounter.get(), null);
                }
                try {
                    this.asyncRequestID = conn.asyncSearch(this.searchRequest);
                    result = this.asyncRequestID.get();
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    result = le2.toLDAPResult();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    result = new SearchResult(this.searchRequest.getLastMessageID(), ResultCode.LOCAL_ERROR, ToolMessages.ERR_MANAGE_ACCT_SEARCH_OP_EXCEPTION.get(String.valueOf(this.searchRequest), StaticUtils.getExceptionMessage(e)), null, null, this.entryCounter.get(), this.referenceCounter.get(), null);
                }
                finally {
                    this.asyncRequestID = null;
                }
                if (result.getResultCode() == ResultCode.SUCCESS) {
                    if (this.simplePageSize > 0) {
                        this.manageAccountProcessor.handleMessage(ToolMessages.INFO_MANAGE_ACCT_SEARCH_OP_SUCCESSFUL_PAGE.get(String.valueOf(this.searchRequest.getFilter()), this.entryCounter.get()), false);
                    }
                    else {
                        this.manageAccountProcessor.handleMessage(ToolMessages.INFO_MANAGE_ACCT_SEARCH_OP_SUCCESSFUL_FULL.get(String.valueOf(this.searchRequest.getFilter()), this.entryCounter.get()), false);
                    }
                }
                else {
                    this.manageAccountProcessor.handleMessage(ToolMessages.ERR_MANAGE_ACCT_SEARCH_OP_FAILED_SECOND_ATTEMPT.get(String.valueOf(this.searchRequest.getFilter()), result.getResultCode(), result.getDiagnosticMessage()), true);
                }
                if (result.getResultCode().isConnectionUsable()) {
                    releaseAsDefunct = false;
                }
                if (result instanceof SearchResult) {
                    return (SearchResult)result;
                }
                return new SearchResult(result.getMessageID(), result.getResultCode(), result.getDiagnosticMessage(), result.getMatchedDN(), result.getReferralURLs(), this.entryCounter.get(), this.referenceCounter.get(), result.getResponseControls());
            }
        }
        finally {
            if (!alreadyReleased) {
                if (releaseAsDefunct) {
                    this.pool.releaseDefunctConnection(conn);
                }
                else {
                    this.pool.releaseConnection(conn);
                }
            }
        }
    }
    
    void cancelSearch() {
        if (this.asyncRequestID != null) {
            this.asyncRequestID.cancel(true);
        }
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        this.entryCounter.incrementAndGet();
        DN parsedDN = null;
        try {
            parsedDN = searchEntry.getParsedDN();
            if (this.dnsProcessed.containsKey(parsedDN)) {
                return;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        this.manageAccountProcessor.process(searchEntry.getDN());
        if (parsedDN != null) {
            this.dnsProcessed.put(parsedDN, parsedDN);
        }
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        this.referenceCounter.incrementAndGet();
        this.manageAccountProcessor.handleMessage(ToolMessages.WARN_MANAGE_ACCT_SEARCH_OP_REFERRAL.get(String.valueOf(this.searchRequest.getFilter()), String.valueOf(searchReference)), true);
    }
    
    @Override
    public void searchResultReceived(final AsyncRequestID requestID, final SearchResult searchResult) {
    }
}
