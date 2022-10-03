package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

final class FastShortMessage extends ShortMessage
{
    private int packedMsg;
    
    FastShortMessage(final int packedMsg) throws InvalidMidiDataException {
        this.getDataLength((this.packedMsg = packedMsg) & 0xFF);
    }
    
    FastShortMessage(final ShortMessage shortMessage) {
        this.packedMsg = (shortMessage.getStatus() | shortMessage.getData1() << 8 | shortMessage.getData2() << 16);
    }
    
    int getPackedMsg() {
        return this.packedMsg;
    }
    
    @Override
    public byte[] getMessage() {
        int n = 0;
        try {
            n = this.getDataLength(this.packedMsg & 0xFF) + 1;
        }
        catch (final InvalidMidiDataException ex) {}
        final byte[] array = new byte[n];
        if (n > 0) {
            array[0] = (byte)(this.packedMsg & 0xFF);
            if (n > 1) {
                array[1] = (byte)((this.packedMsg & 0xFF00) >> 8);
                if (n > 2) {
                    array[2] = (byte)((this.packedMsg & 0xFF0000) >> 16);
                }
            }
        }
        return array;
    }
    
    @Override
    public int getLength() {
        try {
            return this.getDataLength(this.packedMsg & 0xFF) + 1;
        }
        catch (final InvalidMidiDataException ex) {
            return 0;
        }
    }
    
    @Override
    public void setMessage(final int message) throws InvalidMidiDataException {
        if (this.getDataLength(message) != 0) {
            super.setMessage(message);
        }
        this.packedMsg = ((this.packedMsg & 0xFFFF00) | (message & 0xFF));
    }
    
    @Override
    public void setMessage(final int n, final int n2, final int n3) throws InvalidMidiDataException {
        this.getDataLength(n);
        this.packedMsg = ((n & 0xFF) | (n2 & 0xFF) << 8 | (n3 & 0xFF) << 16);
    }
    
    @Override
    public void setMessage(final int n, final int n2, final int n3, final int n4) throws InvalidMidiDataException {
        this.getDataLength(n);
        this.packedMsg = ((n & 0xF0) | (n2 & 0xF) | (n3 & 0xFF) << 8 | (n4 & 0xFF) << 16);
    }
    
    @Override
    public int getChannel() {
        return this.packedMsg & 0xF;
    }
    
    @Override
    public int getCommand() {
        return this.packedMsg & 0xF0;
    }
    
    @Override
    public int getData1() {
        return (this.packedMsg & 0xFF00) >> 8;
    }
    
    @Override
    public int getData2() {
        return (this.packedMsg & 0xFF0000) >> 16;
    }
    
    @Override
    public int getStatus() {
        return this.packedMsg & 0xFF;
    }
    
    @Override
    public Object clone() {
        try {
            return new FastShortMessage(this.packedMsg);
        }
        catch (final InvalidMidiDataException ex) {
            return null;
        }
    }
}
