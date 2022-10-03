package org.apache.catalina.valves;

import javax.servlet.http.HttpSessionBindingEvent;
import java.io.Serializable;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.juli.logging.LogFactory;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import org.apache.catalina.LifecycleException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.Map;
import org.apache.juli.logging.Log;

public class CrawlerSessionManagerValve extends ValveBase
{
    private static final Log log;
    private final Map<String, String> clientIdSessionId;
    private final Map<String, String> sessionIdClientId;
    private String crawlerUserAgents;
    private Pattern uaPattern;
    private String crawlerIps;
    private Pattern ipPattern;
    private int sessionInactiveInterval;
    private boolean isHostAware;
    private boolean isContextAware;
    
    public CrawlerSessionManagerValve() {
        super(true);
        this.clientIdSessionId = new ConcurrentHashMap<String, String>();
        this.sessionIdClientId = new ConcurrentHashMap<String, String>();
        this.crawlerUserAgents = ".*[bB]ot.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*";
        this.uaPattern = null;
        this.crawlerIps = null;
        this.ipPattern = null;
        this.sessionInactiveInterval = 60;
        this.isHostAware = true;
        this.isContextAware = true;
    }
    
    public void setCrawlerUserAgents(final String crawlerUserAgents) {
        this.crawlerUserAgents = crawlerUserAgents;
        if (crawlerUserAgents == null || crawlerUserAgents.length() == 0) {
            this.uaPattern = null;
        }
        else {
            this.uaPattern = Pattern.compile(crawlerUserAgents);
        }
    }
    
    public String getCrawlerUserAgents() {
        return this.crawlerUserAgents;
    }
    
    public void setCrawlerIps(final String crawlerIps) {
        this.crawlerIps = crawlerIps;
        if (crawlerIps == null || crawlerIps.length() == 0) {
            this.ipPattern = null;
        }
        else {
            this.ipPattern = Pattern.compile(crawlerIps);
        }
    }
    
    public String getCrawlerIps() {
        return this.crawlerIps;
    }
    
    public void setSessionInactiveInterval(final int sessionInactiveInterval) {
        this.sessionInactiveInterval = sessionInactiveInterval;
    }
    
    public int getSessionInactiveInterval() {
        return this.sessionInactiveInterval;
    }
    
    public Map<String, String> getClientIpSessionId() {
        return this.clientIdSessionId;
    }
    
    public boolean isHostAware() {
        return this.isHostAware;
    }
    
    public void setHostAware(final boolean isHostAware) {
        this.isHostAware = isHostAware;
    }
    
    public boolean isContextAware() {
        return this.isContextAware;
    }
    
    public void setContextAware(final boolean isContextAware) {
        this.isContextAware = isContextAware;
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        this.uaPattern = Pattern.compile(this.crawlerUserAgents);
    }
    
    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        boolean isBot = false;
        String sessionId = null;
        final String clientIp = request.getRemoteAddr();
        final String clientIdentifier = this.getClientIdentifier(request.getHost(), request.getContext(), clientIp);
        if (CrawlerSessionManagerValve.log.isDebugEnabled()) {
            CrawlerSessionManagerValve.log.debug((Object)(request.hashCode() + ": ClientIdentifier=" + clientIdentifier + ", RequestedSessionId=" + request.getRequestedSessionId()));
        }
        if (request.getSession(false) == null) {
            final Enumeration<String> uaHeaders = request.getHeaders("user-agent");
            String uaHeader = null;
            if (uaHeaders.hasMoreElements()) {
                uaHeader = uaHeaders.nextElement();
            }
            if (uaHeader != null && !uaHeaders.hasMoreElements()) {
                if (CrawlerSessionManagerValve.log.isDebugEnabled()) {
                    CrawlerSessionManagerValve.log.debug((Object)(request.hashCode() + ": UserAgent=" + uaHeader));
                }
                if (this.uaPattern.matcher(uaHeader).matches()) {
                    isBot = true;
                    if (CrawlerSessionManagerValve.log.isDebugEnabled()) {
                        CrawlerSessionManagerValve.log.debug((Object)(request.hashCode() + ": Bot found. UserAgent=" + uaHeader));
                    }
                }
            }
            if (this.ipPattern != null && this.ipPattern.matcher(clientIp).matches()) {
                isBot = true;
                if (CrawlerSessionManagerValve.log.isDebugEnabled()) {
                    CrawlerSessionManagerValve.log.debug((Object)(request.hashCode() + ": Bot found. IP=" + clientIp));
                }
            }
            if (isBot) {
                sessionId = this.clientIdSessionId.get(clientIdentifier);
                if (sessionId != null) {
                    request.setRequestedSessionId(sessionId);
                    if (CrawlerSessionManagerValve.log.isDebugEnabled()) {
                        CrawlerSessionManagerValve.log.debug((Object)(request.hashCode() + ": SessionID=" + sessionId));
                    }
                }
            }
        }
        this.getNext().invoke(request, response);
        if (isBot) {
            if (sessionId == null) {
                final HttpSession s = request.getSession(false);
                if (s != null) {
                    this.clientIdSessionId.put(clientIdentifier, s.getId());
                    this.sessionIdClientId.put(s.getId(), clientIdentifier);
                    s.setAttribute(this.getClass().getName(), (Object)new CrawlerHttpSessionBindingListener((Map)this.clientIdSessionId, clientIdentifier));
                    s.setMaxInactiveInterval(this.sessionInactiveInterval);
                    if (CrawlerSessionManagerValve.log.isDebugEnabled()) {
                        CrawlerSessionManagerValve.log.debug((Object)(request.hashCode() + ": New bot session. SessionID=" + s.getId()));
                    }
                }
            }
            else if (CrawlerSessionManagerValve.log.isDebugEnabled()) {
                CrawlerSessionManagerValve.log.debug((Object)(request.hashCode() + ": Bot session accessed. SessionID=" + sessionId));
            }
        }
    }
    
    private String getClientIdentifier(final Host host, final Context context, final String clientIp) {
        final StringBuilder result = new StringBuilder(clientIp);
        if (this.isHostAware) {
            result.append('-').append(host.getName());
        }
        if (this.isContextAware && context != null) {
            result.append(context.getName());
        }
        return result.toString();
    }
    
    static {
        log = LogFactory.getLog((Class)CrawlerSessionManagerValve.class);
    }
    
    private static class CrawlerHttpSessionBindingListener implements HttpSessionBindingListener, Serializable
    {
        private static final long serialVersionUID = 1L;
        private final transient Map<String, String> clientIdSessionId;
        private final transient String clientIdentifier;
        
        private CrawlerHttpSessionBindingListener(final Map<String, String> clientIdSessionId, final String clientIdentifier) {
            this.clientIdSessionId = clientIdSessionId;
            this.clientIdentifier = clientIdentifier;
        }
        
        public void valueBound(final HttpSessionBindingEvent event) {
        }
        
        public void valueUnbound(final HttpSessionBindingEvent event) {
            if (this.clientIdentifier != null && this.clientIdSessionId != null) {
                this.clientIdSessionId.remove(this.clientIdentifier);
            }
        }
    }
}
