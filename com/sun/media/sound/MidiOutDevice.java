package com.sun.media.sound;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice;

final class MidiOutDevice extends AbstractMidiDevice
{
    MidiOutDevice(final AbstractMidiDeviceProvider.Info info) {
        super(info);
    }
    
    @Override
    protected synchronized void implOpen() throws MidiUnavailableException {
        this.id = this.nOpen(((AbstractMidiDeviceProvider.Info)this.getDeviceInfo()).getIndex());
        if (this.id == 0L) {
            throw new MidiUnavailableException("Unable to open native device");
        }
    }
    
    @Override
    protected synchronized void implClose() {
        final long id = this.id;
        this.id = 0L;
        super.implClose();
        this.nClose(id);
    }
    
    @Override
    public long getMicrosecondPosition() {
        long nGetTimeStamp = -1L;
        if (this.isOpen()) {
            nGetTimeStamp = this.nGetTimeStamp(this.id);
        }
        return nGetTimeStamp;
    }
    
    @Override
    protected boolean hasReceivers() {
        return true;
    }
    
    @Override
    protected Receiver createReceiver() {
        return new MidiOutReceiver();
    }
    
    private native long nOpen(final int p0) throws MidiUnavailableException;
    
    private native void nClose(final long p0);
    
    private native void nSendShortMessage(final long p0, final int p1, final long p2);
    
    private native void nSendLongMessage(final long p0, final byte[] p1, final int p2, final long p3);
    
    private native long nGetTimeStamp(final long p0);
    
    final class MidiOutReceiver extends AbstractReceiver
    {
        @Override
        void implSend(final MidiMessage midiMessage, final long n) {
            final int length = midiMessage.getLength();
            final int status = midiMessage.getStatus();
            if (length <= 3 && status != 240 && status != 247) {
                int packedMsg;
                if (midiMessage instanceof ShortMessage) {
                    if (midiMessage instanceof FastShortMessage) {
                        packedMsg = ((FastShortMessage)midiMessage).getPackedMsg();
                    }
                    else {
                        final ShortMessage shortMessage = (ShortMessage)midiMessage;
                        packedMsg = ((status & 0xFF) | (shortMessage.getData1() & 0xFF) << 8 | (shortMessage.getData2() & 0xFF) << 16);
                    }
                }
                else {
                    packedMsg = 0;
                    final byte[] message = midiMessage.getMessage();
                    if (length > 0) {
                        packedMsg = (message[0] & 0xFF);
                        if (length > 1) {
                            if (status == 255) {
                                return;
                            }
                            packedMsg |= (message[1] & 0xFF) << 8;
                            if (length > 2) {
                                packedMsg |= (message[2] & 0xFF) << 16;
                            }
                        }
                    }
                }
                MidiOutDevice.this.nSendShortMessage(MidiOutDevice.this.id, packedMsg, n);
            }
            else {
                byte[] array;
                if (midiMessage instanceof FastSysexMessage) {
                    array = ((FastSysexMessage)midiMessage).getReadOnlyMessage();
                }
                else {
                    array = midiMessage.getMessage();
                }
                final int min = Math.min(length, array.length);
                if (min > 0) {
                    MidiOutDevice.this.nSendLongMessage(MidiOutDevice.this.id, array, min, n);
                }
            }
        }
        
        synchronized void sendPackedMidiMessage(final int n, final long n2) {
            if (this.isOpen() && MidiOutDevice.this.id != 0L) {
                MidiOutDevice.this.nSendShortMessage(MidiOutDevice.this.id, n, n2);
            }
        }
    }
}
