package sun.security.ec.point;

import sun.security.util.math.MutableIntegerModuloP;
import sun.security.util.math.ImmutableIntegerModuloP;
import sun.security.util.math.IntegerFieldModuloP;
import sun.security.util.math.IntegerModuloP;

public abstract class ProjectivePoint<T extends IntegerModuloP> implements Point
{
    protected final T x;
    protected final T y;
    protected final T z;
    
    protected ProjectivePoint(final T x, final T y, final T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public IntegerFieldModuloP getField() {
        return this.x.getField();
    }
    
    @Override
    public Immutable fixed() {
        return new Immutable(this.x.fixed(), this.y.fixed(), this.z.fixed());
    }
    
    @Override
    public Mutable mutable() {
        return new Mutable(this.x.mutable(), this.y.mutable(), this.z.mutable());
    }
    
    public T getX() {
        return this.x;
    }
    
    public T getY() {
        return this.y;
    }
    
    public T getZ() {
        return this.z;
    }
    
    @Override
    public AffinePoint asAffine() {
        final ImmutableIntegerModuloP multiplicativeInverse = this.z.multiplicativeInverse();
        return new AffinePoint(this.x.multiply((IntegerModuloP)multiplicativeInverse), this.y.multiply((IntegerModuloP)multiplicativeInverse));
    }
    
    public static class Immutable extends ProjectivePoint<ImmutableIntegerModuloP> implements ImmutablePoint
    {
        public Immutable(final ImmutableIntegerModuloP immutableIntegerModuloP, final ImmutableIntegerModuloP immutableIntegerModuloP2, final ImmutableIntegerModuloP immutableIntegerModuloP3) {
            super(immutableIntegerModuloP, immutableIntegerModuloP2, immutableIntegerModuloP3);
        }
    }
    
    public static class Mutable extends ProjectivePoint<MutableIntegerModuloP> implements MutablePoint
    {
        public Mutable(final MutableIntegerModuloP mutableIntegerModuloP, final MutableIntegerModuloP mutableIntegerModuloP2, final MutableIntegerModuloP mutableIntegerModuloP3) {
            super(mutableIntegerModuloP, mutableIntegerModuloP2, mutableIntegerModuloP3);
        }
        
        public Mutable(final IntegerFieldModuloP integerFieldModuloP) {
            super(integerFieldModuloP.get0().mutable(), integerFieldModuloP.get0().mutable(), integerFieldModuloP.get0().mutable());
        }
        
        @Override
        public Mutable conditionalSet(final Point point, final int n) {
            if (!(point instanceof ProjectivePoint)) {
                throw new RuntimeException("Incompatible point");
            }
            return this.conditionalSet((ProjectivePoint<IntegerModuloP>)point, n);
        }
        
        private <T extends IntegerModuloP> Mutable conditionalSet(final ProjectivePoint<T> projectivePoint, final int n) {
            ((MutableIntegerModuloP)this.x).conditionalSet((IntegerModuloP)projectivePoint.x, n);
            ((MutableIntegerModuloP)this.y).conditionalSet((IntegerModuloP)projectivePoint.y, n);
            ((MutableIntegerModuloP)this.z).conditionalSet((IntegerModuloP)projectivePoint.z, n);
            return this;
        }
        
        @Override
        public Mutable setValue(final AffinePoint affinePoint) {
            ((MutableIntegerModuloP)this.x).setValue((IntegerModuloP)affinePoint.getX());
            ((MutableIntegerModuloP)this.y).setValue((IntegerModuloP)affinePoint.getY());
            ((MutableIntegerModuloP)this.z).setValue((IntegerModuloP)affinePoint.getX().getField().get1());
            return this;
        }
        
        @Override
        public Mutable setValue(final Point point) {
            if (!(point instanceof ProjectivePoint)) {
                throw new RuntimeException("Incompatible point");
            }
            return this.setValue((ProjectivePoint<IntegerModuloP>)point);
        }
        
        private <T extends IntegerModuloP> Mutable setValue(final ProjectivePoint<T> projectivePoint) {
            ((MutableIntegerModuloP)this.x).setValue((IntegerModuloP)projectivePoint.x);
            ((MutableIntegerModuloP)this.y).setValue((IntegerModuloP)projectivePoint.y);
            ((MutableIntegerModuloP)this.z).setValue((IntegerModuloP)projectivePoint.z);
            return this;
        }
    }
}
