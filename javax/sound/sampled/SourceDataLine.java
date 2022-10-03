package javax.sound.sampled;

public interface SourceDataLine extends DataLine
{
    void open(final AudioFormat p0, final int p1) throws LineUnavailableException;
    
    void open(final AudioFormat p0) throws LineUnavailableException;
    
    int write(final byte[] p0, final int p1, final int p2);
}
