package org.apache.commons.beanutils;

public class ConvertUtilsBean2 extends ConvertUtilsBean
{
    @Override
    public String convert(final Object value) {
        return (String)this.convert(value, String.class);
    }
    
    @Override
    public Object convert(final String value, final Class<?> clazz) {
        return this.convert((Object)value, clazz);
    }
    
    @Override
    public Object convert(final String[] value, final Class<?> clazz) {
        return this.convert((Object)value, clazz);
    }
}
