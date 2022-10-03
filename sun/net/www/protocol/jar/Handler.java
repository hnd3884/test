package sun.net.www.protocol.jar;

import sun.net.www.ParseUtil;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    private static final String separator = "!/";
    
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        return new JarURLConnection(url, this);
    }
    
    private static int indexOfBangSlash(final String s) {
        for (int n = s.length(); (n = s.lastIndexOf(33, n)) != -1; --n) {
            if (n != s.length() - 1 && s.charAt(n + 1) == '/') {
                return n + 1;
            }
        }
        return -1;
    }
    
    @Override
    protected boolean sameFile(final URL url, final URL url2) {
        if (!url.getProtocol().equals("jar") || !url2.getProtocol().equals("jar")) {
            return false;
        }
        final String file = url.getFile();
        final String file2 = url2.getFile();
        final int index = file.indexOf("!/");
        final int index2 = file2.indexOf("!/");
        if (index == -1 || index2 == -1) {
            return super.sameFile(url, url2);
        }
        if (!file.substring(index + 2).equals(file2.substring(index2 + 2))) {
            return false;
        }
        URL url3;
        URL url4;
        try {
            url3 = new URL(file.substring(0, index));
            url4 = new URL(file2.substring(0, index2));
        }
        catch (final MalformedURLException ex) {
            return super.sameFile(url, url2);
        }
        return super.sameFile(url3, url4);
    }
    
    @Override
    protected int hashCode(final URL url) {
        int n = 0;
        final String protocol = url.getProtocol();
        if (protocol != null) {
            n += protocol.hashCode();
        }
        final String file = url.getFile();
        final int index = file.indexOf("!/");
        if (index == -1) {
            return n + file.hashCode();
        }
        final String substring = file.substring(0, index);
        try {
            n += new URL(substring).hashCode();
        }
        catch (final MalformedURLException ex) {
            n += substring.hashCode();
        }
        return n + file.substring(index + 2).hashCode();
    }
    
    public String checkNestedProtocol(final String s) {
        if (s.regionMatches(true, 0, "jar:", 0, 4)) {
            return "Nested JAR URLs are not supported";
        }
        return null;
    }
    
    @Override
    protected void parseURL(final URL url, String substring, final int n, final int n2) {
        String s = null;
        String substring2 = null;
        final int index = substring.indexOf(35, n2);
        final boolean b = index == n;
        if (index > -1) {
            substring2 = substring.substring(index + 1, substring.length());
            if (b) {
                s = url.getFile();
            }
        }
        boolean equalsIgnoreCase = false;
        if (substring.length() >= 4) {
            equalsIgnoreCase = substring.substring(0, 4).equalsIgnoreCase("jar:");
        }
        substring = substring.substring(n, n2);
        final String checkNestedProtocol = this.checkNestedProtocol(substring);
        if (checkNestedProtocol != null) {
            throw new NullPointerException(checkNestedProtocol);
        }
        if (equalsIgnoreCase) {
            s = this.parseAbsoluteSpec(substring);
        }
        else if (!b) {
            final String contextSpec = this.parseContextSpec(url, substring);
            final int indexOfBangSlash = indexOfBangSlash(contextSpec);
            s = contextSpec.substring(0, indexOfBangSlash) + new ParseUtil().canonizeString(contextSpec.substring(indexOfBangSlash));
        }
        this.setURL(url, "jar", "", -1, s, substring2);
    }
    
    private String parseAbsoluteSpec(final String s) {
        final int indexOfBangSlash;
        if ((indexOfBangSlash = indexOfBangSlash(s)) == -1) {
            throw new NullPointerException("no !/ in spec");
        }
        try {
            final URL url = new URL(s.substring(0, indexOfBangSlash - 1));
        }
        catch (final MalformedURLException ex) {
            throw new NullPointerException("invalid url: " + s + " (" + ex + ")");
        }
        return s;
    }
    
    private String parseContextSpec(final URL url, final String s) {
        String s2 = url.getFile();
        if (s.startsWith("/")) {
            final int indexOfBangSlash = indexOfBangSlash(s2);
            if (indexOfBangSlash == -1) {
                throw new NullPointerException("malformed context url:" + url + ": no !/");
            }
            s2 = s2.substring(0, indexOfBangSlash);
        }
        if (!s2.endsWith("/") && !s.startsWith("/")) {
            final int lastIndex = s2.lastIndexOf(47);
            if (lastIndex == -1) {
                throw new NullPointerException("malformed context url:" + url);
            }
            s2 = s2.substring(0, lastIndex + 1);
        }
        return s2 + s;
    }
}
