package org.bouncycastle.asn1.cms;

import java.text.ParseException;
import java.util.Locale;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.DERGeneralizedTime;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class Time extends ASN1Object implements ASN1Choice
{
    ASN1Primitive time;
    
    public static Time getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    @Deprecated
    public Time(final ASN1Primitive time) {
        if (!(time instanceof ASN1UTCTime) && !(time instanceof ASN1GeneralizedTime)) {
            throw new IllegalArgumentException("unknown object passed to Time");
        }
        this.time = time;
    }
    
    public Time(final Date date) {
        final SimpleTimeZone timeZone = new SimpleTimeZone(0, "Z");
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat.setTimeZone(timeZone);
        final String string = simpleDateFormat.format(date) + "Z";
        final int int1 = Integer.parseInt(string.substring(0, 4));
        if (int1 < 1950 || int1 > 2049) {
            this.time = new DERGeneralizedTime(string);
        }
        else {
            this.time = new DERUTCTime(string.substring(2));
        }
    }
    
    public Time(final Date date, final Locale locale) {
        final SimpleTimeZone timeZone = new SimpleTimeZone(0, "Z");
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", locale);
        simpleDateFormat.setTimeZone(timeZone);
        final String string = simpleDateFormat.format(date) + "Z";
        final int int1 = Integer.parseInt(string.substring(0, 4));
        if (int1 < 1950 || int1 > 2049) {
            this.time = new DERGeneralizedTime(string);
        }
        else {
            this.time = new DERUTCTime(string.substring(2));
        }
    }
    
    public static Time getInstance(final Object o) {
        if (o == null || o instanceof Time) {
            return (Time)o;
        }
        if (o instanceof ASN1UTCTime) {
            return new Time((ASN1Primitive)o);
        }
        if (o instanceof ASN1GeneralizedTime) {
            return new Time((ASN1Primitive)o);
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }
    
    public String getTime() {
        if (this.time instanceof ASN1UTCTime) {
            return ((ASN1UTCTime)this.time).getAdjustedTime();
        }
        return ((ASN1GeneralizedTime)this.time).getTime();
    }
    
    public Date getDate() {
        try {
            if (this.time instanceof ASN1UTCTime) {
                return ((ASN1UTCTime)this.time).getAdjustedDate();
            }
            return ((ASN1GeneralizedTime)this.time).getDate();
        }
        catch (final ParseException ex) {
            throw new IllegalStateException("invalid date string: " + ex.getMessage());
        }
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.time;
    }
}
