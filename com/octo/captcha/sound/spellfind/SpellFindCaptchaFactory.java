package com.octo.captcha.sound.spellfind;

import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.sound.speller.SpellerSound;
import java.util.ResourceBundle;
import java.util.Locale;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.CaptchaException;
import java.security.SecureRandom;
import java.util.Random;
import com.octo.captcha.component.sound.wordtosound.WordToSound;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.sound.SoundCaptchaFactory;

public class SpellFindCaptchaFactory extends SoundCaptchaFactory
{
    private WordGenerator wordGenerator;
    private WordToSound word2Sound;
    private Random myRandom;
    public static final String BUNDLE_QUESTION_KEY;
    
    public SpellFindCaptchaFactory(final WordGenerator wordGenerator, final WordToSound word2Sound) {
        this.myRandom = new SecureRandom();
        if (wordGenerator == null) {
            throw new CaptchaException("Invalid configuration for a SpellFindCaptchaFactory : WordGenerator can't be null");
        }
        if (word2Sound == null) {
            throw new CaptchaException("Invalid configuration for a SpellFindCaptchaFactory : Word2Sound can't be null");
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
        return this.getSoundCaptcha(Locale.getDefault());
    }
    
    @Override
    public SoundCaptcha getSoundCaptcha(final Locale locale) {
        final ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName(), locale);
        final int intValue = this.getRandomLength();
        final StringBuffer sb = new StringBuffer();
        final StringBuffer sb2 = new StringBuffer();
        for (int i = 0; i < intValue; ++i) {
            final String word = this.wordGenerator.getWord(new Integer(this.getRandomLength()), locale);
            final int abs = Math.abs(this.myRandom.nextInt() % word.length());
            sb.append(bundle.getString("number"));
            sb.append(" ");
            sb.append(abs + 1);
            sb.append(" ");
            sb.append(bundle.getString("word"));
            sb.append(" ");
            sb.append(word);
            sb.append(" ");
            sb.append((intValue - 1 == i) ? bundle.getString("end") : bundle.getString("transition"));
            sb2.append(word.charAt(abs));
        }
        return new SpellerSound(this.getQuestion(locale), this.word2Sound.getSound(sb.toString(), locale), sb2.toString());
    }
    
    protected String getQuestion(final Locale locale) {
        return CaptchaQuestionHelper.getQuestion(locale, SpellFindCaptchaFactory.BUNDLE_QUESTION_KEY);
    }
    
    protected Integer getRandomLength() {
        final int n = this.getWordToSound().getMaxAcceptedWordLength() - this.getWordToSound().getMinAcceptedWordLength();
        return new Integer(((n != 0) ? this.myRandom.nextInt(n + 1) : 0) + this.getWordToSound().getMinAcceptedWordLength());
    }
    
    static {
        BUNDLE_QUESTION_KEY = SpellFindCaptchaFactory.class.getName();
    }
}
