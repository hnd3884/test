package com.adventnet.iam.security;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Enumeration;
import java.util.List;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import com.zoho.security.api.HttpConnection;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class ProxyUtil
{
    static final Logger logger;
    
    public static void service(final HttpServletRequest req, final HttpServletResponse res, final ProxyURL pu) throws ServletException, IOException {
        final String urlstr = pu.getRemoteURL(req);
        doProxy(urlstr, req, res, pu);
    }
    
    public static void doProxy(final String urlstr, final HttpServletRequest req, final HttpServletResponse res, final ProxyURL proxyRule) throws IOException {
        if (req.getHeader("X-Proxied-for") != null) {
            throw new IAMSecurityException("Possible looping of proxy url " + req.getRequestURI());
        }
        HttpConnection conn = null;
        ByteArrayOutputStream bos = null;
        String method = req.getMethod();
        boolean isPatchRequest = false;
        if (isPatchRequest = "PATCH".equalsIgnoreCase(method)) {
            method = "POST";
        }
        try {
            conn = new HttpConnection(urlstr, method, SecurityFilterProperties.getProxyConnectionTimeOut(), SecurityFilterProperties.getProxyReadTimeOut());
            String remoteAddr = req.getRemoteAddr();
            if (proxyRule.allowHeaders()) {
                copyRequestHeaders(req, conn, proxyRule.allowCookie(), proxyRule.getExcludedHeaders());
                if (proxyRule.allowCookie()) {
                    try {
                        conn.addRequestProperty("Z-SIGNED_REMOTE_USER_IP", SecurityUtil.generateSignature(remoteAddr));
                    }
                    catch (final Exception e) {
                        ProxyUtil.logger.log(Level.WARNING, "SecurityProxy: Exception occurred while generating signature for remote IP. Exception Msg: {0}", new Object[] { e.getMessage() });
                    }
                }
            }
            if (isPatchRequest) {
                conn.setRequestProperty("X-Http-Method-Override", "PATCH");
            }
            final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance(req);
            conn.setFollowRedirect(filterProps.allowProxyURLRedirect());
            conn.setMaxRedirects(filterProps.getURLRedirectMaxLimit());
            conn.setPostMethodRedirect(filterProps.isEnabledPostMethodRedirect());
            if (filterProps.isAuthenticationProviderConfigured()) {
                remoteAddr = filterProps.getAuthenticationProvider().getRemoteAddr(req, remoteAddr);
            }
            conn.addRequestProperty("X-Forwarded-For", remoteAddr);
            conn.addRequestProperty("X-Proxied-For", remoteAddr);
            conn.addRequestProperty("REMOTE_USER_IP", remoteAddr);
            conn.setRequestProperty("ZSEC_PROXY_REQUEST", "true");
            if (proxyRule.allowReqBody() && HttpConnection.REQ_BODY_ALLOWED_METHODS.contains(method)) {
                bos = new ByteArrayOutputStream();
                copyStream((InputStream)req.getInputStream(), bos);
                conn.setRequestBody(bos.toByteArray());
            }
            conn.connect();
            getResponse(conn, res);
        }
        catch (final IAMSecurityException ex) {
            ProxyUtil.logger.log(Level.SEVERE, "Connection to proxy url failed : {0}, Exception : {1}", new Object[] { req.getRequestURI(), ex.getErrorCode() });
            throw new IAMSecurityException(ex.getErrorCode(), req.getRequestURI(), req.getRemoteAddr(), req.getHeader("Referer"));
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }
    
    private static void copyRequestHeaders(final HttpServletRequest req, final HttpConnection conn, final boolean copyCookie, final List<String> excludedHeadersList) {
        final boolean configuredExcludeHeaders = excludedHeadersList != null && excludedHeadersList.size() > 0;
        final Enumeration<String> enm = req.getHeaderNames();
        while (enm.hasMoreElements()) {
            final String hdr = enm.nextElement();
            if (!configuredExcludeHeaders || !excludedHeadersList.contains(hdr)) {
                if (!copyCookie) {
                    if ("Cookie".equalsIgnoreCase(hdr)) {
                        continue;
                    }
                    if ("host".equalsIgnoreCase(hdr)) {
                        continue;
                    }
                }
                final Enumeration<String> vals = req.getHeaders(hdr);
                while (vals.hasMoreElements()) {
                    final String val = vals.nextElement();
                    if (val != null) {
                        if ("ZSEC_PROXY_SERVER_NAME".equalsIgnoreCase(hdr)) {
                            continue;
                        }
                        if ("ZSEC_PROXY_REQUEST".equalsIgnoreCase(hdr)) {
                            continue;
                        }
                        if ("host".equalsIgnoreCase(hdr)) {
                            conn.addRequestProperty("ZSEC_PROXY_SERVER_NAME", val);
                            try {
                                final String signature = SecurityUtil.sign();
                                conn.addRequestProperty("ZSEC_PROXY_SERVER_SIGNATURE", signature);
                                continue;
                            }
                            catch (final Exception e) {
                                throw new IAMSecurityException("Unable to append iscsignature to the proxy url " + req.getRequestURI());
                            }
                        }
                        conn.addRequestProperty(hdr, val);
                        ProxyUtil.logger.log(Level.FINE, "Header name {0} value {1}", new Object[] { hdr, val });
                    }
                }
            }
        }
    }
    
    private static void getResponse(final HttpConnection conn, final HttpServletResponse res) throws IOException {
        res.setStatus(conn.getResponseCode());
        final Map<String, List<String>> map = conn.getHeaderFields();
        final Set<String> headerNames = map.keySet();
        for (final String headerName : headerNames) {
            if (headerName != null) {
                if (headerName.equalsIgnoreCase("transfer-encoding")) {
                    continue;
                }
                if (headerName.equalsIgnoreCase("connection")) {
                    continue;
                }
            }
            final List<String> headerValues = map.get(headerName);
            for (final String headerValue : headerValues) {
                res.addHeader(headerName, headerValue);
            }
        }
        InputStream remoteIn = conn.getErrorStream();
        remoteIn = ((remoteIn == null) ? conn.getInputStream() : remoteIn);
        if (remoteIn != null) {
            copyStream(remoteIn, (OutputStream)res.getOutputStream());
        }
    }
    
    public static void copyStream(final InputStream in, final OutputStream out) throws IOException {
        int byteCount = 0;
        try {
            final byte[] buf = new byte[4096];
            int bytesIn = 0;
            while ((bytesIn = in.read(buf)) != -1) {
                out.write(buf, 0, bytesIn);
                byteCount += bytesIn;
            }
        }
        catch (final Exception e) {
            ProxyUtil.logger.log(Level.WARNING, null, e);
        }
        ProxyUtil.logger.log(Level.FINE, "Copied the bytes of size {0}", byteCount);
        out.flush();
    }
    
    public static boolean isProxyAPICall(final HttpServletRequest request) {
        return false;
    }
    
    static {
        logger = Logger.getLogger(ProxyUtil.class.getName());
    }
}
