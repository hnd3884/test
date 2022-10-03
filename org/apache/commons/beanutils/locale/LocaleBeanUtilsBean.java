package org.apache.commons.beanutils.locale;

import org.apache.commons.beanutils.ConvertUtils;
import java.beans.PropertyDescriptor;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.DynaClass;
import java.beans.IndexedPropertyDescriptor;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.expression.Resolver;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.BeanUtilsBean;

public class LocaleBeanUtilsBean extends BeanUtilsBean
{
    private static final ContextClassLoaderLocal<LocaleBeanUtilsBean> LOCALE_BEANS_BY_CLASSLOADER;
    private final Log log;
    private final LocaleConvertUtilsBean localeConvertUtils;
    
    public static LocaleBeanUtilsBean getLocaleBeanUtilsInstance() {
        return LocaleBeanUtilsBean.LOCALE_BEANS_BY_CLASSLOADER.get();
    }
    
    public static void setInstance(final LocaleBeanUtilsBean newInstance) {
        LocaleBeanUtilsBean.LOCALE_BEANS_BY_CLASSLOADER.set(newInstance);
    }
    
    public LocaleBeanUtilsBean() {
        this.log = LogFactory.getLog((Class)LocaleBeanUtilsBean.class);
        this.localeConvertUtils = new LocaleConvertUtilsBean();
    }
    
    public LocaleBeanUtilsBean(final LocaleConvertUtilsBean localeConvertUtils, final ConvertUtilsBean convertUtilsBean, final PropertyUtilsBean propertyUtilsBean) {
        super(convertUtilsBean, propertyUtilsBean);
        this.log = LogFactory.getLog((Class)LocaleBeanUtilsBean.class);
        this.localeConvertUtils = localeConvertUtils;
    }
    
    public LocaleBeanUtilsBean(final LocaleConvertUtilsBean localeConvertUtils) {
        this.log = LogFactory.getLog((Class)LocaleBeanUtilsBean.class);
        this.localeConvertUtils = localeConvertUtils;
    }
    
    public LocaleConvertUtilsBean getLocaleConvertUtils() {
        return this.localeConvertUtils;
    }
    
    public Locale getDefaultLocale() {
        return this.getLocaleConvertUtils().getDefaultLocale();
    }
    
    public void setDefaultLocale(final Locale locale) {
        this.getLocaleConvertUtils().setDefaultLocale(locale);
    }
    
    public boolean getApplyLocalized() {
        return this.getLocaleConvertUtils().getApplyLocalized();
    }
    
    public void setApplyLocalized(final boolean newApplyLocalized) {
        this.getLocaleConvertUtils().setApplyLocalized(newApplyLocalized);
    }
    
    public String getIndexedProperty(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getIndexedProperty(bean, name);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }
    
    @Override
    public String getIndexedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getIndexedProperty(bean, name, null);
    }
    
    public String getIndexedProperty(final Object bean, final String name, final int index, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getIndexedProperty(bean, name, index);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }
    
    @Override
    public String getIndexedProperty(final Object bean, final String name, final int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getIndexedProperty(bean, name, index, null);
    }
    
    public String getSimpleProperty(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getSimpleProperty(bean, name);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }
    
    @Override
    public String getSimpleProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getSimpleProperty(bean, name, null);
    }
    
    public String getMappedProperty(final Object bean, final String name, final String key, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getMappedProperty(bean, name, key);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }
    
    @Override
    public String getMappedProperty(final Object bean, final String name, final String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getMappedProperty(bean, name, key, null);
    }
    
    public String getMappedPropertyLocale(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getMappedProperty(bean, name);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }
    
    @Override
    public String getMappedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getMappedPropertyLocale(bean, name, null);
    }
    
    public String getNestedProperty(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object value = this.getPropertyUtils().getNestedProperty(bean, name);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }
    
    @Override
    public String getNestedProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name, null);
    }
    
    public String getProperty(final Object bean, final String name, final String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name, pattern);
    }
    
    @Override
    public String getProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name);
    }
    
    @Override
    public void setProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException {
        this.setProperty(bean, name, value, null);
    }
    
    public void setProperty(final Object bean, String name, final Object value, final String pattern) throws IllegalAccessException, InvocationTargetException {
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
        final int index = resolver.getIndex(name);
        final String key = resolver.getKey(name);
        final Class<?> type = this.definePropertyType(target, name, propName);
        if (type != null) {
            final Object newValue = this.convert(type, index, value, pattern);
            this.invokeSetter(target, propName, key, index, newValue);
        }
    }
    
    protected Class<?> definePropertyType(final Object target, final String name, final String propName) throws IllegalAccessException, InvocationTargetException {
        Class<?> type = null;
        if (target instanceof DynaBean) {
            final DynaClass dynaClass = ((DynaBean)target).getDynaClass();
            final DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return null;
            }
            type = dynaProperty.getType();
        }
        else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor = this.getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return null;
                }
            }
            catch (final NoSuchMethodException e) {
                return null;
            }
            if (descriptor instanceof MappedPropertyDescriptor) {
                type = ((MappedPropertyDescriptor)descriptor).getMappedPropertyType();
            }
            else if (descriptor instanceof IndexedPropertyDescriptor) {
                type = ((IndexedPropertyDescriptor)descriptor).getIndexedPropertyType();
            }
            else {
                type = descriptor.getPropertyType();
            }
        }
        return type;
    }
    
    protected Object convert(final Class<?> type, final int index, final Object value, final String pattern) {
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Converting value '" + value + "' to type:" + type));
        }
        Object newValue = null;
        if (type.isArray() && index < 0) {
            if (value instanceof String) {
                final String[] values = { (String)value };
                newValue = this.getLocaleConvertUtils().convert(values, type, pattern);
            }
            else if (value instanceof String[]) {
                newValue = this.getLocaleConvertUtils().convert((String[])value, type, pattern);
            }
            else {
                newValue = value;
            }
        }
        else if (type.isArray()) {
            if (value instanceof String) {
                newValue = this.getLocaleConvertUtils().convert((String)value, type.getComponentType(), pattern);
            }
            else if (value instanceof String[]) {
                newValue = this.getLocaleConvertUtils().convert(((String[])value)[0], type.getComponentType(), pattern);
            }
            else {
                newValue = value;
            }
        }
        else if (value instanceof String) {
            newValue = this.getLocaleConvertUtils().convert((String)value, type, pattern);
        }
        else if (value instanceof String[]) {
            newValue = this.getLocaleConvertUtils().convert(((String[])value)[0], type, pattern);
        }
        else {
            newValue = value;
        }
        return newValue;
    }
    
    protected Object convert(final Class<?> type, final int index, final Object value) {
        Object newValue = null;
        if (type.isArray() && index < 0) {
            if (value instanceof String) {
                final String[] values = { (String)value };
                newValue = ConvertUtils.convert(values, type);
            }
            else if (value instanceof String[]) {
                newValue = ConvertUtils.convert((String[])value, type);
            }
            else {
                newValue = value;
            }
        }
        else if (type.isArray()) {
            if (value instanceof String) {
                newValue = ConvertUtils.convert((String)value, type.getComponentType());
            }
            else if (value instanceof String[]) {
                newValue = ConvertUtils.convert(((String[])value)[0], type.getComponentType());
            }
            else {
                newValue = value;
            }
        }
        else if (value instanceof String) {
            newValue = ConvertUtils.convert((String)value, type);
        }
        else if (value instanceof String[]) {
            newValue = ConvertUtils.convert(((String[])value)[0], type);
        }
        else {
            newValue = value;
        }
        return newValue;
    }
    
    protected void invokeSetter(final Object target, final String propName, final String key, final int index, final Object newValue) throws IllegalAccessException, InvocationTargetException {
        try {
            if (index >= 0) {
                this.getPropertyUtils().setIndexedProperty(target, propName, index, newValue);
            }
            else if (key != null) {
                this.getPropertyUtils().setMappedProperty(target, propName, key, newValue);
            }
            else {
                this.getPropertyUtils().setProperty(target, propName, newValue);
            }
        }
        catch (final NoSuchMethodException e) {
            throw new InvocationTargetException(e, "Cannot set " + propName);
        }
    }
    
    @Deprecated
    protected Descriptor calculate(final Object bean, String name) throws IllegalAccessException, InvocationTargetException {
        Object target = bean;
        final Resolver resolver = this.getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = this.getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
                continue;
            }
            catch (final NoSuchMethodException e) {
                return null;
            }
            break;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("    Target bean = " + target));
            this.log.trace((Object)("    Target name = " + name));
        }
        final String propName = resolver.getProperty(name);
        final int index = resolver.getIndex(name);
        final String key = resolver.getKey(name);
        return new Descriptor(target, name, propName, key, index);
    }
    
    static {
        LOCALE_BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<LocaleBeanUtilsBean>() {
            @Override
            protected LocaleBeanUtilsBean initialValue() {
                return new LocaleBeanUtilsBean();
            }
        };
    }
    
    @Deprecated
    protected class Descriptor
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
