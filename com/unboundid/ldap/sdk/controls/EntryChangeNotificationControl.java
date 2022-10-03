package com.unboundid.ldap.sdk.controls;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class EntryChangeNotificationControl extends Control implements DecodeableControl
{
    public static final String ENTRY_CHANGE_NOTIFICATION_OID = "2.16.840.1.113730.3.4.7";
    private static final long serialVersionUID = -1305357948140939303L;
    private final long changeNumber;
    private final PersistentSearchChangeType changeType;
    private final String previousDN;
    
    EntryChangeNotificationControl() {
        this.changeNumber = -1L;
        this.changeType = null;
        this.previousDN = null;
    }
    
    public EntryChangeNotificationControl(final PersistentSearchChangeType changeType, final String previousDN, final long changeNumber) {
        this(changeType, previousDN, changeNumber, false);
    }
    
    public EntryChangeNotificationControl(final PersistentSearchChangeType changeType, final String previousDN, final long changeNumber, final boolean isCritical) {
        super("2.16.840.1.113730.3.4.7", isCritical, encodeValue(changeType, previousDN, changeNumber));
        this.changeType = changeType;
        this.previousDN = previousDN;
        this.changeNumber = changeNumber;
    }
    
    public EntryChangeNotificationControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ECN_NO_VALUE.get());
        }
        ASN1Sequence ecnSequence;
        try {
            final ASN1Element element = ASN1Element.decode(value.getValue());
            ecnSequence = ASN1Sequence.decodeAsSequence(element);
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ECN_VALUE_NOT_SEQUENCE.get(ae), ae);
        }
        final ASN1Element[] ecnElements = ecnSequence.elements();
        if (ecnElements.length < 1 || ecnElements.length > 3) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ECN_INVALID_ELEMENT_COUNT.get(ecnElements.length));
        }
        ASN1Enumerated ecnEnumerated;
        try {
            ecnEnumerated = ASN1Enumerated.decodeAsEnumerated(ecnElements[0]);
        }
        catch (final ASN1Exception ae2) {
            Debug.debugException(ae2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ECN_FIRST_NOT_ENUMERATED.get(ae2), ae2);
        }
        this.changeType = PersistentSearchChangeType.valueOf(ecnEnumerated.intValue());
        if (this.changeType == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ECN_INVALID_CHANGE_TYPE.get(ecnEnumerated.intValue()));
        }
        String prevDN = null;
        long chgNum = -1L;
        for (int i = 1; i < ecnElements.length; ++i) {
            switch (ecnElements[i].getType()) {
                case 4: {
                    prevDN = ASN1OctetString.decodeAsOctetString(ecnElements[i]).stringValue();
                    break;
                }
                case 2: {
                    try {
                        chgNum = ASN1Long.decodeAsLong(ecnElements[i]).longValue();
                        break;
                    }
                    catch (final ASN1Exception ae3) {
                        Debug.debugException(ae3);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ECN_CANNOT_DECODE_CHANGE_NUMBER.get(ae3), ae3);
                    }
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ECN_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(ecnElements[i].getType())));
                }
            }
        }
        this.previousDN = prevDN;
        this.changeNumber = chgNum;
    }
    
    @Override
    public EntryChangeNotificationControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new EntryChangeNotificationControl(oid, isCritical, value);
    }
    
    public static EntryChangeNotificationControl get(final SearchResultEntry entry) throws LDAPException {
        final Control c = entry.getControl("2.16.840.1.113730.3.4.7");
        if (c == null) {
            return null;
        }
        if (c instanceof EntryChangeNotificationControl) {
            return (EntryChangeNotificationControl)c;
        }
        return new EntryChangeNotificationControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    private static ASN1OctetString encodeValue(final PersistentSearchChangeType changeType, final String previousDN, final long changeNumber) {
        Validator.ensureNotNull(changeType);
        final ArrayList<ASN1Element> elementList = new ArrayList<ASN1Element>(3);
        elementList.add(new ASN1Enumerated(changeType.intValue()));
        if (previousDN != null) {
            elementList.add(new ASN1OctetString(previousDN));
        }
        if (changeNumber > 0L) {
            elementList.add(new ASN1Long(changeNumber));
        }
        return new ASN1OctetString(new ASN1Sequence(elementList).encode());
    }
    
    public PersistentSearchChangeType getChangeType() {
        return this.changeType;
    }
    
    public String getPreviousDN() {
        return this.previousDN;
    }
    
    public long getChangeNumber() {
        return this.changeNumber;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_ENTRY_CHANGE_NOTIFICATION.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EntryChangeNotificationControl(changeType=");
        buffer.append(this.changeType.getName());
        if (this.previousDN != null) {
            buffer.append(", previousDN='");
            buffer.append(this.previousDN);
            buffer.append('\'');
        }
        if (this.changeNumber > 0L) {
            buffer.append(", changeNumber=");
            buffer.append(this.changeNumber);
        }
        buffer.append(", isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
