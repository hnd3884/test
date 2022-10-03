package sun.management;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MBeanRegistration;

public class HotspotInternal implements HotspotInternalMBean, MBeanRegistration
{
    private static final String HOTSPOT_INTERNAL_MBEAN_NAME = "sun.management:type=HotspotInternal";
    private static ObjectName objName;
    private MBeanServer server;
    
    public HotspotInternal() {
        this.server = null;
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer server, final ObjectName objectName) throws Exception {
        ManagementFactoryHelper.registerInternalMBeans(server);
        this.server = server;
        return HotspotInternal.objName;
    }
    
    @Override
    public void postRegister(final Boolean b) {
    }
    
    @Override
    public void preDeregister() throws Exception {
        ManagementFactoryHelper.unregisterInternalMBeans(this.server);
    }
    
    @Override
    public void postDeregister() {
    }
    
    static {
        HotspotInternal.objName = Util.newObjectName("sun.management:type=HotspotInternal");
    }
}
