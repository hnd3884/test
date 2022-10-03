package org.apache.http.entity.mime;

import org.apache.http.util.Args;
import org.apache.http.entity.mime.content.ContentBody;

public class FormBodyPart
{
    private final String name;
    private final Header header;
    private final ContentBody body;
    
    public FormBodyPart(final String name, final ContentBody body) {
        Args.notNull((Object)name, "Name");
        Args.notNull((Object)body, "Body");
        this.name = name;
        this.body = body;
        this.header = new Header();
        this.generateContentDisp(body);
        this.generateContentType(body);
        this.generateTransferEncoding(body);
    }
    
    public String getName() {
        return this.name;
    }
    
    public ContentBody getBody() {
        return this.body;
    }
    
    public Header getHeader() {
        return this.header;
    }
    
    public void addField(final String name, final String value) {
        Args.notNull((Object)name, "Field name");
        this.header.addField(new MinimalField(name, value));
    }
    
    protected void generateContentDisp(final ContentBody body) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("form-data; name=\"");
        buffer.append(this.getName());
        buffer.append("\"");
        if (body.getFilename() != null) {
            buffer.append("; filename=\"");
            buffer.append(body.getFilename());
            buffer.append("\"");
        }
        this.addField("Content-Disposition", buffer.toString());
    }
    
    protected void generateContentType(final ContentBody body) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(body.getMimeType());
        if (body.getCharset() != null) {
            buffer.append("; charset=");
            buffer.append(body.getCharset());
        }
        this.addField("Content-Type", buffer.toString());
    }
    
    protected void generateTransferEncoding(final ContentBody body) {
        this.addField("Content-Transfer-Encoding", body.getTransferEncoding());
    }
}
