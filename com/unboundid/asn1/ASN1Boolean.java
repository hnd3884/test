package com.unboundid.asn1;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Boolean extends ASN1Element
{
    public static final ASN1Boolean UNIVERSAL_BOOLEAN_FALSE_ELEMENT;
    public static final ASN1Boolean UNIVERSAL_BOOLEAN_TRUE_ELEMENT;
    private static final long serialVersionUID = 7131700816847855524L;
    private final boolean booleanValue;
    
    public ASN1Boolean(final boolean booleanValue) {
        super((byte)1, booleanValue ? ASN1Constants.BOOLEAN_VALUE_TRUE : ASN1Constants.BOOLEAN_VALUE_FALSE);
        this.booleanValue = booleanValue;
    }
    
    public ASN1Boolean(final byte type, final boolean booleanValue) {
        super(type, booleanValue ? ASN1Constants.BOOLEAN_VALUE_TRUE : ASN1Constants.BOOLEAN_VALUE_FALSE);
        this.booleanValue = booleanValue;
    }
    
    private ASN1Boolean(final byte type, final boolean booleanValue, final byte[] value) {
        super(type, value);
        this.booleanValue = booleanValue;
    }
    
    public boolean booleanValue() {
        return this.booleanValue;
    }
    
    public static ASN1Boolean decodeAsBoolean(final byte[] elementBytes) throws ASN1Exception {
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
            if (length != 1) {
                throw new ASN1Exception(ASN1Messages.ERR_BOOLEAN_INVALID_LENGTH.get());
            }
            final byte[] value = { elementBytes[valueStartPos] };
            final boolean booleanValue = value[0] != 0;
            return new ASN1Boolean(elementBytes[0], booleanValue, value);
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
    
    public static ASN1Boolean decodeAsBoolean(final ASN1Element element) throws ASN1Exception {
        final byte[] value = element.getValue();
        if (value.length != 1) {
            throw new ASN1Exception(ASN1Messages.ERR_BOOLEAN_INVALID_LENGTH.get());
        }
        if (value[0] == 0) {
            return new ASN1Boolean(element.getType(), false, value);
        }
        return new ASN1Boolean(element.getType(), true, value);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.booleanValue);
    }
    
    static {
        UNIVERSAL_BOOLEAN_FALSE_ELEMENT = new ASN1Boolean(false);
        UNIVERSAL_BOOLEAN_TRUE_ELEMENT = new ASN1Boolean(true);
    }
}
