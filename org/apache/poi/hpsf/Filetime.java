package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import java.util.Date;
import java.math.BigInteger;
import org.apache.poi.util.Internal;

@Internal
public class Filetime
{
    private static final BigInteger EPOCH_DIFF;
    private static final BigInteger NANO_100;
    private long fileTime;
    
    public Filetime() {
    }
    
    public Filetime(final Date date) {
        this.fileTime = dateToFileTime(date);
    }
    
    public void read(final LittleEndianByteArrayInputStream lei) {
        this.fileTime = lei.readLong();
    }
    
    public byte[] toByteArray() {
        final byte[] result = new byte[8];
        LittleEndian.putLong(result, 0, this.fileTime);
        return result;
    }
    
    public int write(final OutputStream out) throws IOException {
        out.write(this.toByteArray());
        return 8;
    }
    
    public Date getJavaValue() {
        return filetimeToDate(this.fileTime);
    }
    
    public static Date filetimeToDate(final long filetime) {
        final BigInteger bi = (filetime < 0L) ? twoComplement(filetime) : BigInteger.valueOf(filetime);
        return new Date(bi.divide(Filetime.NANO_100).add(Filetime.EPOCH_DIFF).longValue());
    }
    
    public static long dateToFileTime(final Date date) {
        return BigInteger.valueOf(date.getTime()).subtract(Filetime.EPOCH_DIFF).multiply(Filetime.NANO_100).longValue();
    }
    
    public static boolean isUndefined(final Date date) {
        return date == null || dateToFileTime(date) == 0L;
    }
    
    private static BigInteger twoComplement(final long val) {
        final byte[] contents = { (byte)((val < 0L) ? 0 : -1), (byte)(val >> 56 & 0xFFL), (byte)(val >> 48 & 0xFFL), (byte)(val >> 40 & 0xFFL), (byte)(val >> 32 & 0xFFL), (byte)(val >> 24 & 0xFFL), (byte)(val >> 16 & 0xFFL), (byte)(val >> 8 & 0xFFL), (byte)(val & 0xFFL) };
        return new BigInteger(contents);
    }
    
    static {
        EPOCH_DIFF = BigInteger.valueOf(-11644473600000L);
        NANO_100 = BigInteger.valueOf(10000L);
    }
}
