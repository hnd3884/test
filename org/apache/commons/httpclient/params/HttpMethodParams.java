package org.apache.commons.httpclient.params;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.logging.Log;

public class HttpMethodParams extends DefaultHttpParams
{
    private static final Log LOG;
    public static final String USER_AGENT = "http.useragent";
    public static final String PROTOCOL_VERSION = "http.protocol.version";
    public static final String UNAMBIGUOUS_STATUS_LINE = "http.protocol.unambiguous-statusline";
    public static final String SINGLE_COOKIE_HEADER = "http.protocol.single-cookie-header";
    public static final String STRICT_TRANSFER_ENCODING = "http.protocol.strict-transfer-encoding";
    public static final String REJECT_HEAD_BODY = "http.protocol.reject-head-body";
    public static final String HEAD_BODY_CHECK_TIMEOUT = "http.protocol.head-body-timeout";
    public static final String USE_EXPECT_CONTINUE = "http.protocol.expect-continue";
    public static final String CREDENTIAL_CHARSET = "http.protocol.credential-charset";
    public static final String HTTP_ELEMENT_CHARSET = "http.protocol.element-charset";
    public static final String HTTP_URI_CHARSET = "http.protocol.uri-charset";
    public static final String HTTP_CONTENT_CHARSET = "http.protocol.content-charset";
    public static final String COOKIE_POLICY = "http.protocol.cookie-policy";
    public static final String WARN_EXTRA_INPUT = "http.protocol.warn-extra-input";
    public static final String STATUS_LINE_GARBAGE_LIMIT = "http.protocol.status-line-garbage-limit";
    public static final String SO_TIMEOUT = "http.socket.timeout";
    public static final String DATE_PATTERNS = "http.dateparser.patterns";
    public static final String RETRY_HANDLER = "http.method.retry-handler";
    public static final String BUFFER_WARN_TRIGGER_LIMIT = "http.method.response.buffer.warnlimit";
    public static final String VIRTUAL_HOST = "http.virtual-host";
    public static final String MULTIPART_BOUNDARY = "http.method.multipart.boundary";
    private static final String[] PROTOCOL_STRICTNESS_PARAMETERS;
    
    public HttpMethodParams() {
        super(DefaultHttpParams.getDefaultParams());
    }
    
    public HttpMethodParams(final HttpParams defaults) {
        super(defaults);
    }
    
    public String getHttpElementCharset() {
        String charset = (String)this.getParameter("http.protocol.element-charset");
        if (charset == null) {
            HttpMethodParams.LOG.warn((Object)"HTTP element charset not configured, using US-ASCII");
            charset = "US-ASCII";
        }
        return charset;
    }
    
    public void setHttpElementCharset(final String charset) {
        this.setParameter("http.protocol.element-charset", charset);
    }
    
    public String getContentCharset() {
        String charset = (String)this.getParameter("http.protocol.content-charset");
        if (charset == null) {
            HttpMethodParams.LOG.warn((Object)"Default content charset not configured, using ISO-8859-1");
            charset = "ISO-8859-1";
        }
        return charset;
    }
    
    public void setUriCharset(final String charset) {
        this.setParameter("http.protocol.uri-charset", charset);
    }
    
    public String getUriCharset() {
        String charset = (String)this.getParameter("http.protocol.uri-charset");
        if (charset == null) {
            charset = "UTF-8";
        }
        return charset;
    }
    
    public void setContentCharset(final String charset) {
        this.setParameter("http.protocol.content-charset", charset);
    }
    
    public String getCredentialCharset() {
        String charset = (String)this.getParameter("http.protocol.credential-charset");
        if (charset == null) {
            HttpMethodParams.LOG.debug((Object)"Credential charset not configured, using HTTP element charset");
            charset = this.getHttpElementCharset();
        }
        return charset;
    }
    
    public void setCredentialCharset(final String charset) {
        this.setParameter("http.protocol.credential-charset", charset);
    }
    
    public HttpVersion getVersion() {
        final Object param = this.getParameter("http.protocol.version");
        if (param == null) {
            return HttpVersion.HTTP_1_1;
        }
        return (HttpVersion)param;
    }
    
    public void setVersion(final HttpVersion version) {
        this.setParameter("http.protocol.version", version);
    }
    
    public String getCookiePolicy() {
        final Object param = this.getParameter("http.protocol.cookie-policy");
        if (param == null) {
            return "default";
        }
        return (String)param;
    }
    
    public void setCookiePolicy(final String policy) {
        this.setParameter("http.protocol.cookie-policy", policy);
    }
    
    public int getSoTimeout() {
        return this.getIntParameter("http.socket.timeout", 0);
    }
    
    public void setSoTimeout(final int timeout) {
        this.setIntParameter("http.socket.timeout", timeout);
    }
    
    public void setVirtualHost(final String hostname) {
        this.setParameter("http.virtual-host", hostname);
    }
    
    public String getVirtualHost() {
        return (String)this.getParameter("http.virtual-host");
    }
    
    public void makeStrict() {
        this.setParameters(HttpMethodParams.PROTOCOL_STRICTNESS_PARAMETERS, Boolean.TRUE);
        this.setIntParameter("http.protocol.status-line-garbage-limit", 0);
    }
    
    public void makeLenient() {
        this.setParameters(HttpMethodParams.PROTOCOL_STRICTNESS_PARAMETERS, Boolean.FALSE);
        this.setIntParameter("http.protocol.status-line-garbage-limit", Integer.MAX_VALUE);
    }
    
    static {
        LOG = LogFactory.getLog(HttpMethodParams.class);
        PROTOCOL_STRICTNESS_PARAMETERS = new String[] { "http.protocol.unambiguous-statusline", "http.protocol.single-cookie-header", "http.protocol.strict-transfer-encoding", "http.protocol.reject-head-body", "http.protocol.warn-extra-input" };
    }
}
