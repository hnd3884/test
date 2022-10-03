package com.me.devicemanagement.onpremise.server.troubleshooter;

import java.io.BufferedWriter;
import java.io.File;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class ThreadDump extends HttpServlet
{
    public String[] processName;
    public String processId;
    public static final String LOG_FOLDER_NAME = "STS_Tool_logs";
    public long timeIntervalForDump;
    private static final String LINE_SEPARATOR;
    private static final Logger LOGGER;
    
    public ThreadDump() {
        this.processName = ManagementFactory.getRuntimeMXBean().getName().split("@");
        this.processId = this.processName[0];
        this.timeIntervalForDump = 1000L;
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final Logger LOGGER = Logger.getLogger("Sample");
        try {
            this.logThreadDump();
        }
        catch (final Exception e) {
            LOGGER.log(Level.INFO, "exception caught while logging threaddump", e);
        }
        this.processRequest(request, response);
    }
    
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (final PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SampleLog</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>threaddump log created </h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private void logThreadDump() throws Exception {
        ThreadDump.LOGGER.log(Level.INFO, "Printing ThreadDump...");
        ThreadInfo[] initialThreadInfo = null;
        ThreadInfo[] finalThreadInfo = null;
        ArrayList<Long> initialThreadCpuTime = null;
        ArrayList<Long> finalThreadCpuTime = null;
        ArrayList<Long> diffThreadCpuTime = null;
        final File uploadDir = null;
        final BufferedWriter writer = null;
        final StringBuilder threadDetails = new StringBuilder();
        initialThreadCpuTime = new ArrayList<Long>();
        finalThreadCpuTime = new ArrayList<Long>();
        diffThreadCpuTime = new ArrayList<Long>();
        ThreadDump.LOGGER.log(Level.INFO, "process id:{0}", new Object[] { this.processId });
        final ThreadInfo[] dumpAllThreads;
        initialThreadInfo = (dumpAllThreads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true));
        for (final ThreadInfo tInfo : dumpAllThreads) {
            initialThreadCpuTime.add(ManagementFactory.getThreadMXBean().getThreadCpuTime(tInfo.getThreadId()) / 1000000L);
        }
        Thread.sleep(this.timeIntervalForDump);
        final ThreadInfo[] dumpAllThreads2;
        finalThreadInfo = (dumpAllThreads2 = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true));
        for (final ThreadInfo tInfo : dumpAllThreads2) {
            finalThreadCpuTime.add(ManagementFactory.getThreadMXBean().getThreadCpuTime(tInfo.getThreadId()) / 1000000L);
        }
        final int finalThreadInfoLength = finalThreadInfo.length;
        int finalThreadIndex = 0;
        int initialThreadIndex = 0;
        while (finalThreadIndex < finalThreadInfoLength) {
            if (finalThreadInfo[finalThreadIndex].getThreadId() == initialThreadInfo[initialThreadIndex].getThreadId()) {
                diffThreadCpuTime.add(finalThreadCpuTime.get(finalThreadIndex) - initialThreadCpuTime.get(initialThreadIndex));
                ++initialThreadIndex;
            }
            else if (finalThreadInfo[finalThreadIndex].getThreadId() < initialThreadInfo[initialThreadIndex].getThreadId()) {
                ++initialThreadIndex;
                --finalThreadIndex;
            }
            else {
                diffThreadCpuTime.add(finalThreadCpuTime.get(finalThreadIndex));
            }
            ++finalThreadIndex;
        }
        final int noOfProcessor = Runtime.getRuntime().availableProcessors();
        for (finalThreadIndex = 0; finalThreadIndex < finalThreadInfoLength; ++finalThreadIndex) {
            final float percentage = diffThreadCpuTime.get(finalThreadIndex) * 100L / (noOfProcessor * this.timeIntervalForDump * 1.0f);
            threadDetails.append(ThreadDump.LINE_SEPARATOR + "[" + finalThreadInfo[finalThreadIndex].getThreadId() + "]" + "[" + finalThreadInfo[finalThreadIndex].getThreadName() + "]" + "[" + finalThreadCpuTime.get(finalThreadIndex) + "]" + "[" + percentage + "%]");
            final StackTraceElement[] stackTrace;
            final StackTraceElement[] stelmt = stackTrace = finalThreadInfo[finalThreadIndex].getStackTrace();
            for (final StackTraceElement st : stackTrace) {
                threadDetails.append(ThreadDump.LINE_SEPARATOR + st.toString());
            }
            ThreadDump.LOGGER.log(Level.INFO, threadDetails.toString() + ThreadDump.LINE_SEPARATOR);
        }
        ThreadDump.LOGGER.log(Level.INFO, "ThreadDump print completed.");
    }
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
        LOGGER = Logger.getLogger("STSLogger");
    }
}
