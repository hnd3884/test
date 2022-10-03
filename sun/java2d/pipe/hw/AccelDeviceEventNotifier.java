package sun.java2d.pipe.hw;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AccelDeviceEventNotifier
{
    private static AccelDeviceEventNotifier theInstance;
    public static final int DEVICE_RESET = 0;
    public static final int DEVICE_DISPOSED = 1;
    private final Map<AccelDeviceEventListener, Integer> listeners;
    
    private AccelDeviceEventNotifier() {
        this.listeners = Collections.synchronizedMap(new HashMap<AccelDeviceEventListener, Integer>(1));
    }
    
    private static synchronized AccelDeviceEventNotifier getInstance(final boolean b) {
        if (AccelDeviceEventNotifier.theInstance == null && b) {
            AccelDeviceEventNotifier.theInstance = new AccelDeviceEventNotifier();
        }
        return AccelDeviceEventNotifier.theInstance;
    }
    
    public static final void eventOccured(final int n, final int n2) {
        final AccelDeviceEventNotifier instance = getInstance(false);
        if (instance != null) {
            instance.notifyListeners(n2, n);
        }
    }
    
    public static final void addListener(final AccelDeviceEventListener accelDeviceEventListener, final int n) {
        getInstance(true).add(accelDeviceEventListener, n);
    }
    
    public static final void removeListener(final AccelDeviceEventListener accelDeviceEventListener) {
        getInstance(true).remove(accelDeviceEventListener);
    }
    
    private final void add(final AccelDeviceEventListener accelDeviceEventListener, final int n) {
        this.listeners.put(accelDeviceEventListener, n);
    }
    
    private final void remove(final AccelDeviceEventListener accelDeviceEventListener) {
        this.listeners.remove(accelDeviceEventListener);
    }
    
    private final void notifyListeners(final int n, final int n2) {
        final HashMap hashMap;
        synchronized (this.listeners) {
            hashMap = new HashMap((Map<? extends K, ? extends V>)this.listeners);
        }
        for (final AccelDeviceEventListener accelDeviceEventListener : hashMap.keySet()) {
            final Integer n3 = hashMap.get(accelDeviceEventListener);
            if (n3 != null && n3 != n2) {
                continue;
            }
            if (n == 0) {
                accelDeviceEventListener.onDeviceReset();
            }
            else {
                if (n != 1) {
                    continue;
                }
                accelDeviceEventListener.onDeviceDispose();
            }
        }
    }
}
