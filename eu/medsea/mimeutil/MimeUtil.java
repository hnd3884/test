package eu.medsea.mimeutil;

import java.nio.ByteOrder;
import java.net.URL;
import java.io.InputStream;
import java.util.Collection;
import java.io.File;
import eu.medsea.mimeutil.detector.MimeDetector;

public class MimeUtil
{
    private static MimeUtil2 mimeUtil;
    
    static {
        MimeUtil.mimeUtil = new MimeUtil2();
    }
    
    public static void addKnownMimeType(final MimeType mimeType) {
        MimeUtil2.addKnownMimeType(mimeType);
    }
    
    public static void addKnownMimeType(final String mimeType) {
        MimeUtil2.addKnownMimeType(mimeType);
    }
    
    public static MimeDetector registerMimeDetector(final String mimeDetector) {
        return MimeUtil.mimeUtil.registerMimeDetector(mimeDetector);
    }
    
    public static String getExtension(final File file) {
        return MimeUtil2.getExtension(file);
    }
    
    public static String getExtension(final String fileName) {
        return MimeUtil2.getExtension(fileName);
    }
    
    public static MimeType getFirstMimeType(final String mimeTypes) {
        return MimeUtil2.getFirstMimeType(mimeTypes);
    }
    
    public static String getMediaType(final String mimeType) throws MimeException {
        return MimeUtil2.getMediaType(mimeType);
    }
    
    public static double getMimeQuality(final String mimeType) throws MimeException {
        return MimeUtil2.getMimeQuality(mimeType);
    }
    
    public static MimeDetector getMimeDetector(final String name) {
        return MimeUtil.mimeUtil.getMimeDetector(name);
    }
    
    public static final Collection getMimeTypes(final byte[] data) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(data);
    }
    
    public static final Collection getMimeTypes(final byte[] data, final MimeType unknownMimeType) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(data, unknownMimeType);
    }
    
    public static final Collection getMimeTypes(final File file) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(file);
    }
    
    public static final Collection getMimeTypes(final File file, final MimeType unknownMimeType) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(file, unknownMimeType);
    }
    
    public static final Collection getMimeTypes(final InputStream in) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(in);
    }
    
    public static final Collection getMimeTypes(final InputStream in, final MimeType unknownMimeType) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(in, unknownMimeType);
    }
    
    public static final Collection getMimeTypes(final String fileName) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(fileName);
    }
    
    public static final Collection getMimeTypes(final String fileName, final MimeType unknownMimeType) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(fileName, unknownMimeType);
    }
    
    public static final Collection getMimeTypes(final URL url) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(url);
    }
    
    public static final Collection getMimeTypes(final URL url, final MimeType unknownMimeType) throws MimeException {
        return MimeUtil.mimeUtil.getMimeTypes(url, unknownMimeType);
    }
    
    public static ByteOrder getNativeOrder() {
        return MimeUtil2.getNativeOrder();
    }
    
    public static MimeType getPreferedMimeType(final String accept, final String canProvide) {
        return MimeUtil2.getPreferedMimeType(accept, canProvide);
    }
    
    public static MimeType getMostSpecificMimeType(final Collection mimeTypes) {
        return MimeUtil2.getMostSpecificMimeType(mimeTypes);
    }
    
    public static String getSubType(final String mimeType) throws MimeException {
        return MimeUtil2.getSubType(mimeType);
    }
    
    public static boolean isMimeTypeKnown(final MimeType mimeType) {
        return MimeUtil2.isMimeTypeKnown(mimeType);
    }
    
    public static boolean isMimeTypeKnown(final String mimeType) {
        return MimeUtil2.isMimeTypeKnown(mimeType);
    }
    
    public static boolean isTextMimeType(final MimeType mimeType) {
        return MimeUtil2.isTextMimeType(mimeType);
    }
    
    public static MimeDetector unregisterMimeDetector(final MimeDetector mimeDetector) {
        return MimeUtil.mimeUtil.unregisterMimeDetector(mimeDetector);
    }
    
    public static MimeDetector unregisterMimeDetector(final String mimeDetector) {
        return MimeUtil.mimeUtil.unregisterMimeDetector(mimeDetector);
    }
    
    public static double getQuality(final String mimeType) throws MimeException {
        return MimeUtil2.getQuality(mimeType);
    }
    
    public static InputStream getInputStreamForURL(final URL url) throws Exception {
        return MimeUtil2.getInputStreamForURL(url);
    }
}
