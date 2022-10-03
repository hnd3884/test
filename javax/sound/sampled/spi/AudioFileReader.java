package javax.sound.sampled.spi;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat;
import java.io.InputStream;

public abstract class AudioFileReader
{
    public abstract AudioFileFormat getAudioFileFormat(final InputStream p0) throws UnsupportedAudioFileException, IOException;
    
    public abstract AudioFileFormat getAudioFileFormat(final URL p0) throws UnsupportedAudioFileException, IOException;
    
    public abstract AudioFileFormat getAudioFileFormat(final File p0) throws UnsupportedAudioFileException, IOException;
    
    public abstract AudioInputStream getAudioInputStream(final InputStream p0) throws UnsupportedAudioFileException, IOException;
    
    public abstract AudioInputStream getAudioInputStream(final URL p0) throws UnsupportedAudioFileException, IOException;
    
    public abstract AudioInputStream getAudioInputStream(final File p0) throws UnsupportedAudioFileException, IOException;
}
