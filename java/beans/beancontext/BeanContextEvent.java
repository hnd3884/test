package java.beans.beancontext;

import java.util.EventObject;

public abstract class BeanContextEvent extends EventObject
{
    private static final long serialVersionUID = 7267998073569045052L;
    protected BeanContext propagatedFrom;
    
    protected BeanContextEvent(final BeanContext beanContext) {
        super(beanContext);
    }
    
    public BeanContext getBeanContext() {
        return (BeanContext)this.getSource();
    }
    
    public synchronized void setPropagatedFrom(final BeanContext propagatedFrom) {
        this.propagatedFrom = propagatedFrom;
    }
    
    public synchronized BeanContext getPropagatedFrom() {
        return this.propagatedFrom;
    }
    
    public synchronized boolean isPropagated() {
        return this.propagatedFrom != null;
    }
}
