package com.sun.xml.internal.ws.api.pipe;

import java.util.Map;

public abstract class PipeCloner extends TubeCloner
{
    public static Pipe clone(final Pipe p) {
        return new PipeClonerImpl().copy(p);
    }
    
    PipeCloner(final Map<Object, Object> master2copy) {
        super(master2copy);
    }
    
    public abstract <T extends Pipe> T copy(final T p0);
    
    public abstract void add(final Pipe p0, final Pipe p1);
}
