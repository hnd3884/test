package org.jfree.chart.axis;

import org.jfree.util.ObjectUtilities;
import org.jfree.ui.TextAnchor;
import java.io.Serializable;

public abstract class Tick implements Serializable, Cloneable
{
    private static final long serialVersionUID = 6668230383875149773L;
    private String text;
    private TextAnchor textAnchor;
    private TextAnchor rotationAnchor;
    private double angle;
    
    public Tick(final String text, final TextAnchor textAnchor, final TextAnchor rotationAnchor, final double angle) {
        if (textAnchor == null) {
            throw new IllegalArgumentException("Null 'textAnchor' argument.");
        }
        if (rotationAnchor == null) {
            throw new IllegalArgumentException("Null 'rotationAnchor' argument.");
        }
        this.text = text;
        this.textAnchor = textAnchor;
        this.rotationAnchor = rotationAnchor;
        this.angle = angle;
    }
    
    public String getText() {
        return this.text;
    }
    
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }
    
    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }
    
    public double getAngle() {
        return this.angle;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Tick) {
            final Tick t = (Tick)obj;
            return ObjectUtilities.equal((Object)this.text, (Object)t.text) && ObjectUtilities.equal((Object)this.textAnchor, (Object)t.textAnchor) && ObjectUtilities.equal((Object)this.rotationAnchor, (Object)t.rotationAnchor) && this.angle == t.angle;
        }
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final Tick clone = (Tick)super.clone();
        return clone;
    }
    
    public String toString() {
        return this.text;
    }
}
