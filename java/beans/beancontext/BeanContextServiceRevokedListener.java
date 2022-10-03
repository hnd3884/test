package java.beans.beancontext;

import java.util.EventListener;

public interface BeanContextServiceRevokedListener extends EventListener
{
    void serviceRevoked(final BeanContextServiceRevokedEvent p0);
}
