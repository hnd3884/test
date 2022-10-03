package java.math;

class SignedMutableBigInteger extends MutableBigInteger
{
    int sign;
    
    SignedMutableBigInteger() {
        this.sign = 1;
    }
    
    SignedMutableBigInteger(final int n) {
        super(n);
        this.sign = 1;
    }
    
    SignedMutableBigInteger(final MutableBigInteger mutableBigInteger) {
        super(mutableBigInteger);
        this.sign = 1;
    }
    
    void signedAdd(final SignedMutableBigInteger signedMutableBigInteger) {
        if (this.sign == signedMutableBigInteger.sign) {
            this.add(signedMutableBigInteger);
        }
        else {
            this.sign *= this.subtract(signedMutableBigInteger);
        }
    }
    
    void signedAdd(final MutableBigInteger mutableBigInteger) {
        if (this.sign == 1) {
            this.add(mutableBigInteger);
        }
        else {
            this.sign *= this.subtract(mutableBigInteger);
        }
    }
    
    void signedSubtract(final SignedMutableBigInteger signedMutableBigInteger) {
        if (this.sign == signedMutableBigInteger.sign) {
            this.sign *= this.subtract(signedMutableBigInteger);
        }
        else {
            this.add(signedMutableBigInteger);
        }
    }
    
    void signedSubtract(final MutableBigInteger mutableBigInteger) {
        if (this.sign == 1) {
            this.sign *= this.subtract(mutableBigInteger);
        }
        else {
            this.add(mutableBigInteger);
        }
        if (this.intLen == 0) {
            this.sign = 1;
        }
    }
    
    @Override
    public String toString() {
        return this.toBigInteger(this.sign).toString();
    }
}
