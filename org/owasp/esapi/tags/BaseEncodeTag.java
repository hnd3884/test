package org.owasp.esapi.tags;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import org.owasp.esapi.ESAPI;
import javax.servlet.jsp.JspTagException;
import org.owasp.esapi.Encoder;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class BaseEncodeTag extends BodyTagSupport
{
    private static final long serialVersionUID = 1L;
    
    protected abstract String encode(final String p0, final Encoder p1) throws JspTagException;
    
    public int doAfterBody() throws JspTagException {
        String content = this.bodyContent.getString();
        final JspWriter out = this.bodyContent.getEnclosingWriter();
        content = this.encode(content, ESAPI.encoder());
        try {
            out.print(content);
        }
        catch (final IOException e) {
            throw new JspTagException("Error writing to body's enclosing JspWriter", (Throwable)e);
        }
        this.bodyContent.clearBody();
        return 0;
    }
}
