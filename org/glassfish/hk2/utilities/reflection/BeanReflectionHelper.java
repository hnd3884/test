package org.glassfish.hk2.utilities.reflection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.beans.Introspector;
import java.lang.reflect.Method;

public class BeanReflectionHelper
{
    private static final String GET = "get";
    private static final String IS = "is";
    
    public static String getBeanPropertyNameFromGetter(final Method method) {
        return isAGetter(method);
    }
    
    private static String isAGetter(final MethodWrapper method) {
        return isAGetter(method.getMethod());
    }
    
    private static String isAGetter(final Method m) {
        final String name = m.getName();
        if (Void.TYPE.equals(m.getReturnType())) {
            return null;
        }
        final Class<?>[] params = m.getParameterTypes();
        if (params.length != 0) {
            return null;
        }
        if ((m.getModifiers() & 0x1) == 0x0) {
            return null;
        }
        int capIndex;
        if (name.startsWith("get") && name.length() > "get".length()) {
            capIndex = "get".length();
        }
        else {
            if (!name.startsWith("is") || name.length() <= "is".length()) {
                return null;
            }
            capIndex = "is".length();
        }
        if (!Character.isUpperCase(name.charAt(capIndex))) {
            return null;
        }
        final String rawPropName = name.substring(capIndex);
        return Introspector.decapitalize(rawPropName);
    }
    
    private static Method findMethod(final Method m, final Class<?> c) {
        final String name = m.getName();
        final Class<?>[] params = new Class[0];
        try {
            return c.getMethod(name, params);
        }
        catch (final Throwable th) {
            return null;
        }
    }
    
    private static Object getValue(final Object bean, final Method m) {
        try {
            return m.invoke(bean, new Object[0]);
        }
        catch (final Throwable th) {
            return null;
        }
    }
    
    private static PropertyChangeEvent[] getMapChangeEvents(final Map<String, Object> oldBean, final Map<String, Object> newBean) {
        final LinkedList<PropertyChangeEvent> retVal = new LinkedList<PropertyChangeEvent>();
        final Set<String> newKeys = new HashSet<String>(newBean.keySet());
        for (final Map.Entry<String, Object> entry : oldBean.entrySet()) {
            final String key = entry.getKey();
            final Object oldValue = entry.getValue();
            final Object newValue = newBean.get(key);
            newKeys.remove(key);
            if (!GeneralUtilities.safeEquals(oldValue, newValue)) {
                retVal.add(new PropertyChangeEvent(newBean, key, oldValue, newValue));
            }
        }
        for (final String newKey : newKeys) {
            retVal.add(new PropertyChangeEvent(newBean, newKey, null, newBean.get(newKey)));
        }
        return retVal.toArray(new PropertyChangeEvent[retVal.size()]);
    }
    
    public static PropertyChangeEvent[] getChangeEvents(final ClassReflectionHelper helper, final Object oldBean, final Object newBean) {
        if (oldBean instanceof Map) {
            return getMapChangeEvents((Map<String, Object>)oldBean, (Map<String, Object>)newBean);
        }
        final LinkedList<PropertyChangeEvent> retVal = new LinkedList<PropertyChangeEvent>();
        final Set<MethodWrapper> methods = helper.getAllMethods(oldBean.getClass());
        for (final MethodWrapper wrapper : methods) {
            final String propName = isAGetter(wrapper);
            if (propName == null) {
                continue;
            }
            final Method method = wrapper.getMethod();
            final Method newMethod = findMethod(method, newBean.getClass());
            if (newMethod == null) {
                continue;
            }
            final Object oldValue = getValue(oldBean, method);
            final Object newValue = getValue(newBean, newMethod);
            if (GeneralUtilities.safeEquals(oldValue, newValue)) {
                continue;
            }
            retVal.add(new PropertyChangeEvent(newBean, propName, oldValue, newValue));
        }
        return retVal.toArray(new PropertyChangeEvent[retVal.size()]);
    }
    
    public static Map<String, Object> convertJavaBeanToBeanLikeMap(final ClassReflectionHelper helper, final Object bean) {
        final HashMap<String, Object> retVal = new HashMap<String, Object>();
        final Set<MethodWrapper> methods = helper.getAllMethods(bean.getClass());
        for (final MethodWrapper wrapper : methods) {
            final String propName = isAGetter(wrapper);
            if (propName == null) {
                continue;
            }
            if ("class".equals(propName)) {
                continue;
            }
            final Method method = wrapper.getMethod();
            final Object value = getValue(bean, method);
            retVal.put(propName, value);
        }
        return retVal;
    }
}
