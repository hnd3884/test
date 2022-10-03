package org.apache.catalina;

import java.util.EventListener;

public interface SessionListener extends EventListener
{
    void sessionEvent(final SessionEvent p0);
}
