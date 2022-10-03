package org.jfree.base.modules;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import org.jfree.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

public final class PackageSorter
{
    private PackageSorter() {
    }
    
    private static ArrayList collectSubsystemModules(final Module childMod, final HashMap moduleMap) {
        final ArrayList collector = new ArrayList();
        ModuleInfo[] info = childMod.getRequiredModules();
        for (int i = 0; i < info.length; ++i) {
            final SortModule dependentModule = moduleMap.get(info[i].getModuleClass());
            if (dependentModule == null) {
                Log.warn(new Log.SimpleMessage("A dependent module was not found in the list of known modules.", info[i].getModuleClass()));
            }
            else {
                collector.add(dependentModule.getState().getModule().getSubSystem());
            }
        }
        info = childMod.getOptionalModules();
        for (int j = 0; j < info.length; ++j) {
            final Module dependentModule2 = moduleMap.get(info[j].getModuleClass());
            if (dependentModule2 == null) {
                Log.warn("A dependent module was not found in the list of known modules.");
            }
            else {
                collector.add(dependentModule2.getSubSystem());
            }
        }
        return collector;
    }
    
    private static boolean isBaseModule(final Module mod, final ModuleInfo mi) {
        ModuleInfo[] info = mod.getRequiredModules();
        for (int i = 0; i < info.length; ++i) {
            if (info[i].getModuleClass().equals(mi.getModuleClass())) {
                return true;
            }
        }
        info = mod.getOptionalModules();
        for (int j = 0; j < info.length; ++j) {
            if (info[j].getModuleClass().equals(mi.getModuleClass())) {
                return true;
            }
        }
        return false;
    }
    
    private static int searchModulePosition(final SortModule smodule, final HashMap moduleMap) {
        final Module module = smodule.getState().getModule();
        int position = 0;
        ModuleInfo[] modInfo = module.getOptionalModules();
        for (int modPos = 0; modPos < modInfo.length; ++modPos) {
            final String moduleName = modInfo[modPos].getModuleClass();
            final SortModule reqMod = moduleMap.get(moduleName);
            if (reqMod != null && reqMod.getPosition() >= position) {
                position = reqMod.getPosition() + 1;
            }
        }
        modInfo = module.getRequiredModules();
        for (int modPos2 = 0; modPos2 < modInfo.length; ++modPos2) {
            final String moduleName2 = modInfo[modPos2].getModuleClass();
            final SortModule reqMod2 = moduleMap.get(moduleName2);
            if (reqMod2.getPosition() >= position) {
                position = reqMod2.getPosition() + 1;
            }
        }
        final String subSystem = module.getSubSystem();
        final Iterator it = moduleMap.values().iterator();
        while (it.hasNext()) {
            final SortModule mod = it.next();
            if (mod.getState().getModule() != module) {
                final Module subSysMod = mod.getState().getModule();
                if (subSystem.equals(subSysMod.getSubSystem()) || !smodule.getDependSubsystems().contains(subSysMod.getSubSystem()) || isBaseModule(subSysMod, module) || mod.getPosition() < position) {
                    continue;
                }
                position = mod.getPosition() + 1;
            }
        }
        return position;
    }
    
    public static void sort(final List modules) {
        final HashMap moduleMap = new HashMap();
        final ArrayList errorModules = new ArrayList();
        final ArrayList weightModules = new ArrayList();
        for (int i = 0; i < modules.size(); ++i) {
            final PackageState state = modules.get(i);
            if (state.getState() == -2) {
                errorModules.add(state);
            }
            else {
                final SortModule mod = new SortModule(state);
                weightModules.add(mod);
                moduleMap.put(state.getModule().getModuleClass(), mod);
            }
        }
        final SortModule[] weigths = weightModules.toArray(new SortModule[weightModules.size()]);
        for (int j = 0; j < weigths.length; ++j) {
            final SortModule sortMod = weigths[j];
            sortMod.setDependSubsystems(collectSubsystemModules(sortMod.getState().getModule(), moduleMap));
        }
        boolean doneWork = true;
        while (doneWork) {
            doneWork = false;
            for (int k = 0; k < weigths.length; ++k) {
                final SortModule mod2 = weigths[k];
                final int position = searchModulePosition(mod2, moduleMap);
                if (position != mod2.getPosition()) {
                    mod2.setPosition(position);
                    doneWork = true;
                }
            }
        }
        Arrays.sort(weigths);
        modules.clear();
        for (int k = 0; k < weigths.length; ++k) {
            modules.add(weigths[k].getState());
        }
        for (int l = 0; l < errorModules.size(); ++l) {
            modules.add(errorModules.get(l));
        }
    }
    
    private static class SortModule implements Comparable
    {
        private int position;
        private final PackageState state;
        private ArrayList dependSubsystems;
        
        public SortModule(final PackageState state) {
            this.position = -1;
            this.state = state;
        }
        
        public int compareTo(final Object o) {
            final SortModule otherModule = (SortModule)o;
            if (this.position > otherModule.position) {
                return 1;
            }
            if (this.position < otherModule.position) {
                return -1;
            }
            return 0;
        }
        
        public ArrayList getDependSubsystems() {
            return this.dependSubsystems;
        }
        
        public int getPosition() {
            return this.position;
        }
        
        public PackageState getState() {
            return this.state;
        }
        
        public void setDependSubsystems(final ArrayList dependSubsystems) {
            this.dependSubsystems = dependSubsystems;
        }
        
        public void setPosition(final int position) {
            this.position = position;
        }
        
        public String toString() {
            final StringBuffer buffer = new StringBuffer();
            buffer.append("SortModule: ");
            buffer.append(this.position);
            buffer.append(" ");
            buffer.append(this.state.getModule().getName());
            buffer.append(" ");
            buffer.append(this.state.getModule().getModuleClass());
            return buffer.toString();
        }
    }
}
