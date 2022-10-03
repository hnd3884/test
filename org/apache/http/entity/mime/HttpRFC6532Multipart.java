package org.apache.http.entity.mime;

import java.io.IOException;
import java.util.Iterator;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

class HttpRFC6532Multipart extends AbstractMultipartForm
{
    private final List<FormBodyPart> parts;
    
    public HttpRFC6532Multipart(final String subType, final Charset charset, final String boundary, final List<FormBodyPart> parts) {
        super(subType, charset, boundary);
        this.parts = parts;
    }
    
    @Override
    public List<FormBodyPart> getBodyParts() {
        return this.parts;
    }
    
    @Override
    protected void formatMultipartHeader(final FormBodyPart part, final OutputStream out) throws IOException {
        final Header header = part.getHeader();
        for (final MinimalField field : header) {
            AbstractMultipartForm.writeField(field, MIME.UTF8_CHARSET, out);
        }
    }
}
