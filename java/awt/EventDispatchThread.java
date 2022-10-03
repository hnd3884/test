package java.awt;

import sun.awt.SunToolkit;
import sun.awt.ModalExclude;
import sun.awt.EventQueueDelegate;
import sun.awt.dnd.SunDragSourceContextPeer;
import java.util.ArrayList;
import sun.util.logging.PlatformLogger;

class EventDispatchThread extends Thread
{
    private static final PlatformLogger eventLog;
    private EventQueue theQueue;
    private volatile boolean doDispatch;
    private static final int ANY_EVENT = -1;
    private ArrayList<EventFilter> eventFilters;
    
    EventDispatchThread(final ThreadGroup threadGroup, final String s, final EventQueue eventQueue) {
        super(threadGroup, s);
        this.doDispatch = true;
        this.eventFilters = new ArrayList<EventFilter>();
        this.setEventQueue(eventQueue);
    }
    
    public void stopDispatching() {
        this.doDispatch = false;
    }
    
    @Override
    public void run() {
        try {
            this.pumpEvents(new Conditional() {
                @Override
                public boolean evaluate() {
                    return true;
                }
            });
        }
        finally {
            this.getEventQueue().detachDispatchThread(this);
        }
    }
    
    void pumpEvents(final Conditional conditional) {
        this.pumpEvents(-1, conditional);
    }
    
    void pumpEventsForHierarchy(final Conditional conditional, final Component component) {
        this.pumpEventsForHierarchy(-1, conditional, component);
    }
    
    void pumpEvents(final int n, final Conditional conditional) {
        this.pumpEventsForHierarchy(n, conditional, null);
    }
    
    void pumpEventsForHierarchy(final int n, final Conditional conditional, final Component component) {
        this.pumpEventsForFilter(n, conditional, new HierarchyEventFilter(component));
    }
    
    void pumpEventsForFilter(final Conditional conditional, final EventFilter eventFilter) {
        this.pumpEventsForFilter(-1, conditional, eventFilter);
    }
    
    void pumpEventsForFilter(final int n, final Conditional conditional, final EventFilter eventFilter) {
        this.addEventFilter(eventFilter);
        this.doDispatch = true;
        while (this.doDispatch && !this.isInterrupted() && conditional.evaluate()) {
            this.pumpOneEventForFilters(n);
        }
        this.removeEventFilter(eventFilter);
    }
    
    void addEventFilter(final EventFilter eventFilter) {
        if (EventDispatchThread.eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
            EventDispatchThread.eventLog.finest("adding the event filter: " + eventFilter);
        }
        synchronized (this.eventFilters) {
            if (!this.eventFilters.contains(eventFilter)) {
                if (eventFilter instanceof ModalEventFilter) {
                    final ModalEventFilter modalEventFilter = (ModalEventFilter)eventFilter;
                    int i;
                    for (i = 0; i < this.eventFilters.size(); ++i) {
                        final EventFilter eventFilter2 = this.eventFilters.get(i);
                        if (eventFilter2 instanceof ModalEventFilter && ((ModalEventFilter)eventFilter2).compareTo(modalEventFilter) > 0) {
                            break;
                        }
                    }
                    this.eventFilters.add(i, eventFilter);
                }
                else {
                    this.eventFilters.add(eventFilter);
                }
            }
        }
    }
    
    void removeEventFilter(final EventFilter eventFilter) {
        if (EventDispatchThread.eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
            EventDispatchThread.eventLog.finest("removing the event filter: " + eventFilter);
        }
        synchronized (this.eventFilters) {
            this.eventFilters.remove(eventFilter);
        }
    }
    
    boolean filterAndCheckEvent(final AWTEvent awtEvent) {
        boolean b = true;
        synchronized (this.eventFilters) {
            for (int i = this.eventFilters.size() - 1; i >= 0; --i) {
                final EventFilter.FilterAction acceptEvent = this.eventFilters.get(i).acceptEvent(awtEvent);
                if (acceptEvent == EventFilter.FilterAction.REJECT) {
                    b = false;
                    break;
                }
                if (acceptEvent == EventFilter.FilterAction.ACCEPT_IMMEDIATELY) {
                    break;
                }
            }
        }
        return b && SunDragSourceContextPeer.checkEvent(awtEvent);
    }
    
    void pumpOneEventForFilters(final int n) {
        try {
            boolean filterAndCheckEvent;
            EventQueue eventQueue;
            EventQueueDelegate.Delegate delegate;
            AWTEvent nextEvent;
            do {
                eventQueue = this.getEventQueue();
                delegate = EventQueueDelegate.getDelegate();
                if (delegate != null && n == -1) {
                    nextEvent = delegate.getNextEvent(eventQueue);
                }
                else {
                    nextEvent = ((n == -1) ? eventQueue.getNextEvent() : eventQueue.getNextEvent(n));
                }
                filterAndCheckEvent = this.filterAndCheckEvent(nextEvent);
                if (!filterAndCheckEvent) {
                    nextEvent.consume();
                }
            } while (!filterAndCheckEvent);
            if (EventDispatchThread.eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
                EventDispatchThread.eventLog.finest("Dispatching: " + nextEvent);
            }
            Object beforeDispatch = null;
            if (delegate != null) {
                beforeDispatch = delegate.beforeDispatch(nextEvent);
            }
            eventQueue.dispatchEvent(nextEvent);
            if (delegate != null) {
                delegate.afterDispatch(nextEvent, beforeDispatch);
            }
        }
        catch (final ThreadDeath threadDeath) {
            this.doDispatch = false;
            throw threadDeath;
        }
        catch (final InterruptedException ex) {
            this.doDispatch = false;
        }
        catch (final Throwable t) {
            this.processException(t);
        }
    }
    
    private void processException(final Throwable t) {
        if (EventDispatchThread.eventLog.isLoggable(PlatformLogger.Level.FINE)) {
            EventDispatchThread.eventLog.fine("Processing exception: " + t);
        }
        this.getUncaughtExceptionHandler().uncaughtException(this, t);
    }
    
    public synchronized EventQueue getEventQueue() {
        return this.theQueue;
    }
    
    public synchronized void setEventQueue(final EventQueue theQueue) {
        this.theQueue = theQueue;
    }
    
    static {
        eventLog = PlatformLogger.getLogger("java.awt.event.EventDispatchThread");
    }
    
    private static class HierarchyEventFilter implements EventFilter
    {
        private Component modalComponent;
        
        public HierarchyEventFilter(final Component modalComponent) {
            this.modalComponent = modalComponent;
        }
        
        @Override
        public FilterAction acceptEvent(final AWTEvent awtEvent) {
            if (this.modalComponent != null) {
                final int id = awtEvent.getID();
                final boolean b = id >= 500 && id <= 507;
                final boolean b2 = id >= 1001 && id <= 1001;
                final boolean b3 = id == 201;
                if (Component.isInstanceOf(this.modalComponent, "javax.swing.JInternalFrame")) {
                    return b3 ? FilterAction.REJECT : FilterAction.ACCEPT;
                }
                if (b || b2 || b3) {
                    final Object source = awtEvent.getSource();
                    if (source instanceof ModalExclude) {
                        return FilterAction.ACCEPT;
                    }
                    if (source instanceof Component) {
                        Component parent = (Component)source;
                        boolean b4 = false;
                        if (this.modalComponent instanceof Container) {
                            while (parent != this.modalComponent && parent != null) {
                                if (parent instanceof Window && SunToolkit.isModalExcluded((Window)parent)) {
                                    b4 = true;
                                    break;
                                }
                                parent = parent.getParent();
                            }
                        }
                        if (!b4 && parent != this.modalComponent) {
                            return FilterAction.REJECT;
                        }
                    }
                }
            }
            return FilterAction.ACCEPT;
        }
    }
}
