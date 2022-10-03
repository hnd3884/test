package org.w3c.tidy;

public class Style
{
    protected String tag;
    protected String tagClass;
    protected String properties;
    protected Style next;
    
    public Style(final String tag, final String tagClass, final String properties, final Style next) {
        this.tag = tag;
        this.tagClass = tagClass;
        this.properties = properties;
        this.next = next;
    }
}
