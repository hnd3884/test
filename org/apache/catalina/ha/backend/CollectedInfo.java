package org.apache.catalina.ha.backend;

import java.util.Iterator;
import java.util.Set;
import javax.management.ObjectInstance;
import javax.management.QueryExp;
import org.apache.tomcat.util.modeler.Registry;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import org.apache.tomcat.util.res.StringManager;

public class CollectedInfo
{
    private static final StringManager sm;
    protected MBeanServer mBeanServer;
    protected ObjectName objName;
    int ready;
    int busy;
    int port;
    String host;
    
    public CollectedInfo(final String host, final int port) throws Exception {
        this.mBeanServer = null;
        this.objName = null;
        this.port = 0;
        this.host = null;
        this.init(host, port);
    }
    
    public void init(final String host, final int port) throws Exception {
        int iport = 0;
        String shost = null;
        this.mBeanServer = Registry.getRegistry((Object)null, (Object)null).getMBeanServer();
        final String onStr = "*:type=ThreadPool,*";
        final ObjectName objectName = new ObjectName(onStr);
        final Set<ObjectInstance> set = this.mBeanServer.queryMBeans(objectName, null);
        for (final ObjectInstance oi : set) {
            this.objName = oi.getObjectName();
            final String name = this.objName.getKeyProperty("name");
            final String[] elenames = name.split("-");
            final String sport = elenames[elenames.length - 1];
            iport = Integer.parseInt(sport);
            final String[] shosts = elenames[1].split("%2F");
            shost = shosts[0];
            if (port == 0 && host == null) {
                break;
            }
            if (host == null && iport == port) {
                break;
            }
            if (shost.compareTo(host) == 0) {
                break;
            }
        }
        if (this.objName == null) {
            throw new Exception(CollectedInfo.sm.getString("collectedInfo.noConnector", new Object[] { host, port }));
        }
        this.port = iport;
        this.host = shost;
    }
    
    public void refresh() throws Exception {
        if (this.mBeanServer == null || this.objName == null) {
            throw new Exception(CollectedInfo.sm.getString("collectedInfo.notInitialized"));
        }
        final Integer imax = (Integer)this.mBeanServer.getAttribute(this.objName, "maxThreads");
        final Integer ibusy = (Integer)this.mBeanServer.getAttribute(this.objName, "currentThreadsBusy");
        this.busy = ibusy;
        this.ready = imax - ibusy;
    }
    
    static {
        sm = StringManager.getManager((Class)CollectedInfo.class);
    }
}
