package org.apache.commons.collections;

import org.apache.commons.collections.keyvalue.AbstractMapEntry;
import java.lang.reflect.Constructor;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.List;
import org.apache.commons.collections.list.UnmodifiableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.AbstractSet;
import org.apache.commons.collections.set.UnmodifiableSet;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.HashMap;
import java.util.AbstractMap;

public class BeanMap extends AbstractMap implements Cloneable
{
    private transient Object bean;
    private transient HashMap readMethods;
    private transient HashMap writeMethods;
    private transient HashMap types;
    public static final Object[] NULL_ARGUMENTS;
    public static HashMap defaultTransformers;
    
    public BeanMap() {
        this.readMethods = new HashMap();
        this.writeMethods = new HashMap();
        this.types = new HashMap();
    }
    
    public BeanMap(final Object bean) {
        this.readMethods = new HashMap();
        this.writeMethods = new HashMap();
        this.types = new HashMap();
        this.bean = bean;
        this.initialise();
    }
    
    public String toString() {
        return "BeanMap<" + String.valueOf(this.bean) + ">";
    }
    
    public Object clone() throws CloneNotSupportedException {
        final BeanMap newMap = (BeanMap)super.clone();
        if (this.bean == null) {
            return newMap;
        }
        Object newBean = null;
        Class beanClass = null;
        try {
            beanClass = this.bean.getClass();
            newBean = beanClass.newInstance();
        }
        catch (final Exception e) {
            throw new CloneNotSupportedException("Unable to instantiate the underlying bean \"" + beanClass.getName() + "\": " + e);
        }
        try {
            newMap.setBean(newBean);
        }
        catch (final Exception exception) {
            throw new CloneNotSupportedException("Unable to set bean in the cloned bean map: " + exception);
        }
        try {
            final Iterator readableKeys = this.readMethods.keySet().iterator();
            while (readableKeys.hasNext()) {
                final Object key = readableKeys.next();
                if (this.getWriteMethod(key) != null) {
                    newMap.put(key, this.get(key));
                }
            }
        }
        catch (final Exception exception) {
            throw new CloneNotSupportedException("Unable to copy bean values to cloned bean map: " + exception);
        }
        return newMap;
    }
    
    public void putAllWriteable(final BeanMap map) {
        final Iterator readableKeys = map.readMethods.keySet().iterator();
        while (readableKeys.hasNext()) {
            final Object key = readableKeys.next();
            if (this.getWriteMethod(key) != null) {
                this.put(key, map.get(key));
            }
        }
    }
    
    public void clear() {
        if (this.bean == null) {
            return;
        }
        Class beanClass = null;
        try {
            beanClass = this.bean.getClass();
            this.bean = beanClass.newInstance();
        }
        catch (final Exception e) {
            throw new UnsupportedOperationException("Could not create new instance of class: " + beanClass);
        }
    }
    
    public boolean containsKey(final Object name) {
        final Method method = this.getReadMethod(name);
        return method != null;
    }
    
    public boolean containsValue(final Object value) {
        return super.containsValue(value);
    }
    
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
            this.logInfo(e);
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (final IllegalAccessException e2) {
            this.logInfo(e2);
            throw new IllegalArgumentException(e2.getMessage());
        }
        return oldValue;
    }
    
    public int size() {
        return this.readMethods.size();
    }
    
    public Set keySet() {
        return UnmodifiableSet.decorate(this.readMethods.keySet());
    }
    
    public Set entrySet() {
        return UnmodifiableSet.decorate(new AbstractSet() {
            public Iterator iterator() {
                return BeanMap.this.entryIterator();
            }
            
            public int size() {
                return BeanMap.this.readMethods.size();
            }
        });
    }
    
    public Collection values() {
        final ArrayList answer = new ArrayList(this.readMethods.size());
        final Iterator iter = this.valueIterator();
        while (iter.hasNext()) {
            answer.add(iter.next());
        }
        return UnmodifiableList.decorate(answer);
    }
    
    public Class getType(final String name) {
        return this.types.get(name);
    }
    
    public Iterator keyIterator() {
        return this.readMethods.keySet().iterator();
    }
    
    public Iterator valueIterator() {
        final Iterator iter = this.keyIterator();
        return new Iterator() {
            public boolean hasNext() {
                return iter.hasNext();
            }
            
            public Object next() {
                final Object key = iter.next();
                return BeanMap.this.get(key);
            }
            
            public void remove() {
                throw new UnsupportedOperationException("remove() not supported for BeanMap");
            }
        };
    }
    
    public Iterator entryIterator() {
        final Iterator iter = this.keyIterator();
        return new Iterator() {
            public boolean hasNext() {
                return iter.hasNext();
            }
            
            public Object next() {
                final Object key = iter.next();
                final Object value = BeanMap.this.get(key);
                return new MyMapEntry(BeanMap.this, key, value);
            }
            
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
        final Class beanClass = this.getBean().getClass();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (propertyDescriptors != null) {
                for (int i = 0; i < propertyDescriptors.length; ++i) {
                    final PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                    if (propertyDescriptor != null) {
                        final String name = propertyDescriptor.getName();
                        final Method readMethod = propertyDescriptor.getReadMethod();
                        final Method writeMethod = propertyDescriptor.getWriteMethod();
                        final Class aType = propertyDescriptor.getPropertyType();
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
                final Class[] types = method.getParameterTypes();
                if (types != null && types.length > 0) {
                    final Class paramType = types[0];
                    if (!paramType.isAssignableFrom(value.getClass())) {
                        value = this.convertType(paramType, value);
                    }
                }
            }
            final Object[] answer = { value };
            return answer;
        }
        catch (final InvocationTargetException e) {
            this.logInfo(e);
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (final InstantiationException e2) {
            this.logInfo(e2);
            throw new IllegalArgumentException(e2.getMessage());
        }
    }
    
    protected Object convertType(final Class newType, final Object value) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Class[] types = { value.getClass() };
        try {
            final Constructor constructor = newType.getConstructor((Class[])types);
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
    
    protected Transformer getTypeTransformer(final Class aType) {
        return BeanMap.defaultTransformers.get(aType);
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
        (BeanMap.defaultTransformers = new HashMap()).put(Boolean.TYPE, new Transformer() {
            public Object transform(final Object input) {
                return Boolean.valueOf(input.toString());
            }
        });
        BeanMap.defaultTransformers.put(Character.TYPE, new Transformer() {
            public Object transform(final Object input) {
                return new Character(input.toString().charAt(0));
            }
        });
        BeanMap.defaultTransformers.put(Byte.TYPE, new Transformer() {
            public Object transform(final Object input) {
                return Byte.valueOf(input.toString());
            }
        });
        BeanMap.defaultTransformers.put(Short.TYPE, new Transformer() {
            public Object transform(final Object input) {
                return Short.valueOf(input.toString());
            }
        });
        BeanMap.defaultTransformers.put(Integer.TYPE, new Transformer() {
            public Object transform(final Object input) {
                return Integer.valueOf(input.toString());
            }
        });
        BeanMap.defaultTransformers.put(Long.TYPE, new Transformer() {
            public Object transform(final Object input) {
                return Long.valueOf(input.toString());
            }
        });
        BeanMap.defaultTransformers.put(Float.TYPE, new Transformer() {
            public Object transform(final Object input) {
                return Float.valueOf(input.toString());
            }
        });
        BeanMap.defaultTransformers.put(Double.TYPE, new Transformer() {
            public Object transform(final Object input) {
                return Double.valueOf(input.toString());
            }
        });
    }
    
    protected static class MyMapEntry extends AbstractMapEntry
    {
        private BeanMap owner;
        
        protected MyMapEntry(final BeanMap owner, final Object key, final Object value) {
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
