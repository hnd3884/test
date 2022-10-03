package com.octo.captcha.engine.image.gimpy;

import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;
import java.awt.Color;
import com.octo.captcha.component.image.backgroundgenerator.FunkyBackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.TwistedAndShearedRandomFontGenerator;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class SimpleListImageCaptchaEngine extends ListImageCaptchaEngine
{
    @Override
    protected void buildInitialFactories() {
        this.addFactory(new GimpyFactory(new RandomWordGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"), new ComposedWordToImage(new TwistedAndShearedRandomFontGenerator(new Integer(25), new Integer(30)), new FunkyBackgroundGenerator(new Integer(200), new Integer(100)), new RandomTextPaster(new Integer(5), new Integer(8), Color.white))));
    }
}
