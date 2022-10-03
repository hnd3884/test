package org.bouncycastle.asn1;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import org.bouncycastle.util.Strings;

public class ASN1UTCTime extends ASN1Primitive
{
    private byte[] time;
    
    public static ASN1UTCTime getInstance(final Object o) {
        if (o == null || o instanceof ASN1UTCTime) {
            return (ASN1UTCTime)o;
        }
        if (o instanceof byte[]) {
            try {
                return (ASN1UTCTime)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static ASN1UTCTime getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof ASN1UTCTime) {
            return getInstance(object);
        }
        return new ASN1UTCTime(((ASN1OctetString)object).getOctets());
    }
    
    public ASN1UTCTime(final String s) {
        this.time = Strings.toByteArray(s);
        try {
            this.getDate();
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException("invalid date string: " + ex.getMessage());
        }
    }
    
    public ASN1UTCTime(final Date date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'");
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = Strings.toByteArray(simpleDateFormat.format(date));
    }
    
    public ASN1UTCTime(final Date date, final Locale locale) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'", locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = Strings.toByteArray(simpleDateFormat.format(date));
    }
    
    ASN1UTCTime(final byte[] time) {
        this.time = time;
    }
    
    public Date getDate() throws ParseException {
        return new SimpleDateFormat("yyMMddHHmmssz").parse(this.getTime());
    }
    
    public Date getAdjustedDate() throws ParseException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssz");
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        return simpleDateFormat.parse(this.getAdjustedTime());
    }
    
    public String getTime() {
        final String fromByteArray = Strings.fromByteArray(this.time);
        if (fromByteArray.indexOf(45) < 0 && fromByteArray.indexOf(43) < 0) {
            if (fromByteArray.length() == 11) {
                return fromByteArray.substring(0, 10) + "00GMT+00:00";
            }
            return fromByteArray.substring(0, 12) + "GMT+00:00";
        }
        else {
            int n = fromByteArray.indexOf(45);
            if (n < 0) {
                n = fromByteArray.indexOf(43);
            }
            String string = fromByteArray;
            if (n == fromByteArray.length() - 3) {
                string += "00";
            }
            if (n == 10) {
                return string.substring(0, 10) + "00GMT" + string.substring(10, 13) + ":" + string.substring(13, 15);
            }
            return string.substring(0, 12) + "GMT" + string.substring(12, 15) + ":" + string.substring(15, 17);
        }
    }
    
    public String getAdjustedTime() {
        final String time = this.getTime();
        if (time.charAt(0) < '5') {
            return "20" + time;
        }
        return "19" + time;
    }
    
    @Override
    boolean isConstructed() {
        return false;
    }
    
    @Override
    int encodedLength() {
        final int length = this.time.length;
        return 1 + StreamUtil.calculateBodyLength(length) + length;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.write(23);
        final int length = this.time.length;
        asn1OutputStream.writeLength(length);
        for (int i = 0; i != length; ++i) {
            asn1OutputStream.write(this.time[i]);
        }
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof ASN1UTCTime && Arrays.areEqual(this.time, ((ASN1UTCTime)asn1Primitive).time);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.time);
    }
    
    @Override
    public String toString() {
        return Strings.fromByteArray(this.time);
    }
}
