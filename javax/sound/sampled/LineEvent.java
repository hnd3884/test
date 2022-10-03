package javax.sound.sampled;

import java.util.EventObject;

public class LineEvent extends EventObject
{
    private final Type type;
    private final long position;
    
    public LineEvent(final Line line, final Type type, final long position) {
        super(line);
        this.type = type;
        this.position = position;
    }
    
    public final Line getLine() {
        return (Line)this.getSource();
    }
    
    public final Type getType() {
        return this.type;
    }
    
    public final long getFramePosition() {
        return this.position;
    }
    
    @Override
    public String toString() {
        String string = "";
        if (this.type != null) {
            string = this.type.toString() + " ";
        }
        String string2;
        if (this.getLine() == null) {
            string2 = "null";
        }
        else {
            string2 = this.getLine().toString();
        }
        return new String(string + "event from line " + string2);
    }
    
    public static class Type
    {
        private String name;
        public static final Type OPEN;
        public static final Type CLOSE;
        public static final Type START;
        public static final Type STOP;
        
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
        public String toString() {
            return this.name;
        }
        
        static {
            OPEN = new Type("Open");
            CLOSE = new Type("Close");
            START = new Type("Start");
            STOP = new Type("Stop");
        }
    }
}
