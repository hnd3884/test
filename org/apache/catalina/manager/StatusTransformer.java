package org.apache.catalina.manager;

import javax.management.ObjectInstance;
import java.util.Date;
import java.util.Set;
import javax.management.QueryExp;
import java.util.Enumeration;
import java.util.Vector;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.MemoryUsage;
import java.util.Iterator;
import java.util.SortedMap;
import org.apache.tomcat.util.security.Escape;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.TreeMap;
import java.lang.reflect.Method;
import org.apache.tomcat.util.ExceptionUtils;
import java.text.MessageFormat;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

public class StatusTransformer
{
    public static void setContentType(final HttpServletResponse response, final int mode) {
        if (mode == 0) {
            response.setContentType("text/html;charset=utf-8");
        }
        else if (mode == 1) {
            response.setContentType("text/xml;charset=utf-8");
        }
    }
    
    public static void writeHeader(final PrintWriter writer, final Object[] args, final int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.HTML_HEADER_SECTION, args));
        }
        else if (mode == 1) {
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            writer.print(MessageFormat.format("<?xml-stylesheet type=\"text/xsl\" href=\"{0}/xform.xsl\" ?>\n", args));
            writer.write("<status>");
        }
    }
    
    public static void writeBody(final PrintWriter writer, final Object[] args, final int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.BODY_HEADER_SECTION, args));
        }
    }
    
    public static void writeManager(final PrintWriter writer, final Object[] args, final int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.MANAGER_SECTION, args));
        }
    }
    
    public static void writePageHeading(final PrintWriter writer, final Object[] args, final int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.SERVER_HEADER_SECTION, args));
        }
    }
    
    public static void writeServerInfo(final PrintWriter writer, final Object[] args, final int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.SERVER_ROW_SECTION, args));
        }
    }
    
    public static void writeFooter(final PrintWriter writer, final int mode) {
        if (mode == 0) {
            writer.print(Constants.HTML_TAIL_SECTION);
        }
        else if (mode == 1) {
            writer.write("</status>");
        }
    }
    
    public static void writeOSState(final PrintWriter writer, final int mode, final Object[] args) {
        final long[] result = new long[16];
        boolean ok = false;
        try {
            final String methodName = "info";
            final Class<?>[] paramTypes = { result.getClass() };
            final Object[] paramValues = { result };
            final Method method = Class.forName("org.apache.tomcat.jni.OS").getMethod(methodName, paramTypes);
            method.invoke(null, paramValues);
            ok = true;
        }
        catch (Throwable t) {
            t = ExceptionUtils.unwrapInvocationTargetException(t);
            ExceptionUtils.handleThrowable(t);
        }
        if (ok) {
            if (mode == 0) {
                writer.print("<h1>OS</h1>");
                writer.print("<p>");
                writer.print(args[0]);
                writer.print(' ');
                writer.print(formatSize(result[0], true));
                writer.print(' ');
                writer.print(args[1]);
                writer.print(' ');
                writer.print(formatSize(result[1], true));
                writer.print(' ');
                writer.print(args[2]);
                writer.print(' ');
                writer.print(formatSize(result[2], true));
                writer.print(' ');
                writer.print(args[3]);
                writer.print(' ');
                writer.print(formatSize(result[3], true));
                writer.print(' ');
                writer.print(args[4]);
                writer.print(' ');
                writer.print((Object)result[6]);
                writer.print("<br>");
                writer.print(args[5]);
                writer.print(' ');
                writer.print(formatTime(result[11] / 1000L, true));
                writer.print(' ');
                writer.print(args[6]);
                writer.print(' ');
                writer.print(formatTime(result[12] / 1000L, true));
                writer.print("</p>");
            }
            else if (mode == 1) {}
        }
    }
    
    public static void writeVMState(final PrintWriter writer, final int mode, final Object[] args) throws Exception {
        final SortedMap<String, MemoryPoolMXBean> memoryPoolMBeans = new TreeMap<String, MemoryPoolMXBean>();
        for (final MemoryPoolMXBean mbean : ManagementFactory.getMemoryPoolMXBeans()) {
            final String sortKey = mbean.getType() + ":" + mbean.getName();
            memoryPoolMBeans.put(sortKey, mbean);
        }
        if (mode == 0) {
            writer.print("<h1>JVM</h1>");
            writer.print("<p>");
            writer.print(args[0]);
            writer.print(' ');
            writer.print(formatSize(Runtime.getRuntime().freeMemory(), true));
            writer.print(' ');
            writer.print(args[1]);
            writer.print(' ');
            writer.print(formatSize(Runtime.getRuntime().totalMemory(), true));
            writer.print(' ');
            writer.print(args[2]);
            writer.print(' ');
            writer.print(formatSize(Runtime.getRuntime().maxMemory(), true));
            writer.print("</p>");
            writer.write("<table border=\"0\"><thead><tr><th>" + args[3] + "</th><th>" + args[4] + "</th><th>" + args[5] + "</th><th>" + args[6] + "</th><th>" + args[7] + "</th><th>" + args[8] + "</th></tr></thead><tbody>");
            for (final MemoryPoolMXBean memoryPoolMBean : memoryPoolMBeans.values()) {
                final MemoryUsage usage = memoryPoolMBean.getUsage();
                writer.write("<tr><td>");
                writer.print(memoryPoolMBean.getName());
                writer.write("</td><td>");
                writer.print(memoryPoolMBean.getType());
                writer.write("</td><td>");
                writer.print(formatSize(usage.getInit(), true));
                writer.write("</td><td>");
                writer.print(formatSize(usage.getCommitted(), true));
                writer.write("</td><td>");
                writer.print(formatSize(usage.getMax(), true));
                writer.write("</td><td>");
                writer.print(formatSize(usage.getUsed(), true));
                if (usage.getMax() > 0L) {
                    writer.write(" (" + usage.getUsed() * 100L / usage.getMax() + "%)");
                }
                writer.write("</td></tr>");
            }
            writer.write("</tbody></table>");
        }
        else if (mode == 1) {
            writer.write("<jvm>");
            writer.write("<memory");
            writer.write(" free='" + Runtime.getRuntime().freeMemory() + "'");
            writer.write(" total='" + Runtime.getRuntime().totalMemory() + "'");
            writer.write(" max='" + Runtime.getRuntime().maxMemory() + "'/>");
            for (final MemoryPoolMXBean memoryPoolMBean : memoryPoolMBeans.values()) {
                final MemoryUsage usage = memoryPoolMBean.getUsage();
                writer.write("<memorypool");
                writer.write(" name='" + Escape.xml("", memoryPoolMBean.getName()) + "'");
                writer.write(" type='" + memoryPoolMBean.getType() + "'");
                writer.write(" usageInit='" + usage.getInit() + "'");
                writer.write(" usageCommitted='" + usage.getCommitted() + "'");
                writer.write(" usageMax='" + usage.getMax() + "'");
                writer.write(" usageUsed='" + usage.getUsed() + "'/>");
            }
            writer.write("</jvm>");
        }
    }
    
    public static void writeConnectorState(final PrintWriter writer, final ObjectName tpName, final String name, final MBeanServer mBeanServer, final Vector<ObjectName> globalRequestProcessors, final Vector<ObjectName> requestProcessors, final int mode, final Object[] args) throws Exception {
        if (mode == 0) {
            writer.print("<h1>");
            writer.print(name);
            writer.print("</h1>");
            writer.print("<p>");
            writer.print(args[0]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(tpName, "maxThreads"));
            writer.print(' ');
            writer.print(args[1]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(tpName, "currentThreadCount"));
            writer.print(' ');
            writer.print(args[2]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(tpName, "currentThreadsBusy"));
            writer.print(' ');
            writer.print(args[3]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(tpName, "keepAliveCount"));
            writer.print("<br>");
            ObjectName grpName = null;
            Enumeration<ObjectName> enumeration = globalRequestProcessors.elements();
            while (enumeration.hasMoreElements()) {
                final ObjectName objectName = enumeration.nextElement();
                if (name.equals(objectName.getKeyProperty("name")) && objectName.getKeyProperty("Upgrade") == null) {
                    grpName = objectName;
                }
            }
            if (grpName == null) {
                return;
            }
            writer.print(args[4]);
            writer.print(' ');
            writer.print(formatTime(mBeanServer.getAttribute(grpName, "maxTime"), false));
            writer.print(' ');
            writer.print(args[5]);
            writer.print(' ');
            writer.print(formatTime(mBeanServer.getAttribute(grpName, "processingTime"), true));
            writer.print(' ');
            writer.print(args[6]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(grpName, "requestCount"));
            writer.print(' ');
            writer.print(args[7]);
            writer.print(' ');
            writer.print(mBeanServer.getAttribute(grpName, "errorCount"));
            writer.print(' ');
            writer.print(args[8]);
            writer.print(' ');
            writer.print(formatSize(mBeanServer.getAttribute(grpName, "bytesReceived"), true));
            writer.print(' ');
            writer.print(args[9]);
            writer.print(' ');
            writer.print(formatSize(mBeanServer.getAttribute(grpName, "bytesSent"), true));
            writer.print("</p>");
            writer.print("<table border=\"0\"><tr><th>" + args[10] + "</th><th>" + args[11] + "</th><th>" + args[12] + "</th><th>" + args[13] + "</th><th>" + args[14] + "</th><th>" + args[15] + "</th><th>" + args[16] + "</th><th>" + args[17] + "</th></tr>");
            enumeration = requestProcessors.elements();
            while (enumeration.hasMoreElements()) {
                final ObjectName objectName = enumeration.nextElement();
                if (name.equals(objectName.getKeyProperty("worker"))) {
                    writer.print("<tr>");
                    writeProcessorState(writer, objectName, mBeanServer, mode);
                    writer.print("</tr>");
                }
            }
            writer.print("</table>");
            writer.print("<p>");
            writer.print(args[18]);
            writer.print("</p>");
        }
        else if (mode == 1) {
            writer.write("<connector name='" + name + "'>");
            writer.write("<threadInfo ");
            writer.write(" maxThreads=\"" + mBeanServer.getAttribute(tpName, "maxThreads") + "\"");
            writer.write(" currentThreadCount=\"" + mBeanServer.getAttribute(tpName, "currentThreadCount") + "\"");
            writer.write(" currentThreadsBusy=\"" + mBeanServer.getAttribute(tpName, "currentThreadsBusy") + "\"");
            writer.write(" />");
            ObjectName grpName = null;
            Enumeration<ObjectName> enumeration = globalRequestProcessors.elements();
            while (enumeration.hasMoreElements()) {
                final ObjectName objectName = enumeration.nextElement();
                if (name.equals(objectName.getKeyProperty("name")) && objectName.getKeyProperty("Upgrade") == null) {
                    grpName = objectName;
                }
            }
            if (grpName != null) {
                writer.write("<requestInfo ");
                writer.write(" maxTime=\"" + mBeanServer.getAttribute(grpName, "maxTime") + "\"");
                writer.write(" processingTime=\"" + mBeanServer.getAttribute(grpName, "processingTime") + "\"");
                writer.write(" requestCount=\"" + mBeanServer.getAttribute(grpName, "requestCount") + "\"");
                writer.write(" errorCount=\"" + mBeanServer.getAttribute(grpName, "errorCount") + "\"");
                writer.write(" bytesReceived=\"" + mBeanServer.getAttribute(grpName, "bytesReceived") + "\"");
                writer.write(" bytesSent=\"" + mBeanServer.getAttribute(grpName, "bytesSent") + "\"");
                writer.write(" />");
                writer.write("<workers>");
                enumeration = requestProcessors.elements();
                while (enumeration.hasMoreElements()) {
                    final ObjectName objectName = enumeration.nextElement();
                    if (name.equals(objectName.getKeyProperty("worker"))) {
                        writeProcessorState(writer, objectName, mBeanServer, mode);
                    }
                }
                writer.write("</workers>");
            }
            writer.write("</connector>");
        }
    }
    
    protected static void writeProcessorState(final PrintWriter writer, final ObjectName pName, final MBeanServer mBeanServer, final int mode) throws Exception {
        final Integer stageValue = (Integer)mBeanServer.getAttribute(pName, "stage");
        final int stage = stageValue;
        boolean fullStatus = true;
        boolean showRequest = true;
        String stageStr = null;
        switch (stage) {
            case 1: {
                stageStr = "P";
                fullStatus = false;
                break;
            }
            case 2: {
                stageStr = "P";
                fullStatus = false;
                break;
            }
            case 3: {
                stageStr = "S";
                break;
            }
            case 4: {
                stageStr = "F";
                break;
            }
            case 5: {
                stageStr = "F";
                break;
            }
            case 7: {
                stageStr = "R";
                fullStatus = false;
                break;
            }
            case 6: {
                stageStr = "K";
                fullStatus = true;
                showRequest = false;
                break;
            }
            case 0: {
                stageStr = "R";
                fullStatus = false;
                break;
            }
            default: {
                stageStr = "?";
                fullStatus = false;
                break;
            }
        }
        if (mode == 0) {
            writer.write("<td><strong>");
            writer.write(stageStr);
            writer.write("</strong></td>");
            if (fullStatus) {
                writer.write("<td>");
                writer.print(formatTime(mBeanServer.getAttribute(pName, "requestProcessingTime"), false));
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(formatSize(mBeanServer.getAttribute(pName, "requestBytesSent"), false));
                }
                else {
                    writer.write("?");
                }
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(formatSize(mBeanServer.getAttribute(pName, "requestBytesReceived"), false));
                }
                else {
                    writer.write("?");
                }
                writer.write("</td>");
                writer.write("<td>");
                writer.print(Escape.htmlElementContext(mBeanServer.getAttribute(pName, "remoteAddrForwarded")));
                writer.write("</td>");
                writer.write("<td>");
                writer.print(Escape.htmlElementContext(mBeanServer.getAttribute(pName, "remoteAddr")));
                writer.write("</td>");
                writer.write("<td nowrap>");
                writer.write(Escape.htmlElementContext(mBeanServer.getAttribute(pName, "virtualHost")));
                writer.write("</td>");
                writer.write("<td nowrap class=\"row-left\">");
                if (showRequest) {
                    writer.write(Escape.htmlElementContext(mBeanServer.getAttribute(pName, "method")));
                    writer.write(32);
                    writer.write(Escape.htmlElementContext(mBeanServer.getAttribute(pName, "currentUri")));
                    final String queryString = (String)mBeanServer.getAttribute(pName, "currentQueryString");
                    if (queryString != null && !queryString.equals("")) {
                        writer.write("?");
                        writer.print(Escape.htmlElementContent(queryString));
                    }
                    writer.write(32);
                    writer.write(Escape.htmlElementContext(mBeanServer.getAttribute(pName, "protocol")));
                }
                else {
                    writer.write("?");
                }
                writer.write("</td>");
            }
            else {
                writer.write("<td>?</td><td>?</td><td>?</td><td>?</td><td>?</td><td>?</td>");
            }
        }
        else if (mode == 1) {
            writer.write("<worker ");
            writer.write(" stage=\"" + stageStr + "\"");
            if (fullStatus) {
                writer.write(" requestProcessingTime=\"" + mBeanServer.getAttribute(pName, "requestProcessingTime") + "\"");
                writer.write(" requestBytesSent=\"");
                if (showRequest) {
                    writer.write("" + mBeanServer.getAttribute(pName, "requestBytesSent"));
                }
                else {
                    writer.write("0");
                }
                writer.write("\"");
                writer.write(" requestBytesReceived=\"");
                if (showRequest) {
                    writer.write("" + mBeanServer.getAttribute(pName, "requestBytesReceived"));
                }
                else {
                    writer.write("0");
                }
                writer.write("\"");
                writer.write(" remoteAddr=\"" + Escape.htmlElementContext(mBeanServer.getAttribute(pName, "remoteAddr")) + "\"");
                writer.write(" virtualHost=\"" + Escape.htmlElementContext(mBeanServer.getAttribute(pName, "virtualHost")) + "\"");
                if (showRequest) {
                    writer.write(" method=\"" + Escape.htmlElementContext(mBeanServer.getAttribute(pName, "method")) + "\"");
                    writer.write(" currentUri=\"" + Escape.htmlElementContext(mBeanServer.getAttribute(pName, "currentUri")) + "\"");
                    final String queryString = (String)mBeanServer.getAttribute(pName, "currentQueryString");
                    if (queryString != null && !queryString.equals("")) {
                        writer.write(" currentQueryString=\"" + Escape.htmlElementContent(queryString) + "\"");
                    }
                    else {
                        writer.write(" currentQueryString=\"&#63;\"");
                    }
                    writer.write(" protocol=\"" + Escape.htmlElementContext(mBeanServer.getAttribute(pName, "protocol")) + "\"");
                }
                else {
                    writer.write(" method=\"&#63;\"");
                    writer.write(" currentUri=\"&#63;\"");
                    writer.write(" currentQueryString=\"&#63;\"");
                    writer.write(" protocol=\"&#63;\"");
                }
            }
            else {
                writer.write(" requestProcessingTime=\"0\"");
                writer.write(" requestBytesSent=\"0\"");
                writer.write(" requestBytesReceived=\"0\"");
                writer.write(" remoteAddr=\"&#63;\"");
                writer.write(" virtualHost=\"&#63;\"");
                writer.write(" method=\"&#63;\"");
                writer.write(" currentUri=\"&#63;\"");
                writer.write(" currentQueryString=\"&#63;\"");
                writer.write(" protocol=\"&#63;\"");
            }
            writer.write(" />");
        }
    }
    
    public static void writeDetailedState(final PrintWriter writer, final MBeanServer mBeanServer, final int mode) throws Exception {
        if (mode == 0) {
            final ObjectName queryHosts = new ObjectName("*:j2eeType=WebModule,*");
            final Set<ObjectName> hostsON = mBeanServer.queryNames(queryHosts, null);
            writer.print("<h1>");
            writer.print("Application list");
            writer.print("</h1>");
            writer.print("<p>");
            int count = 0;
            Iterator<ObjectName> iterator = hostsON.iterator();
            while (iterator.hasNext()) {
                final ObjectName contextON = iterator.next();
                String webModuleName = contextON.getKeyProperty("name");
                if (webModuleName.startsWith("//")) {
                    webModuleName = webModuleName.substring(2);
                }
                final int slash = webModuleName.indexOf(47);
                if (slash == -1) {
                    ++count;
                }
                else {
                    writer.print("<a href=\"#" + count++ + ".0\">");
                    writer.print(Escape.htmlElementContext((Object)webModuleName));
                    writer.print("</a>");
                    if (!iterator.hasNext()) {
                        continue;
                    }
                    writer.print("<br>");
                }
            }
            writer.print("</p>");
            count = 0;
            iterator = hostsON.iterator();
            while (iterator.hasNext()) {
                final ObjectName contextON = iterator.next();
                writer.print("<a class=\"A.name\" name=\"" + count++ + ".0\">");
                writeContext(writer, contextON, mBeanServer, mode);
            }
        }
        else if (mode == 1) {}
    }
    
    protected static void writeContext(final PrintWriter writer, final ObjectName objectName, final MBeanServer mBeanServer, final int mode) throws Exception {
        if (mode == 0) {
            String name;
            final String webModuleName = name = objectName.getKeyProperty("name");
            if (name == null) {
                return;
            }
            String hostName = null;
            String contextName = null;
            if (name.startsWith("//")) {
                name = name.substring(2);
            }
            final int slash = name.indexOf(47);
            if (slash == -1) {
                return;
            }
            hostName = name.substring(0, slash);
            contextName = name.substring(slash);
            final ObjectName queryManager = new ObjectName(objectName.getDomain() + ":type=Manager,context=" + contextName + ",host=" + hostName + ",*");
            final Set<ObjectName> managersON = mBeanServer.queryNames(queryManager, null);
            ObjectName managerON = null;
            final Iterator i$ = managersON.iterator();
            while (i$.hasNext()) {
                final ObjectName aManagersON = managerON = i$.next();
            }
            final ObjectName queryJspMonitor = new ObjectName(objectName.getDomain() + ":type=JspMonitor,WebModule=" + webModuleName + ",*");
            final Set<ObjectName> jspMonitorONs = mBeanServer.queryNames(queryJspMonitor, null);
            if (contextName.equals("/")) {
                contextName = "";
            }
            writer.print("<h1>");
            writer.print(Escape.htmlElementContext((Object)name));
            writer.print("</h1>");
            writer.print("</a>");
            writer.print("<p>");
            final Object startTime = mBeanServer.getAttribute(objectName, "startTime");
            writer.print(" Start time: " + new Date((long)startTime));
            writer.print(" Startup time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "startupTime"), false));
            writer.print(" TLD scan time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "tldScanTime"), false));
            if (managerON != null) {
                writeManager(writer, managerON, mBeanServer, mode);
            }
            if (jspMonitorONs != null) {
                writeJspMonitor(writer, jspMonitorONs, mBeanServer, mode);
            }
            writer.print("</p>");
            final String onStr = objectName.getDomain() + ":j2eeType=Servlet,WebModule=" + webModuleName + ",*";
            final ObjectName servletObjectName = new ObjectName(onStr);
            final Set<ObjectInstance> set = mBeanServer.queryMBeans(servletObjectName, null);
            for (final ObjectInstance oi : set) {
                writeWrapper(writer, oi.getObjectName(), mBeanServer, mode);
            }
        }
        else if (mode == 1) {}
    }
    
    public static void writeManager(final PrintWriter writer, final ObjectName objectName, final MBeanServer mBeanServer, final int mode) throws Exception {
        if (mode == 0) {
            writer.print("<br>");
            writer.print(" Active sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "activeSessions"));
            writer.print(" Session count: ");
            writer.print(mBeanServer.getAttribute(objectName, "sessionCounter"));
            writer.print(" Max active sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "maxActive"));
            writer.print(" Rejected session creations: ");
            writer.print(mBeanServer.getAttribute(objectName, "rejectedSessions"));
            writer.print(" Expired sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "expiredSessions"));
            writer.print(" Longest session alive time: ");
            writer.print(formatSeconds(mBeanServer.getAttribute(objectName, "sessionMaxAliveTime")));
            writer.print(" Average session alive time: ");
            writer.print(formatSeconds(mBeanServer.getAttribute(objectName, "sessionAverageAliveTime")));
            writer.print(" Processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "processingTime"), false));
        }
        else if (mode == 1) {}
    }
    
    public static void writeJspMonitor(final PrintWriter writer, final Set<ObjectName> jspMonitorONs, final MBeanServer mBeanServer, final int mode) throws Exception {
        int jspCount = 0;
        int jspReloadCount = 0;
        for (final ObjectName jspMonitorON : jspMonitorONs) {
            Object obj = mBeanServer.getAttribute(jspMonitorON, "jspCount");
            jspCount += (int)obj;
            obj = mBeanServer.getAttribute(jspMonitorON, "jspReloadCount");
            jspReloadCount += (int)obj;
        }
        if (mode == 0) {
            writer.print("<br>");
            writer.print(" JSPs loaded: ");
            writer.print(jspCount);
            writer.print(" JSPs reloaded: ");
            writer.print(jspReloadCount);
        }
        else if (mode == 1) {}
    }
    
    public static void writeWrapper(final PrintWriter writer, final ObjectName objectName, final MBeanServer mBeanServer, final int mode) throws Exception {
        if (mode == 0) {
            final String servletName = objectName.getKeyProperty("name");
            final String[] mappings = (String[])mBeanServer.invoke(objectName, "findMappings", null, null);
            writer.print("<h2>");
            writer.print(Escape.htmlElementContext((Object)servletName));
            if (mappings != null && mappings.length > 0) {
                writer.print(" [ ");
                for (int i = 0; i < mappings.length; ++i) {
                    writer.print(Escape.htmlElementContext((Object)mappings[i]));
                    if (i < mappings.length - 1) {
                        writer.print(" , ");
                    }
                }
                writer.print(" ] ");
            }
            writer.print("</h2>");
            writer.print("<p>");
            writer.print(" Processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "processingTime"), true));
            writer.print(" Max time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "maxTime"), false));
            writer.print(" Request count: ");
            writer.print(mBeanServer.getAttribute(objectName, "requestCount"));
            writer.print(" Error count: ");
            writer.print(mBeanServer.getAttribute(objectName, "errorCount"));
            writer.print(" Load time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "loadTime"), false));
            writer.print(" Classloading time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "classLoadTime"), false));
            writer.print("</p>");
        }
        else if (mode == 1) {}
    }
    
    @Deprecated
    public static String filter(final Object obj) {
        if (obj == null) {
            return "?";
        }
        final String message = obj.toString();
        final char[] content = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        final StringBuilder result = new StringBuilder(content.length + 50);
        for (int i = 0; i < content.length; ++i) {
            switch (content[i]) {
                case '<': {
                    result.append("&lt;");
                    break;
                }
                case '>': {
                    result.append("&gt;");
                    break;
                }
                case '&': {
                    result.append("&amp;");
                    break;
                }
                case '\"': {
                    result.append("&quot;");
                    break;
                }
                default: {
                    result.append(content[i]);
                    break;
                }
            }
        }
        return result.toString();
    }
    
    @Deprecated
    public static String filterXml(final String s) {
        if (s == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
            }
            else if (c == '>') {
                sb.append("&gt;");
            }
            else if (c == '\'') {
                sb.append("&apos;");
            }
            else if (c == '&') {
                sb.append("&amp;");
            }
            else if (c == '\"') {
                sb.append("&quot;");
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    public static String formatSize(final Object obj, final boolean mb) {
        long bytes = -1L;
        if (obj instanceof Long) {
            bytes = (long)obj;
        }
        else if (obj instanceof Integer) {
            bytes = (int)obj;
        }
        if (mb) {
            final StringBuilder buff = new StringBuilder();
            if (bytes < 0L) {
                buff.append('-');
                bytes = -bytes;
            }
            final long mbytes = bytes / 1048576L;
            final long rest = (bytes - mbytes * 1048576L) * 100L / 1048576L;
            buff.append(mbytes).append('.');
            if (rest < 10L) {
                buff.append('0');
            }
            buff.append(rest).append(" MB");
            return buff.toString();
        }
        return bytes / 1024L + " KB";
    }
    
    public static String formatTime(final Object obj, final boolean seconds) {
        long time = -1L;
        if (obj instanceof Long) {
            time = (long)obj;
        }
        else if (obj instanceof Integer) {
            time = (int)obj;
        }
        if (seconds) {
            return time / 1000.0f + " s";
        }
        return time + " ms";
    }
    
    public static String formatSeconds(final Object obj) {
        long time = -1L;
        if (obj instanceof Long) {
            time = (long)obj;
        }
        else if (obj instanceof Integer) {
            time = (int)obj;
        }
        return time + " s";
    }
}
