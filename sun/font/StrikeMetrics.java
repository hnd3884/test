package sun.font;

import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

public final class StrikeMetrics
{
    public float ascentX;
    public float ascentY;
    public float descentX;
    public float descentY;
    public float baselineX;
    public float baselineY;
    public float leadingX;
    public float leadingY;
    public float maxAdvanceX;
    public float maxAdvanceY;
    
    StrikeMetrics() {
        final float n = 2.14748365E9f;
        this.ascentY = n;
        this.ascentX = n;
        final float n2 = -2.14748365E9f;
        this.leadingY = n2;
        this.leadingX = n2;
        this.descentY = n2;
        this.descentX = n2;
        final float n3 = -2.14748365E9f;
        this.maxAdvanceY = n3;
        this.maxAdvanceX = n3;
        this.baselineX = n3;
        this.baselineX = n3;
    }
    
    StrikeMetrics(final float ascentX, final float ascentY, final float descentX, final float descentY, final float baselineX, final float baselineY, final float leadingX, final float leadingY, final float maxAdvanceX, final float maxAdvanceY) {
        this.ascentX = ascentX;
        this.ascentY = ascentY;
        this.descentX = descentX;
        this.descentY = descentY;
        this.baselineX = baselineX;
        this.baselineY = baselineY;
        this.leadingX = leadingX;
        this.leadingY = leadingY;
        this.maxAdvanceX = maxAdvanceX;
        this.maxAdvanceY = maxAdvanceY;
    }
    
    public float getAscent() {
        return -this.ascentY;
    }
    
    public float getDescent() {
        return this.descentY;
    }
    
    public float getLeading() {
        return this.leadingY;
    }
    
    public float getMaxAdvance() {
        return this.maxAdvanceX;
    }
    
    void merge(final StrikeMetrics strikeMetrics) {
        if (strikeMetrics == null) {
            return;
        }
        if (strikeMetrics.ascentX < this.ascentX) {
            this.ascentX = strikeMetrics.ascentX;
        }
        if (strikeMetrics.ascentY < this.ascentY) {
            this.ascentY = strikeMetrics.ascentY;
        }
        if (strikeMetrics.descentX > this.descentX) {
            this.descentX = strikeMetrics.descentX;
        }
        if (strikeMetrics.descentY > this.descentY) {
            this.descentY = strikeMetrics.descentY;
        }
        if (strikeMetrics.baselineX > this.baselineX) {
            this.baselineX = strikeMetrics.baselineX;
        }
        if (strikeMetrics.baselineY > this.baselineY) {
            this.baselineY = strikeMetrics.baselineY;
        }
        if (strikeMetrics.leadingX > this.leadingX) {
            this.leadingX = strikeMetrics.leadingX;
        }
        if (strikeMetrics.leadingY > this.leadingY) {
            this.leadingY = strikeMetrics.leadingY;
        }
        if (strikeMetrics.maxAdvanceX > this.maxAdvanceX) {
            this.maxAdvanceX = strikeMetrics.maxAdvanceX;
        }
        if (strikeMetrics.maxAdvanceY > this.maxAdvanceY) {
            this.maxAdvanceY = strikeMetrics.maxAdvanceY;
        }
    }
    
    void convertToUserSpace(final AffineTransform affineTransform) {
        final Point2D.Float float1 = new Point2D.Float();
        float1.x = this.ascentX;
        float1.y = this.ascentY;
        affineTransform.deltaTransform(float1, float1);
        this.ascentX = float1.x;
        this.ascentY = float1.y;
        float1.x = this.descentX;
        float1.y = this.descentY;
        affineTransform.deltaTransform(float1, float1);
        this.descentX = float1.x;
        this.descentY = float1.y;
        float1.x = this.baselineX;
        float1.y = this.baselineY;
        affineTransform.deltaTransform(float1, float1);
        this.baselineX = float1.x;
        this.baselineY = float1.y;
        float1.x = this.leadingX;
        float1.y = this.leadingY;
        affineTransform.deltaTransform(float1, float1);
        this.leadingX = float1.x;
        this.leadingY = float1.y;
        float1.x = this.maxAdvanceX;
        float1.y = this.maxAdvanceY;
        affineTransform.deltaTransform(float1, float1);
        this.maxAdvanceX = float1.x;
        this.maxAdvanceY = float1.y;
    }
    
    @Override
    public String toString() {
        return "ascent:x=" + this.ascentX + " y=" + this.ascentY + " descent:x=" + this.descentX + " y=" + this.descentY + " baseline:x=" + this.baselineX + " y=" + this.baselineY + " leading:x=" + this.leadingX + " y=" + this.leadingY + " maxAdvance:x=" + this.maxAdvanceX + " y=" + this.maxAdvanceY;
    }
}
