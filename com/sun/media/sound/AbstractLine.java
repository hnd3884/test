package com.sun.media.sound;

import java.util.WeakHashMap;
import javax.sound.sampled.LineUnavailableException;
import java.util.List;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.util.Map;
import java.util.Vector;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;

abstract class AbstractLine implements Line
{
    protected final Info info;
    protected Control[] controls;
    AbstractMixer mixer;
    private volatile boolean open;
    private final Vector listeners;
    private static final Map<ThreadGroup, EventDispatcher> dispatchers;
    
    protected AbstractLine(final Info info, final AbstractMixer mixer, Control[] controls) {
        this.listeners = new Vector();
        if (controls == null) {
            controls = new Control[0];
        }
        this.info = info;
        this.mixer = mixer;
        this.controls = controls;
    }
    
    @Override
    public final Info getLineInfo() {
        return this.info;
    }
    
    @Override
    public final boolean isOpen() {
        return this.open;
    }
    
    @Override
    public final void addLineListener(final LineListener lineListener) {
        synchronized (this.listeners) {
            if (!this.listeners.contains(lineListener)) {
                this.listeners.addElement(lineListener);
            }
        }
    }
    
    @Override
    public final void removeLineListener(final LineListener lineListener) {
        this.listeners.removeElement(lineListener);
    }
    
    @Override
    public final Control[] getControls() {
        final Control[] array = new Control[this.controls.length];
        for (int i = 0; i < this.controls.length; ++i) {
            array[i] = this.controls[i];
        }
        return array;
    }
    
    @Override
    public final boolean isControlSupported(final Control.Type type) {
        if (type == null) {
            return false;
        }
        for (int i = 0; i < this.controls.length; ++i) {
            if (type == this.controls[i].getType()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final Control getControl(final Control.Type type) {
        if (type != null) {
            for (int i = 0; i < this.controls.length; ++i) {
                if (type == this.controls[i].getType()) {
                    return this.controls[i];
                }
            }
        }
        throw new IllegalArgumentException("Unsupported control type: " + type);
    }
    
    final void setOpen(final boolean open) {
        boolean b = false;
        final long longFramePosition = this.getLongFramePosition();
        synchronized (this) {
            if (this.open != open) {
                this.open = open;
                b = true;
            }
        }
        if (b) {
            if (open) {
                this.sendEvents(new LineEvent(this, LineEvent.Type.OPEN, longFramePosition));
            }
            else {
                this.sendEvents(new LineEvent(this, LineEvent.Type.CLOSE, longFramePosition));
            }
        }
    }
    
    final void sendEvents(final LineEvent lineEvent) {
        this.getEventDispatcher().sendAudioEvents(lineEvent, this.listeners);
    }
    
    public final int getFramePosition() {
        return (int)this.getLongFramePosition();
    }
    
    public long getLongFramePosition() {
        return -1L;
    }
    
    final AbstractMixer getMixer() {
        return this.mixer;
    }
    
    final EventDispatcher getEventDispatcher() {
        final ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        synchronized (AbstractLine.dispatchers) {
            EventDispatcher eventDispatcher = AbstractLine.dispatchers.get(threadGroup);
            if (eventDispatcher == null) {
                eventDispatcher = new EventDispatcher();
                AbstractLine.dispatchers.put(threadGroup, eventDispatcher);
                eventDispatcher.start();
            }
            return eventDispatcher;
        }
    }
    
    @Override
    public abstract void open() throws LineUnavailableException;
    
    @Override
    public abstract void close();
    
    static {
        dispatchers = new WeakHashMap<ThreadGroup, EventDispatcher>();
    }
}
