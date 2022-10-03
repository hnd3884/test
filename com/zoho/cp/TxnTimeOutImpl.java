package com.zoho.cp;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.TimerTask;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentSkipListMap;

public class TxnTimeOutImpl
{
    private static long start_delay;
    private static long retry_delay;
    private static long interval;
    private static ConcurrentSkipListMap<Long, Bucket> expireTimeVsTxnBucket;
    private static final Logger LOGGER;
    
    private static void initialize() {
        final Timer timer = new Timer("transaction_time_out_thread");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                while (true) {
                    try {
                        while (true) {
                            final long currentTime = System.currentTimeMillis();
                            final Map.Entry<Long, Bucket> entry = TxnTimeOutImpl.expireTimeVsTxnBucket.firstEntry();
                            if (entry == null || entry.getKey() > currentTime) {
                                break;
                            }
                            final Bucket bucket = TxnTimeOutImpl.expireTimeVsTxnBucket.remove(entry.getKey());
                            final Collection<Txn> txns = bucket.txnList.values();
                            for (final Txn txn : txns) {
                                final boolean isMarkedRollBack = txn.timedOut();
                                if (!isMarkedRollBack) {
                                    txn.setExpriyTime(currentTime + TxnTimeOutImpl.retry_delay);
                                    TxnTimeOutImpl.register(txn);
                                }
                            }
                        }
                    }
                    catch (final Exception exc) {
                        TxnTimeOutImpl.LOGGER.log(Level.SEVERE, "Exception occurred in the timeout thread", exc);
                        continue;
                    }
                    break;
                }
            }
        }, TxnTimeOutImpl.start_delay, TxnTimeOutImpl.interval);
    }
    
    static void unregister(final Txn txn) {
        txn.bucket.remove(txn);
    }
    
    static void register(final Txn txn) {
        final long expiryTime = txn.getExpiryTime();
        final Long bucketID = 5000L + expiryTime / 5000L * 5000L;
        Bucket bucket = TxnTimeOutImpl.expireTimeVsTxnBucket.get(bucketID);
        if (bucket == null) {
            bucket = new Bucket();
            final Bucket oldBuck = TxnTimeOutImpl.expireTimeVsTxnBucket.putIfAbsent(bucketID, bucket);
            bucket = ((oldBuck == null) ? bucket : oldBuck);
        }
        final int idx = bucket.add(txn);
        txn.bucket = bucket;
        txn.posInBucket = idx;
    }
    
    static {
        TxnTimeOutImpl.start_delay = 30000L;
        TxnTimeOutImpl.retry_delay = 5000L;
        TxnTimeOutImpl.interval = 15000L;
        TxnTimeOutImpl.expireTimeVsTxnBucket = new ConcurrentSkipListMap<Long, Bucket>();
        LOGGER = Logger.getLogger(TxnTimeOutImpl.class.getName());
        initialize();
        TxnTimeOutImpl.LOGGER.log(Level.INFO, "TransactionTimeout Thread has started.");
    }
}
