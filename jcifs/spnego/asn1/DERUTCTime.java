package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DERUTCTime extends DERObject
{
    String time;
    
    public static DERUTCTime getInstance(final Object obj) {
        if (obj == null || obj instanceof DERUTCTime) {
            return (DERUTCTime)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERUTCTime(((ASN1OctetString)obj).getOctets());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERUTCTime getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public DERUTCTime(final String time) {
        this.time = time;
    }
    
    public DERUTCTime(final Date time) {
        final SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmss'Z'");
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = dateF.format(time);
    }
    
    DERUTCTime(final byte[] bytes) {
        final char[] dateC = new char[bytes.length];
        for (int i = 0; i != dateC.length; ++i) {
            dateC[i] = (char)(bytes[i] & 0xFF);
        }
        this.time = new String(dateC);
    }
    
    public String getTime() {
        if (this.time.length() == 11) {
            return this.time.substring(0, 10) + "00GMT+00:00";
        }
        if (this.time.length() == 13) {
            return this.time.substring(0, 12) + "GMT+00:00";
        }
        if (this.time.length() == 17) {
            return this.time.substring(0, 12) + "GMT" + this.time.substring(12, 15) + ":" + this.time.substring(15, 17);
        }
        return this.time;
    }
    
    public String getAdjustedTime() {
        final String d = this.getTime();
        if (d.charAt(0) < '5') {
            return "20" + d;
        }
        return "19" + d;
    }
    
    private byte[] getOctets() {
        final char[] cs = this.time.toCharArray();
        final byte[] bs = new byte[cs.length];
        for (int i = 0; i != cs.length; ++i) {
            bs[i] = (byte)cs[i];
        }
        return bs;
    }
    
    void encode(final DEROutputStream out) throws IOException {
        out.writeEncoded(23, this.getOctets());
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERUTCTime && this.time.equals(((DERUTCTime)o).time);
    }
}
