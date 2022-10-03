package org.owasp.esapi.reference;

import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.codecs.Hex;
import org.owasp.esapi.crypto.PlainText;
import org.owasp.esapi.errors.IntegrityException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.TreeMap;
import java.util.Collections;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.util.ArrayList;
import org.owasp.esapi.errors.ValidationUploadException;
import java.io.File;
import java.util.List;
import org.owasp.esapi.errors.EncodingException;
import java.util.HashMap;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.errors.IntrusionException;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.owasp.esapi.errors.AuthenticationException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.owasp.esapi.errors.AccessControlException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.StringUtilities;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.HTTPUtilities;

public class DefaultHTTPUtilities implements HTTPUtilities
{
    private static volatile HTTPUtilities instance;
    private final Logger logger;
    static final int maxBytes;
    private ThreadLocalRequest currentRequest;
    private ThreadLocalResponse currentResponse;
    
    public static HTTPUtilities getInstance() {
        if (DefaultHTTPUtilities.instance == null) {
            synchronized (DefaultHTTPUtilities.class) {
                if (DefaultHTTPUtilities.instance == null) {
                    DefaultHTTPUtilities.instance = new DefaultHTTPUtilities();
                }
            }
        }
        return DefaultHTTPUtilities.instance;
    }
    
    public DefaultHTTPUtilities() {
        this.logger = ESAPI.getLogger("HTTPUtilities");
        this.currentRequest = new ThreadLocalRequest();
        this.currentResponse = new ThreadLocalResponse();
    }
    
    @Override
    public void addCookie(final Cookie cookie) {
        this.addCookie(this.getCurrentResponse(), cookie);
    }
    
    @Override
    public void addCookie(final HttpServletResponse response, final Cookie cookie) {
        final String name = cookie.getName();
        final String value = cookie.getValue();
        final int maxAge = cookie.getMaxAge();
        final String domain = cookie.getDomain();
        final String path = cookie.getPath();
        final boolean secure = cookie.getSecure();
        final ValidationErrorList errors = new ValidationErrorList();
        final String cookieName = ESAPI.validator().getValidInput("cookie name", name, "HTTPCookieName", 50, false, errors);
        final String cookieValue = ESAPI.validator().getValidInput("cookie value", value, "HTTPCookieValue", 5000, false, errors);
        if (errors.size() == 0) {
            if (ESAPI.securityConfiguration().getForceHttpOnlyCookies()) {
                final String header = this.createCookieHeader(cookieName, cookieValue, maxAge, domain, path, secure);
                this.addHeader(response, "Set-Cookie", header);
            }
            else {
                cookie.setSecure(secure || ESAPI.securityConfiguration().getForceSecureCookies());
                response.addCookie(cookie);
            }
            return;
        }
        this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to add unsafe data to cookie (skip mode). Skipping cookie and continuing.");
    }
    
    @Override
    public String addCSRFToken(final String href) {
        final User user = ESAPI.authenticator().getCurrentUser();
        if (user.isAnonymous()) {
            return href;
        }
        final String token = "ctoken=" + user.getCSRFToken();
        return (href.indexOf(63) != -1) ? (href + "&" + token) : (href + "?" + token);
    }
    
    @Override
    public void addHeader(final String name, final String value) {
        this.addHeader(this.getCurrentResponse(), name, value);
    }
    
    @Override
    public void addHeader(final HttpServletResponse response, final String name, final String value) {
        try {
            final String strippedName = StringUtilities.replaceLinearWhiteSpace(name);
            final String strippedValue = StringUtilities.replaceLinearWhiteSpace(value);
            final String safeName = ESAPI.validator().getValidInput("addHeader", strippedName, "HTTPHeaderName", 20, false);
            final String safeValue = ESAPI.validator().getValidInput("addHeader", strippedValue, "HTTPHeaderValue", 500, false);
            response.addHeader(safeName, safeValue);
        }
        catch (final ValidationException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to add invalid header denied", e);
        }
    }
    
    @Override
    public void assertSecureChannel() throws AccessControlException {
        this.assertSecureChannel(this.getCurrentRequest());
    }
    
    @Override
    public void assertSecureChannel(final HttpServletRequest request) throws AccessControlException {
        if (request == null) {
            throw new AccessControlException("Insecure request received", "HTTP request was null");
        }
        final StringBuffer sb = request.getRequestURL();
        if (sb == null) {
            throw new AccessControlException("Insecure request received", "HTTP request URL was null");
        }
        final String url = sb.toString();
        if (!url.startsWith("https")) {
            throw new AccessControlException("Insecure request received", "HTTP request did not use SSL");
        }
    }
    
    @Override
    public void assertSecureRequest() throws AccessControlException {
        this.assertSecureRequest(this.getCurrentRequest());
    }
    
    @Override
    public void assertSecureRequest(final HttpServletRequest request) throws AccessControlException {
        this.assertSecureChannel(request);
        final String receivedMethod = request.getMethod();
        final String requiredMethod = "POST";
        if (!receivedMethod.equals(requiredMethod)) {
            throw new AccessControlException("Insecure request received", "Received request using " + receivedMethod + " when only " + requiredMethod + " is allowed");
        }
    }
    
    @Override
    public HttpSession changeSessionIdentifier() throws AuthenticationException {
        return this.changeSessionIdentifier(this.getCurrentRequest());
    }
    
    @Override
    public HttpSession changeSessionIdentifier(final HttpServletRequest request) throws AuthenticationException {
        final HttpSession oldSession = request.getSession();
        final Map<String, Object> temp = new ConcurrentHashMap<String, Object>();
        final Enumeration e = oldSession.getAttributeNames();
        while (e != null && e.hasMoreElements()) {
            final String name = e.nextElement();
            final Object value = oldSession.getAttribute(name);
            temp.put(name, value);
        }
        oldSession.invalidate();
        final HttpSession newSession = request.getSession();
        final User user = ESAPI.authenticator().getCurrentUser();
        user.addSession(newSession);
        user.removeSession(oldSession);
        for (final Map.Entry<String, Object> stringObjectEntry : temp.entrySet()) {
            newSession.setAttribute((String)stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
        return newSession;
    }
    
    @Override
    public void clearCurrent() {
        this.currentRequest.set(null);
        this.currentResponse.set(null);
    }
    
    private String createCookieHeader(final String name, final String value, final int maxAge, final String domain, final String path, final boolean secure) {
        String header = name + "=" + value;
        header = header + "; Max-Age=" + maxAge;
        if (domain != null) {
            header = header + "; Domain=" + domain;
        }
        if (path != null) {
            header = header + "; Path=" + path;
        }
        if (secure || ESAPI.securityConfiguration().getForceSecureCookies()) {
            header += "; Secure";
        }
        if (ESAPI.securityConfiguration().getForceHttpOnlyCookies()) {
            header += "; HttpOnly";
        }
        return header;
    }
    
    @Override
    public String decryptHiddenField(final String encrypted) {
        try {
            return this.decryptString(encrypted);
        }
        catch (final EncryptionException e) {
            throw new IntrusionException("Invalid request", "Tampering detected. Hidden field data did not decrypt properly.", e);
        }
    }
    
    @Override
    public Map<String, String> decryptQueryString(final String encrypted) throws EncryptionException {
        final String plaintext = this.decryptString(encrypted);
        return this.queryToMap(plaintext);
    }
    
    @Override
    public Map<String, String> decryptStateFromCookie() throws EncryptionException {
        return this.decryptStateFromCookie(this.getCurrentRequest());
    }
    
    @Override
    public Map<String, String> decryptStateFromCookie(final HttpServletRequest request) throws EncryptionException {
        try {
            final String encrypted = this.getCookie(request, "estate");
            if (encrypted == null) {
                return new HashMap<String, String>();
            }
            final String plaintext = this.decryptString(encrypted);
            return this.queryToMap(plaintext);
        }
        catch (final ValidationException e) {
            return null;
        }
    }
    
    @Override
    public String encryptHiddenField(final String value) throws EncryptionException {
        return this.encryptString(value);
    }
    
    @Override
    public String encryptQueryString(final String query) throws EncryptionException {
        return this.encryptString(query);
    }
    
    @Override
    public void encryptStateInCookie(final HttpServletResponse response, final Map<String, String> cleartext) throws EncryptionException {
        final StringBuilder sb = new StringBuilder();
        final Iterator i = cleartext.entrySet().iterator();
        while (i.hasNext()) {
            try {
                final Map.Entry entry = i.next();
                final String name = ESAPI.encoder().encodeForURL(entry.getKey().toString());
                final String value = ESAPI.encoder().encodeForURL(entry.getValue().toString());
                sb.append(name).append("=").append(value);
                if (!i.hasNext()) {
                    continue;
                }
                sb.append("&");
            }
            catch (final EncodingException e) {
                this.logger.error(Logger.SECURITY_FAILURE, "Problem encrypting state in cookie - skipping entry", e);
            }
        }
        final String encrypted = this.encryptString(sb.toString());
        if (encrypted.length() > 4096) {
            this.logger.error(Logger.SECURITY_FAILURE, "Problem encrypting state in cookie - skipping entry");
            throw new EncryptionException("Encryption failure", "Encrypted cookie state of " + encrypted.length() + " longer than allowed " + 4096);
        }
        final Cookie cookie = new Cookie("estate", encrypted);
        this.addCookie(response, cookie);
    }
    
    @Override
    public void encryptStateInCookie(final Map<String, String> cleartext) throws EncryptionException {
        this.encryptStateInCookie(this.getCurrentResponse(), cleartext);
    }
    
    @Override
    public String getCookie(final HttpServletRequest request, final String name) throws ValidationException {
        final Cookie c = this.getFirstCookie(request, name);
        if (c == null) {
            return null;
        }
        final String value = c.getValue();
        return ESAPI.validator().getValidInput("HTTP cookie value: " + value, value, "HTTPCookieValue", 1000, false);
    }
    
    @Override
    public String getCookie(final String name) throws ValidationException {
        return this.getCookie(this.getCurrentRequest(), name);
    }
    
    @Override
    public String getCSRFToken() {
        final User user = ESAPI.authenticator().getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getCSRFToken();
    }
    
    @Override
    public HttpServletRequest getCurrentRequest() {
        return this.currentRequest.getRequest();
    }
    
    @Override
    public HttpServletResponse getCurrentResponse() {
        return this.currentResponse.getResponse();
    }
    
    @Override
    public List<File> getFileUploads() throws ValidationException {
        return this.getFileUploads(this.getCurrentRequest(), ESAPI.securityConfiguration().getUploadDirectory(), ESAPI.securityConfiguration().getAllowedFileExtensions());
    }
    
    @Override
    public List<File> getFileUploads(final HttpServletRequest request) throws ValidationException {
        return this.getFileUploads(request, ESAPI.securityConfiguration().getUploadDirectory(), ESAPI.securityConfiguration().getAllowedFileExtensions());
    }
    
    @Override
    public List<File> getFileUploads(final HttpServletRequest request, final File finalDir) throws ValidationException {
        return this.getFileUploads(request, finalDir, ESAPI.securityConfiguration().getAllowedFileExtensions());
    }
    
    @Override
    public List<File> getFileUploads(final HttpServletRequest request, File finalDir, final List allowedExtensions) throws ValidationException {
        final File tempDir = ESAPI.securityConfiguration().getUploadTempDirectory();
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new ValidationUploadException("Upload failed", "Could not create temp directory: " + tempDir.getAbsolutePath());
        }
        if (finalDir != null) {
            if (!finalDir.exists() && !finalDir.mkdirs()) {
                throw new ValidationUploadException("Upload failed", "Could not create final upload directory: " + finalDir.getAbsolutePath());
            }
        }
        else {
            if (!ESAPI.securityConfiguration().getUploadDirectory().exists() && !ESAPI.securityConfiguration().getUploadDirectory().mkdirs()) {
                throw new ValidationUploadException("Upload failed", "Could not create final upload directory: " + ESAPI.securityConfiguration().getUploadDirectory().getAbsolutePath());
            }
            finalDir = ESAPI.securityConfiguration().getUploadDirectory();
        }
        final List<File> newFiles = new ArrayList<File>();
        try {
            final HttpSession session = request.getSession(false);
            if (!ServletFileUpload.isMultipartContent(request)) {
                throw new ValidationUploadException("Upload failed", "Not a multipart request");
            }
            final DiskFileItemFactory factory = new DiskFileItemFactory(0, tempDir);
            final ServletFileUpload upload = new ServletFileUpload((FileItemFactory)factory);
            upload.setSizeMax((long)DefaultHTTPUtilities.maxBytes);
            final ProgressListener progressListener = (ProgressListener)new ProgressListener() {
                private long megaBytes = -1L;
                private long progress = 0L;
                
                public void update(final long pBytesRead, final long pContentLength, final int pItems) {
                    if (pItems == 0) {
                        return;
                    }
                    final long mBytes = pBytesRead / 1000000L;
                    if (this.megaBytes == mBytes) {
                        return;
                    }
                    this.megaBytes = mBytes;
                    this.progress = (long)(pBytesRead / (double)pContentLength * 100.0);
                    if (session != null) {
                        session.setAttribute("progress", (Object)Long.toString(this.progress));
                    }
                }
            };
            upload.setProgressListener(progressListener);
            final List<FileItem> items = upload.parseRequest(request);
            for (final FileItem item : items) {
                if (!item.isFormField() && item.getName() != null && !item.getName().equals("")) {
                    final String[] fparts = item.getName().split("[\\/\\\\]");
                    final String filename = fparts[fparts.length - 1];
                    if (!ESAPI.validator().isValidFileName("upload", filename, allowedExtensions, false)) {
                        throw new ValidationUploadException("Upload only simple filenames with the following extensions " + allowedExtensions, "Upload failed isValidFileName check");
                    }
                    this.logger.info(Logger.SECURITY_SUCCESS, "File upload requested: " + filename);
                    File f = new File(finalDir, filename);
                    if (f.exists()) {
                        final String[] parts = filename.split("\\/.");
                        String extension = "";
                        if (parts.length > 1) {
                            extension = parts[parts.length - 1];
                        }
                        final String filenm = filename.substring(0, filename.length() - extension.length());
                        f = File.createTempFile(filenm, "." + extension, finalDir);
                    }
                    item.write(f);
                    newFiles.add(f);
                    item.delete();
                    this.logger.fatal(Logger.SECURITY_SUCCESS, "File successfully uploaded: " + f);
                    if (session == null) {
                        continue;
                    }
                    session.setAttribute("progress", (Object)Long.toString(0L));
                }
            }
        }
        catch (final Exception e) {
            if (e instanceof ValidationUploadException) {
                throw (ValidationException)e;
            }
            throw new ValidationUploadException("Upload failure", "Problem during upload:" + e.getMessage(), e);
        }
        return Collections.synchronizedList(newFiles);
    }
    
    private Cookie getFirstCookie(final HttpServletRequest request, final String name) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }
    
    @Override
    public String getHeader(final HttpServletRequest request, final String name) throws ValidationException {
        final String value = request.getHeader(name);
        return ESAPI.validator().getValidInput("HTTP header value: " + value, value, "HTTPHeaderValue", 150, false);
    }
    
    @Override
    public String getHeader(final String name) throws ValidationException {
        return this.getHeader(this.getCurrentRequest(), name);
    }
    
    @Override
    public String getParameter(final HttpServletRequest request, final String name) throws ValidationException {
        final String value = request.getParameter(name);
        return ESAPI.validator().getValidInput("HTTP parameter value: " + value, value, "HTTPParameterValue", 2000, true);
    }
    
    @Override
    public String getParameter(final String name) throws ValidationException {
        return this.getParameter(this.getCurrentRequest(), name);
    }
    
    @Override
    public void killAllCookies() {
        this.killAllCookies(this.getCurrentRequest(), this.getCurrentResponse());
    }
    
    @Override
    public void killAllCookies(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                this.killCookie(request, response, cookie.getName());
            }
        }
    }
    
    @Override
    public void killCookie(final HttpServletRequest request, final HttpServletResponse response, final String name) {
        String path = "//";
        String domain = "";
        final Cookie cookie = this.getFirstCookie(request, name);
        if (cookie != null) {
            path = cookie.getPath();
            domain = cookie.getDomain();
        }
        final Cookie deleter = new Cookie(name, "deleted");
        deleter.setMaxAge(0);
        if (domain != null) {
            deleter.setDomain(domain);
        }
        if (path != null) {
            deleter.setPath(path);
        }
        response.addCookie(deleter);
    }
    
    @Override
    public void killCookie(final String name) {
        this.killCookie(this.getCurrentRequest(), this.getCurrentResponse(), name);
    }
    
    @Override
    public void logHTTPRequest() {
        this.logHTTPRequest(this.getCurrentRequest(), this.logger, null);
    }
    
    @Override
    public void logHTTPRequest(final HttpServletRequest request, final Logger logger) {
        this.logHTTPRequest(request, logger, null);
    }
    
    @Override
    public void logHTTPRequest(final HttpServletRequest request, final Logger logger, final List parameterNamesToObfuscate) {
        final StringBuilder params = new StringBuilder();
        final Iterator i = request.getParameterMap().keySet().iterator();
        while (i.hasNext()) {
            final String key = i.next();
            final String[] value = request.getParameterMap().get(key);
            for (int j = 0; j < value.length; ++j) {
                params.append(key).append("=");
                if (parameterNamesToObfuscate != null && parameterNamesToObfuscate.contains(key)) {
                    params.append("********");
                }
                else {
                    params.append(value[j]);
                }
                if (j < value.length - 1) {
                    params.append("&");
                }
            }
            if (i.hasNext()) {
                params.append("&");
            }
        }
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cooky : cookies) {
                if (!cooky.getName().equals(ESAPI.securityConfiguration().getHttpSessionIdName())) {
                    params.append("+").append(cooky.getName()).append("=").append(cooky.getValue());
                }
            }
        }
        final String msg = request.getMethod() + " " + (Object)request.getRequestURL() + ((params.length() > 0) ? ("?" + (Object)params) : "");
        logger.info(Logger.SECURITY_SUCCESS, msg);
    }
    
    private Map<String, String> queryToMap(final String query) {
        final TreeMap<String, String> map = new TreeMap<String, String>();
        final String[] arr$;
        final String[] parts = arr$ = query.split("&");
        for (final String part : arr$) {
            try {
                final String[] nvpair = part.split("=");
                final String name = ESAPI.encoder().decodeFromURL(nvpair[0]);
                final String value = ESAPI.encoder().decodeFromURL(nvpair[1]);
                map.put(name, value);
            }
            catch (final EncodingException ex) {}
        }
        return map;
    }
    
    @Override
    public void sendForward(final HttpServletRequest request, final HttpServletResponse response, final String location) throws AccessControlException, ServletException, IOException {
        if (!location.startsWith("WEB-INF")) {
            throw new AccessControlException("Forward failed", "Bad forward location: " + location);
        }
        final RequestDispatcher dispatcher = request.getRequestDispatcher(location);
        dispatcher.forward((ServletRequest)request, (ServletResponse)response);
    }
    
    @Override
    public void sendForward(final String location) throws AccessControlException, ServletException, IOException {
        this.sendForward(this.getCurrentRequest(), this.getCurrentResponse(), location);
    }
    
    @Override
    public void sendRedirect(final HttpServletResponse response, final String location) throws AccessControlException, IOException {
        if (!ESAPI.validator().isValidRedirectLocation("Redirect", location, false)) {
            this.logger.fatal(Logger.SECURITY_FAILURE, "Bad redirect location: " + location);
            throw new AccessControlException("Redirect failed", "Bad redirect location: " + location);
        }
        response.sendRedirect(location);
    }
    
    @Override
    public void sendRedirect(final String location) throws AccessControlException, IOException {
        this.sendRedirect(this.getCurrentResponse(), location);
    }
    
    @Override
    public void setContentType() {
        this.setContentType(this.getCurrentResponse());
    }
    
    @Override
    public void setContentType(final HttpServletResponse response) {
        response.setContentType(ESAPI.securityConfiguration().getResponseContentType());
    }
    
    @Override
    public void setCurrentHTTP(final HttpServletRequest request, final HttpServletResponse response) {
        this.currentRequest.setRequest(request);
        this.currentResponse.setResponse(response);
    }
    
    @Override
    public void setHeader(final HttpServletResponse response, final String name, final String value) {
        try {
            final String strippedName = StringUtilities.replaceLinearWhiteSpace(name);
            final String strippedValue = StringUtilities.replaceLinearWhiteSpace(value);
            final String safeName = ESAPI.validator().getValidInput("setHeader", strippedName, "HTTPHeaderName", 50, false);
            final String safeValue = ESAPI.validator().getValidInput("setHeader", strippedValue, "HTTPHeaderValue", 500, false);
            response.setHeader(safeName, safeValue);
        }
        catch (final ValidationException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to set invalid header denied", e);
        }
    }
    
    @Override
    public void setHeader(final String name, final String value) {
        this.setHeader(this.getCurrentResponse(), name, value);
    }
    
    @Override
    public void setNoCacheHeaders() {
        this.setNoCacheHeaders(this.getCurrentResponse());
    }
    
    @Override
    public void setNoCacheHeaders(final HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1L);
    }
    
    @Override
    public String setRememberToken(final HttpServletRequest request, final HttpServletResponse response, final String password, final int maxAge, final String domain, final String path) {
        final User user = ESAPI.authenticator().getCurrentUser();
        try {
            this.killCookie(request, response, "rtoken");
            final String clearToken = user.getAccountName() + "|" + password;
            final long expiry = ESAPI.encryptor().getRelativeTimeStamp(maxAge * 1000);
            final String cryptToken = ESAPI.encryptor().seal(clearToken, expiry);
            final Cookie cookie = new Cookie("rtoken", cryptToken);
            cookie.setMaxAge(maxAge);
            cookie.setDomain(domain);
            cookie.setPath(path);
            response.addCookie(cookie);
            this.logger.info(Logger.SECURITY_SUCCESS, "Enabled remember me token for " + user.getAccountName());
            return cryptToken;
        }
        catch (final IntegrityException e) {
            this.logger.warning(Logger.SECURITY_FAILURE, "Attempt to set remember me token failed for " + user.getAccountName(), e);
            return null;
        }
    }
    
    @Override
    public String setRememberToken(final String password, final int maxAge, final String domain, final String path) {
        return this.setRememberToken(this.getCurrentRequest(), this.getCurrentResponse(), password, maxAge, domain, path);
    }
    
    @Override
    public void verifyCSRFToken() throws IntrusionException {
        this.verifyCSRFToken(this.getCurrentRequest());
    }
    
    @Override
    public void verifyCSRFToken(final HttpServletRequest request) throws IntrusionException {
        final User user = ESAPI.authenticator().getCurrentUser();
        if (request.getAttribute(user.getCSRFToken()) != null) {
            return;
        }
        final String token = request.getParameter("ctoken");
        if (!user.getCSRFToken().equals(token)) {
            throw new IntrusionException("Authentication failed", "Possibly forged HTTP request without proper CSRF token detected");
        }
    }
    
    @Override
    public <T> T getSessionAttribute(final String key) {
        final HttpSession session = ESAPI.currentRequest().getSession(false);
        if (session != null) {
            return (T)session.getAttribute(key);
        }
        return null;
    }
    
    @Override
    public <T> T getSessionAttribute(final HttpSession session, final String key) {
        return (T)session.getAttribute(key);
    }
    
    @Override
    public <T> T getRequestAttribute(final String key) {
        return (T)ESAPI.currentRequest().getAttribute(key);
    }
    
    @Override
    public <T> T getRequestAttribute(final HttpServletRequest request, final String key) {
        return (T)request.getAttribute(key);
    }
    
    private String encryptString(final String plaintext) throws EncryptionException {
        final PlainText pt = new PlainText(plaintext);
        final CipherText ct = ESAPI.encryptor().encrypt(pt);
        final byte[] serializedCiphertext = ct.asPortableSerializedByteArray();
        return Hex.encode(serializedCiphertext, false);
    }
    
    private String decryptString(final String ciphertext) throws EncryptionException {
        final byte[] serializedCiphertext = Hex.decode(ciphertext);
        final CipherText restoredCipherText = CipherText.fromPortableSerializedBytes(serializedCiphertext);
        final PlainText plaintext = ESAPI.encryptor().decrypt(restoredCipherText);
        return plaintext.toString();
    }
    
    static {
        DefaultHTTPUtilities.instance = null;
        maxBytes = ESAPI.securityConfiguration().getAllowedFileUploadSize();
    }
    
    private class ThreadLocalRequest extends InheritableThreadLocal<HttpServletRequest>
    {
        public HttpServletRequest getRequest() {
            return super.get();
        }
        
        public HttpServletRequest initialValue() {
            return null;
        }
        
        public void setRequest(final HttpServletRequest newRequest) {
            super.set(newRequest);
        }
    }
    
    private class ThreadLocalResponse extends InheritableThreadLocal<HttpServletResponse>
    {
        public HttpServletResponse getResponse() {
            return super.get();
        }
        
        public HttpServletResponse initialValue() {
            return null;
        }
        
        public void setResponse(final HttpServletResponse newResponse) {
            super.set(newResponse);
        }
    }
}
