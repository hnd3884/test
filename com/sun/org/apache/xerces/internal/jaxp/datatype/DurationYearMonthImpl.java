package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;

class DurationYearMonthImpl extends DurationImpl
{
    public DurationYearMonthImpl(final boolean isPositive, final BigInteger years, final BigInteger months) {
        super(isPositive, years, months, null, null, null, null);
        this.convertToCanonicalYearMonth();
    }
    
    protected DurationYearMonthImpl(final boolean isPositive, final int years, final int months) {
        this(isPositive, DurationImpl.wrap(years), DurationImpl.wrap(months));
    }
    
    protected DurationYearMonthImpl(final long durationInMilliseconds) {
        super(durationInMilliseconds);
        this.convertToCanonicalYearMonth();
        this.days = null;
        this.hours = null;
        this.minutes = null;
        this.seconds = null;
        this.signum = this.calcSignum(this.signum >= 0);
    }
    
    protected DurationYearMonthImpl(final String lexicalRepresentation) {
        super(lexicalRepresentation);
        if (this.getDays() > 0 || this.getHours() > 0 || this.getMinutes() > 0 || this.getSeconds() > 0) {
            throw new IllegalArgumentException("Trying to create an xdt:yearMonthDuration with an invalid lexical representation of \"" + lexicalRepresentation + "\", data model requires PnYnM.");
        }
        this.convertToCanonicalYearMonth();
    }
    
    public int getValue() {
        return this.getYears() * 12 + this.getMonths();
    }
    
    private void convertToCanonicalYearMonth() {
        while (this.getMonths() >= 12) {
            this.months = this.months.subtract(BigInteger.valueOf(12L));
            this.years = BigInteger.valueOf(this.getYears()).add(BigInteger.ONE);
        }
    }
}
