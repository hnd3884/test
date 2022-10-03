package org.apache.catalina.manager;

import org.apache.catalina.Service;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import org.apache.tomcat.util.net.SSLContext;
import java.util.Set;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.io.FileInputStream;
import javax.servlet.ServletInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.apache.catalina.Session;
import org.apache.catalina.Manager;
import org.apache.catalina.util.ServerInfo;
import javax.naming.NamingEnumeration;
import org.apache.tomcat.util.IntrospectionUtils;
import javax.naming.Binding;
import org.apache.tomcat.util.security.Escape;
import java.util.Collection;
import org.apache.tomcat.util.buf.StringUtils;
import java.util.Arrays;
import org.apache.catalina.startup.ExpandWar;
import javax.management.MalformedObjectNameException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.Diagnostics;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.coyote.ProtocolHandler;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.Container;
import org.apache.catalina.Server;
import org.apache.tomcat.util.ExceptionUtils;
import javax.servlet.UnavailableException;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Enumeration;
import org.apache.catalina.util.ContextName;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.catalina.Engine;
import org.apache.catalina.Wrapper;
import org.apache.tomcat.util.res.StringManager;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import org.apache.catalina.Host;
import org.apache.catalina.Context;
import java.io.File;
import org.apache.catalina.ContainerServlet;
import javax.servlet.http.HttpServlet;

public class ManagerServlet extends HttpServlet implements ContainerServlet
{
    private static final long serialVersionUID = 1L;
    protected File configBase;
    protected transient Context context;
    protected int debug;
    protected File versioned;
    protected transient Host host;
    protected transient MBeanServer mBeanServer;
    protected ObjectName oname;
    protected transient javax.naming.Context global;
    protected static final StringManager sm;
    protected transient Wrapper wrapper;
    
    public ManagerServlet() {
        this.configBase = null;
        this.context = null;
        this.debug = 1;
        this.versioned = null;
        this.host = null;
        this.mBeanServer = null;
        this.oname = null;
        this.global = null;
        this.wrapper = null;
    }
    
    public Wrapper getWrapper() {
        return this.wrapper;
    }
    
    public void setWrapper(final Wrapper wrapper) {
        this.wrapper = wrapper;
        if (wrapper == null) {
            this.context = null;
            this.host = null;
            this.oname = null;
        }
        else {
            this.context = (Context)wrapper.getParent();
            this.host = (Host)this.context.getParent();
            final Engine engine = (Engine)this.host.getParent();
            final String name = engine.getName() + ":type=Deployer,host=" + this.host.getName();
            try {
                this.oname = new ObjectName(name);
            }
            catch (final Exception e) {
                this.log(ManagerServlet.sm.getString("managerServlet.objectNameFail", new Object[] { name }), (Throwable)e);
            }
        }
        this.mBeanServer = Registry.getRegistry((Object)null, (Object)null).getMBeanServer();
    }
    
    public void destroy() {
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final StringManager smClient = StringManager.getManager("org.apache.catalina.manager", request.getLocales());
        String command = request.getPathInfo();
        if (command == null) {
            command = request.getServletPath();
        }
        final String config = request.getParameter("config");
        final String path = request.getParameter("path");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter("version"));
        }
        final String type = request.getParameter("type");
        final String war = request.getParameter("war");
        final String tag = request.getParameter("tag");
        boolean update = false;
        if (request.getParameter("update") != null && request.getParameter("update").equals("true")) {
            update = true;
        }
        final String tlsHostName = request.getParameter("tlsHostName");
        boolean statusLine = false;
        if ("true".equals(request.getParameter("statusLine"))) {
            statusLine = true;
        }
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        final PrintWriter writer = response.getWriter();
        if (command == null) {
            writer.println(smClient.getString("managerServlet.noCommand"));
        }
        else if (command.equals("/deploy")) {
            if (war != null || config != null) {
                this.deploy(writer, config, cn, war, update, smClient);
            }
            else if (tag != null) {
                this.deploy(writer, cn, tag, smClient);
            }
            else {
                writer.println(smClient.getString("managerServlet.invalidCommand", new Object[] { command }));
            }
        }
        else if (command.equals("/list")) {
            this.list(writer, smClient);
        }
        else if (command.equals("/reload")) {
            this.reload(writer, cn, smClient);
        }
        else if (command.equals("/resources")) {
            this.resources(writer, type, smClient);
        }
        else if (command.equals("/save")) {
            this.save(writer, path, smClient);
        }
        else if (command.equals("/serverinfo")) {
            this.serverinfo(writer, smClient);
        }
        else if (command.equals("/sessions")) {
            this.expireSessions(writer, cn, request, smClient);
        }
        else if (command.equals("/expire")) {
            this.expireSessions(writer, cn, request, smClient);
        }
        else if (command.equals("/start")) {
            this.start(writer, cn, smClient);
        }
        else if (command.equals("/stop")) {
            this.stop(writer, cn, smClient);
        }
        else if (command.equals("/undeploy")) {
            this.undeploy(writer, cn, smClient);
        }
        else if (command.equals("/findleaks")) {
            this.findleaks(statusLine, writer, smClient);
        }
        else if (command.equals("/vminfo")) {
            this.vmInfo(writer, smClient, request.getLocales());
        }
        else if (command.equals("/threaddump")) {
            this.threadDump(writer, smClient, request.getLocales());
        }
        else if (command.equals("/sslConnectorCiphers")) {
            this.sslConnectorCiphers(writer, smClient);
        }
        else if (command.equals("/sslConnectorCerts")) {
            this.sslConnectorCerts(writer, smClient);
        }
        else if (command.equals("/sslConnectorTrustedCerts")) {
            this.sslConnectorTrustedCerts(writer, smClient);
        }
        else if (command.equals("/sslReload")) {
            this.sslReload(writer, tlsHostName, smClient);
        }
        else {
            writer.println(smClient.getString("managerServlet.unknownCommand", new Object[] { command }));
        }
        writer.flush();
        writer.close();
    }
    
    public void doPut(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final StringManager smClient = StringManager.getManager("org.apache.catalina.manager", request.getLocales());
        String command = request.getPathInfo();
        if (command == null) {
            command = request.getServletPath();
        }
        final String path = request.getParameter("path");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter("version"));
        }
        final String tag = request.getParameter("tag");
        boolean update = false;
        if (request.getParameter("update") != null && request.getParameter("update").equals("true")) {
            update = true;
        }
        response.setContentType("text/plain;charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        final PrintWriter writer = response.getWriter();
        if (command == null) {
            writer.println(smClient.getString("managerServlet.noCommand"));
        }
        else if (command.equals("/deploy")) {
            this.deploy(writer, cn, tag, update, request, smClient);
        }
        else {
            writer.println(smClient.getString("managerServlet.unknownCommand", new Object[] { command }));
        }
        writer.flush();
        writer.close();
    }
    
    public void init() throws ServletException {
        if (this.wrapper == null || this.context == null) {
            throw new UnavailableException(ManagerServlet.sm.getString("managerServlet.noWrapper"));
        }
        String value = null;
        try {
            value = this.getServletConfig().getInitParameter("debug");
            this.debug = Integer.parseInt(value);
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        final Server server = ((Engine)this.host.getParent()).getService().getServer();
        if (server != null) {
            this.global = server.getGlobalNamingContext();
        }
        this.versioned = (File)this.getServletContext().getAttribute("javax.servlet.context.tempdir");
        this.configBase = new File(this.context.getCatalinaBase(), "conf");
        Container container = this.context;
        Container host = null;
        Container engine = null;
        while (container != null) {
            if (container instanceof Host) {
                host = container;
            }
            if (container instanceof Engine) {
                engine = container;
            }
            container = container.getParent();
        }
        if (engine != null) {
            this.configBase = new File(this.configBase, engine.getName());
        }
        if (host != null) {
            this.configBase = new File(this.configBase, host.getName());
        }
        if (this.debug >= 1) {
            this.log("init: Associated with Deployer '" + this.oname + "'");
            if (this.global != null) {
                this.log("init: Global resources are available");
            }
        }
    }
    
    protected void findleaks(final boolean statusLine, final PrintWriter writer, final StringManager smClient) {
        if (!(this.host instanceof StandardHost)) {
            writer.println(smClient.getString("managerServlet.findleaksFail"));
            return;
        }
        final String[] results = ((StandardHost)this.host).findReloadedContextMemoryLeaks();
        if (results.length > 0) {
            if (statusLine) {
                writer.println(smClient.getString("managerServlet.findleaksList"));
            }
            for (String result : results) {
                if (result.isEmpty()) {
                    result = "/";
                }
                writer.println(result);
            }
        }
        else if (statusLine) {
            writer.println(smClient.getString("managerServlet.findleaksNone"));
        }
    }
    
    protected void sslReload(final PrintWriter writer, final String tlsHostName, final StringManager smClient) {
        final Connector[] connectors = this.getConnectors();
        boolean found = false;
        for (final Connector connector : connectors) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                final ProtocolHandler protocol = connector.getProtocolHandler();
                if (protocol instanceof AbstractHttp11Protocol) {
                    final AbstractHttp11Protocol<?> http11Protoocol = (AbstractHttp11Protocol<?>)protocol;
                    if (tlsHostName == null || tlsHostName.length() == 0) {
                        found = true;
                        http11Protoocol.reloadSslHostConfigs();
                    }
                    else {
                        final SSLHostConfig[] arr$2;
                        final SSLHostConfig[] sslHostConfigs = arr$2 = http11Protoocol.findSslHostConfigs();
                        for (final SSLHostConfig sslHostConfig : arr$2) {
                            if (sslHostConfig.getHostName().equalsIgnoreCase(tlsHostName)) {
                                found = true;
                                http11Protoocol.reloadSslHostConfig(tlsHostName);
                            }
                        }
                    }
                }
            }
        }
        if (found) {
            if (tlsHostName == null || tlsHostName.length() == 0) {
                writer.println(smClient.getString("managerServlet.sslReloadAll"));
            }
            else {
                writer.println(smClient.getString("managerServlet.sslReload", new Object[] { tlsHostName }));
            }
        }
        else {
            writer.println(smClient.getString("managerServlet.sslReloadFail"));
        }
    }
    
    protected void vmInfo(final PrintWriter writer, final StringManager smClient, final Enumeration<Locale> requestedLocales) {
        writer.println(smClient.getString("managerServlet.vminfo"));
        writer.print(Diagnostics.getVMInfo((Enumeration)requestedLocales));
    }
    
    protected void threadDump(final PrintWriter writer, final StringManager smClient, final Enumeration<Locale> requestedLocales) {
        writer.println(smClient.getString("managerServlet.threaddump"));
        writer.print(Diagnostics.getThreadDump((Enumeration)requestedLocales));
    }
    
    protected void sslConnectorCiphers(final PrintWriter writer, final StringManager smClient) {
        writer.println(smClient.getString("managerServlet.sslConnectorCiphers"));
        final Map<String, List<String>> connectorCiphers = this.getConnectorCiphers(smClient);
        for (final Map.Entry<String, List<String>> entry : connectorCiphers.entrySet()) {
            writer.println(entry.getKey());
            for (final String cipher : entry.getValue()) {
                writer.print("  ");
                writer.println(cipher);
            }
        }
    }
    
    private void sslConnectorCerts(final PrintWriter writer, final StringManager smClient) {
        writer.println(smClient.getString("managerServlet.sslConnectorCerts"));
        final Map<String, List<String>> connectorCerts = this.getConnectorCerts(smClient);
        for (final Map.Entry<String, List<String>> entry : connectorCerts.entrySet()) {
            writer.println(entry.getKey());
            for (final String cert : entry.getValue()) {
                writer.println(cert);
            }
        }
    }
    
    private void sslConnectorTrustedCerts(final PrintWriter writer, final StringManager smClient) {
        writer.println(smClient.getString("managerServlet.sslConnectorTrustedCerts"));
        final Map<String, List<String>> connectorTrustedCerts = this.getConnectorTrustedCerts(smClient);
        for (final Map.Entry<String, List<String>> entry : connectorTrustedCerts.entrySet()) {
            writer.println(entry.getKey());
            for (final String cert : entry.getValue()) {
                writer.println(cert);
            }
        }
    }
    
    protected synchronized void save(final PrintWriter writer, final String path, final StringManager smClient) {
        ObjectName storeConfigOname;
        try {
            storeConfigOname = new ObjectName("Catalina:type=StoreConfig");
        }
        catch (final MalformedObjectNameException e) {
            this.log(ManagerServlet.sm.getString("managerServlet.exception"), (Throwable)e);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { e.toString() }));
            return;
        }
        if (!this.mBeanServer.isRegistered(storeConfigOname)) {
            writer.println(smClient.getString("managerServlet.storeConfig.noMBean", new Object[] { storeConfigOname }));
            return;
        }
        if (path != null && path.length() != 0) {
            if (path.startsWith("/")) {
                String contextPath = path;
                if (path.equals("/")) {
                    contextPath = "";
                }
                final Context context = (Context)this.host.findChild(contextPath);
                if (context == null) {
                    writer.println(smClient.getString("managerServlet.noContext", new Object[] { path }));
                    return;
                }
                try {
                    final Boolean result = (Boolean)this.mBeanServer.invoke(storeConfigOname, "store", new Object[] { context }, new String[] { "org.apache.catalina.Context" });
                    if (result) {
                        writer.println(smClient.getString("managerServlet.savedContext", new Object[] { path }));
                    }
                    else {
                        writer.println(smClient.getString("managerServlet.savedContextFail", new Object[] { path }));
                    }
                }
                catch (final Exception e2) {
                    this.log("managerServlet.save[" + path + "]", (Throwable)e2);
                    writer.println(smClient.getString("managerServlet.exception", new Object[] { e2.toString() }));
                }
                return;
            }
        }
        try {
            this.mBeanServer.invoke(storeConfigOname, "storeConfig", null, null);
            writer.println(smClient.getString("managerServlet.saved"));
        }
        catch (final Exception e3) {
            this.log("managerServlet.storeConfig", (Throwable)e3);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { e3.toString() }));
        }
    }
    
    protected void deploy(final PrintWriter writer, final ContextName cn, final String tag, final boolean update, final HttpServletRequest request, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log("deploy: Deploying web application '" + cn + "'");
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        final String name = cn.getName();
        final String baseName = cn.getBaseName();
        final String displayPath = cn.getDisplayName();
        final Context context = (Context)this.host.findChild(name);
        if (context != null && !update) {
            writer.println(smClient.getString("managerServlet.alreadyContext", new Object[] { displayPath }));
            return;
        }
        final File deployedWar = new File(this.host.getAppBaseFile(), baseName + ".war");
        File uploadedWar;
        if (tag == null) {
            if (update) {
                uploadedWar = new File(deployedWar.getAbsolutePath() + ".tmp");
                if (uploadedWar.exists() && !uploadedWar.delete()) {
                    writer.println(smClient.getString("managerServlet.deleteFail", new Object[] { uploadedWar }));
                }
            }
            else {
                uploadedWar = deployedWar;
            }
        }
        else {
            final File uploadPath = new File(this.versioned, tag);
            if (!uploadPath.mkdirs() && !uploadPath.isDirectory()) {
                writer.println(smClient.getString("managerServlet.mkdirFail", new Object[] { uploadPath }));
                return;
            }
            uploadedWar = new File(uploadPath, baseName + ".war");
        }
        if (this.debug >= 2) {
            this.log("Uploading WAR file to " + uploadedWar);
        }
        try {
            if (this.tryAddServiced(name)) {
                try {
                    this.uploadWar(writer, request, uploadedWar, smClient);
                    if (update && tag == null) {
                        if (deployedWar.exists() && !deployedWar.delete()) {
                            writer.println(smClient.getString("managerServlet.deleteFail", new Object[] { deployedWar }));
                            return;
                        }
                        if (!uploadedWar.renameTo(deployedWar)) {
                            writer.println(smClient.getString("managerServlet.renameFail", new Object[] { uploadedWar, deployedWar }));
                            return;
                        }
                    }
                    if (tag != null) {
                        copy(uploadedWar, deployedWar);
                    }
                }
                finally {
                    this.removeServiced(name);
                }
                this.check(name);
            }
            else {
                writer.println(smClient.getString("managerServlet.inService", new Object[] { displayPath }));
            }
        }
        catch (final Exception e) {
            this.log("managerServlet.check[" + displayPath + "]", (Throwable)e);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { e.toString() }));
            return;
        }
        this.writeDeployResult(writer, smClient, name, displayPath);
    }
    
    protected void deploy(final PrintWriter writer, final ContextName cn, final String tag, final StringManager smClient) {
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        final String baseName = cn.getBaseName();
        final String name = cn.getName();
        final String displayPath = cn.getDisplayName();
        final File localWar = new File(new File(this.versioned, tag), baseName + ".war");
        final File deployedWar = new File(this.host.getAppBaseFile(), baseName + ".war");
        try {
            if (this.tryAddServiced(name)) {
                try {
                    if (!deployedWar.delete()) {
                        writer.println(smClient.getString("managerServlet.deleteFail", new Object[] { deployedWar }));
                        return;
                    }
                    copy(localWar, deployedWar);
                }
                finally {
                    this.removeServiced(name);
                }
                this.check(name);
            }
            else {
                writer.println(smClient.getString("managerServlet.inService", new Object[] { displayPath }));
            }
        }
        catch (final Exception e) {
            this.log("managerServlet.check[" + displayPath + "]", (Throwable)e);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { e.toString() }));
            return;
        }
        this.writeDeployResult(writer, smClient, name, displayPath);
    }
    
    protected void deploy(final PrintWriter writer, String config, final ContextName cn, String war, final boolean update, final StringManager smClient) {
        if (config != null && config.length() == 0) {
            config = null;
        }
        if (war != null && war.length() == 0) {
            war = null;
        }
        if (this.debug >= 1) {
            if (config != null && config.length() > 0) {
                if (war != null) {
                    this.log("install: Installing context configuration at '" + config + "' from '" + war + "'");
                }
                else {
                    this.log("install: Installing context configuration at '" + config + "'");
                }
            }
            else if (cn != null) {
                this.log("install: Installing web application '" + cn + "' from '" + war + "'");
            }
            else {
                this.log("install: Installing web application from '" + war + "'");
            }
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        final String name = cn.getName();
        final String baseName = cn.getBaseName();
        final String displayPath = cn.getDisplayName();
        final Context context = (Context)this.host.findChild(name);
        if (context != null && !update) {
            writer.println(smClient.getString("managerServlet.alreadyContext", new Object[] { displayPath }));
            return;
        }
        if (config != null && config.startsWith("file:")) {
            config = config.substring("file:".length());
        }
        if (war != null && war.startsWith("file:")) {
            war = war.substring("file:".length());
        }
        try {
            if (this.tryAddServiced(name)) {
                try {
                    if (config != null) {
                        if (!this.configBase.mkdirs() && !this.configBase.isDirectory()) {
                            writer.println(smClient.getString("managerServlet.mkdirFail", new Object[] { this.configBase }));
                            return;
                        }
                        final File localConfig = new File(this.configBase, baseName + ".xml");
                        if (localConfig.isFile() && !localConfig.delete()) {
                            writer.println(smClient.getString("managerServlet.deleteFail", new Object[] { localConfig }));
                            return;
                        }
                        copy(new File(config), localConfig);
                    }
                    if (war != null) {
                        File localWar;
                        if (war.endsWith(".war")) {
                            localWar = new File(this.host.getAppBaseFile(), baseName + ".war");
                        }
                        else {
                            localWar = new File(this.host.getAppBaseFile(), baseName);
                        }
                        if (localWar.exists() && !ExpandWar.delete(localWar)) {
                            writer.println(smClient.getString("managerServlet.deleteFail", new Object[] { localWar }));
                            return;
                        }
                        copy(new File(war), localWar);
                    }
                }
                finally {
                    this.removeServiced(name);
                }
                this.check(name);
            }
            else {
                writer.println(smClient.getString("managerServlet.inService", new Object[] { displayPath }));
            }
            this.writeDeployResult(writer, smClient, name, displayPath);
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log("ManagerServlet.install[" + displayPath + "]", t);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { t.toString() }));
        }
    }
    
    private void writeDeployResult(final PrintWriter writer, final StringManager smClient, final String name, final String displayPath) {
        final Context deployed = (Context)this.host.findChild(name);
        if (deployed != null && deployed.getConfigured() && deployed.getState().isAvailable()) {
            writer.println(smClient.getString("managerServlet.deployed", new Object[] { displayPath }));
        }
        else if (deployed != null && !deployed.getState().isAvailable()) {
            writer.println(smClient.getString("managerServlet.deployedButNotStarted", new Object[] { displayPath }));
        }
        else {
            writer.println(smClient.getString("managerServlet.deployFailed", new Object[] { displayPath }));
        }
    }
    
    protected void list(final PrintWriter writer, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log("list: Listing contexts for virtual host '" + this.host.getName() + "'");
        }
        writer.println(smClient.getString("managerServlet.listed", new Object[] { this.host.getName() }));
        final Container[] arr$;
        final Container[] contexts = arr$ = this.host.findChildren();
        for (final Container container : arr$) {
            final Context context = (Context)container;
            if (context != null) {
                String displayPath = context.getPath();
                if (displayPath.equals("")) {
                    displayPath = "/";
                }
                List<String> parts = null;
                if (context.getState().isAvailable()) {
                    parts = Arrays.asList(displayPath, "running", "" + context.getManager().findSessions().length, context.getDocBase());
                }
                else {
                    parts = Arrays.asList(displayPath, "stopped", "0", context.getDocBase());
                }
                writer.println(StringUtils.join((Collection)parts, ':'));
            }
        }
    }
    
    protected void reload(final PrintWriter writer, final ContextName cn, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log("restart: Reloading web application '" + cn + "'");
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        try {
            final Context context = (Context)this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[] { Escape.htmlElementContent(cn.getDisplayName()) }));
                return;
            }
            if (context.getName().equals(this.context.getName())) {
                writer.println(smClient.getString("managerServlet.noSelf"));
                return;
            }
            context.reload();
            writer.println(smClient.getString("managerServlet.reloaded", new Object[] { cn.getDisplayName() }));
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log("ManagerServlet.reload[" + cn.getDisplayName() + "]", t);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { t.toString() }));
        }
    }
    
    protected void resources(final PrintWriter writer, final String type, final StringManager smClient) {
        if (this.debug >= 1) {
            if (type != null) {
                this.log("resources:  Listing resources of type " + type);
            }
            else {
                this.log("resources:  Listing resources of all types");
            }
        }
        if (this.global == null) {
            writer.println(smClient.getString("managerServlet.noGlobal"));
            return;
        }
        if (type != null) {
            writer.println(smClient.getString("managerServlet.resourcesType", new Object[] { type }));
        }
        else {
            writer.println(smClient.getString("managerServlet.resourcesAll"));
        }
        this.printResources(writer, "", this.global, type, smClient);
    }
    
    @Deprecated
    protected void printResources(final PrintWriter writer, final String prefix, final javax.naming.Context namingContext, final String type, final Class<?> clazz, final StringManager smClient) {
        this.printResources(writer, prefix, namingContext, type, smClient);
    }
    
    protected void printResources(final PrintWriter writer, final String prefix, final javax.naming.Context namingContext, final String type, final StringManager smClient) {
        try {
            final NamingEnumeration<Binding> items = namingContext.listBindings("");
            while (items.hasMore()) {
                final Binding item = items.next();
                final Object obj = item.getObject();
                if (obj instanceof javax.naming.Context) {
                    this.printResources(writer, prefix + item.getName() + "/", (javax.naming.Context)obj, type, smClient);
                }
                else {
                    if (type != null) {
                        if (obj == null) {
                            continue;
                        }
                        if (!IntrospectionUtils.isInstance((Class)obj.getClass(), type)) {
                            continue;
                        }
                    }
                    writer.print(prefix + item.getName());
                    writer.print(':');
                    writer.print(item.getClassName());
                    writer.println();
                }
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log("ManagerServlet.resources[" + type + "]", t);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { t.toString() }));
        }
    }
    
    protected void serverinfo(final PrintWriter writer, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log("serverinfo");
        }
        try {
            writer.println(smClient.getString("managerServlet.serverInfo", new Object[] { ServerInfo.getServerInfo(), System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"), System.getProperty("java.runtime.version"), System.getProperty("java.vm.vendor") }));
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.getServletContext().log("ManagerServlet.serverinfo", t);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { t.toString() }));
        }
    }
    
    protected void sessions(final PrintWriter writer, final ContextName cn, final int idle, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log("sessions: Session information for web application '" + cn + "'");
            if (idle >= 0) {
                this.log("sessions: Session expiration for " + idle + " minutes '" + cn + "'");
            }
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        final String displayPath = cn.getDisplayName();
        try {
            final Context context = (Context)this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[] { Escape.htmlElementContent(displayPath) }));
                return;
            }
            final Manager manager = context.getManager();
            if (manager == null) {
                writer.println(smClient.getString("managerServlet.noManager", new Object[] { Escape.htmlElementContent(displayPath) }));
                return;
            }
            int maxCount = 60;
            int histoInterval = 1;
            final int maxInactiveInterval = context.getSessionTimeout();
            if (maxInactiveInterval > 0) {
                histoInterval = maxInactiveInterval / maxCount;
                if (histoInterval * maxCount < maxInactiveInterval) {
                    ++histoInterval;
                }
                if (0 == histoInterval) {
                    histoInterval = 1;
                }
                maxCount = maxInactiveInterval / histoInterval;
                if (histoInterval * maxCount < maxInactiveInterval) {
                    ++maxCount;
                }
            }
            writer.println(smClient.getString("managerServlet.sessions", new Object[] { displayPath }));
            writer.println(smClient.getString("managerServlet.sessiondefaultmax", new Object[] { "" + maxInactiveInterval }));
            final Session[] sessions = manager.findSessions();
            final int[] timeout = new int[maxCount + 1];
            int notimeout = 0;
            int expired = 0;
            for (final Session session : sessions) {
                int time = (int)(session.getIdleTimeInternal() / 1000L);
                if (idle >= 0 && time >= idle * 60) {
                    session.expire();
                    ++expired;
                }
                time = time / 60 / histoInterval;
                if (time < 0) {
                    ++notimeout;
                }
                else if (time >= maxCount) {
                    final int[] array = timeout;
                    final int n = maxCount;
                    ++array[n];
                }
                else {
                    final int[] array2 = timeout;
                    final int n2 = time;
                    ++array2[n2];
                }
            }
            if (timeout[0] > 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout", new Object[] { "<" + histoInterval, "" + timeout[0] }));
            }
            for (int i = 1; i < maxCount; ++i) {
                if (timeout[i] > 0) {
                    writer.println(smClient.getString("managerServlet.sessiontimeout", new Object[] { "" + i * histoInterval + " - <" + (i + 1) * histoInterval, "" + timeout[i] }));
                }
            }
            if (timeout[maxCount] > 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout", new Object[] { ">=" + maxCount * histoInterval, "" + timeout[maxCount] }));
            }
            if (notimeout > 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout.unlimited", new Object[] { "" + notimeout }));
            }
            if (idle >= 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout.expired", new Object[] { ">" + idle, "" + expired }));
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log("ManagerServlet.sessions[" + displayPath + "]", t);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { t.toString() }));
        }
    }
    
    protected void expireSessions(final PrintWriter writer, final ContextName cn, final HttpServletRequest req, final StringManager smClient) {
        int idle = -1;
        final String idleParam = req.getParameter("idle");
        if (idleParam != null) {
            try {
                idle = Integer.parseInt(idleParam);
            }
            catch (final NumberFormatException e) {
                this.log("Could not parse idle parameter to an int: " + idleParam);
            }
        }
        this.sessions(writer, cn, idle, smClient);
    }
    
    protected void start(final PrintWriter writer, final ContextName cn, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log("start: Starting web application '" + cn + "'");
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        final String displayPath = cn.getDisplayName();
        try {
            final Context context = (Context)this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[] { Escape.htmlElementContent(displayPath) }));
                return;
            }
            context.start();
            if (context.getState().isAvailable()) {
                writer.println(smClient.getString("managerServlet.started", new Object[] { displayPath }));
            }
            else {
                writer.println(smClient.getString("managerServlet.startFailed", new Object[] { displayPath }));
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.getServletContext().log(ManagerServlet.sm.getString("managerServlet.startFailed", new Object[] { displayPath }), t);
            writer.println(smClient.getString("managerServlet.startFailed", new Object[] { displayPath }));
            writer.println(smClient.getString("managerServlet.exception", new Object[] { t.toString() }));
        }
    }
    
    protected void stop(final PrintWriter writer, final ContextName cn, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log("stop: Stopping web application '" + cn + "'");
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        final String displayPath = cn.getDisplayName();
        try {
            final Context context = (Context)this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[] { Escape.htmlElementContent(displayPath) }));
                return;
            }
            if (context.getName().equals(this.context.getName())) {
                writer.println(smClient.getString("managerServlet.noSelf"));
                return;
            }
            context.stop();
            writer.println(smClient.getString("managerServlet.stopped", new Object[] { displayPath }));
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log("ManagerServlet.stop[" + displayPath + "]", t);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { t.toString() }));
        }
    }
    
    protected void undeploy(final PrintWriter writer, final ContextName cn, final StringManager smClient) {
        if (this.debug >= 1) {
            this.log("undeploy: Undeploying web application at '" + cn + "'");
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        final String name = cn.getName();
        final String baseName = cn.getBaseName();
        final String displayPath = cn.getDisplayName();
        try {
            final Context context = (Context)this.host.findChild(name);
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", new Object[] { Escape.htmlElementContent(displayPath) }));
                return;
            }
            if (!this.isDeployed(name)) {
                writer.println(smClient.getString("managerServlet.notDeployed", new Object[] { Escape.htmlElementContent(displayPath) }));
                return;
            }
            if (this.tryAddServiced(name)) {
                try {
                    context.stop();
                }
                catch (final Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                }
                try {
                    final File war = new File(this.host.getAppBaseFile(), baseName + ".war");
                    final File dir = new File(this.host.getAppBaseFile(), baseName);
                    final File xml = new File(this.configBase, baseName + ".xml");
                    if (war.exists() && !war.delete()) {
                        writer.println(smClient.getString("managerServlet.deleteFail", new Object[] { war }));
                        return;
                    }
                    if (dir.exists() && !this.undeployDir(dir)) {
                        writer.println(smClient.getString("managerServlet.deleteFail", new Object[] { dir }));
                        return;
                    }
                    if (xml.exists() && !xml.delete()) {
                        writer.println(smClient.getString("managerServlet.deleteFail", new Object[] { xml }));
                        return;
                    }
                }
                finally {
                    this.removeServiced(name);
                }
                this.check(name);
            }
            else {
                writer.println(smClient.getString("managerServlet.inService", new Object[] { displayPath }));
            }
            writer.println(smClient.getString("managerServlet.undeployed", new Object[] { displayPath }));
        }
        catch (final Throwable t2) {
            ExceptionUtils.handleThrowable(t2);
            this.log("ManagerServlet.undeploy[" + displayPath + "]", t2);
            writer.println(smClient.getString("managerServlet.exception", new Object[] { t2.toString() }));
        }
    }
    
    protected boolean isDeployed(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        final Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "isDeployed", params, signature);
        return result;
    }
    
    protected void check(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        this.mBeanServer.invoke(this.oname, "check", params, signature);
    }
    
    @Deprecated
    protected boolean isServiced(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        final Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "isServiced", params, signature);
        return result;
    }
    
    @Deprecated
    protected void addServiced(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        this.mBeanServer.invoke(this.oname, "addServiced", params, signature);
    }
    
    protected boolean tryAddServiced(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        final Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "tryAddServiced", params, signature);
        return result;
    }
    
    protected void removeServiced(final String name) throws Exception {
        final String[] params = { name };
        final String[] signature = { "java.lang.String" };
        this.mBeanServer.invoke(this.oname, "removeServiced", params, signature);
    }
    
    protected boolean undeployDir(final File dir) {
        String[] files = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length; ++i) {
            final File file = new File(dir, files[i]);
            if (file.isDirectory()) {
                if (!this.undeployDir(file)) {
                    return false;
                }
            }
            else if (!file.delete()) {
                return false;
            }
        }
        return dir.delete();
    }
    
    protected void uploadWar(final PrintWriter writer, final HttpServletRequest request, final File war, final StringManager smClient) throws IOException {
        if (war.exists() && !war.delete()) {
            final String msg = smClient.getString("managerServlet.deleteFail", new Object[] { war });
            throw new IOException(msg);
        }
        try (final ServletInputStream istream = request.getInputStream();
             final BufferedOutputStream ostream = new BufferedOutputStream(new FileOutputStream(war), 1024)) {
            final byte[] buffer = new byte[1024];
            while (true) {
                final int n = istream.read(buffer);
                if (n < 0) {
                    break;
                }
                ostream.write(buffer, 0, n);
            }
        }
        catch (final IOException e) {
            if (war.exists() && !war.delete()) {
                writer.println(smClient.getString("managerServlet.deleteFail", new Object[] { war }));
            }
            throw e;
        }
    }
    
    protected static boolean validateContextName(final ContextName cn, final PrintWriter writer, final StringManager smClient) {
        if (cn != null && (cn.getPath().startsWith("/") || cn.getPath().equals(""))) {
            return true;
        }
        String path = null;
        if (cn != null) {
            path = Escape.htmlElementContent(cn.getPath());
        }
        writer.println(smClient.getString("managerServlet.invalidPath", new Object[] { path }));
        return false;
    }
    
    public static boolean copy(final File src, final File dest) {
        boolean result = false;
        try {
            if (src != null && !src.getCanonicalPath().equals(dest.getCanonicalPath())) {
                result = copyInternal(src, dest, new byte[4096]);
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static boolean copyInternal(final File src, final File dest, final byte[] buf) {
        boolean result = true;
        String[] files = null;
        if (src.isDirectory()) {
            files = src.list();
            result = dest.mkdir();
        }
        else {
            files = new String[] { "" };
        }
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length && result; ++i) {
            final File fileSrc = new File(src, files[i]);
            final File fileDest = new File(dest, files[i]);
            if (fileSrc.isDirectory()) {
                result = copyInternal(fileSrc, fileDest, buf);
            }
            else {
                try (final FileInputStream is = new FileInputStream(fileSrc);
                     final FileOutputStream os = new FileOutputStream(fileDest)) {
                    int len = 0;
                    while (true) {
                        len = is.read(buf);
                        if (len == -1) {
                            break;
                        }
                        os.write(buf, 0, len);
                    }
                }
                catch (final IOException e) {
                    e.printStackTrace();
                    result = false;
                }
            }
        }
        return result;
    }
    
    protected Map<String, List<String>> getConnectorCiphers(final StringManager smClient) {
        final Map<String, List<String>> result = new HashMap<String, List<String>>();
        final Connector[] arr$;
        final Connector[] connectors = arr$ = this.getConnectors();
        for (final Connector connector : arr$) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                final SSLHostConfig[] arr$2;
                final SSLHostConfig[] sslHostConfigs = arr$2 = connector.getProtocolHandler().findSslHostConfigs();
                for (final SSLHostConfig sslHostConfig : arr$2) {
                    final String name = connector.toString() + "-" + sslHostConfig.getHostName();
                    result.put(name, new ArrayList<String>(new LinkedHashSet<String>(Arrays.asList(sslHostConfig.getEnabledCiphers()))));
                }
            }
            else {
                final ArrayList<String> cipherList = new ArrayList<String>(1);
                cipherList.add(smClient.getString("managerServlet.notSslConnector"));
                result.put(connector.toString(), cipherList);
            }
        }
        return result;
    }
    
    protected Map<String, List<String>> getConnectorCerts(final StringManager smClient) {
        final Map<String, List<String>> result = new HashMap<String, List<String>>();
        final Connector[] arr$;
        final Connector[] connectors = arr$ = this.getConnectors();
        for (final Connector connector : arr$) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                final SSLHostConfig[] arr$2;
                final SSLHostConfig[] sslHostConfigs = arr$2 = connector.getProtocolHandler().findSslHostConfigs();
                for (final SSLHostConfig sslHostConfig : arr$2) {
                    if (sslHostConfig.getOpenSslContext() == 0L) {
                        final Set<SSLHostConfigCertificate> sslHostConfigCerts = sslHostConfig.getCertificates();
                        for (final SSLHostConfigCertificate sslHostConfigCert : sslHostConfigCerts) {
                            final String name = connector.toString() + "-" + sslHostConfig.getHostName() + "-" + sslHostConfigCert.getType();
                            final List<String> certList = new ArrayList<String>();
                            final SSLContext sslContext = sslHostConfigCert.getSslContext();
                            String alias = sslHostConfigCert.getCertificateKeyAlias();
                            if (alias == null) {
                                alias = "tomcat";
                            }
                            final X509Certificate[] certs = sslContext.getCertificateChain(alias);
                            if (certs == null) {
                                certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                            }
                            else {
                                for (final Certificate cert : certs) {
                                    certList.add(cert.toString());
                                }
                            }
                            result.put(name, certList);
                        }
                    }
                    else {
                        final List<String> certList2 = new ArrayList<String>();
                        certList2.add(smClient.getString("managerServlet.certsNotAvailable"));
                        final String name2 = connector.toString() + "-" + sslHostConfig.getHostName();
                        result.put(name2, certList2);
                    }
                }
            }
            else {
                final List<String> certList3 = new ArrayList<String>(1);
                certList3.add(smClient.getString("managerServlet.notSslConnector"));
                result.put(connector.toString(), certList3);
            }
        }
        return result;
    }
    
    protected Map<String, List<String>> getConnectorTrustedCerts(final StringManager smClient) {
        final Map<String, List<String>> result = new HashMap<String, List<String>>();
        final Connector[] arr$;
        final Connector[] connectors = arr$ = this.getConnectors();
        for (final Connector connector : arr$) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                final SSLHostConfig[] arr$2;
                final SSLHostConfig[] sslHostConfigs = arr$2 = connector.getProtocolHandler().findSslHostConfigs();
                for (final SSLHostConfig sslHostConfig : arr$2) {
                    final String name = connector.toString() + "-" + sslHostConfig.getHostName();
                    final List<String> certList = new ArrayList<String>();
                    if (sslHostConfig.getOpenSslContext() == 0L) {
                        final SSLContext sslContext = sslHostConfig.getCertificates().iterator().next().getSslContext();
                        final X509Certificate[] certs = sslContext.getAcceptedIssuers();
                        if (certs == null) {
                            certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                        }
                        else if (certs.length == 0) {
                            certList.add(smClient.getString("managerServlet.trustedCertsNotConfigured"));
                        }
                        else {
                            for (final Certificate cert : certs) {
                                certList.add(cert.toString());
                            }
                        }
                    }
                    else {
                        certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                    }
                    result.put(name, certList);
                }
            }
            else {
                final List<String> certList2 = new ArrayList<String>(1);
                certList2.add(smClient.getString("managerServlet.notSslConnector"));
                result.put(connector.toString(), certList2);
            }
        }
        return result;
    }
    
    private Connector[] getConnectors() {
        final Engine e = (Engine)this.host.getParent();
        final Service s = e.getService();
        return s.findConnectors();
    }
    
    static {
        sm = StringManager.getManager("org.apache.catalina.manager");
    }
}
