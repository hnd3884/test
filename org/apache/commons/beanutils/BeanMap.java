package org.apache.commons.beanutils;

import org.apache.commons.collections.keyvalue.AbstractMapEntry;
import java.lang.reflect.Constructor;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import org.apache.commons.collections.Transformer;
import java.util.Map;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.AbstractMap;

public class BeanMap extends AbstractMap<Object, Object> implements Cloneable
{
    private transient Object bean;
    private transient HashMap<String, Method> readMethods;
    private transient HashMap<String, Method> writeMethods;
    private transient HashMap<String, Class<?>> types;
    public static final Object[] NULL_ARGUMENTS;
    private static final Map<Class<?>, Transformer> typeTransformers;
    @Deprecated
    public static HashMap defaultTransformers;
    
    private static Map<Class<?>, Transformer> createTypeTransformers() {
        final Map<Class<?>, Transformer> defaultTransformers = new HashMap<Class<?>, Transformer>();
        defaultTransformers.put(Boolean.TYPE, (Transformer)new Transformer() {
            public Object transform(final Object input) {
                return Boolean.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Character.TYPE, (Transformer)new Transformer() {
            public Object transform(final Object input) {
                return new Character(input.toString().charAt(0));
            }
        });
        defaultTransformers.put(Byte.TYPE, (Transformer)new Transformer() {
            public Object transform(final Object input) {
                return Byte.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Short.TYPE, (Transformer)new Transformer() {
            public Object transform(final Object input) {
                return Short.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Integer.TYPE, (Transformer)new Transformer() {
            public Object transform(final Object input) {
                return Integer.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Long.TYPE, (Transformer)new Transformer() {
            public Object transform(final Object input) {
                return Long.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Float.TYPE, (Transformer)new Transformer() {
            public Object transform(final Object input) {
                return Float.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Double.TYPE, (Transformer)new Transformer() {
            public Object transform(final Object input) {
                return Double.valueOf(input.toString());
            }
        });
        return defaultTransformers;
    }
    
    public BeanMap() {
        this.readMethods = new HashMap<String, Method>();
        this.writeMethods = new HashMap<String, Method>();
        this.types = new HashMap<String, Class<?>>();
    }
    
    public BeanMap(final Object bean) {
        this.readMethods = new HashMap<String, Method>();
        this.writeMethods = new HashMap<String, Method>();
        this.types = new HashMap<String, Class<?>>();
        this.bean = bean;
        this.initialise();
    }
    
    @Override
    public String toString() {
        return "BeanMap<" + String.valueOf(this.bean) + ">";
    }
    
    public Object clone() throws CloneNotSupportedException {
        final BeanMap newMap = (BeanMap)super.clone();
        if (this.bean == null) {
            return newMap;
        }
        Object newBean = null;
        final Class<?> beanClass = this.bean.getClass();
        try {
            newBean = beanClass.newInstance();
        }
        catch (final Exception e) {
            final CloneNotSupportedException cnse = new CloneNotSupportedException("Unable to instantiate the underlying bean \"" + beanClass.getName() + "\": " + e);
            BeanUtils.initCause(cnse, e);
            throw cnse;
        }
        try {
            newMap.setBean(newBean);
        }
        catch (final Exception exception) {
            final CloneNotSupportedException cnse = new CloneNotSupportedException("Unable to set bean in the cloned bean map: " + exception);
            BeanUtils.initCause(cnse, exception);
            throw cnse;
        }
        try {
            for (final Object key : this.readMethods.keySet()) {
                if (this.getWriteMethod(key) != null) {
                    newMap.put(key, this.get(key));
                }
            }
        }
        catch (final Exception exception) {
            final CloneNotSupportedException cnse = new CloneNotSupportedException("Unable to copy bean values to cloned bean map: " + exception);
            BeanUtils.initCause(cnse, exception);
            throw cnse;
        }
        return newMap;
    }
    
    public void putAllWriteable(final BeanMap map) {
        for (final Object key : map.readMethods.keySet()) {
            if (this.getWriteMethod(key) != null) {
                this.put(key, map.get(key));
            }
        }
    }
    
    @Override
    public void clear() {
        if (this.bean == null) {
            return;
        }
        Class<?> beanClass = null;
        try {
            beanClass = this.bean.getClass();
            this.bean = beanClass.newInstance();
        }
        catch (final Exception e) {
            final UnsupportedOperationException uoe = new UnsupportedOperationException("Could not create new instance of class: " + beanClass);
            BeanUtils.initCause(uoe, e);
            throw uoe;
        }
    }
    
    @Override
    public boolean containsKey(final Object name) {
        final Method method = this.getReadMethod(name);
        return method != null;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return super.containsValue(value);
    }
    
    @Override
    public Object get(final Object name) {
        if (this.bean != null) {
            final Method method = this.getReadMethod(name);
            if (method != null) {
                try {
                    return method.invoke(this.bean, BeanMap.NULL_ARGUMENTS);
                }
                catch (final IllegalAccessException e) {
                    this.logWarn(e);
                }
                catch (final IllegalArgumentException e2) {
                    this.logWarn(e2);
                }
                catch (final InvocationTargetException e3) {
                    this.logWarn(e3);
                }
                catch (final NullPointerException e4) {
                    this.logWarn(e4);
                }
            }
        }
        return null;
    }
    
    @Override
    public Object put(final Object name, final Object value) throws IllegalArgumentException, ClassCastException {
        if (this.bean == null) {
            return null;
        }
        final Object oldValue = this.get(name);
        final Method method = this.getWriteMethod(name);
        if (method == null) {
            throw new IllegalArgumentException("The bean of type: " + this.bean.getClass().getName() + " has no property called: " + name);
        }
        try {
            final Object[] arguments = this.createWriteMethodArguments(method, value);
            method.invoke(this.bean, arguments);
            final Object newValue = this.get(name);
            this.firePropertyChange(name, oldValue, newValue);
        }
        catch (final InvocationTargetException e) {
            final IllegalArgumentException iae = new IllegalArgumentException(e.getMessage());
            if (!BeanUtils.initCause(iae, e)) {
                this.logInfo(e);
            }
            throw iae;
        }
        catch (final IllegalAccessException e2) {
            final IllegalArgumentException iae = new IllegalArgumentException(e2.getMessage());
            if (!BeanUtils.initCause(iae, e2)) {
                this.logInfo(e2);
            }
            throw iae;
        }
        return oldValue;
    }
    
    @Override
    public int size() {
        return this.readMethods.size();
    }
    
    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet((Set<?>)this.readMethods.keySet());
    }
    
    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return Collections.unmodifiableSet((Set<? extends Map.Entry<Object, Object>>)new AbstractSet<Map.Entry<Object, Object>>() {
            @Override
            public Iterator<Map.Entry<Object, Object>> iterator() {
                return BeanMap.this.entryIterator();
            }
            
            @Override
            public int size() {
                return BeanMap.this.readMethods.size();
            }
        });
    }
    
    @Override
    public Collection<Object> values() {
        final ArrayList<Object> answer = new ArrayList<Object>(this.readMethods.size());
        final Iterator<Object> iter = this.valueIterator();
        while (iter.hasNext()) {
            answer.add(iter.next());
        }
        return Collections.unmodifiableList((List<?>)answer);
    }
    
    public Class<?> getType(final String name) {
        return this.types.get(name);
    }
    
    public Iterator<String> keyIterator() {
        return this.readMethods.keySet().iterator();
    }
    
    public Iterator<Object> valueIterator() {
        final Iterator<?> iter = this.keyIterator();
        return new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }
            
            @Override
            public Object next() {
                final Object key = iter.next();
                return BeanMap.this.get(key);
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() not supported for BeanMap");
            }
        };
    }
    
    public Iterator<Map.Entry<Object, Object>> entryIterator() {
        final Iterator<String> iter = this.keyIterator();
        return new Iterator<Map.Entry<Object, Object>>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }
            
            @Override
            public Map.Entry<Object, Object> next() {
                final Object key = iter.next();
                final Object value = BeanMap.this.get(key);
                final Map.Entry<Object, Object> tmpEntry = (Map.Entry<Object, Object>)new Entry(BeanMap.this, key, value);
                return tmpEntry;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() not supported for BeanMap");
            }
        };
    }
    
    public Object getBean() {
        return this.bean;
    }
    
    public void setBean(final Object newBean) {
        this.bean = newBean;
        this.reinitialise();
    }
    
    public Method getReadMethod(final String name) {
        return this.readMethods.get(name);
    }
    
    public Method getWriteMethod(final String name) {
        return this.writeMethods.get(name);
    }
    
    protected Method getReadMethod(final Object name) {
        return this.readMethods.get(name);
    }
    
    protected Method getWriteMethod(final Object name) {
        return this.writeMethods.get(name);
    }
    
    protected void reinitialise() {
        this.readMethods.clear();
        this.writeMethods.clear();
        this.types.clear();
        this.initialise();
    }
    
    private void initialise() {
        if (this.getBean() == null) {
            return;
        }
        final Class<?> beanClass = this.getBean().getClass();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (propertyDescriptors != null) {
                for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (propertyDescriptor != null) {
                        final String name = propertyDescriptor.getName();
                        final Method readMethod = propertyDescriptor.getReadMethod();
                        final Method writeMethod = propertyDescriptor.getWriteMethod();
                        final Class<?> aType = propertyDescriptor.getPropertyType();
                        if (readMethod != null) {
                            this.readMethods.put(name, readMethod);
                        }
                        if (writeMethod != null) {
                            this.writeMethods.put(name, writeMethod);
                        }
                        this.types.put(name, aType);
                    }
                }
            }
        }
        catch (final IntrospectionException e) {
            this.logWarn(e);
        }
    }
    
    protected void firePropertyChange(final Object key, final Object oldValue, final Object newValue) {
    }
    
    protected Object[] createWriteMethodArguments(final Method method, Object value) throws IllegalAccessException, ClassCastException {
        try {
            if (value != null) {
                final Class<?>[] types = method.getParameterTypes();
                if (types != null && types.length > 0) {
                    final Class<?> paramType = types[0];
                    if (!paramType.isAssignableFrom(value.getClass())) {
                        value = this.convertType(paramType, value);
                    }
                }
            }
            final Object[] answer = { value };
            return answer;
        }
        catch (final InvocationTargetException e) {
            final IllegalArgumentException iae = new IllegalArgumentException(e.getMessage());
            if (!BeanUtils.initCause(iae, e)) {
                this.logInfo(e);
            }
            throw iae;
        }
        catch (final InstantiationException e2) {
            final IllegalArgumentException iae = new IllegalArgumentException(e2.getMessage());
            if (!BeanUtils.initCause(iae, e2)) {
                this.logInfo(e2);
            }
            BeanUtils.initCause(iae, e2);
            throw iae;
        }
    }
    
    protected Object convertType(final Class<?> newType, final Object value) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Class<?>[] types = { value.getClass() };
        try {
            final Constructor<?> constructor = newType.getConstructor(types);
            final Object[] arguments = { value };
            return constructor.newInstance(arguments);
        }
        catch (final NoSuchMethodException e) {
            final Transformer transformer = this.getTypeTransformer(newType);
            if (transformer != null) {
                return transformer.transform(value);
            }
            return value;
        }
    }
    
    protected Transformer getTypeTransformer(final Class<?> aType) {
        return BeanMap.typeTransformers.get(aType);
    }
    
    protected void logInfo(final Exception ex) {
        System.out.println("INFO: Exception: " + ex);
    }
    
    protected void logWarn(final Exception ex) {
        System.out.println("WARN: Exception: " + ex);
        ex.printStackTrace();
    }
    
    static {
        NULL_ARGUMENTS = new Object[0];
        typeTransformers = Collections.unmodifiableMap((Map<? extends Class<?>, ? extends Transformer>)createTypeTransformers());
        BeanMap.defaultTransformers = new HashMap() {
            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean containsKey(final Object key) {
                return BeanMap.typeTransformers.containsKey(key);
            }
            
            @Override
            public boolean containsValue(final Object value) {
                return BeanMap.typeTransformers.containsValue(value);
            }
            
            @Override
            public Set entrySet() {
                return BeanMap.typeTransformers.entrySet();
            }
            
            @Override
            public Object get(final Object key) {
                return BeanMap.typeTransformers.get(key);
            }
            
            @Override
            public boolean isEmpty() {
                return false;
            }
            
            @Override
            public Set keySet() {
                return BeanMap.typeTransformers.keySet();
            }
            
            @Override
            public Object put(final Object key, final Object value) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void putAll(final Map m) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Object remove(final Object key) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public int size() {
                return BeanMap.typeTransformers.size();
            }
            
            @Override
            public Collection values() {
                return BeanMap.typeTransformers.values();
            }
        };
    }
    
    protected static class Entry extends AbstractMapEntry
    {
        private final BeanMap owner;
        
        protected Entry(final BeanMap owner, final Object key, final Object value) {
            super(key, value);
            this.owner = owner;
        }
        
        public Object setValue(final Object value) {
            final Object key = this.getKey();
            final Object oldValue = this.owner.get(key);
            this.owner.put(key, value);
            final Object newValue = this.owner.get(key);
            super.setValue(newValue);
            return oldValue;
        }
    }
}
