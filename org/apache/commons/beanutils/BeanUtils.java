package org.apache.commons.beanutils;

import java.util.Map;
import java.lang.reflect.InvocationTargetException;

public class BeanUtils
{
    @Deprecated
    private static int debug;
    
    @Deprecated
    public static int getDebug() {
        return BeanUtils.debug;
    }
    
    @Deprecated
    public static void setDebug(final int newDebug) {
        BeanUtils.debug = newDebug;
    }
    
    public static Object cloneBean(final Object bean) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().cloneBean(bean);
    }
    
    public static void copyProperties(final Object dest, final Object orig) throws IllegalAccessException, InvocationTargetException {
        BeanUtilsBean.getInstance().copyProperties(dest, orig);
    }
    
    public static void copyProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException {
        BeanUtilsBean.getInstance().copyProperty(bean, name, value);
    }
    
    public static Map<String, String> describe(final Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().describe(bean);
    }
    
    public static String[] getArrayProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getArrayProperty(bean, name);
    }
    
    public static String getIndexedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getIndexedProperty(bean, name);
    }
    
    public static String getIndexedProperty(final Object bean, final String name, final int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getIndexedProperty(bean, name, index);
    }
    
    public static String getMappedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getMappedProperty(bean, name);
    }
    
    public static String getMappedProperty(final Object bean, final String name, final String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getMappedProperty(bean, name, key);
    }
    
    public static String getNestedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getNestedProperty(bean, name);
    }
    
    public static String getProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getProperty(bean, name);
    }
    
    public static String getSimpleProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getSimpleProperty(bean, name);
    }
    
    public static void populate(final Object bean, final Map<String, ?> properties) throws IllegalAccessException, InvocationTargetException {
        BeanUtilsBean.getInstance().populate(bean, properties);
    }
    
    public static void setProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException {
        BeanUtilsBean.getInstance().setProperty(bean, name, value);
    }
    
    public static boolean initCause(final Throwable throwable, final Throwable cause) {
        return BeanUtilsBean.getInstance().initCause(throwable, cause);
    }
    
    public static <K, V> Map<K, V> createCache() {
        return new WeakFastHashMap<K, V>();
    }
    
    public static boolean getCacheFast(final Map<?, ?> map) {
        return map instanceof WeakFastHashMap && ((WeakFastHashMap)map).getFast();
    }
    
    public static void setCacheFast(final Map<?, ?> map, final boolean fast) {
        if (map instanceof WeakFastHashMap) {
            ((WeakFastHashMap)map).setFast(fast);
        }
    }
    
    static {
        BeanUtils.debug = 0;
    }
}
