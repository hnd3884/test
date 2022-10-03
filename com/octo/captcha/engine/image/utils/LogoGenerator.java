package com.octo.captcha.engine.image.utils;

import java.io.IOException;
import java.io.File;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.textpaster.SimpleTextPaster;
import java.awt.Color;
import com.octo.captcha.component.image.backgroundgenerator.FileReaderRandomBackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.TwistedAndShearedRandomFontGenerator;
import com.octo.captcha.component.word.wordgenerator.DummyWordGenerator;

public class LogoGenerator
{
    public static void main(final String[] array) throws IOException {
        ImageToFile.serialize(new GimpyFactory(new DummyWordGenerator("JCAPTCHA"), new ComposedWordToImage(new TwistedAndShearedRandomFontGenerator(new Integer(30), null), new FileReaderRandomBackgroundGenerator(new Integer(200), new Integer(100), "/gimpybackgrounds"), new SimpleTextPaster(new Integer(8), new Integer(8), Color.white))).getImageCaptcha().getImageChallenge(), new File(array[0]));
    }
}
