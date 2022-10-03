package com.sun.java.swing.plaf.windows;

import java.beans.PropertyChangeEvent;
import java.lang.ref.WeakReference;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;
import javax.swing.LookAndFeel;
import java.beans.PropertyChangeListener;
import java.awt.Toolkit;
import java.awt.Component;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.awt.Frame;
import javax.swing.UIManager;
import java.lang.ref.ReferenceQueue;
import javax.swing.UIDefaults;

public class DesktopProperty implements UIDefaults.ActiveValue
{
    private static boolean updatePending;
    private static final ReferenceQueue<DesktopProperty> queue;
    private WeakPCL pcl;
    private final String key;
    private Object value;
    private final Object fallback;
    
    static void flushUnreferencedProperties() {
        WeakPCL weakPCL;
        while ((weakPCL = (WeakPCL)DesktopProperty.queue.poll()) != null) {
            weakPCL.dispose();
        }
    }
    
    private static synchronized void setUpdatePending(final boolean updatePending) {
        DesktopProperty.updatePending = updatePending;
    }
    
    private static synchronized boolean isUpdatePending() {
        return DesktopProperty.updatePending;
    }
    
    private static void updateAllUIs() {
        if (UIManager.getLookAndFeel().getClass().getPackage().equals(DesktopProperty.class.getPackage())) {
            XPStyle.invalidateStyle();
        }
        final Frame[] frames = Frame.getFrames();
        for (int length = frames.length, i = 0; i < length; ++i) {
            updateWindowUI(frames[i]);
        }
    }
    
    private static void updateWindowUI(final Window window) {
        SwingUtilities.updateComponentTreeUI(window);
        final Window[] ownedWindows = window.getOwnedWindows();
        for (int length = ownedWindows.length, i = 0; i < length; ++i) {
            updateWindowUI(ownedWindows[i]);
        }
    }
    
    public DesktopProperty(final String key, final Object fallback) {
        this.key = key;
        this.fallback = fallback;
        flushUnreferencedProperties();
    }
    
    @Override
    public Object createValue(final UIDefaults uiDefaults) {
        if (this.value == null) {
            this.value = this.configureValue(this.getValueFromDesktop());
            if (this.value == null) {
                this.value = this.configureValue(this.getDefaultValue());
            }
        }
        return this.value;
    }
    
    protected Object getValueFromDesktop() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (this.pcl == null) {
            this.pcl = new WeakPCL(this, this.getKey(), UIManager.getLookAndFeel());
            defaultToolkit.addPropertyChangeListener(this.getKey(), this.pcl);
        }
        return defaultToolkit.getDesktopProperty(this.getKey());
    }
    
    protected Object getDefaultValue() {
        return this.fallback;
    }
    
    public void invalidate(final LookAndFeel lookAndFeel) {
        this.invalidate();
    }
    
    public void invalidate() {
        this.value = null;
    }
    
    protected void updateUI() {
        if (!isUpdatePending()) {
            setUpdatePending(true);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateAllUIs();
                    setUpdatePending(false);
                }
            });
        }
    }
    
    protected Object configureValue(Object o) {
        if (o != null) {
            if (o instanceof Color) {
                return new ColorUIResource((Color)o);
            }
            if (o instanceof Font) {
                return new FontUIResource((Font)o);
            }
            if (o instanceof UIDefaults.LazyValue) {
                o = ((UIDefaults.LazyValue)o).createValue(null);
            }
            else if (o instanceof UIDefaults.ActiveValue) {
                o = ((UIDefaults.ActiveValue)o).createValue(null);
            }
        }
        return o;
    }
    
    protected String getKey() {
        return this.key;
    }
    
    static {
        queue = new ReferenceQueue<DesktopProperty>();
    }
    
    private static class WeakPCL extends WeakReference<DesktopProperty> implements PropertyChangeListener
    {
        private String key;
        private LookAndFeel laf;
        
        WeakPCL(final DesktopProperty desktopProperty, final String key, final LookAndFeel laf) {
            super(desktopProperty, DesktopProperty.queue);
            this.key = key;
            this.laf = laf;
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final DesktopProperty desktopProperty = this.get();
            if (desktopProperty == null || this.laf != UIManager.getLookAndFeel()) {
                this.dispose();
            }
            else {
                desktopProperty.invalidate(this.laf);
                desktopProperty.updateUI();
            }
        }
        
        void dispose() {
            Toolkit.getDefaultToolkit().removePropertyChangeListener(this.key, this);
        }
    }
}
