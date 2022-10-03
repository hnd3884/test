package javax.sound.sampled;

public abstract class Control
{
    private final Type type;
    
    protected Control(final Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return this.type;
    }
    
    @Override
    public String toString() {
        return new String(this.getType() + " Control");
    }
    
    public static class Type
    {
        private String name;
        
        protected Type(final String name) {
            this.name = name;
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
            return this.name;
        }
    }
}
