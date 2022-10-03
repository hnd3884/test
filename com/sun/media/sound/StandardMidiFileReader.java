package com.sun.media.sound;

import javax.sound.midi.Sequence;
import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.DataInputStream;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import java.io.InputStream;
import javax.sound.midi.spi.MidiFileReader;

public final class StandardMidiFileReader extends MidiFileReader
{
    private static final int MThd_MAGIC = 1297377380;
    private static final int bisBufferSize = 1024;
    
    @Override
    public MidiFileFormat getMidiFileFormat(final InputStream inputStream) throws InvalidMidiDataException, IOException {
        return this.getMidiFileFormatFromStream(inputStream, -1, null);
    }
    
    private MidiFileFormat getMidiFileFormatFromStream(final InputStream inputStream, final int n, final SMFParser smfParser) throws InvalidMidiDataException, IOException {
        final int n2 = 16;
        final int n3 = -1;
        DataInputStream stream;
        if (inputStream instanceof DataInputStream) {
            stream = (DataInputStream)inputStream;
        }
        else {
            stream = new DataInputStream(inputStream);
        }
        if (smfParser == null) {
            stream.mark(n2);
        }
        else {
            smfParser.stream = stream;
        }
        short short1;
        float n5;
        int n6;
        try {
            if (stream.readInt() != 1297377380) {
                throw new InvalidMidiDataException("not a valid MIDI file");
            }
            final int n4 = stream.readInt() - 6;
            short1 = stream.readShort();
            final short short2 = stream.readShort();
            final short short3 = stream.readShort();
            if (short3 > 0) {
                n5 = 0.0f;
                n6 = short3;
            }
            else {
                final int n7 = -1 * (short3 >> 8);
                switch (n7) {
                    case 24: {
                        n5 = 24.0f;
                        break;
                    }
                    case 25: {
                        n5 = 25.0f;
                        break;
                    }
                    case 29: {
                        n5 = 29.97f;
                        break;
                    }
                    case 30: {
                        n5 = 30.0f;
                        break;
                    }
                    default: {
                        throw new InvalidMidiDataException("Unknown frame code: " + n7);
                    }
                }
                n6 = (short3 & 0xFF);
            }
            if (smfParser != null) {
                stream.skip(n4);
                smfParser.tracks = short2;
            }
        }
        finally {
            if (smfParser == null) {
                stream.reset();
            }
        }
        return new MidiFileFormat(short1, n5, n6, n, n3);
    }
    
    @Override
    public MidiFileFormat getMidiFileFormat(final URL url) throws InvalidMidiDataException, IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(url.openStream(), 1024);
        MidiFileFormat midiFileFormat = null;
        try {
            midiFileFormat = this.getMidiFileFormat(bufferedInputStream);
        }
        finally {
            bufferedInputStream.close();
        }
        return midiFileFormat;
    }
    
    @Override
    public MidiFileFormat getMidiFileFormat(final File file) throws InvalidMidiDataException, IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file), 1024);
        long length = file.length();
        if (length > 2147483647L) {
            length = -1L;
        }
        MidiFileFormat midiFileFormatFromStream = null;
        try {
            midiFileFormatFromStream = this.getMidiFileFormatFromStream(bufferedInputStream, (int)length, null);
        }
        finally {
            bufferedInputStream.close();
        }
        return midiFileFormatFromStream;
    }
    
    @Override
    public Sequence getSequence(final InputStream inputStream) throws InvalidMidiDataException, IOException {
        final SMFParser smfParser = new SMFParser();
        final MidiFileFormat midiFileFormatFromStream = this.getMidiFileFormatFromStream(inputStream, -1, smfParser);
        if (midiFileFormatFromStream.getType() != 0 && midiFileFormatFromStream.getType() != 1) {
            throw new InvalidMidiDataException("Invalid or unsupported file type: " + midiFileFormatFromStream.getType());
        }
        final Sequence sequence = new Sequence(midiFileFormatFromStream.getDivisionType(), midiFileFormatFromStream.getResolution());
        for (int n = 0; n < smfParser.tracks && smfParser.nextTrack(); ++n) {
            smfParser.readTrack(sequence.createTrack());
        }
        return sequence;
    }
    
    @Override
    public Sequence getSequence(final URL url) throws InvalidMidiDataException, IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(url.openStream(), 1024);
        Sequence sequence = null;
        try {
            sequence = this.getSequence(bufferedInputStream);
        }
        finally {
            bufferedInputStream.close();
        }
        return sequence;
    }
    
    @Override
    public Sequence getSequence(final File file) throws InvalidMidiDataException, IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file), 1024);
        Sequence sequence = null;
        try {
            sequence = this.getSequence(bufferedInputStream);
        }
        finally {
            bufferedInputStream.close();
        }
        return sequence;
    }
}
