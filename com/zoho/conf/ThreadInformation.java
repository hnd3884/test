package com.zoho.conf;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

public class ThreadInformation
{
    private static final Logger LOGGER;
    private static final String NEW_LINE;
    private static final String THREAD_STATE = "  Java.lang.Thread.State: ";
    private static final String ID = "\"  Id=";
    private static final String ON = " on ";
    private static final String WAITING = " waiting to lock ";
    private static final String LOCKED = " - locked ";
    private static final int NO_OF_LINES_PER_STACK_TRACE = 6;
    private static final int NO_OF_CHARS_PER_LINE = 75;
    
    public static void takeThreadDump() {
        final StringBuilder stringBuilder = getThreadDump();
        ThreadInformation.LOGGER.info("Thread dump " + stringBuilder.toString());
    }
    
    public static StringBuilder getThreadDump() {
        final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        final long[] threadIds = threadBean.getAllThreadIds();
        final ThreadInfo[] threadInfoArray = threadBean.getThreadInfo(threadIds, true, true);
        final int stringBuilderSize = threadIds.length * 6 * 75;
        final StringBuilder stringBuilder = new StringBuilder(stringBuilderSize);
        for (final ThreadInfo threadInfo : threadInfoArray) {
            if (threadInfo != null) {
                final MonitorInfo[] monitorArray = threadInfo.getLockedMonitors();
                final long threadId = threadInfo.getThreadId();
                final long lockOwnerId = threadInfo.getLockOwnerId();
                final String threadName = threadInfo.getThreadName();
                final Thread.State threadState = threadInfo.getThreadState();
                final String lockName = threadInfo.getLockName();
                final String lockOwnerName = threadInfo.getLockOwnerName();
                final LockInfo lockInfo = threadInfo.getLockInfo();
                final StackTraceElement[] stackTrace = threadInfo.getStackTrace();
                stringBuilder.append(ThreadInformation.NEW_LINE);
                stringBuilder.append('\"');
                stringBuilder.append(threadName);
                stringBuilder.append("\"  Id=");
                stringBuilder.append(threadId);
                stringBuilder.append("  Java.lang.Thread.State: ");
                stringBuilder.append(threadState);
                if (lockInfo != null) {
                    if (threadState == Thread.State.WAITING || threadState == Thread.State.TIMED_WAITING) {
                        stringBuilder.append(" on ");
                        stringBuilder.append(lockInfo);
                    }
                    if (threadState == Thread.State.BLOCKED) {
                        stringBuilder.append(" waiting to lock ");
                        stringBuilder.append(lockInfo);
                    }
                }
                stringBuilder.append(ThreadInformation.NEW_LINE);
                for (int i = 0; i < stackTrace.length; ++i) {
                    stringBuilder.append(" " + stackTrace[i] + ThreadInformation.NEW_LINE);
                    for (int j = 0; j < monitorArray.length; ++j) {
                        if (monitorArray[j].getLockedStackDepth() == i) {
                            stringBuilder.append(" - locked ");
                            stringBuilder.append(monitorArray[j].toString());
                            stringBuilder.append(ThreadInformation.NEW_LINE);
                        }
                    }
                }
                stringBuilder.append(ThreadInformation.NEW_LINE);
                if (lockOwnerName != null && lockOwnerId != -1L) {
                    stringBuilder.append(" LockName: ").append(lockName);
                    stringBuilder.append(" Owner Id: ").append(lockOwnerId);
                    stringBuilder.append(" Owner Name: ").append(lockOwnerName);
                }
            }
        }
        return stringBuilder;
    }
    
    static {
        LOGGER = Logger.getLogger(ThreadInformation.class.getName());
        NEW_LINE = Configuration.getString("line.separator");
    }
}
