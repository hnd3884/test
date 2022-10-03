package sun.awt.dnd;

import java.awt.Component;
import java.awt.event.MouseEvent;

public class SunDropTargetEvent extends MouseEvent
{
    public static final int MOUSE_DROPPED = 502;
    private final SunDropTargetContextPeer.EventDispatcher dispatcher;
    
    public SunDropTargetEvent(final Component component, final int n, final int n2, final int n3, final SunDropTargetContextPeer.EventDispatcher dispatcher) {
        super(component, n, System.currentTimeMillis(), 0, n2, n3, 0, 0, 0, false, 0);
        (this.dispatcher = dispatcher).registerEvent(this);
    }
    
    public void dispatch() {
        try {
            this.dispatcher.dispatchEvent(this);
        }
        finally {
            this.dispatcher.unregisterEvent(this);
        }
    }
    
    @Override
    public void consume() {
        final boolean consumed = this.isConsumed();
        super.consume();
        if (!consumed && this.isConsumed()) {
            this.dispatcher.unregisterEvent(this);
        }
    }
    
    public SunDropTargetContextPeer.EventDispatcher getDispatcher() {
        return this.dispatcher;
    }
    
    @Override
    public String paramString() {
        switch (this.id) {
            case 502: {
                return "MOUSE_DROPPED" + ",(" + this.getX() + "," + this.getY() + ")";
            }
            default: {
                return super.paramString();
            }
        }
    }
}
