package org.bouncycastle.pqc.math.linearalgebra;

public abstract class GF2nElement implements GFElement
{
    protected GF2nField mField;
    protected int mDegree;
    
    public abstract Object clone();
    
    abstract void assignZero();
    
    abstract void assignOne();
    
    public abstract boolean testRightmostBit();
    
    abstract boolean testBit(final int p0);
    
    public final GF2nField getField() {
        return this.mField;
    }
    
    public abstract GF2nElement increase();
    
    public abstract void increaseThis();
    
    public final GFElement subtract(final GFElement gfElement) {
        return this.add(gfElement);
    }
    
    public final void subtractFromThis(final GFElement gfElement) {
        this.addToThis(gfElement);
    }
    
    public abstract GF2nElement square();
    
    public abstract void squareThis();
    
    public abstract GF2nElement squareRoot();
    
    public abstract void squareRootThis();
    
    public final GF2nElement convert(final GF2nField gf2nField) {
        return this.mField.convert(this, gf2nField);
    }
    
    public abstract int trace();
    
    public abstract GF2nElement solveQuadraticEquation() throws RuntimeException;
}
