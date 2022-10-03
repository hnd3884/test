package com.octo.captcha.module.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

public abstract class QuestionTag extends BaseCaptchaTag implements Tag
{
    @Override
    public int doEndTag() throws JspException {
        final String questionForID = this.getService().getQuestionForID(this.pageContext.getSession().getId(), this.pageContext.getRequest().getLocale());
        try {
            this.pageContext.getOut().write(questionForID);
        }
        catch (final IOException ex) {
            throw new JspException((Throwable)ex);
        }
        return 6;
    }
}
