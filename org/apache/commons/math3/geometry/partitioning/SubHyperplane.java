package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public interface SubHyperplane<S extends Space>
{
    SubHyperplane<S> copySelf();
    
    Hyperplane<S> getHyperplane();
    
    boolean isEmpty();
    
    double getSize();
    
    @Deprecated
    Side side(final Hyperplane<S> p0);
    
    SplitSubHyperplane<S> split(final Hyperplane<S> p0);
    
    SubHyperplane<S> reunite(final SubHyperplane<S> p0);
    
    public static class SplitSubHyperplane<U extends Space>
    {
        private final SubHyperplane<U> plus;
        private final SubHyperplane<U> minus;
        
        public SplitSubHyperplane(final SubHyperplane<U> plus, final SubHyperplane<U> minus) {
            this.plus = plus;
            this.minus = minus;
        }
        
        public SubHyperplane<U> getPlus() {
            return this.plus;
        }
        
        public SubHyperplane<U> getMinus() {
            return this.minus;
        }
        
        public Side getSide() {
            if (this.plus != null && !this.plus.isEmpty()) {
                if (this.minus != null && !this.minus.isEmpty()) {
                    return Side.BOTH;
                }
                return Side.PLUS;
            }
            else {
                if (this.minus != null && !this.minus.isEmpty()) {
                    return Side.MINUS;
                }
                return Side.HYPER;
            }
        }
    }
}
