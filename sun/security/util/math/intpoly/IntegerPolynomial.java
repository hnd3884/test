package sun.security.util.math.intpoly;

import sun.security.util.math.MutableIntegerModuloP;
import sun.security.util.math.IntegerModuloP;
import sun.security.util.math.ImmutableIntegerModuloP;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.nio.ByteBuffer;
import sun.security.util.math.SmallValue;
import java.math.BigInteger;
import sun.security.util.math.IntegerFieldModuloP;

public abstract class IntegerPolynomial implements IntegerFieldModuloP
{
    protected static final BigInteger TWO;
    protected final int numLimbs;
    private final BigInteger modulus;
    protected final int bitsPerLimb;
    private final long[] posModLimbs;
    private final int maxAdds;
    
    protected abstract void reduce(final long[] p0);
    
    protected void multByInt(final long[] array, final long n) {
        for (int i = 0; i < array.length; ++i) {
            final int n2 = i;
            array[n2] *= n;
        }
        this.reduce(array);
    }
    
    protected abstract void mult(final long[] p0, final long[] p1, final long[] p2);
    
    protected abstract void square(final long[] p0, final long[] p1);
    
    IntegerPolynomial(final int bitsPerLimb, final int numLimbs, final int maxAdds, final BigInteger modulus) {
        this.numLimbs = numLimbs;
        this.modulus = modulus;
        this.bitsPerLimb = bitsPerLimb;
        this.maxAdds = maxAdds;
        this.posModLimbs = this.setPosModLimbs();
    }
    
    private long[] setPosModLimbs() {
        final long[] array = new long[this.numLimbs];
        this.setLimbsValuePositive(this.modulus, array);
        return array;
    }
    
    protected int getNumLimbs() {
        return this.numLimbs;
    }
    
    public int getMaxAdds() {
        return this.maxAdds;
    }
    
    @Override
    public BigInteger getSize() {
        return this.modulus;
    }
    
    @Override
    public ImmutableElement get0() {
        return new ImmutableElement(false);
    }
    
    @Override
    public ImmutableElement get1() {
        return new ImmutableElement(true);
    }
    
    @Override
    public ImmutableElement getElement(final BigInteger bigInteger) {
        return new ImmutableElement(bigInteger);
    }
    
    @Override
    public SmallValue getSmallValue(final int n) {
        final int n2 = 1 << this.bitsPerLimb - 1;
        if (Math.abs(n) >= n2) {
            throw new IllegalArgumentException("max magnitude is " + n2);
        }
        return new Limb(n);
    }
    
    protected void encode(final ByteBuffer byteBuffer, final int n, final byte b, final long[] array) {
        final int n2 = (8 * n + (32 - Integer.numberOfLeadingZeros(b)) + this.bitsPerLimb - 1) / this.bitsPerLimb;
        if (n2 > this.numLimbs) {
            final long[] array2 = new long[n2];
            this.encodeSmall(byteBuffer, n, b, array2);
            System.arraycopy(array2, 0, array, 0, array.length);
        }
        else {
            this.encodeSmall(byteBuffer, n, b, array);
        }
    }
    
    protected void encodeSmall(final ByteBuffer byteBuffer, final int n, final byte b, final long[] array) {
        int n2 = 0;
        long n3 = 0L;
        int n4 = 0;
        for (int i = 0; i < n; ++i) {
            final long n5 = byteBuffer.get() & 0xFF;
            if (n4 + 8 >= this.bitsPerLimb) {
                final int n6 = this.bitsPerLimb - n4;
                array[n2++] = n3 + ((n5 & (long)(255 >> 8 - n6)) << n4);
                n3 = n5 >> n6;
                n4 = 8 - n6;
            }
            else {
                n3 += n5 << n4;
                n4 += 8;
            }
        }
        if (b != 0) {
            final long n7 = b & 0xFF;
            if (n4 + 8 >= this.bitsPerLimb) {
                final int n8 = this.bitsPerLimb - n4;
                array[n2++] = n3 + ((n7 & (long)(255 >> 8 - n8)) << n4);
                n3 = n7 >> n8;
            }
            else {
                n3 += n7 << n4;
            }
        }
        if (n2 < array.length) {
            array[n2++] = n3;
        }
        Arrays.fill(array, n2, array.length, 0L);
        this.postEncodeCarry(array);
    }
    
    protected void encode(final byte[] array, final int n, final int n2, final byte b, final long[] array2) {
        final ByteBuffer wrap = ByteBuffer.wrap(array, n, n2);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        this.encode(wrap, n2, b, array2);
    }
    
    protected void postEncodeCarry(final long[] array) {
        this.reduce(array);
    }
    
    @Override
    public ImmutableElement getElement(final byte[] array, final int n, final int n2, final byte b) {
        final long[] array2 = new long[this.numLimbs];
        this.encode(array, n, n2, b, array2);
        return new ImmutableElement(array2, 0);
    }
    
    protected BigInteger evaluate(final long[] array) {
        BigInteger bigInteger = BigInteger.ZERO;
        for (int i = array.length - 1; i >= 0; --i) {
            bigInteger = bigInteger.shiftLeft(this.bitsPerLimb).add(BigInteger.valueOf(array[i]));
        }
        return bigInteger.mod(this.modulus);
    }
    
    protected long carryValue(final long n) {
        return n + (1 << this.bitsPerLimb - 1) >> this.bitsPerLimb;
    }
    
    protected void carry(final long[] array, final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            final long carryOut = this.carryOut(array, i);
            final int n3 = i + 1;
            array[n3] += carryOut;
        }
    }
    
    protected void carry(final long[] array) {
        this.carry(array, 0, array.length - 1);
    }
    
    protected long carryOut(final long[] array, final int n) {
        final long carryValue = this.carryValue(array[n]);
        array[n] -= carryValue << this.bitsPerLimb;
        return carryValue;
    }
    
    private void setLimbsValue(final BigInteger bigInteger, final long[] array) {
        this.setLimbsValuePositive(bigInteger, array);
        this.carry(array);
    }
    
    protected void setLimbsValuePositive(BigInteger shiftRight, final long[] array) {
        final BigInteger value = BigInteger.valueOf(1 << this.bitsPerLimb);
        for (int i = 0; i < array.length; ++i) {
            array[i] = shiftRight.mod(value).longValue();
            shiftRight = shiftRight.shiftRight(this.bitsPerLimb);
        }
    }
    
    protected abstract void finalCarryReduceLast(final long[] p0);
    
    protected void finalReduce(final long[] array) {
        for (int i = 0; i < 2; ++i) {
            this.finalCarryReduceLast(array);
            long n = 0L;
            for (int j = 0; j < this.numLimbs - 1; ++j) {
                final int n2 = j;
                array[n2] += n;
                n = array[j] >> this.bitsPerLimb;
                final int n3 = j;
                array[n3] -= n << this.bitsPerLimb;
            }
            final int n4 = this.numLimbs - 1;
            array[n4] += n;
        }
        int n5 = 1;
        final long[] array2 = new long[this.numLimbs];
        for (int k = this.numLimbs - 1; k >= 0; --k) {
            array2[k] = array[k] - this.posModLimbs[k];
            n5 *= (int)(array2[k] >> 63) + 1;
        }
        conditionalSwap(n5, array, array2);
    }
    
    protected void decode(final long[] array, final byte[] array2, final int n, final int n2) {
        int n3 = 0;
        long n4 = array[n3++];
        int n5 = 0;
        for (int i = 0; i < n2; ++i) {
            final int n6 = i + n;
            if (n5 + 8 >= this.bitsPerLimb) {
                array2[n6] = (byte)n4;
                long n7 = 0L;
                if (n3 < array.length) {
                    n7 = array[n3++];
                }
                final int n8 = this.bitsPerLimb - n5;
                final int n9 = 8 - n8;
                final int n10 = n6;
                array2[n10] += (byte)((n7 & (long)(255 >> n8)) << n8);
                n4 = n7 >> n9;
                n5 = n9;
            }
            else {
                array2[n6] = (byte)n4;
                n4 >>= 8;
                n5 += 8;
            }
        }
    }
    
    protected void addLimbs(final long[] array, final long[] array2, final long[] array3) {
        for (int i = 0; i < array3.length; ++i) {
            array3[i] = array[i] + array2[i];
        }
    }
    
    protected static void conditionalAssign(final int n, final long[] array, final long[] array2) {
        final int n2 = 0 - n;
        for (int i = 0; i < array.length; ++i) {
            array[i] ^= ((long)n2 & (array[i] ^ array2[i]));
        }
    }
    
    protected static void conditionalSwap(final int n, final long[] array, final long[] array2) {
        final int n2 = 0 - n;
        for (int i = 0; i < array.length; ++i) {
            final long n3 = (long)n2 & (array[i] ^ array2[i]);
            array[i] ^= n3;
            array2[i] ^= n3;
        }
    }
    
    protected void limbsToByteArray(final long[] array, final byte[] array2) {
        final long[] array3 = array.clone();
        this.finalReduce(array3);
        this.decode(array3, array2, 0, array2.length);
    }
    
    protected void addLimbsModPowerTwo(final long[] array, final long[] array2, final byte[] array3) {
        final long[] array4 = array2.clone();
        final long[] array5 = array.clone();
        this.finalReduce(array4);
        this.finalReduce(array5);
        this.addLimbs(array5, array4, array5);
        long n = 0L;
        for (int i = 0; i < this.numLimbs; ++i) {
            final long[] array6 = array5;
            final int n2 = i;
            array6[n2] += n;
            n = array5[i] >> this.bitsPerLimb;
            final long[] array7 = array5;
            final int n3 = i;
            array7[n3] -= n << this.bitsPerLimb;
        }
        this.decode(array5, array3, 0, array3.length);
    }
    
    static {
        TWO = BigInteger.valueOf(2L);
    }
    
    private abstract class Element implements IntegerModuloP
    {
        protected long[] limbs;
        protected int numAdds;
        
        public Element(final BigInteger value) {
            this.limbs = new long[IntegerPolynomial.this.numLimbs];
            this.setValue(value);
        }
        
        public Element(final boolean b) {
            (this.limbs = new long[IntegerPolynomial.this.numLimbs])[0] = (b ? 1 : 0);
            this.numAdds = 0;
        }
        
        private Element(final long[] limbs, final int numAdds) {
            this.limbs = limbs;
            this.numAdds = numAdds;
        }
        
        private void setValue(final BigInteger bigInteger) {
            IntegerPolynomial.this.setLimbsValue(bigInteger, this.limbs);
            this.numAdds = 0;
        }
        
        @Override
        public IntegerFieldModuloP getField() {
            return IntegerPolynomial.this;
        }
        
        @Override
        public BigInteger asBigInteger() {
            return IntegerPolynomial.this.evaluate(this.limbs);
        }
        
        @Override
        public MutableElement mutable() {
            return new MutableElement(this.limbs.clone(), this.numAdds);
        }
        
        protected boolean isSummand() {
            return this.numAdds < IntegerPolynomial.this.maxAdds;
        }
        
        @Override
        public ImmutableElement add(final IntegerModuloP integerModuloP) {
            final Element element = (Element)integerModuloP;
            if (!this.isSummand() || !element.isSummand()) {
                throw new ArithmeticException("Not a valid summand");
            }
            final long[] array = new long[this.limbs.length];
            for (int i = 0; i < this.limbs.length; ++i) {
                array[i] = this.limbs[i] + element.limbs[i];
            }
            return new ImmutableElement(array, Math.max(this.numAdds, element.numAdds) + 1);
        }
        
        @Override
        public ImmutableElement additiveInverse() {
            final long[] array = new long[this.limbs.length];
            for (int i = 0; i < this.limbs.length; ++i) {
                array[i] = -this.limbs[i];
            }
            return new ImmutableElement(array, this.numAdds);
        }
        
        protected long[] cloneLow(final long[] array) {
            final long[] array2 = new long[IntegerPolynomial.this.numLimbs];
            this.copyLow(array, array2);
            return array2;
        }
        
        protected void copyLow(final long[] array, final long[] array2) {
            System.arraycopy(array, 0, array2, 0, array2.length);
        }
        
        @Override
        public ImmutableElement multiply(final IntegerModuloP integerModuloP) {
            final Element element = (Element)integerModuloP;
            final long[] array = new long[this.limbs.length];
            IntegerPolynomial.this.mult(this.limbs, element.limbs, array);
            return new ImmutableElement(array, 0);
        }
        
        @Override
        public ImmutableElement square() {
            final long[] array = new long[this.limbs.length];
            IntegerPolynomial.this.square(this.limbs, array);
            return new ImmutableElement(array, 0);
        }
        
        @Override
        public void addModPowerTwo(final IntegerModuloP integerModuloP, final byte[] array) {
            final Element element = (Element)integerModuloP;
            if (!this.isSummand() || !element.isSummand()) {
                throw new ArithmeticException("Not a valid summand");
            }
            IntegerPolynomial.this.addLimbsModPowerTwo(this.limbs, element.limbs, array);
        }
        
        @Override
        public void asByteArray(final byte[] array) {
            if (!this.isSummand()) {
                throw new ArithmeticException("Not a valid summand");
            }
            IntegerPolynomial.this.limbsToByteArray(this.limbs, array);
        }
    }
    
    protected class MutableElement extends Element implements MutableIntegerModuloP
    {
        protected MutableElement(final long[] array, final int n) {
            super(array, n);
        }
        
        @Override
        public ImmutableElement fixed() {
            return new ImmutableElement(this.limbs.clone(), this.numAdds);
        }
        
        @Override
        public void conditionalSet(final IntegerModuloP integerModuloP, final int n) {
            final Element element = (Element)integerModuloP;
            IntegerPolynomial.conditionalAssign(n, this.limbs, element.limbs);
            this.numAdds = element.numAdds;
        }
        
        @Override
        public void conditionalSwapWith(final MutableIntegerModuloP mutableIntegerModuloP, final int n) {
            final MutableElement mutableElement = (MutableElement)mutableIntegerModuloP;
            IntegerPolynomial.conditionalSwap(n, this.limbs, mutableElement.limbs);
            final int numAdds = this.numAdds;
            this.numAdds = mutableElement.numAdds;
            mutableElement.numAdds = numAdds;
        }
        
        @Override
        public MutableElement setValue(final IntegerModuloP integerModuloP) {
            final Element element = (Element)integerModuloP;
            System.arraycopy(element.limbs, 0, this.limbs, 0, element.limbs.length);
            this.numAdds = element.numAdds;
            return this;
        }
        
        @Override
        public MutableElement setValue(final byte[] array, final int n, final int n2, final byte b) {
            IntegerPolynomial.this.encode(array, n, n2, b, this.limbs);
            this.numAdds = 0;
            return this;
        }
        
        @Override
        public MutableElement setValue(final ByteBuffer byteBuffer, final int n, final byte b) {
            IntegerPolynomial.this.encode(byteBuffer, n, b, this.limbs);
            this.numAdds = 0;
            return this;
        }
        
        @Override
        public MutableElement setProduct(final IntegerModuloP integerModuloP) {
            IntegerPolynomial.this.mult(this.limbs, ((Element)integerModuloP).limbs, this.limbs);
            this.numAdds = 0;
            return this;
        }
        
        @Override
        public MutableElement setProduct(final SmallValue smallValue) {
            IntegerPolynomial.this.multByInt(this.limbs, ((Limb)smallValue).value);
            this.numAdds = 0;
            return this;
        }
        
        @Override
        public MutableElement setSum(final IntegerModuloP integerModuloP) {
            final Element element = (Element)integerModuloP;
            if (!this.isSummand() || !element.isSummand()) {
                throw new ArithmeticException("Not a valid summand");
            }
            for (int i = 0; i < this.limbs.length; ++i) {
                this.limbs[i] += element.limbs[i];
            }
            this.numAdds = Math.max(this.numAdds, element.numAdds) + 1;
            return this;
        }
        
        @Override
        public MutableElement setDifference(final IntegerModuloP integerModuloP) {
            final Element element = (Element)integerModuloP;
            if (!this.isSummand() || !element.isSummand()) {
                throw new ArithmeticException("Not a valid summand");
            }
            for (int i = 0; i < this.limbs.length; ++i) {
                this.limbs[i] -= element.limbs[i];
            }
            this.numAdds = Math.max(this.numAdds, element.numAdds) + 1;
            return this;
        }
        
        @Override
        public MutableElement setSquare() {
            IntegerPolynomial.this.square(this.limbs, this.limbs);
            this.numAdds = 0;
            return this;
        }
        
        @Override
        public MutableElement setAdditiveInverse() {
            for (int i = 0; i < this.limbs.length; ++i) {
                this.limbs[i] = -this.limbs[i];
            }
            return this;
        }
        
        @Override
        public MutableElement setReduced() {
            IntegerPolynomial.this.reduce(this.limbs);
            this.numAdds = 0;
            return this;
        }
    }
    
    class ImmutableElement extends Element implements ImmutableIntegerModuloP
    {
        protected ImmutableElement(final BigInteger bigInteger) {
            super(bigInteger);
        }
        
        protected ImmutableElement(final boolean b) {
            super(b);
        }
        
        protected ImmutableElement(final long[] array, final int n) {
            super(array, n);
        }
        
        @Override
        public ImmutableElement fixed() {
            return this;
        }
    }
    
    class Limb implements SmallValue
    {
        int value;
        
        Limb(final int value) {
            this.value = value;
        }
    }
}
