package java.awt.datatransfer;

import java.util.EventListener;

public interface FlavorListener extends EventListener
{
    void flavorsChanged(final FlavorEvent p0);
}
