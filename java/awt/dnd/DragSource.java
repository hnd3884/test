package java.awt.dnd;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.AWTEventMulticaster;
import java.util.EventListener;
import java.awt.Component;
import java.awt.dnd.peer.DragSourceContextPeer;
import sun.awt.dnd.SunDragSourceContextPeer;
import java.awt.datatransfer.Transferable;
import java.awt.Point;
import java.awt.Image;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;
import java.awt.datatransfer.FlavorMap;
import java.awt.Cursor;
import java.io.Serializable;

public class DragSource implements Serializable
{
    private static final long serialVersionUID = 6236096958971414066L;
    public static final Cursor DefaultCopyDrop;
    public static final Cursor DefaultMoveDrop;
    public static final Cursor DefaultLinkDrop;
    public static final Cursor DefaultCopyNoDrop;
    public static final Cursor DefaultMoveNoDrop;
    public static final Cursor DefaultLinkNoDrop;
    private static final DragSource dflt;
    static final String dragSourceListenerK = "dragSourceL";
    static final String dragSourceMotionListenerK = "dragSourceMotionL";
    private transient FlavorMap flavorMap;
    private transient DragSourceListener listener;
    private transient DragSourceMotionListener motionListener;
    
    private static Cursor load(final String s) {
        if (GraphicsEnvironment.isHeadless()) {
            return null;
        }
        try {
            return (Cursor)Toolkit.getDefaultToolkit().getDesktopProperty(s);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("failed to load system cursor: " + s + " : " + ex.getMessage());
        }
    }
    
    public static DragSource getDefaultDragSource() {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        return DragSource.dflt;
    }
    
    public static boolean isDragImageSupported() {
        Toolkit.getDefaultToolkit();
        try {
            return (boolean)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.isDragImageSupported");
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public DragSource() throws HeadlessException {
        this.flavorMap = SystemFlavorMap.getDefaultFlavorMap();
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
    }
    
    public void startDrag(final DragGestureEvent dragGestureEvent, final Cursor cursor, final Image image, final Point point, final Transferable transferable, final DragSourceListener dragSourceListener, final FlavorMap flavorMap) throws InvalidDnDOperationException {
        SunDragSourceContextPeer.setDragDropInProgress(true);
        try {
            if (flavorMap != null) {
                this.flavorMap = flavorMap;
            }
            final DragSourceContextPeer dragSourceContextPeer = Toolkit.getDefaultToolkit().createDragSourceContextPeer(dragGestureEvent);
            final DragSourceContext dragSourceContext = this.createDragSourceContext(dragSourceContextPeer, dragGestureEvent, cursor, image, point, transferable, dragSourceListener);
            if (dragSourceContext == null) {
                throw new InvalidDnDOperationException();
            }
            dragSourceContextPeer.startDrag(dragSourceContext, dragSourceContext.getCursor(), image, point);
        }
        catch (final RuntimeException ex) {
            SunDragSourceContextPeer.setDragDropInProgress(false);
            throw ex;
        }
    }
    
    public void startDrag(final DragGestureEvent dragGestureEvent, final Cursor cursor, final Transferable transferable, final DragSourceListener dragSourceListener, final FlavorMap flavorMap) throws InvalidDnDOperationException {
        this.startDrag(dragGestureEvent, cursor, null, null, transferable, dragSourceListener, flavorMap);
    }
    
    public void startDrag(final DragGestureEvent dragGestureEvent, final Cursor cursor, final Image image, final Point point, final Transferable transferable, final DragSourceListener dragSourceListener) throws InvalidDnDOperationException {
        this.startDrag(dragGestureEvent, cursor, image, point, transferable, dragSourceListener, null);
    }
    
    public void startDrag(final DragGestureEvent dragGestureEvent, final Cursor cursor, final Transferable transferable, final DragSourceListener dragSourceListener) throws InvalidDnDOperationException {
        this.startDrag(dragGestureEvent, cursor, null, null, transferable, dragSourceListener, null);
    }
    
    protected DragSourceContext createDragSourceContext(final DragSourceContextPeer dragSourceContextPeer, final DragGestureEvent dragGestureEvent, final Cursor cursor, final Image image, final Point point, final Transferable transferable, final DragSourceListener dragSourceListener) {
        return new DragSourceContext(dragSourceContextPeer, dragGestureEvent, cursor, image, point, transferable, dragSourceListener);
    }
    
    public FlavorMap getFlavorMap() {
        return this.flavorMap;
    }
    
    public <T extends DragGestureRecognizer> T createDragGestureRecognizer(final Class<T> clazz, final Component component, final int n, final DragGestureListener dragGestureListener) {
        return Toolkit.getDefaultToolkit().createDragGestureRecognizer(clazz, this, component, n, dragGestureListener);
    }
    
    public DragGestureRecognizer createDefaultDragGestureRecognizer(final Component component, final int n, final DragGestureListener dragGestureListener) {
        return Toolkit.getDefaultToolkit().createDragGestureRecognizer(MouseDragGestureRecognizer.class, this, component, n, dragGestureListener);
    }
    
    public void addDragSourceListener(final DragSourceListener dragSourceListener) {
        if (dragSourceListener != null) {
            synchronized (this) {
                this.listener = DnDEventMulticaster.add(this.listener, dragSourceListener);
            }
        }
    }
    
    public void removeDragSourceListener(final DragSourceListener dragSourceListener) {
        if (dragSourceListener != null) {
            synchronized (this) {
                this.listener = DnDEventMulticaster.remove(this.listener, dragSourceListener);
            }
        }
    }
    
    public DragSourceListener[] getDragSourceListeners() {
        return this.getListeners(DragSourceListener.class);
    }
    
    public void addDragSourceMotionListener(final DragSourceMotionListener dragSourceMotionListener) {
        if (dragSourceMotionListener != null) {
            synchronized (this) {
                this.motionListener = DnDEventMulticaster.add(this.motionListener, dragSourceMotionListener);
            }
        }
    }
    
    public void removeDragSourceMotionListener(final DragSourceMotionListener dragSourceMotionListener) {
        if (dragSourceMotionListener != null) {
            synchronized (this) {
                this.motionListener = DnDEventMulticaster.remove(this.motionListener, dragSourceMotionListener);
            }
        }
    }
    
    public DragSourceMotionListener[] getDragSourceMotionListeners() {
        return this.getListeners(DragSourceMotionListener.class);
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        EventListener eventListener = null;
        if (clazz == DragSourceListener.class) {
            eventListener = this.listener;
        }
        else if (clazz == DragSourceMotionListener.class) {
            eventListener = this.motionListener;
        }
        return AWTEventMulticaster.getListeners(eventListener, clazz);
    }
    
    void processDragEnter(final DragSourceDragEvent dragSourceDragEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dragEnter(dragSourceDragEvent);
        }
    }
    
    void processDragOver(final DragSourceDragEvent dragSourceDragEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dragOver(dragSourceDragEvent);
        }
    }
    
    void processDropActionChanged(final DragSourceDragEvent dragSourceDragEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dropActionChanged(dragSourceDragEvent);
        }
    }
    
    void processDragExit(final DragSourceEvent dragSourceEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dragExit(dragSourceEvent);
        }
    }
    
    void processDragDropEnd(final DragSourceDropEvent dragSourceDropEvent) {
        final DragSourceListener listener = this.listener;
        if (listener != null) {
            listener.dragDropEnd(dragSourceDropEvent);
        }
    }
    
    void processDragMouseMoved(final DragSourceDragEvent dragSourceDragEvent) {
        final DragSourceMotionListener motionListener = this.motionListener;
        if (motionListener != null) {
            motionListener.dragMouseMoved(dragSourceDragEvent);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(SerializationTester.test(this.flavorMap) ? this.flavorMap : null);
        DnDEventMulticaster.save(objectOutputStream, "dragSourceL", this.listener);
        DnDEventMulticaster.save(objectOutputStream, "dragSourceMotionL", this.motionListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.flavorMap = (FlavorMap)objectInputStream.readObject();
        if (this.flavorMap == null) {
            this.flavorMap = SystemFlavorMap.getDefaultFlavorMap();
        }
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            final String intern = ((String)object).intern();
            if ("dragSourceL" == intern) {
                this.addDragSourceListener((DragSourceListener)objectInputStream.readObject());
            }
            else if ("dragSourceMotionL" == intern) {
                this.addDragSourceMotionListener((DragSourceMotionListener)objectInputStream.readObject());
            }
            else {
                objectInputStream.readObject();
            }
        }
    }
    
    public static int getDragThreshold() {
        final int intValue = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("awt.dnd.drag.threshold", 0));
        if (intValue > 0) {
            return intValue;
        }
        final Integer n = (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("DnD.gestureMotionThreshold");
        if (n != null) {
            return n;
        }
        return 5;
    }
    
    static {
        DefaultCopyDrop = load("DnD.Cursor.CopyDrop");
        DefaultMoveDrop = load("DnD.Cursor.MoveDrop");
        DefaultLinkDrop = load("DnD.Cursor.LinkDrop");
        DefaultCopyNoDrop = load("DnD.Cursor.CopyNoDrop");
        DefaultMoveNoDrop = load("DnD.Cursor.MoveNoDrop");
        DefaultLinkNoDrop = load("DnD.Cursor.LinkNoDrop");
        dflt = (GraphicsEnvironment.isHeadless() ? null : new DragSource());
    }
}
