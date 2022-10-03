package com.sun.media.sound;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceTransmitter;

public final class MidiDeviceTransmitterEnvelope implements MidiDeviceTransmitter
{
    private final MidiDevice device;
    private final Transmitter transmitter;
    
    public MidiDeviceTransmitterEnvelope(final MidiDevice device, final Transmitter transmitter) {
        if (device == null || transmitter == null) {
            throw new NullPointerException();
        }
        this.device = device;
        this.transmitter = transmitter;
    }
    
    @Override
    public void setReceiver(final Receiver receiver) {
        this.transmitter.setReceiver(receiver);
    }
    
    @Override
    public Receiver getReceiver() {
        return this.transmitter.getReceiver();
    }
    
    @Override
    public void close() {
        this.transmitter.close();
    }
    
    @Override
    public MidiDevice getMidiDevice() {
        return this.device;
    }
    
    public Transmitter getTransmitter() {
        return this.transmitter;
    }
}
