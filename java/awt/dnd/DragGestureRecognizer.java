package java.awt.dnd;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.awt.Point;
import java.util.TooManyListenersException;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.awt.Component;
import java.io.Serializable;

public abstract class DragGestureRecognizer implements Serializable
{
    private static final long serialVersionUID = 8996673345831063337L;
    protected DragSource dragSource;
    protected Component component;
    protected transient DragGestureListener dragGestureListener;
    protected int sourceActions;
    protected ArrayList<InputEvent> events;
    
    protected DragGestureRecognizer(final DragSource dragSource, final Component component, final int n, final DragGestureListener dragGestureListener) {
        this.events = new ArrayList<InputEvent>(1);
        if (dragSource == null) {
            throw new IllegalArgumentException("null DragSource");
        }
        this.dragSource = dragSource;
        this.component = component;
        this.sourceActions = (n & 0x40000003);
        try {
            if (dragGestureListener != null) {
                this.addDragGestureListener(dragGestureListener);
            }
        }
        catch (final TooManyListenersException ex) {}
    }
    
    protected DragGestureRecognizer(final DragSource dragSource, final Component component, final int n) {
        this(dragSource, component, n, null);
    }
    
    protected DragGestureRecognizer(final DragSource dragSource, final Component component) {
        this(dragSource, component, 0);
    }
    
    protected DragGestureRecognizer(final DragSource dragSource) {
        this(dragSource, null);
    }
    
    protected abstract void registerListeners();
    
    protected abstract void unregisterListeners();
    
    public DragSource getDragSource() {
        return this.dragSource;
    }
    
    public synchronized Component getComponent() {
        return this.component;
    }
    
    public synchronized void setComponent(final Component component) {
        if (this.component != null && this.dragGestureListener != null) {
            this.unregisterListeners();
        }
        this.component = component;
        if (this.component != null && this.dragGestureListener != null) {
            this.registerListeners();
        }
    }
    
    public synchronized int getSourceActions() {
        return this.sourceActions;
    }
    
    public synchronized void setSourceActions(final int n) {
        this.sourceActions = (n & 0x40000003);
    }
    
    public InputEvent getTriggerEvent() {
        return this.events.isEmpty() ? null : this.events.get(0);
    }
    
    public void resetRecognizer() {
        this.events.clear();
    }
    
    public synchronized void addDragGestureListener(final DragGestureListener dragGestureListener) throws TooManyListenersException {
        if (this.dragGestureListener != null) {
            throw new TooManyListenersException();
        }
        this.dragGestureListener = dragGestureListener;
        if (this.component != null) {
            this.registerListeners();
        }
    }
    
    public synchronized void removeDragGestureListener(final DragGestureListener dragGestureListener) {
        if (this.dragGestureListener == null || !this.dragGestureListener.equals(dragGestureListener)) {
            throw new IllegalArgumentException();
        }
        this.dragGestureListener = null;
        if (this.component != null) {
            this.unregisterListeners();
        }
    }
    
    protected synchronized void fireDragGestureRecognized(final int n, final Point point) {
        try {
            if (this.dragGestureListener != null) {
                this.dragGestureListener.dragGestureRecognized(new DragGestureEvent(this, n, point, this.events));
            }
        }
        finally {
            this.events.clear();
        }
    }
    
    protected synchronized void appendEvent(final InputEvent inputEvent) {
        this.events.add(inputEvent);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(SerializationTester.test(this.dragGestureListener) ? this.dragGestureListener : null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final DragSource dragSource = (DragSource)fields.get("dragSource", null);
        if (dragSource == null) {
            throw new InvalidObjectException("null DragSource");
        }
        this.dragSource = dragSource;
        this.component = (Component)fields.get("component", null);
        this.sourceActions = (fields.get("sourceActions", 0) & 0x40000003);
        this.events = (ArrayList)fields.get("events", new ArrayList(1));
        this.dragGestureListener = (DragGestureListener)objectInputStream.readObject();
    }
}
