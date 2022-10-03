package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ModifyDNRequestProtocolOp implements ProtocolOp
{
    public static final byte TYPE_NEW_SUPERIOR = Byte.MIN_VALUE;
    private static final long serialVersionUID = 7514385089303489375L;
    private final boolean deleteOldRDN;
    private final String dn;
    private final String newRDN;
    private final String newSuperiorDN;
    
    public ModifyDNRequestProtocolOp(final String dn, final String newRDN, final boolean deleteOldRDN, final String newSuperiorDN) {
        this.dn = dn;
        this.newRDN = newRDN;
        this.deleteOldRDN = deleteOldRDN;
        this.newSuperiorDN = newSuperiorDN;
    }
    
    public ModifyDNRequestProtocolOp(final ModifyDNRequest request) {
        this.dn = request.getDN();
        this.newRDN = request.getNewRDN();
        this.deleteOldRDN = request.deleteOldRDN();
        this.newSuperiorDN = request.getNewSuperiorDN();
    }
    
    ModifyDNRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            final ASN1StreamReaderSequence opSequence = reader.beginSequence();
            this.dn = reader.readString();
            this.newRDN = reader.readString();
            this.deleteOldRDN = reader.readBoolean();
            if (opSequence.hasMoreElements()) {
                this.newSuperiorDN = reader.readString();
            }
            else {
                this.newSuperiorDN = null;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MODIFY_DN_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getDN() {
        return this.dn;
    }
    
    public String getNewRDN() {
        return this.newRDN;
    }
    
    public boolean deleteOldRDN() {
        return this.deleteOldRDN;
    }
    
    public String getNewSuperiorDN() {
        return this.newSuperiorDN;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 108;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        if (this.newSuperiorDN == null) {
            return new ASN1Sequence((byte)108, new ASN1Element[] { new ASN1OctetString(this.dn), new ASN1OctetString(this.newRDN), new ASN1Boolean(this.deleteOldRDN) });
        }
        return new ASN1Sequence((byte)108, new ASN1Element[] { new ASN1OctetString(this.dn), new ASN1OctetString(this.newRDN), new ASN1Boolean(this.deleteOldRDN), new ASN1OctetString((byte)(-128), this.newSuperiorDN) });
    }
    
    public static ModifyDNRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final String dn = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            final String newRDN = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            final boolean deleteOldRDN = ASN1Boolean.decodeAsBoolean(elements[2]).booleanValue();
            String newSuperiorDN;
            if (elements.length > 3) {
                newSuperiorDN = ASN1OctetString.decodeAsOctetString(elements[3]).stringValue();
            }
            else {
                newSuperiorDN = null;
            }
            return new ModifyDNRequestProtocolOp(dn, newRDN, deleteOldRDN, newSuperiorDN);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MODIFY_DN_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence((byte)108);
        buffer.addOctetString(this.dn);
        buffer.addOctetString(this.newRDN);
        buffer.addBoolean(this.deleteOldRDN);
        if (this.newSuperiorDN != null) {
            buffer.addOctetString((byte)(-128), this.newSuperiorDN);
        }
        opSequence.end();
    }
    
    public ModifyDNRequest toModifyDNRequest(final Control... controls) {
        return new ModifyDNRequest(this.dn, this.newRDN, this.deleteOldRDN, this.newSuperiorDN, controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ModifyDNRequestProtocolOp(dn='");
        buffer.append(this.dn);
        buffer.append("', newRDN='");
        buffer.append(this.newRDN);
        buffer.append("', deleteOldRDN=");
        buffer.append(this.deleteOldRDN);
        if (this.newSuperiorDN != null) {
            buffer.append(", newSuperiorDN='");
            buffer.append(this.newSuperiorDN);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
