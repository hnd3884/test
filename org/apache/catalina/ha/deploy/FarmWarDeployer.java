package org.apache.catalina.ha.deploy;

import org.apache.juli.logging.LogFactory;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import org.apache.catalina.Context;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.tribes.Member;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Container;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.catalina.Engine;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import org.apache.catalina.Host;
import java.io.File;
import java.util.HashMap;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.ha.ClusterDeployer;
import org.apache.catalina.ha.ClusterListener;

public class FarmWarDeployer extends ClusterListener implements ClusterDeployer, FileChangeListener
{
    private static final Log log;
    private static final StringManager sm;
    protected boolean started;
    protected final HashMap<String, FileMessageFactory> fileFactories;
    protected String deployDir;
    private File deployDirFile;
    protected String tempDir;
    private File tempDirFile;
    protected String watchDir;
    private File watchDirFile;
    protected boolean watchEnabled;
    protected WarWatcher watcher;
    private int count;
    protected int processDeployFrequency;
    protected File configBase;
    protected Host host;
    protected MBeanServer mBeanServer;
    protected ObjectName oname;
    protected int maxValidTime;
    
    public FarmWarDeployer() {
        this.started = false;
        this.fileFactories = new HashMap<String, FileMessageFactory>();
        this.deployDirFile = null;
        this.tempDirFile = null;
        this.watchDirFile = null;
        this.watchEnabled = false;
        this.watcher = null;
        this.count = 0;
        this.processDeployFrequency = 2;
        this.configBase = null;
        this.host = null;
        this.mBeanServer = null;
        this.oname = null;
        this.maxValidTime = 300;
    }
    
    @Override
    public void start() throws Exception {
        if (this.started) {
            return;
        }
        final Container hcontainer = this.getCluster().getContainer();
        if (!(hcontainer instanceof Host)) {
            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.hostOnly"));
            return;
        }
        this.host = (Host)hcontainer;
        final Container econtainer = this.host.getParent();
        if (!(econtainer instanceof Engine)) {
            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.hostParentEngine", new Object[] { this.host.getName() }));
            return;
        }
        final Engine engine = (Engine)econtainer;
        String hostname = null;
        hostname = this.host.getName();
        try {
            this.oname = new ObjectName(engine.getName() + ":type=Deployer,host=" + hostname);
        }
        catch (final Exception e) {
            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.mbeanNameFail", new Object[] { engine.getName(), hostname }), (Throwable)e);
            return;
        }
        if (this.watchEnabled) {
            this.watcher = new WarWatcher(this, this.getWatchDirFile());
            if (FarmWarDeployer.log.isInfoEnabled()) {
                FarmWarDeployer.log.info((Object)FarmWarDeployer.sm.getString("farmWarDeployer.watchDir", new Object[] { this.getWatchDir() }));
            }
        }
        this.configBase = this.host.getConfigBaseFile();
        this.mBeanServer = Registry.getRegistry((Object)null, (Object)null).getMBeanServer();
        this.started = true;
        this.count = 0;
        this.getCluster().addClusterListener(this);
        if (FarmWarDeployer.log.isInfoEnabled()) {
            FarmWarDeployer.log.info((Object)FarmWarDeployer.sm.getString("farmWarDeployer.started"));
        }
    }
    
    @Override
    public void stop() throws LifecycleException {
        this.started = false;
        this.getCluster().removeClusterListener(this);
        this.count = 0;
        if (this.watcher != null) {
            this.watcher.clear();
            this.watcher = null;
        }
        if (FarmWarDeployer.log.isInfoEnabled()) {
            FarmWarDeployer.log.info((Object)FarmWarDeployer.sm.getString("farmWarDeployer.stopped"));
        }
    }
    
    @Override
    public void messageReceived(final ClusterMessage msg) {
        try {
            if (msg instanceof FileMessage) {
                final FileMessage fmsg = (FileMessage)msg;
                if (FarmWarDeployer.log.isDebugEnabled()) {
                    FarmWarDeployer.log.debug((Object)FarmWarDeployer.sm.getString("farmWarDeployer.msgRxDeploy", new Object[] { fmsg.getContextName(), fmsg.getFileName() }));
                }
                final FileMessageFactory factory = this.getFactory(fmsg);
                if (factory.writeMessage(fmsg)) {
                    String name = factory.getFile().getName();
                    if (!name.endsWith(".war")) {
                        name += ".war";
                    }
                    final File deployable = new File(this.getDeployDirFile(), name);
                    try {
                        final String contextName = fmsg.getContextName();
                        if (this.tryAddServiced(contextName)) {
                            try {
                                this.remove(contextName);
                                if (!factory.getFile().renameTo(deployable)) {
                                    FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.renameFail", new Object[] { factory.getFile(), deployable }));
                                }
                            }
                            finally {
                                this.removeServiced(contextName);
                            }
                            this.check(contextName);
                            if (FarmWarDeployer.log.isDebugEnabled()) {
                                FarmWarDeployer.log.debug((Object)FarmWarDeployer.sm.getString("farmWarDeployer.deployEnd", new Object[] { contextName }));
                            }
                        }
                        else {
                            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.servicingDeploy", new Object[] { contextName, name }));
                        }
                    }
                    catch (final Exception ex) {
                        FarmWarDeployer.log.error((Object)ex);
                    }
                    finally {
                        this.removeFactory(fmsg);
                    }
                }
            }
            else if (msg instanceof UndeployMessage) {
                try {
                    final UndeployMessage umsg = (UndeployMessage)msg;
                    final String contextName2 = umsg.getContextName();
                    if (FarmWarDeployer.log.isDebugEnabled()) {
                        FarmWarDeployer.log.debug((Object)FarmWarDeployer.sm.getString("farmWarDeployer.msgRxUndeploy", new Object[] { contextName2 }));
                    }
                    if (this.tryAddServiced(contextName2)) {
                        try {
                            this.remove(contextName2);
                        }
                        finally {
                            this.removeServiced(contextName2);
                        }
                        if (FarmWarDeployer.log.isDebugEnabled()) {
                            FarmWarDeployer.log.debug((Object)FarmWarDeployer.sm.getString("farmWarDeployer.undeployEnd", new Object[] { contextName2 }));
                        }
                    }
                    else {
                        FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.servicingUndeploy", new Object[] { contextName2 }));
                    }
                }
                catch (final Exception ex2) {
                    FarmWarDeployer.log.error((Object)ex2);
                }
            }
        }
        catch (final IOException x) {
            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.msgIoe"), (Throwable)x);
        }
    }
    
    public synchronized FileMessageFactory getFactory(final FileMessage msg) throws FileNotFoundException, IOException {
        final File writeToFile = new File(this.getTempDirFile(), msg.getFileName());
        FileMessageFactory factory = this.fileFactories.get(msg.getFileName());
        if (factory == null) {
            factory = FileMessageFactory.getInstance(writeToFile, true);
            factory.setMaxValidTime(this.maxValidTime);
            this.fileFactories.put(msg.getFileName(), factory);
        }
        return factory;
    }
    
    public void removeFactory(final FileMessage msg) {
        this.fileFactories.remove(msg.getFileName());
    }
    
    @Override
    public boolean accept(final ClusterMessage msg) {
        return msg instanceof FileMessage || msg instanceof UndeployMessage;
    }
    
    @Override
    public void install(final String contextName, final File webapp) throws IOException {
        final Member[] members = this.getCluster().getMembers();
        if (members.length == 0) {
            return;
        }
        final Member localMember = this.getCluster().getLocalMember();
        final FileMessageFactory factory = FileMessageFactory.getInstance(webapp, false);
        FileMessage msg = new FileMessage(localMember, webapp.getName(), contextName);
        if (FarmWarDeployer.log.isDebugEnabled()) {
            FarmWarDeployer.log.debug((Object)FarmWarDeployer.sm.getString("farmWarDeployer.sendStart", new Object[] { contextName, webapp }));
        }
        for (msg = factory.readMessage(msg); msg != null; msg = factory.readMessage(msg)) {
            for (final Member member : members) {
                if (FarmWarDeployer.log.isDebugEnabled()) {
                    FarmWarDeployer.log.debug((Object)FarmWarDeployer.sm.getString("farmWarDeployer.sendFragment", new Object[] { contextName, webapp, member }));
                }
                this.getCluster().send(msg, member);
            }
        }
        if (FarmWarDeployer.log.isDebugEnabled()) {
            FarmWarDeployer.log.debug((Object)FarmWarDeployer.sm.getString("farmWarDeployer.sendEnd", new Object[] { contextName, webapp }));
        }
    }
    
    @Override
    public void remove(final String contextName, final boolean undeploy) throws IOException {
        if (this.getCluster().getMembers().length > 0) {
            if (FarmWarDeployer.log.isInfoEnabled()) {
                FarmWarDeployer.log.info((Object)FarmWarDeployer.sm.getString("farmWarDeployer.removeStart", new Object[] { contextName }));
            }
            final Member localMember = this.getCluster().getLocalMember();
            final UndeployMessage msg = new UndeployMessage(localMember, System.currentTimeMillis(), "Undeploy:" + contextName + ":" + System.currentTimeMillis(), contextName);
            if (FarmWarDeployer.log.isDebugEnabled()) {
                FarmWarDeployer.log.debug((Object)FarmWarDeployer.sm.getString("farmWarDeployer.removeTxMsg", new Object[] { contextName }));
            }
            this.cluster.send(msg);
        }
        if (undeploy) {
            try {
                if (this.tryAddServiced(contextName)) {
                    try {
                        this.remove(contextName);
                    }
                    finally {
                        this.removeServiced(contextName);
                    }
                    this.check(contextName);
                }
                else {
                    FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.removeFailRemote", new Object[] { contextName }));
                }
            }
            catch (final Exception ex) {
                FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.removeFailLocal", new Object[] { contextName }), (Throwable)ex);
            }
        }
    }
    
    @Override
    public void fileModified(final File newWar) {
        try {
            final File deployWar = new File(this.getDeployDirFile(), newWar.getName());
            final ContextName cn = new ContextName(deployWar.getName(), true);
            if (deployWar.exists() && deployWar.lastModified() > newWar.lastModified()) {
                if (FarmWarDeployer.log.isInfoEnabled()) {
                    FarmWarDeployer.log.info((Object)FarmWarDeployer.sm.getString("farmWarDeployer.alreadyDeployed", new Object[] { cn.getName() }));
                }
                return;
            }
            if (FarmWarDeployer.log.isInfoEnabled()) {
                FarmWarDeployer.log.info((Object)FarmWarDeployer.sm.getString("farmWarDeployer.modInstall", new Object[] { cn.getName(), deployWar.getAbsolutePath() }));
            }
            if (this.tryAddServiced(cn.getName())) {
                try {
                    this.copy(newWar, deployWar);
                }
                finally {
                    this.removeServiced(cn.getName());
                }
                this.check(cn.getName());
            }
            else {
                FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.servicingDeploy", new Object[] { cn.getName(), deployWar.getName() }));
            }
            this.install(cn.getName(), deployWar);
        }
        catch (final Exception x) {
            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.modInstallFail"), (Throwable)x);
        }
    }
    
    @Override
    public void fileRemoved(final File removeWar) {
        try {
            final ContextName cn = new ContextName(removeWar.getName(), true);
            if (FarmWarDeployer.log.isInfoEnabled()) {
                FarmWarDeployer.log.info((Object)FarmWarDeployer.sm.getString("farmWarDeployer.removeLocal", new Object[] { cn.getName() }));
            }
            this.remove(cn.getName(), true);
        }
        catch (final Exception x) {
            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.removeLocalFail"), (Throwable)x);
        }
    }
    
    protected void remove(final String contextName) throws Exception {
        final Context context = (Context)this.host.findChild(contextName);
        if (context != null) {
            if (FarmWarDeployer.log.isDebugEnabled()) {
                FarmWarDeployer.log.debug((Object)FarmWarDeployer.sm.getString("farmWarDeployer.undeployLocal", new Object[] { contextName }));
            }
            context.stop();
            final String baseName = context.getBaseName();
            final File war = new File(this.host.getAppBaseFile(), baseName + ".war");
            final File dir = new File(this.host.getAppBaseFile(), baseName);
            final File xml = new File(this.configBase, baseName + ".xml");
            if (war.exists()) {
                if (!war.delete()) {
                    FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.deleteFail", new Object[] { war }));
                }
            }
            else if (dir.exists()) {
                this.undeployDir(dir);
            }
            else if (!xml.delete()) {
                FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.deleteFail", new Object[] { xml }));
            }
        }
    }
    
    protected void undeployDir(final File dir) {
        String[] files = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (final String s : files) {
            final File file = new File(dir, s);
            if (file.isDirectory()) {
                this.undeployDir(file);
            }
            else if (!file.delete()) {
                FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.deleteFail", new Object[] { file }));
            }
        }
        if (!dir.delete()) {
            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.deleteFail", new Object[] { dir }));
        }
    }
    
    @Override
    public void backgroundProcess() {
        if (this.started) {
            if (this.watchEnabled) {
                this.count = (this.count + 1) % this.processDeployFrequency;
                if (this.count == 0) {
                    this.watcher.check();
                }
            }
            this.removeInvalidFileFactories();
        }
    }
    
    protected void check(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        this.mBeanServer.invoke(this.oname, "check", params, signature);
    }
    
    @Deprecated
    protected boolean isServiced(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        final Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "isServiced", params, signature);
        return result;
    }
    
    @Deprecated
    protected void addServiced(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        this.mBeanServer.invoke(this.oname, "addServiced", params, signature);
    }
    
    protected boolean tryAddServiced(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        final Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "tryAddServiced", params, signature);
        return result;
    }
    
    protected void removeServiced(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        this.mBeanServer.invoke(this.oname, "removeServiced", params, signature);
    }
    
    @Override
    public boolean equals(final Object listener) {
        return super.equals(listener);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public String getDeployDir() {
        return this.deployDir;
    }
    
    public File getDeployDirFile() {
        if (this.deployDirFile != null) {
            return this.deployDirFile;
        }
        final File dir = this.getAbsolutePath(this.getDeployDir());
        return this.deployDirFile = dir;
    }
    
    public void setDeployDir(final String deployDir) {
        this.deployDir = deployDir;
    }
    
    public String getTempDir() {
        return this.tempDir;
    }
    
    public File getTempDirFile() {
        if (this.tempDirFile != null) {
            return this.tempDirFile;
        }
        final File dir = this.getAbsolutePath(this.getTempDir());
        return this.tempDirFile = dir;
    }
    
    public void setTempDir(final String tempDir) {
        this.tempDir = tempDir;
    }
    
    public String getWatchDir() {
        return this.watchDir;
    }
    
    public File getWatchDirFile() {
        if (this.watchDirFile != null) {
            return this.watchDirFile;
        }
        final File dir = this.getAbsolutePath(this.getWatchDir());
        return this.watchDirFile = dir;
    }
    
    public void setWatchDir(final String watchDir) {
        this.watchDir = watchDir;
    }
    
    public boolean isWatchEnabled() {
        return this.watchEnabled;
    }
    
    public boolean getWatchEnabled() {
        return this.watchEnabled;
    }
    
    public void setWatchEnabled(final boolean watchEnabled) {
        this.watchEnabled = watchEnabled;
    }
    
    public int getProcessDeployFrequency() {
        return this.processDeployFrequency;
    }
    
    public void setProcessDeployFrequency(final int processExpiresFrequency) {
        if (processExpiresFrequency <= 0) {
            return;
        }
        this.processDeployFrequency = processExpiresFrequency;
    }
    
    public int getMaxValidTime() {
        return this.maxValidTime;
    }
    
    public void setMaxValidTime(final int maxValidTime) {
        this.maxValidTime = maxValidTime;
    }
    
    protected boolean copy(final File from, final File to) {
        try {
            if (!to.exists() && !to.createNewFile()) {
                FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("fileNewFail", new Object[] { to }));
                return false;
            }
        }
        catch (final IOException e) {
            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.fileCopyFail", new Object[] { from, to }), (Throwable)e);
            return false;
        }
        try (final FileInputStream is = new FileInputStream(from);
             final FileOutputStream os = new FileOutputStream(to, false)) {
            final byte[] buf = new byte[4096];
            while (true) {
                final int len = is.read(buf);
                if (len < 0) {
                    break;
                }
                os.write(buf, 0, len);
            }
        }
        catch (final IOException e) {
            FarmWarDeployer.log.error((Object)FarmWarDeployer.sm.getString("farmWarDeployer.fileCopyFail", new Object[] { from, to }), (Throwable)e);
            return false;
        }
        return true;
    }
    
    protected void removeInvalidFileFactories() {
        final String[] arr$;
        final String[] fileNames = arr$ = this.fileFactories.keySet().toArray(new String[0]);
        for (final String fileName : arr$) {
            final FileMessageFactory factory = this.fileFactories.get(fileName);
            if (!factory.isValid()) {
                this.fileFactories.remove(fileName);
            }
        }
    }
    
    private File getAbsolutePath(final String path) {
        File dir = new File(path);
        if (!dir.isAbsolute()) {
            dir = new File(this.getCluster().getContainer().getCatalinaBase(), dir.getPath());
        }
        try {
            dir = dir.getCanonicalFile();
        }
        catch (final IOException ex) {}
        return dir;
    }
    
    static {
        log = LogFactory.getLog((Class)FarmWarDeployer.class);
        sm = StringManager.getManager((Class)FarmWarDeployer.class);
    }
}
