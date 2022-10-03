package com.microsoft.sqlserver.jdbc;

import java.util.Hashtable;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.text.DecimalFormat;
import java.util.logging.LogManager;
import java.util.UUID;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.Set;
import java.util.Locale;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import java.math.BigInteger;
import java.math.BigDecimal;

final class Util
{
    static final String SYSTEM_SPEC_VERSION;
    static final char[] hexChars;
    static final String WSIDNotAvailable = "";
    static final String ACTIVITY_ID_TRACE_PROPERTY = "com.microsoft.sqlserver.jdbc.traceactivity";
    static final String SYSTEM_JRE;
    static final boolean use43Wrapper;
    
    static boolean isIBM() {
        return Util.SYSTEM_JRE.startsWith("IBM");
    }
    
    static String getJVMArchOnWindows() {
        return System.getProperty("os.arch").contains("64") ? "x64" : "x86";
    }
    
    static final boolean isCharType(final int jdbcType) {
        switch (jdbcType) {
            case -16:
            case -15:
            case -9:
            case -1:
            case 1:
            case 12: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static final Boolean isCharType(final SSType ssType) {
        switch (ssType) {
            case CHAR:
            case NCHAR:
            case VARCHAR:
            case NVARCHAR:
            case VARCHARMAX:
            case NVARCHARMAX: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static final Boolean isBinaryType(final SSType ssType) {
        switch (ssType) {
            case BINARY:
            case VARBINARY:
            case VARBINARYMAX:
            case IMAGE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static final Boolean isBinaryType(final int jdbcType) {
        switch (jdbcType) {
            case -4:
            case -3:
            case -2: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static short readShort(final byte[] data, final int nOffset) {
        return (short)((data[nOffset] & 0xFF) | (data[nOffset + 1] & 0xFF) << 8);
    }
    
    static int readUnsignedShort(final byte[] data, final int nOffset) {
        return (data[nOffset] & 0xFF) | (data[nOffset + 1] & 0xFF) << 8;
    }
    
    static int readUnsignedShortBigEndian(final byte[] data, final int nOffset) {
        return (data[nOffset] & 0xFF) << 8 | (data[nOffset + 1] & 0xFF);
    }
    
    static void writeShort(final short value, final byte[] valueBytes, final int offset) {
        valueBytes[offset + 0] = (byte)(value >> 0 & 0xFF);
        valueBytes[offset + 1] = (byte)(value >> 8 & 0xFF);
    }
    
    static void writeShortBigEndian(final short value, final byte[] valueBytes, final int offset) {
        valueBytes[offset + 0] = (byte)(value >> 8 & 0xFF);
        valueBytes[offset + 1] = (byte)(value >> 0 & 0xFF);
    }
    
    static int readInt(final byte[] data, final int nOffset) {
        final int b1 = data[nOffset + 0] & 0xFF;
        final int b2 = (data[nOffset + 1] & 0xFF) << 8;
        final int b3 = (data[nOffset + 2] & 0xFF) << 16;
        final int b4 = (data[nOffset + 3] & 0xFF) << 24;
        return b4 | b3 | b2 | b1;
    }
    
    static int readIntBigEndian(final byte[] data, final int nOffset) {
        return (data[nOffset + 3] & 0xFF) << 0 | (data[nOffset + 2] & 0xFF) << 8 | (data[nOffset + 1] & 0xFF) << 16 | (data[nOffset + 0] & 0xFF) << 24;
    }
    
    static void writeInt(final int value, final byte[] valueBytes, final int offset) {
        valueBytes[offset + 0] = (byte)(value >> 0 & 0xFF);
        valueBytes[offset + 1] = (byte)(value >> 8 & 0xFF);
        valueBytes[offset + 2] = (byte)(value >> 16 & 0xFF);
        valueBytes[offset + 3] = (byte)(value >> 24 & 0xFF);
    }
    
    static void writeIntBigEndian(final int value, final byte[] valueBytes, final int offset) {
        valueBytes[offset + 0] = (byte)(value >> 24 & 0xFF);
        valueBytes[offset + 1] = (byte)(value >> 16 & 0xFF);
        valueBytes[offset + 2] = (byte)(value >> 8 & 0xFF);
        valueBytes[offset + 3] = (byte)(value >> 0 & 0xFF);
    }
    
    static void writeLongBigEndian(final long value, final byte[] valueBytes, final int offset) {
        valueBytes[offset + 0] = (byte)(value >> 56 & 0xFFL);
        valueBytes[offset + 1] = (byte)(value >> 48 & 0xFFL);
        valueBytes[offset + 2] = (byte)(value >> 40 & 0xFFL);
        valueBytes[offset + 3] = (byte)(value >> 32 & 0xFFL);
        valueBytes[offset + 4] = (byte)(value >> 24 & 0xFFL);
        valueBytes[offset + 5] = (byte)(value >> 16 & 0xFFL);
        valueBytes[offset + 6] = (byte)(value >> 8 & 0xFFL);
        valueBytes[offset + 7] = (byte)(value >> 0 & 0xFFL);
    }
    
    static BigDecimal readBigDecimal(final byte[] valueBytes, final int valueLength, final int scale) {
        final int sign = (0 == valueBytes[0]) ? -1 : 1;
        final byte[] magnitude = new byte[valueLength - 1];
        for (int i = 1; i <= magnitude.length; ++i) {
            magnitude[magnitude.length - i] = valueBytes[i];
        }
        return new BigDecimal(new BigInteger(sign, magnitude), scale);
    }
    
    static long readLong(final byte[] data, final int nOffset) {
        return (long)(data[nOffset + 7] & 0xFF) << 56 | (long)(data[nOffset + 6] & 0xFF) << 48 | (long)(data[nOffset + 5] & 0xFF) << 40 | (long)(data[nOffset + 4] & 0xFF) << 32 | (long)(data[nOffset + 3] & 0xFF) << 24 | (long)(data[nOffset + 2] & 0xFF) << 16 | (long)(data[nOffset + 1] & 0xFF) << 8 | (long)(data[nOffset] & 0xFF);
    }
    
    static void writeLong(final long value, final byte[] valueBytes, int offset) {
        valueBytes[offset++] = (byte)(value & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 8 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 16 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 24 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 32 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 40 & 0xFFL);
        valueBytes[offset++] = (byte)(value >> 48 & 0xFFL);
        valueBytes[offset] = (byte)(value >> 56 & 0xFFL);
    }
    
    static Properties parseUrl(final String url, final Logger logger) throws SQLServerException {
        final Properties p = new Properties();
        String tmpUrl = url;
        final String sPrefix = "jdbc:sqlserver://";
        String result = "";
        String name = "";
        String value = "";
        if (!tmpUrl.startsWith(sPrefix)) {
            return null;
        }
        tmpUrl = tmpUrl.substring(sPrefix.length());
        final int inStart = 0;
        final int inServerName = 1;
        final int inPort = 2;
        final int inInstanceName = 3;
        final int inEscapedValueStart = 4;
        final int inEscapedValueEnd = 5;
        final int inValue = 6;
        final int inName = 7;
        int state = 0;
        for (int i = 0; i < tmpUrl.length(); ++i) {
            final char ch = tmpUrl.charAt(i);
            switch (state) {
                case 0: {
                    if (ch == ';') {
                        state = 7;
                        break;
                    }
                    final StringBuilder builder = new StringBuilder();
                    builder.append(result);
                    builder.append(ch);
                    result = builder.toString();
                    state = 1;
                    break;
                }
                case 1: {
                    if (ch != ';' && ch != ':' && ch != '\\') {
                        final StringBuilder builder = new StringBuilder();
                        builder.append(result);
                        builder.append(ch);
                        result = builder.toString();
                        break;
                    }
                    result = result.trim();
                    if (result.length() > 0) {
                        ((Hashtable<String, String>)p).put(SQLServerDriverStringProperty.SERVER_NAME.toString(), result);
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine("Property:serverName Value:" + result);
                        }
                    }
                    result = "";
                    if (ch == ';') {
                        state = 7;
                        break;
                    }
                    if (ch == ':') {
                        state = 2;
                        break;
                    }
                    state = 3;
                    break;
                }
                case 2: {
                    if (ch == ';') {
                        result = result.trim();
                        if (logger.isLoggable(Level.FINE)) {
                            logger.fine("Property:portNumber Value:" + result);
                        }
                        ((Hashtable<String, String>)p).put(SQLServerDriverIntProperty.PORT_NUMBER.toString(), result);
                        result = "";
                        state = 7;
                        break;
                    }
                    final StringBuilder builder = new StringBuilder();
                    builder.append(result);
                    builder.append(ch);
                    result = builder.toString();
                    break;
                }
                case 3: {
                    if (ch != ';' && ch != ':') {
                        final StringBuilder builder = new StringBuilder();
                        builder.append(result);
                        builder.append(ch);
                        result = builder.toString();
                        break;
                    }
                    result = result.trim();
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Property:instanceName Value:" + result);
                    }
                    ((Hashtable<String, String>)p).put(SQLServerDriverStringProperty.INSTANCE_NAME.toString(), result.toLowerCase(Locale.US));
                    result = "";
                    if (ch == ';') {
                        state = 7;
                        break;
                    }
                    state = 2;
                    break;
                }
                case 7: {
                    if (ch == '=') {
                        name = name.trim();
                        if (name.length() <= 0) {
                            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                        }
                        state = 6;
                        break;
                    }
                    if (ch != ';') {
                        final StringBuilder builder = new StringBuilder();
                        builder.append(name);
                        builder.append(ch);
                        name = builder.toString();
                        break;
                    }
                    name = name.trim();
                    if (name.length() > 0) {
                        SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                        break;
                    }
                    break;
                }
                case 6: {
                    if (ch == ';') {
                        value = value.trim();
                        name = SQLServerDriver.getNormalizedPropertyName(name, logger);
                        if (null != name) {
                            if (logger.isLoggable(Level.FINE) && !name.equals(SQLServerDriverStringProperty.USER.toString())) {
                                if (!name.toLowerCase(Locale.ENGLISH).contains("password") && !name.toLowerCase(Locale.ENGLISH).contains("keystoresecret")) {
                                    logger.fine("Property:" + name + " Value:" + value);
                                }
                                else {
                                    logger.fine("Property:" + name);
                                }
                            }
                            ((Hashtable<String, String>)p).put(name, value);
                        }
                        name = "";
                        value = "";
                        state = 7;
                        break;
                    }
                    if (ch != '{') {
                        final StringBuilder builder = new StringBuilder();
                        builder.append(value);
                        builder.append(ch);
                        value = builder.toString();
                        break;
                    }
                    state = 4;
                    value = value.trim();
                    if (value.length() > 0) {
                        SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                        break;
                    }
                    break;
                }
                case 4: {
                    if (ch == '}') {
                        name = SQLServerDriver.getNormalizedPropertyName(name, logger);
                        if (null != name) {
                            if (logger.isLoggable(Level.FINE) && !name.equals(SQLServerDriverStringProperty.USER.toString()) && !name.equals(SQLServerDriverStringProperty.PASSWORD.toString())) {
                                logger.fine("Property:" + name + " Value:" + value);
                            }
                            ((Hashtable<String, String>)p).put(name, value);
                        }
                        name = "";
                        value = "";
                        state = 5;
                        break;
                    }
                    final StringBuilder builder = new StringBuilder();
                    builder.append(value);
                    builder.append(ch);
                    value = builder.toString();
                    break;
                }
                case 5: {
                    if (ch == ';') {
                        state = 7;
                        break;
                    }
                    if (ch != ' ') {
                        SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                        break;
                    }
                    break;
                }
                default: {
                    assert false : "parseURL: Invalid state " + state;
                    break;
                }
            }
        }
        switch (state) {
            case 1: {
                result = result.trim();
                if (result.length() > 0) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Property:serverName Value:" + result);
                    }
                    ((Hashtable<String, String>)p).put(SQLServerDriverStringProperty.SERVER_NAME.toString(), result);
                    break;
                }
                break;
            }
            case 2: {
                result = result.trim();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Property:portNumber Value:" + result);
                }
                ((Hashtable<String, String>)p).put(SQLServerDriverIntProperty.PORT_NUMBER.toString(), result);
                break;
            }
            case 3: {
                result = result.trim();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Property:instanceName Value:" + result);
                }
                ((Hashtable<String, String>)p).put(SQLServerDriverStringProperty.INSTANCE_NAME.toString(), result);
                break;
            }
            case 6: {
                value = value.trim();
                name = SQLServerDriver.getNormalizedPropertyName(name, logger);
                if (null != name) {
                    if (logger.isLoggable(Level.FINE) && !name.equals(SQLServerDriverStringProperty.USER.toString()) && !name.equals(SQLServerDriverStringProperty.PASSWORD.toString()) && !name.equals(SQLServerDriverStringProperty.KEY_STORE_SECRET.toString())) {
                        logger.fine("Property:" + name + " Value:" + value);
                    }
                    ((Hashtable<String, String>)p).put(name, value);
                    break;
                }
                break;
            }
            case 0:
            case 5: {
                break;
            }
            case 7: {
                name = name.trim();
                if (name.length() > 0) {
                    SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                    break;
                }
                break;
            }
            default: {
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
                break;
            }
        }
        return p;
    }
    
    static String escapeSQLId(final String inID) {
        final StringBuilder outID = new StringBuilder(inID.length() + 2);
        outID.append('[');
        for (int i = 0; i < inID.length(); ++i) {
            final char ch = inID.charAt(i);
            if (']' == ch) {
                outID.append("]]");
            }
            else {
                outID.append(ch);
            }
        }
        outID.append(']');
        return outID.toString();
    }
    
    static void checkDuplicateColumnName(final String columnName, final Set<String> columnNames) throws SQLServerException {
        if (!columnNames.add(columnName)) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPDuplicateColumnName"));
            final Object[] msgArgs = { columnName };
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
    }
    
    static String readUnicodeString(final byte[] b, final int offset, final int byteLength, final SQLServerConnection conn) throws SQLServerException {
        try {
            return new String(b, offset, byteLength, Encoding.UNICODE.charset());
        }
        catch (final IndexOutOfBoundsException ex) {
            final String txtMsg = SQLServerException.checkAndAppendClientConnId(SQLServerException.getErrString("R_stringReadError"), conn);
            final MessageFormat form = new MessageFormat(txtMsg);
            final Object[] msgArgs = { offset };
            throw new SQLServerException(form.format(msgArgs), null, 0, ex);
        }
    }
    
    static String byteToHexDisplayString(final byte[] b) {
        if (null == b) {
            return "(null)";
        }
        final StringBuilder sb = new StringBuilder(b.length * 2 + 2);
        sb.append("0x");
        for (final byte aB : b) {
            final int hexVal = aB & 0xFF;
            sb.append(Util.hexChars[(hexVal & 0xF0) >> 4]);
            sb.append(Util.hexChars[hexVal & 0xF]);
        }
        return sb.toString();
    }
    
    static String bytesToHexString(final byte[] b, final int length) {
        final StringBuilder sb = new StringBuilder(length * 2);
        for (int i = 0; i < length; ++i) {
            final int hexVal = b[i] & 0xFF;
            sb.append(Util.hexChars[(hexVal & 0xF0) >> 4]);
            sb.append(Util.hexChars[hexVal & 0xF]);
        }
        return sb.toString();
    }
    
    static String lookupHostName() {
        try {
            final InetAddress localAddress = InetAddress.getLocalHost();
            if (null != localAddress) {
                String value = localAddress.getHostName();
                if (null != value && value.length() > 0) {
                    return value;
                }
                value = localAddress.getHostAddress();
                if (null != value && value.length() > 0) {
                    return value;
                }
            }
        }
        catch (final UnknownHostException e) {
            return "";
        }
        return "";
    }
    
    static final byte[] asGuidByteArray(final UUID aId) {
        final long msb = aId.getMostSignificantBits();
        final long lsb = aId.getLeastSignificantBits();
        final byte[] buffer = new byte[16];
        writeLongBigEndian(msb, buffer, 0);
        writeLongBigEndian(lsb, buffer, 8);
        byte tmpByte = buffer[0];
        buffer[0] = buffer[3];
        buffer[3] = tmpByte;
        tmpByte = buffer[1];
        buffer[1] = buffer[2];
        buffer[2] = tmpByte;
        tmpByte = buffer[4];
        buffer[4] = buffer[5];
        buffer[5] = tmpByte;
        tmpByte = buffer[6];
        buffer[6] = buffer[7];
        buffer[7] = tmpByte;
        return buffer;
    }
    
    static final UUID readGUIDtoUUID(final byte[] inputGUID) throws SQLServerException {
        if (inputGUID.length != 16) {
            throw new SQLServerException("guid length must be 16", (Throwable)null);
        }
        byte tmpByte = inputGUID[0];
        inputGUID[0] = inputGUID[3];
        inputGUID[3] = tmpByte;
        tmpByte = inputGUID[1];
        inputGUID[1] = inputGUID[2];
        inputGUID[2] = tmpByte;
        tmpByte = inputGUID[4];
        inputGUID[4] = inputGUID[5];
        inputGUID[5] = tmpByte;
        tmpByte = inputGUID[6];
        inputGUID[6] = inputGUID[7];
        inputGUID[7] = tmpByte;
        long msb = 0L;
        for (int i = 0; i < 8; ++i) {
            msb = (msb << 8 | ((long)inputGUID[i] & 0xFFL));
        }
        long lsb = 0L;
        for (int j = 8; j < 16; ++j) {
            lsb = (lsb << 8 | ((long)inputGUID[j] & 0xFFL));
        }
        return new UUID(msb, lsb);
    }
    
    static final String readGUID(final byte[] inputGUID) throws SQLServerException {
        final String guidTemplate = "NNNNNNNN-NNNN-NNNN-NNNN-NNNNNNNNNNNN";
        final byte[] guid = inputGUID;
        final StringBuilder sb = new StringBuilder(guidTemplate.length());
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
        return sb.toString();
    }
    
    static boolean isActivityTraceOn() {
        final LogManager lm = LogManager.getLogManager();
        final String activityTrace = lm.getProperty("com.microsoft.sqlserver.jdbc.traceactivity");
        return "on".equalsIgnoreCase(activityTrace);
    }
    
    static boolean shouldHonorAEForRead(final SQLServerStatementColumnEncryptionSetting stmtColumnEncryptionSetting, final SQLServerConnection connection) {
        switch (stmtColumnEncryptionSetting) {
            case Disabled: {
                return false;
            }
            case Enabled:
            case ResultSetOnly: {
                return true;
            }
            default: {
                assert SQLServerStatementColumnEncryptionSetting.UseConnectionSetting == stmtColumnEncryptionSetting : "Unexpected value for command level override";
                return connection != null && connection.isColumnEncryptionSettingEnabled();
            }
        }
    }
    
    static boolean shouldHonorAEForParameters(final SQLServerStatementColumnEncryptionSetting stmtColumnEncryptionSetting, final SQLServerConnection connection) {
        switch (stmtColumnEncryptionSetting) {
            case Disabled:
            case ResultSetOnly: {
                return false;
            }
            case Enabled: {
                return true;
            }
            default: {
                assert SQLServerStatementColumnEncryptionSetting.UseConnectionSetting == stmtColumnEncryptionSetting : "Unexpected value for command level override";
                return connection != null && connection.isColumnEncryptionSettingEnabled();
            }
        }
    }
    
    static void validateMoneyRange(final BigDecimal bd, final JDBCType jdbcType) throws SQLServerException {
        if (null == bd) {
            return;
        }
        switch (jdbcType) {
            case MONEY: {
                if (1 != bd.compareTo(SSType.MAX_VALUE_MONEY) && -1 != bd.compareTo(SSType.MIN_VALUE_MONEY)) {
                    return;
                }
                break;
            }
            case SMALLMONEY: {
                if (1 != bd.compareTo(SSType.MAX_VALUE_SMALLMONEY) && -1 != bd.compareTo(SSType.MIN_VALUE_SMALLMONEY)) {
                    return;
                }
                break;
            }
        }
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
        final Object[] msgArgs = { jdbcType };
        throw new SQLServerException(form.format(msgArgs), (Throwable)null);
    }
    
    static int getValueLengthBaseOnJavaType(final Object value, JavaType javaType, final Integer precision, final Integer scale, final JDBCType jdbcType) throws SQLServerException {
        Label_0103: {
            switch (javaType) {
                case OBJECT: {
                    switch (jdbcType) {
                        case DECIMAL:
                        case NUMERIC: {
                            javaType = JavaType.BIGDECIMAL;
                            break Label_0103;
                        }
                        case TIME: {
                            javaType = JavaType.TIME;
                            break Label_0103;
                        }
                        case TIMESTAMP: {
                            javaType = JavaType.TIMESTAMP;
                            break Label_0103;
                        }
                        case DATETIMEOFFSET: {
                            javaType = JavaType.DATETIMEOFFSET;
                            break Label_0103;
                        }
                        default: {
                            break Label_0103;
                        }
                    }
                    break;
                }
            }
        }
        switch (javaType) {
            case STRING: {
                if (JDBCType.GUID == jdbcType) {
                    final String guidTemplate = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX";
                    return (null == value) ? 0 : guidTemplate.length();
                }
                if (JDBCType.TIMESTAMP == jdbcType || JDBCType.TIME == jdbcType || JDBCType.DATETIMEOFFSET == jdbcType) {
                    return (null == scale) ? 7 : scale;
                }
                if (JDBCType.BINARY == jdbcType || JDBCType.VARBINARY == jdbcType) {
                    return (null == value) ? 0 : ParameterUtils.HexToBin((String)value).length;
                }
                return (null == value) ? 0 : ((String)value).length();
            }
            case BYTEARRAY: {
                return (null == value) ? 0 : ((byte[])value).length;
            }
            case BIGDECIMAL: {
                int length;
                if (null == precision) {
                    if (null == value) {
                        length = 0;
                    }
                    else if (0 == ((BigDecimal)value).intValue()) {
                        String s = "" + value;
                        s = s.replaceAll("\\-", "");
                        if (s.startsWith("0.")) {
                            s = s.replaceAll("0\\.", "");
                        }
                        else {
                            s = s.replaceAll("\\.", "");
                        }
                        length = s.length();
                    }
                    else if (("" + value).contains("E")) {
                        final DecimalFormat dform = new DecimalFormat("###.#####");
                        String s2 = dform.format(value);
                        s2 = s2.replaceAll("\\.", "");
                        s2 = s2.replaceAll("\\-", "");
                        length = s2.length();
                    }
                    else {
                        length = ((BigDecimal)value).precision();
                    }
                }
                else {
                    length = precision;
                }
                return length;
            }
            case TIMESTAMP:
            case TIME:
            case DATETIMEOFFSET: {
                return (null == scale) ? 7 : scale;
            }
            case CLOB: {
                return (null == value) ? 0 : 2147483646;
            }
            case NCLOB:
            case READER: {
                return (null == value) ? 0 : 1073741823;
            }
            default: {
                return 0;
            }
        }
    }
    
    static synchronized boolean checkIfNeedNewAccessToken(final SQLServerConnection connection, final Date accessTokenExpireDate) {
        final Date now = new Date();
        return accessTokenExpireDate.getTime() - now.getTime() < 2700000L && (accessTokenExpireDate.getTime() - now.getTime() < 600000L || (!connection.attemptRefreshTokenLocked && (connection.attemptRefreshTokenLocked = true)));
    }
    
    static boolean use43Wrapper() {
        return Util.use43Wrapper;
    }
    
    static String escapeSingleQuotes(final String name) {
        return name.replace("'", "''");
    }
    
    static String convertInputStreamToString(final InputStream is) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString();
    }
    
    static {
        SYSTEM_SPEC_VERSION = System.getProperty("java.specification.version");
        hexChars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        SYSTEM_JRE = System.getProperty("java.vendor") + " " + System.getProperty("java.version");
        boolean supportJDBC43 = true;
        try {
            DriverJDBCVersion.checkSupportsJDBC43();
        }
        catch (final UnsupportedOperationException e) {
            supportJDBC43 = false;
        }
        final double jvmVersion = Double.parseDouble(Util.SYSTEM_SPEC_VERSION);
        use43Wrapper = (supportJDBC43 && 9.0 <= jvmVersion);
    }
}
