package com.me.devicemanagement.onpremise.server.util;

import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;

public class DebugUtil
{
    public static String generateThreaddump() throws IOException {
        final StringBuilder dump = new StringBuilder();
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] threadInfo2;
        final ThreadInfo[] threadinfos = threadInfo2 = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);
        for (final ThreadInfo threadInfo : threadInfo2) {
            dump.append("\n\"" + threadInfo.getThreadName() + "\"");
            dump.append("\t Thread ID :\" " + threadInfo.getThreadId() + "\"");
            dump.append("\n");
            final String state = threadInfo.getThreadState().name();
            dump.append("java.lang.thread.state : ").append(state).append("\n");
            if (threadInfo.getLockInfo() != null) {
                if (state.equalsIgnoreCase("blocked")) {
                    dump.append("Waiting to lock " + threadInfo.getLockName() + " owned by " + threadInfo.getLockOwnerName());
                }
                else if (state.equalsIgnoreCase("waiting")) {
                    dump.append("Waiting on " + threadInfo.getLockName());
                }
                else if (state.equalsIgnoreCase("timed_waiting")) {
                    dump.append("Waiting on " + threadInfo.getLockName() + " Locked " + threadInfo.getLockName());
                }
                else if (state.equalsIgnoreCase("runnable")) {
                    dump.append("Locked " + threadInfo.getLockName());
                }
            }
            else {
                if (state.equalsIgnoreCase("timed_waiting")) {
                    dump.append(" Sleeping ");
                }
                if (threadInfo.getLockName() != null) {
                    dump.append(threadInfo.getLockName());
                }
            }
            final StackTraceElement[] stackTrace;
            final StackTraceElement[] stacktraceelements = stackTrace = threadInfo.getStackTrace();
            for (final StackTraceElement ste : stackTrace) {
                dump.append("\n at ");
                dump.append(ste);
            }
            dump.append("\n\n");
        }
        return dump.toString();
    }
}
