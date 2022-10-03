package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class NotificationDestinationChangeSelectionCriteria extends ChangelogBatchChangeSelectionCriteria
{
    static final byte TYPE_SELECTION_CRITERIA_NOTIFICATION_DESTINATION = -124;
    private final String destinationEntryUUID;
    
    public NotificationDestinationChangeSelectionCriteria(final String destinationEntryUUID) {
        Validator.ensureNotNull(destinationEntryUUID);
        this.destinationEntryUUID = destinationEntryUUID;
    }
    
    static NotificationDestinationChangeSelectionCriteria decodeInnerElement(final ASN1Element innerElement) throws LDAPException {
        try {
            return new NotificationDestinationChangeSelectionCriteria(ASN1OctetString.decodeAsOctetString(innerElement).stringValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_NOT_DEST_CHANGE_SELECTION_CRITERIA_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getDestinationEntryUUID() {
        return this.destinationEntryUUID;
    }
    
    public ASN1Element encodeInnerElement() {
        return new ASN1OctetString((byte)(-124), this.destinationEntryUUID);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("NotificationDestinationChangeSelectionCriteria(destinationEntryUUID='");
        buffer.append(this.destinationEntryUUID);
        buffer.append("')");
    }
}
