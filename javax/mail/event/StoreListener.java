package javax.mail.event;

import java.util.EventListener;

public interface StoreListener extends EventListener
{
    void notification(final StoreEvent p0);
}
