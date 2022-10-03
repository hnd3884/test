package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;

public class FieldLocatable<F> implements Locatable
{
    private final Locatable upstream;
    private final F field;
    private final Navigator<?, ?, F, ?> nav;
    
    public FieldLocatable(final Locatable upstream, final F field, final Navigator<?, ?, F, ?> nav) {
        this.upstream = upstream;
        this.field = field;
        this.nav = nav;
    }
    
    @Override
    public Locatable getUpstream() {
        return this.upstream;
    }
    
    @Override
    public Location getLocation() {
        return this.nav.getFieldLocation(this.field);
    }
}
