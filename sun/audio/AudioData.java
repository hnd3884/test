package sun.audio;

import java.util.Arrays;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.AudioFormat;

public final class AudioData
{
    private static final AudioFormat DEFAULT_FORMAT;
    AudioFormat format;
    byte[] buffer;
    
    public AudioData(final byte[] array) {
        this(AudioData.DEFAULT_FORMAT, array);
        try {
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(array));
            this.format = audioInputStream.getFormat();
            audioInputStream.close();
        }
        catch (final IOException ex) {}
        catch (final UnsupportedAudioFileException ex2) {}
    }
    
    AudioData(final AudioFormat format, final byte[] array) {
        this.format = format;
        if (array != null) {
            this.buffer = Arrays.copyOf(array, array.length);
        }
    }
    
    static {
        DEFAULT_FORMAT = new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0f, 8, 1, 1, 8000.0f, true);
    }
}
