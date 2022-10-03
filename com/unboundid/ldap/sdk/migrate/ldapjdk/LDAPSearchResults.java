package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.util.InternalUseOnly;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.NoSuchElementException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.Control;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.ldap.sdk.AsyncRequestID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.Mutable;
import com.unboundid.ldap.sdk.AsyncSearchResultListener;
import java.util.Enumeration;

@Mutable
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class LDAPSearchResults implements Enumeration<Object>, AsyncSearchResultListener
{
    private static final long serialVersionUID = 7884355145560496230L;
    private volatile AsyncRequestID asyncRequestID;
    private final AtomicBoolean searchAbandoned;
    private final AtomicBoolean searchDone;
    private final AtomicInteger count;
    private final AtomicReference<Control[]> lastControls;
    private final AtomicReference<Object> nextResult;
    private final AtomicReference<SearchResult> searchResult;
    private final long maxWaitTime;
    private final LinkedBlockingQueue<Object> resultQueue;
    
    public LDAPSearchResults() {
        this(0L);
    }
    
    public LDAPSearchResults(final long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
        this.asyncRequestID = null;
        this.searchAbandoned = new AtomicBoolean(false);
        this.searchDone = new AtomicBoolean(false);
        this.count = new AtomicInteger(0);
        this.lastControls = new AtomicReference<Control[]>();
        this.nextResult = new AtomicReference<Object>();
        this.searchResult = new AtomicReference<SearchResult>();
        this.resultQueue = new LinkedBlockingQueue<Object>(50);
    }
    
    void setAbandoned() {
        this.searchAbandoned.set(true);
    }
    
    AsyncRequestID getAsyncRequestID() {
        return this.asyncRequestID;
    }
    
    void setAsyncRequestID(final AsyncRequestID asyncRequestID) {
        this.asyncRequestID = asyncRequestID;
    }
    
    private Object nextObject() {
        Object o = this.nextResult.get();
        if (o != null) {
            return o;
        }
        o = this.resultQueue.poll();
        if (o != null) {
            this.nextResult.set(o);
            return o;
        }
        if (this.searchDone.get() || this.searchAbandoned.get()) {
            return null;
        }
        try {
            long stopWaitTime;
            if (this.maxWaitTime > 0L) {
                stopWaitTime = System.currentTimeMillis() + this.maxWaitTime;
            }
            else {
                stopWaitTime = Long.MAX_VALUE;
            }
            while (!this.searchAbandoned.get() && System.currentTimeMillis() < stopWaitTime) {
                o = this.resultQueue.poll(100L, TimeUnit.MILLISECONDS);
                if (o != null) {
                    break;
                }
            }
            if (o == null) {
                if (this.searchAbandoned.get()) {
                    o = new SearchResult(-1, ResultCode.USER_CANCELED, null, null, null, 0, 0, null);
                    this.count.incrementAndGet();
                }
                else {
                    o = new SearchResult(-1, ResultCode.TIMEOUT, null, null, null, 0, 0, null);
                    this.count.incrementAndGet();
                }
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            o = new SearchResult(-1, ResultCode.USER_CANCELED, null, null, null, 0, 0, null);
            this.count.incrementAndGet();
        }
        this.nextResult.set(o);
        return o;
    }
    
    @Override
    public boolean hasMoreElements() {
        final Object o = this.nextObject();
        if (o == null) {
            return false;
        }
        if (o instanceof SearchResult) {
            final SearchResult r = (SearchResult)o;
            if (r.getResultCode().equals(ResultCode.SUCCESS)) {
                this.lastControls.set(r.getResponseControls());
                this.searchDone.set(true);
                this.nextResult.set(null);
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Object nextElement() throws NoSuchElementException {
        final Object o = this.nextObject();
        if (o == null) {
            throw new NoSuchElementException();
        }
        this.nextResult.set(null);
        this.count.decrementAndGet();
        if (o instanceof SearchResultEntry) {
            final SearchResultEntry e = (SearchResultEntry)o;
            this.lastControls.set(e.getControls());
            return new LDAPEntry(e);
        }
        if (o instanceof SearchResultReference) {
            final SearchResultReference r = (SearchResultReference)o;
            this.lastControls.set(r.getControls());
            return new LDAPReferralException(r);
        }
        final SearchResult r2 = (SearchResult)o;
        this.searchDone.set(true);
        this.nextResult.set(null);
        this.lastControls.set(r2.getResponseControls());
        return new LDAPException(r2.getDiagnosticMessage(), r2.getResultCode().intValue(), r2.getDiagnosticMessage(), r2.getMatchedDN());
    }
    
    public LDAPEntry next() throws LDAPException {
        if (!this.hasMoreElements()) {
            throw new LDAPException(null, 94);
        }
        final Object o = this.nextElement();
        if (o instanceof LDAPEntry) {
            return (LDAPEntry)o;
        }
        throw (LDAPException)o;
    }
    
    public int getCount() {
        return this.count.get();
    }
    
    public LDAPControl[] getResponseControls() {
        final Control[] controls = this.lastControls.get();
        if (controls == null || controls.length == 0) {
            return null;
        }
        return LDAPControl.toLDAPControls(controls);
    }
    
    @InternalUseOnly
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        if (this.searchDone.get()) {
            return;
        }
        try {
            this.resultQueue.put(searchEntry);
            this.count.incrementAndGet();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            this.searchDone.set(true);
        }
    }
    
    @InternalUseOnly
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        if (this.searchDone.get()) {
            return;
        }
        try {
            this.resultQueue.put(searchReference);
            this.count.incrementAndGet();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            this.searchDone.set(true);
        }
    }
    
    @InternalUseOnly
    @Override
    public void searchResultReceived(final AsyncRequestID requestID, final SearchResult searchResult) {
        if (this.searchDone.get()) {
            return;
        }
        try {
            this.resultQueue.put(searchResult);
            if (!searchResult.getResultCode().equals(ResultCode.SUCCESS)) {
                this.count.incrementAndGet();
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            this.searchDone.set(true);
        }
    }
}
