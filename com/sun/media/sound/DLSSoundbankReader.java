package com.sun.media.sound;

import java.io.File;
import java.io.InputStream;
import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;
import javax.sound.midi.Soundbank;
import java.net.URL;
import javax.sound.midi.spi.SoundbankReader;

public final class DLSSoundbankReader extends SoundbankReader
{
    @Override
    public Soundbank getSoundbank(final URL url) throws InvalidMidiDataException, IOException {
        try {
            return new DLSSoundbank(url);
        }
        catch (final RIFFInvalidFormatException ex) {
            return null;
        }
        catch (final IOException ex2) {
            return null;
        }
    }
    
    @Override
    public Soundbank getSoundbank(final InputStream inputStream) throws InvalidMidiDataException, IOException {
        try {
            inputStream.mark(512);
            return new DLSSoundbank(inputStream);
        }
        catch (final RIFFInvalidFormatException ex) {
            inputStream.reset();
            return null;
        }
    }
    
    @Override
    public Soundbank getSoundbank(final File file) throws InvalidMidiDataException, IOException {
        try {
            return new DLSSoundbank(file);
        }
        catch (final RIFFInvalidFormatException ex) {
            return null;
        }
    }
}
