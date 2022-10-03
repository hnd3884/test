package sun.audio;

import java.io.ByteArrayInputStream;

public class AudioDataStream extends ByteArrayInputStream
{
    private final AudioData ad;
    
    public AudioDataStream(final AudioData ad) {
        super(ad.buffer);
        this.ad = ad;
    }
    
    final AudioData getAudioData() {
        return this.ad;
    }
}
