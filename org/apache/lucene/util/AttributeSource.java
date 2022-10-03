package org.apache.lucene.util;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Map;

public class AttributeSource
{
    private final Map<Class<? extends Attribute>, AttributeImpl> attributes;
    private final Map<Class<? extends AttributeImpl>, AttributeImpl> attributeImpls;
    private final State[] currentState;
    private final AttributeFactory factory;
    private static final ClassValue<Class<? extends Attribute>[]> implInterfaces;
    
    public AttributeSource() {
        this(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
    }
    
    public AttributeSource(final AttributeSource input) {
        Objects.requireNonNull(input, "input AttributeSource must not be null");
        this.attributes = input.attributes;
        this.attributeImpls = input.attributeImpls;
        this.currentState = input.currentState;
        this.factory = input.factory;
    }
    
    public AttributeSource(final AttributeFactory factory) {
        this.attributes = new LinkedHashMap<Class<? extends Attribute>, AttributeImpl>();
        this.attributeImpls = new LinkedHashMap<Class<? extends AttributeImpl>, AttributeImpl>();
        this.currentState = new State[1];
        this.factory = Objects.requireNonNull(factory, "AttributeFactory must not be null");
    }
    
    public final AttributeFactory getAttributeFactory() {
        return this.factory;
    }
    
    public final Iterator<Class<? extends Attribute>> getAttributeClassesIterator() {
        return Collections.unmodifiableSet((Set<? extends Class<? extends Attribute>>)this.attributes.keySet()).iterator();
    }
    
    public final Iterator<AttributeImpl> getAttributeImplsIterator() {
        final State initState = this.getCurrentState();
        if (initState != null) {
            return new Iterator<AttributeImpl>() {
                private State state = initState;
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public AttributeImpl next() {
                    if (this.state == null) {
                        throw new NoSuchElementException();
                    }
                    final AttributeImpl att = this.state.attribute;
                    this.state = this.state.next;
                    return att;
                }
                
                @Override
                public boolean hasNext() {
                    return this.state != null;
                }
            };
        }
        return Collections.emptySet().iterator();
    }
    
    static Class<? extends Attribute>[] getAttributeInterfaces(final Class<? extends AttributeImpl> clazz) {
        return AttributeSource.implInterfaces.get(clazz);
    }
    
    public final void addAttributeImpl(final AttributeImpl att) {
        final Class<? extends AttributeImpl> clazz = att.getClass();
        if (this.attributeImpls.containsKey(clazz)) {
            return;
        }
        for (final Class<? extends Attribute> curInterface : getAttributeInterfaces(clazz)) {
            if (!this.attributes.containsKey(curInterface)) {
                this.currentState[0] = null;
                this.attributes.put(curInterface, att);
                this.attributeImpls.put(clazz, att);
            }
        }
    }
    
    public final <T extends Attribute> T addAttribute(final Class<T> attClass) {
        AttributeImpl attImpl = this.attributes.get(attClass);
        if (attImpl == null) {
            if (!attClass.isInterface() || !Attribute.class.isAssignableFrom(attClass)) {
                throw new IllegalArgumentException("addAttribute() only accepts an interface that extends Attribute, but " + attClass.getName() + " does not fulfil this contract.");
            }
            this.addAttributeImpl(attImpl = this.factory.createAttributeInstance(attClass));
        }
        return attClass.cast(attImpl);
    }
    
    public final boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }
    
    public final boolean hasAttribute(final Class<? extends Attribute> attClass) {
        return this.attributes.containsKey(attClass);
    }
    
    public final <T extends Attribute> T getAttribute(final Class<T> attClass) {
        return attClass.cast(this.attributes.get(attClass));
    }
    
    private State getCurrentState() {
        State s = this.currentState[0];
        if (s != null || !this.hasAttributes()) {
            return s;
        }
        final State[] currentState = this.currentState;
        final int n = 0;
        final State state = new State();
        currentState[n] = state;
        s = state;
        State c = state;
        final Iterator<AttributeImpl> it = this.attributeImpls.values().iterator();
        c.attribute = it.next();
        while (it.hasNext()) {
            c.next = new State();
            c = c.next;
            c.attribute = it.next();
        }
        return s;
    }
    
    public final void clearAttributes() {
        for (State state = this.getCurrentState(); state != null; state = state.next) {
            state.attribute.clear();
        }
    }
    
    public final void removeAllAttributes() {
        this.attributes.clear();
        this.attributeImpls.clear();
    }
    
    public final State captureState() {
        final State state = this.getCurrentState();
        return (state == null) ? null : state.clone();
    }
    
    public final void restoreState(State state) {
        if (state == null) {
            return;
        }
        do {
            final AttributeImpl targetImpl = this.attributeImpls.get(state.attribute.getClass());
            if (targetImpl == null) {
                throw new IllegalArgumentException("State contains AttributeImpl of type " + state.attribute.getClass().getName() + " that is not in in this AttributeSource");
            }
            state.attribute.copyTo(targetImpl);
            state = state.next;
        } while (state != null);
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        for (State state = this.getCurrentState(); state != null; state = state.next) {
            code = code * 31 + state.attribute.hashCode();
        }
        return code;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AttributeSource)) {
            return false;
        }
        final AttributeSource other = (AttributeSource)obj;
        if (!this.hasAttributes()) {
            return !other.hasAttributes();
        }
        if (!other.hasAttributes()) {
            return false;
        }
        if (this.attributeImpls.size() != other.attributeImpls.size()) {
            return false;
        }
        for (State thisState = this.getCurrentState(), otherState = other.getCurrentState(); thisState != null && otherState != null; thisState = thisState.next, otherState = otherState.next) {
            if (otherState.attribute.getClass() != thisState.attribute.getClass() || !otherState.attribute.equals(thisState.attribute)) {
                return false;
            }
        }
        return true;
    }
    
    public final String reflectAsString(final boolean prependAttClass) {
        final StringBuilder buffer = new StringBuilder();
        this.reflectWith(new AttributeReflector() {
            @Override
            public void reflect(final Class<? extends Attribute> attClass, final String key, final Object value) {
                if (buffer.length() > 0) {
                    buffer.append(',');
                }
                if (prependAttClass) {
                    buffer.append(attClass.getName()).append('#');
                }
                buffer.append(key).append('=').append((value == null) ? "null" : value);
            }
        });
        return buffer.toString();
    }
    
    public final void reflectWith(final AttributeReflector reflector) {
        for (State state = this.getCurrentState(); state != null; state = state.next) {
            state.attribute.reflectWith(reflector);
        }
    }
    
    public final AttributeSource cloneAttributes() {
        final AttributeSource clone = new AttributeSource(this.factory);
        if (this.hasAttributes()) {
            for (State state = this.getCurrentState(); state != null; state = state.next) {
                clone.attributeImpls.put(state.attribute.getClass(), state.attribute.clone());
            }
            for (final Map.Entry<Class<? extends Attribute>, AttributeImpl> entry : this.attributes.entrySet()) {
                clone.attributes.put(entry.getKey(), clone.attributeImpls.get(entry.getValue().getClass()));
            }
        }
        return clone;
    }
    
    public final void copyTo(final AttributeSource target) {
        for (State state = this.getCurrentState(); state != null; state = state.next) {
            final AttributeImpl targetImpl = target.attributeImpls.get(state.attribute.getClass());
            if (targetImpl == null) {
                throw new IllegalArgumentException("This AttributeSource contains AttributeImpl of type " + state.attribute.getClass().getName() + " that is not in the target");
            }
            state.attribute.copyTo(targetImpl);
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " " + this.reflectAsString(false);
    }
    
    static {
        implInterfaces = new ClassValue<Class<? extends Attribute>[]>() {
            @Override
            protected Class<? extends Attribute>[] computeValue(Class<?> clazz) {
                final Set<Class<? extends Attribute>> intfSet = new LinkedHashSet<Class<? extends Attribute>>();
                do {
                    for (final Class<?> curInterface : clazz.getInterfaces()) {
                        if (curInterface != Attribute.class && Attribute.class.isAssignableFrom(curInterface)) {
                            intfSet.add(curInterface.asSubclass(Attribute.class));
                        }
                    }
                    clazz = clazz.getSuperclass();
                } while (clazz != null);
                final Class<? extends Attribute>[] a = intfSet.toArray(new Class[intfSet.size()]);
                return a;
            }
        };
    }
    
    public static final class State implements Cloneable
    {
        AttributeImpl attribute;
        State next;
        
        public State clone() {
            final State clone = new State();
            clone.attribute = this.attribute.clone();
            if (this.next != null) {
                clone.next = this.next.clone();
            }
            return clone;
        }
    }
}
