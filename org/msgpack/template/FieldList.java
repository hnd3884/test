package org.msgpack.template;

import java.util.List;
import java.util.ArrayList;

public class FieldList
{
    private ArrayList<Entry> list;
    
    public FieldList() {
        this.list = new ArrayList<Entry>();
    }
    
    public void add(final String name) {
        this.add(name, FieldOption.DEFAULT);
    }
    
    public void add(final String name, final FieldOption option) {
        this.list.add(new Entry(name, option));
    }
    
    public void put(final int index, final String name) {
        this.put(index, name, FieldOption.DEFAULT);
    }
    
    public void put(final int index, final String name, final FieldOption option) {
        if (this.list.size() < index) {
            do {
                this.list.add(new Entry());
            } while (this.list.size() < index);
            this.list.add(new Entry(name, option));
        }
        else {
            this.list.set(index, new Entry(name, option));
        }
    }
    
    public List<Entry> getList() {
        return this.list;
    }
    
    public static class Entry
    {
        private String name;
        private FieldOption option;
        
        public Entry() {
            this(null, FieldOption.IGNORE);
        }
        
        public Entry(final String name, final FieldOption option) {
            this.name = name;
            this.option = option;
        }
        
        public String getName() {
            return this.name;
        }
        
        public FieldOption getOption() {
            return this.option;
        }
        
        public boolean isAvailable() {
            return this.option != FieldOption.IGNORE;
        }
    }
}
