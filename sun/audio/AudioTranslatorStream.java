package sun.audio;

import java.io.IOException;
import java.io.InputStream;

public final class AudioTranslatorStream extends NativeAudioStream
{
    private final int length = 0;
    
    public AudioTranslatorStream(final InputStream inputStream) throws IOException {
        super(inputStream);
        throw new InvalidAudioFormatException();
    }
    
    @Override
    public int getLength() {
        return 0;
    }
}
