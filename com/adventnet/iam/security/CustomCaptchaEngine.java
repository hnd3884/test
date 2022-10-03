package com.adventnet.iam.security;

import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.word.DictionaryReader;
import com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator;
import com.octo.captcha.component.word.FileDictionary;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.RotateGlyphsRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.HorizontalSpaceGlyphsVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import java.awt.Font;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import java.awt.image.BufferedImageOp;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import java.util.ArrayList;
import com.jhlabs.image.SwimFilter;
import com.jhlabs.image.PinchFilter;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import java.util.List;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class CustomCaptchaEngine extends ListImageCaptchaEngine
{
    int width;
    int height;
    int wordSpacing;
    int[] font;
    private static int[] styles;
    
    public CustomCaptchaEngine(final int width, final int height, final int fontSize, final int wordSpacing) {
        this.font = new int[2];
        this.width = width;
        this.height = height;
        this.font[0] = fontSize;
        this.font[1] = fontSize;
        this.wordSpacing = wordSpacing;
        this.buildCustomInitialFactories();
    }
    
    CustomCaptchaEngine(final int[] imageSize) {
        this(imageSize[0], imageSize[1], imageSize[2], imageSize[3]);
    }
    
    private List<ImageDeformation> getTextDefinitions() {
        final PinchFilter pinch1 = new PinchFilter();
        pinch1.setAmount(-0.6f);
        pinch1.setRadius(70.0f);
        pinch1.setAngle(0.19634955f);
        pinch1.setCentreX(0.5f);
        pinch1.setCentreY(1.51f);
        pinch1.setEdgeAction(1);
        final PinchFilter pinch2 = new PinchFilter();
        pinch2.setAmount(-0.6f);
        pinch2.setRadius(70.0f);
        pinch2.setAngle(0.19634955f);
        pinch2.setCentreX(0.8f);
        pinch2.setCentreY(-0.01f);
        pinch2.setEdgeAction(1);
        final SwimFilter swim = new SwimFilter();
        swim.setScale(30.0f);
        swim.setAmount(3.0f);
        swim.setTime(90.0f);
        swim.setEdgeAction(1);
        final List<ImageDeformation> textDef = new ArrayList<ImageDeformation>();
        textDef.add((ImageDeformation)new ImageDeformationByBufferedImageOp((BufferedImageOp)pinch1));
        textDef.add((ImageDeformation)new ImageDeformationByBufferedImageOp((BufferedImageOp)pinch2));
        textDef.add((ImageDeformation)new ImageDeformationByBufferedImageOp((BufferedImageOp)swim));
        return textDef;
    }
    
    private FontGenerator getFont(int font) {
        final int style = CustomCaptchaEngine.styles[(int)(Math.random() * 3.0)];
        if (font == -1) {
            font = (this.font[0] + this.font[1]) / 2;
        }
        final FontGenerator shearedFont = (FontGenerator)new RandomFontGenerator(Integer.valueOf(font), Integer.valueOf(font + 5), new Font[] { new Font("Sans-Serif", style, font), new Font("nyala", style, font), new Font("Bell MT", style, font), new Font("Credit valley", style, font) }, false);
        return shearedFont;
    }
    
    private TextPaster getRandomPaster(final int width, final int height, final int wordSpace) {
        return (TextPaster)new GlyphsPaster(Integer.valueOf(6), Integer.valueOf(10), (ColorGenerator)new RandomListColorGenerator(new Color[] { new Color(23, 170, 27), new Color(220, 34, 11), new Color(23, 67, 172) }), new GlyphsVisitors[] { (GlyphsVisitors)new TranslateGlyphsVerticalRandomVisitor(2.0), (GlyphsVisitors)new HorizontalSpaceGlyphsVisitor(wordSpace), (GlyphsVisitors)new RotateGlyphsRandomVisitor(0.04908738521234052), (GlyphsVisitors)new TranslateAllToRandomPointVisitor() });
    }
    
    protected void buildInitialFactories() {
        final WordGenerator dictionaryWords = (WordGenerator)new ComposeDictionaryWordGenerator((DictionaryReader)new FileDictionary("toddlist"));
        final BackgroundGenerator back = (BackgroundGenerator)new UniColorBackgroundGenerator(Integer.valueOf(200), Integer.valueOf(70), Color.white);
        final WordToImage word2image = (WordToImage)new DeformedComposedWordToImage(this.getFont(40), back, this.getRandomPaster(200, 70, 0), (List)new ArrayList(), (List)new ArrayList(), (List)this.getTextDefinitions());
        this.addFactory((ImageCaptchaFactory)new GimpyFactory(dictionaryWords, word2image, false));
    }
    
    protected void buildCustomInitialFactories() {
        final WordGenerator dictionaryWords = (WordGenerator)new ComposeDictionaryWordGenerator((DictionaryReader)new FileDictionary("toddlist"));
        final BackgroundGenerator back = (BackgroundGenerator)new UniColorBackgroundGenerator(Integer.valueOf(this.width), Integer.valueOf(this.height), Color.white);
        final WordToImage word2image = (WordToImage)new DeformedComposedWordToImage(this.getFont(-1), back, this.getRandomPaster(this.width, this.height, this.wordSpacing), (List)new ArrayList(), (List)new ArrayList(), (List)this.getTextDefinitions());
        final CaptchaFactory[] capfact = { (CaptchaFactory)new GimpyFactory(dictionaryWords, word2image, false) };
        this.setFactories(capfact);
    }
    
    static {
        CustomCaptchaEngine.styles = new int[] { 0, 2, 1 };
    }
}
