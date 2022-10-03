package com.octo.captcha.service.sound;

import java.util.Locale;
import com.octo.captcha.service.CaptchaServiceException;
import javax.sound.sampled.AudioInputStream;
import com.octo.captcha.service.CaptchaService;

public interface SoundCaptchaService extends CaptchaService
{
    AudioInputStream getSoundChallengeForID(final String p0) throws CaptchaServiceException;
    
    AudioInputStream getSoundChallengeForID(final String p0, final Locale p1) throws CaptchaServiceException;
}
