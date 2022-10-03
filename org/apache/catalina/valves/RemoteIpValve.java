package org.apache.catalina.valves;

import org.apache.juli.logging.LogFactory;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.tomcat.util.http.MimeHeaders;
import java.util.Enumeration;
import java.util.Deque;
import org.apache.tomcat.util.http.parser.Host;
import java.util.Collection;
import org.apache.tomcat.util.buf.StringUtils;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.LinkedList;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import java.util.Iterator;
import java.util.List;
import org.apache.juli.logging.Log;
import java.util.regex.Pattern;

public class RemoteIpValve extends ValveBase
{
    private static final Pattern commaSeparatedValuesPattern;
    private static final Log log;
    private String hostHeader;
    private boolean changeLocalName;
    private int httpServerPort;
    private int httpsServerPort;
    private String portHeader;
    private boolean changeLocalPort;
    private Pattern internalProxies;
    private String protocolHeader;
    private String protocolHeaderHttpsValue;
    private String proxiesHeader;
    private String remoteIpHeader;
    private boolean requestAttributesEnabled;
    private Pattern trustedProxies;
    
    protected static String[] commaDelimitedListToStringArray(final String commaDelimitedStrings) {
        return (commaDelimitedStrings == null || commaDelimitedStrings.length() == 0) ? new String[0] : RemoteIpValve.commaSeparatedValuesPattern.split(commaDelimitedStrings);
    }
    
    @Deprecated
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
    
    public RemoteIpValve() {
        super(true);
        this.hostHeader = null;
        this.changeLocalName = false;
        this.httpServerPort = 80;
        this.httpsServerPort = 443;
        this.portHeader = null;
        this.changeLocalPort = false;
        this.internalProxies = Pattern.compile("10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|169\\.254\\.\\d{1,3}\\.\\d{1,3}|127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|::1");
        this.protocolHeader = "X-Forwarded-Proto";
        this.protocolHeaderHttpsValue = "https";
        this.proxiesHeader = "X-Forwarded-By";
        this.remoteIpHeader = "X-Forwarded-For";
        this.requestAttributesEnabled = true;
        this.trustedProxies = null;
    }
    
    public String getHostHeader() {
        return this.hostHeader;
    }
    
    public void setHostHeader(final String hostHeader) {
        this.hostHeader = hostHeader;
    }
    
    public boolean isChangeLocalName() {
        return this.changeLocalName;
    }
    
    public void setChangeLocalName(final boolean changeLocalName) {
        this.changeLocalName = changeLocalName;
    }
    
    public int getHttpServerPort() {
        return this.httpServerPort;
    }
    
    public int getHttpsServerPort() {
        return this.httpsServerPort;
    }
    
    public String getPortHeader() {
        return this.portHeader;
    }
    
    public void setPortHeader(final String portHeader) {
        this.portHeader = portHeader;
    }
    
    public boolean isChangeLocalPort() {
        return this.changeLocalPort;
    }
    
    public void setChangeLocalPort(final boolean changeLocalPort) {
        this.changeLocalPort = changeLocalPort;
    }
    
    public String getInternalProxies() {
        if (this.internalProxies == null) {
            return null;
        }
        return this.internalProxies.toString();
    }
    
    public String getProtocolHeader() {
        return this.protocolHeader;
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
    
    public String getTrustedProxies() {
        if (this.trustedProxies == null) {
            return null;
        }
        return this.trustedProxies.toString();
    }
    
    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        final String originalRemoteAddr = request.getRemoteAddr();
        final String originalRemoteHost = request.getRemoteHost();
        final String originalScheme = request.getScheme();
        final boolean originalSecure = request.isSecure();
        final String originalServerName = request.getServerName();
        final String originalLocalName = this.isChangeLocalName() ? request.getLocalName() : null;
        final int originalServerPort = request.getServerPort();
        final int originalLocalPort = request.getLocalPort();
        final String originalProxiesHeader = request.getHeader(this.proxiesHeader);
        final String originalRemoteIpHeader = request.getHeader(this.remoteIpHeader);
        final boolean isInternal = this.internalProxies != null && this.internalProxies.matcher(originalRemoteAddr).matches();
        if (isInternal || (this.trustedProxies != null && this.trustedProxies.matcher(originalRemoteAddr).matches())) {
            String remoteIp = null;
            final Deque<String> proxiesHeaderValue = new LinkedList<String>();
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
                proxiesHeaderValue.addFirst(originalRemoteAddr);
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
            if (remoteIp != null) {
                request.setRemoteAddr(remoteIp);
                if (request.getConnector().getEnableLookups()) {
                    try {
                        final InetAddress inetAddress = InetAddress.getByName(remoteIp);
                        request.setRemoteHost(inetAddress.getCanonicalHostName());
                    }
                    catch (final UnknownHostException e2) {
                        RemoteIpValve.log.debug((Object)RemoteIpValve.sm.getString("remoteIpValve.invalidRemoteAddress", new Object[] { remoteIp }), (Throwable)e2);
                        request.setRemoteHost(remoteIp);
                    }
                }
                else {
                    request.setRemoteHost(remoteIp);
                }
                if (proxiesHeaderValue.size() == 0) {
                    request.getCoyoteRequest().getMimeHeaders().removeHeader(this.proxiesHeader);
                }
                else {
                    final String commaDelimitedListOfProxies = StringUtils.join((Collection)proxiesHeaderValue);
                    request.getCoyoteRequest().getMimeHeaders().setValue(this.proxiesHeader).setString(commaDelimitedListOfProxies);
                }
                if (newRemoteIpHeaderValue.size() == 0) {
                    request.getCoyoteRequest().getMimeHeaders().removeHeader(this.remoteIpHeader);
                }
                else {
                    final String commaDelimitedRemoteIpHeaderValue = StringUtils.join((Collection)newRemoteIpHeaderValue);
                    request.getCoyoteRequest().getMimeHeaders().setValue(this.remoteIpHeader).setString(commaDelimitedRemoteIpHeaderValue);
                }
            }
            if (this.protocolHeader != null) {
                final String protocolHeaderValue = request.getHeader(this.protocolHeader);
                if (protocolHeaderValue != null) {
                    if (this.isForwardedProtoHeaderValueSecure(protocolHeaderValue)) {
                        request.setSecure(true);
                        request.getCoyoteRequest().scheme().setString("https");
                        this.setPorts(request, this.httpsServerPort);
                    }
                    else {
                        request.setSecure(false);
                        request.getCoyoteRequest().scheme().setString("http");
                        this.setPorts(request, this.httpServerPort);
                    }
                }
            }
            if (this.hostHeader != null) {
                String hostHeaderValue = request.getHeader(this.hostHeader);
                if (hostHeaderValue != null) {
                    try {
                        final int portIndex = Host.parse(hostHeaderValue);
                        if (portIndex > -1) {
                            RemoteIpValve.log.debug((Object)RemoteIpValve.sm.getString("remoteIpValve.invalidHostWithPort", new Object[] { hostHeaderValue, this.hostHeader }));
                            hostHeaderValue = hostHeaderValue.substring(0, portIndex);
                        }
                        request.getCoyoteRequest().serverName().setString(hostHeaderValue);
                        if (this.isChangeLocalName()) {
                            request.getCoyoteRequest().localName().setString(hostHeaderValue);
                        }
                    }
                    catch (final IllegalArgumentException iae) {
                        RemoteIpValve.log.debug((Object)RemoteIpValve.sm.getString("remoteIpValve.invalidHostHeader", new Object[] { hostHeaderValue, this.hostHeader }));
                    }
                }
            }
            request.setAttribute("org.apache.tomcat.request.forwarded", Boolean.TRUE);
            if (RemoteIpValve.log.isDebugEnabled()) {
                RemoteIpValve.log.debug((Object)("Incoming request " + request.getRequestURI() + " with originalRemoteAddr [" + originalRemoteAddr + "], originalRemoteHost=[" + originalRemoteHost + "], originalSecure=[" + originalSecure + "], originalScheme=[" + originalScheme + "], originalServerName=[" + originalServerName + "], originalServerPort=[" + originalServerPort + "] will be seen as newRemoteAddr=[" + request.getRemoteAddr() + "], newRemoteHost=[" + request.getRemoteHost() + "], newSecure=[" + request.isSecure() + "], newScheme=[" + request.getScheme() + "], newServerName=[" + request.getServerName() + "], newServerPort=[" + request.getServerPort() + "]"));
            }
        }
        else if (RemoteIpValve.log.isDebugEnabled()) {
            RemoteIpValve.log.debug((Object)("Skip RemoteIpValve for request " + request.getRequestURI() + " with originalRemoteAddr '" + request.getRemoteAddr() + "'"));
        }
        if (this.requestAttributesEnabled) {
            request.setAttribute("org.apache.catalina.AccessLog.RemoteAddr", request.getRemoteAddr());
            request.setAttribute("org.apache.tomcat.remoteAddr", request.getRemoteAddr());
            request.setAttribute("org.apache.catalina.AccessLog.RemoteHost", request.getRemoteHost());
            request.setAttribute("org.apache.catalina.AccessLog.Protocol", request.getProtocol());
            request.setAttribute("org.apache.catalina.AccessLog.ServerName", request.getServerName());
            request.setAttribute("org.apache.catalina.AccessLog.ServerPort", request.getServerPort());
        }
        try {
            this.getNext().invoke(request, response);
        }
        finally {
            request.setRemoteAddr(originalRemoteAddr);
            request.setRemoteHost(originalRemoteHost);
            request.setSecure(originalSecure);
            request.getCoyoteRequest().scheme().setString(originalScheme);
            request.getCoyoteRequest().serverName().setString(originalServerName);
            if (this.isChangeLocalName()) {
                request.getCoyoteRequest().localName().setString(originalLocalName);
            }
            request.setServerPort(originalServerPort);
            request.setLocalPort(originalLocalPort);
            final MimeHeaders headers = request.getCoyoteRequest().getMimeHeaders();
            if (originalProxiesHeader == null || originalProxiesHeader.length() == 0) {
                headers.removeHeader(this.proxiesHeader);
            }
            else {
                headers.setValue(this.proxiesHeader).setString(originalProxiesHeader);
            }
            if (originalRemoteIpHeader == null || originalRemoteIpHeader.length() == 0) {
                headers.removeHeader(this.remoteIpHeader);
            }
            else {
                headers.setValue(this.remoteIpHeader).setString(originalRemoteIpHeader);
            }
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
    
    private void setPorts(final Request request, final int defaultPort) {
        int port = defaultPort;
        if (this.portHeader != null) {
            final String portHeaderValue = request.getHeader(this.portHeader);
            if (portHeaderValue != null) {
                try {
                    port = Integer.parseInt(portHeaderValue);
                }
                catch (final NumberFormatException nfe) {
                    if (RemoteIpValve.log.isDebugEnabled()) {
                        RemoteIpValve.log.debug((Object)RemoteIpValve.sm.getString("remoteIpValve.invalidPortHeader", new Object[] { portHeaderValue, this.portHeader }), (Throwable)nfe);
                    }
                }
            }
        }
        request.setServerPort(port);
        if (this.changeLocalPort) {
            request.setLocalPort(port);
        }
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
    
    static {
        commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
        log = LogFactory.getLog((Class)RemoteIpValve.class);
    }
}
