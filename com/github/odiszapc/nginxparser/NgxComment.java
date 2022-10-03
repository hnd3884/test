package com.github.odiszapc.nginxparser;

public class NgxComment extends NgxAbstractEntry
{
    public NgxComment(final String s) {
        super(new String[0]);
        this.getTokens().add(new NgxToken(s.substring(1)));
    }
    
    @Override
    public String getValue() {
        return this.getName();
    }
    
    @Override
    public String toString() {
        return "#" + this.getValue();
    }
}
