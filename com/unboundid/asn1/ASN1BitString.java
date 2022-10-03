package com.unboundid.asn1;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1BitString extends ASN1Element
{
    private static final long serialVersionUID = -5962171503831966571L;
    private final boolean[] bits;
    private final byte[] bytes;
    
    public ASN1BitString(final boolean... bits) {
        this((byte)3, bits);
    }
    
    public ASN1BitString(final byte type, final boolean... bits) {
        this(type, bits, null, encodeValue(bits));
    }
    
    private ASN1BitString(final byte type, final boolean[] bits, final byte[] bytes, final byte[] encodedValue) {
        super(type, encodedValue);
        this.bits = bits;
        if (bytes == null) {
            if (bits.length % 8 == 0) {
                this.bytes = new byte[bits.length / 8];
                byte currentByte = 0;
                int byteIndex = 0;
                for (int i = 0; i < bits.length; ++i) {
                    currentByte <<= 1;
                    if (bits[i]) {
                        currentByte |= 0x1;
                    }
                    if ((i + 1) % 8 == 0) {
                        this.bytes[byteIndex++] = currentByte;
                        currentByte = 0;
                    }
                }
            }
            else {
                this.bytes = null;
            }
        }
        else {
            this.bytes = bytes;
        }
    }
    
    public ASN1BitString(final String stringRepresentation) throws ASN1Exception {
        this((byte)3, stringRepresentation);
    }
    
    public ASN1BitString(final byte type, final String stringRepresentation) throws ASN1Exception {
        this(type, getBits(stringRepresentation));
    }
    
    private static boolean[] getBits(final String s) throws ASN1Exception {
        final char[] chars = s.toCharArray();
        final boolean[] bits = new boolean[chars.length];
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '0') {
                bits[i] = false;
            }
            else {
                if (chars[i] != '1') {
                    throw new ASN1Exception(ASN1Messages.ERR_BIT_STRING_DECODE_STRING_INVALID_CHAR.get());
                }
                bits[i] = true;
            }
        }
        return bits;
    }
    
    private static byte[] encodeValue(final boolean... bits) {
        final int numBitsMod8 = bits.length % 8;
        int paddingBitsNeeded;
        byte[] encodedValue;
        if (numBitsMod8 == 0) {
            paddingBitsNeeded = 0;
            encodedValue = new byte[bits.length / 8 + 1];
        }
        else {
            paddingBitsNeeded = 8 - numBitsMod8;
            encodedValue = new byte[bits.length / 8 + 2];
        }
        encodedValue[0] = (byte)paddingBitsNeeded;
        byte currentByte = 0;
        int bitIndex = 0;
        int encodedValueIndex = 1;
        for (final boolean bit : bits) {
            currentByte <<= 1;
            if (bit) {
                currentByte |= 0x1;
            }
            if (++bitIndex % 8 == 0) {
                encodedValue[encodedValueIndex] = currentByte;
                currentByte = 0;
                ++encodedValueIndex;
            }
        }
        if (paddingBitsNeeded > 0) {
            currentByte <<= (byte)paddingBitsNeeded;
            encodedValue[encodedValueIndex] = currentByte;
        }
        return encodedValue;
    }
    
    public boolean[] getBits() {
        return this.bits;
    }
    
    public byte[] getBytes() throws ASN1Exception {
        if (this.bytes == null) {
            throw new ASN1Exception(ASN1Messages.ERR_BIT_STRING_GET_BYTES_NOT_MULTIPLE_OF_EIGHT_BITS.get(this.bits.length));
        }
        return this.bytes;
    }
    
    public static boolean[] getBitsForBytes(final byte... bytes) {
        final boolean[] bits = new boolean[bytes.length * 8];
        for (int i = 0; i < bytes.length; ++i) {
            final byte b = bytes[i];
            bits[i * 8] = ((b & 0x80) == 0x80);
            bits[i * 8 + 1] = ((b & 0x40) == 0x40);
            bits[i * 8 + 2] = ((b & 0x20) == 0x20);
            bits[i * 8 + 3] = ((b & 0x10) == 0x10);
            bits[i * 8 + 4] = ((b & 0x8) == 0x8);
            bits[i * 8 + 5] = ((b & 0x4) == 0x4);
            bits[i * 8 + 6] = ((b & 0x2) == 0x2);
            bits[i * 8 + 7] = ((b & 0x1) == 0x1);
        }
        return bits;
    }
    
    public static ASN1BitString decodeAsBitString(final byte[] elementBytes) throws ASN1Exception {
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
            final boolean[] bits = decodeValue(elementValue);
            byte[] bytes;
            if (bits.length % 8 == 0) {
                bytes = new byte[elementValue.length - 1];
                System.arraycopy(elementValue, 1, bytes, 0, bytes.length);
            }
            else {
                bytes = null;
            }
            return new ASN1BitString(elementBytes[0], bits, bytes, elementValue);
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
    
    public static ASN1BitString decodeAsBitString(final ASN1Element element) throws ASN1Exception {
        final byte[] elementValue = element.getValue();
        final boolean[] bits = decodeValue(elementValue);
        byte[] bytes;
        if (bits.length % 8 == 0) {
            bytes = new byte[elementValue.length - 1];
            System.arraycopy(elementValue, 1, bytes, 0, bytes.length);
        }
        else {
            bytes = null;
        }
        return new ASN1BitString(element.getType(), bits, bytes, element.getValue());
    }
    
    private static boolean[] decodeValue(final byte[] elementValue) throws ASN1Exception {
        if (elementValue.length == 0) {
            throw new ASN1Exception(ASN1Messages.ERR_BIT_STRING_DECODE_EMPTY_VALUE.get());
        }
        final int paddingBitsNeeded = elementValue[0] & 0xFF;
        if (paddingBitsNeeded > 7) {
            throw new ASN1Exception(ASN1Messages.ERR_BIT_STRING_DECODE_INVALID_PADDING_BIT_COUNT.get(paddingBitsNeeded));
        }
        if (paddingBitsNeeded > 0 && elementValue.length == 1) {
            throw new ASN1Exception(ASN1Messages.ERR_BIT_STRING_DECODE_NONZERO_PADDING_BIT_COUNT_WITH_NO_MORE_BYTES.get());
        }
        int bitsIndex = 0;
        final int numBits = (elementValue.length - 1) * 8 - paddingBitsNeeded;
        final boolean[] bits = new boolean[numBits];
        for (int i = 1; i < elementValue.length; ++i) {
            byte b = elementValue[i];
            if (i == elementValue.length - 1 && paddingBitsNeeded > 0) {
                for (int j = 0; j < 8 - paddingBitsNeeded; ++j) {
                    bits[bitsIndex++] = ((b & 0x80) == 0x80);
                    b <<= 1;
                }
            }
            else {
                bits[bitsIndex++] = ((b & 0x80) == 0x80);
                bits[bitsIndex++] = ((b & 0x40) == 0x40);
                bits[bitsIndex++] = ((b & 0x20) == 0x20);
                bits[bitsIndex++] = ((b & 0x10) == 0x10);
                bits[bitsIndex++] = ((b & 0x8) == 0x8);
                bits[bitsIndex++] = ((b & 0x4) == 0x4);
                bits[bitsIndex++] = ((b & 0x2) == 0x2);
                bits[bitsIndex++] = ((b & 0x1) == 0x1);
            }
        }
        return bits;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.ensureCapacity(buffer.length() + this.bits.length);
        for (final boolean bit : this.bits) {
            if (bit) {
                buffer.append('1');
            }
            else {
                buffer.append('0');
            }
        }
    }
}
