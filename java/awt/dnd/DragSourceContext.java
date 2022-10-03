package java.awt.dnd;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.TooManyListenersException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Image;
import java.awt.Cursor;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;

public class DragSourceContext implements DragSourceListener, DragSourceMotionListener, Serializable
{
    private static final long serialVersionUID = -115407898692194719L;
    protected static final int DEFAULT = 0;
    protected static final int ENTER = 1;
    protected static final int OVER = 2;
    protected static final int CHANGED = 3;
    private static Transferable emptyTransferable;
    private transient DragSourceContextPeer peer;
    private DragGestureEvent trigger;
    private Cursor cursor;
    private transient Transferable transferable;
    private transient DragSourceListener listener;
    private boolean useCustomCursor;
    private int sourceActions;
    
    public DragSourceContext(final DragSourceContextPeer peer, final DragGestureEvent trigger, final Cursor cursor, final Image image, final Point point, final Transferable transferable, final DragSourceListener listener) {
        if (peer == null) {
            throw new NullPointerException("DragSourceContextPeer");
        }
        if (trigger == null) {
            throw new NullPointerException("Trigger");
        }
        if (trigger.getDragSource() == null) {
            throw new IllegalArgumentException("DragSource");
        }
        if (trigger.getComponent() == null) {
            throw new IllegalArgumentException("Component");
        }
        if (trigger.getSourceAsDragGestureRecognizer().getSourceActions() == 0) {
            throw new IllegalArgumentException("source actions");
        }
        if (trigger.getDragAction() == 0) {
            throw new IllegalArgumentException("no drag action");
        }
        if (transferable == null) {
            throw new NullPointerException("Transferable");
        }
        if (image != null && point == null) {
            throw new NullPointerException("offset");
        }
        this.peer = peer;
        this.trigger = trigger;
        this.cursor = cursor;
        this.transferable = transferable;
        this.listener = listener;
        this.sourceActions = trigger.getSourceAsDragGestureRecognizer().getSourceActions();
        this.useCustomCursor = (cursor != null);
        this.updateCurrentCursor(trigger.getDragAction(), this.getSourceActions(), 0);
    }
    
    public DragSource getDragSource() {
        return this.trigger.getDragSource();
    }
    
    public Component getComponent() {
        return this.trigger.getComponent();
    }
    
    public DragGestureEvent getTrigger() {
        return this.trigger;
    }
    
    public int getSourceActions() {
        return this.sourceActions;
    }
    
    public synchronized void setCursor(final Cursor cursorImpl) {
        this.useCustomCursor = (cursorImpl != null);
        this.setCursorImpl(cursorImpl);
    }
    
    public Cursor getCursor() {
        return this.cursor;
    }
    
    public synchronized void addDragSourceListener(final DragSourceListener listener) throws TooManyListenersException {
        if (listener == null) {
            return;
        }
        if (this.equals(listener)) {
            throw new IllegalArgumentException("DragSourceContext may not be its own listener");
        }
        if (this.listener != null) {
            throw new TooManyListenersException();
        }
        this.listener = listener;
    }
    
    public synchronized void removeDragSourceListener(final DragSourceListener dragSourceListener) {
        if (this.listener != null && this.listener.equals(dragSourceListener)) {
            this.listener = null;
            return;
        }
        throw new IllegalArgumentException();
    }
    
    public void transferablesFlavorsChanged() {
        if (this.peer != null) {
            this.peer.transferablesFlavorsChanged();
        }
    }
    
    @Override
    public void dragEnter(final DragSourceDragEvent dragSourceDragEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dragEnter(dragSourceDragEvent);
        }
        this.getDragSource().processDragEnter(dragSourceDragEvent);
        this.updateCurrentCursor(this.getSourceActions(), dragSourceDragEvent.getTargetActions(), 1);
    }
    
    @Override
    public void dragOver(final DragSourceDragEvent dragSourceDragEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dragOver(dragSourceDragEvent);
        }
        this.getDragSource().processDragOver(dragSourceDragEvent);
        this.updateCurrentCursor(this.getSourceActions(), dragSourceDragEvent.getTargetActions(), 2);
    }
    
    @Override
    public void dragExit(final DragSourceEvent dragSourceEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dragExit(dragSourceEvent);
        }
        this.getDragSource().processDragExit(dragSourceEvent);
        this.updateCurrentCursor(0, 0, 0);
    }
    
    @Override
    public void dropActionChanged(final DragSourceDragEvent dragSourceDragEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dropActionChanged(dragSourceDragEvent);
        }
        this.getDragSource().processDropActionChanged(dragSourceDragEvent);
        this.updateCurrentCursor(this.getSourceActions(), dragSourceDragEvent.getTargetActions(), 3);
    }
    
    @Override
    public void dragDropEnd(final DragSourceDropEvent dragSourceDropEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dragDropEnd(dragSourceDropEvent);
        }
        this.getDragSource().processDragDropEnd(dragSourceDropEvent);
    }
    
    @Override
    public void dragMouseMoved(final DragSourceDragEvent dragSourceDragEvent) {
        this.getDragSource().processDragMouseMoved(dragSourceDragEvent);
    }
    
    public Transferable getTransferable() {
        return this.transferable;
    }
    
    protected synchronized void updateCurrentCursor(final int n, int n2, final int n3) {
        if (this.useCustomCursor) {
            return;
        }
        switch (n3) {
            default: {
                n2 = 0;
            }
            case 1:
            case 2:
            case 3: {
                final int n4 = n & n2;
                Cursor cursorImpl;
                if (n4 == 0) {
                    if ((n & 0x40000000) == 0x40000000) {
                        cursorImpl = DragSource.DefaultLinkNoDrop;
                    }
                    else if ((n & 0x2) == 0x2) {
                        cursorImpl = DragSource.DefaultMoveNoDrop;
                    }
                    else {
                        cursorImpl = DragSource.DefaultCopyNoDrop;
                    }
                }
                else if ((n4 & 0x40000000) == 0x40000000) {
                    cursorImpl = DragSource.DefaultLinkDrop;
                }
                else if ((n4 & 0x2) == 0x2) {
                    cursorImpl = DragSource.DefaultMoveDrop;
                }
                else {
                    cursorImpl = DragSource.DefaultCopyDrop;
                }
                this.setCursorImpl(cursorImpl);
            }
        }
    }
    
    private void setCursorImpl(final Cursor cursor) {
        if (this.cursor == null || !this.cursor.equals(cursor)) {
            this.cursor = cursor;
            if (this.peer != null) {
                this.peer.setCursor(this.cursor);
            }
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(SerializationTester.test(this.transferable) ? this.transferable : null);
        objectOutputStream.writeObject(SerializationTester.test(this.listener) ? this.listener : null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final DragGestureEvent trigger = (DragGestureEvent)fields.get("trigger", null);
        if (trigger == null) {
            throw new InvalidObjectException("Null trigger");
        }
        if (trigger.getDragSource() == null) {
            throw new InvalidObjectException("Null DragSource");
        }
        if (trigger.getComponent() == null) {
            throw new InvalidObjectException("Null trigger component");
        }
        final int sourceActions = fields.get("sourceActions", 0) & 0x40000003;
        if (sourceActions == 0) {
            throw new InvalidObjectException("Invalid source actions");
        }
        final int dragAction = trigger.getDragAction();
        if (dragAction != 1 && dragAction != 2 && dragAction != 1073741824) {
            throw new InvalidObjectException("No drag action");
        }
        this.trigger = trigger;
        this.cursor = (Cursor)fields.get("cursor", null);
        this.useCustomCursor = fields.get("useCustomCursor", false);
        this.sourceActions = sourceActions;
        this.transferable = (Transferable)objectInputStream.readObject();
        this.listener = (DragSourceListener)objectInputStream.readObject();
        if (this.transferable == null) {
            if (DragSourceContext.emptyTransferable == null) {
                DragSourceContext.emptyTransferable = new Transferable() {
                    @Override
                    public DataFlavor[] getTransferDataFlavors() {
                        return new DataFlavor[0];
                    }
                    
                    @Override
                    public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
                        return false;
                    }
                    
                    @Override
                    public Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException {
                        throw new UnsupportedFlavorException(dataFlavor);
                    }
                };
            }
            this.transferable = DragSourceContext.emptyTransferable;
        }
    }
}
