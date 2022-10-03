package com.octo.captcha.engine.image.fisheye;

import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.image.fisheye.FishEyeFactory;
import com.octo.captcha.component.image.backgroundgenerator.FileReaderRandomBackgroundGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import java.awt.image.ImageFilter;
import com.jhlabs.image.WaterFilter;
import com.jhlabs.image.TwirlFilter;
import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.SphereFilter;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class SimpleFishEyeEngine extends ListImageCaptchaEngine
{
    @Override
    protected void buildInitialFactories() {
        final SphereFilter sphereFilter = new SphereFilter();
        final RippleFilter rippleFilter = new RippleFilter();
        final TwirlFilter twirlFilter = new TwirlFilter();
        final WaterFilter waterFilter = new WaterFilter();
        rippleFilter.setWaveType(3);
        rippleFilter.setXAmplitude(10.0f);
        rippleFilter.setYAmplitude(10.0f);
        rippleFilter.setXWavelength(10.0f);
        rippleFilter.setYWavelength(10.0f);
        rippleFilter.setEdgeAction(1);
        waterFilter.setAmplitude(10.0f);
        waterFilter.setWavelength(20.0f);
        twirlFilter.setAngle(4.0f);
        sphereFilter.setRefractionIndex(2.0f);
        final ImageDeformationByFilters imageDeformationByFilters = new ImageDeformationByFilters(new ImageFilter[0]);
        final ImageDeformationByFilters imageDeformationByFilters2 = new ImageDeformationByFilters(new ImageFilter[0]);
        final ImageDeformationByFilters imageDeformationByFilters3 = new ImageDeformationByFilters(new ImageFilter[0]);
        final ImageDeformationByFilters imageDeformationByFilters4 = new ImageDeformationByFilters(new ImageFilter[0]);
        final FileReaderRandomBackgroundGenerator fileReaderRandomBackgroundGenerator = new FileReaderRandomBackgroundGenerator(new Integer(250), new Integer(250), "./fisheyebackgrounds");
        this.addFactory(new FishEyeFactory(fileReaderRandomBackgroundGenerator, imageDeformationByFilters2, new Integer(10), new Integer(5)));
        this.addFactory(new FishEyeFactory(fileReaderRandomBackgroundGenerator, imageDeformationByFilters, new Integer(10), new Integer(5)));
        this.addFactory(new FishEyeFactory(fileReaderRandomBackgroundGenerator, imageDeformationByFilters3, new Integer(10), new Integer(5)));
        this.addFactory(new FishEyeFactory(fileReaderRandomBackgroundGenerator, imageDeformationByFilters4, new Integer(10), new Integer(5)));
    }
}
