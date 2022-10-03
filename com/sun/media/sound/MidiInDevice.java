package com.sun.media.sound;

import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice;

final class MidiInDevice extends AbstractMidiDevice implements Runnable
{
    private volatile Thread midiInThread;
    
    MidiInDevice(final AbstractMidiDeviceProvider.Info info) {
        super(info);
    }
    
    @Override
    protected synchronized void implOpen() throws MidiUnavailableException {
        this.id = this.nOpen(((MidiInDeviceProvider.MidiInDeviceInfo)this.getDeviceInfo()).getIndex());
        if (this.id == 0L) {
            throw new MidiUnavailableException("Unable to open native device");
        }
        if (this.midiInThread == null) {
            this.midiInThread = JSSecurityManager.createThread(this, "Java Sound MidiInDevice Thread", false, -1, true);
        }
        this.nStart(this.id);
    }
    
    @Override
    protected synchronized void implClose() {
        final long id = this.id;
        this.id = 0L;
        super.implClose();
        this.nStop(id);
        if (this.midiInThread != null) {
            try {
                this.midiInThread.join(1000L);
            }
            catch (final InterruptedException ex) {}
        }
        this.nClose(id);
    }
    
    @Override
    public long getMicrosecondPosition() {
        long nGetTimeStamp = -1L;
        if (this.isOpen()) {
            nGetTimeStamp = this.nGetTimeStamp(this.id);
        }
        return nGetTimeStamp;
    }
    
    @Override
    protected boolean hasTransmitters() {
        return true;
    }
    
    @Override
    protected Transmitter createTransmitter() {
        return new MidiInTransmitter();
    }
    
    @Override
    public void run() {
        while (this.id != 0L) {
            this.nGetMessages(this.id);
            if (this.id != 0L) {
                try {
                    Thread.sleep(1L);
                }
                catch (final InterruptedException ex) {}
            }
        }
        this.midiInThread = null;
    }
    
    void callbackShortMessage(final int n, final long n2) {
        if (n == 0 || this.id == 0L) {
            return;
        }
        this.getTransmitterList().sendMessage(n, n2);
    }
    
    void callbackLongMessage(final byte[] array, final long n) {
        if (this.id == 0L || array == null) {
            return;
        }
        this.getTransmitterList().sendMessage(array, n);
    }
    
    private native long nOpen(final int p0) throws MidiUnavailableException;
    
    private native void nClose(final long p0);
    
    private native void nStart(final long p0) throws MidiUnavailableException;
    
    private native void nStop(final long p0);
    
    private native long nGetTimeStamp(final long p0);
    
    private native void nGetMessages(final long p0);
    
    private final class MidiInTransmitter extends BasicTransmitter
    {
    }
}
