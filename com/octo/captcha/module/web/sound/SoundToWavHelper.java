package com.octo.captcha.module.web.sound;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import com.octo.captcha.service.CaptchaServiceException;
import java.io.OutputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import com.octo.captcha.service.sound.SoundCaptchaService;
import org.apache.commons.logging.Log;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class SoundToWavHelper
{
    public static void flushNewCaptchaToResponse(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final Log log, final SoundCaptchaService soundCaptchaService, final String s, final Locale locale) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            AudioSystem.write(soundCaptchaService.getSoundChallengeForID(s, locale), AudioFileFormat.Type.WAVE, byteArrayOutputStream);
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
        httpServletResponse.setContentType("audio/x-wav");
        final ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(byteArray);
        outputStream.flush();
        outputStream.close();
    }
}
