package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;

enum GroupKind
{
    ALL("all"), 
    SEQUENCE("sequence"), 
    CHOICE("choice");
    
    private final String name;
    
    private GroupKind(final String name) {
        this.name = name;
    }
    
    Particle write(final ContentModelContainer parent) {
        return parent._element(this.name, Particle.class);
    }
}
