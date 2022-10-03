package com.octo.captcha.engine.image.utils;

import java.io.IOException;
import com.octo.captcha.image.ImageCaptcha;
import java.text.DecimalFormat;
import com.octo.captcha.engine.image.fisheye.SimpleFishEyeEngine;
import com.octo.captcha.engine.image.gimpy.SimpleListImageCaptchaEngine;
import com.octo.captcha.engine.image.gimpy.DeformedBaffleListGimpyEngine;
import com.octo.captcha.engine.image.gimpy.DefaultGimpyEngine;
import com.octo.captcha.engine.image.gimpy.BaffleListGimpyEngine;
import com.octo.captcha.engine.image.ImageCaptchaEngine;
import java.io.File;

public class ImageCaptchaToJPEG
{
    private static boolean SHOULD_DELETE_OLD_JPEGS_FIRST;
    
    public static void main(final String[] array) throws Exception {
        if (array.length < 2) {
            System.out.println("Usage : engineClassName outputDir iterations");
            System.out.println("If engineClassName is 'all', then several Gimpy Engines are used");
            System.exit(1);
        }
        final String s = array[0];
        final File file = new File(array[1]);
        final String s2 = array[2];
        final int int1 = Integer.parseInt(s2);
        System.out.println("args : image captcha engine class='" + s + "'" + ", output dir='" + file + "'" + ",iterations='" + s2 + "'");
        clearOutputDirectory(file);
        ImageCaptchaEngine imageCaptchaEngine = null;
        if (s.equals("all")) {
            final ImageCaptchaEngine[] array2 = { new BaffleListGimpyEngine(), new DefaultGimpyEngine(), new DeformedBaffleListGimpyEngine(), new SimpleListImageCaptchaEngine(), new SimpleFishEyeEngine() };
            for (int i = 0; i < array2.length; ++i) {
                final ImageCaptchaEngine imageCaptchaEngine2 = array2[i];
                System.out.println("Beginning generation with " + imageCaptchaEngine2.getClass().getName());
                try {
                    generate(int1, imageCaptchaEngine2, file);
                }
                catch (final Exception ex) {
                    System.out.println("Errors with class " + imageCaptchaEngine2.getClass().getName());
                }
            }
        }
        else {
            try {
                imageCaptchaEngine = (ImageCaptchaEngine)Class.forName(s).newInstance();
            }
            catch (final Exception ex2) {
                System.out.println("Couldn't initialize '" + s + "', trying a likely package prefix");
                final String s3 = "com.octo.captcha.engine.image.gimpy.";
                try {
                    imageCaptchaEngine = (ImageCaptchaEngine)Class.forName(s3 + s).newInstance();
                }
                catch (final Exception ex3) {
                    System.out.println("Couldn't initialize '" + s + " -- specify a fully attributed name");
                    System.exit(1);
                }
            }
            generate(int1, imageCaptchaEngine, file);
        }
        System.exit(0);
    }
    
    private static void clearOutputDirectory(final File file) {
        if (ImageCaptchaToJPEG.SHOULD_DELETE_OLD_JPEGS_FIRST) {
            final File[] listFiles = file.listFiles();
            if (listFiles == null) {
                return;
            }
            if (listFiles.length > 2) {
                System.out.println("Deleting about " + (listFiles.length - 2) + " jpeg files");
            }
            for (int i = 0; i < listFiles.length; ++i) {
                final File file2 = listFiles[i];
                if (file2.isFile() && file2.getName().endsWith("jpg")) {
                    file2.delete();
                }
            }
        }
    }
    
    private static void generate(final int n, final ImageCaptchaEngine imageCaptchaEngine, final File file) throws IOException {
        file.mkdirs();
        final String substring = imageCaptchaEngine.getClass().getName().substring(imageCaptchaEngine.getClass().getPackage().getName().length() + 1);
        System.out.println("Starting on " + substring);
        long n2 = 0L;
        long n3 = 0L;
        int i = 0;
        try {
            for (i = 0; i < n; ++i) {
                final long currentTimeMillis = System.currentTimeMillis();
                final ImageCaptcha nextImageCaptcha = imageCaptchaEngine.getNextImageCaptcha();
                n2 += System.currentTimeMillis() - currentTimeMillis;
                final long currentTimeMillis2 = System.currentTimeMillis();
                ImageToFile.serialize(nextImageCaptcha.getImageChallenge(), new File(file, File.separator + substring + "Captcha_" + i + ".jpg"));
                n3 += System.currentTimeMillis() - currentTimeMillis2;
                System.out.print(".");
                if (i % 100 == 99) {
                    System.out.println("");
                }
            }
        }
        finally {
            if (i < n) {
                System.out.println("exited early! i=" + i);
            }
            else {
                System.out.println("done");
            }
            final DecimalFormat decimalFormat = new DecimalFormat();
            System.out.println("Summary for " + substring + ":" + " avg image creation = " + decimalFormat.format(n2 / n) + " milliseconds/image," + " avg file creation = " + decimalFormat.format(n3 / n) + " milliseconds/file");
        }
    }
    
    static {
        ImageCaptchaToJPEG.SHOULD_DELETE_OLD_JPEGS_FIRST = true;
    }
}
