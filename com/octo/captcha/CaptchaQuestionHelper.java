package com.octo.captcha;

import java.util.ResourceBundle;
import java.util.Locale;

public final class CaptchaQuestionHelper
{
    public static final String BUNDLE_NAME;
    
    private CaptchaQuestionHelper() {
    }
    
    public static String getQuestion(final Locale locale, final String s) {
        return ResourceBundle.getBundle(CaptchaQuestionHelper.BUNDLE_NAME, locale).getString(s);
    }
    
    static {
        BUNDLE_NAME = CaptchaQuestionHelper.class.getName();
    }
}
