package com.me.devicemanagement.onpremise.server.extensions.processbuilder;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class DMProcessEnvironment extends HashMap<String, String>
{
    private static final long serialVersionUID = -8017839552603542824L;
    private static final String DLL_LIBRARY_FILE_NAME = "SyMNative";
    static final int MIN_NAME_LENGTH = 1;
    private static final NameComparator NAMECOMPARATOR;
    private static final EntryComparator ENTRYCOMPARATOR;
    private static final DMProcessEnvironment THEENVIRONMENT;
    private static final Map<String, String> THEUNMODIFIABLEENVIRONMENT;
    private static final Map<String, String> THECASEINSENSITIVEENVIRONMENT;
    
    private static String validateName(final String name) {
        if (name.indexOf(61, 1) != -1 || name.indexOf(0) != -1) {
            throw new IllegalArgumentException("Invalid environment variable name: \"" + name + "\"");
        }
        return name;
    }
    
    private static String validateValue(final String value) {
        if (value.indexOf(0) != -1) {
            throw new IllegalArgumentException("Invalid environment variable value: \"" + value + "\"");
        }
        return value;
    }
    
    private static String nonNullString(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return (String)o;
    }
    
    @Override
    public String put(final String key, final String value) {
        return super.put(validateName(key), validateValue(value));
    }
    
    @Override
    public String get(final Object key) {
        return super.get(nonNullString(key));
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return super.containsKey(nonNullString(key));
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return super.containsValue(nonNullString(value));
    }
    
    @Override
    public String remove(final Object key) {
        return super.remove(nonNullString(key));
    }
    
    @Override
    public Set<String> keySet() {
        return new CheckedKeySet(super.keySet());
    }
    
    @Override
    public Collection<String> values() {
        return new CheckedValues(super.values());
    }
    
    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return new CheckedEntrySet(super.entrySet());
    }
    
    private DMProcessEnvironment() {
    }
    
    private DMProcessEnvironment(final int capacity) {
        super(capacity);
    }
    
    static String getenv(final String name) {
        return DMProcessEnvironment.THECASEINSENSITIVEENVIRONMENT.get(name);
    }
    
    static Map<String, String> getenv() {
        return DMProcessEnvironment.THEUNMODIFIABLEENVIRONMENT;
    }
    
    static Map<String, String> environment() {
        return (Map)DMProcessEnvironment.THEENVIRONMENT.clone();
    }
    
    static Map<String, String> emptyEnvironment(final int capacity) {
        return new DMProcessEnvironment(capacity);
    }
    
    private static native String environmentBlock();
    
    String toEnvironmentBlock() {
        final List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(this.entrySet());
        Collections.sort(list, DMProcessEnvironment.ENTRYCOMPARATOR);
        final StringBuilder sb = new StringBuilder(this.size() * 30);
        int cmp = -1;
        final String SYSTEMROOT = "SystemRoot";
        for (final Map.Entry<String, String> e : list) {
            final String key = e.getKey();
            final String value = e.getValue();
            if (cmp < 0 && (cmp = DMProcessEnvironment.NAMECOMPARATOR.compare(key, "SystemRoot")) > 0) {
                addToEnvIfSet(sb, "SystemRoot");
            }
            addToEnv(sb, key, value);
        }
        if (cmp < 0) {
            addToEnvIfSet(sb, "SystemRoot");
        }
        if (sb.length() == 0) {
            sb.append('\0');
        }
        sb.append('\0');
        return sb.toString();
    }
    
    private static void addToEnvIfSet(final StringBuilder sb, final String name) {
        final String s = getenv(name);
        if (s != null) {
            addToEnv(sb, name, s);
        }
    }
    
    private static void addToEnv(final StringBuilder sb, final String name, final String val) {
        sb.append(name).append('=').append(val).append('\0');
    }
    
    static String toEnvironmentBlock(final Map<String, String> map) {
        return (map == null) ? null : ((DMProcessEnvironment)map).toEnvironmentBlock();
    }
    
    static {
        System.loadLibrary("SyMNative");
        NAMECOMPARATOR = new NameComparator();
        ENTRYCOMPARATOR = new EntryComparator();
        THEENVIRONMENT = new DMProcessEnvironment();
        THEUNMODIFIABLEENVIRONMENT = Collections.unmodifiableMap((Map<? extends String, ? extends String>)DMProcessEnvironment.THEENVIRONMENT);
        final String envblock = environmentBlock();
        int end;
        int eql;
        for (int beg = 0; (end = envblock.indexOf(0, beg)) != -1 && (eql = envblock.indexOf(61, beg + 1)) != -1; beg = end + 1) {
            if (eql < end) {
                DMProcessEnvironment.THEENVIRONMENT.put(envblock.substring(beg, eql), envblock.substring(eql + 1, end));
            }
        }
        (THECASEINSENSITIVEENVIRONMENT = new TreeMap<String, String>(DMProcessEnvironment.NAMECOMPARATOR)).putAll(DMProcessEnvironment.THEENVIRONMENT);
    }
    
    private static class CheckedEntry implements Map.Entry<String, String>
    {
        private final Map.Entry<String, String> e;
        
        public CheckedEntry(final Map.Entry<String, String> e) {
            this.e = e;
        }
        
        @Override
        public String getKey() {
            return this.e.getKey();
        }
        
        @Override
        public String getValue() {
            return this.e.getValue();
        }
        
        @Override
        public String setValue(final String value) {
            return this.e.setValue(validateValue(value));
        }
        
        @Override
        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }
        
        @Override
        public boolean equals(final Object o) {
            return this.e.equals(o);
        }
        
        @Override
        public int hashCode() {
            return this.e.hashCode();
        }
    }
    
    private static class CheckedEntrySet extends AbstractSet<Map.Entry<String, String>>
    {
        private final Set<Map.Entry<String, String>> s;
        
        public CheckedEntrySet(final Set<Map.Entry<String, String>> s) {
            this.s = s;
        }
        
        @Override
        public int size() {
            return this.s.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.s.isEmpty();
        }
        
        @Override
        public void clear() {
            this.s.clear();
        }
        
        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return new Iterator<Map.Entry<String, String>>() {
                Iterator<Map.Entry<String, String>> i = CheckedEntrySet.this.s.iterator();
                
                @Override
                public boolean hasNext() {
                    return this.i.hasNext();
                }
                
                @Override
                public Map.Entry<String, String> next() {
                    return new CheckedEntry(this.i.next());
                }
                
                @Override
                public void remove() {
                    this.i.remove();
                }
            };
        }
        
        private static Map.Entry<String, String> checkedEntry(final Object o) {
            final Map.Entry<String, String> e = (Map.Entry<String, String>)o;
            nonNullString(e.getKey());
            nonNullString(e.getValue());
            return e;
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.s.contains(checkedEntry(o));
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.s.remove(checkedEntry(o));
        }
    }
    
    private static class CheckedValues extends AbstractCollection<String>
    {
        private final Collection<String> c;
        
        public CheckedValues(final Collection<String> c) {
            this.c = c;
        }
        
        @Override
        public int size() {
            return this.c.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.c.isEmpty();
        }
        
        @Override
        public void clear() {
            this.c.clear();
        }
        
        @Override
        public Iterator<String> iterator() {
            return this.c.iterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.c.contains(nonNullString(o));
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.c.remove(nonNullString(o));
        }
    }
    
    private static class CheckedKeySet extends AbstractSet<String>
    {
        private final Set<String> s;
        
        public CheckedKeySet(final Set<String> s) {
            this.s = s;
        }
        
        @Override
        public int size() {
            return this.s.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.s.isEmpty();
        }
        
        @Override
        public void clear() {
            this.s.clear();
        }
        
        @Override
        public Iterator<String> iterator() {
            return this.s.iterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.s.contains(nonNullString(o));
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.s.remove(nonNullString(o));
        }
    }
    
    private static final class NameComparator implements Comparator<String>
    {
        @Override
        public int compare(final String s1, final String s2) {
            final int n1 = s1.length();
            final int n2 = s2.length();
            for (int min = Math.min(n1, n2), i = 0; i < min; ++i) {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(i);
                if (c1 != c2) {
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    if (c1 != c2) {
                        return c1 - c2;
                    }
                }
            }
            return n1 - n2;
        }
    }
    
    private static final class EntryComparator implements Comparator<Map.Entry<String, String>>
    {
        @Override
        public int compare(final Map.Entry<String, String> e1, final Map.Entry<String, String> e2) {
            return DMProcessEnvironment.NAMECOMPARATOR.compare((String)e1.getKey(), (String)e2.getKey());
        }
    }
}
