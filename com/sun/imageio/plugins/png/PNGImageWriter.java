package com.sun.imageio.plugins.png;

import java.awt.image.SampleModel;
import javax.imageio.IIOImage;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.Rectangle;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import javax.imageio.IIOException;
import java.io.IOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.ImageWriteParam;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageWriter;

public class PNGImageWriter extends ImageWriter
{
    ImageOutputStream stream;
    PNGMetadata metadata;
    int sourceXOffset;
    int sourceYOffset;
    int sourceWidth;
    int sourceHeight;
    int[] sourceBands;
    int periodX;
    int periodY;
    int numBands;
    int bpp;
    RowFilter rowFilter;
    byte[] prevRow;
    byte[] currRow;
    byte[][] filteredRows;
    int[] sampleSize;
    int scalingBitDepth;
    byte[][] scale;
    byte[] scale0;
    byte[][] scaleh;
    byte[][] scalel;
    int totalPixels;
    int pixelsDone;
    private static int[] allowedProgressivePasses;
    
    public PNGImageWriter(final ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
        this.stream = null;
        this.metadata = null;
        this.sourceXOffset = 0;
        this.sourceYOffset = 0;
        this.sourceWidth = 0;
        this.sourceHeight = 0;
        this.sourceBands = null;
        this.periodX = 1;
        this.periodY = 1;
        this.rowFilter = new RowFilter();
        this.prevRow = null;
        this.currRow = null;
        this.filteredRows = null;
        this.sampleSize = null;
        this.scalingBitDepth = -1;
        this.scale = null;
        this.scale0 = null;
        this.scaleh = null;
        this.scalel = null;
    }
    
    @Override
    public void setOutput(final Object output) {
        super.setOutput(output);
        if (output != null) {
            if (!(output instanceof ImageOutputStream)) {
                throw new IllegalArgumentException("output not an ImageOutputStream!");
            }
            this.stream = (ImageOutputStream)output;
        }
        else {
            this.stream = null;
        }
    }
    
    @Override
    public ImageWriteParam getDefaultWriteParam() {
        return new PNGImageWriteParam(this.getLocale());
    }
    
    @Override
    public IIOMetadata getDefaultStreamMetadata(final ImageWriteParam imageWriteParam) {
        return null;
    }
    
    @Override
    public IIOMetadata getDefaultImageMetadata(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        final PNGMetadata pngMetadata = new PNGMetadata();
        pngMetadata.initialize(imageTypeSpecifier, imageTypeSpecifier.getSampleModel().getNumBands());
        return pngMetadata;
    }
    
    @Override
    public IIOMetadata convertStreamMetadata(final IIOMetadata iioMetadata, final ImageWriteParam imageWriteParam) {
        return null;
    }
    
    @Override
    public IIOMetadata convertImageMetadata(final IIOMetadata iioMetadata, final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        if (iioMetadata instanceof PNGMetadata) {
            return (PNGMetadata)((PNGMetadata)iioMetadata).clone();
        }
        return new PNGMetadata(iioMetadata);
    }
    
    private void write_magic() throws IOException {
        this.stream.write(new byte[] { -119, 80, 78, 71, 13, 10, 26, 10 });
    }
    
    private void write_IHDR() throws IOException {
        final ChunkStream chunkStream = new ChunkStream(1229472850, this.stream);
        chunkStream.writeInt(this.metadata.IHDR_width);
        chunkStream.writeInt(this.metadata.IHDR_height);
        chunkStream.writeByte(this.metadata.IHDR_bitDepth);
        chunkStream.writeByte(this.metadata.IHDR_colorType);
        if (this.metadata.IHDR_compressionMethod != 0) {
            throw new IIOException("Only compression method 0 is defined in PNG 1.1");
        }
        chunkStream.writeByte(this.metadata.IHDR_compressionMethod);
        if (this.metadata.IHDR_filterMethod != 0) {
            throw new IIOException("Only filter method 0 is defined in PNG 1.1");
        }
        chunkStream.writeByte(this.metadata.IHDR_filterMethod);
        if (this.metadata.IHDR_interlaceMethod < 0 || this.metadata.IHDR_interlaceMethod > 1) {
            throw new IIOException("Only interlace methods 0 (node) and 1 (adam7) are defined in PNG 1.1");
        }
        chunkStream.writeByte(this.metadata.IHDR_interlaceMethod);
        chunkStream.finish();
    }
    
    private void write_cHRM() throws IOException {
        if (this.metadata.cHRM_present) {
            final ChunkStream chunkStream = new ChunkStream(1665684045, this.stream);
            chunkStream.writeInt(this.metadata.cHRM_whitePointX);
            chunkStream.writeInt(this.metadata.cHRM_whitePointY);
            chunkStream.writeInt(this.metadata.cHRM_redX);
            chunkStream.writeInt(this.metadata.cHRM_redY);
            chunkStream.writeInt(this.metadata.cHRM_greenX);
            chunkStream.writeInt(this.metadata.cHRM_greenY);
            chunkStream.writeInt(this.metadata.cHRM_blueX);
            chunkStream.writeInt(this.metadata.cHRM_blueY);
            chunkStream.finish();
        }
    }
    
    private void write_gAMA() throws IOException {
        if (this.metadata.gAMA_present) {
            final ChunkStream chunkStream = new ChunkStream(1732332865, this.stream);
            chunkStream.writeInt(this.metadata.gAMA_gamma);
            chunkStream.finish();
        }
    }
    
    private void write_iCCP() throws IOException {
        if (this.metadata.iCCP_present) {
            final ChunkStream chunkStream = new ChunkStream(1766015824, this.stream);
            chunkStream.writeBytes(this.metadata.iCCP_profileName);
            chunkStream.writeByte(0);
            chunkStream.writeByte(this.metadata.iCCP_compressionMethod);
            chunkStream.write(this.metadata.iCCP_compressedProfile);
            chunkStream.finish();
        }
    }
    
    private void write_sBIT() throws IOException {
        if (this.metadata.sBIT_present) {
            final ChunkStream chunkStream = new ChunkStream(1933723988, this.stream);
            final int ihdr_colorType = this.metadata.IHDR_colorType;
            if (this.metadata.sBIT_colorType != ihdr_colorType) {
                this.processWarningOccurred(0, "sBIT metadata has wrong color type.\nThe chunk will not be written.");
                return;
            }
            if (ihdr_colorType == 0 || ihdr_colorType == 4) {
                chunkStream.writeByte(this.metadata.sBIT_grayBits);
            }
            else if (ihdr_colorType == 2 || ihdr_colorType == 3 || ihdr_colorType == 6) {
                chunkStream.writeByte(this.metadata.sBIT_redBits);
                chunkStream.writeByte(this.metadata.sBIT_greenBits);
                chunkStream.writeByte(this.metadata.sBIT_blueBits);
            }
            if (ihdr_colorType == 4 || ihdr_colorType == 6) {
                chunkStream.writeByte(this.metadata.sBIT_alphaBits);
            }
            chunkStream.finish();
        }
    }
    
    private void write_sRGB() throws IOException {
        if (this.metadata.sRGB_present) {
            final ChunkStream chunkStream = new ChunkStream(1934772034, this.stream);
            chunkStream.writeByte(this.metadata.sRGB_renderingIntent);
            chunkStream.finish();
        }
    }
    
    private void write_PLTE() throws IOException {
        if (this.metadata.PLTE_present) {
            if (this.metadata.IHDR_colorType == 0 || this.metadata.IHDR_colorType == 4) {
                this.processWarningOccurred(0, "A PLTE chunk may not appear in a gray or gray alpha image.\nThe chunk will not be written");
                return;
            }
            final ChunkStream chunkStream = new ChunkStream(1347179589, this.stream);
            final int length = this.metadata.PLTE_red.length;
            final byte[] array = new byte[length * 3];
            int n = 0;
            for (int i = 0; i < length; ++i) {
                array[n++] = this.metadata.PLTE_red[i];
                array[n++] = this.metadata.PLTE_green[i];
                array[n++] = this.metadata.PLTE_blue[i];
            }
            chunkStream.write(array);
            chunkStream.finish();
        }
    }
    
    private void write_hIST() throws IOException, IIOException {
        if (this.metadata.hIST_present) {
            final ChunkStream chunkStream = new ChunkStream(1749635924, this.stream);
            if (!this.metadata.PLTE_present) {
                throw new IIOException("hIST chunk without PLTE chunk!");
            }
            chunkStream.writeChars(this.metadata.hIST_histogram, 0, this.metadata.hIST_histogram.length);
            chunkStream.finish();
        }
    }
    
    private void write_tRNS() throws IOException, IIOException {
        if (this.metadata.tRNS_present) {
            final ChunkStream chunkStream = new ChunkStream(1951551059, this.stream);
            final int ihdr_colorType = this.metadata.IHDR_colorType;
            int trns_colorType = this.metadata.tRNS_colorType;
            int trns_red = this.metadata.tRNS_red;
            int trns_green = this.metadata.tRNS_green;
            int n = this.metadata.tRNS_blue;
            if (ihdr_colorType == 2 && trns_colorType == 0) {
                trns_colorType = ihdr_colorType;
                trns_green = (trns_red = (n = this.metadata.tRNS_gray));
            }
            if (trns_colorType != ihdr_colorType) {
                this.processWarningOccurred(0, "tRNS metadata has incompatible color type.\nThe chunk will not be written.");
                return;
            }
            if (ihdr_colorType == 3) {
                if (!this.metadata.PLTE_present) {
                    throw new IIOException("tRNS chunk without PLTE chunk!");
                }
                chunkStream.write(this.metadata.tRNS_alpha);
            }
            else if (ihdr_colorType == 0) {
                chunkStream.writeShort(this.metadata.tRNS_gray);
            }
            else {
                if (ihdr_colorType != 2) {
                    throw new IIOException("tRNS chunk for color type 4 or 6!");
                }
                chunkStream.writeShort(trns_red);
                chunkStream.writeShort(trns_green);
                chunkStream.writeShort(n);
            }
            chunkStream.finish();
        }
    }
    
    private void write_bKGD() throws IOException {
        if (this.metadata.bKGD_present) {
            final ChunkStream chunkStream = new ChunkStream(1649100612, this.stream);
            final int n = this.metadata.IHDR_colorType & 0x3;
            int bkgd_colorType = this.metadata.bKGD_colorType;
            int bkgd_red = this.metadata.bKGD_red;
            int bkgd_red2 = this.metadata.bKGD_red;
            int n2 = this.metadata.bKGD_red;
            if (n == 2 && bkgd_colorType == 0) {
                bkgd_colorType = n;
                bkgd_red2 = (bkgd_red = (n2 = this.metadata.bKGD_gray));
            }
            if (bkgd_colorType != n) {
                this.processWarningOccurred(0, "bKGD metadata has incompatible color type.\nThe chunk will not be written.");
                return;
            }
            if (n == 3) {
                chunkStream.writeByte(this.metadata.bKGD_index);
            }
            else if (n == 0 || n == 4) {
                chunkStream.writeShort(this.metadata.bKGD_gray);
            }
            else {
                chunkStream.writeShort(bkgd_red);
                chunkStream.writeShort(bkgd_red2);
                chunkStream.writeShort(n2);
            }
            chunkStream.finish();
        }
    }
    
    private void write_pHYs() throws IOException {
        if (this.metadata.pHYs_present) {
            final ChunkStream chunkStream = new ChunkStream(1883789683, this.stream);
            chunkStream.writeInt(this.metadata.pHYs_pixelsPerUnitXAxis);
            chunkStream.writeInt(this.metadata.pHYs_pixelsPerUnitYAxis);
            chunkStream.writeByte(this.metadata.pHYs_unitSpecifier);
            chunkStream.finish();
        }
    }
    
    private void write_sPLT() throws IOException {
        if (this.metadata.sPLT_present) {
            final ChunkStream chunkStream = new ChunkStream(1934642260, this.stream);
            chunkStream.writeBytes(this.metadata.sPLT_paletteName);
            chunkStream.writeByte(0);
            chunkStream.writeByte(this.metadata.sPLT_sampleDepth);
            final int length = this.metadata.sPLT_red.length;
            if (this.metadata.sPLT_sampleDepth == 8) {
                for (int i = 0; i < length; ++i) {
                    chunkStream.writeByte(this.metadata.sPLT_red[i]);
                    chunkStream.writeByte(this.metadata.sPLT_green[i]);
                    chunkStream.writeByte(this.metadata.sPLT_blue[i]);
                    chunkStream.writeByte(this.metadata.sPLT_alpha[i]);
                    chunkStream.writeShort(this.metadata.sPLT_frequency[i]);
                }
            }
            else {
                for (int j = 0; j < length; ++j) {
                    chunkStream.writeShort(this.metadata.sPLT_red[j]);
                    chunkStream.writeShort(this.metadata.sPLT_green[j]);
                    chunkStream.writeShort(this.metadata.sPLT_blue[j]);
                    chunkStream.writeShort(this.metadata.sPLT_alpha[j]);
                    chunkStream.writeShort(this.metadata.sPLT_frequency[j]);
                }
            }
            chunkStream.finish();
        }
    }
    
    private void write_tIME() throws IOException {
        if (this.metadata.tIME_present) {
            final ChunkStream chunkStream = new ChunkStream(1950960965, this.stream);
            chunkStream.writeShort(this.metadata.tIME_year);
            chunkStream.writeByte(this.metadata.tIME_month);
            chunkStream.writeByte(this.metadata.tIME_day);
            chunkStream.writeByte(this.metadata.tIME_hour);
            chunkStream.writeByte(this.metadata.tIME_minute);
            chunkStream.writeByte(this.metadata.tIME_second);
            chunkStream.finish();
        }
    }
    
    private void write_tEXt() throws IOException {
        final Iterator<String> iterator = this.metadata.tEXt_keyword.iterator();
        final Iterator<String> iterator2 = this.metadata.tEXt_text.iterator();
        while (iterator.hasNext()) {
            final ChunkStream chunkStream = new ChunkStream(1950701684, this.stream);
            chunkStream.writeBytes(iterator.next());
            chunkStream.writeByte(0);
            chunkStream.writeBytes(iterator2.next());
            chunkStream.finish();
        }
    }
    
    private byte[] deflate(final byte[] array) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
        deflaterOutputStream.write(array);
        deflaterOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
    
    private void write_iTXt() throws IOException {
        final Iterator<String> iterator = this.metadata.iTXt_keyword.iterator();
        final Iterator<Boolean> iterator2 = this.metadata.iTXt_compressionFlag.iterator();
        final Iterator<Integer> iterator3 = this.metadata.iTXt_compressionMethod.iterator();
        final Iterator<String> iterator4 = this.metadata.iTXt_languageTag.iterator();
        final Iterator<String> iterator5 = this.metadata.iTXt_translatedKeyword.iterator();
        final Iterator<String> iterator6 = this.metadata.iTXt_text.iterator();
        while (iterator.hasNext()) {
            final ChunkStream chunkStream = new ChunkStream(1767135348, this.stream);
            chunkStream.writeBytes(iterator.next());
            chunkStream.writeByte(0);
            final Boolean b = iterator2.next();
            chunkStream.writeByte(((boolean)b) ? 1 : 0);
            chunkStream.writeByte(iterator3.next());
            chunkStream.writeBytes(iterator4.next());
            chunkStream.writeByte(0);
            chunkStream.write(iterator5.next().getBytes("UTF8"));
            chunkStream.writeByte(0);
            final String s = iterator6.next();
            if (b) {
                chunkStream.write(this.deflate(s.getBytes("UTF8")));
            }
            else {
                chunkStream.write(s.getBytes("UTF8"));
            }
            chunkStream.finish();
        }
    }
    
    private void write_zTXt() throws IOException {
        final Iterator<String> iterator = this.metadata.zTXt_keyword.iterator();
        final Iterator<Integer> iterator2 = this.metadata.zTXt_compressionMethod.iterator();
        final Iterator<String> iterator3 = this.metadata.zTXt_text.iterator();
        while (iterator.hasNext()) {
            final ChunkStream chunkStream = new ChunkStream(2052348020, this.stream);
            chunkStream.writeBytes(iterator.next());
            chunkStream.writeByte(0);
            chunkStream.writeByte(iterator2.next());
            chunkStream.write(this.deflate(iterator3.next().getBytes("ISO-8859-1")));
            chunkStream.finish();
        }
    }
    
    private void writeUnknownChunks() throws IOException {
        final Iterator<String> iterator = this.metadata.unknownChunkType.iterator();
        final Iterator<byte[]> iterator2 = this.metadata.unknownChunkData.iterator();
        while (iterator.hasNext() && iterator2.hasNext()) {
            final ChunkStream chunkStream = new ChunkStream(chunkType(iterator.next()), this.stream);
            chunkStream.write(iterator2.next());
            chunkStream.finish();
        }
    }
    
    private static int chunkType(final String s) {
        return s.charAt(0) << 24 | s.charAt(1) << 16 | s.charAt(2) << 8 | s.charAt(3);
    }
    
    private void encodePass(final ImageOutputStream imageOutputStream, final RenderedImage renderedImage, int n, int n2, int n3, int n4) throws IOException {
        final int sourceXOffset = this.sourceXOffset;
        final int sourceYOffset = this.sourceYOffset;
        final int sourceWidth = this.sourceWidth;
        final int sourceHeight = this.sourceHeight;
        n *= this.periodX;
        n3 *= this.periodX;
        n2 *= this.periodY;
        n4 *= this.periodY;
        final int n5 = (sourceWidth - n + n3 - 1) / n3;
        final int n6 = (sourceHeight - n2 + n4 - 1) / n4;
        if (n5 == 0 || n6 == 0) {
            return;
        }
        n *= this.numBands;
        n3 *= this.numBands;
        final int n7 = 8 / this.metadata.IHDR_bitDepth;
        final int n8 = sourceWidth * this.numBands;
        final int[] array = new int[n8];
        int n9 = n5 * this.numBands;
        if (this.metadata.IHDR_bitDepth < 8) {
            n9 = (n9 + n7 - 1) / n7;
        }
        else if (this.metadata.IHDR_bitDepth == 16) {
            n9 *= 2;
        }
        IndexColorModel indexColorModel = null;
        if (this.metadata.IHDR_colorType == 4 && renderedImage.getColorModel() instanceof IndexColorModel) {
            n9 *= 2;
            indexColorModel = (IndexColorModel)renderedImage.getColorModel();
        }
        this.currRow = new byte[n9 + this.bpp];
        this.prevRow = new byte[n9 + this.bpp];
        this.filteredRows = new byte[5][n9 + this.bpp];
        final int ihdr_bitDepth = this.metadata.IHDR_bitDepth;
        for (int i = sourceYOffset + n2; i < sourceYOffset + sourceHeight; i += n4) {
            Raster raster = renderedImage.getData(new Rectangle(sourceXOffset, i, sourceWidth, 1));
            if (this.sourceBands != null) {
                raster = raster.createChild(sourceXOffset, i, sourceWidth, 1, sourceXOffset, i, this.sourceBands);
            }
            raster.getPixels(sourceXOffset, i, sourceWidth, 1, array);
            if (renderedImage.getColorModel().isAlphaPremultiplied()) {
                final WritableRaster compatibleWritableRaster = raster.createCompatibleWritableRaster();
                compatibleWritableRaster.setPixels(compatibleWritableRaster.getMinX(), compatibleWritableRaster.getMinY(), compatibleWritableRaster.getWidth(), compatibleWritableRaster.getHeight(), array);
                renderedImage.getColorModel().coerceData(compatibleWritableRaster, false);
                compatibleWritableRaster.getPixels(compatibleWritableRaster.getMinX(), compatibleWritableRaster.getMinY(), compatibleWritableRaster.getWidth(), compatibleWritableRaster.getHeight(), array);
            }
            final int[] plte_order = this.metadata.PLTE_order;
            if (plte_order != null) {
                for (int j = 0; j < n8; ++j) {
                    array[j] = plte_order[array[j]];
                }
            }
            int bpp = this.bpp;
            int n10 = 0;
            int n11 = 0;
            switch (ihdr_bitDepth) {
                case 1:
                case 2:
                case 4: {
                    final int n12 = n7 - 1;
                    for (int k = n; k < n8; k += n3) {
                        n11 = (n11 << ihdr_bitDepth | this.scale0[array[k]]);
                        if ((n10++ & n12) == n12) {
                            this.currRow[bpp++] = (byte)n11;
                            n11 = 0;
                            n10 = 0;
                        }
                    }
                    if ((n10 & n12) != 0x0) {
                        this.currRow[bpp++] = (byte)(n11 << (8 / ihdr_bitDepth - n10) * ihdr_bitDepth);
                        break;
                    }
                    break;
                }
                case 8: {
                    if (this.numBands == 1) {
                        for (int l = n; l < n8; l += n3) {
                            this.currRow[bpp++] = this.scale0[array[l]];
                            if (indexColorModel != null) {
                                this.currRow[bpp++] = this.scale0[indexColorModel.getAlpha(0xFF & array[l])];
                            }
                        }
                        break;
                    }
                    for (int n13 = n; n13 < n8; n13 += n3) {
                        for (int n14 = 0; n14 < this.numBands; ++n14) {
                            this.currRow[bpp++] = this.scale[n14][array[n13 + n14]];
                        }
                    }
                    break;
                }
                case 16: {
                    for (int n15 = n; n15 < n8; n15 += n3) {
                        for (int n16 = 0; n16 < this.numBands; ++n16) {
                            this.currRow[bpp++] = this.scaleh[n16][array[n15 + n16]];
                            this.currRow[bpp++] = this.scalel[n16][array[n15 + n16]];
                        }
                    }
                    break;
                }
            }
            final int filterRow = this.rowFilter.filterRow(this.metadata.IHDR_colorType, this.currRow, this.prevRow, this.filteredRows, n9, this.bpp);
            imageOutputStream.write(filterRow);
            imageOutputStream.write(this.filteredRows[filterRow], this.bpp, n9);
            final byte[] currRow = this.currRow;
            this.currRow = this.prevRow;
            this.prevRow = currRow;
            this.pixelsDone += n5;
            this.processImageProgress(100.0f * this.pixelsDone / this.totalPixels);
            if (this.abortRequested()) {
                return;
            }
        }
    }
    
    private void write_IDAT(final RenderedImage renderedImage) throws IOException {
        final IDATOutputStream idatOutputStream = new IDATOutputStream(this.stream, 32768);
        try {
            if (this.metadata.IHDR_interlaceMethod == 1) {
                for (int i = 0; i < 7; ++i) {
                    this.encodePass(idatOutputStream, renderedImage, PNGImageReader.adam7XOffset[i], PNGImageReader.adam7YOffset[i], PNGImageReader.adam7XSubsampling[i], PNGImageReader.adam7YSubsampling[i]);
                    if (this.abortRequested()) {
                        break;
                    }
                }
            }
            else {
                this.encodePass(idatOutputStream, renderedImage, 0, 0, 1, 1);
            }
        }
        finally {
            idatOutputStream.finish();
        }
    }
    
    private void writeIEND() throws IOException {
        new ChunkStream(1229278788, this.stream).finish();
    }
    
    private boolean equals(final int[] array, final int[] array2) {
        if (array == null || array2 == null) {
            return false;
        }
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    private void initializeScaleTables(final int[] sampleSize) {
        final int ihdr_bitDepth = this.metadata.IHDR_bitDepth;
        if (ihdr_bitDepth == this.scalingBitDepth && this.equals(sampleSize, this.sampleSize)) {
            return;
        }
        this.sampleSize = sampleSize;
        this.scalingBitDepth = ihdr_bitDepth;
        final int n = (1 << ihdr_bitDepth) - 1;
        if (ihdr_bitDepth <= 8) {
            this.scale = new byte[this.numBands][];
            for (int i = 0; i < this.numBands; ++i) {
                final int n2 = (1 << sampleSize[i]) - 1;
                final int n3 = n2 / 2;
                this.scale[i] = new byte[n2 + 1];
                for (int j = 0; j <= n2; ++j) {
                    this.scale[i][j] = (byte)((j * n + n3) / n2);
                }
            }
            this.scale0 = this.scale[0];
            final byte[][] array = null;
            this.scalel = array;
            this.scaleh = array;
        }
        else {
            this.scaleh = new byte[this.numBands][];
            this.scalel = new byte[this.numBands][];
            for (int k = 0; k < this.numBands; ++k) {
                final int n4 = (1 << sampleSize[k]) - 1;
                final int n5 = n4 / 2;
                this.scaleh[k] = new byte[n4 + 1];
                this.scalel[k] = new byte[n4 + 1];
                for (int l = 0; l <= n4; ++l) {
                    final int n6 = (l * n + n5) / n4;
                    this.scaleh[k][l] = (byte)(n6 >> 8);
                    this.scalel[k][l] = (byte)(n6 & 0xFF);
                }
            }
            this.scale = null;
            this.scale0 = null;
        }
    }
    
    @Override
    public void write(final IIOMetadata iioMetadata, final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IIOException {
        if (this.stream == null) {
            throw new IllegalStateException("output == null!");
        }
        if (iioImage == null) {
            throw new IllegalArgumentException("image == null!");
        }
        if (iioImage.hasRaster()) {
            throw new UnsupportedOperationException("image has a Raster!");
        }
        final RenderedImage renderedImage = iioImage.getRenderedImage();
        final SampleModel sampleModel = renderedImage.getSampleModel();
        this.numBands = sampleModel.getNumBands();
        this.sourceXOffset = renderedImage.getMinX();
        this.sourceYOffset = renderedImage.getMinY();
        this.sourceWidth = renderedImage.getWidth();
        this.sourceHeight = renderedImage.getHeight();
        this.sourceBands = null;
        this.periodX = 1;
        this.periodY = 1;
        if (imageWriteParam != null) {
            final Rectangle sourceRegion = imageWriteParam.getSourceRegion();
            if (sourceRegion != null) {
                final Rectangle intersection = sourceRegion.intersection(new Rectangle(renderedImage.getMinX(), renderedImage.getMinY(), renderedImage.getWidth(), renderedImage.getHeight()));
                this.sourceXOffset = intersection.x;
                this.sourceYOffset = intersection.y;
                this.sourceWidth = intersection.width;
                this.sourceHeight = intersection.height;
            }
            final int subsamplingXOffset = imageWriteParam.getSubsamplingXOffset();
            final int subsamplingYOffset = imageWriteParam.getSubsamplingYOffset();
            this.sourceXOffset += subsamplingXOffset;
            this.sourceYOffset += subsamplingYOffset;
            this.sourceWidth -= subsamplingXOffset;
            this.sourceHeight -= subsamplingYOffset;
            this.periodX = imageWriteParam.getSourceXSubsampling();
            this.periodY = imageWriteParam.getSourceYSubsampling();
            final int[] sourceBands = imageWriteParam.getSourceBands();
            if (sourceBands != null) {
                this.sourceBands = sourceBands;
                this.numBands = this.sourceBands.length;
            }
        }
        final int ihdr_width = (this.sourceWidth + this.periodX - 1) / this.periodX;
        final int ihdr_height = (this.sourceHeight + this.periodY - 1) / this.periodY;
        if (ihdr_width <= 0 || ihdr_height <= 0) {
            throw new IllegalArgumentException("Empty source region!");
        }
        this.totalPixels = ihdr_width * ihdr_height;
        this.pixelsDone = 0;
        final IIOMetadata metadata = iioImage.getMetadata();
        if (metadata != null) {
            this.metadata = (PNGMetadata)this.convertImageMetadata(metadata, ImageTypeSpecifier.createFromRenderedImage(renderedImage), null);
        }
        else {
            this.metadata = new PNGMetadata();
        }
        if (imageWriteParam != null) {
            switch (imageWriteParam.getProgressiveMode()) {
                case 1: {
                    this.metadata.IHDR_interlaceMethod = 1;
                    break;
                }
                case 0: {
                    this.metadata.IHDR_interlaceMethod = 0;
                    break;
                }
            }
        }
        this.metadata.initialize(new ImageTypeSpecifier(renderedImage), this.numBands);
        this.metadata.IHDR_width = ihdr_width;
        this.metadata.IHDR_height = ihdr_height;
        this.bpp = this.numBands * ((this.metadata.IHDR_bitDepth == 16) ? 2 : 1);
        this.initializeScaleTables(sampleModel.getSampleSize());
        this.clearAbortRequest();
        this.processImageStarted(0);
        try {
            this.write_magic();
            this.write_IHDR();
            this.write_cHRM();
            this.write_gAMA();
            this.write_iCCP();
            this.write_sBIT();
            this.write_sRGB();
            this.write_PLTE();
            this.write_hIST();
            this.write_tRNS();
            this.write_bKGD();
            this.write_pHYs();
            this.write_sPLT();
            this.write_tIME();
            this.write_tEXt();
            this.write_iTXt();
            this.write_zTXt();
            this.writeUnknownChunks();
            this.write_IDAT(renderedImage);
            if (this.abortRequested()) {
                this.processWriteAborted();
            }
            else {
                this.writeIEND();
                this.processImageComplete();
            }
        }
        catch (final IOException ex) {
            throw new IIOException("I/O error writing PNG file!", ex);
        }
    }
    
    static {
        PNGImageWriter.allowedProgressivePasses = new int[] { 1, 7 };
    }
}
