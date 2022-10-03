package com.sun.xml.internal.ws.util;

import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import java.net.URI;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.io.File;
import java.net.URL;
import java.util.UUID;

public final class JAXWSUtils
{
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
    
    public static String getFileOrURLName(final String fileOrURL) {
        try {
            try {
                return escapeSpace(new URL(fileOrURL).toExternalForm());
            }
            catch (final MalformedURLException e) {
                return new File(fileOrURL).getCanonicalFile().toURL().toExternalForm();
            }
        }
        catch (final Exception e2) {
            return fileOrURL;
        }
    }
    
    public static URL getFileOrURL(final String fileOrURL) throws IOException {
        try {
            final URL url = new URL(fileOrURL);
            final String scheme = String.valueOf(url.getProtocol()).toLowerCase();
            if (scheme.equals("http") || scheme.equals("https")) {
                return new URL(url.toURI().toASCIIString());
            }
            return url;
        }
        catch (final URISyntaxException e) {
            return new File(fileOrURL).toURL();
        }
        catch (final MalformedURLException e2) {
            return new File(fileOrURL).toURL();
        }
    }
    
    public static URL getEncodedURL(final String urlStr) throws MalformedURLException {
        final URL url = new URL(urlStr);
        final String scheme = String.valueOf(url.getProtocol()).toLowerCase();
        if (!scheme.equals("http")) {
            if (!scheme.equals("https")) {
                return url;
            }
        }
        try {
            return new URL(url.toURI().toASCIIString());
        }
        catch (final URISyntaxException e) {
            final MalformedURLException malformedURLException = new MalformedURLException(e.getMessage());
            malformedURLException.initCause(e);
            throw malformedURLException;
        }
        return url;
    }
    
    private static String escapeSpace(final String url) {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < url.length(); ++i) {
            if (url.charAt(i) == ' ') {
                buf.append("%20");
            }
            else {
                buf.append(url.charAt(i));
            }
        }
        return buf.toString();
    }
    
    public static String absolutize(final String name) {
        try {
            final URL baseURL = new File(".").getCanonicalFile().toURL();
            return new URL(baseURL, name).toExternalForm();
        }
        catch (final IOException ex) {
            return name;
        }
    }
    
    public static void checkAbsoluteness(final String systemId) {
        try {
            new URL(systemId);
        }
        catch (final MalformedURLException mue) {
            try {
                new URI(systemId);
            }
            catch (final URISyntaxException e) {
                throw new IllegalArgumentException("system ID '" + systemId + "' isn't absolute", e);
            }
        }
    }
    
    public static boolean matchQNames(final QName target, final QName pattern) {
        if (target == null || pattern == null) {
            return false;
        }
        if (pattern.getNamespaceURI().equals(target.getNamespaceURI())) {
            final String regex = pattern.getLocalPart().replaceAll("\\*", ".*");
            return Pattern.matches(regex, target.getLocalPart());
        }
        return false;
    }
}
