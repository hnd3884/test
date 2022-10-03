package com.octo.captcha.module.taglib;

import com.octo.captcha.service.CaptchaService;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import com.octo.captcha.module.config.CaptchaModuleConfig;
import javax.servlet.jsp.tagext.Tag;

public class MessageTag extends BaseCaptchaTag implements Tag
{
    private String messageKey;
    
    public MessageTag() {
        this.messageKey = CaptchaModuleConfig.getInstance().getMessageKey();
    }
    
    @Override
    public int doEndTag() throws JspException {
        final String s = (String)this.pageContext.getRequest().getAttribute(this.messageKey);
        if (s != null) {
            try {
                this.pageContext.getOut().write(s);
            }
            catch (final IOException ex) {
                throw new JspException((Throwable)ex);
            }
        }
        return 6;
    }
    
    @Override
    protected CaptchaService getService() {
        return null;
    }
}
