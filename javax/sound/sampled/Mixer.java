package javax.sound.sampled;

public interface Mixer extends Line
{
    Info getMixerInfo();
    
    Line.Info[] getSourceLineInfo();
    
    Line.Info[] getTargetLineInfo();
    
    Line.Info[] getSourceLineInfo(final Line.Info p0);
    
    Line.Info[] getTargetLineInfo(final Line.Info p0);
    
    boolean isLineSupported(final Line.Info p0);
    
    Line getLine(final Line.Info p0) throws LineUnavailableException;
    
    int getMaxLines(final Line.Info p0);
    
    Line[] getSourceLines();
    
    Line[] getTargetLines();
    
    void synchronize(final Line[] p0, final boolean p1);
    
    void unsynchronize(final Line[] p0);
    
    boolean isSynchronizationSupported(final Line[] p0, final boolean p1);
    
    public static class Info
    {
        private final String name;
        private final String vendor;
        private final String description;
        private final String version;
        
        protected Info(final String name, final String vendor, final String description, final String version) {
            this.name = name;
            this.vendor = vendor;
            this.description = description;
            this.version = version;
        }
        
        @Override
        public final boolean equals(final Object o) {
            return super.equals(o);
        }
        
        @Override
        public final int hashCode() {
            return super.hashCode();
        }
        
        public final String getName() {
            return this.name;
        }
        
        public final String getVendor() {
            return this.vendor;
        }
        
        public final String getDescription() {
            return this.description;
        }
        
        public final String getVersion() {
            return this.version;
        }
        
        @Override
        public final String toString() {
            return this.name + ", version " + this.version;
        }
    }
}
