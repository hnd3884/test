package com.sun.java.accessibility.util;

import java.beans.PropertyChangeEvent;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleContext;
import java.awt.Window;
import javax.accessibility.Accessible;
import java.util.EventListener;
import java.beans.PropertyChangeListener;
import jdk.Exported;

@Exported
public class AccessibilityEventMonitor
{
    protected static final AccessibilityListenerList listenerList;
    protected static final AccessibilityEventListener accessibilityListener;
    
    public static void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (AccessibilityEventMonitor.listenerList.getListenerCount(PropertyChangeListener.class) == 0) {
            AccessibilityEventMonitor.accessibilityListener.installListeners();
        }
        AccessibilityEventMonitor.listenerList.add(PropertyChangeListener.class, propertyChangeListener);
    }
    
    public static void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        AccessibilityEventMonitor.listenerList.remove(PropertyChangeListener.class, propertyChangeListener);
        if (AccessibilityEventMonitor.listenerList.getListenerCount(PropertyChangeListener.class) == 0) {
            AccessibilityEventMonitor.accessibilityListener.removeListeners();
        }
    }
    
    static {
        listenerList = new AccessibilityListenerList();
        accessibilityListener = new AccessibilityEventListener();
    }
    
    static class AccessibilityEventListener implements TopLevelWindowListener, PropertyChangeListener
    {
        public AccessibilityEventListener() {
            EventQueueMonitor.addTopLevelWindowListener(this);
        }
        
        protected void installListeners() {
            final Window[] topLevelWindows = EventQueueMonitor.getTopLevelWindows();
            if (topLevelWindows != null) {
                for (int i = 0; i < topLevelWindows.length; ++i) {
                    if (topLevelWindows[i] instanceof Accessible) {
                        this.installListeners(topLevelWindows[i]);
                    }
                }
            }
        }
        
        protected void installListeners(final Accessible accessible) {
            this.installListeners(accessible.getAccessibleContext());
        }
        
        private void installListeners(final AccessibleContext accessibleContext) {
            if (accessibleContext != null && !accessibleContext.getAccessibleStateSet().contains(AccessibleState.TRANSIENT)) {
                accessibleContext.addPropertyChangeListener(this);
                if (accessibleContext.getAccessibleStateSet().contains(_AccessibleState.MANAGES_DESCENDANTS)) {
                    return;
                }
                final AccessibleRole accessibleRole = accessibleContext.getAccessibleRole();
                if (accessibleRole == AccessibleRole.LIST || accessibleRole == AccessibleRole.TREE) {
                    return;
                }
                if (accessibleRole == AccessibleRole.TABLE) {
                    final Accessible accessibleChild = accessibleContext.getAccessibleChild(0);
                    if (accessibleChild != null) {
                        final AccessibleContext accessibleContext2 = accessibleChild.getAccessibleContext();
                        if (accessibleContext2 != null) {
                            final AccessibleRole accessibleRole2 = accessibleContext2.getAccessibleRole();
                            if (accessibleRole2 != null && accessibleRole2 != AccessibleRole.TABLE) {
                                return;
                            }
                        }
                    }
                }
                for (int accessibleChildrenCount = accessibleContext.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                    final Accessible accessibleChild2 = accessibleContext.getAccessibleChild(i);
                    if (accessibleChild2 != null) {
                        this.installListeners(accessibleChild2);
                    }
                }
            }
        }
        
        protected void removeListeners() {
            final Window[] topLevelWindows = EventQueueMonitor.getTopLevelWindows();
            if (topLevelWindows != null) {
                for (int i = 0; i < topLevelWindows.length; ++i) {
                    if (topLevelWindows[i] instanceof Accessible) {
                        this.removeListeners(topLevelWindows[i]);
                    }
                }
            }
        }
        
        protected void removeListeners(final Accessible accessible) {
            this.removeListeners(accessible.getAccessibleContext());
        }
        
        private void removeListeners(final AccessibleContext accessibleContext) {
            if (accessibleContext != null) {
                final AccessibleStateSet accessibleStateSet = accessibleContext.getAccessibleStateSet();
                if (!accessibleStateSet.contains(AccessibleState.TRANSIENT)) {
                    accessibleContext.removePropertyChangeListener(this);
                    if (accessibleStateSet.contains(_AccessibleState.MANAGES_DESCENDANTS)) {
                        return;
                    }
                    final AccessibleRole accessibleRole = accessibleContext.getAccessibleRole();
                    if (accessibleRole == AccessibleRole.LIST || accessibleRole == AccessibleRole.TABLE || accessibleRole == AccessibleRole.TREE) {
                        return;
                    }
                    for (int accessibleChildrenCount = accessibleContext.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                        final Accessible accessibleChild = accessibleContext.getAccessibleChild(i);
                        if (accessibleChild != null) {
                            this.removeListeners(accessibleChild);
                        }
                    }
                }
            }
        }
        
        @Override
        public void topLevelWindowCreated(final Window window) {
            if (window instanceof Accessible) {
                this.installListeners(window);
            }
        }
        
        @Override
        public void topLevelWindowDestroyed(final Window window) {
            if (window instanceof Accessible) {
                this.removeListeners(window);
            }
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final Object[] listenerList = AccessibilityEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == PropertyChangeListener.class) {
                    ((PropertyChangeListener)listenerList[i + 1]).propertyChange(propertyChangeEvent);
                }
            }
            if (propertyChangeEvent.getPropertyName().compareTo("AccessibleChild") == 0) {
                final Object oldValue = propertyChangeEvent.getOldValue();
                final Object newValue = propertyChangeEvent.getNewValue();
                if (oldValue == null ^ newValue == null) {
                    if (oldValue != null) {
                        if (oldValue instanceof Accessible) {
                            this.removeListeners(((Accessible)oldValue).getAccessibleContext());
                        }
                        else if (oldValue instanceof AccessibleContext) {
                            this.removeListeners((AccessibleContext)oldValue);
                        }
                    }
                    else if (newValue != null) {
                        if (newValue instanceof Accessible) {
                            this.installListeners(((Accessible)newValue).getAccessibleContext());
                        }
                        else if (newValue instanceof AccessibleContext) {
                            this.installListeners((AccessibleContext)newValue);
                        }
                    }
                }
                else {
                    System.out.println("ERROR in usage of PropertyChangeEvents for: " + propertyChangeEvent.toString());
                }
            }
        }
    }
}
