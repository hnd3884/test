package javax.sound.sampled;

import java.io.IOException;

public interface Clip extends DataLine
{
    public static final int LOOP_CONTINUOUSLY = -1;
    
    void open(final AudioFormat p0, final byte[] p1, final int p2, final int p3) throws LineUnavailableException;
    
    void open(final AudioInputStream p0) throws LineUnavailableException, IOException;
    
    int getFrameLength();
    
    long getMicrosecondLength();
    
    void setFramePosition(final int p0);
    
    void setMicrosecondPosition(final long p0);
    
    void setLoopPoints(final int p0, final int p1);
    
    void loop(final int p0);
}
