package org.apache.jasper.runtime;

import org.apache.jasper.compiler.Localizer;
import org.apache.tomcat.InstanceManager;
import javax.servlet.ServletConfig;

public class InstanceManagerFactory
{
    private InstanceManagerFactory() {
    }
    
    public static InstanceManager getInstanceManager(final ServletConfig config) {
        final InstanceManager instanceManager = (InstanceManager)config.getServletContext().getAttribute(InstanceManager.class.getName());
        if (instanceManager == null) {
            throw new IllegalStateException(Localizer.getMessage("jsp.error.noInstanceManager"));
        }
        return instanceManager;
    }
}
