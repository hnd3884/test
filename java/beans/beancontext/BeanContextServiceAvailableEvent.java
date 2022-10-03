package java.beans.beancontext;

import java.util.Iterator;

public class BeanContextServiceAvailableEvent extends BeanContextEvent
{
    private static final long serialVersionUID = -5333985775656400778L;
    protected Class serviceClass;
    
    public BeanContextServiceAvailableEvent(final BeanContextServices beanContextServices, final Class serviceClass) {
        super(beanContextServices);
        this.serviceClass = serviceClass;
    }
    
    public BeanContextServices getSourceAsBeanContextServices() {
        return (BeanContextServices)this.getBeanContext();
    }
    
    public Class getServiceClass() {
        return this.serviceClass;
    }
    
    public Iterator getCurrentServiceSelectors() {
        return ((BeanContextServices)this.getSource()).getCurrentServiceSelectors(this.serviceClass);
    }
}
