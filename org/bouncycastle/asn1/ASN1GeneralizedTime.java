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

public class ASN1GeneralizedTime extends ASN1Primitive
{
    protected byte[] time;
    
    public static ASN1GeneralizedTime getInstance(final Object o) {
        if (o == null || o instanceof ASN1GeneralizedTime) {
            return (ASN1GeneralizedTime)o;
        }
        if (o instanceof byte[]) {
            try {
                return (ASN1GeneralizedTime)ASN1Primitive.fromByteArray((byte[])o);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("encoding error in getInstance: " + ex.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static ASN1GeneralizedTime getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        final ASN1Primitive object = asn1TaggedObject.getObject();
        if (b || object instanceof ASN1GeneralizedTime) {
            return getInstance(object);
        }
        return new ASN1GeneralizedTime(((ASN1OctetString)object).getOctets());
    }
    
    public ASN1GeneralizedTime(final String s) {
        this.time = Strings.toByteArray(s);
        try {
            this.getDate();
        }
        catch (final ParseException ex) {
            throw new IllegalArgumentException("invalid date string: " + ex.getMessage());
        }
    }
    
    public ASN1GeneralizedTime(final Date date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = Strings.toByteArray(simpleDateFormat.format(date));
    }
    
    public ASN1GeneralizedTime(final Date date, final Locale locale) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'", locale);
        simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        this.time = Strings.toByteArray(simpleDateFormat.format(date));
    }
    
    ASN1GeneralizedTime(final byte[] time) {
        this.time = time;
    }
    
    public String getTimeString() {
        return Strings.fromByteArray(this.time);
    }
    
    public String getTime() {
        final String fromByteArray = Strings.fromByteArray(this.time);
        if (fromByteArray.charAt(fromByteArray.length() - 1) == 'Z') {
            return fromByteArray.substring(0, fromByteArray.length() - 1) + "GMT+00:00";
        }
        final int n = fromByteArray.length() - 5;
        final char char1 = fromByteArray.charAt(n);
        if (char1 == '-' || char1 == '+') {
            return fromByteArray.substring(0, n) + "GMT" + fromByteArray.substring(n, n + 3) + ":" + fromByteArray.substring(n + 3);
        }
        final int n2 = fromByteArray.length() - 3;
        final char char2 = fromByteArray.charAt(n2);
        if (char2 == '-' || char2 == '+') {
            return fromByteArray.substring(0, n2) + "GMT" + fromByteArray.substring(n2) + ":00";
        }
        return fromByteArray + this.calculateGMTOffset();
    }
    
    private String calculateGMTOffset() {
        String s = "+";
        final TimeZone default1 = TimeZone.getDefault();
        int rawOffset = default1.getRawOffset();
        if (rawOffset < 0) {
            s = "-";
            rawOffset = -rawOffset;
        }
        int n = rawOffset / 3600000;
        final int n2 = (rawOffset - n * 60 * 60 * 1000) / 60000;
        try {
            if (default1.useDaylightTime() && default1.inDaylightTime(this.getDate())) {
                n += (s.equals("+") ? 1 : -1);
            }
        }
        catch (final ParseException ex) {}
        return "GMT" + s + this.convert(n) + ":" + this.convert(n2);
    }
    
    private String convert(final int n) {
        if (n < 10) {
            return "0" + n;
        }
        return Integer.toString(n);
    }
    
    public Date getDate() throws ParseException {
        String s2;
        final String s = s2 = Strings.fromByteArray(this.time);
        SimpleDateFormat simpleDateFormat;
        if (s.endsWith("Z")) {
            if (this.hasFractionalSeconds()) {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS'Z'");
            }
            else if (this.hasSeconds()) {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
            }
            else if (this.hasMinutes()) {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm'Z'");
            }
            else {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHH'Z'");
            }
            simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        }
        else if (s.indexOf(45) > 0 || s.indexOf(43) > 0) {
            s2 = this.getTime();
            if (this.hasFractionalSeconds()) {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSSz");
            }
            else if (this.hasSeconds()) {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssz");
            }
            else if (this.hasMinutes()) {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmz");
            }
            else {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHz");
            }
            simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
        }
        else {
            if (this.hasFractionalSeconds()) {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
            }
            else if (this.hasSeconds()) {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            }
            else if (this.hasMinutes()) {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            }
            else {
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
            }
            simpleDateFormat.setTimeZone(new SimpleTimeZone(0, TimeZone.getDefault().getID()));
        }
        if (this.hasFractionalSeconds()) {
            String substring;
            int i;
            char char1;
            for (substring = s2.substring(14), i = 1; i < substring.length(); ++i) {
                char1 = substring.charAt(i);
                if ('0' > char1) {
                    break;
                }
                if (char1 > '9') {
                    break;
                }
            }
            if (i - 1 > 3) {
                s2 = s2.substring(0, 14) + (substring.substring(0, 4) + substring.substring(i));
            }
            else if (i - 1 == 1) {
                s2 = s2.substring(0, 14) + (substring.substring(0, i) + "00" + substring.substring(i));
            }
            else if (i - 1 == 2) {
                s2 = s2.substring(0, 14) + (substring.substring(0, i) + "0" + substring.substring(i));
            }
        }
        return simpleDateFormat.parse(s2);
    }
    
    protected boolean hasFractionalSeconds() {
        for (int i = 0; i != this.time.length; ++i) {
            if (this.time[i] == 46 && i == 14) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean hasSeconds() {
        return this.isDigit(12) && this.isDigit(13);
    }
    
    protected boolean hasMinutes() {
        return this.isDigit(10) && this.isDigit(11);
    }
    
    private boolean isDigit(final int n) {
        return this.time.length > n && this.time[n] >= 48 && this.time[n] <= 57;
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
        asn1OutputStream.writeEncoded(24, this.time);
    }
    
    @Override
    ASN1Primitive toDERObject() {
        return new DERGeneralizedTime(this.time);
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        return asn1Primitive instanceof ASN1GeneralizedTime && Arrays.areEqual(this.time, ((ASN1GeneralizedTime)asn1Primitive).time);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.time);
    }
}
