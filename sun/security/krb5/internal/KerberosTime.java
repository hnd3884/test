package sun.security.krb5.internal;

import sun.security.krb5.KrbException;
import sun.security.krb5.Config;
import sun.security.util.DerValue;
import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerOutputStream;
import java.time.Instant;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import sun.security.krb5.Asn1Exception;

public class KerberosTime
{
    private final long kerberosTime;
    private final int microSeconds;
    private static long initMilli;
    private static long initMicro;
    private static boolean DEBUG;
    
    private KerberosTime(final long kerberosTime, final int microSeconds) {
        this.kerberosTime = kerberosTime;
        this.microSeconds = microSeconds;
    }
    
    public KerberosTime(final long n) {
        this(n, 0);
    }
    
    public KerberosTime(final String s) throws Asn1Exception {
        this(toKerberosTime(s), 0);
    }
    
    private static long toKerberosTime(final String s) throws Asn1Exception {
        if (s.length() != 15) {
            throw new Asn1Exception(900);
        }
        if (s.charAt(14) != 'Z') {
            throw new Asn1Exception(900);
        }
        final int int1 = Integer.parseInt(s.substring(0, 4));
        final Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.clear();
        instance.set(int1, Integer.parseInt(s.substring(4, 6)) - 1, Integer.parseInt(s.substring(6, 8)), Integer.parseInt(s.substring(8, 10)), Integer.parseInt(s.substring(10, 12)), Integer.parseInt(s.substring(12, 14)));
        return instance.getTimeInMillis();
    }
    
    public KerberosTime(final Date date) {
        this(date.getTime(), 0);
    }
    
    public KerberosTime(final Instant instant) {
        this(instant.getEpochSecond() * 1000L + instant.getNano() / 1000000L, instant.getNano() / 1000 % 1000);
    }
    
    public static KerberosTime now() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long initMicro = System.nanoTime() / 1000L;
        final long n = initMicro - KerberosTime.initMicro;
        final long n2 = KerberosTime.initMilli + n / 1000L;
        if (n2 - currentTimeMillis > 100L || currentTimeMillis - n2 > 100L) {
            if (KerberosTime.DEBUG) {
                System.out.println("System time adjusted");
            }
            KerberosTime.initMilli = currentTimeMillis;
            KerberosTime.initMicro = initMicro;
            return new KerberosTime(currentTimeMillis, 0);
        }
        return new KerberosTime(n2, (int)(n % 1000L));
    }
    
    public String toGeneralizedTimeString() {
        final Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.clear();
        instance.setTimeInMillis(this.kerberosTime);
        return String.format("%04d%02d%02d%02d%02d%02dZ", instance.get(1), instance.get(2) + 1, instance.get(5), instance.get(11), instance.get(12), instance.get(13));
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putGeneralizedTime(this.toDate());
        return derOutputStream.toByteArray();
    }
    
    public long getTime() {
        return this.kerberosTime;
    }
    
    public Date toDate() {
        return new Date(this.kerberosTime);
    }
    
    public int getMicroSeconds() {
        return new Long(this.kerberosTime % 1000L * 1000L).intValue() + this.microSeconds;
    }
    
    public KerberosTime withMicroSeconds(final int n) {
        return new KerberosTime(this.kerberosTime - this.kerberosTime % 1000L + n / 1000L, n % 1000);
    }
    
    private boolean inClockSkew(final int n) {
        return Math.abs(this.kerberosTime - System.currentTimeMillis()) <= n * 1000L;
    }
    
    public boolean inClockSkew() {
        return this.inClockSkew(getDefaultSkew());
    }
    
    public boolean greaterThanWRTClockSkew(final KerberosTime kerberosTime, final int n) {
        return this.kerberosTime - kerberosTime.kerberosTime > n * 1000L;
    }
    
    public boolean greaterThanWRTClockSkew(final KerberosTime kerberosTime) {
        return this.greaterThanWRTClockSkew(kerberosTime, getDefaultSkew());
    }
    
    public boolean greaterThan(final KerberosTime kerberosTime) {
        return this.kerberosTime > kerberosTime.kerberosTime || (this.kerberosTime == kerberosTime.kerberosTime && this.microSeconds > kerberosTime.microSeconds);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof KerberosTime && this.kerberosTime == ((KerberosTime)o).kerberosTime && this.microSeconds == ((KerberosTime)o).microSeconds);
    }
    
    @Override
    public int hashCode() {
        return (629 + (int)(this.kerberosTime ^ this.kerberosTime >>> 32)) * 17 + this.microSeconds;
    }
    
    public boolean isZero() {
        return this.kerberosTime == 0L && this.microSeconds == 0;
    }
    
    public int getSeconds() {
        return new Long(this.kerberosTime / 1000L).intValue();
    }
    
    public static KerberosTime parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new KerberosTime(derValue.getData().getDerValue().getGeneralizedTime().getTime(), 0);
    }
    
    public static int getDefaultSkew() {
        int intValue = 300;
        try {
            if ((intValue = Config.getInstance().getIntValue("libdefaults", "clockskew")) == Integer.MIN_VALUE) {
                intValue = 300;
            }
        }
        catch (final KrbException ex) {
            if (KerberosTime.DEBUG) {
                System.out.println("Exception in getting clockskew from Configuration using default value " + ex.getMessage());
            }
        }
        return intValue;
    }
    
    @Override
    public String toString() {
        return this.toGeneralizedTimeString();
    }
    
    static {
        KerberosTime.initMilli = System.currentTimeMillis();
        KerberosTime.initMicro = System.nanoTime() / 1000L;
        KerberosTime.DEBUG = Krb5.DEBUG;
    }
}
