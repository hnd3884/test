package org.apache.catalina.filters;

import java.util.Collection;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.apache.tomcat.util.http.parser.Host;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.LinkedList;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import java.util.regex.Pattern;
import javax.servlet.Filter;

public class RemoteIpFilter implements Filter
{
    private static final Pattern commaSeparatedValuesPattern;
    protected static final String HTTP_SERVER_PORT_PARAMETER = "httpServerPort";
    protected static final String HTTPS_SERVER_PORT_PARAMETER = "httpsServerPort";
    protected static final String INTERNAL_PROXIES_PARAMETER = "internalProxies";
    private final Log log;
    protected static final StringManager sm;
    protected static final String PROTOCOL_HEADER_PARAMETER = "protocolHeader";
    protected static final String PROTOCOL_HEADER_HTTPS_VALUE_PARAMETER = "protocolHeaderHttpsValue";
    protected static final String HOST_HEADER_PARAMETER = "hostHeader";
    protected static final String PORT_HEADER_PARAMETER = "portHeader";
    protected static final String CHANGE_LOCAL_NAME_PARAMETER = "changeLocalName";
    protected static final String CHANGE_LOCAL_PORT_PARAMETER = "changeLocalPort";
    protected static final String PROXIES_HEADER_PARAMETER = "proxiesHeader";
    protected static final String REMOTE_IP_HEADER_PARAMETER = "remoteIpHeader";
    protected static final String TRUSTED_PROXIES_PARAMETER = "trustedProxies";
    protected static final String ENABLE_LOOKUPS_PARAMETER = "enableLookups";
    private int httpServerPort;
    private int httpsServerPort;
    private Pattern internalProxies;
    private String protocolHeader;
    private String protocolHeaderHttpsValue;
    private String hostHeader;
    private boolean changeLocalName;
    private String portHeader;
    private boolean changeLocalPort;
    private String proxiesHeader;
    private String remoteIpHeader;
    private boolean requestAttributesEnabled;
    private Pattern trustedProxies;
    private boolean enableLookups;
    
    public RemoteIpFilter() {
        this.log = LogFactory.getLog((Class)RemoteIpFilter.class);
        this.httpServerPort = 80;
        this.httpsServerPort = 443;
        this.internalProxies = Pattern.compile("10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|169\\.254\\.\\d{1,3}\\.\\d{1,3}|127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|::1");
        this.protocolHeader = "X-Forwarded-Proto";
        this.protocolHeaderHttpsValue = "https";
        this.hostHeader = null;
        this.changeLocalName = false;
        this.portHeader = null;
        this.changeLocalPort = false;
        this.proxiesHeader = "X-Forwarded-By";
        this.remoteIpHeader = "X-Forwarded-For";
        this.requestAttributesEnabled = true;
        this.trustedProxies = null;
    }
    
    protected static String[] commaDelimitedListToStringArray(final String commaDelimitedStrings) {
        return (commaDelimitedStrings == null || commaDelimitedStrings.length() == 0) ? new String[0] : RemoteIpFilter.commaSeparatedValuesPattern.split(commaDelimitedStrings);
    }
    
    protected static String listToCommaDelimitedString(final List<String> stringList) {
        if (stringList == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        final Iterator<String> it = stringList.iterator();
        while (it.hasNext()) {
            final Object element = it.next();
            if (element != null) {
                result.append(element);
                if (!it.hasNext()) {
                    continue;
                }
                result.append(", ");
            }
        }
        return result.toString();
    }
    
    public void destroy() {
    }
    
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final boolean isInternal = this.internalProxies != null && this.internalProxies.matcher(request.getRemoteAddr()).matches();
        if (isInternal || (this.trustedProxies != null && this.trustedProxies.matcher(request.getRemoteAddr()).matches())) {
            String remoteIp = null;
            final LinkedList<String> proxiesHeaderValue = new LinkedList<String>();
            final StringBuilder concatRemoteIpHeaderValue = new StringBuilder();
            final Enumeration<String> e = request.getHeaders(this.remoteIpHeader);
            while (e.hasMoreElements()) {
                if (concatRemoteIpHeaderValue.length() > 0) {
                    concatRemoteIpHeaderValue.append(", ");
                }
                concatRemoteIpHeaderValue.append(e.nextElement());
            }
            final String[] remoteIpHeaderValue = commaDelimitedListToStringArray(concatRemoteIpHeaderValue.toString());
            if (!isInternal) {
                proxiesHeaderValue.addFirst(request.getRemoteAddr());
            }
            int idx;
            for (idx = remoteIpHeaderValue.length - 1; idx >= 0; --idx) {
                final String currentRemoteIp = remoteIp = remoteIpHeaderValue[idx];
                if (this.internalProxies == null || !this.internalProxies.matcher(currentRemoteIp).matches()) {
                    if (this.trustedProxies == null || !this.trustedProxies.matcher(currentRemoteIp).matches()) {
                        --idx;
                        break;
                    }
                    proxiesHeaderValue.addFirst(currentRemoteIp);
                }
            }
            final LinkedList<String> newRemoteIpHeaderValue = new LinkedList<String>();
            while (idx >= 0) {
                final String currentRemoteIp2 = remoteIpHeaderValue[idx];
                newRemoteIpHeaderValue.addFirst(currentRemoteIp2);
                --idx;
            }
            final XForwardedRequest xRequest = new XForwardedRequest(request);
            if (remoteIp != null) {
                xRequest.setRemoteAddr(remoteIp);
                if (this.getEnableLookups()) {
                    try {
                        final InetAddress inetAddress = InetAddress.getByName(remoteIp);
                        xRequest.setRemoteHost(inetAddress.getCanonicalHostName());
                    }
                    catch (final UnknownHostException e2) {
                        this.log.debug((Object)RemoteIpFilter.sm.getString("remoteIpFilter.invalidRemoteAddress", new Object[] { remoteIp }), (Throwable)e2);
                        xRequest.setRemoteHost(remoteIp);
                    }
                }
                else {
                    xRequest.setRemoteHost(remoteIp);
                }
                if (proxiesHeaderValue.size() == 0) {
                    xRequest.removeHeader(this.proxiesHeader);
                }
                else {
                    final String commaDelimitedListOfProxies = listToCommaDelimitedString(proxiesHeaderValue);
                    xRequest.setHeader(this.proxiesHeader, commaDelimitedListOfProxies);
                }
                if (newRemoteIpHeaderValue.size() == 0) {
                    xRequest.removeHeader(this.remoteIpHeader);
                }
                else {
                    final String commaDelimitedRemoteIpHeaderValue = listToCommaDelimitedString(newRemoteIpHeaderValue);
                    xRequest.setHeader(this.remoteIpHeader, commaDelimitedRemoteIpHeaderValue);
                }
            }
            if (this.protocolHeader != null) {
                final String protocolHeaderValue = request.getHeader(this.protocolHeader);
                if (protocolHeaderValue != null) {
                    if (this.isForwardedProtoHeaderValueSecure(protocolHeaderValue)) {
                        xRequest.setSecure(true);
                        xRequest.setScheme("https");
                        this.setPorts(xRequest, this.httpsServerPort);
                    }
                    else {
                        xRequest.setSecure(false);
                        xRequest.setScheme("http");
                        this.setPorts(xRequest, this.httpServerPort);
                    }
                }
            }
            if (this.hostHeader != null) {
                String hostHeaderValue = request.getHeader(this.hostHeader);
                if (hostHeaderValue != null) {
                    try {
                        final int portIndex = Host.parse(hostHeaderValue);
                        if (portIndex > -1) {
                            this.log.debug((Object)RemoteIpFilter.sm.getString("remoteIpFilter.invalidHostWithPort", new Object[] { hostHeaderValue, this.hostHeader }));
                            hostHeaderValue = hostHeaderValue.substring(0, portIndex);
                        }
                        xRequest.setServerName(hostHeaderValue);
                        if (this.isChangeLocalName()) {
                            xRequest.setLocalName(hostHeaderValue);
                        }
                    }
                    catch (final IllegalArgumentException iae) {
                        this.log.debug((Object)RemoteIpFilter.sm.getString("remoteIpFilter.invalidHostHeader", new Object[] { hostHeaderValue, this.hostHeader }));
                    }
                }
            }
            request.setAttribute("org.apache.tomcat.request.forwarded", (Object)Boolean.TRUE);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Incoming request " + request.getRequestURI() + " with originalRemoteAddr [" + request.getRemoteAddr() + "], originalRemoteHost=[" + request.getRemoteHost() + "], originalSecure=[" + request.isSecure() + "], originalScheme=[" + request.getScheme() + "], originalServerName=[" + request.getServerName() + "], originalServerPort=[" + request.getServerPort() + "] will be seen as newRemoteAddr=[" + xRequest.getRemoteAddr() + "], newRemoteHost=[" + xRequest.getRemoteHost() + "], newSecure=[" + xRequest.isSecure() + "], newScheme=[" + xRequest.getScheme() + "], newServerName=[" + xRequest.getServerName() + "], newServerPort=[" + xRequest.getServerPort() + "]"));
            }
            if (this.requestAttributesEnabled) {
                request.setAttribute("org.apache.catalina.AccessLog.RemoteAddr", (Object)xRequest.getRemoteAddr());
                request.setAttribute("org.apache.tomcat.remoteAddr", (Object)xRequest.getRemoteAddr());
                request.setAttribute("org.apache.catalina.AccessLog.RemoteHost", (Object)xRequest.getRemoteHost());
                request.setAttribute("org.apache.catalina.AccessLog.Protocol", (Object)xRequest.getProtocol());
                request.setAttribute("org.apache.catalina.AccessLog.ServerName", (Object)xRequest.getServerName());
                request.setAttribute("org.apache.catalina.AccessLog.ServerPort", (Object)xRequest.getServerPort());
            }
            chain.doFilter((ServletRequest)xRequest, (ServletResponse)response);
        }
        else {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Skip RemoteIpFilter for request " + request.getRequestURI() + " with originalRemoteAddr '" + request.getRemoteAddr() + "'"));
            }
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }
    
    private boolean isForwardedProtoHeaderValueSecure(final String protocolHeaderValue) {
        if (!protocolHeaderValue.contains(",")) {
            return this.protocolHeaderHttpsValue.equalsIgnoreCase(protocolHeaderValue);
        }
        final String[] forwardedProtocols = commaDelimitedListToStringArray(protocolHeaderValue);
        if (forwardedProtocols.length == 0) {
            return false;
        }
        for (final String forwardedProtocol : forwardedProtocols) {
            if (!this.protocolHeaderHttpsValue.equalsIgnoreCase(forwardedProtocol)) {
                return false;
            }
        }
        return true;
    }
    
    private void setPorts(final XForwardedRequest xrequest, final int defaultPort) {
        int port = defaultPort;
        if (this.getPortHeader() != null) {
            final String portHeaderValue = xrequest.getHeader(this.getPortHeader());
            if (portHeaderValue != null) {
                try {
                    port = Integer.parseInt(portHeaderValue);
                }
                catch (final NumberFormatException nfe) {
                    this.log.debug((Object)("Invalid port value [" + portHeaderValue + "] provided in header [" + this.getPortHeader() + "]"));
                }
            }
        }
        xrequest.setServerPort(port);
        if (this.isChangeLocalPort()) {
            xrequest.setLocalPort(port);
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
        }
        else {
            chain.doFilter(request, response);
        }
    }
    
    public boolean isChangeLocalName() {
        return this.changeLocalName;
    }
    
    public boolean isChangeLocalPort() {
        return this.changeLocalPort;
    }
    
    public int getHttpsServerPort() {
        return this.httpsServerPort;
    }
    
    public Pattern getInternalProxies() {
        return this.internalProxies;
    }
    
    public String getProtocolHeader() {
        return this.protocolHeader;
    }
    
    public String getPortHeader() {
        return this.portHeader;
    }
    
    public String getProtocolHeaderHttpsValue() {
        return this.protocolHeaderHttpsValue;
    }
    
    public String getProxiesHeader() {
        return this.proxiesHeader;
    }
    
    public String getRemoteIpHeader() {
        return this.remoteIpHeader;
    }
    
    public boolean getRequestAttributesEnabled() {
        return this.requestAttributesEnabled;
    }
    
    public Pattern getTrustedProxies() {
        return this.trustedProxies;
    }
    
    public boolean getEnableLookups() {
        return this.enableLookups;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        if (filterConfig.getInitParameter("internalProxies") != null) {
            this.setInternalProxies(filterConfig.getInitParameter("internalProxies"));
        }
        if (filterConfig.getInitParameter("protocolHeader") != null) {
            this.setProtocolHeader(filterConfig.getInitParameter("protocolHeader"));
        }
        if (filterConfig.getInitParameter("protocolHeaderHttpsValue") != null) {
            this.setProtocolHeaderHttpsValue(filterConfig.getInitParameter("protocolHeaderHttpsValue"));
        }
        if (filterConfig.getInitParameter("hostHeader") != null) {
            this.setHostHeader(filterConfig.getInitParameter("hostHeader"));
        }
        if (filterConfig.getInitParameter("portHeader") != null) {
            this.setPortHeader(filterConfig.getInitParameter("portHeader"));
        }
        if (filterConfig.getInitParameter("changeLocalPort") != null) {
            this.setChangeLocalPort(Boolean.parseBoolean(filterConfig.getInitParameter("changeLocalPort")));
        }
        if (filterConfig.getInitParameter("changeLocalName") != null) {
            this.setChangeLocalName(Boolean.parseBoolean(filterConfig.getInitParameter("changeLocalName")));
        }
        if (filterConfig.getInitParameter("proxiesHeader") != null) {
            this.setProxiesHeader(filterConfig.getInitParameter("proxiesHeader"));
        }
        if (filterConfig.getInitParameter("remoteIpHeader") != null) {
            this.setRemoteIpHeader(filterConfig.getInitParameter("remoteIpHeader"));
        }
        if (filterConfig.getInitParameter("trustedProxies") != null) {
            this.setTrustedProxies(filterConfig.getInitParameter("trustedProxies"));
        }
        if (filterConfig.getInitParameter("httpServerPort") != null) {
            try {
                this.setHttpServerPort(Integer.parseInt(filterConfig.getInitParameter("httpServerPort")));
            }
            catch (final NumberFormatException e) {
                throw new NumberFormatException(RemoteIpFilter.sm.getString("remoteIpFilter.invalidNumber", new Object[] { "httpServerPort", e.getLocalizedMessage() }));
            }
        }
        if (filterConfig.getInitParameter("httpsServerPort") != null) {
            try {
                this.setHttpsServerPort(Integer.parseInt(filterConfig.getInitParameter("httpsServerPort")));
            }
            catch (final NumberFormatException e) {
                throw new NumberFormatException(RemoteIpFilter.sm.getString("remoteIpFilter.invalidNumber", new Object[] { "httpsServerPort", e.getLocalizedMessage() }));
            }
        }
        if (filterConfig.getInitParameter("enableLookups") != null) {
            this.setEnableLookups(Boolean.parseBoolean(filterConfig.getInitParameter("enableLookups")));
        }
    }
    
    public void setChangeLocalName(final boolean changeLocalName) {
        this.changeLocalName = changeLocalName;
    }
    
    public void setChangeLocalPort(final boolean changeLocalPort) {
        this.changeLocalPort = changeLocalPort;
    }
    
    public void setHttpServerPort(final int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }
    
    public void setHttpsServerPort(final int httpsServerPort) {
        this.httpsServerPort = httpsServerPort;
    }
    
    public void setInternalProxies(final String internalProxies) {
        if (internalProxies == null || internalProxies.length() == 0) {
            this.internalProxies = null;
        }
        else {
            this.internalProxies = Pattern.compile(internalProxies);
        }
    }
    
    public void setHostHeader(final String hostHeader) {
        this.hostHeader = hostHeader;
    }
    
    public void setPortHeader(final String portHeader) {
        this.portHeader = portHeader;
    }
    
    public void setProtocolHeader(final String protocolHeader) {
        this.protocolHeader = protocolHeader;
    }
    
    public void setProtocolHeaderHttpsValue(final String protocolHeaderHttpsValue) {
        this.protocolHeaderHttpsValue = protocolHeaderHttpsValue;
    }
    
    public void setProxiesHeader(final String proxiesHeader) {
        this.proxiesHeader = proxiesHeader;
    }
    
    public void setRemoteIpHeader(final String remoteIpHeader) {
        this.remoteIpHeader = remoteIpHeader;
    }
    
    public void setRequestAttributesEnabled(final boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }
    
    public void setTrustedProxies(final String trustedProxies) {
        if (trustedProxies == null || trustedProxies.length() == 0) {
            this.trustedProxies = null;
        }
        else {
            this.trustedProxies = Pattern.compile(trustedProxies);
        }
    }
    
    public void setEnableLookups(final boolean enableLookups) {
        this.enableLookups = enableLookups;
    }
    
    static {
        commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
        sm = StringManager.getManager((Class)RemoteIpFilter.class);
    }
    
    public static class XForwardedRequest extends HttpServletRequestWrapper
    {
        protected final Map<String, List<String>> headers;
        protected String localName;
        protected int localPort;
        protected String remoteAddr;
        protected String remoteHost;
        protected String scheme;
        protected boolean secure;
        protected String serverName;
        protected int serverPort;
        
        public XForwardedRequest(final HttpServletRequest request) {
            super(request);
            this.localName = request.getLocalName();
            this.localPort = request.getLocalPort();
            this.remoteAddr = request.getRemoteAddr();
            this.remoteHost = request.getRemoteHost();
            this.scheme = request.getScheme();
            this.secure = request.isSecure();
            this.serverName = request.getServerName();
            this.serverPort = request.getServerPort();
            this.headers = new HashMap<String, List<String>>();
            final Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                final String header = headerNames.nextElement();
                this.headers.put(header, (List<String>)Collections.list((Enumeration<Object>)request.getHeaders(header)));
            }
        }
        
        public long getDateHeader(final String name) {
            final String value = this.getHeader(name);
            if (value == null) {
                return -1L;
            }
            final long date = FastHttpDateFormat.parseDate(value);
            if (date == -1L) {
                throw new IllegalArgumentException(value);
            }
            return date;
        }
        
        public String getHeader(final String name) {
            final Map.Entry<String, List<String>> header = this.getHeaderEntry(name);
            if (header == null || header.getValue() == null || header.getValue().isEmpty()) {
                return null;
            }
            return header.getValue().get(0);
        }
        
        protected Map.Entry<String, List<String>> getHeaderEntry(final String name) {
            for (final Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    return entry;
                }
            }
            return null;
        }
        
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(this.headers.keySet());
        }
        
        public Enumeration<String> getHeaders(final String name) {
            final Map.Entry<String, List<String>> header = this.getHeaderEntry(name);
            if (header == null || header.getValue() == null) {
                return Collections.enumeration((Collection<String>)Collections.emptyList());
            }
            return Collections.enumeration(header.getValue());
        }
        
        public int getIntHeader(final String name) {
            final String value = this.getHeader(name);
            if (value == null) {
                return -1;
            }
            return Integer.parseInt(value);
        }
        
        public String getLocalName() {
            return this.localName;
        }
        
        public int getLocalPort() {
            return this.localPort;
        }
        
        public String getRemoteAddr() {
            return this.remoteAddr;
        }
        
        public String getRemoteHost() {
            return this.remoteHost;
        }
        
        public String getScheme() {
            return this.scheme;
        }
        
        public String getServerName() {
            return this.serverName;
        }
        
        public int getServerPort() {
            return this.serverPort;
        }
        
        public boolean isSecure() {
            return this.secure;
        }
        
        public void removeHeader(final String name) {
            final Map.Entry<String, List<String>> header = this.getHeaderEntry(name);
            if (header != null) {
                this.headers.remove(header.getKey());
            }
        }
        
        public void setHeader(final String name, final String value) {
            final List<String> values = Collections.singletonList(value);
            final Map.Entry<String, List<String>> header = this.getHeaderEntry(name);
            if (header == null) {
                this.headers.put(name, values);
            }
            else {
                header.setValue(values);
            }
        }
        
        public void setLocalName(final String localName) {
            this.localName = localName;
        }
        
        public void setLocalPort(final int localPort) {
            this.localPort = localPort;
        }
        
        public void setRemoteAddr(final String remoteAddr) {
            this.remoteAddr = remoteAddr;
        }
        
        public void setRemoteHost(final String remoteHost) {
            this.remoteHost = remoteHost;
        }
        
        public void setScheme(final String scheme) {
            this.scheme = scheme;
        }
        
        public void setSecure(final boolean secure) {
            this.secure = secure;
        }
        
        public void setServerName(final String serverName) {
            this.serverName = serverName;
        }
        
        public void setServerPort(final int serverPort) {
            this.serverPort = serverPort;
        }
        
        public StringBuffer getRequestURL() {
            final StringBuffer url = new StringBuffer();
            final String scheme = this.getScheme();
            int port = this.getServerPort();
            if (port < 0) {
                port = 80;
            }
            url.append(scheme);
            url.append("://");
            url.append(this.getServerName());
            if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
                url.append(':');
                url.append(port);
            }
            url.append(this.getRequestURI());
            return url;
        }
    }
}
