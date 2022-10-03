package org.apache.commons.beanutils;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.beans.IndexedPropertyDescriptor;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;
import java.beans.Introspector;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.expression.DefaultResolver;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.beanutils.expression.Resolver;

public class PropertyUtilsBean
{
    private Resolver resolver;
    private WeakFastHashMap<Class<?>, BeanIntrospectionData> descriptorsCache;
    private WeakFastHashMap<Class<?>, FastHashMap> mappedDescriptorsCache;
    private static final Object[] EMPTY_OBJECT_ARRAY;
    private final Log log;
    private final List<BeanIntrospector> introspectors;
    
    protected static PropertyUtilsBean getInstance() {
        return BeanUtilsBean.getInstance().getPropertyUtils();
    }
    
    public PropertyUtilsBean() {
        this.resolver = new DefaultResolver();
        this.descriptorsCache = null;
        this.mappedDescriptorsCache = null;
        this.log = LogFactory.getLog((Class)PropertyUtils.class);
        (this.descriptorsCache = new WeakFastHashMap<Class<?>, BeanIntrospectionData>()).setFast(true);
        (this.mappedDescriptorsCache = new WeakFastHashMap<Class<?>, FastHashMap>()).setFast(true);
        this.introspectors = new CopyOnWriteArrayList<BeanIntrospector>();
        this.resetBeanIntrospectors();
    }
    
    public Resolver getResolver() {
        return this.resolver;
    }
    
    public void setResolver(final Resolver resolver) {
        if (resolver == null) {
            this.resolver = new DefaultResolver();
        }
        else {
            this.resolver = resolver;
        }
    }
    
    public final void resetBeanIntrospectors() {
        this.introspectors.clear();
        this.introspectors.add(DefaultBeanIntrospector.INSTANCE);
        this.introspectors.add(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
    }
    
    public void addBeanIntrospector(final BeanIntrospector introspector) {
        if (introspector == null) {
            throw new IllegalArgumentException("BeanIntrospector must not be null!");
        }
        this.introspectors.add(introspector);
    }
    
    public boolean removeBeanIntrospector(final BeanIntrospector introspector) {
        return this.introspectors.remove(introspector);
    }
    
    public void clearDescriptors() {
        this.descriptorsCache.clear();
        this.mappedDescriptorsCache.clear();
        Introspector.flushCaches();
    }
    
    public void copyProperties(final Object dest, final Object orig) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (orig instanceof DynaBean) {
            final DynaProperty[] dynaProperties;
            final DynaProperty[] origDescriptors = dynaProperties = ((DynaBean)orig).getDynaClass().getDynaProperties();
            for (final DynaProperty origDescriptor : dynaProperties) {
                final String name = origDescriptor.getName();
                if (this.isReadable(orig, name) && this.isWriteable(dest, name)) {
                    try {
                        final Object value = ((DynaBean)orig).get(name);
                        if (dest instanceof DynaBean) {
                            ((DynaBean)dest).set(name, value);
                        }
                        else {
                            this.setSimpleProperty(dest, name, value);
                        }
                    }
                    catch (final NoSuchMethodException e) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)("Error writing to '" + name + "' on class '" + dest.getClass() + "'"), (Throwable)e);
                        }
                    }
                }
            }
        }
        else if (orig instanceof Map) {
            for (final Map.Entry<?, ?> entry : ((Map)orig).entrySet()) {
                final String name2 = (String)entry.getKey();
                if (this.isWriteable(dest, name2)) {
                    try {
                        if (dest instanceof DynaBean) {
                            ((DynaBean)dest).set(name2, entry.getValue());
                        }
                        else {
                            this.setSimpleProperty(dest, name2, entry.getValue());
                        }
                    }
                    catch (final NoSuchMethodException e2) {
                        if (!this.log.isDebugEnabled()) {
                            continue;
                        }
                        this.log.debug((Object)("Error writing to '" + name2 + "' on class '" + dest.getClass() + "'"), (Throwable)e2);
                    }
                }
            }
        }
        else {
            final PropertyDescriptor[] propertyDescriptors;
            final PropertyDescriptor[] origDescriptors2 = propertyDescriptors = this.getPropertyDescriptors(orig);
            for (final PropertyDescriptor origDescriptor2 : propertyDescriptors) {
                final String name = origDescriptor2.getName();
                if (this.isReadable(orig, name) && this.isWriteable(dest, name)) {
                    try {
                        final Object value = this.getSimpleProperty(orig, name);
                        if (dest instanceof DynaBean) {
                            ((DynaBean)dest).set(name, value);
                        }
                        else {
                            this.setSimpleProperty(dest, name, value);
                        }
                    }
                    catch (final NoSuchMethodException e) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)("Error writing to '" + name + "' on class '" + dest.getClass() + "'"), (Throwable)e);
                        }
                    }
                }
            }
        }
    }
    
    public Map<String, Object> describe(final Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        final Map<String, Object> description = new HashMap<String, Object>();
        if (bean instanceof DynaBean) {
            final DynaProperty[] dynaProperties;
            final DynaProperty[] descriptors = dynaProperties = ((DynaBean)bean).getDynaClass().getDynaProperties();
            for (final DynaProperty descriptor : dynaProperties) {
                final String name = descriptor.getName();
                description.put(name, this.getProperty(bean, name));
            }
        }
        else {
            final PropertyDescriptor[] propertyDescriptors;
            final PropertyDescriptor[] descriptors2 = propertyDescriptors = this.getPropertyDescriptors(bean);
            for (final PropertyDescriptor descriptor2 : propertyDescriptors) {
                final String name = descriptor2.getName();
                if (descriptor2.getReadMethod() != null) {
                    description.put(name, this.getProperty(bean, name));
                }
            }
        }
        return description;
    }
    
    public Object getIndexedProperty(final Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        int index = -1;
        try {
            index = this.resolver.getIndex(name);
        }
        catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid indexed property '" + name + "' on bean class '" + bean.getClass() + "' " + e.getMessage());
        }
        if (index < 0) {
            throw new IllegalArgumentException("Invalid indexed property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        name = this.resolver.getProperty(name);
        return this.getIndexedProperty(bean, name, index);
    }
    
    public Object getIndexedProperty(final Object bean, final String name, final int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null || name.length() == 0) {
            if (bean.getClass().isArray()) {
                return Array.get(bean, index);
            }
            if (bean instanceof List) {
                return ((List)bean).get(index);
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            return ((DynaBean)bean).get(name, index);
        }
        else {
            final PropertyDescriptor descriptor2 = this.getPropertyDescriptor(bean, name);
            if (descriptor2 == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            if (descriptor2 instanceof IndexedPropertyDescriptor) {
                Method readMethod = ((IndexedPropertyDescriptor)descriptor2).getIndexedReadMethod();
                readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
                if (readMethod != null) {
                    final Object[] subscript = { new Integer(index) };
                    try {
                        return this.invokeMethod(readMethod, bean, subscript);
                    }
                    catch (final InvocationTargetException e) {
                        if (e.getTargetException() instanceof IndexOutOfBoundsException) {
                            throw (IndexOutOfBoundsException)e.getTargetException();
                        }
                        throw e;
                    }
                }
            }
            Method readMethod = this.getReadMethod(bean.getClass(), descriptor2);
            if (readMethod == null) {
                throw new NoSuchMethodException("Property '" + name + "' has no getter method on bean class '" + bean.getClass() + "'");
            }
            final Object value = this.invokeMethod(readMethod, bean, PropertyUtilsBean.EMPTY_OBJECT_ARRAY);
            if (!value.getClass().isArray()) {
                if (!(value instanceof List)) {
                    throw new IllegalArgumentException("Property '" + name + "' is not indexed on bean class '" + bean.getClass() + "'");
                }
                return ((List)value).get(index);
            }
            else {
                try {
                    return Array.get(value, index);
                }
                catch (final ArrayIndexOutOfBoundsException e2) {
                    throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + Array.getLength(value) + " for property '" + name + "'");
                }
            }
        }
    }
    
    public Object getMappedProperty(final Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        String key = null;
        try {
            key = this.resolver.getKey(name);
        }
        catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid mapped property '" + name + "' on bean class '" + bean.getClass() + "' " + e.getMessage());
        }
        if (key == null) {
            throw new IllegalArgumentException("Invalid mapped property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        name = this.resolver.getProperty(name);
        return this.getMappedProperty(bean, name, key);
    }
    
    public Object getMappedProperty(final Object bean, final String name, final String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException("No key specified for property '" + name + "' on bean class " + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "'+ on bean class '" + bean.getClass() + "'");
            }
            return ((DynaBean)bean).get(name, key);
        }
        else {
            Object result = null;
            final PropertyDescriptor descriptor2 = this.getPropertyDescriptor(bean, name);
            if (descriptor2 == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "'+ on bean class '" + bean.getClass() + "'");
            }
            if (descriptor2 instanceof MappedPropertyDescriptor) {
                Method readMethod = ((MappedPropertyDescriptor)descriptor2).getMappedReadMethod();
                readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
                if (readMethod == null) {
                    throw new NoSuchMethodException("Property '" + name + "' has no mapped getter method on bean class '" + bean.getClass() + "'");
                }
                final Object[] keyArray = { key };
                result = this.invokeMethod(readMethod, bean, keyArray);
            }
            else {
                final Method readMethod = this.getReadMethod(bean.getClass(), descriptor2);
                if (readMethod == null) {
                    throw new NoSuchMethodException("Property '" + name + "' has no mapped getter method on bean class '" + bean.getClass() + "'");
                }
                final Object invokeResult = this.invokeMethod(readMethod, bean, PropertyUtilsBean.EMPTY_OBJECT_ARRAY);
                if (invokeResult instanceof Map) {
                    result = ((Map)invokeResult).get(key);
                }
            }
            return result;
        }
    }
    
    @Deprecated
    public FastHashMap getMappedPropertyDescriptors(final Class<?> beanClass) {
        if (beanClass == null) {
            return null;
        }
        return this.mappedDescriptorsCache.get(beanClass);
    }
    
    @Deprecated
    public FastHashMap getMappedPropertyDescriptors(final Object bean) {
        if (bean == null) {
            return null;
        }
        return this.getMappedPropertyDescriptors(bean.getClass());
    }
    
    public Object getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            final String next = this.resolver.next(name);
            Object nestedBean = null;
            if (bean instanceof Map) {
                nestedBean = this.getPropertyOfMapBean((Map<?, ?>)bean, next);
            }
            else if (this.resolver.isMapped(next)) {
                nestedBean = this.getMappedProperty(bean, next);
            }
            else if (this.resolver.isIndexed(next)) {
                nestedBean = this.getIndexedProperty(bean, next);
            }
            else {
                nestedBean = this.getSimpleProperty(bean, next);
            }
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        if (bean instanceof Map) {
            bean = this.getPropertyOfMapBean((Map<?, ?>)bean, name);
        }
        else if (this.resolver.isMapped(name)) {
            bean = this.getMappedProperty(bean, name);
        }
        else if (this.resolver.isIndexed(name)) {
            bean = this.getIndexedProperty(bean, name);
        }
        else {
            bean = this.getSimpleProperty(bean, name);
        }
        return bean;
    }
    
    protected Object getPropertyOfMapBean(final Map<?, ?> bean, String propertyName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (this.resolver.isMapped(propertyName)) {
            final String name = this.resolver.getProperty(propertyName);
            if (name == null || name.length() == 0) {
                propertyName = this.resolver.getKey(propertyName);
            }
        }
        if (this.resolver.isIndexed(propertyName) || this.resolver.isMapped(propertyName)) {
            throw new IllegalArgumentException("Indexed or mapped properties are not supported on objects of type Map: " + propertyName);
        }
        return bean.get(propertyName);
    }
    
    public Object getProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name);
    }
    
    public PropertyDescriptor getPropertyDescriptor(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            final String next = this.resolver.next(name);
            final Object nestedBean = this.getProperty(bean, next);
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + next + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        name = this.resolver.getProperty(name);
        if (name == null) {
            return null;
        }
        final BeanIntrospectionData data = this.getIntrospectionData(bean.getClass());
        PropertyDescriptor result = data.getDescriptor(name);
        if (result != null) {
            return result;
        }
        FastHashMap mappedDescriptors = this.getMappedPropertyDescriptors(bean);
        if (mappedDescriptors == null) {
            mappedDescriptors = new FastHashMap();
            mappedDescriptors.setFast(true);
            this.mappedDescriptorsCache.put(bean.getClass(), mappedDescriptors);
        }
        result = (PropertyDescriptor)mappedDescriptors.get((Object)name);
        if (result == null) {
            try {
                result = new MappedPropertyDescriptor(name, bean.getClass());
            }
            catch (final IntrospectionException ex) {}
            if (result != null) {
                mappedDescriptors.put((Object)name, (Object)result);
            }
        }
        return result;
    }
    
    public PropertyDescriptor[] getPropertyDescriptors(final Class<?> beanClass) {
        return this.getIntrospectionData(beanClass).getDescriptors();
    }
    
    public PropertyDescriptor[] getPropertyDescriptors(final Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        return this.getPropertyDescriptors(bean.getClass());
    }
    
    public Class<?> getPropertyEditorClass(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        final PropertyDescriptor descriptor = this.getPropertyDescriptor(bean, name);
        if (descriptor != null) {
            return descriptor.getPropertyEditorClass();
        }
        return null;
    }
    
    public Class<?> getPropertyType(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            final String next = this.resolver.next(name);
            final Object nestedBean = this.getProperty(bean, next);
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + next + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        name = this.resolver.getProperty(name);
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                return null;
            }
            final Class<?> type = descriptor.getType();
            if (type == null) {
                return null;
            }
            if (type.isArray()) {
                return type.getComponentType();
            }
            return type;
        }
        else {
            final PropertyDescriptor descriptor2 = this.getPropertyDescriptor(bean, name);
            if (descriptor2 == null) {
                return null;
            }
            if (descriptor2 instanceof IndexedPropertyDescriptor) {
                return ((IndexedPropertyDescriptor)descriptor2).getIndexedPropertyType();
            }
            if (descriptor2 instanceof MappedPropertyDescriptor) {
                return ((MappedPropertyDescriptor)descriptor2).getMappedPropertyType();
            }
            return descriptor2.getPropertyType();
        }
    }
    
    public Method getReadMethod(final PropertyDescriptor descriptor) {
        return MethodUtils.getAccessibleMethod(descriptor.getReadMethod());
    }
    
    Method getReadMethod(final Class<?> clazz, final PropertyDescriptor descriptor) {
        return MethodUtils.getAccessibleMethod(clazz, descriptor.getReadMethod());
    }
    
    public Object getSimpleProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.hasNested(name)) {
            throw new IllegalArgumentException("Nested property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.isIndexed(name)) {
            throw new IllegalArgumentException("Indexed property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.isMapped(name)) {
            throw new IllegalArgumentException("Mapped property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on dynaclass '" + ((DynaBean)bean).getDynaClass() + "'");
            }
            return ((DynaBean)bean).get(name);
        }
        else {
            final PropertyDescriptor descriptor2 = this.getPropertyDescriptor(bean, name);
            if (descriptor2 == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on class '" + bean.getClass() + "'");
            }
            final Method readMethod = this.getReadMethod(bean.getClass(), descriptor2);
            if (readMethod == null) {
                throw new NoSuchMethodException("Property '" + name + "' has no getter method in class '" + bean.getClass() + "'");
            }
            final Object value = this.invokeMethod(readMethod, bean, PropertyUtilsBean.EMPTY_OBJECT_ARRAY);
            return value;
        }
    }
    
    public Method getWriteMethod(final PropertyDescriptor descriptor) {
        return MethodUtils.getAccessibleMethod(descriptor.getWriteMethod());
    }
    
    public Method getWriteMethod(final Class<?> clazz, final PropertyDescriptor descriptor) {
        final BeanIntrospectionData data = this.getIntrospectionData(clazz);
        return MethodUtils.getAccessibleMethod(clazz, data.getWriteMethod(clazz, descriptor));
    }
    
    public boolean isReadable(Object bean, String name) {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            final String next = this.resolver.next(name);
            Object nestedBean = null;
            try {
                nestedBean = this.getProperty(bean, next);
            }
            catch (final IllegalAccessException e) {
                return false;
            }
            catch (final InvocationTargetException e2) {
                return false;
            }
            catch (final NoSuchMethodException e3) {
                return false;
            }
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + next + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        name = this.resolver.getProperty(name);
        if (bean instanceof WrapDynaBean) {
            bean = ((WrapDynaBean)bean).getInstance();
        }
        if (bean instanceof DynaBean) {
            return ((DynaBean)bean).getDynaClass().getDynaProperty(name) != null;
        }
        try {
            final PropertyDescriptor desc = this.getPropertyDescriptor(bean, name);
            if (desc != null) {
                Method readMethod = this.getReadMethod(bean.getClass(), desc);
                if (readMethod == null) {
                    if (desc instanceof IndexedPropertyDescriptor) {
                        readMethod = ((IndexedPropertyDescriptor)desc).getIndexedReadMethod();
                    }
                    else if (desc instanceof MappedPropertyDescriptor) {
                        readMethod = ((MappedPropertyDescriptor)desc).getMappedReadMethod();
                    }
                    readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
                }
                return readMethod != null;
            }
            return false;
        }
        catch (final IllegalAccessException e4) {
            return false;
        }
        catch (final InvocationTargetException e5) {
            return false;
        }
        catch (final NoSuchMethodException e6) {
            return false;
        }
    }
    
    public boolean isWriteable(Object bean, String name) {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            final String next = this.resolver.next(name);
            Object nestedBean = null;
            try {
                nestedBean = this.getProperty(bean, next);
            }
            catch (final IllegalAccessException e) {
                return false;
            }
            catch (final InvocationTargetException e2) {
                return false;
            }
            catch (final NoSuchMethodException e3) {
                return false;
            }
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + next + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        name = this.resolver.getProperty(name);
        if (bean instanceof WrapDynaBean) {
            bean = ((WrapDynaBean)bean).getInstance();
        }
        if (bean instanceof DynaBean) {
            return ((DynaBean)bean).getDynaClass().getDynaProperty(name) != null;
        }
        try {
            final PropertyDescriptor desc = this.getPropertyDescriptor(bean, name);
            if (desc != null) {
                Method writeMethod = this.getWriteMethod(bean.getClass(), desc);
                if (writeMethod == null) {
                    if (desc instanceof IndexedPropertyDescriptor) {
                        writeMethod = ((IndexedPropertyDescriptor)desc).getIndexedWriteMethod();
                    }
                    else if (desc instanceof MappedPropertyDescriptor) {
                        writeMethod = ((MappedPropertyDescriptor)desc).getMappedWriteMethod();
                    }
                    writeMethod = MethodUtils.getAccessibleMethod(bean.getClass(), writeMethod);
                }
                return writeMethod != null;
            }
            return false;
        }
        catch (final IllegalAccessException e4) {
            return false;
        }
        catch (final InvocationTargetException e5) {
            return false;
        }
        catch (final NoSuchMethodException e6) {
            return false;
        }
    }
    
    public void setIndexedProperty(final Object bean, String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        int index = -1;
        try {
            index = this.resolver.getIndex(name);
        }
        catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid indexed property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Invalid indexed property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        name = this.resolver.getProperty(name);
        this.setIndexedProperty(bean, name, index, value);
    }
    
    public void setIndexedProperty(final Object bean, final String name, final int index, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null || name.length() == 0) {
            if (bean.getClass().isArray()) {
                Array.set(bean, index, value);
                return;
            }
            if (bean instanceof List) {
                final List<Object> list = toObjectList(bean);
                list.set(index, value);
                return;
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            ((DynaBean)bean).set(name, index, value);
        }
        else {
            final PropertyDescriptor descriptor2 = this.getPropertyDescriptor(bean, name);
            if (descriptor2 == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            if (descriptor2 instanceof IndexedPropertyDescriptor) {
                Method writeMethod = ((IndexedPropertyDescriptor)descriptor2).getIndexedWriteMethod();
                writeMethod = MethodUtils.getAccessibleMethod(bean.getClass(), writeMethod);
                if (writeMethod != null) {
                    final Object[] subscript = { new Integer(index), value };
                    try {
                        if (this.log.isTraceEnabled()) {
                            final String valueClassName = (value == null) ? "<null>" : value.getClass().getName();
                            this.log.trace((Object)("setSimpleProperty: Invoking method " + writeMethod + " with index=" + index + ", value=" + value + " (class " + valueClassName + ")"));
                        }
                        this.invokeMethod(writeMethod, bean, subscript);
                    }
                    catch (final InvocationTargetException e) {
                        if (e.getTargetException() instanceof IndexOutOfBoundsException) {
                            throw (IndexOutOfBoundsException)e.getTargetException();
                        }
                        throw e;
                    }
                    return;
                }
            }
            final Method readMethod = this.getReadMethod(bean.getClass(), descriptor2);
            if (readMethod == null) {
                throw new NoSuchMethodException("Property '" + name + "' has no getter method on bean class '" + bean.getClass() + "'");
            }
            final Object array = this.invokeMethod(readMethod, bean, PropertyUtilsBean.EMPTY_OBJECT_ARRAY);
            if (!array.getClass().isArray()) {
                if (!(array instanceof List)) {
                    throw new IllegalArgumentException("Property '" + name + "' is not indexed on bean class '" + bean.getClass() + "'");
                }
                final List<Object> list2 = toObjectList(array);
                list2.set(index, value);
            }
            else {
                Array.set(array, index, value);
            }
        }
    }
    
    public void setMappedProperty(final Object bean, String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        String key = null;
        try {
            key = this.resolver.getKey(name);
        }
        catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid mapped property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException("Invalid mapped property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        name = this.resolver.getProperty(name);
        this.setMappedProperty(bean, name, key, value);
    }
    
    public void setMappedProperty(final Object bean, final String name, final String key, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException("No key specified for property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            ((DynaBean)bean).set(name, key, value);
        }
        else {
            final PropertyDescriptor descriptor2 = this.getPropertyDescriptor(bean, name);
            if (descriptor2 == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            if (descriptor2 instanceof MappedPropertyDescriptor) {
                Method mappedWriteMethod = ((MappedPropertyDescriptor)descriptor2).getMappedWriteMethod();
                mappedWriteMethod = MethodUtils.getAccessibleMethod(bean.getClass(), mappedWriteMethod);
                if (mappedWriteMethod == null) {
                    throw new NoSuchMethodException("Property '" + name + "' has no mapped setter methodon bean class '" + bean.getClass() + "'");
                }
                final Object[] params = { key, value };
                if (this.log.isTraceEnabled()) {
                    final String valueClassName = (value == null) ? "<null>" : value.getClass().getName();
                    this.log.trace((Object)("setSimpleProperty: Invoking method " + mappedWriteMethod + " with key=" + key + ", value=" + value + " (class " + valueClassName + ")"));
                }
                this.invokeMethod(mappedWriteMethod, bean, params);
            }
            else {
                final Method readMethod = this.getReadMethod(bean.getClass(), descriptor2);
                if (readMethod == null) {
                    throw new NoSuchMethodException("Property '" + name + "' has no mapped getter method on bean class '" + bean.getClass() + "'");
                }
                final Object invokeResult = this.invokeMethod(readMethod, bean, PropertyUtilsBean.EMPTY_OBJECT_ARRAY);
                if (invokeResult instanceof Map) {
                    final Map<String, Object> map = toPropertyMap(invokeResult);
                    map.put(key, value);
                }
            }
        }
    }
    
    public void setNestedProperty(Object bean, String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            final String next = this.resolver.next(name);
            Object nestedBean = null;
            if (bean instanceof Map) {
                nestedBean = this.getPropertyOfMapBean((Map<?, ?>)bean, next);
            }
            else if (this.resolver.isMapped(next)) {
                nestedBean = this.getMappedProperty(bean, next);
            }
            else if (this.resolver.isIndexed(next)) {
                nestedBean = this.getIndexedProperty(bean, next);
            }
            else {
                nestedBean = this.getSimpleProperty(bean, next);
            }
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        if (bean instanceof Map) {
            this.setPropertyOfMapBean(toPropertyMap(bean), name, value);
        }
        else if (this.resolver.isMapped(name)) {
            this.setMappedProperty(bean, name, value);
        }
        else if (this.resolver.isIndexed(name)) {
            this.setIndexedProperty(bean, name, value);
        }
        else {
            this.setSimpleProperty(bean, name, value);
        }
    }
    
    protected void setPropertyOfMapBean(final Map<String, Object> bean, String propertyName, final Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (this.resolver.isMapped(propertyName)) {
            final String name = this.resolver.getProperty(propertyName);
            if (name == null || name.length() == 0) {
                propertyName = this.resolver.getKey(propertyName);
            }
        }
        if (this.resolver.isIndexed(propertyName) || this.resolver.isMapped(propertyName)) {
            throw new IllegalArgumentException("Indexed or mapped properties are not supported on objects of type Map: " + propertyName);
        }
        bean.put(propertyName, value);
    }
    
    public void setProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        this.setNestedProperty(bean, name, value);
    }
    
    public void setSimpleProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.hasNested(name)) {
            throw new IllegalArgumentException("Nested property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.isIndexed(name)) {
            throw new IllegalArgumentException("Indexed property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.isMapped(name)) {
            throw new IllegalArgumentException("Mapped property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on dynaclass '" + ((DynaBean)bean).getDynaClass() + "'");
            }
            ((DynaBean)bean).set(name, value);
        }
        else {
            final PropertyDescriptor descriptor2 = this.getPropertyDescriptor(bean, name);
            if (descriptor2 == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on class '" + bean.getClass() + "'");
            }
            final Method writeMethod = this.getWriteMethod(bean.getClass(), descriptor2);
            if (writeMethod == null) {
                throw new NoSuchMethodException("Property '" + name + "' has no setter method in class '" + bean.getClass() + "'");
            }
            final Object[] values = { value };
            if (this.log.isTraceEnabled()) {
                final String valueClassName = (value == null) ? "<null>" : value.getClass().getName();
                this.log.trace((Object)("setSimpleProperty: Invoking method " + writeMethod + " with value " + value + " (class " + valueClassName + ")"));
            }
            this.invokeMethod(writeMethod, bean, values);
        }
    }
    
    private Object invokeMethod(final Method method, final Object bean, final Object[] values) throws IllegalAccessException, InvocationTargetException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified - this should have been checked before reaching this method");
        }
        try {
            return method.invoke(bean, values);
        }
        catch (final NullPointerException cause) {
            String valueString = "";
            if (values != null) {
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        valueString += ", ";
                    }
                    if (values[i] == null) {
                        valueString += "<null>";
                    }
                    else {
                        valueString += values[i].getClass().getName();
                    }
                }
            }
            String expectedString = "";
            final Class<?>[] parTypes = method.getParameterTypes();
            if (parTypes != null) {
                for (int j = 0; j < parTypes.length; ++j) {
                    if (j > 0) {
                        expectedString += ", ";
                    }
                    expectedString += parTypes[j].getName();
                }
            }
            final IllegalArgumentException e = new IllegalArgumentException("Cannot invoke " + method.getDeclaringClass().getName() + "." + method.getName() + " on bean class '" + bean.getClass() + "' - " + cause.getMessage() + " - had objects of type \"" + valueString + "\" but expected signature \"" + expectedString + "\"");
            if (!BeanUtils.initCause(e, cause)) {
                this.log.error((Object)"Method invocation failed", (Throwable)cause);
            }
            throw e;
        }
        catch (final IllegalArgumentException cause2) {
            String valueString = "";
            if (values != null) {
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        valueString += ", ";
                    }
                    if (values[i] == null) {
                        valueString += "<null>";
                    }
                    else {
                        valueString += values[i].getClass().getName();
                    }
                }
            }
            String expectedString = "";
            final Class<?>[] parTypes = method.getParameterTypes();
            if (parTypes != null) {
                for (int j = 0; j < parTypes.length; ++j) {
                    if (j > 0) {
                        expectedString += ", ";
                    }
                    expectedString += parTypes[j].getName();
                }
            }
            final IllegalArgumentException e = new IllegalArgumentException("Cannot invoke " + method.getDeclaringClass().getName() + "." + method.getName() + " on bean class '" + bean.getClass() + "' - " + cause2.getMessage() + " - had objects of type \"" + valueString + "\" but expected signature \"" + expectedString + "\"");
            if (!BeanUtils.initCause(e, cause2)) {
                this.log.error((Object)"Method invocation failed", (Throwable)cause2);
            }
            throw e;
        }
    }
    
    private BeanIntrospectionData getIntrospectionData(final Class<?> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException("No bean class specified");
        }
        BeanIntrospectionData data = this.descriptorsCache.get(beanClass);
        if (data == null) {
            data = this.fetchIntrospectionData(beanClass);
            this.descriptorsCache.put(beanClass, data);
        }
        return data;
    }
    
    private BeanIntrospectionData fetchIntrospectionData(final Class<?> beanClass) {
        final DefaultIntrospectionContext ictx = new DefaultIntrospectionContext(beanClass);
        for (final BeanIntrospector bi : this.introspectors) {
            try {
                bi.introspect(ictx);
            }
            catch (final IntrospectionException iex) {
                this.log.error((Object)"Exception during introspection", (Throwable)iex);
            }
        }
        return new BeanIntrospectionData(ictx.getPropertyDescriptors());
    }
    
    private static List<Object> toObjectList(final Object obj) {
        final List<Object> list = (List<Object>)obj;
        return list;
    }
    
    private static Map<String, Object> toPropertyMap(final Object obj) {
        final Map<String, Object> map = (Map<String, Object>)obj;
        return map;
    }
    
    static {
        EMPTY_OBJECT_ARRAY = new Object[0];
    }
}
