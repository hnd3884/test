package javax.sound.midi.spi;

import javax.sound.midi.Sequence;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import java.io.InputStream;

public abstract class MidiFileReader
{
    public abstract MidiFileFormat getMidiFileFormat(final InputStream p0) throws InvalidMidiDataException, IOException;
    
    public abstract MidiFileFormat getMidiFileFormat(final URL p0) throws InvalidMidiDataException, IOException;
    
    public abstract MidiFileFormat getMidiFileFormat(final File p0) throws InvalidMidiDataException, IOException;
    
    public abstract Sequence getSequence(final InputStream p0) throws InvalidMidiDataException, IOException;
    
    public abstract Sequence getSequence(final URL p0) throws InvalidMidiDataException, IOException;
    
    public abstract Sequence getSequence(final File p0) throws InvalidMidiDataException, IOException;
}
