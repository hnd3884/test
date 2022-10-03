package com.unboundid.asn1;

import com.unboundid.util.Debug;
import java.math.BigInteger;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1BigInteger extends ASN1Element
{
    private static final long serialVersionUID = 2631806934961821260L;
    private final BigInteger value;
    
    public ASN1BigInteger(final BigInteger value) {
        this((byte)2, value);
    }
    
    public ASN1BigInteger(final byte type, final BigInteger value) {
        super(type, value.toByteArray());
        this.value = value;
    }
    
    private ASN1BigInteger(final byte type, final BigInteger bigIntegerValue, final byte[] berValue) {
        super(type, berValue);
        this.value = bigIntegerValue;
    }
    
    public ASN1BigInteger(final long value) {
        this((byte)2, BigInteger.valueOf(value));
    }
    
    public ASN1BigInteger(final byte type, final long value) {
        this(type, BigInteger.valueOf(value));
    }
    
    public BigInteger getBigIntegerValue() {
        return this.value;
    }
    
    public static ASN1BigInteger decodeAsBigInteger(final byte[] elementBytes) throws ASN1Exception {
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
            if (length == 0) {
                throw new ASN1Exception(ASN1Messages.ERR_BIG_INTEGER_DECODE_EMPTY_VALUE.get());
            }
            final byte[] elementValue = new byte[length];
            System.arraycopy(elementBytes, valueStartPos, elementValue, 0, length);
            final BigInteger bigIntegerValue = new BigInteger(elementValue);
            return new ASN1BigInteger(elementBytes[0], bigIntegerValue, elementValue);
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
    
    public static ASN1BigInteger decodeAsBigInteger(final ASN1Element element) throws ASN1Exception {
        final byte[] value = element.getValue();
        if (value.length == 0) {
            throw new ASN1Exception(ASN1Messages.ERR_BIG_INTEGER_DECODE_EMPTY_VALUE.get());
        }
        return new ASN1BigInteger(element.getType(), new BigInteger(value), value);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.value);
    }
}
