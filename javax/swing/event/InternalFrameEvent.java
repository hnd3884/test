package javax.swing.event;

import javax.swing.JInternalFrame;
import java.awt.AWTEvent;

public class InternalFrameEvent extends AWTEvent
{
    public static final int INTERNAL_FRAME_FIRST = 25549;
    public static final int INTERNAL_FRAME_LAST = 25555;
    public static final int INTERNAL_FRAME_OPENED = 25549;
    public static final int INTERNAL_FRAME_CLOSING = 25550;
    public static final int INTERNAL_FRAME_CLOSED = 25551;
    public static final int INTERNAL_FRAME_ICONIFIED = 25552;
    public static final int INTERNAL_FRAME_DEICONIFIED = 25553;
    public static final int INTERNAL_FRAME_ACTIVATED = 25554;
    public static final int INTERNAL_FRAME_DEACTIVATED = 25555;
    
    public InternalFrameEvent(final JInternalFrame internalFrame, final int n) {
        super(internalFrame, n);
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 25549: {
                s = "INTERNAL_FRAME_OPENED";
                break;
            }
            case 25550: {
                s = "INTERNAL_FRAME_CLOSING";
                break;
            }
            case 25551: {
                s = "INTERNAL_FRAME_CLOSED";
                break;
            }
            case 25552: {
                s = "INTERNAL_FRAME_ICONIFIED";
                break;
            }
            case 25553: {
                s = "INTERNAL_FRAME_DEICONIFIED";
                break;
            }
            case 25554: {
                s = "INTERNAL_FRAME_ACTIVATED";
                break;
            }
            case 25555: {
                s = "INTERNAL_FRAME_DEACTIVATED";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        return s;
    }
    
    public JInternalFrame getInternalFrame() {
        return (this.source instanceof JInternalFrame) ? ((JInternalFrame)this.source) : null;
    }
}
