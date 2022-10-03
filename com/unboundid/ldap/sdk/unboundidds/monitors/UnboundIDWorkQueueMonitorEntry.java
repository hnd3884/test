package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UnboundIDWorkQueueMonitorEntry extends MonitorEntry
{
    static final String UNBOUNDID_WORK_QUEUE_MONITOR_OC = "ds-unboundid-work-queue-monitor-entry";
    private static final String ATTR_AVERAGE_QUEUE_TIME_MILLIS = "average-operation-queue-time-millis";
    private static final String ATTR_AVERAGE_PCT_BUSY = "average-worker-thread-percent-busy";
    private static final String ATTR_AVERAGE_SIZE = "average-queue-size";
    private static final String ATTR_CURRENT_PCT_BUSY = "current-worker-thread-percent-busy";
    private static final String ATTR_CURRENT_SIZE = "current-queue-size";
    private static final String ATTR_MAX_SIZE = "max-queue-size";
    private static final String ATTR_MAX_PCT_BUSY = "max-worker-thread-percent-busy";
    private static final String ATTR_NUM_BUSY_WORKER_THREADS = "num-busy-worker-threads";
    private static final String ATTR_NUM_WORKER_THREADS = "num-worker-threads";
    private static final String ATTR_RECENT_AVERAGE_SIZE = "recent-average-queue-size";
    private static final String ATTR_RECENT_QUEUE_TIME_MILLIS = "recent-operation-queue-time-millis";
    private static final String ATTR_RECENT_PCT_BUSY = "recent-worker-thread-percent-busy";
    private static final String ATTR_REQUESTS_REJECTED = "rejected-count";
    private static final String ATTR_REQUESTS_STOLEN = "stolen-count";
    private static final String ATTR_CURRENT_ADMIN_QUEUE_SIZE = "current-administrative-session-queue-size";
    private static final String ATTR_MAX_ADMIN_SESSION_QUEUE_SIZE = "max-administrative-session-queue-size";
    private static final String ATTR_NUM_ADMIN_WORKER_THREADS = "num-administrative-session-worker-threads";
    private static final String ATTR_NUM_BUSY_ADMIN_WORKER_THREADS = "num-busy-administrative-session-worker-threads";
    private static final long serialVersionUID = -304216058351812232L;
    private final Long averageQueueTimeMillis;
    private final Long averagePercentBusy;
    private final Long averageSize;
    private final Long currentAdminSize;
    private final Long currentSize;
    private final Long currentPercentBusy;
    private final Long maxAdminSize;
    private final Long maxPercentBusy;
    private final Long maxSize;
    private final Long numAdminWorkerThreads;
    private final Long numBusyWorkerThreads;
    private final Long numBusyAdminWorkerThreads;
    private final Long numWorkerThreads;
    private final Long recentAverageSize;
    private final Long recentQueueTimeMillis;
    private final Long recentPercentBusy;
    private final Long requestsRejected;
    private final Long requestsStolen;
    
    public UnboundIDWorkQueueMonitorEntry(final Entry entry) {
        super(entry);
        this.averageSize = this.getLong("average-queue-size");
        this.currentSize = this.getLong("current-queue-size");
        this.recentAverageSize = this.getLong("recent-average-queue-size");
        this.maxSize = this.getLong("max-queue-size");
        this.requestsRejected = this.getLong("rejected-count");
        this.requestsStolen = this.getLong("stolen-count");
        this.numBusyWorkerThreads = this.getLong("num-busy-worker-threads");
        this.numWorkerThreads = this.getLong("num-worker-threads");
        this.currentPercentBusy = this.getLong("current-worker-thread-percent-busy");
        this.averagePercentBusy = this.getLong("average-worker-thread-percent-busy");
        this.recentPercentBusy = this.getLong("recent-worker-thread-percent-busy");
        this.maxPercentBusy = this.getLong("max-worker-thread-percent-busy");
        this.averageQueueTimeMillis = this.getLong("average-operation-queue-time-millis");
        this.recentQueueTimeMillis = this.getLong("recent-operation-queue-time-millis");
        this.currentAdminSize = this.getLong("current-administrative-session-queue-size");
        this.maxAdminSize = this.getLong("max-administrative-session-queue-size");
        this.numAdminWorkerThreads = this.getLong("num-administrative-session-worker-threads");
        this.numBusyAdminWorkerThreads = this.getLong("num-busy-administrative-session-worker-threads");
    }
    
    public Long getAverageSize() {
        return this.averageSize;
    }
    
    public Long getRecentAverageSize() {
        return this.recentAverageSize;
    }
    
    public Long getCurrentSize() {
        return this.currentSize;
    }
    
    public Long getMaxSize() {
        return this.maxSize;
    }
    
    public Long getRequestsRejectedDueToQueueFull() {
        return this.requestsRejected;
    }
    
    public Long getRequestsStolen() {
        return this.requestsStolen;
    }
    
    public Long getNumWorkerThreads() {
        return this.numWorkerThreads;
    }
    
    public Long getNumBusyWorkerThreads() {
        return this.numBusyWorkerThreads;
    }
    
    public Long getCurrentWorkerThreadPercentBusy() {
        return this.currentPercentBusy;
    }
    
    public Long getAverageWorkerThreadPercentBusy() {
        return this.averagePercentBusy;
    }
    
    public Long getRecentWorkerThreadPercentBusy() {
        return this.recentPercentBusy;
    }
    
    public Long getMaxWorkerThreadPercentBusy() {
        return this.maxPercentBusy;
    }
    
    public Long getAverageOperationQueueTimeMillis() {
        return this.averageQueueTimeMillis;
    }
    
    public Long getRecentOperationQueueTimeMillis() {
        return this.recentQueueTimeMillis;
    }
    
    public Long getCurrentAdministrativeSessionQueueSize() {
        return this.currentAdminSize;
    }
    
    public Long getMaxAdministrativeSessionQueueSize() {
        return this.maxAdminSize;
    }
    
    public Long getNumAdministrativeSessionWorkerThreads() {
        return this.numAdminWorkerThreads;
    }
    
    public Long getNumBusyAdministrativeSessionWorkerThreads() {
        return this.numBusyAdminWorkerThreads;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(50));
        if (this.requestsRejected != null) {
            MonitorEntry.addMonitorAttribute(attrs, "rejected-count", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_REQUESTS_REJECTED.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_REQUESTS_REJECTED.get(), this.requestsRejected);
        }
        if (this.requestsStolen != null) {
            MonitorEntry.addMonitorAttribute(attrs, "stolen-count", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_REQUESTS_STOLEN.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_REQUESTS_STOLEN.get(), this.requestsStolen);
        }
        if (this.currentSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-queue-size", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_CURRENT_SIZE.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_CURRENT_SIZE.get(), this.currentSize);
        }
        if (this.recentAverageSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-average-queue-size", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_RECENT_AVERAGE_SIZE.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_RECENT_AVERAGE_SIZE.get(), this.recentAverageSize);
        }
        if (this.averageSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "average-queue-size", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_AVERAGE_SIZE.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_AVERAGE_SIZE.get(), this.averageSize);
        }
        if (this.maxSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "max-queue-size", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_MAX_SIZE.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_MAX_SIZE.get(), this.maxSize);
        }
        if (this.numWorkerThreads != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-worker-threads", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_NUM_THREADS.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_NUM_THREADS.get(), this.numWorkerThreads);
        }
        if (this.numBusyWorkerThreads != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-busy-worker-threads", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_NUM_BUSY_THREADS.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_NUM_BUSY_THREADS.get(), this.numBusyWorkerThreads);
        }
        if (this.currentPercentBusy != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-worker-thread-percent-busy", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_CURRENT_PCT_BUSY.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_CURRENT_PCT_BUSY.get(), this.currentPercentBusy);
        }
        if (this.averagePercentBusy != null) {
            MonitorEntry.addMonitorAttribute(attrs, "average-worker-thread-percent-busy", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_AVG_PCT_BUSY.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_AVG_PCT_BUSY.get(), this.averagePercentBusy);
        }
        if (this.recentPercentBusy != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-worker-thread-percent-busy", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_RECENT_PCT_BUSY.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_RECENT_PCT_BUSY.get(), this.recentPercentBusy);
        }
        if (this.maxPercentBusy != null) {
            MonitorEntry.addMonitorAttribute(attrs, "max-worker-thread-percent-busy", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_MAX_PCT_BUSY.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_MAX_PCT_BUSY.get(), this.maxPercentBusy);
        }
        if (this.averageQueueTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "average-operation-queue-time-millis", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_AVG_QUEUE_TIME.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_AVG_QUEUE_TIME.get(), this.averageQueueTimeMillis);
        }
        if (this.recentQueueTimeMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-operation-queue-time-millis", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_RECENT_QUEUE_TIME.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_RECENT_QUEUE_TIME.get(), this.recentQueueTimeMillis);
        }
        if (this.currentAdminSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-administrative-session-queue-size", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_CURRENT_ADMIN_QUEUE_SIZE.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_CURRENT_ADMIN_QUEUE_SIZE.get(), this.currentAdminSize);
        }
        if (this.maxAdminSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "max-administrative-session-queue-size", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_MAX_ADMIN_QUEUE_SIZE.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_MAX_ADMIN_QUEUE_SIZE.get(), this.maxAdminSize);
        }
        if (this.numAdminWorkerThreads != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-administrative-session-worker-threads", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_NUM_ADMIN_THREADS.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_NUM_ADMIN_THREADS.get(), this.numAdminWorkerThreads);
        }
        if (this.numBusyAdminWorkerThreads != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-busy-administrative-session-worker-threads", MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DISPNAME_NUM_BUSY_ADMIN_THREADS.get(), MonitorMessages.INFO_UNBOUNDID_WORK_QUEUE_DESC_NUM_BUSY_ADMIN_THREADS.get(), this.numBusyAdminWorkerThreads);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
