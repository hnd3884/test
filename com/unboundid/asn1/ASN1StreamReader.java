package com.unboundid.asn1;

import java.net.SocketTimeoutException;
import java.math.BigInteger;
import com.unboundid.util.StaticUtils;
import java.util.Date;
import java.util.logging.Level;
import com.unboundid.util.Debug;
import java.io.IOException;
import java.io.BufferedInputStream;
import javax.security.sasl.SaslClient;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Closeable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ASN1StreamReader implements Closeable
{
    private boolean ignoreInitialSocketTimeout;
    private boolean ignoreSubsequentSocketTimeout;
    private volatile ByteArrayInputStream saslInputStream;
    private final InputStream inputStream;
    private final int maxElementSize;
    private long totalBytesRead;
    private volatile SaslClient saslClient;
    
    public ASN1StreamReader(final InputStream inputStream) {
        this(inputStream, Integer.MAX_VALUE);
    }
    
    public ASN1StreamReader(final InputStream inputStream, final int maxElementSize) {
        if (inputStream.markSupported()) {
            this.inputStream = inputStream;
        }
        else {
            this.inputStream = new BufferedInputStream(inputStream);
        }
        if (maxElementSize > 0) {
            this.maxElementSize = maxElementSize;
        }
        else {
            this.maxElementSize = Integer.MAX_VALUE;
        }
        this.totalBytesRead = 0L;
        this.ignoreInitialSocketTimeout = false;
        this.ignoreSubsequentSocketTimeout = false;
        this.saslClient = null;
        this.saslInputStream = null;
    }
    
    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
    
    long getTotalBytesRead() {
        return this.totalBytesRead;
    }
    
    @Deprecated
    public boolean ignoreSocketTimeoutException() {
        return this.ignoreInitialSocketTimeout;
    }
    
    public boolean ignoreInitialSocketTimeoutException() {
        return this.ignoreInitialSocketTimeout;
    }
    
    public boolean ignoreSubsequentSocketTimeoutException() {
        return this.ignoreSubsequentSocketTimeout;
    }
    
    @Deprecated
    public void setIgnoreSocketTimeout(final boolean ignoreSocketTimeout) {
        this.ignoreInitialSocketTimeout = ignoreSocketTimeout;
        this.ignoreSubsequentSocketTimeout = ignoreSocketTimeout;
    }
    
    public void setIgnoreSocketTimeout(final boolean ignoreInitialSocketTimeout, final boolean ignoreSubsequentSocketTimeout) {
        this.ignoreInitialSocketTimeout = ignoreInitialSocketTimeout;
        this.ignoreSubsequentSocketTimeout = ignoreSubsequentSocketTimeout;
    }
    
    public int peek() throws IOException {
        InputStream is;
        if (this.saslClient == null) {
            is = this.inputStream;
        }
        else {
            if (this.saslInputStream == null || this.saslInputStream.available() <= 0) {
                this.readAndDecodeSASLData(-1);
            }
            is = this.saslInputStream;
        }
        is.mark(1);
        final int byteRead = this.read(true);
        is.reset();
        return byteRead;
    }
    
    private int readType() throws IOException {
        final int typeInt = this.read(true);
        if (typeInt < 0) {
            this.close();
        }
        else {
            ++this.totalBytesRead;
        }
        return typeInt;
    }
    
    private int readLength() throws IOException {
        int length = this.read(false);
        if (length < 0) {
            throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_FIRST_LENGTH.get());
        }
        ++this.totalBytesRead;
        if (length > 127) {
            final int numLengthBytes = length & 0x7F;
            length = 0;
            if (numLengthBytes < 1 || numLengthBytes > 4) {
                throw new IOException(ASN1Messages.ERR_READ_LENGTH_TOO_LONG.get(numLengthBytes));
            }
            for (int i = 0; i < numLengthBytes; ++i) {
                final int lengthInt = this.read(false);
                if (lengthInt < 0) {
                    throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_LENGTH_END.get());
                }
                length <<= 8;
                length |= (lengthInt & 0xFF);
            }
            this.totalBytesRead += numLengthBytes;
        }
        if (length < 0 || (this.maxElementSize > 0 && length > this.maxElementSize)) {
            throw new IOException(ASN1Messages.ERR_READ_LENGTH_EXCEEDS_MAX.get(length, this.maxElementSize));
        }
        return length;
    }
    
    private void skip(final int numBytes) throws IOException {
        if (numBytes <= 0) {
            return;
        }
        if (this.saslClient == null) {
            long bytesSkipped;
            for (long totalBytesSkipped = this.inputStream.skip(numBytes); totalBytesSkipped < numBytes; totalBytesSkipped += bytesSkipped) {
                bytesSkipped = this.inputStream.skip(numBytes - totalBytesSkipped);
                if (bytesSkipped <= 0L) {
                    while (totalBytesSkipped < numBytes) {
                        final int byteRead = this.read(false);
                        if (byteRead < 0) {
                            throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
                        }
                        ++totalBytesSkipped;
                    }
                }
                else {}
            }
            this.totalBytesRead += numBytes;
            return;
        }
        int skippedSoFar = 0;
        final byte[] skipBuffer = new byte[numBytes];
        while (true) {
            final int bytesRead = this.read(skipBuffer, skippedSoFar, numBytes - skippedSoFar);
            if (bytesRead < 0) {
                return;
            }
            skippedSoFar += bytesRead;
            this.totalBytesRead += bytesRead;
            if (skippedSoFar >= numBytes) {
                return;
            }
        }
    }
    
    public ASN1Element readElement() throws IOException {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        int valueBytesRead = 0;
        int bytesRemaining = length;
        final byte[] value = new byte[length];
        while (valueBytesRead < length) {
            final int bytesRead = this.read(value, valueBytesRead, bytesRemaining);
            if (bytesRead < 0) {
                throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
            }
            valueBytesRead += bytesRead;
            bytesRemaining -= bytesRead;
        }
        this.totalBytesRead += length;
        final ASN1Element e = new ASN1Element((byte)type, value);
        Debug.debugASN1Read(e);
        return e;
    }
    
    public Boolean readBoolean() throws IOException, ASN1Exception {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        if (length != 1) {
            this.skip(length);
            throw new ASN1Exception(ASN1Messages.ERR_BOOLEAN_INVALID_LENGTH.get());
        }
        final int value = this.read(false);
        if (value < 0) {
            throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
        }
        ++this.totalBytesRead;
        final Boolean booleanValue = value != 0;
        Debug.debugASN1Read(Level.INFO, "Boolean", type, 1, booleanValue);
        return booleanValue;
    }
    
    public Integer readEnumerated() throws IOException, ASN1Exception {
        return this.readInteger();
    }
    
    public Date readGeneralizedTime() throws IOException, ASN1Exception {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        int valueBytesRead = 0;
        int bytesRemaining = length;
        final byte[] value = new byte[length];
        while (valueBytesRead < length) {
            final int bytesRead = this.read(value, valueBytesRead, bytesRemaining);
            if (bytesRead < 0) {
                throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
            }
            valueBytesRead += bytesRead;
            bytesRemaining -= bytesRead;
        }
        this.totalBytesRead += length;
        final String timestamp = StaticUtils.toUTF8String(value);
        final Date date = new Date(ASN1GeneralizedTime.decodeTimestamp(timestamp));
        Debug.debugASN1Read(Level.INFO, "GeneralizedTime", type, length, timestamp);
        return date;
    }
    
    public Integer readInteger() throws IOException, ASN1Exception {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        if (length == 0 || length > 4) {
            this.skip(length);
            throw new ASN1Exception(ASN1Messages.ERR_INTEGER_INVALID_LENGTH.get(length));
        }
        boolean negative = false;
        int intValue = 0;
        for (int i = 0; i < length; ++i) {
            final int byteRead = this.read(false);
            if (byteRead < 0) {
                throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
            }
            if (i == 0) {
                negative = ((byteRead & 0x80) != 0x0);
            }
            intValue <<= 8;
            intValue |= (byteRead & 0xFF);
        }
        if (negative) {
            switch (length) {
                case 1: {
                    intValue |= 0xFFFFFF00;
                    break;
                }
                case 2: {
                    intValue |= 0xFFFF0000;
                    break;
                }
                case 3: {
                    intValue |= 0xFF000000;
                    break;
                }
            }
        }
        this.totalBytesRead += length;
        Debug.debugASN1Read(Level.INFO, "Integer", type, length, intValue);
        return intValue;
    }
    
    public Long readLong() throws IOException, ASN1Exception {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        if (length == 0 || length > 8) {
            this.skip(length);
            throw new ASN1Exception(ASN1Messages.ERR_LONG_INVALID_LENGTH.get(length));
        }
        boolean negative = false;
        long longValue = 0L;
        for (int i = 0; i < length; ++i) {
            final int byteRead = this.read(false);
            if (byteRead < 0) {
                throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
            }
            if (i == 0) {
                negative = ((byteRead & 0x80) != 0x0);
            }
            longValue <<= 8;
            longValue |= ((long)byteRead & 0xFFL);
        }
        if (negative) {
            switch (length) {
                case 1: {
                    longValue |= 0xFFFFFFFFFFFFFF00L;
                    break;
                }
                case 2: {
                    longValue |= 0xFFFFFFFFFFFF0000L;
                    break;
                }
                case 3: {
                    longValue |= 0xFFFFFFFFFF000000L;
                    break;
                }
                case 4: {
                    longValue |= 0xFFFFFFFF00000000L;
                    break;
                }
                case 5: {
                    longValue |= 0xFFFFFF0000000000L;
                    break;
                }
                case 6: {
                    longValue |= 0xFFFF000000000000L;
                    break;
                }
                case 7: {
                    longValue |= 0xFF00000000000000L;
                    break;
                }
            }
        }
        this.totalBytesRead += length;
        Debug.debugASN1Read(Level.INFO, "Long", type, length, longValue);
        return longValue;
    }
    
    public BigInteger readBigInteger() throws IOException, ASN1Exception {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        if (length == 0) {
            throw new ASN1Exception(ASN1Messages.ERR_BIG_INTEGER_DECODE_EMPTY_VALUE.get());
        }
        final byte[] valueBytes = new byte[length];
        for (int i = 0; i < length; ++i) {
            final int byteRead = this.read(false);
            if (byteRead < 0) {
                throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
            }
            valueBytes[i] = (byte)byteRead;
        }
        final BigInteger bigIntegerValue = new BigInteger(valueBytes);
        this.totalBytesRead += length;
        Debug.debugASN1Read(Level.INFO, "BigInteger", type, length, bigIntegerValue);
        return bigIntegerValue;
    }
    
    public void readNull() throws IOException, ASN1Exception {
        final int type = this.readType();
        if (type < 0) {
            return;
        }
        final int length = this.readLength();
        if (length != 0) {
            this.skip(length);
            throw new ASN1Exception(ASN1Messages.ERR_NULL_HAS_VALUE.get());
        }
        Debug.debugASN1Read(Level.INFO, "Null", type, 0, null);
    }
    
    public byte[] readBytes() throws IOException {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        int valueBytesRead = 0;
        int bytesRemaining = length;
        final byte[] value = new byte[length];
        while (valueBytesRead < length) {
            final int bytesRead = this.read(value, valueBytesRead, bytesRemaining);
            if (bytesRead < 0) {
                throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
            }
            valueBytesRead += bytesRead;
            bytesRemaining -= bytesRead;
        }
        this.totalBytesRead += length;
        Debug.debugASN1Read(Level.INFO, "byte[]", type, length, value);
        return value;
    }
    
    public String readString() throws IOException {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        int valueBytesRead = 0;
        int bytesRemaining = length;
        final byte[] value = new byte[length];
        while (valueBytesRead < length) {
            final int bytesRead = this.read(value, valueBytesRead, bytesRemaining);
            if (bytesRead < 0) {
                throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
            }
            valueBytesRead += bytesRead;
            bytesRemaining -= bytesRead;
        }
        this.totalBytesRead += length;
        final String s = StaticUtils.toUTF8String(value);
        Debug.debugASN1Read(Level.INFO, "String", type, length, s);
        return s;
    }
    
    public Date readUTCTime() throws IOException, ASN1Exception {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        int valueBytesRead = 0;
        int bytesRemaining = length;
        final byte[] value = new byte[length];
        while (valueBytesRead < length) {
            final int bytesRead = this.read(value, valueBytesRead, bytesRemaining);
            if (bytesRead < 0) {
                throw new IOException(ASN1Messages.ERR_READ_END_BEFORE_VALUE_END.get());
            }
            valueBytesRead += bytesRead;
            bytesRemaining -= bytesRead;
        }
        this.totalBytesRead += length;
        final String timestamp = StaticUtils.toUTF8String(value);
        final Date date = new Date(ASN1UTCTime.decodeTimestamp(timestamp));
        Debug.debugASN1Read(Level.INFO, "UTCTime", type, length, timestamp);
        return date;
    }
    
    public ASN1StreamReaderSequence beginSequence() throws IOException {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        Debug.debugASN1Read(Level.INFO, "Sequence Header", type, length, null);
        return new ASN1StreamReaderSequence(this, (byte)type, length);
    }
    
    public ASN1StreamReaderSet beginSet() throws IOException {
        final int type = this.readType();
        if (type < 0) {
            return null;
        }
        final int length = this.readLength();
        Debug.debugASN1Read(Level.INFO, "Set Header", type, length, null);
        return new ASN1StreamReaderSet(this, (byte)type, length);
    }
    
    private int read(final boolean initial) throws IOException {
        if (this.saslClient != null) {
            if (this.saslInputStream != null) {
                final int b = this.saslInputStream.read();
                if (b >= 0) {
                    return b;
                }
            }
            this.readAndDecodeSASLData(-1);
            return this.saslInputStream.read();
        }
        try {
            final int b = this.inputStream.read();
            if (this.saslClient == null || b < 0) {
                return b;
            }
            this.readAndDecodeSASLData(b);
            return this.saslInputStream.read();
        }
        catch (final SocketTimeoutException ste) {
            Debug.debugException(Level.FINEST, ste);
            if (!initial || !this.ignoreInitialSocketTimeout) {
                if (initial || !this.ignoreSubsequentSocketTimeout) {
                    throw ste;
                }
            }
            try {
                return this.inputStream.read();
            }
            catch (final SocketTimeoutException ste2) {
                Debug.debugException(Level.FINEST, ste2);
                return this.inputStream.read();
            }
            throw ste;
        }
    }
    
    private int read(final byte[] buffer, final int offset, final int length) throws IOException {
        if (this.saslClient != null) {
            if (this.saslInputStream != null) {
                final int bytesRead = this.saslInputStream.read(buffer, offset, length);
                if (bytesRead > 0) {
                    return bytesRead;
                }
            }
            this.readAndDecodeSASLData(-1);
            return this.saslInputStream.read(buffer, offset, length);
        }
        try {
            return this.inputStream.read(buffer, offset, length);
        }
        catch (final SocketTimeoutException ste) {
            Debug.debugException(Level.FINEST, ste);
            if (this.ignoreSubsequentSocketTimeout) {
                try {
                    return this.inputStream.read(buffer, offset, length);
                }
                catch (final SocketTimeoutException ste2) {
                    Debug.debugException(Level.FINEST, ste2);
                    return this.inputStream.read(buffer, offset, length);
                }
            }
            throw ste;
        }
    }
    
    void setSASLClient(final SaslClient saslClient) {
        this.saslClient = saslClient;
    }
    
    private void readAndDecodeSASLData(final int firstByte) throws IOException {
        int numWrappedBytes = 0;
        int numLengthBytes = 4;
        if (firstByte >= 0) {
            numLengthBytes = 3;
            numWrappedBytes = firstByte;
        }
        for (int i = 0; i < numLengthBytes; ++i) {
            final int b = this.inputStream.read();
            if (b < 0) {
                if (i != 0 || firstByte >= 0) {
                    throw new IOException(ASN1Messages.ERR_STREAM_READER_EOS_READING_SASL_LENGTH.get(i));
                }
                this.saslInputStream = new ByteArrayInputStream(StaticUtils.NO_BYTES);
            }
            else {
                numWrappedBytes = (numWrappedBytes << 8 | (b & 0xFF));
            }
        }
        if (this.maxElementSize > 0 && numWrappedBytes > this.maxElementSize) {
            throw new IOException(ASN1Messages.ERR_READ_SASL_LENGTH_EXCEEDS_MAX.get(numWrappedBytes, this.maxElementSize));
        }
        int wrappedDataPos = 0;
        final byte[] wrappedData = new byte[numWrappedBytes];
        while (true) {
            final int numBytesRead = this.inputStream.read(wrappedData, wrappedDataPos, numWrappedBytes - wrappedDataPos);
            if (numBytesRead < 0) {
                throw new IOException(ASN1Messages.ERR_STREAM_READER_EOS_READING_SASL_DATA.get(wrappedDataPos, numWrappedBytes));
            }
            wrappedDataPos += numBytesRead;
            if (wrappedDataPos >= numWrappedBytes) {
                final byte[] unwrappedData = this.saslClient.unwrap(wrappedData, 0, numWrappedBytes);
                this.saslInputStream = new ByteArrayInputStream(unwrappedData, 0, unwrappedData.length);
            }
        }
    }
}
