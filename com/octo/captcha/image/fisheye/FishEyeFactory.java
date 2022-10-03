package com.octo.captcha.image.fisheye;

import java.awt.Point;
import com.octo.captcha.CaptchaQuestionHelper;
import java.awt.image.ImageObserver;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Locale;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.CaptchaException;
import java.security.SecureRandom;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import java.util.Random;
import com.octo.captcha.image.ImageCaptchaFactory;

public class FishEyeFactory extends ImageCaptchaFactory
{
    public static final String BUNDLE_QUESTION_KEY;
    private Random myRandom;
    private BackgroundGenerator generator;
    private ImageDeformation deformation;
    private Integer tolerance;
    private Integer scale;
    
    public FishEyeFactory(final BackgroundGenerator generator, final ImageDeformation deformation, final Integer scale, final Integer tolerance) {
        this.myRandom = new SecureRandom();
        if (generator == null) {
            throw new CaptchaException("Invalid configuration for a FishEyeFactory : BackgroundGenerator can't be null");
        }
        if (deformation == null) {
            throw new CaptchaException("Invalid configuration for a FishEyeFactory : ImageDeformation can't be null");
        }
        this.deformation = deformation;
        this.generator = generator;
        if (scale == null || scale < 1 || scale > 99) {
            throw new CaptchaException("Invalid configuration for a FishEyeFactory : scale can't be null, and must be between 1 and 99");
        }
        this.scale = scale;
        if (tolerance == null || tolerance < 0) {
            throw new CaptchaException("Invalid configuration for a FishEyeFactory : tolerance can't be null, and must be positive");
        }
        this.tolerance = tolerance;
    }
    
    @Override
    public ImageCaptcha getImageCaptcha() {
        return this.getImageCaptcha(Locale.getDefault());
    }
    
    @Override
    public ImageCaptcha getImageCaptcha(final Locale locale) {
        final BufferedImage background = this.generator.getBackground();
        final BufferedImage bufferedImage = new BufferedImage(background.getWidth(), background.getHeight(), background.getType());
        bufferedImage.getGraphics().drawImage(background, 0, 0, null, null);
        final int width = background.getWidth();
        final int height = background.getHeight();
        final int max = Math.max(width * this.scale / 100, 1);
        final int max2 = Math.max(height * this.scale / 100, 1);
        final int nextInt = this.myRandom.nextInt(width - max);
        final int nextInt2 = this.myRandom.nextInt(height - max2);
        bufferedImage.getGraphics().drawImage(this.deformation.deformImage(bufferedImage.getSubimage(nextInt, nextInt2, max, max2)), nextInt, nextInt2, Color.white, null);
        bufferedImage.getGraphics().dispose();
        return new FishEye(CaptchaQuestionHelper.getQuestion(locale, FishEyeFactory.BUNDLE_QUESTION_KEY), bufferedImage, new Point(nextInt + max / 2, nextInt2 + max2 / 2), this.tolerance);
    }
    
    static {
        BUNDLE_QUESTION_KEY = FishEye.class.getName();
    }
}
