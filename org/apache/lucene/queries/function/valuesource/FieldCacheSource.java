package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.ValueSource;

public abstract class FieldCacheSource extends ValueSource
{
    protected final String field;
    
    public FieldCacheSource(final String field) {
        this.field = field;
    }
    
    public String getField() {
        return this.field;
    }
    
    @Override
    public String description() {
        return this.field;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof FieldCacheSource)) {
            return false;
        }
        final FieldCacheSource other = (FieldCacheSource)o;
        return this.field.equals(other.field);
    }
    
    @Override
    public int hashCode() {
        return this.field.hashCode();
    }
}
