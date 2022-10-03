package com.me.devicemanagement.onpremise.tools.servertroubleshooter.cpumonitor;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSMETracker;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSInterface;

public class CPUUsageMonitor implements STSInterface
{
    private static final Logger LOGGER;
    private static final Logger STSLOGGER;
    private static final String CONF_FILE_LOCATION;
    private static final String CPU_USAGE_EXE_LOCATION;
    private static final String SERVER_HOME;
    private static final String STSTOOL_HOME;
    private static final String CPU_USAGE_MONITOR_JSON_KEY = "CPUUsageMonitor";
    private static int normalCpuLimit;
    private static long timeIntervalForLog;
    private static Boolean logSpecificProcesses;
    private static String processNamesWithLocation;
    private static String[] processList;
    public static Boolean threadDumpFlag;
    private static Boolean processDumpFlag;
    private static final String NORMAL_CPU_LIMIT_KEY = "normal.cpu.limit";
    private static final String TIME_INTERVAL_FOR_LOG = "time.interval.for.log";
    private static final String LOG_SPECIFIC_PROCESSES = "log.specific.processes";
    private static final String PROCESS_NAME_WITH_LOCATION = "process.list";
    private static final String THREAD_DUMP_FLAG = "threaddump.invocation";
    private static final String PROCESS_DUMP_FLAG = "processdump.invocation";
    private static final int SYSTEM = 1;
    
    private int getCPUUsage() {
        OperatingSystemMXBean operatingSystemMXBean = null;
        int cpuUsage = -1;
        final int iterationLimit = 20;
        try {
            operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            for (int loopIterator = 0; loopIterator < iterationLimit; ++loopIterator) {
                Thread.sleep(200L);
                Double doubleCpuUse = operatingSystemMXBean.getSystemCpuLoad();
                doubleCpuUse *= 100.0;
                cpuUsage = doubleCpuUse.intValue();
                if (cpuUsage > 0) {
                    break;
                }
            }
            CPUUsageMonitor.STSLOGGER.log(Level.INFO, "CPU usage of system: {0}", cpuUsage);
        }
        catch (final Exception e) {
            CPUUsageMonitor.LOGGER.log(Level.SEVERE, "Exception caught while calculating CPU usage of system", e);
        }
        return cpuUsage;
    }
    
    private Properties getConfProperties() throws Exception {
        CPUUsageMonitor.LOGGER.log(Level.FINE, "Getting properties from conf file : " + CPUUsageMonitor.CONF_FILE_LOCATION);
        final String confFileLoc = CPUUsageMonitor.STSTOOL_HOME + File.separator + CPUUsageMonitor.CONF_FILE_LOCATION;
        final Properties confProperties = new Properties();
        final File conffile = new File(confFileLoc);
        if (!conffile.exists()) {
            CPUUsageMonitor.LOGGER.log(Level.WARNING, "conf file not found setting default values");
        }
        else {
            confProperties.load(new FileInputStream(confFileLoc));
            CPUUsageMonitor.LOGGER.log(Level.FINE, "Properties : " + confProperties.toString());
        }
        return confProperties;
    }
    
    private void setConfProperties() throws Exception {
        final Properties confProps = this.getConfProperties();
        if (confProps.getProperty("normal.cpu.limit") != null) {
            CPUUsageMonitor.normalCpuLimit = Integer.parseInt(confProps.getProperty("normal.cpu.limit").trim());
        }
        if (confProps.getProperty("time.interval.for.log") != null) {
            CPUUsageMonitor.timeIntervalForLog = Long.parseLong(confProps.getProperty("time.interval.for.log").trim());
            CPUUsageMonitor.timeIntervalForLog *= 1000L;
        }
        if (confProps.getProperty("threaddump.invocation") != null) {
            CPUUsageMonitor.threadDumpFlag = Boolean.parseBoolean(confProps.getProperty("threaddump.invocation").trim());
        }
        if (confProps.getProperty("processdump.invocation") != null) {
            CPUUsageMonitor.processDumpFlag = Boolean.parseBoolean(confProps.getProperty("processdump.invocation").trim());
        }
        if (confProps.getProperty("log.specific.processes") != null) {
            CPUUsageMonitor.logSpecificProcesses = Boolean.parseBoolean(confProps.getProperty("log.specific.processes").trim());
        }
        if (CPUUsageMonitor.logSpecificProcesses) {
            if (confProps.getProperty("process.list") != null) {
                CPUUsageMonitor.processNamesWithLocation = confProps.getProperty("process.list").trim();
                CPUUsageMonitor.processList = CPUUsageMonitor.processNamesWithLocation.split(",");
            }
            else {
                CPUUsageMonitor.logSpecificProcesses = false;
                CPUUsageMonitor.LOGGER.log(Level.WARNING, "process.list property is null so log.specific.processes is set to false");
            }
        }
        CPUUsageMonitor.LOGGER.log(Level.FINE, "conf properties values assigned");
    }
    
    public void increaseCountOfCpuHike() {
        final Date date = new Date();
        final SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        final SimpleDateFormat yearFormat = new SimpleDateFormat("yy", Locale.ENGLISH);
        final String month = monthFormat.format(date);
        final String year = yearFormat.format(date);
        int cpuHikeCount = 1;
        final JSONObject stsMETrackingJsonObject = STSMETracker.getInstance().getExistingMETrackingJSON();
        String cpuHikeJsonKey = null;
        JSONObject cpuUsageMonitorJsonObject = null;
        try {
            if (stsMETrackingJsonObject.has("CPUUsageMonitor")) {
                cpuUsageMonitorJsonObject = stsMETrackingJsonObject.getJSONObject("CPUUsageMonitor");
                CPUUsageMonitor.LOGGER.log(Level.FINE, cpuUsageMonitorJsonObject.toString());
                final Iterator<String> jsonkeysIterator = cpuUsageMonitorJsonObject.keys();
                while (jsonkeysIterator.hasNext()) {
                    cpuHikeJsonKey = jsonkeysIterator.next();
                    if (cpuHikeJsonKey.contains(month)) {
                        if (cpuHikeJsonKey.equals(month + year)) {
                            cpuHikeCount = cpuUsageMonitorJsonObject.getInt(cpuHikeJsonKey);
                            ++cpuHikeCount;
                            break;
                        }
                        cpuHikeCount = 1;
                        cpuUsageMonitorJsonObject.remove(cpuHikeJsonKey);
                        break;
                    }
                }
            }
            else {
                cpuUsageMonitorJsonObject = new JSONObject();
            }
            cpuUsageMonitorJsonObject.put(month + year, cpuHikeCount);
            STSMETracker.getInstance().addToMETracking("CPUUsageMonitor", cpuUsageMonitorJsonObject);
        }
        catch (final JSONException e) {
            CPUUsageMonitor.LOGGER.log(Level.SEVERE, "exception while converting string to json object", (Throwable)e);
        }
    }
    
    private void executeCpuUsageApplication(final ArrayList<String> command) throws IOException {
        if (!CPUUsageMonitor.processDumpFlag) {
            CPUUsageMonitor.LOGGER.log(Level.INFO, "Process dump is disabled");
            return;
        }
        CPUUsageMonitor.LOGGER.log(Level.INFO, "Executing CPU usage exe...");
        CPUUsageMonitor.LOGGER.log(Level.INFO, "Command : " + command);
        ProcessBuilder processBuilder = null;
        Process process = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        String line = null;
        try {
            processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            reader = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(reader);
            CPUUsageMonitor.LOGGER.log(Level.INFO, "CPU Usage exe output : ");
            while ((line = bufferedReader.readLine()) != null) {
                final String[] cpuUsageSplit = line.split("CPU_USAGE:");
                if (cpuUsageSplit.length >= 2 && !cpuUsageSplit[1].equals("0")) {
                    CPUUsageMonitor.LOGGER.log(Level.INFO, line);
                }
            }
            CPUUsageMonitor.LOGGER.log(Level.INFO, "CPU Usage exe execution completed");
        }
        catch (final Exception ex) {
            CPUUsageMonitor.LOGGER.log(Level.WARNING, "Caught exception while executing command : " + command, ex);
        }
        finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    public void logCpuUsageOfProcesses() {
        final ArrayList<String> exeParameters = new ArrayList<String>();
        exeParameters.add(CPUUsageMonitor.STSTOOL_HOME + File.separator + CPUUsageMonitor.CPU_USAGE_EXE_LOCATION);
        try {
            while (true) {
                Thread.sleep(CPUUsageMonitor.timeIntervalForLog);
                final int systemCPUUsage = this.getCPUUsage();
                if (systemCPUUsage > CPUUsageMonitor.normalCpuLimit) {
                    CPUUsageMonitor.LOGGER.log(Level.INFO, "System CPU usage : " + systemCPUUsage);
                    this.increaseCountOfCpuHike();
                    if (!CPUUsageMonitor.logSpecificProcesses) {
                        this.executeCpuUsageApplication(exeParameters);
                    }
                    else {
                        CPUUsageMonitor.LOGGER.log(Level.INFO, "Logging cpu usage of specific processes specified in conf file");
                        for (final String process : CPUUsageMonitor.processList) {
                            if (exeParameters.size() == 3) {
                                exeParameters.remove(2);
                            }
                            exeParameters.add(process.replace("<serverhome>", CPUUsageMonitor.SERVER_HOME));
                            this.executeCpuUsageApplication(exeParameters);
                        }
                    }
                }
                else {
                    if (systemCPUUsage >= 0) {
                        continue;
                    }
                    CPUUsageMonitor.LOGGER.log(Level.INFO, "***** Unable to get CPU value *****");
                }
            }
        }
        catch (final Exception e) {
            CPUUsageMonitor.LOGGER.log(Level.SEVERE, "Caught exception while executing CPU monitor tool", e);
        }
    }
    
    @Override
    public void startTool() {
        CPUUsageMonitor.LOGGER.log(Level.INFO, "CPU usage tool execution started");
        try {
            this.setConfProperties();
            this.logCpuUsageOfProcesses();
        }
        catch (final Exception ex) {
            CPUUsageMonitor.LOGGER.log(Level.WARNING, "Caught Exception in CPUUsage Monitor tool");
        }
        CPUUsageMonitor.LOGGER.log(Level.INFO, "CPU usage tool execution completed");
    }
    
    static {
        LOGGER = Logger.getLogger("CPUUsageLogger");
        STSLOGGER = Logger.getLogger(CPUUsageMonitor.class.getName());
        CONF_FILE_LOCATION = "conf" + File.separator + "CPUUsageTool.conf";
        CPU_USAGE_EXE_LOCATION = "bin" + File.separator + "MEDCCPUMonitor.exe";
        SERVER_HOME = System.getProperty("server.home");
        STSTOOL_HOME = System.getProperty("ststool.home");
        CPUUsageMonitor.normalCpuLimit = 49;
        CPUUsageMonitor.timeIntervalForLog = 5000L;
        CPUUsageMonitor.logSpecificProcesses = false;
        CPUUsageMonitor.processNamesWithLocation = null;
        CPUUsageMonitor.processList = null;
        CPUUsageMonitor.threadDumpFlag = false;
        CPUUsageMonitor.processDumpFlag = false;
    }
}
