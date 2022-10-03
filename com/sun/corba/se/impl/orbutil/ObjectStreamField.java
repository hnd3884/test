package com.sun.corba.se.impl.orbutil;

import java.lang.reflect.Field;

class ObjectStreamField implements Comparable
{
    private String name;
    private char type;
    private Field field;
    private String typeString;
    private Class clazz;
    private String signature;
    private long fieldID;
    
    ObjectStreamField(final String name, final Class clazz) {
        this.fieldID = -1L;
        this.name = name;
        this.clazz = clazz;
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                this.type = 'I';
            }
            else if (clazz == Byte.TYPE) {
                this.type = 'B';
            }
            else if (clazz == Long.TYPE) {
                this.type = 'J';
            }
            else if (clazz == Float.TYPE) {
                this.type = 'F';
            }
            else if (clazz == Double.TYPE) {
                this.type = 'D';
            }
            else if (clazz == Short.TYPE) {
                this.type = 'S';
            }
            else if (clazz == Character.TYPE) {
                this.type = 'C';
            }
            else if (clazz == Boolean.TYPE) {
                this.type = 'Z';
            }
        }
        else if (clazz.isArray()) {
            this.type = '[';
            this.typeString = ObjectStreamClass_1_3_1.getSignature(clazz);
        }
        else {
            this.type = 'L';
            this.typeString = ObjectStreamClass_1_3_1.getSignature(clazz);
        }
        if (this.typeString != null) {
            this.signature = this.typeString;
        }
        else {
            this.signature = String.valueOf(this.type);
        }
    }
    
    ObjectStreamField(final Field field) {
        this(field.getName(), field.getType());
        this.field = field;
    }
    
    ObjectStreamField(final String name, final char type, final Field field, final String typeString) {
        this.fieldID = -1L;
        this.name = name;
        this.type = type;
        this.field = field;
        this.typeString = typeString;
        if (this.typeString != null) {
            this.signature = this.typeString;
        }
        else {
            this.signature = String.valueOf(this.type);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class getType() {
        if (this.clazz != null) {
            return this.clazz;
        }
        switch (this.type) {
            case 'B': {
                this.clazz = Byte.TYPE;
                break;
            }
            case 'C': {
                this.clazz = Character.TYPE;
                break;
            }
            case 'S': {
                this.clazz = Short.TYPE;
                break;
            }
            case 'I': {
                this.clazz = Integer.TYPE;
                break;
            }
            case 'J': {
                this.clazz = Long.TYPE;
                break;
            }
            case 'F': {
                this.clazz = Float.TYPE;
                break;
            }
            case 'D': {
                this.clazz = Double.TYPE;
                break;
            }
            case 'Z': {
                this.clazz = Boolean.TYPE;
                break;
            }
            case 'L':
            case '[': {
                this.clazz = Object.class;
                break;
            }
        }
        return this.clazz;
    }
    
    public char getTypeCode() {
        return this.type;
    }
    
    public String getTypeString() {
        return this.typeString;
    }
    
    Field getField() {
        return this.field;
    }
    
    void setField(final Field field) {
        this.field = field;
        this.fieldID = -1L;
    }
    
    ObjectStreamField() {
        this.fieldID = -1L;
    }
    
    public boolean isPrimitive() {
        return this.type != '[' && this.type != 'L';
    }
    
    @Override
    public int compareTo(final Object o) {
        final ObjectStreamField objectStreamField = (ObjectStreamField)o;
        final boolean b = this.typeString == null;
        if (b != (objectStreamField.typeString == null)) {
            return b ? -1 : 1;
        }
        return this.name.compareTo(objectStreamField.name);
    }
    
    public boolean typeEquals(final ObjectStreamField objectStreamField) {
        return objectStreamField != null && this.type == objectStreamField.type && ((this.typeString == null && objectStreamField.typeString == null) || ObjectStreamClass_1_3_1.compareClassNames(this.typeString, objectStreamField.typeString, '/'));
    }
    
    public String getSignature() {
        return this.signature;
    }
    
    @Override
    public String toString() {
        if (this.typeString != null) {
            return this.typeString + " " + this.name;
        }
        return this.type + " " + this.name;
    }
    
    public Class getClazz() {
        return this.clazz;
    }
}
