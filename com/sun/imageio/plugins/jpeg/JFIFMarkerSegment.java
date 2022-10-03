package com.sun.imageio.plugins.jpeg;

import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import java.awt.image.RenderedImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageWriterSpi;
import java.io.OutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.color.ICC_Profile;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.IndexColorModel;
import java.util.List;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.color.ICC_ColorSpace;
import javax.imageio.IIOException;
import java.util.Iterator;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;
import java.io.IOException;
import java.util.ArrayList;

class JFIFMarkerSegment extends MarkerSegment
{
    int majorVersion;
    int minorVersion;
    int resUnits;
    int Xdensity;
    int Ydensity;
    int thumbWidth;
    int thumbHeight;
    JFIFThumbRGB thumb;
    ArrayList extSegments;
    ICCMarkerSegment iccSegment;
    private static final int THUMB_JPEG = 16;
    private static final int THUMB_PALETTE = 17;
    private static final int THUMB_UNASSIGNED = 18;
    private static final int THUMB_RGB = 19;
    private static final int DATA_SIZE = 14;
    private static final int ID_SIZE = 5;
    private final int MAX_THUMB_WIDTH = 255;
    private final int MAX_THUMB_HEIGHT = 255;
    private final boolean debug = false;
    private boolean inICC;
    private ICCMarkerSegment tempICCSegment;
    
    JFIFMarkerSegment() {
        super(224);
        this.thumb = null;
        this.extSegments = new ArrayList();
        this.iccSegment = null;
        this.inICC = false;
        this.tempICCSegment = null;
        this.majorVersion = 1;
        this.minorVersion = 2;
        this.resUnits = 0;
        this.Xdensity = 1;
        this.Ydensity = 1;
        this.thumbWidth = 0;
        this.thumbHeight = 0;
    }
    
    JFIFMarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
        super(jpegBuffer);
        this.thumb = null;
        this.extSegments = new ArrayList();
        this.iccSegment = null;
        this.inICC = false;
        this.tempICCSegment = null;
        jpegBuffer.bufPtr += 5;
        this.majorVersion = jpegBuffer.buf[jpegBuffer.bufPtr++];
        this.minorVersion = jpegBuffer.buf[jpegBuffer.bufPtr++];
        this.resUnits = jpegBuffer.buf[jpegBuffer.bufPtr++];
        this.Xdensity = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8;
        this.Xdensity |= (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        this.Ydensity = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8;
        this.Ydensity |= (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        this.thumbWidth = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        this.thumbHeight = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
        jpegBuffer.bufAvail -= 14;
        if (this.thumbWidth > 0) {
            this.thumb = new JFIFThumbRGB(jpegBuffer, this.thumbWidth, this.thumbHeight);
        }
    }
    
    JFIFMarkerSegment(final Node node) throws IIOInvalidTreeException {
        this();
        this.updateFromNativeNode(node, true);
    }
    
    @Override
    protected Object clone() {
        final JFIFMarkerSegment jfifMarkerSegment = (JFIFMarkerSegment)super.clone();
        if (!this.extSegments.isEmpty()) {
            jfifMarkerSegment.extSegments = new ArrayList();
            final Iterator iterator = this.extSegments.iterator();
            while (iterator.hasNext()) {
                jfifMarkerSegment.extSegments.add(((JFIFExtensionMarkerSegment)iterator.next()).clone());
            }
        }
        if (this.iccSegment != null) {
            jfifMarkerSegment.iccSegment = (ICCMarkerSegment)this.iccSegment.clone();
        }
        return jfifMarkerSegment;
    }
    
    void addJFXX(final JPEGBuffer jpegBuffer, final JPEGImageReader jpegImageReader) throws IOException {
        this.extSegments.add(new JFIFExtensionMarkerSegment(jpegBuffer, jpegImageReader));
    }
    
    void addICC(final JPEGBuffer jpegBuffer) throws IOException {
        if (!this.inICC) {
            if (this.iccSegment != null) {
                throw new IIOException("> 1 ICC APP2 Marker Segment not supported");
            }
            this.tempICCSegment = new ICCMarkerSegment(jpegBuffer);
            if (!this.inICC) {
                this.iccSegment = this.tempICCSegment;
                this.tempICCSegment = null;
            }
        }
        else if (this.tempICCSegment.addData(jpegBuffer)) {
            this.iccSegment = this.tempICCSegment;
            this.tempICCSegment = null;
        }
    }
    
    void addICC(final ICC_ColorSpace icc_ColorSpace) throws IOException {
        if (this.iccSegment != null) {
            throw new IIOException("> 1 ICC APP2 Marker Segment not supported");
        }
        this.iccSegment = new ICCMarkerSegment(icc_ColorSpace);
    }
    
    @Override
    IIOMetadataNode getNativeNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("app0JFIF");
        iioMetadataNode.setAttribute("majorVersion", Integer.toString(this.majorVersion));
        iioMetadataNode.setAttribute("minorVersion", Integer.toString(this.minorVersion));
        iioMetadataNode.setAttribute("resUnits", Integer.toString(this.resUnits));
        iioMetadataNode.setAttribute("Xdensity", Integer.toString(this.Xdensity));
        iioMetadataNode.setAttribute("Ydensity", Integer.toString(this.Ydensity));
        iioMetadataNode.setAttribute("thumbWidth", Integer.toString(this.thumbWidth));
        iioMetadataNode.setAttribute("thumbHeight", Integer.toString(this.thumbHeight));
        if (!this.extSegments.isEmpty()) {
            final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("JFXX");
            iioMetadataNode.appendChild(iioMetadataNode2);
            final Iterator iterator = this.extSegments.iterator();
            while (iterator.hasNext()) {
                iioMetadataNode2.appendChild(((JFIFExtensionMarkerSegment)iterator.next()).getNativeNode());
            }
        }
        if (this.iccSegment != null) {
            iioMetadataNode.appendChild(this.iccSegment.getNativeNode());
        }
        return iioMetadataNode;
    }
    
    void updateFromNativeNode(final Node node, final boolean b) throws IIOInvalidTreeException {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes.getLength() > 0) {
            final int attributeValue = MarkerSegment.getAttributeValue(node, attributes, "majorVersion", 0, 255, false);
            this.majorVersion = ((attributeValue != -1) ? attributeValue : this.majorVersion);
            final int attributeValue2 = MarkerSegment.getAttributeValue(node, attributes, "minorVersion", 0, 255, false);
            this.minorVersion = ((attributeValue2 != -1) ? attributeValue2 : this.minorVersion);
            final int attributeValue3 = MarkerSegment.getAttributeValue(node, attributes, "resUnits", 0, 2, false);
            this.resUnits = ((attributeValue3 != -1) ? attributeValue3 : this.resUnits);
            final int attributeValue4 = MarkerSegment.getAttributeValue(node, attributes, "Xdensity", 1, 65535, false);
            this.Xdensity = ((attributeValue4 != -1) ? attributeValue4 : this.Xdensity);
            final int attributeValue5 = MarkerSegment.getAttributeValue(node, attributes, "Ydensity", 1, 65535, false);
            this.Ydensity = ((attributeValue5 != -1) ? attributeValue5 : this.Ydensity);
            final int attributeValue6 = MarkerSegment.getAttributeValue(node, attributes, "thumbWidth", 0, 255, false);
            this.thumbWidth = ((attributeValue6 != -1) ? attributeValue6 : this.thumbWidth);
            final int attributeValue7 = MarkerSegment.getAttributeValue(node, attributes, "thumbHeight", 0, 255, false);
            this.thumbHeight = ((attributeValue7 != -1) ? attributeValue7 : this.thumbHeight);
        }
        if (node.hasChildNodes()) {
            final NodeList childNodes = node.getChildNodes();
            final int length = childNodes.getLength();
            if (length > 2) {
                throw new IIOInvalidTreeException("app0JFIF node cannot have > 2 children", node);
            }
            for (int i = 0; i < length; ++i) {
                final Node item = childNodes.item(i);
                final String nodeName = item.getNodeName();
                if (nodeName.equals("JFXX")) {
                    if (!this.extSegments.isEmpty() && b) {
                        throw new IIOInvalidTreeException("app0JFIF node cannot have > 1 JFXX node", node);
                    }
                    final NodeList childNodes2 = item.getChildNodes();
                    for (int length2 = childNodes2.getLength(), j = 0; j < length2; ++j) {
                        this.extSegments.add(new JFIFExtensionMarkerSegment(childNodes2.item(j)));
                    }
                }
                if (nodeName.equals("app2ICC")) {
                    if (this.iccSegment != null && b) {
                        throw new IIOInvalidTreeException("> 1 ICC APP2 Marker Segment not supported", node);
                    }
                    this.iccSegment = new ICCMarkerSegment(item);
                }
            }
        }
    }
    
    int getThumbnailWidth(int n) {
        if (this.thumb != null) {
            if (n == 0) {
                return this.thumb.getWidth();
            }
            --n;
        }
        return this.extSegments.get(n).thumb.getWidth();
    }
    
    int getThumbnailHeight(int n) {
        if (this.thumb != null) {
            if (n == 0) {
                return this.thumb.getHeight();
            }
            --n;
        }
        return this.extSegments.get(n).thumb.getHeight();
    }
    
    BufferedImage getThumbnail(final ImageInputStream imageInputStream, int n, final JPEGImageReader jpegImageReader) throws IOException {
        jpegImageReader.thumbnailStarted(n);
        BufferedImage bufferedImage;
        if (this.thumb != null && n == 0) {
            bufferedImage = this.thumb.getThumbnail(imageInputStream, jpegImageReader);
        }
        else {
            if (this.thumb != null) {
                --n;
            }
            bufferedImage = this.extSegments.get(n).thumb.getThumbnail(imageInputStream, jpegImageReader);
        }
        jpegImageReader.thumbnailComplete();
        return bufferedImage;
    }
    
    void write(final ImageOutputStream imageOutputStream, final JPEGImageWriter jpegImageWriter) throws IOException {
        this.write(imageOutputStream, null, jpegImageWriter);
    }
    
    void write(final ImageOutputStream imageOutputStream, final BufferedImage bufferedImage, final JPEGImageWriter jpegImageWriter) throws IOException {
        int min = 0;
        int min2 = 0;
        int length = 0;
        int[] pixels = null;
        if (bufferedImage != null) {
            final int width = bufferedImage.getWidth();
            final int height = bufferedImage.getHeight();
            if (width > 255 || height > 255) {
                jpegImageWriter.warningOccurred(12);
            }
            min = Math.min(width, 255);
            min2 = Math.min(height, 255);
            pixels = bufferedImage.getRaster().getPixels(0, 0, min, min2, (int[])null);
            length = pixels.length;
        }
        this.length = 16 + length;
        this.writeTag(imageOutputStream);
        imageOutputStream.write(new byte[] { 74, 70, 73, 70, 0 });
        imageOutputStream.write(this.majorVersion);
        imageOutputStream.write(this.minorVersion);
        imageOutputStream.write(this.resUnits);
        MarkerSegment.write2bytes(imageOutputStream, this.Xdensity);
        MarkerSegment.write2bytes(imageOutputStream, this.Ydensity);
        imageOutputStream.write(min);
        imageOutputStream.write(min2);
        if (pixels != null) {
            jpegImageWriter.thumbnailStarted(0);
            this.writeThumbnailData(imageOutputStream, pixels, jpegImageWriter);
            jpegImageWriter.thumbnailComplete();
        }
    }
    
    void writeThumbnailData(final ImageOutputStream imageOutputStream, final int[] array, final JPEGImageWriter jpegImageWriter) throws IOException {
        int n = array.length / 20;
        if (n == 0) {
            n = 1;
        }
        for (int i = 0; i < array.length; ++i) {
            imageOutputStream.write(array[i]);
            if (i > n && i % n == 0) {
                jpegImageWriter.thumbnailProgress(i * 100.0f / array.length);
            }
        }
    }
    
    void writeWithThumbs(final ImageOutputStream imageOutputStream, final List list, final JPEGImageWriter jpegImageWriter) throws IOException {
        if (list != null) {
            JFIFExtensionMarkerSegment jfifExtensionMarkerSegment = null;
            if (list.size() == 1) {
                if (!this.extSegments.isEmpty()) {
                    jfifExtensionMarkerSegment = this.extSegments.get(0);
                }
                this.writeThumb(imageOutputStream, list.get(0), jfifExtensionMarkerSegment, 0, true, jpegImageWriter);
            }
            else {
                this.write(imageOutputStream, jpegImageWriter);
                for (int i = 0; i < list.size(); ++i) {
                    JFIFExtensionMarkerSegment jfifExtensionMarkerSegment2 = null;
                    if (i < this.extSegments.size()) {
                        jfifExtensionMarkerSegment2 = this.extSegments.get(i);
                    }
                    this.writeThumb(imageOutputStream, list.get(i), jfifExtensionMarkerSegment2, i, false, jpegImageWriter);
                }
            }
        }
        else {
            this.write(imageOutputStream, jpegImageWriter);
        }
    }
    
    private void writeThumb(final ImageOutputStream imageOutputStream, final BufferedImage bufferedImage, final JFIFExtensionMarkerSegment jfifExtensionMarkerSegment, final int n, final boolean b, final JPEGImageWriter jpegImageWriter) throws IOException {
        final ColorModel colorModel = bufferedImage.getColorModel();
        final ColorSpace colorSpace = colorModel.getColorSpace();
        if (colorModel instanceof IndexColorModel) {
            if (b) {
                this.write(imageOutputStream, jpegImageWriter);
            }
            if (jfifExtensionMarkerSegment == null || jfifExtensionMarkerSegment.code == 17) {
                this.writeJFXXSegment(n, bufferedImage, imageOutputStream, jpegImageWriter);
            }
            else {
                jfifExtensionMarkerSegment.setThumbnail(((IndexColorModel)colorModel).convertToIntDiscrete(bufferedImage.getRaster(), false));
                jpegImageWriter.thumbnailStarted(n);
                jfifExtensionMarkerSegment.write(imageOutputStream, jpegImageWriter);
                jpegImageWriter.thumbnailComplete();
            }
        }
        else if (colorSpace.getType() == 5) {
            if (jfifExtensionMarkerSegment == null) {
                if (b) {
                    this.write(imageOutputStream, bufferedImage, jpegImageWriter);
                }
                else {
                    this.writeJFXXSegment(n, bufferedImage, imageOutputStream, jpegImageWriter);
                }
            }
            else {
                if (b) {
                    this.write(imageOutputStream, jpegImageWriter);
                }
                if (jfifExtensionMarkerSegment.code == 17) {
                    this.writeJFXXSegment(n, bufferedImage, imageOutputStream, jpegImageWriter);
                    jpegImageWriter.warningOccurred(14);
                }
                else {
                    jfifExtensionMarkerSegment.setThumbnail(bufferedImage);
                    jpegImageWriter.thumbnailStarted(n);
                    jfifExtensionMarkerSegment.write(imageOutputStream, jpegImageWriter);
                    jpegImageWriter.thumbnailComplete();
                }
            }
        }
        else if (colorSpace.getType() == 6) {
            if (jfifExtensionMarkerSegment == null) {
                if (b) {
                    this.write(imageOutputStream, expandGrayThumb(bufferedImage), jpegImageWriter);
                }
                else {
                    this.writeJFXXSegment(n, bufferedImage, imageOutputStream, jpegImageWriter);
                }
            }
            else {
                if (b) {
                    this.write(imageOutputStream, jpegImageWriter);
                }
                if (jfifExtensionMarkerSegment.code == 19) {
                    this.writeJFXXSegment(n, expandGrayThumb(bufferedImage), imageOutputStream, jpegImageWriter);
                }
                else if (jfifExtensionMarkerSegment.code == 16) {
                    jfifExtensionMarkerSegment.setThumbnail(bufferedImage);
                    jpegImageWriter.thumbnailStarted(n);
                    jfifExtensionMarkerSegment.write(imageOutputStream, jpegImageWriter);
                    jpegImageWriter.thumbnailComplete();
                }
                else if (jfifExtensionMarkerSegment.code == 17) {
                    this.writeJFXXSegment(n, bufferedImage, imageOutputStream, jpegImageWriter);
                    jpegImageWriter.warningOccurred(15);
                }
            }
        }
        else {
            jpegImageWriter.warningOccurred(9);
        }
    }
    
    private void writeJFXXSegment(final int n, final BufferedImage bufferedImage, final ImageOutputStream imageOutputStream, final JPEGImageWriter jpegImageWriter) throws IOException {
        JFIFExtensionMarkerSegment jfifExtensionMarkerSegment;
        try {
            jfifExtensionMarkerSegment = new JFIFExtensionMarkerSegment(bufferedImage);
        }
        catch (final IllegalThumbException ex) {
            jpegImageWriter.warningOccurred(9);
            return;
        }
        jpegImageWriter.thumbnailStarted(n);
        jfifExtensionMarkerSegment.write(imageOutputStream, jpegImageWriter);
        jpegImageWriter.thumbnailComplete();
    }
    
    private static BufferedImage expandGrayThumb(final BufferedImage bufferedImage) {
        final BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 1);
        bufferedImage2.getGraphics().drawImage(bufferedImage, 0, 0, null);
        return bufferedImage2;
    }
    
    static void writeDefaultJFIF(final ImageOutputStream imageOutputStream, final List list, final ICC_Profile icc_Profile, final JPEGImageWriter jpegImageWriter) throws IOException {
        new JFIFMarkerSegment().writeWithThumbs(imageOutputStream, list, jpegImageWriter);
        if (icc_Profile != null) {
            writeICC(icc_Profile, imageOutputStream);
        }
    }
    
    @Override
    void print() {
        this.printTag("JFIF");
        System.out.print("Version ");
        System.out.print(this.majorVersion);
        System.out.println(".0" + Integer.toString(this.minorVersion));
        System.out.print("Resolution units: ");
        System.out.println(this.resUnits);
        System.out.print("X density: ");
        System.out.println(this.Xdensity);
        System.out.print("Y density: ");
        System.out.println(this.Ydensity);
        System.out.print("Thumbnail Width: ");
        System.out.println(this.thumbWidth);
        System.out.print("Thumbnail Height: ");
        System.out.println(this.thumbHeight);
        if (!this.extSegments.isEmpty()) {
            final Iterator iterator = this.extSegments.iterator();
            while (iterator.hasNext()) {
                ((JFIFExtensionMarkerSegment)iterator.next()).print();
            }
        }
        if (this.iccSegment != null) {
            this.iccSegment.print();
        }
    }
    
    static void writeICC(final ICC_Profile icc_Profile, final ImageOutputStream imageOutputStream) throws IOException {
        final int n = 2;
        final int n2 = "ICC_PROFILE".length() + 1;
        final int n3 = 2;
        final int n4 = 65535 - n - n2 - n3;
        final byte[] data = icc_Profile.getData();
        int n5 = data.length / n4;
        if (data.length % n4 != 0) {
            ++n5;
        }
        int n6 = 1;
        int n7 = 0;
        for (int i = 0; i < n5; ++i) {
            final int min = Math.min(data.length - n7, n4);
            final int n8 = min + n3 + n2 + n;
            imageOutputStream.write(255);
            imageOutputStream.write(226);
            MarkerSegment.write2bytes(imageOutputStream, n8);
            imageOutputStream.write("ICC_PROFILE".getBytes("US-ASCII"));
            imageOutputStream.write(0);
            imageOutputStream.write(n6++);
            imageOutputStream.write(n5);
            imageOutputStream.write(data, n7, min);
            n7 += min;
        }
    }
    
    private class IllegalThumbException extends Exception
    {
    }
    
    class JFIFExtensionMarkerSegment extends MarkerSegment
    {
        int code;
        JFIFThumb thumb;
        private static final int DATA_SIZE = 6;
        private static final int ID_SIZE = 5;
        
        JFIFExtensionMarkerSegment(final JPEGBuffer jpegBuffer, final JPEGImageReader jpegImageReader) throws IOException {
            super(jpegBuffer);
            jpegBuffer.bufPtr += 5;
            this.code = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
            jpegBuffer.bufAvail -= 6;
            if (this.code == 16) {
                this.thumb = new JFIFThumbJPEG(jpegBuffer, this.length, jpegImageReader);
            }
            else {
                jpegBuffer.loadBuf(2);
                final int n = jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF;
                final int n2 = jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF;
                jpegBuffer.bufAvail -= 2;
                if (this.code == 17) {
                    this.thumb = new JFIFThumbPalette(jpegBuffer, n, n2);
                }
                else {
                    this.thumb = new JFIFThumbRGB(jpegBuffer, n, n2);
                }
            }
        }
        
        JFIFExtensionMarkerSegment(final Node node) throws IIOInvalidTreeException {
            super(224);
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes.getLength() > 0) {
                this.code = MarkerSegment.getAttributeValue(node, attributes, "extensionCode", 16, 19, false);
                if (this.code == 18) {
                    throw new IIOInvalidTreeException("invalid extensionCode attribute value", node);
                }
            }
            else {
                this.code = 18;
            }
            if (node.getChildNodes().getLength() != 1) {
                throw new IIOInvalidTreeException("app0JFXX node must have exactly 1 child", node);
            }
            final Node firstChild = node.getFirstChild();
            final String nodeName = firstChild.getNodeName();
            if (nodeName.equals("JFIFthumbJPEG")) {
                if (this.code == 18) {
                    this.code = 16;
                }
                this.thumb = new JFIFThumbJPEG(firstChild);
            }
            else if (nodeName.equals("JFIFthumbPalette")) {
                if (this.code == 18) {
                    this.code = 17;
                }
                this.thumb = new JFIFThumbPalette(firstChild);
            }
            else {
                if (!nodeName.equals("JFIFthumbRGB")) {
                    throw new IIOInvalidTreeException("unrecognized app0JFXX child node", node);
                }
                if (this.code == 18) {
                    this.code = 19;
                }
                this.thumb = new JFIFThumbRGB(firstChild);
            }
        }
        
        JFIFExtensionMarkerSegment(final BufferedImage bufferedImage) throws IllegalThumbException {
            super(224);
            final ColorModel colorModel = bufferedImage.getColorModel();
            final int type = colorModel.getColorSpace().getType();
            if (colorModel.hasAlpha()) {
                throw new IllegalThumbException();
            }
            if (colorModel instanceof IndexColorModel) {
                this.code = 17;
                this.thumb = new JFIFThumbPalette(bufferedImage);
            }
            else if (type == 5) {
                this.code = 19;
                this.thumb = new JFIFThumbRGB(bufferedImage);
            }
            else {
                if (type != 6) {
                    throw new IllegalThumbException();
                }
                this.code = 16;
                this.thumb = new JFIFThumbJPEG(bufferedImage);
            }
        }
        
        void setThumbnail(final BufferedImage bufferedImage) {
            try {
                switch (this.code) {
                    case 17: {
                        this.thumb = new JFIFThumbPalette(bufferedImage);
                        break;
                    }
                    case 19: {
                        this.thumb = new JFIFThumbRGB(bufferedImage);
                        break;
                    }
                    case 16: {
                        this.thumb = new JFIFThumbJPEG(bufferedImage);
                        break;
                    }
                }
            }
            catch (final IllegalThumbException ex) {
                throw new InternalError("Illegal thumb in setThumbnail!", ex);
            }
        }
        
        @Override
        protected Object clone() {
            final JFIFExtensionMarkerSegment jfifExtensionMarkerSegment = (JFIFExtensionMarkerSegment)super.clone();
            if (this.thumb != null) {
                jfifExtensionMarkerSegment.thumb = (JFIFThumb)this.thumb.clone();
            }
            return jfifExtensionMarkerSegment;
        }
        
        @Override
        IIOMetadataNode getNativeNode() {
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("app0JFXX");
            iioMetadataNode.setAttribute("extensionCode", Integer.toString(this.code));
            iioMetadataNode.appendChild(this.thumb.getNativeNode());
            return iioMetadataNode;
        }
        
        void write(final ImageOutputStream imageOutputStream, final JPEGImageWriter jpegImageWriter) throws IOException {
            this.length = 8 + this.thumb.getLength();
            this.writeTag(imageOutputStream);
            imageOutputStream.write(new byte[] { 74, 70, 88, 88, 0 });
            imageOutputStream.write(this.code);
            this.thumb.write(imageOutputStream, jpegImageWriter);
        }
        
        @Override
        void print() {
            this.printTag("JFXX");
            this.thumb.print();
        }
    }
    
    abstract class JFIFThumb implements Cloneable
    {
        long streamPos;
        
        abstract int getLength();
        
        abstract int getWidth();
        
        abstract int getHeight();
        
        abstract BufferedImage getThumbnail(final ImageInputStream p0, final JPEGImageReader p1) throws IOException;
        
        protected JFIFThumb() {
            this.streamPos = -1L;
        }
        
        protected JFIFThumb(final JPEGBuffer jpegBuffer) throws IOException {
            this.streamPos = -1L;
            this.streamPos = jpegBuffer.getStreamPosition();
        }
        
        abstract void print();
        
        abstract IIOMetadataNode getNativeNode();
        
        abstract void write(final ImageOutputStream p0, final JPEGImageWriter p1) throws IOException;
        
        @Override
        protected Object clone() {
            try {
                return super.clone();
            }
            catch (final CloneNotSupportedException ex) {
                return null;
            }
        }
    }
    
    abstract class JFIFThumbUncompressed extends JFIFThumb
    {
        BufferedImage thumbnail;
        int thumbWidth;
        int thumbHeight;
        String name;
        
        JFIFThumbUncompressed(final JPEGBuffer jpegBuffer, final int thumbWidth, final int thumbHeight, final int n, final String name) throws IOException {
            super(jpegBuffer);
            this.thumbnail = null;
            this.thumbWidth = thumbWidth;
            this.thumbHeight = thumbHeight;
            jpegBuffer.skipData(n);
            this.name = name;
        }
        
        JFIFThumbUncompressed(final Node node, final String name) throws IIOInvalidTreeException {
            this.thumbnail = null;
            this.thumbWidth = 0;
            this.thumbHeight = 0;
            this.name = name;
            final NamedNodeMap attributes = node.getAttributes();
            final int length = attributes.getLength();
            if (length > 2) {
                throw new IIOInvalidTreeException(name + " node cannot have > 2 attributes", node);
            }
            if (length != 0) {
                final int attributeValue = MarkerSegment.getAttributeValue(node, attributes, "thumbWidth", 0, 255, false);
                this.thumbWidth = ((attributeValue != -1) ? attributeValue : this.thumbWidth);
                final int attributeValue2 = MarkerSegment.getAttributeValue(node, attributes, "thumbHeight", 0, 255, false);
                this.thumbHeight = ((attributeValue2 != -1) ? attributeValue2 : this.thumbHeight);
            }
        }
        
        JFIFThumbUncompressed(final BufferedImage thumbnail) {
            this.thumbnail = null;
            this.thumbnail = thumbnail;
            this.thumbWidth = thumbnail.getWidth();
            this.thumbHeight = thumbnail.getHeight();
            this.name = null;
        }
        
        void readByteBuffer(final ImageInputStream imageInputStream, final byte[] array, final JPEGImageReader jpegImageReader, final float n, final float n2) throws IOException {
            final int max = Math.max((int)(array.length / 20 / n), 1);
            int i = 0;
            while (i < array.length) {
                imageInputStream.read(array, i, Math.min(max, array.length - i));
                i += max;
                float n3 = i * 100.0f / array.length * n + n2;
                if (n3 > 100.0f) {
                    n3 = 100.0f;
                }
                jpegImageReader.thumbnailProgress(n3);
            }
        }
        
        @Override
        int getWidth() {
            return this.thumbWidth;
        }
        
        @Override
        int getHeight() {
            return this.thumbHeight;
        }
        
        @Override
        IIOMetadataNode getNativeNode() {
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode(this.name);
            iioMetadataNode.setAttribute("thumbWidth", Integer.toString(this.thumbWidth));
            iioMetadataNode.setAttribute("thumbHeight", Integer.toString(this.thumbHeight));
            return iioMetadataNode;
        }
        
        @Override
        void write(final ImageOutputStream imageOutputStream, final JPEGImageWriter jpegImageWriter) throws IOException {
            if (this.thumbWidth > 255 || this.thumbHeight > 255) {
                jpegImageWriter.warningOccurred(12);
            }
            this.thumbWidth = Math.min(this.thumbWidth, 255);
            this.thumbHeight = Math.min(this.thumbHeight, 255);
            imageOutputStream.write(this.thumbWidth);
            imageOutputStream.write(this.thumbHeight);
        }
        
        void writePixels(final ImageOutputStream imageOutputStream, final JPEGImageWriter jpegImageWriter) throws IOException {
            if (this.thumbWidth > 255 || this.thumbHeight > 255) {
                jpegImageWriter.warningOccurred(12);
            }
            this.thumbWidth = Math.min(this.thumbWidth, 255);
            this.thumbHeight = Math.min(this.thumbHeight, 255);
            JFIFMarkerSegment.this.writeThumbnailData(imageOutputStream, this.thumbnail.getRaster().getPixels(0, 0, this.thumbWidth, this.thumbHeight, (int[])null), jpegImageWriter);
        }
        
        @Override
        void print() {
            System.out.print(this.name + " width: ");
            System.out.println(this.thumbWidth);
            System.out.print(this.name + " height: ");
            System.out.println(this.thumbHeight);
        }
    }
    
    class JFIFThumbRGB extends JFIFThumbUncompressed
    {
        JFIFThumbRGB(final JPEGBuffer jpegBuffer, final int n, final int n2) throws IOException {
            super(jpegBuffer, n, n2, n * n2 * 3, "JFIFthumbRGB");
        }
        
        JFIFThumbRGB(final Node node) throws IIOInvalidTreeException {
            super(node, "JFIFthumbRGB");
        }
        
        JFIFThumbRGB(final BufferedImage bufferedImage) throws IllegalThumbException {
            super(bufferedImage);
        }
        
        @Override
        int getLength() {
            return this.thumbWidth * this.thumbHeight * 3;
        }
        
        @Override
        BufferedImage getThumbnail(final ImageInputStream imageInputStream, final JPEGImageReader jpegImageReader) throws IOException {
            imageInputStream.mark();
            imageInputStream.seek(this.streamPos);
            final DataBufferByte dataBufferByte = new DataBufferByte(this.getLength());
            this.readByteBuffer(imageInputStream, dataBufferByte.getData(), jpegImageReader, 1.0f, 0.0f);
            imageInputStream.reset();
            return new BufferedImage(new ComponentColorModel(JPEG.JCS.sRGB, false, false, 1, 0), Raster.createInterleavedRaster(dataBufferByte, this.thumbWidth, this.thumbHeight, this.thumbWidth * 3, 3, new int[] { 0, 1, 2 }, null), false, null);
        }
        
        @Override
        void write(final ImageOutputStream imageOutputStream, final JPEGImageWriter jpegImageWriter) throws IOException {
            super.write(imageOutputStream, jpegImageWriter);
            this.writePixels(imageOutputStream, jpegImageWriter);
        }
    }
    
    class JFIFThumbPalette extends JFIFThumbUncompressed
    {
        private static final int PALETTE_SIZE = 768;
        
        JFIFThumbPalette(final JPEGBuffer jpegBuffer, final int n, final int n2) throws IOException {
            super(jpegBuffer, n, n2, 768 + n * n2, "JFIFThumbPalette");
        }
        
        JFIFThumbPalette(final Node node) throws IIOInvalidTreeException {
            super(node, "JFIFThumbPalette");
        }
        
        JFIFThumbPalette(final BufferedImage bufferedImage) throws IllegalThumbException {
            super(bufferedImage);
            if (((IndexColorModel)this.thumbnail.getColorModel()).getMapSize() > 256) {
                throw new IllegalThumbException();
            }
        }
        
        @Override
        int getLength() {
            return this.thumbWidth * this.thumbHeight + 768;
        }
        
        @Override
        BufferedImage getThumbnail(final ImageInputStream imageInputStream, final JPEGImageReader jpegImageReader) throws IOException {
            imageInputStream.mark();
            imageInputStream.seek(this.streamPos);
            final byte[] array = new byte[768];
            final float n = 768.0f / this.getLength();
            this.readByteBuffer(imageInputStream, array, jpegImageReader, n, 0.0f);
            final DataBufferByte dataBufferByte = new DataBufferByte(this.thumbWidth * this.thumbHeight);
            this.readByteBuffer(imageInputStream, dataBufferByte.getData(), jpegImageReader, 1.0f - n, n);
            imageInputStream.read();
            imageInputStream.reset();
            final IndexColorModel indexColorModel = new IndexColorModel(8, 256, array, 0, false);
            return new BufferedImage(indexColorModel, Raster.createWritableRaster(indexColorModel.createCompatibleSampleModel(this.thumbWidth, this.thumbHeight), dataBufferByte, null), false, null);
        }
        
        @Override
        void write(final ImageOutputStream imageOutputStream, final JPEGImageWriter jpegImageWriter) throws IOException {
            super.write(imageOutputStream, jpegImageWriter);
            final byte[] array = new byte[768];
            final IndexColorModel indexColorModel = (IndexColorModel)this.thumbnail.getColorModel();
            final byte[] array2 = new byte[256];
            final byte[] array3 = new byte[256];
            final byte[] array4 = new byte[256];
            indexColorModel.getReds(array2);
            indexColorModel.getGreens(array3);
            indexColorModel.getBlues(array4);
            for (int i = 0; i < 256; ++i) {
                array[i * 3] = array2[i];
                array[i * 3 + 1] = array3[i];
                array[i * 3 + 2] = array4[i];
            }
            imageOutputStream.write(array);
            this.writePixels(imageOutputStream, jpegImageWriter);
        }
    }
    
    class JFIFThumbJPEG extends JFIFThumb
    {
        JPEGMetadata thumbMetadata;
        byte[] data;
        private static final int PREAMBLE_SIZE = 6;
        
        JFIFThumbJPEG(final JPEGBuffer jpegBuffer, final int n, final JPEGImageReader jpegImageReader) throws IOException {
            super(jpegBuffer);
            this.thumbMetadata = null;
            this.data = null;
            final long n2 = this.streamPos + (n - 6);
            jpegBuffer.iis.seek(this.streamPos);
            this.thumbMetadata = new JPEGMetadata(false, true, jpegBuffer.iis, jpegImageReader);
            jpegBuffer.iis.seek(n2);
            jpegBuffer.bufAvail = 0;
            jpegBuffer.bufPtr = 0;
        }
        
        JFIFThumbJPEG(final Node node) throws IIOInvalidTreeException {
            this.thumbMetadata = null;
            this.data = null;
            if (node.getChildNodes().getLength() > 1) {
                throw new IIOInvalidTreeException("JFIFThumbJPEG node must have 0 or 1 child", node);
            }
            final Node firstChild = node.getFirstChild();
            if (firstChild != null) {
                if (!firstChild.getNodeName().equals("markerSequence")) {
                    throw new IIOInvalidTreeException("JFIFThumbJPEG child must be a markerSequence node", node);
                }
                (this.thumbMetadata = new JPEGMetadata(false, true)).setFromMarkerSequenceNode(firstChild);
            }
        }
        
        JFIFThumbJPEG(final BufferedImage bufferedImage) throws IllegalThumbException {
            this.thumbMetadata = null;
            this.data = null;
            final int n = 4096;
            final int n2 = 65527;
            try {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(n);
                final MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(byteArrayOutputStream);
                final JPEGImageWriter jpegImageWriter = new JPEGImageWriter(null);
                jpegImageWriter.setOutput(output);
                final JPEGMetadata jpegMetadata = (JPEGMetadata)jpegImageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(bufferedImage), null);
                final MarkerSegment markerSegment = jpegMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
                if (markerSegment == null) {
                    throw new IllegalThumbException();
                }
                jpegMetadata.markerSequence.remove(markerSegment);
                jpegImageWriter.write(new IIOImage(bufferedImage, null, jpegMetadata));
                jpegImageWriter.dispose();
                if (byteArrayOutputStream.size() > n2) {
                    throw new IllegalThumbException();
                }
                this.data = byteArrayOutputStream.toByteArray();
            }
            catch (final IOException ex) {
                throw new IllegalThumbException();
            }
        }
        
        @Override
        int getWidth() {
            int samplesPerLine = 0;
            final SOFMarkerSegment sofMarkerSegment = (SOFMarkerSegment)this.thumbMetadata.findMarkerSegment(SOFMarkerSegment.class, true);
            if (sofMarkerSegment != null) {
                samplesPerLine = sofMarkerSegment.samplesPerLine;
            }
            return samplesPerLine;
        }
        
        @Override
        int getHeight() {
            int numLines = 0;
            final SOFMarkerSegment sofMarkerSegment = (SOFMarkerSegment)this.thumbMetadata.findMarkerSegment(SOFMarkerSegment.class, true);
            if (sofMarkerSegment != null) {
                numLines = sofMarkerSegment.numLines;
            }
            return numLines;
        }
        
        @Override
        BufferedImage getThumbnail(final ImageInputStream input, final JPEGImageReader jpegImageReader) throws IOException {
            input.mark();
            input.seek(this.streamPos);
            final JPEGImageReader jpegImageReader2 = new JPEGImageReader(null);
            jpegImageReader2.setInput(input);
            jpegImageReader2.addIIOReadProgressListener(new ThumbnailReadListener(jpegImageReader));
            final BufferedImage read = jpegImageReader2.read(0, null);
            jpegImageReader2.dispose();
            input.reset();
            return read;
        }
        
        @Override
        protected Object clone() {
            final JFIFThumbJPEG jfifThumbJPEG = (JFIFThumbJPEG)super.clone();
            if (this.thumbMetadata != null) {
                jfifThumbJPEG.thumbMetadata = (JPEGMetadata)this.thumbMetadata.clone();
            }
            return jfifThumbJPEG;
        }
        
        @Override
        IIOMetadataNode getNativeNode() {
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("JFIFthumbJPEG");
            if (this.thumbMetadata != null) {
                iioMetadataNode.appendChild(this.thumbMetadata.getNativeTree());
            }
            return iioMetadataNode;
        }
        
        @Override
        int getLength() {
            if (this.data == null) {
                return 0;
            }
            return this.data.length;
        }
        
        @Override
        void write(final ImageOutputStream imageOutputStream, final JPEGImageWriter jpegImageWriter) throws IOException {
            int n = this.data.length / 20;
            if (n == 0) {
                n = 1;
            }
            int i = 0;
            while (i < this.data.length) {
                imageOutputStream.write(this.data, i, Math.min(n, this.data.length - i));
                i += n;
                float n2 = i * 100.0f / this.data.length;
                if (n2 > 100.0f) {
                    n2 = 100.0f;
                }
                jpegImageWriter.thumbnailProgress(n2);
            }
        }
        
        @Override
        void print() {
            System.out.println("JFIF thumbnail stored as JPEG");
        }
        
        private class ThumbnailReadListener implements IIOReadProgressListener
        {
            JPEGImageReader reader;
            
            ThumbnailReadListener(final JPEGImageReader reader) {
                this.reader = null;
                this.reader = reader;
            }
            
            @Override
            public void sequenceStarted(final ImageReader imageReader, final int n) {
            }
            
            @Override
            public void sequenceComplete(final ImageReader imageReader) {
            }
            
            @Override
            public void imageStarted(final ImageReader imageReader, final int n) {
            }
            
            @Override
            public void imageProgress(final ImageReader imageReader, final float n) {
                this.reader.thumbnailProgress(n);
            }
            
            @Override
            public void imageComplete(final ImageReader imageReader) {
            }
            
            @Override
            public void thumbnailStarted(final ImageReader imageReader, final int n, final int n2) {
            }
            
            @Override
            public void thumbnailProgress(final ImageReader imageReader, final float n) {
            }
            
            @Override
            public void thumbnailComplete(final ImageReader imageReader) {
            }
            
            @Override
            public void readAborted(final ImageReader imageReader) {
            }
        }
    }
    
    class ICCMarkerSegment extends MarkerSegment
    {
        ArrayList chunks;
        byte[] profile;
        private static final int ID_SIZE = 12;
        int chunksRead;
        int numChunks;
        
        ICCMarkerSegment(final ICC_ColorSpace icc_ColorSpace) {
            super(226);
            this.chunks = null;
            this.profile = null;
            this.chunks = null;
            this.chunksRead = 0;
            this.numChunks = 0;
            this.profile = icc_ColorSpace.getProfile().getData();
        }
        
        ICCMarkerSegment(final JPEGBuffer jpegBuffer) throws IOException {
            super(jpegBuffer);
            this.chunks = null;
            this.profile = null;
            jpegBuffer.bufPtr += 12;
            jpegBuffer.bufAvail -= 12;
            this.length -= 12;
            final int n = jpegBuffer.buf[jpegBuffer.bufPtr] & 0xFF;
            this.numChunks = (jpegBuffer.buf[jpegBuffer.bufPtr + 1] & 0xFF);
            if (n > this.numChunks) {
                throw new IIOException("Image format Error; chunk num > num chunks");
            }
            if (this.numChunks == 1) {
                this.length -= 2;
                this.profile = new byte[this.length];
                jpegBuffer.bufPtr += 2;
                jpegBuffer.bufAvail -= 2;
                jpegBuffer.readData(this.profile);
                JFIFMarkerSegment.this.inICC = false;
            }
            else {
                final byte[] array = new byte[this.length];
                this.length -= 2;
                jpegBuffer.readData(array);
                (this.chunks = new ArrayList()).add(array);
                this.chunksRead = 1;
                JFIFMarkerSegment.this.inICC = true;
            }
        }
        
        ICCMarkerSegment(final Node node) throws IIOInvalidTreeException {
            super(226);
            this.chunks = null;
            this.profile = null;
            if (node instanceof IIOMetadataNode) {
                final ICC_Profile icc_Profile = (ICC_Profile)((IIOMetadataNode)node).getUserObject();
                if (icc_Profile != null) {
                    this.profile = icc_Profile.getData();
                }
            }
        }
        
        @Override
        protected Object clone() {
            final ICCMarkerSegment iccMarkerSegment = (ICCMarkerSegment)super.clone();
            if (this.profile != null) {
                iccMarkerSegment.profile = this.profile.clone();
            }
            return iccMarkerSegment;
        }
        
        boolean addData(final JPEGBuffer jpegBuffer) throws IOException {
            ++jpegBuffer.bufPtr;
            --jpegBuffer.bufAvail;
            int n = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8 | (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
            jpegBuffer.bufAvail -= 2;
            n -= 2;
            jpegBuffer.bufPtr += 12;
            jpegBuffer.bufAvail -= 12;
            n -= 12;
            if ((jpegBuffer.buf[jpegBuffer.bufPtr] & 0xFF) > this.numChunks) {
                throw new IIOException("Image format Error; chunk num > num chunks");
            }
            if (this.numChunks != (jpegBuffer.buf[jpegBuffer.bufPtr + 1] & 0xFF)) {
                throw new IIOException("Image format Error; icc num chunks mismatch");
            }
            n -= 2;
            boolean b = false;
            final byte[] array = new byte[n];
            jpegBuffer.readData(array);
            this.chunks.add(array);
            this.length += n;
            ++this.chunksRead;
            if (this.chunksRead < this.numChunks) {
                JFIFMarkerSegment.this.inICC = true;
            }
            else {
                this.profile = new byte[this.length];
                int n2 = 0;
                for (byte b2 = 1; b2 <= this.numChunks; ++b2) {
                    boolean b3 = false;
                    for (int i = 0; i < this.chunks.size(); ++i) {
                        final byte[] array2 = this.chunks.get(i);
                        if (array2[0] == b2) {
                            System.arraycopy(array2, 2, this.profile, n2, array2.length - 2);
                            n2 += array2.length - 2;
                            b3 = true;
                        }
                    }
                    if (!b3) {
                        throw new IIOException("Image Format Error: Missing ICC chunk num " + b2);
                    }
                }
                this.chunks = null;
                this.chunksRead = 0;
                this.numChunks = 0;
                JFIFMarkerSegment.this.inICC = false;
                b = true;
            }
            return b;
        }
        
        @Override
        IIOMetadataNode getNativeNode() {
            final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("app2ICC");
            if (this.profile != null) {
                iioMetadataNode.setUserObject(ICC_Profile.getInstance(this.profile));
            }
            return iioMetadataNode;
        }
        
        @Override
        void write(final ImageOutputStream imageOutputStream) throws IOException {
        }
        
        @Override
        void print() {
            this.printTag("ICC Profile APP2");
        }
    }
}
