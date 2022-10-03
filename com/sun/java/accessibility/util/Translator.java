package com.sun.java.accessibility.util;

import java.awt.event.FocusListener;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.FontMetrics;
import java.awt.MenuComponent;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.awt.IllegalComponentStateException;
import java.util.Locale;
import java.awt.Container;
import javax.accessibility.AccessibleState;
import java.awt.Window;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import java.awt.Component;
import java.awt.MenuItem;
import jdk.Exported;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;

@Exported
public class Translator extends AccessibleContext implements Accessible, AccessibleComponent
{
    protected Object source;
    
    protected static Class getTranslatorClass(final Class clazz) {
        if (clazz == null) {
            return null;
        }
        try {
            return Class.forName("com.sun.java.accessibility.util." + clazz.getName() + "Translator");
        }
        catch (final Exception ex) {
            return getTranslatorClass(clazz.getSuperclass());
        }
    }
    
    public static Accessible getAccessible(final Object source) {
        Object o = null;
        if (source == null) {
            return null;
        }
        if (source instanceof Accessible) {
            o = source;
        }
        else {
            final Class translatorClass = getTranslatorClass(source.getClass());
            if (translatorClass != null) {
                try {
                    final Translator translator = translatorClass.newInstance();
                    translator.setSource(source);
                    o = translator;
                }
                catch (final Exception ex) {}
            }
        }
        if (o == null) {
            o = new Translator(source);
        }
        return (Accessible)o;
    }
    
    public Translator() {
    }
    
    public Translator(final Object source) {
        this.source = source;
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.source.equals(o);
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        return this;
    }
    
    @Override
    public String getAccessibleName() {
        if (this.source instanceof MenuItem) {
            return ((MenuItem)this.source).getLabel();
        }
        if (this.source instanceof Component) {
            return ((Component)this.source).getName();
        }
        return null;
    }
    
    @Override
    public void setAccessibleName(final String s) {
        if (this.source instanceof MenuItem) {
            ((MenuItem)this.source).setLabel(s);
        }
        else if (this.source instanceof Component) {
            ((Component)this.source).setName(s);
        }
    }
    
    @Override
    public String getAccessibleDescription() {
        return null;
    }
    
    @Override
    public void setAccessibleDescription(final String s) {
    }
    
    @Override
    public AccessibleRole getAccessibleRole() {
        return AccessibleRole.UNKNOWN;
    }
    
    @Override
    public AccessibleStateSet getAccessibleStateSet() {
        final AccessibleStateSet set = new AccessibleStateSet();
        if (this.source instanceof Component) {
            final Component component = (Component)this.source;
            for (Container container = component.getParent(); container != null; container = container.getParent()) {
                if (container instanceof Window && ((Window)container).getFocusOwner() == component) {
                    set.add(AccessibleState.FOCUSED);
                }
            }
        }
        if (this.isEnabled()) {
            set.add(AccessibleState.ENABLED);
        }
        if (this.isFocusTraversable()) {
            set.add(AccessibleState.FOCUSABLE);
        }
        if (this.source instanceof MenuItem) {
            set.add(AccessibleState.FOCUSABLE);
        }
        return set;
    }
    
    @Override
    public Accessible getAccessibleParent() {
        if (this.accessibleParent != null) {
            return this.accessibleParent;
        }
        if (this.source instanceof Component) {
            return getAccessible(((Component)this.source).getParent());
        }
        return null;
    }
    
    @Override
    public int getAccessibleIndexInParent() {
        if (this.source instanceof Component) {
            final Container parent = ((Component)this.source).getParent();
            if (parent != null) {
                final Component[] components = parent.getComponents();
                for (int i = 0; i < components.length; ++i) {
                    if (this.source.equals(components[i])) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    @Override
    public int getAccessibleChildrenCount() {
        if (this.source instanceof Container) {
            final Component[] components = ((Container)this.source).getComponents();
            int n = 0;
            for (int i = 0; i < components.length; ++i) {
                if (getAccessible(components[i]) != null) {
                    ++n;
                }
            }
            return n;
        }
        return 0;
    }
    
    @Override
    public Accessible getAccessibleChild(final int n) {
        if (this.source instanceof Container) {
            final Component[] components = ((Container)this.source).getComponents();
            int n2 = 0;
            for (int i = 0; i < components.length; ++i) {
                final Accessible accessible = getAccessible(components[i]);
                if (accessible != null) {
                    if (n2 == n) {
                        final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                        if (accessibleContext != null) {
                            accessibleContext.setAccessibleParent(this);
                        }
                        return accessible;
                    }
                    ++n2;
                }
            }
        }
        return null;
    }
    
    @Override
    public Locale getLocale() throws IllegalComponentStateException {
        if (this.source instanceof Component) {
            return ((Component)this.source).getLocale();
        }
        return null;
    }
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
    }
    
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
    }
    
    @Override
    public Color getBackground() {
        if (this.source instanceof Component) {
            return ((Component)this.source).getBackground();
        }
        return null;
    }
    
    @Override
    public void setBackground(final Color background) {
        if (this.source instanceof Component) {
            ((Component)this.source).setBackground(background);
        }
    }
    
    @Override
    public Color getForeground() {
        if (this.source instanceof Component) {
            return ((Component)this.source).getForeground();
        }
        return null;
    }
    
    @Override
    public void setForeground(final Color foreground) {
        if (this.source instanceof Component) {
            ((Component)this.source).setForeground(foreground);
        }
    }
    
    @Override
    public Cursor getCursor() {
        if (this.source instanceof Component) {
            return ((Component)this.source).getCursor();
        }
        return null;
    }
    
    @Override
    public void setCursor(final Cursor cursor) {
        if (this.source instanceof Component) {
            ((Component)this.source).setCursor(cursor);
        }
    }
    
    @Override
    public Font getFont() {
        if (this.source instanceof Component) {
            return ((Component)this.source).getFont();
        }
        if (this.source instanceof MenuComponent) {
            return ((MenuComponent)this.source).getFont();
        }
        return null;
    }
    
    @Override
    public void setFont(final Font font) {
        if (this.source instanceof Component) {
            ((Component)this.source).setFont(font);
        }
        else if (this.source instanceof MenuComponent) {
            ((MenuComponent)this.source).setFont(font);
        }
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        if (this.source instanceof Component) {
            return ((Component)this.source).getFontMetrics(font);
        }
        return null;
    }
    
    @Override
    public boolean isEnabled() {
        if (this.source instanceof Component) {
            return ((Component)this.source).isEnabled();
        }
        return !(this.source instanceof MenuItem) || ((MenuItem)this.source).isEnabled();
    }
    
    @Override
    public void setEnabled(final boolean b) {
        if (this.source instanceof Component) {
            ((Component)this.source).setEnabled(b);
        }
        else if (this.source instanceof MenuItem) {
            ((MenuItem)this.source).setEnabled(b);
        }
    }
    
    @Override
    public boolean isVisible() {
        return this.source instanceof Component && ((Component)this.source).isVisible();
    }
    
    @Override
    public void setVisible(final boolean visible) {
        if (this.source instanceof Component) {
            ((Component)this.source).setVisible(visible);
        }
    }
    
    @Override
    public boolean isShowing() {
        return this.source instanceof Component && ((Component)this.source).isShowing();
    }
    
    @Override
    public boolean contains(final Point point) {
        return this.source instanceof Component && ((Component)this.source).contains(point);
    }
    
    @Override
    public Point getLocationOnScreen() {
        if (this.source instanceof Component) {
            return ((Component)this.source).getLocationOnScreen();
        }
        return null;
    }
    
    @Override
    public Point getLocation() {
        if (this.source instanceof Component) {
            return ((Component)this.source).getLocation();
        }
        return null;
    }
    
    @Override
    public void setLocation(final Point location) {
        if (this.source instanceof Component) {
            ((Component)this.source).setLocation(location);
        }
    }
    
    @Override
    public Rectangle getBounds() {
        if (this.source instanceof Component) {
            return ((Component)this.source).getBounds();
        }
        return null;
    }
    
    @Override
    public void setBounds(final Rectangle bounds) {
        if (this.source instanceof Component) {
            ((Component)this.source).setBounds(bounds);
        }
    }
    
    @Override
    public Dimension getSize() {
        if (this.source instanceof Component) {
            return ((Component)this.source).getSize();
        }
        return null;
    }
    
    @Override
    public void setSize(final Dimension size) {
        if (this.source instanceof Component) {
            ((Component)this.source).setSize(size);
        }
    }
    
    @Override
    public Accessible getAccessibleAt(final Point point) {
        if (this.source instanceof Component) {
            final Component component = ((Component)this.source).getComponentAt(point);
            if (component != null) {
                return getAccessible(component);
            }
        }
        return null;
    }
    
    @Override
    public boolean isFocusTraversable() {
        return this.source instanceof Component && ((Component)this.source).isFocusTraversable();
    }
    
    @Override
    public void requestFocus() {
        if (this.source instanceof Component) {
            ((Component)this.source).requestFocus();
        }
    }
    
    @Override
    public synchronized void addFocusListener(final FocusListener focusListener) {
        if (this.source instanceof Component) {
            ((Component)this.source).addFocusListener(focusListener);
        }
    }
    
    @Override
    public synchronized void removeFocusListener(final FocusListener focusListener) {
        if (this.source instanceof Component) {
            ((Component)this.source).removeFocusListener(focusListener);
        }
    }
}
