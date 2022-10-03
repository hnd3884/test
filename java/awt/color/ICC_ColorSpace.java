package java.awt.color;

import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.CMSManager;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.java2d.cmm.ColorTransform;

public class ICC_ColorSpace extends ColorSpace
{
    static final long serialVersionUID = 3455889114070431483L;
    private ICC_Profile thisProfile;
    private float[] minVal;
    private float[] maxVal;
    private float[] diffMinMax;
    private float[] invDiffMinMax;
    private boolean needScaleInit;
    private transient ColorTransform this2srgb;
    private transient ColorTransform srgb2this;
    private transient ColorTransform this2xyz;
    private transient ColorTransform xyz2this;
    
    public ICC_ColorSpace(final ICC_Profile thisProfile) {
        super(thisProfile.getColorSpaceType(), thisProfile.getNumComponents());
        this.needScaleInit = true;
        final int profileClass = thisProfile.getProfileClass();
        if (profileClass != 0 && profileClass != 1 && profileClass != 2 && profileClass != 4 && profileClass != 6 && profileClass != 5) {
            throw new IllegalArgumentException("Invalid profile type");
        }
        this.thisProfile = thisProfile;
        this.setMinMax();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        if (this.thisProfile == null) {
            this.thisProfile = ICC_Profile.getInstance(1000);
        }
    }
    
    public ICC_Profile getProfile() {
        return this.thisProfile;
    }
    
    @Override
    public float[] toRGB(final float[] array) {
        if (this.this2srgb == null) {
            final ColorTransform[] array2 = new ColorTransform[2];
            final ICC_ColorSpace icc_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
            final PCMM module = CMSManager.getModule();
            array2[0] = module.createTransform(this.thisProfile, -1, 1);
            array2[1] = module.createTransform(icc_ColorSpace.getProfile(), -1, 2);
            this.this2srgb = module.createTransform(array2);
            if (this.needScaleInit) {
                this.setComponentScaling();
            }
        }
        final int numComponents = this.getNumComponents();
        final short[] array3 = new short[numComponents];
        for (int i = 0; i < numComponents; ++i) {
            array3[i] = (short)((array[i] - this.minVal[i]) * this.invDiffMinMax[i] + 0.5f);
        }
        final short[] colorConvert = this.this2srgb.colorConvert(array3, null);
        final float[] array4 = new float[3];
        for (int j = 0; j < 3; ++j) {
            array4[j] = (colorConvert[j] & 0xFFFF) / 65535.0f;
        }
        return array4;
    }
    
    @Override
    public float[] fromRGB(final float[] array) {
        if (this.srgb2this == null) {
            final ColorTransform[] array2 = new ColorTransform[2];
            final ICC_ColorSpace icc_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
            final PCMM module = CMSManager.getModule();
            array2[0] = module.createTransform(icc_ColorSpace.getProfile(), -1, 1);
            array2[1] = module.createTransform(this.thisProfile, -1, 2);
            this.srgb2this = module.createTransform(array2);
            if (this.needScaleInit) {
                this.setComponentScaling();
            }
        }
        final short[] array3 = new short[3];
        for (int i = 0; i < 3; ++i) {
            array3[i] = (short)(array[i] * 65535.0f + 0.5f);
        }
        final short[] colorConvert = this.srgb2this.colorConvert(array3, null);
        final int numComponents = this.getNumComponents();
        final float[] array4 = new float[numComponents];
        for (int j = 0; j < numComponents; ++j) {
            array4[j] = (colorConvert[j] & 0xFFFF) / 65535.0f * this.diffMinMax[j] + this.minVal[j];
        }
        return array4;
    }
    
    @Override
    public float[] toCIEXYZ(final float[] array) {
        if (this.this2xyz == null) {
            final ColorTransform[] array2 = new ColorTransform[2];
            final ICC_ColorSpace icc_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1001);
            final PCMM module = CMSManager.getModule();
            try {
                array2[0] = module.createTransform(this.thisProfile, 1, 1);
            }
            catch (final CMMException ex) {
                array2[0] = module.createTransform(this.thisProfile, -1, 1);
            }
            array2[1] = module.createTransform(icc_ColorSpace.getProfile(), -1, 2);
            this.this2xyz = module.createTransform(array2);
            if (this.needScaleInit) {
                this.setComponentScaling();
            }
        }
        final int numComponents = this.getNumComponents();
        final short[] array3 = new short[numComponents];
        for (int i = 0; i < numComponents; ++i) {
            array3[i] = (short)((array[i] - this.minVal[i]) * this.invDiffMinMax[i] + 0.5f);
        }
        final short[] colorConvert = this.this2xyz.colorConvert(array3, null);
        final float n = 1.9999695f;
        final float[] array4 = new float[3];
        for (int j = 0; j < 3; ++j) {
            array4[j] = (colorConvert[j] & 0xFFFF) / 65535.0f * n;
        }
        return array4;
    }
    
    @Override
    public float[] fromCIEXYZ(final float[] array) {
        if (this.xyz2this == null) {
            final ColorTransform[] array2 = new ColorTransform[2];
            final ICC_ColorSpace icc_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1001);
            final PCMM module = CMSManager.getModule();
            array2[0] = module.createTransform(icc_ColorSpace.getProfile(), -1, 1);
            try {
                array2[1] = module.createTransform(this.thisProfile, 1, 2);
            }
            catch (final CMMException ex) {
                array2[1] = CMSManager.getModule().createTransform(this.thisProfile, -1, 2);
            }
            this.xyz2this = module.createTransform(array2);
            if (this.needScaleInit) {
                this.setComponentScaling();
            }
        }
        final short[] array3 = new short[3];
        final float n = 65535.0f / 1.9999695f;
        for (int i = 0; i < 3; ++i) {
            array3[i] = (short)(array[i] * n + 0.5f);
        }
        final short[] colorConvert = this.xyz2this.colorConvert(array3, null);
        final int numComponents = this.getNumComponents();
        final float[] array4 = new float[numComponents];
        for (int j = 0; j < numComponents; ++j) {
            array4[j] = (colorConvert[j] & 0xFFFF) / 65535.0f * this.diffMinMax[j] + this.minVal[j];
        }
        return array4;
    }
    
    @Override
    public float getMinValue(final int n) {
        if (n < 0 || n > this.getNumComponents() - 1) {
            throw new IllegalArgumentException("Component index out of range: + component");
        }
        return this.minVal[n];
    }
    
    @Override
    public float getMaxValue(final int n) {
        if (n < 0 || n > this.getNumComponents() - 1) {
            throw new IllegalArgumentException("Component index out of range: + component");
        }
        return this.maxVal[n];
    }
    
    private void setMinMax() {
        final int numComponents = this.getNumComponents();
        final int type = this.getType();
        this.minVal = new float[numComponents];
        this.maxVal = new float[numComponents];
        if (type == 1) {
            this.minVal[0] = 0.0f;
            this.maxVal[0] = 100.0f;
            this.minVal[1] = -128.0f;
            this.maxVal[1] = 127.0f;
            this.minVal[2] = -128.0f;
            this.maxVal[2] = 127.0f;
        }
        else if (type == 0) {
            final float[] minVal = this.minVal;
            final int n = 0;
            final float[] minVal2 = this.minVal;
            final int n2 = 1;
            final float[] minVal3 = this.minVal;
            final int n3 = 2;
            final float n4 = 0.0f;
            minVal3[n3] = n4;
            minVal[n] = (minVal2[n2] = n4);
            final float[] maxVal = this.maxVal;
            final int n5 = 0;
            final float[] maxVal2 = this.maxVal;
            final int n6 = 1;
            final float[] maxVal3 = this.maxVal;
            final int n7 = 2;
            final float n8 = 1.9999695f;
            maxVal3[n7] = n8;
            maxVal[n5] = (maxVal2[n6] = n8);
        }
        else {
            for (int i = 0; i < numComponents; ++i) {
                this.minVal[i] = 0.0f;
                this.maxVal[i] = 1.0f;
            }
        }
    }
    
    private void setComponentScaling() {
        final int numComponents = this.getNumComponents();
        this.diffMinMax = new float[numComponents];
        this.invDiffMinMax = new float[numComponents];
        for (int i = 0; i < numComponents; ++i) {
            this.minVal[i] = this.getMinValue(i);
            this.maxVal[i] = this.getMaxValue(i);
            this.diffMinMax[i] = this.maxVal[i] - this.minVal[i];
            this.invDiffMinMax[i] = 65535.0f / this.diffMinMax[i];
        }
        this.needScaleInit = false;
    }
}
