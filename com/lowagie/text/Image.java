package com.lowagie.text;

import com.lowagie.text.pdf.PdfArray;
import java.lang.reflect.Constructor;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PRIndirectReference;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.image.PixelGrabber;
import java.awt.image.BufferedImage;
import java.awt.Color;
import com.lowagie.text.pdf.codec.CCITTG4Encoder;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import com.lowagie.text.pdf.PdfDictionary;
import java.awt.color.ICC_Profile;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfTemplate;
import java.net.URL;

public abstract class Image extends Rectangle
{
    public static final int DEFAULT = 0;
    public static final int RIGHT = 2;
    public static final int LEFT = 0;
    public static final int MIDDLE = 1;
    public static final int TEXTWRAP = 4;
    public static final int UNDERLYING = 8;
    public static final int AX = 0;
    public static final int AY = 1;
    public static final int BX = 2;
    public static final int BY = 3;
    public static final int CX = 4;
    public static final int CY = 5;
    public static final int DX = 6;
    public static final int DY = 7;
    public static final int ORIGINAL_NONE = 0;
    public static final int ORIGINAL_JPEG = 1;
    public static final int ORIGINAL_PNG = 2;
    public static final int ORIGINAL_GIF = 3;
    public static final int ORIGINAL_BMP = 4;
    public static final int ORIGINAL_TIFF = 5;
    public static final int ORIGINAL_WMF = 6;
    public static final int ORIGINAL_PS = 7;
    public static final int ORIGINAL_JPEG2000 = 8;
    public static final int ORIGINAL_JBIG2 = 9;
    protected int type;
    protected URL url;
    protected byte[] rawData;
    protected int bpc;
    protected PdfTemplate[] template;
    protected int alignment;
    protected String alt;
    protected float absoluteX;
    protected float absoluteY;
    protected float plainWidth;
    protected float plainHeight;
    protected float scaledWidth;
    protected float scaledHeight;
    protected int compressionLevel;
    protected Long mySerialId;
    public static final int[] PNGID;
    private PdfIndirectReference directReference;
    static long serialId;
    protected float rotationRadians;
    private float initialRotation;
    protected float indentationLeft;
    protected float indentationRight;
    protected float spacingBefore;
    protected float spacingAfter;
    private float widthPercentage;
    protected Annotation annotation;
    protected PdfOCG layer;
    protected boolean interpolation;
    protected int originalType;
    protected byte[] originalData;
    protected boolean deflated;
    protected int dpiX;
    protected int dpiY;
    private float XYRatio;
    protected int colorspace;
    protected boolean invert;
    protected ICC_Profile profile;
    private PdfDictionary additional;
    protected boolean mask;
    protected Image imageMask;
    private boolean smask;
    protected int[] transparency;
    
    public Image(final URL url) {
        super(0.0f, 0.0f);
        this.bpc = 1;
        this.template = new PdfTemplate[1];
        this.absoluteX = Float.NaN;
        this.absoluteY = Float.NaN;
        this.compressionLevel = -1;
        this.mySerialId = getSerialId();
        this.indentationLeft = 0.0f;
        this.indentationRight = 0.0f;
        this.widthPercentage = 100.0f;
        this.annotation = null;
        this.originalType = 0;
        this.deflated = false;
        this.dpiX = 0;
        this.dpiY = 0;
        this.XYRatio = 0.0f;
        this.colorspace = -1;
        this.invert = false;
        this.profile = null;
        this.additional = null;
        this.mask = false;
        this.url = url;
        this.alignment = 0;
        this.rotationRadians = 0.0f;
    }
    
    public static Image getInstance(final URL url) throws BadElementException, IOException {
        InputStream is = null;
        try {
            is = url.openStream();
            final int c1 = is.read();
            final int c2 = is.read();
            final int c3 = is.read();
            final int c4 = is.read();
            final int c5 = is.read();
            final int c6 = is.read();
            final int c7 = is.read();
            final int c8 = is.read();
            is.close();
            is = null;
            if (c1 == 71 && c2 == 73 && c3 == 70) {
                return ImageLoader.getGifImage(url);
            }
            if (c1 == 255 && c2 == 216) {
                return ImageLoader.getJpegImage(url);
            }
            if (c1 == 0 && c2 == 0 && c3 == 0 && c4 == 12) {
                return ImageLoader.getJpeg2000Image(url);
            }
            if (c1 == 255 && c2 == 79 && c3 == 255 && c4 == 81) {
                return ImageLoader.getJpeg2000Image(url);
            }
            if (c1 == Image.PNGID[0] && c2 == Image.PNGID[1] && c3 == Image.PNGID[2] && c4 == Image.PNGID[3]) {
                return ImageLoader.getPngImage(url);
            }
            if (c1 == 215 && c2 == 205) {
                return new ImgWMF(url);
            }
            if (c1 == 66 && c2 == 77) {
                return ImageLoader.getBmpImage(url);
            }
            if ((c1 == 77 && c2 == 77 && c3 == 0 && c4 == 42) || (c1 == 73 && c2 == 73 && c3 == 42 && c4 == 0)) {
                return ImageLoader.getTiffImage(url);
            }
            if (c1 == 151 && c2 == 74 && c3 == 66 && c4 == 50 && c5 == 13 && c6 == 10 && c7 == 26 && c8 == 10) {
                throw new IOException(url.toString() + " is not a recognized imageformat. JBIG2 support has been removed.");
            }
            throw new IOException(url.toString() + " is not a recognized imageformat.");
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    public static Image getInstance(final String filename) throws BadElementException, IOException {
        return getInstance(Utilities.toURL(filename));
    }
    
    public static Image getInstance(final byte[] imgb) throws BadElementException, IOException {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(imgb);
            final int c1 = is.read();
            final int c2 = is.read();
            final int c3 = is.read();
            final int c4 = is.read();
            is.close();
            is = null;
            if (c1 == 71 && c2 == 73 && c3 == 70) {
                return ImageLoader.getGifImage(imgb);
            }
            if (c1 == 255 && c2 == 216) {
                return ImageLoader.getJpegImage(imgb);
            }
            if (c1 == 0 && c2 == 0 && c3 == 0 && c4 == 12) {
                return ImageLoader.getJpeg2000Image(imgb);
            }
            if (c1 == 255 && c2 == 79 && c3 == 255 && c4 == 81) {
                return ImageLoader.getJpeg2000Image(imgb);
            }
            if (c1 == Image.PNGID[0] && c2 == Image.PNGID[1] && c3 == Image.PNGID[2] && c4 == Image.PNGID[3]) {
                return ImageLoader.getPngImage(imgb);
            }
            if (c1 == 215 && c2 == 205) {
                return new ImgWMF(imgb);
            }
            if (c1 == 66 && c2 == 77) {
                return ImageLoader.getBmpImage(imgb);
            }
            if ((c1 == 77 && c2 == 77 && c3 == 0 && c4 == 42) || (c1 == 73 && c2 == 73 && c3 == 42 && c4 == 0)) {
                return ImageLoader.getTiffImage(imgb);
            }
            if (c1 == 151 && c2 == 74 && c3 == 66 && c4 == 50) {
                is = new ByteArrayInputStream(imgb);
                is.skip(4L);
                final int c5 = is.read();
                final int c6 = is.read();
                final int c7 = is.read();
                final int c8 = is.read();
                if (c5 == 13 && c6 == 10 && c7 == 26 && c8 == 10) {
                    throw new IOException(MessageLocalization.getComposedMessage("the.byte.array.is.not.a.recognized.imageformat"));
                }
            }
            throw new IOException(MessageLocalization.getComposedMessage("the.byte.array.is.not.a.recognized.imageformat"));
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    public static Image getInstance(final int width, final int height, final int components, final int bpc, final byte[] data) throws BadElementException {
        return getInstance(width, height, components, bpc, data, null);
    }
    
    public static Image getInstance(final int width, final int height, final byte[] data, final byte[] globals) {
        final Image img = new ImgJBIG2(width, height, data, globals);
        return img;
    }
    
    public static Image getInstance(final int width, final int height, final boolean reverseBits, final int typeCCITT, final int parameters, final byte[] data) throws BadElementException {
        return getInstance(width, height, reverseBits, typeCCITT, parameters, data, null);
    }
    
    public static Image getInstance(final int width, final int height, final boolean reverseBits, final int typeCCITT, final int parameters, final byte[] data, final int[] transparency) throws BadElementException {
        if (transparency != null && transparency.length != 2) {
            throw new BadElementException(MessageLocalization.getComposedMessage("transparency.length.must.be.equal.to.2.with.ccitt.images"));
        }
        final Image img = new ImgCCITT(width, height, reverseBits, typeCCITT, parameters, data);
        img.transparency = transparency;
        return img;
    }
    
    public static Image getInstance(final int width, final int height, final int components, final int bpc, final byte[] data, final int[] transparency) throws BadElementException {
        if (transparency != null && transparency.length != components * 2) {
            throw new BadElementException(MessageLocalization.getComposedMessage("transparency.length.must.be.equal.to.componentes.2"));
        }
        if (components == 1 && bpc == 1) {
            final byte[] g4 = CCITTG4Encoder.compress(data, width, height);
            return getInstance(width, height, false, 256, 1, g4, transparency);
        }
        final Image img = new ImgRaw(width, height, components, bpc, data);
        img.transparency = transparency;
        return img;
    }
    
    public static Image getInstance(final PdfTemplate template) throws BadElementException {
        return new ImgTemplate(template);
    }
    
    public static Image getInstance(final java.awt.Image image, final Color color, boolean forceBW) throws BadElementException, IOException {
        if (image instanceof BufferedImage) {
            final BufferedImage bi = (BufferedImage)image;
            if (bi.getType() == 12) {
                forceBW = true;
            }
        }
        final PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, true);
        try {
            pg.grabPixels();
        }
        catch (final InterruptedException e) {
            throw new IOException(MessageLocalization.getComposedMessage("java.awt.image.interrupted.waiting.for.pixels"));
        }
        if ((pg.getStatus() & 0x80) != 0x0) {
            throw new IOException(MessageLocalization.getComposedMessage("java.awt.image.fetch.aborted.or.errored"));
        }
        final int w = pg.getWidth();
        final int h = pg.getHeight();
        final int[] pixels = (int[])pg.getPixels();
        if (forceBW) {
            final int byteWidth = w / 8 + (((w & 0x7) != 0x0) ? 1 : 0);
            final byte[] pixelsByte = new byte[byteWidth * h];
            int index = 0;
            final int size = h * w;
            int transColor = 1;
            if (color != null) {
                transColor = ((color.getRed() + color.getGreen() + color.getBlue() >= 384) ? 1 : 0);
            }
            int[] transparency = null;
            int cbyte = 128;
            int wMarker = 0;
            int currByte = 0;
            if (color != null) {
                for (int j = 0; j < size; ++j) {
                    final int alpha = pixels[j] >> 24 & 0xFF;
                    if (alpha < 250) {
                        if (transColor == 1) {
                            currByte |= cbyte;
                        }
                    }
                    else if ((pixels[j] & 0x888) != 0x0) {
                        currByte |= cbyte;
                    }
                    cbyte >>= 1;
                    if (cbyte == 0 || wMarker + 1 >= w) {
                        pixelsByte[index++] = (byte)currByte;
                        cbyte = 128;
                        currByte = 0;
                    }
                    if (++wMarker >= w) {
                        wMarker = 0;
                    }
                }
            }
            else {
                for (int j = 0; j < size; ++j) {
                    if (transparency == null) {
                        final int alpha = pixels[j] >> 24 & 0xFF;
                        if (alpha == 0) {
                            transparency = new int[2];
                            transparency[0] = (transparency[1] = (((pixels[j] & 0x888) != 0x0) ? 255 : 0));
                        }
                    }
                    if ((pixels[j] & 0x888) != 0x0) {
                        currByte |= cbyte;
                    }
                    cbyte >>= 1;
                    if (cbyte == 0 || wMarker + 1 >= w) {
                        pixelsByte[index++] = (byte)currByte;
                        cbyte = 128;
                        currByte = 0;
                    }
                    if (++wMarker >= w) {
                        wMarker = 0;
                    }
                }
            }
            return getInstance(w, h, 1, 1, pixelsByte, transparency);
        }
        final byte[] pixelsByte2 = new byte[w * h * 3];
        byte[] smask = null;
        int index = 0;
        final int size = h * w;
        int red = 255;
        int green = 255;
        int blue = 255;
        if (color != null) {
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
        }
        int[] transparency2 = null;
        if (color != null) {
            for (int i = 0; i < size; ++i) {
                final int alpha2 = pixels[i] >> 24 & 0xFF;
                if (alpha2 < 250) {
                    pixelsByte2[index++] = (byte)red;
                    pixelsByte2[index++] = (byte)green;
                    pixelsByte2[index++] = (byte)blue;
                }
                else {
                    pixelsByte2[index++] = (byte)(pixels[i] >> 16 & 0xFF);
                    pixelsByte2[index++] = (byte)(pixels[i] >> 8 & 0xFF);
                    pixelsByte2[index++] = (byte)(pixels[i] & 0xFF);
                }
            }
        }
        else {
            int transparentPixel = 0;
            smask = new byte[w * h];
            boolean shades = false;
            for (int k = 0; k < size; ++k) {
                final byte[] array = smask;
                final int n = k;
                final byte b = (byte)(pixels[k] >> 24 & 0xFF);
                array[n] = b;
                final byte alpha3 = b;
                if (!shades) {
                    if (alpha3 != 0 && alpha3 != -1) {
                        shades = true;
                    }
                    else if (transparency2 == null) {
                        if (alpha3 == 0) {
                            transparentPixel = (pixels[k] & 0xFFFFFF);
                            transparency2 = new int[6];
                            transparency2[0] = (transparency2[1] = (transparentPixel >> 16 & 0xFF));
                            transparency2[2] = (transparency2[3] = (transparentPixel >> 8 & 0xFF));
                            transparency2[4] = (transparency2[5] = (transparentPixel & 0xFF));
                        }
                    }
                    else if ((pixels[k] & 0xFFFFFF) != transparentPixel) {
                        shades = true;
                    }
                }
                pixelsByte2[index++] = (byte)(pixels[k] >> 16 & 0xFF);
                pixelsByte2[index++] = (byte)(pixels[k] >> 8 & 0xFF);
                pixelsByte2[index++] = (byte)(pixels[k] & 0xFF);
            }
            if (shades) {
                transparency2 = null;
            }
            else {
                smask = null;
            }
        }
        final Image img = getInstance(w, h, 3, 8, pixelsByte2, transparency2);
        if (smask != null) {
            final Image sm = getInstance(w, h, 1, 8, smask);
            try {
                sm.makeMask();
                img.setImageMask(sm);
            }
            catch (final DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }
        return img;
    }
    
    public static Image getInstance(final java.awt.Image image, final Color color) throws BadElementException, IOException {
        return getInstance(image, color, false);
    }
    
    public static Image getInstance(final PdfWriter writer, final java.awt.Image awtImage, final float quality) throws BadElementException, IOException {
        return getInstance(new PdfContentByte(writer), awtImage, quality);
    }
    
    public static Image getInstance(final PdfContentByte cb, final java.awt.Image awtImage, final float quality) throws BadElementException, IOException {
        final PixelGrabber pg = new PixelGrabber(awtImage, 0, 0, -1, -1, true);
        try {
            pg.grabPixels();
        }
        catch (final InterruptedException e) {
            throw new IOException(MessageLocalization.getComposedMessage("java.awt.image.interrupted.waiting.for.pixels"));
        }
        if ((pg.getStatus() & 0x80) != 0x0) {
            throw new IOException(MessageLocalization.getComposedMessage("java.awt.image.fetch.aborted.or.errored"));
        }
        final int w = pg.getWidth();
        final int h = pg.getHeight();
        final PdfTemplate tp = cb.createTemplate((float)w, (float)h);
        final Graphics2D g2d = tp.createGraphics((float)w, (float)h, true, quality);
        g2d.drawImage(awtImage, 0, 0, null);
        g2d.dispose();
        return getInstance(tp);
    }
    
    public PdfIndirectReference getDirectReference() {
        return this.directReference;
    }
    
    public void setDirectReference(final PdfIndirectReference directReference) {
        this.directReference = directReference;
    }
    
    public static Image getInstance(final PRIndirectReference ref) throws BadElementException {
        final PdfDictionary dic = (PdfDictionary)PdfReader.getPdfObjectRelease(ref);
        final int width = ((PdfNumber)PdfReader.getPdfObjectRelease(dic.get(PdfName.WIDTH))).intValue();
        final int height = ((PdfNumber)PdfReader.getPdfObjectRelease(dic.get(PdfName.HEIGHT))).intValue();
        Image imask = null;
        PdfObject obj = dic.get(PdfName.SMASK);
        if (obj != null && obj.isIndirect()) {
            imask = getInstance((PRIndirectReference)obj);
        }
        else {
            obj = dic.get(PdfName.MASK);
            if (obj != null && obj.isIndirect()) {
                final PdfObject obj2 = PdfReader.getPdfObjectRelease(obj);
                if (obj2 instanceof PdfDictionary) {
                    imask = getInstance((PRIndirectReference)obj);
                }
            }
        }
        final Image img = new ImgRaw(width, height, 1, 1, null);
        img.imageMask = imask;
        img.directReference = ref;
        return img;
    }
    
    protected Image(final Image image) {
        super(image);
        this.bpc = 1;
        this.template = new PdfTemplate[1];
        this.absoluteX = Float.NaN;
        this.absoluteY = Float.NaN;
        this.compressionLevel = -1;
        this.mySerialId = getSerialId();
        this.indentationLeft = 0.0f;
        this.indentationRight = 0.0f;
        this.widthPercentage = 100.0f;
        this.annotation = null;
        this.originalType = 0;
        this.deflated = false;
        this.dpiX = 0;
        this.dpiY = 0;
        this.XYRatio = 0.0f;
        this.colorspace = -1;
        this.invert = false;
        this.profile = null;
        this.additional = null;
        this.mask = false;
        this.type = image.type;
        this.url = image.url;
        this.rawData = image.rawData;
        this.bpc = image.bpc;
        this.template = image.template;
        this.alignment = image.alignment;
        this.alt = image.alt;
        this.absoluteX = image.absoluteX;
        this.absoluteY = image.absoluteY;
        this.plainWidth = image.plainWidth;
        this.plainHeight = image.plainHeight;
        this.scaledWidth = image.scaledWidth;
        this.scaledHeight = image.scaledHeight;
        this.mySerialId = image.mySerialId;
        this.directReference = image.directReference;
        this.rotationRadians = image.rotationRadians;
        this.initialRotation = image.initialRotation;
        this.indentationLeft = image.indentationLeft;
        this.indentationRight = image.indentationRight;
        this.spacingBefore = image.spacingBefore;
        this.spacingAfter = image.spacingAfter;
        this.widthPercentage = image.widthPercentage;
        this.annotation = image.annotation;
        this.layer = image.layer;
        this.interpolation = image.interpolation;
        this.originalType = image.originalType;
        this.originalData = image.originalData;
        this.deflated = image.deflated;
        this.dpiX = image.dpiX;
        this.dpiY = image.dpiY;
        this.XYRatio = image.XYRatio;
        this.colorspace = image.colorspace;
        this.invert = image.invert;
        this.profile = image.profile;
        this.additional = image.additional;
        this.mask = image.mask;
        this.imageMask = image.imageMask;
        this.smask = image.smask;
        this.transparency = image.transparency;
    }
    
    public static Image getInstance(final Image image) {
        if (image == null) {
            return null;
        }
        try {
            final Class cs = image.getClass();
            final Constructor constructor = cs.getDeclaredConstructor(Image.class);
            return constructor.newInstance(image);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    @Override
    public int type() {
        return this.type;
    }
    
    @Override
    public boolean isNestable() {
        return true;
    }
    
    public boolean isJpeg() {
        return this.type == 32;
    }
    
    public boolean isImgRaw() {
        return this.type == 34;
    }
    
    public boolean isImgTemplate() {
        return this.type == 35;
    }
    
    public URL getUrl() {
        return this.url;
    }
    
    public void setUrl(final URL url) {
        this.url = url;
    }
    
    public byte[] getRawData() {
        return this.rawData;
    }
    
    public int getBpc() {
        return this.bpc;
    }
    
    public PdfTemplate getTemplateData() {
        return this.template[0];
    }
    
    public void setTemplateData(final PdfTemplate template) {
        this.template[0] = template;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }
    
    public String getAlt() {
        return this.alt;
    }
    
    public void setAlt(final String alt) {
        this.alt = alt;
    }
    
    public void setAbsolutePosition(final float absoluteX, final float absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
    }
    
    public boolean hasAbsoluteX() {
        return !Float.isNaN(this.absoluteX);
    }
    
    public float getAbsoluteX() {
        return this.absoluteX;
    }
    
    public boolean hasAbsoluteY() {
        return !Float.isNaN(this.absoluteY);
    }
    
    public float getAbsoluteY() {
        return this.absoluteY;
    }
    
    public float getScaledWidth() {
        return this.scaledWidth;
    }
    
    public float getScaledHeight() {
        return this.scaledHeight;
    }
    
    public float getPlainWidth() {
        return this.plainWidth;
    }
    
    public float getPlainHeight() {
        return this.plainHeight;
    }
    
    public void scaleAbsolute(final float newWidth, final float newHeight) {
        this.plainWidth = newWidth;
        this.plainHeight = newHeight;
        final float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        this.setWidthPercentage(0.0f);
    }
    
    public void scaleAbsoluteWidth(final float newWidth) {
        this.plainWidth = newWidth;
        final float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        this.setWidthPercentage(0.0f);
    }
    
    public void scaleAbsoluteHeight(final float newHeight) {
        this.plainHeight = newHeight;
        final float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        this.setWidthPercentage(0.0f);
    }
    
    public void scalePercent(final float percent) {
        this.scalePercent(percent, percent);
    }
    
    public void scalePercent(final float percentX, final float percentY) {
        this.plainWidth = this.getWidth() * percentX / 100.0f;
        this.plainHeight = this.getHeight() * percentY / 100.0f;
        final float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        this.setWidthPercentage(0.0f);
    }
    
    public void scaleToFit(final float fitWidth, final float fitHeight) {
        this.scalePercent(100.0f);
        final float percentX = fitWidth * 100.0f / this.getScaledWidth();
        final float percentY = fitHeight * 100.0f / this.getScaledHeight();
        this.scalePercent((percentX < percentY) ? percentX : percentY);
        this.setWidthPercentage(0.0f);
    }
    
    public float[] matrix() {
        final float[] matrix = new float[8];
        final float cosX = (float)Math.cos(this.rotationRadians);
        final float sinX = (float)Math.sin(this.rotationRadians);
        matrix[0] = this.plainWidth * cosX;
        matrix[1] = this.plainWidth * sinX;
        matrix[2] = -this.plainHeight * sinX;
        matrix[3] = this.plainHeight * cosX;
        if (this.rotationRadians < 1.5707963267948966) {
            matrix[4] = matrix[2];
            matrix[5] = 0.0f;
            matrix[6] = matrix[0];
            matrix[7] = matrix[1] + matrix[3];
        }
        else if (this.rotationRadians < 3.141592653589793) {
            matrix[4] = matrix[0] + matrix[2];
            matrix[5] = matrix[3];
            matrix[6] = 0.0f;
            matrix[7] = matrix[1];
        }
        else if (this.rotationRadians < 4.71238898038469) {
            matrix[4] = matrix[0];
            matrix[5] = matrix[1] + matrix[3];
            matrix[6] = matrix[2];
            matrix[7] = 0.0f;
        }
        else {
            matrix[4] = 0.0f;
            matrix[5] = matrix[1];
            matrix[6] = matrix[0] + matrix[2];
            matrix[7] = matrix[3];
        }
        return matrix;
    }
    
    protected static synchronized Long getSerialId() {
        ++Image.serialId;
        return new Long(Image.serialId);
    }
    
    public Long getMySerialId() {
        return this.mySerialId;
    }
    
    public float getImageRotation() {
        final double d = 6.283185307179586;
        float rot = (float)((this.rotationRadians - this.initialRotation) % d);
        if (rot < 0.0f) {
            rot += (float)d;
        }
        return rot;
    }
    
    public void setRotation(final float r) {
        final double d = 6.283185307179586;
        this.rotationRadians = (float)((r + this.initialRotation) % d);
        if (this.rotationRadians < 0.0f) {
            this.rotationRadians += (float)d;
        }
        final float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
    }
    
    public void setRotationDegrees(final float deg) {
        final double d = 3.141592653589793;
        this.setRotation(deg / 180.0f * (float)d);
    }
    
    public float getInitialRotation() {
        return this.initialRotation;
    }
    
    public void setInitialRotation(final float initialRotation) {
        final float old_rot = this.rotationRadians - this.initialRotation;
        this.initialRotation = initialRotation;
        this.setRotation(old_rot);
    }
    
    public float getIndentationLeft() {
        return this.indentationLeft;
    }
    
    public void setIndentationLeft(final float f) {
        this.indentationLeft = f;
    }
    
    public float getIndentationRight() {
        return this.indentationRight;
    }
    
    public void setIndentationRight(final float f) {
        this.indentationRight = f;
    }
    
    public float getSpacingBefore() {
        return this.spacingBefore;
    }
    
    public void setSpacingBefore(final float spacing) {
        this.spacingBefore = spacing;
    }
    
    public float getSpacingAfter() {
        return this.spacingAfter;
    }
    
    public void setSpacingAfter(final float spacing) {
        this.spacingAfter = spacing;
    }
    
    public float getWidthPercentage() {
        return this.widthPercentage;
    }
    
    public void setWidthPercentage(final float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }
    
    public void setAnnotation(final Annotation annotation) {
        this.annotation = annotation;
    }
    
    public Annotation getAnnotation() {
        return this.annotation;
    }
    
    public PdfOCG getLayer() {
        return this.layer;
    }
    
    public void setLayer(final PdfOCG layer) {
        this.layer = layer;
    }
    
    public boolean isInterpolation() {
        return this.interpolation;
    }
    
    public void setInterpolation(final boolean interpolation) {
        this.interpolation = interpolation;
    }
    
    public int getOriginalType() {
        return this.originalType;
    }
    
    public void setOriginalType(final int originalType) {
        this.originalType = originalType;
    }
    
    public byte[] getOriginalData() {
        return this.originalData;
    }
    
    public void setOriginalData(final byte[] originalData) {
        this.originalData = originalData;
    }
    
    public boolean isDeflated() {
        return this.deflated;
    }
    
    public void setDeflated(final boolean deflated) {
        this.deflated = deflated;
    }
    
    public int getDpiX() {
        return this.dpiX;
    }
    
    public int getDpiY() {
        return this.dpiY;
    }
    
    public void setDpi(final int dpiX, final int dpiY) {
        this.dpiX = dpiX;
        this.dpiY = dpiY;
    }
    
    public float getXYRatio() {
        return this.XYRatio;
    }
    
    public void setXYRatio(final float XYRatio) {
        this.XYRatio = XYRatio;
    }
    
    public int getColorspace() {
        return this.colorspace;
    }
    
    public boolean isInverted() {
        return this.invert;
    }
    
    public void setInverted(final boolean invert) {
        this.invert = invert;
    }
    
    public void tagICC(final ICC_Profile profile) {
        this.profile = profile;
    }
    
    public boolean hasICCProfile() {
        return this.profile != null;
    }
    
    public ICC_Profile getICCProfile() {
        return this.profile;
    }
    
    public PdfDictionary getAdditional() {
        return this.additional;
    }
    
    public void setAdditional(final PdfDictionary additional) {
        this.additional = additional;
    }
    
    public void simplifyColorspace() {
        if (this.additional == null) {
            return;
        }
        final PdfArray value = this.additional.getAsArray(PdfName.COLORSPACE);
        if (value == null) {
            return;
        }
        final PdfObject cs = this.simplifyColorspace(value);
        PdfObject newValue;
        if (cs.isName()) {
            newValue = cs;
        }
        else {
            newValue = value;
            final PdfName first = value.getAsName(0);
            if (PdfName.INDEXED.equals(first) && value.size() >= 2) {
                final PdfArray second = value.getAsArray(1);
                if (second != null) {
                    value.set(1, this.simplifyColorspace(second));
                }
            }
        }
        this.additional.put(PdfName.COLORSPACE, newValue);
    }
    
    private PdfObject simplifyColorspace(final PdfArray obj) {
        if (obj == null) {
            return obj;
        }
        final PdfName first = obj.getAsName(0);
        if (PdfName.CALGRAY.equals(first)) {
            return PdfName.DEVICEGRAY;
        }
        if (PdfName.CALRGB.equals(first)) {
            return PdfName.DEVICERGB;
        }
        return obj;
    }
    
    public boolean isMask() {
        return this.mask;
    }
    
    public void makeMask() throws DocumentException {
        if (!this.isMaskCandidate()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("this.image.can.not.be.an.image.mask"));
        }
        this.mask = true;
    }
    
    public boolean isMaskCandidate() {
        return (this.type == 34 && this.bpc > 255) || this.colorspace == 1;
    }
    
    public Image getImageMask() {
        return this.imageMask;
    }
    
    public void setImageMask(final Image mask) throws DocumentException {
        if (this.mask) {
            throw new DocumentException(MessageLocalization.getComposedMessage("an.image.mask.cannot.contain.another.image.mask"));
        }
        if (!mask.mask) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.image.mask.is.not.a.mask.did.you.do.makemask"));
        }
        this.imageMask = mask;
        this.smask = (mask.bpc > 1 && mask.bpc <= 8);
    }
    
    public boolean isSmask() {
        return this.smask;
    }
    
    public void setSmask(final boolean smask) {
        this.smask = smask;
    }
    
    public int[] getTransparency() {
        return this.transparency;
    }
    
    public void setTransparency(final int[] transparency) {
        this.transparency = transparency;
    }
    
    public int getCompressionLevel() {
        return this.compressionLevel;
    }
    
    public void setCompressionLevel(final int compressionLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            this.compressionLevel = -1;
        }
        else {
            this.compressionLevel = compressionLevel;
        }
    }
    
    static {
        PNGID = new int[] { 137, 80, 78, 71, 13, 10, 26, 10 };
        Image.serialId = 0L;
    }
}
