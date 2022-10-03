package org.jfree.base;

import org.jfree.util.ObjectUtilities;
import org.jfree.util.Configuration;
import org.jfree.base.config.ModifiableConfiguration;
import org.jfree.JCommon;

public class BaseBoot extends AbstractBoot
{
    private static BaseBoot singleton;
    private BootableProjectInfo bootableProjectInfo;
    static /* synthetic */ Class class$org$jfree$base$log$DefaultLogModule;
    
    private BaseBoot() {
        this.bootableProjectInfo = JCommon.INFO;
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public static ModifiableConfiguration getConfiguration() {
        return (ModifiableConfiguration)getInstance().getGlobalConfig();
    }
    
    public static synchronized AbstractBoot getInstance() {
        if (BaseBoot.singleton == null) {
            BaseBoot.singleton = new BaseBoot();
        }
        return BaseBoot.singleton;
    }
    
    protected BootableProjectInfo getProjectInfo() {
        return this.bootableProjectInfo;
    }
    
    protected synchronized Configuration loadConfiguration() {
        return this.createDefaultHierarchicalConfiguration("/org/jfree/base/jcommon.properties", "/jcommon.properties", true);
    }
    
    protected void performBoot() {
        ObjectUtilities.setClassLoaderSource(getConfiguration().getConfigProperty("org.jfree.ClassLoader"));
        this.getPackageManager().addModule(((BaseBoot.class$org$jfree$base$log$DefaultLogModule != null) ? BaseBoot.class$org$jfree$base$log$DefaultLogModule : (BaseBoot.class$org$jfree$base$log$DefaultLogModule = class$("org.jfree.base.log.DefaultLogModule"))).getName());
        this.getPackageManager().load("org.jfree.jcommon.modules.");
        this.getPackageManager().initializeModules();
    }
}
