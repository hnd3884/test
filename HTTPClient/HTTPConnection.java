package HTTPClient;

import java.io.FilterOutputStream;
import java.io.DataOutputStream;
import javax.net.ssl.SSLException;
import sun.security.x509.X500Name;
import javax.security.cert.X509Certificate;
import java.net.SocketException;
import java.net.NoRouteToHostException;
import java.net.ConnectException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URL;
import java.applet.Applet;
import javax.net.ssl.SSLSocketFactory;
import java.util.Vector;
import java.net.InetAddress;

public class HTTPConnection implements GlobalConstants, HTTPClientModuleConstants
{
    public static final String version = "RPT-HTTPClient/0.3-3E";
    private static final Object dflt_context;
    private Object Context;
    private int Protocol;
    int ServerProtocolVersion;
    boolean ServProtVersKnown;
    private String RequestProtocolVersion;
    private String Host;
    private int Port;
    private InetAddress LocalAddr;
    private int LocalPort;
    private String Proxy_Host;
    private int Proxy_Port;
    private static String Default_Proxy_Host;
    private static int Default_Proxy_Port;
    private static CIHashtable non_proxy_host_list;
    private static Vector non_proxy_dom_list;
    private static Vector non_proxy_addr_list;
    private static Vector non_proxy_mask_list;
    private SocksClient Socks_client;
    private static SocksClient Default_Socks_client;
    private StreamDemultiplexor input_demux;
    LinkedList DemuxList;
    private LinkedList RequestList;
    private boolean doesKeepAlive;
    private boolean keepAliveUnknown;
    private int keepAliveReqMax;
    private int keepAliveReqLeft;
    private static boolean no_chunked;
    private static boolean force_1_0;
    private static boolean neverPipeline;
    private static boolean noKeepAlives;
    private static boolean haveMSLargeWritesBug;
    static boolean deferStreamed;
    private static int DefaultTimeout;
    private int Timeout;
    private NVPair[] DefaultHeaders;
    private static Vector DefaultModuleList;
    private Vector ModuleList;
    private static boolean defaultAllowUI;
    private boolean allowUI;
    private static SSLSocketFactory defaultSSLFactory;
    private SSLSocketFactory sslFactory;
    private volatile Response early_stall;
    private volatile Response late_stall;
    private volatile Response prev_resp;
    private boolean output_finished;
    
    public HTTPConnection(final Applet applet) throws ProtocolNotSuppException {
        this(applet.getCodeBase().getProtocol(), applet.getCodeBase().getHost(), applet.getCodeBase().getPort());
    }
    
    public HTTPConnection(final String host) {
        this.Context = null;
        this.Proxy_Host = null;
        this.Socks_client = null;
        this.input_demux = null;
        this.DemuxList = new LinkedList();
        this.RequestList = new LinkedList();
        this.doesKeepAlive = false;
        this.keepAliveUnknown = true;
        this.keepAliveReqMax = -1;
        this.DefaultHeaders = new NVPair[0];
        this.early_stall = null;
        this.late_stall = null;
        this.prev_resp = null;
        this.output_finished = true;
        this.Setup(0, host, 80, null, -1);
    }
    
    public HTTPConnection(final String host, final int port) {
        this.Context = null;
        this.Proxy_Host = null;
        this.Socks_client = null;
        this.input_demux = null;
        this.DemuxList = new LinkedList();
        this.RequestList = new LinkedList();
        this.doesKeepAlive = false;
        this.keepAliveUnknown = true;
        this.keepAliveReqMax = -1;
        this.DefaultHeaders = new NVPair[0];
        this.early_stall = null;
        this.late_stall = null;
        this.prev_resp = null;
        this.output_finished = true;
        this.Setup(0, host, port, null, -1);
    }
    
    public HTTPConnection(final String prot, final String host, final int port) throws ProtocolNotSuppException {
        this(prot, host, port, null, -1);
    }
    
    public HTTPConnection(String prot, final String host, final int port, final InetAddress localAddr, final int localPort) throws ProtocolNotSuppException {
        this.Context = null;
        this.Proxy_Host = null;
        this.Socks_client = null;
        this.input_demux = null;
        this.DemuxList = new LinkedList();
        this.RequestList = new LinkedList();
        this.doesKeepAlive = false;
        this.keepAliveUnknown = true;
        this.keepAliveReqMax = -1;
        this.DefaultHeaders = new NVPair[0];
        this.early_stall = null;
        this.late_stall = null;
        this.prev_resp = null;
        this.output_finished = true;
        prot = prot.trim().toLowerCase();
        if (!prot.equals("http") && !prot.equals("https")) {
            throw new ProtocolNotSuppException("Unsupported protocol '" + prot + "'");
        }
        if (prot.equals("http")) {
            this.Setup(0, host, port, localAddr, localPort);
        }
        else if (prot.equals("https")) {
            this.Setup(1, host, port, localAddr, localPort);
        }
        else if (prot.equals("shttp")) {
            this.Setup(2, host, port, localAddr, localPort);
        }
        else if (prot.equals("http-ng")) {
            this.Setup(3, host, port, localAddr, localPort);
        }
    }
    
    public HTTPConnection(final URL url) throws ProtocolNotSuppException {
        this(url.getProtocol(), url.getHost(), url.getPort());
    }
    
    public HTTPConnection(final URI uri) throws ProtocolNotSuppException {
        this(uri.getScheme(), uri.getHost(), uri.getPort());
    }
    
    private void Setup(final int prot, final String host, final int port, final InetAddress localAddr, final int localPort) {
        this.Protocol = prot;
        this.Host = host.trim().toLowerCase();
        this.Port = port;
        this.LocalAddr = localAddr;
        this.LocalPort = localPort;
        if (this.Port == -1) {
            this.Port = URI.defaultPort(this.getProtocol());
        }
        if (HTTPConnection.Default_Proxy_Host != null && !this.matchNonProxy(this.Host)) {
            this.setCurrentProxy(HTTPConnection.Default_Proxy_Host, HTTPConnection.Default_Proxy_Port);
        }
        else {
            this.setCurrentProxy(null, 0);
        }
        this.Socks_client = HTTPConnection.Default_Socks_client;
        this.Timeout = HTTPConnection.DefaultTimeout;
        this.ModuleList = (Vector)HTTPConnection.DefaultModuleList.clone();
        this.allowUI = HTTPConnection.defaultAllowUI;
        if (HTTPConnection.noKeepAlives) {
            this.setDefaultHeaders(new NVPair[] { new NVPair("Connection", "close") });
        }
        if (prot == 1 && HTTPConnection.defaultSSLFactory == null) {
            HTTPConnection.defaultSSLFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        }
        this.sslFactory = HTTPConnection.defaultSSLFactory;
    }
    
    private boolean matchNonProxy(final String host) {
        if (HTTPConnection.non_proxy_host_list.get(host) != null) {
            return true;
        }
        for (int idx = 0; idx < HTTPConnection.non_proxy_dom_list.size(); ++idx) {
            if (host.endsWith(HTTPConnection.non_proxy_dom_list.elementAt(idx))) {
                return true;
            }
        }
        if (HTTPConnection.non_proxy_addr_list.size() == 0) {
            return false;
        }
        InetAddress[] host_addr;
        try {
            host_addr = InetAddress.getAllByName(host);
        }
        catch (final UnknownHostException uhe) {
            return false;
        }
        for (int idx2 = 0; idx2 < HTTPConnection.non_proxy_addr_list.size(); ++idx2) {
            final byte[] addr = HTTPConnection.non_proxy_addr_list.elementAt(idx2);
            final byte[] mask = HTTPConnection.non_proxy_mask_list.elementAt(idx2);
        Label_0191:
            for (int idx3 = 0; idx3 < host_addr.length; ++idx3) {
                final byte[] raw_addr = host_addr[idx3].getAddress();
                if (raw_addr.length == addr.length) {
                    for (int idx4 = 0; idx4 < raw_addr.length; ++idx4) {
                        if ((raw_addr[idx4] & mask[idx4]) != (addr[idx4] & mask[idx4])) {
                            continue Label_0191;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public HTTPResponse Head(final String file) throws IOException, ModuleException {
        return this.Head(file, (String)null, null);
    }
    
    public HTTPResponse Head(final String file, final NVPair[] form_data) throws IOException, ModuleException {
        return this.Head(file, form_data, null);
    }
    
    public HTTPResponse Head(final String file, final NVPair[] form_data, final NVPair[] headers) throws IOException, ModuleException {
        String File = this.stripRef(file);
        final String query = Codecs.nv2query(form_data);
        if (query != null && query.length() > 0) {
            File = File + "?" + query;
        }
        return this.setupRequest("HEAD", File, headers, null, null);
    }
    
    public HTTPResponse Head(final String file, final String query) throws IOException, ModuleException {
        return this.Head(file, query, null);
    }
    
    public HTTPResponse Head(final String file, final String query, final NVPair[] headers) throws IOException, ModuleException {
        String File = this.stripRef(file);
        if (query != null && query.length() > 0) {
            File = File + "?" + Codecs.URLEncode(query);
        }
        return this.setupRequest("HEAD", File, headers, null, null);
    }
    
    public HTTPResponse Get(final String file) throws IOException, ModuleException {
        return this.Get(file, (String)null, null);
    }
    
    public HTTPResponse Get(final String file, final NVPair[] form_data) throws IOException, ModuleException {
        return this.Get(file, form_data, null);
    }
    
    public HTTPResponse Get(final String file, final NVPair[] form_data, final NVPair[] headers) throws IOException, ModuleException {
        String File = this.stripRef(file);
        final String query = Codecs.nv2query(form_data);
        if (query != null && query.length() > 0) {
            File = File + "?" + query;
        }
        return this.setupRequest("GET", File, headers, null, null);
    }
    
    public HTTPResponse Get(final String file, final String query) throws IOException, ModuleException {
        return this.Get(file, query, null);
    }
    
    public HTTPResponse Get(final String file, final String query, final NVPair[] headers) throws IOException, ModuleException {
        String File = this.stripRef(file);
        if (query != null && query.length() > 0) {
            File = File + "?" + Codecs.URLEncode(query);
        }
        return this.setupRequest("GET", File, headers, null, null);
    }
    
    public HTTPResponse Post(final String file) throws IOException, ModuleException {
        return this.Post(file, (byte[])null, null);
    }
    
    public HTTPResponse Post(final String file, final NVPair[] form_data) throws IOException, ModuleException {
        final NVPair[] headers = { new NVPair("Content-type", "application/x-www-form-urlencoded") };
        return this.Post(file, Codecs.nv2query(form_data), headers);
    }
    
    public HTTPResponse Post(final String file, final NVPair[] form_data, NVPair[] headers) throws IOException, ModuleException {
        int idx;
        for (idx = 0; idx < headers.length && !headers[idx].getName().equalsIgnoreCase("Content-type"); ++idx) {}
        if (idx == headers.length) {
            headers = Util.resizeArray(headers, idx + 1);
            headers[idx] = new NVPair("Content-type", "application/x-www-form-urlencoded");
        }
        return this.Post(file, Codecs.nv2query(form_data), headers);
    }
    
    public HTTPResponse Post(final String file, final String data) throws IOException, ModuleException {
        return this.Post(file, data, null);
    }
    
    public HTTPResponse Post(final String file, final String data, final NVPair[] headers) throws IOException, ModuleException {
        byte[] tmp = null;
        if (data != null && data.length() > 0) {
            tmp = data.getBytes();
        }
        return this.Post(file, tmp, headers);
    }
    
    public HTTPResponse Post(final String file, final byte[] data) throws IOException, ModuleException {
        return this.Post(file, data, null);
    }
    
    public HTTPResponse Post(final String file, byte[] data, final NVPair[] headers) throws IOException, ModuleException {
        if (data == null) {
            data = new byte[0];
        }
        return this.setupRequest("POST", this.stripRef(file), headers, data, null);
    }
    
    public HTTPResponse Post(final String file, final HttpOutputStream stream) throws IOException, ModuleException {
        return this.Post(file, stream, null);
    }
    
    public HTTPResponse Post(final String file, final HttpOutputStream stream, final NVPair[] headers) throws IOException, ModuleException {
        return this.setupRequest("POST", this.stripRef(file), headers, null, stream);
    }
    
    public HTTPResponse Put(final String file, final String data) throws IOException, ModuleException {
        return this.Put(file, data, null);
    }
    
    public HTTPResponse Put(final String file, final String data, final NVPair[] headers) throws IOException, ModuleException {
        byte[] tmp = null;
        if (data != null && data.length() > 0) {
            tmp = data.getBytes();
        }
        return this.Put(file, tmp, headers);
    }
    
    public HTTPResponse Put(final String file, final byte[] data) throws IOException, ModuleException {
        return this.Put(file, data, null);
    }
    
    public HTTPResponse Put(final String file, byte[] data, final NVPair[] headers) throws IOException, ModuleException {
        if (data == null) {
            data = new byte[0];
        }
        return this.setupRequest("PUT", this.stripRef(file), headers, data, null);
    }
    
    public HTTPResponse Put(final String file, final HttpOutputStream stream) throws IOException, ModuleException {
        return this.Put(file, stream, null);
    }
    
    public HTTPResponse Put(final String file, final HttpOutputStream stream, final NVPair[] headers) throws IOException, ModuleException {
        return this.setupRequest("PUT", this.stripRef(file), headers, null, stream);
    }
    
    public HTTPResponse Options(final String file) throws IOException, ModuleException {
        return this.Options(file, null, (byte[])null);
    }
    
    public HTTPResponse Options(final String file, final NVPair[] headers) throws IOException, ModuleException {
        return this.Options(file, headers, (byte[])null);
    }
    
    public HTTPResponse Options(final String file, final NVPair[] headers, final byte[] data) throws IOException, ModuleException {
        return this.setupRequest("OPTIONS", this.stripRef(file), headers, data, null);
    }
    
    public HTTPResponse Options(final String file, final NVPair[] headers, final HttpOutputStream stream) throws IOException, ModuleException {
        return this.setupRequest("OPTIONS", this.stripRef(file), headers, null, stream);
    }
    
    public HTTPResponse Delete(final String file) throws IOException, ModuleException {
        return this.Delete(file, null);
    }
    
    public HTTPResponse Delete(final String file, final NVPair[] headers) throws IOException, ModuleException {
        return this.setupRequest("DELETE", this.stripRef(file), headers, null, null);
    }
    
    public HTTPResponse Trace(final String file, final NVPair[] headers) throws IOException, ModuleException {
        return this.setupRequest("TRACE", this.stripRef(file), headers, null, null);
    }
    
    public HTTPResponse Trace(final String file) throws IOException, ModuleException {
        return this.Trace(file, null);
    }
    
    public HTTPResponse ExtensionMethod(final String method, final String file, final byte[] data, final NVPair[] headers) throws IOException, ModuleException {
        return this.setupRequest(method.trim(), this.stripRef(file), headers, data, null);
    }
    
    public HTTPResponse ExtensionMethod(final String method, final String file, final HttpOutputStream os, final NVPair[] headers) throws IOException, ModuleException {
        return this.setupRequest(method.trim(), this.stripRef(file), headers, null, os);
    }
    
    public void stop() {
        for (Request req = (Request)this.RequestList.enumerate(); req != null; req = (Request)this.RequestList.next()) {
            req.aborted = true;
        }
        for (StreamDemultiplexor demux = (StreamDemultiplexor)this.DemuxList.enumerate(); demux != null; demux = (StreamDemultiplexor)this.DemuxList.next()) {
            demux.abort();
        }
    }
    
    public static void setDefaultSSLSocketFactory(final SSLSocketFactory sslFactory) {
        HTTPConnection.defaultSSLFactory = sslFactory;
    }
    
    public static SSLSocketFactory getDefaultSSLSocketFactory() {
        if (HTTPConnection.defaultSSLFactory == null) {
            HTTPConnection.defaultSSLFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        }
        return HTTPConnection.defaultSSLFactory;
    }
    
    public void setSSLSocketFactory(final SSLSocketFactory sslFactory) {
        this.sslFactory = sslFactory;
    }
    
    public SSLSocketFactory getSSLSocketFactory() {
        return this.sslFactory;
    }
    
    public void setDefaultHeaders(final NVPair[] headers) {
        final int length = (headers == null) ? 0 : headers.length;
        NVPair[] def_hdrs = new NVPair[length];
        int sidx = 0;
        int didx = 0;
        while (sidx < length) {
            if (headers[sidx] != null) {
                final String name = headers[sidx].getName().trim();
                if (!name.equalsIgnoreCase("Content-length")) {
                    def_hdrs[didx++] = headers[sidx];
                }
            }
            ++sidx;
        }
        if (didx < length) {
            def_hdrs = Util.resizeArray(def_hdrs, didx);
        }
        synchronized (this.DefaultHeaders) {
            this.DefaultHeaders = def_hdrs;
        }
    }
    
    public NVPair[] getDefaultHeaders() {
        synchronized (this.DefaultHeaders) {
            return this.DefaultHeaders.clone();
        }
    }
    
    public String getProtocol() {
        switch (this.Protocol) {
            case 0: {
                return "http";
            }
            case 1: {
                return "https";
            }
            case 2: {
                return "shttp";
            }
            case 3: {
                return "http-ng";
            }
            default: {
                throw new Error("HTTPClient Internal Error: invalid protocol " + this.Protocol);
            }
        }
    }
    
    public String getHost() {
        return this.Host;
    }
    
    public int getPort() {
        return this.Port;
    }
    
    public String getProxyHost() {
        return this.Proxy_Host;
    }
    
    public int getProxyPort() {
        return this.Proxy_Port;
    }
    
    public boolean isCompatibleWith(final URI uri) {
        if (!uri.getScheme().equals(this.getProtocol()) || !uri.getHost().equalsIgnoreCase(this.Host)) {
            return false;
        }
        int port = uri.getPort();
        if (port == -1) {
            port = URI.defaultPort(uri.getScheme());
        }
        return port == this.Port;
    }
    
    @Deprecated
    public void setRawMode(final boolean raw) {
        final String[] modules = { "HTTPClient.CookieModule", "HTTPClient.RedirectionModule", "HTTPClient.AuthorizationModule", "HTTPClient.DefaultModule", "HTTPClient.TransferEncodingModule", "HTTPClient.ContentMD5Module", "HTTPClient.ContentEncodingModule" };
        for (int idx = 0; idx < modules.length; ++idx) {
            try {
                if (raw) {
                    this.removeModule(Class.forName(modules[idx]));
                }
                else {
                    this.addModule(Class.forName(modules[idx]), -1);
                }
            }
            catch (final ClassNotFoundException ex) {}
        }
    }
    
    public static void setDefaultTimeout(final int time) {
        HTTPConnection.DefaultTimeout = time;
    }
    
    public static int getDefaultTimeout() {
        return HTTPConnection.DefaultTimeout;
    }
    
    public void setTimeout(final int time) {
        this.Timeout = time;
    }
    
    public int getTimeout() {
        return this.Timeout;
    }
    
    public void setAllowUserInteraction(final boolean allow) {
        this.allowUI = allow;
    }
    
    public boolean getAllowUserInteraction() {
        return this.allowUI;
    }
    
    public static void setDefaultAllowUserInteraction(final boolean allow) {
        HTTPConnection.defaultAllowUI = allow;
    }
    
    public static boolean getDefaultAllowUserInteraction() {
        return HTTPConnection.defaultAllowUI;
    }
    
    public static Class[] getDefaultModules() {
        return getModules(HTTPConnection.DefaultModuleList);
    }
    
    public static boolean addDefaultModule(final Class module, final int pos) {
        return addModule(HTTPConnection.DefaultModuleList, module, pos);
    }
    
    public static boolean removeDefaultModule(final Class module) {
        return removeModule(HTTPConnection.DefaultModuleList, module);
    }
    
    public Class[] getModules() {
        return getModules(this.ModuleList);
    }
    
    public boolean addModule(final Class module, final int pos) {
        return addModule(this.ModuleList, module, pos);
    }
    
    public boolean removeModule(final Class module) {
        return removeModule(this.ModuleList, module);
    }
    
    private static final Class[] getModules(final Vector list) {
        synchronized (list) {
            final Class[] modules = new Class[list.size()];
            list.copyInto(modules);
            return modules;
        }
    }
    
    private static final boolean addModule(final Vector list, final Class module, final int pos) {
        if (module == null) {
            return false;
        }
        try {
            final HTTPClientModule httpClientModule = module.newInstance();
        }
        catch (final RuntimeException re) {
            throw re;
        }
        catch (final Exception e) {
            throw new RuntimeException(e.toString());
        }
        synchronized (list) {
            if (list.contains(module)) {
                return false;
            }
            if (pos < 0) {
                list.insertElementAt(module, HTTPConnection.DefaultModuleList.size() + pos + 1);
            }
            else {
                list.insertElementAt(module, pos);
            }
        }
        Log.write(1, "Conn:  Added module " + module.getName() + " to " + ((list == HTTPConnection.DefaultModuleList) ? "default " : "") + "list");
        return true;
    }
    
    private static final boolean removeModule(final Vector list, final Class module) {
        if (module == null) {
            return false;
        }
        final boolean removed = list.removeElement(module);
        if (removed) {
            Log.write(1, "Conn:  Removed module " + module.getName() + " from " + ((list == HTTPConnection.DefaultModuleList) ? "default " : "") + "list");
        }
        return removed;
    }
    
    public void setContext(final Object context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must be non-null");
        }
        if (this.Context != null) {
            throw new IllegalStateException("Context already set");
        }
        this.Context = context;
    }
    
    public Object getContext() {
        if (this.Context != null) {
            return this.Context;
        }
        return HTTPConnection.dflt_context;
    }
    
    public static Object getDefaultContext() {
        return HTTPConnection.dflt_context;
    }
    
    public void addDigestAuthorization(final String realm, final String user, final String passwd) {
        AuthorizationInfo.addDigestAuthorization(this.Host, this.Port, realm, user, passwd, this.getContext());
    }
    
    public void addBasicAuthorization(final String realm, final String user, final String passwd) {
        AuthorizationInfo.addBasicAuthorization(this.Host, this.Port, realm, user, passwd, this.getContext());
    }
    
    public static void setProxyServer(final String host, final int port) {
        if (host == null || host.trim().length() == 0) {
            HTTPConnection.Default_Proxy_Host = null;
        }
        else {
            HTTPConnection.Default_Proxy_Host = host.trim().toLowerCase();
            HTTPConnection.Default_Proxy_Port = port;
        }
    }
    
    public synchronized void setCurrentProxy(final String host, final int port) {
        if (host == null || host.trim().length() == 0) {
            this.Proxy_Host = null;
        }
        else {
            this.Proxy_Host = host.trim().toLowerCase();
            if (port <= 0) {
                this.Proxy_Port = 80;
            }
            else {
                this.Proxy_Port = port;
            }
        }
        switch (this.Protocol) {
            case 0:
            case 1: {
                if (HTTPConnection.force_1_0) {
                    this.ServerProtocolVersion = 65536;
                    this.ServProtVersKnown = true;
                    this.RequestProtocolVersion = "HTTP/1.0";
                    break;
                }
                this.ServerProtocolVersion = 65537;
                this.ServProtVersKnown = false;
                this.RequestProtocolVersion = "HTTP/1.1";
                break;
            }
            case 3: {
                this.ServerProtocolVersion = -1;
                this.ServProtVersKnown = false;
                this.RequestProtocolVersion = "";
                break;
            }
            case 2: {
                this.ServerProtocolVersion = -1;
                this.ServProtVersKnown = false;
                this.RequestProtocolVersion = "Secure-HTTP/1.3";
                break;
            }
            default: {
                throw new Error("HTTPClient Internal Error: invalid protocol " + this.Protocol);
            }
        }
        this.keepAliveUnknown = true;
        this.doesKeepAlive = false;
        this.input_demux = null;
        this.early_stall = null;
        this.late_stall = null;
        this.prev_resp = null;
    }
    
    public static void dontProxyFor(String host) throws ParseException {
        host = host.trim().toLowerCase();
        if (host.charAt(0) == '.') {
            if (!HTTPConnection.non_proxy_dom_list.contains(host)) {
                HTTPConnection.non_proxy_dom_list.addElement(host);
            }
            return;
        }
        for (int idx = 0; idx < host.length(); ++idx) {
            if (!Character.isDigit(host.charAt(idx)) && host.charAt(idx) != '.' && host.charAt(idx) != '/') {
                HTTPConnection.non_proxy_host_list.put(host, "");
                return;
            }
        }
        final int slash;
        byte[] ip_addr;
        byte[] ip_mask;
        if ((slash = host.indexOf(47)) != -1) {
            ip_addr = string2arr(host.substring(0, slash));
            ip_mask = string2arr(host.substring(slash + 1));
            if (ip_addr.length != ip_mask.length) {
                throw new ParseException("length of IP-address (" + ip_addr.length + ") != length of netmask (" + ip_mask.length + ")");
            }
        }
        else {
            ip_addr = string2arr(host);
            ip_mask = new byte[ip_addr.length];
            for (int idx2 = 0; idx2 < ip_mask.length; ++idx2) {
                ip_mask[idx2] = -1;
            }
        }
    Label_0322:
        for (int idx2 = 0; idx2 < HTTPConnection.non_proxy_addr_list.size(); ++idx2) {
            final byte[] addr = HTTPConnection.non_proxy_addr_list.elementAt(idx2);
            final byte[] mask = HTTPConnection.non_proxy_mask_list.elementAt(idx2);
            if (addr.length == ip_addr.length) {
                for (int idx3 = 0; idx3 < addr.length; ++idx3) {
                    if ((ip_addr[idx3] & mask[idx3]) != (addr[idx3] & mask[idx3])) {
                        continue Label_0322;
                    }
                    if (mask[idx3] != ip_mask[idx3]) {
                        continue Label_0322;
                    }
                }
                return;
            }
        }
        HTTPConnection.non_proxy_addr_list.addElement(ip_addr);
        HTTPConnection.non_proxy_mask_list.addElement(ip_mask);
    }
    
    public static void dontProxyFor(final String[] hosts) {
        if (hosts == null || hosts.length == 0) {
            return;
        }
        for (int idx = 0; idx < hosts.length; ++idx) {
            try {
                if (hosts[idx] != null) {
                    dontProxyFor(hosts[idx]);
                }
            }
            catch (final ParseException ex) {}
        }
    }
    
    public static boolean doProxyFor(String host) throws ParseException {
        host = host.trim().toLowerCase();
        if (host.charAt(0) == '.') {
            return HTTPConnection.non_proxy_dom_list.removeElement(host);
        }
        for (int idx = 0; idx < host.length(); ++idx) {
            if (!Character.isDigit(host.charAt(idx)) && host.charAt(idx) != '.' && host.charAt(idx) != '/') {
                return HTTPConnection.non_proxy_host_list.remove(host) != null;
            }
        }
        final int slash;
        byte[] ip_addr;
        byte[] ip_mask;
        if ((slash = host.indexOf(47)) != -1) {
            ip_addr = string2arr(host.substring(0, slash));
            ip_mask = string2arr(host.substring(slash + 1));
            if (ip_addr.length != ip_mask.length) {
                throw new ParseException("length of IP-address (" + ip_addr.length + ") != length of netmask (" + ip_mask.length + ")");
            }
        }
        else {
            ip_addr = string2arr(host);
            ip_mask = new byte[ip_addr.length];
            for (int idx2 = 0; idx2 < ip_mask.length; ++idx2) {
                ip_mask[idx2] = -1;
            }
        }
    Label_0334:
        for (int idx2 = 0; idx2 < HTTPConnection.non_proxy_addr_list.size(); ++idx2) {
            final byte[] addr = HTTPConnection.non_proxy_addr_list.elementAt(idx2);
            final byte[] mask = HTTPConnection.non_proxy_mask_list.elementAt(idx2);
            if (addr.length == ip_addr.length) {
                for (int idx3 = 0; idx3 < addr.length; ++idx3) {
                    if ((ip_addr[idx3] & mask[idx3]) != (addr[idx3] & mask[idx3])) {
                        continue Label_0334;
                    }
                    if (mask[idx3] != ip_mask[idx3]) {
                        continue Label_0334;
                    }
                }
                HTTPConnection.non_proxy_addr_list.removeElementAt(idx2);
                HTTPConnection.non_proxy_mask_list.removeElementAt(idx2);
                return true;
            }
        }
        return false;
    }
    
    private static byte[] string2arr(final String ip) {
        final char[] ip_char = new char[ip.length()];
        ip.getChars(0, ip_char.length, ip_char, 0);
        int cnt = 0;
        for (int idx = 0; idx < ip_char.length; ++idx) {
            if (ip_char[idx] == '.') {
                ++cnt;
            }
        }
        final byte[] arr = new byte[cnt + 1];
        cnt = 0;
        int pos = 0;
        for (int idx2 = 0; idx2 < ip_char.length; ++idx2) {
            if (ip_char[idx2] == '.') {
                arr[cnt] = (byte)Integer.parseInt(ip.substring(pos, idx2));
                ++cnt;
                pos = idx2 + 1;
            }
        }
        arr[cnt] = (byte)Integer.parseInt(ip.substring(pos));
        return arr;
    }
    
    public static void setSocksServer(final String host) {
        setSocksServer(host, 1080);
    }
    
    public static void setSocksServer(final String host, int port) {
        if (port <= 0) {
            port = 1080;
        }
        if (host == null || host.length() == 0) {
            HTTPConnection.Default_Socks_client = null;
        }
        else {
            HTTPConnection.Default_Socks_client = new SocksClient(host, port);
        }
    }
    
    public static void setSocksServer(final String host, int port, final int version) throws SocksException {
        if (port <= 0) {
            port = 1080;
        }
        if (host == null || host.length() == 0) {
            HTTPConnection.Default_Socks_client = null;
        }
        else {
            HTTPConnection.Default_Socks_client = new SocksClient(host, port, version);
        }
    }
    
    private final String stripRef(String file) {
        if (file == null) {
            return "";
        }
        final int hash = file.indexOf(35);
        if (hash != -1) {
            file = file.substring(0, hash);
        }
        return file.trim();
    }
    
    protected final HTTPResponse setupRequest(final String method, final String resource, final NVPair[] headers, final byte[] entity, final HttpOutputStream stream) throws IOException, ModuleException {
        final Request req = new Request(this, method, resource, this.mergedHeaders(headers), entity, stream, this.allowUI);
        this.RequestList.addToEnd(req);
        try {
            final HTTPResponse resp = new HTTPResponse(this.gen_mod_insts(), this.Timeout, req);
            this.handleRequest(req, resp, null, true);
            return resp;
        }
        finally {
            this.RequestList.remove(req);
        }
    }
    
    private NVPair[] mergedHeaders(final NVPair[] spec) {
        final int spec_len = (spec != null) ? spec.length : 0;
        final int defs_len;
        NVPair[] merged;
        synchronized (this.DefaultHeaders) {
            defs_len = ((this.DefaultHeaders != null) ? this.DefaultHeaders.length : 0);
            merged = new NVPair[spec_len + defs_len];
            System.arraycopy(this.DefaultHeaders, 0, merged, 0, defs_len);
        }
        int didx = defs_len;
        for (int sidx = 0; sidx < spec_len; ++sidx) {
            if (spec[sidx] != null) {
                final String s_name = spec[sidx].getName().trim();
                if (!s_name.equalsIgnoreCase("Content-length")) {
                    int search;
                    for (search = 0; search < didx && !merged[search].getName().trim().equalsIgnoreCase(s_name); ++search) {}
                    merged[search] = spec[sidx];
                    if (search == didx) {
                        ++didx;
                    }
                }
            }
        }
        if (didx < merged.length) {
            merged = Util.resizeArray(merged, didx);
        }
        return merged;
    }
    
    private HTTPClientModule[] gen_mod_insts() {
        synchronized (this.ModuleList) {
            final HTTPClientModule[] mod_insts = new HTTPClientModule[this.ModuleList.size()];
            for (int idx = 0; idx < this.ModuleList.size(); ++idx) {
                final Class mod = this.ModuleList.elementAt(idx);
                try {
                    mod_insts[idx] = mod.newInstance();
                }
                catch (final Exception e) {
                    throw new Error("HTTPClient Internal Error: could not create instance of " + mod.getName() + " -\n" + e);
                }
            }
            return mod_insts;
        }
    }
    
    void handleRequest(final Request req, final HTTPResponse http_resp, final Response resp, final boolean usemodules) throws IOException, ModuleException {
        final Response[] rsp_arr = { resp };
        final HTTPClientModule[] modules = http_resp.getModules();
        Label_0302: {
            if (usemodules) {
                for (int idx = 0; idx < modules.length; ++idx) {
                    final int sts = modules[idx].requestHandler(req, rsp_arr);
                    switch (sts) {
                        case 0: {
                            break;
                        }
                        case 1: {
                            idx = -1;
                            break;
                        }
                        case 2: {
                            break Label_0302;
                        }
                        case 3:
                        case 4: {
                            if (rsp_arr[0] == null) {
                                throw new Error("HTTPClient Internal Error: no response returned by module " + modules[idx].getClass().getName());
                            }
                            http_resp.set(req, rsp_arr[0]);
                            if (req.getStream() != null) {
                                req.getStream().ignoreData(req);
                            }
                            if (req.internal_subrequest) {
                                return;
                            }
                            if (sts == 3) {
                                http_resp.handleResponse();
                            }
                            else {
                                http_resp.init(rsp_arr[0]);
                            }
                            return;
                        }
                        case 5: {
                            if (req.internal_subrequest) {
                                return;
                            }
                            req.getConnection().handleRequest(req, http_resp, rsp_arr[0], true);
                            return;
                        }
                        case 6: {
                            if (req.internal_subrequest) {
                                return;
                            }
                            req.getConnection().handleRequest(req, http_resp, rsp_arr[0], false);
                            return;
                        }
                        default: {
                            throw new Error("HTTPClient Internal Error: invalid status " + sts + " returned by module " + modules[idx].getClass().getName());
                        }
                    }
                }
            }
        }
        if (req.internal_subrequest) {
            return;
        }
        if (req.getStream() != null && req.getStream().getLength() == -1) {
            if (!this.ServProtVersKnown || this.ServerProtocolVersion < 65537 || HTTPConnection.no_chunked) {
                req.getStream().goAhead(req, null, http_resp.getTimeout());
                http_resp.set(req, req.getStream());
            }
            else {
                int idx;
                NVPair[] hdrs;
                for (hdrs = req.getHeaders(), idx = 0; idx < hdrs.length && !hdrs[idx].getName().equalsIgnoreCase("Transfer-Encoding"); ++idx) {}
                if (idx == hdrs.length) {
                    hdrs = Util.resizeArray(hdrs, idx + 1);
                    hdrs[idx] = new NVPair("Transfer-Encoding", "chunked");
                    req.setHeaders(hdrs);
                }
                else {
                    final String v = hdrs[idx].getValue();
                    try {
                        if (!Util.hasToken(v, "chunked")) {
                            hdrs[idx] = new NVPair("Transfer-Encoding", v + ", chunked");
                        }
                    }
                    catch (final ParseException pe) {
                        throw new IOException(pe.toString());
                    }
                }
                http_resp.set(req, this.sendRequest(req, http_resp.getTimeout()));
            }
        }
        else {
            http_resp.set(req, this.sendRequest(req, http_resp.getTimeout()));
        }
        if (req.aborted) {
            throw new IOException("Request aborted by user");
        }
    }
    
    Response sendRequest(final Request req, final int con_timeout) throws IOException, ModuleException {
        final ByteArrayOutputStream hdr_buf = new ByteArrayOutputStream(600);
        Response resp = null;
        if (this.early_stall != null) {
            try {
                Log.write(1, "Conn:  Early-stalling Request: " + req.getMethod() + " " + req.getRequestURI());
                synchronized (this.early_stall) {
                    try {
                        this.early_stall.getVersion();
                    }
                    catch (final IOException ex) {}
                    this.early_stall = null;
                }
            }
            catch (final NullPointerException ex2) {}
        }
        final String[] con_hdrs = this.assembleHeaders(req, hdr_buf);
        boolean keep_alive;
        try {
            keep_alive = ((this.ServerProtocolVersion >= 65537 && !Util.hasToken(con_hdrs[0], "close")) || (this.ServerProtocolVersion == 65536 && Util.hasToken(con_hdrs[0], "keep-alive")));
        }
        catch (final ParseException pe) {
            throw new IOException(pe.toString());
        }
        synchronized (this) {
            if (this.late_stall != null) {
                if (this.input_demux != null || this.keepAliveUnknown) {
                    Log.write(1, "Conn:  Stalling Request: " + req.getMethod() + " " + req.getRequestURI());
                    try {
                        this.late_stall.getVersion();
                        if (this.keepAliveUnknown) {
                            this.determineKeepAlive(this.late_stall);
                        }
                    }
                    catch (final IOException ex3) {}
                }
                this.late_stall = null;
            }
            if ((req.getMethod().equals("POST") || req.dont_pipeline) && this.prev_resp != null && this.input_demux != null) {
                Log.write(1, "Conn:  Stalling Request: " + req.getMethod() + " " + req.getRequestURI());
                try {
                    this.prev_resp.getVersion();
                }
                catch (final IOException ex4) {}
            }
            if (!this.output_finished) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ie) {
                    throw new IOException(ie.toString());
                }
            }
            if (req.aborted) {
                throw new IOException("Request aborted by user");
            }
            int try_count = 3;
            while (try_count-- > 0) {
                try {
                    Socket sock;
                    if (this.input_demux == null || (sock = this.input_demux.getSocket()) == null) {
                        sock = this.getSocket(con_timeout);
                        if (this.Protocol == 1) {
                            if (this.Proxy_Host != null) {
                                final Socket[] sarr = { sock };
                                resp = this.enableSSLTunneling(sarr, req, con_timeout);
                                if (resp != null) {
                                    resp.final_resp = true;
                                    return resp;
                                }
                                sock = sarr[0];
                            }
                            sock.setSoTimeout(con_timeout);
                            sock = this.sslFactory.createSocket(sock, this.Host, this.Port, true);
                        }
                        this.input_demux = new StreamDemultiplexor(this.Protocol, sock, this);
                        this.DemuxList.addToEnd(this.input_demux);
                        this.keepAliveReqLeft = this.keepAliveReqMax;
                    }
                    if (req.aborted) {
                        throw new IOException("Request aborted by user");
                    }
                    Log.write(1, "Conn:  Sending Request: ", hdr_buf);
                    OutputStream sock_out = sock.getOutputStream();
                    if (HTTPConnection.haveMSLargeWritesBug) {
                        sock_out = new MSLargeWritesBugStream(sock_out);
                    }
                    hdr_buf.writeTo(sock_out);
                    try {
                        if (this.ServProtVersKnown && this.ServerProtocolVersion >= 65537 && Util.hasToken(con_hdrs[1], "100-continue")) {
                            resp = new Response(req, this.Proxy_Host != null && this.Protocol != 1, this.input_demux);
                            resp.timeout = 60;
                            if (resp.getContinue() != 100) {
                                break;
                            }
                        }
                    }
                    catch (final ParseException pe2) {
                        throw new IOException(pe2.toString());
                    }
                    catch (final InterruptedIOException ex5) {}
                    finally {
                        if (resp != null) {
                            resp.timeout = 0;
                        }
                    }
                    if (req.getData() != null && req.getData().length > 0) {
                        if (req.delay_entity > 0L) {
                            final long num_units = req.delay_entity / 100L;
                            final long one_unit = req.delay_entity / num_units;
                            for (int idx = 0; idx < num_units && this.input_demux.available(null) == 0; ++idx) {
                                try {
                                    Thread.sleep(one_unit);
                                }
                                catch (final InterruptedException ex6) {}
                            }
                            if (this.input_demux.available(null) == 0) {
                                sock_out.write(req.getData());
                            }
                            else {
                                keep_alive = false;
                            }
                        }
                        else {
                            sock_out.write(req.getData());
                        }
                    }
                    if (req.getStream() != null) {
                        req.getStream().goAhead(req, sock_out, 0);
                    }
                    else {
                        sock_out.flush();
                    }
                    if (resp == null) {
                        resp = new Response(req, this.Proxy_Host != null && this.Protocol != 1, this.input_demux);
                    }
                }
                catch (final IOException ioe) {
                    Log.write(1, "Conn:  ", ioe);
                    this.closeDemux(ioe, true);
                    if (try_count == 0 || ioe instanceof UnknownHostException || ioe instanceof ConnectException || ioe instanceof NoRouteToHostException || ioe instanceof InterruptedIOException || req.aborted) {
                        throw ioe;
                    }
                    Log.write(1, "Conn:  Retrying request");
                    continue;
                }
                break;
            }
            this.prev_resp = resp;
            if ((!this.keepAliveUnknown && !this.doesKeepAlive) || !keep_alive || (this.keepAliveReqMax != -1 && this.keepAliveReqLeft-- == 0)) {
                this.input_demux.markForClose(resp);
                this.input_demux = null;
            }
            else {
                this.input_demux.restartTimer();
            }
            if (this.keepAliveReqMax != -1) {
                Log.write(1, "Conn:  Number of requests left: " + this.keepAliveReqLeft);
            }
            if (!this.ServProtVersKnown) {
                (this.early_stall = resp).markAsFirstResponse(req);
            }
            if (this.keepAliveUnknown || !IdempotentSequence.methodIsIdempotent(req.getMethod()) || req.dont_pipeline || HTTPConnection.neverPipeline) {
                this.late_stall = resp;
            }
            if (req.getStream() != null) {
                this.output_finished = false;
            }
            else {
                this.output_finished = true;
                this.notify();
            }
            Log.write(1, "Conn:  Request sent");
        }
        return resp;
    }
    
    private Socket getSocket(final int con_timeout) throws IOException {
        Socket sock = null;
        String actual_host;
        int actual_port;
        if (this.Proxy_Host != null) {
            actual_host = this.Proxy_Host;
            actual_port = this.Proxy_Port;
        }
        else {
            actual_host = this.Host;
            actual_port = this.Port;
        }
        Log.write(1, "Conn:  Creating Socket: " + actual_host + ":" + actual_port);
        if (con_timeout == 0) {
            if (this.Socks_client != null) {
                sock = this.Socks_client.getSocket(actual_host, actual_port);
            }
            else {
                final InetAddress[] addr_list = InetAddress.getAllByName(actual_host);
                int idx = 0;
                while (idx < addr_list.length) {
                    try {
                        if (this.LocalAddr == null) {
                            sock = new Socket(addr_list[idx], actual_port);
                        }
                        else {
                            sock = new Socket(addr_list[idx], actual_port, this.LocalAddr, this.LocalPort);
                        }
                    }
                    catch (final SocketException se) {
                        if (idx == addr_list.length - 1) {
                            throw se;
                        }
                        ++idx;
                        continue;
                    }
                    break;
                }
            }
        }
        else {
            final EstablishConnection con = new EstablishConnection(actual_host, actual_port, this.Socks_client);
            con.start();
            try {
                con.join(con_timeout);
            }
            catch (final InterruptedException ex) {}
            if (con.getException() != null) {
                throw con.getException();
            }
            if ((sock = con.getSocket()) == null) {
                con.forget();
                if ((sock = con.getSocket()) == null) {
                    throw new InterruptedIOException("Connection establishment timed out");
                }
            }
        }
        return sock;
    }
    
    private Response enableSSLTunneling(final Socket[] sock, final Request req, final int timeout) throws IOException, ModuleException {
        final Vector hdrs = new Vector();
        for (int idx = 0; idx < req.getHeaders().length; ++idx) {
            final String name = req.getHeaders()[idx].getName();
            if (name.equalsIgnoreCase("User-Agent") || name.equalsIgnoreCase("Proxy-Authorization")) {
                hdrs.addElement(req.getHeaders()[idx]);
            }
        }
        final NVPair[] h = new NVPair[hdrs.size()];
        hdrs.copyInto(h);
        final Request connect = new Request(this, "CONNECT", this.Host + ":" + this.Port, h, null, null, req.allowUI());
        connect.internal_subrequest = true;
        final ByteArrayOutputStream hdr_buf = new ByteArrayOutputStream(600);
        final HTTPResponse r = new HTTPResponse(this.gen_mod_insts(), timeout, connect);
        Response resp = null;
        while (true) {
            this.handleRequest(connect, r, resp, true);
            hdr_buf.reset();
            this.assembleHeaders(connect, hdr_buf);
            Log.write(1, "Conn:  Sending SSL-Tunneling Subrequest: ", hdr_buf);
            hdr_buf.writeTo(sock[0].getOutputStream());
            resp = new Response(connect, sock[0].getInputStream());
            if (resp.getStatusCode() == 200) {
                return null;
            }
            try {
                resp.getData();
            }
            catch (final IOException ex) {}
            try {
                sock[0].close();
            }
            catch (final IOException ex2) {}
            r.set(connect, resp);
            if (!r.handleResponse()) {
                return resp;
            }
            sock[0] = this.getSocket(timeout);
        }
    }
    
    private static void checkCert(final X509Certificate cert, final String host) throws IOException {
        String name;
        try {
            name = ((X500Name)cert.getSubjectDN()).getCommonName().toLowerCase();
        }
        catch (final Throwable t) {
            return;
        }
        if (Util.wildcardMatch(name, host)) {
            return;
        }
        throw new SSLException("Name in certificate `" + name + "' does not " + "match host name `" + host + "'");
    }
    
    private String[] assembleHeaders(final Request req, final ByteArrayOutputStream hdr_buf) throws IOException {
        final DataOutputStream dataout = new DataOutputStream(hdr_buf);
        final String[] con_hdrs = { "", "" };
        final NVPair[] hdrs = req.getHeaders();
        int ho_idx = -1;
        int ct_idx = -1;
        int ua_idx = -1;
        int co_idx = -1;
        int pc_idx = -1;
        int ka_idx = -1;
        int ex_idx = -1;
        int te_idx = -1;
        int tc_idx = -1;
        int ug_idx = -1;
        for (int idx = 0; idx < hdrs.length; ++idx) {
            final String name = hdrs[idx].getName().trim().toLowerCase();
            if (name.equals("host")) {
                ho_idx = idx;
            }
            else if (name.equals("content-type")) {
                ct_idx = idx;
            }
            else if (name.equals("user-agent")) {
                ua_idx = idx;
            }
            else if (name.equals("connection")) {
                co_idx = idx;
            }
            else if (name.equals("proxy-connection")) {
                pc_idx = idx;
            }
            else if (name.equals("keep-alive")) {
                ka_idx = idx;
            }
            else if (name.equals("expect")) {
                ex_idx = idx;
            }
            else if (name.equals("te")) {
                te_idx = idx;
            }
            else if (name.equals("transfer-encoding")) {
                tc_idx = idx;
            }
            else if (name.equals("upgrade")) {
                ug_idx = idx;
            }
        }
        final String file = Util.escapeUnsafeChars(req.getRequestURI());
        if (this.Proxy_Host != null && this.Protocol != 1 && !file.equals("*")) {
            dataout.writeBytes(req.getMethod() + " http://" + this.Host + ":" + this.Port + file + " " + this.RequestProtocolVersion + "\r\n");
        }
        else {
            dataout.writeBytes(req.getMethod() + " " + file + " " + this.RequestProtocolVersion + "\r\n");
        }
        final String h_hdr = (ho_idx >= 0) ? hdrs[ho_idx].getValue().trim() : this.Host;
        if (this.Port != URI.defaultPort(this.getProtocol())) {
            dataout.writeBytes("Host: " + h_hdr + ":" + this.Port + "\r\n");
        }
        else {
            dataout.writeBytes("Host: " + h_hdr + "\r\n");
        }
        String co_hdr = null;
        if (!this.ServProtVersKnown || this.ServerProtocolVersion < 65537 || co_idx != -1) {
            if (co_idx == -1) {
                co_hdr = "Keep-Alive";
                con_hdrs[0] = "Keep-Alive";
            }
            else {
                con_hdrs[0] = hdrs[co_idx].getValue().trim();
                co_hdr = con_hdrs[0];
            }
            try {
                if (ka_idx != -1 && Util.hasToken(con_hdrs[0], "keep-alive")) {
                    dataout.writeBytes("Keep-Alive: " + hdrs[ka_idx].getValue().trim() + "\r\n");
                }
            }
            catch (final ParseException pe) {
                throw new IOException(pe.toString());
            }
        }
        if (this.Proxy_Host != null && this.Protocol != 1 && (!this.ServProtVersKnown || this.ServerProtocolVersion < 65537) && co_hdr != null) {
            dataout.writeBytes("Proxy-Connection: ");
            dataout.writeBytes(co_hdr);
            dataout.writeBytes("\r\n");
            co_hdr = null;
        }
        Label_0813: {
            if (co_hdr != null) {
                try {
                    if (!Util.hasToken(co_hdr, "TE")) {
                        co_hdr += ", TE";
                    }
                    break Label_0813;
                }
                catch (final ParseException pe) {
                    throw new IOException(pe.toString());
                }
            }
            co_hdr = "TE";
        }
        if (ug_idx != -1) {
            co_hdr += ", Upgrade";
        }
        if (co_hdr != null) {
            dataout.writeBytes("Connection: ");
            dataout.writeBytes(co_hdr);
            dataout.writeBytes("\r\n");
        }
        if (te_idx != -1) {
            dataout.writeBytes("TE: ");
            Vector pte;
            try {
                pte = Util.parseHeader(hdrs[te_idx].getValue());
            }
            catch (final ParseException pe2) {
                throw new IOException(pe2.toString());
            }
            if (!pte.contains(new HttpHeaderElement("trailers"))) {
                dataout.writeBytes("trailers, ");
            }
            dataout.writeBytes(hdrs[te_idx].getValue().trim() + "\r\n");
        }
        else {
            dataout.writeBytes("TE: trailers\r\n");
        }
        if (ua_idx != -1) {
            dataout.writeBytes("User-Agent: " + hdrs[ua_idx].getValue().trim() + " " + "RPT-HTTPClient/0.3-3E" + "\r\n");
        }
        else {
            dataout.writeBytes("User-Agent: RPT-HTTPClient/0.3-3E\r\n");
        }
        for (int idx2 = 0; idx2 < hdrs.length; ++idx2) {
            if (idx2 != ct_idx && idx2 != ua_idx && idx2 != co_idx && idx2 != pc_idx && idx2 != ka_idx && idx2 != ex_idx && idx2 != te_idx && idx2 != ho_idx) {
                dataout.writeBytes(hdrs[idx2].getName().trim() + ": " + hdrs[idx2].getValue().trim() + "\r\n");
            }
        }
        if (req.getData() != null || req.getStream() != null) {
            dataout.writeBytes("Content-type: ");
            if (ct_idx != -1) {
                dataout.writeBytes(hdrs[ct_idx].getValue().trim());
            }
            else {
                dataout.writeBytes("application/octet-stream");
            }
            dataout.writeBytes("\r\n");
            if (req.getData() != null) {
                dataout.writeBytes("Content-length: " + req.getData().length + "\r\n");
            }
            else if (req.getStream().getLength() != -1 && tc_idx == -1) {
                dataout.writeBytes("Content-length: " + req.getStream().getLength() + "\r\n");
            }
            if (ex_idx != -1) {
                con_hdrs[1] = hdrs[ex_idx].getValue().trim();
                dataout.writeBytes("Expect: " + con_hdrs[1] + "\r\n");
            }
        }
        else if (ex_idx != -1) {
            Vector expect_tokens;
            try {
                expect_tokens = Util.parseHeader(hdrs[ex_idx].getValue());
            }
            catch (final ParseException pe2) {
                throw new IOException(pe2.toString());
            }
            final HttpHeaderElement cont = new HttpHeaderElement("100-continue");
            while (expect_tokens.removeElement(cont)) {}
            if (!expect_tokens.isEmpty()) {
                con_hdrs[1] = Util.assembleHeader(expect_tokens);
                dataout.writeBytes("Expect: " + con_hdrs[1] + "\r\n");
            }
        }
        dataout.writeBytes("\r\n");
        return con_hdrs;
    }
    
    boolean handleFirstRequest(final Request req, final Response resp) throws IOException {
        this.ServerProtocolVersion = String2ProtVers(resp.getVersion());
        this.ServProtVersKnown = true;
        final int sts = resp.getStatusCode();
        if (this.Proxy_Host != null && this.Protocol != 1 && resp.getHeader("Via") == null && sts != 407 && sts != 502 && sts != 504) {
            this.ServerProtocolVersion = 65536;
        }
        Log.write(1, "Conn:  Protocol Version established: " + ProtVers2String(this.ServerProtocolVersion));
        if (this.ServerProtocolVersion == 65536 && (resp.getStatusCode() == 400 || resp.getStatusCode() == 500)) {
            if (this.input_demux != null) {
                this.input_demux.markForClose(resp);
            }
            this.input_demux = null;
            this.RequestProtocolVersion = "HTTP/1.0";
            return false;
        }
        return true;
    }
    
    private void determineKeepAlive(final Response resp) throws IOException {
        try {
            String con;
            if (this.ServerProtocolVersion >= 65537 || ((((this.Proxy_Host == null || this.Protocol == 1) && (con = resp.getHeader("Connection")) != null) || (this.Proxy_Host != null && this.Protocol != 1 && (con = resp.getHeader("Proxy-Connection")) != null)) && Util.hasToken(con, "keep-alive"))) {
                this.doesKeepAlive = true;
                this.keepAliveUnknown = false;
                Log.write(1, "Conn:  Keep-Alive enabled");
            }
            else if (resp.getStatusCode() < 400) {
                this.keepAliveUnknown = false;
            }
            if (this.doesKeepAlive && this.ServerProtocolVersion == 65536 && (con = resp.getHeader("Keep-Alive")) != null) {
                final HttpHeaderElement max = Util.getElement(Util.parseHeader(con), "max");
                if (max != null && max.getValue() != null) {
                    this.keepAliveReqMax = Integer.parseInt(max.getValue());
                    this.keepAliveReqLeft = this.keepAliveReqMax;
                    Log.write(1, "Conn:  Max Keep-Alive requests: " + this.keepAliveReqMax);
                }
            }
        }
        catch (final ParseException ex) {}
        catch (final NumberFormatException ex2) {}
        catch (final ClassCastException ex3) {}
    }
    
    synchronized void outputFinished() {
        this.output_finished = true;
        this.notify();
    }
    
    synchronized void closeDemux(final IOException ioe, final boolean was_reset) {
        if (this.input_demux != null) {
            this.input_demux.close(ioe, was_reset);
        }
        this.early_stall = null;
        this.late_stall = null;
        this.prev_resp = null;
    }
    
    static final String ProtVers2String(final int prot_vers) {
        return "HTTP/" + (prot_vers >>> 16) + "." + (prot_vers & 0xFFFF);
    }
    
    static final int String2ProtVers(final String prot_vers) {
        final String vers = prot_vers.substring(5);
        final int dot = vers.indexOf(46);
        return Integer.parseInt(vers.substring(0, dot)) << 16 | Integer.parseInt(vers.substring(dot + 1));
    }
    
    @Override
    public String toString() {
        return this.getProtocol() + "://" + this.getHost() + ((this.getPort() != URI.defaultPort(this.getProtocol())) ? (":" + this.getPort()) : "");
    }
    
    static {
        dflt_context = new Object();
        HTTPConnection.Default_Proxy_Host = null;
        HTTPConnection.non_proxy_host_list = new CIHashtable();
        HTTPConnection.non_proxy_dom_list = new Vector();
        HTTPConnection.non_proxy_addr_list = new Vector();
        HTTPConnection.non_proxy_mask_list = new Vector();
        HTTPConnection.Default_Socks_client = null;
        HTTPConnection.no_chunked = false;
        HTTPConnection.force_1_0 = false;
        HTTPConnection.neverPipeline = false;
        HTTPConnection.noKeepAlives = false;
        HTTPConnection.haveMSLargeWritesBug = false;
        HTTPConnection.deferStreamed = false;
        HTTPConnection.DefaultTimeout = 0;
        HTTPConnection.defaultAllowUI = true;
        HTTPConnection.defaultSSLFactory = null;
        try {
            final String host = System.getProperty("http.proxyHost");
            if (host == null) {
                throw new Exception();
            }
            final int port = Integer.getInteger("http.proxyPort", -1);
            Log.write(1, "Conn:  using proxy " + host + ":" + port);
            setProxyServer(host, port);
        }
        catch (final Exception e) {
            try {
                if (Boolean.getBoolean("proxySet")) {
                    final String host2 = System.getProperty("proxyHost");
                    final int port2 = Integer.getInteger("proxyPort", -1);
                    Log.write(1, "Conn:  using proxy " + host2 + ":" + port2);
                    setProxyServer(host2, port2);
                }
            }
            catch (final Exception ee) {
                HTTPConnection.Default_Proxy_Host = null;
            }
        }
        try {
            String hosts = System.getProperty("HTTPClient.nonProxyHosts");
            if (hosts == null) {
                hosts = System.getProperty("http.nonProxyHosts");
            }
            final String[] list = Util.splitProperty(hosts);
            dontProxyFor(list);
        }
        catch (final Exception ex) {}
        try {
            final String host = System.getProperty("HTTPClient.socksHost");
            if (host != null && host.length() > 0) {
                final int port = Integer.getInteger("HTTPClient.socksPort", -1);
                final int version = Integer.getInteger("HTTPClient.socksVersion", -1);
                Log.write(1, "Conn:  using SOCKS " + host + ":" + port);
                if (version == -1) {
                    setSocksServer(host, port);
                }
                else {
                    setSocksServer(host, port, version);
                }
            }
        }
        catch (final Exception e) {
            HTTPConnection.Default_Socks_client = null;
        }
        String modules = "HTTPClient.RetryModule|HTTPClient.CookieModule|HTTPClient.RedirectionModule|HTTPClient.AuthorizationModule|HTTPClient.DefaultModule|HTTPClient.TransferEncodingModule|HTTPClient.ContentMD5Module|HTTPClient.ContentEncodingModule";
        boolean in_applet = false;
        try {
            modules = System.getProperty("HTTPClient.Modules", modules);
        }
        catch (final SecurityException se) {
            in_applet = true;
        }
        HTTPConnection.DefaultModuleList = new Vector();
        final String[] list2 = Util.splitProperty(modules);
        for (int idx = 0; idx < list2.length; ++idx) {
            try {
                HTTPConnection.DefaultModuleList.addElement(Class.forName(list2[idx]));
                Log.write(1, "Conn:  added module " + list2[idx]);
            }
            catch (final ClassNotFoundException cnfe) {
                if (!in_applet) {
                    throw new NoClassDefFoundError(cnfe.getMessage());
                }
            }
        }
        try {
            HTTPConnection.neverPipeline = Boolean.getBoolean("HTTPClient.disable_pipelining");
            if (HTTPConnection.neverPipeline) {
                Log.write(1, "Conn:  disabling pipelining");
            }
        }
        catch (final Exception ex2) {}
        try {
            HTTPConnection.noKeepAlives = Boolean.getBoolean("HTTPClient.disableKeepAlives");
            if (HTTPConnection.noKeepAlives) {
                Log.write(1, "Conn:  disabling keep-alives");
            }
        }
        catch (final Exception ex3) {}
        try {
            HTTPConnection.force_1_0 = Boolean.getBoolean("HTTPClient.forceHTTP_1.0");
            if (HTTPConnection.force_1_0) {
                Log.write(1, "Conn:  forcing HTTP/1.0 requests");
            }
        }
        catch (final Exception ex4) {}
        try {
            HTTPConnection.no_chunked = Boolean.getBoolean("HTTPClient.dontChunkRequests");
            if (HTTPConnection.no_chunked) {
                Log.write(1, "Conn:  never chunking requests");
            }
        }
        catch (final Exception ex5) {}
        try {
            if (System.getProperty("os.name").indexOf("Windows") >= 0 && System.getProperty("java.version").startsWith("1.1")) {
                HTTPConnection.haveMSLargeWritesBug = true;
            }
            if (HTTPConnection.haveMSLargeWritesBug) {
                Log.write(1, "Conn:  splitting large writes into 20K chunks (M$ bug)");
            }
        }
        catch (final Exception ex6) {}
        try {
            HTTPConnection.deferStreamed = Boolean.getBoolean("HTTPClient.deferStreamed");
            if (HTTPConnection.deferStreamed) {
                Log.write(1, "Conn:  enabling defered handling of responses to streamed requests");
            }
        }
        catch (final Exception ex7) {}
    }
    
    private class EstablishConnection extends Thread
    {
        String actual_host;
        int actual_port;
        IOException exception;
        Socket sock;
        SocksClient Socks_client;
        boolean close;
        
        EstablishConnection(final String host, final int port, final SocksClient socks) {
            super("EstablishConnection (" + host + ":" + port + ")");
            try {
                this.setDaemon(true);
            }
            catch (final SecurityException ex) {}
            this.actual_host = host;
            this.actual_port = port;
            this.Socks_client = socks;
            this.exception = null;
            this.sock = null;
            this.close = false;
        }
        
        @Override
        public void run() {
            try {
                if (this.Socks_client != null) {
                    this.sock = this.Socks_client.getSocket(this.actual_host, this.actual_port);
                }
                else {
                    final InetAddress[] addr_list = InetAddress.getAllByName(this.actual_host);
                    int idx = 0;
                    while (idx < addr_list.length) {
                        try {
                            if (HTTPConnection.this.LocalAddr == null) {
                                this.sock = new Socket(addr_list[idx], this.actual_port);
                            }
                            else {
                                this.sock = new Socket(addr_list[idx], this.actual_port, HTTPConnection.this.LocalAddr, HTTPConnection.this.LocalPort);
                            }
                        }
                        catch (final SocketException se) {
                            if (idx == addr_list.length - 1 || this.close) {
                                throw se;
                            }
                            ++idx;
                            continue;
                        }
                        break;
                    }
                }
            }
            catch (final IOException ioe) {
                this.exception = ioe;
            }
            if (this.close && this.sock != null) {
                try {
                    this.sock.close();
                }
                catch (final IOException ex) {}
                this.sock = null;
            }
        }
        
        IOException getException() {
            return this.exception;
        }
        
        Socket getSocket() {
            return this.sock;
        }
        
        void forget() {
            this.close = true;
        }
    }
    
    private class MSLargeWritesBugStream extends FilterOutputStream
    {
        private final int CHUNK_SIZE = 20000;
        
        MSLargeWritesBugStream(final OutputStream os) {
            super(os);
        }
        
        @Override
        public void write(final byte[] b, int off, int len) throws IOException {
            while (len > 20000) {
                this.out.write(b, off, 20000);
                off += 20000;
                len -= 20000;
            }
            this.out.write(b, off, len);
        }
    }
}
