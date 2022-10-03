package com.sun.media.sound;

import java.io.File;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;
import javax.sound.midi.Receiver;
import javax.sound.midi.MetaMessage;
import java.io.InputStream;
import javax.sound.midi.MidiUnavailableException;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.midi.Sequence;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.spi.AudioFileReader;

public final class SoftMidiAudioFileReader extends AudioFileReader
{
    public static final AudioFileFormat.Type MIDI;
    private static AudioFormat format;
    
    public AudioFileFormat getAudioFileFormat(final Sequence sequence) throws UnsupportedAudioFileException, IOException {
        return new AudioFileFormat(SoftMidiAudioFileReader.MIDI, SoftMidiAudioFileReader.format, (int)(SoftMidiAudioFileReader.format.getFrameRate() * (sequence.getMicrosecondLength() / 1000000L + 4L)));
    }
    
    public AudioInputStream getAudioInputStream(final Sequence sequence) throws UnsupportedAudioFileException, IOException {
        final SoftSynthesizer softSynthesizer = new SoftSynthesizer();
        AudioInputStream openStream;
        Receiver receiver;
        try {
            openStream = softSynthesizer.openStream(SoftMidiAudioFileReader.format, null);
            receiver = softSynthesizer.getReceiver();
        }
        catch (final MidiUnavailableException ex) {
            throw new IOException(ex.toString());
        }
        final float divisionType = sequence.getDivisionType();
        final Track[] tracks = sequence.getTracks();
        final int[] array = new int[tracks.length];
        int n = 500000;
        final int resolution = sequence.getResolution();
        long n2 = 0L;
        long n3 = 0L;
        while (true) {
            MidiEvent midiEvent = null;
            int n4 = -1;
            for (int i = 0; i < tracks.length; ++i) {
                final int n5 = array[i];
                final Track track = tracks[i];
                if (n5 < track.size()) {
                    final MidiEvent value = track.get(n5);
                    if (midiEvent == null || value.getTick() < midiEvent.getTick()) {
                        midiEvent = value;
                        n4 = i;
                    }
                }
            }
            if (n4 == -1) {
                return new AudioInputStream(openStream, openStream.getFormat(), (long)(openStream.getFormat().getFrameRate() * (n3 / 1000000L + 4L)));
            }
            final int[] array2 = array;
            final int n6 = n4;
            ++array2[n6];
            final long tick = midiEvent.getTick();
            if (divisionType == 0.0f) {
                n3 += (tick - n2) * n / resolution;
            }
            else {
                n3 = (long)(tick * 1000000.0 * divisionType / resolution);
            }
            n2 = tick;
            final MidiMessage message = midiEvent.getMessage();
            if (message instanceof MetaMessage) {
                if (divisionType != 0.0f || ((MetaMessage)message).getType() != 81) {
                    continue;
                }
                final byte[] data = ((MetaMessage)message).getData();
                if (data.length < 3) {
                    throw new UnsupportedAudioFileException();
                }
                n = ((data[0] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | (data[2] & 0xFF));
            }
            else {
                receiver.send(message, n3);
            }
        }
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        inputStream.mark(200);
        Sequence sequence;
        try {
            sequence = MidiSystem.getSequence(inputStream);
        }
        catch (final InvalidMidiDataException ex) {
            inputStream.reset();
            throw new UnsupportedAudioFileException();
        }
        catch (final IOException ex2) {
            inputStream.reset();
            throw new UnsupportedAudioFileException();
        }
        return this.getAudioInputStream(sequence);
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final URL url) throws UnsupportedAudioFileException, IOException {
        Sequence sequence;
        try {
            sequence = MidiSystem.getSequence(url);
        }
        catch (final InvalidMidiDataException ex) {
            throw new UnsupportedAudioFileException();
        }
        catch (final IOException ex2) {
            throw new UnsupportedAudioFileException();
        }
        return this.getAudioFileFormat(sequence);
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final File file) throws UnsupportedAudioFileException, IOException {
        Sequence sequence;
        try {
            sequence = MidiSystem.getSequence(file);
        }
        catch (final InvalidMidiDataException ex) {
            throw new UnsupportedAudioFileException();
        }
        catch (final IOException ex2) {
            throw new UnsupportedAudioFileException();
        }
        return this.getAudioFileFormat(sequence);
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final URL url) throws UnsupportedAudioFileException, IOException {
        Sequence sequence;
        try {
            sequence = MidiSystem.getSequence(url);
        }
        catch (final InvalidMidiDataException ex) {
            throw new UnsupportedAudioFileException();
        }
        catch (final IOException ex2) {
            throw new UnsupportedAudioFileException();
        }
        return this.getAudioInputStream(sequence);
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final File file) throws UnsupportedAudioFileException, IOException {
        if (!file.getName().toLowerCase().endsWith(".mid")) {
            throw new UnsupportedAudioFileException();
        }
        Sequence sequence;
        try {
            sequence = MidiSystem.getSequence(file);
        }
        catch (final InvalidMidiDataException ex) {
            throw new UnsupportedAudioFileException();
        }
        catch (final IOException ex2) {
            throw new UnsupportedAudioFileException();
        }
        return this.getAudioInputStream(sequence);
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        inputStream.mark(200);
        Sequence sequence;
        try {
            sequence = MidiSystem.getSequence(inputStream);
        }
        catch (final InvalidMidiDataException ex) {
            inputStream.reset();
            throw new UnsupportedAudioFileException();
        }
        catch (final IOException ex2) {
            inputStream.reset();
            throw new UnsupportedAudioFileException();
        }
        return this.getAudioFileFormat(sequence);
    }
    
    static {
        MIDI = new AudioFileFormat.Type("MIDI", "mid");
        SoftMidiAudioFileReader.format = new AudioFormat(44100.0f, 16, 2, true, false);
    }
}
