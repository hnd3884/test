package com.octo.captcha.component.image.color;

import com.octo.captcha.CaptchaException;
import java.security.SecureRandom;
import java.util.Random;
import java.awt.Color;

public class RandomListColorGenerator implements ColorGenerator
{
    private Color[] colorsList;
    private Random random;
    
    public RandomListColorGenerator(final Color[] colorsList) {
        this.colorsList = null;
        this.random = new SecureRandom();
        if (colorsList == null) {
            throw new CaptchaException("Color list cannot be null");
        }
        for (int i = 0; i < colorsList.length; ++i) {
            if (colorsList[i] == null) {
                throw new CaptchaException("One or several color is null");
            }
        }
        this.colorsList = colorsList;
    }
    
    public Color getNextColor() {
        return this.colorsList[this.random.nextInt(this.colorsList.length)];
    }
}
