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
public final class TraditionalWorkQueueMonitorEntry extends MonitorEntry
{
    static final String TRADITIONAL_WORK_QUEUE_MONITOR_OC = "ds-traditional-work-queue-monitor-entry";
    private static final String ATTR_AVERAGE_BACKLOG = "averageRequestBacklog";
    private static final String ATTR_CURRENT_BACKLOG = "currentRequestBacklog";
    private static final String ATTR_MAX_BACKLOG = "maxRequestBacklog";
    private static final String ATTR_REQUESTS_REJECTED = "requestsRejectedDueToQueueFull";
    private static final String ATTR_REQUESTS_SUBMITTED = "requestsSubmitted";
    private static final long serialVersionUID = 5254676890679281070L;
    private final Long averageBacklog;
    private final Long currentBacklog;
    private final Long maxBacklog;
    private final Long requestsRejected;
    private final Long requestsSubmitted;
    
    public TraditionalWorkQueueMonitorEntry(final Entry entry) {
        super(entry);
        this.averageBacklog = this.getLong("averageRequestBacklog");
        this.currentBacklog = this.getLong("currentRequestBacklog");
        this.maxBacklog = this.getLong("maxRequestBacklog");
        this.requestsRejected = this.getLong("requestsRejectedDueToQueueFull");
        this.requestsSubmitted = this.getLong("requestsSubmitted");
    }
    
    public Long getAverageBacklog() {
        return this.averageBacklog;
    }
    
    public Long getCurrentBacklog() {
        return this.currentBacklog;
    }
    
    public Long getMaxBacklog() {
        return this.maxBacklog;
    }
    
    public Long getRequestsRejectedDueToQueueFull() {
        return this.requestsRejected;
    }
    
    public Long getRequestsSubmitted() {
        return this.requestsSubmitted;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(10));
        if (this.requestsSubmitted != null) {
            MonitorEntry.addMonitorAttribute(attrs, "requestsSubmitted", MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DISPNAME_REQUESTS_SUBMITTED.get(), MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DESC_REQUESTS_SUBMITTED.get(), this.requestsSubmitted);
        }
        if (this.requestsRejected != null) {
            MonitorEntry.addMonitorAttribute(attrs, "requestsRejectedDueToQueueFull", MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DISPNAME_REQUESTS_REJECTED.get(), MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DESC_REQUESTS_REJECTED.get(), this.requestsRejected);
        }
        if (this.currentBacklog != null) {
            MonitorEntry.addMonitorAttribute(attrs, "currentRequestBacklog", MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DISPNAME_CURRENT_BACKLOG.get(), MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DESC_CURRENT_BACKLOG.get(), this.currentBacklog);
        }
        if (this.averageBacklog != null) {
            MonitorEntry.addMonitorAttribute(attrs, "averageRequestBacklog", MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DISPNAME_AVERAGE_BACKLOG.get(), MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DESC_AVERAGE_BACKLOG.get(), this.averageBacklog);
        }
        if (this.maxBacklog != null) {
            MonitorEntry.addMonitorAttribute(attrs, "maxRequestBacklog", MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DISPNAME_MAX_BACKLOG.get(), MonitorMessages.INFO_TRADITIONAL_WORK_QUEUE_DESC_MAX_BACKLOG.get(), this.maxBacklog);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
