package javax.sound.sampled.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;

public abstract class AudioFileWriter
{
    public abstract AudioFileFormat.Type[] getAudioFileTypes();
    
    public boolean isFileTypeSupported(final AudioFileFormat.Type type) {
        final AudioFileFormat.Type[] audioFileTypes = this.getAudioFileTypes();
        for (int i = 0; i < audioFileTypes.length; ++i) {
            if (type.equals(audioFileTypes[i])) {
                return true;
            }
        }
        return false;
    }
    
    public abstract AudioFileFormat.Type[] getAudioFileTypes(final AudioInputStream p0);
    
    public boolean isFileTypeSupported(final AudioFileFormat.Type type, final AudioInputStream audioInputStream) {
        final AudioFileFormat.Type[] audioFileTypes = this.getAudioFileTypes(audioInputStream);
        for (int i = 0; i < audioFileTypes.length; ++i) {
            if (type.equals(audioFileTypes[i])) {
                return true;
            }
        }
        return false;
    }
    
    public abstract int write(final AudioInputStream p0, final AudioFileFormat.Type p1, final OutputStream p2) throws IOException;
    
    public abstract int write(final AudioInputStream p0, final AudioFileFormat.Type p1, final File p2) throws IOException;
}
