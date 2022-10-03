package com.lowagie.text;

import java.util.ArrayList;

public class Meta implements Element
{
    private int type;
    private StringBuffer content;
    
    Meta(final int type, final String content) {
        this.type = type;
        this.content = new StringBuffer(content);
    }
    
    public Meta(final String tag, final String content) {
        this.type = getType(tag);
        this.content = new StringBuffer(content);
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return this.type;
    }
    
    @Override
    public ArrayList getChunks() {
        return new ArrayList();
    }
    
    @Override
    public boolean isContent() {
        return false;
    }
    
    @Override
    public boolean isNestable() {
        return false;
    }
    
    public StringBuffer append(final String string) {
        return this.content.append(string);
    }
    
    public String getContent() {
        return this.content.toString();
    }
    
    public String getName() {
        switch (this.type) {
            case 2: {
                return "subject";
            }
            case 3: {
                return "keywords";
            }
            case 4: {
                return "author";
            }
            case 1: {
                return "title";
            }
            case 5: {
                return "producer";
            }
            case 6: {
                return "creationdate";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    public static int getType(final String tag) {
        if ("subject".equals(tag)) {
            return 2;
        }
        if ("keywords".equals(tag)) {
            return 3;
        }
        if ("author".equals(tag)) {
            return 4;
        }
        if ("title".equals(tag)) {
            return 1;
        }
        if ("producer".equals(tag)) {
            return 5;
        }
        if ("creationdate".equals(tag)) {
            return 6;
        }
        return 0;
    }
}
