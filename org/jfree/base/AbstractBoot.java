package org.jfree.base;

import java.lang.reflect.Method;
import org.jfree.util.ExtendedConfiguration;
import java.io.InputStream;
import java.util.Enumeration;
import org.jfree.base.config.SystemPropertyConfiguration;
import java.io.IOException;
import org.jfree.util.Log;
import java.net.URL;
import java.util.ArrayList;
import org.jfree.util.ObjectUtilities;
import org.jfree.base.config.PropertyFileConfiguration;
import org.jfree.base.config.HierarchicalConfiguration;
import org.jfree.util.Configuration;
import org.jfree.base.modules.PackageManager;
import org.jfree.util.ExtendedConfigurationWrapper;
import org.jfree.base.modules.SubSystem;

public abstract class AbstractBoot implements SubSystem
{
    private ExtendedConfigurationWrapper extWrapper;
    private PackageManager packageManager;
    private Configuration globalConfig;
    private boolean bootInProgress;
    private boolean bootDone;
    
    protected AbstractBoot() {
    }
    
    protected Configuration createDefaultHierarchicalConfiguration(final String staticConfig, final String userConfig, final boolean addSysProps) {
        final HierarchicalConfiguration globalConfig = new HierarchicalConfiguration();
        if (staticConfig != null) {
            final PropertyFileConfiguration rootProperty = new PropertyFileConfiguration();
            rootProperty.load(staticConfig);
            globalConfig.insertConfiguration(rootProperty);
            globalConfig.insertConfiguration(this.getPackageManager().getPackageConfiguration());
        }
        if (userConfig != null) {
            String userConfigStripped;
            if (userConfig.startsWith("/")) {
                userConfigStripped = userConfig.substring(1);
            }
            else {
                userConfigStripped = userConfig;
            }
            try {
                final Enumeration userConfigs = ObjectUtilities.getClassLoader(this.getClass()).getResources(userConfigStripped);
                final ArrayList configs = new ArrayList();
                while (userConfigs.hasMoreElements()) {
                    final URL url = userConfigs.nextElement();
                    try {
                        final PropertyFileConfiguration baseProperty = new PropertyFileConfiguration();
                        final InputStream in = url.openStream();
                        baseProperty.load(in);
                        in.close();
                        configs.add(baseProperty);
                    }
                    catch (final IOException ioe) {
                        Log.warn("Failed to load the user configuration at " + url, ioe);
                    }
                }
                for (int i = configs.size() - 1; i >= 0; --i) {
                    final PropertyFileConfiguration baseProperty = configs.get(i);
                    globalConfig.insertConfiguration(baseProperty);
                }
            }
            catch (final IOException e) {
                Log.warn("Failed to lookup the user configurations.", e);
            }
        }
        final SystemPropertyConfiguration systemConfig = new SystemPropertyConfiguration();
        globalConfig.insertConfiguration(systemConfig);
        return globalConfig;
    }
    
    public synchronized ExtendedConfiguration getExtendedConfig() {
        if (this.extWrapper == null) {
            this.extWrapper = new ExtendedConfigurationWrapper(this.getGlobalConfig());
        }
        return this.extWrapper;
    }
    
    public synchronized Configuration getGlobalConfig() {
        if (this.globalConfig == null) {
            this.globalConfig = this.loadConfiguration();
        }
        return this.globalConfig;
    }
    
    public synchronized PackageManager getPackageManager() {
        if (this.packageManager == null) {
            this.packageManager = PackageManager.createInstance(this);
        }
        return this.packageManager;
    }
    
    protected abstract BootableProjectInfo getProjectInfo();
    
    public final synchronized boolean isBootDone() {
        return this.bootDone;
    }
    
    public final synchronized boolean isBootInProgress() {
        return this.bootInProgress;
    }
    
    protected AbstractBoot loadBooter(final String classname) {
        if (classname == null) {
            return null;
        }
        try {
            final Class c = ObjectUtilities.getClassLoader(this.getClass()).loadClass(classname);
            final Method m = c.getMethod("getInstance", (Class[])null);
            return (AbstractBoot)m.invoke(null, (Object[])null);
        }
        catch (final Exception ex) {
            Log.info("Unable to boot dependent class: " + classname);
            return null;
        }
    }
    
    protected abstract Configuration loadConfiguration();
    
    protected abstract void performBoot();
    
    public final void start() {
        synchronized (this) {
            if (this.isBootDone()) {
                monitorexit(this);
                return;
            }
            while (this.isBootInProgress()) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {}
            }
            this.bootInProgress = true;
        }
        final BootableProjectInfo info = this.getProjectInfo();
        if (info != null) {
            final BootableProjectInfo[] childs = info.getDependencies();
            for (int i = 0; i < childs.length; ++i) {
                final AbstractBoot boot = this.loadBooter(childs[i].getBootClass());
                if (boot != null) {
                    synchronized (boot) {
                        boot.start();
                        while (!boot.isBootDone()) {
                            try {
                                boot.wait();
                            }
                            catch (final InterruptedException ex2) {}
                        }
                        monitorexit(boot);
                    }
                }
            }
        }
        this.performBoot();
        if (info != null) {
            Log.info(String.valueOf(info.getName()) + " " + info.getVersion() + " started.");
        }
        else {
            Log.info(String.valueOf(String.valueOf(this.getClass())) + " started.");
        }
        synchronized (this) {
            this.bootInProgress = false;
            this.bootDone = true;
            this.notifyAll();
        }
    }
}
