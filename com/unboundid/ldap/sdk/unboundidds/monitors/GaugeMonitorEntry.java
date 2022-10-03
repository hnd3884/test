package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import java.util.Date;
import com.unboundid.ldap.sdk.unboundidds.AlarmSeverity;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class GaugeMonitorEntry extends MonitorEntry
{
    static final String GAUGE_MONITOR_OC = "ds-gauge-monitor-entry";
    private static final long serialVersionUID = -6092840651638645538L;
    private final AlarmSeverity currentSeverity;
    private final AlarmSeverity previousSeverity;
    private final Date currentSeverityStartTime;
    private final Date lastCriticalStateEndTime;
    private final Date lastCriticalStateStartTime;
    private final Date lastMajorStateEndTime;
    private final Date lastMajorStateStartTime;
    private final Date lastMinorStateEndTime;
    private final Date lastMinorStateStartTime;
    private final Date lastNormalStateEndTime;
    private final Date lastNormalStateStartTime;
    private final Date lastWarningStateEndTime;
    private final Date lastWarningStateStartTime;
    private final Date initTime;
    private final Date updateTime;
    private final List<String> errorMessages;
    private final Long currentSeverityDurationMillis;
    private final Long lastCriticalStateDurationMillis;
    private final Long lastMajorStateDurationMillis;
    private final Long lastMinorStateDurationMillis;
    private final Long lastNormalStateDurationMillis;
    private final Long lastWarningStateDurationMillis;
    private final Long samplesThisInterval;
    private final Long totalCriticalStateDurationMillis;
    private final Long totalMajorStateDurationMillis;
    private final Long totalMinorStateDurationMillis;
    private final Long totalNormalStateDurationMillis;
    private final Long totalWarningStateDurationMillis;
    private final String currentSeverityDurationString;
    private final String gaugeName;
    private final String lastCriticalStateDurationString;
    private final String lastMajorStateDurationString;
    private final String lastMinorStateDurationString;
    private final String lastNormalStateDurationString;
    private final String lastWarningStateDurationString;
    private final String resource;
    private final String resourceType;
    private final String summary;
    private final String totalCriticalStateDurationString;
    private final String totalMajorStateDurationString;
    private final String totalMinorStateDurationString;
    private final String totalNormalStateDurationString;
    private final String totalWarningStateDurationString;
    
    public GaugeMonitorEntry(final Entry entry) {
        super(entry);
        this.gaugeName = this.getString("gauge-name");
        this.resource = this.getString("resource");
        this.resourceType = this.getString("resource-type");
        final String currentSeverityStr = this.getString("severity");
        if (currentSeverityStr == null) {
            this.currentSeverity = null;
        }
        else {
            this.currentSeverity = AlarmSeverity.forName(currentSeverityStr);
        }
        final String previousSeverityStr = this.getString("previous-severity");
        if (previousSeverityStr == null) {
            this.previousSeverity = null;
        }
        else {
            this.previousSeverity = AlarmSeverity.forName(previousSeverityStr);
        }
        this.summary = this.getString("summary");
        this.errorMessages = this.getStrings("error-message");
        this.initTime = this.getDate("gauge-init-time");
        this.updateTime = this.getDate("update-time");
        this.samplesThisInterval = this.getLong("samples-this-interval");
        this.currentSeverityStartTime = this.getDate("current-severity-start-time");
        this.currentSeverityDurationString = this.getString("current-severity-duration");
        this.currentSeverityDurationMillis = this.getLong("current-severity-duration-millis");
        this.lastNormalStateStartTime = this.getDate("last-normal-state-start-time");
        this.lastNormalStateEndTime = this.getDate("last-normal-state-end-time");
        this.lastNormalStateDurationString = this.getString("last-normal-state-duration");
        this.lastNormalStateDurationMillis = this.getLong("last-normal-state-duration-millis");
        this.totalNormalStateDurationString = this.getString("total-normal-state-duration");
        this.totalNormalStateDurationMillis = this.getLong("total-normal-state-duration-millis");
        this.lastWarningStateStartTime = this.getDate("last-warning-state-start-time");
        this.lastWarningStateEndTime = this.getDate("last-warning-state-end-time");
        this.lastWarningStateDurationString = this.getString("last-warning-state-duration");
        this.lastWarningStateDurationMillis = this.getLong("last-warning-state-duration-millis");
        this.totalWarningStateDurationString = this.getString("total-warning-state-duration");
        this.totalWarningStateDurationMillis = this.getLong("total-warning-state-duration-millis");
        this.lastMinorStateStartTime = this.getDate("last-minor-state-start-time");
        this.lastMinorStateEndTime = this.getDate("last-minor-state-end-time");
        this.lastMinorStateDurationString = this.getString("last-minor-state-duration");
        this.lastMinorStateDurationMillis = this.getLong("last-minor-state-duration-millis");
        this.totalMinorStateDurationString = this.getString("total-minor-state-duration");
        this.totalMinorStateDurationMillis = this.getLong("total-minor-state-duration-millis");
        this.lastMajorStateStartTime = this.getDate("last-major-state-start-time");
        this.lastMajorStateEndTime = this.getDate("last-major-state-end-time");
        this.lastMajorStateDurationString = this.getString("last-major-state-duration");
        this.lastMajorStateDurationMillis = this.getLong("last-major-state-duration-millis");
        this.totalMajorStateDurationString = this.getString("total-major-state-duration");
        this.totalMajorStateDurationMillis = this.getLong("total-major-state-duration-millis");
        this.lastCriticalStateStartTime = this.getDate("last-critical-state-start-time");
        this.lastCriticalStateEndTime = this.getDate("last-critical-state-end-time");
        this.lastCriticalStateDurationString = this.getString("last-critical-state-duration");
        this.lastCriticalStateDurationMillis = this.getLong("last-critical-state-duration-millis");
        this.totalCriticalStateDurationString = this.getString("total-critical-state-duration");
        this.totalCriticalStateDurationMillis = this.getLong("total-critical-state-duration-millis");
    }
    
    public final String getGaugeName() {
        return this.gaugeName;
    }
    
    public final String getResource() {
        return this.resource;
    }
    
    public final String getResourceType() {
        return this.resourceType;
    }
    
    public final AlarmSeverity getCurrentSeverity() {
        return this.currentSeverity;
    }
    
    public final AlarmSeverity getPreviousSeverity() {
        return this.previousSeverity;
    }
    
    public final String getSummary() {
        return this.summary;
    }
    
    public final List<String> getErrorMessages() {
        return this.errorMessages;
    }
    
    public final Date getInitTime() {
        return this.initTime;
    }
    
    public final Date getUpdateTime() {
        return this.updateTime;
    }
    
    public final Long getSamplesThisInterval() {
        return this.samplesThisInterval;
    }
    
    public final Date getCurrentSeverityStartTime() {
        return this.currentSeverityStartTime;
    }
    
    public final String getCurrentSeverityDurationString() {
        return this.currentSeverityDurationString;
    }
    
    public final Long getCurrentSeverityDurationMillis() {
        return this.currentSeverityDurationMillis;
    }
    
    public final Date getLastNormalStateStartTime() {
        return this.lastNormalStateStartTime;
    }
    
    public final Date getLastNormalStateEndTime() {
        return this.lastNormalStateEndTime;
    }
    
    public final String getLastNormalStateDurationString() {
        return this.lastNormalStateDurationString;
    }
    
    public final Long getLastNormalStateDurationMillis() {
        return this.lastNormalStateDurationMillis;
    }
    
    public final String getTotalNormalStateDurationString() {
        return this.totalNormalStateDurationString;
    }
    
    public final Long getTotalNormalStateDurationMillis() {
        return this.totalNormalStateDurationMillis;
    }
    
    public final Date getLastWarningStateStartTime() {
        return this.lastWarningStateStartTime;
    }
    
    public final Date getLastWarningStateEndTime() {
        return this.lastWarningStateEndTime;
    }
    
    public final String getLastWarningStateDurationString() {
        return this.lastWarningStateDurationString;
    }
    
    public final Long getLastWarningStateDurationMillis() {
        return this.lastWarningStateDurationMillis;
    }
    
    public final String getTotalWarningStateDurationString() {
        return this.totalWarningStateDurationString;
    }
    
    public final Long getTotalWarningStateDurationMillis() {
        return this.totalWarningStateDurationMillis;
    }
    
    public final Date getLastMinorStateStartTime() {
        return this.lastMinorStateStartTime;
    }
    
    public final Date getLastMinorStateEndTime() {
        return this.lastMinorStateEndTime;
    }
    
    public final String getLastMinorStateDurationString() {
        return this.lastMinorStateDurationString;
    }
    
    public final Long getLastMinorStateDurationMillis() {
        return this.lastMinorStateDurationMillis;
    }
    
    public final String getTotalMinorStateDurationString() {
        return this.totalMinorStateDurationString;
    }
    
    public final Long getTotalMinorStateDurationMillis() {
        return this.totalMinorStateDurationMillis;
    }
    
    public final Date getLastMajorStateStartTime() {
        return this.lastMajorStateStartTime;
    }
    
    public final Date getLastMajorStateEndTime() {
        return this.lastMajorStateEndTime;
    }
    
    public final String getLastMajorStateDurationString() {
        return this.lastMajorStateDurationString;
    }
    
    public final Long getLastMajorStateDurationMillis() {
        return this.lastMajorStateDurationMillis;
    }
    
    public final String getTotalMajorStateDurationString() {
        return this.totalMajorStateDurationString;
    }
    
    public final Long getTotalMajorStateDurationMillis() {
        return this.totalMajorStateDurationMillis;
    }
    
    public final Date getLastCriticalStateStartTime() {
        return this.lastCriticalStateStartTime;
    }
    
    public final Date getLastCriticalStateEndTime() {
        return this.lastCriticalStateEndTime;
    }
    
    public final String getLastCriticalStateDurationString() {
        return this.lastCriticalStateDurationString;
    }
    
    public final Long getLastCriticalStateDurationMillis() {
        return this.lastCriticalStateDurationMillis;
    }
    
    public final String getTotalCriticalStateDurationString() {
        return this.totalCriticalStateDurationString;
    }
    
    public final Long getTotalCriticalStateDurationMillis() {
        return this.totalCriticalStateDurationMillis;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_GAUGE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_GAUGE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(43));
        if (this.gaugeName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "gauge-name", MonitorMessages.INFO_GAUGE_DISPNAME_GAUGE_NAME.get(), MonitorMessages.INFO_GAUGE_DESC_GAUGE_NAME.get(), this.gaugeName);
        }
        if (this.resource != null) {
            MonitorEntry.addMonitorAttribute(attrs, "resource", MonitorMessages.INFO_GAUGE_DISPNAME_RESOURCE.get(), MonitorMessages.INFO_GAUGE_DESC_RESOURCE.get(), this.resource);
        }
        if (this.resourceType != null) {
            MonitorEntry.addMonitorAttribute(attrs, "resource-type", MonitorMessages.INFO_GAUGE_DISPNAME_RESOURCE_TYPE.get(), MonitorMessages.INFO_GAUGE_DESC_RESOURCE_TYPE.get(), this.resourceType);
        }
        if (this.currentSeverity != null) {
            MonitorEntry.addMonitorAttribute(attrs, "severity", MonitorMessages.INFO_GAUGE_DISPNAME_CURRENT_SEVERITY.get(), MonitorMessages.INFO_GAUGE_DESC_CURRENT_SEVERITY.get(), this.currentSeverity.name());
        }
        if (this.previousSeverity != null) {
            MonitorEntry.addMonitorAttribute(attrs, "previous-severity", MonitorMessages.INFO_GAUGE_DISPNAME_PREVIOUS_SEVERITY.get(), MonitorMessages.INFO_GAUGE_DESC_PREVIOUS_SEVERITY.get(), this.previousSeverity.name());
        }
        if (this.summary != null) {
            MonitorEntry.addMonitorAttribute(attrs, "summary", MonitorMessages.INFO_GAUGE_DISPNAME_SUMMARY.get(), MonitorMessages.INFO_GAUGE_DESC_SUMMARY.get(), this.summary);
        }
        if (!this.errorMessages.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "error-message", MonitorMessages.INFO_GAUGE_DISPNAME_ERROR_MESSAGE.get(), MonitorMessages.INFO_GAUGE_DESC_ERROR_MESSAGE.get(), this.errorMessages);
        }
        if (this.initTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "gauge-init-time", MonitorMessages.INFO_GAUGE_DISPNAME_INIT_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_INIT_TIME.get(), this.initTime);
        }
        if (this.updateTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "update-time", MonitorMessages.INFO_GAUGE_DISPNAME_UPDATE_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_UPDATE_TIME.get(), this.updateTime);
        }
        if (this.samplesThisInterval != null) {
            MonitorEntry.addMonitorAttribute(attrs, "samples-this-interval", MonitorMessages.INFO_GAUGE_DISPNAME_SAMPLES_THIS_INTERVAL.get(), MonitorMessages.INFO_GAUGE_DESC_SAMPLES_THIS_INTERVAL.get(), this.samplesThisInterval);
        }
        if (this.currentSeverityStartTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-severity-start-time", MonitorMessages.INFO_GAUGE_DISPNAME_CURRENT_START_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_CURRENT_START_TIME.get(), this.currentSeverityStartTime);
        }
        if (this.currentSeverityDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-severity-duration", MonitorMessages.INFO_GAUGE_DISPNAME_CURRENT_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_CURRENT_DURATION_STRING.get(), this.currentSeverityDurationString);
        }
        if (this.currentSeverityDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-severity-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_CURRENT_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_CURRENT_DURATION_MILLIS.get(), this.currentSeverityDurationMillis);
        }
        if (this.lastNormalStateStartTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-normal-state-start-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_NORMAL_START_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_NORMAL_START_TIME.get(), this.lastNormalStateStartTime);
        }
        if (this.lastNormalStateEndTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-normal-state-end-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_NORMAL_END_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_NORMAL_END_TIME.get(), this.lastNormalStateEndTime);
        }
        if (this.lastNormalStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-normal-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_NORMAL_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_NORMAL_DURATION_STRING.get(), this.lastNormalStateDurationString);
        }
        if (this.lastNormalStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-normal-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_NORMAL_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_NORMAL_DURATION_MILLIS.get(), this.lastNormalStateDurationMillis);
        }
        if (this.totalNormalStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-normal-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_NORMAL_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_NORMAL_DURATION_STRING.get(), this.totalNormalStateDurationString);
        }
        if (this.totalNormalStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-normal-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_NORMAL_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_NORMAL_DURATION_MILLIS.get(), this.totalNormalStateDurationMillis);
        }
        if (this.lastWarningStateStartTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-warning-state-start-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_WARNING_START_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_WARNING_START_TIME.get(), this.lastWarningStateStartTime);
        }
        if (this.lastWarningStateEndTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-warning-state-end-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_WARNING_END_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_WARNING_END_TIME.get(), this.lastWarningStateEndTime);
        }
        if (this.lastWarningStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-warning-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_WARNING_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_WARNING_DURATION_STRING.get(), this.lastWarningStateDurationString);
        }
        if (this.lastWarningStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-warning-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_WARNING_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_WARNING_DURATION_MILLIS.get(), this.lastWarningStateDurationMillis);
        }
        if (this.totalWarningStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-warning-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_WARNING_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_WARNING_DURATION_STRING.get(), this.totalWarningStateDurationString);
        }
        if (this.totalWarningStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-warning-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_WARNING_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_WARNING_DURATION_MILLIS.get(), this.totalWarningStateDurationMillis);
        }
        if (this.lastMinorStateStartTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-minor-state-start-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_MINOR_START_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_MINOR_START_TIME.get(), this.lastMinorStateStartTime);
        }
        if (this.lastMinorStateEndTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-minor-state-end-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_MINOR_END_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_MINOR_END_TIME.get(), this.lastMinorStateEndTime);
        }
        if (this.lastMinorStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-minor-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_MINOR_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_MINOR_DURATION_STRING.get(), this.lastMinorStateDurationString);
        }
        if (this.lastMinorStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-minor-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_MINOR_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_MINOR_DURATION_MILLIS.get(), this.lastMinorStateDurationMillis);
        }
        if (this.totalMinorStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-minor-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_MINOR_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_MINOR_DURATION_STRING.get(), this.totalMinorStateDurationString);
        }
        if (this.totalMinorStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-minor-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_MINOR_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_MINOR_DURATION_MILLIS.get(), this.totalMinorStateDurationMillis);
        }
        if (this.lastMajorStateStartTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-major-state-start-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_MAJOR_START_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_MAJOR_START_TIME.get(), this.lastMajorStateStartTime);
        }
        if (this.lastMajorStateEndTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-major-state-end-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_MAJOR_END_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_MAJOR_END_TIME.get(), this.lastMajorStateEndTime);
        }
        if (this.lastMajorStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-major-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_MAJOR_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_MAJOR_DURATION_STRING.get(), this.lastMajorStateDurationString);
        }
        if (this.lastMajorStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-major-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_MAJOR_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_MAJOR_DURATION_MILLIS.get(), this.lastMajorStateDurationMillis);
        }
        if (this.totalMajorStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-major-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_MAJOR_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_MAJOR_DURATION_STRING.get(), this.totalMajorStateDurationString);
        }
        if (this.totalMajorStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-major-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_MAJOR_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_MAJOR_DURATION_MILLIS.get(), this.totalMajorStateDurationMillis);
        }
        if (this.lastCriticalStateStartTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-critical-state-start-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_CRITICAL_START_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_CRITICAL_START_TIME.get(), this.lastCriticalStateStartTime);
        }
        if (this.lastCriticalStateEndTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-critical-state-end-time", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_CRITICAL_END_TIME.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_CRITICAL_END_TIME.get(), this.lastCriticalStateEndTime);
        }
        if (this.lastCriticalStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-critical-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_CRITICAL_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_CRITICAL_DURATION_STRING.get(), this.lastCriticalStateDurationString);
        }
        if (this.lastCriticalStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "last-critical-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_LAST_CRITICAL_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_LAST_CRITICAL_DURATION_MILLIS.get(), this.lastCriticalStateDurationMillis);
        }
        if (this.totalCriticalStateDurationString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-critical-state-duration", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_CRITICAL_DURATION_STRING.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_CRITICAL_DURATION_STRING.get(), this.totalCriticalStateDurationString);
        }
        if (this.totalCriticalStateDurationMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-critical-state-duration-millis", MonitorMessages.INFO_GAUGE_DISPNAME_TOTAL_CRITICAL_DURATION_MILLIS.get(), MonitorMessages.INFO_GAUGE_DESC_TOTAL_CRITICAL_DURATION_MILLIS.get(), this.totalCriticalStateDurationMillis);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
