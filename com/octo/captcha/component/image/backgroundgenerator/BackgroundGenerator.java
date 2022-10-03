package com.octo.captcha.component.image.backgroundgenerator;

import java.awt.image.BufferedImage;

public interface BackgroundGenerator
{
    int getImageHeight();
    
    int getImageWidth();
    
    BufferedImage getBackground();
}
