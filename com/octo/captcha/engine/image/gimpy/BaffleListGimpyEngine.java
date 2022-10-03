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
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import java.awt.Color;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.word.DictionaryReader;
import com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator;
import com.octo.captcha.component.word.FileDictionary;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class BaffleListGimpyEngine extends ListImageCaptchaEngine
{
    @Override
    protected void buildInitialFactories() {
        this.addFactory(new GimpyFactory(new ComposeDictionaryWordGenerator(new FileDictionary("toddlist")), new ComposedWordToImage(new RandomFontGenerator(new Integer(20), new Integer(25)), new UniColorBackgroundGenerator(new Integer(200), new Integer(100), Color.white), new DecoratedRandomTextPaster(new Integer(8), new Integer(15), new SingleColorGenerator(Color.BLACK), new TextDecorator[] { new BaffleTextDecorator(2, Color.black) }))));
    }
}
