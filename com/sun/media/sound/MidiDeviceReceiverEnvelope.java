package com.sun.media.sound;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceReceiver;

public final class MidiDeviceReceiverEnvelope implements MidiDeviceReceiver
{
    private final MidiDevice device;
    private final Receiver receiver;
    
    public MidiDeviceReceiverEnvelope(final MidiDevice device, final Receiver receiver) {
        if (device == null || receiver == null) {
            throw new NullPointerException();
        }
        this.device = device;
        this.receiver = receiver;
    }
    
    @Override
    public void close() {
        this.receiver.close();
    }
    
    @Override
    public void send(final MidiMessage midiMessage, final long n) {
        this.receiver.send(midiMessage, n);
    }
    
    @Override
    public MidiDevice getMidiDevice() {
        return this.device;
    }
    
    public Receiver getReceiver() {
        return this.receiver;
    }
}
