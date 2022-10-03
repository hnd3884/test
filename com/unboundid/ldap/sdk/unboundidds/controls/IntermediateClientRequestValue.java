package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.util.Collection;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IntermediateClientRequestValue implements Serializable
{
    private static final byte TYPE_DOWNSTREAM_REQUEST = -96;
    private static final byte TYPE_DOWNSTREAM_CLIENT_ADDRESS = -127;
    private static final byte TYPE_DOWNSTREAM_CLIENT_SECURE = -126;
    private static final byte TYPE_CLIENT_IDENTITY = -125;
    private static final byte TYPE_CLIENT_NAME = -124;
    private static final byte TYPE_CLIENT_SESSION_ID = -123;
    private static final byte TYPE_CLIENT_REQUEST_ID = -122;
    private static final long serialVersionUID = -794887520013838259L;
    private final Boolean downstreamClientSecure;
    private final IntermediateClientRequestValue downstreamRequest;
    private final String clientIdentity;
    private final String downstreamClientAddress;
    private final String clientName;
    private final String clientRequestID;
    private final String clientSessionID;
    
    public IntermediateClientRequestValue(final IntermediateClientRequestValue downstreamRequest, final String downstreamClientAddress, final Boolean downstreamClientSecure, final String clientIdentity, final String clientName, final String clientSessionID, final String clientRequestID) {
        this.downstreamRequest = downstreamRequest;
        this.downstreamClientAddress = downstreamClientAddress;
        this.downstreamClientSecure = downstreamClientSecure;
        this.clientIdentity = clientIdentity;
        this.clientName = clientName;
        this.clientSessionID = clientSessionID;
        this.clientRequestID = clientRequestID;
    }
    
    public IntermediateClientRequestValue getDownstreamRequest() {
        return this.downstreamRequest;
    }
    
    public String getClientIdentity() {
        return this.clientIdentity;
    }
    
    public String getDownstreamClientAddress() {
        return this.downstreamClientAddress;
    }
    
    public Boolean downstreamClientSecure() {
        return this.downstreamClientSecure;
    }
    
    public String getClientName() {
        return this.clientName;
    }
    
    public String getClientSessionID() {
        return this.clientSessionID;
    }
    
    public String getClientRequestID() {
        return this.clientRequestID;
    }
    
    public ASN1Sequence encode() {
        return this.encode((byte)48);
    }
    
    private ASN1Sequence encode(final byte type) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(7);
        if (this.downstreamRequest != null) {
            elements.add(this.downstreamRequest.encode((byte)(-96)));
        }
        if (this.downstreamClientAddress != null) {
            elements.add(new ASN1OctetString((byte)(-127), this.downstreamClientAddress));
        }
        if (this.downstreamClientSecure != null) {
            elements.add(new ASN1Boolean((byte)(-126), this.downstreamClientSecure));
        }
        if (this.clientIdentity != null) {
            elements.add(new ASN1OctetString((byte)(-125), this.clientIdentity));
        }
        if (this.clientName != null) {
            elements.add(new ASN1OctetString((byte)(-124), this.clientName));
        }
        if (this.clientSessionID != null) {
            elements.add(new ASN1OctetString((byte)(-123), this.clientSessionID));
        }
        if (this.clientRequestID != null) {
            elements.add(new ASN1OctetString((byte)(-122), this.clientRequestID));
        }
        return new ASN1Sequence(type, elements);
    }
    
    public static IntermediateClientRequestValue decode(final ASN1Sequence sequence) throws LDAPException {
        Boolean downstreamClientSecure = null;
        IntermediateClientRequestValue downstreamRequest = null;
        String clientIdentity = null;
        String downstreamClientAddress = null;
        String clientName = null;
        String clientRequestID = null;
        String clientSessionID = null;
        for (final ASN1Element element : sequence.elements()) {
            switch (element.getType()) {
                case -96: {
                    try {
                        final ASN1Sequence s = ASN1Sequence.decodeAsSequence(element);
                        downstreamRequest = decode(s);
                        break;
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICREQ_CANNOT_DECODE_DOWNSTREAM_REQUEST.get(le.getMessage()), le);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICREQ_CANNOT_DECODE_DOWNSTREAM_REQUEST.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                }
                case -127: {
                    downstreamClientAddress = ASN1OctetString.decodeAsOctetString(element).stringValue();
                    break;
                }
                case -126: {
                    try {
                        downstreamClientSecure = ASN1Boolean.decodeAsBoolean(element).booleanValue();
                        break;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICREQ_CANNOT_DECODE_DOWNSTREAM_SECURE.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                }
                case -125: {
                    clientIdentity = ASN1OctetString.decodeAsOctetString(element).stringValue();
                    break;
                }
                case -124: {
                    clientName = ASN1OctetString.decodeAsOctetString(element).stringValue();
                    break;
                }
                case -123: {
                    clientSessionID = ASN1OctetString.decodeAsOctetString(element).stringValue();
                    break;
                }
                case -122: {
                    clientRequestID = ASN1OctetString.decodeAsOctetString(element).stringValue();
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICREQ_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(element.getType())));
                }
            }
        }
        return new IntermediateClientRequestValue(downstreamRequest, downstreamClientAddress, downstreamClientSecure, clientIdentity, clientName, clientSessionID, clientRequestID);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        if (this.downstreamRequest != null) {
            hashCode += this.downstreamRequest.hashCode();
        }
        if (this.downstreamClientAddress != null) {
            hashCode += this.downstreamClientAddress.hashCode();
        }
        if (this.downstreamClientSecure != null) {
            hashCode += this.downstreamClientSecure.hashCode();
        }
        if (this.clientIdentity != null) {
            hashCode += this.clientIdentity.hashCode();
        }
        if (this.clientName != null) {
            hashCode += this.clientName.hashCode();
        }
        if (this.clientSessionID != null) {
            hashCode += this.clientSessionID.hashCode();
        }
        if (this.clientRequestID != null) {
            hashCode += this.clientRequestID.hashCode();
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof IntermediateClientRequestValue)) {
            return false;
        }
        final IntermediateClientRequestValue v = (IntermediateClientRequestValue)o;
        if (this.downstreamRequest == null) {
            if (v.downstreamRequest != null) {
                return false;
            }
        }
        else if (!this.downstreamRequest.equals(v.downstreamRequest)) {
            return false;
        }
        if (this.downstreamClientAddress == null) {
            if (v.downstreamClientAddress != null) {
                return false;
            }
        }
        else if (!this.downstreamClientAddress.equals(v.downstreamClientAddress)) {
            return false;
        }
        if (this.downstreamClientSecure == null) {
            if (v.downstreamClientSecure != null) {
                return false;
            }
        }
        else if (!this.downstreamClientSecure.equals(v.downstreamClientSecure)) {
            return false;
        }
        if (this.clientIdentity == null) {
            if (v.clientIdentity != null) {
                return false;
            }
        }
        else if (!this.clientIdentity.equals(v.clientIdentity)) {
            return false;
        }
        if (this.clientName == null) {
            if (v.clientName != null) {
                return false;
            }
        }
        else if (!this.clientName.equals(v.clientName)) {
            return false;
        }
        if (this.clientSessionID == null) {
            if (v.clientSessionID != null) {
                return false;
            }
        }
        else if (!this.clientSessionID.equals(v.clientSessionID)) {
            return false;
        }
        if (this.clientRequestID == null) {
            if (v.clientRequestID != null) {
                return false;
            }
        }
        else if (!this.clientRequestID.equals(v.clientRequestID)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("IntermediateClientRequestValue(");
        boolean added = false;
        if (this.downstreamRequest != null) {
            buffer.append("downstreamRequest=");
            this.downstreamRequest.toString(buffer);
            added = true;
        }
        if (this.clientIdentity != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("clientIdentity='");
            buffer.append(this.clientIdentity);
            buffer.append('\'');
        }
        if (this.downstreamClientAddress != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("downstreamClientAddress='");
            buffer.append(this.downstreamClientAddress);
            buffer.append('\'');
        }
        if (this.downstreamClientSecure != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("downstreamClientSecure='");
            buffer.append(this.downstreamClientSecure);
            buffer.append('\'');
        }
        if (this.clientName != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("clientName='");
            buffer.append(this.clientName);
            buffer.append('\'');
        }
        if (this.clientSessionID != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("clientSessionID='");
            buffer.append(this.clientSessionID);
            buffer.append('\'');
        }
        if (this.clientRequestID != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("clientRequestID='");
            buffer.append(this.clientRequestID);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
