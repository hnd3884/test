package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;

class DurationDayTimeImpl extends DurationImpl
{
    public DurationDayTimeImpl(final boolean isPositive, final BigInteger days, final BigInteger hours, final BigInteger minutes, final BigDecimal seconds) {
        super(isPositive, null, null, days, hours, minutes, seconds);
        this.convertToCanonicalDayTime();
    }
    
    public DurationDayTimeImpl(final boolean isPositive, final int days, final int hours, final int minutes, final int seconds) {
        this(isPositive, DurationImpl.wrap(days), DurationImpl.wrap(hours), DurationImpl.wrap(minutes), (seconds != Integer.MIN_VALUE) ? new BigDecimal(String.valueOf(seconds)) : null);
    }
    
    protected DurationDayTimeImpl(final String lexicalRepresentation) {
        super(lexicalRepresentation);
        if (this.getYears() > 0 || this.getMonths() > 0) {
            throw new IllegalArgumentException("Trying to create an xdt:dayTimeDuration with an invalid lexical representation of \"" + lexicalRepresentation + "\", data model requires a format PnDTnHnMnS.");
        }
        this.convertToCanonicalDayTime();
    }
    
    protected DurationDayTimeImpl(final long durationInMilliseconds) {
        super(durationInMilliseconds);
        this.convertToCanonicalDayTime();
        this.years = null;
        this.months = null;
    }
    
    public float getValue() {
        final float sec = (this.seconds == null) ? 0.0f : this.seconds.floatValue();
        return ((this.getDays() * 24 + this.getHours()) * 60 + this.getMinutes()) * 60 + sec;
    }
    
    private void convertToCanonicalDayTime() {
        while (this.getSeconds() >= 60) {
            this.seconds = this.seconds.subtract(BigDecimal.valueOf(60L));
            this.minutes = BigInteger.valueOf(this.getMinutes()).add(BigInteger.ONE);
        }
        while (this.getMinutes() >= 60) {
            this.minutes = this.minutes.subtract(BigInteger.valueOf(60L));
            this.hours = BigInteger.valueOf(this.getHours()).add(BigInteger.ONE);
        }
        while (this.getHours() >= 24) {
            this.hours = this.hours.subtract(BigInteger.valueOf(24L));
            this.days = BigInteger.valueOf(this.getDays()).add(BigInteger.ONE);
        }
    }
}
