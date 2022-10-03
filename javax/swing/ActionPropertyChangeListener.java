package javax.swing;

import java.lang.ref.WeakReference;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.beans.PropertyChangeEvent;
import java.lang.ref.ReferenceQueue;
import java.io.Serializable;
import java.beans.PropertyChangeListener;

abstract class ActionPropertyChangeListener<T extends JComponent> implements PropertyChangeListener, Serializable
{
    private static ReferenceQueue<JComponent> queue;
    private transient OwnedWeakReference<T> target;
    private Action action;
    
    private static ReferenceQueue<JComponent> getQueue() {
        synchronized (ActionPropertyChangeListener.class) {
            if (ActionPropertyChangeListener.queue == null) {
                ActionPropertyChangeListener.queue = new ReferenceQueue<JComponent>();
            }
        }
        return ActionPropertyChangeListener.queue;
    }
    
    public ActionPropertyChangeListener(final T target, final Action action) {
        this.setTarget(target);
        this.action = action;
    }
    
    @Override
    public final void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final JComponent target = this.getTarget();
        if (target == null) {
            this.getAction().removePropertyChangeListener(this);
        }
        else {
            this.actionPropertyChanged((T)target, this.getAction(), propertyChangeEvent);
        }
    }
    
    protected abstract void actionPropertyChanged(final T p0, final Action p1, final PropertyChangeEvent p2);
    
    private void setTarget(final T t) {
        final ReferenceQueue<JComponent> queue = getQueue();
        OwnedWeakReference ownedWeakReference;
        while ((ownedWeakReference = (OwnedWeakReference)queue.poll()) != null) {
            final ActionPropertyChangeListener owner = ownedWeakReference.getOwner();
            final Action action = owner.getAction();
            if (action != null) {
                action.removePropertyChangeListener(owner);
            }
        }
        this.target = new OwnedWeakReference<T>(t, queue, this);
    }
    
    public T getTarget() {
        if (this.target == null) {
            return null;
        }
        return this.target.get();
    }
    
    public Action getAction() {
        return this.action;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getTarget());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final JComponent target = (JComponent)objectInputStream.readObject();
        if (target != null) {
            this.setTarget((T)target);
        }
    }
    
    private static class OwnedWeakReference<U extends JComponent> extends WeakReference<U>
    {
        private ActionPropertyChangeListener<?> owner;
        
        OwnedWeakReference(final U u, final ReferenceQueue<? super U> referenceQueue, final ActionPropertyChangeListener<?> owner) {
            super(u, referenceQueue);
            this.owner = owner;
        }
        
        public ActionPropertyChangeListener<?> getOwner() {
            return this.owner;
        }
    }
}
