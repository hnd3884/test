package org.apache.catalina.storeconfig;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.Service;
import org.apache.catalina.Host;
import java.net.URL;
import java.io.PrintWriter;
import org.apache.catalina.Context;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;
import org.apache.catalina.core.StandardContext;
import javax.management.ObjectName;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.catalina.Server;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class StoreConfig implements IStoreConfig
{
    private static Log log;
    protected static final StringManager sm;
    private String serverFilename;
    private StoreRegistry registry;
    private Server server;
    
    public StoreConfig() {
        this.serverFilename = "conf/server.xml";
    }
    
    public String getServerFilename() {
        return this.serverFilename;
    }
    
    public void setServerFilename(final String string) {
        this.serverFilename = string;
    }
    
    @Override
    public StoreRegistry getRegistry() {
        return this.registry;
    }
    
    @Override
    public void setServer(final Server aServer) {
        this.server = aServer;
    }
    
    @Override
    public Server getServer() {
        return this.server;
    }
    
    @Override
    public void setRegistry(final StoreRegistry aRegistry) {
        this.registry = aRegistry;
    }
    
    @Override
    public void storeConfig() {
        this.store(this.server);
    }
    
    public synchronized void storeServer(final String aServerName, final boolean backup, final boolean externalAllowed) throws MalformedObjectNameException {
        if (aServerName == null || aServerName.length() == 0) {
            if (StoreConfig.log.isErrorEnabled()) {
                StoreConfig.log.error((Object)"Please, call with a correct server ObjectName!");
            }
            return;
        }
        final MBeanServer mserver = MBeanUtils.createServer();
        final ObjectName objectName = new ObjectName(aServerName);
        if (mserver.isRegistered(objectName)) {
            try {
                final Server aServer = (Server)mserver.getAttribute(objectName, "managedResource");
                StoreDescription desc = null;
                desc = this.getRegistry().findDescription(StandardContext.class);
                if (desc != null) {
                    final boolean oldSeparate = desc.isStoreSeparate();
                    final boolean oldBackup = desc.isBackup();
                    final boolean oldExternalAllowed = desc.isExternalAllowed();
                    try {
                        desc.setStoreSeparate(true);
                        desc.setBackup(backup);
                        desc.setExternalAllowed(externalAllowed);
                        this.store(aServer);
                    }
                    finally {
                        desc.setStoreSeparate(oldSeparate);
                        desc.setBackup(oldBackup);
                        desc.setExternalAllowed(oldExternalAllowed);
                    }
                }
                else {
                    this.store(aServer);
                }
            }
            catch (final Exception e) {
                if (StoreConfig.log.isInfoEnabled()) {
                    StoreConfig.log.info((Object)("Object " + aServerName + " is no a Server instance or store exception"), (Throwable)e);
                }
            }
        }
        else if (StoreConfig.log.isInfoEnabled()) {
            StoreConfig.log.info((Object)("Server " + aServerName + " not found!"));
        }
    }
    
    public synchronized void storeContext(final String aContextName, final boolean backup, final boolean externalAllowed) throws MalformedObjectNameException {
        if (aContextName == null || aContextName.length() == 0) {
            if (StoreConfig.log.isErrorEnabled()) {
                StoreConfig.log.error((Object)"Please, call with a correct context ObjectName!");
            }
            return;
        }
        final MBeanServer mserver = MBeanUtils.createServer();
        final ObjectName objectName = new ObjectName(aContextName);
        if (mserver.isRegistered(objectName)) {
            try {
                final Context aContext = (Context)mserver.getAttribute(objectName, "managedResource");
                final URL configFile = aContext.getConfigFile();
                if (configFile != null) {
                    try {
                        StoreDescription desc = null;
                        desc = this.getRegistry().findDescription(aContext.getClass());
                        if (desc != null) {
                            final boolean oldSeparate = desc.isStoreSeparate();
                            final boolean oldBackup = desc.isBackup();
                            final boolean oldExternalAllowed = desc.isExternalAllowed();
                            try {
                                desc.setStoreSeparate(true);
                                desc.setBackup(backup);
                                desc.setExternalAllowed(externalAllowed);
                                desc.getStoreFactory().store(null, -2, aContext);
                            }
                            finally {
                                desc.setStoreSeparate(oldSeparate);
                                desc.setBackup(oldBackup);
                                desc.setBackup(oldExternalAllowed);
                            }
                        }
                    }
                    catch (final Exception e) {
                        StoreConfig.log.error((Object)e);
                    }
                }
                else {
                    StoreConfig.log.error((Object)("Missing configFile at Context " + aContext.getPath() + " to store!"));
                }
            }
            catch (final Exception e2) {
                if (StoreConfig.log.isInfoEnabled()) {
                    StoreConfig.log.info((Object)("Object " + aContextName + " is no a context instance or store exception"), (Throwable)e2);
                }
            }
        }
        else if (StoreConfig.log.isInfoEnabled()) {
            StoreConfig.log.info((Object)("Context " + aContextName + " not found!"));
        }
    }
    
    @Override
    public synchronized boolean store(final Server aServer) {
        final StoreFileMover mover = new StoreFileMover(System.getProperty("catalina.base"), this.getServerFilename(), this.getRegistry().getEncoding());
        try {
            try (final PrintWriter writer = mover.getWriter()) {
                this.store(writer, -2, aServer);
            }
            mover.move();
            return true;
        }
        catch (final Exception e) {
            StoreConfig.log.error((Object)StoreConfig.sm.getString("config.storeServerError"), (Throwable)e);
            return false;
        }
    }
    
    @Override
    public synchronized boolean store(final Context aContext) {
        try {
            StoreDescription desc = null;
            desc = this.getRegistry().findDescription(aContext.getClass());
            if (desc != null) {
                final boolean old = desc.isStoreSeparate();
                try {
                    desc.setStoreSeparate(true);
                    desc.getStoreFactory().store(null, -2, aContext);
                }
                finally {
                    desc.setStoreSeparate(old);
                }
            }
            return true;
        }
        catch (final Exception e) {
            StoreConfig.log.error((Object)StoreConfig.sm.getString("config.storeContextError", new Object[] { aContext.getName() }), (Throwable)e);
            return false;
        }
    }
    
    @Override
    public void store(final PrintWriter aWriter, final int indent, final Context aContext) throws Exception {
        boolean oldSeparate = true;
        StoreDescription desc = null;
        try {
            desc = this.getRegistry().findDescription(aContext.getClass());
            oldSeparate = desc.isStoreSeparate();
            desc.setStoreSeparate(false);
            desc.getStoreFactory().store(aWriter, indent, aContext);
        }
        finally {
            if (desc != null) {
                desc.setStoreSeparate(oldSeparate);
            }
        }
    }
    
    @Override
    public void store(final PrintWriter aWriter, final int indent, final Host aHost) throws Exception {
        final StoreDescription desc = this.getRegistry().findDescription(aHost.getClass());
        desc.getStoreFactory().store(aWriter, indent, aHost);
    }
    
    @Override
    public void store(final PrintWriter aWriter, final int indent, final Service aService) throws Exception {
        final StoreDescription desc = this.getRegistry().findDescription(aService.getClass());
        desc.getStoreFactory().store(aWriter, indent, aService);
    }
    
    @Override
    public void store(final PrintWriter writer, final int indent, final Server aServer) throws Exception {
        final StoreDescription desc = this.getRegistry().findDescription(aServer.getClass());
        desc.getStoreFactory().store(writer, indent, aServer);
    }
    
    static {
        StoreConfig.log = LogFactory.getLog((Class)StoreConfig.class);
        sm = StringManager.getManager("org.apache.catalina.storeconfig");
    }
}
