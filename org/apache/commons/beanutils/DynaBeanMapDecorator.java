package org.apache.commons.beanutils;

@Deprecated
public class DynaBeanMapDecorator extends BaseDynaBeanMapDecorator<Object>
{
    public DynaBeanMapDecorator(final DynaBean dynaBean, final boolean readOnly) {
        super(dynaBean, readOnly);
    }
    
    public DynaBeanMapDecorator(final DynaBean dynaBean) {
        super(dynaBean);
    }
    
    @Override
    protected Object convertKey(final String propertyName) {
        return propertyName;
    }
}
