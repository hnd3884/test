package org.apache.commons.beanutils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Iterator;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.HashMap;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;

public class WrapDynaClass implements DynaClass
{
    private String beanClassName;
    private Reference<Class<?>> beanClassRef;
    private final PropertyUtilsBean propertyUtilsBean;
    @Deprecated
    protected Class<?> beanClass;
    protected PropertyDescriptor[] descriptors;
    protected HashMap<String, PropertyDescriptor> descriptorsMap;
    protected DynaProperty[] properties;
    protected HashMap<String, DynaProperty> propertiesMap;
    private static final ContextClassLoaderLocal<Map<CacheKey, WrapDynaClass>> CLASSLOADER_CACHE;
    @Deprecated
    protected static HashMap<Object, Object> dynaClasses;
    
    private WrapDynaClass(final Class<?> beanClass, final PropertyUtilsBean propUtils) {
        this.beanClassName = null;
        this.beanClassRef = null;
        this.beanClass = null;
        this.descriptors = null;
        this.descriptorsMap = new HashMap<String, PropertyDescriptor>();
        this.properties = null;
        this.propertiesMap = new HashMap<String, DynaProperty>();
        this.beanClassRef = new SoftReference<Class<?>>(beanClass);
        this.beanClassName = beanClass.getName();
        this.propertyUtilsBean = propUtils;
        this.introspect();
    }
    
    private static Map<Object, Object> getDynaClassesMap() {
        final Map cache = WrapDynaClass.CLASSLOADER_CACHE.get();
        return cache;
    }
    
    private static Map<CacheKey, WrapDynaClass> getClassesCache() {
        return WrapDynaClass.CLASSLOADER_CACHE.get();
    }
    
    protected Class<?> getBeanClass() {
        return this.beanClassRef.get();
    }
    
    @Override
    public String getName() {
        return this.beanClassName;
    }
    
    @Override
    public DynaProperty getDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        return this.propertiesMap.get(name);
    }
    
    @Override
    public DynaProperty[] getDynaProperties() {
        return this.properties;
    }
    
    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        return new WrapDynaBean(this.getBeanClass().newInstance());
    }
    
    public PropertyDescriptor getPropertyDescriptor(final String name) {
        return this.descriptorsMap.get(name);
    }
    
    public static void clear() {
        getClassesCache().clear();
    }
    
    public static WrapDynaClass createDynaClass(final Class<?> beanClass) {
        return createDynaClass(beanClass, null);
    }
    
    public static WrapDynaClass createDynaClass(final Class<?> beanClass, final PropertyUtilsBean pu) {
        final PropertyUtilsBean propUtils = (pu != null) ? pu : PropertyUtilsBean.getInstance();
        final CacheKey key = new CacheKey(beanClass, propUtils);
        WrapDynaClass dynaClass = getClassesCache().get(key);
        if (dynaClass == null) {
            dynaClass = new WrapDynaClass(beanClass, propUtils);
            getClassesCache().put(key, dynaClass);
        }
        return dynaClass;
    }
    
    protected PropertyUtilsBean getPropertyUtilsBean() {
        return this.propertyUtilsBean;
    }
    
    protected void introspect() {
        final Class<?> beanClass = this.getBeanClass();
        PropertyDescriptor[] regulars = this.getPropertyUtilsBean().getPropertyDescriptors(beanClass);
        if (regulars == null) {
            regulars = new PropertyDescriptor[0];
        }
        Map<?, ?> mappeds = (Map<?, ?>)PropertyUtils.getMappedPropertyDescriptors(beanClass);
        if (mappeds == null) {
            mappeds = new HashMap<Object, Object>();
        }
        this.properties = new DynaProperty[regulars.length + mappeds.size()];
        for (int i = 0; i < regulars.length; ++i) {
            this.descriptorsMap.put(regulars[i].getName(), regulars[i]);
            this.properties[i] = new DynaProperty(regulars[i].getName(), regulars[i].getPropertyType());
            this.propertiesMap.put(this.properties[i].getName(), this.properties[i]);
        }
        int j = regulars.length;
        for (final String name : mappeds.keySet()) {
            final PropertyDescriptor descriptor = (PropertyDescriptor)mappeds.get(name);
            this.properties[j] = new DynaProperty(descriptor.getName(), Map.class);
            this.propertiesMap.put(this.properties[j].getName(), this.properties[j]);
            ++j;
        }
    }
    
    static {
        CLASSLOADER_CACHE = new ContextClassLoaderLocal<Map<CacheKey, WrapDynaClass>>() {
            @Override
            protected Map<CacheKey, WrapDynaClass> initialValue() {
                return new WeakHashMap<CacheKey, WrapDynaClass>();
            }
        };
        WrapDynaClass.dynaClasses = new HashMap<Object, Object>() {
            @Override
            public void clear() {
                getDynaClassesMap().clear();
            }
            
            @Override
            public boolean containsKey(final Object key) {
                return getDynaClassesMap().containsKey(key);
            }
            
            @Override
            public boolean containsValue(final Object value) {
                return getDynaClassesMap().containsValue(value);
            }
            
            @Override
            public Set<Map.Entry<Object, Object>> entrySet() {
                return getDynaClassesMap().entrySet();
            }
            
            @Override
            public boolean equals(final Object o) {
                return getDynaClassesMap().equals(o);
            }
            
            @Override
            public Object get(final Object key) {
                return getDynaClassesMap().get(key);
            }
            
            @Override
            public int hashCode() {
                return getDynaClassesMap().hashCode();
            }
            
            @Override
            public boolean isEmpty() {
                return getDynaClassesMap().isEmpty();
            }
            
            @Override
            public Set<Object> keySet() {
                final Set<Object> result = new HashSet<Object>();
                for (final CacheKey k : getClassesCache().keySet()) {
                    result.add(k.beanClass);
                }
                return result;
            }
            
            @Override
            public Object put(final Object key, final Object value) {
                return getClassesCache().put(new CacheKey((Class<?>)key, PropertyUtilsBean.getInstance()), value);
            }
            
            @Override
            public void putAll(final Map<?, ?> m) {
                for (final Map.Entry<?, ?> e : m.entrySet()) {
                    this.put(e.getKey(), e.getValue());
                }
            }
            
            @Override
            public Object remove(final Object key) {
                return getDynaClassesMap().remove(key);
            }
            
            @Override
            public int size() {
                return getDynaClassesMap().size();
            }
            
            @Override
            public Collection<Object> values() {
                return getDynaClassesMap().values();
            }
        };
    }
    
    private static class CacheKey
    {
        private final Class<?> beanClass;
        private final PropertyUtilsBean propUtils;
        
        public CacheKey(final Class<?> beanCls, final PropertyUtilsBean pu) {
            this.beanClass = beanCls;
            this.propUtils = pu;
        }
        
        @Override
        public int hashCode() {
            final int factor = 31;
            int result = 17;
            result += 31 * this.beanClass.hashCode();
            result += 31 * this.propUtils.hashCode();
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            final CacheKey c = (CacheKey)obj;
            return this.beanClass.equals(c.beanClass) && this.propUtils.equals(c.propUtils);
        }
    }
}
