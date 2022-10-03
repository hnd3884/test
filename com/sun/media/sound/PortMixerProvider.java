package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public final class PortMixerProvider extends MixerProvider
{
    private static PortMixerInfo[] infos;
    private static PortMixer[] devices;
    
    public PortMixerProvider() {
        synchronized (PortMixerProvider.class) {
            if (Platform.isPortsEnabled()) {
                init();
            }
            else {
                PortMixerProvider.infos = new PortMixerInfo[0];
                PortMixerProvider.devices = new PortMixer[0];
            }
        }
    }
    
    private static void init() {
        final int nGetNumDevices = nGetNumDevices();
        if (PortMixerProvider.infos == null || PortMixerProvider.infos.length != nGetNumDevices) {
            PortMixerProvider.infos = new PortMixerInfo[nGetNumDevices];
            PortMixerProvider.devices = new PortMixer[nGetNumDevices];
            for (int i = 0; i < PortMixerProvider.infos.length; ++i) {
                PortMixerProvider.infos[i] = nNewPortMixerInfo(i);
            }
        }
    }
    
    @Override
    public Mixer.Info[] getMixerInfo() {
        synchronized (PortMixerProvider.class) {
            final Mixer.Info[] array = new Mixer.Info[PortMixerProvider.infos.length];
            System.arraycopy(PortMixerProvider.infos, 0, array, 0, PortMixerProvider.infos.length);
            return array;
        }
    }
    
    @Override
    public Mixer getMixer(final Mixer.Info info) {
        synchronized (PortMixerProvider.class) {
            for (int i = 0; i < PortMixerProvider.infos.length; ++i) {
                if (PortMixerProvider.infos[i].equals(info)) {
                    return getDevice(PortMixerProvider.infos[i]);
                }
            }
        }
        throw new IllegalArgumentException("Mixer " + info.toString() + " not supported by this provider.");
    }
    
    private static Mixer getDevice(final PortMixerInfo portMixerInfo) {
        final int index = portMixerInfo.getIndex();
        if (PortMixerProvider.devices[index] == null) {
            PortMixerProvider.devices[index] = new PortMixer(portMixerInfo);
        }
        return PortMixerProvider.devices[index];
    }
    
    private static native int nGetNumDevices();
    
    private static native PortMixerInfo nNewPortMixerInfo(final int p0);
    
    static {
        Platform.initialize();
    }
    
    static final class PortMixerInfo extends Mixer.Info
    {
        private final int index;
        
        private PortMixerInfo(final int index, final String s, final String s2, final String s3, final String s4) {
            super("Port " + s, s2, s3, s4);
            this.index = index;
        }
        
        int getIndex() {
            return this.index;
        }
    }
}
