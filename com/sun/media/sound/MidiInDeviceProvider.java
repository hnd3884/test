package com.sun.media.sound;

import javax.sound.midi.MidiDevice;

public final class MidiInDeviceProvider extends AbstractMidiDeviceProvider
{
    private static Info[] infos;
    private static MidiDevice[] devices;
    private static final boolean enabled;
    
    @Override
    Info createInfo(final int n) {
        if (!MidiInDeviceProvider.enabled) {
            return null;
        }
        return new MidiInDeviceInfo(n, (Class)MidiInDeviceProvider.class);
    }
    
    @Override
    MidiDevice createDevice(final Info info) {
        if (MidiInDeviceProvider.enabled && info instanceof MidiInDeviceInfo) {
            return new MidiInDevice(info);
        }
        return null;
    }
    
    @Override
    int getNumDevices() {
        if (!MidiInDeviceProvider.enabled) {
            return 0;
        }
        return nGetNumDevices();
    }
    
    @Override
    MidiDevice[] getDeviceCache() {
        return MidiInDeviceProvider.devices;
    }
    
    @Override
    void setDeviceCache(final MidiDevice[] devices) {
        MidiInDeviceProvider.devices = devices;
    }
    
    @Override
    Info[] getInfoCache() {
        return MidiInDeviceProvider.infos;
    }
    
    @Override
    void setInfoCache(final Info[] infos) {
        MidiInDeviceProvider.infos = infos;
    }
    
    private static native int nGetNumDevices();
    
    private static native String nGetName(final int p0);
    
    private static native String nGetVendor(final int p0);
    
    private static native String nGetDescription(final int p0);
    
    private static native String nGetVersion(final int p0);
    
    static {
        MidiInDeviceProvider.infos = null;
        MidiInDeviceProvider.devices = null;
        Platform.initialize();
        enabled = Platform.isMidiIOEnabled();
    }
    
    static final class MidiInDeviceInfo extends Info
    {
        private final Class providerClass;
        
        private MidiInDeviceInfo(final int n, final Class providerClass) {
            super(nGetName(n), nGetVendor(n), nGetDescription(n), nGetVersion(n), n);
            this.providerClass = providerClass;
        }
    }
}
