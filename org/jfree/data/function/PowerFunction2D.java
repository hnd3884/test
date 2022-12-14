package org.jfree.data.function;

public class PowerFunction2D implements Function2D
{
    private double a;
    private double b;
    
    public PowerFunction2D(final double a, final double b) {
        this.a = a;
        this.b = b;
    }
    
    public double getValue(final double x) {
        return this.a * Math.pow(x, this.b);
    }
}
