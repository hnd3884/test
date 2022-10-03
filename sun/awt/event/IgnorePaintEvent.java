package sun.awt.event;

import java.awt.Rectangle;
import java.awt.Component;
import java.awt.event.PaintEvent;

public class IgnorePaintEvent extends PaintEvent
{
    public IgnorePaintEvent(final Component component, final int n, final Rectangle rectangle) {
        super(component, n, rectangle);
    }
}
