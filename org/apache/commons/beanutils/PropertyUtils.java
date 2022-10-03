package org.apache.commons.beanutils;

import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import org.apache.commons.collections.FastHashMap;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;

public class PropertyUtils
{
    @Deprecated
    public static final char INDEXED_DELIM = '[';
    @Deprecated
    public static final char INDEXED_DELIM2 = ']';
    @Deprecated
    public static final char MAPPED_DELIM = '(';
    @Deprecated
    public static final char MAPPED_DELIM2 = ')';
    @Deprecated
    public static final char NESTED_DELIM = '.';
    @Deprecated
    private static int debug;
    
    @Deprecated
    public static int getDebug() {
        return PropertyUtils.debug;
    }
    
    @Deprecated
    public static void setDebug(final int newDebug) {
        PropertyUtils.debug = newDebug;
    }
    
    public static void clearDescriptors() {
        PropertyUtilsBean.getInstance().clearDescriptors();
    }
    
    public static void resetBeanIntrospectors() {
        PropertyUtilsBean.getInstance().resetBeanIntrospectors();
    }
    
    public static void addBeanIntrospector(final BeanIntrospector introspector) {
        PropertyUtilsBean.getInstance().addBeanIntrospector(introspector);
    }
    
    public static boolean removeBeanIntrospector(final BeanIntrospector introspector) {
        return PropertyUtilsBean.getInstance().removeBeanIntrospector(introspector);
    }
    
    public static void copyProperties(final Object dest, final Object orig) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().copyProperties(dest, orig);
    }
    
    public static Map<String, Object> describe(final Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().describe(bean);
    }
    
    public static Object getIndexedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getIndexedProperty(bean, name);
    }
    
    public static Object getIndexedProperty(final Object bean, final String name, final int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getIndexedProperty(bean, name, index);
    }
    
    public static Object getMappedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getMappedProperty(bean, name);
    }
    
    public static Object getMappedProperty(final Object bean, final String name, final String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getMappedProperty(bean, name, key);
    }
    
    @Deprecated
    public static FastHashMap getMappedPropertyDescriptors(final Class<?> beanClass) {
        return PropertyUtilsBean.getInstance().getMappedPropertyDescriptors(beanClass);
    }
    
    @Deprecated
    public static FastHashMap getMappedPropertyDescriptors(final Object bean) {
        return PropertyUtilsBean.getInstance().getMappedPropertyDescriptors(bean);
    }
    
    public static Object getNestedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getNestedProperty(bean, name);
    }
    
    public static Object getProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getProperty(bean, name);
    }
    
    public static PropertyDescriptor getPropertyDescriptor(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getPropertyDescriptor(bean, name);
    }
    
    public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> beanClass) {
        return PropertyUtilsBean.getInstance().getPropertyDescriptors(beanClass);
    }
    
    public static PropertyDescriptor[] getPropertyDescriptors(final Object bean) {
        return PropertyUtilsBean.getInstance().getPropertyDescriptors(bean);
    }
    
    public static Class<?> getPropertyEditorClass(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getPropertyEditorClass(bean, name);
    }
    
    public static Class<?> getPropertyType(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getPropertyType(bean, name);
    }
    
    public static Method getReadMethod(final PropertyDescriptor descriptor) {
        return PropertyUtilsBean.getInstance().getReadMethod(descriptor);
    }
    
    public static Object getSimpleProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getSimpleProperty(bean, name);
    }
    
    public static Method getWriteMethod(final PropertyDescriptor descriptor) {
        return PropertyUtilsBean.getInstance().getWriteMethod(descriptor);
    }
    
    public static boolean isReadable(final Object bean, final String name) {
        return PropertyUtilsBean.getInstance().isReadable(bean, name);
    }
    
    public static boolean isWriteable(final Object bean, final String name) {
        return PropertyUtilsBean.getInstance().isWriteable(bean, name);
    }
    
    public static void setIndexedProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setIndexedProperty(bean, name, value);
    }
    
    public static void setIndexedProperty(final Object bean, final String name, final int index, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setIndexedProperty(bean, name, index, value);
    }
    
    public static void setMappedProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setMappedProperty(bean, name, value);
    }
    
    public static void setMappedProperty(final Object bean, final String name, final String key, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setMappedProperty(bean, name, key, value);
    }
    
    public static void setNestedProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setNestedProperty(bean, name, value);
    }
    
    public static void setProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setProperty(bean, name, value);
    }
    
    public static void setSimpleProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setSimpleProperty(bean, name, value);
    }
    
    static {
        PropertyUtils.debug = 0;
    }
}
