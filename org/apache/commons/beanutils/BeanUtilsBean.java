package org.apache.commons.beanutils;

import java.beans.IndexedPropertyDescriptor;
import java.util.List;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.beanutils.expression.Resolver;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;

public class BeanUtilsBean
{
    private static final ContextClassLoaderLocal<BeanUtilsBean> BEANS_BY_CLASSLOADER;
    private final Log log;
    private final ConvertUtilsBean convertUtilsBean;
    private final PropertyUtilsBean propertyUtilsBean;
    private static final Method INIT_CAUSE_METHOD;
    
    public static BeanUtilsBean getInstance() {
        return BeanUtilsBean.BEANS_BY_CLASSLOADER.get();
    }
    
    public static void setInstance(final BeanUtilsBean newInstance) {
        BeanUtilsBean.BEANS_BY_CLASSLOADER.set(newInstance);
    }
    
    public BeanUtilsBean() {
        this(new ConvertUtilsBean(), new PropertyUtilsBean());
    }
    
    public BeanUtilsBean(final ConvertUtilsBean convertUtilsBean) {
        this(convertUtilsBean, new PropertyUtilsBean());
    }
    
    public BeanUtilsBean(final ConvertUtilsBean convertUtilsBean, final PropertyUtilsBean propertyUtilsBean) {
        this.log = LogFactory.getLog((Class)BeanUtils.class);
        this.convertUtilsBean = convertUtilsBean;
        this.propertyUtilsBean = propertyUtilsBean;
    }
    
    public Object cloneBean(final Object bean) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Cloning bean: " + bean.getClass().getName()));
        }
        Object newBean = null;
        if (bean instanceof DynaBean) {
            newBean = ((DynaBean)bean).getDynaClass().newInstance();
        }
        else {
            newBean = bean.getClass().newInstance();
        }
        this.getPropertyUtils().copyProperties(newBean, bean);
        return newBean;
    }
    
    public void copyProperties(final Object dest, final Object orig) throws IllegalAccessException, InvocationTargetException {
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("BeanUtils.copyProperties(" + dest + ", " + orig + ")"));
        }
        if (orig instanceof DynaBean) {
            final DynaProperty[] dynaProperties;
            final DynaProperty[] origDescriptors = dynaProperties = ((DynaBean)orig).getDynaClass().getDynaProperties();
            for (final DynaProperty origDescriptor : dynaProperties) {
                final String name = origDescriptor.getName();
                if (this.getPropertyUtils().isReadable(orig, name) && this.getPropertyUtils().isWriteable(dest, name)) {
                    final Object value = ((DynaBean)orig).get(name);
                    this.copyProperty(dest, name, value);
                }
            }
        }
        else if (orig instanceof Map) {
            final Map<String, Object> propMap = (Map<String, Object>)orig;
            for (final Map.Entry<String, Object> entry : propMap.entrySet()) {
                final String name2 = entry.getKey();
                if (this.getPropertyUtils().isWriteable(dest, name2)) {
                    this.copyProperty(dest, name2, entry.getValue());
                }
            }
        }
        else {
            final PropertyDescriptor[] propertyDescriptors;
            final PropertyDescriptor[] origDescriptors2 = propertyDescriptors = this.getPropertyUtils().getPropertyDescriptors(orig);
            for (final PropertyDescriptor origDescriptor2 : propertyDescriptors) {
                final String name = origDescriptor2.getName();
                if (!"class".equals(name)) {
                    if (this.getPropertyUtils().isReadable(orig, name) && this.getPropertyUtils().isWriteable(dest, name)) {
                        try {
                            final Object value = this.getPropertyUtils().getSimpleProperty(orig, name);
                            this.copyProperty(dest, name, value);
                        }
                        catch (final NoSuchMethodException ex) {}
                    }
                }
            }
        }
    }
    
    public void copyProperty(final Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
        if (this.log.isTraceEnabled()) {
            final StringBuilder sb = new StringBuilder("  copyProperty(");
            sb.append(bean);
            sb.append(", ");
            sb.append(name);
            sb.append(", ");
            if (value == null) {
                sb.append("<NULL>");
            }
            else if (value instanceof String) {
                sb.append((String)value);
            }
            else if (value instanceof String[]) {
                final String[] values = (String[])value;
                sb.append('[');
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(values[i]);
                }
                sb.append(']');
            }
            else {
                sb.append(value.toString());
            }
            sb.append(')');
            this.log.trace((Object)sb.toString());
        }
        Object target = bean;
        final Resolver resolver = this.getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = this.getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
                continue;
            }
            catch (final NoSuchMethodException e) {
                return;
            }
            break;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("    Target bean = " + target));
            this.log.trace((Object)("    Target name = " + name));
        }
        final String propName = resolver.getProperty(name);
        Class<?> type = null;
        final int index = resolver.getIndex(name);
        final String key = resolver.getKey(name);
        if (target instanceof DynaBean) {
            final DynaClass dynaClass = ((DynaBean)target).getDynaClass();
            final DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return;
            }
            type = dynaPropertyType(dynaProperty, value);
        }
        else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor = this.getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return;
                }
            }
            catch (final NoSuchMethodException e2) {
                return;
            }
            type = descriptor.getPropertyType();
            if (type == null) {
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("    target type for property '" + propName + "' is null, so skipping ths setter"));
                }
                return;
            }
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("    target propName=" + propName + ", type=" + type + ", index=" + index + ", key=" + key));
        }
        if (index >= 0) {
            value = this.convertForCopy(value, type.getComponentType());
            try {
                this.getPropertyUtils().setIndexedProperty(target, propName, index, value);
                return;
            }
            catch (final NoSuchMethodException e3) {
                throw new InvocationTargetException(e3, "Cannot set " + propName);
            }
        }
        if (key != null) {
            try {
                this.getPropertyUtils().setMappedProperty(target, propName, key, value);
                return;
            }
            catch (final NoSuchMethodException e3) {
                throw new InvocationTargetException(e3, "Cannot set " + propName);
            }
        }
        value = this.convertForCopy(value, type);
        try {
            this.getPropertyUtils().setSimpleProperty(target, propName, value);
        }
        catch (final NoSuchMethodException e3) {
            throw new InvocationTargetException(e3, "Cannot set " + propName);
        }
    }
    
    public Map<String, String> describe(final Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            return new HashMap<String, String>();
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Describing bean: " + bean.getClass().getName()));
        }
        final Map<String, String> description = new HashMap<String, String>();
        if (bean instanceof DynaBean) {
            final DynaProperty[] dynaProperties;
            final DynaProperty[] descriptors = dynaProperties = ((DynaBean)bean).getDynaClass().getDynaProperties();
            for (final DynaProperty descriptor : dynaProperties) {
                final String name = descriptor.getName();
                description.put(name, this.getProperty(bean, name));
            }
        }
        else {
            final PropertyDescriptor[] descriptors2 = this.getPropertyUtils().getPropertyDescriptors(bean);
            final Class<?> clazz = bean.getClass();
            for (final PropertyDescriptor descriptor2 : descriptors2) {
                final String name2 = descriptor2.getName();
                if (this.getPropertyUtils().getReadMethod(clazz, descriptor2) != null) {
                    description.put(name2, this.getProperty(bean, name2));
                }
            }
        }
        return description;
    }
    
    public String[] getArrayProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getProperty(bean, name);
        if (value == null) {
            return null;
        }
        if (value instanceof Collection) {
            final ArrayList<String> values = new ArrayList<String>();
            for (final Object item : (Collection)value) {
                if (item == null) {
                    values.add(null);
                }
                else {
                    values.add(this.getConvertUtils().convert(item));
                }
            }
            return values.toArray(new String[values.size()]);
        }
        if (value.getClass().isArray()) {
            final int n = Array.getLength(value);
            final String[] results = new String[n];
            for (int i = 0; i < n; ++i) {
                final Object item2 = Array.get(value, i);
                if (item2 == null) {
                    results[i] = null;
                }
                else {
                    results[i] = this.getConvertUtils().convert(item2);
                }
            }
            return results;
        }
        final String[] results2 = { this.getConvertUtils().convert(value) };
        return results2;
    }
    
    public String getIndexedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getIndexedProperty(bean, name);
        return this.getConvertUtils().convert(value);
    }
    
    public String getIndexedProperty(final Object bean, final String name, final int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getIndexedProperty(bean, name, index);
        return this.getConvertUtils().convert(value);
    }
    
    public String getMappedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getMappedProperty(bean, name);
        return this.getConvertUtils().convert(value);
    }
    
    public String getMappedProperty(final Object bean, final String name, final String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getMappedProperty(bean, name, key);
        return this.getConvertUtils().convert(value);
    }
    
    public String getNestedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getNestedProperty(bean, name);
        return this.getConvertUtils().convert(value);
    }
    
    public String getProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name);
    }
    
    public String getSimpleProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getSimpleProperty(bean, name);
        return this.getConvertUtils().convert(value);
    }
    
    public void populate(final Object bean, final Map<String, ?> properties) throws IllegalAccessException, InvocationTargetException {
        if (bean == null || properties == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("BeanUtils.populate(" + bean + ", " + properties + ")"));
        }
        for (final Map.Entry<String, ?> entry : properties.entrySet()) {
            final String name = entry.getKey();
            if (name == null) {
                continue;
            }
            this.setProperty(bean, name, entry.getValue());
        }
    }
    
    public void setProperty(final Object bean, String name, final Object value) throws IllegalAccessException, InvocationTargetException {
        if (this.log.isTraceEnabled()) {
            final StringBuilder sb = new StringBuilder("  setProperty(");
            sb.append(bean);
            sb.append(", ");
            sb.append(name);
            sb.append(", ");
            if (value == null) {
                sb.append("<NULL>");
            }
            else if (value instanceof String) {
                sb.append((String)value);
            }
            else if (value instanceof String[]) {
                final String[] values = (String[])value;
                sb.append('[');
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(values[i]);
                }
                sb.append(']');
            }
            else {
                sb.append(value.toString());
            }
            sb.append(')');
            this.log.trace((Object)sb.toString());
        }
        Object target = bean;
        final Resolver resolver = this.getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = this.getPropertyUtils().getProperty(target, resolver.next(name));
                if (target == null) {
                    return;
                }
                name = resolver.remove(name);
                continue;
            }
            catch (final NoSuchMethodException e) {
                return;
            }
            break;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("    Target bean = " + target));
            this.log.trace((Object)("    Target name = " + name));
        }
        final String propName = resolver.getProperty(name);
        Class<?> type = null;
        final int index = resolver.getIndex(name);
        final String key = resolver.getKey(name);
        if (target instanceof DynaBean) {
            final DynaClass dynaClass = ((DynaBean)target).getDynaClass();
            final DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return;
            }
            type = dynaPropertyType(dynaProperty, value);
            if (index >= 0 && List.class.isAssignableFrom(type)) {
                type = Object.class;
            }
        }
        else if (target instanceof Map) {
            type = Object.class;
        }
        else if (target != null && target.getClass().isArray() && index >= 0) {
            type = Array.get(target, index).getClass();
        }
        else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor = this.getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return;
                }
            }
            catch (final NoSuchMethodException e2) {
                return;
            }
            if (descriptor instanceof MappedPropertyDescriptor) {
                if (((MappedPropertyDescriptor)descriptor).getMappedWriteMethod() == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Skipping read-only property");
                    }
                    return;
                }
                type = ((MappedPropertyDescriptor)descriptor).getMappedPropertyType();
            }
            else if (index >= 0 && descriptor instanceof IndexedPropertyDescriptor) {
                if (((IndexedPropertyDescriptor)descriptor).getIndexedWriteMethod() == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Skipping read-only property");
                    }
                    return;
                }
                type = ((IndexedPropertyDescriptor)descriptor).getIndexedPropertyType();
            }
            else if (index >= 0 && List.class.isAssignableFrom(descriptor.getPropertyType())) {
                type = Object.class;
            }
            else if (key != null) {
                if (descriptor.getReadMethod() == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Skipping read-only property");
                    }
                    return;
                }
                type = ((value == null) ? Object.class : value.getClass());
            }
            else {
                if (descriptor.getWriteMethod() == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Skipping read-only property");
                    }
                    return;
                }
                type = descriptor.getPropertyType();
            }
        }
        Object newValue = null;
        if (type.isArray() && index < 0) {
            if (value == null) {
                final String[] values2 = { null };
                newValue = this.getConvertUtils().convert(values2, type);
            }
            else if (value instanceof String) {
                newValue = this.getConvertUtils().convert(value, type);
            }
            else if (value instanceof String[]) {
                newValue = this.getConvertUtils().convert((String[])value, type);
            }
            else {
                newValue = this.convert(value, type);
            }
        }
        else if (type.isArray()) {
            if (value instanceof String || value == null) {
                newValue = this.getConvertUtils().convert((String)value, type.getComponentType());
            }
            else if (value instanceof String[]) {
                newValue = this.getConvertUtils().convert(((String[])value)[0], type.getComponentType());
            }
            else {
                newValue = this.convert(value, type.getComponentType());
            }
        }
        else if (value instanceof String) {
            newValue = this.getConvertUtils().convert((String)value, type);
        }
        else if (value instanceof String[]) {
            newValue = this.getConvertUtils().convert(((String[])value)[0], type);
        }
        else {
            newValue = this.convert(value, type);
        }
        try {
            this.getPropertyUtils().setProperty(target, name, newValue);
        }
        catch (final NoSuchMethodException e2) {
            throw new InvocationTargetException(e2, "Cannot set " + propName);
        }
    }
    
    public ConvertUtilsBean getConvertUtils() {
        return this.convertUtilsBean;
    }
    
    public PropertyUtilsBean getPropertyUtils() {
        return this.propertyUtilsBean;
    }
    
    public boolean initCause(final Throwable throwable, final Throwable cause) {
        if (BeanUtilsBean.INIT_CAUSE_METHOD != null && cause != null) {
            try {
                BeanUtilsBean.INIT_CAUSE_METHOD.invoke(throwable, cause);
                return true;
            }
            catch (final Throwable e) {
                return false;
            }
        }
        return false;
    }
    
    protected Object convert(final Object value, final Class<?> type) {
        final Converter converter = this.getConvertUtils().lookup(type);
        if (converter != null) {
            this.log.trace((Object)("        USING CONVERTER " + converter));
            return converter.convert(type, value);
        }
        return value;
    }
    
    private Object convertForCopy(final Object value, final Class<?> type) {
        return (value != null) ? this.convert(value, type) : value;
    }
    
    private static Method getInitCauseMethod() {
        try {
            final Class<?>[] paramsClasses = { Throwable.class };
            return Throwable.class.getMethod("initCause", paramsClasses);
        }
        catch (final NoSuchMethodException e) {
            final Log log = LogFactory.getLog((Class)BeanUtils.class);
            if (log.isWarnEnabled()) {
                log.warn((Object)"Throwable does not have initCause() method in JDK 1.3");
            }
            return null;
        }
        catch (final Throwable e2) {
            final Log log = LogFactory.getLog((Class)BeanUtils.class);
            if (log.isWarnEnabled()) {
                log.warn((Object)"Error getting the Throwable initCause() method", e2);
            }
            return null;
        }
    }
    
    private static Class<?> dynaPropertyType(final DynaProperty dynaProperty, final Object value) {
        if (!dynaProperty.isMapped()) {
            return dynaProperty.getType();
        }
        return (value == null) ? String.class : value.getClass();
    }
    
    static {
        BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<BeanUtilsBean>() {
            @Override
            protected BeanUtilsBean initialValue() {
                return new BeanUtilsBean();
            }
        };
        INIT_CAUSE_METHOD = getInitCauseMethod();
    }
}
