package com.octo.captcha.service.image;

import java.util.Locale;
import com.octo.captcha.service.CaptchaServiceException;
import java.awt.image.BufferedImage;
import com.octo.captcha.service.CaptchaService;

public interface ImageCaptchaService extends CaptchaService
{
    BufferedImage getImageChallengeForID(final String p0) throws CaptchaServiceException;
    
    BufferedImage getImageChallengeForID(final String p0, final Locale p1) throws CaptchaServiceException;
}
