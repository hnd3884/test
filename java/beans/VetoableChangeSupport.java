package java.beans;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Map;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

public class VetoableChangeSupport implements Serializable
{
    private VetoableChangeListenerMap map;
    private Object source;
    private static final ObjectStreamField[] serialPersistentFields;
    static final long serialVersionUID = -5090210921595982017L;
    
    public VetoableChangeSupport(final Object source) {
        this.map = new VetoableChangeListenerMap();
        if (source == null) {
            throw new NullPointerException();
        }
        this.source = source;
    }
    
    public void addVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        if (vetoableChangeListener == null) {
            return;
        }
        if (vetoableChangeListener instanceof VetoableChangeListenerProxy) {
            final VetoableChangeListenerProxy vetoableChangeListenerProxy = (VetoableChangeListenerProxy)vetoableChangeListener;
            this.addVetoableChangeListener(vetoableChangeListenerProxy.getPropertyName(), vetoableChangeListenerProxy.getListener());
        }
        else {
            this.map.add(null, vetoableChangeListener);
        }
    }
    
    public void removeVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        if (vetoableChangeListener == null) {
            return;
        }
        if (vetoableChangeListener instanceof VetoableChangeListenerProxy) {
            final VetoableChangeListenerProxy vetoableChangeListenerProxy = (VetoableChangeListenerProxy)vetoableChangeListener;
            this.removeVetoableChangeListener(vetoableChangeListenerProxy.getPropertyName(), vetoableChangeListenerProxy.getListener());
        }
        else {
            this.map.remove(null, vetoableChangeListener);
        }
    }
    
    public VetoableChangeListener[] getVetoableChangeListeners() {
        return this.map.getListeners();
    }
    
    public void addVetoableChangeListener(final String s, VetoableChangeListener extract) {
        if (extract == null || s == null) {
            return;
        }
        extract = this.map.extract(extract);
        if (extract != null) {
            this.map.add(s, extract);
        }
    }
    
    public void removeVetoableChangeListener(final String s, VetoableChangeListener extract) {
        if (extract == null || s == null) {
            return;
        }
        extract = this.map.extract(extract);
        if (extract != null) {
            this.map.remove(s, extract);
        }
    }
    
    public VetoableChangeListener[] getVetoableChangeListeners(final String s) {
        return this.map.getListeners(s);
    }
    
    public void fireVetoableChange(final String s, final Object o, final Object o2) throws PropertyVetoException {
        if (o == null || o2 == null || !o.equals(o2)) {
            this.fireVetoableChange(new PropertyChangeEvent(this.source, s, o, o2));
        }
    }
    
    public void fireVetoableChange(final String s, final int n, final int n2) throws PropertyVetoException {
        if (n != n2) {
            this.fireVetoableChange(s, n, (Object)n2);
        }
    }
    
    public void fireVetoableChange(final String s, final boolean b, final boolean b2) throws PropertyVetoException {
        if (b != b2) {
            this.fireVetoableChange(s, b, (Object)b2);
        }
    }
    
    public void fireVetoableChange(PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException {
        final Object oldValue = propertyChangeEvent.getOldValue();
        final Object newValue = propertyChangeEvent.getNewValue();
        if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            final VetoableChangeListener[] array = this.map.get(null);
            final VetoableChangeListener[] array2 = (VetoableChangeListener[])((propertyName != null) ? ((VetoableChangeListener[])this.map.get(propertyName)) : null);
            VetoableChangeListener[] array3;
            if (array == null) {
                array3 = array2;
            }
            else if (array2 == null) {
                array3 = array;
            }
            else {
                array3 = new VetoableChangeListener[array.length + array2.length];
                System.arraycopy(array, 0, array3, 0, array.length);
                System.arraycopy(array2, 0, array3, array.length, array2.length);
            }
            if (array3 != null) {
                int i = 0;
                try {
                    while (i < array3.length) {
                        array3[i].vetoableChange(propertyChangeEvent);
                        ++i;
                    }
                }
                catch (final PropertyVetoException ex) {
                    propertyChangeEvent = new PropertyChangeEvent(this.source, propertyName, newValue, oldValue);
                    for (int j = 0; j < i; ++j) {
                        try {
                            array3[j].vetoableChange(propertyChangeEvent);
                        }
                        catch (final PropertyVetoException ex2) {}
                    }
                    throw ex;
                }
            }
        }
    }
    
    public boolean hasListeners(final String s) {
        return this.map.hasListeners(s);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        Hashtable<String, VetoableChangeSupport> hashtable = null;
        VetoableChangeListener[] array = null;
        synchronized (this.map) {
            for (final Map.Entry entry : this.map.getEntries()) {
                final String s = (String)entry.getKey();
                if (s == null) {
                    array = (VetoableChangeListener[])entry.getValue();
                }
                else {
                    if (hashtable == null) {
                        hashtable = new Hashtable<String, VetoableChangeSupport>();
                    }
                    final VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this.source);
                    vetoableChangeSupport.map.set(null, (VetoableChangeListener[])entry.getValue());
                    hashtable.put(s, vetoableChangeSupport);
                }
            }
        }
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("children", hashtable);
        putFields.put("source", this.source);
        putFields.put("vetoableChangeSupportSerializedDataVersion", 2);
        objectOutputStream.writeFields();
        if (array != null) {
            for (final VetoableChangeListener vetoableChangeListener : array) {
                if (vetoableChangeListener instanceof Serializable) {
                    objectOutputStream.writeObject(vetoableChangeListener);
                }
            }
        }
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        this.map = new VetoableChangeListenerMap();
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final Hashtable hashtable = (Hashtable)fields.get("children", null);
        this.source = fields.get("source", null);
        fields.get("vetoableChangeSupportSerializedDataVersion", 2);
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            this.map.add(null, (VetoableChangeListener)object);
        }
        if (hashtable != null) {
            for (final Map.Entry entry : hashtable.entrySet()) {
                final VetoableChangeListener[] vetoableChangeListeners = ((VetoableChangeSupport)entry.getValue()).getVetoableChangeListeners();
                for (int length = vetoableChangeListeners.length, i = 0; i < length; ++i) {
                    this.map.add((String)entry.getKey(), vetoableChangeListeners[i]);
                }
            }
        }
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("children", Hashtable.class), new ObjectStreamField("source", Object.class), new ObjectStreamField("vetoableChangeSupportSerializedDataVersion", Integer.TYPE) };
    }
    
    private static final class VetoableChangeListenerMap extends ChangeListenerMap<VetoableChangeListener>
    {
        private static final VetoableChangeListener[] EMPTY;
        
        @Override
        protected VetoableChangeListener[] newArray(final int n) {
            return (0 < n) ? new VetoableChangeListener[n] : VetoableChangeListenerMap.EMPTY;
        }
        
        @Override
        protected VetoableChangeListener newProxy(final String s, final VetoableChangeListener vetoableChangeListener) {
            return new VetoableChangeListenerProxy(s, vetoableChangeListener);
        }
        
        @Override
        public final VetoableChangeListener extract(VetoableChangeListener vetoableChangeListener) {
            while (vetoableChangeListener instanceof VetoableChangeListenerProxy) {
                vetoableChangeListener = ((VetoableChangeListenerProxy)vetoableChangeListener).getListener();
            }
            return vetoableChangeListener;
        }
        
        static {
            EMPTY = new VetoableChangeListener[0];
        }
    }
}
