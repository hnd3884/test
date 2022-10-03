package org.w3c.tidy;

public class StyleProp
{
    protected String name;
    protected String value;
    protected StyleProp next;
    
    public StyleProp(final String name, final String value, final StyleProp next) {
        this.name = name;
        this.value = value;
        this.next = next;
    }
}
