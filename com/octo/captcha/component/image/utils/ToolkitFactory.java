package com.octo.captcha.component.image.utils;

import com.octo.captcha.CaptchaException;
import java.awt.Toolkit;

public class ToolkitFactory
{
    public static String TOOLKIT_IMPL;
    
    public static Toolkit getToolkit() {
        Toolkit defaultToolkit;
        try {
            final String property = System.getProperty(ToolkitFactory.TOOLKIT_IMPL);
            if (property != null) {
                defaultToolkit = (Toolkit)Class.forName(property).newInstance();
            }
            else {
                defaultToolkit = getDefaultToolkit();
            }
        }
        catch (final Throwable t) {
            throw new CaptchaException("toolkit has not been abble to be initialized", t);
        }
        return defaultToolkit;
    }
    
    private static Toolkit getDefaultToolkit() {
        return Toolkit.getDefaultToolkit();
    }
    
    static {
        ToolkitFactory.TOOLKIT_IMPL = "toolkit.implementation";
    }
}
