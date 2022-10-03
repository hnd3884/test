package com.sun.media.sound;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

public final class RealTimeSequencerProvider extends MidiDeviceProvider
{
    @Override
    public MidiDevice.Info[] getDeviceInfo() {
        return new MidiDevice.Info[] { RealTimeSequencer.info };
    }
    
    @Override
    public MidiDevice getDevice(final MidiDevice.Info info) {
        if (info != null && !info.equals(RealTimeSequencer.info)) {
            return null;
        }
        try {
            return new RealTimeSequencer();
        }
        catch (final MidiUnavailableException ex) {
            return null;
        }
    }
}
