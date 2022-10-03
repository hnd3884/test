package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.protocol.SearchResultReferenceProtocolOp;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.protocol.SearchResultEntryProtocolOp;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.ReadOnlySearchRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedSearchOperation extends InterceptedOperation implements InMemoryInterceptedSearchRequest, InMemoryInterceptedSearchResult
{
    private SearchRequest searchRequest;
    private LDAPResult searchResult;
    
    InterceptedSearchOperation(final LDAPListenerClientConnection clientConnection, final int messageID, final SearchRequestProtocolOp requestOp, final Control... requestControls) {
        super(clientConnection, messageID);
        this.searchRequest = requestOp.toSearchRequest(requestControls);
        this.searchResult = null;
    }
    
    @Override
    public ReadOnlySearchRequest getRequest() {
        return this.searchRequest;
    }
    
    @Override
    public void setRequest(final SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
    }
    
    @Override
    public LDAPResult getResult() {
        return this.searchResult;
    }
    
    @Override
    public void setResult(final LDAPResult searchResult) {
        this.searchResult = searchResult;
    }
    
    @Override
    public void sendSearchEntry(final Entry entry) throws LDAPException {
        Control[] controls;
        if (entry instanceof SearchResultEntry) {
            controls = ((SearchResultEntry)entry).getControls();
        }
        else {
            controls = null;
        }
        this.getClientConnection().sendSearchResultEntry(this.getMessageID(), new SearchResultEntryProtocolOp(entry), controls);
    }
    
    @Override
    public void sendSearchReference(final SearchResultReference reference) throws LDAPException {
        this.getClientConnection().sendSearchResultReference(this.getMessageID(), new SearchResultReferenceProtocolOp(reference), reference.getControls());
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedSearchOperation(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.searchRequest);
        buffer.append(", result=");
        buffer.append(this.searchResult);
        buffer.append(')');
    }
}
