package com.octo.captcha.engine.image.gimpy;

import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.util.List;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import java.awt.image.BufferedImageOp;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import java.util.ArrayList;
import com.jhlabs.image.PinchFilter;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import java.awt.Font;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.RotateGlyphsRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.word.DictionaryReader;
import com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator;
import com.octo.captcha.component.word.FileDictionary;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class GmailEngine extends ListImageCaptchaEngine
{
    @Override
    protected void buildInitialFactories() {
        final WordGenerator dictionnaryWords = new ComposeDictionaryWordGenerator(new FileDictionary("toddlist"));
        final TextPaster randomPaster = new GlyphsPaster(6, 8, new RandomListColorGenerator(new Color[] { new Color(23, 170, 27), new Color(220, 34, 11), new Color(23, 67, 172) }), new GlyphsVisitors[] { new TranslateGlyphsVerticalRandomVisitor(1.0), new RotateGlyphsRandomVisitor(0.09817477042468103), new OverlapGlyphsUsingShapeVisitor(3.0), new TranslateAllToRandomPointVisitor() });
        final BackgroundGenerator back = new UniColorBackgroundGenerator(200, 70, Color.white);
        final FontGenerator shearedFont = new RandomFontGenerator(40, 40, new Font[] { new Font("Serif.plain", 0, 40), new Font("Serif.italic", 0, 40), new Font("Lucida Bright Regular", 0, 40), new Font("Lucida Bright Italic", 0, 40) }, false);
        final PinchFilter pinch = new PinchFilter();
        pinch.setAmount(-0.5f);
        pinch.setAngle(0.19634955f);
        pinch.setCentreX(0.5f);
        pinch.setCentreY(-0.01f);
        pinch.setEdgeAction(1);
        final PinchFilter pinch2 = new PinchFilter();
        pinch2.setAmount(-0.6f);
        pinch2.setRadius(70.0f);
        pinch2.setAngle(0.19634955f);
        pinch2.setCentreX(0.3f);
        pinch2.setCentreY(1.01f);
        pinch2.setEdgeAction(1);
        final PinchFilter pinch3 = new PinchFilter();
        pinch3.setAmount(-0.6f);
        pinch3.setRadius(70.0f);
        pinch3.setAngle(0.19634955f);
        pinch3.setCentreX(0.8f);
        pinch3.setCentreY(-0.01f);
        pinch3.setEdgeAction(1);
        final List<ImageDeformation> textDef = new ArrayList<ImageDeformation>();
        textDef.add(new ImageDeformationByBufferedImageOp((BufferedImageOp)pinch));
        final WordToImage word2image = new DeformedComposedWordToImage(false, shearedFont, back, randomPaster, new ArrayList<ImageDeformation>(), new ArrayList<ImageDeformation>(), textDef);
        this.addFactory(new GimpyFactory(dictionnaryWords, word2image, false));
    }
}
