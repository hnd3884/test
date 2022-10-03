package com.octo.captcha.component.image.fontgenerator;

import java.awt.Font;

public interface FontGenerator
{
    Font getFont();
    
    int getMinFontSize();
    
    int getMaxFontSize();
}
