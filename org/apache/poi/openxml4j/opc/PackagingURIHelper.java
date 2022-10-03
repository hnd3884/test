package org.apache.poi.openxml4j.opc;

import org.apache.poi.util.POILogFactory;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import java.net.URI;
import org.apache.poi.util.POILogger;

public final class PackagingURIHelper
{
    private static final POILogger _logger;
    private static URI packageRootUri;
    public static final String RELATIONSHIP_PART_EXTENSION_NAME = ".rels";
    public static final String RELATIONSHIP_PART_SEGMENT_NAME = "_rels";
    public static final String PACKAGE_PROPERTIES_SEGMENT_NAME = "docProps";
    public static final String PACKAGE_CORE_PROPERTIES_NAME = "core.xml";
    public static final char FORWARD_SLASH_CHAR = '/';
    public static final String FORWARD_SLASH_STRING = "/";
    public static final URI PACKAGE_RELATIONSHIPS_ROOT_URI;
    public static final PackagePartName PACKAGE_RELATIONSHIPS_ROOT_PART_NAME;
    public static final URI CORE_PROPERTIES_URI;
    public static final PackagePartName CORE_PROPERTIES_PART_NAME;
    public static final URI PACKAGE_ROOT_URI;
    public static final PackagePartName PACKAGE_ROOT_PART_NAME;
    private static final Pattern missingAuthPattern;
    private static final char[] hexDigits;
    
    public static URI getPackageRootUri() {
        return PackagingURIHelper.packageRootUri;
    }
    
    public static boolean isRelationshipPartURI(final URI partUri) {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri");
        }
        return partUri.getPath().matches(".*_rels.*.rels$");
    }
    
    public static String getFilename(final URI uri) {
        if (uri != null) {
            final String path = uri.getPath();
            int num2;
            final int len = num2 = path.length();
            while (--num2 >= 0) {
                final char ch1 = path.charAt(num2);
                if (ch1 == '/') {
                    return path.substring(num2 + 1, len);
                }
            }
        }
        return "";
    }
    
    public static String getFilenameWithoutExtension(final URI uri) {
        final String filename = getFilename(uri);
        final int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            return filename;
        }
        return filename.substring(0, dotIndex);
    }
    
    public static URI getPath(final URI uri) {
        if (uri != null) {
            final String path = uri.getPath();
            int num2 = path.length();
            while (--num2 >= 0) {
                final char ch1 = path.charAt(num2);
                if (ch1 == '/') {
                    try {
                        return new URI(path.substring(0, num2));
                    }
                    catch (final URISyntaxException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
    
    public static URI combine(final URI prefix, final URI suffix) {
        URI retUri;
        try {
            retUri = new URI(combine(prefix.getPath(), suffix.getPath()));
        }
        catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Prefix and suffix can't be combine !");
        }
        return retUri;
    }
    
    public static String combine(final String prefix, final String suffix) {
        if (!prefix.endsWith("/") && !suffix.startsWith("/")) {
            return prefix + '/' + suffix;
        }
        if (prefix.endsWith("/") ^ suffix.startsWith("/")) {
            return prefix + suffix;
        }
        return "";
    }
    
    public static URI relativizeURI(final URI sourceURI, URI targetURI, final boolean msCompatible) {
        final StringBuilder retVal = new StringBuilder();
        final String[] segmentsSource = sourceURI.getPath().split("/", -1);
        final String[] segmentsTarget = targetURI.getPath().split("/", -1);
        if (segmentsSource.length == 0) {
            throw new IllegalArgumentException("Can't relativize an empty source URI !");
        }
        if (segmentsTarget.length == 0) {
            throw new IllegalArgumentException("Can't relativize an empty target URI !");
        }
        if (sourceURI.toString().equals("/")) {
            final String path = targetURI.getPath();
            if (msCompatible && path.length() > 0 && path.charAt(0) == '/') {
                try {
                    targetURI = new URI(path.substring(1));
                }
                catch (final Exception e) {
                    PackagingURIHelper._logger.log(5, new Object[] { e });
                    return null;
                }
            }
            return targetURI;
        }
        int segmentsTheSame = 0;
        for (int i = 0; i < segmentsSource.length && i < segmentsTarget.length && segmentsSource[i].equals(segmentsTarget[i]); ++i) {
            ++segmentsTheSame;
        }
        if ((segmentsTheSame == 0 || segmentsTheSame == 1) && segmentsSource[0].isEmpty() && segmentsTarget[0].isEmpty()) {
            for (int i = 0; i < segmentsSource.length - 2; ++i) {
                retVal.append("../");
            }
            for (int i = 0; i < segmentsTarget.length; ++i) {
                if (!segmentsTarget[i].isEmpty()) {
                    retVal.append(segmentsTarget[i]);
                    if (i != segmentsTarget.length - 1) {
                        retVal.append("/");
                    }
                }
            }
            try {
                return new URI(retVal.toString());
            }
            catch (final Exception e) {
                PackagingURIHelper._logger.log(5, new Object[] { e });
                return null;
            }
        }
        if (segmentsTheSame == segmentsSource.length && segmentsTheSame == segmentsTarget.length) {
            if (sourceURI.equals(targetURI)) {
                retVal.append(segmentsSource[segmentsSource.length - 1]);
            }
        }
        else {
            if (segmentsTheSame == 1) {
                retVal.append("/");
            }
            else {
                for (int j = segmentsTheSame; j < segmentsSource.length - 1; ++j) {
                    retVal.append("../");
                }
            }
            for (int j = segmentsTheSame; j < segmentsTarget.length; ++j) {
                if (retVal.length() > 0 && retVal.charAt(retVal.length() - 1) != '/') {
                    retVal.append("/");
                }
                retVal.append(segmentsTarget[j]);
            }
        }
        final String fragment = targetURI.getRawFragment();
        if (fragment != null) {
            retVal.append("#").append(fragment);
        }
        try {
            return new URI(retVal.toString());
        }
        catch (final Exception e2) {
            PackagingURIHelper._logger.log(5, new Object[] { e2 });
            return null;
        }
    }
    
    public static URI relativizeURI(final URI sourceURI, final URI targetURI) {
        return relativizeURI(sourceURI, targetURI, false);
    }
    
    public static URI resolvePartUri(final URI sourcePartUri, final URI targetUri) {
        if (sourcePartUri == null || sourcePartUri.isAbsolute()) {
            throw new IllegalArgumentException("sourcePartUri invalid - " + sourcePartUri);
        }
        if (targetUri == null || targetUri.isAbsolute()) {
            throw new IllegalArgumentException("targetUri invalid - " + targetUri);
        }
        return sourcePartUri.resolve(targetUri);
    }
    
    public static URI getURIFromPath(final String path) {
        URI retUri;
        try {
            retUri = toURI(path);
        }
        catch (final URISyntaxException e) {
            throw new IllegalArgumentException("path");
        }
        return retUri;
    }
    
    public static URI getSourcePartUriFromRelationshipPartUri(final URI relationshipPartUri) {
        if (relationshipPartUri == null) {
            throw new IllegalArgumentException("Must not be null");
        }
        if (!isRelationshipPartURI(relationshipPartUri)) {
            throw new IllegalArgumentException("Must be a relationship part");
        }
        if (relationshipPartUri.compareTo(PackagingURIHelper.PACKAGE_RELATIONSHIPS_ROOT_URI) == 0) {
            return PackagingURIHelper.PACKAGE_ROOT_URI;
        }
        String filename = relationshipPartUri.getPath();
        final String filenameWithoutExtension = getFilenameWithoutExtension(relationshipPartUri);
        filename = filename.substring(0, filename.length() - filenameWithoutExtension.length() - ".rels".length());
        filename = filename.substring(0, filename.length() - "_rels".length() - 1);
        filename = combine(filename, filenameWithoutExtension);
        return getURIFromPath(filename);
    }
    
    public static PackagePartName createPartName(final URI partUri) throws InvalidFormatException {
        if (partUri == null) {
            throw new IllegalArgumentException("partName");
        }
        return new PackagePartName(partUri, true);
    }
    
    public static PackagePartName createPartName(final String partName) throws InvalidFormatException {
        URI partNameURI;
        try {
            partNameURI = toURI(partName);
        }
        catch (final URISyntaxException e) {
            throw new InvalidFormatException(e.getMessage());
        }
        return createPartName(partNameURI);
    }
    
    public static PackagePartName createPartName(final String partName, final PackagePart relativePart) throws InvalidFormatException {
        URI newPartNameURI;
        try {
            newPartNameURI = resolvePartUri(relativePart.getPartName().getURI(), new URI(partName));
        }
        catch (final URISyntaxException e) {
            throw new InvalidFormatException(e.getMessage());
        }
        return createPartName(newPartNameURI);
    }
    
    public static PackagePartName createPartName(final URI partName, final PackagePart relativePart) throws InvalidFormatException {
        final URI newPartNameURI = resolvePartUri(relativePart.getPartName().getURI(), partName);
        return createPartName(newPartNameURI);
    }
    
    public static boolean isValidPartName(final URI partUri) {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri");
        }
        try {
            createPartName(partUri);
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    public static String decodeURI(final URI uri) {
        final StringBuilder retVal = new StringBuilder(64);
        final String uriStr = uri.toASCIIString();
        for (int length = uriStr.length(), i = 0; i < length; ++i) {
            final char c = uriStr.charAt(i);
            if (c == '%') {
                if (length - i < 2) {
                    throw new IllegalArgumentException("The uri " + uriStr + " contain invalid encoded character !");
                }
                final char decodedChar = (char)Integer.parseInt(uriStr.substring(i + 1, i + 3), 16);
                retVal.append(decodedChar);
                i += 2;
            }
            else {
                retVal.append(c);
            }
        }
        return retVal.toString();
    }
    
    public static PackagePartName getRelationshipPartName(final PackagePartName partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if (PackagingURIHelper.PACKAGE_ROOT_URI.getPath().equals(partName.getURI().getPath())) {
            return PackagingURIHelper.PACKAGE_RELATIONSHIPS_ROOT_PART_NAME;
        }
        if (partName.isRelationshipPartURI()) {
            throw new InvalidOperationException("Can't be a relationship part");
        }
        String fullPath = partName.getURI().getPath();
        final String filename = getFilename(partName.getURI());
        fullPath = fullPath.substring(0, fullPath.length() - filename.length());
        fullPath = combine(fullPath, "_rels");
        fullPath = combine(fullPath, filename);
        fullPath += ".rels";
        PackagePartName retPartName;
        try {
            retPartName = createPartName(fullPath);
        }
        catch (final InvalidFormatException e) {
            return null;
        }
        return retPartName;
    }
    
    public static URI toURI(String value) throws URISyntaxException {
        if (value.contains("\\")) {
            value = value.replace('\\', '/');
        }
        final int fragmentIdx = value.indexOf(35);
        if (fragmentIdx != -1) {
            final String path = value.substring(0, fragmentIdx);
            final String fragment = value.substring(fragmentIdx + 1);
            value = path + "#" + encode(fragment);
        }
        if (value.length() > 0) {
            final StringBuilder b = new StringBuilder();
            int idx;
            for (idx = value.length() - 1; idx >= 0; --idx) {
                final char c = value.charAt(idx);
                if (!Character.isWhitespace(c) && c != ' ') {
                    break;
                }
                b.append(c);
            }
            if (b.length() > 0) {
                value = value.substring(0, idx + 1) + encode(b.reverse().toString());
            }
        }
        if (PackagingURIHelper.missingAuthPattern.matcher(value).matches()) {
            value += "/";
        }
        return new URI(value);
    }
    
    public static String encode(final String s) {
        final int n = s.length();
        if (n == 0) {
            return s;
        }
        final ByteBuffer bb = ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8));
        final StringBuilder sb = new StringBuilder();
        while (bb.hasRemaining()) {
            final int b = bb.get() & 0xFF;
            if (isUnsafe(b)) {
                sb.append('%');
                sb.append(PackagingURIHelper.hexDigits[b >> 4 & 0xF]);
                sb.append(PackagingURIHelper.hexDigits[b >> 0 & 0xF]);
            }
            else {
                sb.append((char)b);
            }
        }
        return sb.toString();
    }
    
    private static boolean isUnsafe(final int ch) {
        return ch >= 128 || ch == 124 || Character.isWhitespace(ch);
    }
    
    static {
        _logger = POILogFactory.getLogger((Class)PackagingURIHelper.class);
        URI uriPACKAGE_ROOT_URI = null;
        URI uriPACKAGE_RELATIONSHIPS_ROOT_URI = null;
        URI uriPACKAGE_PROPERTIES_URI = null;
        try {
            uriPACKAGE_ROOT_URI = new URI("/");
            uriPACKAGE_RELATIONSHIPS_ROOT_URI = new URI("/_rels/.rels");
            PackagingURIHelper.packageRootUri = new URI("/");
            uriPACKAGE_PROPERTIES_URI = new URI("/docProps/core.xml");
        }
        catch (final URISyntaxException ex) {}
        PACKAGE_ROOT_URI = uriPACKAGE_ROOT_URI;
        PACKAGE_RELATIONSHIPS_ROOT_URI = uriPACKAGE_RELATIONSHIPS_ROOT_URI;
        CORE_PROPERTIES_URI = uriPACKAGE_PROPERTIES_URI;
        PackagePartName tmpPACKAGE_ROOT_PART_NAME = null;
        PackagePartName tmpPACKAGE_RELATIONSHIPS_ROOT_PART_NAME = null;
        PackagePartName tmpCORE_PROPERTIES_URI = null;
        try {
            tmpPACKAGE_RELATIONSHIPS_ROOT_PART_NAME = createPartName(PackagingURIHelper.PACKAGE_RELATIONSHIPS_ROOT_URI);
            tmpCORE_PROPERTIES_URI = createPartName(PackagingURIHelper.CORE_PROPERTIES_URI);
            tmpPACKAGE_ROOT_PART_NAME = new PackagePartName(PackagingURIHelper.PACKAGE_ROOT_URI, false);
        }
        catch (final InvalidFormatException ex2) {}
        PACKAGE_RELATIONSHIPS_ROOT_PART_NAME = tmpPACKAGE_RELATIONSHIPS_ROOT_PART_NAME;
        CORE_PROPERTIES_PART_NAME = tmpCORE_PROPERTIES_URI;
        PACKAGE_ROOT_PART_NAME = tmpPACKAGE_ROOT_PART_NAME;
        missingAuthPattern = Pattern.compile("\\w+://");
        hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
