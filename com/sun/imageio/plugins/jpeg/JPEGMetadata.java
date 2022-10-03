package com.sun.imageio.plugins.jpeg;

import java.awt.color.ICC_Profile;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Point;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import java.util.ListIterator;
import java.util.Iterator;
import java.awt.image.ColorModel;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ColorSpace;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.ImageWriteParam;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;

public class JPEGMetadata extends IIOMetadata implements Cloneable
{
    private static final boolean debug = false;
    private List resetSequence;
    private boolean inThumb;
    private boolean hasAlpha;
    List markerSequence;
    final boolean isStream;
    private boolean transparencyDone;
    
    JPEGMetadata(final boolean isStream, final boolean inThumb) {
        super(true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null);
        this.resetSequence = null;
        this.inThumb = false;
        this.markerSequence = new ArrayList();
        this.inThumb = inThumb;
        this.isStream = isStream;
        if (isStream) {
            this.nativeMetadataFormatName = "javax_imageio_jpeg_stream_1.0";
            this.nativeMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat";
        }
    }
    
    JPEGMetadata(final boolean b, final boolean b2, final ImageInputStream imageInputStream, final JPEGImageReader jpegImageReader) throws IOException {
        this(b, b2);
        final JPEGBuffer jpegBuffer = new JPEGBuffer(imageInputStream);
        jpegBuffer.loadBuf(0);
        if ((jpegBuffer.buf[0] & 0xFF) != 0xFF || (jpegBuffer.buf[1] & 0xFF) != 0xD8 || (jpegBuffer.buf[2] & 0xFF) != 0xFF) {
            throw new IIOException("Image format error");
        }
        int i = 0;
        final JPEGBuffer jpegBuffer2 = jpegBuffer;
        jpegBuffer2.bufAvail -= 2;
        jpegBuffer.bufPtr = 2;
        MarkerSegment markerSegment = null;
        while (i == 0) {
            jpegBuffer.loadBuf(1);
            jpegBuffer.scanForFF(jpegImageReader);
            switch (jpegBuffer.buf[jpegBuffer.bufPtr] & 0xFF) {
                case 0: {
                    final JPEGBuffer jpegBuffer3 = jpegBuffer;
                    --jpegBuffer3.bufAvail;
                    final JPEGBuffer jpegBuffer4 = jpegBuffer;
                    ++jpegBuffer4.bufPtr;
                    break;
                }
                case 192:
                case 193:
                case 194: {
                    if (b) {
                        throw new IIOException("SOF not permitted in stream metadata");
                    }
                    markerSegment = new SOFMarkerSegment(jpegBuffer);
                    break;
                }
                case 219: {
                    markerSegment = new DQTMarkerSegment(jpegBuffer);
                    break;
                }
                case 196: {
                    markerSegment = new DHTMarkerSegment(jpegBuffer);
                    break;
                }
                case 221: {
                    markerSegment = new DRIMarkerSegment(jpegBuffer);
                    break;
                }
                case 224: {
                    jpegBuffer.loadBuf(8);
                    final byte[] buf = jpegBuffer.buf;
                    final int bufPtr = jpegBuffer.bufPtr;
                    if (buf[bufPtr + 3] == 74 && buf[bufPtr + 4] == 70 && buf[bufPtr + 5] == 73 && buf[bufPtr + 6] == 70 && buf[bufPtr + 7] == 0) {
                        if (this.inThumb) {
                            jpegImageReader.warningOccurred(1);
                            final JFIFMarkerSegment jfifMarkerSegment = new JFIFMarkerSegment(jpegBuffer);
                            break;
                        }
                        if (b) {
                            throw new IIOException("JFIF not permitted in stream metadata");
                        }
                        if (!this.markerSequence.isEmpty()) {
                            throw new IIOException("JFIF APP0 must be first marker after SOI");
                        }
                        markerSegment = new JFIFMarkerSegment(jpegBuffer);
                        break;
                    }
                    else {
                        if (buf[bufPtr + 3] != 74 || buf[bufPtr + 4] != 70 || buf[bufPtr + 5] != 88 || buf[bufPtr + 6] != 88 || buf[bufPtr + 7] != 0) {
                            markerSegment = new MarkerSegment(jpegBuffer);
                            markerSegment.loadData(jpegBuffer);
                            break;
                        }
                        if (b) {
                            throw new IIOException("JFXX not permitted in stream metadata");
                        }
                        if (this.inThumb) {
                            throw new IIOException("JFXX markers not allowed in JFIF JPEG thumbnail");
                        }
                        final JFIFMarkerSegment jfifMarkerSegment2 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
                        if (jfifMarkerSegment2 == null) {
                            throw new IIOException("JFXX encountered without prior JFIF!");
                        }
                        jfifMarkerSegment2.addJFXX(jpegBuffer, jpegImageReader);
                        break;
                    }
                    break;
                }
                case 226: {
                    jpegBuffer.loadBuf(15);
                    if (jpegBuffer.buf[jpegBuffer.bufPtr + 3] != 73 || jpegBuffer.buf[jpegBuffer.bufPtr + 4] != 67 || jpegBuffer.buf[jpegBuffer.bufPtr + 5] != 67 || jpegBuffer.buf[jpegBuffer.bufPtr + 6] != 95 || jpegBuffer.buf[jpegBuffer.bufPtr + 7] != 80 || jpegBuffer.buf[jpegBuffer.bufPtr + 8] != 82 || jpegBuffer.buf[jpegBuffer.bufPtr + 9] != 79 || jpegBuffer.buf[jpegBuffer.bufPtr + 10] != 70 || jpegBuffer.buf[jpegBuffer.bufPtr + 11] != 73 || jpegBuffer.buf[jpegBuffer.bufPtr + 12] != 76 || jpegBuffer.buf[jpegBuffer.bufPtr + 13] != 69 || jpegBuffer.buf[jpegBuffer.bufPtr + 14] != 0) {
                        markerSegment = new MarkerSegment(jpegBuffer);
                        markerSegment.loadData(jpegBuffer);
                        break;
                    }
                    if (b) {
                        throw new IIOException("ICC profiles not permitted in stream metadata");
                    }
                    final JFIFMarkerSegment jfifMarkerSegment3 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
                    if (jfifMarkerSegment3 == null) {
                        markerSegment = new MarkerSegment(jpegBuffer);
                        markerSegment.loadData(jpegBuffer);
                    }
                    else {
                        jfifMarkerSegment3.addICC(jpegBuffer);
                    }
                    break;
                }
                case 238: {
                    jpegBuffer.loadBuf(8);
                    if (jpegBuffer.buf[jpegBuffer.bufPtr + 3] != 65 || jpegBuffer.buf[jpegBuffer.bufPtr + 4] != 100 || jpegBuffer.buf[jpegBuffer.bufPtr + 5] != 111 || jpegBuffer.buf[jpegBuffer.bufPtr + 6] != 98 || jpegBuffer.buf[jpegBuffer.bufPtr + 7] != 101) {
                        markerSegment = new MarkerSegment(jpegBuffer);
                        markerSegment.loadData(jpegBuffer);
                        break;
                    }
                    if (b) {
                        throw new IIOException("Adobe APP14 markers not permitted in stream metadata");
                    }
                    markerSegment = new AdobeMarkerSegment(jpegBuffer);
                    break;
                }
                case 254: {
                    markerSegment = new COMMarkerSegment(jpegBuffer);
                    break;
                }
                case 218: {
                    if (b) {
                        throw new IIOException("SOS not permitted in stream metadata");
                    }
                    markerSegment = new SOSMarkerSegment(jpegBuffer);
                    break;
                }
                case 208:
                case 209:
                case 210:
                case 211:
                case 212:
                case 213:
                case 214:
                case 215: {
                    final JPEGBuffer jpegBuffer5 = jpegBuffer;
                    ++jpegBuffer5.bufPtr;
                    final JPEGBuffer jpegBuffer6 = jpegBuffer;
                    --jpegBuffer6.bufAvail;
                    break;
                }
                case 217: {
                    i = 1;
                    final JPEGBuffer jpegBuffer7 = jpegBuffer;
                    ++jpegBuffer7.bufPtr;
                    final JPEGBuffer jpegBuffer8 = jpegBuffer;
                    --jpegBuffer8.bufAvail;
                    break;
                }
                default: {
                    markerSegment = new MarkerSegment(jpegBuffer);
                    markerSegment.loadData(jpegBuffer);
                    markerSegment.unknown = true;
                    break;
                }
            }
            if (markerSegment != null) {
                this.markerSequence.add(markerSegment);
                markerSegment = null;
            }
        }
        jpegBuffer.pushBack();
        if (!this.isConsistent()) {
            throw new IIOException("Inconsistent metadata read from stream");
        }
    }
    
    JPEGMetadata(final ImageWriteParam imageWriteParam, final JPEGImageWriter jpegImageWriter) {
        this(true, false);
        JPEGImageWriteParam jpegImageWriteParam = null;
        if (imageWriteParam != null && imageWriteParam instanceof JPEGImageWriteParam) {
            jpegImageWriteParam = (JPEGImageWriteParam)imageWriteParam;
            if (!jpegImageWriteParam.areTablesSet()) {
                jpegImageWriteParam = null;
            }
        }
        if (jpegImageWriteParam != null) {
            this.markerSequence.add(new DQTMarkerSegment(jpegImageWriteParam.getQTables()));
            this.markerSequence.add(new DHTMarkerSegment(jpegImageWriteParam.getDCHuffmanTables(), jpegImageWriteParam.getACHuffmanTables()));
        }
        else {
            this.markerSequence.add(new DQTMarkerSegment(JPEG.getDefaultQTables()));
            this.markerSequence.add(new DHTMarkerSegment(JPEG.getDefaultHuffmanTables(true), JPEG.getDefaultHuffmanTables(false)));
        }
        if (!this.isConsistent()) {
            throw new InternalError("Default stream metadata is inconsistent");
        }
    }
    
    JPEGMetadata(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam, final JPEGImageWriter jpegImageWriter) {
        this(false, false);
        boolean b = true;
        boolean b2 = false;
        int n = 0;
        boolean b3 = true;
        boolean b4 = false;
        boolean b5 = false;
        boolean b6 = false;
        boolean b7 = true;
        boolean b8 = true;
        float compressionQuality = 0.75f;
        final byte[] array = { 1, 2, 3, 4 };
        int n2 = 0;
        ImageTypeSpecifier destinationType = null;
        if (imageWriteParam != null) {
            destinationType = imageWriteParam.getDestinationType();
            if (destinationType != null && imageTypeSpecifier != null) {
                jpegImageWriter.warningOccurred(0);
                destinationType = null;
            }
            if (imageWriteParam.canWriteProgressive() && imageWriteParam.getProgressiveMode() == 1) {
                b5 = true;
                b8 = false;
            }
            if (imageWriteParam instanceof JPEGImageWriteParam) {
                final JPEGImageWriteParam jpegImageWriteParam = (JPEGImageWriteParam)imageWriteParam;
                if (jpegImageWriteParam.areTablesSet()) {
                    b7 = false;
                    b8 = false;
                    if (jpegImageWriteParam.getDCHuffmanTables().length > 2 || jpegImageWriteParam.getACHuffmanTables().length > 2) {
                        b6 = true;
                    }
                }
                if (!b5 && jpegImageWriteParam.getOptimizeHuffmanTables()) {
                    b8 = false;
                }
            }
            if (imageWriteParam.canWriteCompressed() && imageWriteParam.getCompressionMode() == 2) {
                compressionQuality = imageWriteParam.getCompressionQuality();
            }
        }
        ColorSpace colorSpace = null;
        if (destinationType != null) {
            final ColorModel colorModel = destinationType.getColorModel();
            n2 = colorModel.getNumComponents();
            final boolean b9 = colorModel.getNumColorComponents() != n2;
            final boolean hasAlpha = colorModel.hasAlpha();
            colorSpace = colorModel.getColorSpace();
            switch (colorSpace.getType()) {
                case 6: {
                    b3 = false;
                    if (b9) {
                        b = false;
                        break;
                    }
                    break;
                }
                case 3: {
                    if (!b9) {
                        break;
                    }
                    b = false;
                    if (!hasAlpha) {
                        b2 = true;
                        n = 2;
                        break;
                    }
                    break;
                }
                case 5: {
                    b = false;
                    b2 = true;
                    b3 = false;
                    array[0] = 82;
                    array[1] = 71;
                    array[2] = 66;
                    if (hasAlpha) {
                        array[3] = 65;
                        break;
                    }
                    break;
                }
                default: {
                    b = false;
                    b3 = false;
                    break;
                }
            }
        }
        else if (imageTypeSpecifier != null) {
            final ColorModel colorModel2 = imageTypeSpecifier.getColorModel();
            n2 = colorModel2.getNumComponents();
            final boolean b10 = colorModel2.getNumColorComponents() != n2;
            final boolean hasAlpha2 = colorModel2.hasAlpha();
            colorSpace = colorModel2.getColorSpace();
            switch (colorSpace.getType()) {
                case 6: {
                    b3 = false;
                    if (b10) {
                        b = false;
                        break;
                    }
                    break;
                }
                case 5: {
                    if (hasAlpha2) {
                        b = false;
                        break;
                    }
                    break;
                }
                case 13: {
                    b = false;
                    b3 = false;
                    if (!colorSpace.equals(ColorSpace.getInstance(1002))) {
                        break;
                    }
                    b3 = true;
                    b2 = true;
                    array[0] = 89;
                    array[1] = 67;
                    array[2] = 99;
                    if (hasAlpha2) {
                        array[3] = 65;
                        break;
                    }
                    break;
                }
                case 3: {
                    if (!b10) {
                        break;
                    }
                    b = false;
                    if (!hasAlpha2) {
                        b2 = true;
                        n = 2;
                        break;
                    }
                    break;
                }
                case 9: {
                    b = false;
                    b2 = true;
                    n = 2;
                    break;
                }
                default: {
                    b = false;
                    b3 = false;
                    break;
                }
            }
        }
        if (b && JPEG.isNonStandardICC(colorSpace)) {
            b4 = true;
        }
        if (b) {
            final JFIFMarkerSegment jfifMarkerSegment = new JFIFMarkerSegment();
            this.markerSequence.add(jfifMarkerSegment);
            if (b4) {
                try {
                    jfifMarkerSegment.addICC((ICC_ColorSpace)colorSpace);
                }
                catch (final IOException ex) {}
            }
        }
        if (b2) {
            this.markerSequence.add(new AdobeMarkerSegment(n));
        }
        if (b7) {
            this.markerSequence.add(new DQTMarkerSegment(compressionQuality, b3));
        }
        if (b8) {
            this.markerSequence.add(new DHTMarkerSegment(b3));
        }
        this.markerSequence.add(new SOFMarkerSegment(b5, b6, b3, array, n2));
        if (!b5) {
            this.markerSequence.add(new SOSMarkerSegment(b3, array, n2));
        }
        if (!this.isConsistent()) {
            throw new InternalError("Default image metadata is inconsistent");
        }
    }
    
    MarkerSegment findMarkerSegment(final int n) {
        for (final MarkerSegment markerSegment : this.markerSequence) {
            if (markerSegment.tag == n) {
                return markerSegment;
            }
        }
        return null;
    }
    
    MarkerSegment findMarkerSegment(final Class clazz, final boolean b) {
        if (b) {
            for (final MarkerSegment markerSegment : this.markerSequence) {
                if (clazz.isInstance(markerSegment)) {
                    return markerSegment;
                }
            }
        }
        else {
            final ListIterator listIterator = this.markerSequence.listIterator(this.markerSequence.size());
            while (listIterator.hasPrevious()) {
                final MarkerSegment markerSegment2 = (MarkerSegment)listIterator.previous();
                if (clazz.isInstance(markerSegment2)) {
                    return markerSegment2;
                }
            }
        }
        return null;
    }
    
    private int findMarkerSegmentPosition(final Class clazz, final boolean b) {
        if (b) {
            final ListIterator listIterator = this.markerSequence.listIterator();
            int n = 0;
            while (listIterator.hasNext()) {
                if (clazz.isInstance(listIterator.next())) {
                    return n;
                }
                ++n;
            }
        }
        else {
            final ListIterator listIterator2 = this.markerSequence.listIterator(this.markerSequence.size());
            int n2 = this.markerSequence.size() - 1;
            while (listIterator2.hasPrevious()) {
                if (clazz.isInstance(listIterator2.previous())) {
                    return n2;
                }
                --n2;
            }
        }
        return -1;
    }
    
    private int findLastUnknownMarkerSegmentPosition() {
        final ListIterator listIterator = this.markerSequence.listIterator(this.markerSequence.size());
        int n = this.markerSequence.size() - 1;
        while (listIterator.hasPrevious()) {
            if (((MarkerSegment)listIterator.previous()).unknown) {
                return n;
            }
            --n;
        }
        return -1;
    }
    
    @Override
    protected Object clone() {
        JPEGMetadata jpegMetadata = null;
        try {
            jpegMetadata = (JPEGMetadata)super.clone();
        }
        catch (final CloneNotSupportedException ex) {}
        if (this.markerSequence != null) {
            jpegMetadata.markerSequence = this.cloneSequence();
        }
        jpegMetadata.resetSequence = null;
        return jpegMetadata;
    }
    
    private List cloneSequence() {
        if (this.markerSequence == null) {
            return null;
        }
        final ArrayList list = new ArrayList(this.markerSequence.size());
        final Iterator iterator = this.markerSequence.iterator();
        while (iterator.hasNext()) {
            list.add(((MarkerSegment)iterator.next()).clone());
        }
        return list;
    }
    
    @Override
    public Node getAsTree(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("null formatName!");
        }
        if (this.isStream) {
            if (s.equals("javax_imageio_jpeg_stream_1.0")) {
                return this.getNativeTree();
            }
        }
        else {
            if (s.equals("javax_imageio_jpeg_image_1.0")) {
                return this.getNativeTree();
            }
            if (s.equals("javax_imageio_1.0")) {
                return this.getStandardTree();
            }
        }
        throw new IllegalArgumentException("Unsupported format name: " + s);
    }
    
    IIOMetadataNode getNativeTree() {
        final Iterator iterator = this.markerSequence.iterator();
        IIOMetadataNode iioMetadataNode2;
        IIOMetadataNode iioMetadataNode;
        if (this.isStream) {
            iioMetadataNode = (iioMetadataNode2 = new IIOMetadataNode("javax_imageio_jpeg_stream_1.0"));
        }
        else {
            final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("markerSequence");
            if (!this.inThumb) {
                iioMetadataNode = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
                final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("JPEGvariety");
                iioMetadataNode.appendChild(iioMetadataNode4);
                final JFIFMarkerSegment jfifMarkerSegment = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
                if (jfifMarkerSegment != null) {
                    iterator.next();
                    iioMetadataNode4.appendChild(jfifMarkerSegment.getNativeNode());
                }
                iioMetadataNode.appendChild(iioMetadataNode3);
            }
            else {
                iioMetadataNode = iioMetadataNode3;
            }
            iioMetadataNode2 = iioMetadataNode3;
        }
        while (iterator.hasNext()) {
            iioMetadataNode2.appendChild(((MarkerSegment)iterator.next()).getNativeNode());
        }
        return iioMetadataNode;
    }
    
    @Override
    protected IIOMetadataNode getStandardChromaNode() {
        this.hasAlpha = false;
        final SOFMarkerSegment sofMarkerSegment = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
        if (sofMarkerSegment == null) {
            return null;
        }
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Chroma");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final int length = sofMarkerSegment.componentSpecs.length;
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("NumChannels");
        iioMetadataNode.appendChild(iioMetadataNode3);
        iioMetadataNode3.setAttribute("value", Integer.toString(length));
        if (this.findMarkerSegment(JFIFMarkerSegment.class, true) != null) {
            if (length == 1) {
                iioMetadataNode2.setAttribute("name", "GRAY");
            }
            else {
                iioMetadataNode2.setAttribute("name", "YCbCr");
            }
            return iioMetadataNode;
        }
        final AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
        if (adobeMarkerSegment != null) {
            switch (adobeMarkerSegment.transform) {
                case 2: {
                    iioMetadataNode2.setAttribute("name", "YCCK");
                    break;
                }
                case 1: {
                    iioMetadataNode2.setAttribute("name", "YCbCr");
                    break;
                }
                case 0: {
                    if (length == 3) {
                        iioMetadataNode2.setAttribute("name", "RGB");
                        break;
                    }
                    if (length == 4) {
                        iioMetadataNode2.setAttribute("name", "CMYK");
                        break;
                    }
                    break;
                }
            }
            return iioMetadataNode;
        }
        if (length < 3) {
            iioMetadataNode2.setAttribute("name", "GRAY");
            if (length == 2) {
                this.hasAlpha = true;
            }
            return iioMetadataNode;
        }
        boolean b = true;
        for (int i = 0; i < sofMarkerSegment.componentSpecs.length; ++i) {
            final int componentId = sofMarkerSegment.componentSpecs[i].componentId;
            if (componentId < 1 || componentId >= sofMarkerSegment.componentSpecs.length) {
                b = false;
            }
        }
        if (b) {
            iioMetadataNode2.setAttribute("name", "YCbCr");
            if (length == 4) {
                this.hasAlpha = true;
            }
            return iioMetadataNode;
        }
        if (sofMarkerSegment.componentSpecs[0].componentId == 82 && sofMarkerSegment.componentSpecs[1].componentId == 71 && sofMarkerSegment.componentSpecs[2].componentId == 66) {
            iioMetadataNode2.setAttribute("name", "RGB");
            if (length == 4 && sofMarkerSegment.componentSpecs[3].componentId == 65) {
                this.hasAlpha = true;
            }
            return iioMetadataNode;
        }
        if (sofMarkerSegment.componentSpecs[0].componentId == 89 && sofMarkerSegment.componentSpecs[1].componentId == 67 && sofMarkerSegment.componentSpecs[2].componentId == 99) {
            iioMetadataNode2.setAttribute("name", "PhotoYCC");
            if (length == 4 && sofMarkerSegment.componentSpecs[3].componentId == 65) {
                this.hasAlpha = true;
            }
            return iioMetadataNode;
        }
        boolean b2 = false;
        final int hsamplingFactor = sofMarkerSegment.componentSpecs[0].HsamplingFactor;
        final int vsamplingFactor = sofMarkerSegment.componentSpecs[0].VsamplingFactor;
        for (int j = 1; j < sofMarkerSegment.componentSpecs.length; ++j) {
            if (sofMarkerSegment.componentSpecs[j].HsamplingFactor != hsamplingFactor || sofMarkerSegment.componentSpecs[j].VsamplingFactor != vsamplingFactor) {
                b2 = true;
                break;
            }
        }
        if (b2) {
            iioMetadataNode2.setAttribute("name", "YCbCr");
            if (length == 4) {
                this.hasAlpha = true;
            }
            return iioMetadataNode;
        }
        if (length == 3) {
            iioMetadataNode2.setAttribute("name", "RGB");
        }
        else {
            iioMetadataNode2.setAttribute("name", "CMYK");
        }
        return iioMetadataNode;
    }
    
    @Override
    protected IIOMetadataNode getStandardCompressionNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Compression");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
        iioMetadataNode2.setAttribute("value", "JPEG");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("Lossless");
        iioMetadataNode3.setAttribute("value", "FALSE");
        iioMetadataNode.appendChild(iioMetadataNode3);
        int n = 0;
        final Iterator iterator = this.markerSequence.iterator();
        while (iterator.hasNext()) {
            if (((MarkerSegment)iterator.next()).tag == 218) {
                ++n;
            }
        }
        if (n != 0) {
            final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("NumProgressiveScans");
            iioMetadataNode4.setAttribute("value", Integer.toString(n));
            iioMetadataNode.appendChild(iioMetadataNode4);
        }
        return iioMetadataNode;
    }
    
    @Override
    protected IIOMetadataNode getStandardDimensionNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Dimension");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("ImageOrientation");
        iioMetadataNode2.setAttribute("value", "normal");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final JFIFMarkerSegment jfifMarkerSegment = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
        if (jfifMarkerSegment != null) {
            float n;
            if (jfifMarkerSegment.resUnits == 0) {
                n = jfifMarkerSegment.Xdensity / (float)jfifMarkerSegment.Ydensity;
            }
            else {
                n = jfifMarkerSegment.Ydensity / (float)jfifMarkerSegment.Xdensity;
            }
            final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("PixelAspectRatio");
            iioMetadataNode3.setAttribute("value", Float.toString(n));
            iioMetadataNode.insertBefore(iioMetadataNode3, iioMetadataNode2);
            if (jfifMarkerSegment.resUnits != 0) {
                final float n2 = (jfifMarkerSegment.resUnits == 1) ? 25.4f : 10.0f;
                final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("HorizontalPixelSize");
                iioMetadataNode4.setAttribute("value", Float.toString(n2 / jfifMarkerSegment.Xdensity));
                iioMetadataNode.appendChild(iioMetadataNode4);
                final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("VerticalPixelSize");
                iioMetadataNode5.setAttribute("value", Float.toString(n2 / jfifMarkerSegment.Ydensity));
                iioMetadataNode.appendChild(iioMetadataNode5);
            }
        }
        return iioMetadataNode;
    }
    
    @Override
    protected IIOMetadataNode getStandardTextNode() {
        IIOMetadataNode iioMetadataNode = null;
        if (this.findMarkerSegment(254) != null) {
            iioMetadataNode = new IIOMetadataNode("Text");
            for (final MarkerSegment markerSegment : this.markerSequence) {
                if (markerSegment.tag == 254) {
                    final COMMarkerSegment comMarkerSegment = (COMMarkerSegment)markerSegment;
                    final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("TextEntry");
                    iioMetadataNode2.setAttribute("keyword", "comment");
                    iioMetadataNode2.setAttribute("value", comMarkerSegment.getComment());
                    iioMetadataNode.appendChild(iioMetadataNode2);
                }
            }
        }
        return iioMetadataNode;
    }
    
    @Override
    protected IIOMetadataNode getStandardTransparencyNode() {
        IIOMetadataNode iioMetadataNode = null;
        if (this.hasAlpha) {
            iioMetadataNode = new IIOMetadataNode("Transparency");
            final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("Alpha");
            iioMetadataNode2.setAttribute("value", "nonpremultiplied");
            iioMetadataNode.appendChild(iioMetadataNode2);
        }
        return iioMetadataNode;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public void mergeTree(final String s, final Node node) throws IIOInvalidTreeException {
        if (s == null) {
            throw new IllegalArgumentException("null formatName!");
        }
        if (node == null) {
            throw new IllegalArgumentException("null root!");
        }
        List markerSequence;
        if (this.resetSequence == null) {
            this.resetSequence = this.cloneSequence();
            markerSequence = this.resetSequence;
        }
        else {
            markerSequence = this.cloneSequence();
        }
        if (this.isStream && s.equals("javax_imageio_jpeg_stream_1.0")) {
            this.mergeNativeTree(node);
        }
        else if (!this.isStream && s.equals("javax_imageio_jpeg_image_1.0")) {
            this.mergeNativeTree(node);
        }
        else {
            if (this.isStream || !s.equals("javax_imageio_1.0")) {
                throw new IllegalArgumentException("Unsupported format name: " + s);
            }
            this.mergeStandardTree(node);
        }
        if (!this.isConsistent()) {
            this.markerSequence = markerSequence;
            throw new IIOInvalidTreeException("Merged tree is invalid; original restored", node);
        }
    }
    
    private void mergeNativeTree(final Node node) throws IIOInvalidTreeException {
        final String nodeName = node.getNodeName();
        if (nodeName != (this.isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0")) {
            throw new IIOInvalidTreeException("Invalid root node name: " + nodeName, node);
        }
        if (node.getChildNodes().getLength() != 2) {
            throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", node);
        }
        this.mergeJFIFsubtree(node.getFirstChild());
        this.mergeSequenceSubtree(node.getLastChild());
    }
    
    private void mergeJFIFsubtree(final Node node) throws IIOInvalidTreeException {
        if (node.getChildNodes().getLength() != 0) {
            final Node firstChild = node.getFirstChild();
            final JFIFMarkerSegment jfifMarkerSegment = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
            if (jfifMarkerSegment != null) {
                jfifMarkerSegment.updateFromNativeNode(firstChild, false);
            }
            else {
                this.markerSequence.add(0, new JFIFMarkerSegment(firstChild));
            }
        }
    }
    
    private void mergeSequenceSubtree(final Node node) throws IIOInvalidTreeException {
        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item = childNodes.item(i);
            final String nodeName = item.getNodeName();
            if (nodeName.equals("dqt")) {
                this.mergeDQTNode(item);
            }
            else if (nodeName.equals("dht")) {
                this.mergeDHTNode(item);
            }
            else if (nodeName.equals("dri")) {
                this.mergeDRINode(item);
            }
            else if (nodeName.equals("com")) {
                this.mergeCOMNode(item);
            }
            else if (nodeName.equals("app14Adobe")) {
                this.mergeAdobeNode(item);
            }
            else if (nodeName.equals("unknown")) {
                this.mergeUnknownNode(item);
            }
            else if (nodeName.equals("sof")) {
                this.mergeSOFNode(item);
            }
            else {
                if (!nodeName.equals("sos")) {
                    throw new IIOInvalidTreeException("Invalid node: " + nodeName, item);
                }
                this.mergeSOSNode(item);
            }
        }
    }
    
    private void mergeDQTNode(final Node node) throws IIOInvalidTreeException {
        final ArrayList list = new ArrayList();
        for (final MarkerSegment markerSegment : this.markerSequence) {
            if (markerSegment instanceof DQTMarkerSegment) {
                list.add(markerSegment);
            }
        }
        if (!list.isEmpty()) {
            final NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                final Node item = childNodes.item(i);
                final int attributeValue = MarkerSegment.getAttributeValue(item, null, "qtableId", 0, 3, true);
                DQTMarkerSegment dqtMarkerSegment = null;
                int n = -1;
                for (int j = 0; j < list.size(); ++j) {
                    final DQTMarkerSegment dqtMarkerSegment2 = list.get(j);
                    for (int k = 0; k < dqtMarkerSegment2.tables.size(); ++k) {
                        if (attributeValue == ((DQTMarkerSegment.Qtable)dqtMarkerSegment2.tables.get(k)).tableID) {
                            dqtMarkerSegment = dqtMarkerSegment2;
                            n = k;
                            break;
                        }
                    }
                    if (dqtMarkerSegment != null) {
                        break;
                    }
                }
                if (dqtMarkerSegment != null) {
                    dqtMarkerSegment.tables.set(n, dqtMarkerSegment.getQtableFromNode(item));
                }
                else {
                    final DQTMarkerSegment dqtMarkerSegment3 = list.get(list.size() - 1);
                    dqtMarkerSegment3.tables.add(dqtMarkerSegment3.getQtableFromNode(item));
                }
            }
        }
        else {
            final DQTMarkerSegment dqtMarkerSegment4 = new DQTMarkerSegment(node);
            final int markerSegmentPosition = this.findMarkerSegmentPosition(DHTMarkerSegment.class, true);
            final int markerSegmentPosition2 = this.findMarkerSegmentPosition(SOFMarkerSegment.class, true);
            final int markerSegmentPosition3 = this.findMarkerSegmentPosition(SOSMarkerSegment.class, true);
            if (markerSegmentPosition != -1) {
                this.markerSequence.add(markerSegmentPosition, dqtMarkerSegment4);
            }
            else if (markerSegmentPosition2 != -1) {
                this.markerSequence.add(markerSegmentPosition2, dqtMarkerSegment4);
            }
            else if (markerSegmentPosition3 != -1) {
                this.markerSequence.add(markerSegmentPosition3, dqtMarkerSegment4);
            }
            else {
                this.markerSequence.add(dqtMarkerSegment4);
            }
        }
    }
    
    private void mergeDHTNode(final Node node) throws IIOInvalidTreeException {
        final ArrayList list = new ArrayList();
        for (final MarkerSegment markerSegment : this.markerSequence) {
            if (markerSegment instanceof DHTMarkerSegment) {
                list.add(markerSegment);
            }
        }
        if (!list.isEmpty()) {
            final NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                final Node item = childNodes.item(i);
                final NamedNodeMap attributes = item.getAttributes();
                final int attributeValue = MarkerSegment.getAttributeValue(item, attributes, "htableId", 0, 3, true);
                final int attributeValue2 = MarkerSegment.getAttributeValue(item, attributes, "class", 0, 1, true);
                DHTMarkerSegment dhtMarkerSegment = null;
                int n = -1;
                for (int j = 0; j < list.size(); ++j) {
                    final DHTMarkerSegment dhtMarkerSegment2 = list.get(j);
                    for (int k = 0; k < dhtMarkerSegment2.tables.size(); ++k) {
                        final DHTMarkerSegment.Htable htable = dhtMarkerSegment2.tables.get(k);
                        if (attributeValue == htable.tableID && attributeValue2 == htable.tableClass) {
                            dhtMarkerSegment = dhtMarkerSegment2;
                            n = k;
                            break;
                        }
                    }
                    if (dhtMarkerSegment != null) {
                        break;
                    }
                }
                if (dhtMarkerSegment != null) {
                    dhtMarkerSegment.tables.set(n, dhtMarkerSegment.getHtableFromNode(item));
                }
                else {
                    final DHTMarkerSegment dhtMarkerSegment3 = list.get(list.size() - 1);
                    dhtMarkerSegment3.tables.add(dhtMarkerSegment3.getHtableFromNode(item));
                }
            }
        }
        else {
            final DHTMarkerSegment dhtMarkerSegment4 = new DHTMarkerSegment(node);
            final int markerSegmentPosition = this.findMarkerSegmentPosition(DQTMarkerSegment.class, false);
            final int markerSegmentPosition2 = this.findMarkerSegmentPosition(SOFMarkerSegment.class, true);
            final int markerSegmentPosition3 = this.findMarkerSegmentPosition(SOSMarkerSegment.class, true);
            if (markerSegmentPosition != -1) {
                this.markerSequence.add(markerSegmentPosition + 1, dhtMarkerSegment4);
            }
            else if (markerSegmentPosition2 != -1) {
                this.markerSequence.add(markerSegmentPosition2, dhtMarkerSegment4);
            }
            else if (markerSegmentPosition3 != -1) {
                this.markerSequence.add(markerSegmentPosition3, dhtMarkerSegment4);
            }
            else {
                this.markerSequence.add(dhtMarkerSegment4);
            }
        }
    }
    
    private void mergeDRINode(final Node node) throws IIOInvalidTreeException {
        final DRIMarkerSegment driMarkerSegment = (DRIMarkerSegment)this.findMarkerSegment(DRIMarkerSegment.class, true);
        if (driMarkerSegment != null) {
            driMarkerSegment.updateFromNativeNode(node, false);
        }
        else {
            final DRIMarkerSegment driMarkerSegment2 = new DRIMarkerSegment(node);
            final int markerSegmentPosition = this.findMarkerSegmentPosition(SOFMarkerSegment.class, true);
            final int markerSegmentPosition2 = this.findMarkerSegmentPosition(SOSMarkerSegment.class, true);
            if (markerSegmentPosition != -1) {
                this.markerSequence.add(markerSegmentPosition, driMarkerSegment2);
            }
            else if (markerSegmentPosition2 != -1) {
                this.markerSequence.add(markerSegmentPosition2, driMarkerSegment2);
            }
            else {
                this.markerSequence.add(driMarkerSegment2);
            }
        }
    }
    
    private void mergeCOMNode(final Node node) throws IIOInvalidTreeException {
        this.insertCOMMarkerSegment(new COMMarkerSegment(node));
    }
    
    private void insertCOMMarkerSegment(final COMMarkerSegment comMarkerSegment) {
        final int markerSegmentPosition = this.findMarkerSegmentPosition(COMMarkerSegment.class, false);
        final boolean b = this.findMarkerSegment(JFIFMarkerSegment.class, true) != null;
        final int markerSegmentPosition2 = this.findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
        if (markerSegmentPosition != -1) {
            this.markerSequence.add(markerSegmentPosition + 1, comMarkerSegment);
        }
        else if (b) {
            this.markerSequence.add(1, comMarkerSegment);
        }
        else if (markerSegmentPosition2 != -1) {
            this.markerSequence.add(markerSegmentPosition2 + 1, comMarkerSegment);
        }
        else {
            this.markerSequence.add(0, comMarkerSegment);
        }
    }
    
    private void mergeAdobeNode(final Node node) throws IIOInvalidTreeException {
        final AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
        if (adobeMarkerSegment != null) {
            adobeMarkerSegment.updateFromNativeNode(node, false);
        }
        else {
            this.insertAdobeMarkerSegment(new AdobeMarkerSegment(node));
        }
    }
    
    private void insertAdobeMarkerSegment(final AdobeMarkerSegment adobeMarkerSegment) {
        final boolean b = this.findMarkerSegment(JFIFMarkerSegment.class, true) != null;
        final int lastUnknownMarkerSegmentPosition = this.findLastUnknownMarkerSegmentPosition();
        if (b) {
            this.markerSequence.add(1, adobeMarkerSegment);
        }
        else if (lastUnknownMarkerSegmentPosition != -1) {
            this.markerSequence.add(lastUnknownMarkerSegmentPosition + 1, adobeMarkerSegment);
        }
        else {
            this.markerSequence.add(0, adobeMarkerSegment);
        }
    }
    
    private void mergeUnknownNode(final Node node) throws IIOInvalidTreeException {
        final MarkerSegment markerSegment = new MarkerSegment(node);
        final int lastUnknownMarkerSegmentPosition = this.findLastUnknownMarkerSegmentPosition();
        final boolean b = this.findMarkerSegment(JFIFMarkerSegment.class, true) != null;
        final int markerSegmentPosition = this.findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
        if (lastUnknownMarkerSegmentPosition != -1) {
            this.markerSequence.add(lastUnknownMarkerSegmentPosition + 1, markerSegment);
        }
        else if (b) {
            this.markerSequence.add(1, markerSegment);
        }
        if (markerSegmentPosition != -1) {
            this.markerSequence.add(markerSegmentPosition, markerSegment);
        }
        else {
            this.markerSequence.add(0, markerSegment);
        }
    }
    
    private void mergeSOFNode(final Node node) throws IIOInvalidTreeException {
        final SOFMarkerSegment sofMarkerSegment = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
        if (sofMarkerSegment != null) {
            sofMarkerSegment.updateFromNativeNode(node, false);
        }
        else {
            final SOFMarkerSegment sofMarkerSegment2 = new SOFMarkerSegment(node);
            final int markerSegmentPosition = this.findMarkerSegmentPosition(SOSMarkerSegment.class, true);
            if (markerSegmentPosition != -1) {
                this.markerSequence.add(markerSegmentPosition, sofMarkerSegment2);
            }
            else {
                this.markerSequence.add(sofMarkerSegment2);
            }
        }
    }
    
    private void mergeSOSNode(final Node node) throws IIOInvalidTreeException {
        final SOSMarkerSegment sosMarkerSegment = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, true);
        final SOSMarkerSegment sosMarkerSegment2 = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, false);
        if (sosMarkerSegment != null) {
            if (sosMarkerSegment != sosMarkerSegment2) {
                throw new IIOInvalidTreeException("Can't merge SOS node into a tree with > 1 SOS node", node);
            }
            sosMarkerSegment.updateFromNativeNode(node, false);
        }
        else {
            this.markerSequence.add(new SOSMarkerSegment(node));
        }
    }
    
    private void mergeStandardTree(final Node node) throws IIOInvalidTreeException {
        this.transparencyDone = false;
        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item = childNodes.item(i);
            final String nodeName = item.getNodeName();
            if (nodeName.equals("Chroma")) {
                this.mergeStandardChromaNode(item, childNodes);
            }
            else if (nodeName.equals("Compression")) {
                this.mergeStandardCompressionNode(item);
            }
            else if (nodeName.equals("Data")) {
                this.mergeStandardDataNode(item);
            }
            else if (nodeName.equals("Dimension")) {
                this.mergeStandardDimensionNode(item);
            }
            else if (nodeName.equals("Document")) {
                this.mergeStandardDocumentNode(item);
            }
            else if (nodeName.equals("Text")) {
                this.mergeStandardTextNode(item);
            }
            else {
                if (!nodeName.equals("Transparency")) {
                    throw new IIOInvalidTreeException("Invalid node: " + nodeName, item);
                }
                this.mergeStandardTransparencyNode(item);
            }
        }
    }
    
    private void mergeStandardChromaNode(final Node node, final NodeList list) throws IIOInvalidTreeException {
        if (this.transparencyDone) {
            throw new IIOInvalidTreeException("Transparency node must follow Chroma node", node);
        }
        final Node firstChild = node.getFirstChild();
        if (firstChild == null || !firstChild.getNodeName().equals("ColorSpaceType")) {
            return;
        }
        final String nodeValue = firstChild.getAttributes().getNamedItem("name").getNodeValue();
        boolean b = false;
        boolean b2 = false;
        int transform = 0;
        boolean b3 = false;
        final byte[] array = { 1, 2, 3, 4 };
        int n;
        if (nodeValue.equals("GRAY")) {
            n = 1;
            b = true;
        }
        else if (nodeValue.equals("YCbCr")) {
            n = 3;
            b = true;
            b3 = true;
        }
        else if (nodeValue.equals("PhotoYCC")) {
            n = 3;
            b2 = true;
            transform = 1;
            array[0] = 89;
            array[1] = 67;
            array[2] = 99;
        }
        else if (nodeValue.equals("RGB")) {
            n = 3;
            b2 = true;
            transform = 0;
            array[0] = 82;
            array[1] = 71;
            array[2] = 66;
        }
        else if (nodeValue.equals("XYZ") || nodeValue.equals("Lab") || nodeValue.equals("Luv") || nodeValue.equals("YxY") || nodeValue.equals("HSV") || nodeValue.equals("HLS") || nodeValue.equals("CMY") || nodeValue.equals("3CLR")) {
            n = 3;
        }
        else if (nodeValue.equals("YCCK")) {
            n = 4;
            b2 = true;
            transform = 2;
            b3 = true;
        }
        else if (nodeValue.equals("CMYK")) {
            n = 4;
            b2 = true;
            transform = 0;
        }
        else {
            if (!nodeValue.equals("4CLR")) {
                return;
            }
            n = 4;
        }
        boolean wantAlpha = false;
        for (int i = 0; i < list.getLength(); ++i) {
            final Node item = list.item(i);
            if (item.getNodeName().equals("Transparency")) {
                wantAlpha = this.wantAlpha(item);
                break;
            }
        }
        if (wantAlpha) {
            ++n;
            b = false;
            if (array[0] == 82) {
                array[3] = 65;
                b2 = false;
            }
        }
        final JFIFMarkerSegment jfifMarkerSegment = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
        final AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
        final SOFMarkerSegment sofMarkerSegment = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
        final SOSMarkerSegment sosMarkerSegment = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, true);
        if (sofMarkerSegment != null && sofMarkerSegment.tag == 194 && sofMarkerSegment.componentSpecs.length != n && sosMarkerSegment != null) {
            return;
        }
        if (!b && jfifMarkerSegment != null) {
            this.markerSequence.remove(jfifMarkerSegment);
        }
        if (b && !this.isStream) {
            this.markerSequence.add(0, new JFIFMarkerSegment());
        }
        if (b2) {
            if (adobeMarkerSegment == null && !this.isStream) {
                this.insertAdobeMarkerSegment(new AdobeMarkerSegment(transform));
            }
            else {
                adobeMarkerSegment.transform = transform;
            }
        }
        else if (adobeMarkerSegment != null) {
            this.markerSequence.remove(adobeMarkerSegment);
        }
        boolean b4 = false;
        boolean b5 = false;
        final int[] array2 = { 0, 1, 1, 0 };
        final int[] array3 = { 0, 0, 0, 0 };
        final int[] array4 = b3 ? array2 : array3;
        if (sofMarkerSegment != null) {
            final SOFMarkerSegment.ComponentSpec[] componentSpecs = sofMarkerSegment.componentSpecs;
            final boolean b6 = sofMarkerSegment.tag == 194;
            this.markerSequence.set(this.markerSequence.indexOf(sofMarkerSegment), new SOFMarkerSegment(b6, false, b3, array, n));
            for (int j = 0; j < componentSpecs.length; ++j) {
                if (componentSpecs[j].QtableSelector != array4[j]) {
                    b4 = true;
                }
            }
            if (b6) {
                boolean b7 = false;
                for (int k = 0; k < componentSpecs.length; ++k) {
                    if (array[k] != componentSpecs[k].componentId) {
                        b7 = true;
                    }
                }
                if (b7) {
                    for (final MarkerSegment markerSegment : this.markerSequence) {
                        if (markerSegment instanceof SOSMarkerSegment) {
                            final SOSMarkerSegment sosMarkerSegment2 = (SOSMarkerSegment)markerSegment;
                            for (int l = 0; l < sosMarkerSegment2.componentSpecs.length; ++l) {
                                final int componentSelector = sosMarkerSegment2.componentSpecs[l].componentSelector;
                                for (int n2 = 0; n2 < componentSpecs.length; ++n2) {
                                    if (componentSpecs[n2].componentId == componentSelector) {
                                        sosMarkerSegment2.componentSpecs[l].componentSelector = array[n2];
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (sosMarkerSegment != null) {
                for (int n3 = 0; n3 < sosMarkerSegment.componentSpecs.length; ++n3) {
                    if (sosMarkerSegment.componentSpecs[n3].dcHuffTable != array4[n3] || sosMarkerSegment.componentSpecs[n3].acHuffTable != array4[n3]) {
                        b5 = true;
                    }
                }
                this.markerSequence.set(this.markerSequence.indexOf(sosMarkerSegment), new SOSMarkerSegment(b3, array, n));
            }
        }
        else if (this.isStream) {
            b4 = true;
            b5 = true;
        }
        if (b4) {
            final ArrayList list2 = new ArrayList();
            for (final MarkerSegment markerSegment2 : this.markerSequence) {
                if (markerSegment2 instanceof DQTMarkerSegment) {
                    list2.add(markerSegment2);
                }
            }
            if (!list2.isEmpty() && b3) {
                boolean b8 = false;
                final Iterator iterator3 = list2.iterator();
                while (iterator3.hasNext()) {
                    final Iterator iterator4 = ((DQTMarkerSegment)iterator3.next()).tables.iterator();
                    while (iterator4.hasNext()) {
                        if (((DQTMarkerSegment.Qtable)iterator4.next()).tableID == 1) {
                            b8 = true;
                        }
                    }
                }
                if (!b8) {
                    DQTMarkerSegment.Qtable qtable = null;
                    final Iterator iterator5 = list2.iterator();
                    while (iterator5.hasNext()) {
                        for (final DQTMarkerSegment.Qtable qtable2 : ((DQTMarkerSegment)iterator5.next()).tables) {
                            if (qtable2.tableID == 0) {
                                qtable = qtable2;
                            }
                        }
                    }
                    final DQTMarkerSegment dqtMarkerSegment = (DQTMarkerSegment)list2.get(list2.size() - 1);
                    dqtMarkerSegment.tables.add(dqtMarkerSegment.getChromaForLuma(qtable));
                }
            }
        }
        if (b5) {
            final ArrayList list3 = new ArrayList();
            for (final MarkerSegment markerSegment3 : this.markerSequence) {
                if (markerSegment3 instanceof DHTMarkerSegment) {
                    list3.add(markerSegment3);
                }
            }
            if (!list3.isEmpty() && b3) {
                boolean b9 = false;
                final Iterator iterator8 = list3.iterator();
                while (iterator8.hasNext()) {
                    final Iterator iterator9 = ((DHTMarkerSegment)iterator8.next()).tables.iterator();
                    while (iterator9.hasNext()) {
                        if (((DHTMarkerSegment.Htable)iterator9.next()).tableID == 1) {
                            b9 = true;
                        }
                    }
                }
                if (!b9) {
                    final DHTMarkerSegment dhtMarkerSegment = (DHTMarkerSegment)list3.get(list3.size() - 1);
                    dhtMarkerSegment.addHtable(JPEGHuffmanTable.StdDCLuminance, true, 1);
                    dhtMarkerSegment.addHtable(JPEGHuffmanTable.StdACLuminance, true, 1);
                }
            }
        }
    }
    
    private boolean wantAlpha(final Node node) {
        boolean b = false;
        final Node firstChild = node.getFirstChild();
        if (firstChild.getNodeName().equals("Alpha") && firstChild.hasAttributes() && !firstChild.getAttributes().getNamedItem("value").getNodeValue().equals("none")) {
            b = true;
        }
        this.transparencyDone = true;
        return b;
    }
    
    private void mergeStandardCompressionNode(final Node node) throws IIOInvalidTreeException {
    }
    
    private void mergeStandardDataNode(final Node node) throws IIOInvalidTreeException {
    }
    
    private void mergeStandardDimensionNode(final Node node) throws IIOInvalidTreeException {
        MarkerSegment markerSegment = this.findMarkerSegment(JFIFMarkerSegment.class, true);
        if (markerSegment == null) {
            boolean b = false;
            final SOFMarkerSegment sofMarkerSegment = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
            if (sofMarkerSegment != null) {
                final int length = sofMarkerSegment.componentSpecs.length;
                if (length == 1 || length == 3) {
                    b = true;
                    for (int i = 0; i < sofMarkerSegment.componentSpecs.length; ++i) {
                        if (sofMarkerSegment.componentSpecs[i].componentId != i + 1) {
                            b = false;
                        }
                    }
                    final AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
                    if (adobeMarkerSegment != null && adobeMarkerSegment.transform != ((length != 1) ? 1 : 0)) {
                        b = false;
                    }
                }
            }
            if (b) {
                markerSegment = new JFIFMarkerSegment();
                this.markerSequence.add(0, markerSegment);
            }
        }
        if (markerSegment != null) {
            final NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); ++j) {
                final Node item = childNodes.item(j);
                final NamedNodeMap attributes = item.getAttributes();
                final String nodeName = item.getNodeName();
                if (nodeName.equals("PixelAspectRatio")) {
                    final Point integerRatio = findIntegerRatio(Float.parseFloat(attributes.getNamedItem("value").getNodeValue()));
                    ((JFIFMarkerSegment)markerSegment).resUnits = 0;
                    ((JFIFMarkerSegment)markerSegment).Xdensity = integerRatio.x;
                    ((JFIFMarkerSegment)markerSegment).Xdensity = integerRatio.y;
                }
                else if (nodeName.equals("HorizontalPixelSize")) {
                    final int xdensity = (int)Math.round(1.0 / (Float.parseFloat(attributes.getNamedItem("value").getNodeValue()) * 10.0));
                    ((JFIFMarkerSegment)markerSegment).resUnits = 2;
                    ((JFIFMarkerSegment)markerSegment).Xdensity = xdensity;
                }
                else if (nodeName.equals("VerticalPixelSize")) {
                    final int ydensity = (int)Math.round(1.0 / (Float.parseFloat(attributes.getNamedItem("value").getNodeValue()) * 10.0));
                    ((JFIFMarkerSegment)markerSegment).resUnits = 2;
                    ((JFIFMarkerSegment)markerSegment).Ydensity = ydensity;
                }
            }
        }
    }
    
    private static Point findIntegerRatio(float abs) {
        final float n = 0.005f;
        abs = Math.abs(abs);
        if (abs <= n) {
            return new Point(1, 255);
        }
        if (abs >= 255.0f) {
            return new Point(255, 1);
        }
        boolean b = false;
        if (abs < 1.0) {
            abs = 1.0f / abs;
            b = true;
        }
        int n2 = 1;
        int n3 = Math.round(abs);
        for (float n4 = Math.abs(abs - n3); n4 > n; n4 = Math.abs(abs - n3 / (float)n2)) {
            n3 = Math.round(++n2 * abs);
        }
        return b ? new Point(n2, n3) : new Point(n3, n2);
    }
    
    private void mergeStandardDocumentNode(final Node node) throws IIOInvalidTreeException {
    }
    
    private void mergeStandardTextNode(final Node node) throws IIOInvalidTreeException {
        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final NamedNodeMap attributes = childNodes.item(i).getAttributes();
            final Node namedItem = attributes.getNamedItem("compression");
            boolean b = true;
            if (namedItem != null && !namedItem.getNodeValue().equals("none")) {
                b = false;
            }
            if (b) {
                this.insertCOMMarkerSegment(new COMMarkerSegment(attributes.getNamedItem("value").getNodeValue()));
            }
        }
    }
    
    private void mergeStandardTransparencyNode(final Node node) throws IIOInvalidTreeException {
        if (!this.transparencyDone && !this.isStream) {
            final boolean wantAlpha = this.wantAlpha(node);
            final JFIFMarkerSegment jfifMarkerSegment = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
            final AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
            final SOFMarkerSegment sofMarkerSegment = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
            final SOSMarkerSegment sosMarkerSegment = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, true);
            if (sofMarkerSegment != null && sofMarkerSegment.tag == 194) {
                return;
            }
            if (sofMarkerSegment != null) {
                int length = sofMarkerSegment.componentSpecs.length;
                if ((length == 2 || length == 4) != wantAlpha) {
                    if (wantAlpha) {
                        ++length;
                        if (jfifMarkerSegment != null) {
                            this.markerSequence.remove(jfifMarkerSegment);
                        }
                        if (adobeMarkerSegment != null) {
                            adobeMarkerSegment.transform = 0;
                        }
                        final SOFMarkerSegment.ComponentSpec[] componentSpecs = new SOFMarkerSegment.ComponentSpec[length];
                        for (int i = 0; i < sofMarkerSegment.componentSpecs.length; ++i) {
                            componentSpecs[i] = sofMarkerSegment.componentSpecs[i];
                        }
                        final byte b = (byte)(((byte)sofMarkerSegment.componentSpecs[0].componentId > 1) ? 65 : 4);
                        componentSpecs[length - 1] = sofMarkerSegment.getComponentSpec(b, sofMarkerSegment.componentSpecs[0].HsamplingFactor, sofMarkerSegment.componentSpecs[0].QtableSelector);
                        sofMarkerSegment.componentSpecs = componentSpecs;
                        final SOSMarkerSegment.ScanComponentSpec[] componentSpecs2 = new SOSMarkerSegment.ScanComponentSpec[length];
                        for (int j = 0; j < sosMarkerSegment.componentSpecs.length; ++j) {
                            componentSpecs2[j] = sosMarkerSegment.componentSpecs[j];
                        }
                        componentSpecs2[length - 1] = sosMarkerSegment.getScanComponentSpec(b, 0);
                        sosMarkerSegment.componentSpecs = componentSpecs2;
                    }
                    else {
                        final SOFMarkerSegment.ComponentSpec[] componentSpecs3 = new SOFMarkerSegment.ComponentSpec[--length];
                        for (int k = 0; k < length; ++k) {
                            componentSpecs3[k] = sofMarkerSegment.componentSpecs[k];
                        }
                        sofMarkerSegment.componentSpecs = componentSpecs3;
                        final SOSMarkerSegment.ScanComponentSpec[] componentSpecs4 = new SOSMarkerSegment.ScanComponentSpec[length];
                        for (int l = 0; l < length; ++l) {
                            componentSpecs4[l] = sosMarkerSegment.componentSpecs[l];
                        }
                        sosMarkerSegment.componentSpecs = componentSpecs4;
                    }
                }
            }
        }
    }
    
    @Override
    public void setFromTree(final String s, final Node node) throws IIOInvalidTreeException {
        if (s == null) {
            throw new IllegalArgumentException("null formatName!");
        }
        if (node == null) {
            throw new IllegalArgumentException("null root!");
        }
        if (this.isStream && s.equals("javax_imageio_jpeg_stream_1.0")) {
            this.setFromNativeTree(node);
        }
        else if (!this.isStream && s.equals("javax_imageio_jpeg_image_1.0")) {
            this.setFromNativeTree(node);
        }
        else {
            if (this.isStream || !s.equals("javax_imageio_1.0")) {
                throw new IllegalArgumentException("Unsupported format name: " + s);
            }
            super.setFromTree(s, node);
        }
    }
    
    private void setFromNativeTree(final Node node) throws IIOInvalidTreeException {
        if (this.resetSequence == null) {
            this.resetSequence = this.markerSequence;
        }
        this.markerSequence = new ArrayList();
        final String nodeName = node.getNodeName();
        if (nodeName != (this.isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0")) {
            throw new IIOInvalidTreeException("Invalid root node name: " + nodeName, node);
        }
        if (!this.isStream) {
            if (node.getChildNodes().getLength() != 2) {
                throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", node);
            }
            final Node firstChild = node.getFirstChild();
            if (firstChild.getChildNodes().getLength() != 0) {
                this.markerSequence.add(new JFIFMarkerSegment(firstChild.getFirstChild()));
            }
        }
        this.setFromMarkerSequenceNode(this.isStream ? node : node.getLastChild());
    }
    
    void setFromMarkerSequenceNode(final Node node) throws IIOInvalidTreeException {
        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item = childNodes.item(i);
            final String nodeName = item.getNodeName();
            if (nodeName.equals("dqt")) {
                this.markerSequence.add(new DQTMarkerSegment(item));
            }
            else if (nodeName.equals("dht")) {
                this.markerSequence.add(new DHTMarkerSegment(item));
            }
            else if (nodeName.equals("dri")) {
                this.markerSequence.add(new DRIMarkerSegment(item));
            }
            else if (nodeName.equals("com")) {
                this.markerSequence.add(new COMMarkerSegment(item));
            }
            else if (nodeName.equals("app14Adobe")) {
                this.markerSequence.add(new AdobeMarkerSegment(item));
            }
            else if (nodeName.equals("unknown")) {
                this.markerSequence.add(new MarkerSegment(item));
            }
            else if (nodeName.equals("sof")) {
                this.markerSequence.add(new SOFMarkerSegment(item));
            }
            else {
                if (!nodeName.equals("sos")) {
                    throw new IIOInvalidTreeException("Invalid " + (this.isStream ? "stream " : "image ") + "child: " + nodeName, item);
                }
                this.markerSequence.add(new SOSMarkerSegment(item));
            }
        }
    }
    
    private boolean isConsistent() {
        final SOFMarkerSegment sofMarkerSegment = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
        final JFIFMarkerSegment jfifMarkerSegment = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
        final AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
        boolean b = true;
        if (!this.isStream) {
            if (sofMarkerSegment != null) {
                final int length = sofMarkerSegment.componentSpecs.length;
                final int countScanBands = this.countScanBands();
                if (countScanBands != 0 && countScanBands != length) {
                    b = false;
                }
                if (jfifMarkerSegment != null) {
                    if (length != 1 && length != 3) {
                        b = false;
                    }
                    for (int i = 0; i < length; ++i) {
                        if (sofMarkerSegment.componentSpecs[i].componentId != i + 1) {
                            b = false;
                        }
                    }
                    if (adobeMarkerSegment != null && ((length == 1 && adobeMarkerSegment.transform != 0) || (length == 3 && adobeMarkerSegment.transform != 1))) {
                        b = false;
                    }
                }
            }
            else {
                final SOSMarkerSegment sosMarkerSegment = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, true);
                if (jfifMarkerSegment != null || adobeMarkerSegment != null || sofMarkerSegment != null || sosMarkerSegment != null) {
                    b = false;
                }
            }
        }
        return b;
    }
    
    private int countScanBands() {
        final ArrayList list = new ArrayList();
        for (final MarkerSegment markerSegment : this.markerSequence) {
            if (markerSegment instanceof SOSMarkerSegment) {
                final SOSMarkerSegment.ScanComponentSpec[] componentSpecs = ((SOSMarkerSegment)markerSegment).componentSpecs;
                for (int i = 0; i < componentSpecs.length; ++i) {
                    final Integer n = new Integer(componentSpecs[i].componentSelector);
                    if (!list.contains(n)) {
                        list.add(n);
                    }
                }
            }
        }
        return list.size();
    }
    
    void writeToStream(final ImageOutputStream imageOutputStream, final boolean b, final boolean b2, final List list, final ICC_Profile icc_Profile, boolean b3, final int transform, final JPEGImageWriter jpegImageWriter) throws IOException {
        if (b2) {
            JFIFMarkerSegment.writeDefaultJFIF(imageOutputStream, list, icc_Profile, jpegImageWriter);
            if (!b3 && transform != -1 && transform != 0 && transform != 1) {
                b3 = true;
                jpegImageWriter.warningOccurred(13);
            }
        }
        for (final MarkerSegment markerSegment : this.markerSequence) {
            if (markerSegment instanceof JFIFMarkerSegment) {
                if (b) {
                    continue;
                }
                ((JFIFMarkerSegment)markerSegment).writeWithThumbs(imageOutputStream, list, jpegImageWriter);
                if (icc_Profile == null) {
                    continue;
                }
                JFIFMarkerSegment.writeICC(icc_Profile, imageOutputStream);
            }
            else if (markerSegment instanceof AdobeMarkerSegment) {
                if (b3) {
                    continue;
                }
                if (transform != -1) {
                    final AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)markerSegment.clone();
                    adobeMarkerSegment.transform = transform;
                    adobeMarkerSegment.write(imageOutputStream);
                }
                else if (b2) {
                    final AdobeMarkerSegment adobeMarkerSegment2 = (AdobeMarkerSegment)markerSegment;
                    if (adobeMarkerSegment2.transform == 0 || adobeMarkerSegment2.transform == 1) {
                        adobeMarkerSegment2.write(imageOutputStream);
                    }
                    else {
                        jpegImageWriter.warningOccurred(13);
                    }
                }
                else {
                    markerSegment.write(imageOutputStream);
                }
            }
            else {
                markerSegment.write(imageOutputStream);
            }
        }
    }
    
    @Override
    public void reset() {
        if (this.resetSequence != null) {
            this.markerSequence = this.resetSequence;
            this.resetSequence = null;
        }
    }
    
    public void print() {
        for (int i = 0; i < this.markerSequence.size(); ++i) {
            ((MarkerSegment)this.markerSequence.get(i)).print();
        }
    }
}
