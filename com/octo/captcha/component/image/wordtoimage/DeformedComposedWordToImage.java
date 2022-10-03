package com.octo.captcha.component.image.wordtoimage;

import com.octo.captcha.CaptchaException;
import java.util.Iterator;
import java.text.AttributedString;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import java.util.List;

public class DeformedComposedWordToImage extends ComposedWordToImage
{
    private List<ImageDeformation> backgroundDeformations;
    private List<ImageDeformation> textDeformations;
    private List<ImageDeformation> finalDeformations;
    
    public DeformedComposedWordToImage(final FontGenerator fontGenerator, final BackgroundGenerator backgroundGenerator, final TextPaster textPaster, final ImageDeformation imageDeformation, final ImageDeformation imageDeformation2, final ImageDeformation imageDeformation3) {
        super(fontGenerator, backgroundGenerator, textPaster);
        this.backgroundDeformations = new ArrayList<ImageDeformation>();
        this.textDeformations = new ArrayList<ImageDeformation>();
        this.finalDeformations = new ArrayList<ImageDeformation>();
        if (imageDeformation != null) {
            this.backgroundDeformations.add(imageDeformation);
        }
        if (imageDeformation2 != null) {
            this.textDeformations.add(imageDeformation2);
        }
        if (imageDeformation3 != null) {
            this.finalDeformations.add(imageDeformation3);
        }
    }
    
    public DeformedComposedWordToImage(final FontGenerator fontGenerator, final BackgroundGenerator backgroundGenerator, final TextPaster textPaster, final List<ImageDeformation> backgroundDeformations, final List<ImageDeformation> textDeformations, final List<ImageDeformation> finalDeformations) {
        super(fontGenerator, backgroundGenerator, textPaster);
        this.backgroundDeformations = new ArrayList<ImageDeformation>();
        this.textDeformations = new ArrayList<ImageDeformation>();
        this.finalDeformations = new ArrayList<ImageDeformation>();
        this.backgroundDeformations = backgroundDeformations;
        this.textDeformations = textDeformations;
        this.finalDeformations = finalDeformations;
    }
    
    public DeformedComposedWordToImage(final boolean b, final FontGenerator fontGenerator, final BackgroundGenerator backgroundGenerator, final TextPaster textPaster, final List<ImageDeformation> backgroundDeformations, final List<ImageDeformation> textDeformations, final List<ImageDeformation> finalDeformations) {
        super(b, fontGenerator, backgroundGenerator, textPaster);
        this.backgroundDeformations = new ArrayList<ImageDeformation>();
        this.textDeformations = new ArrayList<ImageDeformation>();
        this.finalDeformations = new ArrayList<ImageDeformation>();
        this.backgroundDeformations = backgroundDeformations;
        this.textDeformations = textDeformations;
        this.finalDeformations = finalDeformations;
    }
    
    @Override
    public BufferedImage getImage(final String s) throws CaptchaException {
        final BufferedImage background = this.getBackground();
        final AttributedString attributedString = this.getAttributedString(s, this.checkWordLength(s));
        BufferedImage bufferedImage = new BufferedImage(background.getWidth(), background.getHeight(), background.getType());
        final Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
        graphics2D.drawImage(background, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics2D.dispose();
        final Iterator<ImageDeformation> iterator = this.backgroundDeformations.iterator();
        while (iterator.hasNext()) {
            bufferedImage = iterator.next().deformImage(bufferedImage);
        }
        BufferedImage bufferedImage2 = this.pasteText(new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 2), attributedString);
        final Iterator<ImageDeformation> iterator2 = this.textDeformations.iterator();
        while (iterator2.hasNext()) {
            bufferedImage2 = iterator2.next().deformImage(bufferedImage2);
        }
        final Graphics2D graphics2D2 = (Graphics2D)bufferedImage.getGraphics();
        graphics2D2.drawImage(bufferedImage2, 0, 0, null);
        graphics2D2.dispose();
        final Iterator<ImageDeformation> iterator3 = this.finalDeformations.iterator();
        while (iterator3.hasNext()) {
            bufferedImage = iterator3.next().deformImage(bufferedImage);
        }
        return bufferedImage;
    }
}
