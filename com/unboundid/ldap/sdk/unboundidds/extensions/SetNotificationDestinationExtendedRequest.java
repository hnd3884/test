package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SetNotificationDestinationExtendedRequest extends ExtendedRequest
{
    public static final String SET_NOTIFICATION_DESTINATION_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.36";
    private static final byte BER_TYPE_CHANGE_TYPE = Byte.MIN_VALUE;
    private static final long serialVersionUID = 8651862605802389433L;
    private final List<ASN1OctetString> destinationDetails;
    private final SetNotificationDestinationChangeType changeType;
    private final String destinationID;
    private final String managerID;
    
    public SetNotificationDestinationExtendedRequest(final String managerID, final String destinationID, final ASN1OctetString... destinationDetails) {
        this(managerID, destinationID, StaticUtils.toList(destinationDetails), SetNotificationDestinationChangeType.REPLACE, new Control[0]);
    }
    
    public SetNotificationDestinationExtendedRequest(final String managerID, final String destinationID, final Collection<ASN1OctetString> destinationDetails, final Control... controls) {
        this(managerID, destinationID, destinationDetails, SetNotificationDestinationChangeType.REPLACE, controls);
    }
    
    public SetNotificationDestinationExtendedRequest(final String managerID, final String destinationID, final Collection<ASN1OctetString> destinationDetails, final SetNotificationDestinationChangeType changeType, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.36", encodeValue(managerID, destinationID, destinationDetails, changeType), controls);
        this.managerID = managerID;
        this.destinationID = destinationID;
        this.destinationDetails = Collections.unmodifiableList((List<? extends ASN1OctetString>)new ArrayList<ASN1OctetString>(destinationDetails));
        if (changeType == null) {
            this.changeType = SetNotificationDestinationChangeType.REPLACE;
        }
        else {
            this.changeType = changeType;
        }
    }
    
    public SetNotificationDestinationExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_NOTIFICATION_DEST_REQ_DECODE_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.managerID = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            this.destinationID = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            final ASN1Element[] detailElements = ASN1Sequence.decodeAsSequence(elements[2]).elements();
            final ArrayList<ASN1OctetString> detailList = new ArrayList<ASN1OctetString>(detailElements.length);
            for (final ASN1Element e : detailElements) {
                detailList.add(ASN1OctetString.decodeAsOctetString(e));
            }
            this.destinationDetails = Collections.unmodifiableList((List<? extends ASN1OctetString>)detailList);
            SetNotificationDestinationChangeType ct = SetNotificationDestinationChangeType.REPLACE;
            int i = 3;
            while (i < elements.length) {
                final ASN1Element e2 = elements[i];
                switch (e2.getType()) {
                    case Byte.MIN_VALUE: {
                        final int ctIntValue = ASN1Enumerated.decodeAsEnumerated(e2).intValue();
                        ct = SetNotificationDestinationChangeType.valueOf(ctIntValue);
                        if (ct == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_NOTIFICATION_DEST_REQ_INVALID_CT.get(ctIntValue));
                        }
                        ++i;
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_NOTIFICATION_DEST_REQ_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(e2.getType())));
                    }
                }
            }
            this.changeType = ct;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_NOTIFICATION_DEST_REQ_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e3)), e3);
        }
    }
    
    private static ASN1OctetString encodeValue(final String managerID, final String destinationID, final Collection<ASN1OctetString> destinationDetails, final SetNotificationDestinationChangeType changeType) {
        Validator.ensureNotNull(managerID);
        Validator.ensureNotNull(destinationID);
        Validator.ensureNotNull(destinationDetails);
        Validator.ensureFalse(destinationDetails.isEmpty());
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        elements.add(new ASN1OctetString(managerID));
        elements.add(new ASN1OctetString(destinationID));
        elements.add(new ASN1Sequence(new ArrayList<ASN1Element>(destinationDetails)));
        if (changeType != null && changeType != SetNotificationDestinationChangeType.REPLACE) {
            elements.add(new ASN1Enumerated((byte)(-128), changeType.intValue()));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getManagerID() {
        return this.managerID;
    }
    
    public String getDestinationID() {
        return this.destinationID;
    }
    
    public List<ASN1OctetString> getDestinationDetails() {
        return this.destinationDetails;
    }
    
    public SetNotificationDestinationChangeType getChangeType() {
        return this.changeType;
    }
    
    @Override
    public SetNotificationDestinationExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public SetNotificationDestinationExtendedRequest duplicate(final Control[] controls) {
        final SetNotificationDestinationExtendedRequest r = new SetNotificationDestinationExtendedRequest(this.managerID, this.destinationID, this.destinationDetails, this.changeType, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_SET_NOTIFICATION_DEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SetNotificationDestinationExtendedRequest(managerID='");
        buffer.append(this.managerID);
        buffer.append("', destinationID='");
        buffer.append(this.destinationID);
        buffer.append("', destinationDetails=ASN1OctetString[");
        buffer.append(this.destinationDetails.size());
        buffer.append("], changeType=");
        buffer.append(this.changeType.name());
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
