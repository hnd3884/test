package com.sun.org.apache.xml.internal.serializer.utils;

import javax.xml.transform.TransformerException;
import java.io.File;

public final class SystemIDResolver
{
    public static String getAbsoluteURIFromRelative(final String localPath) {
        if (localPath == null || localPath.length() == 0) {
            return "";
        }
        String absolutePath = localPath;
        if (!isAbsolutePath(localPath)) {
            try {
                absolutePath = getAbsolutePathFromRelativePath(localPath);
            }
            catch (final SecurityException se) {
                return "file:" + localPath;
            }
        }
        String urlString;
        if (null != absolutePath) {
            if (absolutePath.startsWith(File.separator)) {
                urlString = "file://" + absolutePath;
            }
            else {
                urlString = "file:///" + absolutePath;
            }
        }
        else {
            urlString = "file:" + localPath;
        }
        return replaceChars(urlString);
    }
    
    private static String getAbsolutePathFromRelativePath(final String relativePath) {
        return new File(relativePath).getAbsolutePath();
    }
    
    public static boolean isAbsoluteURI(final String systemId) {
        if (isWindowsAbsolutePath(systemId)) {
            return false;
        }
        final int fragmentIndex = systemId.indexOf(35);
        final int queryIndex = systemId.indexOf(63);
        final int slashIndex = systemId.indexOf(47);
        final int colonIndex = systemId.indexOf(58);
        int index = systemId.length() - 1;
        if (fragmentIndex > 0) {
            index = fragmentIndex;
        }
        if (queryIndex > 0 && queryIndex < index) {
            index = queryIndex;
        }
        if (slashIndex > 0 && slashIndex < index) {
            index = slashIndex;
        }
        return colonIndex > 0 && colonIndex < index;
    }
    
    public static boolean isAbsolutePath(final String systemId) {
        if (systemId == null) {
            return false;
        }
        final File file = new File(systemId);
        return file.isAbsolute();
    }
    
    private static boolean isWindowsAbsolutePath(final String systemId) {
        return isAbsolutePath(systemId) && (systemId.length() > 2 && systemId.charAt(1) == ':' && Character.isLetter(systemId.charAt(0)) && (systemId.charAt(2) == '\\' || systemId.charAt(2) == '/'));
    }
    
    private static String replaceChars(final String str) {
        final StringBuffer buf = new StringBuffer(str);
        for (int length = buf.length(), i = 0; i < length; ++i) {
            final char currentChar = buf.charAt(i);
            if (currentChar == ' ') {
                buf.setCharAt(i, '%');
                buf.insert(i + 1, "20");
                length += 2;
                i += 2;
            }
            else if (currentChar == '\\') {
                buf.setCharAt(i, '/');
            }
        }
        return buf.toString();
    }
    
    public static String getAbsoluteURI(final String systemId) {
        String absoluteURI = systemId;
        if (!isAbsoluteURI(systemId)) {
            return getAbsoluteURIFromRelative(systemId);
        }
        if (!systemId.startsWith("file:")) {
            return systemId;
        }
        final String str = systemId.substring(5);
        if (str != null && str.startsWith("/")) {
            if (str.startsWith("///") || !str.startsWith("//")) {
                final int secondColonIndex = systemId.indexOf(58, 5);
                if (secondColonIndex > 0) {
                    final String localPath = systemId.substring(secondColonIndex - 1);
                    try {
                        if (!isAbsolutePath(localPath)) {
                            absoluteURI = systemId.substring(0, secondColonIndex - 1) + getAbsolutePathFromRelativePath(localPath);
                        }
                    }
                    catch (final SecurityException se) {
                        return systemId;
                    }
                }
            }
            return replaceChars(absoluteURI);
        }
        return getAbsoluteURIFromRelative(systemId.substring(5));
    }
    
    public static String getAbsoluteURI(final String urlString, final String base) throws TransformerException {
        if (base == null) {
            return getAbsoluteURI(urlString);
        }
        final String absoluteBase = getAbsoluteURI(base);
        URI uri = null;
        try {
            final URI baseURI = new URI(absoluteBase);
            uri = new URI(baseURI, urlString);
        }
        catch (final URI.MalformedURIException mue) {
            throw new TransformerException(mue);
        }
        return replaceChars(uri.toString());
    }
}
