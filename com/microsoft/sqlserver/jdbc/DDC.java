package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.util.TimeZone;
import microsoft.sql.DateTimeOffset;
import java.util.Calendar;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.io.StringReader;
import java.sql.Time;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.sql.Date;
import java.sql.Timestamp;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.math.BigDecimal;
import java.math.BigInteger;

final class DDC
{
    private static final BigInteger maxRPCDecimalValue;
    
    static final Object convertIntegerToObject(final int intValue, final int valueLength, final JDBCType jdbcType, final StreamType streamType) {
        switch (jdbcType) {
            case INTEGER: {
                return intValue;
            }
            case SMALLINT:
            case TINYINT: {
                return (short)intValue;
            }
            case BIT:
            case BOOLEAN: {
                return 0 != intValue;
            }
            case BIGINT: {
                return intValue;
            }
            case DECIMAL:
            case NUMERIC:
            case MONEY:
            case SMALLMONEY: {
                return new BigDecimal(Integer.toString(intValue));
            }
            case FLOAT:
            case DOUBLE: {
                return intValue;
            }
            case REAL: {
                return intValue;
            }
            case BINARY: {
                return convertIntToBytes(intValue, valueLength);
            }
            default: {
                return Integer.toString(intValue);
            }
        }
    }
    
    static final Object convertLongToObject(final long longVal, final JDBCType jdbcType, final SSType baseSSType, final StreamType streamType) {
        switch (jdbcType) {
            case BIGINT: {
                return longVal;
            }
            case INTEGER: {
                return (int)longVal;
            }
            case SMALLINT:
            case TINYINT: {
                return (short)longVal;
            }
            case BIT:
            case BOOLEAN: {
                return 0L != longVal;
            }
            case DECIMAL:
            case NUMERIC:
            case MONEY:
            case SMALLMONEY: {
                return new BigDecimal(Long.toString(longVal));
            }
            case FLOAT:
            case DOUBLE: {
                return longVal;
            }
            case REAL: {
                return longVal;
            }
            case BINARY: {
                final byte[] convertedBytes = convertLongToBytes(longVal);
                switch (baseSSType) {
                    case BIT:
                    case TINYINT: {
                        final int bytesToReturnLength = 1;
                        final byte[] bytesToReturn = new byte[bytesToReturnLength];
                        System.arraycopy(convertedBytes, convertedBytes.length - bytesToReturnLength, bytesToReturn, 0, bytesToReturnLength);
                        return bytesToReturn;
                    }
                    case SMALLINT: {
                        final int bytesToReturnLength = 2;
                        final byte[] bytesToReturn = new byte[bytesToReturnLength];
                        System.arraycopy(convertedBytes, convertedBytes.length - bytesToReturnLength, bytesToReturn, 0, bytesToReturnLength);
                        return bytesToReturn;
                    }
                    case INTEGER: {
                        final int bytesToReturnLength = 4;
                        final byte[] bytesToReturn = new byte[bytesToReturnLength];
                        System.arraycopy(convertedBytes, convertedBytes.length - bytesToReturnLength, bytesToReturn, 0, bytesToReturnLength);
                        return bytesToReturn;
                    }
                    case BIGINT: {
                        final int bytesToReturnLength = 8;
                        final byte[] bytesToReturn = new byte[bytesToReturnLength];
                        System.arraycopy(convertedBytes, convertedBytes.length - bytesToReturnLength, bytesToReturn, 0, bytesToReturnLength);
                        return bytesToReturn;
                    }
                    default: {
                        return convertedBytes;
                    }
                }
                break;
            }
            case VARBINARY: {
                switch (baseSSType) {
                    case BIGINT: {
                        return longVal;
                    }
                    case INTEGER: {
                        return (int)longVal;
                    }
                    case TINYINT:
                    case SMALLINT: {
                        return (short)longVal;
                    }
                    case BIT: {
                        return 0L != longVal;
                    }
                    case DECIMAL:
                    case NUMERIC:
                    case MONEY:
                    case SMALLMONEY: {
                        return new BigDecimal(Long.toString(longVal));
                    }
                    case FLOAT: {
                        return longVal;
                    }
                    case REAL: {
                        return longVal;
                    }
                    case BINARY: {
                        return convertLongToBytes(longVal);
                    }
                    default: {
                        return Long.toString(longVal);
                    }
                }
                break;
            }
            default: {
                return Long.toString(longVal);
            }
        }
    }
    
    static final byte[] convertIntToBytes(int intValue, final int valueLength) {
        final byte[] bytes = new byte[valueLength];
        int i = valueLength;
        while (i-- > 0) {
            bytes[i] = (byte)(intValue & 0xFF);
            intValue >>= 8;
        }
        return bytes;
    }
    
    static final Object convertFloatToObject(final float floatVal, final JDBCType jdbcType, final StreamType streamType) {
        switch (jdbcType) {
            case REAL: {
                return floatVal;
            }
            case INTEGER: {
                return (int)floatVal;
            }
            case SMALLINT:
            case TINYINT: {
                return (short)floatVal;
            }
            case BIT:
            case BOOLEAN: {
                return 0 != Float.compare(0.0f, floatVal);
            }
            case BIGINT: {
                return (long)floatVal;
            }
            case DECIMAL:
            case NUMERIC:
            case MONEY:
            case SMALLMONEY: {
                return new BigDecimal(Float.toString(floatVal));
            }
            case FLOAT:
            case DOUBLE: {
                return floatVal;
            }
            case BINARY: {
                return convertIntToBytes(Float.floatToRawIntBits(floatVal), 4);
            }
            default: {
                return Float.toString(floatVal);
            }
        }
    }
    
    static final byte[] convertLongToBytes(long longValue) {
        final byte[] bytes = new byte[8];
        int i = 8;
        while (i-- > 0) {
            bytes[i] = (byte)(longValue & 0xFFL);
            longValue >>= 8;
        }
        return bytes;
    }
    
    static final Object convertDoubleToObject(final double doubleVal, final JDBCType jdbcType, final StreamType streamType) {
        switch (jdbcType) {
            case FLOAT:
            case DOUBLE: {
                return doubleVal;
            }
            case REAL: {
                return doubleVal.floatValue();
            }
            case INTEGER: {
                return (int)doubleVal;
            }
            case SMALLINT:
            case TINYINT: {
                return (short)doubleVal;
            }
            case BIT:
            case BOOLEAN: {
                return 0 != Double.compare(0.0, doubleVal);
            }
            case BIGINT: {
                return (long)doubleVal;
            }
            case DECIMAL:
            case NUMERIC:
            case MONEY:
            case SMALLMONEY: {
                return new BigDecimal(Double.toString(doubleVal));
            }
            case BINARY: {
                return convertLongToBytes(Double.doubleToRawLongBits(doubleVal));
            }
            default: {
                return Double.toString(doubleVal);
            }
        }
    }
    
    static final byte[] convertBigDecimalToBytes(BigDecimal bigDecimalVal, final int scale) {
        byte[] valueBytes;
        if (bigDecimalVal == null) {
            valueBytes = new byte[] { (byte)scale, 0 };
        }
        else {
            final boolean isNegative = bigDecimalVal.signum() < 0;
            if (bigDecimalVal.scale() < 0) {
                bigDecimalVal = bigDecimalVal.setScale(0);
            }
            BigInteger bi = bigDecimalVal.unscaledValue();
            if (isNegative) {
                bi = bi.negate();
            }
            final byte[] unscaledBytes = bi.toByteArray();
            valueBytes = new byte[unscaledBytes.length + 3];
            int j = 0;
            valueBytes[j++] = (byte)bigDecimalVal.scale();
            valueBytes[j++] = (byte)(unscaledBytes.length + 1);
            valueBytes[j++] = (byte)(isNegative ? 0 : 1);
            for (int i = unscaledBytes.length - 1; i >= 0; --i) {
                valueBytes[j++] = unscaledBytes[i];
            }
        }
        return valueBytes;
    }
    
    static final Object convertBigDecimalToObject(final BigDecimal bigDecimalVal, final JDBCType jdbcType, final StreamType streamType) {
        switch (jdbcType) {
            case DECIMAL:
            case NUMERIC:
            case MONEY:
            case SMALLMONEY: {
                return bigDecimalVal;
            }
            case FLOAT:
            case DOUBLE: {
                return bigDecimalVal.doubleValue();
            }
            case REAL: {
                return bigDecimalVal.floatValue();
            }
            case INTEGER: {
                return bigDecimalVal.intValue();
            }
            case SMALLINT:
            case TINYINT: {
                return bigDecimalVal.shortValue();
            }
            case BIT:
            case BOOLEAN: {
                return 0 != bigDecimalVal.compareTo(BigDecimal.valueOf(0L));
            }
            case BIGINT: {
                return bigDecimalVal.longValue();
            }
            case BINARY: {
                return convertBigDecimalToBytes(bigDecimalVal, bigDecimalVal.scale());
            }
            default: {
                return bigDecimalVal.toString();
            }
        }
    }
    
    static final Object convertMoneyToObject(final BigDecimal bigDecimalVal, final JDBCType jdbcType, final StreamType streamType, final int numberOfBytes) {
        switch (jdbcType) {
            case DECIMAL:
            case NUMERIC:
            case MONEY:
            case SMALLMONEY: {
                return bigDecimalVal;
            }
            case FLOAT:
            case DOUBLE: {
                return bigDecimalVal.doubleValue();
            }
            case REAL: {
                return bigDecimalVal.floatValue();
            }
            case INTEGER: {
                return bigDecimalVal.intValue();
            }
            case SMALLINT:
            case TINYINT: {
                return bigDecimalVal.shortValue();
            }
            case BIT:
            case BOOLEAN: {
                return 0 != bigDecimalVal.compareTo(BigDecimal.valueOf(0L));
            }
            case BIGINT: {
                return bigDecimalVal.longValue();
            }
            case BINARY: {
                return convertToBytes(bigDecimalVal, bigDecimalVal.scale(), numberOfBytes);
            }
            default: {
                return bigDecimalVal.toString();
            }
        }
    }
    
    private static byte[] convertToBytes(BigDecimal value, final int scale, final int numBytes) {
        final boolean isNeg = value.signum() < 0;
        value = value.setScale(scale);
        final BigInteger bigInt = value.unscaledValue();
        final byte[] unscaledBytes = bigInt.toByteArray();
        final byte[] ret = new byte[numBytes];
        if (unscaledBytes.length < numBytes) {
            for (int i = 0; i < numBytes - unscaledBytes.length; ++i) {
                ret[i] = (byte)(isNeg ? -1 : 0);
            }
        }
        final int offset = numBytes - unscaledBytes.length;
        System.arraycopy(unscaledBytes, 0, ret, offset, numBytes - offset);
        return ret;
    }
    
    static final Object convertBytesToObject(final byte[] bytesValue, final JDBCType jdbcType, final TypeInfo baseTypeInfo) throws SQLServerException {
        switch (jdbcType) {
            case CHAR: {
                final String str = Util.bytesToHexString(bytesValue, bytesValue.length);
                if (SSType.BINARY == baseTypeInfo.getSSType() && str.length() < baseTypeInfo.getPrecision() * 2) {
                    final StringBuilder strbuf = new StringBuilder(str);
                    while (strbuf.length() < baseTypeInfo.getPrecision() * 2) {
                        strbuf.append('0');
                    }
                    return strbuf.toString();
                }
                return str;
            }
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY: {
                if (SSType.BINARY == baseTypeInfo.getSSType() && bytesValue.length < baseTypeInfo.getPrecision()) {
                    final byte[] newBytes = new byte[baseTypeInfo.getPrecision()];
                    System.arraycopy(bytesValue, 0, newBytes, 0, bytesValue.length);
                    return newBytes;
                }
                return bytesValue;
            }
            default: {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
                throw new SQLServerException(form.format(new Object[] { baseTypeInfo.getSSType().name(), jdbcType }), null, 0, null);
            }
        }
    }
    
    static final Object convertStringToObject(final String stringVal, final Charset charset, final JDBCType jdbcType, final StreamType streamType) throws UnsupportedEncodingException, IllegalArgumentException {
        switch (jdbcType) {
            case DECIMAL:
            case NUMERIC:
            case MONEY:
            case SMALLMONEY: {
                return new BigDecimal(stringVal.trim());
            }
            case FLOAT:
            case DOUBLE: {
                return Double.valueOf(stringVal.trim());
            }
            case REAL: {
                return Float.valueOf(stringVal.trim());
            }
            case INTEGER: {
                return Integer.valueOf(stringVal.trim());
            }
            case SMALLINT:
            case TINYINT: {
                return Short.valueOf(stringVal.trim());
            }
            case BIT:
            case BOOLEAN: {
                final String trimmedString = stringVal.trim();
                return (1 == trimmedString.length()) ? ('1' == trimmedString.charAt(0)) : Boolean.valueOf(trimmedString);
            }
            case BIGINT: {
                return Long.valueOf(stringVal.trim());
            }
            case TIMESTAMP: {
                return Timestamp.valueOf(stringVal.trim());
            }
            case LOCALDATETIME: {
                return parseStringIntoLDT(stringVal.trim());
            }
            case DATE: {
                return Date.valueOf(getDatePart(stringVal.trim()));
            }
            case TIME: {
                final Timestamp ts = Timestamp.valueOf("1970-01-01 " + getTimePart(stringVal.trim()));
                final GregorianCalendar cal = new GregorianCalendar(Locale.US);
                cal.clear();
                cal.setTimeInMillis(ts.getTime());
                if (ts.getNanos() % 1000000 >= 500000) {
                    cal.add(14, 1);
                }
                cal.set(1970, 0, 1);
                return new Time(cal.getTimeInMillis());
            }
            case BINARY: {
                return stringVal.getBytes(charset);
            }
            default: {
                switch (streamType) {
                    case CHARACTER: {
                        return new StringReader(stringVal);
                    }
                    case ASCII: {
                        return new ByteArrayInputStream(stringVal.getBytes(StandardCharsets.US_ASCII));
                    }
                    case BINARY: {
                        return new ByteArrayInputStream(stringVal.getBytes());
                    }
                    default: {
                        return stringVal;
                    }
                }
                break;
            }
        }
    }
    
    private static LocalDateTime parseStringIntoLDT(String s) {
        final int YEAR_LENGTH = 4;
        final int MONTH_LENGTH = 2;
        final int DAY_LENGTH = 2;
        final int MAX_MONTH = 12;
        final int MAX_DAY = 31;
        int year = 0;
        int month = 0;
        int day = 0;
        int a_nanos = 0;
        final String formatError = "Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]";
        if (s == null) {
            throw new IllegalArgumentException("null string");
        }
        s = s.trim();
        final int dividingSpace = s.indexOf(32);
        if (dividingSpace < 0) {
            throw new IllegalArgumentException(formatError);
        }
        final int firstDash = s.indexOf(45);
        final int secondDash = s.indexOf(45, firstDash + 1);
        final int firstColon = s.indexOf(58, dividingSpace + 1);
        final int secondColon = s.indexOf(58, firstColon + 1);
        final int period = s.indexOf(46, secondColon + 1);
        boolean parsedDate = false;
        if (firstDash > 0 && secondDash > 0 && secondDash < dividingSpace - 1 && firstDash == 4 && secondDash - firstDash > 1 && secondDash - firstDash <= 3 && dividingSpace - secondDash > 1 && dividingSpace - secondDash <= 3) {
            year = Integer.parseInt((CharSequence)s, 0, firstDash, 10);
            month = Integer.parseInt((CharSequence)s, firstDash + 1, secondDash, 10);
            day = Integer.parseInt((CharSequence)s, secondDash + 1, dividingSpace, 10);
            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                parsedDate = true;
            }
        }
        if (!parsedDate) {
            throw new IllegalArgumentException(formatError);
        }
        final int len = s.length();
        if (firstColon > 0 && secondColon > 0 && secondColon < len - 1) {
            final int hour = Integer.parseInt((CharSequence)s, dividingSpace + 1, firstColon, 10);
            final int minute = Integer.parseInt((CharSequence)s, firstColon + 1, secondColon, 10);
            int second;
            if (period > 0 && period < len - 1) {
                second = Integer.parseInt((CharSequence)s, secondColon + 1, period, 10);
                int nanoPrecision = len - (period + 1);
                if (nanoPrecision > 9) {
                    throw new IllegalArgumentException(formatError);
                }
                if (!Character.isDigit(s.charAt(period + 1))) {
                    throw new IllegalArgumentException(formatError);
                }
                int tmpNanos = Integer.parseInt((CharSequence)s, period + 1, len, 10);
                while (nanoPrecision < 9) {
                    tmpNanos *= 10;
                    ++nanoPrecision;
                }
                a_nanos = tmpNanos;
            }
            else {
                if (period > 0) {
                    throw new IllegalArgumentException(formatError);
                }
                second = Integer.parseInt((CharSequence)s, secondColon + 1, len, 10);
            }
            return LocalDateTime.of(year, month, day, hour, minute, second, a_nanos);
        }
        throw new IllegalArgumentException(formatError);
    }
    
    static final Object convertStreamToObject(final BaseInputStream stream, final TypeInfo typeInfo, final JDBCType jdbcType, final InputStreamGetterArgs getterArgs) throws SQLServerException {
        if (null == stream) {
            return null;
        }
        assert null != typeInfo;
        assert null != getterArgs;
        final SSType ssType = typeInfo.getSSType();
        try {
            switch (jdbcType) {
                case CLOB: {
                    return new SQLServerClob(stream, typeInfo);
                }
                case NCLOB: {
                    return new SQLServerNClob(stream, typeInfo);
                }
                case SQLXML: {
                    return new SQLServerSQLXML(stream, getterArgs, typeInfo);
                }
                case BINARY:
                case VARBINARY:
                case LONGVARBINARY:
                case BLOB: {
                    if (StreamType.BINARY == getterArgs.streamType) {
                        return stream;
                    }
                    if (JDBCType.BLOB == jdbcType) {
                        return new SQLServerBlob(stream);
                    }
                    return stream.getBytes();
                }
                default: {
                    if (SSType.BINARY == ssType || SSType.VARBINARY == ssType || SSType.VARBINARYMAX == ssType || SSType.TIMESTAMP == ssType || SSType.IMAGE == ssType || SSType.UDT == ssType) {
                        if (StreamType.ASCII == getterArgs.streamType) {
                            return stream;
                        }
                        assert StreamType.NONE == getterArgs.streamType;
                        final byte[] byteValue = stream.getBytes();
                        if (JDBCType.GUID == jdbcType) {
                            return Util.readGUID(byteValue);
                        }
                        if (JDBCType.GEOMETRY == jdbcType) {
                            if (!typeInfo.getSSTypeName().equalsIgnoreCase(jdbcType.toString())) {
                                DataTypes.throwConversionError(typeInfo.getSSTypeName().toUpperCase(), jdbcType.toString());
                            }
                            return Geometry.STGeomFromWKB(byteValue);
                        }
                        if (JDBCType.GEOGRAPHY == jdbcType) {
                            if (!typeInfo.getSSTypeName().equalsIgnoreCase(jdbcType.toString())) {
                                DataTypes.throwConversionError(typeInfo.getSSTypeName().toUpperCase(), jdbcType.toString());
                            }
                            return Geography.STGeomFromWKB(byteValue);
                        }
                        final String hexString = Util.bytesToHexString(byteValue, byteValue.length);
                        if (StreamType.NONE == getterArgs.streamType) {
                            return hexString;
                        }
                        return new StringReader(hexString);
                    }
                    else if (StreamType.ASCII == getterArgs.streamType) {
                        if (typeInfo.supportsFastAsciiConversion()) {
                            return new AsciiFilteredInputStream(stream);
                        }
                        if (getterArgs.isAdaptive) {
                            return AsciiFilteredUnicodeInputStream.MakeAsciiFilteredUnicodeInputStream(stream, new BufferedReader(new InputStreamReader(stream, typeInfo.getCharset())));
                        }
                        return new ByteArrayInputStream(new String(stream.getBytes(), typeInfo.getCharset()).getBytes(StandardCharsets.US_ASCII));
                    }
                    else {
                        if (StreamType.CHARACTER != getterArgs.streamType && StreamType.NCHARACTER != getterArgs.streamType) {
                            return convertStringToObject(new String(stream.getBytes(), typeInfo.getCharset()), typeInfo.getCharset(), jdbcType, getterArgs.streamType);
                        }
                        if (getterArgs.isAdaptive) {
                            return new BufferedReader(new InputStreamReader(stream, typeInfo.getCharset()));
                        }
                        return new StringReader(new String(stream.getBytes(), typeInfo.getCharset()));
                    }
                    break;
                }
            }
        }
        catch (final IllegalArgumentException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
            throw new SQLServerException(form.format(new Object[] { typeInfo.getSSType(), jdbcType }), null, 0, e);
        }
        catch (final UnsupportedEncodingException e2) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
            throw new SQLServerException(form.format(new Object[] { typeInfo.getSSType(), jdbcType }), null, 0, e2);
        }
    }
    
    private static String getDatePart(final String s) {
        final int sp = s.indexOf(32);
        if (-1 == sp) {
            return s;
        }
        return s.substring(0, sp);
    }
    
    private static String getTimePart(final String s) {
        final int sp = s.indexOf(32);
        if (-1 == sp) {
            return s;
        }
        return s.substring(sp + 1);
    }
    
    private static String fractionalSecondsString(final long subSecondNanos, final int scale) {
        assert 0L <= subSecondNanos && subSecondNanos < 1000000000L;
        assert 0 <= scale && scale <= 7;
        if (0 == scale) {
            return "";
        }
        return BigDecimal.valueOf(subSecondNanos % 1000000000L, 9).setScale(scale).toPlainString().substring(1);
    }
    
    static final Object convertTemporalToObject(final JDBCType jdbcType, final SSType ssType, final Calendar timeZoneCalendar, final int daysSinceBaseDate, final long ticksSinceMidnight, final int fractionalSecondsScale) {
        if (null == timeZoneCalendar) {
            return convertTemporalToObject(jdbcType, ssType, daysSinceBaseDate, ticksSinceMidnight, fractionalSecondsScale);
        }
        final TimeZone localTimeZone = timeZoneCalendar.getTimeZone();
        final TimeZone componentTimeZone = (SSType.DATETIMEOFFSET == ssType) ? UTC.timeZone : localTimeZone;
        GregorianCalendar cal = new GregorianCalendar(componentTimeZone, Locale.US);
        cal.setLenient(true);
        cal.clear();
        int subSecondNanos = 0;
        switch (ssType) {
            case TIME: {
                cal.set(1900, 0, 1, 0, 0, 0);
                cal.set(14, (int)(ticksSinceMidnight / 1000000L));
                subSecondNanos = (int)(ticksSinceMidnight % 1000000000L);
                break;
            }
            case DATE:
            case DATETIME2:
            case DATETIMEOFFSET: {
                if (daysSinceBaseDate >= GregorianChange.DAYS_SINCE_BASE_DATE_HINT) {
                    cal.set(1, 0, 1 + daysSinceBaseDate + GregorianChange.EXTRA_DAYS_TO_BE_ADDED, 0, 0, 0);
                    cal.set(14, (int)(ticksSinceMidnight / 1000000L));
                }
                else {
                    cal.setGregorianChange(GregorianChange.PURE_CHANGE_DATE);
                    cal.set(1, 0, 1 + daysSinceBaseDate, 0, 0, 0);
                    cal.set(14, (int)(ticksSinceMidnight / 1000000L));
                    final int year = cal.get(1);
                    final int month = cal.get(2);
                    final int date = cal.get(5);
                    final int hour = cal.get(11);
                    final int minute = cal.get(12);
                    final int second = cal.get(13);
                    final int millis = cal.get(14);
                    cal.setGregorianChange(GregorianChange.STANDARD_CHANGE_DATE);
                    cal.set(year, month, date, hour, minute, second);
                    cal.set(14, millis);
                }
                if (SSType.DATETIMEOFFSET == ssType && !componentTimeZone.hasSameRules(localTimeZone)) {
                    final GregorianCalendar localCalendar = new GregorianCalendar(localTimeZone, Locale.US);
                    localCalendar.clear();
                    localCalendar.setTimeInMillis(cal.getTimeInMillis());
                    cal = localCalendar;
                }
                subSecondNanos = (int)(ticksSinceMidnight % 1000000000L);
                break;
            }
            case DATETIME: {
                cal.set(1900, 0, 1 + daysSinceBaseDate, 0, 0, 0);
                cal.set(14, (int)ticksSinceMidnight);
                subSecondNanos = (int)(ticksSinceMidnight * 1000000L % 1000000000L);
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected SSType: " + ssType));
            }
        }
        final int localMillisOffset = timeZoneCalendar.get(15);
        switch (jdbcType.category) {
            case BINARY:
            case SQL_VARIANT: {
                switch (ssType) {
                    case DATE: {
                        cal.set(11, 0);
                        cal.set(12, 0);
                        cal.set(13, 0);
                        cal.set(14, 0);
                        return new Date(cal.getTimeInMillis());
                    }
                    case DATETIME2:
                    case DATETIME: {
                        final Timestamp ts = new Timestamp(cal.getTimeInMillis());
                        ts.setNanos(subSecondNanos);
                        return ts;
                    }
                    case DATETIMEOFFSET: {
                        assert SSType.DATETIMEOFFSET == ssType;
                        assert 0 == localMillisOffset % 60000;
                        final Timestamp ts = new Timestamp(cal.getTimeInMillis());
                        ts.setNanos(subSecondNanos);
                        return DateTimeOffset.valueOf(ts, localMillisOffset / 60000);
                    }
                    case TIME: {
                        if (subSecondNanos % 1000000 >= 500000) {
                            cal.add(14, 1);
                        }
                        cal.set(1970, 0, 1);
                        return new Time(cal.getTimeInMillis());
                    }
                    default: {
                        throw new AssertionError((Object)("Unexpected SSType: " + ssType));
                    }
                }
                break;
            }
            case DATE: {
                cal.set(11, 0);
                cal.set(12, 0);
                cal.set(13, 0);
                cal.set(14, 0);
                return new Date(cal.getTimeInMillis());
            }
            case TIME: {
                if (subSecondNanos % 1000000 >= 500000) {
                    cal.add(14, 1);
                }
                cal.set(1970, 0, 1);
                return new Time(cal.getTimeInMillis());
            }
            case TIMESTAMP: {
                final Timestamp ts = new Timestamp(cal.getTimeInMillis());
                ts.setNanos(subSecondNanos);
                if (jdbcType == JDBCType.LOCALDATETIME) {
                    return ts.toLocalDateTime();
                }
                return ts;
            }
            case DATETIMEOFFSET: {
                assert SSType.DATETIMEOFFSET == ssType;
                assert 0 == localMillisOffset % 60000;
                final Timestamp ts = new Timestamp(cal.getTimeInMillis());
                ts.setNanos(subSecondNanos);
                return DateTimeOffset.valueOf(ts, localMillisOffset / 60000);
            }
            case CHARACTER: {
                switch (ssType) {
                    case DATE: {
                        return String.format(Locale.US, "%1$tF", cal);
                    }
                    case TIME: {
                        return String.format(Locale.US, "%1$tT%2$s", cal, fractionalSecondsString(subSecondNanos, fractionalSecondsScale));
                    }
                    case DATETIME2: {
                        return String.format(Locale.US, "%1$tF %1$tT%2$s", cal, fractionalSecondsString(subSecondNanos, fractionalSecondsScale));
                    }
                    case DATETIMEOFFSET: {
                        assert 0 == localMillisOffset % 60000;
                        final int unsignedMinutesOffset = Math.abs(localMillisOffset / 60000);
                        return String.format(Locale.US, "%1$tF %1$tT%2$s %3$c%4$02d:%5$02d", cal, fractionalSecondsString(subSecondNanos, fractionalSecondsScale), (localMillisOffset >= 0) ? '+' : '-', unsignedMinutesOffset / 60, unsignedMinutesOffset % 60);
                    }
                    case DATETIME: {
                        return new Timestamp(cal.getTimeInMillis()).toString();
                    }
                    default: {
                        throw new AssertionError((Object)("Unexpected SSType: " + ssType));
                    }
                }
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected JDBCType: " + jdbcType));
            }
        }
    }
    
    private static Object convertTemporalToObject(final JDBCType jdbcType, final SSType ssType, final int daysSinceBaseDate, final long ticksSinceMidnight, final int fractionalSecondsScale) {
        LocalDateTime ldt = null;
        int subSecondNanos = 0;
        switch (ssType) {
            case TIME: {
                ldt = LocalDateTime.of(1900, 1, 1, 0, 0, 0).plusNanos(ticksSinceMidnight);
                subSecondNanos = (int)(ticksSinceMidnight % 1000000000L);
                break;
            }
            case DATE:
            case DATETIME2:
            case DATETIMEOFFSET: {
                ldt = LocalDateTime.of(1, 1, 1, 0, 0, 0);
                ldt = ldt.plusDays(daysSinceBaseDate);
                if (jdbcType.category != JDBCType.Category.DATE) {
                    ldt = ldt.plusNanos(ticksSinceMidnight);
                }
                subSecondNanos = (int)(ticksSinceMidnight % 1000000000L);
                break;
            }
            case DATETIME: {
                ldt = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
                ldt = ldt.plusDays(daysSinceBaseDate);
                if (jdbcType.category != JDBCType.Category.DATE) {
                    ldt = ldt.plusNanos(ticksSinceMidnight * 1000000L);
                }
                subSecondNanos = (int)(ticksSinceMidnight * 1000000L % 1000000000L);
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected SSType: " + ssType));
            }
        }
        switch (jdbcType.category) {
            case BINARY:
            case SQL_VARIANT: {
                switch (ssType) {
                    case DATE: {
                        return Date.valueOf(ldt.toLocalDate());
                    }
                    case DATETIME2:
                    case DATETIME: {
                        final Timestamp ts = Timestamp.valueOf(ldt);
                        ts.setNanos(subSecondNanos);
                        return ts;
                    }
                    case TIME: {
                        if (subSecondNanos % 1000000 >= 500000) {
                            ldt = ldt.plusNanos(1000000L);
                        }
                        final Time t = Time.valueOf(ldt.toLocalTime());
                        t.setTime(t.getTime() + ldt.getNano() / 1000000);
                        return t;
                    }
                    default: {
                        throw new AssertionError((Object)("Unexpected SSType: " + ssType));
                    }
                }
                break;
            }
            case DATE: {
                return Date.valueOf(ldt.toLocalDate());
            }
            case TIME: {
                if (subSecondNanos % 1000000 >= 500000) {
                    ldt = ldt.plusNanos(1000000L);
                }
                final Time t = Time.valueOf(ldt.toLocalTime());
                t.setTime(t.getTime() + ldt.getNano() / 1000000);
                return t;
            }
            case TIMESTAMP: {
                if (jdbcType == JDBCType.LOCALDATETIME) {
                    return ldt;
                }
                final Timestamp ts = Timestamp.valueOf(ldt);
                ts.setNanos(subSecondNanos);
                return ts;
            }
            case CHARACTER: {
                switch (ssType) {
                    case DATE: {
                        return String.format(Locale.US, "%1$tF", Timestamp.valueOf(ldt));
                    }
                    case TIME: {
                        return String.format(Locale.US, "%1$tT%2$s", ldt, fractionalSecondsString(subSecondNanos, fractionalSecondsScale));
                    }
                    case DATETIME2: {
                        return String.format(Locale.US, "%1$tF %1$tT%2$s", Timestamp.valueOf(ldt), fractionalSecondsString(subSecondNanos, fractionalSecondsScale));
                    }
                    case DATETIME: {
                        return Timestamp.valueOf(ldt).toString();
                    }
                    default: {
                        throw new AssertionError((Object)("Unexpected SSType: " + ssType));
                    }
                }
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected JDBCType: " + jdbcType));
            }
        }
    }
    
    static int daysSinceBaseDate(final int year, final int dayOfYear, final int baseYear) {
        assert year >= 1;
        assert baseYear >= 1;
        assert dayOfYear >= 1;
        return dayOfYear - 1 + (year - baseYear) * 365 + leapDaysBeforeYear(year) - leapDaysBeforeYear(baseYear);
    }
    
    private static int leapDaysBeforeYear(final int year) {
        assert year >= 1;
        return (year - 1) / 4 - (year - 1) / 100 + (year - 1) / 400;
    }
    
    static final boolean exceedsMaxRPCDecimalPrecisionOrScale(final BigDecimal bigDecimalValue) {
        if (null == bigDecimalValue) {
            return false;
        }
        if (bigDecimalValue.scale() > 38) {
            return true;
        }
        BigInteger bi = (bigDecimalValue.scale() < 0) ? bigDecimalValue.setScale(0).unscaledValue() : bigDecimalValue.unscaledValue();
        if (bigDecimalValue.signum() < 0) {
            bi = bi.negate();
        }
        return bi.compareTo(DDC.maxRPCDecimalValue) > 0;
    }
    
    static String convertReaderToString(final Reader reader, final int readerLength) throws SQLServerException {
        assert readerLength >= 0;
        if (null == reader) {
            return null;
        }
        if (0 == readerLength) {
            return "";
        }
        try {
            final StringBuilder sb = new StringBuilder((-1 != readerLength) ? readerLength : 4000);
            final char[] charArray = new char[(-1 != readerLength && readerLength < 4000) ? readerLength : 4000];
            int readChars;
            while ((readChars = reader.read(charArray, 0, charArray.length)) > 0) {
                if (readChars > charArray.length) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    final Object[] msgArgs = { SQLServerException.getErrString("R_streamReadReturnedInvalidValue") };
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                }
                sb.append(charArray, 0, readChars);
            }
            return sb.toString();
        }
        catch (final IOException ioEx) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
            final Object[] msgArgs2 = { ioEx.toString() };
            SQLServerException.makeFromDriverError(null, null, form2.format(msgArgs2), "", true);
            return null;
        }
    }
    
    static {
        maxRPCDecimalValue = new BigInteger("99999999999999999999999999999999999999");
    }
}
