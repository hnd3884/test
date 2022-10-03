package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;

public class MethodLocatable<M> implements Locatable
{
    private final Locatable upstream;
    private final M method;
    private final Navigator<?, ?, ?, M> nav;
    
    public MethodLocatable(final Locatable upstream, final M method, final Navigator<?, ?, ?, M> nav) {
        this.upstream = upstream;
        this.method = method;
        this.nav = nav;
    }
    
    @Override
    public Locatable getUpstream() {
        return this.upstream;
    }
    
    @Override
    public Location getLocation() {
        return this.nav.getMethodLocation(this.method);
    }
}
