package com.sun.xml.internal.ws.api.pipe;

import java.util.Map;

public abstract class TubeCloner
{
    public final Map<Object, Object> master2copy;
    
    public static Tube clone(final Tube p) {
        return new PipeClonerImpl().copy(p);
    }
    
    TubeCloner(final Map<Object, Object> master2copy) {
        this.master2copy = master2copy;
    }
    
    public abstract <T extends Tube> T copy(final T p0);
    
    public abstract void add(final Tube p0, final Tube p1);
}
