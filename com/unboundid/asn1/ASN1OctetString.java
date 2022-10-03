package com.unboundid.asn1;

import com.unboundid.util.Debug;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.Validator;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.ByteString;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1OctetString extends ASN1Element implements ByteString
{
    private static final long serialVersionUID = -7857753188341295516L;
    private byte[] valueBytes;
    private volatile byte[] valueBytesGuard;
    private int length;
    private int offset;
    private String stringValue;
    
    public ASN1OctetString() {
        super((byte)4);
        this.valueBytes = StaticUtils.NO_BYTES;
        this.stringValue = "";
        this.offset = 0;
        this.length = 0;
    }
    
    public ASN1OctetString(final byte type) {
        super(type);
        this.valueBytes = StaticUtils.NO_BYTES;
        this.stringValue = "";
        this.offset = 0;
        this.length = 0;
    }
    
    public ASN1OctetString(final byte[] value) {
        super((byte)4);
        if (value == null) {
            this.valueBytes = StaticUtils.NO_BYTES;
            this.stringValue = "";
            this.offset = 0;
            this.length = 0;
        }
        else {
            this.valueBytes = value;
            this.stringValue = null;
            this.offset = 0;
            this.length = value.length;
        }
    }
    
    public ASN1OctetString(final byte[] value, final int offset, final int length) {
        super((byte)4);
        Validator.ensureNotNull(value);
        Validator.ensureTrue(offset >= 0 && length >= 0 && offset + length <= value.length);
        this.valueBytes = value;
        this.stringValue = null;
        this.offset = offset;
        this.length = length;
    }
    
    public ASN1OctetString(final byte type, final byte[] value) {
        super(type);
        if (value == null) {
            this.valueBytes = StaticUtils.NO_BYTES;
            this.stringValue = "";
            this.offset = 0;
            this.length = 0;
        }
        else {
            this.valueBytes = value;
            this.stringValue = null;
            this.offset = 0;
            this.length = value.length;
        }
    }
    
    public ASN1OctetString(final byte type, final byte[] value, final int offset, final int length) {
        super(type);
        Validator.ensureTrue(offset >= 0 && length >= 0 && offset + length <= value.length);
        this.valueBytes = value;
        this.stringValue = null;
        this.offset = offset;
        this.length = length;
    }
    
    public ASN1OctetString(final String value) {
        super((byte)4);
        if (value == null) {
            this.valueBytes = StaticUtils.NO_BYTES;
            this.stringValue = "";
            this.offset = 0;
            this.length = 0;
        }
        else {
            this.valueBytes = null;
            this.stringValue = value;
            this.offset = -1;
            this.length = -1;
        }
    }
    
    public ASN1OctetString(final byte type, final String value) {
        super(type);
        if (value == null) {
            this.valueBytes = StaticUtils.NO_BYTES;
            this.stringValue = "";
            this.offset = 0;
            this.length = 0;
        }
        else {
            this.valueBytes = null;
            this.stringValue = value;
            this.offset = -1;
            this.length = -1;
        }
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
        if (this.valueBytes == null) {
            this.valueBytesGuard = StaticUtils.getBytes(this.stringValue);
            this.offset = 0;
            this.length = this.valueBytesGuard.length;
            this.valueBytes = this.valueBytesGuard;
        }
        else if (this.offset != 0 || this.length != this.valueBytes.length) {
            final byte[] newArray = new byte[this.length];
            System.arraycopy(this.valueBytes, this.offset, newArray, 0, this.length);
            this.offset = 0;
            this.valueBytesGuard = newArray;
            this.valueBytes = this.valueBytesGuard;
        }
        return this.valueBytes;
    }
    
    @Override
    public void encodeTo(final ByteStringBuffer buffer) {
        buffer.append(this.getType());
        if (this.valueBytes == null) {
            final int stringLength = this.stringValue.length();
            final int lengthStartPos = buffer.length();
            ASN1Element.encodeLengthTo(stringLength, buffer);
            final int valueStartPos = buffer.length();
            buffer.append((CharSequence)this.stringValue);
            final int stringBytesLength = buffer.length() - valueStartPos;
            if (stringBytesLength != stringLength) {
                final byte[] newLengthBytes = ASN1Element.encodeLength(stringBytesLength);
                if (newLengthBytes.length == valueStartPos - lengthStartPos) {
                    System.arraycopy(newLengthBytes, 0, buffer.getBackingArray(), lengthStartPos, newLengthBytes.length);
                }
                else {
                    buffer.setLength(lengthStartPos);
                    buffer.append(newLengthBytes);
                    buffer.append((CharSequence)this.stringValue);
                }
            }
        }
        else {
            ASN1Element.encodeLengthTo(this.length, buffer);
            buffer.append(this.valueBytes, this.offset, this.length);
        }
    }
    
    @Override
    public String stringValue() {
        if (this.stringValue == null) {
            if (this.length == 0) {
                this.stringValue = "";
            }
            else {
                this.stringValue = StaticUtils.toUTF8String(this.valueBytes, this.offset, this.length);
            }
        }
        return this.stringValue;
    }
    
    public static ASN1OctetString decodeAsOctetString(final byte[] elementBytes) throws ASN1Exception {
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
            return new ASN1OctetString(elementBytes[0], elementBytes, valueStartPos, length);
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
    
    public static ASN1OctetString decodeAsOctetString(final ASN1Element element) {
        return new ASN1OctetString(element.getType(), element.getValue());
    }
    
    @Override
    public void appendValueTo(final ByteStringBuffer buffer) {
        if (this.valueBytes == null) {
            buffer.append((CharSequence)this.stringValue);
        }
        else {
            buffer.append(this.valueBytes, this.offset, this.length);
        }
    }
    
    @Override
    public ASN1OctetString toASN1OctetString() {
        return this;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.stringValue());
    }
}
