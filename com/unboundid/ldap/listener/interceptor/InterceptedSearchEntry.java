package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.SearchResultEntryProtocolOp;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.ReadOnlySearchRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedSearchEntry extends InterceptedOperation implements InMemoryInterceptedSearchEntry
{
    private final ReadOnlySearchRequest searchRequest;
    private SearchResultEntry entry;
    
    InterceptedSearchEntry(final InterceptedSearchOperation op, final SearchResultEntryProtocolOp entry, final Control... requestControls) {
        super(op);
        this.searchRequest = op.getRequest();
        this.entry = entry.toSearchResultEntry(requestControls);
    }
    
    @Override
    public ReadOnlySearchRequest getRequest() {
        return this.searchRequest;
    }
    
    @Override
    public SearchResultEntry getSearchEntry() {
        return this.entry;
    }
    
    @Override
    public void setSearchEntry(final Entry entry) {
        if (entry == null) {
            this.entry = null;
        }
        else if (entry instanceof SearchResultEntry) {
            this.entry = (SearchResultEntry)entry;
        }
        else {
            this.entry = new SearchResultEntry(entry, new Control[0]);
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedSearchEntry(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.searchRequest);
        buffer.append(", entry=");
        buffer.append(this.entry);
        buffer.append(')');
    }
}
