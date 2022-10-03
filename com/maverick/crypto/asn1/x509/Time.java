package com.maverick.crypto.asn1.x509;

import java.text.ParsePosition;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.util.Date;
import com.maverick.crypto.asn1.DERGeneralizedTime;
import com.maverick.crypto.asn1.DERUTCTime;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import com.maverick.crypto.asn1.DERObject;
import com.maverick.crypto.asn1.DEREncodable;

public class Time implements DEREncodable
{
    DERObject t;
    
    public static Time getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public Time(final DERObject t) {
        if (!(t instanceof DERUTCTime) && !(t instanceof DERGeneralizedTime)) {
            throw new IllegalArgumentException("unknown object passed to Time");
        }
        this.t = t;
    }
    
    public Time(final Date date) {
        final SimpleTimeZone timeZone = new SimpleTimeZone(0, "Z");
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat.setTimeZone(timeZone);
        final String string = simpleDateFormat.format(date) + "Z";
        final int int1 = Integer.parseInt(string.substring(0, 4));
        if (int1 < 1950 || int1 > 2049) {
            this.t = new DERGeneralizedTime(string);
        }
        else {
            this.t = new DERUTCTime(string.substring(2));
        }
    }
    
    public static Time getInstance(final Object o) {
        if (o instanceof Time) {
            return (Time)o;
        }
        if (o instanceof DERUTCTime) {
            return new Time((DERObject)o);
        }
        if (o instanceof DERGeneralizedTime) {
            return new Time((DERObject)o);
        }
        throw new IllegalArgumentException("unknown object in factory");
    }
    
    public String getTime() {
        if (this.t instanceof DERUTCTime) {
            return ((DERUTCTime)this.t).getAdjustedTime();
        }
        return ((DERGeneralizedTime)this.t).getTime();
    }
    
    public Date getDate() {
        return new SimpleDateFormat("yyyyMMddHHmmssz").parse(this.getTime(), new ParsePosition(0));
    }
    
    public DERObject getDERObject() {
        return this.t;
    }
}
