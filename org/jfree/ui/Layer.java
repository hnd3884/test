package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class Layer implements Serializable
{
    private static final long serialVersionUID = -1470104570733183430L;
    public static final Layer FOREGROUND;
    public static final Layer BACKGROUND;
    private String name;
    
    static {
        FOREGROUND = new Layer("Layer.FOREGROUND");
        BACKGROUND = new Layer("Layer.BACKGROUND");
    }
    
    private Layer(final String name) {
        this.name = name;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Layer)) {
            return false;
        }
        final Layer layer = (Layer)o;
        return this.name.equals(layer.name);
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private Object readResolve() throws ObjectStreamException {
        Layer result = null;
        if (this.equals(Layer.FOREGROUND)) {
            result = Layer.FOREGROUND;
        }
        else if (this.equals(Layer.BACKGROUND)) {
            result = Layer.BACKGROUND;
        }
        return result;
    }
    
    public String toString() {
        return this.name;
    }
}
