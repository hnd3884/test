package org.jfree.chart.renderer;

import java.awt.geom.Point2D;

public class Outlier implements Comparable
{
    private Point2D point;
    private double radius;
    
    public Outlier(final double xCoord, final double yCoord, final double radius) {
        this.point = new Point2D.Double(xCoord - radius, yCoord - radius);
        this.radius = radius;
    }
    
    public Point2D getPoint() {
        return this.point;
    }
    
    public void setPoint(final Point2D point) {
        this.point = point;
    }
    
    public double getX() {
        return this.getPoint().getX();
    }
    
    public double getY() {
        return this.getPoint().getY();
    }
    
    public double getRadius() {
        return this.radius;
    }
    
    public void setRadius(final double radius) {
        this.radius = radius;
    }
    
    public int compareTo(final Object o) {
        final Outlier outlier = (Outlier)o;
        final Point2D p1 = this.getPoint();
        final Point2D p2 = outlier.getPoint();
        if (p1.equals(p2)) {
            return 0;
        }
        if (p1.getX() < p2.getX() || p1.getY() < p2.getY()) {
            return -1;
        }
        return 1;
    }
    
    public boolean overlaps(final Outlier other) {
        return other.getX() >= this.getX() - this.radius * 1.1 && other.getX() <= this.getX() + this.radius * 1.1 && other.getY() >= this.getY() - this.radius * 1.1 && other.getY() <= this.getY() + this.radius * 1.1;
    }
    
    public String toString() {
        return "{" + this.getX() + "," + this.getY() + "}";
    }
}
