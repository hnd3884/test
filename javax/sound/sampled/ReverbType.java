package javax.sound.sampled;

public class ReverbType
{
    private String name;
    private int earlyReflectionDelay;
    private float earlyReflectionIntensity;
    private int lateReflectionDelay;
    private float lateReflectionIntensity;
    private int decayTime;
    
    protected ReverbType(final String name, final int earlyReflectionDelay, final float earlyReflectionIntensity, final int lateReflectionDelay, final float lateReflectionIntensity, final int decayTime) {
        this.name = name;
        this.earlyReflectionDelay = earlyReflectionDelay;
        this.earlyReflectionIntensity = earlyReflectionIntensity;
        this.lateReflectionDelay = lateReflectionDelay;
        this.lateReflectionIntensity = lateReflectionIntensity;
        this.decayTime = decayTime;
    }
    
    public String getName() {
        return this.name;
    }
    
    public final int getEarlyReflectionDelay() {
        return this.earlyReflectionDelay;
    }
    
    public final float getEarlyReflectionIntensity() {
        return this.earlyReflectionIntensity;
    }
    
    public final int getLateReflectionDelay() {
        return this.lateReflectionDelay;
    }
    
    public final float getLateReflectionIntensity() {
        return this.lateReflectionIntensity;
    }
    
    public final int getDecayTime() {
        return this.decayTime;
    }
    
    @Override
    public final boolean equals(final Object o) {
        return super.equals(o);
    }
    
    @Override
    public final int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public final String toString() {
        return this.name + ", early reflection delay " + this.earlyReflectionDelay + " ns, early reflection intensity " + this.earlyReflectionIntensity + " dB, late deflection delay " + this.lateReflectionDelay + " ns, late reflection intensity " + this.lateReflectionIntensity + " dB, decay time " + this.decayTime;
    }
}
