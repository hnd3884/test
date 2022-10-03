package java.beans.beancontext;

public class BeanContextServiceRevokedEvent extends BeanContextEvent
{
    private static final long serialVersionUID = -1295543154724961754L;
    protected Class serviceClass;
    private boolean invalidateRefs;
    
    public BeanContextServiceRevokedEvent(final BeanContextServices beanContextServices, final Class serviceClass, final boolean invalidateRefs) {
        super(beanContextServices);
        this.serviceClass = serviceClass;
        this.invalidateRefs = invalidateRefs;
    }
    
    public BeanContextServices getSourceAsBeanContextServices() {
        return (BeanContextServices)this.getBeanContext();
    }
    
    public Class getServiceClass() {
        return this.serviceClass;
    }
    
    public boolean isServiceClass(final Class clazz) {
        return this.serviceClass.equals(clazz);
    }
    
    public boolean isCurrentServiceInvalidNow() {
        return this.invalidateRefs;
    }
}
