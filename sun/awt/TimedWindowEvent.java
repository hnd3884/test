package sun.awt;

import java.awt.Window;
import java.awt.event.WindowEvent;

public class TimedWindowEvent extends WindowEvent
{
    private long time;
    
    public long getWhen() {
        return this.time;
    }
    
    public TimedWindowEvent(final Window window, final int n, final Window window2, final long time) {
        super(window, n, window2);
        this.time = time;
    }
    
    public TimedWindowEvent(final Window window, final int n, final Window window2, final int n2, final int n3, final long time) {
        super(window, n, window2, n2, n3);
        this.time = time;
    }
}
