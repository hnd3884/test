package com.octo.captcha.sound.gimpy;

import com.octo.captcha.CaptchaQuestionHelper;
import java.util.Locale;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.CaptchaException;
import java.security.SecureRandom;
import java.util.Random;
import com.octo.captcha.component.sound.wordtosound.WordToSound;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.sound.SoundCaptchaFactory;

public class GimpySoundFactory extends SoundCaptchaFactory
{
    private WordGenerator wordGenerator;
    private WordToSound word2Sound;
    private Random myRandom;
    public static final String BUNDLE_QUESTION_KEY;
    
    public GimpySoundFactory(final WordGenerator wordGenerator, final WordToSound word2Sound) {
        this.myRandom = new SecureRandom();
        if (wordGenerator == null) {
            throw new CaptchaException("Invalid configuration for a GimpySoundFactory : WordGenerator can't be null");
        }
        if (word2Sound == null) {
            throw new CaptchaException("Invalid configuration for a GimpySoundFactory : Word2Sound can't be null");
        }
        this.wordGenerator = wordGenerator;
        this.word2Sound = word2Sound;
    }
    
    public WordToSound getWordToSound() {
        return this.word2Sound;
    }
    
    public WordGenerator getWordGenerator() {
        return this.wordGenerator;
    }
    
    @Override
    public SoundCaptcha getSoundCaptcha() {
        final String word = this.wordGenerator.getWord(this.getRandomLength(), Locale.getDefault());
        return new GimpySound(this.getQuestion(Locale.getDefault()), this.word2Sound.getSound(word), word);
    }
    
    @Override
    public SoundCaptcha getSoundCaptcha(final Locale locale) {
        final String word = this.wordGenerator.getWord(this.getRandomLength(), locale);
        return new GimpySound(this.getQuestion(locale), this.word2Sound.getSound(word, locale), word);
    }
    
    protected String getQuestion(final Locale locale) {
        return CaptchaQuestionHelper.getQuestion(locale, GimpySoundFactory.BUNDLE_QUESTION_KEY);
    }
    
    protected Integer getRandomLength() {
        final int n = this.getWordToSound().getMaxAcceptedWordLength() - this.getWordToSound().getMinAcceptedWordLength();
        return new Integer(((n != 0) ? this.myRandom.nextInt(n + 1) : 0) + this.getWordToSound().getMinAcceptedWordLength());
    }
    
    static {
        BUNDLE_QUESTION_KEY = GimpySound.class.getName();
    }
}
