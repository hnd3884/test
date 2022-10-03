package sun.java2d.cmm.lcms;

import sun.java2d.cmm.ProfileDeferralMgr;
import java.awt.image.SampleModel;
import java.awt.image.Raster;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.color.CMMException;
import java.awt.image.BufferedImage;
import java.awt.color.ICC_Profile;
import sun.java2d.cmm.ColorTransform;

public class LCMSTransform implements ColorTransform
{
    long ID;
    private int inFormatter;
    private boolean isInIntPacked;
    private int outFormatter;
    private boolean isOutIntPacked;
    ICC_Profile[] profiles;
    LCMSProfile[] lcmsProfiles;
    int renderType;
    int transformType;
    private int numInComponents;
    private int numOutComponents;
    private Object disposerReferent;
    
    public LCMSTransform(final ICC_Profile icc_Profile, final int n, final int transformType) {
        this.inFormatter = 0;
        this.isInIntPacked = false;
        this.outFormatter = 0;
        this.isOutIntPacked = false;
        this.numInComponents = -1;
        this.numOutComponents = -1;
        this.disposerReferent = new Object();
        (this.profiles = new ICC_Profile[1])[0] = icc_Profile;
        (this.lcmsProfiles = new LCMSProfile[1])[0] = LCMS.getProfileID(icc_Profile);
        this.renderType = ((n == -1) ? 0 : n);
        this.transformType = transformType;
        this.numInComponents = this.profiles[0].getNumComponents();
        this.numOutComponents = this.profiles[this.profiles.length - 1].getNumComponents();
    }
    
    public LCMSTransform(final ColorTransform[] array) {
        this.inFormatter = 0;
        this.isInIntPacked = false;
        this.outFormatter = 0;
        this.isOutIntPacked = false;
        this.numInComponents = -1;
        this.numOutComponents = -1;
        this.disposerReferent = new Object();
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            n += ((LCMSTransform)array[i]).profiles.length;
        }
        this.profiles = new ICC_Profile[n];
        this.lcmsProfiles = new LCMSProfile[n];
        int n2 = 0;
        for (int j = 0; j < array.length; ++j) {
            final LCMSTransform lcmsTransform = (LCMSTransform)array[j];
            System.arraycopy(lcmsTransform.profiles, 0, this.profiles, n2, lcmsTransform.profiles.length);
            System.arraycopy(lcmsTransform.lcmsProfiles, 0, this.lcmsProfiles, n2, lcmsTransform.lcmsProfiles.length);
            n2 += lcmsTransform.profiles.length;
        }
        this.renderType = ((LCMSTransform)array[0]).renderType;
        this.numInComponents = this.profiles[0].getNumComponents();
        this.numOutComponents = this.profiles[this.profiles.length - 1].getNumComponents();
    }
    
    @Override
    public int getNumInComponents() {
        return this.numInComponents;
    }
    
    @Override
    public int getNumOutComponents() {
        return this.numOutComponents;
    }
    
    private synchronized void doTransform(final LCMSImageLayout lcmsImageLayout, final LCMSImageLayout lcmsImageLayout2) {
        if (this.ID == 0L || this.inFormatter != lcmsImageLayout.pixelType || this.isInIntPacked != lcmsImageLayout.isIntPacked || this.outFormatter != lcmsImageLayout2.pixelType || this.isOutIntPacked != lcmsImageLayout2.isIntPacked) {
            if (this.ID != 0L) {
                this.disposerReferent = new Object();
            }
            this.inFormatter = lcmsImageLayout.pixelType;
            this.isInIntPacked = lcmsImageLayout.isIntPacked;
            this.outFormatter = lcmsImageLayout2.pixelType;
            this.isOutIntPacked = lcmsImageLayout2.isIntPacked;
            this.ID = LCMS.createTransform(this.lcmsProfiles, this.renderType, this.inFormatter, this.isInIntPacked, this.outFormatter, this.isOutIntPacked, this.disposerReferent);
        }
        LCMS.colorConvert(this, lcmsImageLayout, lcmsImageLayout2);
    }
    
    @Override
    public void colorConvert(final BufferedImage bufferedImage, final BufferedImage bufferedImage2) {
        try {
            if (!bufferedImage2.getColorModel().hasAlpha()) {
                final LCMSImageLayout imageLayout = LCMSImageLayout.createImageLayout(bufferedImage2);
                if (imageLayout != null) {
                    final LCMSImageLayout imageLayout2 = LCMSImageLayout.createImageLayout(bufferedImage);
                    if (imageLayout2 != null) {
                        this.doTransform(imageLayout2, imageLayout);
                        return;
                    }
                }
            }
        }
        catch (final LCMSImageLayout.ImageLayoutException ex) {
            throw new CMMException("Unable to convert images");
        }
        final WritableRaster raster = bufferedImage.getRaster();
        final WritableRaster raster2 = bufferedImage2.getRaster();
        final ColorModel colorModel = bufferedImage.getColorModel();
        final ColorModel colorModel2 = bufferedImage2.getColorModel();
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        final int numColorComponents = colorModel.getNumColorComponents();
        final int numColorComponents2 = colorModel2.getNumColorComponents();
        int n = 8;
        float n2 = 255.0f;
        for (int i = 0; i < numColorComponents; ++i) {
            if (colorModel.getComponentSize(i) > 8) {
                n = 16;
                n2 = 65535.0f;
            }
        }
        for (int j = 0; j < numColorComponents2; ++j) {
            if (colorModel2.getComponentSize(j) > 8) {
                n = 16;
                n2 = 65535.0f;
            }
        }
        final float[] array = new float[numColorComponents];
        final float[] array2 = new float[numColorComponents];
        final ColorSpace colorSpace = colorModel.getColorSpace();
        for (int k = 0; k < numColorComponents; ++k) {
            array[k] = colorSpace.getMinValue(k);
            array2[k] = n2 / (colorSpace.getMaxValue(k) - array[k]);
        }
        final ColorSpace colorSpace2 = colorModel2.getColorSpace();
        final float[] array3 = new float[numColorComponents2];
        final float[] array4 = new float[numColorComponents2];
        for (int l = 0; l < numColorComponents2; ++l) {
            array3[l] = colorSpace2.getMinValue(l);
            array4[l] = (colorSpace2.getMaxValue(l) - array3[l]) / n2;
        }
        final boolean hasAlpha = colorModel2.hasAlpha();
        final boolean b = colorModel.hasAlpha() && hasAlpha;
        float[] array5;
        if (hasAlpha) {
            array5 = new float[numColorComponents2 + 1];
        }
        else {
            array5 = new float[numColorComponents2];
        }
        if (n == 8) {
            final byte[] array6 = new byte[width * numColorComponents];
            final byte[] array7 = new byte[width * numColorComponents2];
            float[] array8 = null;
            if (b) {
                array8 = new float[width];
            }
            LCMSImageLayout lcmsImageLayout;
            LCMSImageLayout lcmsImageLayout2;
            try {
                lcmsImageLayout = new LCMSImageLayout(array6, array6.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumInComponents());
                lcmsImageLayout2 = new LCMSImageLayout(array7, array7.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumOutComponents());
            }
            catch (final LCMSImageLayout.ImageLayoutException ex2) {
                throw new CMMException("Unable to convert images");
            }
            for (int n3 = 0; n3 < height; ++n3) {
                Object dataElements = null;
                float[] normalizedComponents = null;
                int n4 = 0;
                for (int n5 = 0; n5 < width; ++n5) {
                    dataElements = raster.getDataElements(n5, n3, dataElements);
                    normalizedComponents = colorModel.getNormalizedComponents(dataElements, normalizedComponents, 0);
                    for (int n6 = 0; n6 < numColorComponents; ++n6) {
                        array6[n4++] = (byte)((normalizedComponents[n6] - array[n6]) * array2[n6] + 0.5f);
                    }
                    if (b) {
                        array8[n5] = normalizedComponents[numColorComponents];
                    }
                }
                this.doTransform(lcmsImageLayout, lcmsImageLayout2);
                Object dataElements2 = null;
                int n7 = 0;
                for (int n8 = 0; n8 < width; ++n8) {
                    for (int n9 = 0; n9 < numColorComponents2; ++n9) {
                        array5[n9] = (array7[n7++] & 0xFF) * array4[n9] + array3[n9];
                    }
                    if (b) {
                        array5[numColorComponents2] = array8[n8];
                    }
                    else if (hasAlpha) {
                        array5[numColorComponents2] = 1.0f;
                    }
                    dataElements2 = colorModel2.getDataElements(array5, 0, dataElements2);
                    raster2.setDataElements(n8, n3, dataElements2);
                }
            }
        }
        else {
            final short[] array9 = new short[width * numColorComponents];
            final short[] array10 = new short[width * numColorComponents2];
            float[] array11 = null;
            if (b) {
                array11 = new float[width];
            }
            LCMSImageLayout lcmsImageLayout3;
            LCMSImageLayout lcmsImageLayout4;
            try {
                lcmsImageLayout3 = new LCMSImageLayout(array9, array9.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumInComponents() * 2);
                lcmsImageLayout4 = new LCMSImageLayout(array10, array10.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumOutComponents() * 2);
            }
            catch (final LCMSImageLayout.ImageLayoutException ex3) {
                throw new CMMException("Unable to convert images");
            }
            for (int n10 = 0; n10 < height; ++n10) {
                Object dataElements3 = null;
                float[] normalizedComponents2 = null;
                int n11 = 0;
                for (int n12 = 0; n12 < width; ++n12) {
                    dataElements3 = raster.getDataElements(n12, n10, dataElements3);
                    normalizedComponents2 = colorModel.getNormalizedComponents(dataElements3, normalizedComponents2, 0);
                    for (int n13 = 0; n13 < numColorComponents; ++n13) {
                        array9[n11++] = (short)((normalizedComponents2[n13] - array[n13]) * array2[n13] + 0.5f);
                    }
                    if (b) {
                        array11[n12] = normalizedComponents2[numColorComponents];
                    }
                }
                this.doTransform(lcmsImageLayout3, lcmsImageLayout4);
                Object dataElements4 = null;
                int n14 = 0;
                for (int n15 = 0; n15 < width; ++n15) {
                    for (int n16 = 0; n16 < numColorComponents2; ++n16) {
                        array5[n16] = (array10[n14++] & 0xFFFF) * array4[n16] + array3[n16];
                    }
                    if (b) {
                        array5[numColorComponents2] = array11[n15];
                    }
                    else if (hasAlpha) {
                        array5[numColorComponents2] = 1.0f;
                    }
                    dataElements4 = colorModel2.getDataElements(array5, 0, dataElements4);
                    raster2.setDataElements(n15, n10, dataElements4);
                }
            }
        }
    }
    
    @Override
    public void colorConvert(final Raster raster, final WritableRaster writableRaster, final float[] array, final float[] array2, final float[] array3, final float[] array4) {
        final SampleModel sampleModel = raster.getSampleModel();
        final SampleModel sampleModel2 = writableRaster.getSampleModel();
        final int transferType = raster.getTransferType();
        final int transferType2 = writableRaster.getTransferType();
        final boolean b = transferType == 4 || transferType == 5;
        final boolean b2 = transferType2 == 4 || transferType2 == 5;
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int numBands = raster.getNumBands();
        final int numBands2 = writableRaster.getNumBands();
        final float[] array5 = new float[numBands];
        final float[] array6 = new float[numBands2];
        final float[] array7 = new float[numBands];
        final float[] array8 = new float[numBands2];
        for (int i = 0; i < numBands; ++i) {
            if (b) {
                array5[i] = 65535.0f / (array2[i] - array[i]);
                array7[i] = array[i];
            }
            else {
                if (transferType == 2) {
                    array5[i] = 2.0000305f;
                }
                else {
                    array5[i] = 65535.0f / ((1 << sampleModel.getSampleSize(i)) - 1);
                }
                array7[i] = 0.0f;
            }
        }
        for (int j = 0; j < numBands2; ++j) {
            if (b2) {
                array6[j] = (array4[j] - array3[j]) / 65535.0f;
                array8[j] = array3[j];
            }
            else {
                if (transferType2 == 2) {
                    array6[j] = 0.49999237f;
                }
                else {
                    array6[j] = ((1 << sampleModel2.getSampleSize(j)) - 1) / 65535.0f;
                }
                array8[j] = 0.0f;
            }
        }
        int minY = raster.getMinY();
        int minY2 = writableRaster.getMinY();
        final short[] array9 = new short[width * numBands];
        final short[] array10 = new short[width * numBands2];
        LCMSImageLayout lcmsImageLayout;
        LCMSImageLayout lcmsImageLayout2;
        try {
            lcmsImageLayout = new LCMSImageLayout(array9, array9.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumInComponents() * 2);
            lcmsImageLayout2 = new LCMSImageLayout(array10, array10.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumOutComponents() * 2);
        }
        catch (final LCMSImageLayout.ImageLayoutException ex) {
            throw new CMMException("Unable to convert rasters");
        }
        for (int k = 0; k < height; ++k, ++minY, ++minY2) {
            int minX = raster.getMinX();
            int n = 0;
            for (int l = 0; l < width; ++l, ++minX) {
                for (int n2 = 0; n2 < numBands; ++n2) {
                    array9[n++] = (short)((raster.getSampleFloat(minX, minY, n2) - array7[n2]) * array5[n2] + 0.5f);
                }
            }
            this.doTransform(lcmsImageLayout, lcmsImageLayout2);
            int minX2 = writableRaster.getMinX();
            int n3 = 0;
            for (int n4 = 0; n4 < width; ++n4, ++minX2) {
                for (int n5 = 0; n5 < numBands2; ++n5) {
                    writableRaster.setSample(minX2, minY2, n5, (array10[n3++] & 0xFFFF) * array6[n5] + array8[n5]);
                }
            }
        }
    }
    
    @Override
    public void colorConvert(final Raster raster, final WritableRaster writableRaster) {
        final LCMSImageLayout imageLayout = LCMSImageLayout.createImageLayout(writableRaster);
        if (imageLayout != null) {
            final LCMSImageLayout imageLayout2 = LCMSImageLayout.createImageLayout(raster);
            if (imageLayout2 != null) {
                this.doTransform(imageLayout2, imageLayout);
                return;
            }
        }
        final SampleModel sampleModel = raster.getSampleModel();
        final SampleModel sampleModel2 = writableRaster.getSampleModel();
        final int transferType = raster.getTransferType();
        final int transferType2 = writableRaster.getTransferType();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int numBands = raster.getNumBands();
        final int numBands2 = writableRaster.getNumBands();
        int n = 8;
        float n2 = 255.0f;
        for (int i = 0; i < numBands; ++i) {
            if (sampleModel.getSampleSize(i) > 8) {
                n = 16;
                n2 = 65535.0f;
            }
        }
        for (int j = 0; j < numBands2; ++j) {
            if (sampleModel2.getSampleSize(j) > 8) {
                n = 16;
                n2 = 65535.0f;
            }
        }
        final float[] array = new float[numBands];
        final float[] array2 = new float[numBands2];
        for (int k = 0; k < numBands; ++k) {
            if (transferType == 2) {
                array[k] = n2 / 32767.0f;
            }
            else {
                array[k] = n2 / ((1 << sampleModel.getSampleSize(k)) - 1);
            }
        }
        for (int l = 0; l < numBands2; ++l) {
            if (transferType2 == 2) {
                array2[l] = 32767.0f / n2;
            }
            else {
                array2[l] = ((1 << sampleModel2.getSampleSize(l)) - 1) / n2;
            }
        }
        int minY = raster.getMinY();
        int minY2 = writableRaster.getMinY();
        if (n == 8) {
            final byte[] array3 = new byte[width * numBands];
            final byte[] array4 = new byte[width * numBands2];
            LCMSImageLayout lcmsImageLayout;
            LCMSImageLayout lcmsImageLayout2;
            try {
                lcmsImageLayout = new LCMSImageLayout(array3, array3.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumInComponents());
                lcmsImageLayout2 = new LCMSImageLayout(array4, array4.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumOutComponents());
            }
            catch (final LCMSImageLayout.ImageLayoutException ex) {
                throw new CMMException("Unable to convert rasters");
            }
            for (int n3 = 0; n3 < height; ++n3, ++minY, ++minY2) {
                int minX = raster.getMinX();
                int n4 = 0;
                for (int n5 = 0; n5 < width; ++n5, ++minX) {
                    for (int n6 = 0; n6 < numBands; ++n6) {
                        array3[n4++] = (byte)(raster.getSample(minX, minY, n6) * array[n6] + 0.5f);
                    }
                }
                this.doTransform(lcmsImageLayout, lcmsImageLayout2);
                int minX2 = writableRaster.getMinX();
                int n7 = 0;
                for (int n8 = 0; n8 < width; ++n8, ++minX2) {
                    for (int n9 = 0; n9 < numBands2; ++n9) {
                        writableRaster.setSample(minX2, minY2, n9, (int)((array4[n7++] & 0xFF) * array2[n9] + 0.5f));
                    }
                }
            }
        }
        else {
            final short[] array5 = new short[width * numBands];
            final short[] array6 = new short[width * numBands2];
            LCMSImageLayout lcmsImageLayout3;
            LCMSImageLayout lcmsImageLayout4;
            try {
                lcmsImageLayout3 = new LCMSImageLayout(array5, array5.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumInComponents() * 2);
                lcmsImageLayout4 = new LCMSImageLayout(array6, array6.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumOutComponents() * 2);
            }
            catch (final LCMSImageLayout.ImageLayoutException ex2) {
                throw new CMMException("Unable to convert rasters");
            }
            for (int n10 = 0; n10 < height; ++n10, ++minY, ++minY2) {
                int minX3 = raster.getMinX();
                int n11 = 0;
                for (int n12 = 0; n12 < width; ++n12, ++minX3) {
                    for (int n13 = 0; n13 < numBands; ++n13) {
                        array5[n11++] = (short)(raster.getSample(minX3, minY, n13) * array[n13] + 0.5f);
                    }
                }
                this.doTransform(lcmsImageLayout3, lcmsImageLayout4);
                int minX4 = writableRaster.getMinX();
                int n14 = 0;
                for (int n15 = 0; n15 < width; ++n15, ++minX4) {
                    for (int n16 = 0; n16 < numBands2; ++n16) {
                        writableRaster.setSample(minX4, minY2, n16, (int)((array6[n14++] & 0xFFFF) * array2[n16] + 0.5f));
                    }
                }
            }
        }
    }
    
    @Override
    public short[] colorConvert(final short[] array, short[] array2) {
        if (array2 == null) {
            array2 = new short[array.length / this.getNumInComponents() * this.getNumOutComponents()];
        }
        try {
            this.doTransform(new LCMSImageLayout(array, array.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumInComponents() * 2), new LCMSImageLayout(array2, array2.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), this.getNumOutComponents() * 2));
            return array2;
        }
        catch (final LCMSImageLayout.ImageLayoutException ex) {
            throw new CMMException("Unable to convert data");
        }
    }
    
    @Override
    public byte[] colorConvert(final byte[] array, byte[] array2) {
        if (array2 == null) {
            array2 = new byte[array.length / this.getNumInComponents() * this.getNumOutComponents()];
        }
        try {
            this.doTransform(new LCMSImageLayout(array, array.length / this.getNumInComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumInComponents()), new LCMSImageLayout(array2, array2.length / this.getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(this.getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), this.getNumOutComponents()));
            return array2;
        }
        catch (final LCMSImageLayout.ImageLayoutException ex) {
            throw new CMMException("Unable to convert data");
        }
    }
    
    static {
        if (ProfileDeferralMgr.deferring) {
            ProfileDeferralMgr.activateProfiles();
        }
    }
}
