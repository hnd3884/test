package com.unboundid.asn1;

import java.nio.ByteBuffer;
import java.io.IOException;
import com.unboundid.util.Debug;
import com.unboundid.util.DebugType;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import com.unboundid.util.ByteStringBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ASN1Buffer implements Serializable
{
    private static final int DEFAULT_MAX_BUFFER_SIZE = 1048576;
    private static final byte[] MULTIBYTE_LENGTH_HEADER_PLUS_ONE;
    private static final byte[] MULTIBYTE_LENGTH_HEADER_PLUS_TWO;
    private static final byte[] MULTIBYTE_LENGTH_HEADER_PLUS_THREE;
    private static final byte[] MULTIBYTE_LENGTH_HEADER_PLUS_FOUR;
    private static final long serialVersionUID = -4898230771376551562L;
    private final AtomicBoolean zeroBufferOnClear;
    private final ByteStringBuffer buffer;
    private final int maxBufferSize;
    
    public ASN1Buffer() {
        this(1048576);
    }
    
    public ASN1Buffer(final int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
        this.buffer = new ByteStringBuffer();
        this.zeroBufferOnClear = new AtomicBoolean(false);
    }
    
    public boolean zeroBufferOnClear() {
        return this.zeroBufferOnClear.get();
    }
    
    public void setZeroBufferOnClear() {
        this.zeroBufferOnClear.set(true);
    }
    
    public void clear() {
        this.buffer.clear(this.zeroBufferOnClear.getAndSet(false));
        if (this.maxBufferSize > 0 && this.buffer.capacity() > this.maxBufferSize) {
            this.buffer.setCapacity(this.maxBufferSize);
        }
    }
    
    public int length() {
        return this.buffer.length();
    }
    
    public void addElement(final ASN1Element element) {
        element.encodeTo(this.buffer);
    }
    
    public void addBoolean(final boolean booleanValue) {
        this.addBoolean((byte)1, booleanValue);
    }
    
    public void addBoolean(final byte type, final boolean booleanValue) {
        this.buffer.append(type);
        this.buffer.append((byte)1);
        if (booleanValue) {
            this.buffer.append((byte)(-1));
        }
        else {
            this.buffer.append((byte)0);
        }
    }
    
    public void addEnumerated(final int intValue) {
        this.addInteger((byte)10, intValue);
    }
    
    public void addEnumerated(final byte type, final int intValue) {
        this.addInteger(type, intValue);
    }
    
    public void addGeneralizedTime(final Date date) {
        this.addGeneralizedTime(date.getTime());
    }
    
    public void addGeneralizedTime(final byte type, final Date date) {
        this.addGeneralizedTime(type, date.getTime());
    }
    
    public void addGeneralizedTime(final long time) {
        this.addGeneralizedTime((byte)24, time);
    }
    
    public void addGeneralizedTime(final byte type, final long time) {
        this.buffer.append(type);
        final String timestamp = ASN1GeneralizedTime.encodeTimestamp(time, true);
        ASN1Element.encodeLengthTo(timestamp.length(), this.buffer);
        this.buffer.append((CharSequence)timestamp);
    }
    
    public void addInteger(final int intValue) {
        this.addInteger((byte)2, intValue);
    }
    
    public void addInteger(final byte type, final int intValue) {
        this.buffer.append(type);
        if (intValue < 0) {
            if ((intValue & 0xFFFFFF80) == 0xFFFFFF80) {
                this.buffer.append((byte)1);
                this.buffer.append((byte)(intValue & 0xFF));
            }
            else if ((intValue & 0xFFFF8000) == 0xFFFF8000) {
                this.buffer.append((byte)2);
                this.buffer.append((byte)(intValue >> 8 & 0xFF));
                this.buffer.append((byte)(intValue & 0xFF));
            }
            else if ((intValue & 0xFF800000) == 0xFF800000) {
                this.buffer.append((byte)3);
                this.buffer.append((byte)(intValue >> 16 & 0xFF));
                this.buffer.append((byte)(intValue >> 8 & 0xFF));
                this.buffer.append((byte)(intValue & 0xFF));
            }
            else {
                this.buffer.append((byte)4);
                this.buffer.append((byte)(intValue >> 24 & 0xFF));
                this.buffer.append((byte)(intValue >> 16 & 0xFF));
                this.buffer.append((byte)(intValue >> 8 & 0xFF));
                this.buffer.append((byte)(intValue & 0xFF));
            }
        }
        else if ((intValue & 0x7F) == intValue) {
            this.buffer.append((byte)1);
            this.buffer.append((byte)(intValue & 0x7F));
        }
        else if ((intValue & 0x7FFF) == intValue) {
            this.buffer.append((byte)2);
            this.buffer.append((byte)(intValue >> 8 & 0x7F));
            this.buffer.append((byte)(intValue & 0xFF));
        }
        else if ((intValue & 0x7FFFFF) == intValue) {
            this.buffer.append((byte)3);
            this.buffer.append((byte)(intValue >> 16 & 0x7F));
            this.buffer.append((byte)(intValue >> 8 & 0xFF));
            this.buffer.append((byte)(intValue & 0xFF));
        }
        else {
            this.buffer.append((byte)4);
            this.buffer.append((byte)(intValue >> 24 & 0x7F));
            this.buffer.append((byte)(intValue >> 16 & 0xFF));
            this.buffer.append((byte)(intValue >> 8 & 0xFF));
            this.buffer.append((byte)(intValue & 0xFF));
        }
    }
    
    public void addInteger(final long longValue) {
        this.addInteger((byte)2, longValue);
    }
    
    public void addInteger(final byte type, final long longValue) {
        this.buffer.append(type);
        if (longValue < 0L) {
            if ((longValue & 0xFFFFFFFFFFFFFF80L) == 0xFFFFFFFFFFFFFF80L) {
                this.buffer.append((byte)1);
                this.buffer.append((byte)(longValue & 0xFFL));
            }
            else if ((longValue & 0xFFFFFFFFFFFF8000L) == 0xFFFFFFFFFFFF8000L) {
                this.buffer.append((byte)2);
                this.buffer.append((byte)(longValue >> 8 & 0xFFL));
                this.buffer.append((byte)(longValue & 0xFFL));
            }
            else if ((longValue & 0xFFFFFFFFFF800000L) == 0xFFFFFFFFFF800000L) {
                this.buffer.append((byte)3);
                this.buffer.append((byte)(longValue >> 16 & 0xFFL));
                this.buffer.append((byte)(longValue >> 8 & 0xFFL));
                this.buffer.append((byte)(longValue & 0xFFL));
            }
            else if ((longValue & 0xFFFFFFFF80000000L) == 0xFFFFFFFF80000000L) {
                this.buffer.append((byte)4);
                this.buffer.append((byte)(longValue >> 24 & 0xFFL));
                this.buffer.append((byte)(longValue >> 16 & 0xFFL));
                this.buffer.append((byte)(longValue >> 8 & 0xFFL));
                this.buffer.append((byte)(longValue & 0xFFL));
            }
            else if ((longValue & 0xFFFFFF8000000000L) == 0xFFFFFF8000000000L) {
                this.buffer.append((byte)5);
                this.buffer.append((byte)(longValue >> 32 & 0xFFL));
                this.buffer.append((byte)(longValue >> 24 & 0xFFL));
                this.buffer.append((byte)(longValue >> 16 & 0xFFL));
                this.buffer.append((byte)(longValue >> 8 & 0xFFL));
                this.buffer.append((byte)(longValue & 0xFFL));
            }
            else if ((longValue & 0xFFFF800000000000L) == 0xFFFF800000000000L) {
                this.buffer.append((byte)6);
                this.buffer.append((byte)(longValue >> 40 & 0xFFL));
                this.buffer.append((byte)(longValue >> 32 & 0xFFL));
                this.buffer.append((byte)(longValue >> 24 & 0xFFL));
                this.buffer.append((byte)(longValue >> 16 & 0xFFL));
                this.buffer.append((byte)(longValue >> 8 & 0xFFL));
                this.buffer.append((byte)(longValue & 0xFFL));
            }
            else if ((longValue & 0xFF80000000000000L) == 0xFF80000000000000L) {
                this.buffer.append((byte)7);
                this.buffer.append((byte)(longValue >> 48 & 0xFFL));
                this.buffer.append((byte)(longValue >> 40 & 0xFFL));
                this.buffer.append((byte)(longValue >> 32 & 0xFFL));
                this.buffer.append((byte)(longValue >> 24 & 0xFFL));
                this.buffer.append((byte)(longValue >> 16 & 0xFFL));
                this.buffer.append((byte)(longValue >> 8 & 0xFFL));
                this.buffer.append((byte)(longValue & 0xFFL));
            }
            else {
                this.buffer.append((byte)8);
                this.buffer.append((byte)(longValue >> 56 & 0xFFL));
                this.buffer.append((byte)(longValue >> 48 & 0xFFL));
                this.buffer.append((byte)(longValue >> 40 & 0xFFL));
                this.buffer.append((byte)(longValue >> 32 & 0xFFL));
                this.buffer.append((byte)(longValue >> 24 & 0xFFL));
                this.buffer.append((byte)(longValue >> 16 & 0xFFL));
                this.buffer.append((byte)(longValue >> 8 & 0xFFL));
                this.buffer.append((byte)(longValue & 0xFFL));
            }
        }
        else if ((longValue & 0x7FL) == longValue) {
            this.buffer.append((byte)1);
            this.buffer.append((byte)(longValue & 0x7FL));
        }
        else if ((longValue & 0x7FFFL) == longValue) {
            this.buffer.append((byte)2);
            this.buffer.append((byte)(longValue >> 8 & 0x7FL));
            this.buffer.append((byte)(longValue & 0xFFL));
        }
        else if ((longValue & 0x7FFFFFL) == longValue) {
            this.buffer.append((byte)3);
            this.buffer.append((byte)(longValue >> 16 & 0x7FL));
            this.buffer.append((byte)(longValue >> 8 & 0xFFL));
            this.buffer.append((byte)(longValue & 0xFFL));
        }
        else if ((longValue & 0x7FFFFFFFL) == longValue) {
            this.buffer.append((byte)4);
            this.buffer.append((byte)(longValue >> 24 & 0x7FL));
            this.buffer.append((byte)(longValue >> 16 & 0xFFL));
            this.buffer.append((byte)(longValue >> 8 & 0xFFL));
            this.buffer.append((byte)(longValue & 0xFFL));
        }
        else if ((longValue & 0x7FFFFFFFFFL) == longValue) {
            this.buffer.append((byte)5);
            this.buffer.append((byte)(longValue >> 32 & 0x7FL));
            this.buffer.append((byte)(longValue >> 24 & 0xFFL));
            this.buffer.append((byte)(longValue >> 16 & 0xFFL));
            this.buffer.append((byte)(longValue >> 8 & 0xFFL));
            this.buffer.append((byte)(longValue & 0xFFL));
        }
        else if ((longValue & 0x7FFFFFFFFFFFL) == longValue) {
            this.buffer.append((byte)6);
            this.buffer.append((byte)(longValue >> 40 & 0x7FL));
            this.buffer.append((byte)(longValue >> 32 & 0xFFL));
            this.buffer.append((byte)(longValue >> 24 & 0xFFL));
            this.buffer.append((byte)(longValue >> 16 & 0xFFL));
            this.buffer.append((byte)(longValue >> 8 & 0xFFL));
            this.buffer.append((byte)(longValue & 0xFFL));
        }
        else if ((longValue & 0x7FFFFFFFFFFFFFL) == longValue) {
            this.buffer.append((byte)7);
            this.buffer.append((byte)(longValue >> 48 & 0x7FL));
            this.buffer.append((byte)(longValue >> 40 & 0xFFL));
            this.buffer.append((byte)(longValue >> 32 & 0xFFL));
            this.buffer.append((byte)(longValue >> 24 & 0xFFL));
            this.buffer.append((byte)(longValue >> 16 & 0xFFL));
            this.buffer.append((byte)(longValue >> 8 & 0xFFL));
            this.buffer.append((byte)(longValue & 0xFFL));
        }
        else {
            this.buffer.append((byte)8);
            this.buffer.append((byte)(longValue >> 56 & 0x7FL));
            this.buffer.append((byte)(longValue >> 48 & 0xFFL));
            this.buffer.append((byte)(longValue >> 40 & 0xFFL));
            this.buffer.append((byte)(longValue >> 32 & 0xFFL));
            this.buffer.append((byte)(longValue >> 24 & 0xFFL));
            this.buffer.append((byte)(longValue >> 16 & 0xFFL));
            this.buffer.append((byte)(longValue >> 8 & 0xFFL));
            this.buffer.append((byte)(longValue & 0xFFL));
        }
    }
    
    public void addInteger(final BigInteger value) {
        this.addInteger((byte)2, value);
    }
    
    public void addInteger(final byte type, final BigInteger value) {
        this.buffer.append(type);
        final byte[] valueBytes = value.toByteArray();
        ASN1Element.encodeLengthTo(valueBytes.length, this.buffer);
        this.buffer.append(valueBytes);
    }
    
    public void addNull() {
        this.addNull((byte)5);
    }
    
    public void addNull(final byte type) {
        this.buffer.append(type);
        this.buffer.append((byte)0);
    }
    
    public void addOctetString() {
        this.addOctetString((byte)4);
    }
    
    public void addOctetString(final byte type) {
        this.buffer.append(type);
        this.buffer.append((byte)0);
    }
    
    public void addOctetString(final byte[] value) {
        this.addOctetString((byte)4, value);
    }
    
    public void addOctetString(final CharSequence value) {
        if (value == null) {
            this.addOctetString((byte)4);
        }
        else {
            this.addOctetString((byte)4, value.toString());
        }
    }
    
    public void addOctetString(final String value) {
        this.addOctetString((byte)4, value);
    }
    
    public void addOctetString(final byte type, final byte[] value) {
        this.buffer.append(type);
        if (value == null) {
            this.buffer.append((byte)0);
        }
        else {
            ASN1Element.encodeLengthTo(value.length, this.buffer);
            this.buffer.append(value);
        }
    }
    
    public void addOctetString(final byte type, final CharSequence value) {
        if (value == null) {
            this.addOctetString(type);
        }
        else {
            this.addOctetString(type, value.toString());
        }
    }
    
    public void addOctetString(final byte type, final String value) {
        this.buffer.append(type);
        if (value == null) {
            this.buffer.append((byte)0);
        }
        else {
            final int lengthStartPos = this.buffer.length();
            ASN1Element.encodeLengthTo(value.length(), this.buffer);
            final int valueStartPos = this.buffer.length();
            this.buffer.append((CharSequence)value);
            if (this.buffer.length() != valueStartPos + value.length()) {
                final byte[] valueBytes = new byte[this.buffer.length() - valueStartPos];
                System.arraycopy(this.buffer.getBackingArray(), valueStartPos, valueBytes, 0, valueBytes.length);
                this.buffer.setLength(lengthStartPos);
                ASN1Element.encodeLengthTo(valueBytes.length, this.buffer);
                this.buffer.append(valueBytes);
            }
        }
    }
    
    public void addUTCTime(final Date date) {
        this.addUTCTime(date.getTime());
    }
    
    public void addUTCTime(final byte type, final Date date) {
        this.addUTCTime(type, date.getTime());
    }
    
    public void addUTCTime(final long time) {
        this.addUTCTime((byte)23, time);
    }
    
    public void addUTCTime(final byte type, final long time) {
        this.buffer.append(type);
        final String timestamp = ASN1UTCTime.encodeTimestamp(time);
        ASN1Element.encodeLengthTo(timestamp.length(), this.buffer);
        this.buffer.append((CharSequence)timestamp);
    }
    
    public ASN1BufferSequence beginSequence() {
        return this.beginSequence((byte)48);
    }
    
    public ASN1BufferSequence beginSequence(final byte type) {
        this.buffer.append(type);
        return new ASN1BufferSequence(this);
    }
    
    public ASN1BufferSet beginSet() {
        return this.beginSet((byte)49);
    }
    
    public ASN1BufferSet beginSet(final byte type) {
        this.buffer.append(type);
        return new ASN1BufferSet(this);
    }
    
    void endSequenceOrSet(final int valueStartPos) {
        final int length = this.buffer.length() - valueStartPos;
        if (length == 0) {
            this.buffer.append((byte)0);
            return;
        }
        if ((length & 0x7F) == length) {
            this.buffer.insert(valueStartPos, (byte)length);
        }
        else if ((length & 0xFF) == length) {
            this.buffer.insert(valueStartPos, ASN1Buffer.MULTIBYTE_LENGTH_HEADER_PLUS_ONE);
            final byte[] backingArray = this.buffer.getBackingArray();
            backingArray[valueStartPos + 1] = (byte)(length & 0xFF);
        }
        else if ((length & 0xFFFF) == length) {
            this.buffer.insert(valueStartPos, ASN1Buffer.MULTIBYTE_LENGTH_HEADER_PLUS_TWO);
            final byte[] backingArray = this.buffer.getBackingArray();
            backingArray[valueStartPos + 1] = (byte)(length >> 8 & 0xFF);
            backingArray[valueStartPos + 2] = (byte)(length & 0xFF);
        }
        else if ((length & 0xFFFFFF) == length) {
            this.buffer.insert(valueStartPos, ASN1Buffer.MULTIBYTE_LENGTH_HEADER_PLUS_THREE);
            final byte[] backingArray = this.buffer.getBackingArray();
            backingArray[valueStartPos + 1] = (byte)(length >> 16 & 0xFF);
            backingArray[valueStartPos + 2] = (byte)(length >> 8 & 0xFF);
            backingArray[valueStartPos + 3] = (byte)(length & 0xFF);
        }
        else {
            this.buffer.insert(valueStartPos, ASN1Buffer.MULTIBYTE_LENGTH_HEADER_PLUS_FOUR);
            final byte[] backingArray = this.buffer.getBackingArray();
            backingArray[valueStartPos + 1] = (byte)(length >> 24 & 0xFF);
            backingArray[valueStartPos + 2] = (byte)(length >> 16 & 0xFF);
            backingArray[valueStartPos + 3] = (byte)(length >> 8 & 0xFF);
            backingArray[valueStartPos + 4] = (byte)(length & 0xFF);
        }
    }
    
    public void writeTo(final OutputStream outputStream) throws IOException {
        if (Debug.debugEnabled(DebugType.ASN1)) {
            Debug.debugASN1Write(this);
        }
        this.buffer.write(outputStream);
    }
    
    public byte[] toByteArray() {
        return this.buffer.toByteArray();
    }
    
    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(this.buffer.getBackingArray(), 0, this.buffer.length());
    }
    
    static {
        MULTIBYTE_LENGTH_HEADER_PLUS_ONE = new byte[] { -127, 0 };
        MULTIBYTE_LENGTH_HEADER_PLUS_TWO = new byte[] { -126, 0, 0 };
        MULTIBYTE_LENGTH_HEADER_PLUS_THREE = new byte[] { -125, 0, 0, 0 };
        MULTIBYTE_LENGTH_HEADER_PLUS_FOUR = new byte[] { -124, 0, 0, 0, 0 };
    }
}
