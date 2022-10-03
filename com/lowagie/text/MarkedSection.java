package com.lowagie.text;

import java.util.Collection;
import java.util.Iterator;

public class MarkedSection extends MarkedObject
{
    protected MarkedObject title;
    
    public MarkedSection(final Section section) {
        this.title = null;
        if (section.title != null) {
            this.title = new MarkedObject(section.title);
            section.setTitle(null);
        }
        this.element = section;
    }
    
    public void add(final int index, final Object o) {
        ((Section)this.element).add(index, o);
    }
    
    public boolean add(final Object o) {
        return ((Section)this.element).add(o);
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            for (final Element element : (Section)this.element) {
                listener.add(element);
            }
            return true;
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    public boolean addAll(final Collection collection) {
        return ((Section)this.element).addAll(collection);
    }
    
    public MarkedSection addSection(final float indentation, final int numberDepth) {
        final MarkedSection section = ((Section)this.element).addMarkedSection();
        section.setIndentation(indentation);
        section.setNumberDepth(numberDepth);
        return section;
    }
    
    public MarkedSection addSection(final float indentation) {
        final MarkedSection section = ((Section)this.element).addMarkedSection();
        section.setIndentation(indentation);
        return section;
    }
    
    public MarkedSection addSection(final int numberDepth) {
        final MarkedSection section = ((Section)this.element).addMarkedSection();
        section.setNumberDepth(numberDepth);
        return section;
    }
    
    public MarkedSection addSection() {
        return ((Section)this.element).addMarkedSection();
    }
    
    public void setTitle(final MarkedObject title) {
        if (title.element instanceof Paragraph) {
            this.title = title;
        }
    }
    
    public MarkedObject getTitle() {
        final Paragraph result = Section.constructTitle((Paragraph)this.title.element, ((Section)this.element).numbers, ((Section)this.element).numberDepth, ((Section)this.element).numberStyle);
        final MarkedObject mo = new MarkedObject(result);
        mo.markupAttributes = this.title.markupAttributes;
        return mo;
    }
    
    public void setNumberDepth(final int numberDepth) {
        ((Section)this.element).setNumberDepth(numberDepth);
    }
    
    public void setIndentationLeft(final float indentation) {
        ((Section)this.element).setIndentationLeft(indentation);
    }
    
    public void setIndentationRight(final float indentation) {
        ((Section)this.element).setIndentationRight(indentation);
    }
    
    public void setIndentation(final float indentation) {
        ((Section)this.element).setIndentation(indentation);
    }
    
    public void setBookmarkOpen(final boolean bookmarkOpen) {
        ((Section)this.element).setBookmarkOpen(bookmarkOpen);
    }
    
    public void setTriggerNewPage(final boolean triggerNewPage) {
        ((Section)this.element).setTriggerNewPage(triggerNewPage);
    }
    
    public void setBookmarkTitle(final String bookmarkTitle) {
        ((Section)this.element).setBookmarkTitle(bookmarkTitle);
    }
    
    public void newPage() {
        ((Section)this.element).newPage();
    }
}
