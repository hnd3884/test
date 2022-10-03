package sun.misc;

import java.lang.ref.SoftReference;

@Deprecated
public abstract class Ref
{
    private SoftReference soft;
    
    public synchronized Object get() {
        Object thing = this.check();
        if (thing == null) {
            thing = this.reconstitute();
            this.setThing(thing);
        }
        return thing;
    }
    
    public abstract Object reconstitute();
    
    public synchronized void flush() {
        final SoftReference soft = this.soft;
        if (soft != null) {
            soft.clear();
        }
        this.soft = null;
    }
    
    public synchronized void setThing(final Object o) {
        this.flush();
        this.soft = new SoftReference((T)o);
    }
    
    public synchronized Object check() {
        final SoftReference soft = this.soft;
        if (soft == null) {
            return null;
        }
        return soft.get();
    }
    
    public Ref() {
        this.soft = null;
    }
    
    public Ref(final Object thing) {
        this.soft = null;
        this.setThing(thing);
    }
}
