package com.octo.captcha.component.image.backgroundgenerator;

import java.security.SecureRandom;
import java.util.Random;

public abstract class AbstractBackgroundGenerator implements BackgroundGenerator
{
    private int height;
    private int width;
    Random myRandom;
    
    AbstractBackgroundGenerator(final Integer n, final Integer n2) {
        this.height = 100;
        this.width = 200;
        this.myRandom = new SecureRandom();
        this.width = ((n != null) ? n : this.width);
        this.height = ((n2 != null) ? n2 : this.height);
    }
    
    public int getImageHeight() {
        return this.height;
    }
    
    public int getImageWidth() {
        return this.width;
    }
}
