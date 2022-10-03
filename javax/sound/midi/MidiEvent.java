package javax.sound.midi;

public class MidiEvent
{
    private final MidiMessage message;
    private long tick;
    
    public MidiEvent(final MidiMessage message, final long tick) {
        this.message = message;
        this.tick = tick;
    }
    
    public MidiMessage getMessage() {
        return this.message;
    }
    
    public void setTick(final long tick) {
        this.tick = tick;
    }
    
    public long getTick() {
        return this.tick;
    }
}
