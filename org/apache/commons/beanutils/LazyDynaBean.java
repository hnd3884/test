package org.apache.commons.beanutils;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.lang.reflect.Array;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.logging.Log;
import java.io.Serializable;

public class LazyDynaBean implements DynaBean, Serializable
{
    private transient Log logger;
    protected static final BigInteger BigInteger_ZERO;
    protected static final BigDecimal BigDecimal_ZERO;
    protected static final Character Character_SPACE;
    protected static final Byte Byte_ZERO;
    protected static final Short Short_ZERO;
    protected static final Integer Integer_ZERO;
    protected static final Long Long_ZERO;
    protected static final Float Float_ZERO;
    protected static final Double Double_ZERO;
    protected Map<String, Object> values;
    private transient Map<String, Object> mapDecorator;
    protected MutableDynaClass dynaClass;
    
    public LazyDynaBean() {
        this(new LazyDynaClass());
    }
    
    public LazyDynaBean(final String name) {
        this(new LazyDynaClass(name));
    }
    
    public LazyDynaBean(final DynaClass dynaClass) {
        this.logger = LogFactory.getLog((Class)LazyDynaBean.class);
        this.values = this.newMap();
        if (dynaClass instanceof MutableDynaClass) {
            this.dynaClass = (MutableDynaClass)dynaClass;
        }
        else {
            this.dynaClass = new LazyDynaClass(dynaClass.getName(), dynaClass.getDynaProperties());
        }
    }
    
    public Map<String, Object> getMap() {
        if (this.mapDecorator == null) {
            this.mapDecorator = new DynaBeanPropertyMapDecorator(this);
        }
        return this.mapDecorator;
    }
    
    public int size(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        final Object value = this.values.get(name);
        if (value == null) {
            return 0;
        }
        if (value instanceof Map) {
            return ((Map)value).size();
        }
        if (value instanceof List) {
            return ((List)value).size();
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value);
        }
        return 0;
    }
    
    @Override
    public boolean contains(final String name, final String key) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        final Object value = this.values.get(name);
        return value != null && value instanceof Map && ((Map)value).containsKey(key);
    }
    
    @Override
    public Object get(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        Object value = this.values.get(name);
        if (value != null) {
            return value;
        }
        if (!this.isDynaProperty(name)) {
            return null;
        }
        value = this.createProperty(name, this.dynaClass.getDynaProperty(name).getType());
        if (value != null) {
            this.set(name, value);
        }
        return value;
    }
    
    @Override
    public Object get(final String name, final int index) {
        if (!this.isDynaProperty(name)) {
            this.set(name, this.defaultIndexedProperty(name));
        }
        Object indexedProperty = this.get(name);
        if (!this.dynaClass.getDynaProperty(name).isIndexed()) {
            throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]' " + this.dynaClass.getDynaProperty(name).getName());
        }
        indexedProperty = this.growIndexedProperty(name, indexedProperty, index);
        if (indexedProperty.getClass().isArray()) {
            return Array.get(indexedProperty, index);
        }
        if (indexedProperty instanceof List) {
            return ((List)indexedProperty).get(index);
        }
        throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]' " + indexedProperty.getClass().getName());
    }
    
    @Override
    public Object get(final String name, final String key) {
        if (!this.isDynaProperty(name)) {
            this.set(name, this.defaultMappedProperty(name));
        }
        final Object mappedProperty = this.get(name);
        if (!this.dynaClass.getDynaProperty(name).isMapped()) {
            throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")' " + this.dynaClass.getDynaProperty(name).getType().getName());
        }
        if (mappedProperty instanceof Map) {
            return ((Map)mappedProperty).get(key);
        }
        throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'" + mappedProperty.getClass().getName());
    }
    
    @Override
    public DynaClass getDynaClass() {
        return this.dynaClass;
    }
    
    @Override
    public void remove(final String name, final String key) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        final Object value = this.values.get(name);
        if (value == null) {
            return;
        }
        if (value instanceof Map) {
            ((Map)value).remove(key);
            return;
        }
        throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'" + value.getClass().getName());
    }
    
    @Override
    public void set(final String name, final Object value) {
        if (!this.isDynaProperty(name)) {
            if (this.dynaClass.isRestricted()) {
                throw new IllegalArgumentException("Invalid property name '" + name + "' (DynaClass is restricted)");
            }
            if (value == null) {
                this.dynaClass.add(name);
            }
            else {
                this.dynaClass.add(name, value.getClass());
            }
        }
        final DynaProperty descriptor = this.dynaClass.getDynaProperty(name);
        if (value == null) {
            if (descriptor.getType().isPrimitive()) {
                throw new NullPointerException("Primitive value for '" + name + "'");
            }
        }
        else if (!this.isAssignable(descriptor.getType(), value.getClass())) {
            throw new ConversionException("Cannot assign value of type '" + value.getClass().getName() + "' to property '" + name + "' of type '" + descriptor.getType().getName() + "'");
        }
        this.values.put(name, value);
    }
    
    @Override
    public void set(final String name, final int index, final Object value) {
        if (!this.isDynaProperty(name)) {
            this.set(name, this.defaultIndexedProperty(name));
        }
        Object indexedProperty = this.get(name);
        if (!this.dynaClass.getDynaProperty(name).isIndexed()) {
            throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]'" + this.dynaClass.getDynaProperty(name).getType().getName());
        }
        indexedProperty = this.growIndexedProperty(name, indexedProperty, index);
        if (indexedProperty.getClass().isArray()) {
            Array.set(indexedProperty, index, value);
        }
        else {
            if (!(indexedProperty instanceof List)) {
                throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]' " + indexedProperty.getClass().getName());
            }
            final List<Object> values = (List<Object>)indexedProperty;
            values.set(index, value);
        }
    }
    
    @Override
    public void set(final String name, final String key, final Object value) {
        if (!this.isDynaProperty(name)) {
            this.set(name, this.defaultMappedProperty(name));
        }
        final Object mappedProperty = this.get(name);
        if (!this.dynaClass.getDynaProperty(name).isMapped()) {
            throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'" + this.dynaClass.getDynaProperty(name).getType().getName());
        }
        final Map<String, Object> valuesMap = (Map<String, Object>)mappedProperty;
        valuesMap.put(key, value);
    }
    
    protected Object growIndexedProperty(final String name, Object indexedProperty, final int index) {
        if (indexedProperty instanceof List) {
            final List<Object> list = (List<Object>)indexedProperty;
            while (index >= list.size()) {
                final Class<?> contentType = this.getDynaClass().getDynaProperty(name).getContentType();
                Object value = null;
                if (contentType != null) {
                    value = this.createProperty(name + "[" + list.size() + "]", contentType);
                }
                list.add(value);
            }
        }
        if (indexedProperty.getClass().isArray()) {
            final int length = Array.getLength(indexedProperty);
            if (index >= length) {
                final Class<?> componentType = indexedProperty.getClass().getComponentType();
                final Object newArray = Array.newInstance(componentType, index + 1);
                System.arraycopy(indexedProperty, 0, newArray, 0, length);
                indexedProperty = newArray;
                this.set(name, indexedProperty);
                for (int newLength = Array.getLength(indexedProperty), i = length; i < newLength; ++i) {
                    Array.set(indexedProperty, i, this.createProperty(name + "[" + i + "]", componentType));
                }
            }
        }
        return indexedProperty;
    }
    
    protected Object createProperty(final String name, final Class<?> type) {
        if (type == null) {
            return null;
        }
        if (type.isArray() || List.class.isAssignableFrom(type)) {
            return this.createIndexedProperty(name, type);
        }
        if (Map.class.isAssignableFrom(type)) {
            return this.createMappedProperty(name, type);
        }
        if (DynaBean.class.isAssignableFrom(type)) {
            return this.createDynaBeanProperty(name, type);
        }
        if (type.isPrimitive()) {
            return this.createPrimitiveProperty(name, type);
        }
        if (Number.class.isAssignableFrom(type)) {
            return this.createNumberProperty(name, type);
        }
        return this.createOtherProperty(name, type);
    }
    
    protected Object createIndexedProperty(final String name, final Class<?> type) {
        Object indexedProperty = null;
        if (type == null) {
            indexedProperty = this.defaultIndexedProperty(name);
        }
        else {
            if (!type.isArray()) {
                if (List.class.isAssignableFrom(type)) {
                    if (type.isInterface()) {
                        indexedProperty = this.defaultIndexedProperty(name);
                        return indexedProperty;
                    }
                    try {
                        indexedProperty = type.newInstance();
                        return indexedProperty;
                    }
                    catch (final Exception ex) {
                        throw new IllegalArgumentException("Error instantiating indexed property of type '" + type.getName() + "' for '" + name + "' " + ex);
                    }
                }
                throw new IllegalArgumentException("Non-indexed property of type '" + type.getName() + "' for '" + name + "'");
            }
            indexedProperty = Array.newInstance(type.getComponentType(), 0);
        }
        return indexedProperty;
    }
    
    protected Object createMappedProperty(final String name, final Class<?> type) {
        Object mappedProperty = null;
        if (type == null) {
            mappedProperty = this.defaultMappedProperty(name);
        }
        else {
            if (!type.isInterface()) {
                if (Map.class.isAssignableFrom(type)) {
                    try {
                        mappedProperty = type.newInstance();
                        return mappedProperty;
                    }
                    catch (final Exception ex) {
                        throw new IllegalArgumentException("Error instantiating mapped property of type '" + type.getName() + "' for '" + name + "' " + ex);
                    }
                }
                throw new IllegalArgumentException("Non-mapped property of type '" + type.getName() + "' for '" + name + "'");
            }
            mappedProperty = this.defaultMappedProperty(name);
        }
        return mappedProperty;
    }
    
    protected Object createDynaBeanProperty(final String name, final Class<?> type) {
        try {
            return type.newInstance();
        }
        catch (final Exception ex) {
            if (this.logger().isWarnEnabled()) {
                this.logger().warn((Object)("Error instantiating DynaBean property of type '" + type.getName() + "' for '" + name + "' " + ex));
            }
            return null;
        }
    }
    
    protected Object createPrimitiveProperty(final String name, final Class<?> type) {
        if (type == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (type == Integer.TYPE) {
            return LazyDynaBean.Integer_ZERO;
        }
        if (type == Long.TYPE) {
            return LazyDynaBean.Long_ZERO;
        }
        if (type == Double.TYPE) {
            return LazyDynaBean.Double_ZERO;
        }
        if (type == Float.TYPE) {
            return LazyDynaBean.Float_ZERO;
        }
        if (type == Byte.TYPE) {
            return LazyDynaBean.Byte_ZERO;
        }
        if (type == Short.TYPE) {
            return LazyDynaBean.Short_ZERO;
        }
        if (type == Character.TYPE) {
            return LazyDynaBean.Character_SPACE;
        }
        return null;
    }
    
    protected Object createNumberProperty(final String name, final Class<?> type) {
        return null;
    }
    
    protected Object createOtherProperty(final String name, final Class<?> type) {
        if (type == Object.class || type == String.class || type == Boolean.class || type == Character.class || Date.class.isAssignableFrom(type)) {
            return null;
        }
        try {
            return type.newInstance();
        }
        catch (final Exception ex) {
            if (this.logger().isWarnEnabled()) {
                this.logger().warn((Object)("Error instantiating property of type '" + type.getName() + "' for '" + name + "' " + ex));
            }
            return null;
        }
    }
    
    protected Object defaultIndexedProperty(final String name) {
        return new ArrayList();
    }
    
    protected Map<String, Object> defaultMappedProperty(final String name) {
        return new HashMap<String, Object>();
    }
    
    protected boolean isDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        if (this.dynaClass instanceof LazyDynaClass) {
            return ((LazyDynaClass)this.dynaClass).isDynaProperty(name);
        }
        return this.dynaClass.getDynaProperty(name) != null;
    }
    
    protected boolean isAssignable(final Class<?> dest, final Class<?> source) {
        return dest.isAssignableFrom(source) || (dest == Boolean.TYPE && source == Boolean.class) || (dest == Byte.TYPE && source == Byte.class) || (dest == Character.TYPE && source == Character.class) || (dest == Double.TYPE && source == Double.class) || (dest == Float.TYPE && source == Float.class) || (dest == Integer.TYPE && source == Integer.class) || (dest == Long.TYPE && source == Long.class) || (dest == Short.TYPE && source == Short.class);
    }
    
    protected Map<String, Object> newMap() {
        return new HashMap<String, Object>();
    }
    
    private Log logger() {
        if (this.logger == null) {
            this.logger = LogFactory.getLog((Class)LazyDynaBean.class);
        }
        return this.logger;
    }
    
    static {
        BigInteger_ZERO = new BigInteger("0");
        BigDecimal_ZERO = new BigDecimal("0");
        Character_SPACE = new Character(' ');
        Byte_ZERO = new Byte((byte)0);
        Short_ZERO = new Short((short)0);
        Integer_ZERO = new Integer(0);
        Long_ZERO = new Long(0L);
        Float_ZERO = new Float(0.0f);
        Double_ZERO = new Double(0.0);
    }
}
