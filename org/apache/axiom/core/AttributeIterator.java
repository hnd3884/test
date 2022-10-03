package org.apache.axiom.core;

import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.Iterator;

final class AttributeIterator<T extends CoreAttribute, S> implements Iterator<S>
{
    private final Class<T> type;
    private final Mapper<T, S> mapper;
    private final Semantics semantics;
    private CoreAttribute currentAttribute;
    private CoreAttribute nextAttribute;
    private boolean nextAttributeSet;
    
    private AttributeIterator(final CoreAttribute firstAttribute, final Class<T> type, final Mapper<T, S> mapper, final Semantics semantics) {
        this.type = type;
        this.mapper = mapper;
        this.semantics = semantics;
        this.nextAttribute = firstAttribute;
        this.nextAttributeSet = true;
    }
    
    static <T extends CoreAttribute, S> Iterator<S> create(final CoreElement element, final Class<T> type, final Mapper<T, S> mapper, final Semantics semantics) {
        CoreAttribute attribute;
        for (attribute = CoreElementSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetFirstAttribute(element); attribute != null && !type.isInstance(attribute); attribute = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(attribute)) {}
        if (attribute == null) {
            return Collections.emptyList().iterator();
        }
        return new AttributeIterator<Object, S>(attribute, type, mapper, semantics);
    }
    
    public final boolean hasNext() {
        if (!this.nextAttributeSet) {
            CoreAttribute attribute = this.currentAttribute;
            do {
                attribute = CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(attribute);
            } while (attribute != null && !this.type.isInstance(attribute));
            this.nextAttribute = attribute;
            this.nextAttributeSet = true;
        }
        return this.nextAttribute != null;
    }
    
    public final S next() {
        if (this.hasNext()) {
            final CoreAttribute attribute = this.nextAttribute;
            this.currentAttribute = attribute;
            this.nextAttribute = null;
            this.nextAttributeSet = false;
            return this.mapper.map(this.type.cast(attribute));
        }
        throw new NoSuchElementException();
    }
    
    public final void remove() {
        if (this.currentAttribute == null) {
            throw new IllegalStateException();
        }
        this.hasNext();
        CoreAttributeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreRemove(this.currentAttribute, this.semantics);
        this.currentAttribute = null;
    }
}
