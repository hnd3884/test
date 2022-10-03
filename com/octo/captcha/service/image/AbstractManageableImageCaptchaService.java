package com.octo.captcha.service.image;

import java.awt.image.ImageObserver;
import java.awt.Image;
import com.octo.captcha.Captcha;
import java.util.Locale;
import com.octo.captcha.service.CaptchaServiceException;
import java.awt.image.BufferedImage;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.AbstractManageableCaptchaService;

public abstract class AbstractManageableImageCaptchaService extends AbstractManageableCaptchaService implements ImageCaptchaService
{
    protected AbstractManageableImageCaptchaService(final CaptchaStore captchaStore, final CaptchaEngine captchaEngine, final int n, final int n2, final int n3) {
        super(captchaStore, captchaEngine, n, n2, n3);
    }
    
    public BufferedImage getImageChallengeForID(final String s) throws CaptchaServiceException {
        return (BufferedImage)this.getChallengeForID(s);
    }
    
    public BufferedImage getImageChallengeForID(final String s, final Locale locale) throws CaptchaServiceException {
        return (BufferedImage)this.getChallengeForID(s, locale);
    }
    
    @Override
    protected Object getChallengeClone(final Captcha captcha) {
        final BufferedImage bufferedImage = (BufferedImage)captcha.getChallenge();
        final BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        bufferedImage2.getGraphics().drawImage(bufferedImage, 0, 0, bufferedImage2.getWidth(), bufferedImage2.getHeight(), null);
        bufferedImage2.getGraphics().dispose();
        return bufferedImage2;
    }
}
