package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.Control;
import java.util.Collection;
import com.unboundid.asn1.ASN1OctetString;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.IntermediateResponse;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class StreamDirectoryValuesIntermediateResponse extends IntermediateResponse
{
    public static final String STREAM_DIRECTORY_VALUES_INTERMEDIATE_RESPONSE_OID = "1.3.6.1.4.1.30221.2.6.7";
    public static final int RESULT_ALL_VALUES_RETURNED = 0;
    public static final int RESULT_MORE_VALUES_TO_RETURN = 1;
    public static final int RESULT_ATTRIBUTE_NOT_INDEXED = 2;
    public static final int RESULT_PROCESSING_ERROR = 3;
    private static final byte TYPE_ATTRIBUTE_NAME = Byte.MIN_VALUE;
    private static final byte TYPE_RESULT = -127;
    private static final byte TYPE_DIAGNOSTIC_MESSAGE = -126;
    private static final byte TYPE_VALUES = -93;
    private static final long serialVersionUID = -1756020236490168006L;
    private final int result;
    private final List<ASN1OctetString> values;
    private final String attributeName;
    private final String diagnosticMessage;
    
    public StreamDirectoryValuesIntermediateResponse(final String attributeName, final int result, final String diagnosticMessage, final Collection<ASN1OctetString> values, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.7", encodeValue(attributeName, result, diagnosticMessage, values), controls);
        this.attributeName = attributeName;
        this.result = result;
        this.diagnosticMessage = diagnosticMessage;
        if (values == null || values.isEmpty()) {
            this.values = Collections.emptyList();
        }
        else {
            this.values = Collections.unmodifiableList((List<? extends ASN1OctetString>)new ArrayList<ASN1OctetString>(values));
        }
    }
    
    public StreamDirectoryValuesIntermediateResponse(final IntermediateResponse intermediateResponse) throws LDAPException {
        super(intermediateResponse);
        final ASN1OctetString value = intermediateResponse.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_DIRECTORY_VALUES_RESPONSE_NO_VALUE.get());
        }
        int tmpResult = -1;
        String tmpAttr = null;
        String tmpMessage = null;
        final ArrayList<ASN1OctetString> tmpValues = new ArrayList<ASN1OctetString>(100);
        try {
            final ASN1Element[] arr$;
            final ASN1Element[] elements = arr$ = ASN1Element.decode(value.getValue()).decodeAsSequence().elements();
            for (final ASN1Element e : arr$) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        tmpAttr = e.decodeAsOctetString().stringValue();
                        break;
                    }
                    case -127: {
                        tmpResult = e.decodeAsEnumerated().intValue();
                        if (tmpResult < 0) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_DIRECTORY_VALUES_RESPONSE_INVALID_RESULT.get(tmpResult));
                        }
                        break;
                    }
                    case -126: {
                        tmpMessage = e.decodeAsOctetString().stringValue();
                        break;
                    }
                    case -93: {
                        final ASN1Element[] arr$2;
                        final ASN1Element[] valueElements = arr$2 = e.decodeAsSet().elements();
                        for (final ASN1Element ve : arr$2) {
                            tmpValues.add(ve.decodeAsOctetString());
                        }
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_DIRECTORY_VALUES_RESPONSE_INVALID_SEQUENCE_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
        }
        catch (final LDAPException le) {
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_DIRECTORY_VALUES_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (tmpResult < 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_DIRECTORY_VALUES_RESPONSE_NO_RESULT.get());
        }
        this.attributeName = tmpAttr;
        this.result = tmpResult;
        this.diagnosticMessage = tmpMessage;
        this.values = Collections.unmodifiableList((List<? extends ASN1OctetString>)tmpValues);
    }
    
    private static ASN1OctetString encodeValue(final String attributeName, final int result, final String diagnosticMessage, final Collection<ASN1OctetString> values) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        if (attributeName != null) {
            elements.add(new ASN1OctetString((byte)(-128), attributeName));
        }
        elements.add(new ASN1Enumerated((byte)(-127), result));
        if (diagnosticMessage != null) {
            elements.add(new ASN1OctetString((byte)(-126), diagnosticMessage));
        }
        if (values != null && !values.isEmpty()) {
            elements.add(new ASN1Set((byte)(-93), values));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public int getResult() {
        return this.result;
    }
    
    public String getDiagnosticMessage() {
        return this.diagnosticMessage;
    }
    
    public List<ASN1OctetString> getValues() {
        return this.values;
    }
    
    @Override
    public String getIntermediateResponseName() {
        return ExtOpMessages.INFO_INTERMEDIATE_RESPONSE_NAME_STREAM_DIRECTORY_VALUES.get();
    }
    
    @Override
    public String valueToString() {
        final StringBuilder buffer = new StringBuilder();
        if (this.attributeName != null) {
            buffer.append("attributeName='");
            buffer.append(this.attributeName);
            buffer.append("' ");
        }
        buffer.append("result='");
        switch (this.result) {
            case 0: {
                buffer.append("all values returned");
                break;
            }
            case 2: {
                buffer.append("attribute not indexed");
                break;
            }
            case 1: {
                buffer.append("more values to return");
                break;
            }
            case 3: {
                buffer.append("processing error");
                break;
            }
            default: {
                buffer.append(this.result);
                break;
            }
        }
        buffer.append('\'');
        if (this.diagnosticMessage != null) {
            buffer.append(" diagnosticMessage='");
            buffer.append(this.diagnosticMessage);
            buffer.append('\'');
        }
        buffer.append(" valueCount='");
        buffer.append(this.values.size());
        buffer.append('\'');
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("StreamDirectoryValuesIntermediateResponse(");
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append("messageID=");
            buffer.append(messageID);
            buffer.append(", ");
        }
        if (this.attributeName != null) {
            buffer.append("attributeName='");
            buffer.append(this.attributeName);
            buffer.append("', ");
        }
        buffer.append("result=");
        buffer.append(this.result);
        if (this.diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(this.diagnosticMessage);
            buffer.append('\'');
        }
        buffer.append(", values={");
        final Iterator<ASN1OctetString> iterator = this.values.iterator();
        while (iterator.hasNext()) {
            buffer.append('\'');
            buffer.append(iterator.next().stringValue());
            buffer.append('\'');
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
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
        buffer.append("})");
    }
}
