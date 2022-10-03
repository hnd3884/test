package javax.swing.plaf.nimbus;

import java.awt.Color;

abstract class ShadowEffect extends Effect
{
    protected Color color;
    protected float opacity;
    protected int angle;
    protected int distance;
    protected int spread;
    protected int size;
    
    ShadowEffect() {
        this.color = Color.BLACK;
        this.opacity = 0.75f;
        this.angle = 135;
        this.distance = 5;
        this.spread = 0;
        this.size = 5;
    }
    
    Color getColor() {
        return this.color;
    }
    
    void setColor(final Color color) {
        this.getColor();
        this.color = color;
    }
    
    @Override
    float getOpacity() {
        return this.opacity;
    }
    
    void setOpacity(final float opacity) {
        this.getOpacity();
        this.opacity = opacity;
    }
    
    int getAngle() {
        return this.angle;
    }
    
    void setAngle(final int angle) {
        this.getAngle();
        this.angle = angle;
    }
    
    int getDistance() {
        return this.distance;
    }
    
    void setDistance(final int distance) {
        this.getDistance();
        this.distance = distance;
    }
    
    int getSpread() {
        return this.spread;
    }
    
    void setSpread(final int spread) {
        this.getSpread();
        this.spread = spread;
    }
    
    int getSize() {
        return this.size;
    }
    
    void setSize(final int size) {
        this.getSize();
        this.size = size;
    }
}
