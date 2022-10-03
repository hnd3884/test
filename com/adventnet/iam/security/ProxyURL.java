package com.adventnet.iam.security;

import java.util.ArrayList;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import org.w3c.dom.Element;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class ProxyURL
{
    private static final Logger logger;
    private String path;
    private String remotePath;
    private String remoteServer;
    private String serviceParams;
    private boolean pathRegex;
    private Pattern pathPattern;
    private Properties properties;
    private boolean allowCookie;
    private boolean allowHeaders;
    private boolean allowQuerystring;
    private boolean allowReqBody;
    private List<String> excludedHeaders;
    
    public ProxyURL() {
        this.path = null;
        this.remotePath = null;
        this.remoteServer = null;
        this.serviceParams = null;
        this.pathRegex = false;
        this.pathPattern = null;
        this.properties = null;
        this.allowCookie = true;
        this.allowHeaders = true;
        this.allowQuerystring = true;
        this.allowReqBody = true;
    }
    
    public ProxyURL(final Element element, final Properties properties) {
        this.path = null;
        this.remotePath = null;
        this.remoteServer = null;
        this.serviceParams = null;
        this.pathRegex = false;
        this.pathPattern = null;
        this.properties = null;
        this.allowCookie = true;
        this.allowHeaders = true;
        this.allowQuerystring = true;
        this.allowReqBody = true;
        this.pathRegex = "true".equals(element.getAttribute("path-regex"));
        this.setPath(element.getAttribute("path"));
        this.setRemotePath(element.getAttribute("remote-path"));
        this.setRemoteServer(element.getAttribute("remote-server"));
        this.setServiceParams(element.getAttribute("service-params"));
        this.setAllowCookie(!"false".equalsIgnoreCase(element.getAttribute("allow-cookie")));
        this.setAllowHeaders(!"false".equalsIgnoreCase(element.getAttribute("allow-headers")));
        this.setAllowQuerystring(!"false".equalsIgnoreCase(element.getAttribute("allow-querystring")));
        this.setAllowBody(!"false".equalsIgnoreCase(element.getAttribute("allow-body")));
        this.setExcludedHeaders(element.getAttribute("exclude-headers"));
        this.properties = properties;
        this.validate();
    }
    
    public void setPath(final String path) {
        this.path = path;
        if (this.pathRegex) {
            this.pathPattern = Pattern.compile(path);
        }
    }
    
    public void setRemotePath(final String remotePath) {
        ProxyURL.logger.log(Level.FINE, "Remote path   = {0}", remotePath);
        if (remotePath == null || "".equals(remotePath.trim())) {
            this.remotePath = this.path;
        }
        else {
            this.remotePath = remotePath;
        }
        if (!this.remotePath.startsWith("/")) {
            this.remotePath = "/" + this.remotePath;
        }
    }
    
    public void setRemoteServer(final String remoteServer) {
        this.remoteServer = remoteServer;
    }
    
    public void setServiceParams(final String serviceParams) {
        this.serviceParams = serviceParams;
    }
    
    public String getRemoteURL(final HttpServletRequest request) {
        String urlstr = this.remoteServer;
        if (this.pathRegex) {
            String rp = this.remotePath;
            String uri = SecurityUtil.getRequestURI(request);
            uri = SecurityUtil.ignoreURIPrefixAndTrailingSlash(uri, SecurityFilterProperties.getInstance(request));
            final Matcher m = SecurityUtil.getTimeLimitedPatternMatcher(this.pathPattern, uri);
            if (!m.matches()) {
                throw new IAMSecurityException("Path regext does not match : " + this.path);
            }
            for (int gcnt = m.groupCount(), i = 1; i <= gcnt; ++i) {
                rp = rp.replace("$" + i, m.group(i));
            }
            urlstr += rp;
        }
        else {
            urlstr += this.remotePath;
        }
        if (this.allowQuerystring) {
            urlstr = addParams(urlstr, request.getQueryString());
        }
        urlstr = addParams(urlstr, this.serviceParams);
        return urlstr;
    }
    
    public Pattern getPathPattern() {
        return this.pathPattern;
    }
    
    public String getRemoteServer() {
        return this.remoteServer;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public boolean isProxyURLInRegex() {
        return this.pathRegex;
    }
    
    public void validate() {
        if (this.path == null || this.remoteServer == null || "".equals(this.path.trim()) || "".equals(this.remoteServer.trim())) {
            throw new IAMSecurityException("path and remote-server are mandatory for proxy element");
        }
        if (this.remoteServer.startsWith("$")) {
            final String key = this.remoteServer.substring(1);
            final String value = this.getPropertyValue(key);
            if (value == null || "".equals(value.trim())) {
                ProxyURL.logger.log(Level.SEVERE, "Invalid proxy configuration :: The remote server security-property ''{0}'' is not configured or not yet loaded in proxy url ''{1}''", new Object[] { key, this.path });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            this.remoteServer = value;
        }
        final int startLoc = this.remotePath.indexOf("${");
        if (startLoc > -1) {
            int endLoc = this.remotePath.substring(startLoc).indexOf(125);
            if (endLoc == -1) {
                ProxyURL.logger.log(Level.SEVERE, "Invalid proxy configuration :: security/system property reference is not valid in remote-path: {0}, proxy url: {1}", new Object[] { this.remotePath, this.path });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            endLoc += startLoc;
            final String propName = this.remotePath.substring(startLoc + 2, endLoc);
            String propValue = null;
            if (!"url.static.version".equals(propName)) {
                ProxyURL.logger.log(Level.SEVERE, "Invalid proxy configuration :: Unknown property specified in remote-path:{0}, proxy url : {1}", new Object[] { this.remotePath, this.path });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            propValue = this.getPropertyValue(propName);
            if (propValue == null || "".equals(propValue.trim())) {
                ProxyURL.logger.log(Level.SEVERE, "Invalid proxy configuration :: security/system property ''{0}'' specified in remote-path ''{1}'' is not configured or not yet loaded, proxy url :{2}", new Object[] { propName, this.remotePath, this.path });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            this.remotePath = this.remotePath.substring(0, startLoc) + propValue + this.remotePath.substring(endLoc + 1);
        }
    }
    
    private String getPropertyValue(final String propertyName) {
        final String propValue = this.properties.getProperty(propertyName);
        return (propValue == null) ? System.getProperty(propertyName) : propValue;
    }
    
    public static String addParams(String url, final String params) {
        if (params == null || "".equals(params.trim())) {
            return url;
        }
        if (url == null) {
            url = "";
        }
        final StringBuilder sb = new StringBuilder(url);
        if (url.indexOf(38) != -1 || url.indexOf(63) != -1) {
            sb.append('&');
        }
        else {
            sb.append('?');
        }
        return sb.append(params).toString();
    }
    
    public boolean allowCookie() {
        return this.allowCookie;
    }
    
    public void setAllowCookie(final boolean cookie) {
        this.allowCookie = cookie;
    }
    
    public boolean allowHeaders() {
        return this.allowHeaders;
    }
    
    public void setAllowHeaders(final boolean headers) {
        this.allowHeaders = headers;
    }
    
    public boolean allowQuerystring() {
        return this.allowQuerystring;
    }
    
    public void setAllowQuerystring(final boolean queryString) {
        this.allowQuerystring = queryString;
    }
    
    public boolean allowReqBody() {
        return this.allowReqBody;
    }
    
    public void setAllowBody(final boolean body) {
        this.allowReqBody = body;
    }
    
    private void setExcludedHeaders(final String excludeHeaderNames) {
        if (SecurityUtil.isValid(excludeHeaderNames)) {
            this.excludedHeaders = new ArrayList<String>();
            final String[] split;
            final String[] headerNameList = split = excludeHeaderNames.split(",");
            for (final String headerName : split) {
                this.excludedHeaders.add(headerName.toLowerCase());
            }
        }
    }
    
    public List<String> getExcludedHeaders() {
        return this.excludedHeaders;
    }
    
    public String getRemotePath() {
        return this.remotePath;
    }
    
    public String getServiceParams() {
        return this.serviceParams;
    }
    
    @Override
    public String toString() {
        return "Path = " + this.path + " remote-path = " + this.remotePath + " remote-server = " + this.remoteServer + " service-params = " + this.serviceParams;
    }
    
    static {
        logger = Logger.getLogger(ProxyURL.class.getName());
    }
}
