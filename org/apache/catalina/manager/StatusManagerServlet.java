package org.apache.catalina.manager;

import javax.management.MBeanServerNotification;
import javax.management.Notification;
import java.io.IOException;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.catalina.util.ServerInfo;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Iterator;
import java.util.Set;
import javax.management.NotificationFilter;
import javax.management.ObjectInstance;
import javax.management.QueryExp;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import javax.management.ObjectName;
import java.util.Vector;
import javax.management.MBeanServer;
import javax.management.NotificationListener;
import javax.servlet.http.HttpServlet;

public class StatusManagerServlet extends HttpServlet implements NotificationListener
{
    private static final long serialVersionUID = 1L;
    protected MBeanServer mBeanServer;
    protected final Vector<ObjectName> protocolHandlers;
    protected final Vector<ObjectName> threadPools;
    protected final Vector<ObjectName> requestProcessors;
    protected final Vector<ObjectName> globalRequestProcessors;
    protected static final StringManager sm;
    
    public StatusManagerServlet() {
        this.mBeanServer = null;
        this.protocolHandlers = new Vector<ObjectName>();
        this.threadPools = new Vector<ObjectName>();
        this.requestProcessors = new Vector<ObjectName>();
        this.globalRequestProcessors = new Vector<ObjectName>();
    }
    
    public void init() throws ServletException {
        this.mBeanServer = Registry.getRegistry((Object)null, (Object)null).getMBeanServer();
        try {
            String onStr = "*:type=ProtocolHandler,*";
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = this.mBeanServer.queryMBeans(objectName, null);
            for (final ObjectInstance oi : set) {
                this.protocolHandlers.addElement(oi.getObjectName());
            }
            onStr = "*:type=ThreadPool,*";
            objectName = new ObjectName(onStr);
            set = this.mBeanServer.queryMBeans(objectName, null);
            for (final ObjectInstance oi : set) {
                this.threadPools.addElement(oi.getObjectName());
            }
            onStr = "*:type=GlobalRequestProcessor,*";
            objectName = new ObjectName(onStr);
            set = this.mBeanServer.queryMBeans(objectName, null);
            for (final ObjectInstance oi : set) {
                this.globalRequestProcessors.addElement(oi.getObjectName());
            }
            onStr = "*:type=RequestProcessor,*";
            objectName = new ObjectName(onStr);
            set = this.mBeanServer.queryMBeans(objectName, null);
            for (final ObjectInstance oi : set) {
                this.requestProcessors.addElement(oi.getObjectName());
            }
            onStr = "JMImplementation:type=MBeanServerDelegate";
            objectName = new ObjectName(onStr);
            this.mBeanServer.addNotificationListener(objectName, this, null, null);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void destroy() {
        final String onStr = "JMImplementation:type=MBeanServerDelegate";
        try {
            final ObjectName objectName = new ObjectName(onStr);
            this.mBeanServer.removeNotificationListener(objectName, this, null, null);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final StringManager smClient = StringManager.getManager("org.apache.catalina.manager", request.getLocales());
        int mode = 0;
        if (request.getParameter("XML") != null && request.getParameter("XML").equals("true")) {
            mode = 1;
        }
        StatusTransformer.setContentType(response, mode);
        final PrintWriter writer = response.getWriter();
        boolean completeStatus = false;
        if (request.getPathInfo() != null && request.getPathInfo().equals("/all")) {
            completeStatus = true;
        }
        Object[] args = { request.getContextPath() };
        StatusTransformer.writeHeader(writer, args, mode);
        args = new Object[] { request.getContextPath(), null };
        if (completeStatus) {
            args[1] = smClient.getString("statusServlet.complete");
        }
        else {
            args[1] = smClient.getString("statusServlet.title");
        }
        StatusTransformer.writeBody(writer, args, mode);
        args = new Object[] { smClient.getString("htmlManagerServlet.manager"), response.encodeURL(request.getContextPath() + "/html/list"), smClient.getString("htmlManagerServlet.list"), request.getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpHtmlManagerFile"), smClient.getString("htmlManagerServlet.helpHtmlManager"), request.getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpManagerFile"), smClient.getString("htmlManagerServlet.helpManager"), null, null };
        if (completeStatus) {
            args[7] = response.encodeURL(request.getContextPath() + "/status");
            args[8] = smClient.getString("statusServlet.title");
        }
        else {
            args[7] = response.encodeURL(request.getContextPath() + "/status/all");
            args[8] = smClient.getString("statusServlet.complete");
        }
        StatusTransformer.writeManager(writer, args, mode);
        args = new Object[] { smClient.getString("htmlManagerServlet.serverTitle"), smClient.getString("htmlManagerServlet.serverVersion"), smClient.getString("htmlManagerServlet.serverJVMVersion"), smClient.getString("htmlManagerServlet.serverJVMVendor"), smClient.getString("htmlManagerServlet.serverOSName"), smClient.getString("htmlManagerServlet.serverOSVersion"), smClient.getString("htmlManagerServlet.serverOSArch"), smClient.getString("htmlManagerServlet.serverHostname"), smClient.getString("htmlManagerServlet.serverIPAddress") };
        StatusTransformer.writePageHeading(writer, args, mode);
        args = new Object[] { ServerInfo.getServerInfo(), System.getProperty("java.runtime.version"), System.getProperty("java.vm.vendor"), System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"), null, null };
        try {
            final InetAddress address = InetAddress.getLocalHost();
            args[6] = address.getHostName();
            args[7] = address.getHostAddress();
        }
        catch (final UnknownHostException e) {
            args[7] = (args[6] = "-");
        }
        StatusTransformer.writeServerInfo(writer, args, mode);
        try {
            args = new Object[] { smClient.getString("htmlManagerServlet.osPhysicalMemory"), smClient.getString("htmlManagerServlet.osAvailableMemory"), smClient.getString("htmlManagerServlet.osTotalPageFile"), smClient.getString("htmlManagerServlet.osFreePageFile"), smClient.getString("htmlManagerServlet.osMemoryLoad"), smClient.getString("htmlManagerServlet.osKernelTime"), smClient.getString("htmlManagerServlet.osUserTime") };
            StatusTransformer.writeOSState(writer, mode, args);
            args = new Object[] { smClient.getString("htmlManagerServlet.jvmFreeMemory"), smClient.getString("htmlManagerServlet.jvmTotalMemory"), smClient.getString("htmlManagerServlet.jvmMaxMemory"), smClient.getString("htmlManagerServlet.jvmTableTitleMemoryPool"), smClient.getString("htmlManagerServlet.jvmTableTitleType"), smClient.getString("htmlManagerServlet.jvmTableTitleInitial"), smClient.getString("htmlManagerServlet.jvmTableTitleTotal"), smClient.getString("htmlManagerServlet.jvmTableTitleMaximum"), smClient.getString("htmlManagerServlet.jvmTableTitleUsed") };
            StatusTransformer.writeVMState(writer, mode, args);
            final Enumeration<ObjectName> enumeration = this.threadPools.elements();
            while (enumeration.hasMoreElements()) {
                final ObjectName objectName = enumeration.nextElement();
                final String name = objectName.getKeyProperty("name");
                args = new Object[] { smClient.getString("htmlManagerServlet.connectorStateMaxThreads"), smClient.getString("htmlManagerServlet.connectorStateThreadCount"), smClient.getString("htmlManagerServlet.connectorStateThreadBusy"), smClient.getString("htmlManagerServlet.connectorStateAliveSocketCount"), smClient.getString("htmlManagerServlet.connectorStateMaxProcessingTime"), smClient.getString("htmlManagerServlet.connectorStateProcessingTime"), smClient.getString("htmlManagerServlet.connectorStateRequestCount"), smClient.getString("htmlManagerServlet.connectorStateErrorCount"), smClient.getString("htmlManagerServlet.connectorStateBytesReceived"), smClient.getString("htmlManagerServlet.connectorStateBytesSent"), smClient.getString("htmlManagerServlet.connectorStateTableTitleStage"), smClient.getString("htmlManagerServlet.connectorStateTableTitleTime"), smClient.getString("htmlManagerServlet.connectorStateTableTitleBSent"), smClient.getString("htmlManagerServlet.connectorStateTableTitleBRecv"), smClient.getString("htmlManagerServlet.connectorStateTableTitleClientForw"), smClient.getString("htmlManagerServlet.connectorStateTableTitleClientAct"), smClient.getString("htmlManagerServlet.connectorStateTableTitleVHost"), smClient.getString("htmlManagerServlet.connectorStateTableTitleRequest"), smClient.getString("htmlManagerServlet.connectorStateHint") };
                StatusTransformer.writeConnectorState(writer, objectName, name, this.mBeanServer, this.globalRequestProcessors, this.requestProcessors, mode, args);
            }
            if (request.getPathInfo() != null && request.getPathInfo().equals("/all")) {
                StatusTransformer.writeDetailedState(writer, this.mBeanServer, mode);
            }
        }
        catch (final Exception e2) {
            throw new ServletException((Throwable)e2);
        }
        StatusTransformer.writeFooter(writer, mode);
    }
    
    public void handleNotification(final Notification notification, final Object handback) {
        if (notification instanceof MBeanServerNotification) {
            final ObjectName objectName = ((MBeanServerNotification)notification).getMBeanName();
            if (notification.getType().equals("JMX.mbean.registered")) {
                final String type = objectName.getKeyProperty("type");
                if (type != null) {
                    if (type.equals("ProtocolHandler")) {
                        this.protocolHandlers.addElement(objectName);
                    }
                    else if (type.equals("ThreadPool")) {
                        this.threadPools.addElement(objectName);
                    }
                    else if (type.equals("GlobalRequestProcessor")) {
                        this.globalRequestProcessors.addElement(objectName);
                    }
                    else if (type.equals("RequestProcessor")) {
                        this.requestProcessors.addElement(objectName);
                    }
                }
            }
            else if (notification.getType().equals("JMX.mbean.unregistered")) {
                final String type = objectName.getKeyProperty("type");
                if (type != null) {
                    if (type.equals("ProtocolHandler")) {
                        this.protocolHandlers.removeElement(objectName);
                    }
                    else if (type.equals("ThreadPool")) {
                        this.threadPools.removeElement(objectName);
                    }
                    else if (type.equals("GlobalRequestProcessor")) {
                        this.globalRequestProcessors.removeElement(objectName);
                    }
                    else if (type.equals("RequestProcessor")) {
                        this.requestProcessors.removeElement(objectName);
                    }
                }
            }
        }
    }
    
    static {
        sm = StringManager.getManager("org.apache.catalina.manager");
    }
}
