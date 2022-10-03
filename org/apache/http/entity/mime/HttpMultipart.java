package org.apache.http.entity.mime;

import java.io.IOException;
import java.util.Iterator;
import java.io.OutputStream;
import java.util.ArrayList;
import java.nio.charset.Charset;
import java.util.List;

@Deprecated
public class HttpMultipart extends AbstractMultipartForm
{
    private final HttpMultipartMode mode;
    private final List<FormBodyPart> parts;
    
    public HttpMultipart(final String subType, final Charset charset, final String boundary, final HttpMultipartMode mode) {
        super(subType, charset, boundary);
        this.mode = mode;
        this.parts = new ArrayList<FormBodyPart>();
    }
    
    public HttpMultipart(final String subType, final Charset charset, final String boundary) {
        this(subType, charset, boundary, HttpMultipartMode.STRICT);
    }
    
    public HttpMultipart(final String subType, final String boundary) {
        this(subType, null, boundary);
    }
    
    public HttpMultipartMode getMode() {
        return this.mode;
    }
    
    @Override
    protected void formatMultipartHeader(final FormBodyPart part, final OutputStream out) throws IOException {
        final Header header = part.getHeader();
        switch (this.mode) {
            case BROWSER_COMPATIBLE: {
                final MinimalField cd = header.getField("Content-Disposition");
                AbstractMultipartForm.writeField(cd, this.charset, out);
                final String filename = part.getBody().getFilename();
                if (filename != null) {
                    final MinimalField ct = header.getField("Content-Type");
                    AbstractMultipartForm.writeField(ct, this.charset, out);
                    break;
                }
                break;
            }
            default: {
                for (final MinimalField field : header) {
                    AbstractMultipartForm.writeField(field, out);
                }
                break;
            }
        }
    }
    
    @Override
    public List<FormBodyPart> getBodyParts() {
        return this.parts;
    }
    
    public void addBodyPart(final FormBodyPart part) {
        if (part == null) {
            return;
        }
        this.parts.add(part);
    }
}
