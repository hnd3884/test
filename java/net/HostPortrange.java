package java.net;

import java.util.Formatter;
import java.util.Locale;
import sun.net.util.IPAddressUtil;

class HostPortrange
{
    String hostname;
    String scheme;
    int[] portrange;
    boolean wildcard;
    boolean literal;
    boolean ipv6;
    boolean ipv4;
    static final int PORT_MIN = 0;
    static final int PORT_MAX = 65535;
    static final int CASE_DIFF = -32;
    static final int[] HTTP_PORT;
    static final int[] HTTPS_PORT;
    static final int[] NO_PORT;
    
    boolean equals(final HostPortrange hostPortrange) {
        return this.hostname.equals(hostPortrange.hostname) && this.portrange[0] == hostPortrange.portrange[0] && this.portrange[1] == hostPortrange.portrange[1] && this.wildcard == hostPortrange.wildcard && this.literal == hostPortrange.literal;
    }
    
    @Override
    public int hashCode() {
        return this.hostname.hashCode() + this.portrange[0] + this.portrange[1];
    }
    
    HostPortrange(final String scheme, final String s) {
        String s2 = null;
        this.scheme = scheme;
        if (s.charAt(0) == '[') {
            final boolean b = true;
            this.literal = b;
            this.ipv6 = b;
            final int index = s.indexOf(93);
            if (index == -1) {
                throw new IllegalArgumentException("invalid IPv6 address: " + s);
            }
            final String substring = s.substring(1, index);
            final int index2 = s.indexOf(58, index + 1);
            if (index2 != -1 && s.length() > index2) {
                s2 = s.substring(index2 + 1);
            }
            final byte[] textToNumericFormatV6 = IPAddressUtil.textToNumericFormatV6(substring);
            if (textToNumericFormatV6 == null) {
                throw new IllegalArgumentException("illegal IPv6 address");
            }
            final StringBuilder sb = new StringBuilder();
            new Formatter(sb, Locale.US).format("%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x", textToNumericFormatV6[0], textToNumericFormatV6[1], textToNumericFormatV6[2], textToNumericFormatV6[3], textToNumericFormatV6[4], textToNumericFormatV6[5], textToNumericFormatV6[6], textToNumericFormatV6[7], textToNumericFormatV6[8], textToNumericFormatV6[9], textToNumericFormatV6[10], textToNumericFormatV6[11], textToNumericFormatV6[12], textToNumericFormatV6[13], textToNumericFormatV6[14], textToNumericFormatV6[15]);
            this.hostname = sb.toString();
        }
        else {
            final int index3 = s.indexOf(58);
            String hostname;
            if (index3 != -1 && s.length() > index3) {
                hostname = s.substring(0, index3);
                s2 = s.substring(index3 + 1);
            }
            else {
                hostname = ((index3 == -1) ? s : s.substring(0, index3));
            }
            if (hostname.lastIndexOf(42) > 0) {
                throw new IllegalArgumentException("invalid host wildcard specification");
            }
            if (hostname.startsWith("*")) {
                this.wildcard = true;
                if (hostname.equals("*")) {
                    hostname = "";
                }
                else {
                    if (!hostname.startsWith("*.")) {
                        throw new IllegalArgumentException("invalid host wildcard specification");
                    }
                    hostname = toLowerCase(hostname.substring(1));
                }
            }
            else {
                final int lastIndex = hostname.lastIndexOf(46);
                if (lastIndex != -1 && hostname.length() > 1) {
                    boolean b2 = true;
                    for (int i = lastIndex + 1; i < hostname.length(); ++i) {
                        final char char1 = hostname.charAt(i);
                        if (char1 < '0' || char1 > '9') {
                            b2 = false;
                            break;
                        }
                    }
                    final boolean b3 = b2;
                    this.literal = b3;
                    this.ipv4 = b3;
                    if (b2) {
                        final byte[] textToNumericFormatV7 = IPAddressUtil.textToNumericFormatV4(hostname);
                        if (textToNumericFormatV7 == null) {
                            throw new IllegalArgumentException("illegal IPv4 address");
                        }
                        final StringBuilder sb2 = new StringBuilder();
                        new Formatter(sb2, Locale.US).format("%d.%d.%d.%d", textToNumericFormatV7[0], textToNumericFormatV7[1], textToNumericFormatV7[2], textToNumericFormatV7[3]);
                        hostname = sb2.toString();
                    }
                    else {
                        hostname = toLowerCase(hostname);
                    }
                }
            }
            this.hostname = hostname;
        }
        try {
            this.portrange = this.parsePort(s2);
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException("invalid port range: " + s2);
        }
    }
    
    static String toLowerCase(final String s) {
        final int length = s.length();
        StringBuilder sb = null;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if ((char1 >= 'a' && char1 <= 'z') || char1 == '.') {
                if (sb != null) {
                    sb.append(char1);
                }
            }
            else if ((char1 >= '0' && char1 <= '9') || char1 == '-') {
                if (sb != null) {
                    sb.append(char1);
                }
            }
            else {
                if (char1 < 'A' || char1 > 'Z') {
                    throw new IllegalArgumentException("Invalid characters in hostname");
                }
                if (sb == null) {
                    sb = new StringBuilder(length);
                    sb.append(s, 0, i);
                }
                sb.append((char)(char1 + 32));
            }
        }
        return (sb == null) ? s : sb.toString();
    }
    
    public boolean literal() {
        return this.literal;
    }
    
    public boolean ipv4Literal() {
        return this.ipv4;
    }
    
    public boolean ipv6Literal() {
        return this.ipv6;
    }
    
    public String hostname() {
        return this.hostname;
    }
    
    public int[] portrange() {
        return this.portrange;
    }
    
    public boolean wildcard() {
        return this.wildcard;
    }
    
    int[] defaultPort() {
        if (this.scheme.equals("http")) {
            return HostPortrange.HTTP_PORT;
        }
        if (this.scheme.equals("https")) {
            return HostPortrange.HTTPS_PORT;
        }
        return HostPortrange.NO_PORT;
    }
    
    int[] parsePort(final String s) {
        if (s == null || s.equals("")) {
            return this.defaultPort();
        }
        if (s.equals("*")) {
            return new int[] { 0, 65535 };
        }
        try {
            final int index = s.indexOf(45);
            if (index == -1) {
                final int int1 = Integer.parseInt(s);
                return new int[] { int1, int1 };
            }
            final String substring = s.substring(0, index);
            final String substring2 = s.substring(index + 1);
            int int2;
            if (substring.equals("")) {
                int2 = 0;
            }
            else {
                int2 = Integer.parseInt(substring);
            }
            int int3;
            if (substring2.equals("")) {
                int3 = 65535;
            }
            else {
                int3 = Integer.parseInt(substring2);
            }
            if (int2 < 0 || int3 < 0 || int3 < int2) {
                return this.defaultPort();
            }
            return new int[] { int2, int3 };
        }
        catch (final IllegalArgumentException ex) {
            return this.defaultPort();
        }
    }
    
    static {
        HTTP_PORT = new int[] { 80, 80 };
        HTTPS_PORT = new int[] { 443, 443 };
        NO_PORT = new int[] { -1, -1 };
    }
}
