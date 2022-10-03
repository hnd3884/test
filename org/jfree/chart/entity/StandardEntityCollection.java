package org.jfree.chart.entity;

import org.jfree.util.ObjectUtilities;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class StandardEntityCollection implements EntityCollection, Cloneable, Serializable
{
    private static final long serialVersionUID = 5384773031184897047L;
    private List entities;
    
    public StandardEntityCollection() {
        this.entities = new ArrayList();
    }
    
    public int getEntityCount() {
        return this.entities.size();
    }
    
    public ChartEntity getEntity(final int index) {
        return this.entities.get(index);
    }
    
    public void clear() {
        this.entities.clear();
    }
    
    public void add(final ChartEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Null 'entity' argument.");
        }
        this.entities.add(entity);
    }
    
    public void addAll(final EntityCollection collection) {
        this.entities.addAll(collection.getEntities());
    }
    
    public ChartEntity getEntity(final double x, final double y) {
        final int entityCount = this.entities.size();
        for (int i = entityCount - 1; i >= 0; --i) {
            final ChartEntity entity = this.entities.get(i);
            if (entity.getArea().contains(x, y)) {
                return entity;
            }
        }
        return null;
    }
    
    public Collection getEntities() {
        return Collections.unmodifiableCollection((Collection<?>)this.entities);
    }
    
    public Iterator iterator() {
        return this.entities.iterator();
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof StandardEntityCollection) {
            final StandardEntityCollection that = (StandardEntityCollection)obj;
            return ObjectUtilities.equal((Object)this.entities, (Object)that.entities);
        }
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
