package com.microsoft.sqlserver.jdbc.spatialdatatypes;

public class Point
{
    private final double x;
    private final double y;
    private final double z;
    private final double m;
    
    public Point(final double x, final double y, final double z, final double m) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.m = m;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public double getM() {
        return this.m;
    }
}
