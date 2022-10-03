package org.apache.tika.metadata;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;

public final class Property implements Comparable<Property>
{
    private static final Map<String, Property> PROPERTIES;
    private final String name;
    private final boolean internal;
    private final PropertyType propertyType;
    private final ValueType valueType;
    private final Property primaryProperty;
    private final Property[] secondaryExtractProperties;
    private final Set<String> choices;
    
    private Property(final String name, final boolean internal, final PropertyType propertyType, final ValueType valueType, final String[] choices, final Property primaryProperty, final Property[] secondaryExtractProperties) {
        this.name = name;
        this.internal = internal;
        this.propertyType = propertyType;
        this.valueType = valueType;
        if (choices != null) {
            this.choices = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>((Collection<? extends String>)Arrays.asList((Object[])choices.clone())));
        }
        else {
            this.choices = null;
        }
        if (primaryProperty != null) {
            this.primaryProperty = primaryProperty;
            this.secondaryExtractProperties = secondaryExtractProperties;
        }
        else {
            this.primaryProperty = this;
            this.secondaryExtractProperties = null;
            synchronized (Property.PROPERTIES) {
                Property.PROPERTIES.put(name, this);
            }
        }
    }
    
    private Property(final String name, final boolean internal, final PropertyType propertyType, final ValueType valueType, final String[] choices) {
        this(name, internal, propertyType, valueType, choices, null, null);
    }
    
    private Property(final String name, final boolean internal, final ValueType valueType, final String[] choices) {
        this(name, internal, PropertyType.SIMPLE, valueType, choices);
    }
    
    private Property(final String name, final boolean internal, final ValueType valueType) {
        this(name, internal, PropertyType.SIMPLE, valueType, null);
    }
    
    private Property(final String name, final boolean internal, final PropertyType propertyType, final ValueType valueType) {
        this(name, internal, propertyType, valueType, null);
    }
    
    public static PropertyType getPropertyType(final String key) {
        PropertyType type = null;
        final Property prop = Property.PROPERTIES.get(key);
        if (prop != null) {
            type = prop.getPropertyType();
        }
        return type;
    }
    
    public static Property get(final String key) {
        return Property.PROPERTIES.get(key);
    }
    
    public static SortedSet<Property> getProperties(final String prefix) {
        final SortedSet<Property> set = new TreeSet<Property>();
        final String p = prefix + ":";
        synchronized (Property.PROPERTIES) {
            for (final String name : Property.PROPERTIES.keySet()) {
                if (name.startsWith(p)) {
                    set.add(Property.PROPERTIES.get(name));
                }
            }
        }
        return set;
    }
    
    public static Property internalBoolean(final String name) {
        return new Property(name, true, ValueType.BOOLEAN);
    }
    
    public static Property internalClosedChoise(final String name, final String... choices) {
        return new Property(name, true, ValueType.CLOSED_CHOICE, choices);
    }
    
    public static Property internalDate(final String name) {
        return new Property(name, true, ValueType.DATE);
    }
    
    public static Property internalInteger(final String name) {
        return new Property(name, true, ValueType.INTEGER);
    }
    
    public static Property internalIntegerSequence(final String name) {
        return new Property(name, true, PropertyType.SEQ, ValueType.INTEGER);
    }
    
    public static Property internalRational(final String name) {
        return new Property(name, true, ValueType.RATIONAL);
    }
    
    public static Property internalOpenChoise(final String name, final String... choices) {
        return new Property(name, true, ValueType.OPEN_CHOICE, choices);
    }
    
    public static Property internalReal(final String name) {
        return new Property(name, true, ValueType.REAL);
    }
    
    public static Property internalText(final String name) {
        return new Property(name, true, ValueType.TEXT);
    }
    
    public static Property internalTextBag(final String name) {
        return new Property(name, true, PropertyType.BAG, ValueType.TEXT);
    }
    
    public static Property internalURI(final String name) {
        return new Property(name, true, ValueType.URI);
    }
    
    public static Property externalClosedChoise(final String name, final String... choices) {
        return new Property(name, false, ValueType.CLOSED_CHOICE, choices);
    }
    
    public static Property externalOpenChoise(final String name, final String... choices) {
        return new Property(name, false, ValueType.OPEN_CHOICE, choices);
    }
    
    public static Property externalDate(final String name) {
        return new Property(name, false, ValueType.DATE);
    }
    
    public static Property externalReal(final String name) {
        return new Property(name, false, ValueType.REAL);
    }
    
    public static Property externalRealSeq(final String name) {
        return new Property(name, false, PropertyType.SEQ, ValueType.REAL);
    }
    
    public static Property externalInteger(final String name) {
        return new Property(name, false, ValueType.INTEGER);
    }
    
    public static Property externalBoolean(final String name) {
        return new Property(name, false, ValueType.BOOLEAN);
    }
    
    public static Property externalBooleanSeq(final String name) {
        return new Property(name, false, PropertyType.SEQ, ValueType.BOOLEAN);
    }
    
    public static Property externalText(final String name) {
        return new Property(name, false, ValueType.TEXT);
    }
    
    public static Property externalTextBag(final String name) {
        return new Property(name, false, PropertyType.BAG, ValueType.TEXT);
    }
    
    public static Property composite(final Property primaryProperty, final Property[] secondaryExtractProperties) {
        if (primaryProperty == null) {
            throw new NullPointerException("primaryProperty must not be null");
        }
        if (primaryProperty.getPropertyType() == PropertyType.COMPOSITE) {
            throw new PropertyTypeException(primaryProperty.getPropertyType());
        }
        if (secondaryExtractProperties != null) {
            for (final Property secondaryExtractProperty : secondaryExtractProperties) {
                if (secondaryExtractProperty.getPropertyType() == PropertyType.COMPOSITE) {
                    throw new PropertyTypeException(secondaryExtractProperty.getPropertyType());
                }
            }
        }
        String[] choices = null;
        if (primaryProperty.getChoices() != null) {
            choices = primaryProperty.getChoices().toArray(new String[0]);
        }
        return new Property(primaryProperty.getName(), primaryProperty.isInternal(), PropertyType.COMPOSITE, ValueType.PROPERTY, choices, primaryProperty, secondaryExtractProperties);
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isInternal() {
        return this.internal;
    }
    
    public boolean isExternal() {
        return !this.internal;
    }
    
    public boolean isMultiValuePermitted() {
        return this.propertyType == PropertyType.BAG || this.propertyType == PropertyType.SEQ || this.propertyType == PropertyType.ALT || (this.propertyType == PropertyType.COMPOSITE && this.primaryProperty.isMultiValuePermitted());
    }
    
    public PropertyType getPropertyType() {
        return this.propertyType;
    }
    
    public ValueType getValueType() {
        return this.valueType;
    }
    
    public Set<String> getChoices() {
        return this.choices;
    }
    
    public Property getPrimaryProperty() {
        return this.primaryProperty;
    }
    
    public Property[] getSecondaryExtractProperties() {
        return this.secondaryExtractProperties;
    }
    
    @Override
    public int compareTo(final Property o) {
        return this.name.compareTo(o.name);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Property && this.name.equals(((Property)o).name);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    static {
        PROPERTIES = new ConcurrentHashMap<String, Property>();
    }
    
    public enum PropertyType
    {
        SIMPLE, 
        STRUCTURE, 
        BAG, 
        SEQ, 
        ALT, 
        COMPOSITE;
    }
    
    public enum ValueType
    {
        BOOLEAN, 
        OPEN_CHOICE, 
        CLOSED_CHOICE, 
        DATE, 
        INTEGER, 
        LOCALE, 
        MIME_TYPE, 
        PROPER_NAME, 
        RATIONAL, 
        REAL, 
        TEXT, 
        URI, 
        URL, 
        XPATH, 
        PROPERTY;
    }
}
