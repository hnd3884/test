package com.unboundid.ldap.sdk.examples;

import java.util.Iterator;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV2RequestControl;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.FixedRateBarrier;
import com.unboundid.util.ValuePattern;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.util.ResultCodeCounter;
import java.util.Random;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.concurrent.CyclicBarrier;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

final class SearchAndModRateThread extends Thread
{
    private final AtomicBoolean stopRequested;
    private final AtomicInteger runningThreads;
    private final AtomicLong errorCounter;
    private final AtomicLong modCounter;
    private final AtomicLong modDurations;
    private final AtomicLong remainingIterationsBeforeReconnect;
    private final AtomicLong searchCounter;
    private final AtomicLong searchDurations;
    private final AtomicReference<Thread> searchAndModThread;
    private final AtomicReference<ResultCode> resultCode;
    private final byte[] charSet;
    private final CyclicBarrier startBarrier;
    private final int valueLength;
    private final Integer simplePageSize;
    private LDAPConnection connection;
    private final List<Control> modifyControls;
    private final List<Control> searchControls;
    private final long iterationsBeforeReconnect;
    private final Random random;
    private final ResultCodeCounter rcCounter;
    private final SearchAndModRate searchAndModRate;
    private final SearchRequest searchRequest;
    private final String[] modAttributes;
    private final ValuePattern authzID;
    private final ValuePattern baseDN;
    private final ValuePattern filter;
    private final FixedRateBarrier fixedRateBarrier;
    
    SearchAndModRateThread(final SearchAndModRate searchAndModRate, final int threadNumber, final LDAPConnection connection, final ValuePattern baseDN, final SearchScope scope, final ValuePattern filter, final String[] returnAttributes, final String[] modAttributes, final int valueLength, final byte[] charSet, final ValuePattern authzID, final Integer simplePageSize, final List<Control> searchControls, final List<Control> modifyControls, final long iterationsBeforeReconnect, final long randomSeed, final AtomicInteger runningThreads, final CyclicBarrier startBarrier, final AtomicLong searchCounter, final AtomicLong modCounter, final AtomicLong searchDurations, final AtomicLong modDurations, final AtomicLong errorCounter, final ResultCodeCounter rcCounter, final FixedRateBarrier rateBarrier) {
        this.setName("SearchAndModRate Thread " + threadNumber);
        this.setDaemon(true);
        this.searchAndModRate = searchAndModRate;
        this.connection = connection;
        this.baseDN = baseDN;
        this.filter = filter;
        this.modAttributes = modAttributes;
        this.valueLength = valueLength;
        this.charSet = charSet;
        this.authzID = authzID;
        this.simplePageSize = simplePageSize;
        this.searchControls = searchControls;
        this.modifyControls = modifyControls;
        this.iterationsBeforeReconnect = iterationsBeforeReconnect;
        this.searchCounter = searchCounter;
        this.modCounter = modCounter;
        this.searchDurations = searchDurations;
        this.modDurations = modDurations;
        this.errorCounter = errorCounter;
        this.rcCounter = rcCounter;
        this.runningThreads = runningThreads;
        this.startBarrier = startBarrier;
        this.fixedRateBarrier = rateBarrier;
        if (iterationsBeforeReconnect > 0L) {
            this.remainingIterationsBeforeReconnect = new AtomicLong(iterationsBeforeReconnect);
        }
        else {
            this.remainingIterationsBeforeReconnect = null;
        }
        connection.setConnectionName("search-and-mod-" + threadNumber);
        this.random = new Random(randomSeed);
        this.resultCode = new AtomicReference<ResultCode>(null);
        this.searchAndModThread = new AtomicReference<Thread>(null);
        this.stopRequested = new AtomicBoolean(false);
        this.searchRequest = new SearchRequest("", scope, Filter.createPresenceFilter("objectClass"), returnAttributes);
    }
    
    @Override
    public void run() {
        try {
            this.searchAndModThread.set(Thread.currentThread());
            this.runningThreads.incrementAndGet();
            final Modification[] mods = new Modification[this.modAttributes.length];
            final byte[] valueBytes = new byte[this.valueLength];
            final ASN1OctetString[] values = { null };
            final ModifyRequest modifyRequest = new ModifyRequest("", mods);
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
                        this.connection = this.searchAndModRate.getConnection();
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
                try {
                    this.searchRequest.setBaseDN(this.baseDN.nextValue());
                    this.searchRequest.setFilter(this.filter.nextValue());
                    this.searchRequest.setControls(this.searchControls);
                    if (this.authzID != null) {
                        proxyControl = new ProxiedAuthorizationV2RequestControl(this.authzID.nextValue());
                        this.searchRequest.addControl(proxyControl);
                    }
                    if (this.simplePageSize != null) {
                        this.searchRequest.addControl(new SimplePagedResultsControl(this.simplePageSize));
                    }
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    this.errorCounter.incrementAndGet();
                    final ResultCode rc2 = le2.getResultCode();
                    this.rcCounter.increment(rc2);
                    this.resultCode.compareAndSet(null, rc2);
                    continue;
                }
                final ASN1OctetString pagedResultCookie = null;
                final long searchStartTime = System.nanoTime();
                while (true) {
                    SearchResult r;
                    try {
                        r = this.connection.search(this.searchRequest);
                    }
                    catch (final LDAPSearchException lse) {
                        Debug.debugException(lse);
                        this.errorCounter.incrementAndGet();
                        final ResultCode rc3 = lse.getResultCode();
                        this.rcCounter.increment(rc3);
                        this.resultCode.compareAndSet(null, rc3);
                        if (!lse.getResultCode().isConnectionUsable()) {
                            this.connection.close();
                            this.connection = null;
                        }
                        break;
                    }
                    finally {
                        this.searchCounter.incrementAndGet();
                        this.searchDurations.addAndGet(System.nanoTime() - searchStartTime);
                    }
                    for (int i = 0; i < this.valueLength; ++i) {
                        valueBytes[i] = this.charSet[this.random.nextInt(this.charSet.length)];
                    }
                    values[0] = new ASN1OctetString(valueBytes);
                    for (int i = 0; i < this.modAttributes.length; ++i) {
                        mods[i] = new Modification(ModificationType.REPLACE, this.modAttributes[i], values);
                    }
                    modifyRequest.setModifications(mods);
                    modifyRequest.setControls(this.modifyControls);
                    if (proxyControl != null) {
                        modifyRequest.addControl(proxyControl);
                    }
                    for (final SearchResultEntry e2 : r.getSearchEntries()) {
                        if (this.fixedRateBarrier != null) {
                            this.fixedRateBarrier.await();
                        }
                        modifyRequest.setDN(e2.getDN());
                        final long modStartTime = System.nanoTime();
                        try {
                            if (this.connection == null) {
                                continue;
                            }
                            this.connection.modify(modifyRequest);
                        }
                        catch (final LDAPException le3) {
                            Debug.debugException(le3);
                            this.errorCounter.incrementAndGet();
                            final ResultCode rc4 = le3.getResultCode();
                            this.rcCounter.increment(rc4);
                            this.resultCode.compareAndSet(null, rc4);
                            if (le3.getResultCode().isConnectionUsable()) {
                                continue;
                            }
                            this.connection.close();
                            this.connection = null;
                        }
                        finally {
                            this.modCounter.incrementAndGet();
                            this.modDurations.addAndGet(System.nanoTime() - modStartTime);
                        }
                    }
                    if (this.simplePageSize == null) {
                        break;
                    }
                    try {
                        final SimplePagedResultsControl sprResponse = SimplePagedResultsControl.get(r);
                        if (sprResponse == null || !sprResponse.moreResultsToReturn()) {
                            break;
                        }
                        this.searchRequest.setControls(this.searchControls);
                        if (proxyControl != null) {
                            this.searchRequest.addControl(proxyControl);
                        }
                        if (this.simplePageSize == null) {
                            continue;
                        }
                        this.searchRequest.addControl(new SimplePagedResultsControl(this.simplePageSize, sprResponse.getCookie()));
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                        break;
                    }
                }
            }
        }
        finally {
            if (this.connection != null) {
                this.connection.close();
            }
            this.searchAndModThread.set(null);
            this.runningThreads.decrementAndGet();
        }
    }
    
    public ResultCode stopRunning() {
        this.stopRequested.set(true);
        if (this.fixedRateBarrier != null) {
            this.fixedRateBarrier.shutdownRequested();
        }
        final Thread t = this.searchAndModThread.get();
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
