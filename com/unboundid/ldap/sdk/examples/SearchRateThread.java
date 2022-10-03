package com.unboundid.ldap.sdk.examples;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV2RequestControl;
import com.unboundid.ldap.sdk.LDAPURL;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.util.ValuePattern;
import java.util.concurrent.Semaphore;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.util.ResultCodeCounter;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.util.FixedRateBarrier;
import java.util.concurrent.CyclicBarrier;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.ldap.sdk.SearchResultListener;

final class SearchRateThread extends Thread implements SearchResultListener
{
    private static final long serialVersionUID = -6714705986829223364L;
    private final AtomicBoolean stopRequested;
    private final AtomicInteger runningThreads;
    private final AtomicLong entryCounter;
    private final AtomicLong errorCounter;
    private final AtomicLong remainingIterationsBeforeReconnect;
    private final AtomicLong searchCounter;
    private final AtomicLong searchDurations;
    private final AtomicReference<Thread> searchThread;
    private final boolean async;
    private LDAPConnection connection;
    private final AtomicReference<ResultCode> resultCode;
    private final CyclicBarrier startBarrier;
    private final FixedRateBarrier fixedRateBarrier;
    private final Integer simplePageSize;
    private final List<Control> requestControls;
    private final long iterationsBeforeReconnect;
    private final ResultCodeCounter rcCounter;
    private final SearchRate searchRate;
    private final SearchRequest searchRequest;
    private final SearchScope scope;
    private final Semaphore asyncSemaphore;
    private final String[] attributes;
    private final ValuePattern authzID;
    private final ValuePattern baseDN;
    private final ValuePattern filter;
    private final ValuePattern ldapURL;
    
    SearchRateThread(final SearchRate searchRate, final int threadNumber, final LDAPConnection connection, final boolean async, final ValuePattern baseDN, final SearchScope scope, final DereferencePolicy dereferencePolicy, final int sizeLimit, final int timeLimitSeconds, final boolean typesOnly, final ValuePattern filter, final String[] attributes, final ValuePattern ldapURL, final ValuePattern authzID, final Integer simplePageSize, final List<Control> requestControls, final long iterationsBeforeReconnect, final AtomicInteger runningThreads, final CyclicBarrier startBarrier, final AtomicLong searchCounter, final AtomicLong entryCounter, final AtomicLong searchDurations, final AtomicLong errorCounter, final ResultCodeCounter rcCounter, final FixedRateBarrier rateBarrier, final Semaphore asyncSemaphore) {
        this.setName("SearchRate Thread " + threadNumber);
        this.setDaemon(true);
        this.searchRate = searchRate;
        this.connection = connection;
        this.async = async;
        this.baseDN = baseDN;
        this.scope = scope;
        this.filter = filter;
        this.attributes = attributes;
        this.ldapURL = ldapURL;
        this.authzID = authzID;
        this.simplePageSize = simplePageSize;
        this.requestControls = requestControls;
        this.iterationsBeforeReconnect = iterationsBeforeReconnect;
        this.searchCounter = searchCounter;
        this.entryCounter = entryCounter;
        this.searchDurations = searchDurations;
        this.errorCounter = errorCounter;
        this.rcCounter = rcCounter;
        this.runningThreads = runningThreads;
        this.startBarrier = startBarrier;
        this.asyncSemaphore = asyncSemaphore;
        this.fixedRateBarrier = rateBarrier;
        if (iterationsBeforeReconnect > 0L) {
            this.remainingIterationsBeforeReconnect = new AtomicLong(iterationsBeforeReconnect);
        }
        else {
            this.remainingIterationsBeforeReconnect = null;
        }
        connection.setConnectionName("search-" + threadNumber);
        this.resultCode = new AtomicReference<ResultCode>(null);
        this.searchThread = new AtomicReference<Thread>(null);
        this.stopRequested = new AtomicBoolean(false);
        this.searchRequest = new SearchRequest(this, "", scope, dereferencePolicy, sizeLimit, timeLimitSeconds, typesOnly, Filter.createPresenceFilter("objectClass"), attributes);
    }
    
    @Override
    public void run() {
        try {
            this.searchThread.set(Thread.currentThread());
            this.runningThreads.incrementAndGet();
            try {
                this.startBarrier.await();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            while (!this.stopRequested.get()) {
                if (this.iterationsBeforeReconnect > 0L && this.remainingIterationsBeforeReconnect.decrementAndGet() <= 0L) {
                    this.remainingIterationsBeforeReconnect.set(this.iterationsBeforeReconnect);
                    if (this.connection != null) {
                        this.connection.close();
                        this.connection = null;
                    }
                }
                if (this.connection == null) {
                    try {
                        this.connection = this.searchRate.getConnection();
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        this.errorCounter.incrementAndGet();
                        final ResultCode rc = le.getResultCode();
                        this.rcCounter.increment(rc);
                        this.resultCode.compareAndSet(null, rc);
                        if (this.fixedRateBarrier == null) {
                            continue;
                        }
                        this.fixedRateBarrier.await();
                        continue;
                    }
                }
                if (this.fixedRateBarrier != null) {
                    this.fixedRateBarrier.await();
                }
                ProxiedAuthorizationV2RequestControl proxyControl = null;
                if (this.async) {
                    if (this.asyncSemaphore != null) {
                        try {
                            this.asyncSemaphore.acquire();
                        }
                        catch (final Exception e2) {
                            Debug.debugException(e2);
                            this.errorCounter.incrementAndGet();
                            final ResultCode rc2 = ResultCode.LOCAL_ERROR;
                            this.rcCounter.increment(rc2);
                            this.resultCode.compareAndSet(null, rc2);
                            continue;
                        }
                    }
                    final SearchRateAsyncListener listener = new SearchRateAsyncListener(this.searchCounter, this.entryCounter, this.searchDurations, this.errorCounter, this.rcCounter, this.asyncSemaphore, this.resultCode);
                    try {
                        SearchRequest r;
                        if (this.ldapURL == null) {
                            r = new SearchRequest(listener, this.baseDN.nextValue(), this.scope, this.searchRequest.getDereferencePolicy(), this.searchRequest.getSizeLimit(), this.searchRequest.getTimeLimitSeconds(), this.searchRequest.typesOnly(), this.filter.nextValue(), this.attributes);
                        }
                        else {
                            final LDAPURL url = new LDAPURL(this.ldapURL.nextValue());
                            r = new SearchRequest(listener, url.getBaseDN().toString(), url.getScope(), this.searchRequest.getDereferencePolicy(), this.searchRequest.getSizeLimit(), this.searchRequest.getTimeLimitSeconds(), this.searchRequest.typesOnly(), url.getFilter(), url.getAttributes());
                        }
                        r.setControls(this.requestControls);
                        if (this.authzID != null) {
                            r.addControl(new ProxiedAuthorizationV2RequestControl(this.authzID.nextValue()));
                        }
                        this.connection.asyncSearch(r);
                    }
                    catch (final LDAPException le2) {
                        Debug.debugException(le2);
                        this.errorCounter.incrementAndGet();
                        final ResultCode rc3 = le2.getResultCode();
                        this.rcCounter.increment(rc3);
                        this.resultCode.compareAndSet(null, rc3);
                        if (this.asyncSemaphore == null) {
                            continue;
                        }
                        this.asyncSemaphore.release();
                    }
                }
                else {
                    try {
                        if (this.ldapURL == null) {
                            this.searchRequest.setBaseDN(this.baseDN.nextValue());
                            this.searchRequest.setFilter(this.filter.nextValue());
                        }
                        else {
                            final LDAPURL url2 = new LDAPURL(this.ldapURL.nextValue());
                            this.searchRequest.setBaseDN(url2.getBaseDN());
                            this.searchRequest.setScope(url2.getScope());
                            this.searchRequest.setFilter(url2.getFilter());
                            this.searchRequest.setAttributes(url2.getAttributes());
                        }
                        this.searchRequest.setControls(this.requestControls);
                        if (this.simplePageSize != null) {
                            this.searchRequest.addControl(new SimplePagedResultsControl(this.simplePageSize));
                        }
                        if (this.authzID != null) {
                            proxyControl = new ProxiedAuthorizationV2RequestControl(this.authzID.nextValue());
                            this.searchRequest.addControl(proxyControl);
                        }
                    }
                    catch (final LDAPException le3) {
                        Debug.debugException(le3);
                        this.errorCounter.incrementAndGet();
                        final ResultCode rc2 = le3.getResultCode();
                        this.rcCounter.increment(rc2);
                        this.resultCode.compareAndSet(null, rc2);
                        continue;
                    }
                    long entriesReturned = 0L;
                    final long startTime = System.nanoTime();
                    while (true) {
                        SearchResult r2;
                        try {
                            r2 = this.connection.search(this.searchRequest);
                            entriesReturned += r2.getEntryCount();
                        }
                        catch (final LDAPSearchException lse) {
                            Debug.debugException(lse);
                            r2 = lse.getSearchResult();
                            this.errorCounter.incrementAndGet();
                            entriesReturned += lse.getEntryCount();
                            final ResultCode rc4 = lse.getResultCode();
                            this.rcCounter.increment(rc4);
                            this.resultCode.compareAndSet(null, rc4);
                            if (!lse.getResultCode().isConnectionUsable()) {
                                this.connection.close();
                                this.connection = null;
                            }
                            break;
                        }
                        if (this.simplePageSize == null) {
                            break;
                        }
                        try {
                            final SimplePagedResultsControl sprResponse = SimplePagedResultsControl.get(r2);
                            if (sprResponse == null || !sprResponse.moreResultsToReturn()) {
                                break;
                            }
                            this.searchRequest.setControls(this.requestControls);
                            if (this.simplePageSize != null) {
                                this.searchRequest.addControl(new SimplePagedResultsControl(this.simplePageSize, sprResponse.getCookie()));
                            }
                            if (proxyControl == null) {
                                continue;
                            }
                            this.searchRequest.addControl(proxyControl);
                        }
                        catch (final Exception e3) {
                            Debug.debugException(e3);
                            break;
                        }
                    }
                    this.searchCounter.incrementAndGet();
                    this.searchDurations.addAndGet(System.nanoTime() - startTime);
                    this.entryCounter.addAndGet(entriesReturned);
                }
            }
            if (this.asyncSemaphore != null) {
                while (this.asyncSemaphore.availablePermits() < this.searchRate.getMaxOutstandingRequests()) {
                    try {
                        Thread.sleep(1L);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        if (e instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                        continue;
                    }
                }
            }
        }
        finally {
            if (this.connection != null) {
                this.connection.close();
            }
            this.searchThread.set(null);
            this.runningThreads.decrementAndGet();
        }
    }
    
    void signalShutdown() {
        this.stopRequested.set(true);
        if (this.fixedRateBarrier != null) {
            this.fixedRateBarrier.shutdownRequested();
        }
    }
    
    ResultCode waitForShutdown() {
        final Thread t = this.searchThread.get();
        if (t != null) {
            try {
                t.join();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        this.resultCode.compareAndSet(null, ResultCode.SUCCESS);
        return this.resultCode.get();
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
    }
}
