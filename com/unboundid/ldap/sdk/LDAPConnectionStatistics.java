package com.unboundid.ldap.sdk;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_THREADSAFE)
public final class LDAPConnectionStatistics implements Serializable
{
    private static final long serialVersionUID = -1096417617572481790L;
    private final AtomicLong numAbandonRequests;
    private final AtomicLong numAddRequests;
    private final AtomicLong numAddResponses;
    private final AtomicLong numBindRequests;
    private final AtomicLong numBindResponses;
    private final AtomicLong numCompareRequests;
    private final AtomicLong numCompareResponses;
    private final AtomicLong numConnects;
    private final AtomicLong numDeleteRequests;
    private final AtomicLong numDeleteResponses;
    private final AtomicLong numDisconnects;
    private final AtomicLong numExtendedRequests;
    private final AtomicLong numExtendedResponses;
    private final AtomicLong numModifyRequests;
    private final AtomicLong numModifyResponses;
    private final AtomicLong numModifyDNRequests;
    private final AtomicLong numModifyDNResponses;
    private final AtomicLong numSearchRequests;
    private final AtomicLong numSearchEntryResponses;
    private final AtomicLong numSearchReferenceResponses;
    private final AtomicLong numSearchDoneResponses;
    private final AtomicLong numUnbindRequests;
    private final AtomicLong totalAddResponseTime;
    private final AtomicLong totalBindResponseTime;
    private final AtomicLong totalCompareResponseTime;
    private final AtomicLong totalDeleteResponseTime;
    private final AtomicLong totalExtendedResponseTime;
    private final AtomicLong totalModifyResponseTime;
    private final AtomicLong totalModifyDNResponseTime;
    private final AtomicLong totalSearchResponseTime;
    
    public LDAPConnectionStatistics() {
        this.numAbandonRequests = new AtomicLong(0L);
        this.numAddRequests = new AtomicLong(0L);
        this.numAddResponses = new AtomicLong(0L);
        this.numBindRequests = new AtomicLong(0L);
        this.numBindResponses = new AtomicLong(0L);
        this.numCompareRequests = new AtomicLong(0L);
        this.numCompareResponses = new AtomicLong(0L);
        this.numConnects = new AtomicLong(0L);
        this.numDeleteRequests = new AtomicLong(0L);
        this.numDeleteResponses = new AtomicLong(0L);
        this.numDisconnects = new AtomicLong(0L);
        this.numExtendedRequests = new AtomicLong(0L);
        this.numExtendedResponses = new AtomicLong(0L);
        this.numModifyRequests = new AtomicLong(0L);
        this.numModifyResponses = new AtomicLong(0L);
        this.numModifyDNRequests = new AtomicLong(0L);
        this.numModifyDNResponses = new AtomicLong(0L);
        this.numSearchRequests = new AtomicLong(0L);
        this.numSearchEntryResponses = new AtomicLong(0L);
        this.numSearchReferenceResponses = new AtomicLong(0L);
        this.numSearchDoneResponses = new AtomicLong(0L);
        this.numUnbindRequests = new AtomicLong(0L);
        this.totalAddResponseTime = new AtomicLong(0L);
        this.totalBindResponseTime = new AtomicLong(0L);
        this.totalCompareResponseTime = new AtomicLong(0L);
        this.totalDeleteResponseTime = new AtomicLong(0L);
        this.totalExtendedResponseTime = new AtomicLong(0L);
        this.totalModifyResponseTime = new AtomicLong(0L);
        this.totalModifyDNResponseTime = new AtomicLong(0L);
        this.totalSearchResponseTime = new AtomicLong(0L);
    }
    
    public void reset() {
        this.numAbandonRequests.set(0L);
        this.numAddRequests.set(0L);
        this.numAddResponses.set(0L);
        this.numBindRequests.set(0L);
        this.numBindResponses.set(0L);
        this.numCompareRequests.set(0L);
        this.numCompareResponses.set(0L);
        this.numConnects.set(0L);
        this.numDeleteRequests.set(0L);
        this.numDeleteResponses.set(0L);
        this.numDisconnects.set(0L);
        this.numExtendedRequests.set(0L);
        this.numExtendedResponses.set(0L);
        this.numModifyRequests.set(0L);
        this.numModifyResponses.set(0L);
        this.numModifyDNRequests.set(0L);
        this.numModifyDNResponses.set(0L);
        this.numSearchRequests.set(0L);
        this.numSearchEntryResponses.set(0L);
        this.numSearchReferenceResponses.set(0L);
        this.numSearchDoneResponses.set(0L);
        this.numUnbindRequests.set(0L);
        this.totalAddResponseTime.set(0L);
        this.totalBindResponseTime.set(0L);
        this.totalCompareResponseTime.set(0L);
        this.totalDeleteResponseTime.set(0L);
        this.totalExtendedResponseTime.set(0L);
        this.totalModifyResponseTime.set(0L);
        this.totalModifyDNResponseTime.set(0L);
        this.totalSearchResponseTime.set(0L);
    }
    
    public long getNumConnects() {
        return this.numConnects.get();
    }
    
    void incrementNumConnects() {
        this.numConnects.incrementAndGet();
    }
    
    public long getNumDisconnects() {
        return this.numDisconnects.get();
    }
    
    void incrementNumDisconnects() {
        this.numDisconnects.incrementAndGet();
    }
    
    public long getNumAbandonRequests() {
        return this.numAbandonRequests.get();
    }
    
    void incrementNumAbandonRequests() {
        this.numAbandonRequests.incrementAndGet();
    }
    
    public long getNumAddRequests() {
        return this.numAddRequests.get();
    }
    
    void incrementNumAddRequests() {
        this.numAddRequests.incrementAndGet();
    }
    
    public long getNumAddResponses() {
        return this.numAddResponses.get();
    }
    
    void incrementNumAddResponses(final long responseTime) {
        this.numAddResponses.incrementAndGet();
        if (responseTime > 0L) {
            this.totalAddResponseTime.addAndGet(responseTime);
        }
    }
    
    public long getTotalAddResponseTimeNanos() {
        return this.totalAddResponseTime.get();
    }
    
    public long getTotalAddResponseTimeMillis() {
        return Math.round(this.totalAddResponseTime.get() / 1000000.0);
    }
    
    public double getAverageAddResponseTimeNanos() {
        final long totalTime = this.totalAddResponseTime.get();
        final long totalCount = this.numAddResponses.get();
        if (totalTime > 0L) {
            return 1.0 * totalTime / totalCount;
        }
        return Double.NaN;
    }
    
    public double getAverageAddResponseTimeMillis() {
        final long totalTime = this.totalAddResponseTime.get();
        final long totalCount = this.numAddResponses.get();
        if (totalTime > 0L) {
            return totalTime / 1000000.0 / totalCount;
        }
        return Double.NaN;
    }
    
    public long getNumBindRequests() {
        return this.numBindRequests.get();
    }
    
    void incrementNumBindRequests() {
        this.numBindRequests.incrementAndGet();
    }
    
    public long getNumBindResponses() {
        return this.numBindResponses.get();
    }
    
    void incrementNumBindResponses(final long responseTime) {
        this.numBindResponses.incrementAndGet();
        if (responseTime > 0L) {
            this.totalBindResponseTime.addAndGet(responseTime);
        }
    }
    
    public long getTotalBindResponseTimeNanos() {
        return this.totalBindResponseTime.get();
    }
    
    public long getTotalBindResponseTimeMillis() {
        return Math.round(this.totalBindResponseTime.get() / 1000000.0);
    }
    
    public double getAverageBindResponseTimeNanos() {
        final long totalTime = this.totalBindResponseTime.get();
        final long totalCount = this.numBindResponses.get();
        if (totalTime > 0L) {
            return 1.0 * totalTime / totalCount;
        }
        return Double.NaN;
    }
    
    public double getAverageBindResponseTimeMillis() {
        final long totalTime = this.totalBindResponseTime.get();
        final long totalCount = this.numBindResponses.get();
        if (totalTime > 0L) {
            return totalTime / 1000000.0 / totalCount;
        }
        return Double.NaN;
    }
    
    public long getNumCompareRequests() {
        return this.numCompareRequests.get();
    }
    
    void incrementNumCompareRequests() {
        this.numCompareRequests.incrementAndGet();
    }
    
    public long getNumCompareResponses() {
        return this.numCompareResponses.get();
    }
    
    void incrementNumCompareResponses(final long responseTime) {
        this.numCompareResponses.incrementAndGet();
        if (responseTime > 0L) {
            this.totalCompareResponseTime.addAndGet(responseTime);
        }
    }
    
    public long getTotalCompareResponseTimeNanos() {
        return this.totalCompareResponseTime.get();
    }
    
    public long getTotalCompareResponseTimeMillis() {
        return Math.round(this.totalCompareResponseTime.get() / 1000000.0);
    }
    
    public double getAverageCompareResponseTimeNanos() {
        final long totalTime = this.totalCompareResponseTime.get();
        final long totalCount = this.numCompareResponses.get();
        if (totalTime > 0L) {
            return 1.0 * totalTime / totalCount;
        }
        return Double.NaN;
    }
    
    public double getAverageCompareResponseTimeMillis() {
        final long totalTime = this.totalCompareResponseTime.get();
        final long totalCount = this.numCompareResponses.get();
        if (totalTime > 0L) {
            return totalTime / 1000000.0 / totalCount;
        }
        return Double.NaN;
    }
    
    public long getNumDeleteRequests() {
        return this.numDeleteRequests.get();
    }
    
    void incrementNumDeleteRequests() {
        this.numDeleteRequests.incrementAndGet();
    }
    
    public long getNumDeleteResponses() {
        return this.numDeleteResponses.get();
    }
    
    void incrementNumDeleteResponses(final long responseTime) {
        this.numDeleteResponses.incrementAndGet();
        if (responseTime > 0L) {
            this.totalDeleteResponseTime.addAndGet(responseTime);
        }
    }
    
    public long getTotalDeleteResponseTimeNanos() {
        return this.totalDeleteResponseTime.get();
    }
    
    public long getTotalDeleteResponseTimeMillis() {
        return Math.round(this.totalDeleteResponseTime.get() / 1000000.0);
    }
    
    public double getAverageDeleteResponseTimeNanos() {
        final long totalTime = this.totalDeleteResponseTime.get();
        final long totalCount = this.numDeleteResponses.get();
        if (totalTime > 0L) {
            return 1.0 * totalTime / totalCount;
        }
        return Double.NaN;
    }
    
    public double getAverageDeleteResponseTimeMillis() {
        final long totalTime = this.totalDeleteResponseTime.get();
        final long totalCount = this.numDeleteResponses.get();
        if (totalTime > 0L) {
            return totalTime / 1000000.0 / totalCount;
        }
        return Double.NaN;
    }
    
    public long getNumExtendedRequests() {
        return this.numExtendedRequests.get();
    }
    
    void incrementNumExtendedRequests() {
        this.numExtendedRequests.incrementAndGet();
    }
    
    public long getNumExtendedResponses() {
        return this.numExtendedResponses.get();
    }
    
    void incrementNumExtendedResponses(final long responseTime) {
        this.numExtendedResponses.incrementAndGet();
        if (responseTime > 0L) {
            this.totalExtendedResponseTime.addAndGet(responseTime);
        }
    }
    
    public long getTotalExtendedResponseTimeNanos() {
        return this.totalExtendedResponseTime.get();
    }
    
    public long getTotalExtendedResponseTimeMillis() {
        return Math.round(this.totalExtendedResponseTime.get() / 1000000.0);
    }
    
    public double getAverageExtendedResponseTimeNanos() {
        final long totalTime = this.totalExtendedResponseTime.get();
        final long totalCount = this.numExtendedResponses.get();
        if (totalTime > 0L) {
            return 1.0 * totalTime / totalCount;
        }
        return Double.NaN;
    }
    
    public double getAverageExtendedResponseTimeMillis() {
        final long totalTime = this.totalExtendedResponseTime.get();
        final long totalCount = this.numExtendedResponses.get();
        if (totalTime > 0L) {
            return totalTime / 1000000.0 / totalCount;
        }
        return Double.NaN;
    }
    
    public long getNumModifyRequests() {
        return this.numModifyRequests.get();
    }
    
    void incrementNumModifyRequests() {
        this.numModifyRequests.incrementAndGet();
    }
    
    public long getNumModifyResponses() {
        return this.numModifyResponses.get();
    }
    
    void incrementNumModifyResponses(final long responseTime) {
        this.numModifyResponses.incrementAndGet();
        if (responseTime > 0L) {
            this.totalModifyResponseTime.addAndGet(responseTime);
        }
    }
    
    public long getTotalModifyResponseTimeNanos() {
        return this.totalModifyResponseTime.get();
    }
    
    public long getTotalModifyResponseTimeMillis() {
        return Math.round(this.totalModifyResponseTime.get() / 1000000.0);
    }
    
    public double getAverageModifyResponseTimeNanos() {
        final long totalTime = this.totalModifyResponseTime.get();
        final long totalCount = this.numModifyResponses.get();
        if (totalTime > 0L) {
            return 1.0 * totalTime / totalCount;
        }
        return Double.NaN;
    }
    
    public double getAverageModifyResponseTimeMillis() {
        final long totalTime = this.totalModifyResponseTime.get();
        final long totalCount = this.numModifyResponses.get();
        if (totalTime > 0L) {
            return totalTime / 1000000.0 / totalCount;
        }
        return Double.NaN;
    }
    
    public long getNumModifyDNRequests() {
        return this.numModifyDNRequests.get();
    }
    
    void incrementNumModifyDNRequests() {
        this.numModifyDNRequests.incrementAndGet();
    }
    
    public long getNumModifyDNResponses() {
        return this.numModifyDNResponses.get();
    }
    
    void incrementNumModifyDNResponses(final long responseTime) {
        this.numModifyDNResponses.incrementAndGet();
        if (responseTime > 0L) {
            this.totalModifyDNResponseTime.addAndGet(responseTime);
        }
    }
    
    public long getTotalModifyDNResponseTimeNanos() {
        return this.totalModifyDNResponseTime.get();
    }
    
    public long getTotalModifyDNResponseTimeMillis() {
        return Math.round(this.totalModifyDNResponseTime.get() / 1000000.0);
    }
    
    public double getAverageModifyDNResponseTimeNanos() {
        final long totalTime = this.totalModifyDNResponseTime.get();
        final long totalCount = this.numModifyDNResponses.get();
        if (totalTime > 0L) {
            return 1.0 * totalTime / totalCount;
        }
        return Double.NaN;
    }
    
    public double getAverageModifyDNResponseTimeMillis() {
        final long totalTime = this.totalModifyDNResponseTime.get();
        final long totalCount = this.numModifyDNResponses.get();
        if (totalTime > 0L) {
            return totalTime / 1000000.0 / totalCount;
        }
        return Double.NaN;
    }
    
    public long getNumSearchRequests() {
        return this.numSearchRequests.get();
    }
    
    void incrementNumSearchRequests() {
        this.numSearchRequests.incrementAndGet();
    }
    
    public long getNumSearchEntryResponses() {
        return this.numSearchEntryResponses.get();
    }
    
    public long getNumSearchReferenceResponses() {
        return this.numSearchReferenceResponses.get();
    }
    
    public long getNumSearchDoneResponses() {
        return this.numSearchDoneResponses.get();
    }
    
    void incrementNumSearchResponses(final int numEntries, final int numReferences, final long responseTime) {
        this.numSearchEntryResponses.addAndGet(numEntries);
        this.numSearchReferenceResponses.addAndGet(numReferences);
        this.numSearchDoneResponses.incrementAndGet();
        if (responseTime > 0L) {
            this.totalSearchResponseTime.addAndGet(responseTime);
        }
    }
    
    public long getTotalSearchResponseTimeNanos() {
        return this.totalSearchResponseTime.get();
    }
    
    public long getTotalSearchResponseTimeMillis() {
        return Math.round(this.totalSearchResponseTime.get() / 1000000.0);
    }
    
    public double getAverageSearchResponseTimeNanos() {
        final long totalTime = this.totalSearchResponseTime.get();
        final long totalCount = this.numSearchDoneResponses.get();
        if (totalTime > 0L) {
            return 1.0 * totalTime / totalCount;
        }
        return Double.NaN;
    }
    
    public double getAverageSearchResponseTimeMillis() {
        final long totalTime = this.totalSearchResponseTime.get();
        final long totalCount = this.numSearchDoneResponses.get();
        if (totalTime > 0L) {
            return totalTime / 1000000.0 / totalCount;
        }
        return Double.NaN;
    }
    
    public long getNumUnbindRequests() {
        return this.numUnbindRequests.get();
    }
    
    void incrementNumUnbindRequests() {
        this.numUnbindRequests.incrementAndGet();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        final long connects = this.numConnects.get();
        final long disconnects = this.numDisconnects.get();
        final long abandonRequests = this.numAbandonRequests.get();
        final long addRequests = this.numAddRequests.get();
        final long addResponses = this.numAddResponses.get();
        final long addTimes = this.totalAddResponseTime.get();
        final long bindRequests = this.numBindRequests.get();
        final long bindResponses = this.numBindResponses.get();
        final long bindTimes = this.totalBindResponseTime.get();
        final long compareRequests = this.numCompareRequests.get();
        final long compareResponses = this.numCompareResponses.get();
        final long compareTimes = this.totalCompareResponseTime.get();
        final long deleteRequests = this.numDeleteRequests.get();
        final long deleteResponses = this.numDeleteResponses.get();
        final long deleteTimes = this.totalDeleteResponseTime.get();
        final long extendedRequests = this.numExtendedRequests.get();
        final long extendedResponses = this.numExtendedResponses.get();
        final long extendedTimes = this.totalExtendedResponseTime.get();
        final long modifyRequests = this.numModifyRequests.get();
        final long modifyResponses = this.numModifyResponses.get();
        final long modifyTimes = this.totalModifyResponseTime.get();
        final long modifyDNRequests = this.numModifyDNRequests.get();
        final long modifyDNResponses = this.numModifyDNResponses.get();
        final long modifyDNTimes = this.totalModifyDNResponseTime.get();
        final long searchRequests = this.numSearchRequests.get();
        final long searchEntries = this.numSearchEntryResponses.get();
        final long searchReferences = this.numSearchReferenceResponses.get();
        final long searchDone = this.numSearchDoneResponses.get();
        final long searchTimes = this.totalSearchResponseTime.get();
        final long unbindRequests = this.numUnbindRequests.get();
        final DecimalFormat f = new DecimalFormat("0.000");
        buffer.append("LDAPConnectionStatistics(numConnects=");
        buffer.append(connects);
        buffer.append(", numDisconnects=");
        buffer.append(disconnects);
        buffer.append(", numAbandonRequests=");
        buffer.append(abandonRequests);
        buffer.append(", numAddRequests=");
        buffer.append(addRequests);
        buffer.append(", numAddResponses=");
        buffer.append(addResponses);
        buffer.append(", totalAddResponseTimeNanos=");
        buffer.append(addTimes);
        if (addTimes > 0L) {
            buffer.append(", averageAddResponseTimeNanos=");
            buffer.append(f.format(1.0 * addResponses / addTimes));
        }
        buffer.append(", numBindRequests=");
        buffer.append(bindRequests);
        buffer.append(", numBindResponses=");
        buffer.append(bindResponses);
        buffer.append(", totalBindResponseTimeNanos=");
        buffer.append(bindTimes);
        if (bindTimes > 0L) {
            buffer.append(", averageBindResponseTimeNanos=");
            buffer.append(f.format(1.0 * bindResponses / bindTimes));
        }
        buffer.append(", numCompareRequests=");
        buffer.append(compareRequests);
        buffer.append(", numCompareResponses=");
        buffer.append(compareResponses);
        buffer.append(", totalCompareResponseTimeNanos=");
        buffer.append(compareTimes);
        if (compareTimes > 0L) {
            buffer.append(", averageCompareResponseTimeNanos=");
            buffer.append(f.format(1.0 * compareResponses / compareTimes));
        }
        buffer.append(", numDeleteRequests=");
        buffer.append(deleteRequests);
        buffer.append(", numDeleteResponses=");
        buffer.append(deleteResponses);
        buffer.append(", totalDeleteResponseTimeNanos=");
        buffer.append(deleteTimes);
        if (deleteTimes > 0L) {
            buffer.append(", averageDeleteResponseTimeNanos=");
            buffer.append(f.format(1.0 * deleteResponses / deleteTimes));
        }
        buffer.append(", numExtendedRequests=");
        buffer.append(extendedRequests);
        buffer.append(", numExtendedResponses=");
        buffer.append(extendedResponses);
        buffer.append(", totalExtendedResponseTimeNanos=");
        buffer.append(extendedTimes);
        if (extendedTimes > 0L) {
            buffer.append(", averageExtendedResponseTimeNanos=");
            buffer.append(f.format(1.0 * extendedResponses / extendedTimes));
        }
        buffer.append(", numModifyRequests=");
        buffer.append(modifyRequests);
        buffer.append(", numModifyResponses=");
        buffer.append(modifyResponses);
        buffer.append(", totalModifyResponseTimeNanos=");
        buffer.append(modifyTimes);
        if (modifyTimes > 0L) {
            buffer.append(", averageModifyResponseTimeNanos=");
            buffer.append(f.format(1.0 * modifyResponses / modifyTimes));
        }
        buffer.append(", numModifyDNRequests=");
        buffer.append(modifyDNRequests);
        buffer.append(", numModifyDNResponses=");
        buffer.append(modifyDNResponses);
        buffer.append(", totalModifyDNResponseTimeNanos=");
        buffer.append(modifyDNTimes);
        if (modifyDNTimes > 0L) {
            buffer.append(", averageModifyDNResponseTimeNanos=");
            buffer.append(f.format(1.0 * modifyDNResponses / modifyDNTimes));
        }
        buffer.append(", numSearchRequests=");
        buffer.append(searchRequests);
        buffer.append(", numSearchEntries=");
        buffer.append(searchEntries);
        buffer.append(", numSearchReferences=");
        buffer.append(searchReferences);
        buffer.append(", numSearchDone=");
        buffer.append(searchDone);
        buffer.append(", totalSearchResponseTimeNanos=");
        buffer.append(searchTimes);
        if (searchTimes > 0L) {
            buffer.append(", averageSearchResponseTimeNanos=");
            buffer.append(f.format(1.0 * searchDone / searchTimes));
        }
        buffer.append(", numUnbindRequests=");
        buffer.append(unbindRequests);
        buffer.append(')');
    }
}
