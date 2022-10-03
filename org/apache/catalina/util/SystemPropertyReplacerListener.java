package org.apache.catalina.util;

import org.apache.tomcat.util.digester.Digester;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

@Deprecated
public class SystemPropertyReplacerListener implements LifecycleListener
{
    @Override
    public void lifecycleEvent(final LifecycleEvent event) {
        if ("before_init".equals(event.getType())) {
            Digester.replaceSystemProperties();
        }
    }
}
