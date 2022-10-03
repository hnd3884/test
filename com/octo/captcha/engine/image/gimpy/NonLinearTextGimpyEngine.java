package com.octo.captcha.engine.image.gimpy;

import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.NonLinearTextPaster;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator;
import java.awt.Color;
import com.octo.captcha.component.image.fontgenerator.DeformedRandomFontGenerator;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class NonLinearTextGimpyEngine extends ListImageCaptchaEngine
{
    @Override
    protected void buildInitialFactories() {
        this.addFactory(new GimpyFactory(new RandomWordGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"), new ComposedWordToImage(new DeformedRandomFontGenerator(new Integer(25), new Integer(30)), new GradientBackgroundGenerator(new Integer(200), new Integer(100), Color.CYAN, Color.GRAY), new NonLinearTextPaster(new Integer(5), new Integer(7), new RandomListColorGenerator(new Color[] { Color.BLACK, Color.YELLOW, Color.WHITE }), Boolean.TRUE))));
    }
}
