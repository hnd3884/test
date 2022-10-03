package javax.sound.sampled;

public interface Port extends Line
{
    public static class Info extends Line.Info
    {
        public static final Info MICROPHONE;
        public static final Info LINE_IN;
        public static final Info COMPACT_DISC;
        public static final Info SPEAKER;
        public static final Info HEADPHONE;
        public static final Info LINE_OUT;
        private String name;
        private boolean isSource;
        
        public Info(final Class<?> clazz, final String name, final boolean isSource) {
            super(clazz);
            this.name = name;
            this.isSource = isSource;
        }
        
        public String getName() {
            return this.name;
        }
        
        public boolean isSource() {
            return this.isSource;
        }
        
        @Override
        public boolean matches(final Line.Info info) {
            return super.matches(info) && this.name.equals(((Info)info).getName()) && this.isSource == ((Info)info).isSource();
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
            return this.name + (this.isSource ? " source" : " target") + " port";
        }
        
        static {
            MICROPHONE = new Info(Port.class, "MICROPHONE", true);
            LINE_IN = new Info(Port.class, "LINE_IN", true);
            COMPACT_DISC = new Info(Port.class, "COMPACT_DISC", true);
            SPEAKER = new Info(Port.class, "SPEAKER", false);
            HEADPHONE = new Info(Port.class, "HEADPHONE", false);
            LINE_OUT = new Info(Port.class, "LINE_OUT", false);
        }
    }
}
