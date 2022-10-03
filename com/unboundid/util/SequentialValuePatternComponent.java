package com.unboundid.util;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

final class SequentialValuePatternComponent extends ValuePatternComponent
{
    private static final long serialVersionUID = -3553865579642557953L;
    private final AtomicLong nextValue;
    private final long increment;
    private final long lowerBound;
    private final long upperBound;
    private final String formatString;
    private final ThreadLocal<DecimalFormat> decimalFormat;
    
    SequentialValuePatternComponent(final long lowerBound, final long upperBound, final long increment, final String formatString) {
        if (lowerBound == upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.increment = 0L;
        }
        else if (lowerBound > upperBound) {
            this.lowerBound = upperBound;
            this.upperBound = lowerBound;
            if (Math.abs(increment) > lowerBound - upperBound) {
                this.increment = 0L;
            }
            else {
                this.increment = -1L * increment;
            }
        }
        else {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            if (Math.abs(increment) > upperBound - lowerBound) {
                this.increment = 0L;
            }
            else {
                this.increment = increment;
            }
        }
        this.formatString = formatString;
        this.decimalFormat = new ThreadLocal<DecimalFormat>();
        this.nextValue = new AtomicLong(lowerBound);
    }
    
    @Override
    void append(final StringBuilder buffer) {
        long value = this.nextValue.getAndAdd(this.increment);
        if (value > this.upperBound) {
            if (this.nextValue.compareAndSet(value + this.increment, this.lowerBound)) {
                value = this.nextValue.getAndAdd(this.increment);
            }
            else {
                long v;
                do {
                    v = this.nextValue.get();
                } while (v >= this.upperBound && !this.nextValue.compareAndSet(v, this.lowerBound));
                value = this.nextValue.getAndAdd(this.increment);
            }
        }
        else if (value < this.lowerBound) {
            if (this.nextValue.compareAndSet(value + this.increment, this.upperBound)) {
                value = this.nextValue.getAndAdd(this.increment);
            }
            else {
                long v;
                do {
                    v = this.nextValue.get();
                } while (v <= this.lowerBound && !this.nextValue.compareAndSet(v, this.upperBound));
                value = this.nextValue.getAndAdd(this.increment);
            }
        }
        if (this.formatString == null) {
            buffer.append(value);
        }
        else {
            DecimalFormat f = this.decimalFormat.get();
            if (f == null) {
                f = new DecimalFormat(this.formatString);
                this.decimalFormat.set(f);
            }
            buffer.append(f.format(value));
        }
    }
    
    @Override
    boolean supportsBackReference() {
        return true;
    }
}
