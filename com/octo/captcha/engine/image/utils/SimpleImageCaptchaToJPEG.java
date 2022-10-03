package com.octo.captcha.engine.image.utils;

import java.io.IOException;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import java.io.OutputStream;
import com.sun.image.codec.jpeg.JPEGCodec;
import java.io.FileOutputStream;
import java.io.File;
import com.octo.captcha.engine.image.gimpy.SimpleListImageCaptchaEngine;

public class SimpleImageCaptchaToJPEG
{
    public static void main(final String[] array) throws ImageFormatException, IOException {
        final SimpleListImageCaptchaEngine simpleListImageCaptchaEngine = new SimpleListImageCaptchaEngine();
        System.out.println("got gimpy");
        final ImageCaptchaFactory imageCaptchaFactory = simpleListImageCaptchaEngine.getImageCaptchaFactory();
        System.out.println("got factory");
        final ImageCaptcha imageCaptcha = imageCaptchaFactory.getImageCaptcha();
        System.out.println("got image");
        System.out.println(imageCaptcha.getQuestion());
        JPEGCodec.createJPEGEncoder(new FileOutputStream(new File("foo.jpg"))).encode(imageCaptcha.getImageChallenge());
    }
}
