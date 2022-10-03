package com.sun.media.sound;

import javax.sound.midi.MidiChannel;

public final class SoftChannelProxy implements MidiChannel
{
    private MidiChannel channel;
    
    public SoftChannelProxy() {
        this.channel = null;
    }
    
    public MidiChannel getChannel() {
        return this.channel;
    }
    
    public void setChannel(final MidiChannel channel) {
        this.channel = channel;
    }
    
    @Override
    public void allNotesOff() {
        if (this.channel == null) {
            return;
        }
        this.channel.allNotesOff();
    }
    
    @Override
    public void allSoundOff() {
        if (this.channel == null) {
            return;
        }
        this.channel.allSoundOff();
    }
    
    @Override
    public void controlChange(final int n, final int n2) {
        if (this.channel == null) {
            return;
        }
        this.channel.controlChange(n, n2);
    }
    
    @Override
    public int getChannelPressure() {
        if (this.channel == null) {
            return 0;
        }
        return this.channel.getChannelPressure();
    }
    
    @Override
    public int getController(final int n) {
        if (this.channel == null) {
            return 0;
        }
        return this.channel.getController(n);
    }
    
    @Override
    public boolean getMono() {
        return this.channel != null && this.channel.getMono();
    }
    
    @Override
    public boolean getMute() {
        return this.channel != null && this.channel.getMute();
    }
    
    @Override
    public boolean getOmni() {
        return this.channel != null && this.channel.getOmni();
    }
    
    @Override
    public int getPitchBend() {
        if (this.channel == null) {
            return 8192;
        }
        return this.channel.getPitchBend();
    }
    
    @Override
    public int getPolyPressure(final int n) {
        if (this.channel == null) {
            return 0;
        }
        return this.channel.getPolyPressure(n);
    }
    
    @Override
    public int getProgram() {
        if (this.channel == null) {
            return 0;
        }
        return this.channel.getProgram();
    }
    
    @Override
    public boolean getSolo() {
        return this.channel != null && this.channel.getSolo();
    }
    
    @Override
    public boolean localControl(final boolean b) {
        return this.channel != null && this.channel.localControl(b);
    }
    
    @Override
    public void noteOff(final int n) {
        if (this.channel == null) {
            return;
        }
        this.channel.noteOff(n);
    }
    
    @Override
    public void noteOff(final int n, final int n2) {
        if (this.channel == null) {
            return;
        }
        this.channel.noteOff(n, n2);
    }
    
    @Override
    public void noteOn(final int n, final int n2) {
        if (this.channel == null) {
            return;
        }
        this.channel.noteOn(n, n2);
    }
    
    @Override
    public void programChange(final int n) {
        if (this.channel == null) {
            return;
        }
        this.channel.programChange(n);
    }
    
    @Override
    public void programChange(final int n, final int n2) {
        if (this.channel == null) {
            return;
        }
        this.channel.programChange(n, n2);
    }
    
    @Override
    public void resetAllControllers() {
        if (this.channel == null) {
            return;
        }
        this.channel.resetAllControllers();
    }
    
    @Override
    public void setChannelPressure(final int channelPressure) {
        if (this.channel == null) {
            return;
        }
        this.channel.setChannelPressure(channelPressure);
    }
    
    @Override
    public void setMono(final boolean mono) {
        if (this.channel == null) {
            return;
        }
        this.channel.setMono(mono);
    }
    
    @Override
    public void setMute(final boolean mute) {
        if (this.channel == null) {
            return;
        }
        this.channel.setMute(mute);
    }
    
    @Override
    public void setOmni(final boolean omni) {
        if (this.channel == null) {
            return;
        }
        this.channel.setOmni(omni);
    }
    
    @Override
    public void setPitchBend(final int pitchBend) {
        if (this.channel == null) {
            return;
        }
        this.channel.setPitchBend(pitchBend);
    }
    
    @Override
    public void setPolyPressure(final int n, final int n2) {
        if (this.channel == null) {
            return;
        }
        this.channel.setPolyPressure(n, n2);
    }
    
    @Override
    public void setSolo(final boolean solo) {
        if (this.channel == null) {
            return;
        }
        this.channel.setSolo(solo);
    }
}
