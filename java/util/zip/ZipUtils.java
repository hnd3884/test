package java.util.zip;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.nio.file.attribute.FileTime;

class ZipUtils
{
    private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
    
    public static final FileTime winTimeToFileTime(final long n) {
        return FileTime.from(n / 10L - 11644473600000000L, TimeUnit.MICROSECONDS);
    }
    
    public static final long fileTimeToWinTime(final FileTime fileTime) {
        return (fileTime.to(TimeUnit.MICROSECONDS) + 11644473600000000L) * 10L;
    }
    
    public static final FileTime unixTimeToFileTime(final long n) {
        return FileTime.from(n, TimeUnit.SECONDS);
    }
    
    public static final long fileTimeToUnixTime(final FileTime fileTime) {
        return fileTime.to(TimeUnit.SECONDS);
    }
    
    private static long dosToJavaTime(final long n) {
        return new Date((int)((n >> 25 & 0x7FL) + 80L), (int)((n >> 21 & 0xFL) - 1L), (int)(n >> 16 & 0x1FL), (int)(n >> 11 & 0x1FL), (int)(n >> 5 & 0x3FL), (int)(n << 1 & 0x3EL)).getTime();
    }
    
    public static long extendedDosToJavaTime(final long n) {
        return dosToJavaTime(n) + (n >> 32);
    }
    
    private static long javaToDosTime(final long n) {
        final Date date = new Date(n);
        final int n2 = date.getYear() + 1900;
        if (n2 < 1980) {
            return 2162688L;
        }
        return n2 - 1980 << 25 | date.getMonth() + 1 << 21 | date.getDate() << 16 | date.getHours() << 11 | date.getMinutes() << 5 | date.getSeconds() >> 1;
    }
    
    public static long javaToExtendedDosTime(final long n) {
        if (n < 0L) {
            return 2162688L;
        }
        final long javaToDosTime = javaToDosTime(n);
        return (javaToDosTime != 2162688L) ? (javaToDosTime + (n % 2000L << 32)) : 2162688L;
    }
    
    public static final int get16(final byte[] array, final int n) {
        return Byte.toUnsignedInt(array[n]) | Byte.toUnsignedInt(array[n + 1]) << 8;
    }
    
    public static final long get32(final byte[] array, final int n) {
        return ((long)get16(array, n) | (long)get16(array, n + 2) << 16) & 0xFFFFFFFFL;
    }
    
    public static final long get64(final byte[] array, final int n) {
        return get32(array, n) | get32(array, n + 4) << 32;
    }
}
