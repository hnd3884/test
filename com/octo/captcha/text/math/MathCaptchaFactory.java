package com.octo.captcha.text.math;

import com.octo.captcha.CaptchaQuestionHelper;
import java.util.Locale;
import com.octo.captcha.text.TextCaptcha;
import java.security.SecureRandom;
import java.util.Random;
import com.octo.captcha.text.TextCaptchaFactory;

public class MathCaptchaFactory extends TextCaptchaFactory
{
    private static final String BUNDLE_QUESTION_KEY;
    Random myRamdom;
    
    public MathCaptchaFactory() {
        this.myRamdom = new SecureRandom();
    }
    
    @Override
    public TextCaptcha getTextCaptcha() {
        return this.getTextCaptcha(Locale.getDefault());
    }
    
    @Override
    public TextCaptcha getTextCaptcha(final Locale locale) {
        final int nextInt = this.myRamdom.nextInt(50);
        final int nextInt2 = this.myRamdom.nextInt(50);
        return new MathCaptcha(this.getQuestion(locale), nextInt + "+" + nextInt2, String.valueOf(nextInt + nextInt2));
    }
    
    protected String getQuestion(final Locale locale) {
        return CaptchaQuestionHelper.getQuestion(locale, MathCaptchaFactory.BUNDLE_QUESTION_KEY);
    }
    
    static {
        BUNDLE_QUESTION_KEY = MathCaptcha.class.getName();
    }
}
