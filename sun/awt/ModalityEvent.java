package sun.awt;

import java.awt.ActiveEvent;
import java.awt.AWTEvent;

public class ModalityEvent extends AWTEvent implements ActiveEvent
{
    public static final int MODALITY_PUSHED = 1300;
    public static final int MODALITY_POPPED = 1301;
    private ModalityListener listener;
    
    public ModalityEvent(final Object o, final ModalityListener listener, final int n) {
        super(o, n);
        this.listener = listener;
    }
    
    @Override
    public void dispatch() {
        switch (this.getID()) {
            case 1300: {
                this.listener.modalityPushed(this);
                break;
            }
            case 1301: {
                this.listener.modalityPopped(this);
                break;
            }
            default: {
                throw new Error("Invalid event id.");
            }
        }
    }
}
