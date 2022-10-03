package com.sun.media.sound;

import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

public interface ReferenceCountingDevice
{
    Receiver getReceiverReferenceCounting() throws MidiUnavailableException;
    
    Transmitter getTransmitterReferenceCounting() throws MidiUnavailableException;
}
