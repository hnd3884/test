package javax.sound.midi;

public interface Receiver extends AutoCloseable
{
    void send(final MidiMessage p0, final long p1);
    
    void close();
}
