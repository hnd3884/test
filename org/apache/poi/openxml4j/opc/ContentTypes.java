package org.apache.poi.openxml4j.opc;

import java.util.Locale;

public final class ContentTypes
{
    public static final String CORE_PROPERTIES_PART = "application/vnd.openxmlformats-package.core-properties+xml";
    public static final String DIGITAL_SIGNATURE_CERTIFICATE_PART = "application/vnd.openxmlformats-package.digital-signature-certificate";
    public static final String DIGITAL_SIGNATURE_ORIGIN_PART = "application/vnd.openxmlformats-package.digital-signature-origin";
    public static final String DIGITAL_SIGNATURE_XML_SIGNATURE_PART = "application/vnd.openxmlformats-package.digital-signature-xmlsignature+xml";
    public static final String RELATIONSHIPS_PART = "application/vnd.openxmlformats-package.relationships+xml";
    public static final String CUSTOM_XML_PART = "application/vnd.openxmlformats-officedocument.customXmlProperties+xml";
    public static final String PLAIN_OLD_XML = "application/xml";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String EXTENSION_JPG_1 = "jpg";
    public static final String EXTENSION_JPG_2 = "jpeg";
    public static final String IMAGE_PNG = "image/png";
    public static final String EXTENSION_PNG = "png";
    public static final String IMAGE_GIF = "image/gif";
    public static final String EXTENSION_GIF = "gif";
    public static final String IMAGE_TIFF = "image/tiff";
    public static final String EXTENSION_TIFF = "tiff";
    public static final String IMAGE_PICT = "image/pict";
    public static final String EXTENSION_PICT = "pict";
    public static final String XML = "text/xml";
    public static final String EXTENSION_XML = "xml";
    
    public static String getContentTypeFromFileExtension(final String filename) {
        final String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            return "image/jpeg";
        }
        if (extension.equals("gif")) {
            return "image/gif";
        }
        if (extension.equals("pict")) {
            return "image/pict";
        }
        if (extension.equals("png")) {
            return "image/png";
        }
        if (extension.equals("tiff")) {
            return "image/tiff";
        }
        if (extension.equals("xml")) {
            return "text/xml";
        }
        return null;
    }
}
