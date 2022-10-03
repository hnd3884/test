package java.awt;

import sun.awt.AWTAccessor;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.security.AccessController;
import java.awt.event.InputEvent;
import java.security.PrivilegedAction;
import java.lang.reflect.Field;
import java.security.AccessControlContext;
import sun.util.logging.PlatformLogger;
import java.util.EventObject;

public abstract class AWTEvent extends EventObject
{
    private static final PlatformLogger log;
    private byte[] bdata;
    protected int id;
    protected boolean consumed;
    private transient volatile AccessControlContext acc;
    transient boolean focusManagerIsDispatching;
    transient boolean isPosted;
    private transient boolean isSystemGenerated;
    public static final long COMPONENT_EVENT_MASK = 1L;
    public static final long CONTAINER_EVENT_MASK = 2L;
    public static final long FOCUS_EVENT_MASK = 4L;
    public static final long KEY_EVENT_MASK = 8L;
    public static final long MOUSE_EVENT_MASK = 16L;
    public static final long MOUSE_MOTION_EVENT_MASK = 32L;
    public static final long WINDOW_EVENT_MASK = 64L;
    public static final long ACTION_EVENT_MASK = 128L;
    public static final long ADJUSTMENT_EVENT_MASK = 256L;
    public static final long ITEM_EVENT_MASK = 512L;
    public static final long TEXT_EVENT_MASK = 1024L;
    public static final long INPUT_METHOD_EVENT_MASK = 2048L;
    static final long INPUT_METHODS_ENABLED_MASK = 4096L;
    public static final long PAINT_EVENT_MASK = 8192L;
    public static final long INVOCATION_EVENT_MASK = 16384L;
    public static final long HIERARCHY_EVENT_MASK = 32768L;
    public static final long HIERARCHY_BOUNDS_EVENT_MASK = 65536L;
    public static final long MOUSE_WHEEL_EVENT_MASK = 131072L;
    public static final long WINDOW_STATE_EVENT_MASK = 262144L;
    public static final long WINDOW_FOCUS_EVENT_MASK = 524288L;
    public static final int RESERVED_ID_MAX = 1999;
    private static Field inputEvent_CanAccessSystemClipboard_Field;
    private static final long serialVersionUID = -1825314779160409405L;
    
    final AccessControlContext getAccessControlContext() {
        if (this.acc == null) {
            throw new SecurityException("AWTEvent is missing AccessControlContext");
        }
        return this.acc;
    }
    
    private static synchronized Field get_InputEvent_CanAccessSystemClipboard() {
        if (AWTEvent.inputEvent_CanAccessSystemClipboard_Field == null) {
            AWTEvent.inputEvent_CanAccessSystemClipboard_Field = AccessController.doPrivileged((PrivilegedAction<Field>)new PrivilegedAction<Field>() {
                @Override
                public Field run() {
                    try {
                        final Field declaredField = InputEvent.class.getDeclaredField("canAccessSystemClipboard");
                        declaredField.setAccessible(true);
                        return declaredField;
                    }
                    catch (final SecurityException ex) {
                        if (AWTEvent.log.isLoggable(PlatformLogger.Level.FINE)) {
                            AWTEvent.log.fine("AWTEvent.get_InputEvent_CanAccessSystemClipboard() got SecurityException ", ex);
                        }
                    }
                    catch (final NoSuchFieldException ex2) {
                        if (AWTEvent.log.isLoggable(PlatformLogger.Level.FINE)) {
                            AWTEvent.log.fine("AWTEvent.get_InputEvent_CanAccessSystemClipboard() got NoSuchFieldException ", ex2);
                        }
                    }
                    return null;
                }
            });
        }
        return AWTEvent.inputEvent_CanAccessSystemClipboard_Field;
    }
    
    private static native void initIDs();
    
    public AWTEvent(final Event event) {
        this(event.target, event.id);
    }
    
    public AWTEvent(final Object o, final int id) {
        super(o);
        this.consumed = false;
        this.acc = AccessController.getContext();
        this.focusManagerIsDispatching = false;
        switch (this.id = id) {
            case 601:
            case 701:
            case 900:
            case 1001: {
                this.consumed = true;
                break;
            }
        }
    }
    
    public void setSource(final Object source) {
        if (this.source == source) {
            return;
        }
        Component parent = null;
        if (source instanceof Component) {
            for (parent = (Component)source; parent != null && parent.peer != null && parent.peer instanceof LightweightPeer; parent = parent.parent) {}
        }
        synchronized (this) {
            this.source = source;
            if (parent != null) {
                final ComponentPeer peer = parent.peer;
                if (peer != null) {
                    this.nativeSetSource(peer);
                }
            }
        }
    }
    
    private native void nativeSetSource(final ComponentPeer p0);
    
    public int getID() {
        return this.id;
    }
    
    @Override
    public String toString() {
        Object o = null;
        if (this.source instanceof Component) {
            o = ((Component)this.source).getName();
        }
        else if (this.source instanceof MenuComponent) {
            o = ((MenuComponent)this.source).getName();
        }
        return this.getClass().getName() + "[" + this.paramString() + "] on " + ((o != null) ? o : this.source);
    }
    
    public String paramString() {
        return "";
    }
    
    protected void consume() {
        switch (this.id) {
            case 401:
            case 402:
            case 501:
            case 502:
            case 503:
            case 504:
            case 505:
            case 506:
            case 507:
            case 1100:
            case 1101: {
                this.consumed = true;
                break;
            }
        }
    }
    
    protected boolean isConsumed() {
        return this.consumed;
    }
    
    Event convertToOld() {
        final Object source = this.getSource();
        int id = this.id;
        switch (this.id) {
            case 401:
            case 402: {
                final KeyEvent keyEvent = (KeyEvent)this;
                if (keyEvent.isActionKey()) {
                    id = ((this.id == 401) ? 403 : 404);
                }
                final int keyCode = keyEvent.getKeyCode();
                if (keyCode == 16 || keyCode == 17 || keyCode == 18) {
                    return null;
                }
                return new Event(source, keyEvent.getWhen(), id, 0, 0, Event.getOldEventKey(keyEvent), keyEvent.getModifiers() & 0xFFFFFFEF);
            }
            case 501:
            case 502:
            case 503:
            case 504:
            case 505:
            case 506: {
                final MouseEvent mouseEvent = (MouseEvent)this;
                final Event event = new Event(source, mouseEvent.getWhen(), id, mouseEvent.getX(), mouseEvent.getY(), 0, mouseEvent.getModifiers() & 0xFFFFFFEF);
                event.clickCount = mouseEvent.getClickCount();
                return event;
            }
            case 1004: {
                return new Event(source, 1004, null);
            }
            case 1005: {
                return new Event(source, 1005, null);
            }
            case 201:
            case 203:
            case 204: {
                return new Event(source, id, null);
            }
            case 100: {
                if (source instanceof Frame || source instanceof Dialog) {
                    final Point location = ((Component)source).getLocation();
                    return new Event(source, 0L, 205, location.x, location.y, 0, 0);
                }
                break;
            }
            case 1001: {
                final ActionEvent actionEvent = (ActionEvent)this;
                String s;
                if (source instanceof Button) {
                    s = ((Button)source).getLabel();
                }
                else if (source instanceof MenuItem) {
                    s = ((MenuItem)source).getLabel();
                }
                else {
                    s = actionEvent.getActionCommand();
                }
                return new Event(source, 0L, id, 0, 0, 0, actionEvent.getModifiers(), s);
            }
            case 701: {
                final ItemEvent itemEvent = (ItemEvent)this;
                int n;
                Object o;
                if (source instanceof List) {
                    n = ((itemEvent.getStateChange() == 1) ? 701 : 702);
                    o = itemEvent.getItem();
                }
                else {
                    n = 1001;
                    if (source instanceof Choice) {
                        o = itemEvent.getItem();
                    }
                    else {
                        o = (itemEvent.getStateChange() == 1);
                    }
                }
                return new Event(source, n, o);
            }
            case 601: {
                final AdjustmentEvent adjustmentEvent = (AdjustmentEvent)this;
                int n2 = 0;
                switch (adjustmentEvent.getAdjustmentType()) {
                    case 1: {
                        n2 = 602;
                        break;
                    }
                    case 2: {
                        n2 = 601;
                        break;
                    }
                    case 4: {
                        n2 = 604;
                        break;
                    }
                    case 3: {
                        n2 = 603;
                        break;
                    }
                    case 5: {
                        if (adjustmentEvent.getValueIsAdjusting()) {
                            n2 = 605;
                            break;
                        }
                        n2 = 607;
                        break;
                    }
                    default: {
                        return null;
                    }
                }
                return new Event(source, n2, adjustmentEvent.getValue());
            }
        }
        return null;
    }
    
    void copyPrivateDataInto(final AWTEvent awtEvent) {
        awtEvent.bdata = this.bdata;
        if (this instanceof InputEvent && awtEvent instanceof InputEvent) {
            final Field get_InputEvent_CanAccessSystemClipboard = get_InputEvent_CanAccessSystemClipboard();
            if (get_InputEvent_CanAccessSystemClipboard != null) {
                try {
                    get_InputEvent_CanAccessSystemClipboard.setBoolean(awtEvent, get_InputEvent_CanAccessSystemClipboard.getBoolean(this));
                }
                catch (final IllegalAccessException ex) {
                    if (AWTEvent.log.isLoggable(PlatformLogger.Level.FINE)) {
                        AWTEvent.log.fine("AWTEvent.copyPrivateDataInto() got IllegalAccessException ", ex);
                    }
                }
            }
        }
        awtEvent.isSystemGenerated = this.isSystemGenerated;
    }
    
    void dispatched() {
        if (this instanceof InputEvent) {
            final Field get_InputEvent_CanAccessSystemClipboard = get_InputEvent_CanAccessSystemClipboard();
            if (get_InputEvent_CanAccessSystemClipboard != null) {
                try {
                    get_InputEvent_CanAccessSystemClipboard.setBoolean(this, false);
                }
                catch (final IllegalAccessException ex) {
                    if (AWTEvent.log.isLoggable(PlatformLogger.Level.FINE)) {
                        AWTEvent.log.fine("AWTEvent.dispatched() got IllegalAccessException ", ex);
                    }
                }
            }
        }
    }
    
    static {
        log = PlatformLogger.getLogger("java.awt.AWTEvent");
        AWTEvent.inputEvent_CanAccessSystemClipboard_Field = null;
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setAWTEventAccessor(new AWTAccessor.AWTEventAccessor() {
            @Override
            public void setPosted(final AWTEvent awtEvent) {
                awtEvent.isPosted = true;
            }
            
            @Override
            public void setSystemGenerated(final AWTEvent awtEvent) {
                awtEvent.isSystemGenerated = true;
            }
            
            @Override
            public boolean isSystemGenerated(final AWTEvent awtEvent) {
                return awtEvent.isSystemGenerated;
            }
            
            @Override
            public AccessControlContext getAccessControlContext(final AWTEvent awtEvent) {
                return awtEvent.getAccessControlContext();
            }
            
            @Override
            public byte[] getBData(final AWTEvent awtEvent) {
                return awtEvent.bdata;
            }
            
            @Override
            public void setBData(final AWTEvent awtEvent, final byte[] array) {
                awtEvent.bdata = array;
            }
        });
    }
}
