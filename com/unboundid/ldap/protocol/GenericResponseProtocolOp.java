package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.Control;
import java.util.Iterator;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1StreamReader;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public abstract class GenericResponseProtocolOp implements ProtocolOp
{
    public static final byte TYPE_REFERRALS = -93;
    private static final long serialVersionUID = 3837308973105414874L;
    private final byte type;
    private final int resultCode;
    private final List<String> referralURLs;
    private final String diagnosticMessage;
    private final String matchedDN;
    
    protected GenericResponseProtocolOp(final byte type, final int resultCode, final String matchedDN, final String diagnosticMessage, final List<String> referralURLs) {
        this.type = type;
        this.resultCode = resultCode;
        this.matchedDN = matchedDN;
        this.diagnosticMessage = diagnosticMessage;
        if (referralURLs == null) {
            this.referralURLs = Collections.emptyList();
        }
        else {
            this.referralURLs = Collections.unmodifiableList((List<? extends String>)referralURLs);
        }
    }
    
    protected GenericResponseProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            this.type = (byte)reader.peek();
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
            if (opSequence.hasMoreElements()) {
                final ArrayList<String> refs = new ArrayList<String>(1);
                final ASN1StreamReaderSequence refSequence = reader.beginSequence();
                while (refSequence.hasMoreElements()) {
                    refs.add(reader.readString());
                }
                this.referralURLs = Collections.unmodifiableList((List<? extends String>)refs);
            }
            else {
                this.referralURLs = Collections.emptyList();
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public final int getResultCode() {
        return this.resultCode;
    }
    
    public final String getMatchedDN() {
        return this.matchedDN;
    }
    
    public final String getDiagnosticMessage() {
        return this.diagnosticMessage;
    }
    
    public final List<String> getReferralURLs() {
        return this.referralURLs;
    }
    
    @Override
    public byte getProtocolOpType() {
        return this.type;
    }
    
    @Override
    public final void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence(this.type);
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
        opSequence.end();
    }
    
    public LDAPResult toLDAPResult(final Control... controls) {
        String[] refs;
        if (this.referralURLs.isEmpty()) {
            refs = StaticUtils.NO_STRINGS;
        }
        else {
            refs = new String[this.referralURLs.size()];
            this.referralURLs.toArray(refs);
        }
        return new LDAPResult(-1, ResultCode.valueOf(this.resultCode), this.diagnosticMessage, this.matchedDN, refs, controls);
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public final void toString(final StringBuilder buffer) {
        buffer.append("ResponseProtocolOp(type=");
        StaticUtils.toHex(this.type, buffer);
        buffer.append(", resultCode=");
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
        buffer.append(')');
    }
}
