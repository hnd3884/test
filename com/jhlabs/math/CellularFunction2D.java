package com.jhlabs.math;

import java.util.Random;

public class CellularFunction2D implements Function2D
{
    public float distancePower;
    public boolean cells;
    public boolean angular;
    private float[] coefficients;
    private Random random;
    private Point[] results;
    
    public CellularFunction2D() {
        this.distancePower = 2.0f;
        this.cells = false;
        this.angular = false;
        this.coefficients = new float[] { 1.0f, 0.0f, 0.0f, 0.0f };
        this.random = new Random();
        this.results = null;
        this.results = new Point[2];
        for (int j = 0; j < this.results.length; ++j) {
            this.results[j] = new Point();
        }
    }
    
    public void setCoefficient(final int c, final float v) {
        this.coefficients[c] = v;
    }
    
    public float getCoefficient(final int c) {
        return this.coefficients[c];
    }
    
    private float checkCube(final float x, final float y, final int cubeX, final int cubeY, final Point[] results) {
        this.random.setSeed(571 * cubeX + 23 * cubeY);
        int numPoints = 3 + this.random.nextInt() % 4;
        numPoints = 4;
        for (int i = 0; i < numPoints; ++i) {
            final float px = this.random.nextFloat();
            final float py = this.random.nextFloat();
            final float dx = Math.abs(x - px);
            final float dy = Math.abs(y - py);
            float d;
            if (this.distancePower == 1.0f) {
                d = dx + dy;
            }
            else if (this.distancePower == 2.0f) {
                d = (float)Math.sqrt(dx * dx + dy * dy);
            }
            else {
                d = (float)Math.pow(Math.pow(dx, this.distancePower) + Math.pow(dy, this.distancePower), 1.0f / this.distancePower);
            }
            for (int j = 0; j < results.length; ++j) {
                if (results[j].distance == Double.POSITIVE_INFINITY) {
                    final Point last = results[j];
                    last.distance = d;
                    last.x = px;
                    last.y = py;
                    results[j] = last;
                    break;
                }
                if (d < results[j].distance) {
                    final Point last = results[results.length - 1];
                    for (int k = results.length - 1; k > j; --k) {
                        results[k] = results[k - 1];
                    }
                    last.distance = d;
                    last.x = px;
                    last.y = py;
                    results[j] = last;
                    break;
                }
            }
        }
        return results[1].distance;
    }
    
    public float evaluate(final float x, final float y) {
        for (int j = 0; j < this.results.length; ++j) {
            this.results[j].distance = Float.POSITIVE_INFINITY;
        }
        final int ix = (int)x;
        final int iy = (int)y;
        final float fx = x - ix;
        final float fy = y - iy;
        float d = this.checkCube(fx, fy, ix, iy, this.results);
        if (d > fy) {
            d = this.checkCube(fx, fy + 1.0f, ix, iy - 1, this.results);
        }
        if (d > 1.0f - fy) {
            d = this.checkCube(fx, fy - 1.0f, ix, iy + 1, this.results);
        }
        if (d > fx) {
            this.checkCube(fx + 1.0f, fy, ix - 1, iy, this.results);
            if (d > fy) {
                d = this.checkCube(fx + 1.0f, fy + 1.0f, ix - 1, iy - 1, this.results);
            }
            if (d > 1.0f - fy) {
                d = this.checkCube(fx + 1.0f, fy - 1.0f, ix - 1, iy + 1, this.results);
            }
        }
        if (d > 1.0f - fx) {
            d = this.checkCube(fx - 1.0f, fy, ix + 1, iy, this.results);
            if (d > fy) {
                d = this.checkCube(fx - 1.0f, fy + 1.0f, ix + 1, iy - 1, this.results);
            }
            if (d > 1.0f - fy) {
                d = this.checkCube(fx - 1.0f, fy - 1.0f, ix + 1, iy + 1, this.results);
            }
        }
        float t = 0.0f;
        for (int i = 0; i < 2; ++i) {
            t += this.coefficients[i] * this.results[i].distance;
        }
        if (this.angular) {
            t += (float)(Math.atan2(fy - this.results[0].y, fx - this.results[0].x) / 6.283185307179586 + 0.5);
        }
        return t;
    }
    
    class Point
    {
        int index;
        float x;
        float y;
        float distance;
    }
}
