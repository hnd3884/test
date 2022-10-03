package org.jfree.base.modules;

import org.jfree.base.config.HierarchicalConfiguration;
import org.jfree.base.config.PropertyFileConfiguration;
import java.io.PrintStream;
import org.jfree.util.ObjectUtilities;
import java.util.Iterator;
import org.jfree.util.Configuration;
import org.jfree.base.log.PadMessage;
import java.util.List;
import org.jfree.util.Log;
import java.util.Arrays;
import java.util.HashMap;
import org.jfree.base.AbstractBoot;
import java.util.ArrayList;

public final class PackageManager
{
    private static final int RETURN_MODULE_LOADED = 0;
    private static final int RETURN_MODULE_UNKNOWN = 1;
    private static final int RETURN_MODULE_ERROR = 2;
    private final PackageConfiguration packageConfiguration;
    private final ArrayList modules;
    private final ArrayList initSections;
    private AbstractBoot booter;
    private static HashMap instances;
    
    private PackageManager(final AbstractBoot booter) {
        if (booter == null) {
            throw new NullPointerException();
        }
        this.booter = booter;
        this.packageConfiguration = new PackageConfiguration();
        this.modules = new ArrayList();
        this.initSections = new ArrayList();
    }
    
    private int acceptVersion(final String modVer, final String depModVer) {
        final int mLength = Math.max(modVer.length(), depModVer.length());
        char[] modVerArray;
        char[] depVerArray;
        if (modVer.length() > depModVer.length()) {
            modVerArray = modVer.toCharArray();
            depVerArray = new char[mLength];
            final int delta = modVer.length() - depModVer.length();
            Arrays.fill(depVerArray, 0, delta, ' ');
            System.arraycopy(depVerArray, delta, depModVer.toCharArray(), 0, depModVer.length());
        }
        else if (modVer.length() < depModVer.length()) {
            depVerArray = depModVer.toCharArray();
            modVerArray = new char[mLength];
            final char[] b1 = new char[mLength];
            final int delta2 = depModVer.length() - modVer.length();
            Arrays.fill(b1, 0, delta2, ' ');
            System.arraycopy(b1, delta2, modVer.toCharArray(), 0, modVer.length());
        }
        else {
            depVerArray = depModVer.toCharArray();
            modVerArray = modVer.toCharArray();
        }
        return new String(modVerArray).compareTo(new String(depVerArray));
    }
    
    private boolean acceptVersion(final ModuleInfo moduleRequirement, final Module module) {
        if (moduleRequirement.getMajorVersion() == null) {
            return true;
        }
        if (module.getMajorVersion() == null) {
            Log.warn("Module " + module.getName() + " does not define a major version.");
        }
        else {
            final int compare = this.acceptVersion(moduleRequirement.getMajorVersion(), module.getMajorVersion());
            if (compare > 0) {
                return false;
            }
            if (compare < 0) {
                return true;
            }
        }
        if (moduleRequirement.getMinorVersion() == null) {
            return true;
        }
        if (module.getMinorVersion() == null) {
            Log.warn("Module " + module.getName() + " does not define a minor version.");
        }
        else {
            final int compare = this.acceptVersion(moduleRequirement.getMinorVersion(), module.getMinorVersion());
            if (compare > 0) {
                return false;
            }
            if (compare < 0) {
                return true;
            }
        }
        if (moduleRequirement.getPatchLevel() == null) {
            return true;
        }
        if (module.getPatchLevel() == null) {
            Log.debug("Module " + module.getName() + " does not define a patch level.");
        }
        else if (this.acceptVersion(moduleRequirement.getPatchLevel(), module.getPatchLevel()) > 0) {
            Log.debug("Did not accept patchlevel: " + moduleRequirement.getPatchLevel() + " - " + module.getPatchLevel());
            return false;
        }
        return true;
    }
    
    public synchronized void addModule(final String modClass) {
        final ArrayList loadModules = new ArrayList();
        final ModuleInfo modInfo = new DefaultModuleInfo(modClass, null, null, null);
        if (this.loadModule(modInfo, new ArrayList(), loadModules, false)) {
            for (int i = 0; i < loadModules.size(); ++i) {
                final Module mod = loadModules.get(i);
                this.modules.add(new PackageState(mod));
            }
        }
    }
    
    private int containsModule(final ArrayList tempModules, final ModuleInfo module) {
        if (tempModules != null) {
            final ModuleInfo[] mods = tempModules.toArray(new ModuleInfo[tempModules.size()]);
            for (int i = 0; i < mods.length; ++i) {
                if (mods[i].getModuleClass().equals(module.getModuleClass())) {
                    return 0;
                }
            }
        }
        final PackageState[] packageStates = this.modules.toArray(new PackageState[this.modules.size()]);
        int i = 0;
        while (i < packageStates.length) {
            if (packageStates[i].getModule().getModuleClass().equals(module.getModuleClass())) {
                if (packageStates[i].getState() == -2) {
                    return 2;
                }
                return 0;
            }
            else {
                ++i;
            }
        }
        return 1;
    }
    
    public static PackageManager createInstance(final AbstractBoot booter) {
        if (PackageManager.instances == null) {
            PackageManager.instances = new HashMap();
            final PackageManager manager = new PackageManager(booter);
            PackageManager.instances.put(booter, manager);
            return manager;
        }
        PackageManager manager = PackageManager.instances.get(booter);
        if (manager == null) {
            manager = new PackageManager(booter);
            PackageManager.instances.put(booter, manager);
        }
        return manager;
    }
    
    private void dropFailedModule(final PackageState state) {
        if (!this.modules.contains(state)) {
            this.modules.add(state);
        }
    }
    
    public Module[] getActiveModules() {
        final ArrayList mods = new ArrayList();
        for (int i = 0; i < this.modules.size(); ++i) {
            final PackageState state = this.modules.get(i);
            if (state.getState() == 2) {
                mods.add(state.getModule());
            }
        }
        return mods.toArray(new Module[mods.size()]);
    }
    
    public Module[] getAllModules() {
        final Module[] mods = new Module[this.modules.size()];
        for (int i = 0; i < this.modules.size(); ++i) {
            final PackageState state = this.modules.get(i);
            mods[i] = state.getModule();
        }
        return mods;
    }
    
    public PackageConfiguration getPackageConfiguration() {
        return this.packageConfiguration;
    }
    
    public synchronized void initializeModules() {
        PackageSorter.sort(this.modules);
        for (int i = 0; i < this.modules.size(); ++i) {
            final PackageState mod = this.modules.get(i);
            if (mod.configure(this.booter)) {
                Log.debug(new Log.SimpleMessage("Conf: ", new PadMessage(mod.getModule().getModuleClass(), 70), " [", mod.getModule().getSubSystem(), "]"));
            }
        }
        for (int j = 0; j < this.modules.size(); ++j) {
            final PackageState mod2 = this.modules.get(j);
            if (mod2.initialize(this.booter)) {
                Log.debug(new Log.SimpleMessage("Init: ", new PadMessage(mod2.getModule().getModuleClass(), 70), " [", mod2.getModule().getSubSystem(), "]"));
            }
        }
    }
    
    public boolean isModuleAvailable(final ModuleInfo moduleDescription) {
        final PackageState[] packageStates = this.modules.toArray(new PackageState[this.modules.size()]);
        for (int i = 0; i < packageStates.length; ++i) {
            final PackageState state = packageStates[i];
            if (state.getModule().getModuleClass().equals(moduleDescription.getModuleClass())) {
                return state.getState() == 2;
            }
        }
        return false;
    }
    
    public void load(final String modulePrefix) {
        if (this.initSections.contains(modulePrefix)) {
            return;
        }
        this.initSections.add(modulePrefix);
        final Configuration config = this.booter.getGlobalConfig();
        final Iterator it = config.findPropertyKeys(modulePrefix);
        while (it.hasNext()) {
            final String key = it.next();
            if (key.endsWith(".Module")) {
                this.addModule(config.getConfigProperty(key));
            }
        }
        Log.debug("Loaded a total of " + this.modules.size() + " modules under prefix: " + modulePrefix);
    }
    
    private boolean loadModule(final ModuleInfo moduleInfo, final ArrayList incompleteModules, final ArrayList modules, final boolean fatal) {
        try {
            final Class c = ObjectUtilities.getClassLoader(this.getClass()).loadClass(moduleInfo.getModuleClass());
            final Module module = c.newInstance();
            if (!this.acceptVersion(moduleInfo, module)) {
                Log.warn("Module " + module.getName() + ": required version: " + moduleInfo + ", but found Version: \n" + module);
                final PackageState state = new PackageState(module, -2);
                this.dropFailedModule(state);
                return false;
            }
            final int moduleContained = this.containsModule(modules, module);
            if (moduleContained == 2) {
                Log.debug("Indicated failure for module: " + module.getModuleClass());
                final PackageState state2 = new PackageState(module, -2);
                this.dropFailedModule(state2);
                return false;
            }
            if (moduleContained == 1) {
                if (incompleteModules.contains(module)) {
                    Log.error(new Log.SimpleMessage("Circular module reference: This module definition is invalid: ", module.getClass()));
                    final PackageState state2 = new PackageState(module, -2);
                    this.dropFailedModule(state2);
                    return false;
                }
                incompleteModules.add(module);
                final ModuleInfo[] required = module.getRequiredModules();
                for (int i = 0; i < required.length; ++i) {
                    if (!this.loadModule(required[i], incompleteModules, modules, true)) {
                        Log.debug("Indicated failure for module: " + module.getModuleClass());
                        final PackageState state3 = new PackageState(module, -2);
                        this.dropFailedModule(state3);
                        return false;
                    }
                }
                final ModuleInfo[] optional = module.getOptionalModules();
                for (int j = 0; j < optional.length; ++j) {
                    if (!this.loadModule(optional[j], incompleteModules, modules, true)) {
                        Log.debug(new Log.SimpleMessage("Optional module: ", optional[j].getModuleClass(), " was not loaded."));
                    }
                }
                if (this.containsModule(modules, module) == 1) {
                    modules.add(module);
                }
                incompleteModules.remove(module);
            }
            return true;
        }
        catch (final ClassNotFoundException cnfe) {
            if (fatal) {
                Log.warn(new Log.SimpleMessage("Unresolved dependency for package: ", moduleInfo.getModuleClass()));
            }
            Log.debug(new Log.SimpleMessage("ClassNotFound: ", cnfe.getMessage()));
            return false;
        }
        catch (final Exception e) {
            Log.warn(new Log.SimpleMessage("Exception while loading module: ", moduleInfo), e);
            return false;
        }
    }
    
    public void printUsedModules(final PrintStream p) {
        final Module[] allMods = this.getAllModules();
        final ArrayList activeModules = new ArrayList();
        final ArrayList failedModules = new ArrayList();
        for (int i = 0; i < allMods.length; ++i) {
            if (this.isModuleAvailable(allMods[i])) {
                activeModules.add(allMods[i]);
            }
            else {
                failedModules.add(allMods[i]);
            }
        }
        p.print("Active modules: ");
        p.println(activeModules.size());
        p.println("----------------------------------------------------------");
        for (int j = 0; j < activeModules.size(); ++j) {
            final Module mod = activeModules.get(j);
            p.print(new PadMessage(mod.getModuleClass(), 70));
            p.print(" [");
            p.print(mod.getSubSystem());
            p.println("]");
            p.print("  Version: ");
            p.print(mod.getMajorVersion());
            p.print("-");
            p.print(mod.getMinorVersion());
            p.print("-");
            p.print(mod.getPatchLevel());
            p.print(" Producer: ");
            p.println(mod.getProducer());
            p.print("  Description: ");
            p.println(mod.getDescription());
        }
    }
    
    public static class PackageConfiguration extends PropertyFileConfiguration
    {
        public void insertConfiguration(final HierarchicalConfiguration config) {
            super.insertConfiguration(config);
        }
    }
}
