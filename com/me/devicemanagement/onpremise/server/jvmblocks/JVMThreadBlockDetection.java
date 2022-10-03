package com.me.devicemanagement.onpremise.server.jvmblocks;

import com.me.devicemanagement.onpremise.server.util.DebugUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.logging.Logger;

public class JVMThreadBlockDetection extends Thread
{
    private static Logger logger;
    private static final String NORMAL_SLEEP_TIME = "jvm_block_detection_interval";
    private static final String REDUCED_SLEEP_TIME = "jvm_block_detection_reduced_interval";
    private static final String RESTART_INTERVAL = "jvm_restart_time_interval";
    private static final String BLOCK_TIME_THRESHOLD = "jvm_block_detection_threshold";
    public static final String NUMBER_OF_THREADBLOCK = "NoOfThreadBlocksOccured";
    public static final String NUMBER_OF_BLOCKTIME_EXCEEDS = "blockTimeExceeds";
    public static final String LAST_OCCURRED_BLOCK = "LastOccuredBlock";
    public static final String MAX_BLOCK_TIME = "maxBlockTime";
    private static long normalSleepTime;
    private static long reducedSleepTime;
    private static long restartInterval;
    private static long blockThreshold;
    
    public JVMThreadBlockDetection(final Properties properties) {
        setThreadBlockDetectionProperties(properties);
        this.setDaemon(true);
    }
    
    @Override
    public void run() {
        long initialBlockStartTime = -1L;
        while (true) {
            final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            bean.setThreadContentionMonitoringEnabled(true);
            long maxBlockTime = -1L;
            int numberOfBlockedThreads = 0;
            JVMThreadBlockDetection.logger.log(Level.INFO, "    ******************** START OF A SCAN ********************    ");
            for (final ThreadInfo threadInfo : bean.dumpAllThreads(true, true)) {
                if (threadInfo.getThreadState().equals(State.BLOCKED)) {
                    final long blockedtime = threadInfo.getBlockedTime();
                    if (blockedtime > JVMThreadBlockDetection.blockThreshold) {
                        final long blockedDuration = TimeUnit.MILLISECONDS.toMinutes(blockedtime);
                        final StringBuilder dump = new StringBuilder();
                        dump.append("Thread Name : " + threadInfo.getThreadName()).append("\n");
                        dump.append("Thread State : " + threadInfo.getThreadState()).append("\n");
                        dump.append("Thread Id : " + threadInfo.getThreadId()).append("\n");
                        final long cpuTime = bean.getThreadCpuTime(threadInfo.getThreadId());
                        if (blockedtime > maxBlockTime) {
                            maxBlockTime = blockedtime;
                        }
                        dump.append("Blocked time in mins : " + blockedDuration).append("\n");
                        dump.append("CPU time in mins :" + TimeUnit.NANOSECONDS.toMinutes(cpuTime)).append("\n");
                        dump.append("Blocked Due to : Thread Id - " + threadInfo.getLockOwnerId() + " Thread Name - " + threadInfo.getLockOwnerName()).append("\n");
                        ++numberOfBlockedThreads;
                        dump.append("--------------------*****************************--------------------\n");
                        dump.append("Blocked Thread Stack Trace").append("\n");
                        final StackTraceElement[] stackTrace;
                        StackTraceElement[] stacktraceelements = stackTrace = threadInfo.getStackTrace();
                        for (final StackTraceElement ste : stackTrace) {
                            dump.append("\n at ");
                            dump.append(ste);
                        }
                        dump.append("\n\n");
                        dump.append("--------------------*****************************--------------------\n");
                        dump.append("Lock Owner Thread Stack Trace").append("\n");
                        final StackTraceElement[] stackTrace2;
                        stacktraceelements = (stackTrace2 = bean.getThreadInfo(threadInfo.getLockOwnerId(), 100).getStackTrace());
                        for (final StackTraceElement ste : stackTrace2) {
                            dump.append("\n at ");
                            dump.append(ste);
                        }
                        dump.append("--------------------*****************************--------------------\n");
                        JVMThreadBlockDetection.logger.log(Level.INFO, dump.toString());
                    }
                }
            }
            long sleepTime;
            if (numberOfBlockedThreads < 1) {
                JVMThreadBlockDetection.logger.log(Level.INFO, "No Blocked Threads");
                sleepTime = JVMThreadBlockDetection.normalSleepTime;
                initialBlockStartTime = -1L;
            }
            else {
                JVMThreadBlockDetection.logger.log(Level.INFO, "Number of Blocked threads: " + numberOfBlockedThreads);
                JVMThreadBlockDetection.logger.log(Level.INFO, "Full thread dump will be found in JVMBlockTraceLogger");
                this.updateJvmBlockCountInDB(maxBlockTime);
                this.logAllThreadDetails();
                sleepTime = JVMThreadBlockDetection.reducedSleepTime;
                if (initialBlockStartTime > -1L) {
                    final long totalBlockTime = System.currentTimeMillis() - initialBlockStartTime;
                    if (totalBlockTime >= JVMThreadBlockDetection.restartInterval) {
                        this.updateBlockTimeExceedsThreshold();
                    }
                }
            }
            JVMThreadBlockDetection.logger.log(Level.INFO, "    ********************* END OF A SCAN *********************    ");
            try {
                Thread.sleep(sleepTime);
            }
            catch (final InterruptedException e) {
                JVMThreadBlockDetection.logger.log(Level.SEVERE, e.getStackTrace().toString());
            }
        }
    }
    
    private void updateJvmBlockCountInDB(final Long maxBlockedTime) {
        final String numberOfBlockOccurredFromDB = SyMUtil.getServerParameter("NoOfThreadBlocksOccured");
        int numberOfBlockOccurred = 1;
        if (numberOfBlockOccurredFromDB != null) {
            numberOfBlockOccurred = Integer.parseInt(numberOfBlockOccurredFromDB) + 1;
        }
        SyMUtil.updateServerParameter("NoOfThreadBlocksOccured", String.valueOf(numberOfBlockOccurred));
        SyMUtil.updateServerParameter("LastOccuredBlock", Long.toString(SyMUtil.getCurrentTimeInMillis()));
        SyMUtil.updateServerParameter("maxBlockTime", Long.toString(maxBlockedTime));
    }
    
    private void updateBlockTimeExceedsThreshold() {
        final String blockTimeExceedsFromDB = SyMUtil.getServerParameter("blockTimeExceeds");
        int blockTimeExceeds = 1;
        if (blockTimeExceedsFromDB != null) {
            blockTimeExceeds = Integer.parseInt(blockTimeExceedsFromDB) + 1;
        }
        SyMUtil.updateServerParameter("blockTimeExceeds", String.valueOf(blockTimeExceeds));
    }
    
    private void logAllThreadDetails() {
        final Logger logger = Logger.getLogger("JVMBlockTraceLogger");
        try {
            logger.log(Level.INFO, "--------------------------------------------------------------------------------");
            logger.log(Level.WARNING, "Thread Dump taken " + DebugUtil.generateThreaddump());
            logger.log(Level.INFO, "--------------------------------------------------------------------------------");
        }
        catch (final Exception e) {
            logger.log(Level.WARNING, "Exception while taking thread dump", e);
        }
    }
    
    public static void setThreadBlockDetectionProperties(final Properties props) {
        try {
            if (props != null && props.containsKey("jvm_block_detection_interval") && props.containsKey("jvm_block_detection_reduced_interval")) {
                JVMThreadBlockDetection.normalSleepTime = Long.parseLong(props.getProperty("jvm_block_detection_interval"));
                JVMThreadBlockDetection.reducedSleepTime = Long.parseLong(props.getProperty("jvm_block_detection_reduced_interval"));
                JVMThreadBlockDetection.restartInterval = Long.parseLong(props.getProperty("jvm_restart_time_interval"));
                JVMThreadBlockDetection.blockThreshold = Long.parseLong(props.getProperty("jvm_block_detection_threshold"));
            }
            else {
                JVMThreadBlockDetection.normalSleepTime = 10800000L;
                JVMThreadBlockDetection.reducedSleepTime = 900000L;
                JVMThreadBlockDetection.restartInterval = 18000000L;
                JVMThreadBlockDetection.blockThreshold = 3600000L;
            }
        }
        catch (final Exception ex) {
            JVMThreadBlockDetection.logger.log(Level.WARNING, "Caught exception while getting jvm thread lock detection properties ", ex);
        }
    }
    
    static {
        JVMThreadBlockDetection.logger = Logger.getLogger("JVMThreadBlockLogger");
    }
}
