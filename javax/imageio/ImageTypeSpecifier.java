package javax.imageio;

import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.BandedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.util.Hashtable;
import java.awt.image.Raster;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.color.ColorSpace;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;

public class ImageTypeSpecifier
{
    protected ColorModel colorModel;
    protected SampleModel sampleModel;
    private static ImageTypeSpecifier[] BISpecifier;
    private static ColorSpace sRGB;
    
    private ImageTypeSpecifier() {
    }
    
    public ImageTypeSpecifier(final ColorModel colorModel, final SampleModel sampleModel) {
        if (colorModel == null) {
            throw new IllegalArgumentException("colorModel == null!");
        }
        if (sampleModel == null) {
            throw new IllegalArgumentException("sampleModel == null!");
        }
        if (!colorModel.isCompatibleSampleModel(sampleModel)) {
            throw new IllegalArgumentException("sampleModel is incompatible with colorModel!");
        }
        this.colorModel = colorModel;
        this.sampleModel = sampleModel;
    }
    
    public ImageTypeSpecifier(final RenderedImage renderedImage) {
        if (renderedImage == null) {
            throw new IllegalArgumentException("image == null!");
        }
        this.colorModel = renderedImage.getColorModel();
        this.sampleModel = renderedImage.getSampleModel();
    }
    
    public static ImageTypeSpecifier createPacked(final ColorSpace colorSpace, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b) {
        return new Packed(colorSpace, n, n2, n3, n4, n5, b);
    }
    
    static ColorModel createComponentCM(final ColorSpace colorSpace, final int n, final int n2, final boolean b, final boolean b2) {
        final int n3 = b ? 3 : 1;
        final int[] array = new int[n];
        final int dataTypeSize = DataBuffer.getDataTypeSize(n2);
        for (int i = 0; i < n; ++i) {
            array[i] = dataTypeSize;
        }
        return new ComponentColorModel(colorSpace, array, b, b2, n3, n2);
    }
    
    public static ImageTypeSpecifier createInterleaved(final ColorSpace colorSpace, final int[] array, final int n, final boolean b, final boolean b2) {
        return new Interleaved(colorSpace, array, n, b, b2);
    }
    
    public static ImageTypeSpecifier createBanded(final ColorSpace colorSpace, final int[] array, final int[] array2, final int n, final boolean b, final boolean b2) {
        return new Banded(colorSpace, array, array2, n, b, b2);
    }
    
    public static ImageTypeSpecifier createGrayscale(final int n, final int n2, final boolean b) {
        return new Grayscale(n, n2, b, false, false);
    }
    
    public static ImageTypeSpecifier createGrayscale(final int n, final int n2, final boolean b, final boolean b2) {
        return new Grayscale(n, n2, b, true, b2);
    }
    
    public static ImageTypeSpecifier createIndexed(final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4, final int n, final int n2) {
        return new Indexed(array, array2, array3, array4, n, n2);
    }
    
    public static ImageTypeSpecifier createFromBufferedImageType(final int n) {
        if (n >= 1 && n <= 13) {
            return getSpecifier(n);
        }
        if (n == 0) {
            throw new IllegalArgumentException("Cannot create from TYPE_CUSTOM!");
        }
        throw new IllegalArgumentException("Invalid BufferedImage type!");
    }
    
    public static ImageTypeSpecifier createFromRenderedImage(final RenderedImage renderedImage) {
        if (renderedImage == null) {
            throw new IllegalArgumentException("image == null!");
        }
        if (renderedImage instanceof BufferedImage) {
            final int type = ((BufferedImage)renderedImage).getType();
            if (type != 0) {
                return getSpecifier(type);
            }
        }
        return new ImageTypeSpecifier(renderedImage);
    }
    
    public int getBufferedImageType() {
        return this.createBufferedImage(1, 1).getType();
    }
    
    public int getNumComponents() {
        return this.colorModel.getNumComponents();
    }
    
    public int getNumBands() {
        return this.sampleModel.getNumBands();
    }
    
    public int getBitsPerBand(final int n) {
        if (n < 0 | n >= this.getNumBands()) {
            throw new IllegalArgumentException("band out of range!");
        }
        return this.sampleModel.getSampleSize(n);
    }
    
    public SampleModel getSampleModel() {
        return this.sampleModel;
    }
    
    public SampleModel getSampleModel(final int n, final int n2) {
        if (n * (long)n2 > 2147483647L) {
            throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!");
        }
        return this.sampleModel.createCompatibleSampleModel(n, n2);
    }
    
    public ColorModel getColorModel() {
        return this.colorModel;
    }
    
    public BufferedImage createBufferedImage(final int n, final int n2) {
        try {
            return new BufferedImage(this.colorModel, Raster.createWritableRaster(this.getSampleModel(n, n2), new Point(0, 0)), this.colorModel.isAlphaPremultiplied(), new Hashtable<Object, Object>());
        }
        catch (final NegativeArraySizeException ex) {
            throw new IllegalArgumentException("Array size > Integer.MAX_VALUE!");
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ImageTypeSpecifier)) {
            return false;
        }
        final ImageTypeSpecifier imageTypeSpecifier = (ImageTypeSpecifier)o;
        return this.colorModel.equals(imageTypeSpecifier.colorModel) && this.sampleModel.equals(imageTypeSpecifier.sampleModel);
    }
    
    @Override
    public int hashCode() {
        return 9 * this.colorModel.hashCode() + 14 * this.sampleModel.hashCode();
    }
    
    private static ImageTypeSpecifier getSpecifier(final int n) {
        if (ImageTypeSpecifier.BISpecifier[n] == null) {
            ImageTypeSpecifier.BISpecifier[n] = createSpecifier(n);
        }
        return ImageTypeSpecifier.BISpecifier[n];
    }
    
    private static ImageTypeSpecifier createSpecifier(final int n) {
        switch (n) {
            case 1: {
                return createPacked(ImageTypeSpecifier.sRGB, 16711680, 65280, 255, 0, 3, false);
            }
            case 2: {
                return createPacked(ImageTypeSpecifier.sRGB, 16711680, 65280, 255, -16777216, 3, false);
            }
            case 3: {
                return createPacked(ImageTypeSpecifier.sRGB, 16711680, 65280, 255, -16777216, 3, true);
            }
            case 4: {
                return createPacked(ImageTypeSpecifier.sRGB, 255, 65280, 16711680, 0, 3, false);
            }
            case 5: {
                return createInterleaved(ImageTypeSpecifier.sRGB, new int[] { 2, 1, 0 }, 0, false, false);
            }
            case 6: {
                return createInterleaved(ImageTypeSpecifier.sRGB, new int[] { 3, 2, 1, 0 }, 0, true, false);
            }
            case 7: {
                return createInterleaved(ImageTypeSpecifier.sRGB, new int[] { 3, 2, 1, 0 }, 0, true, true);
            }
            case 8: {
                return createPacked(ImageTypeSpecifier.sRGB, 63488, 2016, 31, 0, 1, false);
            }
            case 9: {
                return createPacked(ImageTypeSpecifier.sRGB, 31744, 992, 31, 0, 1, false);
            }
            case 10: {
                return createGrayscale(8, 0, false);
            }
            case 11: {
                return createGrayscale(16, 1, false);
            }
            case 12: {
                return createGrayscale(1, 0, false);
            }
            case 13: {
                final IndexColorModel indexColorModel = (IndexColorModel)new BufferedImage(1, 1, 13).getColorModel();
                final int mapSize = indexColorModel.getMapSize();
                final byte[] array = new byte[mapSize];
                final byte[] array2 = new byte[mapSize];
                final byte[] array3 = new byte[mapSize];
                final byte[] array4 = new byte[mapSize];
                indexColorModel.getReds(array);
                indexColorModel.getGreens(array2);
                indexColorModel.getBlues(array3);
                indexColorModel.getAlphas(array4);
                return createIndexed(array, array2, array3, array4, 8, 0);
            }
            default: {
                throw new IllegalArgumentException("Invalid BufferedImage type!");
            }
        }
    }
    
    static {
        ImageTypeSpecifier.sRGB = ColorSpace.getInstance(1000);
        ImageTypeSpecifier.BISpecifier = new ImageTypeSpecifier[14];
    }
    
    static class Packed extends ImageTypeSpecifier
    {
        ColorSpace colorSpace;
        int redMask;
        int greenMask;
        int blueMask;
        int alphaMask;
        int transferType;
        boolean isAlphaPremultiplied;
        
        public Packed(final ColorSpace colorSpace, final int redMask, final int greenMask, final int blueMask, final int alphaMask, final int transferType, final boolean isAlphaPremultiplied) {
            super((ImageTypeSpecifier$1)null);
            if (colorSpace == null) {
                throw new IllegalArgumentException("colorSpace == null!");
            }
            if (colorSpace.getType() != 5) {
                throw new IllegalArgumentException("colorSpace is not of type TYPE_RGB!");
            }
            if (transferType != 0 && transferType != 1 && transferType != 3) {
                throw new IllegalArgumentException("Bad value for transferType!");
            }
            if (redMask == 0 && greenMask == 0 && blueMask == 0 && alphaMask == 0) {
                throw new IllegalArgumentException("No mask has at least 1 bit set!");
            }
            this.colorSpace = colorSpace;
            this.redMask = redMask;
            this.greenMask = greenMask;
            this.blueMask = blueMask;
            this.alphaMask = alphaMask;
            this.transferType = transferType;
            this.isAlphaPremultiplied = isAlphaPremultiplied;
            this.colorModel = new DirectColorModel(colorSpace, 32, redMask, greenMask, blueMask, alphaMask, isAlphaPremultiplied, transferType);
            this.sampleModel = this.colorModel.createCompatibleSampleModel(1, 1);
        }
    }
    
    static class Interleaved extends ImageTypeSpecifier
    {
        ColorSpace colorSpace;
        int[] bandOffsets;
        int dataType;
        boolean hasAlpha;
        boolean isAlphaPremultiplied;
        
        public Interleaved(final ColorSpace colorSpace, final int[] array, final int dataType, final boolean hasAlpha, final boolean isAlphaPremultiplied) {
            super((ImageTypeSpecifier$1)null);
            if (colorSpace == null) {
                throw new IllegalArgumentException("colorSpace == null!");
            }
            if (array == null) {
                throw new IllegalArgumentException("bandOffsets == null!");
            }
            if (array.length != colorSpace.getNumComponents() + (hasAlpha ? 1 : 0)) {
                throw new IllegalArgumentException("bandOffsets.length is wrong!");
            }
            if (dataType != 0 && dataType != 2 && dataType != 1 && dataType != 3 && dataType != 4 && dataType != 5) {
                throw new IllegalArgumentException("Bad value for dataType!");
            }
            this.colorSpace = colorSpace;
            this.bandOffsets = array.clone();
            this.dataType = dataType;
            this.hasAlpha = hasAlpha;
            this.isAlphaPremultiplied = isAlphaPremultiplied;
            this.colorModel = ImageTypeSpecifier.createComponentCM(colorSpace, array.length, dataType, hasAlpha, isAlphaPremultiplied);
            int max;
            int min = max = array[0];
            for (int i = 0; i < array.length; ++i) {
                final int n = array[i];
                min = Math.min(n, min);
                max = Math.max(n, max);
            }
            final int n2 = max - min + 1;
            final int n3 = 1;
            this.sampleModel = new PixelInterleavedSampleModel(dataType, n3, 1, n2, n3 * n2, array);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null || !(o instanceof Interleaved)) {
                return false;
            }
            final Interleaved interleaved = (Interleaved)o;
            if (!this.colorSpace.equals(interleaved.colorSpace) || this.dataType != interleaved.dataType || this.hasAlpha != interleaved.hasAlpha || this.isAlphaPremultiplied != interleaved.isAlphaPremultiplied || this.bandOffsets.length != interleaved.bandOffsets.length) {
                return false;
            }
            for (int i = 0; i < this.bandOffsets.length; ++i) {
                if (this.bandOffsets[i] != interleaved.bandOffsets[i]) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            return super.hashCode() + 4 * this.bandOffsets.length + 25 * this.dataType + (this.hasAlpha ? 17 : 18);
        }
    }
    
    static class Banded extends ImageTypeSpecifier
    {
        ColorSpace colorSpace;
        int[] bankIndices;
        int[] bandOffsets;
        int dataType;
        boolean hasAlpha;
        boolean isAlphaPremultiplied;
        
        public Banded(final ColorSpace colorSpace, final int[] array, final int[] array2, final int dataType, final boolean hasAlpha, final boolean isAlphaPremultiplied) {
            super((ImageTypeSpecifier$1)null);
            if (colorSpace == null) {
                throw new IllegalArgumentException("colorSpace == null!");
            }
            if (array == null) {
                throw new IllegalArgumentException("bankIndices == null!");
            }
            if (array2 == null) {
                throw new IllegalArgumentException("bandOffsets == null!");
            }
            if (array.length != array2.length) {
                throw new IllegalArgumentException("bankIndices.length != bandOffsets.length!");
            }
            if (dataType != 0 && dataType != 2 && dataType != 1 && dataType != 3 && dataType != 4 && dataType != 5) {
                throw new IllegalArgumentException("Bad value for dataType!");
            }
            if (array2.length != colorSpace.getNumComponents() + (hasAlpha ? 1 : 0)) {
                throw new IllegalArgumentException("bandOffsets.length is wrong!");
            }
            this.colorSpace = colorSpace;
            this.bankIndices = array.clone();
            this.bandOffsets = array2.clone();
            this.dataType = dataType;
            this.hasAlpha = hasAlpha;
            this.isAlphaPremultiplied = isAlphaPremultiplied;
            this.colorModel = ImageTypeSpecifier.createComponentCM(colorSpace, array.length, dataType, hasAlpha, isAlphaPremultiplied);
            final int n = 1;
            this.sampleModel = new BandedSampleModel(dataType, n, 1, n, array, array2);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null || !(o instanceof Banded)) {
                return false;
            }
            final Banded banded = (Banded)o;
            if (!this.colorSpace.equals(banded.colorSpace) || this.dataType != banded.dataType || this.hasAlpha != banded.hasAlpha || this.isAlphaPremultiplied != banded.isAlphaPremultiplied || this.bankIndices.length != banded.bankIndices.length || this.bandOffsets.length != banded.bandOffsets.length) {
                return false;
            }
            for (int i = 0; i < this.bankIndices.length; ++i) {
                if (this.bankIndices[i] != banded.bankIndices[i]) {
                    return false;
                }
            }
            for (int j = 0; j < this.bandOffsets.length; ++j) {
                if (this.bandOffsets[j] != banded.bandOffsets[j]) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            return super.hashCode() + 3 * this.bandOffsets.length + 7 * this.bankIndices.length + 21 * this.dataType + (this.hasAlpha ? 19 : 29);
        }
    }
    
    static class Grayscale extends ImageTypeSpecifier
    {
        int bits;
        int dataType;
        boolean isSigned;
        boolean hasAlpha;
        boolean isAlphaPremultiplied;
        
        public Grayscale(final int bits, final int dataType, final boolean isSigned, final boolean hasAlpha, final boolean isAlphaPremultiplied) {
            super((ImageTypeSpecifier$1)null);
            if (bits != 1 && bits != 2 && bits != 4 && bits != 8 && bits != 16) {
                throw new IllegalArgumentException("Bad value for bits!");
            }
            if (dataType != 0 && dataType != 2 && dataType != 1) {
                throw new IllegalArgumentException("Bad value for dataType!");
            }
            if (bits > 8 && dataType == 0) {
                throw new IllegalArgumentException("Too many bits for dataType!");
            }
            this.bits = bits;
            this.dataType = dataType;
            this.isSigned = isSigned;
            this.hasAlpha = hasAlpha;
            this.isAlphaPremultiplied = isAlphaPremultiplied;
            final ColorSpace instance = ColorSpace.getInstance(1003);
            if ((bits == 8 && dataType == 0) || (bits == 16 && (dataType == 2 || dataType == 1))) {
                final int n = hasAlpha ? 2 : 1;
                final int n2 = hasAlpha ? 3 : 1;
                final int[] array = new int[n];
                array[0] = bits;
                if (n == 2) {
                    array[1] = bits;
                }
                this.colorModel = new ComponentColorModel(instance, array, hasAlpha, isAlphaPremultiplied, n2, dataType);
                final int[] array2 = new int[n];
                array2[0] = 0;
                if (n == 2) {
                    array2[1] = 1;
                }
                final int n3 = 1;
                this.sampleModel = new PixelInterleavedSampleModel(dataType, n3, 1, n, n3 * n, array2);
            }
            else {
                final int n4 = 1 << bits;
                final byte[] array3 = new byte[n4];
                for (int i = 0; i < n4; ++i) {
                    array3[i] = (byte)(i * 255 / (n4 - 1));
                }
                this.colorModel = new IndexColorModel(bits, n4, array3, array3, array3);
                this.sampleModel = new MultiPixelPackedSampleModel(dataType, 1, 1, bits);
            }
        }
    }
    
    static class Indexed extends ImageTypeSpecifier
    {
        byte[] redLUT;
        byte[] greenLUT;
        byte[] blueLUT;
        byte[] alphaLUT;
        int bits;
        int dataType;
        
        public Indexed(final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4, final int bits, final int dataType) {
            super((ImageTypeSpecifier$1)null);
            this.alphaLUT = null;
            if (array == null || array2 == null || array3 == null) {
                throw new IllegalArgumentException("LUT is null!");
            }
            if (bits != 1 && bits != 2 && bits != 4 && bits != 8 && bits != 16) {
                throw new IllegalArgumentException("Bad value for bits!");
            }
            if (dataType != 0 && dataType != 2 && dataType != 1 && dataType != 3) {
                throw new IllegalArgumentException("Bad value for dataType!");
            }
            if ((bits > 8 && dataType == 0) || (bits > 16 && dataType != 3)) {
                throw new IllegalArgumentException("Too many bits for dataType!");
            }
            final int n = 1 << bits;
            if (array.length != n || array2.length != n || array3.length != n || (array4 != null && array4.length != n)) {
                throw new IllegalArgumentException("LUT has improper length!");
            }
            this.redLUT = array.clone();
            this.greenLUT = array2.clone();
            this.blueLUT = array3.clone();
            if (array4 != null) {
                this.alphaLUT = array4.clone();
            }
            this.bits = bits;
            this.dataType = dataType;
            if (array4 == null) {
                this.colorModel = new IndexColorModel(bits, array.length, array, array2, array3);
            }
            else {
                this.colorModel = new IndexColorModel(bits, array.length, array, array2, array3, array4);
            }
            if ((bits == 8 && dataType == 0) || (bits == 16 && (dataType == 2 || dataType == 1))) {
                this.sampleModel = new PixelInterleavedSampleModel(dataType, 1, 1, 1, 1, new int[] { 0 });
            }
            else {
                this.sampleModel = new MultiPixelPackedSampleModel(dataType, 1, 1, bits);
            }
        }
    }
}
