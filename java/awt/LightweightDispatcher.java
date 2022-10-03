package java.awt;

import java.awt.event.MouseWheelEvent;
import sun.awt.SunToolkit;
import sun.awt.AppContext;
import sun.awt.AWTAccessor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import sun.awt.dnd.SunDropTargetEvent;
import java.lang.ref.WeakReference;
import sun.util.logging.PlatformLogger;
import java.awt.event.AWTEventListener;
import java.io.Serializable;

class LightweightDispatcher implements Serializable, AWTEventListener
{
    private static final long serialVersionUID = 5184291520170872969L;
    private static final int LWD_MOUSE_DRAGGED_OVER = 1500;
    private static final PlatformLogger eventLog;
    private static final int BUTTONS_DOWN_MASK;
    private Container nativeContainer;
    private Component focus;
    private transient WeakReference<Component> mouseEventTarget;
    private transient WeakReference<Component> targetLastEntered;
    private transient WeakReference<Component> targetLastEnteredDT;
    private transient boolean isMouseInNativeContainer;
    private transient boolean isMouseDTInNativeContainer;
    private Cursor nativeCursor;
    private long eventMask;
    private static final long PROXY_EVENT_MASK = 131132L;
    private static final long MOUSE_MASK = 131120L;
    
    LightweightDispatcher(final Container nativeContainer) {
        this.isMouseInNativeContainer = false;
        this.isMouseDTInNativeContainer = false;
        this.nativeContainer = nativeContainer;
        this.mouseEventTarget = new WeakReference<Component>(null);
        this.targetLastEntered = new WeakReference<Component>(null);
        this.targetLastEnteredDT = new WeakReference<Component>(null);
        this.eventMask = 0L;
    }
    
    void dispose() {
        this.stopListeningForOtherDrags();
        this.mouseEventTarget.clear();
        this.targetLastEntered.clear();
        this.targetLastEnteredDT.clear();
    }
    
    void enableEvents(final long n) {
        this.eventMask |= n;
    }
    
    boolean dispatchEvent(final AWTEvent awtEvent) {
        boolean b = false;
        if (awtEvent instanceof SunDropTargetEvent) {
            b = this.processDropTargetEvent((SunDropTargetEvent)awtEvent);
        }
        else {
            if (awtEvent instanceof MouseEvent && (this.eventMask & 0x20030L) != 0x0L) {
                b = this.processMouseEvent((MouseEvent)awtEvent);
            }
            if (awtEvent.getID() == 503) {
                this.nativeContainer.updateCursorImmediately();
            }
        }
        return b;
    }
    
    private boolean isMouseGrab(final MouseEvent mouseEvent) {
        int modifiersEx = mouseEvent.getModifiersEx();
        if (mouseEvent.getID() == 501 || mouseEvent.getID() == 502) {
            modifiersEx ^= InputEvent.getMaskForButton(mouseEvent.getButton());
        }
        return (modifiersEx & LightweightDispatcher.BUTTONS_DOWN_MASK) != 0x0;
    }
    
    private boolean processMouseEvent(final MouseEvent mouseEvent) {
        final int id = mouseEvent.getID();
        final Component mouseEventTarget = this.nativeContainer.getMouseEventTarget(mouseEvent.getX(), mouseEvent.getY(), true);
        this.trackMouseEnterExit(mouseEventTarget, mouseEvent);
        Component component = this.mouseEventTarget.get();
        if (!this.isMouseGrab(mouseEvent) && id != 500) {
            component = ((mouseEventTarget != this.nativeContainer) ? mouseEventTarget : null);
            this.mouseEventTarget = new WeakReference<Component>(component);
        }
        if (component != null) {
            switch (id) {
                case 501: {
                    this.retargetMouseEvent(component, id, mouseEvent);
                    break;
                }
                case 502: {
                    this.retargetMouseEvent(component, id, mouseEvent);
                    break;
                }
                case 500: {
                    if (mouseEventTarget == component) {
                        this.retargetMouseEvent(mouseEventTarget, id, mouseEvent);
                        break;
                    }
                    break;
                }
                case 503: {
                    this.retargetMouseEvent(component, id, mouseEvent);
                    break;
                }
                case 506: {
                    if (this.isMouseGrab(mouseEvent)) {
                        this.retargetMouseEvent(component, id, mouseEvent);
                        break;
                    }
                    break;
                }
                case 507: {
                    if (LightweightDispatcher.eventLog.isLoggable(PlatformLogger.Level.FINEST) && mouseEventTarget != null) {
                        LightweightDispatcher.eventLog.finest("retargeting mouse wheel to " + mouseEventTarget.getName() + ", " + ((Container)mouseEventTarget).getClass());
                    }
                    this.retargetMouseEvent(mouseEventTarget, id, mouseEvent);
                    break;
                }
            }
            if (id != 507) {
                mouseEvent.consume();
            }
        }
        return mouseEvent.isConsumed();
    }
    
    private boolean processDropTargetEvent(final SunDropTargetEvent sunDropTargetEvent) {
        final int id = sunDropTargetEvent.getID();
        int x = sunDropTargetEvent.getX();
        int y = sunDropTargetEvent.getY();
        if (!this.nativeContainer.contains(x, y)) {
            final Dimension size = this.nativeContainer.getSize();
            if (size.width <= x) {
                x = size.width - 1;
            }
            else if (x < 0) {
                x = 0;
            }
            if (size.height <= y) {
                y = size.height - 1;
            }
            else if (y < 0) {
                y = 0;
            }
        }
        final Component dropTargetEventTarget = this.nativeContainer.getDropTargetEventTarget(x, y, true);
        this.trackMouseEnterExit(dropTargetEventTarget, sunDropTargetEvent);
        if (dropTargetEventTarget != this.nativeContainer && dropTargetEventTarget != null) {
            switch (id) {
                case 504:
                case 505: {
                    break;
                }
                default: {
                    this.retargetMouseEvent(dropTargetEventTarget, id, sunDropTargetEvent);
                    sunDropTargetEvent.consume();
                    break;
                }
            }
        }
        return sunDropTargetEvent.isConsumed();
    }
    
    private void trackDropTargetEnterExit(final Component component, final MouseEvent mouseEvent) {
        final int id = mouseEvent.getID();
        if (id == 504 && this.isMouseDTInNativeContainer) {
            this.targetLastEnteredDT.clear();
        }
        else if (id == 504) {
            this.isMouseDTInNativeContainer = true;
        }
        else if (id == 505) {
            this.isMouseDTInNativeContainer = false;
        }
        this.targetLastEnteredDT = new WeakReference<Component>(this.retargetMouseEnterExit(component, mouseEvent, this.targetLastEnteredDT.get(), this.isMouseDTInNativeContainer));
    }
    
    private void trackMouseEnterExit(final Component component, final MouseEvent mouseEvent) {
        if (mouseEvent instanceof SunDropTargetEvent) {
            this.trackDropTargetEnterExit(component, mouseEvent);
            return;
        }
        final int id = mouseEvent.getID();
        if (id != 505 && id != 506 && id != 1500 && !this.isMouseInNativeContainer) {
            this.isMouseInNativeContainer = true;
            this.startListeningForOtherDrags();
        }
        else if (id == 505) {
            this.isMouseInNativeContainer = false;
            this.stopListeningForOtherDrags();
        }
        this.targetLastEntered = new WeakReference<Component>(this.retargetMouseEnterExit(component, mouseEvent, this.targetLastEntered.get(), this.isMouseInNativeContainer));
    }
    
    private Component retargetMouseEnterExit(final Component component, final MouseEvent mouseEvent, final Component component2, final boolean b) {
        final int id = mouseEvent.getID();
        final Component component3 = b ? component : null;
        if (component2 != component3) {
            if (component2 != null) {
                this.retargetMouseEvent(component2, 505, mouseEvent);
            }
            if (id == 505) {
                mouseEvent.consume();
            }
            if (component3 != null) {
                this.retargetMouseEvent(component3, 504, mouseEvent);
            }
            if (id == 504) {
                mouseEvent.consume();
            }
        }
        return component3;
    }
    
    private void startListeningForOtherDrags() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                LightweightDispatcher.this.nativeContainer.getToolkit().addAWTEventListener(LightweightDispatcher.this, 48L);
                return null;
            }
        });
    }
    
    private void stopListeningForOtherDrags() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                LightweightDispatcher.this.nativeContainer.getToolkit().removeAWTEventListener(LightweightDispatcher.this);
                return null;
            }
        });
    }
    
    @Override
    public void eventDispatched(final AWTEvent awtEvent) {
        if (!(awtEvent instanceof MouseEvent) || awtEvent instanceof SunDropTargetEvent || awtEvent.id != 506 || awtEvent.getSource() == this.nativeContainer) {
            return;
        }
        final MouseEvent mouseEvent = (MouseEvent)awtEvent;
        final MouseEvent mouseEvent2;
        synchronized (this.nativeContainer.getTreeLock()) {
            final Component component = mouseEvent.getComponent();
            if (!component.isShowing()) {
                return;
            }
            Container container;
            for (container = this.nativeContainer; container != null && !(container instanceof Window); container = container.getParent_NoClientCode()) {}
            if (container == null || ((Window)container).isModalBlocked()) {
                return;
            }
            mouseEvent2 = new MouseEvent(this.nativeContainer, 1500, mouseEvent.getWhen(), mouseEvent.getModifiersEx() | mouseEvent.getModifiers(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), mouseEvent.getButton());
            final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
            mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
            mouseEvent.copyPrivateDataInto(mouseEvent2);
            final Point locationOnScreen = component.getLocationOnScreen();
            if (AppContext.getAppContext() != this.nativeContainer.appContext) {
                SunToolkit.executeOnEventHandlerThread(this.nativeContainer, new Runnable() {
                    @Override
                    public void run() {
                        if (!LightweightDispatcher.this.nativeContainer.isShowing()) {
                            return;
                        }
                        final Point locationOnScreen = LightweightDispatcher.this.nativeContainer.getLocationOnScreen();
                        mouseEvent2.translatePoint(locationOnScreen.x - locationOnScreen.x, locationOnScreen.y - locationOnScreen.y);
                        LightweightDispatcher.this.trackMouseEnterExit(LightweightDispatcher.this.nativeContainer.getMouseEventTarget(mouseEvent2.getX(), mouseEvent2.getY(), true), mouseEvent2);
                    }
                });
                return;
            }
            if (!this.nativeContainer.isShowing()) {
                return;
            }
            final Point locationOnScreen2 = this.nativeContainer.getLocationOnScreen();
            mouseEvent2.translatePoint(locationOnScreen.x - locationOnScreen2.x, locationOnScreen.y - locationOnScreen2.y);
        }
        this.trackMouseEnterExit(this.nativeContainer.getMouseEventTarget(mouseEvent2.getX(), mouseEvent2.getY(), true), mouseEvent2);
    }
    
    void retargetMouseEvent(final Component component, final int n, final MouseEvent mouseEvent) {
        if (component == null) {
            return;
        }
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        Component parent;
        for (parent = component; parent != null && parent != this.nativeContainer; parent = parent.getParent()) {
            x -= parent.x;
            y -= parent.y;
        }
        if (parent != null) {
            MouseEvent mouseEvent2;
            if (mouseEvent instanceof SunDropTargetEvent) {
                mouseEvent2 = new SunDropTargetEvent(component, n, x, y, ((SunDropTargetEvent)mouseEvent).getDispatcher());
            }
            else if (n == 507) {
                mouseEvent2 = new MouseWheelEvent(component, n, mouseEvent.getWhen(), mouseEvent.getModifiersEx() | mouseEvent.getModifiers(), x, y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), ((MouseWheelEvent)mouseEvent).getScrollType(), ((MouseWheelEvent)mouseEvent).getScrollAmount(), ((MouseWheelEvent)mouseEvent).getWheelRotation(), ((MouseWheelEvent)mouseEvent).getPreciseWheelRotation());
            }
            else {
                mouseEvent2 = new MouseEvent(component, n, mouseEvent.getWhen(), mouseEvent.getModifiersEx() | mouseEvent.getModifiers(), x, y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), mouseEvent.getButton());
                final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
                mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
            }
            mouseEvent.copyPrivateDataInto(mouseEvent2);
            if (component == this.nativeContainer) {
                ((Container)component).dispatchEventToSelf(mouseEvent2);
            }
            else {
                assert AppContext.getAppContext() == component.appContext;
                if (this.nativeContainer.modalComp != null) {
                    if (((Container)this.nativeContainer.modalComp).isAncestorOf(component)) {
                        component.dispatchEvent(mouseEvent2);
                    }
                    else {
                        mouseEvent.consume();
                    }
                }
                else {
                    component.dispatchEvent(mouseEvent2);
                }
            }
            if (n == 507 && mouseEvent2.isConsumed()) {
                mouseEvent.consume();
            }
        }
    }
    
    static {
        eventLog = PlatformLogger.getLogger("java.awt.event.LightweightDispatcher");
        final int[] buttonDownMasks = AWTAccessor.getInputEventAccessor().getButtonDownMasks();
        int buttons_DOWN_MASK = 0;
        final int[] array = buttonDownMasks;
        for (int length = array.length, i = 0; i < length; ++i) {
            buttons_DOWN_MASK |= array[i];
        }
        BUTTONS_DOWN_MASK = buttons_DOWN_MASK;
    }
}
