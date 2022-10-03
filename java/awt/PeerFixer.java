package java.awt;

import java.awt.peer.ScrollPanePeer;
import java.awt.event.AdjustmentEvent;
import java.io.Serializable;
import java.awt.event.AdjustmentListener;

class PeerFixer implements AdjustmentListener, Serializable
{
    private static final long serialVersionUID = 7051237413532574756L;
    private ScrollPane scroller;
    
    PeerFixer(final ScrollPane scroller) {
        this.scroller = scroller;
    }
    
    @Override
    public void adjustmentValueChanged(final AdjustmentEvent adjustmentEvent) {
        final Adjustable adjustable = adjustmentEvent.getAdjustable();
        final int value = adjustmentEvent.getValue();
        final ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.scroller.peer;
        if (scrollPanePeer != null) {
            scrollPanePeer.setValue(adjustable, value);
        }
        final Component component = this.scroller.getComponent(0);
        switch (adjustable.getOrientation()) {
            case 1: {
                component.move(component.getLocation().x, -value);
                break;
            }
            case 0: {
                component.move(-value, component.getLocation().y);
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal adjustable orientation");
            }
        }
    }
}
