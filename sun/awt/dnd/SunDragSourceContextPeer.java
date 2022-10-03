package sun.awt.dnd;

import java.awt.event.MouseEvent;
import java.awt.AWTEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceDragEvent;
import sun.awt.SunToolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.EventQueue;
import java.util.Map;
import java.util.SortedMap;
import sun.awt.datatransfer.DataTransferer;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.DragSourceContext;
import java.awt.Point;
import java.awt.Image;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.peer.DragSourceContextPeer;

public abstract class SunDragSourceContextPeer implements DragSourceContextPeer
{
    private DragGestureEvent trigger;
    private Component component;
    private Cursor cursor;
    private Image dragImage;
    private Point dragImageOffset;
    private long nativeCtxt;
    private DragSourceContext dragSourceContext;
    private int sourceActions;
    private static boolean dragDropInProgress;
    private static boolean discardingMouseEvents;
    protected static final int DISPATCH_ENTER = 1;
    protected static final int DISPATCH_MOTION = 2;
    protected static final int DISPATCH_CHANGED = 3;
    protected static final int DISPATCH_EXIT = 4;
    protected static final int DISPATCH_FINISH = 5;
    protected static final int DISPATCH_MOUSE_MOVED = 6;
    
    public SunDragSourceContextPeer(final DragGestureEvent trigger) {
        this.trigger = trigger;
        if (this.trigger != null) {
            this.component = this.trigger.getComponent();
        }
        else {
            this.component = null;
        }
    }
    
    public void startSecondaryEventLoop() {
    }
    
    public void quitSecondaryEventLoop() {
    }
    
    @Override
    public void startDrag(final DragSourceContext dragSourceContext, final Cursor cursor, final Image dragImage, final Point dragImageOffset) throws InvalidDnDOperationException {
        if (this.getTrigger().getTriggerEvent() == null) {
            throw new InvalidDnDOperationException("DragGestureEvent has a null trigger");
        }
        this.dragSourceContext = dragSourceContext;
        this.cursor = cursor;
        this.sourceActions = this.getDragSourceContext().getSourceActions();
        this.dragImage = dragImage;
        this.dragImageOffset = dragImageOffset;
        final Transferable transferable = this.getDragSourceContext().getTransferable();
        final SortedMap<Long, DataFlavor> formatsForTransferable = DataTransferer.getInstance().getFormatsForTransferable(transferable, DataTransferer.adaptFlavorMap(this.getTrigger().getDragSource().getFlavorMap()));
        DataTransferer.getInstance();
        this.startDrag(transferable, DataTransferer.keysToLongArray(formatsForTransferable), formatsForTransferable);
        SunDragSourceContextPeer.discardingMouseEvents = true;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SunDragSourceContextPeer.discardingMouseEvents = false;
            }
        });
    }
    
    protected abstract void startDrag(final Transferable p0, final long[] p1, final Map p2);
    
    @Override
    public void setCursor(final Cursor cursor) throws InvalidDnDOperationException {
        synchronized (this) {
            if (this.cursor == null || !this.cursor.equals(cursor)) {
                this.cursor = cursor;
                this.setNativeCursor(this.getNativeContext(), cursor, (cursor != null) ? cursor.getType() : 0);
            }
        }
    }
    
    @Override
    public Cursor getCursor() {
        return this.cursor;
    }
    
    public Image getDragImage() {
        return this.dragImage;
    }
    
    public Point getDragImageOffset() {
        if (this.dragImageOffset == null) {
            return new Point(0, 0);
        }
        return new Point(this.dragImageOffset);
    }
    
    protected abstract void setNativeCursor(final long p0, final Cursor p1, final int p2);
    
    protected synchronized void setTrigger(final DragGestureEvent trigger) {
        this.trigger = trigger;
        if (this.trigger != null) {
            this.component = this.trigger.getComponent();
        }
        else {
            this.component = null;
        }
    }
    
    protected DragGestureEvent getTrigger() {
        return this.trigger;
    }
    
    protected Component getComponent() {
        return this.component;
    }
    
    protected synchronized void setNativeContext(final long nativeCtxt) {
        this.nativeCtxt = nativeCtxt;
    }
    
    protected synchronized long getNativeContext() {
        return this.nativeCtxt;
    }
    
    protected DragSourceContext getDragSourceContext() {
        return this.dragSourceContext;
    }
    
    @Override
    public void transferablesFlavorsChanged() {
    }
    
    protected final void postDragSourceDragEvent(final int n, final int n2, final int n3, final int n4, final int n5) {
        SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(this.getComponent()), new EventDispatcher(n5, new DragSourceDragEvent(this.getDragSourceContext(), convertModifiersToDropAction(n2, this.sourceActions), n & this.sourceActions, n2, n3, n4)));
        this.startSecondaryEventLoop();
    }
    
    protected void dragEnter(final int n, final int n2, final int n3, final int n4) {
        this.postDragSourceDragEvent(n, n2, n3, n4, 1);
    }
    
    private void dragMotion(final int n, final int n2, final int n3, final int n4) {
        this.postDragSourceDragEvent(n, n2, n3, n4, 2);
    }
    
    private void operationChanged(final int n, final int n2, final int n3, final int n4) {
        this.postDragSourceDragEvent(n, n2, n3, n4, 3);
    }
    
    protected final void dragExit(final int n, final int n2) {
        SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(this.getComponent()), new EventDispatcher(4, new DragSourceEvent(this.getDragSourceContext(), n, n2)));
        this.startSecondaryEventLoop();
    }
    
    private void dragMouseMoved(final int n, final int n2, final int n3, final int n4) {
        this.postDragSourceDragEvent(n, n2, n3, n4, 6);
    }
    
    protected final void dragDropFinished(final boolean b, final int n, final int n2, final int n3) {
        SunToolkit.invokeLaterOnAppContext(SunToolkit.targetToAppContext(this.getComponent()), new EventDispatcher(5, new DragSourceDropEvent(this.getDragSourceContext(), n & this.sourceActions, b, n2, n3)));
        this.startSecondaryEventLoop();
        this.setNativeContext(0L);
        this.dragImage = null;
        this.dragImageOffset = null;
    }
    
    public static void setDragDropInProgress(final boolean dragDropInProgress) throws InvalidDnDOperationException {
        synchronized (SunDragSourceContextPeer.class) {
            if (SunDragSourceContextPeer.dragDropInProgress == dragDropInProgress) {
                throw new InvalidDnDOperationException(getExceptionMessage(dragDropInProgress));
            }
            SunDragSourceContextPeer.dragDropInProgress = dragDropInProgress;
        }
    }
    
    public static boolean checkEvent(final AWTEvent awtEvent) {
        return !SunDragSourceContextPeer.discardingMouseEvents || !(awtEvent instanceof MouseEvent) || ((MouseEvent)awtEvent) instanceof SunDropTargetEvent;
    }
    
    public static void checkDragDropInProgress() throws InvalidDnDOperationException {
        if (SunDragSourceContextPeer.dragDropInProgress) {
            throw new InvalidDnDOperationException(getExceptionMessage(true));
        }
    }
    
    private static String getExceptionMessage(final boolean b) {
        return b ? "Drag and drop in progress" : "No drag in progress";
    }
    
    public static int convertModifiersToDropAction(final int n, final int n2) {
        int n3 = 0;
        switch (n & 0xC0) {
            case 192: {
                n3 = 1073741824;
                break;
            }
            case 128: {
                n3 = 1;
                break;
            }
            case 64: {
                n3 = 2;
                break;
            }
            default: {
                if ((n2 & 0x2) != 0x0) {
                    n3 = 2;
                    break;
                }
                if ((n2 & 0x1) != 0x0) {
                    n3 = 1;
                    break;
                }
                if ((n2 & 0x40000000) != 0x0) {
                    n3 = 1073741824;
                    break;
                }
                break;
            }
        }
        return n3 & n2;
    }
    
    private void cleanup() {
        this.trigger = null;
        this.component = null;
        this.cursor = null;
        this.dragSourceContext = null;
        SunDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(null);
        setDragDropInProgress(false);
    }
    
    static {
        SunDragSourceContextPeer.dragDropInProgress = false;
        SunDragSourceContextPeer.discardingMouseEvents = false;
    }
    
    private class EventDispatcher implements Runnable
    {
        private final int dispatchType;
        private final DragSourceEvent event;
        
        EventDispatcher(final int dispatchType, final DragSourceEvent event) {
            switch (dispatchType) {
                case 1:
                case 2:
                case 3:
                case 6: {
                    if (!(event instanceof DragSourceDragEvent)) {
                        throw new IllegalArgumentException("Event: " + event);
                    }
                    break;
                }
                case 4: {
                    break;
                }
                case 5: {
                    if (!(event instanceof DragSourceDropEvent)) {
                        throw new IllegalArgumentException("Event: " + event);
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Dispatch type: " + dispatchType);
                }
            }
            this.dispatchType = dispatchType;
            this.event = event;
        }
        
        @Override
        public void run() {
            final DragSourceContext dragSourceContext = SunDragSourceContextPeer.this.getDragSourceContext();
            try {
                switch (this.dispatchType) {
                    case 1: {
                        dragSourceContext.dragEnter((DragSourceDragEvent)this.event);
                        break;
                    }
                    case 2: {
                        dragSourceContext.dragOver((DragSourceDragEvent)this.event);
                        break;
                    }
                    case 3: {
                        dragSourceContext.dropActionChanged((DragSourceDragEvent)this.event);
                        break;
                    }
                    case 4: {
                        dragSourceContext.dragExit(this.event);
                        break;
                    }
                    case 6: {
                        dragSourceContext.dragMouseMoved((DragSourceDragEvent)this.event);
                        break;
                    }
                    case 5: {
                        try {
                            dragSourceContext.dragDropEnd((DragSourceDropEvent)this.event);
                        }
                        finally {
                            SunDragSourceContextPeer.this.cleanup();
                        }
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Dispatch type: " + this.dispatchType);
                    }
                }
            }
            finally {
                SunDragSourceContextPeer.this.quitSecondaryEventLoop();
            }
        }
    }
}
