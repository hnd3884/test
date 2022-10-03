package com.microsoft.sqlserver.jdbc;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.Calendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicInteger;
import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityClassification;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
import java.io.Serializable;

final class TDSReader implements Serializable
{
    private static final long serialVersionUID = -392905303734809731L;
    private static final Logger logger;
    private final String traceID;
    private ScheduledFuture<?> timeout;
    private final TDSChannel tdsChannel;
    private final SQLServerConnection con;
    private final TDSCommand command;
    private TDSPacket currentPacket;
    private TDSPacket lastPacket;
    private int payloadOffset;
    private int packetNum;
    private boolean isStreaming;
    private boolean useColumnEncryption;
    private boolean serverSupportsColumnEncryption;
    private boolean serverSupportsDataClassification;
    private ColumnEncryptionVersion columnEncryptionVersion;
    private final byte[] valueBytes;
    protected SensitivityClassification sensitivityClassification;
    private static final AtomicInteger lastReaderID;
    private static final int[] SCALED_MULTIPLIERS;
    static final String guidTemplate = "NNNNNNNN-NNNN-NNNN-NNNN-NNNNNNNNNNNN";
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    final TDSCommand getCommand() {
        assert null != this.command;
        return this.command;
    }
    
    final SQLServerConnection getConnection() {
        return this.con;
    }
    
    private static int nextReaderID() {
        return TDSReader.lastReaderID.incrementAndGet();
    }
    
    TDSReader(final TDSChannel tdsChannel, final SQLServerConnection con, final TDSCommand command) {
        this.currentPacket = new TDSPacket(0);
        this.lastPacket = this.currentPacket;
        this.payloadOffset = 0;
        this.packetNum = 0;
        this.isStreaming = true;
        this.useColumnEncryption = false;
        this.serverSupportsColumnEncryption = false;
        this.serverSupportsDataClassification = false;
        this.valueBytes = new byte[256];
        this.tdsChannel = tdsChannel;
        this.con = con;
        this.command = command;
        if (TDSReader.logger.isLoggable(Level.FINE)) {
            this.traceID = "TDSReader@" + nextReaderID() + " (" + con.toString() + ")";
        }
        else {
            this.traceID = con.toString();
        }
        if (con.isColumnEncryptionSettingEnabled()) {
            this.useColumnEncryption = true;
        }
        this.serverSupportsColumnEncryption = con.getServerSupportsColumnEncryption();
        this.columnEncryptionVersion = con.getServerColumnEncryptionVersion();
        this.serverSupportsDataClassification = con.getServerSupportsDataClassification();
    }
    
    final boolean isColumnEncryptionSettingEnabled() {
        return this.useColumnEncryption;
    }
    
    final boolean getServerSupportsColumnEncryption() {
        return this.serverSupportsColumnEncryption;
    }
    
    final boolean getServerSupportsDataClassification() {
        return this.serverSupportsDataClassification;
    }
    
    final void throwInvalidTDS() throws SQLServerException {
        if (TDSReader.logger.isLoggable(Level.SEVERE)) {
            TDSReader.logger.severe(this.toString() + " got unexpected value in TDS response at offset:" + this.payloadOffset);
        }
        this.con.throwInvalidTDS();
    }
    
    final void throwInvalidTDSToken(final String tokenName) throws SQLServerException {
        if (TDSReader.logger.isLoggable(Level.SEVERE)) {
            TDSReader.logger.severe(this.toString() + " got unexpected value in TDS response at offset:" + this.payloadOffset);
        }
        this.con.throwInvalidTDSToken(tokenName);
    }
    
    private boolean ensurePayload() throws SQLServerException {
        if (this.payloadOffset == this.currentPacket.payloadLength && !this.nextPacket()) {
            return false;
        }
        assert this.payloadOffset < this.currentPacket.payloadLength;
        return true;
    }
    
    private boolean nextPacket() throws SQLServerException {
        assert null != this.currentPacket;
        final TDSPacket consumedPacket = this.currentPacket;
        assert this.payloadOffset == consumedPacket.payloadLength;
        if (null == consumedPacket.next) {
            this.readPacket();
            if (null == consumedPacket.next) {
                return false;
            }
        }
        final TDSPacket nextPacket = consumedPacket.next;
        if (this.isStreaming) {
            if (TDSReader.logger.isLoggable(Level.FINEST)) {
                TDSReader.logger.finest(this.toString() + " Moving to next packet -- unlinking consumed packet");
            }
            consumedPacket.next = null;
        }
        this.currentPacket = nextPacket;
        this.payloadOffset = 0;
        return true;
    }
    
    final synchronized boolean readPacket() throws SQLServerException {
        if (null != this.command && !this.command.readingResponse()) {
            return false;
        }
        assert this.tdsChannel.numMsgsRcvd < this.tdsChannel.numMsgsSent : "numMsgsRcvd:" + this.tdsChannel.numMsgsRcvd + " should be less than numMsgsSent:" + this.tdsChannel.numMsgsSent;
        final TDSPacket newPacket = new TDSPacket(this.con.getTDSPacketSize());
        if (null != this.command && this.command.getCancelQueryTimeoutSeconds() > 0 && this.command.getQueryTimeoutSeconds() > 0) {
            final int seconds = this.command.getCancelQueryTimeoutSeconds() + this.command.getQueryTimeoutSeconds();
            this.timeout = this.con.getSharedTimer().schedule(new TDSTimeoutTask(this.command, this.con), seconds);
        }
        int bytesRead;
        for (int headerBytesRead = 0; headerBytesRead < 8; headerBytesRead += bytesRead) {
            bytesRead = this.tdsChannel.read(newPacket.header, headerBytesRead, 8 - headerBytesRead);
            if (bytesRead < 0) {
                if (TDSReader.logger.isLoggable(Level.FINER)) {
                    TDSReader.logger.finer(this.toString() + " Premature EOS in response. packetNum:" + this.packetNum + " headerBytesRead:" + headerBytesRead);
                }
                this.con.terminate(3, (0 == this.packetNum && 0 == headerBytesRead) ? SQLServerException.getErrString("R_noServerResponse") : SQLServerException.getErrString("R_truncatedServerResponse"));
            }
        }
        if (this.timeout != null) {
            this.timeout.cancel(false);
            this.timeout = null;
        }
        final int packetLength = Util.readUnsignedShortBigEndian(newPacket.header, 2);
        if (packetLength < 8 || packetLength > this.con.getTDSPacketSize()) {
            if (TDSReader.logger.isLoggable(Level.WARNING)) {
                TDSReader.logger.warning(this.toString() + " TDS header contained invalid packet length:" + packetLength + "; packet size:" + this.con.getTDSPacketSize());
            }
            this.throwInvalidTDS();
        }
        newPacket.payloadLength = packetLength - 8;
        this.tdsChannel.setSPID(Util.readUnsignedShortBigEndian(newPacket.header, 4));
        byte[] logBuffer = null;
        if (this.tdsChannel.isLoggingPackets()) {
            logBuffer = new byte[packetLength];
            System.arraycopy(newPacket.header, 0, logBuffer, 0, 8);
        }
        int bytesRead2;
        for (int payloadBytesRead = 0; payloadBytesRead < newPacket.payloadLength; payloadBytesRead += bytesRead2) {
            bytesRead2 = this.tdsChannel.read(newPacket.payload, payloadBytesRead, newPacket.payloadLength - payloadBytesRead);
            if (bytesRead2 < 0) {
                this.con.terminate(3, SQLServerException.getErrString("R_truncatedServerResponse"));
            }
        }
        ++this.packetNum;
        this.lastPacket.next = newPacket;
        this.lastPacket = newPacket;
        if (this.tdsChannel.isLoggingPackets()) {
            System.arraycopy(newPacket.payload, 0, logBuffer, 8, newPacket.payloadLength);
            this.tdsChannel.logPacket(logBuffer, 0, packetLength, this.toString() + " received Packet:" + this.packetNum + " (" + newPacket.payloadLength + " bytes)");
        }
        if (newPacket.isEOM()) {
            final TDSChannel tdsChannel = this.tdsChannel;
            ++tdsChannel.numMsgsRcvd;
            if (null != this.command) {
                this.command.onResponseEOM();
            }
        }
        return true;
    }
    
    final TDSReaderMark mark() {
        final TDSReaderMark mark = new TDSReaderMark(this.currentPacket, this.payloadOffset);
        this.isStreaming = false;
        if (TDSReader.logger.isLoggable(Level.FINEST)) {
            TDSReader.logger.finest(this.toString() + ": Buffering from: " + mark.toString());
        }
        return mark;
    }
    
    final void reset(final TDSReaderMark mark) {
        if (TDSReader.logger.isLoggable(Level.FINEST)) {
            TDSReader.logger.finest(this.toString() + ": Resetting to: " + mark.toString());
        }
        this.currentPacket = mark.packet;
        this.payloadOffset = mark.payloadOffset;
    }
    
    final void stream() {
        this.isStreaming = true;
    }
    
    final int available() {
        int available = this.currentPacket.payloadLength - this.payloadOffset;
        for (TDSPacket packet = this.currentPacket.next; null != packet; packet = packet.next) {
            available += packet.payloadLength;
        }
        return available;
    }
    
    final int availableCurrentPacket() {
        final int available = this.currentPacket.payloadLength - this.payloadOffset;
        return available;
    }
    
    final int peekTokenType() throws SQLServerException {
        if (!this.ensurePayload()) {
            return -1;
        }
        return this.currentPacket.payload[this.payloadOffset] & 0xFF;
    }
    
    final short peekStatusFlag() throws SQLServerException {
        if (this.payloadOffset + 3 <= this.currentPacket.payloadLength) {
            final short value = Util.readShort(this.currentPacket.payload, this.payloadOffset + 1);
            return value;
        }
        return 0;
    }
    
    final int readUnsignedByte() throws SQLServerException {
        if (!this.ensurePayload()) {
            this.throwInvalidTDS();
        }
        return this.currentPacket.payload[this.payloadOffset++] & 0xFF;
    }
    
    final short readShort() throws SQLServerException {
        if (this.payloadOffset + 2 <= this.currentPacket.payloadLength) {
            final short value = Util.readShort(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 2;
            return value;
        }
        return Util.readShort(this.readWrappedBytes(2), 0);
    }
    
    final int readUnsignedShort() throws SQLServerException {
        if (this.payloadOffset + 2 <= this.currentPacket.payloadLength) {
            final int value = Util.readUnsignedShort(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 2;
            return value;
        }
        return Util.readUnsignedShort(this.readWrappedBytes(2), 0);
    }
    
    final String readUnicodeString(final int length) throws SQLServerException {
        final int byteLength = 2 * length;
        final byte[] bytes = new byte[byteLength];
        this.readBytes(bytes, 0, byteLength);
        return Util.readUnicodeString(bytes, 0, byteLength, this.con);
    }
    
    final char readChar() throws SQLServerException {
        return (char)this.readShort();
    }
    
    final int readInt() throws SQLServerException {
        if (this.payloadOffset + 4 <= this.currentPacket.payloadLength) {
            final int value = Util.readInt(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 4;
            return value;
        }
        return Util.readInt(this.readWrappedBytes(4), 0);
    }
    
    final int readIntBigEndian() throws SQLServerException {
        if (this.payloadOffset + 4 <= this.currentPacket.payloadLength) {
            final int value = Util.readIntBigEndian(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 4;
            return value;
        }
        return Util.readIntBigEndian(this.readWrappedBytes(4), 0);
    }
    
    final long readUnsignedInt() throws SQLServerException {
        return (long)this.readInt() & 0xFFFFFFFFL;
    }
    
    final long readLong() throws SQLServerException {
        if (this.payloadOffset + 8 <= this.currentPacket.payloadLength) {
            final long value = Util.readLong(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 8;
            return value;
        }
        return Util.readLong(this.readWrappedBytes(8), 0);
    }
    
    final void readBytes(final byte[] value, final int valueOffset, final int valueLength) throws SQLServerException {
        int bytesToCopy;
        for (int bytesRead = 0; bytesRead < valueLength; bytesRead += bytesToCopy, this.payloadOffset += bytesToCopy) {
            if (!this.ensurePayload()) {
                this.throwInvalidTDS();
            }
            bytesToCopy = valueLength - bytesRead;
            if (bytesToCopy > this.currentPacket.payloadLength - this.payloadOffset) {
                bytesToCopy = this.currentPacket.payloadLength - this.payloadOffset;
            }
            if (TDSReader.logger.isLoggable(Level.FINEST)) {
                TDSReader.logger.finest(this.toString() + " Reading " + bytesToCopy + " bytes from offset " + this.payloadOffset);
            }
            System.arraycopy(this.currentPacket.payload, this.payloadOffset, value, valueOffset + bytesRead, bytesToCopy);
        }
    }
    
    final byte[] readWrappedBytes(final int valueLength) throws SQLServerException {
        assert valueLength <= this.valueBytes.length;
        this.readBytes(this.valueBytes, 0, valueLength);
        return this.valueBytes;
    }
    
    final Object readDecimal(final int valueLength, final TypeInfo typeInfo, final JDBCType jdbcType, final StreamType streamType) throws SQLServerException {
        if (valueLength > this.valueBytes.length) {
            if (TDSReader.logger.isLoggable(Level.WARNING)) {
                TDSReader.logger.warning(this.toString() + " Invalid value length:" + valueLength);
            }
            this.throwInvalidTDS();
        }
        this.readBytes(this.valueBytes, 0, valueLength);
        return DDC.convertBigDecimalToObject(Util.readBigDecimal(this.valueBytes, valueLength, typeInfo.getScale()), jdbcType, streamType);
    }
    
    final Object readMoney(final int valueLength, final JDBCType jdbcType, final StreamType streamType) throws SQLServerException {
        BigInteger bi = null;
        switch (valueLength) {
            case 8: {
                final int intBitsHi = this.readInt();
                final int intBitsLo = this.readInt();
                if (JDBCType.BINARY == jdbcType) {
                    final byte[] value = new byte[8];
                    Util.writeIntBigEndian(intBitsHi, value, 0);
                    Util.writeIntBigEndian(intBitsLo, value, 4);
                    return value;
                }
                bi = BigInteger.valueOf((long)intBitsHi << 32 | ((long)intBitsLo & 0xFFFFFFFFL));
                break;
            }
            case 4: {
                if (JDBCType.BINARY == jdbcType) {
                    final byte[] value2 = new byte[4];
                    Util.writeIntBigEndian(this.readInt(), value2, 0);
                    return value2;
                }
                bi = BigInteger.valueOf(this.readInt());
                break;
            }
            default: {
                this.throwInvalidTDS();
                return null;
            }
        }
        return DDC.convertBigDecimalToObject(new BigDecimal(bi, 4), jdbcType, streamType);
    }
    
    final Object readReal(final int valueLength, final JDBCType jdbcType, final StreamType streamType) throws SQLServerException {
        if (4 != valueLength) {
            this.throwInvalidTDS();
        }
        return DDC.convertFloatToObject(Float.intBitsToFloat(this.readInt()), jdbcType, streamType);
    }
    
    final Object readFloat(final int valueLength, final JDBCType jdbcType, final StreamType streamType) throws SQLServerException {
        if (8 != valueLength) {
            this.throwInvalidTDS();
        }
        return DDC.convertDoubleToObject(Double.longBitsToDouble(this.readLong()), jdbcType, streamType);
    }
    
    final Object readDateTime(final int valueLength, final Calendar appTimeZoneCalendar, final JDBCType jdbcType, final StreamType streamType) throws SQLServerException {
        int daysSinceSQLBaseDate = 0;
        int msecSinceMidnight = 0;
        switch (valueLength) {
            case 8: {
                daysSinceSQLBaseDate = this.readInt();
                final int ticksSinceMidnight = this.readInt();
                if (JDBCType.BINARY == jdbcType) {
                    final byte[] value = new byte[8];
                    Util.writeIntBigEndian(daysSinceSQLBaseDate, value, 0);
                    Util.writeIntBigEndian(ticksSinceMidnight, value, 4);
                    return value;
                }
                msecSinceMidnight = (ticksSinceMidnight * 10 + 1) / 3;
                break;
            }
            case 4: {
                daysSinceSQLBaseDate = this.readUnsignedShort();
                final int ticksSinceMidnight = this.readUnsignedShort();
                if (JDBCType.BINARY == jdbcType) {
                    final byte[] value = new byte[4];
                    Util.writeShortBigEndian((short)daysSinceSQLBaseDate, value, 0);
                    Util.writeShortBigEndian((short)ticksSinceMidnight, value, 2);
                    return value;
                }
                msecSinceMidnight = ticksSinceMidnight * 60 * 1000;
                break;
            }
            default: {
                this.throwInvalidTDS();
                return null;
            }
        }
        return DDC.convertTemporalToObject(jdbcType, SSType.DATETIME, appTimeZoneCalendar, daysSinceSQLBaseDate, msecSinceMidnight, 0);
    }
    
    final Object readDate(final int valueLength, final Calendar appTimeZoneCalendar, final JDBCType jdbcType) throws SQLServerException {
        if (3 != valueLength) {
            this.throwInvalidTDS();
        }
        final int localDaysIntoCE = this.readDaysIntoCE();
        return DDC.convertTemporalToObject(jdbcType, SSType.DATE, appTimeZoneCalendar, localDaysIntoCE, 0L, 0);
    }
    
    final Object readTime(final int valueLength, final TypeInfo typeInfo, final Calendar appTimeZoneCalendar, final JDBCType jdbcType) throws SQLServerException {
        if (TDS.timeValueLength(typeInfo.getScale()) != valueLength) {
            this.throwInvalidTDS();
        }
        final long localNanosSinceMidnight = this.readNanosSinceMidnight(typeInfo.getScale());
        return DDC.convertTemporalToObject(jdbcType, SSType.TIME, appTimeZoneCalendar, 0, localNanosSinceMidnight, typeInfo.getScale());
    }
    
    final Object readDateTime2(final int valueLength, final TypeInfo typeInfo, final Calendar appTimeZoneCalendar, final JDBCType jdbcType) throws SQLServerException {
        if (TDS.datetime2ValueLength(typeInfo.getScale()) != valueLength) {
            this.throwInvalidTDS();
        }
        final long localNanosSinceMidnight = this.readNanosSinceMidnight(typeInfo.getScale());
        final int localDaysIntoCE = this.readDaysIntoCE();
        return DDC.convertTemporalToObject(jdbcType, SSType.DATETIME2, appTimeZoneCalendar, localDaysIntoCE, localNanosSinceMidnight, typeInfo.getScale());
    }
    
    final Object readDateTimeOffset(final int valueLength, final TypeInfo typeInfo, final JDBCType jdbcType) throws SQLServerException {
        if (TDS.datetimeoffsetValueLength(typeInfo.getScale()) != valueLength) {
            this.throwInvalidTDS();
        }
        final long utcNanosSinceMidnight = this.readNanosSinceMidnight(typeInfo.getScale());
        final int utcDaysIntoCE = this.readDaysIntoCE();
        final int localMinutesOffset = this.readShort();
        return DDC.convertTemporalToObject(jdbcType, SSType.DATETIMEOFFSET, new GregorianCalendar(new SimpleTimeZone(localMinutesOffset * 60 * 1000, ""), Locale.US), utcDaysIntoCE, utcNanosSinceMidnight, typeInfo.getScale());
    }
    
    private int readDaysIntoCE() throws SQLServerException {
        final byte[] value = new byte[3];
        this.readBytes(value, 0, value.length);
        int daysIntoCE = 0;
        for (int i = 0; i < value.length; ++i) {
            daysIntoCE |= (value[i] & 0xFF) << 8 * i;
        }
        if (daysIntoCE < 0) {
            this.throwInvalidTDS();
        }
        return daysIntoCE;
    }
    
    private long readNanosSinceMidnight(final int scale) throws SQLServerException {
        assert 0 <= scale && scale <= 7;
        final byte[] value = new byte[TDS.nanosSinceMidnightLength(scale)];
        this.readBytes(value, 0, value.length);
        long hundredNanosSinceMidnight = 0L;
        for (int i = 0; i < value.length; ++i) {
            hundredNanosSinceMidnight |= ((long)value[i] & 0xFFL) << 8 * i;
        }
        hundredNanosSinceMidnight *= TDSReader.SCALED_MULTIPLIERS[scale];
        if (0L > hundredNanosSinceMidnight || hundredNanosSinceMidnight >= 864000000000L) {
            this.throwInvalidTDS();
        }
        return 100L * hundredNanosSinceMidnight;
    }
    
    final Object readGUID(final int valueLength, final JDBCType jdbcType, final StreamType streamType) throws SQLServerException {
        if (16 != valueLength) {
            this.throwInvalidTDS();
        }
        final byte[] guid = new byte[16];
        this.readBytes(guid, 0, 16);
        switch (jdbcType) {
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR:
            case GUID: {
                final StringBuilder sb = new StringBuilder("NNNNNNNN-NNNN-NNNN-NNNN-NNNNNNNNNNNN".length());
                for (int i = 0; i < 4; ++i) {
                    sb.append(Util.hexChars[(guid[3 - i] & 0xF0) >> 4]);
                    sb.append(Util.hexChars[guid[3 - i] & 0xF]);
                }
                sb.append('-');
                for (int i = 0; i < 2; ++i) {
                    sb.append(Util.hexChars[(guid[5 - i] & 0xF0) >> 4]);
                    sb.append(Util.hexChars[guid[5 - i] & 0xF]);
                }
                sb.append('-');
                for (int i = 0; i < 2; ++i) {
                    sb.append(Util.hexChars[(guid[7 - i] & 0xF0) >> 4]);
                    sb.append(Util.hexChars[guid[7 - i] & 0xF]);
                }
                sb.append('-');
                for (int i = 0; i < 2; ++i) {
                    sb.append(Util.hexChars[(guid[8 + i] & 0xF0) >> 4]);
                    sb.append(Util.hexChars[guid[8 + i] & 0xF]);
                }
                sb.append('-');
                for (int i = 0; i < 6; ++i) {
                    sb.append(Util.hexChars[(guid[10 + i] & 0xF0) >> 4]);
                    sb.append(Util.hexChars[guid[10 + i] & 0xF]);
                }
                try {
                    return DDC.convertStringToObject(sb.toString(), Encoding.UNICODE.charset(), jdbcType, streamType);
                }
                catch (final UnsupportedEncodingException e) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                    throw new SQLServerException(form.format(new Object[] { "UNIQUEIDENTIFIER", jdbcType }), null, 0, e);
                }
                break;
            }
        }
        if (StreamType.BINARY == streamType || StreamType.ASCII == streamType) {
            return new ByteArrayInputStream(guid);
        }
        return guid;
    }
    
    final SQLIdentifier readSQLIdentifier() throws SQLServerException {
        final int numParts = this.readUnsignedByte();
        if (1 > numParts || numParts > 4) {
            this.throwInvalidTDS();
        }
        final String[] nameParts = new String[numParts];
        for (int i = 0; i < numParts; ++i) {
            nameParts[i] = this.readUnicodeString(this.readUnsignedShort());
        }
        final SQLIdentifier identifier = new SQLIdentifier();
        identifier.setObjectName(nameParts[numParts - 1]);
        if (numParts >= 2) {
            identifier.setSchemaName(nameParts[numParts - 2]);
        }
        if (numParts >= 3) {
            identifier.setDatabaseName(nameParts[numParts - 3]);
        }
        if (4 == numParts) {
            identifier.setServerName(nameParts[numParts - 4]);
        }
        return identifier;
    }
    
    final SQLCollation readCollation() throws SQLServerException {
        SQLCollation collation = null;
        try {
            collation = new SQLCollation(this);
        }
        catch (final UnsupportedEncodingException e) {
            this.con.terminate(4, e.getMessage(), e);
        }
        return collation;
    }
    
    final void skip(int bytesToSkip) throws SQLServerException {
        assert bytesToSkip >= 0;
        while (bytesToSkip > 0) {
            if (!this.ensurePayload()) {
                this.throwInvalidTDS();
            }
            int bytesSkipped = bytesToSkip;
            if (bytesSkipped > this.currentPacket.payloadLength - this.payloadOffset) {
                bytesSkipped = this.currentPacket.payloadLength - this.payloadOffset;
            }
            bytesToSkip -= bytesSkipped;
            this.payloadOffset += bytesSkipped;
        }
    }
    
    final void tryProcessFeatureExtAck(final boolean featureExtAckReceived) throws SQLServerException {
        if (null != this.con.getRoutingInfo()) {
            return;
        }
        if (this.isColumnEncryptionSettingEnabled() && !featureExtAckReceived) {
            throw new SQLServerException(this, SQLServerException.getErrString("R_AE_NotSupportedByServer"), null, 0, false);
        }
    }
    
    final void trySetSensitivityClassification(final SensitivityClassification sensitivityClassification) {
        this.sensitivityClassification = sensitivityClassification;
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.Reader");
        lastReaderID = new AtomicInteger(0);
        SCALED_MULTIPLIERS = new int[] { 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1 };
    }
}
