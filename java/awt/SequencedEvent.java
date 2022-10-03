package java.awt;

import sun.awt.AWTAccessor;
import java.util.Iterator;
import sun.awt.SunToolkit;
import sun.awt.AppContext;
import java.util.LinkedList;

class SequencedEvent extends AWTEvent implements ActiveEvent
{
    private static final long serialVersionUID = 547742659238625067L;
    private static final int ID = 1006;
    private static final LinkedList<SequencedEvent> list;
    private final AWTEvent nested;
    private AppContext appContext;
    private boolean disposed;
    private final LinkedList<AWTEvent> pendingEvents;
    
    public SequencedEvent(final AWTEvent nested) {
        super(nested.getSource(), 1006);
        this.pendingEvents = new LinkedList<AWTEvent>();
        SunToolkit.setSystemGenerated(this.nested = nested);
        synchronized (SequencedEvent.class) {
            SequencedEvent.list.add(this);
        }
    }
    
    @Override
    public final void dispatch() {
        try {
            this.appContext = AppContext.getAppContext();
            if (getFirst() != this) {
                if (EventQueue.isDispatchThread()) {
                    ((EventDispatchThread)Thread.currentThread()).pumpEventsForFilter(() -> !this.isFirstOrDisposed(), new SequencedEventsFilter(this));
                }
                else {
                    while (!this.isFirstOrDisposed()) {
                        synchronized (SequencedEvent.class) {
                            try {
                                SequencedEvent.class.wait(1000L);
                            }
                            catch (final InterruptedException ex) {
                                break;
                            }
                        }
                    }
                }
            }
            if (!this.disposed) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().setCurrentSequencedEvent(this);
                Toolkit.getEventQueue().dispatchEvent(this.nested);
            }
        }
        finally {
            this.dispose();
        }
    }
    
    private static final boolean isOwnerAppContextDisposed(final SequencedEvent sequencedEvent) {
        if (sequencedEvent != null) {
            final Object source = sequencedEvent.nested.getSource();
            if (source instanceof Component) {
                return ((Component)source).appContext.isDisposed();
            }
        }
        return false;
    }
    
    public final boolean isFirstOrDisposed() {
        return this.disposed || this == getFirstWithContext() || this.disposed;
    }
    
    private static final synchronized SequencedEvent getFirst() {
        return SequencedEvent.list.getFirst();
    }
    
    private static final SequencedEvent getFirstWithContext() {
        SequencedEvent sequencedEvent;
        for (sequencedEvent = getFirst(); isOwnerAppContextDisposed(sequencedEvent); sequencedEvent = getFirst()) {
            sequencedEvent.dispose();
        }
        return sequencedEvent;
    }
    
    final void dispose() {
        synchronized (SequencedEvent.class) {
            if (this.disposed) {
                return;
            }
            if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent() == this) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().setCurrentSequencedEvent(null);
            }
            this.disposed = true;
        }
        SequencedEvent sequencedEvent = null;
        synchronized (SequencedEvent.class) {
            SequencedEvent.class.notifyAll();
            if (SequencedEvent.list.getFirst() == this) {
                SequencedEvent.list.removeFirst();
                if (!SequencedEvent.list.isEmpty()) {
                    sequencedEvent = SequencedEvent.list.getFirst();
                }
            }
            else {
                SequencedEvent.list.remove(this);
            }
        }
        if (sequencedEvent != null && sequencedEvent.appContext != null) {
            SunToolkit.postEvent(sequencedEvent.appContext, new SentEvent());
        }
        final Iterator<Object> iterator = this.pendingEvents.iterator();
        while (iterator.hasNext()) {
            SunToolkit.postEvent(this.appContext, iterator.next());
        }
    }
    
    static {
        list = new LinkedList<SequencedEvent>();
        AWTAccessor.setSequencedEventAccessor(new AWTAccessor.SequencedEventAccessor() {
            @Override
            public AWTEvent getNested(final AWTEvent awtEvent) {
                return ((SequencedEvent)awtEvent).nested;
            }
            
            @Override
            public boolean isSequencedEvent(final AWTEvent awtEvent) {
                return awtEvent instanceof SequencedEvent;
            }
        });
    }
    
    private static final class SequencedEventsFilter implements EventFilter
    {
        private final SequencedEvent currentSequencedEvent;
        
        private SequencedEventsFilter(final SequencedEvent currentSequencedEvent) {
            this.currentSequencedEvent = currentSequencedEvent;
        }
        
        @Override
        public FilterAction acceptEvent(final AWTEvent awtEvent) {
            if (awtEvent.getID() == 1006) {
                synchronized (SequencedEvent.class) {
                    for (final SequencedEvent sequencedEvent : SequencedEvent.list) {
                        if (sequencedEvent.equals(this.currentSequencedEvent)) {
                            break;
                        }
                        if (sequencedEvent.equals(awtEvent)) {
                            return FilterAction.ACCEPT;
                        }
                    }
                }
            }
            else if (awtEvent.getID() == 1007) {
                return FilterAction.ACCEPT;
            }
            this.currentSequencedEvent.pendingEvents.add(awtEvent);
            return FilterAction.REJECT;
        }
    }
}
