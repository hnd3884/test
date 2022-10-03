package com.google.zxing;

public class ResultPoint
{
    private final float x;
    private final float y;
    
    public ResultPoint(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public final float getX() {
        return this.x;
    }
    
    public final float getY() {
        return this.y;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ResultPoint) {
            final ResultPoint otherPoint = (ResultPoint)other;
            return this.x == otherPoint.x && this.y == otherPoint.y;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 31 * Float.floatToIntBits(this.x) + Float.floatToIntBits(this.y);
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(25);
        result.append('(');
        result.append(this.x);
        result.append(',');
        result.append(this.y);
        result.append(')');
        return result.toString();
    }
    
    public static void orderBestPatterns(final ResultPoint[] patterns) {
        final float zeroOneDistance = distance(patterns[0], patterns[1]);
        final float oneTwoDistance = distance(patterns[1], patterns[2]);
        final float zeroTwoDistance = distance(patterns[0], patterns[2]);
        ResultPoint pointB;
        ResultPoint pointA;
        ResultPoint pointC;
        if (oneTwoDistance >= zeroOneDistance && oneTwoDistance >= zeroTwoDistance) {
            pointB = patterns[0];
            pointA = patterns[1];
            pointC = patterns[2];
        }
        else if (zeroTwoDistance >= oneTwoDistance && zeroTwoDistance >= zeroOneDistance) {
            pointB = patterns[1];
            pointA = patterns[0];
            pointC = patterns[2];
        }
        else {
            pointB = patterns[2];
            pointA = patterns[0];
            pointC = patterns[1];
        }
        if (crossProductZ(pointA, pointB, pointC) < 0.0f) {
            final ResultPoint temp = pointA;
            pointA = pointC;
            pointC = temp;
        }
        patterns[0] = pointA;
        patterns[1] = pointB;
        patterns[2] = pointC;
    }
    
    public static float distance(final ResultPoint pattern1, final ResultPoint pattern2) {
        final float xDiff = pattern1.x - pattern2.x;
        final float yDiff = pattern1.y - pattern2.y;
        return (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
    
    private static float crossProductZ(final ResultPoint pointA, final ResultPoint pointB, final ResultPoint pointC) {
        final float bX = pointB.x;
        final float bY = pointB.y;
        return (pointC.x - bX) * (pointA.y - bY) - (pointC.y - bY) * (pointA.x - bX);
    }
}
