package com.unboundid.asn1;

import com.unboundid.util.StaticUtils;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.unboundid.util.Debug;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class ASN1Element implements Serializable
{
    private static final long serialVersionUID = -1871166128693521335L;
    private final byte type;
    private final byte[] value;
    private int hashCode;
    private final int valueLength;
    private final int valueOffset;
    
    public ASN1Element(final byte type) {
        this.hashCode = -1;
        this.type = type;
        this.value = ASN1Constants.NO_VALUE;
        this.valueOffset = 0;
        this.valueLength = 0;
    }
    
    public ASN1Element(final byte type, final byte[] value) {
        this.hashCode = -1;
        this.type = type;
        if (value == null) {
            this.value = ASN1Constants.NO_VALUE;
        }
        else {
            this.value = value;
        }
        this.valueOffset = 0;
        this.valueLength = this.value.length;
    }
    
    public ASN1Element(final byte type, final byte[] value, final int offset, final int length) {
        this.hashCode = -1;
        this.type = type;
        this.value = value;
        this.valueOffset = offset;
        this.valueLength = length;
    }
    
    public final byte getType() {
        return this.type;
    }
    
    public byte getTypeClass() {
        return (byte)(this.type & 0xC0);
    }
    
    public boolean isConstructed() {
        return (this.type & 0x20) != 0x0;
    }
    
    byte[] getValueArray() {
        return this.value;
    }
    
    int getValueOffset() {
        return this.valueOffset;
    }
    
    public int getValueLength() {
        return this.valueLength;
    }
    
    public byte[] getValue() {
        if (this.valueOffset == 0 && this.valueLength == this.value.length) {
            return this.value;
        }
        final byte[] returnValue = new byte[this.valueLength];
        System.arraycopy(this.value, this.valueOffset, returnValue, 0, this.valueLength);
        return returnValue;
    }
    
    public final byte[] encode() {
        final byte[] valueArray = this.getValueArray();
        final int length = this.getValueLength();
        final int offset = this.getValueOffset();
        if (length == 0) {
            return new byte[] { this.type, 0 };
        }
        final byte[] lengthBytes = encodeLength(length);
        final byte[] elementBytes = new byte[1 + lengthBytes.length + length];
        elementBytes[0] = this.type;
        System.arraycopy(lengthBytes, 0, elementBytes, 1, lengthBytes.length);
        System.arraycopy(valueArray, offset, elementBytes, 1 + lengthBytes.length, length);
        return elementBytes;
    }
    
    static void encodeLengthTo(final int length, final ByteStringBuffer buffer) {
        if ((length & 0x7F) == length) {
            buffer.append((byte)length);
        }
        else if ((length & 0xFF) == length) {
            buffer.append((byte)(-127));
            buffer.append((byte)(length & 0xFF));
        }
        else if ((length & 0xFFFF) == length) {
            buffer.append((byte)(-126));
            buffer.append((byte)(length >> 8 & 0xFF));
            buffer.append((byte)(length & 0xFF));
        }
        else if ((length & 0xFFFFFF) == length) {
            buffer.append((byte)(-125));
            buffer.append((byte)(length >> 16 & 0xFF));
            buffer.append((byte)(length >> 8 & 0xFF));
            buffer.append((byte)(length & 0xFF));
        }
        else {
            buffer.append((byte)(-124));
            buffer.append((byte)(length >> 24 & 0xFF));
            buffer.append((byte)(length >> 16 & 0xFF));
            buffer.append((byte)(length >> 8 & 0xFF));
            buffer.append((byte)(length & 0xFF));
        }
    }
    
    public void encodeTo(final ByteStringBuffer buffer) {
        final byte[] valueArray = this.getValueArray();
        final int length = this.getValueLength();
        final int offset = this.getValueOffset();
        buffer.append(this.type);
        if (length == 0) {
            buffer.append((byte)0);
        }
        else {
            encodeLengthTo(length, buffer);
            buffer.append(valueArray, offset, length);
        }
    }
    
    public static byte[] encodeLength(final int length) {
        switch (length) {
            case 0: {
                return ASN1Constants.LENGTH_0;
            }
            case 1: {
                return ASN1Constants.LENGTH_1;
            }
            case 2: {
                return ASN1Constants.LENGTH_2;
            }
            case 3: {
                return ASN1Constants.LENGTH_3;
            }
            case 4: {
                return ASN1Constants.LENGTH_4;
            }
            case 5: {
                return ASN1Constants.LENGTH_5;
            }
            case 6: {
                return ASN1Constants.LENGTH_6;
            }
            case 7: {
                return ASN1Constants.LENGTH_7;
            }
            case 8: {
                return ASN1Constants.LENGTH_8;
            }
            case 9: {
                return ASN1Constants.LENGTH_9;
            }
            case 10: {
                return ASN1Constants.LENGTH_10;
            }
            case 11: {
                return ASN1Constants.LENGTH_11;
            }
            case 12: {
                return ASN1Constants.LENGTH_12;
            }
            case 13: {
                return ASN1Constants.LENGTH_13;
            }
            case 14: {
                return ASN1Constants.LENGTH_14;
            }
            case 15: {
                return ASN1Constants.LENGTH_15;
            }
            case 16: {
                return ASN1Constants.LENGTH_16;
            }
            case 17: {
                return ASN1Constants.LENGTH_17;
            }
            case 18: {
                return ASN1Constants.LENGTH_18;
            }
            case 19: {
                return ASN1Constants.LENGTH_19;
            }
            case 20: {
                return ASN1Constants.LENGTH_20;
            }
            case 21: {
                return ASN1Constants.LENGTH_21;
            }
            case 22: {
                return ASN1Constants.LENGTH_22;
            }
            case 23: {
                return ASN1Constants.LENGTH_23;
            }
            case 24: {
                return ASN1Constants.LENGTH_24;
            }
            case 25: {
                return ASN1Constants.LENGTH_25;
            }
            case 26: {
                return ASN1Constants.LENGTH_26;
            }
            case 27: {
                return ASN1Constants.LENGTH_27;
            }
            case 28: {
                return ASN1Constants.LENGTH_28;
            }
            case 29: {
                return ASN1Constants.LENGTH_29;
            }
            case 30: {
                return ASN1Constants.LENGTH_30;
            }
            case 31: {
                return ASN1Constants.LENGTH_31;
            }
            case 32: {
                return ASN1Constants.LENGTH_32;
            }
            case 33: {
                return ASN1Constants.LENGTH_33;
            }
            case 34: {
                return ASN1Constants.LENGTH_34;
            }
            case 35: {
                return ASN1Constants.LENGTH_35;
            }
            case 36: {
                return ASN1Constants.LENGTH_36;
            }
            case 37: {
                return ASN1Constants.LENGTH_37;
            }
            case 38: {
                return ASN1Constants.LENGTH_38;
            }
            case 39: {
                return ASN1Constants.LENGTH_39;
            }
            case 40: {
                return ASN1Constants.LENGTH_40;
            }
            case 41: {
                return ASN1Constants.LENGTH_41;
            }
            case 42: {
                return ASN1Constants.LENGTH_42;
            }
            case 43: {
                return ASN1Constants.LENGTH_43;
            }
            case 44: {
                return ASN1Constants.LENGTH_44;
            }
            case 45: {
                return ASN1Constants.LENGTH_45;
            }
            case 46: {
                return ASN1Constants.LENGTH_46;
            }
            case 47: {
                return ASN1Constants.LENGTH_47;
            }
            case 48: {
                return ASN1Constants.LENGTH_48;
            }
            case 49: {
                return ASN1Constants.LENGTH_49;
            }
            case 50: {
                return ASN1Constants.LENGTH_50;
            }
            case 51: {
                return ASN1Constants.LENGTH_51;
            }
            case 52: {
                return ASN1Constants.LENGTH_52;
            }
            case 53: {
                return ASN1Constants.LENGTH_53;
            }
            case 54: {
                return ASN1Constants.LENGTH_54;
            }
            case 55: {
                return ASN1Constants.LENGTH_55;
            }
            case 56: {
                return ASN1Constants.LENGTH_56;
            }
            case 57: {
                return ASN1Constants.LENGTH_57;
            }
            case 58: {
                return ASN1Constants.LENGTH_58;
            }
            case 59: {
                return ASN1Constants.LENGTH_59;
            }
            case 60: {
                return ASN1Constants.LENGTH_60;
            }
            case 61: {
                return ASN1Constants.LENGTH_61;
            }
            case 62: {
                return ASN1Constants.LENGTH_62;
            }
            case 63: {
                return ASN1Constants.LENGTH_63;
            }
            case 64: {
                return ASN1Constants.LENGTH_64;
            }
            case 65: {
                return ASN1Constants.LENGTH_65;
            }
            case 66: {
                return ASN1Constants.LENGTH_66;
            }
            case 67: {
                return ASN1Constants.LENGTH_67;
            }
            case 68: {
                return ASN1Constants.LENGTH_68;
            }
            case 69: {
                return ASN1Constants.LENGTH_69;
            }
            case 70: {
                return ASN1Constants.LENGTH_70;
            }
            case 71: {
                return ASN1Constants.LENGTH_71;
            }
            case 72: {
                return ASN1Constants.LENGTH_72;
            }
            case 73: {
                return ASN1Constants.LENGTH_73;
            }
            case 74: {
                return ASN1Constants.LENGTH_74;
            }
            case 75: {
                return ASN1Constants.LENGTH_75;
            }
            case 76: {
                return ASN1Constants.LENGTH_76;
            }
            case 77: {
                return ASN1Constants.LENGTH_77;
            }
            case 78: {
                return ASN1Constants.LENGTH_78;
            }
            case 79: {
                return ASN1Constants.LENGTH_79;
            }
            case 80: {
                return ASN1Constants.LENGTH_80;
            }
            case 81: {
                return ASN1Constants.LENGTH_81;
            }
            case 82: {
                return ASN1Constants.LENGTH_82;
            }
            case 83: {
                return ASN1Constants.LENGTH_83;
            }
            case 84: {
                return ASN1Constants.LENGTH_84;
            }
            case 85: {
                return ASN1Constants.LENGTH_85;
            }
            case 86: {
                return ASN1Constants.LENGTH_86;
            }
            case 87: {
                return ASN1Constants.LENGTH_87;
            }
            case 88: {
                return ASN1Constants.LENGTH_88;
            }
            case 89: {
                return ASN1Constants.LENGTH_89;
            }
            case 90: {
                return ASN1Constants.LENGTH_90;
            }
            case 91: {
                return ASN1Constants.LENGTH_91;
            }
            case 92: {
                return ASN1Constants.LENGTH_92;
            }
            case 93: {
                return ASN1Constants.LENGTH_93;
            }
            case 94: {
                return ASN1Constants.LENGTH_94;
            }
            case 95: {
                return ASN1Constants.LENGTH_95;
            }
            case 96: {
                return ASN1Constants.LENGTH_96;
            }
            case 97: {
                return ASN1Constants.LENGTH_97;
            }
            case 98: {
                return ASN1Constants.LENGTH_98;
            }
            case 99: {
                return ASN1Constants.LENGTH_99;
            }
            case 100: {
                return ASN1Constants.LENGTH_100;
            }
            case 101: {
                return ASN1Constants.LENGTH_101;
            }
            case 102: {
                return ASN1Constants.LENGTH_102;
            }
            case 103: {
                return ASN1Constants.LENGTH_103;
            }
            case 104: {
                return ASN1Constants.LENGTH_104;
            }
            case 105: {
                return ASN1Constants.LENGTH_105;
            }
            case 106: {
                return ASN1Constants.LENGTH_106;
            }
            case 107: {
                return ASN1Constants.LENGTH_107;
            }
            case 108: {
                return ASN1Constants.LENGTH_108;
            }
            case 109: {
                return ASN1Constants.LENGTH_109;
            }
            case 110: {
                return ASN1Constants.LENGTH_110;
            }
            case 111: {
                return ASN1Constants.LENGTH_111;
            }
            case 112: {
                return ASN1Constants.LENGTH_112;
            }
            case 113: {
                return ASN1Constants.LENGTH_113;
            }
            case 114: {
                return ASN1Constants.LENGTH_114;
            }
            case 115: {
                return ASN1Constants.LENGTH_115;
            }
            case 116: {
                return ASN1Constants.LENGTH_116;
            }
            case 117: {
                return ASN1Constants.LENGTH_117;
            }
            case 118: {
                return ASN1Constants.LENGTH_118;
            }
            case 119: {
                return ASN1Constants.LENGTH_119;
            }
            case 120: {
                return ASN1Constants.LENGTH_120;
            }
            case 121: {
                return ASN1Constants.LENGTH_121;
            }
            case 122: {
                return ASN1Constants.LENGTH_122;
            }
            case 123: {
                return ASN1Constants.LENGTH_123;
            }
            case 124: {
                return ASN1Constants.LENGTH_124;
            }
            case 125: {
                return ASN1Constants.LENGTH_125;
            }
            case 126: {
                return ASN1Constants.LENGTH_126;
            }
            case 127: {
                return ASN1Constants.LENGTH_127;
            }
            default: {
                if ((length & 0xFF) == length) {
                    return new byte[] { -127, (byte)(length & 0xFF) };
                }
                if ((length & 0xFFFF) == length) {
                    return new byte[] { -126, (byte)(length >> 8 & 0xFF), (byte)(length & 0xFF) };
                }
                if ((length & 0xFFFFFF) == length) {
                    return new byte[] { -125, (byte)(length >> 16 & 0xFF), (byte)(length >> 8 & 0xFF), (byte)(length & 0xFF) };
                }
                return new byte[] { -124, (byte)(length >> 24 & 0xFF), (byte)(length >> 16 & 0xFF), (byte)(length >> 8 & 0xFF), (byte)(length & 0xFF) };
            }
        }
    }
    
    public static ASN1Element decode(final byte[] elementBytes) throws ASN1Exception {
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
            final byte[] value = new byte[length];
            System.arraycopy(elementBytes, valueStartPos, value, 0, length);
            return new ASN1Element(elementBytes[0], value);
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
    
    public final ASN1BitString decodeAsBitString() throws ASN1Exception {
        return ASN1BitString.decodeAsBitString(this);
    }
    
    public final ASN1Boolean decodeAsBoolean() throws ASN1Exception {
        return ASN1Boolean.decodeAsBoolean(this);
    }
    
    public final ASN1Enumerated decodeAsEnumerated() throws ASN1Exception {
        return ASN1Enumerated.decodeAsEnumerated(this);
    }
    
    public final ASN1GeneralizedTime decodeAsGeneralizedTime() throws ASN1Exception {
        return ASN1GeneralizedTime.decodeAsGeneralizedTime(this);
    }
    
    public final ASN1IA5String decodeAsIA5String() throws ASN1Exception {
        return ASN1IA5String.decodeAsIA5String(this);
    }
    
    public final ASN1Integer decodeAsInteger() throws ASN1Exception {
        return ASN1Integer.decodeAsInteger(this);
    }
    
    public final ASN1Long decodeAsLong() throws ASN1Exception {
        return ASN1Long.decodeAsLong(this);
    }
    
    public final ASN1BigInteger decodeAsBigInteger() throws ASN1Exception {
        return ASN1BigInteger.decodeAsBigInteger(this);
    }
    
    public final ASN1Null decodeAsNull() throws ASN1Exception {
        return ASN1Null.decodeAsNull(this);
    }
    
    public final ASN1NumericString decodeAsNumericString() throws ASN1Exception {
        return ASN1NumericString.decodeAsNumericString(this);
    }
    
    public final ASN1ObjectIdentifier decodeAsObjectIdentifier() throws ASN1Exception {
        return ASN1ObjectIdentifier.decodeAsObjectIdentifier(this);
    }
    
    public final ASN1OctetString decodeAsOctetString() {
        return ASN1OctetString.decodeAsOctetString(this);
    }
    
    public final ASN1PrintableString decodeAsPrintableString() throws ASN1Exception {
        return ASN1PrintableString.decodeAsPrintableString(this);
    }
    
    public final ASN1Sequence decodeAsSequence() throws ASN1Exception {
        return ASN1Sequence.decodeAsSequence(this);
    }
    
    public final ASN1Set decodeAsSet() throws ASN1Exception {
        return ASN1Set.decodeAsSet(this);
    }
    
    public final ASN1UTCTime decodeAsUTCTime() throws ASN1Exception {
        return ASN1UTCTime.decodeAsUTCTime(this);
    }
    
    public final ASN1UTF8String decodeAsUTF8String() throws ASN1Exception {
        return ASN1UTF8String.decodeAsUTF8String(this);
    }
    
    public static ASN1Element readFrom(final InputStream inputStream) throws IOException, ASN1Exception {
        return readFrom(inputStream, -1);
    }
    
    public static ASN1Element readFrom(final InputStream inputStream, final int maxSize) throws IOException, ASN1Exception {
        final int typeInt = inputStream.read();
        if (typeInt < 0) {
            return null;
        }
        final byte type = (byte)typeInt;
        int length = inputStream.read();
        if (length < 0) {
            throw new ASN1Exception(ASN1Messages.ERR_READ_END_BEFORE_FIRST_LENGTH.get());
        }
        if (length > 127) {
            final int numLengthBytes = length & 0x7F;
            length = 0;
            if (numLengthBytes < 1 || numLengthBytes > 4) {
                throw new ASN1Exception(ASN1Messages.ERR_READ_LENGTH_TOO_LONG.get(numLengthBytes));
            }
            for (int i = 0; i < numLengthBytes; ++i) {
                final int lengthInt = inputStream.read();
                if (lengthInt < 0) {
                    throw new ASN1Exception(ASN1Messages.ERR_READ_END_BEFORE_LENGTH_END.get());
                }
                length <<= 8;
                length |= (lengthInt & 0xFF);
            }
        }
        if (length < 0 || (maxSize > 0 && length > maxSize)) {
            throw new ASN1Exception(ASN1Messages.ERR_READ_LENGTH_EXCEEDS_MAX.get(length, maxSize));
        }
        int totalBytesRead = 0;
        int bytesRemaining = length;
        final byte[] value = new byte[length];
        while (totalBytesRead < length) {
            final int bytesRead = inputStream.read(value, totalBytesRead, bytesRemaining);
            if (bytesRead < 0) {
                throw new ASN1Exception(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
            }
            totalBytesRead += bytesRead;
            bytesRemaining -= bytesRead;
        }
        final ASN1Element e = new ASN1Element(type, value);
        Debug.debugASN1Read(e);
        return e;
    }
    
    public final int writeTo(final OutputStream outputStream) throws IOException {
        Debug.debugASN1Write(this);
        final ByteStringBuffer buffer = new ByteStringBuffer();
        this.encodeTo(buffer);
        buffer.write(outputStream);
        return buffer.length();
    }
    
    @Override
    public final int hashCode() {
        if (this.hashCode == -1) {
            int hash = 0;
            for (final byte b : this.getValue()) {
                hash = hash * 31 + b;
            }
            this.hashCode = hash;
        }
        return this.hashCode;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        try {
            final ASN1Element e = (ASN1Element)o;
            return this.type == e.getType() && Arrays.equals(this.getValue(), e.getValue());
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            return false;
        }
    }
    
    public final boolean equalsIgnoreType(final ASN1Element element) {
        return element != null && (element == this || Arrays.equals(this.getValue(), element.getValue()));
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        final byte[] v = this.getValue();
        buffer.append("ASN1Element(type=");
        StaticUtils.toHex(this.type, buffer);
        buffer.append(", valueLength=");
        buffer.append(v.length);
        buffer.append(", valueBytes='");
        StaticUtils.toHex(v, buffer);
        buffer.append("')");
    }
}
