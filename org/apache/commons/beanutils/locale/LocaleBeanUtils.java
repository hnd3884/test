package org.apache.commons.beanutils.locale;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.apache.commons.beanutils.BeanUtils;

public class LocaleBeanUtils extends BeanUtils
{
    public static Locale getDefaultLocale() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getDefaultLocale();
    }
    
    public static void setDefaultLocale(final Locale locale) {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setDefaultLocale(locale);
    }
    
    public static boolean getApplyLocalized() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getApplyLocalized();
    }
    
    public static void setApplyLocalized(final boolean newApplyLocalized) {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setApplyLocalized(newApplyLocalized);
    }
    
    public static String getIndexedProperty(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, pattern);
    }
    
    public static String getIndexedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name);
    }
    
    public static String getIndexedProperty(final Object bean, final String name, final int index, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index, pattern);
    }
    
    public static String getIndexedProperty(final Object bean, final String name, final int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index);
    }
    
    public static String getSimpleProperty(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name, pattern);
    }
    
    public static String getSimpleProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name);
    }
    
    public static String getMappedProperty(final Object bean, final String name, final String key, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key, pattern);
    }
    
    public static String getMappedProperty(final Object bean, final String name, final String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key);
    }
    
    public static String getMappedPropertyLocale(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedPropertyLocale(bean, name, pattern);
    }
    
    public static String getMappedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name);
    }
    
    public static String getNestedProperty(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name, pattern);
    }
    
    public static String getNestedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name);
    }
    
    public static String getProperty(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getProperty(bean, name, pattern);
    }
    
    public static String getProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getProperty(bean, name);
    }
    
    public static void setProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value);
    }
    
    public static void setProperty(final Object bean, final String name, final Object value, final String pattern) throws IllegalAccessException, InvocationTargetException {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value, pattern);
    }
    
    protected static Class<?> definePropertyType(final Object target, final String name, final String propName) throws IllegalAccessException, InvocationTargetException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().definePropertyType(target, name, propName);
    }
    
    protected static Object convert(final Class<?> type, final int index, final Object value, final String pattern) {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value, pattern);
    }
    
    protected static Object convert(final Class<?> type, final int index, final Object value) {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value);
    }
    
    protected static void invokeSetter(final Object target, final String propName, final String key, final int index, final Object newValue) throws IllegalAccessException, InvocationTargetException {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().invokeSetter(target, propName, key, index, newValue);
    }
    
    @Deprecated
    protected static Descriptor calculate(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException {
        final LocaleBeanUtilsBean.Descriptor descriptor = LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().calculate(bean, name);
        return new Descriptor(descriptor.getTarget(), descriptor.getName(), descriptor.getPropName(), descriptor.getKey(), descriptor.getIndex());
    }
    
    @Deprecated
    protected static class Descriptor
    {
        private int index;
        private String name;
        private String propName;
        private String key;
        private Object target;
        
        public Descriptor(final Object target, final String name, final String propName, final String key, final int index) {
            this.index = -1;
            this.setTarget(target);
            this.setName(name);
            this.setPropName(propName);
            this.setKey(key);
            this.setIndex(index);
        }
        
        public Object getTarget() {
            return this.target;
        }
        
        public void setTarget(final Object target) {
            this.target = target;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public void setKey(final String key) {
            this.key = key;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public void setIndex(final int index) {
            this.index = index;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getPropName() {
            return this.propName;
        }
        
        public void setPropName(final String propName) {
            this.propName = propName;
        }
    }
}
