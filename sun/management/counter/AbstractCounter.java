package sun.management.counter;

public abstract class AbstractCounter implements Counter
{
    String name;
    Units units;
    Variability variability;
    int flags;
    int vectorLength;
    private static final long serialVersionUID = 6992337162326171013L;
    
    protected AbstractCounter(final String name, final Units units, final Variability variability, final int flags, final int vectorLength) {
        this.name = name;
        this.units = units;
        this.variability = variability;
        this.flags = flags;
        this.vectorLength = vectorLength;
    }
    
    protected AbstractCounter(final String s, final Units units, final Variability variability, final int n) {
        this(s, units, variability, n, 0);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Units getUnits() {
        return this.units;
    }
    
    @Override
    public Variability getVariability() {
        return this.variability;
    }
    
    @Override
    public boolean isVector() {
        return this.vectorLength > 0;
    }
    
    @Override
    public int getVectorLength() {
        return this.vectorLength;
    }
    
    @Override
    public boolean isInternal() {
        return (this.flags & 0x1) == 0x0;
    }
    
    @Override
    public int getFlags() {
        return this.flags;
    }
    
    @Override
    public abstract Object getValue();
    
    @Override
    public String toString() {
        final String string = this.getName() + ": " + this.getValue() + " " + this.getUnits();
        if (this.isInternal()) {
            return string + " [INTERNAL]";
        }
        return string;
    }
    
    class Flags
    {
        static final int SUPPORTED = 1;
    }
}
