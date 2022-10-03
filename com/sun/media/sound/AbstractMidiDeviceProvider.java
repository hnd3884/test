package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

public abstract class AbstractMidiDeviceProvider extends MidiDeviceProvider
{
    private static final boolean enabled;
    
    final synchronized void readDeviceInfos() {
        final Info[] infoCache = this.getInfoCache();
        final MidiDevice[] deviceCache = this.getDeviceCache();
        if (!AbstractMidiDeviceProvider.enabled) {
            if (infoCache == null || infoCache.length != 0) {
                this.setInfoCache(new Info[0]);
            }
            if (deviceCache == null || deviceCache.length != 0) {
                this.setDeviceCache(new MidiDevice[0]);
            }
            return;
        }
        final int n = (infoCache == null) ? -1 : infoCache.length;
        final int numDevices = this.getNumDevices();
        if (n != numDevices) {
            final Info[] infoCache2 = new Info[numDevices];
            final MidiDevice[] deviceCache2 = new MidiDevice[numDevices];
            for (int i = 0; i < numDevices; ++i) {
                final Info info = this.createInfo(i);
                if (infoCache != null) {
                    for (int j = 0; j < infoCache.length; ++j) {
                        final Info info2 = infoCache[j];
                        if (info2 != null && info2.equalStrings(info)) {
                            (infoCache2[i] = info2).setIndex(i);
                            infoCache[j] = null;
                            deviceCache2[i] = deviceCache[j];
                            deviceCache[j] = null;
                            break;
                        }
                    }
                }
                if (infoCache2[i] == null) {
                    infoCache2[i] = info;
                }
            }
            if (infoCache != null) {
                for (int k = 0; k < infoCache.length; ++k) {
                    if (infoCache[k] != null) {
                        infoCache[k].setIndex(-1);
                    }
                }
            }
            this.setInfoCache(infoCache2);
            this.setDeviceCache(deviceCache2);
        }
    }
    
    @Override
    public final MidiDevice.Info[] getDeviceInfo() {
        this.readDeviceInfos();
        final Info[] infoCache = this.getInfoCache();
        final MidiDevice.Info[] array = new MidiDevice.Info[infoCache.length];
        System.arraycopy(infoCache, 0, array, 0, infoCache.length);
        return array;
    }
    
    @Override
    public final MidiDevice getDevice(final MidiDevice.Info info) {
        if (info instanceof Info) {
            this.readDeviceInfos();
            final MidiDevice[] deviceCache = this.getDeviceCache();
            final Info[] infoCache = this.getInfoCache();
            final Info info2 = (Info)info;
            final int index = info2.getIndex();
            if (index >= 0 && index < deviceCache.length && infoCache[index] == info) {
                if (deviceCache[index] == null) {
                    deviceCache[index] = this.createDevice(info2);
                }
                if (deviceCache[index] != null) {
                    return deviceCache[index];
                }
            }
        }
        throw new IllegalArgumentException("MidiDevice " + info.toString() + " not supported by this provider.");
    }
    
    abstract int getNumDevices();
    
    abstract MidiDevice[] getDeviceCache();
    
    abstract void setDeviceCache(final MidiDevice[] p0);
    
    abstract Info[] getInfoCache();
    
    abstract void setInfoCache(final Info[] p0);
    
    abstract Info createInfo(final int p0);
    
    abstract MidiDevice createDevice(final Info p0);
    
    static {
        Platform.initialize();
        enabled = Platform.isMidiIOEnabled();
    }
    
    static class Info extends MidiDevice.Info
    {
        private int index;
        
        Info(final String s, final String s2, final String s3, final String s4, final int index) {
            super(s, s2, s3, s4);
            this.index = index;
        }
        
        final boolean equalStrings(final Info info) {
            return info != null && this.getName().equals(info.getName()) && this.getVendor().equals(info.getVendor()) && this.getDescription().equals(info.getDescription()) && this.getVersion().equals(info.getVersion());
        }
        
        final int getIndex() {
            return this.index;
        }
        
        final void setIndex(final int index) {
            this.index = index;
        }
    }
}
