package javax.management.remote;

import java.util.StringTokenizer;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import com.sun.jmx.remote.util.ClassLogger;
import java.util.BitSet;
import java.io.Serializable;

public class JMXServiceURL implements Serializable
{
    private static final long serialVersionUID = 8173364409860779292L;
    private static final String INVALID_INSTANCE_MSG = "Trying to deserialize an invalid instance of JMXServiceURL";
    private static final Exception randomException;
    private static final BitSet alphaBitSet;
    private static final BitSet numericBitSet;
    private static final BitSet alphaNumericBitSet;
    private static final BitSet protocolBitSet;
    private static final BitSet hostNameBitSet;
    private String protocol;
    private String host;
    private int port;
    private String urlPath;
    private transient String toString;
    private static final ClassLogger logger;
    
    public JMXServiceURL(final String s) throws MalformedURLException {
        final int length = s.length();
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 < ' ' || char1 >= '\u007f') {
                throw new MalformedURLException("Service URL contains non-ASCII character 0x" + Integer.toHexString(char1));
            }
        }
        final int length2 = "service:jmx:".length();
        if (!s.regionMatches(true, 0, "service:jmx:", 0, length2)) {
            throw new MalformedURLException("Service URL must start with service:jmx:");
        }
        final int n = length2;
        final int index = indexOf(s, ':', n);
        this.protocol = s.substring(n, index).toLowerCase();
        if (!s.regionMatches(index, "://", 0, 3)) {
            throw new MalformedURLException("Missing \"://\" after protocol name");
        }
        final int n2 = index + 3;
        int indexOfFirstNotInSet;
        if (n2 < length && s.charAt(n2) == '[') {
            indexOfFirstNotInSet = s.indexOf(93, n2) + 1;
            if (indexOfFirstNotInSet == 0) {
                throw new MalformedURLException("Bad host name: [ without ]");
            }
            this.host = s.substring(n2 + 1, indexOfFirstNotInSet - 1);
            if (!isNumericIPv6Address(this.host)) {
                throw new MalformedURLException("Address inside [...] must be numeric IPv6 address");
            }
        }
        else {
            indexOfFirstNotInSet = indexOfFirstNotInSet(s, JMXServiceURL.hostNameBitSet, n2);
            this.host = s.substring(n2, indexOfFirstNotInSet);
        }
        int indexOfFirstNotInSet2;
        if (indexOfFirstNotInSet < length && s.charAt(indexOfFirstNotInSet) == ':') {
            if (this.host.length() == 0) {
                throw new MalformedURLException("Cannot give port number without host name");
            }
            final int n3 = indexOfFirstNotInSet + 1;
            indexOfFirstNotInSet2 = indexOfFirstNotInSet(s, JMXServiceURL.numericBitSet, n3);
            final String substring = s.substring(n3, indexOfFirstNotInSet2);
            try {
                this.port = Integer.parseInt(substring);
            }
            catch (final NumberFormatException ex) {
                throw new MalformedURLException("Bad port number: \"" + substring + "\": " + ex);
            }
        }
        else {
            indexOfFirstNotInSet2 = indexOfFirstNotInSet;
            this.port = 0;
        }
        final int n4 = indexOfFirstNotInSet2;
        if (n4 < length) {
            this.urlPath = s.substring(n4);
        }
        else {
            this.urlPath = "";
        }
        this.validate();
    }
    
    public JMXServiceURL(final String s, final String s2, final int n) throws MalformedURLException {
        this(s, s2, n, null);
    }
    
    public JMXServiceURL(String s, String host, final int port, String urlPath) throws MalformedURLException {
        if (s == null) {
            s = "jmxmp";
        }
        if (host == null) {
            InetAddress localHost;
            try {
                localHost = InetAddress.getLocalHost();
            }
            catch (final UnknownHostException ex) {
                throw new MalformedURLException("Local host name unknown: " + ex);
            }
            host = localHost.getHostName();
            try {
                validateHost(host, port);
            }
            catch (final MalformedURLException ex2) {
                if (JMXServiceURL.logger.fineOn()) {
                    JMXServiceURL.logger.fine("JMXServiceURL", "Replacing illegal local host name " + host + " with numeric IP address (see RFC 1034)", ex2);
                }
                host = localHost.getHostAddress();
            }
        }
        if (host.startsWith("[")) {
            if (!host.endsWith("]")) {
                throw new MalformedURLException("Host starts with [ but does not end with ]");
            }
            host = host.substring(1, host.length() - 1);
            if (!isNumericIPv6Address(host)) {
                throw new MalformedURLException("Address inside [...] must be numeric IPv6 address");
            }
            if (host.startsWith("[")) {
                throw new MalformedURLException("More than one [[...]]");
            }
        }
        this.protocol = s.toLowerCase();
        this.host = host;
        this.port = port;
        if (urlPath == null) {
            urlPath = "";
        }
        this.urlPath = urlPath;
        this.validate();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final String host = (String)fields.get("host", null);
        final int value = fields.get("port", -1);
        final String protocol = (String)fields.get("protocol", null);
        final String urlPath = (String)fields.get("urlPath", null);
        if (protocol == null || urlPath == null || host == null) {
            final StringBuilder append = new StringBuilder("Trying to deserialize an invalid instance of JMXServiceURL").append('[');
            int n = 1;
            if (protocol == null) {
                append.append("protocol=null");
                n = 0;
            }
            if (host == null) {
                append.append((n != 0) ? "" : ",").append("host=null");
                n = 0;
            }
            if (urlPath == null) {
                append.append((n != 0) ? "" : ",").append("urlPath=null");
            }
            append.append(']');
            throw new InvalidObjectException(append.toString());
        }
        if (host.contains("[") || host.contains("]")) {
            throw new InvalidObjectException("Invalid host name: " + host);
        }
        try {
            this.validate(protocol, host, value, urlPath);
            this.protocol = protocol;
            this.host = host;
            this.port = value;
            this.urlPath = urlPath;
        }
        catch (final MalformedURLException ex) {
            throw new InvalidObjectException("Trying to deserialize an invalid instance of JMXServiceURL: " + ex.getMessage());
        }
    }
    
    private void validate(final String s, final String s2, final int n, final String s3) throws MalformedURLException {
        final int indexOfFirstNotInSet = indexOfFirstNotInSet(s, JMXServiceURL.protocolBitSet, 0);
        if (indexOfFirstNotInSet == 0 || indexOfFirstNotInSet < s.length() || !JMXServiceURL.alphaBitSet.get(s.charAt(0))) {
            throw new MalformedURLException("Missing or invalid protocol name: \"" + s + "\"");
        }
        validateHost(s2, n);
        if (n < 0) {
            throw new MalformedURLException("Bad port: " + n);
        }
        if (s3.length() > 0 && !s3.startsWith("/") && !s3.startsWith(";")) {
            throw new MalformedURLException("Bad URL path: " + s3);
        }
    }
    
    private void validate() throws MalformedURLException {
        this.validate(this.protocol, this.host, this.port, this.urlPath);
    }
    
    private static void validateHost(final String s, final int n) throws MalformedURLException {
        if (s.length() != 0) {
            if (isNumericIPv6Address(s)) {
                try {
                    InetAddress.getByName(s);
                    return;
                }
                catch (final Exception ex) {
                    final MalformedURLException ex2 = new MalformedURLException("Bad IPv6 address: " + s);
                    EnvHelp.initCause(ex2, ex);
                    throw ex2;
                }
            }
            final int length = s.length();
            int n2 = 46;
            boolean b = false;
            int n3 = 0;
            for (int i = 0; i < length; ++i) {
                final char char1 = s.charAt(i);
                final boolean value = JMXServiceURL.alphaNumericBitSet.get(char1);
                if (n2 == 46) {
                    n3 = char1;
                }
                if (value) {
                    n2 = 97;
                }
                else if (char1 == '-') {
                    if (n2 == 46) {
                        break;
                    }
                    n2 = 45;
                }
                else {
                    if (char1 != '.') {
                        n2 = 46;
                        break;
                    }
                    b = true;
                    if (n2 != 97) {
                        break;
                    }
                    n2 = 46;
                }
            }
            try {
                if (n2 != 97) {
                    throw JMXServiceURL.randomException;
                }
                if (b && !JMXServiceURL.alphaBitSet.get(n3)) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(s, ".", true);
                    for (int j = 0; j < 4; ++j) {
                        final int int1 = Integer.parseInt(stringTokenizer.nextToken());
                        if (int1 < 0 || int1 > 255) {
                            throw JMXServiceURL.randomException;
                        }
                        if (j < 3 && !stringTokenizer.nextToken().equals(".")) {
                            throw JMXServiceURL.randomException;
                        }
                    }
                    if (stringTokenizer.hasMoreTokens()) {
                        throw JMXServiceURL.randomException;
                    }
                }
            }
            catch (final Exception ex3) {
                throw new MalformedURLException("Bad host: \"" + s + "\"");
            }
            return;
        }
        if (n != 0) {
            throw new MalformedURLException("Cannot give port number without host name");
        }
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getURLPath() {
        return this.urlPath;
    }
    
    @Override
    public String toString() {
        if (this.toString != null) {
            return this.toString;
        }
        final StringBuilder sb = new StringBuilder("service:jmx:");
        sb.append(this.getProtocol()).append("://");
        final String host = this.getHost();
        if (isNumericIPv6Address(host)) {
            sb.append('[').append(host).append(']');
        }
        else {
            sb.append(host);
        }
        final int port = this.getPort();
        if (port != 0) {
            sb.append(':').append(port);
        }
        sb.append(this.getURLPath());
        return this.toString = sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof JMXServiceURL)) {
            return false;
        }
        final JMXServiceURL jmxServiceURL = (JMXServiceURL)o;
        return jmxServiceURL.getProtocol().equalsIgnoreCase(this.getProtocol()) && jmxServiceURL.getHost().equalsIgnoreCase(this.getHost()) && jmxServiceURL.getPort() == this.getPort() && jmxServiceURL.getURLPath().equals(this.getURLPath());
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    private static boolean isNumericIPv6Address(final String s) {
        return s.indexOf(58) >= 0;
    }
    
    private static int indexOf(final String s, final char c, final int n) {
        final int index = s.indexOf(c, n);
        if (index < 0) {
            return s.length();
        }
        return index;
    }
    
    private static int indexOfFirstNotInSet(final String s, final BitSet set, final int n) {
        final int length = s.length();
        int i = n;
        while (i < length) {
            final char char1 = s.charAt(i);
            if (char1 < '\u0080') {
                if (set.get(char1)) {
                    ++i;
                    continue;
                }
            }
            return i;
        }
        return i;
    }
    
    static {
        randomException = new Exception();
        alphaBitSet = new BitSet(128);
        numericBitSet = new BitSet(128);
        alphaNumericBitSet = new BitSet(128);
        protocolBitSet = new BitSet(128);
        hostNameBitSet = new BitSet(128);
        for (int i = 48; i <= 57; i = (char)(i + 1)) {
            JMXServiceURL.numericBitSet.set(i);
        }
        for (int j = 65; j <= 90; j = (char)(j + 1)) {
            JMXServiceURL.alphaBitSet.set(j);
        }
        for (int k = 97; k <= 122; k = (char)(k + 1)) {
            JMXServiceURL.alphaBitSet.set(k);
        }
        JMXServiceURL.alphaNumericBitSet.or(JMXServiceURL.alphaBitSet);
        JMXServiceURL.alphaNumericBitSet.or(JMXServiceURL.numericBitSet);
        JMXServiceURL.protocolBitSet.or(JMXServiceURL.alphaNumericBitSet);
        JMXServiceURL.protocolBitSet.set(43);
        JMXServiceURL.protocolBitSet.set(45);
        JMXServiceURL.hostNameBitSet.or(JMXServiceURL.alphaNumericBitSet);
        JMXServiceURL.hostNameBitSet.set(45);
        JMXServiceURL.hostNameBitSet.set(46);
        logger = new ClassLogger("javax.management.remote.misc", "JMXServiceURL");
    }
}
