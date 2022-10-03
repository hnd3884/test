package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class VerticalAlignment implements Serializable
{
    private static final long serialVersionUID = 7272397034325429853L;
    public static final VerticalAlignment TOP;
    public static final VerticalAlignment BOTTOM;
    public static final VerticalAlignment CENTER;
    private String name;
    
    static {
        TOP = new VerticalAlignment("VerticalAlignment.TOP");
        BOTTOM = new VerticalAlignment("VerticalAlignment.BOTTOM");
        CENTER = new VerticalAlignment("VerticalAlignment.CENTER");
    }
    
    private VerticalAlignment(final String name) {
        this.name = name;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerticalAlignment)) {
            return false;
        }
        final VerticalAlignment alignment = (VerticalAlignment)o;
        return this.name.equals(alignment.name);
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(VerticalAlignment.TOP)) {
            return VerticalAlignment.TOP;
        }
        if (this.equals(VerticalAlignment.BOTTOM)) {
            return VerticalAlignment.BOTTOM;
        }
        if (this.equals(VerticalAlignment.CENTER)) {
            return VerticalAlignment.CENTER;
        }
        return null;
    }
    
    public String toString() {
        return this.name;
    }
}
