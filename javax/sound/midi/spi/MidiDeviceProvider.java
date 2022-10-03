package javax.sound.midi.spi;

import javax.sound.midi.MidiDevice;

public abstract class MidiDeviceProvider
{
    public boolean isDeviceSupported(final MidiDevice.Info info) {
        final MidiDevice.Info[] deviceInfo = this.getDeviceInfo();
        for (int i = 0; i < deviceInfo.length; ++i) {
            if (info.equals(deviceInfo[i])) {
                return true;
            }
        }
        return false;
    }
    
    public abstract MidiDevice.Info[] getDeviceInfo();
    
    public abstract MidiDevice getDevice(final MidiDevice.Info p0);
}
