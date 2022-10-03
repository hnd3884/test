package sun.awt;

import java.util.Iterator;
import java.awt.IllegalComponentStateException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Map;
import sun.util.logging.PlatformLogger;

public class SunDisplayChanger
{
    private static final PlatformLogger log;
    private Map<DisplayChangedListener, Void> listeners;
    
    public SunDisplayChanger() {
        this.listeners = Collections.synchronizedMap(new WeakHashMap<DisplayChangedListener, Void>(1));
    }
    
    public void add(final DisplayChangedListener displayChangedListener) {
        if (SunDisplayChanger.log.isLoggable(PlatformLogger.Level.FINE) && displayChangedListener == null) {
            SunDisplayChanger.log.fine("Assertion (theListener != null) failed");
        }
        if (SunDisplayChanger.log.isLoggable(PlatformLogger.Level.FINER)) {
            SunDisplayChanger.log.finer("Adding listener: " + displayChangedListener);
        }
        this.listeners.put(displayChangedListener, null);
    }
    
    public void remove(final DisplayChangedListener displayChangedListener) {
        if (SunDisplayChanger.log.isLoggable(PlatformLogger.Level.FINE) && displayChangedListener == null) {
            SunDisplayChanger.log.fine("Assertion (theListener != null) failed");
        }
        if (SunDisplayChanger.log.isLoggable(PlatformLogger.Level.FINER)) {
            SunDisplayChanger.log.finer("Removing listener: " + displayChangedListener);
        }
        this.listeners.remove(displayChangedListener);
    }
    
    public void notifyListeners() {
        if (SunDisplayChanger.log.isLoggable(PlatformLogger.Level.FINEST)) {
            SunDisplayChanger.log.finest("notifyListeners");
        }
        final HashSet set;
        synchronized (this.listeners) {
            set = new HashSet((Collection<? extends E>)this.listeners.keySet());
        }
        for (final DisplayChangedListener displayChangedListener : set) {
            try {
                if (SunDisplayChanger.log.isLoggable(PlatformLogger.Level.FINEST)) {
                    SunDisplayChanger.log.finest("displayChanged for listener: " + displayChangedListener);
                }
                displayChangedListener.displayChanged();
            }
            catch (final IllegalComponentStateException ex) {
                this.listeners.remove(displayChangedListener);
            }
        }
    }
    
    public void notifyPaletteChanged() {
        if (SunDisplayChanger.log.isLoggable(PlatformLogger.Level.FINEST)) {
            SunDisplayChanger.log.finest("notifyPaletteChanged");
        }
        final HashSet set;
        synchronized (this.listeners) {
            set = new HashSet((Collection<? extends E>)this.listeners.keySet());
        }
        for (final DisplayChangedListener displayChangedListener : set) {
            try {
                if (SunDisplayChanger.log.isLoggable(PlatformLogger.Level.FINEST)) {
                    SunDisplayChanger.log.finest("paletteChanged for listener: " + displayChangedListener);
                }
                displayChangedListener.paletteChanged();
            }
            catch (final IllegalComponentStateException ex) {
                this.listeners.remove(displayChangedListener);
            }
        }
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.multiscreen.SunDisplayChanger");
    }
}
