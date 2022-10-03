package com.unboundid.ldap.sdk.examples;

import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV2RequestControl;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.util.ValuePattern;
import com.unboundid.util.ResultCodeCounter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.FixedRateBarrier;
import java.util.concurrent.CyclicBarrier;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

final class ModRateThread extends Thread
{
    private final AtomicBoolean stopRequested;
    private final AtomicInteger runningThreads;
    private final AtomicLong errorCounter;
    private final AtomicLong modCounter;
    private final AtomicLong modDurations;
    private final AtomicLong remainingIterationsBeforeReconnect;
    private final AtomicReference<ResultCode> resultCode;
    private final boolean increment;
    private final Control[] modifyControls;
    private final CyclicBarrier startBarrier;
    private final FixedRateBarrier fixedRateBarrier;
    private final int incrementAmount;
    private final int valueCount;
    private LDAPConnection connection;
    private final long iterationsBeforeReconnect;
    private final ModRate modRate;
    private final ResultCodeCounter rcCounter;
    private final String[] attributes;
    private final AtomicReference<Thread> modThread;
    private final ValuePattern authzID;
    private final ValuePattern entryDN;
    private final ValuePattern valuePattern;
    
    ModRateThread(final ModRate modRate, final int threadNumber, final LDAPConnection connection, final ValuePattern entryDN, final String[] attributes, final ValuePattern valuePattern, final int valueCount, final boolean increment, final int incrementAmount, final Control[] modifyControls, final ValuePattern authzID, final long iterationsBeforeReconnect, final AtomicInteger runningThreads, final CyclicBarrier startBarrier, final AtomicLong modCounter, final AtomicLong modDurations, final AtomicLong errorCounter, final ResultCodeCounter rcCounter, final FixedRateBarrier rateBarrier) {
        this.setName("ModRate Thread " + threadNumber);
        this.setDaemon(true);
        this.modRate = modRate;
        this.connection = connection;
        this.entryDN = entryDN;
        this.attributes = attributes;
        this.valuePattern = valuePattern;
        this.valueCount = valueCount;
        this.increment = increment;
        this.incrementAmount = incrementAmount;
        this.modifyControls = modifyControls;
        this.authzID = authzID;
        this.iterationsBeforeReconnect = iterationsBeforeReconnect;
        this.modCounter = modCounter;
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
        connection.setConnectionName("mod-" + threadNumber);
        this.resultCode = new AtomicReference<ResultCode>(null);
        this.modThread = new AtomicReference<Thread>(null);
        this.stopRequested = new AtomicBoolean(false);
    }
    
    @Override
    public void run() {
        try {
            this.modThread.set(Thread.currentThread());
            this.runningThreads.incrementAndGet();
            final Modification[] mods = new Modification[this.attributes.length];
            final String[] values = new String[this.valueCount];
            if (this.increment) {
                values[0] = String.valueOf(this.incrementAmount);
                for (int i = 0; i < this.attributes.length; ++i) {
                    mods[i] = new Modification(ModificationType.INCREMENT, this.attributes[i], values);
                }
            }
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
                        this.connection = this.modRate.getConnection();
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
                modifyRequest.setDN(this.entryDN.nextValue());
                if (!this.increment) {
                    for (int j = 0; j < this.valueCount; ++j) {
                        values[j] = this.valuePattern.nextValue();
                    }
                    for (int j = 0; j < this.attributes.length; ++j) {
                        mods[j] = new Modification(ModificationType.REPLACE, this.attributes[j], values);
                    }
                    modifyRequest.setModifications(mods);
                }
                modifyRequest.setControls(this.modifyControls);
                if (this.authzID != null) {
                    modifyRequest.addControl(new ProxiedAuthorizationV2RequestControl(this.authzID.nextValue()));
                }
                if (this.fixedRateBarrier != null) {
                    this.fixedRateBarrier.await();
                }
                final long startTime = System.nanoTime();
                try {
                    this.connection.modify(modifyRequest);
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    this.errorCounter.incrementAndGet();
                    final ResultCode rc2 = le2.getResultCode();
                    this.rcCounter.increment(rc2);
                    this.resultCode.compareAndSet(null, rc2);
                    if (!le2.getResultCode().isConnectionUsable()) {
                        this.connection.close();
                        this.connection = null;
                    }
                }
                this.modCounter.incrementAndGet();
                this.modDurations.addAndGet(System.nanoTime() - startTime);
            }
        }
        finally {
            if (this.connection != null) {
                this.connection.close();
            }
            this.modThread.set(null);
            this.runningThreads.decrementAndGet();
        }
    }
    
    public ResultCode stopRunning() {
        final Thread t = this.modThread.get();
        this.stopRequested.set(true);
        if (this.fixedRateBarrier != null) {
            this.fixedRateBarrier.shutdownRequested();
        }
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
