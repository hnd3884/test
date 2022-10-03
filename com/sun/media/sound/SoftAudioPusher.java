package com.sun.media.sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public final class SoftAudioPusher implements Runnable
{
    private volatile boolean active;
    private SourceDataLine sourceDataLine;
    private Thread audiothread;
    private final AudioInputStream ais;
    private final byte[] buffer;
    
    public SoftAudioPusher(final SourceDataLine sourceDataLine, final AudioInputStream ais, final int n) {
        this.active = false;
        this.sourceDataLine = null;
        this.ais = ais;
        this.buffer = new byte[n];
        this.sourceDataLine = sourceDataLine;
    }
    
    public synchronized void start() {
        if (this.active) {
            return;
        }
        this.active = true;
        (this.audiothread = new Thread(this)).setDaemon(true);
        this.audiothread.setPriority(10);
        this.audiothread.start();
    }
    
    public synchronized void stop() {
        if (!this.active) {
            return;
        }
        this.active = false;
        try {
            this.audiothread.join();
        }
        catch (final InterruptedException ex) {}
    }
    
    @Override
    public void run() {
        final byte[] buffer = this.buffer;
        final AudioInputStream ais = this.ais;
        final SourceDataLine sourceDataLine = this.sourceDataLine;
        try {
            while (this.active) {
                final int read = ais.read(buffer);
                if (read < 0) {
                    break;
                }
                sourceDataLine.write(buffer, 0, read);
            }
        }
        catch (final IOException ex) {
            this.active = false;
        }
    }
}
