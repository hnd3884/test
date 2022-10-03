package java.lang;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

final class ProcessEnvironment extends HashMap<String, String>
{
    private static final long serialVersionUID = -8017839552603542824L;
    static final int MIN_NAME_LENGTH = 1;
    private static final NameComparator nameComparator;
    private static final EntryComparator entryComparator;
    private static final ProcessEnvironment theEnvironment;
    private static final Map<String, String> theUnmodifiableEnvironment;
    private static final Map<String, String> theCaseInsensitiveEnvironment;
    
    private static String validateName(final String s) {
        if (s.indexOf(61, 1) != -1 || s.indexOf(0) != -1) {
            throw new IllegalArgumentException("Invalid environment variable name: \"" + s + "\"");
        }
        return s;
    }
    
    private static String validateValue(final String s) {
        if (s.indexOf(0) != -1) {
            throw new IllegalArgumentException("Invalid environment variable value: \"" + s + "\"");
        }
        return s;
    }
    
    private static String nonNullString(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return (String)o;
    }
    
    @Override
    public String put(final String s, final String s2) {
        return super.put(validateName(s), validateValue(s2));
    }
    
    @Override
    public String get(final Object o) {
        return super.get(nonNullString(o));
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return super.containsKey(nonNullString(o));
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return super.containsValue(nonNullString(o));
    }
    
    @Override
    public String remove(final Object o) {
        return super.remove(nonNullString(o));
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
    
    private ProcessEnvironment() {
    }
    
    private ProcessEnvironment(final int n) {
        super(n);
    }
    
    static String getenv(final String s) {
        return ProcessEnvironment.theCaseInsensitiveEnvironment.get(s);
    }
    
    static Map<String, String> getenv() {
        return ProcessEnvironment.theUnmodifiableEnvironment;
    }
    
    static Map<String, String> environment() {
        return (Map)ProcessEnvironment.theEnvironment.clone();
    }
    
    static Map<String, String> emptyEnvironment(final int n) {
        return new ProcessEnvironment(n);
    }
    
    private static native String environmentBlock();
    
    String toEnvironmentBlock() {
        final ArrayList list = new ArrayList((Collection<? extends E>)this.entrySet());
        Collections.sort((List<Object>)list, (Comparator<? super Object>)ProcessEnvironment.entryComparator);
        final StringBuilder sb = new StringBuilder(this.size() * 30);
        int compare = -1;
        for (final Map.Entry entry : list) {
            final String s = (String)entry.getKey();
            final String s2 = (String)entry.getValue();
            if (compare < 0 && (compare = ProcessEnvironment.nameComparator.compare(s, "SystemRoot")) > 0) {
                addToEnvIfSet(sb, "SystemRoot");
            }
            addToEnv(sb, s, s2);
        }
        if (compare < 0) {
            addToEnvIfSet(sb, "SystemRoot");
        }
        if (sb.length() == 0) {
            sb.append('\0');
        }
        sb.append('\0');
        return sb.toString();
    }
    
    private static void addToEnvIfSet(final StringBuilder sb, final String s) {
        final String getenv = getenv(s);
        if (getenv != null) {
            addToEnv(sb, s, getenv);
        }
    }
    
    private static void addToEnv(final StringBuilder sb, final String s, final String s2) {
        sb.append(s).append('=').append(s2).append('\0');
    }
    
    static String toEnvironmentBlock(final Map<String, String> map) {
        return (map == null) ? null : ((ProcessEnvironment)map).toEnvironmentBlock();
    }
    
    static {
        nameComparator = new NameComparator();
        entryComparator = new EntryComparator();
        theEnvironment = new ProcessEnvironment();
        theUnmodifiableEnvironment = Collections.unmodifiableMap((Map<? extends String, ? extends String>)ProcessEnvironment.theEnvironment);
        final String environmentBlock = environmentBlock();
        int index;
        int index2;
        for (int n = 0; (index = environmentBlock.indexOf(0, n)) != -1 && (index2 = environmentBlock.indexOf(61, n + 1)) != -1; n = index + 1) {
            if (index2 < index) {
                ProcessEnvironment.theEnvironment.put(environmentBlock.substring(n, index2), environmentBlock.substring(index2 + 1, index));
            }
        }
        (theCaseInsensitiveEnvironment = new TreeMap<String, String>(ProcessEnvironment.nameComparator)).putAll(ProcessEnvironment.theEnvironment);
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
        public String setValue(final String s) {
            return this.e.setValue(validateValue(s));
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
            final Map.Entry entry = (Map.Entry)o;
            nonNullString(entry.getKey());
            nonNullString(entry.getValue());
            return entry;
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
        public int compare(final String s, final String s2) {
            final int length = s.length();
            final int length2 = s2.length();
            for (int min = Math.min(length, length2), i = 0; i < min; ++i) {
                final char char1 = s.charAt(i);
                final char char2 = s2.charAt(i);
                if (char1 != char2) {
                    final char upperCase = Character.toUpperCase(char1);
                    final char upperCase2 = Character.toUpperCase(char2);
                    if (upperCase != upperCase2) {
                        return upperCase - upperCase2;
                    }
                }
            }
            return length - length2;
        }
    }
    
    private static final class EntryComparator implements Comparator<Map.Entry<String, String>>
    {
        @Override
        public int compare(final Map.Entry<String, String> entry, final Map.Entry<String, String> entry2) {
            return ProcessEnvironment.nameComparator.compare((String)entry.getKey(), (String)entry2.getKey());
        }
    }
}
