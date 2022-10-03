package javax.mail;

import java.util.Vector;

public class FetchProfile
{
    private Vector<Item> specials;
    private Vector<String> headers;
    
    public FetchProfile() {
        this.specials = null;
        this.headers = null;
    }
    
    public void add(final Item item) {
        if (this.specials == null) {
            this.specials = new Vector<Item>();
        }
        this.specials.addElement(item);
    }
    
    public void add(final String headerName) {
        if (this.headers == null) {
            this.headers = new Vector<String>();
        }
        this.headers.addElement(headerName);
    }
    
    public boolean contains(final Item item) {
        return this.specials != null && this.specials.contains(item);
    }
    
    public boolean contains(final String headerName) {
        return this.headers != null && this.headers.contains(headerName);
    }
    
    public Item[] getItems() {
        if (this.specials == null) {
            return new Item[0];
        }
        final Item[] s = new Item[this.specials.size()];
        this.specials.copyInto(s);
        return s;
    }
    
    public String[] getHeaderNames() {
        if (this.headers == null) {
            return new String[0];
        }
        final String[] s = new String[this.headers.size()];
        this.headers.copyInto(s);
        return s;
    }
    
    public static class Item
    {
        public static final Item ENVELOPE;
        public static final Item CONTENT_INFO;
        public static final Item SIZE;
        public static final Item FLAGS;
        private String name;
        
        protected Item(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "[" + this.name + "]";
        }
        
        static {
            ENVELOPE = new Item("ENVELOPE");
            CONTENT_INFO = new Item("CONTENT_INFO");
            SIZE = new Item("SIZE");
            FLAGS = new Item("FLAGS");
        }
    }
}
