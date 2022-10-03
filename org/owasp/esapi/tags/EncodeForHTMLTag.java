package org.owasp.esapi.tags;

import org.owasp.esapi.Encoder;

public class EncodeForHTMLTag extends BaseEncodeTag
{
    private static final long serialVersionUID = 3L;
    
    @Override
    protected String encode(final String content, final Encoder enc) {
        return enc.encodeForHTML(content);
    }
}
