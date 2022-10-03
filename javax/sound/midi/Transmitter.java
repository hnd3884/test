package javax.sound.midi;

public interface Transmitter extends AutoCloseable
{
    void setReceiver(final Receiver p0);
    
    Receiver getReceiver();
    
    void close();
}
