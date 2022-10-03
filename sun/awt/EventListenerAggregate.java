package sun.awt;

import java.lang.reflect.Array;
import java.util.EventListener;

public class EventListenerAggregate
{
    private EventListener[] listenerList;
    
    public EventListenerAggregate(final Class<? extends EventListener> clazz) {
        if (clazz == null) {
            throw new NullPointerException("listener class is null");
        }
        this.listenerList = (EventListener[])Array.newInstance(clazz, 0);
    }
    
    private Class<?> getListenerClass() {
        return this.listenerList.getClass().getComponentType();
    }
    
    public synchronized void add(final EventListener eventListener) {
        final Class<?> listenerClass = this.getListenerClass();
        if (!listenerClass.isInstance(eventListener)) {
            throw new ClassCastException("listener " + eventListener + " is not an instance of listener class " + listenerClass);
        }
        final EventListener[] listenerList = (EventListener[])Array.newInstance(listenerClass, this.listenerList.length + 1);
        System.arraycopy(this.listenerList, 0, listenerList, 0, this.listenerList.length);
        listenerList[this.listenerList.length] = eventListener;
        this.listenerList = listenerList;
    }
    
    public synchronized boolean remove(final EventListener eventListener) {
        final Class<?> listenerClass = this.getListenerClass();
        if (!listenerClass.isInstance(eventListener)) {
            throw new ClassCastException("listener " + eventListener + " is not an instance of listener class " + listenerClass);
        }
        for (int i = 0; i < this.listenerList.length; ++i) {
            if (this.listenerList[i].equals(eventListener)) {
                final EventListener[] listenerList = (EventListener[])Array.newInstance(listenerClass, this.listenerList.length - 1);
                System.arraycopy(this.listenerList, 0, listenerList, 0, i);
                System.arraycopy(this.listenerList, i + 1, listenerList, i, this.listenerList.length - i - 1);
                this.listenerList = listenerList;
                return true;
            }
        }
        return false;
    }
    
    public synchronized EventListener[] getListenersInternal() {
        return this.listenerList;
    }
    
    public synchronized EventListener[] getListenersCopy() {
        return (this.listenerList.length == 0) ? this.listenerList : this.listenerList.clone();
    }
    
    public synchronized int size() {
        return this.listenerList.length;
    }
    
    public synchronized boolean isEmpty() {
        return this.listenerList.length == 0;
    }
}
