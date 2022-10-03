package sun.swing;

import java.awt.Image;
import java.util.ListIterator;
import java.awt.GraphicsConfiguration;
import java.lang.ref.SoftReference;
import java.util.LinkedList;

public class ImageCache
{
    private int maxCount;
    private final LinkedList<SoftReference<Entry>> entries;
    
    public ImageCache(final int maxCount) {
        this.maxCount = maxCount;
        this.entries = new LinkedList<SoftReference<Entry>>();
    }
    
    void setMaxCount(final int maxCount) {
        this.maxCount = maxCount;
    }
    
    public void flush() {
        this.entries.clear();
    }
    
    private Entry getEntry(final Object o, final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final Object[] array) {
        final ListIterator<Object> listIterator = this.entries.listIterator();
        while (listIterator.hasNext()) {
            final SoftReference softReference = listIterator.next();
            final Entry entry = (Entry)softReference.get();
            if (entry == null) {
                listIterator.remove();
            }
            else {
                if (entry.equals(graphicsConfiguration, n, n2, array)) {
                    listIterator.remove();
                    this.entries.addFirst(softReference);
                    return entry;
                }
                continue;
            }
        }
        final Entry entry2 = new Entry(graphicsConfiguration, n, n2, array);
        if (this.entries.size() >= this.maxCount) {
            this.entries.removeLast();
        }
        this.entries.addFirst(new SoftReference<Entry>(entry2));
        return entry2;
    }
    
    public Image getImage(final Object o, final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final Object[] array) {
        return this.getEntry(o, graphicsConfiguration, n, n2, array).getImage();
    }
    
    public void setImage(final Object o, final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final Object[] array, final Image image) {
        this.getEntry(o, graphicsConfiguration, n, n2, array).setImage(image);
    }
    
    private static class Entry
    {
        private final GraphicsConfiguration config;
        private final int w;
        private final int h;
        private final Object[] args;
        private Image image;
        
        Entry(final GraphicsConfiguration config, final int w, final int h, final Object[] args) {
            this.config = config;
            this.args = args;
            this.w = w;
            this.h = h;
        }
        
        public void setImage(final Image image) {
            this.image = image;
        }
        
        public Image getImage() {
            return this.image;
        }
        
        @Override
        public String toString() {
            String s = super.toString() + "[ graphicsConfig=" + this.config + ", image=" + this.image + ", w=" + this.w + ", h=" + this.h;
            if (this.args != null) {
                for (int i = 0; i < this.args.length; ++i) {
                    s = s + ", " + this.args[i];
                }
            }
            return s + "]";
        }
        
        public boolean equals(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final Object[] array) {
            if (this.w == n && this.h == n2 && ((this.config != null && this.config.equals(graphicsConfiguration)) || (this.config == null && graphicsConfiguration == null))) {
                if (this.args == null && array == null) {
                    return true;
                }
                if (this.args != null && array != null && this.args.length == array.length) {
                    for (int i = array.length - 1; i >= 0; --i) {
                        final Object o = this.args[i];
                        final Object o2 = array[i];
                        if ((o == null && o2 != null) || (o != null && !o.equals(o2))) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }
}
