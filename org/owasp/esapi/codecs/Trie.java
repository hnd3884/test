package org.owasp.esapi.codecs;

import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.Map;

public interface Trie<T> extends Map<CharSequence, T>
{
    Entry<CharSequence, T> getLongestMatch(final CharSequence p0);
    
    Entry<CharSequence, T> getLongestMatch(final PushbackReader p0) throws IOException;
    
    int getMaxKeyLength();
    
    public static class TrieProxy<T> implements Trie<T>
    {
        private Trie<T> wrapped;
        
        TrieProxy(final Trie<T> toWrap) {
            this.wrapped = toWrap;
        }
        
        protected Trie<T> getWrapped() {
            return this.wrapped;
        }
        
        @Override
        public Entry<CharSequence, T> getLongestMatch(final CharSequence key) {
            return this.wrapped.getLongestMatch(key);
        }
        
        @Override
        public Entry<CharSequence, T> getLongestMatch(final PushbackReader keyIn) throws IOException {
            return this.wrapped.getLongestMatch(keyIn);
        }
        
        @Override
        public int getMaxKeyLength() {
            return this.wrapped.getMaxKeyLength();
        }
        
        @Override
        public int size() {
            return this.wrapped.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.wrapped.isEmpty();
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return this.wrapped.containsKey(key);
        }
        
        @Override
        public boolean containsValue(final Object val) {
            return this.wrapped.containsValue(val);
        }
        
        @Override
        public T get(final Object key) {
            return this.wrapped.get(key);
        }
        
        @Override
        public T put(final CharSequence key, final T value) {
            return this.wrapped.put(key, value);
        }
        
        @Override
        public T remove(final Object key) {
            return this.wrapped.remove(key);
        }
        
        @Override
        public void putAll(final Map<? extends CharSequence, ? extends T> t) {
            this.wrapped.putAll((Map<?, ?>)t);
        }
        
        @Override
        public void clear() {
            this.wrapped.clear();
        }
        
        @Override
        public Set<CharSequence> keySet() {
            return this.wrapped.keySet();
        }
        
        @Override
        public Collection<T> values() {
            return this.wrapped.values();
        }
        
        @Override
        public Set<Entry<CharSequence, T>> entrySet() {
            return this.wrapped.entrySet();
        }
        
        @Override
        public boolean equals(final Object other) {
            return this.wrapped.equals(other);
        }
        
        @Override
        public int hashCode() {
            return this.wrapped.hashCode();
        }
    }
    
    public static class Unmodifiable<T> extends TrieProxy<T>
    {
        Unmodifiable(final Trie<T> toWrap) {
            super(toWrap);
        }
        
        @Override
        public T put(final CharSequence key, final T value) {
            throw new UnsupportedOperationException("Unmodifiable Trie");
        }
        
        public T remove(final CharSequence key) {
            throw new UnsupportedOperationException("Unmodifiable Trie");
        }
        
        @Override
        public void putAll(final Map<? extends CharSequence, ? extends T> t) {
            throw new UnsupportedOperationException("Unmodifiable Trie");
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException("Unmodifiable Trie");
        }
        
        @Override
        public Set<CharSequence> keySet() {
            return Collections.unmodifiableSet((Set<? extends CharSequence>)super.keySet());
        }
        
        @Override
        public Collection<T> values() {
            return Collections.unmodifiableCollection(super.values());
        }
        
        @Override
        public Set<Entry<CharSequence, T>> entrySet() {
            return Collections.unmodifiableSet((Set<? extends Entry<CharSequence, T>>)super.entrySet());
        }
    }
    
    public static class Util
    {
        private Util() {
        }
        
        static <T> Trie<T> unmodifiable(final Trie<T> toWrap) {
            return new Unmodifiable<T>(toWrap);
        }
    }
}
