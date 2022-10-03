package com.sun.media.sound;

import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiDevice;
import java.util.TreeMap;
import javax.sound.midi.MidiDeviceReceiver;

public final class SoftReceiver implements MidiDeviceReceiver
{
    boolean open;
    private final Object control_mutex;
    private final SoftSynthesizer synth;
    TreeMap<Long, Object> midimessages;
    SoftMainMixer mainmixer;
    
    public SoftReceiver(final SoftSynthesizer synth) {
        this.open = true;
        this.control_mutex = synth.control_mutex;
        this.synth = synth;
        this.mainmixer = synth.getMainMixer();
        if (this.mainmixer != null) {
            this.midimessages = this.mainmixer.midimessages;
        }
    }
    
    @Override
    public MidiDevice getMidiDevice() {
        return this.synth;
    }
    
    @Override
    public void send(final MidiMessage midiMessage, long n) {
        synchronized (this.control_mutex) {
            if (!this.open) {
                throw new IllegalStateException("Receiver is not open");
            }
        }
        if (n != -1L) {
            synchronized (this.control_mutex) {
                this.mainmixer.activity();
                while (this.midimessages.get(n) != null) {
                    ++n;
                }
                if (midiMessage instanceof ShortMessage && ((ShortMessage)midiMessage).getChannel() > 15) {
                    this.midimessages.put(n, midiMessage.clone());
                }
                else {
                    this.midimessages.put(n, midiMessage.getMessage());
                }
            }
        }
        else {
            this.mainmixer.processMessage(midiMessage);
        }
    }
    
    @Override
    public void close() {
        synchronized (this.control_mutex) {
            this.open = false;
        }
        this.synth.removeReceiver(this);
    }
}
