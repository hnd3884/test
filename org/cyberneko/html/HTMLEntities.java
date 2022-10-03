package org.cyberneko.html;

import java.util.Enumeration;
import java.util.Collections;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;

public class HTMLEntities
{
    protected static final Map ENTITIES;
    protected static final IntProperties SEITITNE;
    
    public static int get(final String name) {
        final String value = HTMLEntities.ENTITIES.get(name);
        return (value != null) ? value.charAt(0) : -1;
    }
    
    public static String get(final int c) {
        return HTMLEntities.SEITITNE.get(c);
    }
    
    private static void load0(final Properties props, final String filename) {
        try {
            final InputStream stream = HTMLEntities.class.getResourceAsStream(filename);
            props.load(stream);
            stream.close();
        }
        catch (final IOException e) {
            System.err.println("error: unable to load resource \"" + filename + "\"");
        }
    }
    
    static {
        SEITITNE = new IntProperties();
        final Properties props = new Properties();
        load0(props, "res/HTMLlat1.properties");
        load0(props, "res/HTMLspecial.properties");
        load0(props, "res/HTMLsymbol.properties");
        load0(props, "res/XMLbuiltin.properties");
        final Enumeration keys = props.propertyNames();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            final String value = props.getProperty(key);
            if (value.length() == 1) {
                final int ivalue = value.charAt(0);
                HTMLEntities.SEITITNE.put(ivalue, key);
            }
        }
        ENTITIES = Collections.unmodifiableMap((Map<?, ?>)new HashMap<Object, Object>(props));
    }
    
    static class IntProperties
    {
        private Entry[] entries;
        
        IntProperties() {
            this.entries = new Entry[101];
        }
        
        public void put(final int key, final String value) {
            final int hash = key % this.entries.length;
            final Entry entry = new Entry(key, value, this.entries[hash]);
            this.entries[hash] = entry;
        }
        
        public String get(final int key) {
            final int hash = key % this.entries.length;
            for (Entry entry = this.entries[hash]; entry != null; entry = entry.next) {
                if (entry.key == key) {
                    return entry.value;
                }
            }
            return null;
        }
        
        static class Entry
        {
            public int key;
            public String value;
            public Entry next;
            
            public Entry(final int key, final String value, final Entry next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }
    }
}
