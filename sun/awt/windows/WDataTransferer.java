package sun.awt.windows;

import java.util.Collections;
import java.util.HashMap;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.image.WritableRaster;
import sun.awt.image.ImageRepresentation;
import java.awt.image.DataBufferByte;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.ComponentColorModel;
import java.awt.color.ColorSpace;
import java.awt.image.ImageObserver;
import sun.awt.image.ToolkitImage;
import java.awt.Image;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.net.URL;
import java.util.Arrays;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.awt.datatransfer.Transferable;
import java.util.SortedMap;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.DataFlavor;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import java.awt.image.DirectColorModel;
import java.util.Map;
import sun.awt.datatransfer.DataTransferer;

final class WDataTransferer extends DataTransferer
{
    private static final String[] predefinedClipboardNames;
    private static final Map<String, Long> predefinedClipboardNameMap;
    public static final int CF_TEXT = 1;
    public static final int CF_METAFILEPICT = 3;
    public static final int CF_DIB = 8;
    public static final int CF_ENHMETAFILE = 14;
    public static final int CF_HDROP = 15;
    public static final int CF_LOCALE = 16;
    public static final long CF_HTML;
    public static final long CFSTR_INETURL;
    public static final long CF_PNG;
    public static final long CF_JFIF;
    public static final long CF_FILEGROUPDESCRIPTORW;
    public static final long CF_FILEGROUPDESCRIPTORA;
    private static final Long L_CF_LOCALE;
    private static final DirectColorModel directColorModel;
    private static final int[] bandmasks;
    private static WDataTransferer transferer;
    private final ToolkitThreadBlockedHandler handler;
    private static final byte[] UNICODE_NULL_TERMINATOR;
    
    private WDataTransferer() {
        this.handler = new WToolkitThreadBlockedHandler();
    }
    
    static synchronized WDataTransferer getInstanceImpl() {
        if (WDataTransferer.transferer == null) {
            WDataTransferer.transferer = new WDataTransferer();
        }
        return WDataTransferer.transferer;
    }
    
    @Override
    public SortedMap<Long, DataFlavor> getFormatsForFlavors(final DataFlavor[] array, final FlavorTable flavorTable) {
        final SortedMap<Long, DataFlavor> formatsForFlavors = super.getFormatsForFlavors(array, flavorTable);
        formatsForFlavors.remove(WDataTransferer.L_CF_LOCALE);
        return formatsForFlavors;
    }
    
    @Override
    public String getDefaultUnicodeEncoding() {
        return "utf-16le";
    }
    
    @Override
    public byte[] translateTransferable(final Transferable transferable, final DataFlavor dataFlavor, final long n) throws IOException {
        byte[] array;
        if (n == WDataTransferer.CF_HTML) {
            if (transferable.isDataFlavorSupported(DataFlavor.selectionHtmlFlavor)) {
                array = super.translateTransferable(transferable, DataFlavor.selectionHtmlFlavor, n);
            }
            else if (transferable.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
                array = super.translateTransferable(transferable, DataFlavor.allHtmlFlavor, n);
            }
            else {
                array = HTMLCodec.convertToHTMLFormat(super.translateTransferable(transferable, dataFlavor, n));
            }
        }
        else {
            array = super.translateTransferable(transferable, dataFlavor, n);
        }
        return array;
    }
    
    @Override
    public Object translateStream(InputStream inputStream, final DataFlavor dataFlavor, final long n, final Transferable transferable) throws IOException {
        if (n == WDataTransferer.CF_HTML && dataFlavor.isFlavorTextType()) {
            inputStream = new HTMLCodec(inputStream, EHTMLReadMode.getEHTMLReadMode(dataFlavor));
        }
        return super.translateStream(inputStream, dataFlavor, n, transferable);
    }
    
    @Override
    public Object translateBytes(final byte[] array, final DataFlavor dataFlavor, final long n, final Transferable transferable) throws IOException {
        if (n == WDataTransferer.CF_FILEGROUPDESCRIPTORA || n == WDataTransferer.CF_FILEGROUPDESCRIPTORW) {
            if (array == null || !DataFlavor.javaFileListFlavor.equals(dataFlavor)) {
                throw new IOException("data translation failed");
            }
            final String[] split = new String(array, 0, array.length, "UTF-16LE").split("\u0000");
            if (0 == split.length) {
                return null;
            }
            final File[] array2 = new File[split.length];
            for (int i = 0; i < split.length; ++i) {
                (array2[i] = new File(split[i])).deleteOnExit();
            }
            return Arrays.asList(array2);
        }
        else {
            if (n == WDataTransferer.CFSTR_INETURL && URL.class.equals(dataFlavor.getRepresentationClass())) {
                String defaultTextCharset = DataTransferer.getDefaultTextCharset();
                if (transferable != null && transferable.isDataFlavorSupported(WDataTransferer.javaTextEncodingFlavor)) {
                    try {
                        defaultTextCharset = new String((byte[])transferable.getTransferData(WDataTransferer.javaTextEncodingFlavor), "UTF-8");
                    }
                    catch (final UnsupportedFlavorException ex) {}
                }
                return new URL(new String(array, defaultTextCharset));
            }
            return super.translateBytes(array, dataFlavor, n, transferable);
        }
    }
    
    @Override
    public boolean isLocaleDependentTextFormat(final long n) {
        return n == 1L || n == WDataTransferer.CFSTR_INETURL;
    }
    
    @Override
    public boolean isFileFormat(final long n) {
        return n == 15L || n == WDataTransferer.CF_FILEGROUPDESCRIPTORA || n == WDataTransferer.CF_FILEGROUPDESCRIPTORW;
    }
    
    @Override
    protected Long getFormatForNativeAsLong(final String s) {
        Long value = WDataTransferer.predefinedClipboardNameMap.get(s);
        if (value == null) {
            value = registerClipboardFormat(s);
        }
        return value;
    }
    
    @Override
    protected String getNativeForFormat(final long n) {
        return (n < WDataTransferer.predefinedClipboardNames.length) ? WDataTransferer.predefinedClipboardNames[(int)n] : getClipboardFormatName(n);
    }
    
    @Override
    public ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler() {
        return this.handler;
    }
    
    private static native long registerClipboardFormat(final String p0);
    
    private static native String getClipboardFormatName(final long p0);
    
    @Override
    public boolean isImageFormat(final long n) {
        return n == 8L || n == 14L || n == 3L || n == WDataTransferer.CF_PNG || n == WDataTransferer.CF_JFIF;
    }
    
    @Override
    protected byte[] imageToPlatformBytes(final Image image, final long n) throws IOException {
        String s = null;
        if (n == WDataTransferer.CF_PNG) {
            s = "image/png";
        }
        else if (n == WDataTransferer.CF_JFIF) {
            s = "image/jpeg";
        }
        if (s != null) {
            return this.imageToStandardBytes(image, s);
        }
        int n2;
        int n3;
        if (image instanceof ToolkitImage) {
            final ImageRepresentation imageRep = ((ToolkitImage)image).getImageRep();
            imageRep.reconstruct(32);
            n2 = imageRep.getWidth();
            n3 = imageRep.getHeight();
        }
        else {
            n2 = image.getWidth(null);
            n3 = image.getHeight(null);
        }
        final int n4 = n2 * 3 % 4;
        final int n5 = (n4 > 0) ? (4 - n4) : 0;
        final ColorSpace instance = ColorSpace.getInstance(1000);
        final int[] array = { 8, 8, 8 };
        final int[] array2 = { 2, 1, 0 };
        final ComponentColorModel componentColorModel = new ComponentColorModel(instance, array, false, false, 1, 0);
        final WritableRaster interleavedRaster = Raster.createInterleavedRaster(0, n2, n3, n2 * 3 + n5, 3, array2, null);
        final BufferedImage bufferedImage = new BufferedImage(componentColorModel, interleavedRaster, false, null);
        final AffineTransform affineTransform = new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, (float)n3);
        final Graphics2D graphics = bufferedImage.createGraphics();
        try {
            graphics.drawImage(image, affineTransform, null);
        }
        finally {
            graphics.dispose();
        }
        return this.imageDataToPlatformImageBytes(((DataBufferByte)interleavedRaster.getDataBuffer()).getData(), n2, n3, n);
    }
    
    @Override
    protected ByteArrayOutputStream convertFileListToBytes(final ArrayList<String> list) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (list.isEmpty()) {
            byteArrayOutputStream.write(WDataTransferer.UNICODE_NULL_TERMINATOR);
        }
        else {
            for (int i = 0; i < list.size(); ++i) {
                final byte[] bytes = list.get(i).getBytes(this.getDefaultUnicodeEncoding());
                byteArrayOutputStream.write(bytes, 0, bytes.length);
                byteArrayOutputStream.write(WDataTransferer.UNICODE_NULL_TERMINATOR);
            }
        }
        byteArrayOutputStream.write(WDataTransferer.UNICODE_NULL_TERMINATOR);
        return byteArrayOutputStream;
    }
    
    private native byte[] imageDataToPlatformImageBytes(final byte[] p0, final int p1, final int p2, final long p3);
    
    @Override
    protected Image platformImageBytesToImage(final byte[] array, final long n) throws IOException {
        String s = null;
        if (n == WDataTransferer.CF_PNG) {
            s = "image/png";
        }
        else if (n == WDataTransferer.CF_JFIF) {
            s = "image/jpeg";
        }
        if (s != null) {
            return this.standardImageBytesToImage(array, s);
        }
        final int[] platformImageBytesToImageData = this.platformImageBytesToImageData(array, n);
        if (platformImageBytesToImageData == null) {
            throw new IOException("data translation failed");
        }
        final int n2 = platformImageBytesToImageData.length - 2;
        final int n3 = platformImageBytesToImageData[n2];
        return new BufferedImage(WDataTransferer.directColorModel, Raster.createPackedRaster(new DataBufferInt(platformImageBytesToImageData, n2), n3, platformImageBytesToImageData[n2 + 1], n3, WDataTransferer.bandmasks, null), false, null);
    }
    
    private native int[] platformImageBytesToImageData(final byte[] p0, final long p1) throws IOException;
    
    @Override
    protected native String[] dragQueryFile(final byte[] p0);
    
    static {
        predefinedClipboardNames = new String[] { "", "TEXT", "BITMAP", "METAFILEPICT", "SYLK", "DIF", "TIFF", "OEM TEXT", "DIB", "PALETTE", "PENDATA", "RIFF", "WAVE", "UNICODE TEXT", "ENHMETAFILE", "HDROP", "LOCALE", "DIBV5" };
        final HashMap hashMap = new HashMap(WDataTransferer.predefinedClipboardNames.length, 1.0f);
        for (int i = 1; i < WDataTransferer.predefinedClipboardNames.length; ++i) {
            hashMap.put(WDataTransferer.predefinedClipboardNames[i], (long)i);
        }
        predefinedClipboardNameMap = Collections.synchronizedMap((Map<String, Long>)hashMap);
        CF_HTML = registerClipboardFormat("HTML Format");
        CFSTR_INETURL = registerClipboardFormat("UniformResourceLocator");
        CF_PNG = registerClipboardFormat("PNG");
        CF_JFIF = registerClipboardFormat("JFIF");
        CF_FILEGROUPDESCRIPTORW = registerClipboardFormat("FileGroupDescriptorW");
        CF_FILEGROUPDESCRIPTORA = registerClipboardFormat("FileGroupDescriptor");
        L_CF_LOCALE = WDataTransferer.predefinedClipboardNameMap.get(WDataTransferer.predefinedClipboardNames[16]);
        directColorModel = new DirectColorModel(24, 16711680, 65280, 255);
        bandmasks = new int[] { WDataTransferer.directColorModel.getRedMask(), WDataTransferer.directColorModel.getGreenMask(), WDataTransferer.directColorModel.getBlueMask() };
        UNICODE_NULL_TERMINATOR = new byte[] { 0, 0 };
    }
}
