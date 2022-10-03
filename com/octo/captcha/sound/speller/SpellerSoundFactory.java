package com.octo.captcha.sound.speller;

import com.octo.captcha.CaptchaQuestionHelper;
import java.util.Locale;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.CaptchaException;
import java.security.SecureRandom;
import com.octo.captcha.component.word.worddecorator.SpellerWordDecorator;
import java.util.Random;
import com.octo.captcha.component.word.worddecorator.WordDecorator;
import com.octo.captcha.component.sound.wordtosound.WordToSound;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.sound.SoundCaptchaFactory;

public class SpellerSoundFactory extends SoundCaptchaFactory
{
    private WordGenerator wordGenerator;
    private WordToSound word2Sound;
    private WordDecorator wordDecorator;
    private Random myRandom;
    public static final String BUNDLE_QUESTION_KEY;
    
    public SpellerSoundFactory(final WordGenerator wordGenerator, final WordToSound word2Sound, final SpellerWordDecorator wordDecorator) {
        this.myRandom = new SecureRandom();
        if (wordGenerator == null) {
            throw new CaptchaException("Invalid configuration for a SpellingSoundFactory : WordGenerator can't be null");
        }
        if (word2Sound == null) {
            throw new CaptchaException("Invalid configuration for a SpellingSoundFactory : Word2Sound can't be null");
        }
        if (wordDecorator == null) {
            throw new CaptchaException("Invalid configuration for a SpellingSoundFactory : wordDecorator can't be null");
        }
        this.wordGenerator = wordGenerator;
        this.word2Sound = word2Sound;
        this.wordDecorator = wordDecorator;
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
        return new SpellerSound(this.getQuestion(Locale.getDefault()), this.word2Sound.getSound(this.wordDecorator.decorateWord(word)), word);
    }
    
    @Override
    public SoundCaptcha getSoundCaptcha(final Locale locale) {
        final String word = this.wordGenerator.getWord(this.getRandomLength(), locale);
        return new SpellerSound(this.getQuestion(locale), this.word2Sound.getSound(this.wordDecorator.decorateWord(word), locale), word);
    }
    
    protected String getQuestion(final Locale locale) {
        return CaptchaQuestionHelper.getQuestion(locale, SpellerSoundFactory.BUNDLE_QUESTION_KEY);
    }
    
    protected Integer getRandomLength() {
        final int n = this.getWordToSound().getMaxAcceptedWordLength() - this.getWordToSound().getMinAcceptedWordLength();
        return new Integer(((n != 0) ? this.myRandom.nextInt(n + 1) : 0) + this.getWordToSound().getMinAcceptedWordLength());
    }
    
    static {
        BUNDLE_QUESTION_KEY = SpellerSound.class.getName();
    }
}
