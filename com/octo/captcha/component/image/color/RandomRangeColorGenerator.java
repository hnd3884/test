package com.octo.captcha.component.image.color;

import java.awt.Color;
import com.octo.captcha.CaptchaException;
import java.security.SecureRandom;
import java.util.Random;

public class RandomRangeColorGenerator implements ColorGenerator
{
    public static final int MIN_COLOR_COMPONENT_VALUE = 0;
    public static final int MAX_COLOR_COMPONENT_VALUE = 255;
    public static final int TRANSPARENT_ALPHA_COMPONENT_VALUE = 0;
    public static final int OPAQUE_ALPHA_COMPONENT_VALUE = 255;
    private int[] redComponentRange;
    private int[] greenComponentRange;
    private int[] blueComponentRange;
    private int[] alphaComponentRange;
    private Random random;
    
    public RandomRangeColorGenerator(final int[] redComponentRange, final int[] greenComponentRange, final int[] blueComponentRange, final int[] alphaComponentRange) {
        this.random = new SecureRandom();
        this.validateColorComponentRange(redComponentRange);
        this.setRedComponentRange(redComponentRange);
        this.validateColorComponentRange(greenComponentRange);
        this.setGreenComponentRange(greenComponentRange);
        this.validateColorComponentRange(blueComponentRange);
        this.setBlueComponentRange(blueComponentRange);
        this.validateColorComponentRange(alphaComponentRange);
        this.setAlphaComponentRange(alphaComponentRange);
    }
    
    public RandomRangeColorGenerator(final int[] array, final int[] array2, final int[] array3) {
        this(array, array2, array3, new int[] { 255, 255 });
    }
    
    private void validateColorComponentRange(final int[] array) throws CaptchaException {
        if (array.length != 2) {
            throw new CaptchaException("Range length must be 2");
        }
        if (array[0] > array[1]) {
            throw new CaptchaException("Start value of color component range is greater than end value");
        }
        this.validateColorComponentValue(array[0]);
        this.validateColorComponentValue(array[1]);
    }
    
    private void validateColorComponentValue(final int n) throws CaptchaException {
        if (n < 0 || n > 255) {
            throw new CaptchaException("Color component value is always between 0 and 255");
        }
    }
    
    public Color getNextColor() {
        return new Color(this.getRandomInRange(this.redComponentRange[0], this.redComponentRange[1]), this.getRandomInRange(this.greenComponentRange[0], this.greenComponentRange[1]), this.getRandomInRange(this.blueComponentRange[0], this.blueComponentRange[1]), this.getRandomInRange(this.alphaComponentRange[0], this.alphaComponentRange[1]));
    }
    
    private int getRandomInRange(final int n, final int n2) {
        if (n == n2) {
            return n;
        }
        return this.random.nextInt(n2 - n) + n;
    }
    
    private void setAlphaComponentRange(final int[] alphaComponentRange) {
        this.alphaComponentRange = alphaComponentRange;
    }
    
    private void setBlueComponentRange(final int[] blueComponentRange) {
        this.blueComponentRange = blueComponentRange;
    }
    
    private void setGreenComponentRange(final int[] greenComponentRange) {
        this.greenComponentRange = greenComponentRange;
    }
    
    private void setRedComponentRange(final int[] redComponentRange) {
        this.redComponentRange = redComponentRange;
    }
}
