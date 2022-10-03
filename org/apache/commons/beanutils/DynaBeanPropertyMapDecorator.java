package org.apache.commons.beanutils;

public class DynaBeanPropertyMapDecorator extends BaseDynaBeanMapDecorator<String>
{
    public DynaBeanPropertyMapDecorator(final DynaBean dynaBean, final boolean readOnly) {
        super(dynaBean, readOnly);
    }
    
    public DynaBeanPropertyMapDecorator(final DynaBean dynaBean) {
        super(dynaBean);
    }
    
    @Override
    protected String convertKey(final String propertyName) {
        return propertyName;
    }
}
