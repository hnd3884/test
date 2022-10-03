package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

final class FastSysexMessage extends SysexMessage
{
    FastSysexMessage(final byte[] array) throws InvalidMidiDataException {
        super(array);
        if (array.length == 0 || ((array[0] & 0xFF) != 0xF0 && (array[0] & 0xFF) != 0xF7)) {
            super.setMessage(array, array.length);
        }
    }
    
    byte[] getReadOnlyMessage() {
        return this.data;
    }
    
    @Override
    public void setMessage(final byte[] array, final int length) throws InvalidMidiDataException {
        if (array.length == 0 || ((array[0] & 0xFF) != 0xF0 && (array[0] & 0xFF) != 0xF7)) {
            super.setMessage(array, array.length);
        }
        this.length = length;
        System.arraycopy(array, 0, this.data = new byte[this.length], 0, length);
    }
}
