package com.octo.captcha.module.config;

import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;

public class CaptchaModuleConfigHelper
{
    public static String getId(final HttpServletRequest httpServletRequest) {
        String s;
        if (CaptchaModuleConfig.getInstance().getIdType().equals("generated")) {
            s = httpServletRequest.getParameter(CaptchaModuleConfig.getInstance().getIdKey());
        }
        else {
            s = httpServletRequest.getSession().getId();
        }
        return s;
    }
    
    public static String getMessage(final HttpServletRequest httpServletRequest) {
        String s = null;
        if (CaptchaModuleConfig.getInstance().getMessageType().equals("bundle")) {
            final ResourceBundle bundle = ResourceBundle.getBundle(CaptchaModuleConfig.getInstance().getMessageValue(), httpServletRequest.getLocale());
            if (bundle != null) {
                s = bundle.getString(CaptchaModuleConfig.getInstance().getMessageKey());
            }
            if (s == null) {
                s = ResourceBundle.getBundle(CaptchaModuleConfig.getInstance().getMessageValue()).getString(CaptchaModuleConfig.getInstance().getMessageKey());
            }
        }
        else {
            s = CaptchaModuleConfig.getInstance().getMessageValue();
        }
        return s;
    }
}
