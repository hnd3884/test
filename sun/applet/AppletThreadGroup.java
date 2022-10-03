package sun.applet;

public class AppletThreadGroup extends ThreadGroup
{
    public AppletThreadGroup(final String s) {
        this(Thread.currentThread().getThreadGroup(), s);
    }
    
    public AppletThreadGroup(final ThreadGroup threadGroup, final String s) {
        super(threadGroup, s);
        this.setMaxPriority(4);
    }
}
