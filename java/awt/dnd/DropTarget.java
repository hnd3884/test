package java.awt.dnd;

import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Rectangle;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.Point;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.dnd.peer.DropTargetPeer;
import java.awt.peer.LightweightPeer;
import java.awt.datatransfer.SystemFlavorMap;
import java.util.TooManyListenersException;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import java.awt.datatransfer.FlavorMap;
import java.awt.peer.ComponentPeer;
import java.awt.Component;
import java.io.Serializable;

public class DropTarget implements DropTargetListener, Serializable
{
    private static final long serialVersionUID = -6283860791671019047L;
    private DropTargetContext dropTargetContext;
    private Component component;
    private transient ComponentPeer componentPeer;
    private transient ComponentPeer nativePeer;
    int actions;
    boolean active;
    private transient DropTargetAutoScroller autoScroller;
    private transient DropTargetListener dtListener;
    private transient FlavorMap flavorMap;
    private transient boolean isDraggingInside;
    
    public DropTarget(final Component component, final int defaultActions, final DropTargetListener dropTargetListener, final boolean active, final FlavorMap flavorMap) throws HeadlessException {
        this.dropTargetContext = this.createDropTargetContext();
        this.actions = 3;
        this.active = true;
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        this.component = component;
        this.setDefaultActions(defaultActions);
        if (dropTargetListener != null) {
            try {
                this.addDropTargetListener(dropTargetListener);
            }
            catch (final TooManyListenersException ex) {}
        }
        if (component != null) {
            component.setDropTarget(this);
            this.setActive(active);
        }
        if (flavorMap != null) {
            this.flavorMap = flavorMap;
        }
        else {
            this.flavorMap = SystemFlavorMap.getDefaultFlavorMap();
        }
    }
    
    public DropTarget(final Component component, final int n, final DropTargetListener dropTargetListener, final boolean b) throws HeadlessException {
        this(component, n, dropTargetListener, b, null);
    }
    
    public DropTarget() throws HeadlessException {
        this(null, 3, null, true, null);
    }
    
    public DropTarget(final Component component, final DropTargetListener dropTargetListener) throws HeadlessException {
        this(component, 3, dropTargetListener, true, null);
    }
    
    public DropTarget(final Component component, final int n, final DropTargetListener dropTargetListener) throws HeadlessException {
        this(component, n, dropTargetListener, true);
    }
    
    public synchronized void setComponent(final Component component) {
        if (this.component == component || (this.component != null && this.component.equals(component))) {
            return;
        }
        ComponentPeer componentPeer = null;
        final Component component2;
        if ((component2 = this.component) != null) {
            this.clearAutoscroll();
            this.component = null;
            if (this.componentPeer != null) {
                componentPeer = this.componentPeer;
                this.removeNotify(this.componentPeer);
            }
            component2.setDropTarget(null);
        }
        if ((this.component = component) != null) {
            try {
                component.setDropTarget(this);
            }
            catch (final Exception ex) {
                if (component2 != null) {
                    component2.setDropTarget(this);
                    this.addNotify(componentPeer);
                }
            }
        }
    }
    
    public synchronized Component getComponent() {
        return this.component;
    }
    
    public void setDefaultActions(final int n) {
        this.getDropTargetContext().setTargetActions(n & 0x40000003);
    }
    
    void doSetDefaultActions(final int actions) {
        this.actions = actions;
    }
    
    public int getDefaultActions() {
        return this.actions;
    }
    
    public synchronized void setActive(final boolean active) {
        if (active != this.active) {
            this.active = active;
        }
        if (!this.active) {
            this.clearAutoscroll();
        }
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public synchronized void addDropTargetListener(final DropTargetListener dtListener) throws TooManyListenersException {
        if (dtListener == null) {
            return;
        }
        if (this.equals(dtListener)) {
            throw new IllegalArgumentException("DropTarget may not be its own Listener");
        }
        if (this.dtListener == null) {
            this.dtListener = dtListener;
            return;
        }
        throw new TooManyListenersException();
    }
    
    public synchronized void removeDropTargetListener(final DropTargetListener dropTargetListener) {
        if (dropTargetListener != null && this.dtListener != null) {
            if (!this.dtListener.equals(dropTargetListener)) {
                throw new IllegalArgumentException("listener mismatch");
            }
            this.dtListener = null;
        }
    }
    
    @Override
    public synchronized void dragEnter(final DropTargetDragEvent dropTargetDragEvent) {
        this.isDraggingInside = true;
        if (!this.active) {
            return;
        }
        if (this.dtListener != null) {
            this.dtListener.dragEnter(dropTargetDragEvent);
        }
        else {
            dropTargetDragEvent.getDropTargetContext().setTargetActions(0);
        }
        this.initializeAutoscrolling(dropTargetDragEvent.getLocation());
    }
    
    @Override
    public synchronized void dragOver(final DropTargetDragEvent dropTargetDragEvent) {
        if (!this.active) {
            return;
        }
        if (this.dtListener != null && this.active) {
            this.dtListener.dragOver(dropTargetDragEvent);
        }
        this.updateAutoscroll(dropTargetDragEvent.getLocation());
    }
    
    @Override
    public synchronized void dropActionChanged(final DropTargetDragEvent dropTargetDragEvent) {
        if (!this.active) {
            return;
        }
        if (this.dtListener != null) {
            this.dtListener.dropActionChanged(dropTargetDragEvent);
        }
        this.updateAutoscroll(dropTargetDragEvent.getLocation());
    }
    
    @Override
    public synchronized void dragExit(final DropTargetEvent dropTargetEvent) {
        this.isDraggingInside = false;
        if (!this.active) {
            return;
        }
        if (this.dtListener != null && this.active) {
            this.dtListener.dragExit(dropTargetEvent);
        }
        this.clearAutoscroll();
    }
    
    @Override
    public synchronized void drop(final DropTargetDropEvent dropTargetDropEvent) {
        this.isDraggingInside = false;
        this.clearAutoscroll();
        if (this.dtListener != null && this.active) {
            this.dtListener.drop(dropTargetDropEvent);
        }
        else {
            dropTargetDropEvent.rejectDrop();
        }
    }
    
    public FlavorMap getFlavorMap() {
        return this.flavorMap;
    }
    
    public void setFlavorMap(final FlavorMap flavorMap) {
        this.flavorMap = ((flavorMap == null) ? SystemFlavorMap.getDefaultFlavorMap() : flavorMap);
    }
    
    public void addNotify(ComponentPeer peer) {
        if (peer == this.componentPeer) {
            return;
        }
        this.componentPeer = peer;
        for (Component component = this.component; component != null && peer instanceof LightweightPeer; peer = component.getPeer(), component = component.getParent()) {}
        if (peer instanceof DropTargetPeer) {
            this.nativePeer = peer;
            ((DropTargetPeer)peer).addDropTarget(this);
        }
        else {
            this.nativePeer = null;
        }
    }
    
    public void removeNotify(final ComponentPeer componentPeer) {
        if (this.nativePeer != null) {
            ((DropTargetPeer)this.nativePeer).removeDropTarget(this);
        }
        final ComponentPeer componentPeer2 = null;
        this.nativePeer = componentPeer2;
        this.componentPeer = componentPeer2;
        synchronized (this) {
            if (this.isDraggingInside) {
                this.dragExit(new DropTargetEvent(this.getDropTargetContext()));
            }
        }
    }
    
    public DropTargetContext getDropTargetContext() {
        return this.dropTargetContext;
    }
    
    protected DropTargetContext createDropTargetContext() {
        return new DropTargetContext(this);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(SerializationTester.test(this.dtListener) ? this.dtListener : null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        try {
            this.dropTargetContext = (DropTargetContext)fields.get("dropTargetContext", null);
        }
        catch (final IllegalArgumentException ex) {}
        if (this.dropTargetContext == null) {
            this.dropTargetContext = this.createDropTargetContext();
        }
        this.component = (Component)fields.get("component", null);
        this.actions = fields.get("actions", 3);
        this.active = fields.get("active", true);
        try {
            this.dtListener = (DropTargetListener)fields.get("dtListener", null);
        }
        catch (final IllegalArgumentException ex2) {
            this.dtListener = (DropTargetListener)objectInputStream.readObject();
        }
    }
    
    protected DropTargetAutoScroller createDropTargetAutoScroller(final Component component, final Point point) {
        return new DropTargetAutoScroller(component, point);
    }
    
    protected void initializeAutoscrolling(final Point point) {
        if (this.component == null || !(this.component instanceof Autoscroll)) {
            return;
        }
        this.autoScroller = this.createDropTargetAutoScroller(this.component, point);
    }
    
    protected void updateAutoscroll(final Point point) {
        if (this.autoScroller != null) {
            this.autoScroller.updateLocation(point);
        }
    }
    
    protected void clearAutoscroll() {
        if (this.autoScroller != null) {
            this.autoScroller.stop();
            this.autoScroller = null;
        }
    }
    
    protected static class DropTargetAutoScroller implements ActionListener
    {
        private Component component;
        private Autoscroll autoScroll;
        private Timer timer;
        private Point locn;
        private Point prev;
        private Rectangle outer;
        private Rectangle inner;
        private int hysteresis;
        
        protected DropTargetAutoScroller(final Component component, final Point point) {
            this.outer = new Rectangle();
            this.inner = new Rectangle();
            this.hysteresis = 10;
            this.component = component;
            this.autoScroll = (Autoscroll)this.component;
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            Integer value = 100;
            Integer value2 = 100;
            try {
                value = (Integer)defaultToolkit.getDesktopProperty("DnD.Autoscroll.initialDelay");
            }
            catch (final Exception ex) {}
            try {
                value2 = (Integer)defaultToolkit.getDesktopProperty("DnD.Autoscroll.interval");
            }
            catch (final Exception ex2) {}
            (this.timer = new Timer(value2, this)).setCoalesce(true);
            this.timer.setInitialDelay(value);
            this.locn = point;
            this.prev = point;
            try {
                this.hysteresis = (int)defaultToolkit.getDesktopProperty("DnD.Autoscroll.cursorHysteresis");
            }
            catch (final Exception ex3) {}
            this.timer.start();
        }
        
        private void updateRegion() {
            final Insets autoscrollInsets = this.autoScroll.getAutoscrollInsets();
            final Dimension size = this.component.getSize();
            if (size.width != this.outer.width || size.height != this.outer.height) {
                this.outer.reshape(0, 0, size.width, size.height);
            }
            if (this.inner.x != autoscrollInsets.left || this.inner.y != autoscrollInsets.top) {
                this.inner.setLocation(autoscrollInsets.left, autoscrollInsets.top);
            }
            final int n = size.width - (autoscrollInsets.left + autoscrollInsets.right);
            final int n2 = size.height - (autoscrollInsets.top + autoscrollInsets.bottom);
            if (n != this.inner.width || n2 != this.inner.height) {
                this.inner.setSize(n, n2);
            }
        }
        
        protected synchronized void updateLocation(final Point locn) {
            this.prev = this.locn;
            this.locn = locn;
            if (Math.abs(this.locn.x - this.prev.x) > this.hysteresis || Math.abs(this.locn.y - this.prev.y) > this.hysteresis) {
                if (this.timer.isRunning()) {
                    this.timer.stop();
                }
            }
            else if (!this.timer.isRunning()) {
                this.timer.start();
            }
        }
        
        protected void stop() {
            this.timer.stop();
        }
        
        @Override
        public synchronized void actionPerformed(final ActionEvent actionEvent) {
            this.updateRegion();
            if (this.outer.contains(this.locn) && !this.inner.contains(this.locn)) {
                this.autoScroll.autoscroll(this.locn);
            }
        }
    }
}
