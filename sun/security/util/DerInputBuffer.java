package sun.security.util;

import sun.util.calendar.CalendarDate;
import sun.util.calendar.Gregorian;
import java.util.TimeZone;
import sun.util.calendar.CalendarSystem;
import java.util.Date;
import java.math.BigInteger;
import java.io.IOException;
import java.io.ByteArrayInputStream;

class DerInputBuffer extends ByteArrayInputStream implements Cloneable
{
    boolean allowBER;
    
    DerInputBuffer(final byte[] array) {
        this(array, true);
    }
    
    DerInputBuffer(final byte[] array, final boolean allowBER) {
        super(array);
        this.allowBER = true;
        this.allowBER = allowBER;
    }
    
    DerInputBuffer(final byte[] array, final int n, final int n2, final boolean allowBER) {
        super(array, n, n2);
        this.allowBER = true;
        this.allowBER = allowBER;
    }
    
    DerInputBuffer dup() {
        try {
            final DerInputBuffer derInputBuffer = (DerInputBuffer)this.clone();
            derInputBuffer.mark(Integer.MAX_VALUE);
            return derInputBuffer;
        }
        catch (final CloneNotSupportedException ex) {
            throw new IllegalArgumentException(ex.toString());
        }
    }
    
    byte[] toByteArray() {
        final int available = this.available();
        if (available <= 0) {
            return null;
        }
        final byte[] array = new byte[available];
        System.arraycopy(this.buf, this.pos, array, 0, available);
        return array;
    }
    
    int peek() throws IOException {
        if (this.pos >= this.count) {
            throw new IOException("out of data");
        }
        return this.buf[this.pos];
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DerInputBuffer && this.equals((DerInputBuffer)o);
    }
    
    boolean equals(final DerInputBuffer derInputBuffer) {
        if (this == derInputBuffer) {
            return true;
        }
        final int available = this.available();
        if (derInputBuffer.available() != available) {
            return false;
        }
        for (int i = 0; i < available; ++i) {
            if (this.buf[this.pos + i] != derInputBuffer.buf[derInputBuffer.pos + i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        final int available = this.available();
        final int pos = this.pos;
        for (byte b = 0; b < available; ++b) {
            n += this.buf[pos + b] * b;
        }
        return n;
    }
    
    void truncate(final int n) throws IOException {
        if (n > this.available()) {
            throw new IOException("insufficient data");
        }
        this.count = this.pos + n;
    }
    
    BigInteger getBigInteger(final int n, final boolean b) throws IOException {
        if (n > this.available()) {
            throw new IOException("short read of integer");
        }
        if (n == 0) {
            throw new IOException("Invalid encoding: zero length Int value");
        }
        final byte[] array = new byte[n];
        System.arraycopy(this.buf, this.pos, array, 0, n);
        this.skip(n);
        if (!this.allowBER && n >= 2 && array[0] == 0 && array[1] >= 0) {
            throw new IOException("Invalid encoding: redundant leading 0s");
        }
        if (b) {
            return new BigInteger(1, array);
        }
        return new BigInteger(array);
    }
    
    public int getInteger(final int n) throws IOException {
        final BigInteger bigInteger = this.getBigInteger(n, false);
        if (bigInteger.compareTo(BigInteger.valueOf(-2147483648L)) < 0) {
            throw new IOException("Integer below minimum valid value");
        }
        if (bigInteger.compareTo(BigInteger.valueOf(2147483647L)) > 0) {
            throw new IOException("Integer exceeds maximum valid value");
        }
        return bigInteger.intValue();
    }
    
    public byte[] getBitString(final int n) throws IOException {
        if (n > this.available()) {
            throw new IOException("short read of bit string");
        }
        if (n == 0) {
            throw new IOException("Invalid encoding: zero length bit string");
        }
        final byte b = this.buf[this.pos];
        if (b < 0 || b > 7) {
            throw new IOException("Invalid number of padding bits");
        }
        final byte[] array = new byte[n - 1];
        System.arraycopy(this.buf, this.pos + 1, array, 0, n - 1);
        if (b != 0) {
            final byte[] array2 = array;
            final int n2 = n - 2;
            array2[n2] &= (byte)(255 << b);
        }
        this.skip(n);
        return array;
    }
    
    byte[] getBitString() throws IOException {
        return this.getBitString(this.available());
    }
    
    BitArray getUnalignedBitString() throws IOException {
        if (this.pos >= this.count) {
            return null;
        }
        final int available = this.available();
        final int n = this.buf[this.pos] & 0xFF;
        if (n > 7) {
            throw new IOException("Invalid value for unused bits: " + n);
        }
        final byte[] array = new byte[available - 1];
        final int n2 = (array.length == 0) ? 0 : (array.length * 8 - n);
        System.arraycopy(this.buf, this.pos + 1, array, 0, available - 1);
        final BitArray bitArray = new BitArray(n2, array);
        this.pos = this.count;
        return bitArray;
    }
    
    public Date getUTCTime(final int n) throws IOException {
        if (n > this.available()) {
            throw new IOException("short read of DER UTC Time");
        }
        if (n < 11 || n > 17) {
            throw new IOException("DER UTC Time length error");
        }
        return this.getTime(n, false);
    }
    
    public Date getGeneralizedTime(final int n) throws IOException {
        if (n > this.available()) {
            throw new IOException("short read of DER Generalized Time");
        }
        if (n < 13) {
            throw new IOException("DER Generalized Time length error");
        }
        return this.getTime(n, true);
    }
    
    private Date getTime(int n, final boolean b) throws IOException {
        String s;
        int n2;
        if (b) {
            s = "Generalized";
            n2 = 1000 * toDigit(this.buf[this.pos++], s) + 100 * toDigit(this.buf[this.pos++], s) + 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
            n -= 2;
        }
        else {
            s = "UTC";
            n2 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
            if (n2 < 50) {
                n2 += 2000;
            }
            else {
                n2 += 1900;
            }
        }
        final int n3 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
        final int n4 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
        final int n5 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
        final int n6 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
        n -= 10;
        int n7 = 0;
        int n8;
        if (n > 2) {
            n8 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
            n -= 2;
            if (b && (this.buf[this.pos] == 46 || this.buf[this.pos] == 44)) {
                if (--n == 0) {
                    throw new IOException("Parse " + s + " time, empty fractional part");
                }
                ++this.pos;
                int n9 = 0;
                while (this.buf[this.pos] != 90 && this.buf[this.pos] != 43 && this.buf[this.pos] != 45) {
                    final int digit = toDigit(this.buf[this.pos], s);
                    ++n9;
                    if (--n == 0) {
                        throw new IOException("Parse " + s + " time, invalid fractional part");
                    }
                    ++this.pos;
                    switch (n9) {
                        case 1: {
                            n7 += 100 * digit;
                            continue;
                        }
                        case 2: {
                            n7 += 10 * digit;
                            continue;
                        }
                        case 3: {
                            n7 += digit;
                            continue;
                        }
                    }
                }
                if (n9 == 0) {
                    throw new IOException("Parse " + s + " time, empty fractional part");
                }
            }
        }
        else {
            n8 = 0;
        }
        if (n3 == 0 || n4 == 0 || n3 > 12 || n4 > 31 || n5 >= 24 || n6 >= 60 || n8 >= 60) {
            throw new IOException("Parse " + s + " time, invalid format");
        }
        final Gregorian gregorianCalendar = CalendarSystem.getGregorianCalendar();
        final CalendarDate calendarDate = gregorianCalendar.newCalendarDate(null);
        calendarDate.setDate(n2, n3, n4);
        calendarDate.setTimeOfDay(n5, n6, n8, n7);
        long time = gregorianCalendar.getTime(calendarDate);
        if (n != 1 && n != 5) {
            throw new IOException("Parse " + s + " time, invalid offset");
        }
        switch (this.buf[this.pos++]) {
            case 43: {
                if (n != 5) {
                    throw new IOException("Parse " + s + " time, invalid offset");
                }
                final int n10 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
                final int n11 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
                if (n10 >= 24 || n11 >= 60) {
                    throw new IOException("Parse " + s + " time, +hhmm");
                }
                time -= (n10 * 60 + n11) * 60 * 1000;
                break;
            }
            case 45: {
                if (n != 5) {
                    throw new IOException("Parse " + s + " time, invalid offset");
                }
                final int n12 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
                final int n13 = 10 * toDigit(this.buf[this.pos++], s) + toDigit(this.buf[this.pos++], s);
                if (n12 >= 24 || n13 >= 60) {
                    throw new IOException("Parse " + s + " time, -hhmm");
                }
                time += (n12 * 60 + n13) * 60 * 1000;
                break;
            }
            case 90: {
                if (n != 1) {
                    throw new IOException("Parse " + s + " time, invalid format");
                }
                break;
            }
            default: {
                throw new IOException("Parse " + s + " time, garbage offset");
            }
        }
        return new Date(time);
    }
    
    private static int toDigit(final byte b, final String s) throws IOException {
        if (b < 48 || b > 57) {
            throw new IOException("Parse " + s + " time, invalid format");
        }
        return b - 48;
    }
}
