package com.octo.captcha.module.web.image;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import com.octo.captcha.service.CaptchaServiceException;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.apache.commons.logging.Log;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ImageToJpegHelper
{
    public static void flushNewCaptchaToResponse(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final Log log, final ImageCaptchaService imageCaptchaService, final String s, final Locale locale) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(imageCaptchaService.getImageChallengeForID(s, locale), "png", byteArrayOutputStream);
        }
        catch (final IllegalArgumentException ex) {
            if (log != null && log.isWarnEnabled()) {
                log.warn((Object)("There was a try from " + httpServletRequest.getRemoteAddr() + " to render an captcha with invalid ID :'" + s + "' or with a too long one"));
                httpServletResponse.sendError(404);
                return;
            }
        }
        catch (final CaptchaServiceException ex2) {
            if (log != null && log.isWarnEnabled()) {
                log.warn((Object)"Error trying to generate a captcha and render its challenge as JPEG", (Throwable)ex2);
            }
            httpServletResponse.sendError(404);
            return;
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0L);
        httpServletResponse.setContentType("image/jpeg");
        final ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(byteArray);
        outputStream.flush();
        outputStream.close();
    }
}
