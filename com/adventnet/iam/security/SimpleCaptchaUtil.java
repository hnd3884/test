package com.adventnet.iam.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import com.octo.captcha.engine.image.gimpy.DefaultGimpyEngine;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import com.zoho.security.cache.RedisCacheAPI;
import com.zoho.security.util.HashUtil;
import javax.servlet.http.HttpServletRequest;
import com.octo.captcha.image.gimpy.GimpyFactory;
import java.util.Random;
import java.util.logging.Logger;

public class SimpleCaptchaUtil
{
    private static final Logger LOGGER;
    private static final Random RANDOM_GENERATOR;
    private static char[] captchars;
    private static GimpyFactory gimpyFactory;
    
    public static String getCaptchaString() {
        final int len = SimpleCaptchaUtil.captchars.length - 1;
        String hipCode = "";
        for (int i = 0; i < 6; ++i) {
            hipCode += SimpleCaptchaUtil.captchars[SimpleCaptchaUtil.RANDOM_GENERATOR.nextInt(len) + 1];
        }
        return hipCode;
    }
    
    public static String generateCAPTCHA(final HttpServletRequest request) {
        final String captcha = getCaptchaString();
        final String digest = HashUtil.SHA512(captcha + ":" + System.currentTimeMillis());
        final SecurityFilterProperties secFilterProps = SecurityFilterProperties.getInstance(request);
        if (secFilterProps != null && secFilterProps.getCaptchaCache() != null) {
            RedisCacheAPI.setDataWithExpireTime(digest, captcha, 300, secFilterProps.getCaptchaCache());
        }
        else {
            SimpleCaptchaUtil.LOGGER.log(Level.FINE, "Store the digest and captcha value in local LRUCacheMap since Redis cache is not enabled");
            InMemCacheAccessInfo.HIP_CACHE.put(digest, captcha);
        }
        return digest;
    }
    
    public static void renderCAPTCHA(final HttpServletRequest request, final HttpServletResponse response, final String captchaStr) throws ServletException, IOException {
        response.setDateHeader("Expires", 0L);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        final ServletOutputStream out = response.getOutputStream();
        if (SimpleCaptchaUtil.gimpyFactory == null) {
            final int[] captchaImageDimensions = SecurityFilterProperties.getInstance(request).getCaptchaImageDimensions();
            if (captchaImageDimensions != null) {
                SimpleCaptchaUtil.gimpyFactory = (GimpyFactory)new CustomCaptchaEngine(captchaImageDimensions).getImageCaptchaFactory();
            }
            else {
                SimpleCaptchaUtil.gimpyFactory = (GimpyFactory)new DefaultGimpyEngine().getImageCaptchaFactory();
            }
        }
        ImageIO.write(SimpleCaptchaUtil.gimpyFactory.getWordToImage().getImage(captchaStr), "jpg", (OutputStream)out);
    }
    
    public static boolean verifyCAPTCHA(final HttpServletRequest request, final String digest, final String captcha) {
        boolean verified = false;
        final SecurityFilterProperties secFilterProps = SecurityFilterProperties.getInstance(request);
        if (digest != null && captcha != null) {
            if (secFilterProps != null && secFilterProps.getCaptchaCache() != null) {
                final String storedCaptcha = RedisCacheAPI.getData(digest, secFilterProps.getCaptchaCache());
                if (storedCaptcha == null) {
                    SimpleCaptchaUtil.LOGGER.log(Level.WARNING, "Invalid captcha digest or the captcha digest gets expired");
                }
                verified = captcha.equals(storedCaptcha);
                RedisCacheAPI.removeData(digest, secFilterProps.getCaptchaCache());
            }
            else {
                SimpleCaptchaUtil.LOGGER.log(Level.FINE, "Retrieve captcha string for the digest from local LRUCacheMap since Redis cache is not enabled");
                final LRUCacheMap<String, String> hipCodeMap = InMemCacheAccessInfo.HIP_CACHE;
                if (hipCodeMap.containsKey(digest)) {
                    verified = captcha.equals(hipCodeMap.get(digest));
                    hipCodeMap.remove(digest);
                }
            }
        }
        return verified;
    }
    
    public static boolean deleteCAPTCHA(final HttpServletRequest request, final String digest) {
        boolean result = false;
        final SecurityFilterProperties secFilterProps = SecurityFilterProperties.getInstance(request);
        if (digest != null) {
            if (secFilterProps != null && secFilterProps.getCaptchaCache() != null) {
                RedisCacheAPI.removeData(digest, secFilterProps.getCaptchaCache());
                result = true;
            }
            else {
                SimpleCaptchaUtil.LOGGER.log(Level.FINE, "Clear the digest from local LRUCacheMap since Redis cache is not enabled");
                InMemCacheAccessInfo.HIP_CACHE.remove(digest);
                result = true;
            }
        }
        return result;
    }
    
    static {
        LOGGER = Logger.getLogger(SimpleCaptchaUtil.class.getName());
        RANDOM_GENERATOR = new Random();
        SimpleCaptchaUtil.captchars = new char[] { 'a', 'b', 'c', 'd', 'e', '2', '3', '4', '5', '6', '7', '8', 'g', 'f', 'y', 'n', 'm', 'n', 'p', 'w', 'x' };
        SimpleCaptchaUtil.gimpyFactory = null;
    }
}
