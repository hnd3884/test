package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.SearchResultReferenceProtocolOp;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.ReadOnlySearchRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedSearchReference extends InterceptedOperation implements InMemoryInterceptedSearchReference
{
    private final ReadOnlySearchRequest searchRequest;
    private SearchResultReference reference;
    
    InterceptedSearchReference(final InterceptedSearchOperation op, final SearchResultReferenceProtocolOp reference, final Control... requestControls) {
        super(op);
        this.searchRequest = op.getRequest();
        this.reference = reference.toSearchResultReference(requestControls);
    }
    
    @Override
    public ReadOnlySearchRequest getRequest() {
        return this.searchRequest;
    }
    
    @Override
    public SearchResultReference getSearchReference() {
        return this.reference;
    }
    
    @Override
    public void setSearchReference(final SearchResultReference reference) {
        this.reference = reference;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedSearchReference(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.searchRequest);
        buffer.append(", reference=");
        buffer.append(this.reference);
        buffer.append(')');
    }
}
