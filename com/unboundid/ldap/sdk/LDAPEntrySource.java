package com.unboundid.ldap.sdk;

import com.unboundid.util.InternalUseOnly;
import com.unboundid.util.Debug;
import java.util.concurrent.TimeUnit;
import com.unboundid.util.Validator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPEntrySource extends EntrySource implements AsyncSearchResultListener
{
    private static final String END_OF_RESULTS = "END OF RESULTS";
    private static final long serialVersionUID = 1080386705549149135L;
    private final AsyncRequestID asyncRequestID;
    private final AtomicBoolean closed;
    private final AtomicReference<SearchResult> searchResult;
    private final boolean closeConnection;
    private final LDAPConnection connection;
    private final LinkedBlockingQueue<Object> queue;
    
    public LDAPEntrySource(final LDAPConnection connection, final SearchRequest searchRequest, final boolean closeConnection) throws LDAPException {
        this(connection, searchRequest, closeConnection, 100);
    }
    
    public LDAPEntrySource(final LDAPConnection connection, final SearchRequest searchRequest, final boolean closeConnection, final int queueSize) throws LDAPException {
        Validator.ensureNotNull(connection, searchRequest);
        Validator.ensureTrue(queueSize > 0, "LDAPEntrySource.queueSize must be greater than 0.");
        this.connection = connection;
        this.closeConnection = closeConnection;
        if (searchRequest.getSearchResultListener() != null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_LDAP_ENTRY_SOURCE_REQUEST_HAS_LISTENER.get());
        }
        this.closed = new AtomicBoolean(false);
        this.searchResult = new AtomicReference<SearchResult>();
        this.queue = new LinkedBlockingQueue<Object>(queueSize);
        final SearchRequest r = new SearchRequest(this, searchRequest.getControls(), searchRequest.getBaseDN(), searchRequest.getScope(), searchRequest.getDereferencePolicy(), searchRequest.getSizeLimit(), searchRequest.getTimeLimitSeconds(), searchRequest.typesOnly(), searchRequest.getFilter(), searchRequest.getAttributes());
        this.asyncRequestID = connection.asyncSearch(r);
    }
    
    @Override
    public Entry nextEntry() throws EntrySourceException {
        while (!this.closed.get() || !this.queue.isEmpty()) {
            Object o;
            try {
                o = this.queue.poll(10L, TimeUnit.MILLISECONDS);
            }
            catch (final InterruptedException ie) {
                Debug.debugException(ie);
                Thread.currentThread().interrupt();
                throw new EntrySourceException(true, LDAPMessages.ERR_LDAP_ENTRY_SOURCE_NEXT_ENTRY_INTERRUPTED.get(), ie);
            }
            if (o != null) {
                if (o == "END OF RESULTS") {
                    return null;
                }
                if (o instanceof Entry) {
                    return (Entry)o;
                }
                throw (EntrySourceException)o;
            }
        }
        return null;
    }
    
    @Override
    public void close() {
        this.closeInternal(true);
    }
    
    private void closeInternal(final boolean abandon) {
        this.addToQueue("END OF RESULTS");
        if (this.closed.compareAndSet(false, true)) {
            if (abandon) {
                try {
                    this.connection.abandon(this.asyncRequestID);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
            if (this.closeConnection) {
                this.connection.close();
            }
        }
    }
    
    public SearchResult getSearchResult() {
        return this.searchResult.get();
    }
    
    @InternalUseOnly
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        this.addToQueue(searchEntry);
    }
    
    @InternalUseOnly
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        this.addToQueue(new SearchResultReferenceEntrySourceException(searchReference));
    }
    
    @InternalUseOnly
    @Override
    public void searchResultReceived(final AsyncRequestID requestID, final SearchResult searchResult) {
        this.searchResult.set(searchResult);
        if (!searchResult.getResultCode().equals(ResultCode.SUCCESS)) {
            this.addToQueue(new EntrySourceException(false, new LDAPSearchException(searchResult)));
        }
        this.closeInternal(false);
    }
    
    private void addToQueue(final Object o) {
        while (!this.closed.get()) {
            try {
                if (this.queue.offer(o, 100L, TimeUnit.MILLISECONDS)) {
                    return;
                }
                continue;
            }
            catch (final InterruptedException ie) {
                Debug.debugException(ie);
            }
        }
    }
}
