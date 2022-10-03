package javax.sound.sampled.spi;

import javax.sound.sampled.Mixer;

public abstract class MixerProvider
{
    public boolean isMixerSupported(final Mixer.Info info) {
        final Mixer.Info[] mixerInfo = this.getMixerInfo();
        for (int i = 0; i < mixerInfo.length; ++i) {
            if (info.equals(mixerInfo[i])) {
                return true;
            }
        }
        return false;
    }
    
    public abstract Mixer.Info[] getMixerInfo();
    
    public abstract Mixer getMixer(final Mixer.Info p0);
}
