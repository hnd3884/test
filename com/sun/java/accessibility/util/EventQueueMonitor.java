package com.sun.java.accessibility.util;

import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.Accessible;
import java.awt.event.MouseEvent;
import java.awt.Container;
import java.awt.Dialog;
import java.security.AccessController;
import java.awt.Toolkit;
import java.security.PrivilegedAction;
import java.awt.AWTEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseMotionListener;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.util.Vector;
import jdk.Exported;
import java.awt.event.AWTEventListener;

@Exported
public class EventQueueMonitor implements AWTEventListener
{
    static Vector topLevelWindows;
    static Window topLevelWindowWithFocus;
    static Point currentMousePosition;
    static Component currentMouseComponent;
    static GUIInitializedListener guiInitializedListener;
    static TopLevelWindowListener topLevelWindowListener;
    static MouseMotionListener mouseMotionListener;
    static boolean guiInitialized;
    static EventQueueMonitorItem componentEventQueue;
    private static ComponentEvtDispatchThread cedt;
    static Object componentEventQueueLock;
    
    public EventQueueMonitor() {
        if (EventQueueMonitor.cedt == null) {
            (EventQueueMonitor.cedt = new ComponentEvtDispatchThread("EventQueueMonitor-ComponentEvtDispatch")).setDaemon(true);
            EventQueueMonitor.cedt.start();
        }
    }
    
    static void queueComponentEvent(final ComponentEvent componentEvent) {
        synchronized (EventQueueMonitor.componentEventQueueLock) {
            final EventQueueMonitorItem eventQueueMonitorItem = new EventQueueMonitorItem(componentEvent);
            if (EventQueueMonitor.componentEventQueue == null) {
                EventQueueMonitor.componentEventQueue = eventQueueMonitorItem;
            }
            else {
                EventQueueMonitorItem eventQueueMonitorItem2;
                for (eventQueueMonitorItem2 = EventQueueMonitor.componentEventQueue; eventQueueMonitorItem2.next != null; eventQueueMonitorItem2 = eventQueueMonitorItem2.next) {}
                eventQueueMonitorItem2.next = eventQueueMonitorItem;
            }
            EventQueueMonitor.componentEventQueueLock.notifyAll();
        }
    }
    
    public static void maybeInitialize() {
        if (EventQueueMonitor.cedt == null) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    try {
                        Toolkit.getDefaultToolkit().addAWTEventListener(new EventQueueMonitor(), 100L);
                    }
                    catch (final Exception ex) {}
                    return null;
                }
            });
        }
    }
    
    @Override
    public void eventDispatched(final AWTEvent awtEvent) {
        processEvent(awtEvent);
    }
    
    static void maybeNotifyAssistiveTechnologies() {
        if (!EventQueueMonitor.guiInitialized) {
            EventQueueMonitor.guiInitialized = true;
            if (EventQueueMonitor.guiInitializedListener != null) {
                EventQueueMonitor.guiInitializedListener.guiInitialized();
            }
        }
    }
    
    static void addTopLevelWindow(final Component component) {
        if (component == null) {
            return;
        }
        if (!(component instanceof Window)) {
            addTopLevelWindow(component.getParent());
            return;
        }
        Container parent;
        if (component instanceof Dialog || component instanceof Window) {
            parent = (Container)component;
        }
        else {
            parent = component.getParent();
            if (parent != null) {
                addTopLevelWindow(parent);
                return;
            }
        }
        if (parent == null) {
            parent = (Container)component;
        }
        synchronized (EventQueueMonitor.topLevelWindows) {
            if (parent != null && !EventQueueMonitor.topLevelWindows.contains(parent)) {
                EventQueueMonitor.topLevelWindows.addElement(parent);
                if (EventQueueMonitor.topLevelWindowListener != null) {
                    EventQueueMonitor.topLevelWindowListener.topLevelWindowCreated((Window)parent);
                }
            }
        }
    }
    
    static void removeTopLevelWindow(final Window window) {
        synchronized (EventQueueMonitor.topLevelWindows) {
            if (EventQueueMonitor.topLevelWindows.contains(window)) {
                EventQueueMonitor.topLevelWindows.removeElement(window);
                if (EventQueueMonitor.topLevelWindowListener != null) {
                    EventQueueMonitor.topLevelWindowListener.topLevelWindowDestroyed(window);
                }
            }
        }
    }
    
    static void updateCurrentMousePosition(final MouseEvent mouseEvent) {
        final Point currentMousePosition = EventQueueMonitor.currentMousePosition;
        try {
            final Point point = mouseEvent.getPoint();
            EventQueueMonitor.currentMouseComponent = (Component)mouseEvent.getSource();
            (EventQueueMonitor.currentMousePosition = EventQueueMonitor.currentMouseComponent.getLocationOnScreen()).translate(point.x, point.y);
        }
        catch (final Exception ex) {
            EventQueueMonitor.currentMousePosition = currentMousePosition;
        }
    }
    
    static void processEvent(final AWTEvent awtEvent) {
        switch (awtEvent.getID()) {
            case 206:
            case 503:
            case 506:
            case 1004: {
                queueComponentEvent((ComponentEvent)awtEvent);
                break;
            }
            case 205: {
                if (awtEvent instanceof ComponentEvent) {
                    final ComponentEvent componentEvent = (ComponentEvent)awtEvent;
                    if (componentEvent.getComponent() instanceof Window) {
                        addTopLevelWindow(componentEvent.getComponent());
                        maybeNotifyAssistiveTechnologies();
                    }
                    else {
                        maybeNotifyAssistiveTechnologies();
                        addTopLevelWindow(componentEvent.getComponent());
                    }
                }
                queueComponentEvent((ComponentEvent)awtEvent);
                break;
            }
            case 200: {
                if (awtEvent instanceof ComponentEvent) {
                    final ComponentEvent componentEvent2 = (ComponentEvent)awtEvent;
                    if (componentEvent2.getComponent() instanceof Window) {
                        addTopLevelWindow(componentEvent2.getComponent());
                        maybeNotifyAssistiveTechnologies();
                    }
                    else {
                        maybeNotifyAssistiveTechnologies();
                        addTopLevelWindow(componentEvent2.getComponent());
                    }
                    break;
                }
                break;
            }
            case 202: {
                if (awtEvent instanceof ComponentEvent) {
                    removeTopLevelWindow((Window)((ComponentEvent)awtEvent).getComponent());
                    break;
                }
                break;
            }
        }
    }
    
    static synchronized Component getShowingComponentAt(final Container container, final int n, final int n2) {
        if (!container.contains(n, n2)) {
            return null;
        }
        for (int componentCount = container.getComponentCount(), i = 0; i < componentCount; ++i) {
            final Component component = container.getComponent(i);
            if (component != null && component.isShowing()) {
                final Point location = component.getLocation();
                if (component.contains(n - location.x, n2 - location.y)) {
                    return component;
                }
            }
        }
        return container;
    }
    
    static synchronized Component getComponentAt(final Container container, final Point point) {
        if (!container.isShowing()) {
            return null;
        }
        final Point locationOnScreen = container.getLocationOnScreen();
        final Point point2 = new Point(point.x - locationOnScreen.x, point.y - locationOnScreen.y);
        final Component showingComponent = getShowingComponentAt(container, point2.x, point2.y);
        if (showingComponent != container && showingComponent instanceof Container) {
            return getComponentAt((Container)showingComponent, point);
        }
        return showingComponent;
    }
    
    public static Accessible getAccessibleAt(final Point point) {
        final Window topLevelWindowWithFocus = getTopLevelWindowWithFocus();
        final Window[] topLevelWindows = getTopLevelWindows();
        Object o = null;
        if (EventQueueMonitor.currentMousePosition == null) {
            return null;
        }
        if (EventQueueMonitor.currentMousePosition.equals(point) && EventQueueMonitor.currentMouseComponent instanceof Container) {
            o = getComponentAt((Container)EventQueueMonitor.currentMouseComponent, point);
        }
        if (o == null && topLevelWindowWithFocus != null) {
            o = getComponentAt(topLevelWindowWithFocus, point);
        }
        if (o == null) {
            for (int i = 0; i < topLevelWindows.length; ++i) {
                o = getComponentAt(topLevelWindows[i], point);
                if (o != null) {
                    break;
                }
            }
        }
        if (o instanceof Accessible) {
            final AccessibleContext accessibleContext = ((Accessible)o).getAccessibleContext();
            if (accessibleContext != null) {
                final AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                if (accessibleComponent != null && accessibleContext.getAccessibleChildrenCount() != 0) {
                    final Point locationOnScreen = accessibleComponent.getLocationOnScreen();
                    locationOnScreen.move(point.x - locationOnScreen.x, point.y - locationOnScreen.y);
                    return accessibleComponent.getAccessibleAt(locationOnScreen);
                }
            }
            return (Accessible)o;
        }
        return Translator.getAccessible(o);
    }
    
    public static boolean isGUIInitialized() {
        maybeInitialize();
        return EventQueueMonitor.guiInitialized;
    }
    
    public static void addGUIInitializedListener(final GUIInitializedListener guiInitializedListener) {
        maybeInitialize();
        EventQueueMonitor.guiInitializedListener = GUIInitializedMulticaster.add(EventQueueMonitor.guiInitializedListener, guiInitializedListener);
    }
    
    public static void removeGUIInitializedListener(final GUIInitializedListener guiInitializedListener) {
        EventQueueMonitor.guiInitializedListener = GUIInitializedMulticaster.remove(EventQueueMonitor.guiInitializedListener, guiInitializedListener);
    }
    
    public static void addTopLevelWindowListener(final TopLevelWindowListener topLevelWindowListener) {
        EventQueueMonitor.topLevelWindowListener = TopLevelWindowMulticaster.add(EventQueueMonitor.topLevelWindowListener, topLevelWindowListener);
    }
    
    public static void removeTopLevelWindowListener(final TopLevelWindowListener topLevelWindowListener) {
        EventQueueMonitor.topLevelWindowListener = TopLevelWindowMulticaster.remove(EventQueueMonitor.topLevelWindowListener, topLevelWindowListener);
    }
    
    public static Point getCurrentMousePosition() {
        return EventQueueMonitor.currentMousePosition;
    }
    
    public static Window[] getTopLevelWindows() {
        synchronized (EventQueueMonitor.topLevelWindows) {
            final int size = EventQueueMonitor.topLevelWindows.size();
            if (size > 0) {
                final Window[] array = new Window[size];
                for (int i = 0; i < size; ++i) {
                    array[i] = (Window)EventQueueMonitor.topLevelWindows.elementAt(i);
                }
                return array;
            }
            return new Window[0];
        }
    }
    
    public static Window getTopLevelWindowWithFocus() {
        return EventQueueMonitor.topLevelWindowWithFocus;
    }
    
    static {
        EventQueueMonitor.topLevelWindows = new Vector();
        EventQueueMonitor.topLevelWindowWithFocus = null;
        EventQueueMonitor.currentMousePosition = null;
        EventQueueMonitor.currentMouseComponent = null;
        EventQueueMonitor.guiInitializedListener = null;
        EventQueueMonitor.topLevelWindowListener = null;
        EventQueueMonitor.mouseMotionListener = null;
        EventQueueMonitor.guiInitialized = false;
        EventQueueMonitor.componentEventQueue = null;
        EventQueueMonitor.cedt = null;
        EventQueueMonitor.componentEventQueueLock = new Object();
    }
}
