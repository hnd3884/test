package com.octo.captcha.component.image.fontgenerator;

public abstract class AbstractFontGenerator implements FontGenerator
{
    private int minFontSize;
    private int maxFontSize;
    
    AbstractFontGenerator(final Integer n, final Integer n2) {
        this.minFontSize = 10;
        this.maxFontSize = 14;
        this.minFontSize = ((n != null) ? n : this.minFontSize);
        this.maxFontSize = ((n2 != null && n2 >= this.minFontSize) ? n2 : Math.max(this.maxFontSize, this.minFontSize + 1));
    }
    
    public int getMinFontSize() {
        return this.minFontSize;
    }
    
    public int getMaxFontSize() {
        return this.maxFontSize;
    }
}
