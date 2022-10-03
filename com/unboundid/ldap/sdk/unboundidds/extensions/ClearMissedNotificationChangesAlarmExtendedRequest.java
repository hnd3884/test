package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ClearMissedNotificationChangesAlarmExtendedRequest extends ExtendedRequest
{
    public static final String CLEAR_MISSED_NOTIFICATION_CHANGES_ALARM_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.42";
    private static final long serialVersionUID = -5245417833641929585L;
    private final String destinationID;
    private final String managerID;
    
    public ClearMissedNotificationChangesAlarmExtendedRequest(final String managerID, final String destinationID, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.42", encodeValue(managerID, destinationID), controls);
        this.managerID = managerID;
        this.destinationID = destinationID;
    }
    
    public ClearMissedNotificationChangesAlarmExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CLEAR_MISSED_NOTIFICATION_CHANGES_ALARM_REQ_DECODE_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.managerID = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            this.destinationID = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CLEAR_MISSED_NOTIFICATION_CHANGES_ALARM_REQ_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String managerID, final String destinationID) {
        Validator.ensureNotNull(managerID);
        Validator.ensureNotNull(destinationID);
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(managerID), new ASN1OctetString(destinationID) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public String getManagerID() {
        return this.managerID;
    }
    
    public String getDestinationID() {
        return this.destinationID;
    }
    
    @Override
    public ClearMissedNotificationChangesAlarmExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public ClearMissedNotificationChangesAlarmExtendedRequest duplicate(final Control[] controls) {
        final ClearMissedNotificationChangesAlarmExtendedRequest r = new ClearMissedNotificationChangesAlarmExtendedRequest(this.managerID, this.destinationID, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_CLEAR_MISSED_NOTIFICATION_CHANGES_ALARM.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ClearMissedNotificationChangesAlarmExtendedRequest(managerID='");
        buffer.append(this.managerID);
        buffer.append("', destinationID='");
        buffer.append(this.destinationID);
        buffer.append('\'');
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
