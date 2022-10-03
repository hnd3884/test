package java.awt.datatransfer;

import java.util.EventObject;

public class FlavorEvent extends EventObject
{
    public FlavorEvent(final Clipboard clipboard) {
        super(clipboard);
    }
}
