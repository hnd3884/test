package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.Entry;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ReadOnlyEntry;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AlarmEntry extends ReadOnlyEntry
{
    private static final long serialVersionUID = -2481622467368820030L;
    private final AlarmSeverity currentSeverity;
    private final AlarmSeverity previousSeverity;
    private final Date lastCriticalTime;
    private final Date lastIndeterminateTime;
    private final Date lastMajorTime;
    private final Date lastMinorTime;
    private final Date lastNormalTime;
    private final Date lastWarningTime;
    private final Date startTime;
    private final Integer eventType;
    private final Integer probableCause;
    private final Long totalDurationCriticalMillis;
    private final Long totalDurationIndeterminateMillis;
    private final Long totalDurationMajorMillis;
    private final Long totalDurationMinorMillis;
    private final Long totalDurationNormalMillis;
    private final Long totalDurationWarningMillis;
    private final String additionalText;
    private final String condition;
    private final String details;
    private final String id;
    private final String specificResource;
    private final String specificResourceType;
    
    public AlarmEntry(final Entry entry) {
        super(entry);
        this.id = entry.getAttributeValue("ds-alarm-id");
        this.condition = entry.getAttributeValue("ds-alarm-condition");
        this.startTime = entry.getAttributeValueAsDate("ds-alarm-start-time");
        this.specificResource = entry.getAttributeValue("ds-alarm-specific-resource");
        this.specificResourceType = entry.getAttributeValue("ds-alarm-specific-resource-type");
        this.details = entry.getAttributeValue("ds-alarm-details");
        this.additionalText = entry.getAttributeValue("ds-alarm-additional-text");
        this.lastNormalTime = entry.getAttributeValueAsDate("ds-alarm-normal-last-time");
        this.lastWarningTime = entry.getAttributeValueAsDate("ds-alarm-warning-last-time");
        this.lastMinorTime = entry.getAttributeValueAsDate("ds-alarm-minor-last-time");
        this.lastMajorTime = entry.getAttributeValueAsDate("ds-alarm-major-last-time");
        this.lastCriticalTime = entry.getAttributeValueAsDate("ds-alarm-critical-last-time");
        this.lastIndeterminateTime = entry.getAttributeValueAsDate("ds-alarm-indeterminate-last-time");
        this.totalDurationNormalMillis = entry.getAttributeValueAsLong("ds-alarm-normal-total-duration-millis");
        this.totalDurationWarningMillis = entry.getAttributeValueAsLong("ds-alarm-warning-total-duration-millis");
        this.totalDurationMinorMillis = entry.getAttributeValueAsLong("ds-alarm-minor-total-duration-millis");
        this.totalDurationMajorMillis = entry.getAttributeValueAsLong("ds-alarm-major-total-duration-millis");
        this.totalDurationCriticalMillis = entry.getAttributeValueAsLong("ds-alarm-critical-total-duration-millis");
        this.totalDurationIndeterminateMillis = entry.getAttributeValueAsLong("ds-alarm-indeterminate-total-duration-millis");
        this.eventType = entry.getAttributeValueAsInteger("ds-alarm-event-type");
        this.probableCause = entry.getAttributeValueAsInteger("ds-alarm-probable-cause");
        final String currentSeverityStr = entry.getAttributeValue("ds-alarm-severity");
        if (currentSeverityStr == null) {
            this.currentSeverity = null;
        }
        else {
            this.currentSeverity = AlarmSeverity.forName(currentSeverityStr);
        }
        final String previousSeverityStr = entry.getAttributeValue("ds-alarm-previous-severity");
        if (previousSeverityStr == null) {
            this.previousSeverity = null;
        }
        else {
            this.previousSeverity = AlarmSeverity.forName(previousSeverityStr);
        }
    }
    
    public String getAlarmID() {
        return this.id;
    }
    
    public String getAlarmCondition() {
        return this.condition;
    }
    
    public AlarmSeverity getCurrentAlarmSeverity() {
        return this.currentSeverity;
    }
    
    public AlarmSeverity getPreviousAlarmSeverity() {
        return this.previousSeverity;
    }
    
    public Date getAlarmStartTime() {
        return this.startTime;
    }
    
    public String getAlarmSpecificResource() {
        return this.specificResource;
    }
    
    public String getAlarmSpecificResourceType() {
        return this.specificResourceType;
    }
    
    public String getAlarmDetails() {
        return this.details;
    }
    
    public String getAlarmAdditionalText() {
        return this.additionalText;
    }
    
    public Date getAlarmLastNormalTime() {
        return this.lastNormalTime;
    }
    
    public Date getAlarmLastWarningTime() {
        return this.lastWarningTime;
    }
    
    public Date getAlarmLastMinorTime() {
        return this.lastMinorTime;
    }
    
    public Date getAlarmLastMajorTime() {
        return this.lastMajorTime;
    }
    
    public Date getAlarmLastCriticalTime() {
        return this.lastCriticalTime;
    }
    
    public Date getAlarmLastIndeterminateTime() {
        return this.lastIndeterminateTime;
    }
    
    public Long getAlarmTotalDurationNormalMillis() {
        return this.totalDurationNormalMillis;
    }
    
    public Long getAlarmTotalDurationWarningMillis() {
        return this.totalDurationWarningMillis;
    }
    
    public Long getAlarmTotalDurationMinorMillis() {
        return this.totalDurationMinorMillis;
    }
    
    public Long getAlarmTotalDurationMajorMillis() {
        return this.totalDurationMajorMillis;
    }
    
    public Long getAlarmTotalDurationCriticalMillis() {
        return this.totalDurationCriticalMillis;
    }
    
    public Long getAlarmTotalDurationIndeterminateMillis() {
        return this.totalDurationIndeterminateMillis;
    }
    
    public Integer getAlarmEventType() {
        return this.eventType;
    }
    
    public Integer getAlarmProbableCause() {
        return this.probableCause;
    }
}
