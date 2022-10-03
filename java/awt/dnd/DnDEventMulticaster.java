package java.awt.dnd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.awt.AWTEventMulticaster;

class DnDEventMulticaster extends AWTEventMulticaster implements DragSourceListener, DragSourceMotionListener
{
    protected DnDEventMulticaster(final EventListener eventListener, final EventListener eventListener2) {
        super(eventListener, eventListener2);
    }
    
    @Override
    public void dragEnter(final DragSourceDragEvent dragSourceDragEvent) {
        ((DragSourceListener)this.a).dragEnter(dragSourceDragEvent);
        ((DragSourceListener)this.b).dragEnter(dragSourceDragEvent);
    }
    
    @Override
    public void dragOver(final DragSourceDragEvent dragSourceDragEvent) {
        ((DragSourceListener)this.a).dragOver(dragSourceDragEvent);
        ((DragSourceListener)this.b).dragOver(dragSourceDragEvent);
    }
    
    @Override
    public void dropActionChanged(final DragSourceDragEvent dragSourceDragEvent) {
        ((DragSourceListener)this.a).dropActionChanged(dragSourceDragEvent);
        ((DragSourceListener)this.b).dropActionChanged(dragSourceDragEvent);
    }
    
    @Override
    public void dragExit(final DragSourceEvent dragSourceEvent) {
        ((DragSourceListener)this.a).dragExit(dragSourceEvent);
        ((DragSourceListener)this.b).dragExit(dragSourceEvent);
    }
    
    @Override
    public void dragDropEnd(final DragSourceDropEvent dragSourceDropEvent) {
        ((DragSourceListener)this.a).dragDropEnd(dragSourceDropEvent);
        ((DragSourceListener)this.b).dragDropEnd(dragSourceDropEvent);
    }
    
    @Override
    public void dragMouseMoved(final DragSourceDragEvent dragSourceDragEvent) {
        ((DragSourceMotionListener)this.a).dragMouseMoved(dragSourceDragEvent);
        ((DragSourceMotionListener)this.b).dragMouseMoved(dragSourceDragEvent);
    }
    
    public static DragSourceListener add(final DragSourceListener dragSourceListener, final DragSourceListener dragSourceListener2) {
        return (DragSourceListener)addInternal(dragSourceListener, dragSourceListener2);
    }
    
    public static DragSourceMotionListener add(final DragSourceMotionListener dragSourceMotionListener, final DragSourceMotionListener dragSourceMotionListener2) {
        return (DragSourceMotionListener)addInternal(dragSourceMotionListener, dragSourceMotionListener2);
    }
    
    public static DragSourceListener remove(final DragSourceListener dragSourceListener, final DragSourceListener dragSourceListener2) {
        return (DragSourceListener)removeInternal(dragSourceListener, dragSourceListener2);
    }
    
    public static DragSourceMotionListener remove(final DragSourceMotionListener dragSourceMotionListener, final DragSourceMotionListener dragSourceMotionListener2) {
        return (DragSourceMotionListener)removeInternal(dragSourceMotionListener, dragSourceMotionListener2);
    }
    
    protected static EventListener addInternal(final EventListener eventListener, final EventListener eventListener2) {
        if (eventListener == null) {
            return eventListener2;
        }
        if (eventListener2 == null) {
            return eventListener;
        }
        return new DnDEventMulticaster(eventListener, eventListener2);
    }
    
    @Override
    protected EventListener remove(final EventListener eventListener) {
        if (eventListener == this.a) {
            return this.b;
        }
        if (eventListener == this.b) {
            return this.a;
        }
        final EventListener removeInternal = removeInternal(this.a, eventListener);
        final EventListener removeInternal2 = removeInternal(this.b, eventListener);
        if (removeInternal == this.a && removeInternal2 == this.b) {
            return this;
        }
        return addInternal(removeInternal, removeInternal2);
    }
    
    protected static EventListener removeInternal(final EventListener eventListener, final EventListener eventListener2) {
        if (eventListener == eventListener2 || eventListener == null) {
            return null;
        }
        if (eventListener instanceof DnDEventMulticaster) {
            return ((DnDEventMulticaster)eventListener).remove(eventListener2);
        }
        return eventListener;
    }
    
    protected static void save(final ObjectOutputStream objectOutputStream, final String s, final EventListener eventListener) throws IOException {
        AWTEventMulticaster.save(objectOutputStream, s, eventListener);
    }
}
