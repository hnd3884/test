package javax.sound.sampled;

public interface TargetDataLine extends DataLine
{
    void open(final AudioFormat p0, final int p1) throws LineUnavailableException;
    
    void open(final AudioFormat p0) throws LineUnavailableException;
    
    int read(final byte[] p0, final int p1, final int p2);
}
