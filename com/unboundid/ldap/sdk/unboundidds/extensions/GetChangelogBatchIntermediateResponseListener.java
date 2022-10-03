package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.IntermediateResponse;
import java.util.concurrent.atomic.AtomicInteger;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.IntermediateResponseListener;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class GetChangelogBatchIntermediateResponseListener implements IntermediateResponseListener
{
    private static final long serialVersionUID = -6143619102991670053L;
    private final AtomicInteger entryCounter;
    private final ChangelogEntryListener entryListener;
    
    GetChangelogBatchIntermediateResponseListener(final ChangelogEntryListener entryListener) {
        this.entryListener = entryListener;
        this.entryCounter = new AtomicInteger(0);
    }
    
    @Override
    public void intermediateResponseReturned(final IntermediateResponse intermediateResponse) {
        final String oid = intermediateResponse.getOID();
        if (oid == null) {
            this.entryListener.handleOtherIntermediateResponse(intermediateResponse);
            return;
        }
        if (oid.equals("1.3.6.1.4.1.30221.2.6.11")) {
            ChangelogEntryIntermediateResponse r;
            try {
                r = new ChangelogEntryIntermediateResponse(intermediateResponse);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.entryListener.handleOtherIntermediateResponse(intermediateResponse);
                return;
            }
            this.entryCounter.incrementAndGet();
            this.entryListener.handleChangelogEntry(r);
        }
        else if (oid.equals("1.3.6.1.4.1.30221.2.6.12")) {
            MissingChangelogEntriesIntermediateResponse r2;
            try {
                r2 = new MissingChangelogEntriesIntermediateResponse(intermediateResponse);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.entryListener.handleOtherIntermediateResponse(intermediateResponse);
                return;
            }
            this.entryListener.handleMissingChangelogEntries(r2);
        }
        else {
            this.entryListener.handleOtherIntermediateResponse(intermediateResponse);
        }
    }
    
    ChangelogEntryListener getEntryListener() {
        return this.entryListener;
    }
    
    int getEntryCount() {
        return this.entryCounter.get();
    }
}
