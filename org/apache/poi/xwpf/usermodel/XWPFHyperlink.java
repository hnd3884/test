package org.apache.poi.xwpf.usermodel;

public class XWPFHyperlink
{
    String id;
    String url;
    
    public XWPFHyperlink(final String id, final String url) {
        this.id = id;
        this.url = url;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getURL() {
        return this.url;
    }
}
