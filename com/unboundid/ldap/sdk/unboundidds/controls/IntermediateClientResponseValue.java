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
public final class IntermediateClientResponseValue implements Serializable
{
    private static final byte TYPE_UPSTREAM_RESPONSE = -96;
    private static final byte TYPE_UPSTREAM_SERVER_ADDRESS = -127;
    private static final byte TYPE_UPSTREAM_SERVER_SECURE = -126;
    private static final byte TYPE_SERVER_NAME = -125;
    private static final byte TYPE_SERVER_SESSION_ID = -124;
    private static final byte TYPE_SERVER_RESPONSE_ID = -123;
    private static final long serialVersionUID = 5165171788442351399L;
    private final Boolean upstreamServerSecure;
    private final IntermediateClientResponseValue upstreamResponse;
    private final String serverName;
    private final String serverResponseID;
    private final String serverSessionID;
    private final String upstreamServerAddress;
    
    public IntermediateClientResponseValue(final IntermediateClientResponseValue upstreamResponse, final String upstreamServerAddress, final Boolean upstreamServerSecure, final String serverName, final String serverSessionID, final String serverResponseID) {
        this.upstreamResponse = upstreamResponse;
        this.upstreamServerAddress = upstreamServerAddress;
        this.upstreamServerSecure = upstreamServerSecure;
        this.serverName = serverName;
        this.serverSessionID = serverSessionID;
        this.serverResponseID = serverResponseID;
    }
    
    public IntermediateClientResponseValue getUpstreamResponse() {
        return this.upstreamResponse;
    }
    
    public String getUpstreamServerAddress() {
        return this.upstreamServerAddress;
    }
    
    public Boolean upstreamServerSecure() {
        return this.upstreamServerSecure;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public String getServerSessionID() {
        return this.serverSessionID;
    }
    
    public String getServerResponseID() {
        return this.serverResponseID;
    }
    
    public ASN1Sequence encode() {
        return this.encode((byte)48);
    }
    
    private ASN1Sequence encode(final byte type) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(6);
        if (this.upstreamResponse != null) {
            elements.add(this.upstreamResponse.encode((byte)(-96)));
        }
        if (this.upstreamServerAddress != null) {
            elements.add(new ASN1OctetString((byte)(-127), this.upstreamServerAddress));
        }
        if (this.upstreamServerSecure != null) {
            elements.add(new ASN1Boolean((byte)(-126), this.upstreamServerSecure));
        }
        if (this.serverName != null) {
            elements.add(new ASN1OctetString((byte)(-125), this.serverName));
        }
        if (this.serverSessionID != null) {
            elements.add(new ASN1OctetString((byte)(-124), this.serverSessionID));
        }
        if (this.serverResponseID != null) {
            elements.add(new ASN1OctetString((byte)(-123), this.serverResponseID));
        }
        return new ASN1Sequence(type, elements);
    }
    
    public static IntermediateClientResponseValue decode(final ASN1Sequence sequence) throws LDAPException {
        Boolean upstreamServerSecure = null;
        IntermediateClientResponseValue upstreamResponse = null;
        String upstreamServerAddress = null;
        String serverName = null;
        String serverResponseID = null;
        String serverSessionID = null;
        for (final ASN1Element element : sequence.elements()) {
            switch (element.getType()) {
                case -96: {
                    try {
                        final ASN1Sequence s = ASN1Sequence.decodeAsSequence(element);
                        upstreamResponse = decode(s);
                        break;
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICRESP_CANNOT_DECODE_UPSTREAM_RESPONSE.get(le.getMessage()), le);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICRESP_CANNOT_DECODE_UPSTREAM_RESPONSE.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                }
                case -127: {
                    upstreamServerAddress = ASN1OctetString.decodeAsOctetString(element).stringValue();
                    break;
                }
                case -126: {
                    try {
                        upstreamServerSecure = ASN1Boolean.decodeAsBoolean(element).booleanValue();
                        break;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICRESP_CANNOT_DECODE_UPSTREAM_SECURE.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                }
                case -125: {
                    serverName = ASN1OctetString.decodeAsOctetString(element).stringValue();
                    break;
                }
                case -124: {
                    serverSessionID = ASN1OctetString.decodeAsOctetString(element).stringValue();
                    break;
                }
                case -123: {
                    serverResponseID = ASN1OctetString.decodeAsOctetString(element).stringValue();
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ICRESP_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(element.getType())));
                }
            }
        }
        return new IntermediateClientResponseValue(upstreamResponse, upstreamServerAddress, upstreamServerSecure, serverName, serverSessionID, serverResponseID);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        if (this.upstreamResponse != null) {
            hashCode += this.upstreamResponse.hashCode();
        }
        if (this.upstreamServerAddress != null) {
            hashCode += this.upstreamServerAddress.hashCode();
        }
        if (this.upstreamServerSecure != null) {
            hashCode += this.upstreamServerSecure.hashCode();
        }
        if (this.serverName != null) {
            hashCode += this.serverName.hashCode();
        }
        if (this.serverSessionID != null) {
            hashCode += this.serverSessionID.hashCode();
        }
        if (this.serverResponseID != null) {
            hashCode += this.serverResponseID.hashCode();
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
        if (!(o instanceof IntermediateClientResponseValue)) {
            return false;
        }
        final IntermediateClientResponseValue v = (IntermediateClientResponseValue)o;
        if (this.upstreamResponse == null) {
            if (v.upstreamResponse != null) {
                return false;
            }
        }
        else if (!this.upstreamResponse.equals(v.upstreamResponse)) {
            return false;
        }
        if (this.upstreamServerAddress == null) {
            if (v.upstreamServerAddress != null) {
                return false;
            }
        }
        else if (!this.upstreamServerAddress.equals(v.upstreamServerAddress)) {
            return false;
        }
        if (this.upstreamServerSecure == null) {
            if (v.upstreamServerSecure != null) {
                return false;
            }
        }
        else if (!this.upstreamServerSecure.equals(v.upstreamServerSecure)) {
            return false;
        }
        if (this.serverName == null) {
            if (v.serverName != null) {
                return false;
            }
        }
        else if (!this.serverName.equals(v.serverName)) {
            return false;
        }
        if (this.serverSessionID == null) {
            if (v.serverSessionID != null) {
                return false;
            }
        }
        else if (!this.serverSessionID.equals(v.serverSessionID)) {
            return false;
        }
        if (this.serverResponseID == null) {
            if (v.serverResponseID != null) {
                return false;
            }
        }
        else if (!this.serverResponseID.equals(v.serverResponseID)) {
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
        buffer.append("IntermediateClientResponseValue(");
        boolean added = false;
        if (this.upstreamResponse != null) {
            buffer.append("upstreamResponse=");
            this.upstreamResponse.toString(buffer);
            added = true;
        }
        if (this.upstreamServerAddress != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("upstreamServerAddress='");
            buffer.append(this.upstreamServerAddress);
            buffer.append('\'');
        }
        if (this.upstreamServerSecure != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("upstreamServerSecure='");
            buffer.append(this.upstreamServerSecure);
            buffer.append('\'');
        }
        if (this.serverName != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("serverName='");
            buffer.append(this.serverName);
            buffer.append('\'');
        }
        if (this.serverSessionID != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("serverSessionID='");
            buffer.append(this.serverSessionID);
            buffer.append('\'');
        }
        if (this.serverResponseID != null) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append("serverResponseID='");
            buffer.append(this.serverResponseID);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
