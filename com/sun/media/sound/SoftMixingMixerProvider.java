package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public final class SoftMixingMixerProvider extends MixerProvider
{
    static SoftMixingMixer globalmixer;
    static Thread lockthread;
    static final Object mutex;
    
    @Override
    public Mixer getMixer(final Mixer.Info info) {
        if (info != null && info != SoftMixingMixer.info) {
            throw new IllegalArgumentException("Mixer " + info.toString() + " not supported by this provider.");
        }
        synchronized (SoftMixingMixerProvider.mutex) {
            if (SoftMixingMixerProvider.lockthread != null && Thread.currentThread() == SoftMixingMixerProvider.lockthread) {
                throw new IllegalArgumentException("Mixer " + info.toString() + " not supported by this provider.");
            }
            if (SoftMixingMixerProvider.globalmixer == null) {
                SoftMixingMixerProvider.globalmixer = new SoftMixingMixer();
            }
            return SoftMixingMixerProvider.globalmixer;
        }
    }
    
    @Override
    public Mixer.Info[] getMixerInfo() {
        return new Mixer.Info[] { SoftMixingMixer.info };
    }
    
    static {
        SoftMixingMixerProvider.globalmixer = null;
        SoftMixingMixerProvider.lockthread = null;
        mutex = new Object();
    }
}
