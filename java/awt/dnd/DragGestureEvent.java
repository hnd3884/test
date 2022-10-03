package java.awt.dnd;

import java.util.Collections;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.Cursor;
import java.util.Iterator;
import java.awt.event.InputEvent;
import java.awt.Point;
import java.awt.Component;
import java.util.List;
import java.util.EventObject;

public class DragGestureEvent extends EventObject
{
    private static final long serialVersionUID = 9080172649166731306L;
    private transient List events;
    private DragSource dragSource;
    private Component component;
    private Point origin;
    private int action;
    
    public DragGestureEvent(final DragGestureRecognizer dragGestureRecognizer, final int action, final Point origin, final List<? extends InputEvent> events) {
        super(dragGestureRecognizer);
        final Component component = dragGestureRecognizer.getComponent();
        this.component = component;
        if (component == null) {
            throw new IllegalArgumentException("null component");
        }
        if ((this.dragSource = dragGestureRecognizer.getDragSource()) == null) {
            throw new IllegalArgumentException("null DragSource");
        }
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("null or empty list of events");
        }
        if (action != 1 && action != 2 && action != 1073741824) {
            throw new IllegalArgumentException("bad action");
        }
        if (origin == null) {
            throw new IllegalArgumentException("null origin");
        }
        this.events = events;
        this.action = action;
        this.origin = origin;
    }
    
    public DragGestureRecognizer getSourceAsDragGestureRecognizer() {
        return (DragGestureRecognizer)this.getSource();
    }
    
    public Component getComponent() {
        return this.component;
    }
    
    public DragSource getDragSource() {
        return this.dragSource;
    }
    
    public Point getDragOrigin() {
        return this.origin;
    }
    
    public Iterator<InputEvent> iterator() {
        return this.events.iterator();
    }
    
    public Object[] toArray() {
        return this.events.toArray();
    }
    
    public Object[] toArray(final Object[] array) {
        return this.events.toArray(array);
    }
    
    public int getDragAction() {
        return this.action;
    }
    
    public InputEvent getTriggerEvent() {
        return this.getSourceAsDragGestureRecognizer().getTriggerEvent();
    }
    
    public void startDrag(final Cursor cursor, final Transferable transferable) throws InvalidDnDOperationException {
        this.dragSource.startDrag(this, cursor, transferable, null);
    }
    
    public void startDrag(final Cursor cursor, final Transferable transferable, final DragSourceListener dragSourceListener) throws InvalidDnDOperationException {
        this.dragSource.startDrag(this, cursor, transferable, dragSourceListener);
    }
    
    public void startDrag(final Cursor cursor, final Image image, final Point point, final Transferable transferable, final DragSourceListener dragSourceListener) throws InvalidDnDOperationException {
        this.dragSource.startDrag(this, cursor, image, point, transferable, dragSourceListener);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(SerializationTester.test(this.events) ? this.events : null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final DragSource dragSource = (DragSource)fields.get("dragSource", null);
        if (dragSource == null) {
            throw new InvalidObjectException("null DragSource");
        }
        this.dragSource = dragSource;
        final Component component = (Component)fields.get("component", null);
        if (component == null) {
            throw new InvalidObjectException("null component");
        }
        this.component = component;
        final Point origin = (Point)fields.get("origin", null);
        if (origin == null) {
            throw new InvalidObjectException("null origin");
        }
        this.origin = origin;
        final int value = fields.get("action", 0);
        if (value != 1 && value != 2 && value != 1073741824) {
            throw new InvalidObjectException("bad action");
        }
        this.action = value;
        List<Object> emptyList;
        try {
            emptyList = (List)fields.get("events", null);
        }
        catch (final IllegalArgumentException ex) {
            emptyList = (List)objectInputStream.readObject();
        }
        if (emptyList != null && emptyList.isEmpty()) {
            throw new InvalidObjectException("empty list of events");
        }
        if (emptyList == null) {
            emptyList = Collections.emptyList();
        }
        this.events = emptyList;
    }
}
