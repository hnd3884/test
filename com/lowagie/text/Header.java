package com.lowagie.text;

public class Header extends Meta
{
    private StringBuffer name;
    
    public Header(final String name, final String content) {
        super(0, content);
        this.name = new StringBuffer(name);
    }
    
    @Override
    public String getName() {
        return this.name.toString();
    }
}
