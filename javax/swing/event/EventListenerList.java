package javax.swing.event;

import sun.reflect.misc.ReflectUtil;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.EventListener;
import java.io.Serializable;

public class EventListenerList implements Serializable
{
    private static final Object[] NULL_ARRAY;
    protected transient Object[] listenerList;
    
    public EventListenerList() {
        this.listenerList = EventListenerList.NULL_ARRAY;
    }
    
    public Object[] getListenerList() {
        return this.listenerList;
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        final Object[] listenerList = this.listenerList;
        final EventListener[] array = (EventListener[])Array.newInstance(clazz, this.getListenerCount(listenerList, clazz));
        int n = 0;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == clazz) {
                array[n++] = (EventListener)listenerList[i + 1];
            }
        }
        return (T[])array;
    }
    
    public int getListenerCount() {
        return this.listenerList.length / 2;
    }
    
    public int getListenerCount(final Class<?> clazz) {
        return this.getListenerCount(this.listenerList, clazz);
    }
    
    private int getListenerCount(final Object[] array, final Class clazz) {
        int n = 0;
        for (int i = 0; i < array.length; i += 2) {
            if (clazz == array[i]) {
                ++n;
            }
        }
        return n;
    }
    
    public synchronized <T extends EventListener> void add(final Class<T> clazz, final T t) {
        if (t == null) {
            return;
        }
        if (!clazz.isInstance(t)) {
            throw new IllegalArgumentException("Listener " + t + " is not of type " + clazz);
        }
        if (this.listenerList == EventListenerList.NULL_ARRAY) {
            this.listenerList = new Object[] { clazz, t };
        }
        else {
            final int length = this.listenerList.length;
            final Object[] listenerList = new Object[length + 2];
            System.arraycopy(this.listenerList, 0, listenerList, 0, length);
            listenerList[length] = clazz;
            listenerList[length + 1] = t;
            this.listenerList = listenerList;
        }
    }
    
    public synchronized <T extends EventListener> void remove(final Class<T> clazz, final T t) {
        if (t == null) {
            return;
        }
        if (!clazz.isInstance(t)) {
            throw new IllegalArgumentException("Listener " + t + " is not of type " + clazz);
        }
        int n = -1;
        for (int i = this.listenerList.length - 2; i >= 0; i -= 2) {
            if (this.listenerList[i] == clazz && this.listenerList[i + 1].equals(t)) {
                n = i;
                break;
            }
        }
        if (n != -1) {
            final Object[] array = new Object[this.listenerList.length - 2];
            System.arraycopy(this.listenerList, 0, array, 0, n);
            if (n < array.length) {
                System.arraycopy(this.listenerList, n + 2, array, n, array.length - n);
            }
            this.listenerList = ((array.length == 0) ? EventListenerList.NULL_ARRAY : array);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Object[] listenerList = this.listenerList;
        objectOutputStream.defaultWriteObject();
        for (int i = 0; i < listenerList.length; i += 2) {
            final Class clazz = (Class)listenerList[i];
            final EventListener eventListener = (EventListener)listenerList[i + 1];
            if (eventListener != null && eventListener instanceof Serializable) {
                objectOutputStream.writeObject(clazz.getName());
                objectOutputStream.writeObject(eventListener);
            }
        }
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.listenerList = EventListenerList.NULL_ARRAY;
        objectInputStream.defaultReadObject();
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            final Object o = objectInputStream.readObject();
            final String s = (String)object;
            ReflectUtil.checkPackageAccess(s);
            this.add(Class.forName(s, true, contextClassLoader), o);
        }
    }
    
    @Override
    public String toString() {
        final Object[] listenerList = this.listenerList;
        String s = "EventListenerList: " + listenerList.length / 2 + " listeners: ";
        for (int i = 0; i <= listenerList.length - 2; i += 2) {
            s = s + " type " + ((Class)listenerList[i]).getName() + " listener " + listenerList[i + 1];
        }
        return s;
    }
    
    static {
        NULL_ARRAY = new Object[0];
    }
}
