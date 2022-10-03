package com.unboundid.asn1;

import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.util.ByteStringBuffer;
import java.util.Collection;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Sequence extends ASN1Element
{
    private static final long serialVersionUID = 7294248008273774906L;
    private final ASN1Element[] elements;
    private byte[] encodedValue;
    private volatile byte[] encodedValueGuard;
    
    public ASN1Sequence() {
        super((byte)48);
        this.elements = ASN1Constants.NO_ELEMENTS;
        this.encodedValue = ASN1Constants.NO_VALUE;
    }
    
    public ASN1Sequence(final byte type) {
        super(type);
        this.elements = ASN1Constants.NO_ELEMENTS;
        this.encodedValue = ASN1Constants.NO_VALUE;
    }
    
    public ASN1Sequence(final ASN1Element... elements) {
        super((byte)48);
        if (elements == null) {
            this.elements = ASN1Constants.NO_ELEMENTS;
        }
        else {
            this.elements = elements;
        }
        this.encodedValue = null;
    }
    
    public ASN1Sequence(final Collection<? extends ASN1Element> elements) {
        super((byte)48);
        if (elements == null || elements.isEmpty()) {
            this.elements = ASN1Constants.NO_ELEMENTS;
        }
        else {
            elements.toArray(this.elements = new ASN1Element[elements.size()]);
        }
        this.encodedValue = null;
    }
    
    public ASN1Sequence(final byte type, final ASN1Element... elements) {
        super(type);
        if (elements == null) {
            this.elements = ASN1Constants.NO_ELEMENTS;
        }
        else {
            this.elements = elements;
        }
        this.encodedValue = null;
    }
    
    public ASN1Sequence(final byte type, final Collection<? extends ASN1Element> elements) {
        super(type);
        if (elements == null || elements.isEmpty()) {
            this.elements = ASN1Constants.NO_ELEMENTS;
        }
        else {
            elements.toArray(this.elements = new ASN1Element[elements.size()]);
        }
        this.encodedValue = null;
    }
    
    private ASN1Sequence(final byte type, final ASN1Element[] elements, final byte[] value) {
        super(type);
        this.elements = elements;
        this.encodedValue = value;
    }
    
    @Override
    byte[] getValueArray() {
        return this.getValue();
    }
    
    @Override
    int getValueOffset() {
        return 0;
    }
    
    @Override
    public int getValueLength() {
        return this.getValue().length;
    }
    
    @Override
    public byte[] getValue() {
        if (this.encodedValue == null) {
            this.encodedValueGuard = encodeElements(this.elements);
            this.encodedValue = this.encodedValueGuard;
        }
        return this.encodedValue;
    }
    
    @Override
    public void encodeTo(final ByteStringBuffer buffer) {
        buffer.append(this.getType());
        if (this.elements.length == 0) {
            buffer.append((byte)0);
            return;
        }
        final int originalLength = buffer.length();
        for (final ASN1Element e : this.elements) {
            e.encodeTo(buffer);
        }
        buffer.insert(originalLength, ASN1Element.encodeLength(buffer.length() - originalLength));
    }
    
    static byte[] encodeElements(final ASN1Element[] elements) {
        if (elements == null || elements.length == 0) {
            return ASN1Constants.NO_VALUE;
        }
        int totalLength = 0;
        final int numElements = elements.length;
        final byte[][] encodedElements = new byte[numElements][];
        for (int i = 0; i < numElements; ++i) {
            encodedElements[i] = elements[i].encode();
            totalLength += encodedElements[i].length;
        }
        int pos = 0;
        final byte[] b = new byte[totalLength];
        for (int j = 0; j < numElements; ++j) {
            System.arraycopy(encodedElements[j], 0, b, pos, encodedElements[j].length);
            pos += encodedElements[j].length;
        }
        return b;
    }
    
    public ASN1Element[] elements() {
        return this.elements;
    }
    
    public static ASN1Sequence decodeAsSequence(final byte[] elementBytes) throws ASN1Exception {
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
            int numElements = 0;
            final ArrayList<ASN1Element> elementList = new ArrayList<ASN1Element>(5);
            try {
                int l;
                for (int pos = 0; pos < value.length; pos += l, ++numElements) {
                    final byte type = value[pos++];
                    final byte firstLengthByte = value[pos++];
                    l = (firstLengthByte & 0x7F);
                    if (l != firstLengthByte) {
                        final int numLengthBytes2 = l;
                        l = 0;
                        for (int j = 0; j < numLengthBytes2; ++j) {
                            l <<= 8;
                            l |= (value[pos++] & 0xFF);
                        }
                    }
                    final int posPlusLength = pos + l;
                    if (l < 0 || posPlusLength < 0 || posPlusLength > value.length) {
                        throw new ASN1Exception(ASN1Messages.ERR_SEQUENCE_BYTES_DECODE_LENGTH_EXCEEDS_AVAILABLE.get());
                    }
                    elementList.add(new ASN1Element(type, value, pos, l));
                }
            }
            catch (final ASN1Exception ae) {
                throw ae;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new ASN1Exception(ASN1Messages.ERR_SEQUENCE_BYTES_DECODE_EXCEPTION.get(e), e);
            }
            int k = 0;
            final ASN1Element[] elements = new ASN1Element[numElements];
            for (final ASN1Element e2 : elementList) {
                elements[k++] = e2;
            }
            return new ASN1Sequence(elementBytes[0], elements, value);
        }
        catch (final ASN1Exception ae2) {
            Debug.debugException(ae2);
            throw ae2;
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new ASN1Exception(ASN1Messages.ERR_ELEMENT_DECODE_EXCEPTION.get(e3), e3);
        }
    }
    
    public static ASN1Sequence decodeAsSequence(final ASN1Element element) throws ASN1Exception {
        int numElements = 0;
        final ArrayList<ASN1Element> elementList = new ArrayList<ASN1Element>(5);
        final byte[] value = element.getValue();
        try {
            int length;
            for (int pos = 0; pos < value.length; pos += length, ++numElements) {
                final byte type = value[pos++];
                final byte firstLengthByte = value[pos++];
                length = (firstLengthByte & 0x7F);
                if (length != firstLengthByte) {
                    final int numLengthBytes = length;
                    length = 0;
                    for (int i = 0; i < numLengthBytes; ++i) {
                        length <<= 8;
                        length |= (value[pos++] & 0xFF);
                    }
                }
                final int posPlusLength = pos + length;
                if (length < 0 || posPlusLength < 0 || posPlusLength > value.length) {
                    throw new ASN1Exception(ASN1Messages.ERR_SEQUENCE_DECODE_LENGTH_EXCEEDS_AVAILABLE.get(String.valueOf(element)));
                }
                elementList.add(new ASN1Element(type, value, pos, length));
            }
        }
        catch (final ASN1Exception ae) {
            throw ae;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ASN1Exception(ASN1Messages.ERR_SEQUENCE_DECODE_EXCEPTION.get(String.valueOf(element), e), e);
        }
        int j = 0;
        final ASN1Element[] elements = new ASN1Element[numElements];
        for (final ASN1Element e2 : elementList) {
            elements[j++] = e2;
        }
        return new ASN1Sequence(element.getType(), elements, value);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append('[');
        for (int i = 0; i < this.elements.length; ++i) {
            if (i > 0) {
                buffer.append(',');
            }
            this.elements[i].toString(buffer);
        }
        buffer.append(']');
    }
}
