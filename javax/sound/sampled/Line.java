package javax.sound.sampled;

public interface Line extends AutoCloseable
{
    Info getLineInfo();
    
    void open() throws LineUnavailableException;
    
    void close();
    
    boolean isOpen();
    
    Control[] getControls();
    
    boolean isControlSupported(final Control.Type p0);
    
    Control getControl(final Control.Type p0);
    
    void addLineListener(final LineListener p0);
    
    void removeLineListener(final LineListener p0);
    
    public static class Info
    {
        private final Class lineClass;
        
        public Info(final Class<?> lineClass) {
            if (lineClass == null) {
                this.lineClass = Line.class;
            }
            else {
                this.lineClass = lineClass;
            }
        }
        
        public Class<?> getLineClass() {
            return this.lineClass;
        }
        
        public boolean matches(final Info info) {
            return this.getClass().isInstance(info) && this.getLineClass().isAssignableFrom(info.getLineClass());
        }
        
        @Override
        public String toString() {
            final String s = "javax.sound.sampled.";
            final String s2 = new String(this.getLineClass().toString());
            final int index = s2.indexOf(s);
            String string;
            if (index != -1) {
                string = s2.substring(0, index) + s2.substring(index + s.length(), s2.length());
            }
            else {
                string = s2;
            }
            return string;
        }
    }
}
