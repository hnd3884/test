package java.awt.image;

import sun.java2d.cmm.ProfileDeferralMgr;
import java.awt.geom.Point2D;
import java.awt.Point;
import java.util.Hashtable;
import java.awt.geom.Rectangle2D;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.CMSManager;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ICC_ColorSpace;
import java.awt.RenderingHints;
import sun.java2d.cmm.ColorTransform;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;

public class ColorConvertOp implements BufferedImageOp, RasterOp
{
    ICC_Profile[] profileList;
    ColorSpace[] CSList;
    ColorTransform thisTransform;
    ColorTransform thisRasterTransform;
    ICC_Profile thisSrcProfile;
    ICC_Profile thisDestProfile;
    RenderingHints hints;
    boolean gotProfiles;
    float[] srcMinVals;
    float[] srcMaxVals;
    float[] dstMinVals;
    float[] dstMaxVals;
    
    public ColorConvertOp(final RenderingHints hints) {
        this.profileList = new ICC_Profile[0];
        this.hints = hints;
    }
    
    public ColorConvertOp(final ColorSpace colorSpace, final RenderingHints hints) {
        if (colorSpace == null) {
            throw new NullPointerException("ColorSpace cannot be null");
        }
        if (colorSpace instanceof ICC_ColorSpace) {
            (this.profileList = new ICC_Profile[1])[0] = ((ICC_ColorSpace)colorSpace).getProfile();
        }
        else {
            (this.CSList = new ColorSpace[1])[0] = colorSpace;
        }
        this.hints = hints;
    }
    
    public ColorConvertOp(final ColorSpace colorSpace, final ColorSpace colorSpace2, final RenderingHints hints) {
        if (colorSpace == null || colorSpace2 == null) {
            throw new NullPointerException("ColorSpaces cannot be null");
        }
        if (colorSpace instanceof ICC_ColorSpace && colorSpace2 instanceof ICC_ColorSpace) {
            (this.profileList = new ICC_Profile[2])[0] = ((ICC_ColorSpace)colorSpace).getProfile();
            this.profileList[1] = ((ICC_ColorSpace)colorSpace2).getProfile();
            this.getMinMaxValsFromColorSpaces(colorSpace, colorSpace2);
        }
        else {
            (this.CSList = new ColorSpace[2])[0] = colorSpace;
            this.CSList[1] = colorSpace2;
        }
        this.hints = hints;
    }
    
    public ColorConvertOp(final ICC_Profile[] array, final RenderingHints hints) {
        if (array == null) {
            throw new NullPointerException("Profiles cannot be null");
        }
        this.gotProfiles = true;
        this.profileList = new ICC_Profile[array.length];
        for (int i = 0; i < array.length; ++i) {
            this.profileList[i] = array[i];
        }
        this.hints = hints;
    }
    
    public final ICC_Profile[] getICC_Profiles() {
        if (this.gotProfiles) {
            final ICC_Profile[] array = new ICC_Profile[this.profileList.length];
            for (int i = 0; i < this.profileList.length; ++i) {
                array[i] = this.profileList[i];
            }
            return array;
        }
        return null;
    }
    
    @Override
    public final BufferedImage filter(BufferedImage convertToIntDiscrete, BufferedImage bufferedImage) {
        BufferedImage bufferedImage2 = null;
        if (convertToIntDiscrete.getColorModel() instanceof IndexColorModel) {
            convertToIntDiscrete = ((IndexColorModel)convertToIntDiscrete.getColorModel()).convertToIntDiscrete(convertToIntDiscrete.getRaster(), true);
        }
        final ColorSpace colorSpace = convertToIntDiscrete.getColorModel().getColorSpace();
        ColorSpace colorSpace2;
        if (bufferedImage != null) {
            if (bufferedImage.getColorModel() instanceof IndexColorModel) {
                bufferedImage2 = bufferedImage;
                bufferedImage = null;
                colorSpace2 = null;
            }
            else {
                colorSpace2 = bufferedImage.getColorModel().getColorSpace();
            }
        }
        else {
            colorSpace2 = null;
        }
        if (this.CSList != null || !(colorSpace instanceof ICC_ColorSpace) || (bufferedImage != null && !(colorSpace2 instanceof ICC_ColorSpace))) {
            bufferedImage = this.nonICCBIFilter(convertToIntDiscrete, colorSpace, bufferedImage, colorSpace2);
        }
        else {
            bufferedImage = this.ICCBIFilter(convertToIntDiscrete, colorSpace, bufferedImage, colorSpace2);
        }
        if (bufferedImage2 != null) {
            final Graphics2D graphics = bufferedImage2.createGraphics();
            try {
                graphics.drawImage(bufferedImage, 0, 0, null);
            }
            finally {
                graphics.dispose();
            }
            return bufferedImage2;
        }
        return bufferedImage;
    }
    
    private final BufferedImage ICCBIFilter(final BufferedImage bufferedImage, final ColorSpace colorSpace, BufferedImage compatibleDestImage, final ColorSpace colorSpace2) {
        final int length = this.profileList.length;
        final ICC_Profile profile = ((ICC_ColorSpace)colorSpace).getProfile();
        ICC_Profile profile2;
        if (compatibleDestImage == null) {
            if (length == 0) {
                throw new IllegalArgumentException("Destination ColorSpace is undefined");
            }
            profile2 = this.profileList[length - 1];
            compatibleDestImage = this.createCompatibleDestImage(bufferedImage, null);
        }
        else {
            if (bufferedImage.getHeight() != compatibleDestImage.getHeight() || bufferedImage.getWidth() != compatibleDestImage.getWidth()) {
                throw new IllegalArgumentException("Width or height of BufferedImages do not match");
            }
            profile2 = ((ICC_ColorSpace)colorSpace2).getProfile();
        }
        if (profile == profile2) {
            boolean b = true;
            for (int i = 0; i < length; ++i) {
                if (profile != this.profileList[i]) {
                    b = false;
                    break;
                }
            }
            if (b) {
                final Graphics2D graphics = compatibleDestImage.createGraphics();
                try {
                    graphics.drawImage(bufferedImage, 0, 0, null);
                }
                finally {
                    graphics.dispose();
                }
                return compatibleDestImage;
            }
        }
        if (this.thisTransform == null || this.thisSrcProfile != profile || this.thisDestProfile != profile2) {
            this.updateBITransform(profile, profile2);
        }
        this.thisTransform.colorConvert(bufferedImage, compatibleDestImage);
        return compatibleDestImage;
    }
    
    private void updateBITransform(final ICC_Profile thisSrcProfile, final ICC_Profile thisDestProfile) {
        boolean b = false;
        boolean b2 = false;
        int length;
        final int n = length = this.profileList.length;
        if (n == 0 || thisSrcProfile != this.profileList[0]) {
            ++length;
            b = true;
        }
        if (n == 0 || thisDestProfile != this.profileList[n - 1] || length < 2) {
            ++length;
            b2 = true;
        }
        final ICC_Profile[] array = new ICC_Profile[length];
        int n2 = 0;
        if (b) {
            array[n2++] = thisSrcProfile;
        }
        for (int i = 0; i < n; ++i) {
            array[n2++] = this.profileList[i];
        }
        if (b2) {
            array[n2] = thisDestProfile;
        }
        final ColorTransform[] array2 = new ColorTransform[length];
        int renderingIntent;
        if (array[0].getProfileClass() == 2) {
            renderingIntent = 1;
        }
        else {
            renderingIntent = 0;
        }
        int n3 = 1;
        final PCMM module = CMSManager.getModule();
        for (int j = 0; j < length; ++j) {
            if (j == length - 1) {
                n3 = 2;
            }
            else if (n3 == 4 && array[j].getProfileClass() == 5) {
                renderingIntent = 0;
                n3 = 1;
            }
            array2[j] = module.createTransform(array[j], renderingIntent, n3);
            renderingIntent = this.getRenderingIntent(array[j]);
            n3 = 4;
        }
        this.thisTransform = module.createTransform(array2);
        this.thisSrcProfile = thisSrcProfile;
        this.thisDestProfile = thisDestProfile;
    }
    
    @Override
    public final WritableRaster filter(final Raster raster, WritableRaster compatibleDestRaster) {
        if (this.CSList != null) {
            return this.nonICCRasterFilter(raster, compatibleDestRaster);
        }
        final int length = this.profileList.length;
        if (length < 2) {
            throw new IllegalArgumentException("Source or Destination ColorSpace is undefined");
        }
        if (raster.getNumBands() != this.profileList[0].getNumComponents()) {
            throw new IllegalArgumentException("Numbers of source Raster bands and source color space components do not match");
        }
        if (compatibleDestRaster == null) {
            compatibleDestRaster = this.createCompatibleDestRaster(raster);
        }
        else {
            if (raster.getHeight() != compatibleDestRaster.getHeight() || raster.getWidth() != compatibleDestRaster.getWidth()) {
                throw new IllegalArgumentException("Width or height of Rasters do not match");
            }
            if (compatibleDestRaster.getNumBands() != this.profileList[length - 1].getNumComponents()) {
                throw new IllegalArgumentException("Numbers of destination Raster bands and destination color space components do not match");
            }
        }
        if (this.thisRasterTransform == null) {
            final ColorTransform[] array = new ColorTransform[length];
            int renderingIntent;
            if (this.profileList[0].getProfileClass() == 2) {
                renderingIntent = 1;
            }
            else {
                renderingIntent = 0;
            }
            int n = 1;
            final PCMM module = CMSManager.getModule();
            for (int i = 0; i < length; ++i) {
                if (i == length - 1) {
                    n = 2;
                }
                else if (n == 4 && this.profileList[i].getProfileClass() == 5) {
                    renderingIntent = 0;
                    n = 1;
                }
                array[i] = module.createTransform(this.profileList[i], renderingIntent, n);
                renderingIntent = this.getRenderingIntent(this.profileList[i]);
                n = 4;
            }
            this.thisRasterTransform = module.createTransform(array);
        }
        final int transferType = raster.getTransferType();
        final int transferType2 = compatibleDestRaster.getTransferType();
        if (transferType == 4 || transferType == 5 || transferType2 == 4 || transferType2 == 5) {
            if (this.srcMinVals == null) {
                this.getMinMaxValsFromProfiles(this.profileList[0], this.profileList[length - 1]);
            }
            this.thisRasterTransform.colorConvert(raster, compatibleDestRaster, this.srcMinVals, this.srcMaxVals, this.dstMinVals, this.dstMaxVals);
        }
        else {
            this.thisRasterTransform.colorConvert(raster, compatibleDestRaster);
        }
        return compatibleDestRaster;
    }
    
    @Override
    public final Rectangle2D getBounds2D(final BufferedImage bufferedImage) {
        return this.getBounds2D(bufferedImage.getRaster());
    }
    
    @Override
    public final Rectangle2D getBounds2D(final Raster raster) {
        return raster.getBounds();
    }
    
    @Override
    public BufferedImage createCompatibleDestImage(final BufferedImage bufferedImage, final ColorModel colorModel) {
        ColorSpace colorSpace = null;
        if (colorModel == null) {
            if (this.CSList == null) {
                final int length = this.profileList.length;
                if (length == 0) {
                    throw new IllegalArgumentException("Destination ColorSpace is undefined");
                }
                colorSpace = new ICC_ColorSpace(this.profileList[length - 1]);
            }
            else {
                colorSpace = this.CSList[this.CSList.length - 1];
            }
        }
        return this.createCompatibleDestImage(bufferedImage, colorModel, colorSpace);
    }
    
    private BufferedImage createCompatibleDestImage(final BufferedImage bufferedImage, ColorModel colorModel, final ColorSpace colorSpace) {
        if (colorModel == null) {
            final ColorModel colorModel2 = bufferedImage.getColorModel();
            int numComponents = colorSpace.getNumComponents();
            final boolean hasAlpha = colorModel2.hasAlpha();
            if (hasAlpha) {
                ++numComponents;
            }
            final int[] array = new int[numComponents];
            for (int i = 0; i < numComponents; ++i) {
                array[i] = 8;
            }
            colorModel = new ComponentColorModel(colorSpace, array, hasAlpha, colorModel2.isAlphaPremultiplied(), colorModel2.getTransparency(), 0);
        }
        return new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(bufferedImage.getWidth(), bufferedImage.getHeight()), colorModel.isAlphaPremultiplied(), null);
    }
    
    @Override
    public WritableRaster createCompatibleDestRaster(final Raster raster) {
        int n;
        if (this.CSList != null) {
            if (this.CSList.length != 2) {
                throw new IllegalArgumentException("Destination ColorSpace is undefined");
            }
            n = this.CSList[1].getNumComponents();
        }
        else {
            final int length = this.profileList.length;
            if (length < 2) {
                throw new IllegalArgumentException("Destination ColorSpace is undefined");
            }
            n = this.profileList[length - 1].getNumComponents();
        }
        return Raster.createInterleavedRaster(0, raster.getWidth(), raster.getHeight(), n, new Point(raster.getMinX(), raster.getMinY()));
    }
    
    @Override
    public final Point2D getPoint2D(final Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            point2D2 = new Point2D.Float();
        }
        point2D2.setLocation(point2D.getX(), point2D.getY());
        return point2D2;
    }
    
    private int getRenderingIntent(final ICC_Profile icc_Profile) {
        final byte[] data = icc_Profile.getData(1751474532);
        final int n = 64;
        return (data[n + 2] & 0xFF) << 8 | (data[n + 3] & 0xFF);
    }
    
    @Override
    public final RenderingHints getRenderingHints() {
        return this.hints;
    }
    
    private final BufferedImage nonICCBIFilter(final BufferedImage bufferedImage, final ColorSpace colorSpace, BufferedImage compatibleDestImage, ColorSpace colorSpace2) {
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        final ICC_ColorSpace icc_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1001);
        if (compatibleDestImage == null) {
            compatibleDestImage = this.createCompatibleDestImage(bufferedImage, null);
            colorSpace2 = compatibleDestImage.getColorModel().getColorSpace();
        }
        else if (height != compatibleDestImage.getHeight() || width != compatibleDestImage.getWidth()) {
            throw new IllegalArgumentException("Width or height of BufferedImages do not match");
        }
        final WritableRaster raster = bufferedImage.getRaster();
        final WritableRaster raster2 = compatibleDestImage.getRaster();
        final ColorModel colorModel = bufferedImage.getColorModel();
        final ColorModel colorModel2 = compatibleDestImage.getColorModel();
        final int numColorComponents = colorModel.getNumColorComponents();
        final int numColorComponents2 = colorModel2.getNumColorComponents();
        final boolean hasAlpha = colorModel2.hasAlpha();
        final boolean b = colorModel.hasAlpha() && hasAlpha;
        if (this.CSList == null && this.profileList.length != 0) {
            boolean b2;
            ICC_Profile icc_Profile;
            if (!(colorSpace instanceof ICC_ColorSpace)) {
                b2 = true;
                icc_Profile = icc_ColorSpace.getProfile();
            }
            else {
                b2 = false;
                icc_Profile = ((ICC_ColorSpace)colorSpace).getProfile();
            }
            boolean b3;
            ICC_Profile icc_Profile2;
            if (!(colorSpace2 instanceof ICC_ColorSpace)) {
                b3 = true;
                icc_Profile2 = icc_ColorSpace.getProfile();
            }
            else {
                b3 = false;
                icc_Profile2 = ((ICC_ColorSpace)colorSpace2).getProfile();
            }
            if (this.thisTransform == null || this.thisSrcProfile != icc_Profile || this.thisDestProfile != icc_Profile2) {
                this.updateBITransform(icc_Profile, icc_Profile2);
            }
            final float n = 65535.0f;
            ColorSpace colorSpace3;
            int n2;
            if (b2) {
                colorSpace3 = icc_ColorSpace;
                n2 = 3;
            }
            else {
                colorSpace3 = colorSpace;
                n2 = numColorComponents;
            }
            final float[] array = new float[n2];
            final float[] array2 = new float[n2];
            for (int i = 0; i < numColorComponents; ++i) {
                array[i] = colorSpace3.getMinValue(i);
                array2[i] = n / (colorSpace3.getMaxValue(i) - array[i]);
            }
            ColorSpace colorSpace4;
            int n3;
            if (b3) {
                colorSpace4 = icc_ColorSpace;
                n3 = 3;
            }
            else {
                colorSpace4 = colorSpace2;
                n3 = numColorComponents2;
            }
            final float[] array3 = new float[n3];
            final float[] array4 = new float[n3];
            for (int j = 0; j < numColorComponents2; ++j) {
                array3[j] = colorSpace4.getMinValue(j);
                array4[j] = (colorSpace4.getMaxValue(j) - array3[j]) / n;
            }
            float[] array5;
            if (hasAlpha) {
                array5 = new float[(numColorComponents2 + 1 > 3) ? (numColorComponents2 + 1) : 3];
            }
            else {
                array5 = new float[(numColorComponents2 > 3) ? numColorComponents2 : 3];
            }
            final short[] array6 = new short[width * n2];
            final short[] array7 = new short[width * n3];
            float[] array8 = null;
            if (b) {
                array8 = new float[width];
            }
            for (int k = 0; k < height; ++k) {
                Object dataElements = null;
                float[] array9 = null;
                int n4 = 0;
                for (int l = 0; l < width; ++l) {
                    dataElements = raster.getDataElements(l, k, dataElements);
                    array9 = colorModel.getNormalizedComponents(dataElements, array9, 0);
                    if (b) {
                        array8[l] = array9[numColorComponents];
                    }
                    if (b2) {
                        array9 = colorSpace.toCIEXYZ(array9);
                    }
                    for (int n5 = 0; n5 < n2; ++n5) {
                        array6[n4++] = (short)((array9[n5] - array[n5]) * array2[n5] + 0.5f);
                    }
                }
                this.thisTransform.colorConvert(array6, array7);
                Object dataElements2 = null;
                int n6 = 0;
                for (int n7 = 0; n7 < width; ++n7) {
                    for (int n8 = 0; n8 < n3; ++n8) {
                        array5[n8] = (array7[n6++] & 0xFFFF) * array4[n8] + array3[n8];
                    }
                    if (b3) {
                        final float[] fromCIEXYZ = colorSpace.fromCIEXYZ(array5);
                        for (int n9 = 0; n9 < numColorComponents2; ++n9) {
                            array5[n9] = fromCIEXYZ[n9];
                        }
                    }
                    if (b) {
                        array5[numColorComponents2] = array8[n7];
                    }
                    else if (hasAlpha) {
                        array5[numColorComponents2] = 1.0f;
                    }
                    dataElements2 = colorModel2.getDataElements(array5, 0, dataElements2);
                    raster2.setDataElements(n7, k, dataElements2);
                }
            }
        }
        else {
            int length;
            if (this.CSList == null) {
                length = 0;
            }
            else {
                length = this.CSList.length;
            }
            float[] array10;
            if (hasAlpha) {
                array10 = new float[numColorComponents2 + 1];
            }
            else {
                array10 = new float[numColorComponents2];
            }
            Object dataElements3 = null;
            Object dataElements4 = null;
            float[] normalizedComponents = null;
            for (int n10 = 0; n10 < height; ++n10) {
                for (int n11 = 0; n11 < width; ++n11) {
                    dataElements3 = raster.getDataElements(n11, n10, dataElements3);
                    normalizedComponents = colorModel.getNormalizedComponents(dataElements3, normalizedComponents, 0);
                    float[] array11 = colorSpace.toCIEXYZ(normalizedComponents);
                    for (int n12 = 0; n12 < length; ++n12) {
                        array11 = this.CSList[n12].toCIEXYZ(this.CSList[n12].fromCIEXYZ(array11));
                    }
                    final float[] fromCIEXYZ2 = colorSpace2.fromCIEXYZ(array11);
                    for (int n13 = 0; n13 < numColorComponents2; ++n13) {
                        array10[n13] = fromCIEXYZ2[n13];
                    }
                    if (b) {
                        array10[numColorComponents2] = normalizedComponents[numColorComponents];
                    }
                    else if (hasAlpha) {
                        array10[numColorComponents2] = 1.0f;
                    }
                    dataElements4 = colorModel2.getDataElements(array10, 0, dataElements4);
                    raster2.setDataElements(n11, n10, dataElements4);
                }
            }
        }
        return compatibleDestImage;
    }
    
    private final WritableRaster nonICCRasterFilter(final Raster raster, WritableRaster compatibleDestRaster) {
        if (this.CSList.length != 2) {
            throw new IllegalArgumentException("Destination ColorSpace is undefined");
        }
        if (raster.getNumBands() != this.CSList[0].getNumComponents()) {
            throw new IllegalArgumentException("Numbers of source Raster bands and source color space components do not match");
        }
        if (compatibleDestRaster == null) {
            compatibleDestRaster = this.createCompatibleDestRaster(raster);
        }
        else {
            if (raster.getHeight() != compatibleDestRaster.getHeight() || raster.getWidth() != compatibleDestRaster.getWidth()) {
                throw new IllegalArgumentException("Width or height of Rasters do not match");
            }
            if (compatibleDestRaster.getNumBands() != this.CSList[1].getNumComponents()) {
                throw new IllegalArgumentException("Numbers of destination Raster bands and destination color space components do not match");
            }
        }
        if (this.srcMinVals == null) {
            this.getMinMaxValsFromColorSpaces(this.CSList[0], this.CSList[1]);
        }
        final SampleModel sampleModel = raster.getSampleModel();
        final SampleModel sampleModel2 = compatibleDestRaster.getSampleModel();
        final int transferType = raster.getTransferType();
        final int transferType2 = compatibleDestRaster.getTransferType();
        final boolean b = transferType == 4 || transferType == 5;
        final boolean b2 = transferType2 == 4 || transferType2 == 5;
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int numBands = raster.getNumBands();
        final int numBands2 = compatibleDestRaster.getNumBands();
        float[] array = null;
        float[] array2 = null;
        if (!b) {
            array = new float[numBands];
            for (int i = 0; i < numBands; ++i) {
                if (transferType == 2) {
                    array[i] = (this.srcMaxVals[i] - this.srcMinVals[i]) / 32767.0f;
                }
                else {
                    array[i] = (this.srcMaxVals[i] - this.srcMinVals[i]) / ((1 << sampleModel.getSampleSize(i)) - 1);
                }
            }
        }
        if (!b2) {
            array2 = new float[numBands2];
            for (int j = 0; j < numBands2; ++j) {
                if (transferType2 == 2) {
                    array2[j] = 32767.0f / (this.dstMaxVals[j] - this.dstMinVals[j]);
                }
                else {
                    array2[j] = ((1 << sampleModel2.getSampleSize(j)) - 1) / (this.dstMaxVals[j] - this.dstMinVals[j]);
                }
            }
        }
        int minY = raster.getMinY();
        int minY2 = compatibleDestRaster.getMinY();
        final float[] array3 = new float[numBands];
        final ColorSpace colorSpace = this.CSList[0];
        final ColorSpace colorSpace2 = this.CSList[1];
        for (int k = 0; k < height; ++k, ++minY, ++minY2) {
            for (int minX = raster.getMinX(), minX2 = compatibleDestRaster.getMinX(), l = 0; l < width; ++l, ++minX, ++minX2) {
                for (int n = 0; n < numBands; ++n) {
                    float sampleFloat = raster.getSampleFloat(minX, minY, n);
                    if (!b) {
                        sampleFloat = sampleFloat * array[n] + this.srcMinVals[n];
                    }
                    array3[n] = sampleFloat;
                }
                final float[] fromCIEXYZ = colorSpace2.fromCIEXYZ(colorSpace.toCIEXYZ(array3));
                for (int n2 = 0; n2 < numBands2; ++n2) {
                    float n3 = fromCIEXYZ[n2];
                    if (!b2) {
                        n3 = (n3 - this.dstMinVals[n2]) * array2[n2];
                    }
                    compatibleDestRaster.setSample(minX2, minY2, n2, n3);
                }
            }
        }
        return compatibleDestRaster;
    }
    
    private void getMinMaxValsFromProfiles(final ICC_Profile icc_Profile, final ICC_Profile icc_Profile2) {
        final int colorSpaceType = icc_Profile.getColorSpaceType();
        final int numComponents = icc_Profile.getNumComponents();
        this.srcMinVals = new float[numComponents];
        this.srcMaxVals = new float[numComponents];
        this.setMinMax(colorSpaceType, numComponents, this.srcMinVals, this.srcMaxVals);
        final int colorSpaceType2 = icc_Profile2.getColorSpaceType();
        final int numComponents2 = icc_Profile2.getNumComponents();
        this.dstMinVals = new float[numComponents2];
        this.dstMaxVals = new float[numComponents2];
        this.setMinMax(colorSpaceType2, numComponents2, this.dstMinVals, this.dstMaxVals);
    }
    
    private void setMinMax(final int n, final int n2, final float[] array, final float[] array2) {
        if (n == 1) {
            array[0] = 0.0f;
            array2[0] = 100.0f;
            array[1] = -128.0f;
            array2[1] = 127.0f;
            array[2] = -128.0f;
            array2[2] = 127.0f;
        }
        else if (n == 0) {
            final int n3 = 0;
            final int n4 = 1;
            final int n5 = 2;
            final float n6 = 0.0f;
            array[n5] = n6;
            array[n3] = (array[n4] = n6);
            final int n7 = 0;
            final int n8 = 1;
            final int n9 = 2;
            final float n10 = 1.9999695f;
            array2[n9] = n10;
            array2[n7] = (array2[n8] = n10);
        }
        else {
            for (int i = 0; i < n2; ++i) {
                array[i] = 0.0f;
                array2[i] = 1.0f;
            }
        }
    }
    
    private void getMinMaxValsFromColorSpaces(final ColorSpace colorSpace, final ColorSpace colorSpace2) {
        final int numComponents = colorSpace.getNumComponents();
        this.srcMinVals = new float[numComponents];
        this.srcMaxVals = new float[numComponents];
        for (int i = 0; i < numComponents; ++i) {
            this.srcMinVals[i] = colorSpace.getMinValue(i);
            this.srcMaxVals[i] = colorSpace.getMaxValue(i);
        }
        final int numComponents2 = colorSpace2.getNumComponents();
        this.dstMinVals = new float[numComponents2];
        this.dstMaxVals = new float[numComponents2];
        for (int j = 0; j < numComponents2; ++j) {
            this.dstMinVals[j] = colorSpace2.getMinValue(j);
            this.dstMaxVals[j] = colorSpace2.getMaxValue(j);
        }
    }
    
    static {
        if (ProfileDeferralMgr.deferring) {
            ProfileDeferralMgr.activateProfiles();
        }
    }
}
