package com.octo.captcha.sound;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import com.octo.captcha.CaptchaException;
import java.io.OutputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioInputStream;
import com.octo.captcha.Captcha;

public abstract class SoundCaptcha implements Captcha
{
    protected Boolean hasChallengeBeenCalled;
    protected String question;
    protected byte[] challenge;
    
    protected SoundCaptcha(final String question, final AudioInputStream audioInputStream) {
        this.hasChallengeBeenCalled = Boolean.FALSE;
        this.question = question;
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
            this.challenge = byteArrayOutputStream.toByteArray();
        }
        catch (final IOException ex) {
            throw new CaptchaException("unable to serialize input stream", ex);
        }
    }
    
    public final String getQuestion() {
        return this.question;
    }
    
    public final Object getChallenge() {
        return this.getSoundChallenge();
    }
    
    public final AudioInputStream getSoundChallenge() {
        try {
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(this.challenge));
            this.hasChallengeBeenCalled = Boolean.TRUE;
            return audioInputStream;
        }
        catch (final UnsupportedAudioFileException ex) {
            throw new CaptchaException("unable to deserialize input stream", ex);
        }
        catch (final IOException ex2) {
            throw new CaptchaException("unable to deserialize input stream", ex2);
        }
    }
    
    public void disposeChallenge() {
        this.challenge = null;
    }
    
    public Boolean hasGetChalengeBeenCalled() {
        return this.hasChallengeBeenCalled;
    }
}
