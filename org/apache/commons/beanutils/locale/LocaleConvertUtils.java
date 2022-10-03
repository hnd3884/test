package org.apache.commons.beanutils.locale;

import org.apache.commons.collections.FastHashMap;
import java.util.Locale;

public class LocaleConvertUtils
{
    public static Locale getDefaultLocale() {
        return LocaleConvertUtilsBean.getInstance().getDefaultLocale();
    }
    
    public static void setDefaultLocale(final Locale locale) {
        LocaleConvertUtilsBean.getInstance().setDefaultLocale(locale);
    }
    
    public static boolean getApplyLocalized() {
        return LocaleConvertUtilsBean.getInstance().getApplyLocalized();
    }
    
    public static void setApplyLocalized(final boolean newApplyLocalized) {
        LocaleConvertUtilsBean.getInstance().setApplyLocalized(newApplyLocalized);
    }
    
    public static String convert(final Object value) {
        return LocaleConvertUtilsBean.getInstance().convert(value);
    }
    
    public static String convert(final Object value, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, pattern);
    }
    
    public static String convert(final Object value, final Locale locale, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, locale, pattern);
    }
    
    public static Object convert(final String value, final Class<?> clazz) {
        return LocaleConvertUtilsBean.getInstance().convert(value, clazz);
    }
    
    public static Object convert(final String value, final Class<?> clazz, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, pattern);
    }
    
    public static Object convert(final String value, final Class<?> clazz, final Locale locale, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, locale, pattern);
    }
    
    public static Object convert(final String[] values, final Class<?> clazz, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, pattern);
    }
    
    public static Object convert(final String[] values, final Class<?> clazz) {
        return LocaleConvertUtilsBean.getInstance().convert(values, clazz);
    }
    
    public static Object convert(final String[] values, final Class<?> clazz, final Locale locale, final String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, locale, pattern);
    }
    
    public static void register(final LocaleConverter converter, final Class<?> clazz, final Locale locale) {
        LocaleConvertUtilsBean.getInstance().register(converter, clazz, locale);
    }
    
    public static void deregister() {
        LocaleConvertUtilsBean.getInstance().deregister();
    }
    
    public static void deregister(final Locale locale) {
        LocaleConvertUtilsBean.getInstance().deregister(locale);
    }
    
    public static void deregister(final Class<?> clazz, final Locale locale) {
        LocaleConvertUtilsBean.getInstance().deregister(clazz, locale);
    }
    
    public static LocaleConverter lookup(final Class<?> clazz, final Locale locale) {
        return LocaleConvertUtilsBean.getInstance().lookup(clazz, locale);
    }
    
    @Deprecated
    protected static FastHashMap lookup(final Locale locale) {
        return LocaleConvertUtilsBean.getInstance().lookup(locale);
    }
    
    @Deprecated
    protected static FastHashMap create(final Locale locale) {
        return LocaleConvertUtilsBean.getInstance().create(locale);
    }
}
