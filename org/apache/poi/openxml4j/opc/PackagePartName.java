package org.apache.poi.openxml4j.opc;

import java.math.BigInteger;
import java.util.Locale;
import java.net.URISyntaxException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import java.net.URI;

public final class PackagePartName implements Comparable<PackagePartName>
{
    private final URI partNameURI;
    private static final String RFC3986_PCHAR_SUB_DELIMS = "!$&'()*+,;=";
    private static final String RFC3986_PCHAR_UNRESERVED_SUP = "-._~";
    private static final String RFC3986_PCHAR_AUTHORIZED_SUP = ":@";
    private final boolean isRelationship;
    
    PackagePartName(final URI uri, final boolean checkConformance) throws InvalidFormatException {
        if (checkConformance) {
            throwExceptionIfInvalidPartUri(uri);
        }
        else if (!PackagingURIHelper.PACKAGE_ROOT_URI.equals(uri)) {
            throw new OpenXML4JRuntimeException("OCP conformance must be check for ALL part name except special cases : ['/']");
        }
        this.partNameURI = uri;
        this.isRelationship = this.isRelationshipPartURI(this.partNameURI);
    }
    
    PackagePartName(final String partName, final boolean checkConformance) throws InvalidFormatException {
        URI partURI;
        try {
            partURI = new URI(partName);
        }
        catch (final URISyntaxException e) {
            throw new IllegalArgumentException("partName argmument is not a valid OPC part name !");
        }
        if (checkConformance) {
            throwExceptionIfInvalidPartUri(partURI);
        }
        else if (!PackagingURIHelper.PACKAGE_ROOT_URI.equals(partURI)) {
            throw new OpenXML4JRuntimeException("OCP conformance must be check for ALL part name except special cases : ['/']");
        }
        this.partNameURI = partURI;
        this.isRelationship = this.isRelationshipPartURI(this.partNameURI);
    }
    
    private boolean isRelationshipPartURI(final URI partUri) {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri");
        }
        return partUri.getPath().matches("^.*/_rels/.*\\.rels$");
    }
    
    public boolean isRelationshipPartURI() {
        return this.isRelationship;
    }
    
    private static void throwExceptionIfInvalidPartUri(final URI partUri) throws InvalidFormatException {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri");
        }
        throwExceptionIfEmptyURI(partUri);
        throwExceptionIfAbsoluteUri(partUri);
        throwExceptionIfPartNameNotStartsWithForwardSlashChar(partUri);
        throwExceptionIfPartNameEndsWithForwardSlashChar(partUri);
        throwExceptionIfPartNameHaveInvalidSegments(partUri);
    }
    
    private static void throwExceptionIfEmptyURI(final URI partURI) throws InvalidFormatException {
        if (partURI == null) {
            throw new IllegalArgumentException("partURI");
        }
        final String uriPath = partURI.getPath();
        if (uriPath.length() == 0 || (uriPath.length() == 1 && uriPath.charAt(0) == '/')) {
            throw new InvalidFormatException("A part name shall not be empty [M1.1]: " + partURI.getPath());
        }
    }
    
    private static void throwExceptionIfPartNameHaveInvalidSegments(final URI partUri) throws InvalidFormatException {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri");
        }
        final String[] segments = partUri.toASCIIString().replaceFirst("^/", "").split("/");
        if (segments.length < 1) {
            throw new InvalidFormatException("A part name shall not have empty segments [M1.3]: " + partUri.getPath());
        }
        for (final String seg : segments) {
            if (seg == null || seg.isEmpty()) {
                throw new InvalidFormatException("A part name shall not have empty segments [M1.3]: " + partUri.getPath());
            }
            if (seg.endsWith(".")) {
                throw new InvalidFormatException("A segment shall not end with a dot ('.') character [M1.9]: " + partUri.getPath());
            }
            if (seg.replaceAll("\\\\.", "").isEmpty()) {
                throw new InvalidFormatException("A segment shall include at least one non-dot character. [M1.10]: " + partUri.getPath());
            }
            checkPCharCompliance(seg);
        }
    }
    
    private static void checkPCharCompliance(final String segment) throws InvalidFormatException {
        for (int length = segment.length(), i = 0; i < length; ++i) {
            final char c = segment.charAt(i);
            if (!isDigitOrLetter(c) && "-._~".indexOf(c) <= -1 && ":@".indexOf(c) <= -1) {
                if ("!$&'()*+,;=".indexOf(c) <= -1) {
                    if (c != '%') {
                        throw new InvalidFormatException("A segment shall not hold any characters other than pchar characters. [M1.6]");
                    }
                    if (length - i < 2 || !isHexDigit(segment.charAt(i + 1)) || !isHexDigit(segment.charAt(i + 2))) {
                        throw new InvalidFormatException("The segment " + segment + " contain invalid encoded character !");
                    }
                    final char decodedChar = (char)Integer.parseInt(segment.substring(i + 1, i + 3), 16);
                    i += 2;
                    if (decodedChar == '/' || decodedChar == '\\') {
                        throw new InvalidFormatException("A segment shall not contain percent-encoded forward slash ('/'), or backward slash ('') characters. [M1.7]");
                    }
                    if (isDigitOrLetter(decodedChar) || "-._~".indexOf(decodedChar) > -1) {
                        throw new InvalidFormatException("A segment shall not contain percent-encoded unreserved characters. [M1.8]");
                    }
                }
            }
        }
    }
    
    private static void throwExceptionIfPartNameNotStartsWithForwardSlashChar(final URI partUri) throws InvalidFormatException {
        final String uriPath = partUri.getPath();
        if (uriPath.length() > 0 && uriPath.charAt(0) != '/') {
            throw new InvalidFormatException("A part name shall start with a forward slash ('/') character [M1.4]: " + partUri.getPath());
        }
    }
    
    private static void throwExceptionIfPartNameEndsWithForwardSlashChar(final URI partUri) throws InvalidFormatException {
        final String uriPath = partUri.getPath();
        if (uriPath.length() > 0 && uriPath.charAt(uriPath.length() - 1) == '/') {
            throw new InvalidFormatException("A part name shall not have a forward slash as the last character [M1.5]: " + partUri.getPath());
        }
    }
    
    private static void throwExceptionIfAbsoluteUri(final URI partUri) throws InvalidFormatException {
        if (partUri.isAbsolute()) {
            throw new InvalidFormatException("Absolute URI forbidden: " + partUri);
        }
    }
    
    @Override
    public int compareTo(final PackagePartName other) {
        return compare(this, other);
    }
    
    public String getExtension() {
        final String fragment = this.partNameURI.getPath();
        if (fragment.length() > 0) {
            final int i = fragment.lastIndexOf(".");
            if (i > -1) {
                return fragment.substring(i + 1);
            }
        }
        return "";
    }
    
    public String getName() {
        return this.getURI().toASCIIString();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof PackagePartName && compare(this.getName(), ((PackagePartName)other).getName()) == 0;
    }
    
    @Override
    public int hashCode() {
        return this.getName().toLowerCase(Locale.ROOT).hashCode();
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    public URI getURI() {
        return this.partNameURI;
    }
    
    public static int compare(final PackagePartName obj1, final PackagePartName obj2) {
        return compare((obj1 == null) ? null : obj1.getName(), (obj2 == null) ? null : obj2.getName());
    }
    
    public static int compare(final String str1, final String str2) {
        if (str1 == null) {
            return (str2 == null) ? 0 : -1;
        }
        if (str2 == null) {
            return 1;
        }
        if (str1.equalsIgnoreCase(str2)) {
            return 0;
        }
        final String name1 = str1.toLowerCase(Locale.ROOT);
        final String name2 = str2.toLowerCase(Locale.ROOT);
        final int len1 = name1.length();
        final int len2 = name2.length();
        int idx1 = 0;
        int idx2 = 0;
        while (idx1 < len1 && idx2 < len2) {
            final char c1 = name1.charAt(idx1++);
            final char c2 = name2.charAt(idx2++);
            if (Character.isDigit(c1) && Character.isDigit(c2)) {
                final int beg1 = idx1 - 1;
                while (idx1 < len1 && Character.isDigit(name1.charAt(idx1))) {
                    ++idx1;
                }
                final int beg2 = idx2 - 1;
                while (idx2 < len2 && Character.isDigit(name2.charAt(idx2))) {
                    ++idx2;
                }
                final BigInteger b1 = new BigInteger(name1.substring(beg1, idx1));
                final BigInteger b2 = new BigInteger(name2.substring(beg2, idx2));
                final int cmp = b1.compareTo(b2);
                if (cmp != 0) {
                    return cmp;
                }
                continue;
            }
            else {
                if (c1 != c2) {
                    return c1 - c2;
                }
                continue;
            }
        }
        return len1 - len2;
    }
    
    private static boolean isDigitOrLetter(final char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
    
    private static boolean isHexDigit(final char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }
}
