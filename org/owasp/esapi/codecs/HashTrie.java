package org.owasp.esapi.codecs;

import java.util.HashMap;
import org.owasp.esapi.util.NullSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.Map;

public class HashTrie<T> implements Trie<T>
{
    private Node<T> root;
    private int maxKeyLen;
    private int size;
    
    public HashTrie() {
        this.clear();
    }
    
    @Override
    public Map.Entry<CharSequence, T> getLongestMatch(final CharSequence key) {
        if (this.root == null || key == null) {
            return null;
        }
        return this.root.getLongestMatch(key, 0);
    }
    
    @Override
    public Map.Entry<CharSequence, T> getLongestMatch(final PushbackReader keyIn) throws IOException {
        if (this.root == null || keyIn == null) {
            return null;
        }
        return this.root.getLongestMatch(keyIn, new StringBuilder());
    }
    
    @Override
    public int getMaxKeyLength() {
        return this.maxKeyLen;
    }
    
    @Override
    public void clear() {
        this.root = null;
        this.maxKeyLen = -1;
        this.size = 0;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.get(key) != null;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.root != null && this.root.containsValue(value);
    }
    
    @Override
    public T put(final CharSequence key, final T value) throws NullPointerException {
        if (key == null) {
            throw new NullPointerException("Null keys are not handled");
        }
        if (value == null) {
            throw new NullPointerException("Null values are not handled");
        }
        if (this.root == null) {
            this.root = new Node<T>();
        }
        final T old;
        if ((old = this.root.put(key, 0, value)) != null) {
            return old;
        }
        final int len;
        if ((len = key.length()) > this.maxKeyLen) {
            this.maxKeyLen = len;
        }
        ++this.size;
        return null;
    }
    
    @Override
    public T remove(final Object key) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(final Map<? extends CharSequence, ? extends T> map) {
        for (final Map.Entry<? extends CharSequence, ? extends T> entry : map.entrySet()) {
            this.put((CharSequence)entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public Set<CharSequence> keySet() {
        final Set<CharSequence> keys = new HashSet<CharSequence>(this.size);
        if (this.root == null) {
            return keys;
        }
        return this.root.keySet(new StringBuilder(), keys);
    }
    
    @Override
    public Collection<T> values() {
        final ArrayList<T> values = new ArrayList<T>(this.size());
        if (this.root == null) {
            return values;
        }
        return this.root.values(values);
    }
    
    @Override
    public Set<Map.Entry<CharSequence, T>> entrySet() {
        final Set<Map.Entry<CharSequence, T>> entries = new HashSet<Map.Entry<CharSequence, T>>(this.size());
        if (this.root == null) {
            return entries;
        }
        return this.root.entrySet(new StringBuilder(), entries);
    }
    
    @Override
    public T get(final Object key) {
        if (this.root == null || key == null) {
            return null;
        }
        if (!(key instanceof CharSequence)) {
            return null;
        }
        return this.root.get((CharSequence)key, 0);
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other instanceof Map && this.entrySet().equals(((Map)other).entrySet());
    }
    
    @Override
    public int hashCode() {
        return this.entrySet().hashCode();
    }
    
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("{ ");
        for (final Map.Entry<CharSequence, T> entry : this.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                sb.append(", ");
            }
            sb.append(entry.toString());
        }
        sb.append(" }");
        return sb.toString();
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    private static class Entry<T> implements Map.Entry<CharSequence, T>
    {
        private CharSequence key;
        private T value;
        
        Entry(final CharSequence key, final T value) {
            this.key = key;
            this.value = value;
        }
        
        static <T> Entry<T> newInstanceIfNeeded(CharSequence key, final int keyLength, final T value) {
            if (value == null || key == null) {
                return null;
            }
            if (key.length() > keyLength) {
                key = key.subSequence(0, keyLength);
            }
            return new Entry<T>(key, value);
        }
        
        static <T> Entry<T> newInstanceIfNeeded(final CharSequence key, final T value) {
            if (value == null || key == null) {
                return null;
            }
            return new Entry<T>(key, value);
        }
        
        @Override
        public CharSequence getKey() {
            return this.key;
        }
        
        @Override
        public T getValue() {
            return this.value;
        }
        
        @Override
        public T setValue(final T value) {
            throw new UnsupportedOperationException();
        }
        
        public boolean equals(final Map.Entry other) {
            return NullSafe.equals(this.key, other.getKey()) && NullSafe.equals(this.value, other.getValue());
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Map.Entry && this.equals((Map.Entry)o);
        }
        
        @Override
        public int hashCode() {
            return NullSafe.hashCode(this.key) ^ NullSafe.hashCode(this.value);
        }
        
        @Override
        public String toString() {
            return NullSafe.toString(this.key) + " => " + NullSafe.toString(this.value);
        }
    }
    
    private static class Node<T>
    {
        private T value;
        private Map<Character, Node<T>> nextMap;
        
        private Node() {
            this.value = null;
        }
        
        private static <T> Map<Character, Node<T>> newNodeMap() {
            return new HashMap<Character, Node<T>>();
        }
        
        private static <T> Map<Character, Node<T>> newNodeMap(final Map<Character, Node<T>> prev) {
            return new HashMap<Character, Node<T>>(prev);
        }
        
        void setValue(final T value) {
            this.value = value;
        }
        
        Node<T> getNextNode(final Character ch) {
            if (this.nextMap == null) {
                return null;
            }
            return this.nextMap.get(ch);
        }
        
        T put(final CharSequence key, final int pos, final T addValue) {
            if (key.length() == pos) {
                final T old = this.value;
                this.setValue(addValue);
                return old;
            }
            final Character ch = key.charAt(pos);
            Node<T> nextNode;
            if (this.nextMap == null) {
                this.nextMap = newNodeMap();
                nextNode = new Node<T>();
                this.nextMap.put(ch, nextNode);
            }
            else if ((nextNode = this.nextMap.get(ch)) == null) {
                nextNode = new Node<T>();
                this.nextMap.put(ch, nextNode);
            }
            return nextNode.put(key, pos + 1, addValue);
        }
        
        T get(final CharSequence key, final int pos) {
            if (key.length() <= pos) {
                return this.value;
            }
            final Node<T> nextNode;
            if ((nextNode = this.getNextNode(key.charAt(pos))) == null) {
                return null;
            }
            return nextNode.get(key, pos + 1);
        }
        
        Entry<T> getLongestMatch(final CharSequence key, final int pos) {
            if (key.length() <= pos) {
                return Entry.newInstanceIfNeeded(key, this.value);
            }
            final Node<T> nextNode;
            if ((nextNode = this.getNextNode(key.charAt(pos))) == null) {
                return Entry.newInstanceIfNeeded(key, pos, this.value);
            }
            final Entry<T> ret;
            if ((ret = nextNode.getLongestMatch(key, pos + 1)) != null) {
                return ret;
            }
            return Entry.newInstanceIfNeeded(key, pos, this.value);
        }
        
        Entry<T> getLongestMatch(final PushbackReader keyIn, final StringBuilder key) throws IOException {
            final int c;
            if ((c = keyIn.read()) < 0) {
                return Entry.newInstanceIfNeeded(key, this.value);
            }
            final char ch = (char)c;
            final int prevLen = key.length();
            key.append(ch);
            final Node<T> nextNode;
            if ((nextNode = this.getNextNode(ch)) == null) {
                return Entry.newInstanceIfNeeded(key, this.value);
            }
            final Entry<T> ret;
            if ((ret = nextNode.getLongestMatch(keyIn, key)) != null) {
                return ret;
            }
            key.setLength(prevLen);
            keyIn.unread(c);
            return Entry.newInstanceIfNeeded(key, this.value);
        }
        
        void remap() {
            if (this.nextMap == null) {
                return;
            }
            this.nextMap = newNodeMap(this.nextMap);
            for (final Node<T> node : this.nextMap.values()) {
                node.remap();
            }
        }
        
        boolean containsValue(final Object toFind) {
            if (this.value != null && toFind.equals(this.value)) {
                return true;
            }
            if (this.nextMap == null) {
                return false;
            }
            for (final Node<T> node : this.nextMap.values()) {
                if (node.containsValue(toFind)) {
                    return true;
                }
            }
            return false;
        }
        
        Collection<T> values(final Collection<T> values) {
            if (this.value != null) {
                values.add(this.value);
            }
            if (this.nextMap == null) {
                return values;
            }
            for (final Node<T> node : this.nextMap.values()) {
                node.values(values);
            }
            return values;
        }
        
        Set<CharSequence> keySet(final StringBuilder key, final Set<CharSequence> keys) {
            final int len = key.length();
            if (this.value != null) {
                keys.add(key.toString());
            }
            if (this.nextMap != null && this.nextMap.size() > 0) {
                key.append('X');
                for (final Map.Entry<Character, Node<T>> entry : this.nextMap.entrySet()) {
                    key.setCharAt(len, entry.getKey());
                    entry.getValue().keySet(key, keys);
                }
                key.setLength(len);
            }
            return keys;
        }
        
        Set<Map.Entry<CharSequence, T>> entrySet(final StringBuilder key, final Set<Map.Entry<CharSequence, T>> entries) {
            final int len = key.length();
            if (this.value != null) {
                entries.add(new Entry<T>(key.toString(), this.value));
            }
            if (this.nextMap != null && this.nextMap.size() > 0) {
                key.append('X');
                for (final Map.Entry<Character, Node<T>> entry : this.nextMap.entrySet()) {
                    key.setCharAt(len, entry.getKey());
                    entry.getValue().entrySet(key, entries);
                }
                key.setLength(len);
            }
            return entries;
        }
    }
}
