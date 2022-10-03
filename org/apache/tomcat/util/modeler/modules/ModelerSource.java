package org.apache.tomcat.util.modeler.modules;

import javax.management.ObjectName;
import java.util.List;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public abstract class ModelerSource
{
    protected static final StringManager sm;
    protected Object source;
    
    public abstract List<ObjectName> loadDescriptors(final Registry p0, final String p1, final Object p2) throws Exception;
    
    static {
        sm = StringManager.getManager((Class)Registry.class);
    }
}
