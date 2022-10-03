package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.Repository;

public final class ObjectType extends ReferenceType
{
    private String class_name;
    
    public ObjectType(final String class_name) {
        super((byte)14, "L" + class_name.replace('.', '/') + ";");
        this.class_name = class_name.replace('/', '.');
    }
    
    public String getClassName() {
        return this.class_name;
    }
    
    @Override
    public int hashCode() {
        return this.class_name.hashCode();
    }
    
    @Override
    public boolean equals(final Object type) {
        return type instanceof ObjectType && ((ObjectType)type).class_name.equals(this.class_name);
    }
    
    public boolean referencesClass() {
        final JavaClass jc = Repository.lookupClass(this.class_name);
        return jc != null && jc.isClass();
    }
    
    public boolean referencesInterface() {
        final JavaClass jc = Repository.lookupClass(this.class_name);
        return jc != null && !jc.isClass();
    }
    
    public boolean subclassOf(final ObjectType superclass) {
        return !this.referencesInterface() && !superclass.referencesInterface() && Repository.instanceOf(this.class_name, superclass.class_name);
    }
    
    public boolean accessibleTo(final ObjectType accessor) {
        final JavaClass jc = Repository.lookupClass(this.class_name);
        if (jc.isPublic()) {
            return true;
        }
        final JavaClass acc = Repository.lookupClass(accessor.class_name);
        return acc.getPackageName().equals(jc.getPackageName());
    }
}
