package org.apache.tika.sax;

class LinkBuilder
{
    private final String type;
    private final StringBuilder text;
    private String uri;
    private String title;
    private String rel;
    
    public LinkBuilder(final String type) {
        this.text = new StringBuilder();
        this.uri = "";
        this.title = "";
        this.rel = "";
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setURI(final String uri) {
        if (uri != null) {
            this.uri = uri;
        }
        else {
            this.uri = "";
        }
    }
    
    public void setTitle(final String title) {
        if (title != null) {
            this.title = title;
        }
        else {
            this.title = "";
        }
    }
    
    public void setRel(final String rel) {
        if (rel != null) {
            this.rel = rel;
        }
        else {
            this.rel = "";
        }
    }
    
    public void characters(final char[] ch, final int offset, final int length) {
        this.text.append(ch, offset, length);
    }
    
    public Link getLink() {
        return this.getLink(false);
    }
    
    public Link getLink(final boolean collapseWhitespace) {
        String anchor = this.text.toString();
        if (collapseWhitespace) {
            anchor = anchor.replaceAll("\\s+", " ").trim();
        }
        return new Link(this.type, this.uri, this.title, anchor, this.rel);
    }
}
