package com.unboundid.asn1;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Long extends ASN1Element
{
    private static final long serialVersionUID = -3445506299288414013L;
    private final long longValue;
    
    public ASN1Long(final long longValue) {
        super((byte)2, encodeLongValue(longValue));
        this.longValue = longValue;
    }
    
    public ASN1Long(final byte type, final long longValue) {
        super(type, encodeLongValue(longValue));
        this.longValue = longValue;
    }
    
    private ASN1Long(final byte type, final long longValue, final byte[] value) {
        super(type, value);
        this.longValue = longValue;
    }
    
    static byte[] encodeLongValue(final long longValue) {
        if (longValue < 0L) {
            if ((longValue & 0xFFFFFFFFFFFFFF80L) == 0xFFFFFFFFFFFFFF80L) {
                return new byte[] { (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0xFFFFFFFFFFFF8000L) == 0xFFFFFFFFFFFF8000L) {
                return new byte[] { (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0xFFFFFFFFFF800000L) == 0xFFFFFFFFFF800000L) {
                return new byte[] { (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0xFFFFFFFF80000000L) == 0xFFFFFFFF80000000L) {
                return new byte[] { (byte)(longValue >> 24 & 0xFFL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0xFFFFFF8000000000L) == 0xFFFFFF8000000000L) {
                return new byte[] { (byte)(longValue >> 32 & 0xFFL), (byte)(longValue >> 24 & 0xFFL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0xFFFF800000000000L) == 0xFFFF800000000000L) {
                return new byte[] { (byte)(longValue >> 40 & 0xFFL), (byte)(longValue >> 32 & 0xFFL), (byte)(longValue >> 24 & 0xFFL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0xFF80000000000000L) == 0xFF80000000000000L) {
                return new byte[] { (byte)(longValue >> 48 & 0xFFL), (byte)(longValue >> 40 & 0xFFL), (byte)(longValue >> 32 & 0xFFL), (byte)(longValue >> 24 & 0xFFL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            return new byte[] { (byte)(longValue >> 56 & 0xFFL), (byte)(longValue >> 48 & 0xFFL), (byte)(longValue >> 40 & 0xFFL), (byte)(longValue >> 32 & 0xFFL), (byte)(longValue >> 24 & 0xFFL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
        }
        else {
            if ((longValue & 0x7FL) == longValue) {
                return new byte[] { (byte)(longValue & 0x7FL) };
            }
            if ((longValue & 0x7FFFL) == longValue) {
                return new byte[] { (byte)(longValue >> 8 & 0x7FL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0x7FFFFFL) == longValue) {
                return new byte[] { (byte)(longValue >> 16 & 0x7FL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0x7FFFFFFFL) == longValue) {
                return new byte[] { (byte)(longValue >> 24 & 0x7FL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0x7FFFFFFFFFL) == longValue) {
                return new byte[] { (byte)(longValue >> 32 & 0x7FL), (byte)(longValue >> 24 & 0xFFL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0x7FFFFFFFFFFFL) == longValue) {
                return new byte[] { (byte)(longValue >> 40 & 0x7FL), (byte)(longValue >> 32 & 0xFFL), (byte)(longValue >> 24 & 0xFFL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            if ((longValue & 0x7FFFFFFFFFFFFFL) == longValue) {
                return new byte[] { (byte)(longValue >> 48 & 0x7FL), (byte)(longValue >> 40 & 0xFFL), (byte)(longValue >> 32 & 0xFFL), (byte)(longValue >> 24 & 0xFFL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
            }
            return new byte[] { (byte)(longValue >> 56 & 0x7FL), (byte)(longValue >> 48 & 0xFFL), (byte)(longValue >> 40 & 0xFFL), (byte)(longValue >> 32 & 0xFFL), (byte)(longValue >> 24 & 0xFFL), (byte)(longValue >> 16 & 0xFFL), (byte)(longValue >> 8 & 0xFFL), (byte)(longValue & 0xFFL) };
        }
    }
    
    public long longValue() {
        return this.longValue;
    }
    
    public static ASN1Long decodeAsLong(final byte[] elementBytes) throws ASN1Exception {
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
            long longValue = 0L;
            switch (value.length) {
                case 1: {
                    longValue = ((long)value[0] & 0xFFL);
                    if (((long)value[0] & 0x80L) != 0x0L) {
                        longValue |= 0xFFFFFFFFFFFFFF00L;
                        break;
                    }
                    break;
                }
                case 2: {
                    longValue = (((long)value[0] & 0xFFL) << 8 | ((long)value[1] & 0xFFL));
                    if (((long)value[0] & 0x80L) != 0x0L) {
                        longValue |= 0xFFFFFFFFFFFF0000L;
                        break;
                    }
                    break;
                }
                case 3: {
                    longValue = (((long)value[0] & 0xFFL) << 16 | ((long)value[1] & 0xFFL) << 8 | ((long)value[2] & 0xFFL));
                    if (((long)value[0] & 0x80L) != 0x0L) {
                        longValue |= 0xFFFFFFFFFF000000L;
                        break;
                    }
                    break;
                }
                case 4: {
                    longValue = (((long)value[0] & 0xFFL) << 24 | ((long)value[1] & 0xFFL) << 16 | ((long)value[2] & 0xFFL) << 8 | ((long)value[3] & 0xFFL));
                    if (((long)value[0] & 0x80L) != 0x0L) {
                        longValue |= 0xFFFFFFFF00000000L;
                        break;
                    }
                    break;
                }
                case 5: {
                    longValue = (((long)value[0] & 0xFFL) << 32 | ((long)value[1] & 0xFFL) << 24 | ((long)value[2] & 0xFFL) << 16 | ((long)value[3] & 0xFFL) << 8 | ((long)value[4] & 0xFFL));
                    if (((long)value[0] & 0x80L) != 0x0L) {
                        longValue |= 0xFFFFFF0000000000L;
                        break;
                    }
                    break;
                }
                case 6: {
                    longValue = (((long)value[0] & 0xFFL) << 40 | ((long)value[1] & 0xFFL) << 32 | ((long)value[2] & 0xFFL) << 24 | ((long)value[3] & 0xFFL) << 16 | ((long)value[4] & 0xFFL) << 8 | ((long)value[5] & 0xFFL));
                    if (((long)value[0] & 0x80L) != 0x0L) {
                        longValue |= 0xFFFF000000000000L;
                        break;
                    }
                    break;
                }
                case 7: {
                    longValue = (((long)value[0] & 0xFFL) << 48 | ((long)value[1] & 0xFFL) << 40 | ((long)value[2] & 0xFFL) << 32 | ((long)value[3] & 0xFFL) << 24 | ((long)value[4] & 0xFFL) << 16 | ((long)value[5] & 0xFFL) << 8 | ((long)value[6] & 0xFFL));
                    if (((long)value[0] & 0x80L) != 0x0L) {
                        longValue |= 0xFF00000000000000L;
                        break;
                    }
                    break;
                }
                case 8: {
                    longValue = (((long)value[0] & 0xFFL) << 56 | ((long)value[1] & 0xFFL) << 48 | ((long)value[2] & 0xFFL) << 40 | ((long)value[3] & 0xFFL) << 32 | ((long)value[4] & 0xFFL) << 24 | ((long)value[5] & 0xFFL) << 16 | ((long)value[6] & 0xFFL) << 8 | ((long)value[7] & 0xFFL));
                    break;
                }
                default: {
                    throw new ASN1Exception(ASN1Messages.ERR_LONG_INVALID_LENGTH.get(value.length));
                }
            }
            return new ASN1Long(elementBytes[0], longValue, value);
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
    
    public static ASN1Long decodeAsLong(final ASN1Element element) throws ASN1Exception {
        final byte[] value = element.getValue();
        long longValue = 0L;
        switch (value.length) {
            case 1: {
                longValue = ((long)value[0] & 0xFFL);
                if (((long)value[0] & 0x80L) != 0x0L) {
                    longValue |= 0xFFFFFFFFFFFFFF00L;
                    break;
                }
                break;
            }
            case 2: {
                longValue = (((long)value[0] & 0xFFL) << 8 | ((long)value[1] & 0xFFL));
                if (((long)value[0] & 0x80L) != 0x0L) {
                    longValue |= 0xFFFFFFFFFFFF0000L;
                    break;
                }
                break;
            }
            case 3: {
                longValue = (((long)value[0] & 0xFFL) << 16 | ((long)value[1] & 0xFFL) << 8 | ((long)value[2] & 0xFFL));
                if (((long)value[0] & 0x80L) != 0x0L) {
                    longValue |= 0xFFFFFFFFFF000000L;
                    break;
                }
                break;
            }
            case 4: {
                longValue = (((long)value[0] & 0xFFL) << 24 | ((long)value[1] & 0xFFL) << 16 | ((long)value[2] & 0xFFL) << 8 | ((long)value[3] & 0xFFL));
                if (((long)value[0] & 0x80L) != 0x0L) {
                    longValue |= 0xFFFFFFFF00000000L;
                    break;
                }
                break;
            }
            case 5: {
                longValue = (((long)value[0] & 0xFFL) << 32 | ((long)value[1] & 0xFFL) << 24 | ((long)value[2] & 0xFFL) << 16 | ((long)value[3] & 0xFFL) << 8 | ((long)value[4] & 0xFFL));
                if (((long)value[0] & 0x80L) != 0x0L) {
                    longValue |= 0xFFFFFF0000000000L;
                    break;
                }
                break;
            }
            case 6: {
                longValue = (((long)value[0] & 0xFFL) << 40 | ((long)value[1] & 0xFFL) << 32 | ((long)value[2] & 0xFFL) << 24 | ((long)value[3] & 0xFFL) << 16 | ((long)value[4] & 0xFFL) << 8 | ((long)value[5] & 0xFFL));
                if (((long)value[0] & 0x80L) != 0x0L) {
                    longValue |= 0xFFFF000000000000L;
                    break;
                }
                break;
            }
            case 7: {
                longValue = (((long)value[0] & 0xFFL) << 48 | ((long)value[1] & 0xFFL) << 40 | ((long)value[2] & 0xFFL) << 32 | ((long)value[3] & 0xFFL) << 24 | ((long)value[4] & 0xFFL) << 16 | ((long)value[5] & 0xFFL) << 8 | ((long)value[6] & 0xFFL));
                if (((long)value[0] & 0x80L) != 0x0L) {
                    longValue |= 0xFF00000000000000L;
                    break;
                }
                break;
            }
            case 8: {
                longValue = (((long)value[0] & 0xFFL) << 56 | ((long)value[1] & 0xFFL) << 48 | ((long)value[2] & 0xFFL) << 40 | ((long)value[3] & 0xFFL) << 32 | ((long)value[4] & 0xFFL) << 24 | ((long)value[5] & 0xFFL) << 16 | ((long)value[6] & 0xFFL) << 8 | ((long)value[7] & 0xFFL));
                break;
            }
            default: {
                throw new ASN1Exception(ASN1Messages.ERR_LONG_INVALID_LENGTH.get(value.length));
            }
        }
        return new ASN1Long(element.getType(), longValue, value);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.longValue);
    }
}
