package com.sun.jna;

import java.util.Iterator;

public abstract class Union extends Structure
{
    private StructField activeField;
    StructField biggestField;
    
    protected Union() {
    }
    
    protected Union(final Pointer p) {
        super(p);
    }
    
    protected Union(final Pointer p, final int alignType) {
        super(p, alignType);
    }
    
    protected Union(final TypeMapper mapper) {
        super(mapper);
    }
    
    protected Union(final Pointer p, final int alignType, final TypeMapper mapper) {
        super(p, alignType, mapper);
    }
    
    public void setType(final Class type) {
        this.ensureAllocated();
        final Iterator i = this.fields().values().iterator();
        while (i.hasNext()) {
            final StructField f = i.next();
            if (f.type == type) {
                this.activeField = f;
                return;
            }
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }
    
    public Object readField(final String name) {
        this.ensureAllocated();
        final StructField f = this.fields().get(name);
        if (f != null) {
            this.setType(f.type);
        }
        return super.readField(name);
    }
    
    public void writeField(final String name) {
        this.ensureAllocated();
        final StructField f = this.fields().get(name);
        if (f != null) {
            this.setType(f.type);
        }
        super.writeField(name);
    }
    
    public void writeField(final String name, final Object value) {
        this.ensureAllocated();
        final StructField f = this.fields().get(name);
        if (f != null) {
            this.setType(f.type);
        }
        super.writeField(name, value);
    }
    
    public Object getTypedValue(final Class type) {
        this.ensureAllocated();
        final Iterator i = this.fields().values().iterator();
        while (i.hasNext()) {
            final StructField f = i.next();
            if (f.type == type) {
                this.activeField = f;
                this.read();
                return this.getField(this.activeField);
            }
        }
        throw new IllegalArgumentException("No field of type " + type + " in " + this);
    }
    
    public Object setTypedValue(final Object object) {
        this.ensureAllocated();
        final StructField f = this.findField(object.getClass());
        if (f != null) {
            this.setField(this.activeField = f, object);
            return this;
        }
        throw new IllegalArgumentException("No field of type " + object.getClass() + " in " + this);
    }
    
    private StructField findField(final Class type) {
        final Iterator i = this.fields().values().iterator();
        while (i.hasNext()) {
            final StructField f = i.next();
            if (f.type.isAssignableFrom(type)) {
                return f;
            }
        }
        return null;
    }
    
    void writeField(final StructField field) {
        if (field == this.activeField) {
            super.writeField(field);
        }
    }
    
    Object readField(final StructField field) {
        if (field == this.activeField || (!Structure.class.isAssignableFrom(field.type) && !String.class.isAssignableFrom(field.type) && !WString.class.isAssignableFrom(field.type))) {
            return super.readField(field);
        }
        return null;
    }
    
    int calculateSize(final boolean force) {
        int size = super.calculateSize(force);
        if (size != -1) {
            int fsize = 0;
            final Iterator i = this.fields().values().iterator();
            while (i.hasNext()) {
                final StructField f = i.next();
                f.offset = 0;
                if (f.size > fsize || (f.size == fsize && Structure.class.isAssignableFrom(f.type))) {
                    fsize = f.size;
                    this.biggestField = f;
                }
            }
            size = this.calculateAlignedSize(fsize);
            if (size > 0 && this instanceof ByValue) {
                this.getTypeInfo();
            }
        }
        return size;
    }
    
    protected int getNativeAlignment(final Class type, final Object value, final boolean isFirstElement) {
        return super.getNativeAlignment(type, value, true);
    }
    
    Pointer getTypeInfo() {
        if (this.biggestField == null) {
            return null;
        }
        return super.getTypeInfo();
    }
}
