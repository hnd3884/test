package javax.swing;

import java.awt.Component;

public abstract class Spring
{
    public static final int UNSET = Integer.MIN_VALUE;
    
    protected Spring() {
    }
    
    public abstract int getMinimumValue();
    
    public abstract int getPreferredValue();
    
    public abstract int getMaximumValue();
    
    public abstract int getValue();
    
    public abstract void setValue(final int p0);
    
    private double range(final boolean b) {
        return b ? (this.getPreferredValue() - this.getMinimumValue()) : ((double)(this.getMaximumValue() - this.getPreferredValue()));
    }
    
    double getStrain() {
        return (this.getValue() - this.getPreferredValue()) / this.range(this.getValue() < this.getPreferredValue());
    }
    
    void setStrain(final double n) {
        this.setValue(this.getPreferredValue() + (int)(n * this.range(n < 0.0)));
    }
    
    boolean isCyclic(final SpringLayout springLayout) {
        return false;
    }
    
    public static Spring constant(final int n) {
        return constant(n, n, n);
    }
    
    public static Spring constant(final int n, final int n2, final int n3) {
        return new StaticSpring(n, n2, n3);
    }
    
    public static Spring minus(final Spring spring) {
        return new NegativeSpring(spring);
    }
    
    public static Spring sum(final Spring spring, final Spring spring2) {
        return new SumSpring(spring, spring2);
    }
    
    public static Spring max(final Spring spring, final Spring spring2) {
        return new MaxSpring(spring, spring2);
    }
    
    static Spring difference(final Spring spring, final Spring spring2) {
        return sum(spring, minus(spring2));
    }
    
    public static Spring scale(final Spring spring, final float n) {
        checkArg(spring);
        return new ScaleSpring(spring, n);
    }
    
    public static Spring width(final Component component) {
        checkArg(component);
        return new WidthSpring(component);
    }
    
    public static Spring height(final Component component) {
        checkArg(component);
        return new HeightSpring(component);
    }
    
    private static void checkArg(final Object o) {
        if (o == null) {
            throw new NullPointerException("Argument must not be null");
        }
    }
    
    abstract static class AbstractSpring extends Spring
    {
        protected int size;
        
        AbstractSpring() {
            this.size = Integer.MIN_VALUE;
        }
        
        @Override
        public int getValue() {
            return (this.size != Integer.MIN_VALUE) ? this.size : this.getPreferredValue();
        }
        
        @Override
        public final void setValue(final int nonClearValue) {
            if (this.size == nonClearValue) {
                return;
            }
            if (nonClearValue == Integer.MIN_VALUE) {
                this.clear();
            }
            else {
                this.setNonClearValue(nonClearValue);
            }
        }
        
        protected void clear() {
            this.size = Integer.MIN_VALUE;
        }
        
        protected void setNonClearValue(final int size) {
            this.size = size;
        }
    }
    
    private static class StaticSpring extends AbstractSpring
    {
        protected int min;
        protected int pref;
        protected int max;
        
        public StaticSpring(final int n) {
            this(n, n, n);
        }
        
        public StaticSpring(final int min, final int pref, final int max) {
            this.min = min;
            this.pref = pref;
            this.max = max;
        }
        
        @Override
        public String toString() {
            return "StaticSpring [" + this.min + ", " + this.pref + ", " + this.max + "]";
        }
        
        @Override
        public int getMinimumValue() {
            return this.min;
        }
        
        @Override
        public int getPreferredValue() {
            return this.pref;
        }
        
        @Override
        public int getMaximumValue() {
            return this.max;
        }
    }
    
    private static class NegativeSpring extends Spring
    {
        private Spring s;
        
        public NegativeSpring(final Spring s) {
            this.s = s;
        }
        
        @Override
        public int getMinimumValue() {
            return -this.s.getMaximumValue();
        }
        
        @Override
        public int getPreferredValue() {
            return -this.s.getPreferredValue();
        }
        
        @Override
        public int getMaximumValue() {
            return -this.s.getMinimumValue();
        }
        
        @Override
        public int getValue() {
            return -this.s.getValue();
        }
        
        @Override
        public void setValue(final int n) {
            this.s.setValue(-n);
        }
        
        @Override
        boolean isCyclic(final SpringLayout springLayout) {
            return this.s.isCyclic(springLayout);
        }
    }
    
    private static class ScaleSpring extends Spring
    {
        private Spring s;
        private float factor;
        
        private ScaleSpring(final Spring s, final float factor) {
            this.s = s;
            this.factor = factor;
        }
        
        @Override
        public int getMinimumValue() {
            return Math.round(((this.factor < 0.0f) ? this.s.getMaximumValue() : this.s.getMinimumValue()) * this.factor);
        }
        
        @Override
        public int getPreferredValue() {
            return Math.round(this.s.getPreferredValue() * this.factor);
        }
        
        @Override
        public int getMaximumValue() {
            return Math.round(((this.factor < 0.0f) ? this.s.getMinimumValue() : this.s.getMaximumValue()) * this.factor);
        }
        
        @Override
        public int getValue() {
            return Math.round(this.s.getValue() * this.factor);
        }
        
        @Override
        public void setValue(final int n) {
            if (n == Integer.MIN_VALUE) {
                this.s.setValue(Integer.MIN_VALUE);
            }
            else {
                this.s.setValue(Math.round(n / this.factor));
            }
        }
        
        @Override
        boolean isCyclic(final SpringLayout springLayout) {
            return this.s.isCyclic(springLayout);
        }
    }
    
    static class WidthSpring extends AbstractSpring
    {
        Component c;
        
        public WidthSpring(final Component c) {
            this.c = c;
        }
        
        @Override
        public int getMinimumValue() {
            return this.c.getMinimumSize().width;
        }
        
        @Override
        public int getPreferredValue() {
            return this.c.getPreferredSize().width;
        }
        
        @Override
        public int getMaximumValue() {
            return Math.min(32767, this.c.getMaximumSize().width);
        }
    }
    
    static class HeightSpring extends AbstractSpring
    {
        Component c;
        
        public HeightSpring(final Component c) {
            this.c = c;
        }
        
        @Override
        public int getMinimumValue() {
            return this.c.getMinimumSize().height;
        }
        
        @Override
        public int getPreferredValue() {
            return this.c.getPreferredSize().height;
        }
        
        @Override
        public int getMaximumValue() {
            return Math.min(32767, this.c.getMaximumSize().height);
        }
    }
    
    abstract static class SpringMap extends Spring
    {
        private Spring s;
        
        public SpringMap(final Spring s) {
            this.s = s;
        }
        
        protected abstract int map(final int p0);
        
        protected abstract int inv(final int p0);
        
        @Override
        public int getMinimumValue() {
            return this.map(this.s.getMinimumValue());
        }
        
        @Override
        public int getPreferredValue() {
            return this.map(this.s.getPreferredValue());
        }
        
        @Override
        public int getMaximumValue() {
            return Math.min(32767, this.map(this.s.getMaximumValue()));
        }
        
        @Override
        public int getValue() {
            return this.map(this.s.getValue());
        }
        
        @Override
        public void setValue(final int n) {
            if (n == Integer.MIN_VALUE) {
                this.s.setValue(Integer.MIN_VALUE);
            }
            else {
                this.s.setValue(this.inv(n));
            }
        }
        
        @Override
        boolean isCyclic(final SpringLayout springLayout) {
            return this.s.isCyclic(springLayout);
        }
    }
    
    abstract static class CompoundSpring extends StaticSpring
    {
        protected Spring s1;
        protected Spring s2;
        
        public CompoundSpring(final Spring s1, final Spring s2) {
            super(Integer.MIN_VALUE);
            this.s1 = s1;
            this.s2 = s2;
        }
        
        @Override
        public String toString() {
            return "CompoundSpring of " + this.s1 + " and " + this.s2;
        }
        
        @Override
        protected void clear() {
            super.clear();
            final int min = Integer.MIN_VALUE;
            this.max = min;
            this.pref = min;
            this.min = min;
            this.s1.setValue(Integer.MIN_VALUE);
            this.s2.setValue(Integer.MIN_VALUE);
        }
        
        protected abstract int op(final int p0, final int p1);
        
        @Override
        public int getMinimumValue() {
            if (this.min == Integer.MIN_VALUE) {
                this.min = this.op(this.s1.getMinimumValue(), this.s2.getMinimumValue());
            }
            return this.min;
        }
        
        @Override
        public int getPreferredValue() {
            if (this.pref == Integer.MIN_VALUE) {
                this.pref = this.op(this.s1.getPreferredValue(), this.s2.getPreferredValue());
            }
            return this.pref;
        }
        
        @Override
        public int getMaximumValue() {
            if (this.max == Integer.MIN_VALUE) {
                this.max = this.op(this.s1.getMaximumValue(), this.s2.getMaximumValue());
            }
            return this.max;
        }
        
        @Override
        public int getValue() {
            if (this.size == Integer.MIN_VALUE) {
                this.size = this.op(this.s1.getValue(), this.s2.getValue());
            }
            return this.size;
        }
        
        @Override
        boolean isCyclic(final SpringLayout springLayout) {
            return springLayout.isCyclic(this.s1) || springLayout.isCyclic(this.s2);
        }
    }
    
    private static class SumSpring extends CompoundSpring
    {
        public SumSpring(final Spring spring, final Spring spring2) {
            super(spring, spring2);
        }
        
        @Override
        protected int op(final int n, final int n2) {
            return n + n2;
        }
        
        @Override
        protected void setNonClearValue(final int nonClearValue) {
            super.setNonClearValue(nonClearValue);
            this.s1.setStrain(this.getStrain());
            this.s2.setValue(nonClearValue - this.s1.getValue());
        }
    }
    
    private static class MaxSpring extends CompoundSpring
    {
        public MaxSpring(final Spring spring, final Spring spring2) {
            super(spring, spring2);
        }
        
        @Override
        protected int op(final int n, final int n2) {
            return Math.max(n, n2);
        }
        
        @Override
        protected void setNonClearValue(final int value) {
            super.setNonClearValue(value);
            this.s1.setValue(value);
            this.s2.setValue(value);
        }
    }
}
