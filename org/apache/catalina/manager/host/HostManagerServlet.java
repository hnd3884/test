package org.apache.catalina.manager.host;

import javax.management.MBeanServer;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.catalina.core.ContainerBase;
import java.nio.file.Path;
import java.io.InputStream;
import org.apache.catalina.Container;
import java.util.StringTokenizer;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.startup.HostConfig;
import org.apache.catalina.core.StandardHost;
import java.nio.file.Files;
import java.nio.file.CopyOption;
import java.io.File;
import org.apache.tomcat.util.ExceptionUtils;
import javax.servlet.UnavailableException;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.Wrapper;
import org.apache.tomcat.util.res.StringManager;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Context;
import org.apache.catalina.ContainerServlet;
import javax.servlet.http.HttpServlet;

public class HostManagerServlet extends HttpServlet implements ContainerServlet
{
    private static final long serialVersionUID = 1L;
    protected transient Context context;
    protected int debug;
    protected transient Host installedHost;
    protected transient Engine engine;
    protected static final StringManager sm;
    protected transient Wrapper wrapper;
    
    public HostManagerServlet() {
        this.context = null;
        this.debug = 1;
        this.installedHost = null;
        this.engine = null;
        this.wrapper = null;
    }
    
    public Wrapper getWrapper() {
        return this.wrapper;
    }
    
    public void setWrapper(final Wrapper wrapper) {
        this.wrapper = wrapper;
        if (wrapper == null) {
            this.context = null;
            this.installedHost = null;
            this.engine = null;
        }
        else {
            this.context = (Context)wrapper.getParent();
            this.installedHost = (Host)this.context.getParent();
            this.engine = (Engine)this.installedHost.getParent();
        }
    }
    
    public void destroy() {
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final StringManager smClient = StringManager.getManager("org.apache.catalina.manager.host", request.getLocales());
        String command = request.getPathInfo();
        if (command == null) {
            command = request.getServletPath();
        }
        final String name = request.getParameter("name");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        final PrintWriter writer = response.getWriter();
        if (command == null) {
            writer.println(smClient.getString("hostManagerServlet.noCommand"));
        }
        else if (command.equals("/add")) {
            this.add(request, writer, name, false, smClient);
        }
        else if (command.equals("/remove")) {
            this.remove(writer, name, smClient);
        }
        else if (command.equals("/list")) {
            this.list(writer, smClient);
        }
        else if (command.equals("/start")) {
            this.start(writer, name, smClient);
        }
        else if (command.equals("/stop")) {
            this.stop(writer, name, smClient);
        }
        else if (command.equals("/persist")) {
            this.persist(writer, smClient);
        }
        else {
            writer.println(smClient.getString("hostManagerServlet.unknownCommand", new Object[] { command }));
        }
        writer.flush();
        writer.close();
    }
    
    protected void add(final HttpServletRequest request, final PrintWriter writer, final String name, final boolean htmlMode, final StringManager smClient) {
        final String aliases = request.getParameter("aliases");
        final String appBase = request.getParameter("appBase");
        final boolean manager = this.booleanParameter(request, "manager", false, htmlMode);
        final boolean autoDeploy = this.booleanParameter(request, "autoDeploy", true, htmlMode);
        final boolean deployOnStartup = this.booleanParameter(request, "deployOnStartup", true, htmlMode);
        final boolean deployXML = this.booleanParameter(request, "deployXML", true, htmlMode);
        final boolean unpackWARs = this.booleanParameter(request, "unpackWARs", true, htmlMode);
        final boolean copyXML = this.booleanParameter(request, "copyXML", false, htmlMode);
        this.add(writer, name, aliases, appBase, manager, autoDeploy, deployOnStartup, deployXML, unpackWARs, copyXML, smClient);
    }
    
    protected boolean booleanParameter(final HttpServletRequest request, final String parameter, final boolean theDefault, final boolean htmlMode) {
        final String value = request.getParameter(parameter);
        boolean booleanValue = theDefault;
        if (value != null) {
            if (htmlMode) {
                if (value.equals("on")) {
                    booleanValue = true;
                }
            }
            else if (theDefault) {
                if (value.equals("false")) {
                    booleanValue = false;
                }
            }
            else if (value.equals("true")) {
                booleanValue = true;
            }
        }
        else if (htmlMode) {
            booleanValue = false;
        }
        return booleanValue;
    }
    
    public void init() throws ServletException {
        if (this.wrapper == null || this.context == null) {
            throw new UnavailableException(HostManagerServlet.sm.getString("hostManagerServlet.noWrapper"));
        }
        String value = null;
        try {
            value = this.getServletConfig().getInitParameter("debug");
            this.debug = Integer.parseInt(value);
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
    }
    
    protected synchronized void add(final PrintWriter writer, final String name, final String aliases, final String appBase, final boolean manager, final boolean autoDeploy, final boolean deployOnStartup, final boolean deployXML, final boolean unpackWARs, final boolean copyXML, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log(HostManagerServlet.sm.getString("hostManagerServlet.add", new Object[] { name }));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", new Object[] { name }));
            return;
        }
        if (this.engine.findChild(name) != null) {
            writer.println(smClient.getString("hostManagerServlet.alreadyHost", new Object[] { name }));
            return;
        }
        File appBaseFile = null;
        File file = null;
        String applicationBase = appBase;
        if (applicationBase == null || applicationBase.length() == 0) {
            applicationBase = name;
        }
        file = new File(applicationBase);
        if (!file.isAbsolute()) {
            file = new File(this.engine.getCatalinaBase(), file.getPath());
        }
        try {
            appBaseFile = file.getCanonicalFile();
        }
        catch (final IOException e) {
            appBaseFile = file;
        }
        if (!appBaseFile.mkdirs() && !appBaseFile.isDirectory()) {
            writer.println(smClient.getString("hostManagerServlet.appBaseCreateFail", new Object[] { appBaseFile.toString(), name }));
            return;
        }
        final File configBaseFile = this.getConfigBase(name);
        if (manager) {
            if (configBaseFile == null) {
                writer.println(smClient.getString("hostManagerServlet.configBaseCreateFail", new Object[] { name }));
                return;
            }
            try (final InputStream is = this.getServletContext().getResourceAsStream("/WEB-INF/manager.xml")) {
                final Path dest = new File(configBaseFile, "manager.xml").toPath();
                Files.copy(is, dest, new CopyOption[0]);
            }
            catch (final IOException e2) {
                writer.println(smClient.getString("hostManagerServlet.managerXml"));
                return;
            }
        }
        StandardHost host = new StandardHost();
        host.setAppBase(applicationBase);
        host.setName(name);
        host.addLifecycleListener(new HostConfig());
        if (aliases != null && !aliases.isEmpty()) {
            final StringTokenizer tok = new StringTokenizer(aliases, ", ");
            while (tok.hasMoreTokens()) {
                host.addAlias(tok.nextToken());
            }
        }
        host.setAutoDeploy(autoDeploy);
        host.setDeployOnStartup(deployOnStartup);
        host.setDeployXML(deployXML);
        host.setUnpackWARs(unpackWARs);
        host.setCopyXML(copyXML);
        try {
            this.engine.addChild(host);
        }
        catch (final Exception e3) {
            writer.println(smClient.getString("hostManagerServlet.exception", new Object[] { e3.toString() }));
            return;
        }
        host = (StandardHost)this.engine.findChild(name);
        if (host != null) {
            writer.println(smClient.getString("hostManagerServlet.addSuccess", new Object[] { name }));
        }
        else {
            writer.println(smClient.getString("hostManagerServlet.addFailed", new Object[] { name }));
        }
    }
    
    protected synchronized void remove(final PrintWriter writer, final String name, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log(HostManagerServlet.sm.getString("hostManagerServlet.remove", new Object[] { name }));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", new Object[] { name }));
            return;
        }
        if (this.engine.findChild(name) == null) {
            writer.println(smClient.getString("hostManagerServlet.noHost", new Object[] { name }));
            return;
        }
        if (this.engine.findChild(name) == this.installedHost) {
            writer.println(smClient.getString("hostManagerServlet.cannotRemoveOwnHost", new Object[] { name }));
            return;
        }
        try {
            final Container child = this.engine.findChild(name);
            this.engine.removeChild(child);
            if (child instanceof ContainerBase) {
                ((ContainerBase)child).destroy();
            }
        }
        catch (final Exception e) {
            writer.println(smClient.getString("hostManagerServlet.exception", new Object[] { e.toString() }));
            return;
        }
        final Host host = (StandardHost)this.engine.findChild(name);
        if (host == null) {
            writer.println(smClient.getString("hostManagerServlet.removeSuccess", new Object[] { name }));
        }
        else {
            writer.println(smClient.getString("hostManagerServlet.removeFailed", new Object[] { name }));
        }
    }
    
    protected void list(final PrintWriter writer, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log(HostManagerServlet.sm.getString("hostManagerServlet.list", new Object[] { this.engine.getName() }));
        }
        writer.println(smClient.getString("hostManagerServlet.listed", new Object[] { this.engine.getName() }));
        final Container[] arr$;
        final Container[] hosts = arr$ = this.engine.findChildren();
        for (final Container container : arr$) {
            final Host host = (Host)container;
            final String name = host.getName();
            final String[] aliases = host.findAliases();
            writer.println(String.format("[%s]:[%s]", name, StringUtils.join(aliases)));
        }
    }
    
    protected void start(final PrintWriter writer, final String name, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log(HostManagerServlet.sm.getString("hostManagerServlet.start", new Object[] { name }));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", new Object[] { name }));
            return;
        }
        final Container host = this.engine.findChild(name);
        if (host == null) {
            writer.println(smClient.getString("hostManagerServlet.noHost", new Object[] { name }));
            return;
        }
        if (host == this.installedHost) {
            writer.println(smClient.getString("hostManagerServlet.cannotStartOwnHost", new Object[] { name }));
            return;
        }
        if (host.getState().isAvailable()) {
            writer.println(smClient.getString("hostManagerServlet.alreadyStarted", new Object[] { name }));
            return;
        }
        try {
            host.start();
            writer.println(smClient.getString("hostManagerServlet.started", new Object[] { name }));
        }
        catch (final Exception e) {
            this.getServletContext().log(HostManagerServlet.sm.getString("hostManagerServlet.startFailed", new Object[] { name }), (Throwable)e);
            writer.println(smClient.getString("hostManagerServlet.startFailed", new Object[] { name }));
            writer.println(smClient.getString("hostManagerServlet.exception", new Object[] { e.toString() }));
        }
    }
    
    protected void stop(final PrintWriter writer, final String name, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log(HostManagerServlet.sm.getString("hostManagerServlet.stop", new Object[] { name }));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", new Object[] { name }));
            return;
        }
        final Container host = this.engine.findChild(name);
        if (host == null) {
            writer.println(smClient.getString("hostManagerServlet.noHost", new Object[] { name }));
            return;
        }
        if (host == this.installedHost) {
            writer.println(smClient.getString("hostManagerServlet.cannotStopOwnHost", new Object[] { name }));
            return;
        }
        if (!host.getState().isAvailable()) {
            writer.println(smClient.getString("hostManagerServlet.alreadyStopped", new Object[] { name }));
            return;
        }
        try {
            host.stop();
            writer.println(smClient.getString("hostManagerServlet.stopped", new Object[] { name }));
        }
        catch (final Exception e) {
            this.getServletContext().log(HostManagerServlet.sm.getString("hostManagerServlet.stopFailed", new Object[] { name }), (Throwable)e);
            writer.println(smClient.getString("hostManagerServlet.stopFailed", new Object[] { name }));
            writer.println(smClient.getString("hostManagerServlet.exception", new Object[] { e.toString() }));
        }
    }
    
    protected void persist(final PrintWriter writer, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log(HostManagerServlet.sm.getString("hostManagerServlet.persist"));
        }
        try {
            final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            final ObjectName oname = new ObjectName(this.engine.getDomain() + ":type=StoreConfig");
            platformMBeanServer.invoke(oname, "storeConfig", null, null);
            writer.println(smClient.getString("hostManagerServlet.persisted"));
        }
        catch (final Exception e) {
            this.getServletContext().log(HostManagerServlet.sm.getString("hostManagerServlet.persistFailed"), (Throwable)e);
            writer.println(smClient.getString("hostManagerServlet.persistFailed"));
            if (e instanceof InstanceNotFoundException) {
                writer.println("Please enable StoreConfig to use this feature.");
            }
            else {
                writer.println(smClient.getString("hostManagerServlet.exception", new Object[] { e.toString() }));
            }
        }
    }
    
    protected File getConfigBase(final String hostName) {
        File configBase = new File(this.context.getCatalinaBase(), "conf");
        if (!configBase.exists()) {
            return null;
        }
        if (this.engine != null) {
            configBase = new File(configBase, this.engine.getName());
        }
        if (this.installedHost != null) {
            configBase = new File(configBase, hostName);
        }
        if (!configBase.mkdirs() && !configBase.isDirectory()) {
            return null;
        }
        return configBase;
    }
    
    static {
        sm = StringManager.getManager("org.apache.catalina.manager.host");
    }
}
