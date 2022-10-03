package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public final class DirectAudioDeviceProvider extends MixerProvider
{
    private static DirectAudioDeviceInfo[] infos;
    private static DirectAudioDevice[] devices;
    
    public DirectAudioDeviceProvider() {
        synchronized (DirectAudioDeviceProvider.class) {
            if (Platform.isDirectAudioEnabled()) {
                init();
            }
            else {
                DirectAudioDeviceProvider.infos = new DirectAudioDeviceInfo[0];
                DirectAudioDeviceProvider.devices = new DirectAudioDevice[0];
            }
        }
    }
    
    private static void init() {
        final int nGetNumDevices = nGetNumDevices();
        if (DirectAudioDeviceProvider.infos == null || DirectAudioDeviceProvider.infos.length != nGetNumDevices) {
            DirectAudioDeviceProvider.infos = new DirectAudioDeviceInfo[nGetNumDevices];
            DirectAudioDeviceProvider.devices = new DirectAudioDevice[nGetNumDevices];
            for (int i = 0; i < DirectAudioDeviceProvider.infos.length; ++i) {
                DirectAudioDeviceProvider.infos[i] = nNewDirectAudioDeviceInfo(i);
            }
        }
    }
    
    @Override
    public Mixer.Info[] getMixerInfo() {
        synchronized (DirectAudioDeviceProvider.class) {
            final Mixer.Info[] array = new Mixer.Info[DirectAudioDeviceProvider.infos.length];
            System.arraycopy(DirectAudioDeviceProvider.infos, 0, array, 0, DirectAudioDeviceProvider.infos.length);
            return array;
        }
    }
    
    @Override
    public Mixer getMixer(final Mixer.Info info) {
        synchronized (DirectAudioDeviceProvider.class) {
            if (info == null) {
                for (int i = 0; i < DirectAudioDeviceProvider.infos.length; ++i) {
                    final Mixer device = getDevice(DirectAudioDeviceProvider.infos[i]);
                    if (device.getSourceLineInfo().length > 0) {
                        return device;
                    }
                }
            }
            for (int j = 0; j < DirectAudioDeviceProvider.infos.length; ++j) {
                if (DirectAudioDeviceProvider.infos[j].equals(info)) {
                    return getDevice(DirectAudioDeviceProvider.infos[j]);
                }
            }
        }
        throw new IllegalArgumentException("Mixer " + info.toString() + " not supported by this provider.");
    }
    
    private static Mixer getDevice(final DirectAudioDeviceInfo directAudioDeviceInfo) {
        final int index = directAudioDeviceInfo.getIndex();
        if (DirectAudioDeviceProvider.devices[index] == null) {
            DirectAudioDeviceProvider.devices[index] = new DirectAudioDevice(directAudioDeviceInfo);
        }
        return DirectAudioDeviceProvider.devices[index];
    }
    
    private static native int nGetNumDevices();
    
    private static native DirectAudioDeviceInfo nNewDirectAudioDeviceInfo(final int p0);
    
    static {
        Platform.initialize();
    }
    
    static final class DirectAudioDeviceInfo extends Mixer.Info
    {
        private final int index;
        private final int maxSimulLines;
        private final int deviceID;
        
        private DirectAudioDeviceInfo(final int index, final int deviceID, final int maxSimulLines, final String s, final String s2, final String s3, final String s4) {
            super(s, s2, "Direct Audio Device: " + s3, s4);
            this.index = index;
            this.maxSimulLines = maxSimulLines;
            this.deviceID = deviceID;
        }
        
        int getIndex() {
            return this.index;
        }
        
        int getMaxSimulLines() {
            return this.maxSimulLines;
        }
        
        int getDeviceID() {
            return this.deviceID;
        }
    }
}
