package com.octo.captcha.engine.image.gimpy;

import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.util.List;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import java.awt.image.BufferedImageOp;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import java.util.ArrayList;
import com.jhlabs.image.SwimFilter;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import java.awt.Font;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class HotmailEngine2008 extends ListImageCaptchaEngine
{
    @Override
    protected void buildInitialFactories() {
        final RandomWordGenerator randomWordGenerator = new RandomWordGenerator("ABCDEGHJKLMNRSTUWXY235689");
        final GlyphsPaster glyphsPaster = new GlyphsPaster(8, 8, new SingleColorGenerator(new Color(0, 0, 80)), new GlyphsVisitors[] { new OverlapGlyphsUsingShapeVisitor(3.0), new TranslateAllToRandomPointVisitor(20.0, 20.0) });
        final UniColorBackgroundGenerator uniColorBackgroundGenerator = new UniColorBackgroundGenerator(218, 48, new Color(238, 238, 238));
        final RandomFontGenerator randomFontGenerator = new RandomFontGenerator(30, 35, new Font[] { new Font("Caslon", 1, 30) }, false);
        final SwimFilter swimFilter = new SwimFilter();
        swimFilter.setScale(30.0f);
        swimFilter.setAmount(10.0f);
        swimFilter.setEdgeAction(1);
        final SwimFilter swimFilter2 = new SwimFilter();
        swimFilter2.setScale(30.0f);
        swimFilter2.setAmount(10.0f);
        swimFilter2.setTime(90.0f);
        swimFilter2.setEdgeAction(1);
        final ArrayList list = new ArrayList();
        list.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)swimFilter));
        list.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)swimFilter2));
        this.addFactory(new GimpyFactory(randomWordGenerator, new DeformedComposedWordToImage(false, randomFontGenerator, uniColorBackgroundGenerator, glyphsPaster, new ArrayList<ImageDeformation>(), list, new ArrayList<ImageDeformation>()), false));
    }
}
