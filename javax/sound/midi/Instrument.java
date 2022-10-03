package javax.sound.midi;

public abstract class Instrument extends SoundbankResource
{
    private final Patch patch;
    
    protected Instrument(final Soundbank soundbank, final Patch patch, final String s, final Class<?> clazz) {
        super(soundbank, s, clazz);
        this.patch = patch;
    }
    
    public Patch getPatch() {
        return this.patch;
    }
}
