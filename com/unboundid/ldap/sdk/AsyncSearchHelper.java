package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.util.DebugType;
import java.util.logging.Level;
import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
final class AsyncSearchHelper implements CommonAsyncHelper, IntermediateResponseListener
{
    private static final long serialVersionUID = 1006163445423767824L;
    private final AsyncRequestID asyncRequestID;
    private final AsyncSearchResultListener resultListener;
    private final AtomicBoolean responseReturned;
    private int numEntries;
    private int numReferences;
    private final IntermediateResponseListener intermediateResponseListener;
    private final LDAPConnection connection;
    private final long createTime;
    
    @InternalUseOnly
    AsyncSearchHelper(final LDAPConnection connection, final int messageID, final AsyncSearchResultListener resultListener, final IntermediateResponseListener intermediateResponseListener) {
        this.connection = connection;
        this.resultListener = resultListener;
        this.intermediateResponseListener = intermediateResponseListener;
        this.numEntries = 0;
        this.numReferences = 0;
        this.asyncRequestID = new AsyncRequestID(messageID, connection);
        this.responseReturned = new AtomicBoolean(false);
        this.createTime = System.nanoTime();
    }
    
    @Override
    public AsyncRequestID getAsyncRequestID() {
        return this.asyncRequestID;
    }
    
    @Override
    public LDAPConnection getConnection() {
        return this.connection;
    }
    
    @Override
    public long getCreateTimeNanos() {
        return this.createTime;
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.SEARCH;
    }
    
    int getNumEntries() {
        return this.numEntries;
    }
    
    int getNumReferences() {
        return this.numReferences;
    }
    
    @InternalUseOnly
    @Override
    public void responseReceived(final LDAPResponse response) throws LDAPException {
        if (this.responseReturned.get()) {
            return;
        }
        if (response instanceof ConnectionClosedResponse) {
            if (!this.responseReturned.compareAndSet(false, true)) {
                return;
            }
            final ConnectionClosedResponse ccr = (ConnectionClosedResponse)response;
            final String ccrMessage = ccr.getMessage();
            String message;
            if (ccrMessage == null) {
                message = LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE.get();
            }
            else {
                message = LDAPMessages.ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE_WITH_MESSAGE.get(ccrMessage);
            }
            this.connection.getConnectionStatistics().incrementNumSearchResponses(this.numEntries, this.numReferences, System.nanoTime() - this.createTime);
            final SearchResult searchResult = new SearchResult(this.asyncRequestID.getMessageID(), ccr.getResultCode(), message, null, StaticUtils.NO_STRINGS, this.numEntries, this.numReferences, StaticUtils.NO_CONTROLS);
            this.resultListener.searchResultReceived(this.asyncRequestID, searchResult);
            this.asyncRequestID.setResult(searchResult);
        }
        else if (response instanceof SearchResultEntry) {
            ++this.numEntries;
            this.resultListener.searchEntryReturned((SearchResultEntry)response);
        }
        else if (response instanceof SearchResultReference) {
            ++this.numReferences;
            this.resultListener.searchReferenceReturned((SearchResultReference)response);
        }
        else {
            if (!this.responseReturned.compareAndSet(false, true)) {
                return;
            }
            this.connection.getConnectionStatistics().incrementNumSearchResponses(this.numEntries, this.numReferences, System.nanoTime() - this.createTime);
            final SearchResult searchResult2 = (SearchResult)response;
            searchResult2.setCounts(this.numEntries, null, this.numReferences, null);
            this.resultListener.searchResultReceived(this.asyncRequestID, searchResult2);
            this.asyncRequestID.setResult(searchResult2);
        }
    }
    
    @InternalUseOnly
    @Override
    public void intermediateResponseReturned(final IntermediateResponse intermediateResponse) {
        if (this.intermediateResponseListener == null) {
            Debug.debug(Level.WARNING, DebugType.LDAP, LDAPMessages.WARN_INTERMEDIATE_RESPONSE_WITH_NO_LISTENER.get(String.valueOf(intermediateResponse)));
        }
        else {
            this.intermediateResponseListener.intermediateResponseReturned(intermediateResponse);
        }
    }
}
