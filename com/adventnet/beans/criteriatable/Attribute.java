package com.adventnet.beans.criteriatable;

import java.util.Date;

public class Attribute
{
    public static final Class STRING_TYPE;
    public static final Class INTEGER_TYPE;
    public static final Class FLOAT_TYPE;
    public static final Class DOUBLE_TYPE;
    public static final Class LONG_TYPE;
    public static final Class DATE_TYPE;
    public static final Class BOOLEAN_TYPE;
    public static final Class OBJECT_TYPE;
    protected String displayName;
    protected Object valueObject;
    protected String[] comparators;
    protected AttributeValueEditorComponent valueEditor;
    protected Class type;
    
    public Attribute(final String displayName, final Object valueObject, final Class type, final String[] comparators, final AttributeValueEditorComponent valueEditor) {
        this.type = Attribute.OBJECT_TYPE;
        this.displayName = displayName;
        this.valueObject = valueObject;
        this.comparators = comparators;
        this.valueEditor = valueEditor;
        this.type = type;
    }
    
    public Attribute(final String s, final Object o, final String[] array, final AttributeValueEditorComponent attributeValueEditorComponent) {
        this(s, o, Attribute.STRING_TYPE, array, attributeValueEditorComponent);
    }
    
    public Attribute(final String s, final Object o, final Class clazz) {
        this(s, o, clazz, null, null);
    }
    
    public Attribute(final String s, final Object o, final Class clazz, final String[] array) {
        this(s, o, clazz, array, null);
    }
    
    public Attribute(final String s, final Object o, final Class clazz, final AttributeValueEditorComponent attributeValueEditorComponent) {
        this(s, o, clazz, null, attributeValueEditorComponent);
    }
    
    public Class getAttributeClass() {
        return this.type;
    }
    
    public Object getValueObject() {
        return this.valueObject;
    }
    
    public String[] getComparators() {
        return this.comparators;
    }
    
    public AttributeValueEditorComponent getValueEditorComponent() {
        return this.valueEditor;
    }
    
    public String toString() {
        return this.displayName;
    }
    
    static {
        STRING_TYPE = String.class;
        INTEGER_TYPE = Integer.class;
        FLOAT_TYPE = Float.class;
        DOUBLE_TYPE = Double.class;
        LONG_TYPE = Long.class;
        DATE_TYPE = Date.class;
        BOOLEAN_TYPE = Boolean.class;
        OBJECT_TYPE = Object.class;
    }
}
