package com.octo.captcha.service.text;

import java.util.Locale;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.CaptchaService;

public interface TextCaptchaService extends CaptchaService
{
    String getTextChallengeForID(final String p0) throws CaptchaServiceException;
    
    String getTextChallengeForID(final String p0, final Locale p1) throws CaptchaServiceException;
}
