package org.htmlparser.visitors;

import org.htmlparser.tags.LinkTag;
import org.htmlparser.Tag;
import java.util.Locale;

public class LinkFindingVisitor extends NodeVisitor
{
    private String linkTextToFind;
    private int count;
    private Locale locale;
    
    public LinkFindingVisitor(final String linkTextToFind) {
        this(linkTextToFind, null);
    }
    
    public LinkFindingVisitor(final String linkTextToFind, final Locale locale) {
        this.count = 0;
        this.locale = ((null == locale) ? Locale.ENGLISH : locale);
        this.linkTextToFind = linkTextToFind.toUpperCase(this.locale);
    }
    
    public void visitTag(final Tag tag) {
        if (tag instanceof LinkTag && -1 != ((LinkTag)tag).getLinkText().toUpperCase(this.locale).indexOf(this.linkTextToFind)) {
            ++this.count;
        }
    }
    
    public boolean linkTextFound() {
        return 0 != this.count;
    }
    
    public int getCount() {
        return this.count;
    }
}
