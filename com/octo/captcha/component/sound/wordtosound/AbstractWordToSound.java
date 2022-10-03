package com.octo.captcha.component.sound.wordtosound;

import javax.sound.sampled.AudioInputStream;
import com.octo.captcha.component.sound.soundconfigurator.SoundConfigurator;

public abstract class AbstractWordToSound implements WordToSound
{
    protected int maxAcceptedWordLength;
    protected int minAcceptedWordLength;
    protected SoundConfigurator configurator;
    
    public AbstractWordToSound(final SoundConfigurator configurator, final int minAcceptedWordLength, final int maxAcceptedWordLength) {
        this.configurator = null;
        this.configurator = configurator;
        this.minAcceptedWordLength = minAcceptedWordLength;
        this.maxAcceptedWordLength = maxAcceptedWordLength;
    }
    
    public int getMaxAcceptedWordLength() {
        return this.maxAcceptedWordLength;
    }
    
    public int getMinAcceptedWordLength() {
        return this.minAcceptedWordLength;
    }
    
    public int getMaxAcceptedWordLenght() {
        return this.maxAcceptedWordLength;
    }
    
    public int getMinAcceptedWordLenght() {
        return this.minAcceptedWordLength;
    }
    
    protected abstract AudioInputStream addEffects(final AudioInputStream p0);
}
