package sun.audio;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class NativeAudioStream extends FilterInputStream
{
    public NativeAudioStream(final InputStream inputStream) throws IOException {
        super(inputStream);
    }
    
    public int getLength() {
        return 0;
    }
}
