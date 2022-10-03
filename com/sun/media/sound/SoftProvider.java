package com.sun.media.sound;

import java.util.Arrays;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

public final class SoftProvider extends MidiDeviceProvider
{
    static final MidiDevice.Info softinfo;
    private static final MidiDevice.Info[] softinfos;
    
    @Override
    public MidiDevice.Info[] getDeviceInfo() {
        return Arrays.copyOf(SoftProvider.softinfos, SoftProvider.softinfos.length);
    }
    
    @Override
    public MidiDevice getDevice(final MidiDevice.Info info) {
        if (info == SoftProvider.softinfo) {
            return new SoftSynthesizer();
        }
        return null;
    }
    
    static {
        softinfo = SoftSynthesizer.info;
        softinfos = new MidiDevice.Info[] { SoftProvider.softinfo };
    }
}
