package org.cyberneko.html;

import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.xerces.xni.Augmentations;

public class HTMLAugmentations implements Augmentations
{
    protected final Hashtable fItems;
    
    public HTMLAugmentations() {
        this.fItems = new Hashtable();
    }
    
    HTMLAugmentations(final Augmentations augs) {
        this.fItems = new Hashtable();
        final Enumeration keys = augs.keys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            Object value = augs.getItem(key);
            if (value instanceof HTMLScanner.LocationItem) {
                value = new HTMLScanner.LocationItem((HTMLScanner.LocationItem)value);
            }
            this.fItems.put(key, value);
        }
    }
    
    public void removeAllItems() {
        this.fItems.clear();
    }
    
    public void clear() {
        this.fItems.clear();
    }
    
    public Object putItem(final String key, final Object item) {
        return this.fItems.put(key, item);
    }
    
    public Object getItem(final String key) {
        return this.fItems.get(key);
    }
    
    public Object removeItem(final String key) {
        return this.fItems.remove(key);
    }
    
    public Enumeration keys() {
        return this.fItems.keys();
    }
}
