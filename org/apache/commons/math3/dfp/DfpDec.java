package org.apache.commons.math3.dfp;

public class DfpDec extends Dfp
{
    protected DfpDec(final DfpField factory) {
        super(factory);
    }
    
    protected DfpDec(final DfpField factory, final byte x) {
        super(factory, x);
    }
    
    protected DfpDec(final DfpField factory, final int x) {
        super(factory, x);
    }
    
    protected DfpDec(final DfpField factory, final long x) {
        super(factory, x);
    }
    
    protected DfpDec(final DfpField factory, final double x) {
        super(factory, x);
        this.round(0);
    }
    
    public DfpDec(final Dfp d) {
        super(d);
        this.round(0);
    }
    
    protected DfpDec(final DfpField factory, final String s) {
        super(factory, s);
        this.round(0);
    }
    
    protected DfpDec(final DfpField factory, final byte sign, final byte nans) {
        super(factory, sign, nans);
    }
    
    @Override
    public Dfp newInstance() {
        return new DfpDec(this.getField());
    }
    
    @Override
    public Dfp newInstance(final byte x) {
        return new DfpDec(this.getField(), x);
    }
    
    @Override
    public Dfp newInstance(final int x) {
        return new DfpDec(this.getField(), x);
    }
    
    @Override
    public Dfp newInstance(final long x) {
        return new DfpDec(this.getField(), x);
    }
    
    @Override
    public Dfp newInstance(final double x) {
        return new DfpDec(this.getField(), x);
    }
    
    @Override
    public Dfp newInstance(final Dfp d) {
        if (this.getField().getRadixDigits() != d.getField().getRadixDigits()) {
            this.getField().setIEEEFlagsBits(1);
            final Dfp result = this.newInstance(this.getZero());
            result.nans = 3;
            return this.dotrap(1, "newInstance", d, result);
        }
        return new DfpDec(d);
    }
    
    @Override
    public Dfp newInstance(final String s) {
        return new DfpDec(this.getField(), s);
    }
    
    @Override
    public Dfp newInstance(final byte sign, final byte nans) {
        return new DfpDec(this.getField(), sign, nans);
    }
    
    protected int getDecimalDigits() {
        return this.getRadixDigits() * 4 - 3;
    }
    
    @Override
    protected int round(final int in) {
        final int msb = this.mant[this.mant.length - 1];
        if (msb == 0) {
            return 0;
        }
        int cmaxdigits = this.mant.length * 4;
        for (int lsbthreshold = 1000; lsbthreshold > msb; lsbthreshold /= 10, --cmaxdigits) {}
        final int digits = this.getDecimalDigits();
        final int lsbshift = cmaxdigits - digits;
        final int lsd = lsbshift / 4;
        int lsbthreshold = 1;
        for (int i = 0; i < lsbshift % 4; ++i) {
            lsbthreshold *= 10;
        }
        final int lsb = this.mant[lsd];
        if (lsbthreshold <= 1 && digits == 4 * this.mant.length - 3) {
            return super.round(in);
        }
        int discarded = in;
        int n;
        if (lsbthreshold == 1) {
            n = this.mant[lsd - 1] / 1000 % 10;
            final int[] mant = this.mant;
            final int n2 = lsd - 1;
            mant[n2] %= 1000;
            discarded |= this.mant[lsd - 1];
        }
        else {
            n = lsb * 10 / lsbthreshold % 10;
            discarded |= lsb % (lsbthreshold / 10);
        }
        for (int j = 0; j < lsd; ++j) {
            discarded |= this.mant[j];
            this.mant[j] = 0;
        }
        this.mant[lsd] = lsb / lsbthreshold * lsbthreshold;
        boolean inc = false;
        switch (this.getField().getRoundingMode()) {
            case ROUND_DOWN: {
                inc = false;
                break;
            }
            case ROUND_UP: {
                inc = (n != 0 || discarded != 0);
                break;
            }
            case ROUND_HALF_UP: {
                inc = (n >= 5);
                break;
            }
            case ROUND_HALF_DOWN: {
                inc = (n > 5);
                break;
            }
            case ROUND_HALF_EVEN: {
                inc = (n > 5 || (n == 5 && discarded != 0) || (n == 5 && discarded == 0 && (lsb / lsbthreshold & 0x1) == 0x1));
                break;
            }
            case ROUND_HALF_ODD: {
                inc = (n > 5 || (n == 5 && discarded != 0) || (n == 5 && discarded == 0 && (lsb / lsbthreshold & 0x1) == 0x0));
                break;
            }
            case ROUND_CEIL: {
                inc = (this.sign == 1 && (n != 0 || discarded != 0));
                break;
            }
            default: {
                inc = (this.sign == -1 && (n != 0 || discarded != 0));
                break;
            }
        }
        if (inc) {
            int rh = lsbthreshold;
            for (int k = lsd; k < this.mant.length; ++k) {
                final int r = this.mant[k] + rh;
                rh = r / 10000;
                this.mant[k] = r % 10000;
            }
            if (rh != 0) {
                this.shiftRight();
                this.mant[this.mant.length - 1] = rh;
            }
        }
        if (this.exp < -32767) {
            this.getField().setIEEEFlagsBits(8);
            return 8;
        }
        if (this.exp > 32768) {
            this.getField().setIEEEFlagsBits(4);
            return 4;
        }
        if (n != 0 || discarded != 0) {
            this.getField().setIEEEFlagsBits(16);
            return 16;
        }
        return 0;
    }
    
    @Override
    public Dfp nextAfter(final Dfp x) {
        final String trapName = "nextAfter";
        if (this.getField().getRadixDigits() != x.getField().getRadixDigits()) {
            this.getField().setIEEEFlagsBits(1);
            final Dfp result = this.newInstance(this.getZero());
            result.nans = 3;
            return this.dotrap(1, "nextAfter", x, result);
        }
        boolean up = false;
        if (this.lessThan(x)) {
            up = true;
        }
        if (this.equals(x)) {
            return this.newInstance(x);
        }
        if (this.lessThan(this.getZero())) {
            up = !up;
        }
        Dfp result2;
        if (up) {
            Dfp inc = this.power10(this.intLog10() - this.getDecimalDigits() + 1);
            inc = Dfp.copysign(inc, this);
            if (this.equals(this.getZero())) {
                inc = this.power10K(-32767 - this.mant.length - 1);
            }
            if (inc.equals(this.getZero())) {
                result2 = Dfp.copysign(this.newInstance(this.getZero()), this);
            }
            else {
                result2 = this.add(inc);
            }
        }
        else {
            Dfp inc = this.power10(this.intLog10());
            inc = Dfp.copysign(inc, this);
            if (this.equals(inc)) {
                inc = inc.divide(this.power10(this.getDecimalDigits()));
            }
            else {
                inc = inc.divide(this.power10(this.getDecimalDigits() - 1));
            }
            if (this.equals(this.getZero())) {
                inc = this.power10K(-32767 - this.mant.length - 1);
            }
            if (inc.equals(this.getZero())) {
                result2 = Dfp.copysign(this.newInstance(this.getZero()), this);
            }
            else {
                result2 = this.subtract(inc);
            }
        }
        if (result2.classify() == 1 && this.classify() != 1) {
            this.getField().setIEEEFlagsBits(16);
            result2 = this.dotrap(16, "nextAfter", x, result2);
        }
        if (result2.equals(this.getZero()) && !this.equals(this.getZero())) {
            this.getField().setIEEEFlagsBits(16);
            result2 = this.dotrap(16, "nextAfter", x, result2);
        }
        return result2;
    }
}
