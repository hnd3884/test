package sun.awt;

import java.awt.Component;
import java.awt.AWTEvent;

public class UngrabEvent extends AWTEvent
{
    private static final int UNGRAB_EVENT_ID = 1998;
    
    public UngrabEvent(final Component component) {
        super(component, 1998);
    }
    
    @Override
    public String toString() {
        return "sun.awt.UngrabEvent[" + this.getSource() + "]";
    }
}
