package com.sun.media.sound;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.sound.midi.InvalidMidiDataException;
import java.io.EOFException;
import java.io.IOException;
import java.io.DataInputStream;

final class SMFParser
{
    private static final int MTrk_MAGIC = 1297379947;
    private static final boolean STRICT_PARSER = false;
    private static final boolean DEBUG = false;
    int tracks;
    DataInputStream stream;
    private int trackLength;
    private byte[] trackData;
    private int pos;
    
    SMFParser() {
        this.trackLength = 0;
        this.trackData = null;
        this.pos = 0;
    }
    
    private int readUnsigned() throws IOException {
        return this.trackData[this.pos++] & 0xFF;
    }
    
    private void read(final byte[] array) throws IOException {
        System.arraycopy(this.trackData, this.pos, array, 0, array.length);
        this.pos += array.length;
    }
    
    private long readVarInt() throws IOException {
        long n = 0L;
        int n2;
        do {
            n2 = (this.trackData[this.pos++] & 0xFF);
            n = (n << 7) + (n2 & 0x7F);
        } while ((n2 & 0x80) != 0x0);
        return n;
    }
    
    private int readIntFromStream() throws IOException {
        try {
            return this.stream.readInt();
        }
        catch (final EOFException ex) {
            throw new EOFException("invalid MIDI file");
        }
    }
    
    boolean nextTrack() throws IOException, InvalidMidiDataException {
        this.trackLength = 0;
        while (this.stream.skipBytes(this.trackLength) == this.trackLength) {
            final int intFromStream = this.readIntFromStream();
            this.trackLength = this.readIntFromStream();
            if (intFromStream == 1297379947) {
                if (this.trackLength < 0) {
                    return false;
                }
                try {
                    this.trackData = new byte[this.trackLength];
                }
                catch (final OutOfMemoryError outOfMemoryError) {
                    throw new IOException("Track length too big", outOfMemoryError);
                }
                try {
                    this.stream.readFully(this.trackData);
                }
                catch (final EOFException ex) {
                    return false;
                }
                this.pos = 0;
                return true;
            }
        }
        return false;
    }
    
    private boolean trackFinished() {
        return this.pos >= this.trackLength;
    }
    
    void readTrack(final Track track) throws IOException, InvalidMidiDataException {
        try {
            long n = 0L;
            int n2 = 0;
            int n3 = 0;
            while (!this.trackFinished() && n3 == 0) {
                int n4 = -1;
                n += this.readVarInt();
                final int unsigned = this.readUnsigned();
                if (unsigned >= 128) {
                    n2 = unsigned;
                }
                else {
                    n4 = unsigned;
                }
                MidiMessage midiMessage = null;
                Label_0433: {
                    switch (n2 & 0xF0) {
                        case 128:
                        case 144:
                        case 160:
                        case 176:
                        case 224: {
                            if (n4 == -1) {
                                n4 = this.readUnsigned();
                            }
                            midiMessage = new FastShortMessage(n2 | n4 << 8 | this.readUnsigned() << 16);
                            break;
                        }
                        case 192:
                        case 208: {
                            if (n4 == -1) {
                                n4 = this.readUnsigned();
                            }
                            midiMessage = new FastShortMessage(n2 | n4 << 8);
                            break;
                        }
                        case 240: {
                            switch (n2) {
                                case 240:
                                case 247: {
                                    final int n5 = (int)this.readVarInt();
                                    final byte[] array = new byte[n5];
                                    this.read(array);
                                    final SysexMessage sysexMessage = new SysexMessage();
                                    sysexMessage.setMessage(n2, array, n5);
                                    midiMessage = sysexMessage;
                                    break Label_0433;
                                }
                                case 255: {
                                    final int unsigned2 = this.readUnsigned();
                                    final int n6 = (int)this.readVarInt();
                                    byte[] array2;
                                    try {
                                        array2 = new byte[n6];
                                    }
                                    catch (final OutOfMemoryError outOfMemoryError) {
                                        throw new IOException("Meta length too big", outOfMemoryError);
                                    }
                                    this.read(array2);
                                    final MetaMessage metaMessage = new MetaMessage();
                                    metaMessage.setMessage(unsigned2, array2, n6);
                                    midiMessage = metaMessage;
                                    if (unsigned2 == 47) {
                                        n3 = 1;
                                        break Label_0433;
                                    }
                                    break Label_0433;
                                }
                                default: {
                                    throw new InvalidMidiDataException("Invalid status byte: " + n2);
                                }
                            }
                            break;
                        }
                        default: {
                            throw new InvalidMidiDataException("Invalid status byte: " + n2);
                        }
                    }
                }
                track.add(new MidiEvent(midiMessage, n));
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new EOFException("invalid MIDI file");
        }
    }
}
