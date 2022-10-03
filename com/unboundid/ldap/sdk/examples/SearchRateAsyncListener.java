package com.unboundid.ldap.sdk.examples;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.AsyncRequestID;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.concurrent.Semaphore;
import com.unboundid.util.ResultCodeCounter;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.ldap.sdk.AsyncSearchResultListener;

final class SearchRateAsyncListener implements AsyncSearchResultListener
{
    private static final long serialVersionUID = 4929527281011834420L;
    private final AtomicLong entryCounter;
    private final AtomicLong errorCounter;
    private final AtomicLong searchCounter;
    private final AtomicLong searchDurations;
    private final AtomicReference<ResultCode> resultCode;
    private final long startTime;
    private final ResultCodeCounter rcCounter;
    private final Semaphore asyncSemaphore;
    
    SearchRateAsyncListener(final AtomicLong searchCounter, final AtomicLong entryCounter, final AtomicLong searchDurations, final AtomicLong errorCounter, final ResultCodeCounter rcCounter, final Semaphore asyncSemaphore, final AtomicReference<ResultCode> resultCode) {
        this.searchCounter = searchCounter;
        this.entryCounter = entryCounter;
        this.searchDurations = searchDurations;
        this.errorCounter = errorCounter;
        this.rcCounter = rcCounter;
        this.asyncSemaphore = asyncSemaphore;
        this.resultCode = resultCode;
        this.startTime = System.nanoTime();
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
    }
    
    @Override
    public void searchResultReceived(final AsyncRequestID requestID, final SearchResult searchResult) {
        this.searchDurations.addAndGet(System.nanoTime() - this.startTime);
        if (this.asyncSemaphore != null) {
            this.asyncSemaphore.release();
        }
        this.searchCounter.incrementAndGet();
        this.entryCounter.addAndGet(searchResult.getEntryCount());
        final ResultCode rc = searchResult.getResultCode();
        if (rc != ResultCode.SUCCESS) {
            this.errorCounter.incrementAndGet();
            this.rcCounter.increment(rc);
            this.resultCode.compareAndSet(null, rc);
        }
    }
}
