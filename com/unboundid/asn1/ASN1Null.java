package com.unboundid.asn1;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Null extends ASN1Element
{
    public static final ASN1Null UNIVERSAL_NULL_ELEMENT;
    private static final long serialVersionUID = -3264450066845549348L;
    
    public ASN1Null() {
        super((byte)5);
    }
    
    public ASN1Null(final byte type) {
        super(type);
    }
    
    public static ASN1Null decodeAsNull(final byte[] elementBytes) throws ASN1Exception {
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
            if (length != 0) {
                throw new ASN1Exception(ASN1Messages.ERR_NULL_HAS_VALUE.get());
            }
            return new ASN1Null(elementBytes[0]);
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
    
    public static ASN1Null decodeAsNull(final ASN1Element element) throws ASN1Exception {
        if (element.getValue().length != 0) {
            throw new ASN1Exception(ASN1Messages.ERR_NULL_HAS_VALUE.get());
        }
        return new ASN1Null(element.getType());
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ASN1Null(type=");
        StaticUtils.toHex(this.getType(), buffer);
        buffer.append(')');
    }
    
    static {
        UNIVERSAL_NULL_ELEMENT = new ASN1Null();
    }
}
