package com.unboundid.ldap.sdk.examples;

import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.PLAINBindRequest;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequest;
import com.unboundid.ldap.sdk.CRAMMD5BindRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Filter;
import java.util.List;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.FixedRateBarrier;
import com.unboundid.util.ValuePattern;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.util.ResultCodeCounter;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.concurrent.CyclicBarrier;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

final class AuthRateThread extends Thread
{
    private static final int AUTH_TYPE_SIMPLE = 0;
    private static final int AUTH_TYPE_CRAM_MD5 = 1;
    private static final int AUTH_TYPE_DIGEST_MD5 = 2;
    private static final int AUTH_TYPE_PLAIN = 3;
    private final AtomicBoolean stopRequested;
    private final AtomicInteger runningThreads;
    private final AtomicLong authCounter;
    private final AtomicLong authDurations;
    private final AtomicLong errorCounter;
    private final AtomicReference<ResultCode> resultCode;
    private final AtomicReference<Thread> authThread;
    private final AuthRate authRate;
    private final boolean bindOnly;
    private final Control[] bindControls;
    private final CyclicBarrier startBarrier;
    private final int authType;
    private LDAPConnection bindConnection;
    private LDAPConnection searchConnection;
    private final ResultCodeCounter rcCounter;
    private final SearchRequest searchRequest;
    private final String userPassword;
    private final ValuePattern baseDN;
    private final ValuePattern filter;
    private final FixedRateBarrier fixedRateBarrier;
    
    AuthRateThread(final AuthRate authRate, final int threadNumber, final LDAPConnection searchConnection, final LDAPConnection bindConnection, final ValuePattern baseDN, final SearchScope scope, final ValuePattern filter, final String[] attributes, final String userPassword, final boolean bindOnly, final String authType, final List<Control> searchControls, final List<Control> bindControls, final AtomicInteger runningThreads, final CyclicBarrier startBarrier, final AtomicLong authCounter, final AtomicLong authDurations, final AtomicLong errorCounter, final ResultCodeCounter rcCounter, final FixedRateBarrier rateBarrier) {
        this.setName("AuthRate Thread " + threadNumber);
        this.setDaemon(true);
        this.authRate = authRate;
        this.searchConnection = searchConnection;
        this.bindConnection = bindConnection;
        this.baseDN = baseDN;
        this.filter = filter;
        this.userPassword = userPassword;
        this.bindOnly = bindOnly;
        this.authCounter = authCounter;
        this.authDurations = authDurations;
        this.errorCounter = errorCounter;
        this.rcCounter = rcCounter;
        this.runningThreads = runningThreads;
        this.startBarrier = startBarrier;
        this.fixedRateBarrier = rateBarrier;
        searchConnection.setConnectionName("search-" + threadNumber);
        bindConnection.setConnectionName("bind-" + threadNumber);
        if (authType.equalsIgnoreCase("cram-md5")) {
            this.authType = 1;
        }
        else if (authType.equalsIgnoreCase("digest-md5")) {
            this.authType = 2;
        }
        else if (authType.equalsIgnoreCase("plain")) {
            this.authType = 3;
        }
        else {
            this.authType = 0;
        }
        this.resultCode = new AtomicReference<ResultCode>(null);
        this.authThread = new AtomicReference<Thread>(null);
        this.stopRequested = new AtomicBoolean(false);
        (this.searchRequest = new SearchRequest("", scope, Filter.createPresenceFilter("objectClass"), attributes)).setControls(searchControls);
        if (bindControls.isEmpty()) {
            this.bindControls = StaticUtils.NO_CONTROLS;
        }
        else {
            this.bindControls = bindControls.toArray(new Control[bindControls.size()]);
        }
    }
    
    @Override
    public void run() {
        try {
            this.authThread.set(Thread.currentThread());
            this.runningThreads.incrementAndGet();
            try {
                this.startBarrier.await();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            while (!this.stopRequested.get()) {
                if (this.searchConnection == null) {
                    try {
                        this.searchConnection = this.authRate.getConnection();
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
                if (this.bindConnection == null) {
                    try {
                        this.bindConnection = this.authRate.getConnection();
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
                if (!this.bindOnly) {
                    try {
                        this.searchRequest.setBaseDN(this.baseDN.nextValue());
                        this.searchRequest.setFilter(this.filter.nextValue());
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        this.errorCounter.incrementAndGet();
                        final ResultCode rc = le.getResultCode();
                        this.rcCounter.increment(rc);
                        this.resultCode.compareAndSet(null, rc);
                        continue;
                    }
                }
                if (this.fixedRateBarrier != null) {
                    this.fixedRateBarrier.await();
                }
                final long startTime = System.nanoTime();
                try {
                    String bindDN;
                    if (this.bindOnly) {
                        bindDN = this.baseDN.nextValue();
                    }
                    else {
                        final SearchResult r = this.searchConnection.search(this.searchRequest);
                        switch (r.getEntryCount()) {
                            case 0: {
                                this.errorCounter.incrementAndGet();
                                this.rcCounter.increment(ResultCode.NO_RESULTS_RETURNED);
                                this.resultCode.compareAndSet(null, ResultCode.NO_RESULTS_RETURNED);
                                continue;
                            }
                            case 1: {
                                bindDN = r.getSearchEntries().get(0).getDN();
                                break;
                            }
                            default: {
                                this.errorCounter.incrementAndGet();
                                this.rcCounter.increment(ResultCode.MORE_RESULTS_TO_RETURN);
                                this.resultCode.compareAndSet(null, ResultCode.MORE_RESULTS_TO_RETURN);
                                continue;
                            }
                        }
                    }
                    BindRequest bindRequest = null;
                    switch (this.authType) {
                        case 0: {
                            bindRequest = new SimpleBindRequest(bindDN, this.userPassword, this.bindControls);
                            break;
                        }
                        case 1: {
                            bindRequest = new CRAMMD5BindRequest("dn:" + bindDN, this.userPassword, this.bindControls);
                            break;
                        }
                        case 2: {
                            bindRequest = new DIGESTMD5BindRequest("dn:" + bindDN, null, this.userPassword, null, this.bindControls);
                            break;
                        }
                        case 3: {
                            bindRequest = new PLAINBindRequest("dn:" + bindDN, this.userPassword, this.bindControls);
                            break;
                        }
                    }
                    this.bindConnection.bind(bindRequest);
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    this.errorCounter.incrementAndGet();
                    final ResultCode rc2 = le2.getResultCode();
                    this.rcCounter.increment(rc2);
                    this.resultCode.compareAndSet(null, rc2);
                    if (le2.getResultCode().isConnectionUsable()) {
                        continue;
                    }
                    this.searchConnection.close();
                    this.searchConnection = null;
                    this.bindConnection.close();
                    this.bindConnection = null;
                }
                finally {
                    this.authCounter.incrementAndGet();
                    this.authDurations.addAndGet(System.nanoTime() - startTime);
                }
            }
        }
        finally {
            if (this.searchConnection != null) {
                this.searchConnection.close();
            }
            if (this.bindConnection != null) {
                this.bindConnection.close();
            }
            this.authThread.set(null);
            this.runningThreads.decrementAndGet();
        }
    }
    
    public ResultCode stopRunning() {
        this.stopRequested.set(true);
        if (this.fixedRateBarrier != null) {
            this.fixedRateBarrier.shutdownRequested();
        }
        final Thread t = this.authThread.get();
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
}
