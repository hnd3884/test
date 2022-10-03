package com.octo.captcha.image.gimpy;

import java.awt.image.BufferedImage;
import com.octo.captcha.CaptchaQuestionHelper;
import java.util.Locale;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.CaptchaException;
import java.security.SecureRandom;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import java.util.Random;
import com.octo.captcha.image.ImageCaptchaFactory;

public class GimpyFactory extends ImageCaptchaFactory
{
    private Random myRandom;
    private WordToImage wordToImage;
    private WordGenerator wordGenerator;
    private boolean caseSensitive;
    public static final String BUNDLE_QUESTION_KEY;
    
    public GimpyFactory(final WordGenerator wordGenerator, final WordToImage wordToImage) {
        this(wordGenerator, wordToImage, true);
    }
    
    public GimpyFactory(final WordGenerator wordGenerator, final WordToImage wordToImage, final boolean caseSensitive) {
        this.myRandom = new SecureRandom();
        this.caseSensitive = true;
        if (wordToImage == null) {
            throw new CaptchaException("Invalid configuration for a GimpyFactory : WordToImage can't be null");
        }
        if (wordGenerator == null) {
            throw new CaptchaException("Invalid configuration for a GimpyFactory : WordGenerator can't be null");
        }
        this.wordToImage = wordToImage;
        this.wordGenerator = wordGenerator;
        this.caseSensitive = caseSensitive;
    }
    
    @Override
    public ImageCaptcha getImageCaptcha() {
        return this.getImageCaptcha(Locale.getDefault());
    }
    
    public WordToImage getWordToImage() {
        return this.wordToImage;
    }
    
    public WordGenerator getWordGenerator() {
        return this.wordGenerator;
    }
    
    @Override
    public ImageCaptcha getImageCaptcha(final Locale locale) {
        final String word = this.getWordGenerator().getWord(this.getRandomLength(), locale);
        BufferedImage image;
        try {
            image = this.getWordToImage().getImage(word);
        }
        catch (final Throwable t) {
            throw new CaptchaException(t);
        }
        return new Gimpy(CaptchaQuestionHelper.getQuestion(locale, GimpyFactory.BUNDLE_QUESTION_KEY), image, word, this.caseSensitive);
    }
    
    protected Integer getRandomLength() {
        final int n = this.getWordToImage().getMaxAcceptedWordLength() - this.getWordToImage().getMinAcceptedWordLength();
        return new Integer(((n != 0) ? this.myRandom.nextInt(n + 1) : 0) + this.getWordToImage().getMinAcceptedWordLength());
    }
    
    static {
        BUNDLE_QUESTION_KEY = Gimpy.class.getName();
    }
}
