package java.awt;

import sun.awt.AWTAccessor;
import java.util.ListIterator;
import java.util.Set;
import java.awt.peer.ComponentPeer;
import java.util.Iterator;
import java.util.List;
import java.awt.peer.LightweightPeer;
import sun.awt.TimedWindowEvent;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.CausedFocusEvent;
import java.awt.event.WindowEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.lang.ref.WeakReference;
import sun.util.logging.PlatformLogger;

public class DefaultKeyboardFocusManager extends KeyboardFocusManager
{
    private static final PlatformLogger focusLog;
    private static final WeakReference<Window> NULL_WINDOW_WR;
    private static final WeakReference<Component> NULL_COMPONENT_WR;
    private WeakReference<Window> realOppositeWindowWR;
    private WeakReference<Component> realOppositeComponentWR;
    private int inSendMessage;
    private LinkedList<KeyEvent> enqueuedKeyEvents;
    private LinkedList<TypeAheadMarker> typeAheadMarkers;
    private boolean consumeNextKeyTyped;
    private Component restoreFocusTo;
    
    public DefaultKeyboardFocusManager() {
        this.realOppositeWindowWR = DefaultKeyboardFocusManager.NULL_WINDOW_WR;
        this.realOppositeComponentWR = DefaultKeyboardFocusManager.NULL_COMPONENT_WR;
        this.enqueuedKeyEvents = new LinkedList<KeyEvent>();
        this.typeAheadMarkers = new LinkedList<TypeAheadMarker>();
    }
    
    private Window getOwningFrameDialog(Window window) {
        while (window != null && !(window instanceof Frame) && !(window instanceof Dialog)) {
            window = (Window)window.getParent();
        }
        return window;
    }
    
    private void restoreFocus(final FocusEvent focusEvent, final Window window) {
        final Component component = this.realOppositeComponentWR.get();
        final Component component2 = focusEvent.getComponent();
        if (window == null || !this.restoreFocus(window, component2, false)) {
            if (component == null || !this.doRestoreFocus(component, component2, false)) {
                if (focusEvent.getOppositeComponent() == null || !this.doRestoreFocus(focusEvent.getOppositeComponent(), component2, false)) {
                    this.clearGlobalFocusOwnerPriv();
                }
            }
        }
    }
    
    private void restoreFocus(final WindowEvent windowEvent) {
        final Window window = this.realOppositeWindowWR.get();
        if (window == null || !this.restoreFocus(window, null, false)) {
            if (windowEvent.getOppositeWindow() == null || !this.restoreFocus(windowEvent.getOppositeWindow(), null, false)) {
                this.clearGlobalFocusOwnerPriv();
            }
        }
    }
    
    private boolean restoreFocus(final Window window, final Component component, final boolean b) {
        this.restoreFocusTo = null;
        Component restoreFocusTo = KeyboardFocusManager.getMostRecentFocusOwner(window);
        if (restoreFocusTo != null && restoreFocusTo != component) {
            if (KeyboardFocusManager.getHeavyweight(window) != this.getNativeFocusOwner()) {
                if (!restoreFocusTo.isShowing() || !restoreFocusTo.canBeFocusOwner()) {
                    restoreFocusTo = restoreFocusTo.getNextFocusCandidate();
                }
                if (restoreFocusTo != null && restoreFocusTo != component) {
                    if (!restoreFocusTo.requestFocus(false, CausedFocusEvent.Cause.ROLLBACK)) {
                        this.restoreFocusTo = restoreFocusTo;
                    }
                    return true;
                }
            }
            else if (this.doRestoreFocus(restoreFocusTo, component, false)) {
                return true;
            }
        }
        if (b) {
            this.clearGlobalFocusOwnerPriv();
            return true;
        }
        return false;
    }
    
    private boolean restoreFocus(final Component component, final boolean b) {
        return this.doRestoreFocus(component, null, b);
    }
    
    private boolean doRestoreFocus(final Component restoreFocusTo, final Component component, final boolean b) {
        boolean requestFocus = true;
        if (restoreFocusTo != component && restoreFocusTo.isShowing() && restoreFocusTo.canBeFocusOwner() && (requestFocus = restoreFocusTo.requestFocus(false, CausedFocusEvent.Cause.ROLLBACK))) {
            return true;
        }
        if (!requestFocus && this.getGlobalFocusedWindow() != SunToolkit.getContainingWindow(restoreFocusTo)) {
            this.restoreFocusTo = restoreFocusTo;
            return true;
        }
        final Component nextFocusCandidate = restoreFocusTo.getNextFocusCandidate();
        if (nextFocusCandidate != null && nextFocusCandidate != component && nextFocusCandidate.requestFocusInWindow(CausedFocusEvent.Cause.ROLLBACK)) {
            return true;
        }
        if (b) {
            this.clearGlobalFocusOwnerPriv();
            return true;
        }
        return false;
    }
    
    static boolean sendMessage(final Component component, final AWTEvent awtEvent) {
        awtEvent.isPosted = true;
        final AppContext appContext = AppContext.getAppContext();
        final AppContext appContext2 = component.appContext;
        final DefaultKeyboardFocusManagerSentEvent defaultKeyboardFocusManagerSentEvent = new DefaultKeyboardFocusManagerSentEvent(awtEvent, appContext);
        if (appContext == appContext2) {
            defaultKeyboardFocusManagerSentEvent.dispatch();
        }
        else {
            if (appContext2.isDisposed()) {
                return false;
            }
            SunToolkit.postEvent(appContext2, defaultKeyboardFocusManagerSentEvent);
            if (EventQueue.isDispatchThread()) {
                ((EventDispatchThread)Thread.currentThread()).pumpEvents(1007, new Conditional() {
                    @Override
                    public boolean evaluate() {
                        return !defaultKeyboardFocusManagerSentEvent.dispatched && !appContext2.isDisposed();
                    }
                });
            }
            else {
                synchronized (defaultKeyboardFocusManagerSentEvent) {
                    while (!defaultKeyboardFocusManagerSentEvent.dispatched && !appContext2.isDisposed()) {
                        try {
                            defaultKeyboardFocusManagerSentEvent.wait(1000L);
                            continue;
                        }
                        catch (final InterruptedException ex) {}
                        break;
                    }
                }
            }
        }
        return defaultKeyboardFocusManagerSentEvent.dispatched;
    }
    
    private boolean repostIfFollowsKeyEvents(final WindowEvent windowEvent) {
        if (!(windowEvent instanceof TimedWindowEvent)) {
            return false;
        }
        final long when = ((TimedWindowEvent)windowEvent).getWhen();
        synchronized (this) {
            final KeyEvent keyEvent = this.enqueuedKeyEvents.isEmpty() ? null : this.enqueuedKeyEvents.getFirst();
            if (keyEvent != null && when >= keyEvent.getWhen()) {
                final TypeAheadMarker typeAheadMarker = this.typeAheadMarkers.isEmpty() ? null : this.typeAheadMarkers.getFirst();
                if (typeAheadMarker != null) {
                    final Window containingWindow = typeAheadMarker.untilFocused.getContainingWindow();
                    if (containingWindow != null && containingWindow.isFocused()) {
                        SunToolkit.postEvent(AppContext.getAppContext(), new SequencedEvent(windowEvent));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean dispatchEvent(final AWTEvent awtEvent) {
        if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINE) && (awtEvent instanceof WindowEvent || awtEvent instanceof FocusEvent)) {
            DefaultKeyboardFocusManager.focusLog.fine("" + awtEvent);
        }
        switch (awtEvent.getID()) {
            case 207: {
                if (this.repostIfFollowsKeyEvents((WindowEvent)awtEvent)) {
                    break;
                }
                AWTEvent awtEvent2 = awtEvent;
                final Window globalFocusedWindow = this.getGlobalFocusedWindow();
                final Window window = ((WindowEvent)awtEvent2).getWindow();
                if (window == globalFocusedWindow) {
                    break;
                }
                if (!window.isFocusableWindow() || !window.isVisible() || !window.isDisplayable()) {
                    this.restoreFocus((WindowEvent)awtEvent2);
                    break;
                }
                if (globalFocusedWindow != null && !sendMessage(globalFocusedWindow, new WindowEvent(globalFocusedWindow, 208, window))) {
                    this.setGlobalFocusOwner(null);
                    this.setGlobalFocusedWindow(null);
                }
                final Window owningFrameDialog = this.getOwningFrameDialog(window);
                final Window globalActiveWindow = this.getGlobalActiveWindow();
                if (owningFrameDialog != globalActiveWindow) {
                    sendMessage(owningFrameDialog, new WindowEvent(owningFrameDialog, 205, globalActiveWindow));
                    if (owningFrameDialog != this.getGlobalActiveWindow()) {
                        this.restoreFocus((WindowEvent)awtEvent2);
                        break;
                    }
                }
                this.setGlobalFocusedWindow(window);
                if (window != this.getGlobalFocusedWindow()) {
                    this.restoreFocus((WindowEvent)awtEvent2);
                    break;
                }
                if (this.inSendMessage == 0) {
                    Component component = KeyboardFocusManager.getMostRecentFocusOwner(window);
                    final boolean b = this.restoreFocusTo != null && component == this.restoreFocusTo;
                    if (component == null && window.isFocusableWindow()) {
                        component = window.getFocusTraversalPolicy().getInitialComponent(window);
                    }
                    Component setTemporaryLostComponent = null;
                    synchronized (KeyboardFocusManager.class) {
                        setTemporaryLostComponent = window.setTemporaryLostComponent(null);
                    }
                    if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                        DefaultKeyboardFocusManager.focusLog.finer("tempLost {0}, toFocus {1}", setTemporaryLostComponent, component);
                    }
                    if (setTemporaryLostComponent != null) {
                        setTemporaryLostComponent.requestFocusInWindow((b && setTemporaryLostComponent == component) ? CausedFocusEvent.Cause.ROLLBACK : CausedFocusEvent.Cause.ACTIVATION);
                    }
                    if (component != null && component != setTemporaryLostComponent) {
                        component.requestFocusInWindow(CausedFocusEvent.Cause.ACTIVATION);
                    }
                }
                this.restoreFocusTo = null;
                final Window window2 = this.realOppositeWindowWR.get();
                if (window2 != ((WindowEvent)awtEvent2).getOppositeWindow()) {
                    awtEvent2 = new WindowEvent(window, 207, window2);
                }
                return this.typeAheadAssertions(window, awtEvent2);
            }
            case 205: {
                final WindowEvent windowEvent = (WindowEvent)awtEvent;
                final Window globalActiveWindow2 = this.getGlobalActiveWindow();
                final Window window3 = windowEvent.getWindow();
                if (globalActiveWindow2 == window3) {
                    break;
                }
                if (globalActiveWindow2 != null) {
                    if (!sendMessage(globalActiveWindow2, new WindowEvent(globalActiveWindow2, 206, window3))) {
                        this.setGlobalActiveWindow(null);
                    }
                    if (this.getGlobalActiveWindow() != null) {
                        break;
                    }
                }
                this.setGlobalActiveWindow(window3);
                if (window3 != this.getGlobalActiveWindow()) {
                    break;
                }
                return this.typeAheadAssertions(window3, windowEvent);
            }
            case 1004: {
                this.restoreFocusTo = null;
                FocusEvent focusEvent = (FocusEvent)awtEvent;
                final CausedFocusEvent.Cause cause = (focusEvent instanceof CausedFocusEvent) ? ((CausedFocusEvent)focusEvent).getCause() : CausedFocusEvent.Cause.UNKNOWN;
                final Component globalFocusOwner = this.getGlobalFocusOwner();
                final Component component2 = focusEvent.getComponent();
                if (globalFocusOwner == component2) {
                    if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINE)) {
                        DefaultKeyboardFocusManager.focusLog.fine("Skipping {0} because focus owner is the same", awtEvent);
                    }
                    this.dequeueKeyEvents(-1L, component2);
                    break;
                }
                if (globalFocusOwner != null && !sendMessage(globalFocusOwner, new CausedFocusEvent(globalFocusOwner, 1005, focusEvent.isTemporary(), component2, cause))) {
                    this.setGlobalFocusOwner(null);
                    if (!focusEvent.isTemporary()) {
                        this.setGlobalPermanentFocusOwner(null);
                    }
                }
                final Window containingWindow = SunToolkit.getContainingWindow(component2);
                final Window globalFocusedWindow2 = this.getGlobalFocusedWindow();
                if (containingWindow != null && containingWindow != globalFocusedWindow2) {
                    sendMessage(containingWindow, new WindowEvent(containingWindow, 207, globalFocusedWindow2));
                    if (containingWindow != this.getGlobalFocusedWindow()) {
                        this.dequeueKeyEvents(-1L, component2);
                        break;
                    }
                }
                if (!component2.isFocusable() || !component2.isShowing() || (!component2.isEnabled() && !cause.equals(CausedFocusEvent.Cause.UNKNOWN))) {
                    this.dequeueKeyEvents(-1L, component2);
                    if (KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                        if (containingWindow == null) {
                            this.restoreFocus(focusEvent, globalFocusedWindow2);
                        }
                        else {
                            this.restoreFocus(focusEvent, containingWindow);
                        }
                        KeyboardFocusManager.setMostRecentFocusOwner(containingWindow, null);
                        break;
                    }
                    break;
                }
                else {
                    this.setGlobalFocusOwner(component2);
                    if (component2 == this.getGlobalFocusOwner()) {
                        if (!focusEvent.isTemporary()) {
                            this.setGlobalPermanentFocusOwner(component2);
                            if (component2 != this.getGlobalPermanentFocusOwner()) {
                                this.dequeueKeyEvents(-1L, component2);
                                if (KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                                    this.restoreFocus(focusEvent, containingWindow);
                                    break;
                                }
                                break;
                            }
                        }
                        this.setNativeFocusOwner(KeyboardFocusManager.getHeavyweight(component2));
                        final Component component3 = this.realOppositeComponentWR.get();
                        if (component3 != null && component3 != focusEvent.getOppositeComponent()) {
                            focusEvent = new CausedFocusEvent(component2, 1004, focusEvent.isTemporary(), component3, cause);
                            focusEvent.isPosted = true;
                        }
                        return this.typeAheadAssertions(component2, focusEvent);
                    }
                    this.dequeueKeyEvents(-1L, component2);
                    if (KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                        this.restoreFocus(focusEvent, containingWindow);
                        break;
                    }
                    break;
                }
                break;
            }
            case 1005: {
                final FocusEvent focusEvent2 = (FocusEvent)awtEvent;
                final Component globalFocusOwner2 = this.getGlobalFocusOwner();
                if (globalFocusOwner2 == null) {
                    if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINE)) {
                        DefaultKeyboardFocusManager.focusLog.fine("Skipping {0} because focus owner is null", awtEvent);
                        break;
                    }
                    break;
                }
                else if (globalFocusOwner2 == focusEvent2.getOppositeComponent()) {
                    if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINE)) {
                        DefaultKeyboardFocusManager.focusLog.fine("Skipping {0} because current focus owner is equal to opposite", awtEvent);
                        break;
                    }
                    break;
                }
                else {
                    this.setGlobalFocusOwner(null);
                    if (this.getGlobalFocusOwner() != null) {
                        this.restoreFocus(globalFocusOwner2, true);
                        break;
                    }
                    if (!focusEvent2.isTemporary()) {
                        this.setGlobalPermanentFocusOwner(null);
                        if (this.getGlobalPermanentFocusOwner() != null) {
                            this.restoreFocus(globalFocusOwner2, true);
                            break;
                        }
                    }
                    else {
                        final Window containingWindow2 = globalFocusOwner2.getContainingWindow();
                        if (containingWindow2 != null) {
                            containingWindow2.setTemporaryLostComponent(globalFocusOwner2);
                        }
                    }
                    this.setNativeFocusOwner(null);
                    focusEvent2.setSource(globalFocusOwner2);
                    this.realOppositeComponentWR = ((focusEvent2.getOppositeComponent() != null) ? new WeakReference<Component>(globalFocusOwner2) : DefaultKeyboardFocusManager.NULL_COMPONENT_WR);
                    return this.typeAheadAssertions(globalFocusOwner2, focusEvent2);
                }
                break;
            }
            case 206: {
                final WindowEvent windowEvent2 = (WindowEvent)awtEvent;
                final Window globalActiveWindow3 = this.getGlobalActiveWindow();
                if (globalActiveWindow3 == null) {
                    break;
                }
                if (globalActiveWindow3 != awtEvent.getSource()) {
                    break;
                }
                this.setGlobalActiveWindow(null);
                if (this.getGlobalActiveWindow() != null) {
                    break;
                }
                windowEvent2.setSource(globalActiveWindow3);
                return this.typeAheadAssertions(globalActiveWindow3, windowEvent2);
            }
            case 208: {
                if (this.repostIfFollowsKeyEvents((WindowEvent)awtEvent)) {
                    break;
                }
                final WindowEvent windowEvent3 = (WindowEvent)awtEvent;
                final Window globalFocusedWindow3 = this.getGlobalFocusedWindow();
                final Window window4 = windowEvent3.getWindow();
                final Window globalActiveWindow4 = this.getGlobalActiveWindow();
                final Window oppositeWindow = windowEvent3.getOppositeWindow();
                if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINE)) {
                    DefaultKeyboardFocusManager.focusLog.fine("Active {0}, Current focused {1}, losing focus {2} opposite {3}", globalActiveWindow4, globalFocusedWindow3, window4, oppositeWindow);
                }
                if (globalFocusedWindow3 == null) {
                    break;
                }
                if (this.inSendMessage == 0 && window4 == globalActiveWindow4 && oppositeWindow == globalFocusedWindow3) {
                    break;
                }
                final Component globalFocusOwner3 = this.getGlobalFocusOwner();
                if (globalFocusOwner3 != null) {
                    Component component4 = null;
                    if (oppositeWindow != null) {
                        component4 = oppositeWindow.getTemporaryLostComponent();
                        if (component4 == null) {
                            component4 = oppositeWindow.getMostRecentFocusOwner();
                        }
                    }
                    if (component4 == null) {
                        component4 = oppositeWindow;
                    }
                    sendMessage(globalFocusOwner3, new CausedFocusEvent(globalFocusOwner3, 1005, true, component4, CausedFocusEvent.Cause.ACTIVATION));
                }
                this.setGlobalFocusedWindow(null);
                if (this.getGlobalFocusedWindow() != null) {
                    this.restoreFocus(globalFocusedWindow3, null, true);
                    break;
                }
                windowEvent3.setSource(globalFocusedWindow3);
                this.realOppositeWindowWR = ((oppositeWindow != null) ? new WeakReference<Window>(globalFocusedWindow3) : DefaultKeyboardFocusManager.NULL_WINDOW_WR);
                this.typeAheadAssertions(globalFocusedWindow3, windowEvent3);
                if (oppositeWindow != null || globalActiveWindow4 == null) {
                    break;
                }
                sendMessage(globalActiveWindow4, new WindowEvent(globalActiveWindow4, 206, null));
                if (this.getGlobalActiveWindow() != null) {
                    this.restoreFocus(globalFocusedWindow3, null, true);
                    break;
                }
                break;
            }
            case 400:
            case 401:
            case 402: {
                return this.typeAheadAssertions(null, awtEvent);
            }
            default: {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        final Component component = keyEvent.isPosted ? this.getFocusOwner() : keyEvent.getComponent();
        if (component != null && component.isShowing() && component.canBeFocusOwner() && !keyEvent.isConsumed()) {
            final Component component2 = keyEvent.getComponent();
            if (component2 != null && component2.isEnabled()) {
                this.redispatchEvent(component2, keyEvent);
            }
        }
        boolean postProcessKeyEvent = false;
        final List<KeyEventPostProcessor> keyEventPostProcessors = this.getKeyEventPostProcessors();
        if (keyEventPostProcessors != null) {
            for (Iterator<KeyEventPostProcessor> iterator = keyEventPostProcessors.iterator(); !postProcessKeyEvent && iterator.hasNext(); postProcessKeyEvent = iterator.next().postProcessKeyEvent(keyEvent)) {}
        }
        if (!postProcessKeyEvent) {
            this.postProcessKeyEvent(keyEvent);
        }
        final Component component3 = keyEvent.getComponent();
        ComponentPeer componentPeer = component3.getPeer();
        if (componentPeer == null || componentPeer instanceof LightweightPeer) {
            final Container nativeContainer = component3.getNativeContainer();
            if (nativeContainer != null) {
                componentPeer = nativeContainer.getPeer();
            }
        }
        if (componentPeer != null) {
            componentPeer.handleEvent(keyEvent);
        }
        return true;
    }
    
    @Override
    public boolean postProcessKeyEvent(final KeyEvent keyEvent) {
        if (!keyEvent.isConsumed()) {
            final Component component = keyEvent.getComponent();
            final Container container = (Container)((component instanceof Container) ? component : component.getParent());
            if (container != null) {
                container.postProcessKeyEvent(keyEvent);
            }
        }
        return true;
    }
    
    private void pumpApprovedKeyEvents() {
        KeyEvent keyEvent;
        do {
            keyEvent = null;
            synchronized (this) {
                if (this.enqueuedKeyEvents.size() != 0) {
                    keyEvent = this.enqueuedKeyEvents.getFirst();
                    if (this.typeAheadMarkers.size() != 0 && keyEvent.getWhen() > this.typeAheadMarkers.getFirst().after) {
                        keyEvent = null;
                    }
                    if (keyEvent != null) {
                        if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                            DefaultKeyboardFocusManager.focusLog.finer("Pumping approved event {0}", keyEvent);
                        }
                        this.enqueuedKeyEvents.removeFirst();
                    }
                }
            }
            if (keyEvent != null) {
                this.preDispatchKeyEvent(keyEvent);
            }
        } while (keyEvent != null);
    }
    
    void dumpMarkers() {
        if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            DefaultKeyboardFocusManager.focusLog.finest(">>> Markers dump, time: {0}", System.currentTimeMillis());
            synchronized (this) {
                if (this.typeAheadMarkers.size() != 0) {
                    final Iterator<Object> iterator = this.typeAheadMarkers.iterator();
                    while (iterator.hasNext()) {
                        DefaultKeyboardFocusManager.focusLog.finest("    {0}", iterator.next());
                    }
                }
            }
        }
    }
    
    private boolean typeAheadAssertions(final Component component, final AWTEvent awtEvent) {
        this.pumpApprovedKeyEvents();
        switch (awtEvent.getID()) {
            case 400:
            case 401:
            case 402: {
                final KeyEvent keyEvent = (KeyEvent)awtEvent;
                synchronized (this) {
                    if (awtEvent.isPosted && this.typeAheadMarkers.size() != 0) {
                        final TypeAheadMarker typeAheadMarker = this.typeAheadMarkers.getFirst();
                        if (keyEvent.getWhen() > typeAheadMarker.after) {
                            if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                                DefaultKeyboardFocusManager.focusLog.finer("Storing event {0} because of marker {1}", keyEvent, typeAheadMarker);
                            }
                            this.enqueuedKeyEvents.addLast(keyEvent);
                            return true;
                        }
                    }
                }
                return this.preDispatchKeyEvent(keyEvent);
            }
            case 1004: {
                if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                    DefaultKeyboardFocusManager.focusLog.finest("Markers before FOCUS_GAINED on {0}", component);
                }
                this.dumpMarkers();
                synchronized (this) {
                    boolean b = false;
                    if (this.hasMarker(component)) {
                        final Iterator<Object> iterator = this.typeAheadMarkers.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().untilFocused == component) {
                                b = true;
                            }
                            else if (b) {
                                break;
                            }
                            iterator.remove();
                        }
                    }
                    else if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                        DefaultKeyboardFocusManager.focusLog.finer("Event without marker {0}", awtEvent);
                    }
                }
                DefaultKeyboardFocusManager.focusLog.finest("Markers after FOCUS_GAINED");
                this.dumpMarkers();
                this.redispatchEvent(component, awtEvent);
                this.pumpApprovedKeyEvents();
                return true;
            }
            default: {
                this.redispatchEvent(component, awtEvent);
                return true;
            }
        }
    }
    
    private boolean hasMarker(final Component component) {
        final Iterator<Object> iterator = this.typeAheadMarkers.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().untilFocused == component) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    void clearMarkers() {
        synchronized (this) {
            this.typeAheadMarkers.clear();
        }
    }
    
    private boolean preDispatchKeyEvent(final KeyEvent currentEventAndMostRecentTime) {
        if (currentEventAndMostRecentTime.isPosted) {
            final Component focusOwner = this.getFocusOwner();
            currentEventAndMostRecentTime.setSource((focusOwner != null) ? focusOwner : this.getFocusedWindow());
        }
        if (currentEventAndMostRecentTime.getSource() == null) {
            return true;
        }
        EventQueue.setCurrentEventAndMostRecentTime(currentEventAndMostRecentTime);
        if (KeyboardFocusManager.isProxyActive(currentEventAndMostRecentTime)) {
            final Container nativeContainer = ((Component)currentEventAndMostRecentTime.getSource()).getNativeContainer();
            if (nativeContainer != null) {
                final ComponentPeer peer = nativeContainer.getPeer();
                if (peer != null) {
                    peer.handleEvent(currentEventAndMostRecentTime);
                    currentEventAndMostRecentTime.consume();
                }
            }
            return true;
        }
        final List<KeyEventDispatcher> keyEventDispatchers = this.getKeyEventDispatchers();
        if (keyEventDispatchers != null) {
            final Iterator<KeyEventDispatcher> iterator = keyEventDispatchers.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().dispatchKeyEvent(currentEventAndMostRecentTime)) {
                    return true;
                }
            }
        }
        return this.dispatchKeyEvent(currentEventAndMostRecentTime);
    }
    
    private void consumeNextKeyTyped(final KeyEvent keyEvent) {
        this.consumeNextKeyTyped = true;
    }
    
    private void consumeTraversalKey(final KeyEvent keyEvent) {
        keyEvent.consume();
        this.consumeNextKeyTyped = (keyEvent.getID() == 401 && !keyEvent.isActionKey());
    }
    
    private boolean consumeProcessedKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.getID() == 400 && this.consumeNextKeyTyped) {
            keyEvent.consume();
            this.consumeNextKeyTyped = false;
            return true;
        }
        return false;
    }
    
    @Override
    public void processKeyEvent(final Component component, final KeyEvent keyEvent) {
        if (this.consumeProcessedKeyEvent(keyEvent)) {
            return;
        }
        if (keyEvent.getID() == 400) {
            return;
        }
        if (component.getFocusTraversalKeysEnabled() && !keyEvent.isConsumed()) {
            final AWTKeyStroke awtKeyStrokeForEvent = AWTKeyStroke.getAWTKeyStrokeForEvent(keyEvent);
            final AWTKeyStroke awtKeyStroke = AWTKeyStroke.getAWTKeyStroke(awtKeyStrokeForEvent.getKeyCode(), awtKeyStrokeForEvent.getModifiers(), !awtKeyStrokeForEvent.isOnKeyRelease());
            final Set<AWTKeyStroke> focusTraversalKeys = component.getFocusTraversalKeys(0);
            final boolean contains = focusTraversalKeys.contains(awtKeyStrokeForEvent);
            final boolean contains2 = focusTraversalKeys.contains(awtKeyStroke);
            if (contains || contains2) {
                this.consumeTraversalKey(keyEvent);
                if (contains) {
                    this.focusNextComponent(component);
                }
                return;
            }
            if (keyEvent.getID() == 401) {
                this.consumeNextKeyTyped = false;
            }
            final Set<AWTKeyStroke> focusTraversalKeys2 = component.getFocusTraversalKeys(1);
            final boolean contains3 = focusTraversalKeys2.contains(awtKeyStrokeForEvent);
            final boolean contains4 = focusTraversalKeys2.contains(awtKeyStroke);
            if (contains3 || contains4) {
                this.consumeTraversalKey(keyEvent);
                if (contains3) {
                    this.focusPreviousComponent(component);
                }
                return;
            }
            final Set<AWTKeyStroke> focusTraversalKeys3 = component.getFocusTraversalKeys(2);
            final boolean contains5 = focusTraversalKeys3.contains(awtKeyStrokeForEvent);
            final boolean contains6 = focusTraversalKeys3.contains(awtKeyStroke);
            if (contains5 || contains6) {
                this.consumeTraversalKey(keyEvent);
                if (contains5) {
                    this.upFocusCycle(component);
                }
                return;
            }
            if (!(component instanceof Container) || !((Container)component).isFocusCycleRoot()) {
                return;
            }
            final Set<AWTKeyStroke> focusTraversalKeys4 = component.getFocusTraversalKeys(3);
            final boolean contains7 = focusTraversalKeys4.contains(awtKeyStrokeForEvent);
            final boolean contains8 = focusTraversalKeys4.contains(awtKeyStroke);
            if (contains7 || contains8) {
                this.consumeTraversalKey(keyEvent);
                if (contains7) {
                    this.downFocusCycle((Container)component);
                }
            }
        }
    }
    
    @Override
    protected synchronized void enqueueKeyEvents(final long n, final Component component) {
        if (component == null) {
            return;
        }
        if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            DefaultKeyboardFocusManager.focusLog.finer("Enqueue at {0} for {1}", n, component);
        }
        int n2 = 0;
        int i = this.typeAheadMarkers.size();
        final ListIterator<TypeAheadMarker> listIterator = this.typeAheadMarkers.listIterator(i);
        while (i > 0) {
            if (listIterator.previous().after <= n) {
                n2 = i;
                break;
            }
            --i;
        }
        this.typeAheadMarkers.add(n2, new TypeAheadMarker(n, component));
    }
    
    @Override
    protected synchronized void dequeueKeyEvents(final long n, final Component component) {
        if (component == null) {
            return;
        }
        if (DefaultKeyboardFocusManager.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            DefaultKeyboardFocusManager.focusLog.finer("Dequeue at {0} for {1}", n, component);
        }
        final ListIterator<TypeAheadMarker> listIterator = this.typeAheadMarkers.listIterator((n >= 0L) ? this.typeAheadMarkers.size() : 0);
        if (n < 0L) {
            while (listIterator.hasNext()) {
                if (listIterator.next().untilFocused == component) {
                    listIterator.remove();
                }
            }
        }
        else {
            while (listIterator.hasPrevious()) {
                final TypeAheadMarker typeAheadMarker = listIterator.previous();
                if (typeAheadMarker.untilFocused == component && typeAheadMarker.after == n) {
                    listIterator.remove();
                }
            }
        }
    }
    
    @Override
    protected synchronized void discardKeyEvents(final Component component) {
        if (component == null) {
            return;
        }
        long after = -1L;
        final Iterator<Object> iterator = this.typeAheadMarkers.iterator();
        while (iterator.hasNext()) {
            final TypeAheadMarker typeAheadMarker = iterator.next();
            Component component2;
            boolean b;
            for (component2 = typeAheadMarker.untilFocused, b = (component2 == component); !b && component2 != null && !(component2 instanceof Window); component2 = component2.getParent(), b = (component2 == component)) {}
            if (b) {
                if (after < 0L) {
                    after = typeAheadMarker.after;
                }
                iterator.remove();
            }
            else {
                if (after < 0L) {
                    continue;
                }
                this.purgeStampedEvents(after, typeAheadMarker.after);
                after = -1L;
            }
        }
        this.purgeStampedEvents(after, -1L);
    }
    
    private void purgeStampedEvents(final long n, final long n2) {
        if (n < 0L) {
            return;
        }
        final Iterator<Object> iterator = this.enqueuedKeyEvents.iterator();
        while (iterator.hasNext()) {
            final long when = iterator.next().getWhen();
            if (n < when && (n2 < 0L || when <= n2)) {
                iterator.remove();
            }
            if (n2 >= 0L && when > n2) {
                break;
            }
        }
    }
    
    @Override
    public void focusPreviousComponent(final Component component) {
        if (component != null) {
            component.transferFocusBackward();
        }
    }
    
    @Override
    public void focusNextComponent(final Component component) {
        if (component != null) {
            component.transferFocus();
        }
    }
    
    @Override
    public void upFocusCycle(final Component component) {
        if (component != null) {
            component.transferFocusUpCycle();
        }
    }
    
    @Override
    public void downFocusCycle(final Container container) {
        if (container != null && container.isFocusCycleRoot()) {
            container.transferFocusDownCycle();
        }
    }
    
    static {
        focusLog = PlatformLogger.getLogger("java.awt.focus.DefaultKeyboardFocusManager");
        NULL_WINDOW_WR = new WeakReference<Window>(null);
        NULL_COMPONENT_WR = new WeakReference<Component>(null);
        AWTAccessor.setDefaultKeyboardFocusManagerAccessor(new AWTAccessor.DefaultKeyboardFocusManagerAccessor() {
            @Override
            public void consumeNextKeyTyped(final DefaultKeyboardFocusManager defaultKeyboardFocusManager, final KeyEvent keyEvent) {
                defaultKeyboardFocusManager.consumeNextKeyTyped(keyEvent);
            }
        });
    }
    
    private static class TypeAheadMarker
    {
        long after;
        Component untilFocused;
        
        TypeAheadMarker(final long after, final Component untilFocused) {
            this.after = after;
            this.untilFocused = untilFocused;
        }
        
        @Override
        public String toString() {
            return ">>> Marker after " + this.after + " on " + this.untilFocused;
        }
    }
    
    private static class DefaultKeyboardFocusManagerSentEvent extends SentEvent
    {
        private static final long serialVersionUID = -2924743257508701758L;
        
        public DefaultKeyboardFocusManagerSentEvent(final AWTEvent awtEvent, final AppContext appContext) {
            super(awtEvent, appContext);
        }
        
        @Override
        public final void dispatch() {
            final KeyboardFocusManager currentKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            final DefaultKeyboardFocusManager defaultKeyboardFocusManager = (currentKeyboardFocusManager instanceof DefaultKeyboardFocusManager) ? ((DefaultKeyboardFocusManager)currentKeyboardFocusManager) : null;
            if (defaultKeyboardFocusManager != null) {
                synchronized (defaultKeyboardFocusManager) {
                    defaultKeyboardFocusManager.inSendMessage++;
                }
            }
            super.dispatch();
            if (defaultKeyboardFocusManager != null) {
                synchronized (defaultKeyboardFocusManager) {
                    defaultKeyboardFocusManager.inSendMessage--;
                }
            }
        }
    }
}
