package org.bouncycastle.asn1.eac;

import org.bouncycastle.util.Arrays;
import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PackedDate
{
    private byte[] time;
    
    public PackedDate(final String s) {
        this.time = this.convert(s);
    }
    
    public PackedDate(final Date date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd'Z'");
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = this.convert(simpleDateFormat.format(date));
    }
    
    public PackedDate(final Date date, final Locale locale) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd'Z'", locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = this.convert(simpleDateFormat.format(date));
    }
    
    private byte[] convert(final String s) {
        final char[] charArray = s.toCharArray();
        final byte[] array = new byte[6];
        for (int i = 0; i != 6; ++i) {
            array[i] = (byte)(charArray[i] - '0');
        }
        return array;
    }
    
    PackedDate(final byte[] time) {
        this.time = time;
    }
    
    public Date getDate() throws ParseException {
        return new SimpleDateFormat("yyyyMMdd").parse("20" + this.toString());
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.time);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof PackedDate && Arrays.areEqual(this.time, ((PackedDate)o).time);
    }
    
    @Override
    public String toString() {
        final char[] array = new char[this.time.length];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (char)((this.time[i] & 0xFF) + 48);
        }
        return new String(array);
    }
    
    public byte[] getEncoding() {
        return Arrays.clone(this.time);
    }
}
