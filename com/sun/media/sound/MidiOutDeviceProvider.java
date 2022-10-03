package com.sun.media.sound;

import javax.sound.midi.MidiDevice;

public final class MidiOutDeviceProvider extends AbstractMidiDeviceProvider
{
    private static Info[] infos;
    private static MidiDevice[] devices;
    private static final boolean enabled;
    
    @Override
    Info createInfo(final int n) {
        if (!MidiOutDeviceProvider.enabled) {
            return null;
        }
        return new MidiOutDeviceInfo(n, (Class)MidiOutDeviceProvider.class);
    }
    
    @Override
    MidiDevice createDevice(final Info info) {
        if (MidiOutDeviceProvider.enabled && info instanceof MidiOutDeviceInfo) {
            return new MidiOutDevice(info);
        }
        return null;
    }
    
    @Override
    int getNumDevices() {
        if (!MidiOutDeviceProvider.enabled) {
            return 0;
        }
        return nGetNumDevices();
    }
    
    @Override
    MidiDevice[] getDeviceCache() {
        return MidiOutDeviceProvider.devices;
    }
    
    @Override
    void setDeviceCache(final MidiDevice[] devices) {
        MidiOutDeviceProvider.devices = devices;
    }
    
    @Override
    Info[] getInfoCache() {
        return MidiOutDeviceProvider.infos;
    }
    
    @Override
    void setInfoCache(final Info[] infos) {
        MidiOutDeviceProvider.infos = infos;
    }
    
    private static native int nGetNumDevices();
    
    private static native String nGetName(final int p0);
    
    private static native String nGetVendor(final int p0);
    
    private static native String nGetDescription(final int p0);
    
    private static native String nGetVersion(final int p0);
    
    static {
        MidiOutDeviceProvider.infos = null;
        MidiOutDeviceProvider.devices = null;
        Platform.initialize();
        enabled = Platform.isMidiIOEnabled();
    }
    
    static final class MidiOutDeviceInfo extends Info
    {
        private final Class providerClass;
        
        private MidiOutDeviceInfo(final int n, final Class providerClass) {
            super(nGetName(n), nGetVendor(n), nGetDescription(n), nGetVersion(n), n);
            this.providerClass = providerClass;
        }
    }
}
