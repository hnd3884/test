package com.adventnet.mfw.modulestartup;

import com.zoho.conf.Configuration;
import java.util.Collection;
import com.adventnet.mfw.ConsoleOut;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.util.logging.Level;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.logging.Logger;

public class ModuleStartStopProcessorUtil
{
    private static final Logger LOG;
    private static HashMap processorMap;
    private static HashMap processorVsModulenameMap;
    private static String server_home;
    private static LinkedHashMap<String, ArrayList<String>> moduleVsProcessorListMap;
    
    public static void loadModuleStartupProcessor(final String moduleName) throws Exception {
        final String moduleStartupProcessorFileName = ModuleStartStopProcessorUtil.server_home + "/conf/" + moduleName + "/module-startstop-processors.xml";
        final File moduleStartupProcessorFile = new File(moduleStartupProcessorFileName);
        ModuleStartStopProcessorUtil.LOG.log(Level.INFO, "Loading the module-startstop-processors.xml ::" + moduleStartupProcessorFile);
        final ArrayList<String> processorList = new ArrayList<String>();
        if (!moduleStartupProcessorFile.exists()) {
            ModuleStartStopProcessorUtil.LOG.log(Level.INFO, "No module-startstop-processor specified for the module" + moduleName);
            return;
        }
        final DataObject moduleStartupProcessorDO = Xml2DoConverter.transform(moduleStartupProcessorFile.toURI().toURL());
        final List tableNames = moduleStartupProcessorDO.getTableNames();
        final Iterator moduleStartupProcessorIterator = moduleStartupProcessorDO.getRows("ModuleStartStopProcessor");
        while (moduleStartupProcessorIterator.hasNext()) {
            final Row moduleStartupProcessorRow = moduleStartupProcessorIterator.next();
            final String moduleStartupProcessorName = (String)moduleStartupProcessorRow.get("PROCESSOR_NAME");
            final ModuleStartStopProcessor moduleStartupProcessor = (ModuleStartStopProcessor)Class.forName((String)moduleStartupProcessorRow.get("CLASSNAME")).newInstance();
            moduleStartupProcessor.initialize();
            processorList.add(moduleStartupProcessorName);
            ModuleStartStopProcessorUtil.processorMap.put(moduleStartupProcessorName, moduleStartupProcessor);
            ModuleStartStopProcessorUtil.processorVsModulenameMap.put(moduleStartupProcessorName, moduleName);
        }
        ModuleStartStopProcessorUtil.moduleVsProcessorListMap.put(moduleName, processorList);
    }
    
    public static void execute_preStartProcesses(final String moduleName) {
        try {
            loadModuleStartupProcessor(moduleName);
            if (!ModuleStartStopProcessorUtil.moduleVsProcessorListMap.containsKey(moduleName)) {
                return;
            }
            final ListIterator<String> li = ModuleStartStopProcessorUtil.moduleVsProcessorListMap.get(moduleName).listIterator();
            while (li.hasNext()) {
                final String moduleStartupProcessorName = li.next();
                printName("ModulePreStartProcess :: " + moduleName + "::" + moduleStartupProcessorName);
                final ModuleStartStopProcessor processor = ModuleStartStopProcessorUtil.processorMap.get(moduleStartupProcessorName);
                processor.preStartProcess();
                printPreStartStatus(moduleName + "::" + moduleStartupProcessorName, " STARTED ");
            }
        }
        catch (final Throwable e) {
            throw new RuntimeException("ModuleStartupProcessor Prevoke execution failed", e);
        }
    }
    
    public static void execute_postStartProcesses(final String moduleName) {
        try {
            if (!ModuleStartStopProcessorUtil.moduleVsProcessorListMap.containsKey(moduleName)) {
                return;
            }
            final ListIterator<String> li = ModuleStartStopProcessorUtil.moduleVsProcessorListMap.get(moduleName).listIterator();
            while (li.hasNext()) {
                final String moduleStartupProcessorName = li.next();
                printName("ModulePostStartProcess:: " + moduleName + "::" + moduleStartupProcessorName);
                final ModuleStartStopProcessor processor = ModuleStartStopProcessorUtil.processorMap.get(moduleStartupProcessorName);
                processor.postStartProcess();
                printPostStartStatus(moduleName + "::" + moduleStartupProcessorName, " STARTED ");
            }
        }
        catch (final Throwable e) {
            throw new RuntimeException("ModuleStartupProcessor Post invoke execution failed", e);
        }
    }
    
    public static void execute_stopProcesses() {
        if (!ModuleStartStopProcessorUtil.moduleVsProcessorListMap.values().isEmpty()) {
            ConsoleOut.println("Stopping Module processors");
            ConsoleOut.println("");
        }
        final ArrayList<String> moduleList = new ArrayList<String>();
        ModuleStartStopProcessorUtil.moduleVsProcessorListMap.values().forEach(obj -> moduleList.addAll(obj));
        final ListIterator<String> li = moduleList.listIterator(moduleList.size());
        while (li.hasPrevious()) {
            try {
                final String moduleStartupProcessorName = li.previous();
                final String moduleName = ModuleStartStopProcessorUtil.processorVsModulenameMap.get(moduleStartupProcessorName);
                final ModuleStartStopProcessor processor = ModuleStartStopProcessorUtil.processorMap.get(moduleStartupProcessorName);
                processor.stopProcess();
                printStopStatus(moduleName + "::" + moduleStartupProcessorName);
            }
            catch (final Throwable e) {
                ModuleStartStopProcessorUtil.LOG.log(Level.SEVERE, e.getMessage());
            }
        }
    }
    
    private static void printName(final String name) {
        for (int i = 1; i <= 5; ++i) {
            ConsoleOut.print(" ");
        }
        ConsoleOut.print(name);
    }
    
    private static void printPreStartStatus(final String name, final String status) {
        final String temp = "ModulePreStartProcess :: ";
        for (int i = temp.length() + name.length(); i < 45; ++i) {
            ConsoleOut.print(" ");
        }
        ConsoleOut.println("[" + status + "]");
    }
    
    private static void printPostStartStatus(final String name, final String status) {
        final String temp = "ModulePostStartProcess:: ";
        for (int i = temp.length() + name.length(); i < 45; ++i) {
            ConsoleOut.print(" ");
        }
        ConsoleOut.println("[" + status + "]");
    }
    
    private static void printStopStatus(final String name) {
        ConsoleOut.print("ModuleStopProcess:: " + name);
        for (int i = 1; i <= 5; ++i) {
            ConsoleOut.print(" ");
        }
        ConsoleOut.println("[ STOPPED ]");
    }
    
    static {
        LOG = Logger.getLogger(ModuleStartStopProcessorUtil.class.getName());
        ModuleStartStopProcessorUtil.processorMap = new HashMap();
        ModuleStartStopProcessorUtil.processorVsModulenameMap = new HashMap();
        ModuleStartStopProcessorUtil.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        ModuleStartStopProcessorUtil.moduleVsProcessorListMap = new LinkedHashMap<String, ArrayList<String>>();
    }
}
