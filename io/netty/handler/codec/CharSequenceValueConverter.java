package io.netty.handler.codec;

import io.netty.util.internal.PlatformDependent;
import java.text.ParseException;
import java.util.Date;
import io.netty.util.AsciiString;

public class CharSequenceValueConverter implements ValueConverter<CharSequence>
{
    public static final CharSequenceValueConverter INSTANCE;
    private static final AsciiString TRUE_ASCII;
    
    @Override
    public CharSequence convertObject(final Object value) {
        if (value instanceof CharSequence) {
            return (CharSequence)value;
        }
        return value.toString();
    }
    
    @Override
    public CharSequence convertInt(final int value) {
        return String.valueOf(value);
    }
    
    @Override
    public CharSequence convertLong(final long value) {
        return String.valueOf(value);
    }
    
    @Override
    public CharSequence convertDouble(final double value) {
        return String.valueOf(value);
    }
    
    @Override
    public CharSequence convertChar(final char value) {
        return String.valueOf(value);
    }
    
    @Override
    public CharSequence convertBoolean(final boolean value) {
        return String.valueOf(value);
    }
    
    @Override
    public CharSequence convertFloat(final float value) {
        return String.valueOf(value);
    }
    
    @Override
    public boolean convertToBoolean(final CharSequence value) {
        return AsciiString.contentEqualsIgnoreCase(value, CharSequenceValueConverter.TRUE_ASCII);
    }
    
    @Override
    public CharSequence convertByte(final byte value) {
        return String.valueOf(value);
    }
    
    @Override
    public byte convertToByte(final CharSequence value) {
        if (value instanceof AsciiString && value.length() == 1) {
            return ((AsciiString)value).byteAt(0);
        }
        return Byte.parseByte(value.toString());
    }
    
    @Override
    public char convertToChar(final CharSequence value) {
        return value.charAt(0);
    }
    
    @Override
    public CharSequence convertShort(final short value) {
        return String.valueOf(value);
    }
    
    @Override
    public short convertToShort(final CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseShort();
        }
        return Short.parseShort(value.toString());
    }
    
    @Override
    public int convertToInt(final CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseInt();
        }
        return Integer.parseInt(value.toString());
    }
    
    @Override
    public long convertToLong(final CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseLong();
        }
        return Long.parseLong(value.toString());
    }
    
    @Override
    public CharSequence convertTimeMillis(final long value) {
        return DateFormatter.format(new Date(value));
    }
    
    @Override
    public long convertToTimeMillis(final CharSequence value) {
        final Date date = DateFormatter.parseHttpDate(value);
        if (date == null) {
            PlatformDependent.throwException(new ParseException("header can't be parsed into a Date: " + (Object)value, 0));
            return 0L;
        }
        return date.getTime();
    }
    
    @Override
    public float convertToFloat(final CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseFloat();
        }
        return Float.parseFloat(value.toString());
    }
    
    @Override
    public double convertToDouble(final CharSequence value) {
        if (value instanceof AsciiString) {
            return ((AsciiString)value).parseDouble();
        }
        return Double.parseDouble(value.toString());
    }
    
    static {
        INSTANCE = new CharSequenceValueConverter();
        TRUE_ASCII = new AsciiString("true");
    }
}
