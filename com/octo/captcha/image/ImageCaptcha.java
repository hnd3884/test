package com.octo.captcha.image;

import javax.imageio.stream.ImageInputStream;
import java.io.InputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.OutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.io.ObjectOutputStream;
import java.awt.image.BufferedImage;
import com.octo.captcha.Captcha;

public abstract class ImageCaptcha implements Captcha
{
    private Boolean hasChallengeBeenCalled;
    protected String question;
    protected transient BufferedImage challenge;
    
    protected ImageCaptcha(final String question, final BufferedImage challenge) {
        this.hasChallengeBeenCalled = Boolean.FALSE;
        this.challenge = challenge;
        this.question = question;
    }
    
    public final String getQuestion() {
        return this.question;
    }
    
    public final Object getChallenge() {
        return this.getImageChallenge();
    }
    
    public final BufferedImage getImageChallenge() {
        this.hasChallengeBeenCalled = Boolean.TRUE;
        return this.challenge;
    }
    
    public final void disposeChallenge() {
        this.challenge = null;
    }
    
    public Boolean hasGetChalengeBeenCalled() {
        return this.hasChallengeBeenCalled;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.challenge != null) {
            ImageIO.write(this.challenge, "png", new MemoryCacheImageOutputStream(objectOutputStream));
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.challenge = ImageIO.read(new MemoryCacheImageInputStream(objectInputStream));
        }
        catch (final IOException ex) {
            if (!this.hasChallengeBeenCalled) {
                throw ex;
            }
        }
    }
}
