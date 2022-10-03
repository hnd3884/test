package sun.awt;

import java.awt.event.WindowEvent;

public interface WindowClosingListener
{
    RuntimeException windowClosingNotify(final WindowEvent p0);
    
    RuntimeException windowClosingDelivered(final WindowEvent p0);
}
