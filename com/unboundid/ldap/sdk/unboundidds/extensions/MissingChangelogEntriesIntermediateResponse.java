package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.IntermediateResponse;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MissingChangelogEntriesIntermediateResponse extends IntermediateResponse
{
    public static final String MISSING_CHANGELOG_ENTRIES_INTERMEDIATE_RESPONSE_OID = "1.3.6.1.4.1.30221.2.6.12";
    private static final byte TYPE_MESSAGE = Byte.MIN_VALUE;
    private static final long serialVersionUID = -4961560327295588578L;
    private final String message;
    
    public MissingChangelogEntriesIntermediateResponse(final String message, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.12", encodeValue(message), controls);
        this.message = message;
    }
    
    public MissingChangelogEntriesIntermediateResponse(final IntermediateResponse r) throws LDAPException {
        super(r);
        final ASN1OctetString value = r.getValue();
        if (value == null) {
            this.message = null;
            return;
        }
        ASN1Sequence valueSequence;
        try {
            valueSequence = ASN1Sequence.decodeAsSequence(value.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_MISSING_CHANGELOG_ENTRIES_IR_VALUE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        String msg = null;
        final ASN1Element[] arr$ = valueSequence.elements();
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final ASN1Element e2 = arr$[i$];
            final byte type = e2.getType();
            switch (type) {
                case Byte.MIN_VALUE: {
                    msg = ASN1OctetString.decodeAsOctetString(e2).stringValue();
                    ++i$;
                    continue;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_MISSING_CHANGELOG_ENTRIES_IR_UNEXPECTED_VALUE_TYPE.get(StaticUtils.toHex(type)));
                }
            }
        }
        this.message = msg;
    }
    
    private static ASN1OctetString encodeValue(final String message) {
        if (message == null) {
            return null;
        }
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString((byte)(-128), message) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public String getIntermediateResponseName() {
        return ExtOpMessages.INFO_MISSING_CHANGELOG_ENTRIES_IR_NAME.get();
    }
    
    @Override
    public String valueToString() {
        if (this.message == null) {
            return null;
        }
        final StringBuilder buffer = new StringBuilder();
        buffer.append("message='");
        buffer.append(this.message);
        buffer.append('\'');
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("MissingChangelogEntriesIntermediateResponse(");
        boolean appended = false;
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append("messageID=");
            buffer.append(messageID);
            appended = true;
        }
        if (this.message != null) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("message='");
            buffer.append(this.message);
            buffer.append('\'');
            appended = true;
        }
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            if (appended) {
                buffer.append(", ");
            }
            buffer.append("controls={");
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
