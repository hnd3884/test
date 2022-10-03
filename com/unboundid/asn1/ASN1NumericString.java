package com.unboundid.asn1;

import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1NumericString extends ASN1Element
{
    private static final long serialVersionUID = -3972798266250943461L;
    private final String stringValue;
    
    public ASN1NumericString(final String stringValue) throws ASN1Exception {
        this((byte)18, stringValue);
    }
    
    public ASN1NumericString(final byte type, final String stringValue) throws ASN1Exception {
        this(type, stringValue, StaticUtils.getBytes(stringValue));
    }
    
    private ASN1NumericString(final byte type, final String stringValue, final byte[] encodedValue) throws ASN1Exception {
        super(type, encodedValue);
        if (stringValue == null) {
            this.stringValue = "";
        }
        else {
            this.stringValue = stringValue;
            for (final char c : stringValue.toCharArray()) {
                if (c < '0' || c > '9') {
                    if (c != ' ') {
                        throw new ASN1Exception(ASN1Messages.ERR_NUMERIC_STRING_DECODE_VALUE_NOT_NUMERIC.get());
                    }
                }
            }
        }
    }
    
    public String stringValue() {
        return this.stringValue;
    }
    
    public static ASN1NumericString decodeAsNumericString(final byte[] elementBytes) throws ASN1Exception {
        try {
            int valueStartPos = 2;
            int length = elementBytes[1] & 0x7F;
            if (length != elementBytes[1]) {
                final int numLengthBytes = length;
                length = 0;
                for (int i = 0; i < numLengthBytes; ++i) {
                    length <<= 8;
                    length |= (elementBytes[valueStartPos++] & 0xFF);
                }
            }
            if (elementBytes.length - valueStartPos != length) {
                throw new ASN1Exception(ASN1Messages.ERR_ELEMENT_LENGTH_MISMATCH.get(length, elementBytes.length - valueStartPos));
            }
            final byte[] elementValue = new byte[length];
            System.arraycopy(elementBytes, valueStartPos, elementValue, 0, length);
            return new ASN1NumericString(elementBytes[0], StaticUtils.toUTF8String(elementValue), elementValue);
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw ae;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ASN1Exception(ASN1Messages.ERR_ELEMENT_DECODE_EXCEPTION.get(e), e);
        }
    }
    
    public static ASN1NumericString decodeAsNumericString(final ASN1Element element) throws ASN1Exception {
        final byte[] elementValue = element.getValue();
        return new ASN1NumericString(element.getType(), StaticUtils.toUTF8String(elementValue), elementValue);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.stringValue);
    }
}
