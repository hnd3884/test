package com.microsoft.sqlserver.jdbc;

import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.nio.charset.Charset;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetTime;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.SimpleTimeZone;
import microsoft.sql.DateTimeOffset;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.util.TimeZone;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

final class TDSWriter
{
    private static Logger logger;
    private final String traceID;
    private final TDSChannel tdsChannel;
    private final SQLServerConnection con;
    private boolean dataIsLoggable;
    private TDSCommand command;
    private byte tdsMessageType;
    private volatile int sendResetConnection;
    private int currentPacketSize;
    private static final int TDS_PACKET_HEADER_SIZE = 8;
    private static final byte[] placeholderHeader;
    private byte[] valueBytes;
    private int packetNum;
    private static final int BYTES4 = 4;
    private static final int BYTES8 = 8;
    private static final int BYTES12 = 12;
    private static final int BYTES16 = 16;
    public static final int BIGDECIMAL_MAX_LENGTH = 17;
    private boolean isEOMSent;
    private ByteBuffer stagingBuffer;
    private ByteBuffer socketBuffer;
    private ByteBuffer logBuffer;
    private CryptoMetadata cryptoMeta;
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    void setDataLoggable(final boolean value) {
        this.dataIsLoggable = value;
    }
    
    SharedTimer getSharedTimer() throws SQLServerException {
        return this.con.getSharedTimer();
    }
    
    boolean isEOMSent() {
        return this.isEOMSent;
    }
    
    TDSWriter(final TDSChannel tdsChannel, final SQLServerConnection con) {
        this.dataIsLoggable = true;
        this.command = null;
        this.sendResetConnection = 0;
        this.currentPacketSize = 0;
        this.valueBytes = new byte[256];
        this.packetNum = 0;
        this.isEOMSent = false;
        this.cryptoMeta = null;
        this.tdsChannel = tdsChannel;
        this.con = con;
        this.traceID = "TDSWriter@" + Integer.toHexString(this.hashCode()) + " (" + con.toString() + ")";
    }
    
    void preparePacket() throws SQLServerException {
        if (this.tdsChannel.isLoggingPackets()) {
            Arrays.fill(this.logBuffer.array(), (byte)(-2));
            this.logBuffer.clear();
        }
        this.writeBytes(TDSWriter.placeholderHeader);
    }
    
    void writeMessageHeader() throws SQLServerException {
        if (1 == this.tdsMessageType || 14 == this.tdsMessageType || 3 == this.tdsMessageType) {
            boolean includeTraceHeader = false;
            int totalHeaderLength = 22;
            if ((1 == this.tdsMessageType || 3 == this.tdsMessageType) && this.con.isDenaliOrLater() && Util.isActivityTraceOn() && !ActivityCorrelator.getCurrent().isSentToServer()) {
                includeTraceHeader = true;
                totalHeaderLength += 26;
            }
            this.writeInt(totalHeaderLength);
            this.writeInt(18);
            this.writeShort((short)2);
            this.writeBytes(this.con.getTransactionDescriptor());
            this.writeInt(1);
            if (includeTraceHeader) {
                this.writeInt(26);
                this.writeTraceHeaderData();
                ActivityCorrelator.setCurrentActivityIdSentFlag();
            }
        }
    }
    
    void writeTraceHeaderData() throws SQLServerException {
        final ActivityId activityId = ActivityCorrelator.getCurrent();
        final byte[] actIdByteArray = Util.asGuidByteArray(activityId.getId());
        final long seqNum = activityId.getSequence();
        this.writeShort((short)3);
        this.writeBytes(actIdByteArray, 0, actIdByteArray.length);
        this.writeInt((int)seqNum);
        if (TDSWriter.logger.isLoggable(Level.FINER)) {
            TDSWriter.logger.finer("Send Trace Header - ActivityID: " + activityId.toString());
        }
    }
    
    void startMessage(final TDSCommand command, final byte tdsMessageType) throws SQLServerException {
        this.command = command;
        this.tdsMessageType = tdsMessageType;
        this.packetNum = 0;
        this.isEOMSent = false;
        this.dataIsLoggable = true;
        final int negotiatedPacketSize = this.con.getTDSPacketSize();
        if (this.currentPacketSize != negotiatedPacketSize) {
            this.socketBuffer = ByteBuffer.allocate(negotiatedPacketSize).order(ByteOrder.LITTLE_ENDIAN);
            this.stagingBuffer = ByteBuffer.allocate(negotiatedPacketSize).order(ByteOrder.LITTLE_ENDIAN);
            this.logBuffer = ByteBuffer.allocate(negotiatedPacketSize).order(ByteOrder.LITTLE_ENDIAN);
            this.currentPacketSize = negotiatedPacketSize;
        }
        this.socketBuffer.position(this.socketBuffer.limit());
        this.stagingBuffer.clear();
        this.preparePacket();
        this.writeMessageHeader();
    }
    
    final void endMessage() throws SQLServerException {
        if (TDSWriter.logger.isLoggable(Level.FINEST)) {
            TDSWriter.logger.finest(this.toString() + " Finishing TDS message");
        }
        this.writePacket(1);
    }
    
    final boolean ignoreMessage() throws SQLServerException {
        if (this.packetNum <= 0 && 7 != this.tdsMessageType) {
            return false;
        }
        assert !this.isEOMSent;
        if (TDSWriter.logger.isLoggable(Level.FINER)) {
            TDSWriter.logger.finest(this.toString() + " Finishing TDS message by sending ignore bit and end of message");
        }
        this.writePacket(3);
        return true;
    }
    
    final void resetPooledConnection() {
        if (TDSWriter.logger.isLoggable(Level.FINEST)) {
            TDSWriter.logger.finest(this.toString() + " resetPooledConnection");
        }
        this.sendResetConnection = 8;
    }
    
    void writeByte(final byte value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 1) {
            this.stagingBuffer.put(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.put(value);
                }
                else {
                    this.logBuffer.position(this.logBuffer.position() + 1);
                }
            }
        }
        else {
            this.valueBytes[0] = value;
            this.writeWrappedBytes(this.valueBytes, 1);
        }
    }
    
    void writeCollationForSqlVariant(final SqlVariant variantType) throws SQLServerException {
        this.writeInt(variantType.getCollation().getCollationInfo());
        this.writeByte((byte)(variantType.getCollation().getCollationSortID() & 0xFF));
    }
    
    void writeChar(final char value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 2) {
            this.stagingBuffer.putChar(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putChar(value);
                }
                else {
                    this.logBuffer.position(this.logBuffer.position() + 2);
                }
            }
        }
        else {
            Util.writeShort((short)value, this.valueBytes, 0);
            this.writeWrappedBytes(this.valueBytes, 2);
        }
    }
    
    void writeShort(final short value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 2) {
            this.stagingBuffer.putShort(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putShort(value);
                }
                else {
                    this.logBuffer.position(this.logBuffer.position() + 2);
                }
            }
        }
        else {
            Util.writeShort(value, this.valueBytes, 0);
            this.writeWrappedBytes(this.valueBytes, 2);
        }
    }
    
    void writeInt(final int value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 4) {
            this.stagingBuffer.putInt(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putInt(value);
                }
                else {
                    this.logBuffer.position(this.logBuffer.position() + 4);
                }
            }
        }
        else {
            Util.writeInt(value, this.valueBytes, 0);
            this.writeWrappedBytes(this.valueBytes, 4);
        }
    }
    
    void writeReal(final float value) throws SQLServerException {
        this.writeInt(Float.floatToRawIntBits(value));
    }
    
    void writeDouble(final double value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 8) {
            this.stagingBuffer.putDouble(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putDouble(value);
                }
                else {
                    this.logBuffer.position(this.logBuffer.position() + 8);
                }
            }
        }
        else {
            final long bits = Double.doubleToLongBits(value);
            long mask = 255L;
            int nShift = 0;
            for (int i = 0; i < 8; ++i) {
                this.writeByte((byte)((bits & mask) >> nShift));
                nShift += 8;
                mask <<= 8;
            }
        }
    }
    
    void writeBigDecimal(BigDecimal bigDecimalVal, final int srcJdbcType, final int precision, final int scale) throws SQLServerException {
        bigDecimalVal = bigDecimalVal.setScale(scale, RoundingMode.HALF_UP);
        final int bLength = 17;
        this.writeByte((byte)bLength);
        final byte[] bytes = new byte[bLength];
        final byte[] valueBytes = DDC.convertBigDecimalToBytes(bigDecimalVal, scale);
        System.arraycopy(valueBytes, 2, bytes, 0, valueBytes.length - 2);
        this.writeBytes(bytes);
    }
    
    void writeSqlVariantInternalBigDecimal(final BigDecimal bigDecimalVal, final int srcJdbcType) throws SQLServerException {
        final boolean isNegative = bigDecimalVal.signum() < 0;
        BigInteger bi = bigDecimalVal.unscaledValue();
        if (isNegative) {
            bi = bi.negate();
        }
        final int bLength = 16;
        this.writeByte((byte)(isNegative ? 0 : 1));
        final byte[] unscaledBytes = bi.toByteArray();
        if (unscaledBytes.length > bLength) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
            final Object[] msgArgs = { JDBCType.of(srcJdbcType) };
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET, null);
        }
        final byte[] bytes = new byte[bLength];
        final int remaining = bLength - unscaledBytes.length;
        int i = 0;
        for (int j = unscaledBytes.length - 1; i < unscaledBytes.length; bytes[i++] = unscaledBytes[j--]) {}
        while (i < remaining) {
            bytes[i] = 0;
            ++i;
        }
        this.writeBytes(bytes);
    }
    
    void writeSmalldatetime(final String value) throws SQLServerException {
        final GregorianCalendar calendar = this.initializeCalender(TimeZone.getDefault());
        final Timestamp timestampValue = Timestamp.valueOf(value);
        final long utcMillis = timestampValue.getTime();
        calendar.setTimeInMillis(utcMillis);
        int daysSinceSQLBaseDate = DDC.daysSinceBaseDate(calendar.get(1), calendar.get(6), 1900);
        int millisSinceMidnight = 1000 * calendar.get(13) + 60000 * calendar.get(12) + 3600000 * calendar.get(11);
        if (86399999 <= millisSinceMidnight) {
            ++daysSinceSQLBaseDate;
            millisSinceMidnight = 0;
        }
        this.writeShort((short)daysSinceSQLBaseDate);
        final int secondsSinceMidnight = millisSinceMidnight / 1000;
        int minutesSinceMidnight = secondsSinceMidnight / 60;
        minutesSinceMidnight = ((secondsSinceMidnight % 60 > 29.998) ? (minutesSinceMidnight + 1) : minutesSinceMidnight);
        this.writeShort((short)minutesSinceMidnight);
    }
    
    void writeDatetime(final String value) throws SQLServerException {
        final GregorianCalendar calendar = this.initializeCalender(TimeZone.getDefault());
        final Timestamp timestampValue = Timestamp.valueOf(value);
        final long utcMillis = timestampValue.getTime();
        final int subSecondNanos = timestampValue.getNanos();
        calendar.setTimeInMillis(utcMillis);
        int daysSinceSQLBaseDate = DDC.daysSinceBaseDate(calendar.get(1), calendar.get(6), 1900);
        int millisSinceMidnight = (subSecondNanos + 500000) / 1000000 + 1000 * calendar.get(13) + 60000 * calendar.get(12) + 3600000 * calendar.get(11);
        if (86399999 <= millisSinceMidnight) {
            ++daysSinceSQLBaseDate;
            millisSinceMidnight = 0;
        }
        if (daysSinceSQLBaseDate < DDC.daysSinceBaseDate(1753, 1, 1900) || daysSinceSQLBaseDate >= DDC.daysSinceBaseDate(10000, 1, 1900)) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
            final Object[] msgArgs = { SSType.DATETIME };
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
        }
        this.writeInt(daysSinceSQLBaseDate);
        this.writeInt((3 * millisSinceMidnight + 5) / 10);
    }
    
    void writeDate(final String value) throws SQLServerException {
        final GregorianCalendar calendar = this.initializeCalender(TimeZone.getDefault());
        final Date dateValue = Date.valueOf(value);
        final long utcMillis = dateValue.getTime();
        calendar.setTimeInMillis(utcMillis);
        this.writeScaledTemporal(calendar, 0, 0, SSType.DATE);
    }
    
    void writeTime(final Timestamp value, final int scale) throws SQLServerException {
        final GregorianCalendar calendar = this.initializeCalender(TimeZone.getDefault());
        final long utcMillis = value.getTime();
        final int subSecondNanos = value.getNanos();
        calendar.setTimeInMillis(utcMillis);
        this.writeScaledTemporal(calendar, subSecondNanos, scale, SSType.TIME);
    }
    
    void writeDateTimeOffset(final Object value, final int scale, final SSType destSSType) throws SQLServerException {
        final DateTimeOffset dtoValue = (DateTimeOffset)value;
        final long utcMillis = dtoValue.getTimestamp().getTime();
        final int subSecondNanos = dtoValue.getTimestamp().getNanos();
        final int minutesOffset = dtoValue.getMinutesOffset();
        final TimeZone timeZone = (SSType.DATETIMEOFFSET == destSSType) ? UTC.timeZone : new SimpleTimeZone(minutesOffset * 60 * 1000, "");
        final GregorianCalendar calendar = new GregorianCalendar(timeZone, Locale.US);
        calendar.setLenient(true);
        calendar.clear();
        calendar.setTimeInMillis(utcMillis);
        this.writeScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIMEOFFSET);
        this.writeShort((short)minutesOffset);
    }
    
    void writeOffsetDateTimeWithTimezone(final OffsetDateTime offsetDateTimeValue, final int scale) throws SQLServerException {
        int minutesOffset = 0;
        try {
            minutesOffset = offsetDateTimeValue.getOffset().getTotalSeconds() / 60;
        }
        catch (final Exception e) {
            throw new SQLServerException(SQLServerException.getErrString("R_zoneOffsetError"), null, 0, e);
        }
        int subSecondNanos = offsetDateTimeValue.getNano();
        for (int padding = 9 - String.valueOf(subSecondNanos).length(); padding > 0; --padding) {
            subSecondNanos *= 10;
        }
        final TimeZone timeZone = UTC.timeZone;
        final String offDateTimeStr = String.format("%04d", offsetDateTimeValue.getYear()) + '-' + offsetDateTimeValue.getMonthValue() + '-' + offsetDateTimeValue.getDayOfMonth() + ' ' + offsetDateTimeValue.getHour() + ':' + offsetDateTimeValue.getMinute() + ':' + offsetDateTimeValue.getSecond();
        final long utcMillis = Timestamp.valueOf(offDateTimeStr).getTime();
        final GregorianCalendar calendar = this.initializeCalender(timeZone);
        calendar.setTimeInMillis(utcMillis);
        int minuteAdjustment = TimeZone.getDefault().getRawOffset() / 60000;
        if (TimeZone.getDefault().inDaylightTime(calendar.getTime())) {
            minuteAdjustment += TimeZone.getDefault().getDSTSavings() / 60000;
        }
        minuteAdjustment += ((minuteAdjustment < 0) ? (minutesOffset * -1) : minutesOffset);
        calendar.add(12, minuteAdjustment);
        this.writeScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIMEOFFSET);
        this.writeShort((short)minutesOffset);
    }
    
    void writeOffsetTimeWithTimezone(final OffsetTime offsetTimeValue, final int scale) throws SQLServerException {
        int minutesOffset = 0;
        try {
            minutesOffset = offsetTimeValue.getOffset().getTotalSeconds() / 60;
        }
        catch (final Exception e) {
            throw new SQLServerException(SQLServerException.getErrString("R_zoneOffsetError"), null, 0, e);
        }
        int subSecondNanos = offsetTimeValue.getNano();
        for (int padding = 9 - String.valueOf(subSecondNanos).length(); padding > 0; --padding) {
            subSecondNanos *= 10;
        }
        final TimeZone timeZone = UTC.timeZone;
        final String offsetTimeStr = "1900-01-01 " + offsetTimeValue.getHour() + ':' + offsetTimeValue.getMinute() + ':' + offsetTimeValue.getSecond();
        final long utcMillis = Timestamp.valueOf(offsetTimeStr).getTime();
        final GregorianCalendar calendar = this.initializeCalender(timeZone);
        calendar.setTimeInMillis(utcMillis);
        int minuteAdjustment = TimeZone.getDefault().getRawOffset() / 60000;
        if (TimeZone.getDefault().inDaylightTime(calendar.getTime())) {
            minuteAdjustment += TimeZone.getDefault().getDSTSavings() / 60000;
        }
        minuteAdjustment += ((minuteAdjustment < 0) ? (minutesOffset * -1) : minutesOffset);
        calendar.add(12, minuteAdjustment);
        this.writeScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIMEOFFSET);
        this.writeShort((short)minutesOffset);
    }
    
    void writeLong(final long value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 8) {
            this.stagingBuffer.putLong(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putLong(value);
                }
                else {
                    this.logBuffer.position(this.logBuffer.position() + 8);
                }
            }
        }
        else {
            Util.writeLong(value, this.valueBytes, 0);
            this.writeWrappedBytes(this.valueBytes, 8);
        }
    }
    
    void writeBytes(final byte[] value) throws SQLServerException {
        this.writeBytes(value, 0, value.length);
    }
    
    void writeBytes(final byte[] value, final int offset, final int length) throws SQLServerException {
        assert length <= value.length;
        int bytesWritten = 0;
        if (TDSWriter.logger.isLoggable(Level.FINEST)) {
            TDSWriter.logger.finest(this.toString() + " Writing " + length + " bytes");
        }
        int bytesToWrite;
        while ((bytesToWrite = length - bytesWritten) > 0) {
            if (0 == this.stagingBuffer.remaining()) {
                this.writePacket(0);
            }
            if (bytesToWrite > this.stagingBuffer.remaining()) {
                bytesToWrite = this.stagingBuffer.remaining();
            }
            this.stagingBuffer.put(value, offset + bytesWritten, bytesToWrite);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.put(value, offset + bytesWritten, bytesToWrite);
                }
                else {
                    this.logBuffer.position(this.logBuffer.position() + bytesToWrite);
                }
            }
            bytesWritten += bytesToWrite;
        }
    }
    
    void writeWrappedBytes(final byte[] value, final int valueLength) throws SQLServerException {
        assert valueLength <= value.length;
        int remaining = this.stagingBuffer.remaining();
        assert remaining < valueLength;
        assert valueLength <= this.stagingBuffer.capacity();
        remaining = this.stagingBuffer.remaining();
        if (remaining > 0) {
            this.stagingBuffer.put(value, 0, remaining);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.put(value, 0, remaining);
                }
                else {
                    this.logBuffer.position(this.logBuffer.position() + remaining);
                }
            }
        }
        this.writePacket(0);
        this.stagingBuffer.put(value, remaining, valueLength - remaining);
        if (this.tdsChannel.isLoggingPackets()) {
            if (this.dataIsLoggable) {
                this.logBuffer.put(value, remaining, valueLength - remaining);
            }
            else {
                this.logBuffer.position(this.logBuffer.position() + remaining);
            }
        }
    }
    
    void writeString(final String value) throws SQLServerException {
        int charsCopied = 0;
        final int length = value.length();
        while (charsCopied < length) {
            int bytesToCopy = 2 * (length - charsCopied);
            if (bytesToCopy > this.valueBytes.length) {
                bytesToCopy = this.valueBytes.length;
            }
            int bytesCopied = 0;
            try {
                while (bytesCopied < bytesToCopy) {
                    final char ch = value.charAt(charsCopied++);
                    this.valueBytes[bytesCopied++] = (byte)(ch >> 0 & 0xFF);
                    this.valueBytes[bytesCopied++] = (byte)(ch >> 8 & 0xFF);
                }
                this.writeBytes(this.valueBytes, 0, bytesCopied);
            }
            catch (final ArrayIndexOutOfBoundsException e) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
                final Object[] msgArgs = { bytesCopied };
                this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
            }
        }
    }
    
    void writeStream(final InputStream inputStream, final long advertisedLength, final boolean writeChunkSizes) throws SQLServerException {
        assert advertisedLength >= 0L;
        long actualLength = 0L;
        final byte[] streamByteBuffer = new byte[4 * this.currentPacketSize];
        int bytesRead = 0;
        int bytesToWrite;
        do {
            for (bytesToWrite = 0; -1 != bytesRead && bytesToWrite < streamByteBuffer.length; bytesToWrite += bytesRead) {
                try {
                    bytesRead = inputStream.read(streamByteBuffer, bytesToWrite, streamByteBuffer.length - bytesToWrite);
                }
                catch (final IOException e) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    final Object[] msgArgs = { e.toString() };
                    this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
                }
                if (-1 == bytesRead) {
                    break;
                }
                if (bytesRead < 0 || bytesRead > streamByteBuffer.length - bytesToWrite) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    final Object[] msgArgs2 = { SQLServerException.getErrString("R_streamReadReturnedInvalidValue") };
                    this.error(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
                }
            }
            if (writeChunkSizes) {
                this.writeInt(bytesToWrite);
            }
            this.writeBytes(streamByteBuffer, 0, bytesToWrite);
            actualLength += bytesToWrite;
        } while (-1 != bytesRead || bytesToWrite > 0);
        if (-1L != advertisedLength && actualLength != advertisedLength) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
            final Object[] msgArgs2 = { advertisedLength, actualLength };
            this.error(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET);
        }
    }
    
    void writeNonUnicodeReader(final Reader reader, final long advertisedLength, final boolean isDestBinary, final Charset charSet) throws SQLServerException {
        assert advertisedLength >= 0L;
        long actualLength = 0L;
        final char[] streamCharBuffer = new char[this.currentPacketSize];
        final byte[] streamByteBuffer = new byte[this.currentPacketSize];
        int charsRead = 0;
        int charsToWrite;
        do {
            for (charsToWrite = 0; -1 != charsRead && charsToWrite < streamCharBuffer.length; charsToWrite += charsRead) {
                try {
                    charsRead = reader.read(streamCharBuffer, charsToWrite, streamCharBuffer.length - charsToWrite);
                }
                catch (final IOException e) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    final Object[] msgArgs = { e.toString() };
                    this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
                }
                if (-1 == charsRead) {
                    break;
                }
                if (charsRead < 0 || charsRead > streamCharBuffer.length - charsToWrite) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    final Object[] msgArgs2 = { SQLServerException.getErrString("R_streamReadReturnedInvalidValue") };
                    this.error(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
                }
            }
            if (!isDestBinary) {
                this.writeInt(charsToWrite);
                for (int charsCopied = 0; charsCopied < charsToWrite; ++charsCopied) {
                    if (null == charSet) {
                        streamByteBuffer[charsCopied] = (byte)(streamCharBuffer[charsCopied] & '\u00ff');
                    }
                    else {
                        streamByteBuffer[charsCopied] = new String(streamCharBuffer[charsCopied] + "").getBytes(charSet)[0];
                    }
                }
                this.writeBytes(streamByteBuffer, 0, charsToWrite);
            }
            else {
                int bytesToWrite;
                if (0 != (bytesToWrite = charsToWrite)) {
                    bytesToWrite = charsToWrite / 2;
                }
                final String streamString = new String(streamCharBuffer);
                final byte[] bytes = ParameterUtils.HexToBin(streamString.trim());
                this.writeInt(bytesToWrite);
                this.writeBytes(bytes, 0, bytesToWrite);
            }
            actualLength += charsToWrite;
        } while (-1 != charsRead || charsToWrite > 0);
        if (-1L != advertisedLength && actualLength != advertisedLength) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
            final Object[] msgArgs2 = { advertisedLength, actualLength };
            this.error(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET);
        }
    }
    
    void writeReader(final Reader reader, final long advertisedLength, final boolean writeChunkSizes) throws SQLServerException {
        assert advertisedLength >= 0L;
        long actualLength = 0L;
        final char[] streamCharBuffer = new char[2 * this.currentPacketSize];
        final byte[] streamByteBuffer = new byte[4 * this.currentPacketSize];
        int charsRead = 0;
        int charsToWrite;
        do {
            for (charsToWrite = 0; -1 != charsRead && charsToWrite < streamCharBuffer.length; charsToWrite += charsRead) {
                try {
                    charsRead = reader.read(streamCharBuffer, charsToWrite, streamCharBuffer.length - charsToWrite);
                }
                catch (final IOException e) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    final Object[] msgArgs = { e.toString() };
                    this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
                }
                if (-1 == charsRead) {
                    break;
                }
                if (charsRead < 0 || charsRead > streamCharBuffer.length - charsToWrite) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    final Object[] msgArgs2 = { SQLServerException.getErrString("R_streamReadReturnedInvalidValue") };
                    this.error(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
                }
            }
            if (writeChunkSizes) {
                this.writeInt(2 * charsToWrite);
            }
            for (int charsCopied = 0; charsCopied < charsToWrite; ++charsCopied) {
                streamByteBuffer[2 * charsCopied] = (byte)(streamCharBuffer[charsCopied] >> 0 & 0xFF);
                streamByteBuffer[2 * charsCopied + 1] = (byte)(streamCharBuffer[charsCopied] >> 8 & 0xFF);
            }
            this.writeBytes(streamByteBuffer, 0, 2 * charsToWrite);
            actualLength += charsToWrite;
        } while (-1 != charsRead || charsToWrite > 0);
        if (-1L != advertisedLength && actualLength != advertisedLength) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
            final Object[] msgArgs2 = { advertisedLength, actualLength };
            this.error(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET);
        }
    }
    
    GregorianCalendar initializeCalender(final TimeZone timeZone) {
        final GregorianCalendar calendar = new GregorianCalendar(timeZone, Locale.US);
        calendar.setLenient(true);
        calendar.clear();
        return calendar;
    }
    
    final void error(final String reason, final SQLState sqlState, final DriverError driverError) throws SQLServerException {
        assert null != this.command;
        this.command.interrupt(reason);
        throw new SQLServerException(reason, sqlState, driverError, null);
    }
    
    final boolean sendAttention() throws SQLServerException {
        if (this.packetNum > 0) {
            if (TDSWriter.logger.isLoggable(Level.FINE)) {
                TDSWriter.logger.fine(this + ": sending attention...");
            }
            final TDSChannel tdsChannel = this.tdsChannel;
            ++tdsChannel.numMsgsSent;
            this.startMessage(this.command, (byte)6);
            this.endMessage();
            return true;
        }
        return false;
    }
    
    private void writePacket(final int tdsMessageStatus) throws SQLServerException {
        final boolean atEOM = 0x1 == (0x1 & tdsMessageStatus);
        final boolean isCancelled = 6 == this.tdsMessageType || (tdsMessageStatus & 0x2) == 0x2;
        if (null != this.command && !isCancelled) {
            this.command.checkForInterrupt();
        }
        this.writePacketHeader(tdsMessageStatus | this.sendResetConnection);
        this.sendResetConnection = 0;
        this.flush(atEOM);
        if (atEOM) {
            this.flush(atEOM);
            this.isEOMSent = true;
            final TDSChannel tdsChannel = this.tdsChannel;
            ++tdsChannel.numMsgsSent;
        }
        if (16 == this.tdsMessageType && 1 == this.packetNum && 0 == this.con.getNegotiatedEncryptionLevel()) {
            this.tdsChannel.disableSSL();
        }
        if (null != this.command && !isCancelled && atEOM) {
            this.command.onRequestComplete();
        }
    }
    
    private void writePacketHeader(final int tdsMessageStatus) {
        final int tdsMessageLength = this.stagingBuffer.position();
        ++this.packetNum;
        this.stagingBuffer.put(0, this.tdsMessageType);
        this.stagingBuffer.put(1, (byte)tdsMessageStatus);
        this.stagingBuffer.put(2, (byte)(tdsMessageLength >> 8 & 0xFF));
        this.stagingBuffer.put(3, (byte)(tdsMessageLength >> 0 & 0xFF));
        this.stagingBuffer.put(4, (byte)(this.tdsChannel.getSPID() >> 8 & 0xFF));
        this.stagingBuffer.put(5, (byte)(this.tdsChannel.getSPID() >> 0 & 0xFF));
        this.stagingBuffer.put(6, (byte)(this.packetNum % 256));
        this.stagingBuffer.put(7, (byte)0);
        if (this.tdsChannel.isLoggingPackets()) {
            this.logBuffer.put(0, this.tdsMessageType);
            this.logBuffer.put(1, (byte)tdsMessageStatus);
            this.logBuffer.put(2, (byte)(tdsMessageLength >> 8 & 0xFF));
            this.logBuffer.put(3, (byte)(tdsMessageLength >> 0 & 0xFF));
            this.logBuffer.put(4, (byte)(this.tdsChannel.getSPID() >> 8 & 0xFF));
            this.logBuffer.put(5, (byte)(this.tdsChannel.getSPID() >> 0 & 0xFF));
            this.logBuffer.put(6, (byte)(this.packetNum % 256));
            this.logBuffer.put(7, (byte)0);
        }
    }
    
    void flush(final boolean atEOM) throws SQLServerException {
        this.tdsChannel.write(this.socketBuffer.array(), this.socketBuffer.position(), this.socketBuffer.remaining());
        this.socketBuffer.position(this.socketBuffer.limit());
        if (this.stagingBuffer.position() >= 8) {
            final ByteBuffer swapBuffer = this.stagingBuffer;
            this.stagingBuffer = this.socketBuffer;
            (this.socketBuffer = swapBuffer).flip();
            this.stagingBuffer.clear();
            if (this.tdsChannel.isLoggingPackets()) {
                this.tdsChannel.logPacket(this.logBuffer.array(), 0, this.socketBuffer.limit(), this.toString() + " sending packet (" + this.socketBuffer.limit() + " bytes)");
            }
            if (!atEOM) {
                this.preparePacket();
            }
            this.tdsChannel.write(this.socketBuffer.array(), this.socketBuffer.position(), this.socketBuffer.remaining());
            this.socketBuffer.position(this.socketBuffer.limit());
        }
    }
    
    void writeRPCNameValType(final String sName, final boolean bOut, final TDSType tdsType) throws SQLServerException {
        int nNameLen = 0;
        if (null != sName) {
            nNameLen = sName.length() + 1;
        }
        this.writeByte((byte)nNameLen);
        if (nNameLen > 0) {
            this.writeChar('@');
            this.writeString(sName);
        }
        if (null != this.cryptoMeta) {
            this.writeByte((byte)(bOut ? 9 : 8));
        }
        else {
            this.writeByte((byte)(bOut ? 1 : 0));
        }
        this.writeByte(tdsType.byteValue());
    }
    
    void writeRPCBit(final String sName, final Boolean booleanValue, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.BITN);
        this.writeByte((byte)1);
        if (null == booleanValue) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)1);
            this.writeByte((byte)(((boolean)booleanValue) ? 1 : 0));
        }
    }
    
    void writeRPCByte(final String sName, final Byte byteValue, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.INTN);
        this.writeByte((byte)1);
        if (null == byteValue) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)1);
            this.writeByte(byteValue);
        }
    }
    
    void writeRPCShort(final String sName, final Short shortValue, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.INTN);
        this.writeByte((byte)2);
        if (null == shortValue) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)2);
            this.writeShort(shortValue);
        }
    }
    
    void writeRPCInt(final String sName, final Integer intValue, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.INTN);
        this.writeByte((byte)4);
        if (null == intValue) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)4);
            this.writeInt(intValue);
        }
    }
    
    void writeRPCLong(final String sName, final Long longValue, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.INTN);
        this.writeByte((byte)8);
        if (null == longValue) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)8);
            this.writeLong(longValue);
        }
    }
    
    void writeRPCReal(final String sName, final Float floatValue, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.FLOATN);
        if (null == floatValue) {
            this.writeByte((byte)4);
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)4);
            this.writeByte((byte)4);
            this.writeInt(Float.floatToRawIntBits(floatValue));
        }
    }
    
    void writeRPCSqlVariant(final String sName, final SqlVariant sqlVariantValue, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.SQL_VARIANT);
        if (null == sqlVariantValue) {
            this.writeInt(0);
            this.writeInt(0);
        }
    }
    
    void writeRPCDouble(final String sName, final Double doubleValue, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.FLOATN);
        final int l = 8;
        this.writeByte((byte)l);
        if (null == doubleValue) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)l);
            final long bits = Double.doubleToLongBits(doubleValue);
            long mask = 255L;
            int nShift = 0;
            for (int i = 0; i < 8; ++i) {
                this.writeByte((byte)((bits & mask) >> nShift));
                nShift += 8;
                mask <<= 8;
            }
        }
    }
    
    void writeRPCBigDecimal(final String sName, final BigDecimal bdValue, final int nScale, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.DECIMALN);
        this.writeByte((byte)17);
        this.writeByte((byte)38);
        final byte[] valueBytes = DDC.convertBigDecimalToBytes(bdValue, nScale);
        this.writeBytes(valueBytes, 0, valueBytes.length);
    }
    
    void writeVMaxHeader(final long headerLength, final boolean isNull, final SQLCollation collation) throws SQLServerException {
        this.writeShort((short)(-1));
        if (null != collation) {
            collation.writeCollation(this);
        }
        if (isNull) {
            this.writeLong(-1L);
        }
        else if (-1L == headerLength) {
            this.writeLong(-2L);
        }
        else {
            this.writeLong(headerLength);
        }
    }
    
    void writeRPCStringUnicode(final String sValue) throws SQLServerException {
        this.writeRPCStringUnicode(null, sValue, false, null);
    }
    
    void writeRPCStringUnicode(final String sName, final String sValue, final boolean bOut, SQLCollation collation) throws SQLServerException {
        final boolean bValueNull = sValue == null;
        final int nValueLen = bValueNull ? 0 : (2 * sValue.length());
        final boolean isShortValue = nValueLen <= 8000;
        if (null == collation) {
            collation = this.con.getDatabaseCollation();
        }
        final boolean usePLP = !isShortValue || bOut;
        if (usePLP) {
            this.writeRPCNameValType(sName, bOut, TDSType.NVARCHAR);
            this.writeVMaxHeader(nValueLen, bValueNull, collation);
            if (!bValueNull) {
                if (nValueLen > 0) {
                    this.writeInt(nValueLen);
                    this.writeString(sValue);
                }
                this.writeInt(0);
            }
        }
        else {
            if (isShortValue) {
                this.writeRPCNameValType(sName, bOut, TDSType.NVARCHAR);
                this.writeShort((short)8000);
            }
            else {
                this.writeRPCNameValType(sName, bOut, TDSType.NTEXT);
                this.writeInt(Integer.MAX_VALUE);
            }
            collation.writeCollation(this);
            if (bValueNull) {
                this.writeShort((short)(-1));
            }
            else {
                if (isShortValue) {
                    this.writeShort((short)nValueLen);
                }
                else {
                    this.writeInt(nValueLen);
                }
                if (0 != nValueLen) {
                    this.writeString(sValue);
                }
            }
        }
    }
    
    void writeTVP(final TVP value) throws SQLServerException {
        if (!value.isNull()) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)2);
        }
        this.writeByte((byte)(-13));
        if (null != value.getDbNameTVP()) {
            this.writeByte((byte)value.getDbNameTVP().length());
            this.writeString(value.getDbNameTVP());
        }
        else {
            this.writeByte((byte)0);
        }
        if (null != value.getOwningSchemaNameTVP()) {
            this.writeByte((byte)value.getOwningSchemaNameTVP().length());
            this.writeString(value.getOwningSchemaNameTVP());
        }
        else {
            this.writeByte((byte)0);
        }
        if (null != value.getTVPName()) {
            this.writeByte((byte)value.getTVPName().length());
            this.writeString(value.getTVPName());
        }
        else {
            this.writeByte((byte)0);
        }
        if (!value.isNull()) {
            this.writeTVPColumnMetaData(value);
            this.writeTvpOrderUnique(value);
        }
        else {
            this.writeShort((short)(-1));
        }
        this.writeByte((byte)0);
        try {
            this.writeTVPRows(value);
        }
        catch (final NumberFormatException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_TVPInvalidColumnValue"), e);
        }
        catch (final ClassCastException e2) {
            throw new SQLServerException(SQLServerException.getErrString("R_TVPInvalidColumnValue"), e2);
        }
    }
    
    void writeTVPRows(final TVP value) throws SQLServerException {
        boolean tdsWritterCached = false;
        ByteBuffer cachedTVPHeaders = null;
        TDSCommand cachedCommand = null;
        boolean cachedRequestComplete = false;
        boolean cachedInterruptsEnabled = false;
        boolean cachedProcessedResponse = false;
        if (!value.isNull()) {
            if (TVPType.ResultSet == value.tvpType && null != value.sourceResultSet && value.sourceResultSet instanceof SQLServerResultSet) {
                final SQLServerResultSet sourceResultSet = (SQLServerResultSet)value.sourceResultSet;
                final SQLServerStatement src_stmt = (SQLServerStatement)sourceResultSet.getStatement();
                final int resultSetServerCursorId = sourceResultSet.getServerCursorId();
                if (this.con.equals(src_stmt.getConnection()) && 0 != resultSetServerCursorId) {
                    cachedTVPHeaders = ByteBuffer.allocate(this.stagingBuffer.capacity()).order(this.stagingBuffer.order());
                    cachedTVPHeaders.put(this.stagingBuffer.array(), 0, this.stagingBuffer.position());
                    cachedCommand = this.command;
                    cachedRequestComplete = this.command.getRequestComplete();
                    cachedInterruptsEnabled = this.command.getInterruptsEnabled();
                    cachedProcessedResponse = this.command.getProcessedResponse();
                    tdsWritterCached = true;
                    if (sourceResultSet.isForwardOnly()) {
                        sourceResultSet.setFetchSize(1);
                    }
                }
            }
            final Map<Integer, SQLServerMetaData> columnMetadata = value.getColumnMetadata();
            while (value.next()) {
                if (tdsWritterCached) {
                    this.command = cachedCommand;
                    this.stagingBuffer.clear();
                    this.logBuffer.clear();
                    this.writeBytes(cachedTVPHeaders.array(), 0, cachedTVPHeaders.position());
                }
                final Object[] rowData = value.getRowData();
                this.writeByte((byte)1);
                final Iterator<Map.Entry<Integer, SQLServerMetaData>> columnsIterator = columnMetadata.entrySet().iterator();
                int currentColumn = 0;
                while (columnsIterator.hasNext()) {
                    final Map.Entry<Integer, SQLServerMetaData> columnPair = columnsIterator.next();
                    if (columnPair.getValue().useServerDefault) {
                        ++currentColumn;
                    }
                    else {
                        final JDBCType jdbcType = JDBCType.of(columnPair.getValue().javaSqlType);
                        String currentColumnStringValue = null;
                        Object currentObject = null;
                        if (null != rowData && rowData.length > currentColumn) {
                            currentObject = rowData[currentColumn];
                            if (null != currentObject) {
                                currentColumnStringValue = String.valueOf(currentObject);
                            }
                        }
                        this.writeInternalTVPRowValues(jdbcType, currentColumnStringValue, currentObject, columnPair, false);
                        ++currentColumn;
                    }
                }
                if (tdsWritterCached) {
                    this.writeByte((byte)0);
                    this.writePacket(1);
                    final TDSReader tdsReader = this.tdsChannel.getReader(this.command);
                    final int tokenType = tdsReader.peekTokenType();
                    if (170 == tokenType) {
                        final SQLServerError databaseError = new SQLServerError();
                        databaseError.setFromTDS(tdsReader);
                        SQLServerException.makeFromDatabaseError(this.con, null, databaseError.getErrorMessage(), databaseError, false);
                    }
                    this.command.setInterruptsEnabled(true);
                    this.command.setRequestComplete(false);
                }
            }
        }
        if (tdsWritterCached) {
            this.command.setRequestComplete(cachedRequestComplete);
            this.command.setInterruptsEnabled(cachedInterruptsEnabled);
            this.command.setProcessedResponse(cachedProcessedResponse);
        }
        else {
            this.writeByte((byte)0);
        }
    }
    
    private void writeInternalTVPRowValues(final JDBCType jdbcType, final String currentColumnStringValue, final Object currentObject, final Map.Entry<Integer, SQLServerMetaData> columnPair, final boolean isSqlVariant) throws SQLServerException {
        switch (jdbcType) {
            case BIGINT: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(10, TDSType.INT8.byteValue(), (byte)0);
                }
                else {
                    this.writeByte((byte)8);
                }
                this.writeLong(Long.valueOf(currentColumnStringValue));
                break;
            }
            case BIT: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(3, TDSType.BIT1.byteValue(), (byte)0);
                }
                else {
                    this.writeByte((byte)1);
                }
                this.writeByte((byte)(((boolean)Boolean.valueOf(currentColumnStringValue)) ? 1 : 0));
                break;
            }
            case INTEGER: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (!isSqlVariant) {
                    this.writeByte((byte)4);
                }
                else {
                    this.writeTVPSqlVariantHeader(6, TDSType.INT4.byteValue(), (byte)0);
                }
                this.writeInt(Integer.valueOf(currentColumnStringValue));
                break;
            }
            case SMALLINT:
            case TINYINT: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(6, TDSType.INT4.byteValue(), (byte)0);
                    this.writeInt(Integer.valueOf(currentColumnStringValue));
                    break;
                }
                this.writeByte((byte)2);
                this.writeShort(Short.valueOf(currentColumnStringValue));
                break;
            }
            case DECIMAL:
            case NUMERIC: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(21, TDSType.DECIMALN.byteValue(), (byte)2);
                    this.writeByte((byte)38);
                    this.writeByte((byte)4);
                }
                else {
                    this.writeByte((byte)17);
                }
                BigDecimal bdValue = new BigDecimal(currentColumnStringValue);
                bdValue = bdValue.setScale(columnPair.getValue().scale, RoundingMode.HALF_UP);
                final byte[] valueBytes = DDC.convertBigDecimalToBytes(bdValue, bdValue.scale());
                final byte[] byteValue = new byte[17];
                System.arraycopy(valueBytes, 2, byteValue, 0, valueBytes.length - 2);
                this.writeBytes(byteValue);
                break;
            }
            case DOUBLE: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(10, TDSType.FLOAT8.byteValue(), (byte)0);
                    this.writeDouble(Double.valueOf(currentColumnStringValue));
                    break;
                }
                this.writeByte((byte)8);
                final long bits = Double.doubleToLongBits(Double.valueOf(currentColumnStringValue));
                long mask = 255L;
                int nShift = 0;
                for (int i = 0; i < 8; ++i) {
                    this.writeByte((byte)((bits & mask) >> nShift));
                    nShift += 8;
                    mask <<= 8;
                }
                break;
            }
            case FLOAT:
            case REAL: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(6, TDSType.FLOAT4.byteValue(), (byte)0);
                    this.writeInt(Float.floatToRawIntBits(Float.valueOf(currentColumnStringValue)));
                    break;
                }
                this.writeByte((byte)4);
                this.writeInt(Float.floatToRawIntBits(Float.valueOf(currentColumnStringValue)));
                break;
            }
            case DATE:
            case TIME:
            case TIMESTAMP:
            case DATETIMEOFFSET:
            case DATETIME:
            case SMALLDATETIME:
            case TIMESTAMP_WITH_TIMEZONE:
            case TIME_WITH_TIMEZONE:
            case CHAR:
            case VARCHAR:
            case NCHAR:
            case NVARCHAR:
            case LONGVARCHAR:
            case LONGNVARCHAR:
            case SQLXML: {
                final boolean isShortValue = 2L * columnPair.getValue().precision <= 8000L;
                final boolean isNull = null == currentColumnStringValue;
                final int dataLength = isNull ? 0 : (currentColumnStringValue.length() * 2);
                if (!isShortValue) {
                    if (isNull) {
                        this.writeLong(-1L);
                    }
                    else if (isSqlVariant) {
                        if (dataLength > 16000) {
                            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidStringValue"));
                            throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
                        }
                        final int length = currentColumnStringValue.length();
                        this.writeTVPSqlVariantHeader(9 + length, TDSType.BIGVARCHAR.byteValue(), (byte)7);
                        final SQLCollation col = this.con.getDatabaseCollation();
                        this.writeInt(col.getCollationInfo());
                        this.writeByte((byte)col.getCollationSortID());
                        this.writeShort((short)length);
                        this.writeBytes(currentColumnStringValue.getBytes());
                        break;
                    }
                    else if (-1 == dataLength) {
                        this.writeLong(-2L);
                    }
                    else {
                        this.writeLong(dataLength);
                    }
                    if (!isNull) {
                        if (dataLength > 0) {
                            this.writeInt(dataLength);
                            this.writeString(currentColumnStringValue);
                        }
                        this.writeInt(0);
                        break;
                    }
                    break;
                }
                else {
                    if (isNull) {
                        this.writeShort((short)(-1));
                        break;
                    }
                    if (isSqlVariant) {
                        final int length = currentColumnStringValue.length() * 2;
                        this.writeTVPSqlVariantHeader(9 + length, TDSType.NVARCHAR.byteValue(), (byte)7);
                        final SQLCollation col = this.con.getDatabaseCollation();
                        this.writeInt(col.getCollationInfo());
                        this.writeByte((byte)col.getCollationSortID());
                        final int stringLength = currentColumnStringValue.length();
                        final byte[] typevarlen = { (byte)(2 * stringLength & 0xFF), (byte)(2 * stringLength >> 8 & 0xFF) };
                        this.writeBytes(typevarlen);
                        this.writeString(currentColumnStringValue);
                        break;
                    }
                    this.writeShort((short)dataLength);
                    this.writeString(currentColumnStringValue);
                    break;
                }
                break;
            }
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY: {
                final boolean isShortValue = columnPair.getValue().precision <= 8000;
                final boolean isNull = null == currentObject;
                int dataLength;
                if (currentObject instanceof String) {
                    dataLength = ParameterUtils.HexToBin(currentObject.toString()).length;
                }
                else {
                    dataLength = (isNull ? 0 : ((byte[])currentObject).length);
                }
                if (!isShortValue) {
                    if (isNull) {
                        this.writeLong(-1L);
                    }
                    else if (-1 == dataLength) {
                        this.writeLong(-2L);
                    }
                    else {
                        this.writeLong(dataLength);
                    }
                    if (!isNull) {
                        if (dataLength > 0) {
                            this.writeInt(dataLength);
                            if (currentObject instanceof String) {
                                this.writeBytes(ParameterUtils.HexToBin(currentObject.toString()));
                            }
                            else {
                                this.writeBytes((byte[])currentObject);
                            }
                        }
                        this.writeInt(0);
                        break;
                    }
                    break;
                }
                else {
                    if (isNull) {
                        this.writeShort((short)(-1));
                        break;
                    }
                    this.writeShort((short)dataLength);
                    if (currentObject instanceof String) {
                        this.writeBytes(ParameterUtils.HexToBin(currentObject.toString()));
                        break;
                    }
                    this.writeBytes((byte[])currentObject);
                    break;
                }
                break;
            }
            case SQL_VARIANT: {
                final boolean isShiloh = 8 >= this.con.getServerMajorVersion();
                if (isShiloh) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_SQLVariantSupport"));
                    throw new SQLServerException(null, form2.format(new Object[0]), null, 0, false);
                }
                final JavaType javaType = JavaType.of(currentObject);
                final JDBCType internalJDBCType = javaType.getJDBCType(SSType.UNKNOWN, jdbcType);
                this.writeInternalTVPRowValues(internalJDBCType, currentColumnStringValue, currentObject, columnPair, true);
                break;
            }
            default: {
                assert false : "Unexpected JDBC type " + jdbcType.toString();
                break;
            }
        }
    }
    
    private void writeTVPSqlVariantHeader(final int length, final byte tdsType, final byte probBytes) throws SQLServerException {
        this.writeInt(length);
        this.writeByte(tdsType);
        this.writeByte(probBytes);
    }
    
    void writeTVPColumnMetaData(final TVP value) throws SQLServerException {
        this.writeShort((short)value.getTVPColumnCount());
        final Map<Integer, SQLServerMetaData> columnMetadata = value.getColumnMetadata();
        for (final Map.Entry<Integer, SQLServerMetaData> pair : columnMetadata.entrySet()) {
            final JDBCType jdbcType = JDBCType.of(pair.getValue().javaSqlType);
            final boolean useServerDefault = pair.getValue().useServerDefault;
            this.writeInt(0);
            short flags = 1;
            if (useServerDefault) {
                flags |= 0x200;
            }
            this.writeShort(flags);
            switch (jdbcType) {
                case BIGINT: {
                    this.writeByte(TDSType.INTN.byteValue());
                    this.writeByte((byte)8);
                    break;
                }
                case BIT: {
                    this.writeByte(TDSType.BITN.byteValue());
                    this.writeByte((byte)1);
                    break;
                }
                case INTEGER: {
                    this.writeByte(TDSType.INTN.byteValue());
                    this.writeByte((byte)4);
                    break;
                }
                case SMALLINT:
                case TINYINT: {
                    this.writeByte(TDSType.INTN.byteValue());
                    this.writeByte((byte)2);
                    break;
                }
                case DECIMAL:
                case NUMERIC: {
                    this.writeByte(TDSType.NUMERICN.byteValue());
                    this.writeByte((byte)17);
                    this.writeByte((byte)pair.getValue().precision);
                    this.writeByte((byte)pair.getValue().scale);
                    break;
                }
                case DOUBLE: {
                    this.writeByte(TDSType.FLOATN.byteValue());
                    this.writeByte((byte)8);
                    break;
                }
                case FLOAT:
                case REAL: {
                    this.writeByte(TDSType.FLOATN.byteValue());
                    this.writeByte((byte)4);
                    break;
                }
                case DATE:
                case TIME:
                case TIMESTAMP:
                case DATETIMEOFFSET:
                case DATETIME:
                case SMALLDATETIME:
                case TIMESTAMP_WITH_TIMEZONE:
                case TIME_WITH_TIMEZONE:
                case CHAR:
                case VARCHAR:
                case NCHAR:
                case NVARCHAR:
                case LONGVARCHAR:
                case LONGNVARCHAR:
                case SQLXML: {
                    this.writeByte(TDSType.NVARCHAR.byteValue());
                    final boolean isShortValue = 2L * pair.getValue().precision <= 8000L;
                    if (!isShortValue) {
                        this.writeShort((short)(-1));
                        this.con.getDatabaseCollation().writeCollation(this);
                        break;
                    }
                    this.writeShort((short)8000);
                    this.con.getDatabaseCollation().writeCollation(this);
                    break;
                }
                case BINARY:
                case VARBINARY:
                case LONGVARBINARY: {
                    this.writeByte(TDSType.BIGVARBINARY.byteValue());
                    final boolean isShortValue = pair.getValue().precision <= 8000;
                    if (!isShortValue) {
                        this.writeShort((short)(-1));
                        break;
                    }
                    this.writeShort((short)8000);
                    break;
                }
                case SQL_VARIANT: {
                    this.writeByte(TDSType.SQL_VARIANT.byteValue());
                    this.writeInt(8009);
                    break;
                }
                default: {
                    assert false : "Unexpected JDBC type " + jdbcType.toString();
                    break;
                }
            }
            this.writeByte((byte)0);
        }
    }
    
    void writeTvpOrderUnique(final TVP value) throws SQLServerException {
        final Map<Integer, SQLServerMetaData> columnMetadata = value.getColumnMetadata();
        final Iterator<Map.Entry<Integer, SQLServerMetaData>> columnsIterator = columnMetadata.entrySet().iterator();
        final LinkedList<TdsOrderUnique> columnList = new LinkedList<TdsOrderUnique>();
        while (columnsIterator.hasNext()) {
            byte flags = 0;
            final Map.Entry<Integer, SQLServerMetaData> pair = columnsIterator.next();
            final SQLServerMetaData metaData = pair.getValue();
            if (SQLServerSortOrder.Ascending == metaData.sortOrder) {
                flags = 1;
            }
            else if (SQLServerSortOrder.Descending == metaData.sortOrder) {
                flags = 2;
            }
            if (metaData.isUniqueKey) {
                flags |= 0x4;
            }
            if (0 != flags) {
                columnList.add(new TdsOrderUnique(pair.getKey(), flags));
            }
        }
        if (!columnList.isEmpty()) {
            this.writeByte((byte)16);
            this.writeShort((short)columnList.size());
            for (final TdsOrderUnique column : columnList) {
                this.writeShort((short)(column.columnOrdinal + 1));
                this.writeByte(column.flags);
            }
        }
    }
    
    void setCryptoMetaData(final CryptoMetadata cryptoMetaForBulk) {
        this.cryptoMeta = cryptoMetaForBulk;
    }
    
    CryptoMetadata getCryptoMetaData() {
        return this.cryptoMeta;
    }
    
    void writeEncryptedRPCByteArray(final byte[] bValue) throws SQLServerException {
        final boolean bValueNull = bValue == null;
        final long nValueLen = bValueNull ? 0L : bValue.length;
        final boolean isShortValue = nValueLen <= 8000L;
        final boolean isPLP = !isShortValue && nValueLen <= 2147483647L;
        if (isShortValue) {
            this.writeShort((short)8000);
        }
        else if (isPLP) {
            this.writeShort((short)(-1));
        }
        else {
            this.writeInt(Integer.MAX_VALUE);
        }
        if (bValueNull) {
            this.writeShort((short)(-1));
        }
        else {
            if (isShortValue) {
                this.writeShort((short)nValueLen);
            }
            else if (isPLP) {
                this.writeLong(nValueLen);
            }
            else {
                this.writeInt((int)nValueLen);
            }
            if (0L != nValueLen) {
                if (isPLP) {
                    this.writeInt((int)nValueLen);
                }
                this.writeBytes(bValue);
            }
            if (isPLP) {
                this.writeInt(0);
            }
        }
    }
    
    void writeEncryptedRPCPLP() throws SQLServerException {
        this.writeShort((short)(-1));
        this.writeLong(0L);
        this.writeInt(0);
    }
    
    void writeCryptoMetaData() throws SQLServerException {
        this.writeByte(this.cryptoMeta.cipherAlgorithmId);
        this.writeByte(this.cryptoMeta.encryptionType.getValue());
        this.writeInt(this.cryptoMeta.cekTableEntry.getColumnEncryptionKeyValues().get(0).databaseId);
        this.writeInt(this.cryptoMeta.cekTableEntry.getColumnEncryptionKeyValues().get(0).cekId);
        this.writeInt(this.cryptoMeta.cekTableEntry.getColumnEncryptionKeyValues().get(0).cekVersion);
        this.writeBytes(this.cryptoMeta.cekTableEntry.getColumnEncryptionKeyValues().get(0).cekMdVersion);
        this.writeByte(this.cryptoMeta.normalizationRuleVersion);
    }
    
    void writeRPCByteArray(final String sName, final byte[] bValue, final boolean bOut, final JDBCType jdbcType, SQLCollation collation) throws SQLServerException {
        final boolean bValueNull = bValue == null;
        final int nValueLen = bValueNull ? 0 : bValue.length;
        final boolean isShortValue = nValueLen <= 8000;
        final boolean usePLP = !isShortValue || bOut;
        TDSType tdsType = null;
        if (null != this.cryptoMeta) {
            tdsType = ((isShortValue || usePLP) ? TDSType.BIGVARBINARY : TDSType.IMAGE);
            collation = null;
        }
        else {
            switch (jdbcType) {
                default: {
                    tdsType = ((isShortValue || usePLP) ? TDSType.BIGVARBINARY : TDSType.IMAGE);
                    collation = null;
                    break;
                }
                case CHAR:
                case VARCHAR:
                case LONGVARCHAR:
                case CLOB: {
                    tdsType = ((isShortValue || usePLP) ? TDSType.BIGVARCHAR : TDSType.TEXT);
                    if (null == collation) {
                        collation = this.con.getDatabaseCollation();
                        break;
                    }
                    break;
                }
                case NCHAR:
                case NVARCHAR:
                case LONGNVARCHAR:
                case NCLOB: {
                    tdsType = ((isShortValue || usePLP) ? TDSType.NVARCHAR : TDSType.NTEXT);
                    if (null == collation) {
                        collation = this.con.getDatabaseCollation();
                        break;
                    }
                    break;
                }
            }
        }
        this.writeRPCNameValType(sName, bOut, tdsType);
        if (usePLP) {
            this.writeVMaxHeader(nValueLen, bValueNull, collation);
            if (!bValueNull) {
                if (nValueLen > 0) {
                    this.writeInt(nValueLen);
                    this.writeBytes(bValue);
                }
                this.writeInt(0);
            }
        }
        else {
            if (isShortValue) {
                this.writeShort((short)8000);
            }
            else {
                this.writeInt(Integer.MAX_VALUE);
            }
            if (null != collation) {
                collation.writeCollation(this);
            }
            if (bValueNull) {
                this.writeShort((short)(-1));
            }
            else {
                if (isShortValue) {
                    this.writeShort((short)nValueLen);
                }
                else {
                    this.writeInt(nValueLen);
                }
                if (0 != nValueLen) {
                    this.writeBytes(bValue);
                }
            }
        }
    }
    
    void writeRPCDateTime(final String sName, final GregorianCalendar cal, final int subSecondNanos, final boolean bOut) throws SQLServerException {
        assert subSecondNanos >= 0 && subSecondNanos < 1000000000 : "Invalid subNanoSeconds value: " + subSecondNanos;
        assert subSecondNanos == 0 : "Invalid subNanoSeconds value when calendar is null: " + subSecondNanos;
        this.writeRPCNameValType(sName, bOut, TDSType.DATETIMEN);
        this.writeByte((byte)8);
        if (null == cal) {
            this.writeByte((byte)0);
            return;
        }
        this.writeByte((byte)8);
        int daysSinceSQLBaseDate = DDC.daysSinceBaseDate(cal.get(1), cal.get(6), 1900);
        int millisSinceMidnight = (subSecondNanos + 500000) / 1000000 + 1000 * cal.get(13) + 60000 * cal.get(12) + 3600000 * cal.get(11);
        if (millisSinceMidnight >= 86399999) {
            ++daysSinceSQLBaseDate;
            millisSinceMidnight = 0;
        }
        if (daysSinceSQLBaseDate < DDC.daysSinceBaseDate(1753, 1, 1900) || daysSinceSQLBaseDate >= DDC.daysSinceBaseDate(10000, 1, 1900)) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
            final Object[] msgArgs = { SSType.DATETIME };
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
        }
        this.writeInt(daysSinceSQLBaseDate);
        this.writeInt((3 * millisSinceMidnight + 5) / 10);
    }
    
    void writeRPCTime(final String sName, final GregorianCalendar localCalendar, final int subSecondNanos, final int scale, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.TIMEN);
        this.writeByte((byte)scale);
        if (null == localCalendar) {
            this.writeByte((byte)0);
            return;
        }
        this.writeByte((byte)TDS.timeValueLength(scale));
        this.writeScaledTemporal(localCalendar, subSecondNanos, scale, SSType.TIME);
    }
    
    void writeRPCDate(final String sName, final GregorianCalendar localCalendar, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.DATEN);
        if (null == localCalendar) {
            this.writeByte((byte)0);
            return;
        }
        this.writeByte((byte)3);
        this.writeScaledTemporal(localCalendar, 0, 0, SSType.DATE);
    }
    
    void writeEncryptedRPCTime(final String sName, final GregorianCalendar localCalendar, final int subSecondNanos, final int scale, final boolean bOut) throws SQLServerException {
        if (this.con.getSendTimeAsDatetime()) {
            throw new SQLServerException(SQLServerException.getErrString("R_sendTimeAsDateTimeForAE"), (Throwable)null);
        }
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == localCalendar) {
            this.writeEncryptedRPCByteArray(null);
        }
        else {
            this.writeEncryptedRPCByteArray(this.writeEncryptedScaledTemporal(localCalendar, subSecondNanos, scale, SSType.TIME, (short)0));
        }
        this.writeByte(TDSType.TIMEN.byteValue());
        this.writeByte((byte)scale);
        this.writeCryptoMetaData();
    }
    
    void writeEncryptedRPCDate(final String sName, final GregorianCalendar localCalendar, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == localCalendar) {
            this.writeEncryptedRPCByteArray(null);
        }
        else {
            this.writeEncryptedRPCByteArray(this.writeEncryptedScaledTemporal(localCalendar, 0, 0, SSType.DATE, (short)0));
        }
        this.writeByte(TDSType.DATEN.byteValue());
        this.writeCryptoMetaData();
    }
    
    void writeEncryptedRPCDateTime(final String sName, final GregorianCalendar cal, final int subSecondNanos, final boolean bOut, final JDBCType jdbcType) throws SQLServerException {
        assert subSecondNanos >= 0 && subSecondNanos < 1000000000 : "Invalid subNanoSeconds value: " + subSecondNanos;
        assert subSecondNanos == 0 : "Invalid subNanoSeconds value when calendar is null: " + subSecondNanos;
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == cal) {
            this.writeEncryptedRPCByteArray(null);
        }
        else {
            this.writeEncryptedRPCByteArray(this.getEncryptedDateTimeAsBytes(cal, subSecondNanos, jdbcType));
        }
        if (JDBCType.SMALLDATETIME == jdbcType) {
            this.writeByte(TDSType.DATETIMEN.byteValue());
            this.writeByte((byte)4);
        }
        else {
            this.writeByte(TDSType.DATETIMEN.byteValue());
            this.writeByte((byte)8);
        }
        this.writeCryptoMetaData();
    }
    
    byte[] getEncryptedDateTimeAsBytes(final GregorianCalendar cal, final int subSecondNanos, final JDBCType jdbcType) throws SQLServerException {
        int daysSinceSQLBaseDate = DDC.daysSinceBaseDate(cal.get(1), cal.get(6), 1900);
        int millisSinceMidnight = (subSecondNanos + 500000) / 1000000 + 1000 * cal.get(13) + 60000 * cal.get(12) + 3600000 * cal.get(11);
        if (millisSinceMidnight >= 86399999) {
            ++daysSinceSQLBaseDate;
            millisSinceMidnight = 0;
        }
        if (JDBCType.SMALLDATETIME == jdbcType) {
            final int secondsSinceMidnight = millisSinceMidnight / 1000;
            int minutesSinceMidnight = secondsSinceMidnight / 60;
            minutesSinceMidnight = ((secondsSinceMidnight % 60 > 29.998) ? (minutesSinceMidnight + 1) : minutesSinceMidnight);
            final int maxMinutesSinceMidnight_SmallDateTime = 1440;
            if (daysSinceSQLBaseDate < DDC.daysSinceBaseDate(1900, 1, 1900) || daysSinceSQLBaseDate > DDC.daysSinceBaseDate(2079, 157, 1900) || (daysSinceSQLBaseDate == DDC.daysSinceBaseDate(2079, 157, 1900) && minutesSinceMidnight >= maxMinutesSinceMidnight_SmallDateTime)) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                final Object[] msgArgs = { SSType.SMALLDATETIME };
                throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
            }
            final ByteBuffer days = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            days.putShort((short)daysSinceSQLBaseDate);
            final ByteBuffer seconds = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            seconds.putShort((short)minutesSinceMidnight);
            final byte[] value = new byte[4];
            System.arraycopy(days.array(), 0, value, 0, 2);
            System.arraycopy(seconds.array(), 0, value, 2, 2);
            return SQLServerSecurityUtility.encryptWithKey(value, this.cryptoMeta, this.con);
        }
        else if (JDBCType.DATETIME == jdbcType) {
            if (daysSinceSQLBaseDate < DDC.daysSinceBaseDate(1753, 1, 1900) || daysSinceSQLBaseDate >= DDC.daysSinceBaseDate(10000, 1, 1900)) {
                final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                final Object[] msgArgs2 = { SSType.DATETIME };
                throw new SQLServerException(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
            }
            final ByteBuffer days2 = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            days2.putInt(daysSinceSQLBaseDate);
            final ByteBuffer seconds2 = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            seconds2.putInt((3 * millisSinceMidnight + 5) / 10);
            final byte[] value2 = new byte[8];
            System.arraycopy(days2.array(), 0, value2, 0, 4);
            System.arraycopy(seconds2.array(), 0, value2, 4, 4);
            return SQLServerSecurityUtility.encryptWithKey(value2, this.cryptoMeta, this.con);
        }
        else {
            assert false : "Unexpected JDBCType type " + jdbcType;
            return null;
        }
    }
    
    void writeEncryptedRPCDateTime2(final String sName, final GregorianCalendar localCalendar, final int subSecondNanos, final int scale, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == localCalendar) {
            this.writeEncryptedRPCByteArray(null);
        }
        else {
            this.writeEncryptedRPCByteArray(this.writeEncryptedScaledTemporal(localCalendar, subSecondNanos, scale, SSType.DATETIME2, (short)0));
        }
        this.writeByte(TDSType.DATETIME2N.byteValue());
        this.writeByte((byte)scale);
        this.writeCryptoMetaData();
    }
    
    void writeEncryptedRPCDateTimeOffset(final String sName, final GregorianCalendar utcCalendar, final int minutesOffset, final int subSecondNanos, final int scale, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == utcCalendar) {
            this.writeEncryptedRPCByteArray(null);
        }
        else {
            assert 0 == utcCalendar.get(15);
            this.writeEncryptedRPCByteArray(this.writeEncryptedScaledTemporal(utcCalendar, subSecondNanos, scale, SSType.DATETIMEOFFSET, (short)minutesOffset));
        }
        this.writeByte(TDSType.DATETIMEOFFSETN.byteValue());
        this.writeByte((byte)scale);
        this.writeCryptoMetaData();
    }
    
    void writeRPCDateTime2(final String sName, final GregorianCalendar localCalendar, final int subSecondNanos, final int scale, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.DATETIME2N);
        this.writeByte((byte)scale);
        if (null == localCalendar) {
            this.writeByte((byte)0);
            return;
        }
        this.writeByte((byte)TDS.datetime2ValueLength(scale));
        this.writeScaledTemporal(localCalendar, subSecondNanos, scale, SSType.DATETIME2);
    }
    
    void writeRPCDateTimeOffset(final String sName, final GregorianCalendar utcCalendar, final int minutesOffset, final int subSecondNanos, final int scale, final boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.DATETIMEOFFSETN);
        this.writeByte((byte)scale);
        if (null == utcCalendar) {
            this.writeByte((byte)0);
            return;
        }
        assert 0 == utcCalendar.get(15);
        this.writeByte((byte)TDS.datetimeoffsetValueLength(scale));
        this.writeScaledTemporal(utcCalendar, subSecondNanos, scale, SSType.DATETIMEOFFSET);
        this.writeShort((short)minutesOffset);
    }
    
    private int getRoundedSubSecondNanos(final int subSecondNanos) {
        final int roundedNanos = (subSecondNanos + Nanos.PER_MAX_SCALE_INTERVAL / 2) / Nanos.PER_MAX_SCALE_INTERVAL * Nanos.PER_MAX_SCALE_INTERVAL;
        return roundedNanos;
    }
    
    private void writeScaledTemporal(final GregorianCalendar cal, final int subSecondNanos, final int scale, final SSType ssType) throws SQLServerException {
        assert this.con.isKatmaiOrLater();
        assert SSType.DATETIMEOFFSET == ssType : "Unexpected SSType: " + ssType;
        if (SSType.TIME == ssType || SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) {
            assert subSecondNanos >= 0;
            assert subSecondNanos < 1000000000;
            assert scale >= 0;
            assert scale <= 7;
            final int secondsSinceMidnight = cal.get(13) + 60 * cal.get(12) + 3600 * cal.get(11);
            final long divisor = Nanos.PER_MAX_SCALE_INTERVAL * (long)Math.pow(10.0, 7 - scale);
            long scaledNanos = (1000000000L * secondsSinceMidnight + this.getRoundedSubSecondNanos(subSecondNanos) + divisor / 2L) / divisor;
            if (86400000000000L / divisor == scaledNanos) {
                if (SSType.TIME == ssType) {
                    --scaledNanos;
                }
                else {
                    assert SSType.DATETIMEOFFSET == ssType : "Unexpected SSType: " + ssType;
                    cal.add(13, 1);
                    if (cal.get(1) <= 9999) {
                        scaledNanos = 0L;
                    }
                    else {
                        cal.add(13, -1);
                        --scaledNanos;
                    }
                }
            }
            final int encodedLength = TDS.nanosSinceMidnightLength(scale);
            final byte[] encodedBytes = this.scaledNanosToEncodedBytes(scaledNanos, encodedLength);
            this.writeBytes(encodedBytes);
        }
        if (SSType.DATE == ssType || SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) {
            if (cal.getTimeInMillis() < GregorianChange.STANDARD_CHANGE_DATE.getTime() || cal.getActualMaximum(6) < 365) {
                final int year = cal.get(1);
                final int month = cal.get(2);
                final int date = cal.get(5);
                cal.setGregorianChange(GregorianChange.PURE_CHANGE_DATE);
                cal.set(year, month, date);
            }
            final int daysIntoCE = DDC.daysSinceBaseDate(cal.get(1), cal.get(6), 1);
            if (daysIntoCE < 0 || daysIntoCE >= DDC.daysSinceBaseDate(10000, 1, 1)) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                final Object[] msgArgs = { ssType };
                throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
            }
            final byte[] encodedBytes2 = { (byte)(daysIntoCE >> 0 & 0xFF), (byte)(daysIntoCE >> 8 & 0xFF), (byte)(daysIntoCE >> 16 & 0xFF) };
            this.writeBytes(encodedBytes2);
        }
    }
    
    byte[] writeEncryptedScaledTemporal(final GregorianCalendar cal, final int subSecondNanos, final int scale, final SSType ssType, final short minutesOffset) throws SQLServerException {
        assert this.con.isKatmaiOrLater();
        assert SSType.DATETIMEOFFSET == ssType : "Unexpected SSType: " + ssType;
        byte[] encodedBytesForEncryption = null;
        int secondsSinceMidnight = 0;
        long divisor = 0L;
        long scaledNanos = 0L;
        if (SSType.TIME == ssType || SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) {
            assert subSecondNanos >= 0;
            assert subSecondNanos < 1000000000;
            assert scale >= 0;
            assert scale <= 7;
            secondsSinceMidnight = cal.get(13) + 60 * cal.get(12) + 3600 * cal.get(11);
            divisor = Nanos.PER_MAX_SCALE_INTERVAL * (long)Math.pow(10.0, 7 - scale);
            scaledNanos = (1000000000L * secondsSinceMidnight + this.getRoundedSubSecondNanos(subSecondNanos) + divisor / 2L) / divisor * divisor / 100L;
            if (SSType.TIME == ssType && 864000000000L <= scaledNanos) {
                scaledNanos = (1000000000L * secondsSinceMidnight + this.getRoundedSubSecondNanos(subSecondNanos)) / divisor * divisor / 100L;
            }
            if (86400000000000L / divisor == scaledNanos) {
                if (SSType.TIME == ssType) {
                    --scaledNanos;
                }
                else {
                    assert SSType.DATETIMEOFFSET == ssType : "Unexpected SSType: " + ssType;
                    cal.add(13, 1);
                    if (cal.get(1) <= 9999) {
                        scaledNanos = 0L;
                    }
                    else {
                        cal.add(13, -1);
                        --scaledNanos;
                    }
                }
            }
            final int encodedLength = TDS.nanosSinceMidnightLength(7);
            final byte[] encodedBytes = this.scaledNanosToEncodedBytes(scaledNanos, encodedLength);
            if (SSType.TIME == ssType) {
                final byte[] cipherText = SQLServerSecurityUtility.encryptWithKey(encodedBytes, this.cryptoMeta, this.con);
                return cipherText;
            }
            if (SSType.DATETIME2 == ssType) {
                encodedBytesForEncryption = new byte[encodedLength + 3];
                System.arraycopy(encodedBytes, 0, encodedBytesForEncryption, 0, encodedBytes.length);
            }
            else if (SSType.DATETIMEOFFSET == ssType) {
                encodedBytesForEncryption = new byte[encodedLength + 5];
                System.arraycopy(encodedBytes, 0, encodedBytesForEncryption, 0, encodedBytes.length);
            }
        }
        if (SSType.DATE != ssType && SSType.DATETIME2 != ssType && SSType.DATETIMEOFFSET != ssType) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unknownSSType"));
            final Object[] msgArgs = { ssType };
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
            return null;
        }
        if (cal.getTimeInMillis() < GregorianChange.STANDARD_CHANGE_DATE.getTime() || cal.getActualMaximum(6) < 365) {
            final int year = cal.get(1);
            final int month = cal.get(2);
            final int date = cal.get(5);
            cal.setGregorianChange(GregorianChange.PURE_CHANGE_DATE);
            cal.set(year, month, date);
        }
        final int daysIntoCE = DDC.daysSinceBaseDate(cal.get(1), cal.get(6), 1);
        if (daysIntoCE < 0 || daysIntoCE >= DDC.daysSinceBaseDate(10000, 1, 1)) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
            final Object[] msgArgs2 = { ssType };
            throw new SQLServerException(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
        }
        final byte[] encodedBytes = { (byte)(daysIntoCE >> 0 & 0xFF), (byte)(daysIntoCE >> 8 & 0xFF), (byte)(daysIntoCE >> 16 & 0xFF) };
        byte[] cipherText;
        if (SSType.DATE == ssType) {
            cipherText = SQLServerSecurityUtility.encryptWithKey(encodedBytes, this.cryptoMeta, this.con);
        }
        else if (SSType.DATETIME2 == ssType) {
            if (3652058 == daysIntoCE && 864000000000L == scaledNanos) {
                scaledNanos = (1000000000L * secondsSinceMidnight + this.getRoundedSubSecondNanos(subSecondNanos)) / divisor * divisor / 100L;
                final int encodedLength2 = TDS.nanosSinceMidnightLength(7);
                final byte[] encodedNanoBytes = this.scaledNanosToEncodedBytes(scaledNanos, encodedLength2);
                encodedBytesForEncryption = new byte[encodedLength2 + 3];
                System.arraycopy(encodedNanoBytes, 0, encodedBytesForEncryption, 0, encodedNanoBytes.length);
            }
            System.arraycopy(encodedBytes, 0, encodedBytesForEncryption, encodedBytesForEncryption.length - 3, 3);
            cipherText = SQLServerSecurityUtility.encryptWithKey(encodedBytesForEncryption, this.cryptoMeta, this.con);
        }
        else {
            if (3652058 == daysIntoCE && 864000000000L == scaledNanos) {
                scaledNanos = (1000000000L * secondsSinceMidnight + this.getRoundedSubSecondNanos(subSecondNanos)) / divisor * divisor / 100L;
                final int encodedLength2 = TDS.nanosSinceMidnightLength(7);
                final byte[] encodedNanoBytes = this.scaledNanosToEncodedBytes(scaledNanos, encodedLength2);
                encodedBytesForEncryption = new byte[encodedLength2 + 5];
                System.arraycopy(encodedNanoBytes, 0, encodedBytesForEncryption, 0, encodedNanoBytes.length);
            }
            System.arraycopy(encodedBytes, 0, encodedBytesForEncryption, encodedBytesForEncryption.length - 5, 3);
            System.arraycopy(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(minutesOffset).array(), 0, encodedBytesForEncryption, encodedBytesForEncryption.length - 2, 2);
            cipherText = SQLServerSecurityUtility.encryptWithKey(encodedBytesForEncryption, this.cryptoMeta, this.con);
        }
        return cipherText;
    }
    
    private byte[] scaledNanosToEncodedBytes(final long scaledNanos, final int encodedLength) {
        final byte[] encodedBytes = new byte[encodedLength];
        for (int i = 0; i < encodedLength; ++i) {
            encodedBytes[i] = (byte)(scaledNanos >> 8 * i & 0xFFL);
        }
        return encodedBytes;
    }
    
    void writeRPCInputStream(final String sName, InputStream stream, long streamLength, final boolean bOut, final JDBCType jdbcType, final SQLCollation collation) throws SQLServerException {
        assert null != stream;
        assert streamLength >= 0L;
        final boolean usePLP = -1L == streamLength || streamLength > 8000L;
        if (usePLP) {
            assert streamLength <= 2147483647L;
            this.writeRPCNameValType(sName, bOut, jdbcType.isTextual() ? TDSType.BIGVARCHAR : TDSType.BIGVARBINARY);
            this.writeVMaxHeader(streamLength, false, jdbcType.isTextual() ? collation : null);
        }
        else {
            if (-1L == streamLength) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream(8000);
                streamLength = 0L;
                final long maxStreamLength = 65535L * this.con.getTDSPacketSize();
                try {
                    int bytesRead;
                    for (byte[] buff = new byte[8000]; streamLength < maxStreamLength && -1 != (bytesRead = stream.read(buff, 0, buff.length)); streamLength += bytesRead) {
                        baos.write(buff);
                    }
                }
                catch (final IOException e) {
                    throw new SQLServerException(e.getMessage(), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, e);
                }
                if (streamLength >= maxStreamLength) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
                    final Object[] msgArgs = { streamLength };
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                }
                assert streamLength <= 2147483647L;
                stream = new ByteArrayInputStream(baos.toByteArray(), 0, (int)streamLength);
            }
            assert 0L <= streamLength && streamLength <= 2147483647L;
            final boolean useVarType = streamLength <= 8000L;
            this.writeRPCNameValType(sName, bOut, jdbcType.isTextual() ? (useVarType ? TDSType.BIGVARCHAR : TDSType.TEXT) : (useVarType ? TDSType.BIGVARBINARY : TDSType.IMAGE));
            if (useVarType) {
                this.writeShort((short)8000);
                if (jdbcType.isTextual()) {
                    collation.writeCollation(this);
                }
                this.writeShort((short)streamLength);
            }
            else {
                this.writeInt(Integer.MAX_VALUE);
                if (jdbcType.isTextual()) {
                    collation.writeCollation(this);
                }
                this.writeInt((int)streamLength);
            }
        }
        this.writeStream(stream, streamLength, usePLP);
    }
    
    void writeRPCXML(final String sName, final InputStream stream, final long streamLength, final boolean bOut) throws SQLServerException {
        assert streamLength >= 0L;
        assert streamLength <= 2147483647L;
        this.writeRPCNameValType(sName, bOut, TDSType.XML);
        this.writeByte((byte)0);
        if (null == stream) {
            this.writeLong(-1L);
        }
        else if (-1L == streamLength) {
            this.writeLong(-2L);
        }
        else {
            this.writeLong(streamLength);
        }
        if (null != stream) {
            this.writeStream(stream, streamLength, true);
        }
    }
    
    void writeRPCReaderUnicode(final String sName, final Reader re, final long reLength, final boolean bOut, SQLCollation collation) throws SQLServerException {
        assert null != re;
        assert reLength >= 0L;
        if (null == collation) {
            collation = this.con.getDatabaseCollation();
        }
        final boolean usePLP = -1L == reLength || reLength > 4000L;
        if (usePLP) {
            assert reLength <= 1073741823L;
            this.writeRPCNameValType(sName, bOut, TDSType.NVARCHAR);
            this.writeVMaxHeader((-1L == reLength) ? -1L : (2L * reLength), false, collation);
        }
        else {
            assert 0L <= reLength && reLength <= 1073741823L;
            final boolean useVarType = reLength <= 4000L;
            this.writeRPCNameValType(sName, bOut, useVarType ? TDSType.NVARCHAR : TDSType.NTEXT);
            if (useVarType) {
                this.writeShort((short)8000);
                collation.writeCollation(this);
                this.writeShort((short)(2L * reLength));
            }
            else {
                this.writeInt(1073741823);
                collation.writeCollation(this);
                this.writeInt((int)(2L * reLength));
            }
        }
        this.writeReader(re, reLength, usePLP);
    }
    
    void sendEnclavePackage(final String sql, final ArrayList<byte[]> enclaveCEKs) throws SQLServerException {
        if (null != this.con && this.con.isAEv2()) {
            if (null != sql && !sql.isEmpty() && null != enclaveCEKs && 0 < enclaveCEKs.size() && this.con.enclaveEstablished()) {
                final byte[] b = this.con.generateEnclavePackage(sql, enclaveCEKs);
                if (null != b && 0 != b.length) {
                    this.writeShort((short)b.length);
                    this.writeBytes(b);
                }
                else {
                    this.writeShort((short)0);
                }
            }
            else {
                this.writeShort((short)0);
            }
        }
    }
    
    static {
        TDSWriter.logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.Writer");
        placeholderHeader = new byte[8];
    }
    
    private class TdsOrderUnique
    {
        int columnOrdinal;
        byte flags;
        
        TdsOrderUnique(final int ordinal, final byte flags) {
            this.columnOrdinal = ordinal;
            this.flags = flags;
        }
    }
}
