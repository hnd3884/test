package java.awt.event;

import sun.awt.AppContext;
import sun.awt.SunToolkit;
import java.awt.Component;
import java.awt.Window;

public class WindowEvent extends ComponentEvent
{
    public static final int WINDOW_FIRST = 200;
    public static final int WINDOW_OPENED = 200;
    public static final int WINDOW_CLOSING = 201;
    public static final int WINDOW_CLOSED = 202;
    public static final int WINDOW_ICONIFIED = 203;
    public static final int WINDOW_DEICONIFIED = 204;
    public static final int WINDOW_ACTIVATED = 205;
    public static final int WINDOW_DEACTIVATED = 206;
    public static final int WINDOW_GAINED_FOCUS = 207;
    public static final int WINDOW_LOST_FOCUS = 208;
    public static final int WINDOW_STATE_CHANGED = 209;
    public static final int WINDOW_LAST = 209;
    transient Window opposite;
    int oldState;
    int newState;
    private static final long serialVersionUID = -1567959133147912127L;
    
    public WindowEvent(final Window window, final int n, final Window opposite, final int oldState, final int newState) {
        super(window, n);
        this.opposite = opposite;
        this.oldState = oldState;
        this.newState = newState;
    }
    
    public WindowEvent(final Window window, final int n, final Window window2) {
        this(window, n, window2, 0, 0);
    }
    
    public WindowEvent(final Window window, final int n, final int n2, final int n3) {
        this(window, n, null, n2, n3);
    }
    
    public WindowEvent(final Window window, final int n) {
        this(window, n, null, 0, 0);
    }
    
    public Window getWindow() {
        return (this.source instanceof Window) ? ((Window)this.source) : null;
    }
    
    public Window getOppositeWindow() {
        if (this.opposite == null) {
            return null;
        }
        return (SunToolkit.targetToAppContext(this.opposite) == AppContext.getAppContext()) ? this.opposite : null;
    }
    
    public int getOldState() {
        return this.oldState;
    }
    
    public int getNewState() {
        return this.newState;
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 200: {
                s = "WINDOW_OPENED";
                break;
            }
            case 201: {
                s = "WINDOW_CLOSING";
                break;
            }
            case 202: {
                s = "WINDOW_CLOSED";
                break;
            }
            case 203: {
                s = "WINDOW_ICONIFIED";
                break;
            }
            case 204: {
                s = "WINDOW_DEICONIFIED";
                break;
            }
            case 205: {
                s = "WINDOW_ACTIVATED";
                break;
            }
            case 206: {
                s = "WINDOW_DEACTIVATED";
                break;
            }
            case 207: {
                s = "WINDOW_GAINED_FOCUS";
                break;
            }
            case 208: {
                s = "WINDOW_LOST_FOCUS";
                break;
            }
            case 209: {
                s = "WINDOW_STATE_CHANGED";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        return s + ",opposite=" + this.getOppositeWindow() + ",oldState=" + this.oldState + ",newState=" + this.newState;
    }
}
