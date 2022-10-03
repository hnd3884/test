package com.octo.captcha.component.image.color;

import com.octo.captcha.CaptchaException;
import java.awt.Color;

public class SingleColorGenerator implements ColorGenerator
{
    public Color color;
    
    public SingleColorGenerator(final Color color) {
        this.color = null;
        if (color == null) {
            throw new CaptchaException("Color is null");
        }
        this.color = color;
    }
    
    public Color getNextColor() {
        return this.color;
    }
}
