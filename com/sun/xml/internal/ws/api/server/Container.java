package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import com.sun.xml.internal.ws.api.Component;
import java.util.Set;
import com.sun.xml.internal.ws.api.ComponentEx;
import com.sun.xml.internal.ws.api.ComponentRegistry;

public abstract class Container implements ComponentRegistry, ComponentEx
{
    private final Set<Component> components;
    public static final Container NONE;
    
    protected Container() {
        this.components = new CopyOnWriteArraySet<Component>();
    }
    
    @Override
    public <S> S getSPI(final Class<S> spiType) {
        if (this.components == null) {
            return null;
        }
        for (final Component c : this.components) {
            final S s = c.getSPI(spiType);
            if (s != null) {
                return s;
            }
        }
        return null;
    }
    
    @Override
    public Set<Component> getComponents() {
        return this.components;
    }
    
    @NotNull
    @Override
    public <E> Iterable<E> getIterableSPI(final Class<E> spiType) {
        final E item = this.getSPI(spiType);
        if (item != null) {
            final Collection<E> c = Collections.singletonList(item);
            return c;
        }
        return (Iterable<E>)Collections.emptySet();
    }
    
    static {
        NONE = new NoneContainer();
    }
    
    private static final class NoneContainer extends Container
    {
    }
}
