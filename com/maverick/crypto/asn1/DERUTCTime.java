package com.maverick.crypto.asn1;

import java.io.IOException;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DERUTCTime extends DERObject
{
    String gc;
    
    public static DERUTCTime getInstance(final Object o) {
        if (o == null || o instanceof DERUTCTime) {
            return (DERUTCTime)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERUTCTime(((ASN1OctetString)o).getOctets());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERUTCTime getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERUTCTime(final String gc) {
        this.gc = gc;
    }
    
    public DERUTCTime(final Date date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'");
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.gc = simpleDateFormat.format(date);
    }
    
    DERUTCTime(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        this.gc = new String(array2);
    }
    
    public String getTime() {
        if (this.gc.length() == 11) {
            return this.gc.substring(0, 10) + "00GMT+00:00";
        }
        if (this.gc.length() == 13) {
            return this.gc.substring(0, 12) + "GMT+00:00";
        }
        if (this.gc.length() == 17) {
            return this.gc.substring(0, 12) + "GMT" + this.gc.substring(12, 15) + ":" + this.gc.substring(15, 17);
        }
        return this.gc;
    }
    
    public String getAdjustedTime() {
        final String time = this.getTime();
        if (time.charAt(0) < '5') {
            return "20" + time;
        }
        return "19" + time;
    }
    
    private byte[] h() {
        final char[] charArray = this.gc.toCharArray();
        final byte[] array = new byte[charArray.length];
        for (int i = 0; i != charArray.length; ++i) {
            array[i] = (byte)charArray[i];
        }
        return array;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(23, this.h());
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERUTCTime && this.gc.equals(((DERUTCTime)o).gc);
    }
}
