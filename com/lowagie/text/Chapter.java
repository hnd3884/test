package com.lowagie.text;

import java.util.ArrayList;

public class Chapter extends Section
{
    private static final long serialVersionUID = 1791000695779357361L;
    
    public Chapter(final int number) {
        super(null, 1);
        (this.numbers = new ArrayList()).add(new Integer(number));
        this.triggerNewPage = true;
    }
    
    public Chapter(final Paragraph title, final int number) {
        super(title, 1);
        (this.numbers = new ArrayList()).add(new Integer(number));
        this.triggerNewPage = true;
    }
    
    public Chapter(final String title, final int number) {
        this(new Paragraph(title), number);
    }
    
    @Override
    public int type() {
        return 16;
    }
    
    @Override
    public boolean isNestable() {
        return false;
    }
}
