package com.octo.captcha.service.multitype;

import com.octo.captcha.service.text.TextCaptchaService;
import com.octo.captcha.service.sound.SoundCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

public interface MultiTypeCaptchaService extends ImageCaptchaService, SoundCaptchaService, TextCaptchaService
{
}
