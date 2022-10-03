package com.unboundid.asn1;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Integer extends ASN1Element
{
    private static final long serialVersionUID = -733929804601994372L;
    private final int intValue;
    
    public ASN1Integer(final int intValue) {
        super((byte)2, encodeIntValue(intValue));
        this.intValue = intValue;
    }
    
    public ASN1Integer(final byte type, final int intValue) {
        super(type, encodeIntValue(intValue));
        this.intValue = intValue;
    }
    
    private ASN1Integer(final byte type, final int intValue, final byte[] value) {
        super(type, value);
        this.intValue = intValue;
    }
    
    static byte[] encodeIntValue(final int intValue) {
        if (intValue < 0) {
            if ((intValue & 0xFFFFFF80) == 0xFFFFFF80) {
                return new byte[] { (byte)(intValue & 0xFF) };
            }
            if ((intValue & 0xFFFF8000) == 0xFFFF8000) {
                return new byte[] { (byte)(intValue >> 8 & 0xFF), (byte)(intValue & 0xFF) };
            }
            if ((intValue & 0xFF800000) == 0xFF800000) {
                return new byte[] { (byte)(intValue >> 16 & 0xFF), (byte)(intValue >> 8 & 0xFF), (byte)(intValue & 0xFF) };
            }
            return new byte[] { (byte)(intValue >> 24 & 0xFF), (byte)(intValue >> 16 & 0xFF), (byte)(intValue >> 8 & 0xFF), (byte)(intValue & 0xFF) };
        }
        else {
            if ((intValue & 0x7F) == intValue) {
                return new byte[] { (byte)(intValue & 0x7F) };
            }
            if ((intValue & 0x7FFF) == intValue) {
                return new byte[] { (byte)(intValue >> 8 & 0x7F), (byte)(intValue & 0xFF) };
            }
            if ((intValue & 0x7FFFFF) == intValue) {
                return new byte[] { (byte)(intValue >> 16 & 0x7F), (byte)(intValue >> 8 & 0xFF), (byte)(intValue & 0xFF) };
            }
            return new byte[] { (byte)(intValue >> 24 & 0x7F), (byte)(intValue >> 16 & 0xFF), (byte)(intValue >> 8 & 0xFF), (byte)(intValue & 0xFF) };
        }
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static ASN1Integer decodeAsInteger(final byte[] elementBytes) throws ASN1Exception {
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
            int intValue = 0;
            switch (value.length) {
                case 1: {
                    intValue = (value[0] & 0xFF);
                    if ((value[0] & 0x80) != 0x0) {
                        intValue |= 0xFFFFFF00;
                        break;
                    }
                    break;
                }
                case 2: {
                    intValue = ((value[0] & 0xFF) << 8 | (value[1] & 0xFF));
                    if ((value[0] & 0x80) != 0x0) {
                        intValue |= 0xFFFF0000;
                        break;
                    }
                    break;
                }
                case 3: {
                    intValue = ((value[0] & 0xFF) << 16 | (value[1] & 0xFF) << 8 | (value[2] & 0xFF));
                    if ((value[0] & 0x80) != 0x0) {
                        intValue |= 0xFF000000;
                        break;
                    }
                    break;
                }
                case 4: {
                    intValue = ((value[0] & 0xFF) << 24 | (value[1] & 0xFF) << 16 | (value[2] & 0xFF) << 8 | (value[3] & 0xFF));
                    break;
                }
                default: {
                    throw new ASN1Exception(ASN1Messages.ERR_ENUMERATED_INVALID_LENGTH.get(value.length));
                }
            }
            return new ASN1Integer(elementBytes[0], intValue, value);
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
    
    public static ASN1Integer decodeAsInteger(final ASN1Element element) throws ASN1Exception {
        final byte[] value = element.getValue();
        int intValue = 0;
        switch (value.length) {
            case 1: {
                intValue = (value[0] & 0xFF);
                if ((value[0] & 0x80) != 0x0) {
                    intValue |= 0xFFFFFF00;
                    break;
                }
                break;
            }
            case 2: {
                intValue = ((value[0] & 0xFF) << 8 | (value[1] & 0xFF));
                if ((value[0] & 0x80) != 0x0) {
                    intValue |= 0xFFFF0000;
                    break;
                }
                break;
            }
            case 3: {
                intValue = ((value[0] & 0xFF) << 16 | (value[1] & 0xFF) << 8 | (value[2] & 0xFF));
                if ((value[0] & 0x80) != 0x0) {
                    intValue |= 0xFF000000;
                    break;
                }
                break;
            }
            case 4: {
                intValue = ((value[0] & 0xFF) << 24 | (value[1] & 0xFF) << 16 | (value[2] & 0xFF) << 8 | (value[3] & 0xFF));
                break;
            }
            default: {
                throw new ASN1Exception(ASN1Messages.ERR_INTEGER_INVALID_LENGTH.get(value.length));
            }
        }
        return new ASN1Integer(element.getType(), intValue, value);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.intValue);
    }
}
