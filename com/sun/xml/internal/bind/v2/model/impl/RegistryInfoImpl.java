package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Iterator;
import com.sun.xml.internal.bind.v2.model.annotation.MethodLocatable;
import javax.xml.bind.annotation.XmlElementDecl;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.util.LinkedHashSet;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import java.util.Set;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.core.RegistryInfo;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;

final class RegistryInfoImpl<T, C, F, M> implements Locatable, RegistryInfo<T, C>
{
    final C registryClass;
    private final Locatable upstream;
    private final Navigator<T, C, F, M> nav;
    private final Set<TypeInfo<T, C>> references;
    
    RegistryInfoImpl(final ModelBuilder<T, C, F, M> builder, final Locatable upstream, final C registryClass) {
        this.references = new LinkedHashSet<TypeInfo<T, C>>();
        this.nav = builder.nav;
        this.registryClass = registryClass;
        this.upstream = upstream;
        builder.registries.put(this.getPackageName(), this);
        if (this.nav.getDeclaredField(registryClass, "_useJAXBProperties") != null) {
            builder.reportError(new IllegalAnnotationException(Messages.MISSING_JAXB_PROPERTIES.format(this.getPackageName()), this));
            return;
        }
        for (final M m : this.nav.getDeclaredMethods(registryClass)) {
            final XmlElementDecl em = builder.reader.getMethodAnnotation(XmlElementDecl.class, m, this);
            if (em == null) {
                if (!this.nav.getMethodName(m).startsWith("create")) {
                    continue;
                }
                this.references.add(builder.getTypeInfo(this.nav.getReturnType(m), new MethodLocatable<Object>(this, m, this.nav)));
            }
            else {
                ElementInfoImpl<T, C, F, M> ei;
                try {
                    ei = builder.createElementInfo(this, m);
                }
                catch (final IllegalAnnotationException e) {
                    builder.reportError(e);
                    continue;
                }
                builder.typeInfoSet.add(ei, builder);
                this.references.add(ei);
            }
        }
    }
    
    @Override
    public Locatable getUpstream() {
        return this.upstream;
    }
    
    @Override
    public Location getLocation() {
        return this.nav.getClassLocation(this.registryClass);
    }
    
    @Override
    public Set<TypeInfo<T, C>> getReferences() {
        return this.references;
    }
    
    public String getPackageName() {
        return this.nav.getPackageName(this.registryClass);
    }
    
    @Override
    public C getClazz() {
        return this.registryClass;
    }
}
