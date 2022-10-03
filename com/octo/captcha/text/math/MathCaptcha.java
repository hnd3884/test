package com.octo.captcha.text.math;

import com.octo.captcha.text.TextCaptcha;

public class MathCaptcha extends TextCaptcha
{
    private String response;
    
    MathCaptcha(final String s, final String s2, final String response) {
        super(s, s2);
        this.response = response;
    }
    
    public final Boolean validateResponse(final Object o) {
        return (null != o && o instanceof String) ? this.validateResponse((String)o) : Boolean.FALSE;
    }
    
    private final Boolean validateResponse(final String s) {
        return s.equals(this.response);
    }
}
