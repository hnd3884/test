package org.apache.tika.sax;

public class Link
{
    private final String type;
    private final String uri;
    private final String title;
    private final String text;
    private final String rel;
    
    public Link(final String type, final String uri, final String title, final String text) {
        this.type = type;
        this.uri = uri;
        this.title = title;
        this.text = text;
        this.rel = "";
    }
    
    public Link(final String type, final String uri, final String title, final String text, final String rel) {
        this.type = type;
        this.uri = uri;
        this.title = title;
        this.text = text;
        this.rel = rel;
    }
    
    public boolean isAnchor() {
        return "a".equals(this.type);
    }
    
    public boolean isImage() {
        return "img".equals(this.type);
    }
    
    public boolean isLink() {
        return "link".equals(this.type);
    }
    
    public boolean isIframe() {
        return "iframe".equals(this.type);
    }
    
    public boolean isScript() {
        return "script".equals(this.type);
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getRel() {
        return this.rel;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (this.isImage()) {
            builder.append("<img src=\"");
            builder.append(this.uri);
            if (this.title != null && this.title.length() > 0) {
                builder.append("\" title=\"");
                builder.append(this.title);
            }
            if (this.text != null && this.text.length() > 0) {
                builder.append("\" alt=\"");
                builder.append(this.text);
            }
            builder.append("\"/>");
        }
        else {
            builder.append("<");
            builder.append(this.type);
            builder.append(" href=\"");
            builder.append(this.uri);
            if (this.title != null && this.title.length() > 0) {
                builder.append("\" title=\"");
                builder.append(this.title);
            }
            if (this.rel != null && this.rel.length() > 0) {
                builder.append("\" rel=\"");
                builder.append(this.rel);
            }
            builder.append("\">");
            builder.append(this.text);
            builder.append("</");
            builder.append(this.type);
            builder.append(">");
        }
        return builder.toString();
    }
}
