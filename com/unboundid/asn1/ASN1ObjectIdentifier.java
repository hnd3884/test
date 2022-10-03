package com.unboundid.asn1;

import java.util.ArrayList;
import com.unboundid.util.Debug;
import java.util.Iterator;
import java.util.List;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1ObjectIdentifier extends ASN1Element
{
    private static final long serialVersionUID = -777778295086222273L;
    private final OID oid;
    
    public ASN1ObjectIdentifier(final OID oid) throws ASN1Exception {
        this((byte)6, oid);
    }
    
    public ASN1ObjectIdentifier(final byte type, final OID oid) throws ASN1Exception {
        this(type, oid, encodeValue(oid));
    }
    
    public ASN1ObjectIdentifier(final String oidString) throws ASN1Exception {
        this((byte)6, oidString);
    }
    
    public ASN1ObjectIdentifier(final byte type, final String oidString) throws ASN1Exception {
        this(type, new OID(oidString));
    }
    
    private ASN1ObjectIdentifier(final byte type, final OID oid, final byte[] encodedValue) {
        super(type, encodedValue);
        this.oid = oid;
    }
    
    private static byte[] encodeValue(final OID oid) throws ASN1Exception {
        if (!oid.isValidNumericOID()) {
            throw new ASN1Exception(ASN1Messages.ERR_OID_ENCODE_NOT_NUMERIC.get());
        }
        final List<Integer> components = oid.getComponents();
        if (components.size() < 2) {
            throw new ASN1Exception(ASN1Messages.ERR_OID_ENCODE_NOT_ENOUGH_COMPONENTS.get(oid.toString()));
        }
        final Iterator<Integer> componentIterator = components.iterator();
        final int firstComponent = componentIterator.next();
        if (firstComponent < 0 || firstComponent > 2) {
            throw new ASN1Exception(ASN1Messages.ERR_OID_ENCODE_INVALID_FIRST_COMPONENT.get(oid.toString(), firstComponent));
        }
        final int secondComponent = componentIterator.next();
        if (secondComponent < 0 || (firstComponent != 2 && secondComponent > 39)) {
            throw new ASN1Exception(ASN1Messages.ERR_OID_ENCODE_INVALID_SECOND_COMPONENT.get(oid.toString(), firstComponent, secondComponent));
        }
        final ByteStringBuffer buffer = new ByteStringBuffer();
        final int mergedFirstComponents = 40 * firstComponent + secondComponent;
        encodeComponent(mergedFirstComponents, buffer);
        while (componentIterator.hasNext()) {
            encodeComponent(componentIterator.next(), buffer);
        }
        return buffer.toByteArray();
    }
    
    private static void encodeComponent(final int c, final ByteStringBuffer b) {
        final int finalByte = c & 0x7F;
        if (finalByte == c) {
            b.append((byte)finalByte);
        }
        else if ((c & 0x3FFF) == c) {
            b.append((byte)(0x80 | (c >> 7 & 0x7F)));
            b.append((byte)finalByte);
        }
        else if ((c & 0x1FFFFF) == c) {
            b.append((byte)(0x80 | (c >> 14 & 0x7F)));
            b.append((byte)(0x80 | (c >> 7 & 0x7F)));
            b.append((byte)finalByte);
        }
        else if ((c & 0xFFFFFFF) == c) {
            b.append((byte)(0x80 | (c >> 21 & 0x7F)));
            b.append((byte)(0x80 | (c >> 14 & 0x7F)));
            b.append((byte)(0x80 | (c >> 7 & 0x7F)));
            b.append((byte)finalByte);
        }
        else {
            b.append((byte)(0x80 | (c >> 28 & 0x7F)));
            b.append((byte)(0x80 | (c >> 21 & 0x7F)));
            b.append((byte)(0x80 | (c >> 14 & 0x7F)));
            b.append((byte)(0x80 | (c >> 7 & 0x7F)));
            b.append((byte)finalByte);
        }
    }
    
    public OID getOID() {
        return this.oid;
    }
    
    public static ASN1ObjectIdentifier decodeAsObjectIdentifier(final byte[] elementBytes) throws ASN1Exception {
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
            final OID oid = decodeValue(elementValue);
            return new ASN1ObjectIdentifier(elementBytes[0], oid, elementValue);
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
    
    public static ASN1ObjectIdentifier decodeAsObjectIdentifier(final ASN1Element element) throws ASN1Exception {
        final OID oid = decodeValue(element.getValue());
        return new ASN1ObjectIdentifier(element.getType(), oid, element.getValue());
    }
    
    private static OID decodeValue(final byte[] elementValue) throws ASN1Exception {
        if (elementValue.length == 0) {
            throw new ASN1Exception(ASN1Messages.ERR_OID_DECODE_EMPTY_VALUE.get());
        }
        final byte lastByte = elementValue[elementValue.length - 1];
        if ((lastByte & 0x80) == 0x80) {
            throw new ASN1Exception(ASN1Messages.ERR_OID_DECODE_INCOMPLETE_VALUE.get());
        }
        int currentComponent = 0;
        final ArrayList<Integer> components = new ArrayList<Integer>(elementValue.length);
        for (final byte b : elementValue) {
            currentComponent <<= 7;
            currentComponent |= (b & 0x7F);
            if ((b & 0x80) == 0x0) {
                if (components.isEmpty()) {
                    if (currentComponent < 40) {
                        components.add(0);
                        components.add(currentComponent);
                    }
                    else if (currentComponent < 80) {
                        components.add(1);
                        components.add(currentComponent - 40);
                    }
                    else {
                        components.add(2);
                        components.add(currentComponent - 80);
                    }
                }
                else {
                    components.add(currentComponent);
                }
                currentComponent = 0;
            }
        }
        return new OID(components);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.oid.toString());
    }
}
