package com.sun.media.sound;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Line;
import javax.sound.sampled.Control;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;

abstract class AbstractDataLine extends AbstractLine implements DataLine
{
    private final AudioFormat defaultFormat;
    private final int defaultBufferSize;
    protected final Object lock;
    protected AudioFormat format;
    protected int bufferSize;
    private volatile boolean running;
    private volatile boolean started;
    private volatile boolean active;
    
    protected AbstractDataLine(final DataLine.Info info, final AbstractMixer abstractMixer, final Control[] array) {
        this(info, abstractMixer, array, null, -1);
    }
    
    protected AbstractDataLine(final DataLine.Info info, final AbstractMixer abstractMixer, final Control[] array, final AudioFormat defaultFormat, final int defaultBufferSize) {
        super(info, abstractMixer, array);
        this.lock = new Object();
        if (defaultFormat != null) {
            this.defaultFormat = defaultFormat;
        }
        else {
            this.defaultFormat = new AudioFormat(44100.0f, 16, 2, true, Platform.isBigEndian());
        }
        if (defaultBufferSize > 0) {
            this.defaultBufferSize = defaultBufferSize;
        }
        else {
            this.defaultBufferSize = (int)(this.defaultFormat.getFrameRate() / 2.0f) * this.defaultFormat.getFrameSize();
        }
        this.format = this.defaultFormat;
        this.bufferSize = this.defaultBufferSize;
    }
    
    public final void open(final AudioFormat audioFormat, final int bufferSize) throws LineUnavailableException {
        synchronized (this.mixer) {
            if (!this.isOpen()) {
                Toolkit.isFullySpecifiedAudioFormat(audioFormat);
                this.mixer.open(this);
                try {
                    this.implOpen(audioFormat, bufferSize);
                    this.setOpen(true);
                    return;
                }
                catch (final LineUnavailableException ex) {
                    this.mixer.close(this);
                    throw ex;
                }
            }
            if (!audioFormat.matches(this.getFormat())) {
                throw new IllegalStateException("Line is already open with format " + this.getFormat() + " and bufferSize " + this.getBufferSize());
            }
            if (bufferSize > 0) {
                this.setBufferSize(bufferSize);
            }
        }
    }
    
    public final void open(final AudioFormat audioFormat) throws LineUnavailableException {
        this.open(audioFormat, -1);
    }
    
    @Override
    public int available() {
        return 0;
    }
    
    @Override
    public void drain() {
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public final void start() {
        synchronized (this.mixer) {
            if (this.isOpen() && !this.isStartedRunning()) {
                this.mixer.start(this);
                this.implStart();
                this.running = true;
            }
        }
        synchronized (this.lock) {
            this.lock.notifyAll();
        }
    }
    
    @Override
    public final void stop() {
        synchronized (this.mixer) {
            if (this.isOpen() && this.isStartedRunning()) {
                this.implStop();
                this.mixer.stop(this);
                this.running = false;
                if (this.started && !this.isActive()) {
                    this.setStarted(false);
                }
            }
        }
        synchronized (this.lock) {
            this.lock.notifyAll();
        }
    }
    
    @Override
    public final boolean isRunning() {
        return this.started;
    }
    
    @Override
    public final boolean isActive() {
        return this.active;
    }
    
    @Override
    public final long getMicrosecondPosition() {
        long n = this.getLongFramePosition();
        if (n != -1L) {
            n = Toolkit.frames2micros(this.getFormat(), n);
        }
        return n;
    }
    
    @Override
    public final AudioFormat getFormat() {
        return this.format;
    }
    
    @Override
    public final int getBufferSize() {
        return this.bufferSize;
    }
    
    public final int setBufferSize(final int n) {
        return this.getBufferSize();
    }
    
    @Override
    public final float getLevel() {
        return -1.0f;
    }
    
    final boolean isStartedRunning() {
        return this.running;
    }
    
    final void setActive(final boolean active) {
        synchronized (this) {
            if (this.active != active) {
                this.active = active;
            }
        }
    }
    
    final void setStarted(final boolean started) {
        boolean b = false;
        final long longFramePosition = this.getLongFramePosition();
        synchronized (this) {
            if (this.started != started) {
                this.started = started;
                b = true;
            }
        }
        if (b) {
            if (started) {
                this.sendEvents(new LineEvent(this, LineEvent.Type.START, longFramePosition));
            }
            else {
                this.sendEvents(new LineEvent(this, LineEvent.Type.STOP, longFramePosition));
            }
        }
    }
    
    final void setEOM() {
        this.setStarted(false);
    }
    
    @Override
    public final void open() throws LineUnavailableException {
        this.open(this.format, this.bufferSize);
    }
    
    @Override
    public final void close() {
        synchronized (this.mixer) {
            if (this.isOpen()) {
                this.stop();
                this.setOpen(false);
                this.implClose();
                this.mixer.close(this);
                this.format = this.defaultFormat;
                this.bufferSize = this.defaultBufferSize;
            }
        }
    }
    
    abstract void implOpen(final AudioFormat p0, final int p1) throws LineUnavailableException;
    
    abstract void implClose();
    
    abstract void implStart();
    
    abstract void implStop();
}
