package java.beans.beancontext;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeSupport;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class BeanContextChildSupport implements BeanContextChild, BeanContextServicesListener, Serializable
{
    static final long serialVersionUID = 6328947014421475877L;
    public BeanContextChild beanContextChildPeer;
    protected PropertyChangeSupport pcSupport;
    protected VetoableChangeSupport vcSupport;
    protected transient BeanContext beanContext;
    protected transient boolean rejectedSetBCOnce;
    
    public BeanContextChildSupport() {
        this.beanContextChildPeer = this;
        this.pcSupport = new PropertyChangeSupport(this.beanContextChildPeer);
        this.vcSupport = new VetoableChangeSupport(this.beanContextChildPeer);
    }
    
    public BeanContextChildSupport(final BeanContextChild beanContextChild) {
        this.beanContextChildPeer = ((beanContextChild != null) ? beanContextChild : this);
        this.pcSupport = new PropertyChangeSupport(this.beanContextChildPeer);
        this.vcSupport = new VetoableChangeSupport(this.beanContextChildPeer);
    }
    
    @Override
    public synchronized void setBeanContext(final BeanContext beanContext) throws PropertyVetoException {
        if (beanContext == this.beanContext) {
            return;
        }
        final BeanContext beanContext2 = this.beanContext;
        if (!this.rejectedSetBCOnce) {
            if (this.rejectedSetBCOnce = !this.validatePendingSetBeanContext(beanContext)) {
                throw new PropertyVetoException("setBeanContext() change rejected:", new PropertyChangeEvent(this.beanContextChildPeer, "beanContext", beanContext2, beanContext));
            }
            try {
                this.fireVetoableChange("beanContext", beanContext2, beanContext);
            }
            catch (final PropertyVetoException ex) {
                this.rejectedSetBCOnce = true;
                throw ex;
            }
        }
        if (this.beanContext != null) {
            this.releaseBeanContextResources();
        }
        this.beanContext = beanContext;
        this.rejectedSetBCOnce = false;
        this.firePropertyChange("beanContext", beanContext2, beanContext);
        if (this.beanContext != null) {
            this.initializeBeanContextResources();
        }
    }
    
    @Override
    public synchronized BeanContext getBeanContext() {
        return this.beanContext;
    }
    
    @Override
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.pcSupport.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    @Override
    public void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.pcSupport.removePropertyChangeListener(s, propertyChangeListener);
    }
    
    @Override
    public void addVetoableChangeListener(final String s, final VetoableChangeListener vetoableChangeListener) {
        this.vcSupport.addVetoableChangeListener(s, vetoableChangeListener);
    }
    
    @Override
    public void removeVetoableChangeListener(final String s, final VetoableChangeListener vetoableChangeListener) {
        this.vcSupport.removeVetoableChangeListener(s, vetoableChangeListener);
    }
    
    @Override
    public void serviceRevoked(final BeanContextServiceRevokedEvent beanContextServiceRevokedEvent) {
    }
    
    @Override
    public void serviceAvailable(final BeanContextServiceAvailableEvent beanContextServiceAvailableEvent) {
    }
    
    public BeanContextChild getBeanContextChildPeer() {
        return this.beanContextChildPeer;
    }
    
    public boolean isDelegated() {
        return !this.equals(this.beanContextChildPeer);
    }
    
    public void firePropertyChange(final String s, final Object o, final Object o2) {
        this.pcSupport.firePropertyChange(s, o, o2);
    }
    
    public void fireVetoableChange(final String s, final Object o, final Object o2) throws PropertyVetoException {
        this.vcSupport.fireVetoableChange(s, o, o2);
    }
    
    public boolean validatePendingSetBeanContext(final BeanContext beanContext) {
        return true;
    }
    
    protected void releaseBeanContextResources() {
    }
    
    protected void initializeBeanContextResources() {
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (!this.equals(this.beanContextChildPeer) && !(this.beanContextChildPeer instanceof Serializable)) {
            throw new IOException("BeanContextChildSupport beanContextChildPeer not Serializable");
        }
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
    }
}
