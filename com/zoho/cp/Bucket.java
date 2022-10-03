package com.zoho.cp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class Bucket
{
    private AtomicInteger i;
    ConcurrentHashMap<Integer, Txn> txnList;
    
    Bucket() {
        this.i = new AtomicInteger();
        this.txnList = new ConcurrentHashMap<Integer, Txn>();
    }
    
    int add(final Txn obj) {
        final int index = this.i.getAndIncrement();
        this.txnList.put(index, obj);
        return index;
    }
    
    void remove(final Txn txn) {
        this.txnList.remove(txn.posInBucket);
    }
    
    @Override
    public String toString() {
        return "Bucket [i=" + this.i + ", map=" + this.txnList + "]";
    }
}
