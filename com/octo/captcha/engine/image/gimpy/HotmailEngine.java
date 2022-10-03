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
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.glyphsdecorator.RandomLinesGlyphsDecorator;
import com.octo.captcha.component.image.textpaster.glyphsdecorator.GlyphsDecorator;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.HorizontalSpaceGlyphsVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.ShearGlyphsRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.RotateGlyphsRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class HotmailEngine extends ListImageCaptchaEngine
{
    @Override
    protected void buildInitialFactories() {
        final RandomWordGenerator randomWordGenerator = new RandomWordGenerator("ABCDEGHJKLMNRSTUWXY235689");
        final GlyphsPaster glyphsPaster = new GlyphsPaster(8, 8, new SingleColorGenerator(new Color(0, 0, 80)), new GlyphsVisitors[] { new TranslateGlyphsVerticalRandomVisitor(5.0), new RotateGlyphsRandomVisitor(0.09817477042468103), new ShearGlyphsRandomVisitor(0.2, 0.2), new HorizontalSpaceGlyphsVisitor(4), new TranslateAllToRandomPointVisitor() }, new GlyphsDecorator[] { new RandomLinesGlyphsDecorator(1.2, new SingleColorGenerator(new Color(0, 0, 80)), 2.0, 25.0), new RandomLinesGlyphsDecorator(1.0, new SingleColorGenerator(new Color(238, 238, 238)), 1.0, 25.0) });
        final UniColorBackgroundGenerator uniColorBackgroundGenerator = new UniColorBackgroundGenerator(218, 48, new Color(238, 238, 238));
        final RandomFontGenerator randomFontGenerator = new RandomFontGenerator(30, 35, new Font[] { new Font("Caslon", 1, 30) }, false);
        final SwimFilter swimFilter = new SwimFilter();
        swimFilter.setScale(30.0f);
        swimFilter.setStretch(1.0f);
        swimFilter.setTurbulence(1.0f);
        swimFilter.setAmount(2.0f);
        swimFilter.setTime(0.0f);
        swimFilter.setEdgeAction(1);
        final ArrayList list = new ArrayList();
        list.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)swimFilter));
        this.addFactory(new GimpyFactory(randomWordGenerator, new DeformedComposedWordToImage(false, randomFontGenerator, uniColorBackgroundGenerator, glyphsPaster, new ArrayList<ImageDeformation>(), new ArrayList<ImageDeformation>(), list), false));
    }
}
