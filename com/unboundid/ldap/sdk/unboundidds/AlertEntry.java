package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.Entry;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ReadOnlyEntry;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AlertEntry extends ReadOnlyEntry
{
    public static final String OC_ALERT = "ds-admin-alert";
    public static final String ATTR_ALERT_GENERATOR = "ds-alert-generator";
    public static final String ATTR_ALERT_ID = "ds-alert-id";
    public static final String ATTR_ALERT_MESSAGE = "ds-alert-message";
    public static final String ATTR_ALERT_SEVERITY = "ds-alert-severity";
    public static final String ATTR_ALERT_TIME = "ds-alert-time";
    public static final String ATTR_ALERT_TYPE = "ds-alert-type";
    public static final String ATTR_ALERT_TYPE_OID = "ds-alert-type-oid";
    private static final long serialVersionUID = -2912778595612338699L;
    private final AlertSeverity alertSeverity;
    private final Date alertTime;
    private final String alertGeneratorClass;
    private final String alertID;
    private final String alertMessage;
    private final String alertType;
    private final String alertTypeOID;
    
    public AlertEntry(final Entry entry) {
        super(entry);
        this.alertGeneratorClass = entry.getAttributeValue("ds-alert-generator");
        this.alertID = entry.getAttributeValue("ds-alert-id");
        this.alertMessage = entry.getAttributeValue("ds-alert-message");
        this.alertType = entry.getAttributeValue("ds-alert-type");
        this.alertTypeOID = entry.getAttributeValue("ds-alert-type-oid");
        this.alertTime = entry.getAttributeValueAsDate("ds-alert-time");
        final String severityStr = entry.getAttributeValue("ds-alert-severity");
        if (severityStr == null) {
            this.alertSeverity = null;
        }
        else {
            this.alertSeverity = AlertSeverity.forName(severityStr);
        }
    }
    
    public String getAlertGeneratorClass() {
        return this.alertGeneratorClass;
    }
    
    public String getAlertID() {
        return this.alertID;
    }
    
    public String getAlertMessage() {
        return this.alertMessage;
    }
    
    public AlertSeverity getAlertSeverity() {
        return this.alertSeverity;
    }
    
    public Date getAlertTime() {
        return this.alertTime;
    }
    
    public String getAlertType() {
        return this.alertType;
    }
    
    public String getAlertTypeOID() {
        return this.alertTypeOID;
    }
}
