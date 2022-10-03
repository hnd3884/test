package org.apache.commons.validator.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import org.apache.commons.validator.Var;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Msg;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import java.util.Collection;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.PropertyUtils;

public class ValidatorUtils
{
    public static String replace(String value, final String key, final String replaceValue) {
        if (value == null || key == null || replaceValue == null) {
            return value;
        }
        final int pos = value.indexOf(key);
        if (pos < 0) {
            return value;
        }
        final int length = value.length();
        final int start = pos;
        final int end = pos + key.length();
        if (length == key.length()) {
            value = replaceValue;
        }
        else if (end == length) {
            value = value.substring(0, start) + replaceValue;
        }
        else {
            value = value.substring(0, start) + replaceValue + replace(value.substring(end), key, replaceValue);
        }
        return value;
    }
    
    public static String getValueAsString(final Object bean, final String property) {
        Object value = null;
        try {
            value = PropertyUtils.getProperty(bean, property);
        }
        catch (final IllegalAccessException e) {
            final Log log = LogFactory.getLog(ValidatorUtils.class);
            log.error((Object)e.getMessage(), (Throwable)e);
        }
        catch (final InvocationTargetException e2) {
            final Log log2 = LogFactory.getLog(ValidatorUtils.class);
            log2.error((Object)e2.getMessage(), (Throwable)e2);
        }
        catch (final NoSuchMethodException e3) {
            final Log log3 = LogFactory.getLog(ValidatorUtils.class);
            log3.error((Object)e3.getMessage(), (Throwable)e3);
        }
        if (value == null) {
            return null;
        }
        if (value instanceof String[]) {
            return (((String[])value).length > 0) ? value.toString() : "";
        }
        if (value instanceof Collection) {
            return ((Collection)value).isEmpty() ? "" : value.toString();
        }
        return value.toString();
    }
    
    public static FastHashMap copyFastHashMap(final FastHashMap map) {
        final FastHashMap results = new FastHashMap();
        final Iterator i = map.keySet().iterator();
        while (i.hasNext()) {
            final String key = i.next();
            final Object value = map.get((Object)key);
            if (value instanceof Msg) {
                results.put((Object)key, ((Msg)value).clone());
            }
            else if (value instanceof Arg) {
                results.put((Object)key, ((Arg)value).clone());
            }
            else if (value instanceof Var) {
                results.put((Object)key, ((Var)value).clone());
            }
            else {
                results.put((Object)key, value);
            }
        }
        results.setFast(true);
        return results;
    }
    
    public static Map copyMap(final Map map) {
        final Map results = new HashMap();
        final Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            final Object value = map.get(key);
            if (value instanceof Msg) {
                results.put(key, ((Msg)value).clone());
            }
            else if (value instanceof Arg) {
                results.put(key, ((Arg)value).clone());
            }
            else if (value instanceof Var) {
                results.put(key, ((Var)value).clone());
            }
            else {
                results.put(key, value);
            }
        }
        return results;
    }
}
