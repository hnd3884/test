package javax.sound.midi;

public abstract class SoundbankResource
{
    private final Soundbank soundBank;
    private final String name;
    private final Class dataClass;
    
    protected SoundbankResource(final Soundbank soundBank, final String name, final Class<?> dataClass) {
        this.soundBank = soundBank;
        this.name = name;
        this.dataClass = dataClass;
    }
    
    public Soundbank getSoundbank() {
        return this.soundBank;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class<?> getDataClass() {
        return this.dataClass;
    }
    
    public abstract Object getData();
}
