package com.octo.captcha.image.gimpy;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import com.octo.captcha.image.ImageCaptcha;

public class Gimpy extends ImageCaptcha implements Serializable
{
    private String response;
    private boolean caseSensitive;
    
    Gimpy(final String s, final BufferedImage bufferedImage, final String response, final boolean caseSensitive) {
        super(s, bufferedImage);
        this.caseSensitive = true;
        this.response = response;
        this.caseSensitive = caseSensitive;
    }
    
    Gimpy(final String s, final BufferedImage bufferedImage, final String s2) {
        this(s, bufferedImage, s2, true);
    }
    
    public final Boolean validateResponse(final Object o) {
        return (null != o && o instanceof String) ? this.validateResponse((String)o) : Boolean.FALSE;
    }
    
    private final Boolean validateResponse(final String s) {
        return this.caseSensitive ? s.equals(this.response) : s.equalsIgnoreCase(this.response);
    }
}
