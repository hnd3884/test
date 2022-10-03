package org.apache.commons.beanutils;

public class BeanUtilsBean2 extends BeanUtilsBean
{
    public BeanUtilsBean2() {
        super(new ConvertUtilsBean2());
    }
    
    @Override
    protected Object convert(final Object value, final Class<?> type) {
        return this.getConvertUtils().convert(value, type);
    }
}
