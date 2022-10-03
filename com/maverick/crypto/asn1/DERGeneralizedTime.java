package com.maverick.crypto.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DERGeneralizedTime extends DERObject
{
    String wb;
    
    public static DERGeneralizedTime getInstance(final Object o) {
        if (o == null || o instanceof DERGeneralizedTime) {
            return (DERGeneralizedTime)o;
        }
        if (o instanceof ASN1OctetString) {
            return new DERGeneralizedTime(((ASN1OctetString)o).getOctets());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DERGeneralizedTime getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public DERGeneralizedTime(final String wb) {
        this.wb = wb;
    }
    
    public DERGeneralizedTime(final Date date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.wb = simpleDateFormat.format(date);
    }
    
    DERGeneralizedTime(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        this.wb = new String(array2);
    }
    
    public String getTime() {
        if (this.wb.charAt(this.wb.length() - 1) == 'Z') {
            return this.wb.substring(0, this.wb.length() - 1) + "GMT+00:00";
        }
        final int n = this.wb.length() - 5;
        final char char1 = this.wb.charAt(n);
        if (char1 == '-' || char1 == '+') {
            return this.wb.substring(0, n) + "GMT" + this.wb.substring(n, n + 3) + ":" + this.wb.substring(n + 3);
        }
        final int n2 = this.wb.length() - 3;
        final char char2 = this.wb.charAt(n2);
        if (char2 == '-' || char2 == '+') {
            return this.wb.substring(0, n2) + "GMT" + this.wb.substring(n2) + ":00";
        }
        return this.wb;
    }
    
    public Date getDate() throws ParseException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        return simpleDateFormat.parse(this.wb);
    }
    
    private byte[] f() {
        final char[] charArray = this.wb.toCharArray();
        final byte[] array = new byte[charArray.length];
        for (int i = 0; i != charArray.length; ++i) {
            array[i] = (byte)charArray[i];
        }
        return array;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(24, this.f());
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof DERGeneralizedTime && this.wb.equals(((DERGeneralizedTime)o).wb);
    }
}
