package com.zoho.security.util;

import org.apache.tika.detect.Detector;
import java.util.HashMap;
import org.apache.tika.mime.MimeTypeException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeType;
import java.util.Map;
import org.apache.tika.Tika;
import java.util.regex.Pattern;

public class TikaUtil
{
    private static final Pattern TIKA_SUPPORTED_TYPES_PATTERN;
    private static final Tika TIKA;
    private static Map<String, MimeType> extension_vs_contentType_map;
    
    public static Tika getTikaInstance() {
        return TikaUtil.TIKA;
    }
    
    public static MimeTypes getMimeTypeDetector() {
        return (MimeTypes)TikaUtil.TIKA.getDetector();
    }
    
    public static List<MimeType> getSupportedContentTypes() {
        final List<MimeType> contentTypes = new ArrayList<MimeType>();
        for (final MimeType mimeType : getMimeTypeDetector().getTypes().values()) {
            if (isValidContentType(mimeType)) {
                contentTypes.add(mimeType);
            }
        }
        return contentTypes;
    }
    
    private static boolean isValidContentType(final MimeType mimeType) {
        return mimeType.hasMagic() || mimeType.hasExtension() || mimeType.hasRootXML();
    }
    
    public static boolean isValidContentType(final String contentType) {
        try {
            final MimeType mimeType = getMimeTypeDetector().getRegisteredMimeType(contentType);
            return mimeType != null && isValidContentType(mimeType);
        }
        catch (final MimeTypeException e) {
            return false;
        }
    }
    
    public static boolean isValidType(final String type) {
        return TikaUtil.TIKA_SUPPORTED_TYPES_PATTERN.matcher(type).matches();
    }
    
    public static List<String> getExtensionsByContentType(final String contentType) {
        try {
            final MimeType mimeType = getMimeTypeDetector().getRegisteredMimeType(contentType);
            if (mimeType != null && mimeType.hasExtension()) {
                return mimeType.getExtensions();
            }
        }
        catch (final MimeTypeException ex) {}
        return null;
    }
    
    public static MimeType getContentTypeByExtension(final String extension) {
        if (TikaUtil.extension_vs_contentType_map == null) {
            createExtensionVsContentTypeMap();
        }
        return TikaUtil.extension_vs_contentType_map.get(extension);
    }
    
    private static synchronized void createExtensionVsContentTypeMap() {
        if (TikaUtil.extension_vs_contentType_map != null) {
            return;
        }
        final Map<String, MimeType> extensionVsContentType = new HashMap<String, MimeType>();
        for (final MimeType contentType : getSupportedContentTypes()) {
            if (contentType.hasExtension()) {
                for (final String extension : contentType.getExtensions()) {
                    extensionVsContentType.put(extension, contentType);
                }
            }
        }
        TikaUtil.extension_vs_contentType_map = extensionVsContentType;
    }
    
    static {
        TIKA_SUPPORTED_TYPES_PATTERN = Pattern.compile("application|text|image|audio|video|model|chemical|message|multipart|x-conference");
        TIKA = new Tika((Detector)MimeTypes.getDefaultMimeTypes());
    }
}
