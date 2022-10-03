package org.cyberneko.html;

import java.util.Iterator;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import java.util.ArrayList;
import java.util.List;

class LostText
{
    private final List entries;
    
    LostText() {
        this.entries = new ArrayList();
    }
    
    public void add(final XMLString text, final Augmentations augs) {
        if (!this.entries.isEmpty() || text.toString().trim().length() > 0) {
            this.entries.add(new Entry(text, augs));
        }
    }
    
    public void refeed(final XMLDocumentHandler tagBalancer) {
        final Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            final Entry entry = iter.next();
            tagBalancer.characters(entry.text_, entry.augs_);
        }
        this.entries.clear();
    }
    
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }
    
    static class Entry
    {
        private XMLString text_;
        private Augmentations augs_;
        
        public Entry(final XMLString text, final Augmentations augs) {
            final char[] chars = new char[text.length];
            System.arraycopy(text.ch, text.offset, chars, 0, text.length);
            this.text_ = new XMLString(chars, 0, chars.length);
            if (augs != null) {
                this.augs_ = (Augmentations)new HTMLAugmentations(augs);
            }
        }
    }
}
