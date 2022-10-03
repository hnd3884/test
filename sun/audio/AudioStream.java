package sun.audio;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;
import javax.sound.midi.MidiSystem;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.midi.MidiFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.FilterInputStream;

public final class AudioStream extends FilterInputStream
{
    AudioInputStream ais;
    AudioFormat format;
    MidiFileFormat midiformat;
    InputStream stream;
    
    public AudioStream(final InputStream stream) throws IOException {
        super(stream);
        this.ais = null;
        this.format = null;
        this.midiformat = null;
        this.stream = null;
        this.stream = stream;
        if (!stream.markSupported()) {
            this.stream = new BufferedInputStream(stream, 1024);
        }
        try {
            this.ais = AudioSystem.getAudioInputStream(this.stream);
            this.format = this.ais.getFormat();
            this.in = this.ais;
        }
        catch (final UnsupportedAudioFileException ex) {
            try {
                this.midiformat = MidiSystem.getMidiFileFormat(this.stream);
            }
            catch (final InvalidMidiDataException ex2) {
                throw new IOException("could not create audio stream from input stream");
            }
        }
    }
    
    public AudioData getData() throws IOException {
        final int length = this.getLength();
        if (length < 1048576) {
            final byte[] array = new byte[length];
            try {
                this.ais.read(array, 0, length);
            }
            catch (final IOException ex) {
                throw new IOException("Could not create AudioData Object");
            }
            return new AudioData(this.format, array);
        }
        throw new IOException("could not create AudioData object");
    }
    
    public int getLength() {
        if (this.ais != null && this.format != null) {
            return (int)(this.ais.getFrameLength() * this.ais.getFormat().getFrameSize());
        }
        if (this.midiformat != null) {
            return this.midiformat.getByteLength();
        }
        return -1;
    }
}
