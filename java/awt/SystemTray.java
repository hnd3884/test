package java.awt;

import sun.awt.AWTAccessor;
import sun.security.util.SecurityConstants;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.SunToolkit;
import java.awt.peer.SystemTrayPeer;

public class SystemTray
{
    private static SystemTray systemTray;
    private int currentIconID;
    private transient SystemTrayPeer peer;
    private static final TrayIcon[] EMPTY_TRAY_ARRAY;
    
    private SystemTray() {
        this.currentIconID = 0;
        this.addNotify();
    }
    
    public static SystemTray getSystemTray() {
        checkSystemTrayAllowed();
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        initializeSystemTrayIfNeeded();
        if (!isSupported()) {
            throw new UnsupportedOperationException("The system tray is not supported on the current platform.");
        }
        return SystemTray.systemTray;
    }
    
    public static boolean isSupported() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof SunToolkit) {
            initializeSystemTrayIfNeeded();
            return ((SunToolkit)defaultToolkit).isTraySupported();
        }
        return defaultToolkit instanceof HeadlessToolkit && ((HeadlessToolkit)defaultToolkit).isTraySupported();
    }
    
    public void add(final TrayIcon trayIcon) throws AWTException {
        if (trayIcon == null) {
            throw new NullPointerException("adding null TrayIcon");
        }
        Object trayIcons = null;
        Object trayIcons2 = null;
        Object o = null;
        synchronized (this) {
            trayIcons = SystemTray.systemTray.getTrayIcons();
            o = AppContext.getAppContext().get(TrayIcon.class);
            if (o == null) {
                o = new Vector(3);
                AppContext.getAppContext().put(TrayIcon.class, o);
            }
            else if (((Vector)o).contains(trayIcon)) {
                throw new IllegalArgumentException("adding TrayIcon that is already added");
            }
            ((Vector)o).add(trayIcon);
            trayIcons2 = SystemTray.systemTray.getTrayIcons();
            trayIcon.setID(++this.currentIconID);
        }
        try {
            trayIcon.addNotify();
        }
        catch (final AWTException ex) {
            ((Vector)o).remove(trayIcon);
            throw ex;
        }
        this.firePropertyChange("trayIcons", trayIcons, trayIcons2);
    }
    
    public void remove(final TrayIcon trayIcon) {
        if (trayIcon == null) {
            return;
        }
        Object trayIcons = null;
        Object trayIcons2 = null;
        synchronized (this) {
            trayIcons = SystemTray.systemTray.getTrayIcons();
            final Vector vector = (Vector)AppContext.getAppContext().get(TrayIcon.class);
            if (vector == null || !vector.remove(trayIcon)) {
                return;
            }
            trayIcon.removeNotify();
            trayIcons2 = SystemTray.systemTray.getTrayIcons();
        }
        this.firePropertyChange("trayIcons", trayIcons, trayIcons2);
    }
    
    public TrayIcon[] getTrayIcons() {
        final Vector vector = (Vector)AppContext.getAppContext().get(TrayIcon.class);
        if (vector != null) {
            return vector.toArray(new TrayIcon[vector.size()]);
        }
        return SystemTray.EMPTY_TRAY_ARRAY;
    }
    
    public Dimension getTrayIconSize() {
        return this.peer.getTrayIconSize();
    }
    
    public synchronized void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        if (propertyChangeListener == null) {
            return;
        }
        this.getCurrentChangeSupport().addPropertyChangeListener(s, propertyChangeListener);
    }
    
    public synchronized void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        if (propertyChangeListener == null) {
            return;
        }
        this.getCurrentChangeSupport().removePropertyChangeListener(s, propertyChangeListener);
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(final String s) {
        return this.getCurrentChangeSupport().getPropertyChangeListeners(s);
    }
    
    private void firePropertyChange(final String s, final Object o, final Object o2) {
        if (o != null && o2 != null && o.equals(o2)) {
            return;
        }
        this.getCurrentChangeSupport().firePropertyChange(s, o, o2);
    }
    
    private synchronized PropertyChangeSupport getCurrentChangeSupport() {
        PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(SystemTray.class);
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
            AppContext.getAppContext().put(SystemTray.class, propertyChangeSupport);
        }
        return propertyChangeSupport;
    }
    
    synchronized void addNotify() {
        if (this.peer == null) {
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            if (defaultToolkit instanceof SunToolkit) {
                this.peer = ((SunToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
            }
            else if (defaultToolkit instanceof HeadlessToolkit) {
                this.peer = ((HeadlessToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
            }
        }
    }
    
    static void checkSystemTrayAllowed() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.ACCESS_SYSTEM_TRAY_PERMISSION);
        }
    }
    
    private static void initializeSystemTrayIfNeeded() {
        synchronized (SystemTray.class) {
            if (SystemTray.systemTray == null) {
                SystemTray.systemTray = new SystemTray();
            }
        }
    }
    
    static {
        EMPTY_TRAY_ARRAY = new TrayIcon[0];
        AWTAccessor.setSystemTrayAccessor(new AWTAccessor.SystemTrayAccessor() {
            @Override
            public void firePropertyChange(final SystemTray systemTray, final String s, final Object o, final Object o2) {
                systemTray.firePropertyChange(s, o, o2);
            }
        });
    }
}
