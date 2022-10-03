package java.awt;

import sun.misc.SharedSecrets;
import java.lang.reflect.InvocationTargetException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import sun.awt.AWTAccessor;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.FocusEvent;
import java.util.EmptyStackException;
import java.awt.event.InvocationEvent;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.dnd.SunDropTargetEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.ComponentPeer;
import java.awt.event.PaintEvent;
import sun.awt.EventQueueItem;
import sun.awt.PeerEvent;
import sun.awt.AWTAutoShutdown;
import sun.awt.SunToolkit;
import sun.misc.JavaSecurityAccess;
import sun.util.logging.PlatformLogger;
import sun.awt.FwDispatcher;
import sun.awt.AppContext;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.atomic.AtomicInteger;

public class EventQueue
{
    private static final AtomicInteger threadInitNumber;
    private static final int LOW_PRIORITY = 0;
    private static final int NORM_PRIORITY = 1;
    private static final int HIGH_PRIORITY = 2;
    private static final int ULTIMATE_PRIORITY = 3;
    private static final int NUM_PRIORITIES = 4;
    private Queue[] queues;
    private EventQueue nextQueue;
    private EventQueue previousQueue;
    private final Lock pushPopLock;
    private final Condition pushPopCond;
    private static final Runnable dummyRunnable;
    private EventDispatchThread dispatchThread;
    private final ThreadGroup threadGroup;
    private final ClassLoader classLoader;
    private long mostRecentEventTime;
    private long mostRecentKeyEventTime;
    private WeakReference<AWTEvent> currentEvent;
    private volatile int waitForID;
    private final AppContext appContext;
    private final String name;
    private FwDispatcher fwDispatcher;
    private static volatile PlatformLogger eventLog;
    private static final int PAINT = 0;
    private static final int UPDATE = 1;
    private static final int MOVE = 2;
    private static final int DRAG = 3;
    private static final int PEER = 4;
    private static final int CACHE_LENGTH = 5;
    private static final JavaSecurityAccess javaSecurityAccess;
    
    private static final PlatformLogger getEventLog() {
        if (EventQueue.eventLog == null) {
            EventQueue.eventLog = PlatformLogger.getLogger("java.awt.event.EventQueue");
        }
        return EventQueue.eventLog;
    }
    
    public EventQueue() {
        this.queues = new Queue[4];
        this.threadGroup = Thread.currentThread().getThreadGroup();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.mostRecentEventTime = System.currentTimeMillis();
        this.mostRecentKeyEventTime = System.currentTimeMillis();
        this.name = "AWT-EventQueue-" + EventQueue.threadInitNumber.getAndIncrement();
        for (int i = 0; i < 4; ++i) {
            this.queues[i] = new Queue();
        }
        this.appContext = AppContext.getAppContext();
        this.pushPopLock = (Lock)this.appContext.get(AppContext.EVENT_QUEUE_LOCK_KEY);
        this.pushPopCond = (Condition)this.appContext.get(AppContext.EVENT_QUEUE_COND_KEY);
    }
    
    public void postEvent(final AWTEvent awtEvent) {
        SunToolkit.flushPendingEvents(this.appContext);
        this.postEventPrivate(awtEvent);
    }
    
    private final void postEventPrivate(final AWTEvent awtEvent) {
        awtEvent.isPosted = true;
        this.pushPopLock.lock();
        try {
            if (this.nextQueue != null) {
                this.nextQueue.postEventPrivate(awtEvent);
                return;
            }
            if (this.dispatchThread == null) {
                if (awtEvent.getSource() == AWTAutoShutdown.getInstance()) {
                    return;
                }
                this.initDispatchThread();
            }
            this.postEvent(awtEvent, getPriority(awtEvent));
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    private static int getPriority(final AWTEvent awtEvent) {
        if (awtEvent instanceof PeerEvent) {
            final PeerEvent peerEvent = (PeerEvent)awtEvent;
            if ((peerEvent.getFlags() & 0x2L) != 0x0L) {
                return 3;
            }
            if ((peerEvent.getFlags() & 0x1L) != 0x0L) {
                return 2;
            }
            if ((peerEvent.getFlags() & 0x4L) != 0x0L) {
                return 0;
            }
        }
        final int id = awtEvent.getID();
        if (id >= 800 && id <= 801) {
            return 0;
        }
        return 1;
    }
    
    private void postEvent(final AWTEvent awtEvent, final int n) {
        if (this.coalesceEvent(awtEvent, n)) {
            return;
        }
        final EventQueueItem eventQueueItem = new EventQueueItem(awtEvent);
        this.cacheEQItem(eventQueueItem);
        final boolean b = awtEvent.getID() == this.waitForID;
        if (this.queues[n].head == null) {
            final boolean noEvents = this.noEvents();
            final Queue queue = this.queues[n];
            final Queue queue2 = this.queues[n];
            final EventQueueItem eventQueueItem2 = eventQueueItem;
            queue2.tail = eventQueueItem2;
            queue.head = eventQueueItem2;
            if (noEvents) {
                if (awtEvent.getSource() != AWTAutoShutdown.getInstance()) {
                    AWTAutoShutdown.getInstance().notifyThreadBusy(this.dispatchThread);
                }
                this.pushPopCond.signalAll();
            }
            else if (b) {
                this.pushPopCond.signalAll();
            }
        }
        else {
            this.queues[n].tail.next = eventQueueItem;
            this.queues[n].tail = eventQueueItem;
            if (b) {
                this.pushPopCond.signalAll();
            }
        }
    }
    
    private boolean coalescePaintEvent(final PaintEvent paintEvent) {
        final ComponentPeer peer = ((Component)paintEvent.getSource()).peer;
        if (peer != null) {
            peer.coalescePaintEvent(paintEvent);
        }
        final EventQueueItem[] eventCache = ((Component)paintEvent.getSource()).eventCache;
        if (eventCache == null) {
            return false;
        }
        final int eventToCacheIndex = eventToCacheIndex(paintEvent);
        if (eventToCacheIndex != -1 && eventCache[eventToCacheIndex] != null) {
            final PaintEvent mergePaintEvents = this.mergePaintEvents(paintEvent, (PaintEvent)eventCache[eventToCacheIndex].event);
            if (mergePaintEvents != null) {
                eventCache[eventToCacheIndex].event = mergePaintEvents;
                return true;
            }
        }
        return false;
    }
    
    private PaintEvent mergePaintEvents(final PaintEvent paintEvent, final PaintEvent paintEvent2) {
        final Rectangle updateRect = paintEvent.getUpdateRect();
        final Rectangle updateRect2 = paintEvent2.getUpdateRect();
        if (updateRect2.contains(updateRect)) {
            return paintEvent2;
        }
        if (updateRect.contains(updateRect2)) {
            return paintEvent;
        }
        return null;
    }
    
    private boolean coalesceMouseEvent(final MouseEvent event) {
        final EventQueueItem[] eventCache = ((Component)event.getSource()).eventCache;
        if (eventCache == null) {
            return false;
        }
        final int eventToCacheIndex = eventToCacheIndex(event);
        if (eventToCacheIndex != -1 && eventCache[eventToCacheIndex] != null) {
            eventCache[eventToCacheIndex].event = event;
            return true;
        }
        return false;
    }
    
    private boolean coalescePeerEvent(PeerEvent coalesceEvents) {
        final EventQueueItem[] eventCache = ((Component)coalesceEvents.getSource()).eventCache;
        if (eventCache == null) {
            return false;
        }
        final int eventToCacheIndex = eventToCacheIndex(coalesceEvents);
        if (eventToCacheIndex != -1 && eventCache[eventToCacheIndex] != null) {
            coalesceEvents = coalesceEvents.coalesceEvents((PeerEvent)eventCache[eventToCacheIndex].event);
            if (coalesceEvents != null) {
                eventCache[eventToCacheIndex].event = coalesceEvents;
                return true;
            }
            eventCache[eventToCacheIndex] = null;
        }
        return false;
    }
    
    private boolean coalesceOtherEvent(final AWTEvent awtEvent, final int n) {
        final int id = awtEvent.getID();
        final Component component = (Component)awtEvent.getSource();
        for (EventQueueItem eventQueueItem = this.queues[n].head; eventQueueItem != null; eventQueueItem = eventQueueItem.next) {
            if (eventQueueItem.event.getSource() == component && eventQueueItem.event.getID() == id) {
                final AWTEvent coalesceEvents = component.coalesceEvents(eventQueueItem.event, awtEvent);
                if (coalesceEvents != null) {
                    eventQueueItem.event = coalesceEvents;
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean coalesceEvent(final AWTEvent awtEvent, final int n) {
        if (!(awtEvent.getSource() instanceof Component)) {
            return false;
        }
        if (awtEvent instanceof PeerEvent) {
            return this.coalescePeerEvent((PeerEvent)awtEvent);
        }
        if (((Component)awtEvent.getSource()).isCoalescingEnabled() && this.coalesceOtherEvent(awtEvent, n)) {
            return true;
        }
        if (awtEvent instanceof PaintEvent) {
            return this.coalescePaintEvent((PaintEvent)awtEvent);
        }
        return awtEvent instanceof MouseEvent && this.coalesceMouseEvent((MouseEvent)awtEvent);
    }
    
    private void cacheEQItem(final EventQueueItem eventQueueItem) {
        final int eventToCacheIndex = eventToCacheIndex(eventQueueItem.event);
        if (eventToCacheIndex != -1 && eventQueueItem.event.getSource() instanceof Component) {
            final Component component = (Component)eventQueueItem.event.getSource();
            if (component.eventCache == null) {
                component.eventCache = new EventQueueItem[5];
            }
            component.eventCache[eventToCacheIndex] = eventQueueItem;
        }
    }
    
    private void uncacheEQItem(final EventQueueItem eventQueueItem) {
        final int eventToCacheIndex = eventToCacheIndex(eventQueueItem.event);
        if (eventToCacheIndex != -1 && eventQueueItem.event.getSource() instanceof Component) {
            final Component component = (Component)eventQueueItem.event.getSource();
            if (component.eventCache == null) {
                return;
            }
            component.eventCache[eventToCacheIndex] = null;
        }
    }
    
    private static int eventToCacheIndex(final AWTEvent awtEvent) {
        switch (awtEvent.getID()) {
            case 800: {
                return 0;
            }
            case 801: {
                return 1;
            }
            case 503: {
                return 2;
            }
            case 506: {
                return (awtEvent instanceof SunDropTargetEvent) ? -1 : 3;
            }
            default: {
                return (awtEvent instanceof PeerEvent) ? 4 : -1;
            }
        }
    }
    
    private boolean noEvents() {
        for (int i = 0; i < 4; ++i) {
            if (this.queues[i].head != null) {
                return false;
            }
        }
        return true;
    }
    
    public AWTEvent getNextEvent() throws InterruptedException {
        while (true) {
            SunToolkit.flushPendingEvents(this.appContext);
            this.pushPopLock.lock();
            try {
                final AWTEvent nextEventPrivate = this.getNextEventPrivate();
                if (nextEventPrivate != null) {
                    return nextEventPrivate;
                }
                AWTAutoShutdown.getInstance().notifyThreadFree(this.dispatchThread);
                this.pushPopCond.await();
            }
            finally {
                this.pushPopLock.unlock();
            }
        }
    }
    
    AWTEvent getNextEventPrivate() throws InterruptedException {
        for (int i = 3; i >= 0; --i) {
            if (this.queues[i].head != null) {
                final EventQueueItem head = this.queues[i].head;
                this.queues[i].head = head.next;
                if (head.next == null) {
                    this.queues[i].tail = null;
                }
                this.uncacheEQItem(head);
                return head.event;
            }
        }
        return null;
    }
    
    AWTEvent getNextEvent(final int waitForID) throws InterruptedException {
        while (true) {
            SunToolkit.flushPendingEvents(this.appContext);
            this.pushPopLock.lock();
            try {
                for (int i = 0; i < 4; ++i) {
                    EventQueueItem eventQueueItem = this.queues[i].head;
                    EventQueueItem tail = null;
                    while (eventQueueItem != null) {
                        if (eventQueueItem.event.getID() == waitForID) {
                            if (tail == null) {
                                this.queues[i].head = eventQueueItem.next;
                            }
                            else {
                                tail.next = eventQueueItem.next;
                            }
                            if (this.queues[i].tail == eventQueueItem) {
                                this.queues[i].tail = tail;
                            }
                            this.uncacheEQItem(eventQueueItem);
                            return eventQueueItem.event;
                        }
                        tail = eventQueueItem;
                        eventQueueItem = eventQueueItem.next;
                    }
                }
                this.waitForID = waitForID;
                this.pushPopCond.await();
                this.waitForID = 0;
            }
            finally {
                this.pushPopLock.unlock();
            }
        }
    }
    
    public AWTEvent peekEvent() {
        this.pushPopLock.lock();
        try {
            for (int i = 3; i >= 0; --i) {
                if (this.queues[i].head != null) {
                    return this.queues[i].head.event;
                }
            }
        }
        finally {
            this.pushPopLock.unlock();
        }
        return null;
    }
    
    public AWTEvent peekEvent(final int n) {
        this.pushPopLock.lock();
        try {
            for (int i = 3; i >= 0; --i) {
                for (EventQueueItem eventQueueItem = this.queues[i].head; eventQueueItem != null; eventQueueItem = eventQueueItem.next) {
                    if (eventQueueItem.event.getID() == n) {
                        return eventQueueItem.event;
                    }
                }
            }
        }
        finally {
            this.pushPopLock.unlock();
        }
        return null;
    }
    
    protected void dispatchEvent(final AWTEvent awtEvent) {
        final Object source = awtEvent.getSource();
        final PrivilegedAction<Void> privilegedAction = new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                if (EventQueue.this.fwDispatcher == null || EventQueue.this.isDispatchThreadImpl()) {
                    EventQueue.this.dispatchEventImpl(awtEvent, source);
                }
                else {
                    EventQueue.this.fwDispatcher.scheduleDispatch(new Runnable() {
                        @Override
                        public void run() {
                            if (EventQueue.this.dispatchThread.filterAndCheckEvent(awtEvent)) {
                                EventQueue.this.dispatchEventImpl(awtEvent, source);
                            }
                        }
                    });
                }
                return null;
            }
        };
        final AccessControlContext context = AccessController.getContext();
        final AccessControlContext accessControlContext = getAccessControlContextFrom(source);
        final AccessControlContext accessControlContext2 = awtEvent.getAccessControlContext();
        if (accessControlContext == null) {
            EventQueue.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)privilegedAction, context, accessControlContext2);
        }
        else {
            EventQueue.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    EventQueue.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)privilegedAction, accessControlContext2);
                    return null;
                }
            }, context, accessControlContext);
        }
    }
    
    private static AccessControlContext getAccessControlContextFrom(final Object o) {
        return (o instanceof Component) ? ((Component)o).getAccessControlContext() : ((o instanceof MenuComponent) ? ((MenuComponent)o).getAccessControlContext() : ((o instanceof TrayIcon) ? ((TrayIcon)o).getAccessControlContext() : null));
    }
    
    private void dispatchEventImpl(final AWTEvent currentEventAndMostRecentTimeImpl, final Object o) {
        currentEventAndMostRecentTimeImpl.isPosted = true;
        if (currentEventAndMostRecentTimeImpl instanceof ActiveEvent) {
            this.setCurrentEventAndMostRecentTimeImpl(currentEventAndMostRecentTimeImpl);
            ((ActiveEvent)currentEventAndMostRecentTimeImpl).dispatch();
        }
        else if (o instanceof Component) {
            ((Component)o).dispatchEvent(currentEventAndMostRecentTimeImpl);
            currentEventAndMostRecentTimeImpl.dispatched();
        }
        else if (o instanceof MenuComponent) {
            ((MenuComponent)o).dispatchEvent(currentEventAndMostRecentTimeImpl);
        }
        else if (o instanceof TrayIcon) {
            ((TrayIcon)o).dispatchEvent(currentEventAndMostRecentTimeImpl);
        }
        else if (o instanceof AWTAutoShutdown) {
            if (this.noEvents()) {
                this.dispatchThread.stopDispatching();
            }
        }
        else if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
            getEventLog().fine("Unable to dispatch event: " + currentEventAndMostRecentTimeImpl);
        }
    }
    
    public static long getMostRecentEventTime() {
        return Toolkit.getEventQueue().getMostRecentEventTimeImpl();
    }
    
    private long getMostRecentEventTimeImpl() {
        this.pushPopLock.lock();
        try {
            return (Thread.currentThread() == this.dispatchThread) ? this.mostRecentEventTime : System.currentTimeMillis();
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    long getMostRecentEventTimeEx() {
        this.pushPopLock.lock();
        try {
            return this.mostRecentEventTime;
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    public static AWTEvent getCurrentEvent() {
        return Toolkit.getEventQueue().getCurrentEventImpl();
    }
    
    private AWTEvent getCurrentEventImpl() {
        this.pushPopLock.lock();
        try {
            return (Thread.currentThread() == this.dispatchThread) ? this.currentEvent.get() : null;
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    public void push(final EventQueue eventQueue) {
        if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
            getEventLog().fine("EventQueue.push(" + eventQueue + ")");
        }
        this.pushPopLock.lock();
        try {
            EventQueue nextQueue;
            for (nextQueue = this; nextQueue.nextQueue != null; nextQueue = nextQueue.nextQueue) {}
            if (nextQueue.fwDispatcher != null) {
                throw new RuntimeException("push() to queue with fwDispatcher");
            }
            if (nextQueue.dispatchThread != null && nextQueue.dispatchThread.getEventQueue() == this) {
                eventQueue.dispatchThread = nextQueue.dispatchThread;
                nextQueue.dispatchThread.setEventQueue(eventQueue);
            }
            while (nextQueue.peekEvent() != null) {
                try {
                    eventQueue.postEventPrivate(nextQueue.getNextEventPrivate());
                }
                catch (final InterruptedException ex) {
                    if (!getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
                        continue;
                    }
                    getEventLog().fine("Interrupted push", ex);
                }
            }
            if (nextQueue.dispatchThread != null) {
                nextQueue.postEventPrivate(new InvocationEvent(nextQueue, EventQueue.dummyRunnable));
            }
            eventQueue.previousQueue = nextQueue;
            nextQueue.nextQueue = eventQueue;
            if (this.appContext.get(AppContext.EVENT_QUEUE_KEY) == nextQueue) {
                this.appContext.put(AppContext.EVENT_QUEUE_KEY, eventQueue);
            }
            this.pushPopCond.signalAll();
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    protected void pop() throws EmptyStackException {
        if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
            getEventLog().fine("EventQueue.pop(" + this + ")");
        }
        this.pushPopLock.lock();
        try {
            EventQueue nextQueue;
            for (nextQueue = this; nextQueue.nextQueue != null; nextQueue = nextQueue.nextQueue) {}
            final EventQueue previousQueue = nextQueue.previousQueue;
            if (previousQueue == null) {
                throw new EmptyStackException();
            }
            nextQueue.previousQueue = null;
            previousQueue.nextQueue = null;
            while (nextQueue.peekEvent() != null) {
                try {
                    previousQueue.postEventPrivate(nextQueue.getNextEventPrivate());
                }
                catch (final InterruptedException ex) {
                    if (!getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
                        continue;
                    }
                    getEventLog().fine("Interrupted pop", ex);
                }
            }
            if (nextQueue.dispatchThread != null && nextQueue.dispatchThread.getEventQueue() == this) {
                previousQueue.dispatchThread = nextQueue.dispatchThread;
                nextQueue.dispatchThread.setEventQueue(previousQueue);
            }
            if (this.appContext.get(AppContext.EVENT_QUEUE_KEY) == this) {
                this.appContext.put(AppContext.EVENT_QUEUE_KEY, previousQueue);
            }
            nextQueue.postEventPrivate(new InvocationEvent(nextQueue, EventQueue.dummyRunnable));
            this.pushPopCond.signalAll();
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    public SecondaryLoop createSecondaryLoop() {
        return this.createSecondaryLoop(null, null, 0L);
    }
    
    SecondaryLoop createSecondaryLoop(final Conditional conditional, final EventFilter eventFilter, final long n) {
        this.pushPopLock.lock();
        try {
            if (this.nextQueue != null) {
                return this.nextQueue.createSecondaryLoop(conditional, eventFilter, n);
            }
            if (this.fwDispatcher != null) {
                return new FwSecondaryLoopWrapper(this.fwDispatcher.createSecondaryLoop(), eventFilter);
            }
            if (this.dispatchThread == null) {
                this.initDispatchThread();
            }
            return new WaitDispatchSupport(this.dispatchThread, conditional, eventFilter, n);
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    public static boolean isDispatchThread() {
        return Toolkit.getEventQueue().isDispatchThreadImpl();
    }
    
    final boolean isDispatchThreadImpl() {
        EventQueue eventQueue = this;
        this.pushPopLock.lock();
        try {
            for (EventQueue eventQueue2 = eventQueue.nextQueue; eventQueue2 != null; eventQueue2 = eventQueue.nextQueue) {
                eventQueue = eventQueue2;
            }
            if (eventQueue.fwDispatcher != null) {
                return eventQueue.fwDispatcher.isDispatchThread();
            }
            return Thread.currentThread() == eventQueue.dispatchThread;
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    final void initDispatchThread() {
        this.pushPopLock.lock();
        try {
            if (this.dispatchThread == null && !this.threadGroup.isDestroyed() && !this.appContext.isDisposed()) {
                (this.dispatchThread = AccessController.doPrivileged((PrivilegedAction<EventDispatchThread>)new PrivilegedAction<EventDispatchThread>() {
                    @Override
                    public EventDispatchThread run() {
                        final EventDispatchThread eventDispatchThread = new EventDispatchThread(EventQueue.this.threadGroup, EventQueue.this.name, EventQueue.this);
                        eventDispatchThread.setContextClassLoader(EventQueue.this.classLoader);
                        eventDispatchThread.setPriority(6);
                        eventDispatchThread.setDaemon(false);
                        AWTAutoShutdown.getInstance().notifyThreadBusy(eventDispatchThread);
                        return eventDispatchThread;
                    }
                })).start();
            }
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    final void detachDispatchThread(final EventDispatchThread eventDispatchThread) {
        SunToolkit.flushPendingEvents(this.appContext);
        this.pushPopLock.lock();
        try {
            if (eventDispatchThread == this.dispatchThread) {
                this.dispatchThread = null;
            }
            AWTAutoShutdown.getInstance().notifyThreadFree(eventDispatchThread);
            if (this.peekEvent() != null) {
                this.initDispatchThread();
            }
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    final EventDispatchThread getDispatchThread() {
        this.pushPopLock.lock();
        try {
            return this.dispatchThread;
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    final void removeSourceEvents(final Object o, final boolean b) {
        SunToolkit.flushPendingEvents(this.appContext);
        this.pushPopLock.lock();
        try {
            for (int i = 0; i < 4; ++i) {
                EventQueueItem eventQueueItem = this.queues[i].head;
                EventQueueItem tail = null;
                while (eventQueueItem != null) {
                    if (eventQueueItem.event.getSource() == o && (b || (!(eventQueueItem.event instanceof SequencedEvent) && !(eventQueueItem.event instanceof SentEvent) && !(eventQueueItem.event instanceof FocusEvent) && !(eventQueueItem.event instanceof WindowEvent) && !(eventQueueItem.event instanceof KeyEvent) && !(eventQueueItem.event instanceof InputMethodEvent)))) {
                        if (eventQueueItem.event instanceof SequencedEvent) {
                            ((SequencedEvent)eventQueueItem.event).dispose();
                        }
                        if (eventQueueItem.event instanceof SentEvent) {
                            ((SentEvent)eventQueueItem.event).dispose();
                        }
                        if (eventQueueItem.event instanceof InvocationEvent) {
                            AWTAccessor.getInvocationEventAccessor().dispose((InvocationEvent)eventQueueItem.event);
                        }
                        if (tail == null) {
                            this.queues[i].head = eventQueueItem.next;
                        }
                        else {
                            tail.next = eventQueueItem.next;
                        }
                        this.uncacheEQItem(eventQueueItem);
                    }
                    else {
                        tail = eventQueueItem;
                    }
                    eventQueueItem = eventQueueItem.next;
                }
                this.queues[i].tail = tail;
            }
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    synchronized long getMostRecentKeyEventTime() {
        this.pushPopLock.lock();
        try {
            return this.mostRecentKeyEventTime;
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    static void setCurrentEventAndMostRecentTime(final AWTEvent currentEventAndMostRecentTimeImpl) {
        Toolkit.getEventQueue().setCurrentEventAndMostRecentTimeImpl(currentEventAndMostRecentTimeImpl);
    }
    
    private void setCurrentEventAndMostRecentTimeImpl(final AWTEvent awtEvent) {
        this.pushPopLock.lock();
        try {
            if (Thread.currentThread() != this.dispatchThread) {
                return;
            }
            this.currentEvent = new WeakReference<AWTEvent>(awtEvent);
            long n = Long.MIN_VALUE;
            if (awtEvent instanceof InputEvent) {
                final InputEvent inputEvent = (InputEvent)awtEvent;
                n = inputEvent.getWhen();
                if (awtEvent instanceof KeyEvent) {
                    this.mostRecentKeyEventTime = inputEvent.getWhen();
                }
            }
            else if (awtEvent instanceof InputMethodEvent) {
                n = ((InputMethodEvent)awtEvent).getWhen();
            }
            else if (awtEvent instanceof ActionEvent) {
                n = ((ActionEvent)awtEvent).getWhen();
            }
            else if (awtEvent instanceof InvocationEvent) {
                n = ((InvocationEvent)awtEvent).getWhen();
            }
            this.mostRecentEventTime = Math.max(this.mostRecentEventTime, n);
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    public static void invokeLater(final Runnable runnable) {
        Toolkit.getEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), runnable));
    }
    
    public static void invokeAndWait(final Runnable runnable) throws InterruptedException, InvocationTargetException {
        invokeAndWait(Toolkit.getDefaultToolkit(), runnable);
    }
    
    static void invokeAndWait(final Object o, final Runnable runnable) throws InterruptedException, InvocationTargetException {
        if (isDispatchThread()) {
            throw new Error("Cannot call invokeAndWait from the event dispatcher thread");
        }
        class AWTInvocationLock
        {
        }
        final AWTInvocationLock awtInvocationLock = new AWTInvocationLock();
        final InvocationEvent invocationEvent = new InvocationEvent(o, runnable, awtInvocationLock, true);
        synchronized (awtInvocationLock) {
            Toolkit.getEventQueue().postEvent(invocationEvent);
            while (!invocationEvent.isDispatched()) {
                awtInvocationLock.wait();
            }
        }
        final Throwable throwable = invocationEvent.getThrowable();
        if (throwable != null) {
            throw new InvocationTargetException(throwable);
        }
    }
    
    private void wakeup(final boolean b) {
        this.pushPopLock.lock();
        try {
            if (this.nextQueue != null) {
                this.nextQueue.wakeup(b);
            }
            else if (this.dispatchThread != null) {
                this.pushPopCond.signalAll();
            }
            else if (!b) {
                this.initDispatchThread();
            }
        }
        finally {
            this.pushPopLock.unlock();
        }
    }
    
    private void setFwDispatcher(final FwDispatcher fwDispatcher) {
        if (this.nextQueue != null) {
            this.nextQueue.setFwDispatcher(fwDispatcher);
        }
        else {
            this.fwDispatcher = fwDispatcher;
        }
    }
    
    static {
        threadInitNumber = new AtomicInteger(0);
        dummyRunnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        AWTAccessor.setEventQueueAccessor(new AWTAccessor.EventQueueAccessor() {
            @Override
            public Thread getDispatchThread(final EventQueue eventQueue) {
                return eventQueue.getDispatchThread();
            }
            
            @Override
            public boolean isDispatchThreadImpl(final EventQueue eventQueue) {
                return eventQueue.isDispatchThreadImpl();
            }
            
            @Override
            public void removeSourceEvents(final EventQueue eventQueue, final Object o, final boolean b) {
                eventQueue.removeSourceEvents(o, b);
            }
            
            @Override
            public boolean noEvents(final EventQueue eventQueue) {
                return eventQueue.noEvents();
            }
            
            @Override
            public void wakeup(final EventQueue eventQueue, final boolean b) {
                eventQueue.wakeup(b);
            }
            
            @Override
            public void invokeAndWait(final Object o, final Runnable runnable) throws InterruptedException, InvocationTargetException {
                EventQueue.invokeAndWait(o, runnable);
            }
            
            @Override
            public void setFwDispatcher(final EventQueue eventQueue, final FwDispatcher fwDispatcher) {
                eventQueue.setFwDispatcher(fwDispatcher);
            }
            
            @Override
            public long getMostRecentEventTime(final EventQueue eventQueue) {
                return eventQueue.getMostRecentEventTimeImpl();
            }
        });
        javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
    }
    
    private class FwSecondaryLoopWrapper implements SecondaryLoop
    {
        private final SecondaryLoop loop;
        private final EventFilter filter;
        
        public FwSecondaryLoopWrapper(final SecondaryLoop loop, final EventFilter filter) {
            this.loop = loop;
            this.filter = filter;
        }
        
        @Override
        public boolean enter() {
            if (this.filter != null) {
                EventQueue.this.dispatchThread.addEventFilter(this.filter);
            }
            return this.loop.enter();
        }
        
        @Override
        public boolean exit() {
            if (this.filter != null) {
                EventQueue.this.dispatchThread.removeEventFilter(this.filter);
            }
            return this.loop.exit();
        }
    }
}
