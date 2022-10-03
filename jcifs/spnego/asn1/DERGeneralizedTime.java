package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DERGeneralizedTime extends DERObject
{
    String time;
    
    public static DERGeneralizedTime getInstance(final Object obj) {
        if (obj == null || obj instanceof DERGeneralizedTime) {
            return (DERGeneralizedTime)obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new DERGeneralizedTime(((ASN1OctetString)obj).getOctets());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }
    
    public static DERGeneralizedTime getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        return getInstance(obj.getObject());
    }
    
    public DERGeneralizedTime(final String time) {
        this.time = time;
    }
    
    public DERGeneralizedTime(final Date time) {
        final SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = dateF.format(time);
    }
    
    DERGeneralizedTime(final byte[] bytes) {
        final char[] dateC = new char[bytes.length];
        for (int i = 0; i != dateC.length; ++i) {
            dateC[i] = (char)(bytes[i] & 0xFF);
        }
        this.time = new String(dateC);
    }
    
    public String getTime() {
        if (this.time.length() == 15) {
            return this.time.substring(0, 14) + "GMT+00:00";
        }
        if (this.time.length() == 17) {
            return this.time.substring(0, 14) + "GMT" + this.time.substring(15, 17) + ":" + this.time.substring(17, 19);
        }
        return this.time;
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
        out.writeEncoded(24, this.getOctets());
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERGeneralizedTime && this.time.equals(((DERGeneralizedTime)o).time);
    }
}
