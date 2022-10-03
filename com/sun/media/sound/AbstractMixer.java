package com.sun.media.sound;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Control;
import java.util.Vector;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

abstract class AbstractMixer extends AbstractLine implements Mixer
{
    protected static final int PCM = 0;
    protected static final int ULAW = 1;
    protected static final int ALAW = 2;
    private final Mixer.Info mixerInfo;
    protected Line.Info[] sourceLineInfo;
    protected Line.Info[] targetLineInfo;
    private boolean started;
    private boolean manuallyOpened;
    private final Vector sourceLines;
    private final Vector targetLines;
    
    protected AbstractMixer(final Mixer.Info mixerInfo, Control[] array, final Line.Info[] sourceLineInfo, final Line.Info[] targetLineInfo) {
        super(new Line.Info(Mixer.class), null, array);
        this.started = false;
        this.manuallyOpened = false;
        this.sourceLines = new Vector();
        this.targetLines = new Vector();
        this.mixer = this;
        if (array == null) {
            array = new Control[0];
        }
        this.mixerInfo = mixerInfo;
        this.sourceLineInfo = sourceLineInfo;
        this.targetLineInfo = targetLineInfo;
    }
    
    @Override
    public final Mixer.Info getMixerInfo() {
        return this.mixerInfo;
    }
    
    @Override
    public final Line.Info[] getSourceLineInfo() {
        final Line.Info[] array = new Line.Info[this.sourceLineInfo.length];
        System.arraycopy(this.sourceLineInfo, 0, array, 0, this.sourceLineInfo.length);
        return array;
    }
    
    @Override
    public final Line.Info[] getTargetLineInfo() {
        final Line.Info[] array = new Line.Info[this.targetLineInfo.length];
        System.arraycopy(this.targetLineInfo, 0, array, 0, this.targetLineInfo.length);
        return array;
    }
    
    @Override
    public final Line.Info[] getSourceLineInfo(final Line.Info info) {
        final Vector vector = new Vector();
        for (int i = 0; i < this.sourceLineInfo.length; ++i) {
            if (info.matches(this.sourceLineInfo[i])) {
                vector.addElement(this.sourceLineInfo[i]);
            }
        }
        final Line.Info[] array = new Line.Info[vector.size()];
        for (int j = 0; j < array.length; ++j) {
            array[j] = (Line.Info)vector.elementAt(j);
        }
        return array;
    }
    
    @Override
    public final Line.Info[] getTargetLineInfo(final Line.Info info) {
        final Vector vector = new Vector();
        for (int i = 0; i < this.targetLineInfo.length; ++i) {
            if (info.matches(this.targetLineInfo[i])) {
                vector.addElement(this.targetLineInfo[i]);
            }
        }
        final Line.Info[] array = new Line.Info[vector.size()];
        for (int j = 0; j < array.length; ++j) {
            array[j] = (Line.Info)vector.elementAt(j);
        }
        return array;
    }
    
    @Override
    public final boolean isLineSupported(final Line.Info info) {
        for (int i = 0; i < this.sourceLineInfo.length; ++i) {
            if (info.matches(this.sourceLineInfo[i])) {
                return true;
            }
        }
        for (int j = 0; j < this.targetLineInfo.length; ++j) {
            if (info.matches(this.targetLineInfo[j])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public abstract Line getLine(final Line.Info p0) throws LineUnavailableException;
    
    @Override
    public abstract int getMaxLines(final Line.Info p0);
    
    protected abstract void implOpen() throws LineUnavailableException;
    
    protected abstract void implStart();
    
    protected abstract void implStop();
    
    protected abstract void implClose();
    
    @Override
    public final Line[] getSourceLines() {
        final Line[] array;
        synchronized (this.sourceLines) {
            array = new Line[this.sourceLines.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = (Line)this.sourceLines.elementAt(i);
            }
        }
        return array;
    }
    
    @Override
    public final Line[] getTargetLines() {
        final Line[] array;
        synchronized (this.targetLines) {
            array = new Line[this.targetLines.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = (Line)this.targetLines.elementAt(i);
            }
        }
        return array;
    }
    
    @Override
    public final void synchronize(final Line[] array, final boolean b) {
        throw new IllegalArgumentException("Synchronization not supported by this mixer.");
    }
    
    @Override
    public final void unsynchronize(final Line[] array) {
        throw new IllegalArgumentException("Synchronization not supported by this mixer.");
    }
    
    @Override
    public final boolean isSynchronizationSupported(final Line[] array, final boolean b) {
        return false;
    }
    
    @Override
    public final synchronized void open() throws LineUnavailableException {
        this.open(true);
    }
    
    final synchronized void open(final boolean b) throws LineUnavailableException {
        if (!this.isOpen()) {
            this.implOpen();
            this.setOpen(true);
            if (b) {
                this.manuallyOpened = true;
            }
        }
    }
    
    final synchronized void open(final Line line) throws LineUnavailableException {
        if (this.equals(line)) {
            return;
        }
        if (this.isSourceLine(line.getLineInfo())) {
            if (!this.sourceLines.contains(line)) {
                this.open(false);
                this.sourceLines.addElement(line);
            }
        }
        else if (this.isTargetLine(line.getLineInfo()) && !this.targetLines.contains(line)) {
            this.open(false);
            this.targetLines.addElement(line);
        }
    }
    
    final synchronized void close(final Line line) {
        if (this.equals(line)) {
            return;
        }
        this.sourceLines.removeElement(line);
        this.targetLines.removeElement(line);
        if (this.sourceLines.isEmpty() && this.targetLines.isEmpty() && !this.manuallyOpened) {
            this.close();
        }
    }
    
    @Override
    public final synchronized void close() {
        if (this.isOpen()) {
            final Line[] sourceLines = this.getSourceLines();
            for (int i = 0; i < sourceLines.length; ++i) {
                sourceLines[i].close();
            }
            final Line[] targetLines = this.getTargetLines();
            for (int j = 0; j < targetLines.length; ++j) {
                targetLines[j].close();
            }
            this.implClose();
            this.setOpen(false);
        }
        this.manuallyOpened = false;
    }
    
    final synchronized void start(final Line line) {
        if (this.equals(line)) {
            return;
        }
        if (!this.started) {
            this.implStart();
            this.started = true;
        }
    }
    
    final synchronized void stop(final Line line) {
        if (this.equals(line)) {
            return;
        }
        final Vector vector = (Vector)this.sourceLines.clone();
        for (int i = 0; i < vector.size(); ++i) {
            if (vector.elementAt(i) instanceof AbstractDataLine) {
                final AbstractDataLine abstractDataLine = vector.elementAt(i);
                if (abstractDataLine.isStartedRunning() && !abstractDataLine.equals(line)) {
                    return;
                }
            }
        }
        final Vector vector2 = (Vector)this.targetLines.clone();
        for (int j = 0; j < vector2.size(); ++j) {
            if (vector2.elementAt(j) instanceof AbstractDataLine) {
                final AbstractDataLine abstractDataLine2 = vector2.elementAt(j);
                if (abstractDataLine2.isStartedRunning() && !abstractDataLine2.equals(line)) {
                    return;
                }
            }
        }
        this.started = false;
        this.implStop();
    }
    
    final boolean isSourceLine(final Line.Info info) {
        for (int i = 0; i < this.sourceLineInfo.length; ++i) {
            if (info.matches(this.sourceLineInfo[i])) {
                return true;
            }
        }
        return false;
    }
    
    final boolean isTargetLine(final Line.Info info) {
        for (int i = 0; i < this.targetLineInfo.length; ++i) {
            if (info.matches(this.targetLineInfo[i])) {
                return true;
            }
        }
        return false;
    }
    
    final Line.Info getLineInfo(final Line.Info info) {
        if (info == null) {
            return null;
        }
        for (int i = 0; i < this.sourceLineInfo.length; ++i) {
            if (info.matches(this.sourceLineInfo[i])) {
                return this.sourceLineInfo[i];
            }
        }
        for (int j = 0; j < this.targetLineInfo.length; ++j) {
            if (info.matches(this.targetLineInfo[j])) {
                return this.targetLineInfo[j];
            }
        }
        return null;
    }
}
