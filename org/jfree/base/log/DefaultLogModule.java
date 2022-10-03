package org.jfree.base.log;

import org.jfree.util.LogTarget;
import org.jfree.util.PrintStreamLogTarget;
import org.jfree.util.Log;
import org.jfree.base.modules.SubSystem;
import org.jfree.base.modules.ModuleInitializeException;
import org.jfree.base.modules.AbstractModule;

public class DefaultLogModule extends AbstractModule
{
    static /* synthetic */ Class class$org$jfree$util$PrintStreamLogTarget;
    
    public DefaultLogModule() throws ModuleInitializeException {
        this.loadModuleInfo();
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public void initialize(final SubSystem subSystem) throws ModuleInitializeException {
        if (LogConfiguration.isDisableLogging()) {
            return;
        }
        if (LogConfiguration.getLogTarget().equals(((DefaultLogModule.class$org$jfree$util$PrintStreamLogTarget != null) ? DefaultLogModule.class$org$jfree$util$PrintStreamLogTarget : (DefaultLogModule.class$org$jfree$util$PrintStreamLogTarget = class$("org.jfree.util.PrintStreamLogTarget"))).getName())) {
            DefaultLog.installDefaultLog();
            Log.getInstance().addTarget(new PrintStreamLogTarget());
            if ("true".equals(subSystem.getGlobalConfig().getConfigProperty("org.jfree.base.LogAutoInit"))) {
                Log.getInstance().init();
            }
            Log.info("Default log target started ... previous log messages could have been ignored.");
        }
    }
}
