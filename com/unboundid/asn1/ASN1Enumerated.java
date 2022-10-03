package com.unboundid.asn1;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Enumerated extends ASN1Element
{
    private static final long serialVersionUID = -5915912036130847725L;
    private final int intValue;
    
    public ASN1Enumerated(final int intValue) {
        super((byte)10, ASN1Integer.encodeIntValue(intValue));
        this.intValue = intValue;
    }
    
    public ASN1Enumerated(final byte type, final int intValue) {
        super(type, ASN1Integer.encodeIntValue(intValue));
        this.intValue = intValue;
    }
    
    private ASN1Enumerated(final byte type, final int intValue, final byte[] value) {
        super(type, value);
        this.intValue = intValue;
    }
    
    public int intValue() {
        return this.intValue;
    }
    
    public static ASN1Enumerated decodeAsEnumerated(final byte[] elementBytes) throws ASN1Exception {
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
            return new ASN1Enumerated(elementBytes[0], intValue, value);
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
    
    public static ASN1Enumerated decodeAsEnumerated(final ASN1Element element) throws ASN1Exception {
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
                throw new ASN1Exception(ASN1Messages.ERR_ENUMERATED_INVALID_LENGTH.get(value.length));
            }
        }
        return new ASN1Enumerated(element.getType(), intValue, value);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.intValue);
    }
}
