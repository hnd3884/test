package com.me.devicemanagement.framework.webclient.taglib;

import java.io.IOException;
import com.me.devicemanagement.framework.server.util.Utils;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class DCServerDateTag extends TagSupport
{
    public int doStartTag() throws JspTagException {
        return 1;
    }
    
    public int doEndTag() throws JspTagException {
        try {
            final String currentTime = Utils.getServerTimeInString();
            this.pageContext.getOut().write(currentTime);
        }
        catch (final IOException ex) {
            throw new JspTagException("DC- server date exception");
        }
        return 6;
    }
}
