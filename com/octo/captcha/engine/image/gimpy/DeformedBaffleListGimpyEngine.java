package com.octo.captcha.engine.image.gimpy;

import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.fontgenerator.TwistedAndShearedRandomFontGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.word.DictionaryReader;
import com.octo.captcha.component.word.wordgenerator.DictionaryWordGenerator;
import com.octo.captcha.component.word.FileDictionary;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import java.awt.image.ImageFilter;
import com.jhlabs.image.WeaveFilter;
import com.jhlabs.image.WaterFilter;
import com.jhlabs.image.TwirlFilter;
import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.SphereFilter;
import com.jhlabs.image.EmbossFilter;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class DeformedBaffleListGimpyEngine extends ListImageCaptchaEngine
{
    @Override
    protected void buildInitialFactories() {
        final EmbossFilter embossFilter = new EmbossFilter();
        final SphereFilter sphereFilter = new SphereFilter();
        final RippleFilter rippleFilter = new RippleFilter();
        final RippleFilter rippleFilter2 = new RippleFilter();
        final TwirlFilter twirlFilter = new TwirlFilter();
        final WaterFilter waterFilter = new WaterFilter();
        final WeaveFilter weaveFilter = new WeaveFilter();
        rippleFilter2.setWaveType(3);
        rippleFilter2.setXAmplitude(3.0f);
        rippleFilter2.setYAmplitude(3.0f);
        rippleFilter2.setXWavelength(20.0f);
        rippleFilter2.setYWavelength(10.0f);
        rippleFilter2.setEdgeAction(1);
        rippleFilter.setWaveType(3);
        rippleFilter.setXAmplitude(5.0f);
        rippleFilter.setYAmplitude(5.0f);
        rippleFilter.setXWavelength(10.0f);
        rippleFilter.setYWavelength(10.0f);
        rippleFilter.setEdgeAction(1);
        waterFilter.setAmplitude(1.0f);
        waterFilter.setWavelength(20.0f);
        twirlFilter.setAngle(0.0f);
        sphereFilter.setRefractionIndex(1.0f);
        weaveFilter.setUseImageColors(true);
        final ImageDeformationByFilters imageDeformationByFilters = new ImageDeformationByFilters(new ImageFilter[0]);
        final ImageDeformationByFilters imageDeformationByFilters2 = new ImageDeformationByFilters(new ImageFilter[0]);
        final ImageDeformationByFilters imageDeformationByFilters3 = new ImageDeformationByFilters(new ImageFilter[0]);
        final ImageDeformationByFilters imageDeformationByFilters4 = new ImageDeformationByFilters(new ImageFilter[0]);
        final ImageDeformationByFilters imageDeformationByFilters5 = new ImageDeformationByFilters(new ImageFilter[0]);
        final ImageDeformationByFilters imageDeformationByFilters6 = new ImageDeformationByFilters(null);
        final DictionaryWordGenerator dictionaryWordGenerator = new DictionaryWordGenerator(new FileDictionary("toddlist"));
        final DecoratedRandomTextPaster decoratedRandomTextPaster = new DecoratedRandomTextPaster(new Integer(6), new Integer(7), new SingleColorGenerator(Color.black), new TextDecorator[] { new BaffleTextDecorator(new Integer(1), Color.white) });
        final UniColorBackgroundGenerator uniColorBackgroundGenerator = new UniColorBackgroundGenerator(new Integer(200), new Integer(100), Color.white);
        final TwistedAndShearedRandomFontGenerator twistedAndShearedRandomFontGenerator = new TwistedAndShearedRandomFontGenerator(new Integer(30), new Integer(40));
        this.addFactory(new GimpyFactory(dictionaryWordGenerator, new ComposedWordToImage(twistedAndShearedRandomFontGenerator, uniColorBackgroundGenerator, decoratedRandomTextPaster)));
        this.addFactory(new GimpyFactory(dictionaryWordGenerator, new DeformedComposedWordToImage(twistedAndShearedRandomFontGenerator, uniColorBackgroundGenerator, decoratedRandomTextPaster, imageDeformationByFilters, imageDeformationByFilters2, imageDeformationByFilters3)));
        this.addFactory(new GimpyFactory(dictionaryWordGenerator, new DeformedComposedWordToImage(twistedAndShearedRandomFontGenerator, uniColorBackgroundGenerator, decoratedRandomTextPaster, imageDeformationByFilters4, null, imageDeformationByFilters)));
        this.addFactory(new GimpyFactory(dictionaryWordGenerator, new DeformedComposedWordToImage(twistedAndShearedRandomFontGenerator, uniColorBackgroundGenerator, decoratedRandomTextPaster, imageDeformationByFilters4, imageDeformationByFilters6, imageDeformationByFilters5)));
    }
}
