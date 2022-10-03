package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

public final class DataPusher implements Runnable
{
    private static final int AUTO_CLOSE_TIME = 5000;
    private static final boolean DEBUG = false;
    private final SourceDataLine source;
    private final AudioFormat format;
    private final AudioInputStream ais;
    private final byte[] audioData;
    private final int audioDataByteLength;
    private int pos;
    private int newPos;
    private boolean looping;
    private Thread pushThread;
    private int wantedState;
    private int threadState;
    private final int STATE_NONE = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_WAITING = 2;
    private final int STATE_STOPPING = 3;
    private final int STATE_STOPPED = 4;
    private final int BUFFER_SIZE = 16384;
    
    public DataPusher(final SourceDataLine sourceDataLine, final AudioFormat audioFormat, final byte[] array, final int n) {
        this(sourceDataLine, audioFormat, null, array, n);
    }
    
    public DataPusher(final SourceDataLine sourceDataLine, final AudioInputStream audioInputStream) {
        this(sourceDataLine, audioInputStream.getFormat(), audioInputStream, null, 0);
    }
    
    private DataPusher(final SourceDataLine source, final AudioFormat format, final AudioInputStream ais, final byte[] array, final int audioDataByteLength) {
        this.newPos = -1;
        this.pushThread = null;
        this.source = source;
        this.format = format;
        this.ais = ais;
        this.audioDataByteLength = audioDataByteLength;
        this.audioData = (byte[])((array == null) ? null : Arrays.copyOf(array, array.length));
    }
    
    public synchronized void start() {
        this.start(false);
    }
    
    public synchronized void start(final boolean looping) {
        try {
            if (this.threadState == 3) {
                this.stop();
            }
            this.looping = looping;
            this.newPos = 0;
            this.wantedState = 1;
            if (!this.source.isOpen()) {
                this.source.open(this.format);
            }
            this.source.flush();
            this.source.start();
            if (this.pushThread == null) {
                this.pushThread = JSSecurityManager.createThread(this, null, false, -1, true);
            }
            this.notifyAll();
        }
        catch (final Exception ex) {}
    }
    
    public synchronized void stop() {
        if (this.threadState == 3 || this.threadState == 4 || this.pushThread == null) {
            return;
        }
        this.wantedState = 2;
        if (this.source != null) {
            this.source.flush();
        }
        this.notifyAll();
        int n = 50;
        while (n-- >= 0 && this.threadState == 1) {
            try {
                this.wait(100L);
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    synchronized void close() {
        if (this.source != null) {
            this.source.close();
        }
    }
    
    @Override
    public void run() {
        final boolean b = this.ais != null;
        byte[] audioData;
        if (b) {
            audioData = new byte[16384];
        }
        else {
            audioData = this.audioData;
        }
        while (this.wantedState != 3) {
            if (this.wantedState == 2) {
                try {
                    synchronized (this) {
                        this.threadState = 2;
                        this.wantedState = 3;
                        this.wait(5000L);
                    }
                }
                catch (final InterruptedException ex) {}
            }
            else {
                if (this.newPos >= 0) {
                    this.pos = this.newPos;
                    this.newPos = -1;
                }
                this.threadState = 1;
                int read = 16384;
                if (b) {
                    try {
                        this.pos = 0;
                        read = this.ais.read(audioData, 0, audioData.length);
                    }
                    catch (final IOException ex2) {
                        read = -1;
                    }
                }
                else {
                    if (read > this.audioDataByteLength - this.pos) {
                        read = this.audioDataByteLength - this.pos;
                    }
                    if (read == 0) {
                        read = -1;
                    }
                }
                if (read < 0) {
                    if (!b && this.looping) {
                        this.pos = 0;
                    }
                    else {
                        this.wantedState = 2;
                        this.source.drain();
                    }
                }
                else {
                    this.pos += this.source.write(audioData, this.pos, read);
                }
            }
        }
        this.threadState = 3;
        this.source.flush();
        this.source.stop();
        this.source.flush();
        this.source.close();
        this.threadState = 4;
        synchronized (this) {
            this.pushThread = null;
            this.notifyAll();
        }
    }
}
