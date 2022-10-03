package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPResult;
import java.util.Collections;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ExtendedResponseProtocolOp implements ProtocolOp
{
    public static final byte TYPE_RESPONSE_OID = -118;
    public static final byte TYPE_RESPONSE_VALUE = -117;
    private static final long serialVersionUID = -7757619031268544913L;
    private final ASN1OctetString responseValue;
    private final int resultCode;
    private final List<String> referralURLs;
    private final String diagnosticMessage;
    private final String matchedDN;
    private final String responseOID;
    
    public ExtendedResponseProtocolOp(final int resultCode, final String matchedDN, final String diagnosticMessage, final List<String> referralURLs, final String responseOID, final ASN1OctetString responseValue) {
        this.resultCode = resultCode;
        this.matchedDN = matchedDN;
        this.diagnosticMessage = diagnosticMessage;
        this.responseOID = responseOID;
        if (referralURLs == null) {
            this.referralURLs = Collections.emptyList();
        }
        else {
            this.referralURLs = Collections.unmodifiableList((List<? extends String>)referralURLs);
        }
        if (responseValue == null) {
            this.responseValue = null;
        }
        else {
            this.responseValue = new ASN1OctetString((byte)(-117), responseValue.getValue());
        }
    }
    
    public ExtendedResponseProtocolOp(final LDAPResult result) {
        this.resultCode = result.getResultCode().intValue();
        this.matchedDN = result.getMatchedDN();
        this.diagnosticMessage = result.getDiagnosticMessage();
        this.referralURLs = StaticUtils.toList(result.getReferralURLs());
        if (result instanceof ExtendedResult) {
            final ExtendedResult r = (ExtendedResult)result;
            this.responseOID = r.getOID();
            this.responseValue = r.getValue();
        }
        else {
            this.responseOID = null;
            this.responseValue = null;
        }
    }
    
    ExtendedResponseProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            final ASN1StreamReaderSequence opSequence = reader.beginSequence();
            this.resultCode = reader.readEnumerated();
            String s = reader.readString();
            Validator.ensureNotNull(s);
            if (s.isEmpty()) {
                this.matchedDN = null;
            }
            else {
                this.matchedDN = s;
            }
            s = reader.readString();
            Validator.ensureNotNull(s);
            if (s.isEmpty()) {
                this.diagnosticMessage = null;
            }
            else {
                this.diagnosticMessage = s;
            }
            ASN1OctetString value = null;
            String oid = null;
            final ArrayList<String> refs = new ArrayList<String>(1);
            while (opSequence.hasMoreElements()) {
                final byte type = (byte)reader.peek();
                if (type == -93) {
                    final ASN1StreamReaderSequence refSequence = reader.beginSequence();
                    while (refSequence.hasMoreElements()) {
                        refs.add(reader.readString());
                    }
                }
                else if (type == -118) {
                    oid = reader.readString();
                }
                else {
                    if (type != -117) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_EXTENDED_RESPONSE_INVALID_ELEMENT.get(StaticUtils.toHex(type)));
                    }
                    value = new ASN1OctetString(type, reader.readBytes());
                }
            }
            this.referralURLs = Collections.unmodifiableList((List<? extends String>)refs);
            this.responseOID = oid;
            this.responseValue = value;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_EXTENDED_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public int getResultCode() {
        return this.resultCode;
    }
    
    public String getMatchedDN() {
        return this.matchedDN;
    }
    
    public String getDiagnosticMessage() {
        return this.diagnosticMessage;
    }
    
    public List<String> getReferralURLs() {
        return this.referralURLs;
    }
    
    public String getResponseOID() {
        return this.responseOID;
    }
    
    public ASN1OctetString getResponseValue() {
        return this.responseValue;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 120;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(6);
        elements.add(new ASN1Enumerated(this.getResultCode()));
        final String mdn = this.getMatchedDN();
        if (mdn == null) {
            elements.add(new ASN1OctetString());
        }
        else {
            elements.add(new ASN1OctetString(mdn));
        }
        final String dm = this.getDiagnosticMessage();
        if (dm == null) {
            elements.add(new ASN1OctetString());
        }
        else {
            elements.add(new ASN1OctetString(dm));
        }
        final List<String> refs = this.getReferralURLs();
        if (!refs.isEmpty()) {
            final ArrayList<ASN1Element> refElements = new ArrayList<ASN1Element>(refs.size());
            for (final String r : refs) {
                refElements.add(new ASN1OctetString(r));
            }
            elements.add(new ASN1Sequence((byte)(-93), refElements));
        }
        if (this.responseOID != null) {
            elements.add(new ASN1OctetString((byte)(-118), this.responseOID));
        }
        if (this.responseValue != null) {
            elements.add(this.responseValue);
        }
        return new ASN1Sequence((byte)120, elements);
    }
    
    public static ExtendedResponseProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final int resultCode = ASN1Enumerated.decodeAsEnumerated(elements[0]).intValue();
            final String md = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            String matchedDN;
            if (!md.isEmpty()) {
                matchedDN = md;
            }
            else {
                matchedDN = null;
            }
            final String dm = ASN1OctetString.decodeAsOctetString(elements[2]).stringValue();
            String diagnosticMessage;
            if (!dm.isEmpty()) {
                diagnosticMessage = dm;
            }
            else {
                diagnosticMessage = null;
            }
            ASN1OctetString responseValue = null;
            List<String> referralURLs = null;
            String responseOID = null;
            if (elements.length > 3) {
                for (int i = 3; i < elements.length; ++i) {
                    switch (elements[i].getType()) {
                        case -93: {
                            final ASN1Element[] refElements = ASN1Sequence.decodeAsSequence(elements[3]).elements();
                            referralURLs = new ArrayList<String>(refElements.length);
                            for (final ASN1Element e : refElements) {
                                referralURLs.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
                            }
                            break;
                        }
                        case -118: {
                            responseOID = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                            break;
                        }
                        case -117: {
                            responseValue = ASN1OctetString.decodeAsOctetString(elements[i]);
                            break;
                        }
                        default: {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_EXTENDED_RESPONSE_INVALID_ELEMENT.get(StaticUtils.toHex(elements[i].getType())));
                        }
                    }
                }
            }
            return new ExtendedResponseProtocolOp(resultCode, matchedDN, diagnosticMessage, referralURLs, responseOID, responseValue);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_EXTENDED_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence((byte)120);
        buffer.addEnumerated(this.resultCode);
        buffer.addOctetString(this.matchedDN);
        buffer.addOctetString(this.diagnosticMessage);
        if (!this.referralURLs.isEmpty()) {
            final ASN1BufferSequence refSequence = buffer.beginSequence((byte)(-93));
            for (final String s : this.referralURLs) {
                buffer.addOctetString(s);
            }
            refSequence.end();
        }
        if (this.responseOID != null) {
            buffer.addOctetString((byte)(-118), this.responseOID);
        }
        if (this.responseValue != null) {
            buffer.addOctetString((byte)(-117), this.responseValue.getValue());
        }
        opSequence.end();
    }
    
    public ExtendedResult toExtendedResult(final Control... controls) {
        final String[] referralArray = new String[this.referralURLs.size()];
        this.referralURLs.toArray(referralArray);
        return new ExtendedResult(-1, ResultCode.valueOf(this.resultCode), this.diagnosticMessage, this.matchedDN, referralArray, this.responseOID, this.responseValue, controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ExtendedResponseProtocolOp(resultCode=");
        buffer.append(this.resultCode);
        if (this.matchedDN != null) {
            buffer.append(", matchedDN='");
            buffer.append(this.matchedDN);
            buffer.append('\'');
        }
        if (this.diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(this.diagnosticMessage);
            buffer.append('\'');
        }
        if (!this.referralURLs.isEmpty()) {
            buffer.append(", referralURLs={");
            final Iterator<String> iterator = this.referralURLs.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        if (this.responseOID != null) {
            buffer.append(", responseOID='");
            buffer.append(this.responseOID);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
