package sun.applet;

import java.util.EventObject;

public class AppletEvent extends EventObject
{
    private Object arg;
    private int id;
    
    public AppletEvent(final Object o, final int id, final Object arg) {
        super(o);
        this.arg = arg;
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
    
    public Object getArgument() {
        return this.arg;
    }
    
    @Override
    public String toString() {
        String s = this.getClass().getName() + "[source=" + this.source + " + id=" + this.id;
        if (this.arg != null) {
            s = s + " + arg=" + this.arg;
        }
        return s + " ]";
    }
}
