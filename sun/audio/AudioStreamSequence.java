package sun.audio;

import java.io.InputStream;
import java.util.Enumeration;
import java.io.SequenceInputStream;

public final class AudioStreamSequence extends SequenceInputStream
{
    Enumeration e;
    InputStream in;
    
    public AudioStreamSequence(final Enumeration enumeration) {
        super(enumeration);
    }
}
