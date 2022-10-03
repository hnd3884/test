package jcifs.http;

import jcifs.Config;
import java.net.PasswordAuthentication;
import java.net.InetAddress;
import java.net.Authenticator;
import java.net.URLDecoder;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.NtlmMessage;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;
import jcifs.ntlmssp.Type1Message;
import java.net.ProtocolException;
import java.io.OutputStream;
import java.io.InputStream;
import java.security.Permission;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.io.IOException;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.net.HttpURLConnection;

public class NtlmHttpURLConnection extends HttpURLConnection
{
    private static final int MAX_REDIRECTS;
    private static final int LM_COMPATIBILITY;
    private static final String DEFAULT_DOMAIN;
    private HttpURLConnection connection;
    private Map requestProperties;
    private Map headerFields;
    private ByteArrayOutputStream cachedOutput;
    private String authProperty;
    private String authMethod;
    private boolean handshakeComplete;
    
    public NtlmHttpURLConnection(final HttpURLConnection connection) {
        super(connection.getURL());
        this.connection = connection;
        this.requestProperties = new HashMap();
    }
    
    public void connect() throws IOException {
        if (this.connected) {
            return;
        }
        this.connection.connect();
        this.connected = true;
    }
    
    private void handshake() throws IOException {
        if (this.handshakeComplete) {
            return;
        }
        this.doHandshake();
        this.handshakeComplete = true;
    }
    
    public URL getURL() {
        return this.connection.getURL();
    }
    
    public int getContentLength() {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getContentLength();
    }
    
    public String getContentType() {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getContentType();
    }
    
    public String getContentEncoding() {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getContentEncoding();
    }
    
    public long getExpiration() {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getExpiration();
    }
    
    public long getDate() {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getDate();
    }
    
    public long getLastModified() {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getLastModified();
    }
    
    public String getHeaderField(final String header) {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getHeaderField(header);
    }
    
    private Map getHeaderFields0() {
        if (this.headerFields != null) {
            return this.headerFields;
        }
        final Map map = new HashMap();
        String key = this.connection.getHeaderFieldKey(0);
        String value = this.connection.getHeaderField(0);
        for (int i = 1; key != null || value != null; key = this.connection.getHeaderFieldKey(i), value = this.connection.getHeaderField(i), ++i) {
            List values = map.get(key);
            if (values == null) {
                values = new ArrayList();
                map.put(key, values);
            }
            values.add(value);
        }
        final Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            final Map.Entry entry = entries.next();
            entry.setValue(Collections.unmodifiableList((List<?>)entry.getValue()));
        }
        return this.headerFields = Collections.unmodifiableMap((Map<?, ?>)map);
    }
    
    public Map getHeaderFields() {
        if (this.headerFields != null) {
            return this.headerFields;
        }
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.getHeaderFields0();
    }
    
    public int getHeaderFieldInt(final String header, final int def) {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getHeaderFieldInt(header, def);
    }
    
    public long getHeaderFieldDate(final String header, final long def) {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getHeaderFieldDate(header, def);
    }
    
    public String getHeaderFieldKey(final int index) {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getHeaderFieldKey(index);
    }
    
    public String getHeaderField(final int index) {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getHeaderField(index);
    }
    
    public Object getContent() throws IOException {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getContent();
    }
    
    public Object getContent(final Class[] classes) throws IOException {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getContent(classes);
    }
    
    public Permission getPermission() throws IOException {
        return this.connection.getPermission();
    }
    
    public InputStream getInputStream() throws IOException {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getInputStream();
    }
    
    public OutputStream getOutputStream() throws IOException {
        try {
            this.connect();
        }
        catch (final IOException ex) {}
        final OutputStream output = this.connection.getOutputStream();
        this.cachedOutput = new ByteArrayOutputStream();
        return new CacheStream(output, this.cachedOutput);
    }
    
    public String toString() {
        return this.connection.toString();
    }
    
    public void setDoInput(final boolean doInput) {
        this.connection.setDoInput(doInput);
        this.doInput = doInput;
    }
    
    public boolean getDoInput() {
        return this.connection.getDoInput();
    }
    
    public void setDoOutput(final boolean doOutput) {
        this.connection.setDoOutput(doOutput);
        this.doOutput = doOutput;
    }
    
    public boolean getDoOutput() {
        return this.connection.getDoOutput();
    }
    
    public void setAllowUserInteraction(final boolean allowUserInteraction) {
        this.connection.setAllowUserInteraction(allowUserInteraction);
        this.allowUserInteraction = allowUserInteraction;
    }
    
    public boolean getAllowUserInteraction() {
        return this.connection.getAllowUserInteraction();
    }
    
    public void setUseCaches(final boolean useCaches) {
        this.connection.setUseCaches(useCaches);
        this.useCaches = useCaches;
    }
    
    public boolean getUseCaches() {
        return this.connection.getUseCaches();
    }
    
    public void setIfModifiedSince(final long ifModifiedSince) {
        this.connection.setIfModifiedSince(ifModifiedSince);
        this.ifModifiedSince = ifModifiedSince;
    }
    
    public long getIfModifiedSince() {
        return this.connection.getIfModifiedSince();
    }
    
    public boolean getDefaultUseCaches() {
        return this.connection.getDefaultUseCaches();
    }
    
    public void setDefaultUseCaches(final boolean defaultUseCaches) {
        this.connection.setDefaultUseCaches(defaultUseCaches);
    }
    
    public void setRequestProperty(final String key, final String value) {
        if (key == null) {
            throw new NullPointerException();
        }
        final List values = new ArrayList();
        values.add(value);
        boolean found = false;
        final Iterator entries = this.requestProperties.entrySet().iterator();
        while (entries.hasNext()) {
            final Map.Entry entry = entries.next();
            if (key.equalsIgnoreCase(entry.getKey())) {
                entry.setValue(values);
                found = true;
                break;
            }
        }
        if (!found) {
            this.requestProperties.put(key, values);
        }
        this.connection.setRequestProperty(key, value);
    }
    
    public void addRequestProperty(final String key, final String value) {
        if (key == null) {
            throw new NullPointerException();
        }
        List values = null;
        final Iterator entries = this.requestProperties.entrySet().iterator();
        while (entries.hasNext()) {
            final Map.Entry entry = entries.next();
            if (key.equalsIgnoreCase(entry.getKey())) {
                values = entry.getValue();
                values.add(value);
                break;
            }
        }
        if (values == null) {
            values = new ArrayList();
            values.add(value);
            this.requestProperties.put(key, values);
        }
        final StringBuffer buffer = new StringBuffer();
        final Iterator propertyValues = values.iterator();
        while (propertyValues.hasNext()) {
            buffer.append(propertyValues.next());
            if (propertyValues.hasNext()) {
                buffer.append(", ");
            }
        }
        this.connection.setRequestProperty(key, buffer.toString());
    }
    
    public String getRequestProperty(final String key) {
        return this.connection.getRequestProperty(key);
    }
    
    public Map getRequestProperties() {
        final Map map = new HashMap();
        final Iterator entries = this.requestProperties.entrySet().iterator();
        while (entries.hasNext()) {
            final Map.Entry entry = entries.next();
            map.put(entry.getKey(), Collections.unmodifiableList((List<?>)entry.getValue()));
        }
        return Collections.unmodifiableMap((Map<?, ?>)map);
    }
    
    public void setInstanceFollowRedirects(final boolean instanceFollowRedirects) {
        this.connection.setInstanceFollowRedirects(instanceFollowRedirects);
    }
    
    public boolean getInstanceFollowRedirects() {
        return this.connection.getInstanceFollowRedirects();
    }
    
    public void setRequestMethod(final String requestMethod) throws ProtocolException {
        this.connection.setRequestMethod(requestMethod);
        this.method = requestMethod;
    }
    
    public String getRequestMethod() {
        return this.connection.getRequestMethod();
    }
    
    public int getResponseCode() throws IOException {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getResponseCode();
    }
    
    public String getResponseMessage() throws IOException {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getResponseMessage();
    }
    
    public void disconnect() {
        this.connection.disconnect();
        this.handshakeComplete = false;
        this.connected = false;
    }
    
    public boolean usingProxy() {
        return this.connection.usingProxy();
    }
    
    public InputStream getErrorStream() {
        try {
            this.handshake();
        }
        catch (final IOException ex) {}
        return this.connection.getErrorStream();
    }
    
    private int parseResponseCode() throws IOException {
        try {
            String response;
            int index;
            for (response = this.connection.getHeaderField(0), index = response.indexOf(32); response.charAt(index) == ' '; ++index) {}
            return Integer.parseInt(response.substring(index, index + 3));
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    private void doHandshake() throws IOException {
        this.connect();
        try {
            int response = this.parseResponseCode();
            if (response != 401 && response != 407) {
                return;
            }
            final Type1Message type1 = (Type1Message)this.attemptNegotiation(response);
            if (type1 == null) {
                return;
            }
            int attempt = 0;
            while (attempt < NtlmHttpURLConnection.MAX_REDIRECTS) {
                this.connection.setRequestProperty(this.authProperty, this.authMethod + ' ' + Base64.encode(type1.toByteArray()));
                this.connection.connect();
                response = this.parseResponseCode();
                if (response != 401 && response != 407) {
                    return;
                }
                final Type3Message type2 = (Type3Message)this.attemptNegotiation(response);
                if (type2 == null) {
                    return;
                }
                this.connection.setRequestProperty(this.authProperty, this.authMethod + ' ' + Base64.encode(type2.toByteArray()));
                this.connection.connect();
                if (this.cachedOutput != null && this.doOutput) {
                    final OutputStream output = this.connection.getOutputStream();
                    this.cachedOutput.writeTo(output);
                    output.flush();
                }
                response = this.parseResponseCode();
                if (response != 401 && response != 407) {
                    return;
                }
                ++attempt;
                if (!this.allowUserInteraction || attempt >= NtlmHttpURLConnection.MAX_REDIRECTS) {
                    break;
                }
                this.reconnect();
            }
            throw new IOException("Unable to negotiate NTLM authentication.");
        }
        finally {
            this.cachedOutput = null;
        }
    }
    
    private NtlmMessage attemptNegotiation(final int response) throws IOException {
        this.authProperty = null;
        this.authMethod = null;
        final InputStream errorStream = this.connection.getErrorStream();
        if (errorStream != null && errorStream.available() != 0) {
            final byte[] buf = new byte[1024];
            int count;
            while ((count = errorStream.read(buf, 0, 1024)) != -1) {}
        }
        String authHeader;
        if (response == 401) {
            authHeader = "WWW-Authenticate";
            this.authProperty = "Authorization";
        }
        else {
            authHeader = "Proxy-Authenticate";
            this.authProperty = "Proxy-Authorization";
        }
        String authorization = null;
        final List methods = this.getHeaderFields0().get(authHeader);
        if (methods == null) {
            return null;
        }
        final Iterator iterator = methods.iterator();
        while (iterator.hasNext()) {
            final String currentAuthMethod = iterator.next();
            if (currentAuthMethod.startsWith("NTLM")) {
                if (currentAuthMethod.length() == 4) {
                    this.authMethod = "NTLM";
                    break;
                }
                if (currentAuthMethod.indexOf(32) != 4) {
                    continue;
                }
                this.authMethod = "NTLM";
                authorization = currentAuthMethod.substring(5).trim();
                break;
            }
            else {
                if (!currentAuthMethod.startsWith("Negotiate")) {
                    continue;
                }
                if (currentAuthMethod.length() == 9) {
                    this.authMethod = "Negotiate";
                    break;
                }
                if (currentAuthMethod.indexOf(32) != 9) {
                    continue;
                }
                this.authMethod = "Negotiate";
                authorization = currentAuthMethod.substring(10).trim();
                break;
            }
        }
        if (this.authMethod == null) {
            return null;
        }
        NtlmMessage message = (authorization != null) ? new Type2Message(Base64.decode(authorization)) : null;
        this.reconnect();
        if (message == null) {
            message = new Type1Message();
            if (NtlmHttpURLConnection.LM_COMPATIBILITY > 2) {
                message.setFlag(4, true);
            }
        }
        else {
            String domain = NtlmHttpURLConnection.DEFAULT_DOMAIN;
            String user = Type3Message.getDefaultUser();
            String password = Type3Message.getDefaultPassword();
            String userInfo = this.url.getUserInfo();
            if (userInfo != null) {
                userInfo = URLDecoder.decode(userInfo);
                int index = userInfo.indexOf(58);
                user = ((index != -1) ? userInfo.substring(0, index) : userInfo);
                if (index != -1) {
                    password = userInfo.substring(index + 1);
                }
                index = user.indexOf(92);
                if (index == -1) {
                    index = user.indexOf(47);
                }
                domain = ((index != -1) ? user.substring(0, index) : domain);
                user = ((index != -1) ? user.substring(index + 1) : user);
            }
            if (user == null) {
                if (!this.allowUserInteraction) {
                    return null;
                }
                try {
                    final URL url = this.getURL();
                    final String protocol = url.getProtocol();
                    int port = url.getPort();
                    if (port == -1) {
                        port = ("https".equalsIgnoreCase(protocol) ? 443 : 80);
                    }
                    final PasswordAuthentication auth = Authenticator.requestPasswordAuthentication(null, port, protocol, "", this.authMethod);
                    if (auth == null) {
                        return null;
                    }
                    user = auth.getUserName();
                    password = new String(auth.getPassword());
                }
                catch (final Exception ex) {}
            }
            final Type2Message type2 = (Type2Message)message;
            message = new Type3Message(type2, password, domain, user, Type3Message.getDefaultWorkstation());
        }
        return message;
    }
    
    private void reconnect() throws IOException {
        (this.connection = (HttpURLConnection)this.connection.getURL().openConnection()).setRequestMethod(this.method);
        this.headerFields = null;
        final Iterator properties = this.requestProperties.entrySet().iterator();
        while (properties.hasNext()) {
            final Map.Entry property = properties.next();
            final String key = property.getKey();
            final StringBuffer value = new StringBuffer();
            final Iterator values = property.getValue().iterator();
            while (values.hasNext()) {
                value.append(values.next());
                if (values.hasNext()) {
                    value.append(", ");
                }
            }
            this.connection.setRequestProperty(key, value.toString());
        }
        this.connection.setAllowUserInteraction(this.allowUserInteraction);
        this.connection.setDoInput(this.doInput);
        this.connection.setDoOutput(this.doOutput);
        this.connection.setIfModifiedSince(this.ifModifiedSince);
        this.connection.setUseCaches(this.useCaches);
    }
    
    static {
        MAX_REDIRECTS = Integer.parseInt(System.getProperty("http.maxRedirects", "20"));
        LM_COMPATIBILITY = Config.getInt("jcifs.smb.lmCompatibility", 0);
        String domain = System.getProperty("http.auth.ntlm.domain");
        if (domain == null) {
            domain = Type3Message.getDefaultDomain();
        }
        DEFAULT_DOMAIN = domain;
    }
    
    private static class CacheStream extends OutputStream
    {
        private final OutputStream stream;
        private final OutputStream collector;
        
        public CacheStream(final OutputStream stream, final OutputStream collector) {
            this.stream = stream;
            this.collector = collector;
        }
        
        public void close() throws IOException {
            this.stream.close();
            this.collector.close();
        }
        
        public void flush() throws IOException {
            this.stream.flush();
            this.collector.flush();
        }
        
        public void write(final byte[] b) throws IOException {
            this.stream.write(b);
            this.collector.write(b);
        }
        
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.stream.write(b, off, len);
            this.collector.write(b, off, len);
        }
        
        public void write(final int b) throws IOException {
            this.stream.write(b);
            this.collector.write(b);
        }
    }
}
