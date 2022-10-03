package com.sun.xml.internal.messaging.saaj.client.p2p;

import com.sun.xml.internal.messaging.saaj.util.Base64;
import com.sun.xml.internal.messaging.saaj.util.ParseUtil;
import java.security.Security;
import java.security.Provider;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.net.HttpURLConnection;
import java.io.InputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import java.util.StringTokenizer;
import javax.xml.soap.MimeHeaders;
import java.io.IOException;
import javax.xml.soap.MimeHeader;
import java.net.URI;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.logging.Level;
import javax.xml.soap.MessageFactory;
import java.util.logging.Logger;
import javax.xml.soap.SOAPConnection;

class HttpSOAPConnection extends SOAPConnection
{
    public static final String vmVendor;
    private static final String sunVmVendor = "http://java.sun.com/";
    private static final String ibmVmVendor = "http://www.ibm.com/";
    private static final boolean isSunVM;
    private static final boolean isIBMVM;
    private static final String JAXM_URLENDPOINT = "javax.xml.messaging.URLEndpoint";
    protected static final Logger log;
    MessageFactory messageFactory;
    boolean closed;
    private static final String SSL_PKG;
    private static final String SSL_PROVIDER;
    private static final int dL = 0;
    
    public HttpSOAPConnection() throws SOAPException {
        this.messageFactory = null;
        this.closed = false;
        try {
            this.messageFactory = MessageFactory.newInstance("Dynamic Protocol");
        }
        catch (final NoSuchMethodError ex) {
            this.messageFactory = MessageFactory.newInstance();
        }
        catch (final Exception ex2) {
            HttpSOAPConnection.log.log(Level.SEVERE, "SAAJ0001.p2p.cannot.create.msg.factory", ex2);
            throw new SOAPExceptionImpl("Unable to create message factory", ex2);
        }
    }
    
    @Override
    public void close() throws SOAPException {
        if (this.closed) {
            HttpSOAPConnection.log.severe("SAAJ0002.p2p.close.already.closed.conn");
            throw new SOAPExceptionImpl("Connection already closed");
        }
        this.messageFactory = null;
        this.closed = true;
    }
    
    @Override
    public SOAPMessage call(final SOAPMessage message, Object endPoint) throws SOAPException {
        if (this.closed) {
            HttpSOAPConnection.log.severe("SAAJ0003.p2p.call.already.closed.conn");
            throw new SOAPExceptionImpl("Connection is closed");
        }
        Class urlEndpointClass = null;
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            if (loader != null) {
                urlEndpointClass = loader.loadClass("javax.xml.messaging.URLEndpoint");
            }
            else {
                urlEndpointClass = Class.forName("javax.xml.messaging.URLEndpoint");
            }
        }
        catch (final ClassNotFoundException ex) {
            if (HttpSOAPConnection.log.isLoggable(Level.FINEST)) {
                HttpSOAPConnection.log.finest("SAAJ0090.p2p.endpoint.available.only.for.JAXM");
            }
        }
        if (urlEndpointClass != null && urlEndpointClass.isInstance(endPoint)) {
            String url = null;
            try {
                final Method m = urlEndpointClass.getMethod("getURL", (Class[])null);
                url = (String)m.invoke(endPoint, (Object[])null);
            }
            catch (final Exception ex2) {
                HttpSOAPConnection.log.log(Level.SEVERE, "SAAJ0004.p2p.internal.err", ex2);
                throw new SOAPExceptionImpl("Internal error: " + ex2.getMessage());
            }
            try {
                endPoint = new URL(url);
            }
            catch (final MalformedURLException mex) {
                HttpSOAPConnection.log.log(Level.SEVERE, "SAAJ0005.p2p.", mex);
                throw new SOAPExceptionImpl("Bad URL: " + mex.getMessage());
            }
        }
        if (endPoint instanceof String) {
            try {
                endPoint = new URL((String)endPoint);
            }
            catch (final MalformedURLException mex2) {
                HttpSOAPConnection.log.log(Level.SEVERE, "SAAJ0006.p2p.bad.URL", mex2);
                throw new SOAPExceptionImpl("Bad URL: " + mex2.getMessage());
            }
        }
        if (endPoint instanceof URL) {
            try {
                final SOAPMessage response = this.post(message, (URL)endPoint);
                return response;
            }
            catch (final Exception ex3) {
                throw new SOAPExceptionImpl(ex3);
            }
        }
        HttpSOAPConnection.log.severe("SAAJ0007.p2p.bad.endPoint.type");
        throw new SOAPExceptionImpl("Bad endPoint type " + endPoint);
    }
    
    SOAPMessage post(final SOAPMessage message, final URL endPoint) throws SOAPException, IOException {
        boolean isFailure = false;
        URL url = null;
        HttpURLConnection httpConnection = null;
        int responseCode = 0;
        try {
            if (endPoint.getProtocol().equals("https")) {
                this.initHttps();
            }
            final URI uri = new URI(endPoint.toString());
            final String userInfo = uri.getRawUserInfo();
            url = endPoint;
            if (!url.getProtocol().equalsIgnoreCase("http") && !url.getProtocol().equalsIgnoreCase("https")) {
                HttpSOAPConnection.log.severe("SAAJ0052.p2p.protocol.mustbe.http.or.https");
                throw new IllegalArgumentException("Protocol " + url.getProtocol() + " not supported in URL " + url);
            }
            httpConnection = this.createConnection(url);
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setInstanceFollowRedirects(true);
            if (message.saveRequired()) {
                message.saveChanges();
            }
            final MimeHeaders headers = message.getMimeHeaders();
            final Iterator it = headers.getAllHeaders();
            boolean hasAuth = false;
            while (it.hasNext()) {
                final MimeHeader header = it.next();
                final String[] values = headers.getHeader(header.getName());
                if (values.length == 1) {
                    httpConnection.setRequestProperty(header.getName(), header.getValue());
                }
                else {
                    final StringBuffer concat = new StringBuffer();
                    for (int i = 0; i < values.length; ++i) {
                        if (i != 0) {
                            concat.append(',');
                        }
                        concat.append(values[i]);
                    }
                    httpConnection.setRequestProperty(header.getName(), concat.toString());
                }
                if ("Authorization".equals(header.getName())) {
                    hasAuth = true;
                    if (!HttpSOAPConnection.log.isLoggable(Level.FINE)) {
                        continue;
                    }
                    HttpSOAPConnection.log.fine("SAAJ0091.p2p.https.auth.in.POST.true");
                }
            }
            if (!hasAuth && userInfo != null) {
                this.initAuthUserInfo(httpConnection, userInfo);
            }
            final OutputStream out = httpConnection.getOutputStream();
            try {
                message.writeTo(out);
                out.flush();
            }
            finally {
                out.close();
            }
            httpConnection.connect();
            try {
                responseCode = httpConnection.getResponseCode();
                if (responseCode == 500) {
                    isFailure = true;
                }
                else if (responseCode / 100 != 2) {
                    HttpSOAPConnection.log.log(Level.SEVERE, "SAAJ0008.p2p.bad.response", new String[] { httpConnection.getResponseMessage() });
                    throw new SOAPExceptionImpl("Bad response: (" + responseCode + httpConnection.getResponseMessage());
                }
            }
            catch (final IOException e) {
                responseCode = httpConnection.getResponseCode();
                if (responseCode != 500) {
                    throw e;
                }
                isFailure = true;
            }
        }
        catch (final SOAPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            HttpSOAPConnection.log.severe("SAAJ0009.p2p.msg.send.failed");
            throw new SOAPExceptionImpl("Message send failed", ex2);
        }
        SOAPMessage response = null;
        InputStream httpIn = null;
        if (responseCode != 200) {
            if (!isFailure) {
                return response;
            }
        }
        try {
            final MimeHeaders headers = new MimeHeaders();
            int j = 1;
            while (true) {
                final String key = httpConnection.getHeaderFieldKey(j);
                final String value = httpConnection.getHeaderField(j);
                if (key == null && value == null) {
                    break;
                }
                if (key != null) {
                    final StringTokenizer values2 = new StringTokenizer(value, ",");
                    while (values2.hasMoreTokens()) {
                        headers.addHeader(key, values2.nextToken().trim());
                    }
                }
                ++j;
            }
            httpIn = (isFailure ? httpConnection.getErrorStream() : httpConnection.getInputStream());
            final byte[] bytes = this.readFully(httpIn);
            final int length = (httpConnection.getContentLength() == -1) ? bytes.length : httpConnection.getContentLength();
            if (length == 0) {
                response = null;
                HttpSOAPConnection.log.warning("SAAJ0014.p2p.content.zero");
            }
            else {
                final ByteInputStream in = new ByteInputStream(bytes, length);
                response = this.messageFactory.createMessage(headers, in);
            }
        }
        catch (final SOAPException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            HttpSOAPConnection.log.log(Level.SEVERE, "SAAJ0010.p2p.cannot.read.resp", ex4);
            throw new SOAPExceptionImpl("Unable to read response: " + ex4.getMessage());
        }
        finally {
            if (httpIn != null) {
                httpIn.close();
            }
            httpConnection.disconnect();
        }
        return response;
    }
    
    @Override
    public SOAPMessage get(Object endPoint) throws SOAPException {
        if (this.closed) {
            HttpSOAPConnection.log.severe("SAAJ0011.p2p.get.already.closed.conn");
            throw new SOAPExceptionImpl("Connection is closed");
        }
        Class urlEndpointClass = null;
        try {
            urlEndpointClass = Class.forName("javax.xml.messaging.URLEndpoint");
        }
        catch (final Exception ex3) {}
        if (urlEndpointClass != null && urlEndpointClass.isInstance(endPoint)) {
            String url = null;
            try {
                final Method m = urlEndpointClass.getMethod("getURL", (Class[])null);
                url = (String)m.invoke(endPoint, (Object[])null);
            }
            catch (final Exception ex) {
                HttpSOAPConnection.log.severe("SAAJ0004.p2p.internal.err");
                throw new SOAPExceptionImpl("Internal error: " + ex.getMessage());
            }
            try {
                endPoint = new URL(url);
            }
            catch (final MalformedURLException mex) {
                HttpSOAPConnection.log.severe("SAAJ0005.p2p.");
                throw new SOAPExceptionImpl("Bad URL: " + mex.getMessage());
            }
        }
        if (endPoint instanceof String) {
            try {
                endPoint = new URL((String)endPoint);
            }
            catch (final MalformedURLException mex2) {
                HttpSOAPConnection.log.severe("SAAJ0006.p2p.bad.URL");
                throw new SOAPExceptionImpl("Bad URL: " + mex2.getMessage());
            }
        }
        if (endPoint instanceof URL) {
            try {
                final SOAPMessage response = this.doGet((URL)endPoint);
                return response;
            }
            catch (final Exception ex2) {
                throw new SOAPExceptionImpl(ex2);
            }
        }
        throw new SOAPExceptionImpl("Bad endPoint type " + endPoint);
    }
    
    SOAPMessage doGet(final URL endPoint) throws SOAPException, IOException {
        boolean isFailure = false;
        URL url = null;
        HttpURLConnection httpConnection = null;
        int responseCode = 0;
        try {
            if (endPoint.getProtocol().equals("https")) {
                this.initHttps();
            }
            final URI uri = new URI(endPoint.toString());
            final String userInfo = uri.getRawUserInfo();
            url = endPoint;
            if (!url.getProtocol().equalsIgnoreCase("http") && !url.getProtocol().equalsIgnoreCase("https")) {
                HttpSOAPConnection.log.severe("SAAJ0052.p2p.protocol.mustbe.http.or.https");
                throw new IllegalArgumentException("Protocol " + url.getProtocol() + " not supported in URL " + url);
            }
            httpConnection = this.createConnection(url);
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setUseCaches(false);
            HttpURLConnection.setFollowRedirects(true);
            httpConnection.connect();
            try {
                responseCode = httpConnection.getResponseCode();
                if (responseCode == 500) {
                    isFailure = true;
                }
                else if (responseCode / 100 != 2) {
                    HttpSOAPConnection.log.log(Level.SEVERE, "SAAJ0008.p2p.bad.response", new String[] { httpConnection.getResponseMessage() });
                    throw new SOAPExceptionImpl("Bad response: (" + responseCode + httpConnection.getResponseMessage());
                }
            }
            catch (final IOException e) {
                responseCode = httpConnection.getResponseCode();
                if (responseCode != 500) {
                    throw e;
                }
                isFailure = true;
            }
        }
        catch (final SOAPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            HttpSOAPConnection.log.severe("SAAJ0012.p2p.get.failed");
            throw new SOAPExceptionImpl("Get failed", ex2);
        }
        SOAPMessage response = null;
        InputStream httpIn = null;
        if (responseCode != 200) {
            if (!isFailure) {
                return response;
            }
        }
        try {
            final MimeHeaders headers = new MimeHeaders();
            int i = 1;
            while (true) {
                final String key = httpConnection.getHeaderFieldKey(i);
                final String value = httpConnection.getHeaderField(i);
                if (key == null && value == null) {
                    break;
                }
                if (key != null) {
                    final StringTokenizer values = new StringTokenizer(value, ",");
                    while (values.hasMoreTokens()) {
                        headers.addHeader(key, values.nextToken().trim());
                    }
                }
                ++i;
            }
            httpIn = (isFailure ? httpConnection.getErrorStream() : httpConnection.getInputStream());
            if (httpIn == null || httpConnection.getContentLength() == 0 || httpIn.available() == 0) {
                response = null;
                HttpSOAPConnection.log.warning("SAAJ0014.p2p.content.zero");
            }
            else {
                response = this.messageFactory.createMessage(headers, httpIn);
            }
        }
        catch (final SOAPException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            HttpSOAPConnection.log.log(Level.SEVERE, "SAAJ0010.p2p.cannot.read.resp", ex4);
            throw new SOAPExceptionImpl("Unable to read response: " + ex4.getMessage());
        }
        finally {
            if (httpIn != null) {
                httpIn.close();
            }
            httpConnection.disconnect();
        }
        return response;
    }
    
    private byte[] readFully(final InputStream istream) throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final byte[] buf = new byte[1024];
        int num = 0;
        while ((num = istream.read(buf)) != -1) {
            bout.write(buf, 0, num);
        }
        final byte[] ret = bout.toByteArray();
        return ret;
    }
    
    private void initHttps() {
        String pkgs = SAAJUtil.getSystemProperty("java.protocol.handler.pkgs");
        if (HttpSOAPConnection.log.isLoggable(Level.FINE)) {
            HttpSOAPConnection.log.log(Level.FINE, "SAAJ0053.p2p.providers", new String[] { pkgs });
        }
        if (pkgs == null || pkgs.indexOf(HttpSOAPConnection.SSL_PKG) < 0) {
            if (pkgs == null) {
                pkgs = HttpSOAPConnection.SSL_PKG;
            }
            else {
                pkgs = pkgs + "|" + HttpSOAPConnection.SSL_PKG;
            }
            System.setProperty("java.protocol.handler.pkgs", pkgs);
            if (HttpSOAPConnection.log.isLoggable(Level.FINE)) {
                HttpSOAPConnection.log.log(Level.FINE, "SAAJ0054.p2p.set.providers", new String[] { pkgs });
            }
            try {
                final Class c = Class.forName(HttpSOAPConnection.SSL_PROVIDER);
                final Provider p = c.newInstance();
                Security.addProvider(p);
                if (HttpSOAPConnection.log.isLoggable(Level.FINE)) {
                    HttpSOAPConnection.log.log(Level.FINE, "SAAJ0055.p2p.added.ssl.provider", new String[] { HttpSOAPConnection.SSL_PROVIDER });
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    private void initAuthUserInfo(final HttpURLConnection conn, final String userInfo) {
        if (userInfo != null) {
            int delimiter = userInfo.indexOf(58);
            String user;
            String password;
            if (delimiter == -1) {
                user = ParseUtil.decode(userInfo);
                password = null;
            }
            else {
                user = ParseUtil.decode(userInfo.substring(0, delimiter++));
                password = ParseUtil.decode(userInfo.substring(delimiter));
            }
            final String plain = user + ":";
            final byte[] nameBytes = plain.getBytes();
            final byte[] passwdBytes = password.getBytes();
            final byte[] concat = new byte[nameBytes.length + passwdBytes.length];
            System.arraycopy(nameBytes, 0, concat, 0, nameBytes.length);
            System.arraycopy(passwdBytes, 0, concat, nameBytes.length, passwdBytes.length);
            final String auth = "Basic " + new String(Base64.encode(concat));
            conn.setRequestProperty("Authorization", auth);
        }
    }
    
    private void d(final String s) {
        HttpSOAPConnection.log.log(Level.SEVERE, "SAAJ0013.p2p.HttpSOAPConnection", new String[] { s });
        System.err.println("HttpSOAPConnection: " + s);
    }
    
    private HttpURLConnection createConnection(final URL endpoint) throws IOException {
        return (HttpURLConnection)endpoint.openConnection();
    }
    
    static {
        vmVendor = SAAJUtil.getSystemProperty("java.vendor.url");
        isSunVM = "http://java.sun.com/".equals(HttpSOAPConnection.vmVendor);
        isIBMVM = "http://www.ibm.com/".equals(HttpSOAPConnection.vmVendor);
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.client.p2p", "com.sun.xml.internal.messaging.saaj.client.p2p.LocalStrings");
        if (HttpSOAPConnection.isIBMVM) {
            SSL_PKG = "com.ibm.net.ssl.internal.www.protocol";
            SSL_PROVIDER = "com.ibm.net.ssl.internal.ssl.Provider";
        }
        else {
            SSL_PKG = "com.sun.net.ssl.internal.www.protocol";
            SSL_PROVIDER = "com.sun.net.ssl.internal.ssl.Provider";
        }
    }
}
