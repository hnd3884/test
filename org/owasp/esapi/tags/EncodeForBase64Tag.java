package org.owasp.esapi.tags;

import java.io.UnsupportedEncodingException;
import javax.servlet.jsp.JspTagException;
import org.owasp.esapi.Encoder;

public class EncodeForBase64Tag extends BaseEncodeTag
{
    private static final long serialVersionUID = 3L;
    private boolean wrap;
    private String encoding;
    
    public EncodeForBase64Tag() {
        this.wrap = false;
        this.encoding = "UTF-8";
    }
    
    @Override
    protected String encode(final String content, final Encoder enc) throws JspTagException {
        try {
            return enc.encodeForBase64(content.getBytes(this.encoding), this.wrap);
        }
        catch (final UnsupportedEncodingException e) {
            throw new JspTagException("Unsupported encoding " + enc, (Throwable)e);
        }
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setWrap(final boolean wrap) {
        this.wrap = wrap;
    }
    
    public boolean getWrap() {
        return this.wrap;
    }
}
