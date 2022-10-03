package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public final class SoftShortMessage extends ShortMessage
{
    int channel;
    
    public SoftShortMessage() {
        this.channel = 0;
    }
    
    @Override
    public int getChannel() {
        return this.channel;
    }
    
    @Override
    public void setMessage(final int n, final int channel, final int n2, final int n3) throws InvalidMidiDataException {
        super.setMessage(n, (this.channel = channel) & 0xF, n2, n3);
    }
    
    @Override
    public Object clone() {
        final SoftShortMessage softShortMessage = new SoftShortMessage();
        try {
            softShortMessage.setMessage(this.getCommand(), this.getChannel(), this.getData1(), this.getData2());
        }
        catch (final InvalidMidiDataException ex) {
            throw new IllegalArgumentException(ex);
        }
        return softShortMessage;
    }
}
