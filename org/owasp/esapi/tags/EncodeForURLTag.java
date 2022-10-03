package org.owasp.esapi.tags;

import org.owasp.esapi.errors.EncodingException;
import javax.servlet.jsp.JspTagException;
import org.owasp.esapi.Encoder;

public class EncodeForURLTag extends BaseEncodeTag
{
    private static final long serialVersionUID = 3L;
    
    @Override
    protected String encode(final String content, final Encoder enc) throws JspTagException {
        try {
            return enc.encodeForURL(content);
        }
        catch (final EncodingException e) {
            throw new JspTagException("Unable to encode to URL encoding", (Throwable)e);
        }
    }
}
