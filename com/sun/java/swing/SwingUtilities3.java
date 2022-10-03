package com.sun.java.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.awt.AWTEvent;
import java.util.Collections;
import java.util.WeakHashMap;
import sun.awt.EventQueueDelegate;
import sun.awt.SunToolkit;
import java.awt.Component;
import java.awt.Window;
import java.applet.Applet;
import sun.awt.AppContext;
import javax.swing.RepaintManager;
import javax.swing.JComponent;
import java.awt.Container;
import java.util.Map;

public class SwingUtilities3
{
    private static final Object DELEGATE_REPAINT_MANAGER_KEY;
    private static final Map<Container, Boolean> vsyncedMap;
    
    public static void setDelegateRepaintManager(final JComponent component, final RepaintManager repaintManager) {
        AppContext.getAppContext().put(SwingUtilities3.DELEGATE_REPAINT_MANAGER_KEY, Boolean.TRUE);
        component.putClientProperty(SwingUtilities3.DELEGATE_REPAINT_MANAGER_KEY, repaintManager);
    }
    
    public static void setVsyncRequested(final Container container, final boolean b) {
        assert container instanceof Applet || container instanceof Window;
        if (b) {
            SwingUtilities3.vsyncedMap.put(container, Boolean.TRUE);
        }
        else {
            SwingUtilities3.vsyncedMap.remove(container);
        }
    }
    
    public static boolean isVsyncRequested(final Container container) {
        assert container instanceof Applet || container instanceof Window;
        return Boolean.TRUE == SwingUtilities3.vsyncedMap.get(container);
    }
    
    public static RepaintManager getDelegateRepaintManager(Component component) {
        RepaintManager repaintManager = null;
        if (Boolean.TRUE == SunToolkit.targetToAppContext(component).get(SwingUtilities3.DELEGATE_REPAINT_MANAGER_KEY)) {
            while (repaintManager == null && component != null) {
                while (component != null && !(component instanceof JComponent)) {
                    component = component.getParent();
                }
                if (component != null) {
                    repaintManager = (RepaintManager)((JComponent)component).getClientProperty(SwingUtilities3.DELEGATE_REPAINT_MANAGER_KEY);
                    component = component.getParent();
                }
            }
        }
        return repaintManager;
    }
    
    public static void setEventQueueDelegate(final Map<String, Map<String, Object>> map) {
        EventQueueDelegate.setDelegate(new EventQueueDelegateFromMap(map));
    }
    
    static {
        DELEGATE_REPAINT_MANAGER_KEY = new StringBuilder("DelegateRepaintManagerKey");
        vsyncedMap = Collections.synchronizedMap(new WeakHashMap<Container, Boolean>());
    }
    
    private static class EventQueueDelegateFromMap implements EventQueueDelegate.Delegate
    {
        private final AWTEvent[] afterDispatchEventArgument;
        private final Object[] afterDispatchHandleArgument;
        private final Callable<Void> afterDispatchCallable;
        private final AWTEvent[] beforeDispatchEventArgument;
        private final Callable<Object> beforeDispatchCallable;
        private final EventQueue[] getNextEventEventQueueArgument;
        private final Callable<AWTEvent> getNextEventCallable;
        
        public EventQueueDelegateFromMap(final Map<String, Map<String, Object>> map) {
            final Map map2 = map.get("afterDispatch");
            this.afterDispatchEventArgument = (AWTEvent[])map2.get("event");
            this.afterDispatchHandleArgument = (Object[])map2.get("handle");
            this.afterDispatchCallable = (Callable<Void>)map2.get("method");
            final Map map3 = map.get("beforeDispatch");
            this.beforeDispatchEventArgument = (AWTEvent[])map3.get("event");
            this.beforeDispatchCallable = (Callable<Object>)map3.get("method");
            final Map map4 = map.get("getNextEvent");
            this.getNextEventEventQueueArgument = (EventQueue[])map4.get("eventQueue");
            this.getNextEventCallable = (Callable<AWTEvent>)map4.get("method");
        }
        
        @Override
        public void afterDispatch(final AWTEvent awtEvent, final Object o) throws InterruptedException {
            this.afterDispatchEventArgument[0] = awtEvent;
            this.afterDispatchHandleArgument[0] = o;
            try {
                this.afterDispatchCallable.call();
            }
            catch (final InterruptedException ex) {
                throw ex;
            }
            catch (final RuntimeException ex2) {
                throw ex2;
            }
            catch (final Exception ex3) {
                throw new RuntimeException(ex3);
            }
        }
        
        @Override
        public Object beforeDispatch(final AWTEvent awtEvent) throws InterruptedException {
            this.beforeDispatchEventArgument[0] = awtEvent;
            try {
                return this.beforeDispatchCallable.call();
            }
            catch (final InterruptedException ex) {
                throw ex;
            }
            catch (final RuntimeException ex2) {
                throw ex2;
            }
            catch (final Exception ex3) {
                throw new RuntimeException(ex3);
            }
        }
        
        @Override
        public AWTEvent getNextEvent(final EventQueue eventQueue) throws InterruptedException {
            this.getNextEventEventQueueArgument[0] = eventQueue;
            try {
                return this.getNextEventCallable.call();
            }
            catch (final InterruptedException ex) {
                throw ex;
            }
            catch (final RuntimeException ex2) {
                throw ex2;
            }
            catch (final Exception ex3) {
                throw new RuntimeException(ex3);
            }
        }
    }
}
