package com.unboundid.asn1;

import com.unboundid.util.Debug;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import com.unboundid.util.StaticUtils;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1UTCTime extends ASN1Element
{
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTERS;
    private static final long serialVersionUID = -3107099228691194285L;
    private final long time;
    private final String stringRepresentation;
    
    public ASN1UTCTime() {
        this((byte)23);
    }
    
    public ASN1UTCTime(final byte type) {
        this(type, System.currentTimeMillis());
    }
    
    public ASN1UTCTime(final Date date) {
        this((byte)23, date.getTime());
    }
    
    public ASN1UTCTime(final byte type, final Date date) {
        this(type, date.getTime());
    }
    
    public ASN1UTCTime(final long time) {
        this((byte)23, time);
    }
    
    public ASN1UTCTime(final byte type, final long time) {
        super(type, StaticUtils.getBytes(encodeTimestamp(time)));
        final GregorianCalendar calendar = new GregorianCalendar(StaticUtils.getUTCTimeZone());
        calendar.setTimeInMillis(time);
        calendar.set(14, 0);
        this.time = calendar.getTimeInMillis();
        this.stringRepresentation = encodeTimestamp(time);
    }
    
    public ASN1UTCTime(final String timestamp) throws ASN1Exception {
        this((byte)23, timestamp);
    }
    
    public ASN1UTCTime(final byte type, final String timestamp) throws ASN1Exception {
        super(type, StaticUtils.getBytes(timestamp));
        this.time = decodeTimestamp(timestamp);
        this.stringRepresentation = timestamp;
    }
    
    public static String encodeTimestamp(final Date date) {
        return getDateFormatter().format(date);
    }
    
    private static SimpleDateFormat getDateFormatter() {
        final SimpleDateFormat existingFormatter = ASN1UTCTime.DATE_FORMATTERS.get();
        if (existingFormatter != null) {
            return existingFormatter;
        }
        final SimpleDateFormat newFormatter = new SimpleDateFormat("yyMMddHHmmss'Z'");
        newFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        newFormatter.setLenient(false);
        ASN1UTCTime.DATE_FORMATTERS.set(newFormatter);
        return newFormatter;
    }
    
    public static String encodeTimestamp(final long time) {
        return encodeTimestamp(new Date(time));
    }
    
    public static long decodeTimestamp(final String timestamp) throws ASN1Exception {
        if (timestamp.length() != 13) {
            throw new ASN1Exception(ASN1Messages.ERR_UTC_TIME_STRING_INVALID_LENGTH.get());
        }
        if (!timestamp.endsWith("Z") && !timestamp.endsWith("z")) {
            throw new ASN1Exception(ASN1Messages.ERR_UTC_TIME_STRING_DOES_NOT_END_WITH_Z.get());
        }
        for (int i = 0; i < timestamp.length() - 1; ++i) {
            final char c = timestamp.charAt(i);
            if (c < '0' || c > '9') {
                throw new ASN1Exception(ASN1Messages.ERR_UTC_TIME_STRING_CHAR_NOT_DIGIT.get(i + 1));
            }
        }
        final int month = Integer.parseInt(timestamp.substring(2, 4));
        if (month < 1 || month > 12) {
            throw new ASN1Exception(ASN1Messages.ERR_UTC_TIME_STRING_INVALID_MONTH.get());
        }
        final int day = Integer.parseInt(timestamp.substring(4, 6));
        if (day < 1 || day > 31) {
            throw new ASN1Exception(ASN1Messages.ERR_UTC_TIME_STRING_INVALID_DAY.get());
        }
        final int hour = Integer.parseInt(timestamp.substring(6, 8));
        if (hour > 23) {
            throw new ASN1Exception(ASN1Messages.ERR_UTC_TIME_STRING_INVALID_HOUR.get());
        }
        final int minute = Integer.parseInt(timestamp.substring(8, 10));
        if (minute > 59) {
            throw new ASN1Exception(ASN1Messages.ERR_UTC_TIME_STRING_INVALID_MINUTE.get());
        }
        final int second = Integer.parseInt(timestamp.substring(10, 12));
        if (second > 60) {
            throw new ASN1Exception(ASN1Messages.ERR_UTC_TIME_STRING_INVALID_SECOND.get());
        }
        try {
            return getDateFormatter().parse(timestamp).getTime();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ASN1Exception(ASN1Messages.ERR_UTC_TIME_STRING_CANNOT_PARSE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public long getTime() {
        return this.time;
    }
    
    public Date getDate() {
        return new Date(this.time);
    }
    
    public String getStringRepresentation() {
        return this.stringRepresentation;
    }
    
    public static ASN1UTCTime decodeAsUTCTime(final byte[] elementBytes) throws ASN1Exception {
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
            return new ASN1UTCTime(elementBytes[0], StaticUtils.toUTF8String(elementValue));
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
    
    public static ASN1UTCTime decodeAsUTCTime(final ASN1Element element) throws ASN1Exception {
        return new ASN1UTCTime(element.getType(), StaticUtils.toUTF8String(element.getValue()));
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.stringRepresentation);
    }
    
    static {
        DATE_FORMATTERS = new ThreadLocal<SimpleDateFormat>();
    }
}
