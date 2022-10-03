package com.me.devicemanagement.onpremise.tools.servertroubleshooter.util;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.Properties;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.start.ToolCaller;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class STSToolWorker
{
    private static final Logger LOGGER;
    private static final Logger COMMON_LOGGER;
    private static STSToolWorker stsToolWorkerObj;
    private static String toolName;
    private ExecutorService stsWorkers;
    private List<STSInterface> toolsClassList;
    
    private STSToolWorker() {
        this.stsWorkers = null;
        this.toolsClassList = null;
    }
    
    public static STSToolWorker getInstance() {
        if (STSToolWorker.stsToolWorkerObj == null) {
            STSToolWorker.stsToolWorkerObj = new STSToolWorker();
        }
        return STSToolWorker.stsToolWorkerObj;
    }
    
    public void executeTools() {
        STSToolWorker.LOGGER.log(Level.INFO, "Starting worker pool...");
        try {
            this.initializeWorkerPool();
            this.startTools();
            this.waitForCompletion();
            STSToolWorker.LOGGER.log(Level.INFO, "Tools execution completed");
        }
        catch (final Exception ex) {
            STSToolWorker.LOGGER.log(Level.WARNING, "Caught exception in STSToolWorker while executing tools : ", ex);
        }
    }
    
    private void initializeWorkerPool() throws IOException {
        STSToolWorker.LOGGER.log(Level.FINE, "Getting worker pool size from conf file");
        final String workerpoolSizeAsString = STSToolUtil.getSTSConfFileProps().getProperty("sts.tool.workerpool.size", new String("10"));
        final int workerpoolSize = Integer.parseInt(workerpoolSizeAsString.trim());
        STSToolWorker.LOGGER.log(Level.FINE, "Worker pool size from conf file : {0}", workerpoolSize);
        this.stsWorkers = Executors.newFixedThreadPool(workerpoolSize);
        STSToolWorker.LOGGER.log(Level.INFO, "Started Worker pool with size : {0}", workerpoolSize);
    }
    
    private void startTools() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        STSToolWorker.LOGGER.log(Level.FINE, "Going to start the tools");
        this.toolsClassList = getToolsClassList();
        for (final STSInterface classObj : this.toolsClassList) {
            STSToolWorker.LOGGER.log(Level.INFO, "Submitting {0} tool in new Thread", classObj.getClass().getName());
            final ToolCaller newClassThread = new ToolCaller();
            newClassThread.toolObj = classObj;
            this.stsWorkers.submit((Callable<Object>)newClassThread);
        }
        STSToolWorker.LOGGER.log(Level.INFO, "{0} tools started", this.toolsClassList.size());
    }
    
    private static List<STSInterface> getToolsClassList() throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        STSToolWorker.LOGGER.log(Level.INFO, "Preparing list of tools to be started");
        List<STSInterface> toolsClassList = null;
        final Properties stsConfProps = STSToolUtil.getSTSConfFileProps();
        final String toolsList = stsConfProps.getProperty("tools.list");
        if (toolsList != null) {
            toolsClassList = new ArrayList<STSInterface>();
            for (String toolsName : toolsList.split(",")) {
                toolsName = toolsName.trim();
                STSToolWorker.LOGGER.log(Level.FINE, "Processing {0} tool", toolsName);
                if (stsConfProps.getProperty(toolsName + ".execution.allow").trim().equalsIgnoreCase(Boolean.TRUE.toString())) {
                    final String className = stsConfProps.getProperty(toolsName + ".class").trim();
                    toolsClassList.add((STSInterface)Class.forName(className).newInstance());
                    STSToolWorker.LOGGER.log(Level.FINE, "{0} class is added", className);
                }
            }
        }
        STSToolWorker.LOGGER.log(Level.INFO, "Tools list : {0}", Arrays.toString(toolsClassList.toArray()));
        return toolsClassList;
    }
    
    private void waitForCompletion() throws InterruptedException {
        STSToolWorker.LOGGER.log(Level.INFO, "Waiting for completion...");
        int activeWorkers = 0;
        int pendingTasks = 0;
        int counter = 0;
        do {
            Thread.sleep(5000L);
            ++counter;
            activeWorkers = ((ThreadPoolExecutor)this.stsWorkers).getActiveCount();
            pendingTasks = ((ThreadPoolExecutor)this.stsWorkers).getQueue().size();
            if (counter % 60 == 0) {
                STSToolWorker.COMMON_LOGGER.log(Level.INFO, "No.of Active workers / Pending Tasks : {0} / {1}", new Object[] { activeWorkers, pendingTasks });
                counter = 0;
            }
            if (this.stsWorkers.isShutdown()) {
                throw new InterruptedException("Worker pool interrupted");
            }
        } while (activeWorkers != 0 || pendingTasks != 0);
        STSToolWorker.LOGGER.log(Level.FINE, "Worker pool execution completed");
    }
    
    public void stopExecution() {
        STSToolWorker.LOGGER.log(Level.FINE, "Stopping worker pool");
        if (this.stsWorkers != null) {
            this.stsWorkers.shutdownNow();
            STSToolWorker.LOGGER.log(Level.INFO, "Stopping worker pool stopped");
        }
    }
    
    static {
        LOGGER = Logger.getLogger("STSStarter");
        COMMON_LOGGER = Logger.getLogger(STSToolWorker.class.getName());
        STSToolWorker.stsToolWorkerObj = null;
        STSToolWorker.toolName = null;
    }
}
