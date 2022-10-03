package com.unboundid.asn1;

import com.unboundid.util.Debug;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import com.unboundid.util.StaticUtils;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1GeneralizedTime extends ASN1Element
{
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTERS_WITHOUT_MILLIS;
    private static final long serialVersionUID = -7215431927354583052L;
    private final long time;
    private final String stringRepresentation;
    
    public ASN1GeneralizedTime() {
        this((byte)24);
    }
    
    public ASN1GeneralizedTime(final byte type) {
        this(type, System.currentTimeMillis());
    }
    
    public ASN1GeneralizedTime(final Date date) {
        this((byte)24, date);
    }
    
    public ASN1GeneralizedTime(final byte type, final Date date) {
        this(type, date.getTime());
    }
    
    public ASN1GeneralizedTime(final long time) {
        this((byte)24, time);
    }
    
    public ASN1GeneralizedTime(final byte type, final long time) {
        this(type, time, encodeTimestamp(time, true));
    }
    
    public ASN1GeneralizedTime(final String timestamp) throws ASN1Exception {
        this((byte)24, timestamp);
    }
    
    public ASN1GeneralizedTime(final byte type, final String timestamp) throws ASN1Exception {
        this(type, decodeTimestamp(timestamp), timestamp);
    }
    
    private ASN1GeneralizedTime(final byte type, final long time, final String stringRepresentation) {
        super(type, StaticUtils.getBytes(stringRepresentation));
        this.time = time;
        this.stringRepresentation = stringRepresentation;
    }
    
    public static String encodeTimestamp(final Date date, final boolean includeMilliseconds) {
        if (!includeMilliseconds) {
            SimpleDateFormat dateFormat = ASN1GeneralizedTime.DATE_FORMATTERS_WITHOUT_MILLIS.get();
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                ASN1GeneralizedTime.DATE_FORMATTERS_WITHOUT_MILLIS.set(dateFormat);
            }
            return dateFormat.format(date);
        }
        final String timestamp = StaticUtils.encodeGeneralizedTime(date);
        if (!timestamp.endsWith("0Z")) {
            return timestamp;
        }
        final StringBuilder buffer = new StringBuilder(timestamp);
        char c;
        do {
            c = buffer.charAt(buffer.length() - 2);
            if (c == '0' || c == '.') {
                buffer.deleteCharAt(buffer.length() - 2);
            }
        } while (c == '0');
        return buffer.toString();
    }
    
    public static String encodeTimestamp(final long time, final boolean includeMilliseconds) {
        return encodeTimestamp(new Date(time), includeMilliseconds);
    }
    
    public static long decodeTimestamp(final String timestamp) throws ASN1Exception {
        if (timestamp.length() < 15) {
            throw new ASN1Exception(ASN1Messages.ERR_GENERALIZED_TIME_STRING_TOO_SHORT.get());
        }
        if (!timestamp.endsWith("Z") && !timestamp.endsWith("z")) {
            throw new ASN1Exception(ASN1Messages.ERR_GENERALIZED_TIME_STRING_DOES_NOT_END_WITH_Z.get());
        }
        boolean hasSubSecond = false;
        for (int i = 0; i < timestamp.length() - 1; ++i) {
            final char c = timestamp.charAt(i);
            if (i == 14) {
                if (c != '.') {
                    throw new ASN1Exception(ASN1Messages.ERR_GENERALIZED_TIME_STRING_CHAR_NOT_PERIOD.get(i + 1));
                }
                hasSubSecond = true;
            }
            else if (c < '0' || c > '9') {
                throw new ASN1Exception(ASN1Messages.ERR_GENERALIZED_TIME_STRING_CHAR_NOT_DIGIT.get(i + 1));
            }
        }
        final GregorianCalendar calendar = new GregorianCalendar(StaticUtils.getUTCTimeZone());
        final int year = Integer.parseInt(timestamp.substring(0, 4));
        calendar.set(1, year);
        final int month = Integer.parseInt(timestamp.substring(4, 6));
        if (month < 1 || month > 12) {
            throw new ASN1Exception(ASN1Messages.ERR_GENERALIZED_TIME_STRING_INVALID_MONTH.get());
        }
        calendar.set(2, month - 1);
        final int day = Integer.parseInt(timestamp.substring(6, 8));
        if (day < 1 || day > 31) {
            throw new ASN1Exception(ASN1Messages.ERR_GENERALIZED_TIME_STRING_INVALID_DAY.get());
        }
        calendar.set(5, day);
        final int hour = Integer.parseInt(timestamp.substring(8, 10));
        if (hour > 23) {
            throw new ASN1Exception(ASN1Messages.ERR_GENERALIZED_TIME_STRING_INVALID_HOUR.get());
        }
        calendar.set(11, hour);
        final int minute = Integer.parseInt(timestamp.substring(10, 12));
        if (minute > 59) {
            throw new ASN1Exception(ASN1Messages.ERR_GENERALIZED_TIME_STRING_INVALID_MINUTE.get());
        }
        calendar.set(12, minute);
        final int second = Integer.parseInt(timestamp.substring(12, 14));
        if (second > 60) {
            throw new ASN1Exception(ASN1Messages.ERR_GENERALIZED_TIME_STRING_INVALID_SECOND.get());
        }
        calendar.set(13, second);
        if (hasSubSecond) {
            final StringBuilder subSecondString = new StringBuilder(timestamp.substring(15, timestamp.length() - 1));
            while (subSecondString.length() < 3) {
                subSecondString.append('0');
            }
            boolean addOne;
            if (subSecondString.length() > 3) {
                final char charFour = subSecondString.charAt(3);
                addOne = (charFour >= '5' && charFour <= '9');
                subSecondString.setLength(3);
            }
            else {
                addOne = false;
            }
            while (subSecondString.charAt(0) == '0') {
                subSecondString.deleteCharAt(0);
            }
            final int millisecond = Integer.parseInt(subSecondString.toString());
            if (addOne) {
                calendar.set(14, millisecond + 1);
            }
            else {
                calendar.set(14, millisecond);
            }
        }
        else {
            calendar.set(14, 0);
        }
        return calendar.getTimeInMillis();
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
    
    public static ASN1GeneralizedTime decodeAsGeneralizedTime(final byte[] elementBytes) throws ASN1Exception {
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
            return new ASN1GeneralizedTime(elementBytes[0], StaticUtils.toUTF8String(elementValue));
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
    
    public static ASN1GeneralizedTime decodeAsGeneralizedTime(final ASN1Element element) throws ASN1Exception {
        return new ASN1GeneralizedTime(element.getType(), StaticUtils.toUTF8String(element.getValue()));
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.stringRepresentation);
    }
    
    static {
        DATE_FORMATTERS_WITHOUT_MILLIS = new ThreadLocal<SimpleDateFormat>();
    }
}
