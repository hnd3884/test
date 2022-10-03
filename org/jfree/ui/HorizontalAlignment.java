package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class HorizontalAlignment implements Serializable
{
    private static final long serialVersionUID = -8249740987565309567L;
    public static final HorizontalAlignment LEFT;
    public static final HorizontalAlignment RIGHT;
    public static final HorizontalAlignment CENTER;
    private String name;
    
    static {
        LEFT = new HorizontalAlignment("HorizontalAlignment.LEFT");
        RIGHT = new HorizontalAlignment("HorizontalAlignment.RIGHT");
        CENTER = new HorizontalAlignment("HorizontalAlignment.CENTER");
    }
    
    private HorizontalAlignment(final String name) {
        this.name = name;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HorizontalAlignment)) {
            return false;
        }
        final HorizontalAlignment that = (HorizontalAlignment)obj;
        return this.name.equals(that.name);
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private Object readResolve() throws ObjectStreamException {
        HorizontalAlignment result = null;
        if (this.equals(HorizontalAlignment.LEFT)) {
            result = HorizontalAlignment.LEFT;
        }
        else if (this.equals(HorizontalAlignment.RIGHT)) {
            result = HorizontalAlignment.RIGHT;
        }
        else if (this.equals(HorizontalAlignment.CENTER)) {
            result = HorizontalAlignment.CENTER;
        }
        return result;
    }
    
    public String toString() {
        return this.name;
    }
}
