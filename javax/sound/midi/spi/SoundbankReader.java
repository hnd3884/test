package javax.sound.midi.spi;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import java.net.URL;

public abstract class SoundbankReader
{
    public abstract Soundbank getSoundbank(final URL p0) throws InvalidMidiDataException, IOException;
    
    public abstract Soundbank getSoundbank(final InputStream p0) throws InvalidMidiDataException, IOException;
    
    public abstract Soundbank getSoundbank(final File p0) throws InvalidMidiDataException, IOException;
}
