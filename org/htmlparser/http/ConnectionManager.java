package org.htmlparser.http;

import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Date;
import java.net.MalformedURLException;
import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.io.IOException;
import java.net.UnknownHostException;
import org.htmlparser.util.ParserException;
import java.net.URLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Vector;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

public class ConnectionManager
{
    protected static Hashtable mDefaultRequestProperties;
    private static final String[] FOUR_OH_FOUR;
    private static final char[] BASE64_CHAR_TABLE;
    protected Hashtable mRequestProperties;
    protected String mProxyHost;
    protected int mProxyPort;
    protected String mProxyUser;
    protected String mProxyPassword;
    protected String mUser;
    protected String mPassword;
    protected Hashtable mCookieJar;
    protected ConnectionMonitor mMonitor;
    protected boolean mRedirectionProcessingEnabled;
    protected static SimpleDateFormat mFormat;
    
    public ConnectionManager() {
        this(getDefaultRequestProperties());
    }
    
    public ConnectionManager(final Hashtable properties) {
        this.mRequestProperties = properties;
        this.mProxyHost = null;
        this.mProxyPort = 0;
        this.mProxyUser = null;
        this.mProxyPassword = null;
        this.mUser = null;
        this.mPassword = null;
        this.mCookieJar = null;
        this.mMonitor = null;
        this.mRedirectionProcessingEnabled = false;
    }
    
    public static Hashtable getDefaultRequestProperties() {
        return ConnectionManager.mDefaultRequestProperties;
    }
    
    public static void setDefaultRequestProperties(final Hashtable properties) {
        ConnectionManager.mDefaultRequestProperties = properties;
    }
    
    public Hashtable getRequestProperties() {
        return this.mRequestProperties;
    }
    
    public void setRequestProperties(final Hashtable properties) {
        this.mRequestProperties = properties;
    }
    
    public String getProxyHost() {
        return this.mProxyHost;
    }
    
    public void setProxyHost(final String host) {
        this.mProxyHost = host;
    }
    
    public int getProxyPort() {
        return this.mProxyPort;
    }
    
    public void setProxyPort(final int port) {
        this.mProxyPort = port;
    }
    
    public String getProxyUser() {
        return this.mProxyUser;
    }
    
    public void setProxyUser(final String user) {
        this.mProxyUser = user;
    }
    
    public String getProxyPassword() {
        return this.mProxyPassword;
    }
    
    public void setProxyPassword(final String password) {
        this.mProxyPassword = password;
    }
    
    public String getUser() {
        return this.mUser;
    }
    
    public void setUser(final String user) {
        this.mUser = user;
    }
    
    public String getPassword() {
        return this.mPassword;
    }
    
    public void setPassword(final String password) {
        this.mPassword = password;
    }
    
    public boolean getCookieProcessingEnabled() {
        return null != this.mCookieJar;
    }
    
    public void setCookieProcessingEnabled(final boolean enable) {
        if (enable) {
            this.mCookieJar = ((null == this.mCookieJar) ? new Hashtable() : this.mCookieJar);
        }
        else {
            this.mCookieJar = null;
        }
    }
    
    public void setCookie(final Cookie cookie, String domain) {
        if (null != cookie.getDomain()) {
            domain = cookie.getDomain();
        }
        final String path = cookie.getPath();
        if (null == this.mCookieJar) {
            this.mCookieJar = new Hashtable();
        }
        Vector cookies = this.mCookieJar.get(domain);
        if (null != cookies) {
            boolean found = false;
            for (int j = 0; j < cookies.size(); ++j) {
                final Cookie probe = cookies.elementAt(j);
                if (probe.getName().equalsIgnoreCase(cookie.getName())) {
                    if (probe.getPath().equals(path)) {
                        cookies.setElementAt(cookie, j);
                        found = true;
                        break;
                    }
                    if (path.startsWith(probe.getPath())) {
                        cookies.insertElementAt(cookie, j);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                cookies.addElement(cookie);
            }
        }
        else {
            cookies = new Vector();
            cookies.addElement(cookie);
            this.mCookieJar.put(domain, cookies);
        }
    }
    
    public ConnectionMonitor getMonitor() {
        return this.mMonitor;
    }
    
    public void setMonitor(final ConnectionMonitor monitor) {
        this.mMonitor = monitor;
    }
    
    public boolean getRedirectionProcessingEnabled() {
        return this.mRedirectionProcessingEnabled;
    }
    
    public void setRedirectionProcessingEnabled(final boolean enabled) {
        this.mRedirectionProcessingEnabled = enabled;
    }
    
    protected String getLocation(final HttpURLConnection http) {
        String ret = null;
        String value;
        for (int i = 0; null == ret && null != (value = http.getHeaderField(i)); ++i) {
            final String key;
            if (null != (key = http.getHeaderFieldKey(i)) && key.equalsIgnoreCase("Location")) {
                ret = value;
            }
        }
        return ret;
    }
    
    public URLConnection openConnection(URL url) throws ParserException {
        String set = null;
        String host = null;
        String port = null;
        String host2 = null;
        String port2 = null;
        int repeated = 0;
        boolean repeat;
        URLConnection ret;
        do {
            repeat = false;
            try {
                try {
                    if (null != this.getProxyHost() && 0 != this.getProxyPort()) {
                        final Properties sysprops = System.getProperties();
                        set = ((Hashtable<String, String>)sysprops).put("proxySet", "true");
                        host = ((Hashtable<String, String>)sysprops).put("proxyHost", this.getProxyHost());
                        port = ((Hashtable<String, String>)sysprops).put("proxyPort", Integer.toString(this.getProxyPort()));
                        host2 = ((Hashtable<String, String>)sysprops).put("http.proxyHost", this.getProxyHost());
                        port2 = ((Hashtable<String, String>)sysprops).put("http.proxyPort", Integer.toString(this.getProxyPort()));
                        System.setProperties(sysprops);
                    }
                    ret = url.openConnection();
                    HttpURLConnection http;
                    if (ret instanceof HttpURLConnection) {
                        http = (HttpURLConnection)ret;
                        if (this.getRedirectionProcessingEnabled()) {
                            http.setInstanceFollowRedirects(false);
                        }
                        final Hashtable properties = this.getRequestProperties();
                        if (null != properties) {
                            final Enumeration enumeration = properties.keys();
                            while (enumeration.hasMoreElements()) {
                                final String key = enumeration.nextElement();
                                final String value = properties.get(key);
                                ret.setRequestProperty(key, value);
                            }
                        }
                        if (null != this.getProxyUser() && null != this.getProxyPassword()) {
                            final String auth = this.getProxyUser() + ":" + this.getProxyPassword();
                            final String encoded = encode(auth.getBytes("ISO-8859-1"));
                            ret.setRequestProperty("Proxy-Authorization", encoded);
                        }
                        if (null != this.getUser() && null != this.getPassword()) {
                            final String auth = this.getUser() + ":" + this.getPassword();
                            final String encoded = encode(auth.getBytes("ISO-8859-1"));
                            ret.setRequestProperty("Authorization", "Basic " + encoded);
                        }
                        if (this.getCookieProcessingEnabled()) {
                            this.addCookies(ret);
                        }
                        if (null != this.getMonitor()) {
                            this.getMonitor().preConnect(http);
                        }
                    }
                    else {
                        http = null;
                    }
                    try {
                        ret.connect();
                        if (null == http) {
                            continue;
                        }
                        if (null != this.getMonitor()) {
                            this.getMonitor().postConnect(http);
                        }
                        if (this.getCookieProcessingEnabled()) {
                            this.parseCookies(ret);
                        }
                        final int code = http.getResponseCode();
                        final String uri;
                        if (3 != code / 100 || repeated >= 20 || null == (uri = this.getLocation(http))) {
                            continue;
                        }
                        url = new URL(uri);
                        repeat = true;
                        ++repeated;
                    }
                    catch (final UnknownHostException uhe) {
                        final int message = (int)(Math.random() * ConnectionManager.FOUR_OH_FOUR.length);
                        throw new ParserException(ConnectionManager.FOUR_OH_FOUR[message], uhe);
                    }
                    catch (final IOException ioe) {
                        throw new ParserException(ioe.getMessage(), ioe);
                    }
                }
                finally {
                    if (null != this.getProxyHost() && 0 != this.getProxyPort()) {
                        final Properties sysprops = System.getProperties();
                        if (null != set) {
                            ((Hashtable<String, String>)sysprops).put("proxySet", set);
                        }
                        else {
                            sysprops.remove("proxySet");
                        }
                        if (null != host) {
                            ((Hashtable<String, String>)sysprops).put("proxyHost", host);
                        }
                        else {
                            sysprops.remove("proxyHost");
                        }
                        if (null != port) {
                            ((Hashtable<String, String>)sysprops).put("proxyPort", port);
                        }
                        else {
                            sysprops.remove("proxyPort");
                        }
                        if (null != host2) {
                            ((Hashtable<String, String>)sysprops).put("http.proxyHost", host2);
                        }
                        else {
                            sysprops.remove("http.proxyHost");
                        }
                        if (null != port2) {
                            ((Hashtable<String, String>)sysprops).put("http.proxyPort", port2);
                        }
                        else {
                            sysprops.remove("http.proxyPort");
                        }
                        System.setProperties(sysprops);
                    }
                }
            }
            catch (final IOException ioe) {
                final String msg = "Error in opening a connection to " + url.toExternalForm();
                final ParserException ex = new ParserException(msg, ioe);
                throw ex;
            }
        } while (repeat);
        return ret;
    }
    
    public static final String encode(final byte[] array) {
        String ret;
        if (null != array && 0 != array.length) {
            final int last = array.length - 1;
            final int count = last / 3 + 1 << 2;
            int separators = (count - 1) / 76;
            final int length = count + separators;
            final char[] encoded = new char[length];
            int index = 0;
            separators = 0;
            for (int i = 0; i <= last; i += 3) {
                final int left = last - i;
                final int end = (left > 1) ? 2 : left;
                int block = 0;
                int r = 16;
                for (int j = 0; j <= end; ++j) {
                    final int n = array[i + j];
                    block += ((n < 0) ? (n + 256) : n) << r;
                    r -= 8;
                }
                encoded[index++] = ConnectionManager.BASE64_CHAR_TABLE[block >>> 18 & 0x3F];
                encoded[index++] = ConnectionManager.BASE64_CHAR_TABLE[block >>> 12 & 0x3F];
                encoded[index++] = ((left > 0) ? ConnectionManager.BASE64_CHAR_TABLE[block >>> 6 & 0x3F] : '=');
                encoded[index++] = ((left > 1) ? ConnectionManager.BASE64_CHAR_TABLE[block & 0x3F] : '=');
                if (0 == (index - separators) % 76 && index < length) {
                    encoded[index++] = '\n';
                    ++separators;
                }
            }
            ret = new String(encoded);
        }
        else {
            ret = "";
        }
        return ret;
    }
    
    public String fixSpaces(String url) {
        final int index = url.indexOf(32);
        if (-1 != index) {
            final int length = url.length();
            final StringBuffer buffer = new StringBuffer(length * 3);
            buffer.append(url.substring(0, index));
            for (int i = index; i < length; ++i) {
                final char ch = url.charAt(i);
                if (ch == ' ') {
                    buffer.append("%20");
                }
                else {
                    buffer.append(ch);
                }
            }
            url = buffer.toString();
        }
        return url;
    }
    
    public URLConnection openConnection(final String string) throws ParserException {
        final String prefix = "file://localhost";
        URLConnection ret;
        try {
            final URL url = new URL(this.fixSpaces(string));
            ret = this.openConnection(url);
        }
        catch (final MalformedURLException murle) {
            try {
                final File file = new File(string);
                final String resource = file.getCanonicalPath();
                final StringBuffer buffer = new StringBuffer("file://localhost".length() + resource.length());
                buffer.append("file://localhost");
                if (!resource.startsWith("/")) {
                    buffer.append("/");
                }
                buffer.append(resource);
                final URL url = new URL(this.fixSpaces(buffer.toString()));
                ret = this.openConnection(url);
            }
            catch (final MalformedURLException murle2) {
                final String msg = "Error in opening a connection to " + string;
                final ParserException ex = new ParserException(msg, murle2);
                throw ex;
            }
            catch (final IOException ioe) {
                final String msg = "Error in opening a connection to " + string;
                final ParserException ex = new ParserException(msg, ioe);
                throw ex;
            }
        }
        return ret;
    }
    
    public void addCookies(final URLConnection connection) {
        if (null != this.mCookieJar) {
            Vector list = null;
            final URL url = connection.getURL();
            final String host = url.getHost();
            String path = url.getPath();
            if (0 == path.length()) {
                path = "/";
            }
            if (null != host) {
                list = this.addCookies(this.mCookieJar.get(host), path, list);
                final String domain = this.getDomain(host);
                if (null != domain) {
                    list = this.addCookies(this.mCookieJar.get(domain), path, list);
                }
                else {
                    list = this.addCookies(this.mCookieJar.get("." + host), path, list);
                }
            }
            if (null != list) {
                connection.setRequestProperty("Cookie", this.generateCookieProperty(list));
            }
        }
    }
    
    protected Vector addCookies(final Vector cookies, final String path, Vector list) {
        if (null != cookies) {
            final Date now = new Date();
            for (int i = 0; i < cookies.size(); ++i) {
                final Cookie cookie = cookies.elementAt(i);
                final Date expires = cookie.getExpiryDate();
                if (null != expires && expires.before(now)) {
                    cookies.remove(i);
                    --i;
                }
                else if (path.startsWith(cookie.getPath())) {
                    if (null == list) {
                        list = new Vector();
                    }
                    list.addElement(cookie);
                }
            }
        }
        return list;
    }
    
    protected String getDomain(final String host) {
        String ret = null;
        final StringTokenizer tokenizer = new StringTokenizer(host, ".");
        final int count = tokenizer.countTokens();
        if (3 <= count) {
            int length = host.length();
            boolean ok = false;
            for (int i = 0; i < length && !ok; ++i) {
                final char c = host.charAt(i);
                if (!Character.isDigit(c) && c != '.') {
                    ok = true;
                }
            }
            if (ok) {
                final String server = tokenizer.nextToken();
                length = server.length();
                ret = host.substring(length);
            }
        }
        return ret;
    }
    
    protected String generateCookieProperty(final Vector cookies) {
        String ret = null;
        final StringBuffer buffer = new StringBuffer();
        int version = 0;
        for (int i = 0; i < cookies.size(); ++i) {
            version = Math.max(version, cookies.elementAt(i).getVersion());
        }
        if (0 != version) {
            buffer.append("$Version=\"");
            buffer.append(version);
            buffer.append("\"");
        }
        for (int i = 0; i < cookies.size(); ++i) {
            final Cookie cookie = cookies.elementAt(i);
            if (0 != buffer.length()) {
                buffer.append("; ");
            }
            buffer.append(cookie.getName());
            buffer.append(cookie.getName().equals("") ? "" : "=");
            if (0 != version) {
                buffer.append("\"");
            }
            buffer.append(cookie.getValue());
            if (0 != version) {
                buffer.append("\"");
            }
            if (0 != version) {
                if (null != cookie.getPath() && 0 != cookie.getPath().length()) {
                    buffer.append("; $Path=\"");
                    buffer.append(cookie.getPath());
                    buffer.append("\"");
                }
                if (null != cookie.getDomain() && 0 != cookie.getDomain().length()) {
                    buffer.append("; $Domain=\"");
                    buffer.append(cookie.getDomain());
                    buffer.append("\"");
                }
            }
        }
        if (0 != buffer.length()) {
            ret = buffer.toString();
        }
        return ret;
    }
    
    public void parseCookies(final URLConnection connection) {
        final String string = connection.getHeaderField("Set-Cookie");
        if (null != string) {
            final Vector cookies = new Vector();
            final StringTokenizer tokenizer = new StringTokenizer(string, ";,", true);
            Cookie cookie = null;
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken().trim();
                if (token.equals(";")) {
                    continue;
                }
                if (token.equals(",")) {
                    cookie = null;
                }
                else {
                    final int index = token.indexOf(61);
                    String name;
                    String value;
                    String key;
                    if (-1 == index) {
                        if (null == cookie) {
                            name = "";
                            value = token;
                            key = name;
                        }
                        else {
                            name = token;
                            value = null;
                            key = name.toLowerCase();
                        }
                    }
                    else {
                        name = token.substring(0, index);
                        value = token.substring(index + 1);
                        key = name.toLowerCase();
                    }
                    if (null == cookie) {
                        try {
                            cookie = new Cookie(name, value);
                            cookies.addElement(cookie);
                            continue;
                        }
                        catch (final IllegalArgumentException iae) {
                            break;
                        }
                    }
                    if (key.equals("expires")) {
                        final String comma = tokenizer.nextToken();
                        final String rest = tokenizer.nextToken();
                        try {
                            final Date date = ConnectionManager.mFormat.parse(value + comma + rest);
                            cookie.setExpiryDate(date);
                        }
                        catch (final ParseException pe) {
                            cookie.setExpiryDate(null);
                        }
                    }
                    else if (key.equals("domain")) {
                        cookie.setDomain(value);
                    }
                    else if (key.equals("path")) {
                        cookie.setPath(value);
                    }
                    else if (key.equals("secure")) {
                        cookie.setSecure(true);
                    }
                    else if (key.equals("comment")) {
                        cookie.setComment(value);
                    }
                    else if (key.equals("version")) {
                        cookie.setVersion(Integer.parseInt(value));
                    }
                    else {
                        if (!key.equals("max-age")) {
                            try {
                                cookie = new Cookie(name, value);
                                cookies.addElement(cookie);
                                continue;
                            }
                            catch (final IllegalArgumentException iae) {}
                            break;
                        }
                        final Date date2 = new Date();
                        final long then = date2.getTime() + Integer.parseInt(value) * 1000;
                        date2.setTime(then);
                        cookie.setExpiryDate(date2);
                    }
                }
            }
            if (0 != cookies.size()) {
                this.saveCookies(cookies, connection);
            }
        }
    }
    
    protected void saveCookies(final Vector list, final URLConnection connection) {
        for (int i = 0; i < list.size(); ++i) {
            final Cookie cookie = list.elementAt(i);
            String domain = cookie.getDomain();
            if (null == domain) {
                domain = connection.getURL().getHost();
            }
            this.setCookie(cookie, domain);
        }
    }
    
    static {
        (ConnectionManager.mDefaultRequestProperties = new Hashtable()).put("User-Agent", "HTMLParser/1.6");
        ConnectionManager.mDefaultRequestProperties.put("Accept-Encoding", "gzip, deflate");
        FOUR_OH_FOUR = new String[] { "The web site you seek cannot be located, but countless more exist", "You step in the stream, but the water has moved on. This page is not here.", "Yesterday the page existed. Today it does not. The internet is like that.", "That page was so big. It might have been very useful. But now it is gone.", "Three things are certain: death, taxes and broken links. Guess which has occured.", "Chaos reigns within. Reflect, repent and enter the correct URL. Order shall return.", "Stay the patient course. Of little worth is your ire. The page is not found.", "A non-existant URL reduces your expensive computer to a simple stone.", "Many people have visited that page. Today, you are not one of the lucky ones.", "Cutting the wind with a knife. Bookmarking a URL. Both are ephemeral." };
        BASE64_CHAR_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        ConnectionManager.mFormat = new SimpleDateFormat("EEE, dd-MMM-yy kk:mm:ss z");
    }
}
