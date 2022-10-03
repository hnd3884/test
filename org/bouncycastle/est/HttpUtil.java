package org.bouncycastle.est;

import java.util.HashMap;
import java.util.Iterator;
import java.io.StringWriter;
import java.util.Map;

class HttpUtil
{
    static String mergeCSL(final String s, final Map<String, String> map) {
        final StringWriter stringWriter = new StringWriter();
        stringWriter.write(s);
        stringWriter.write(32);
        int n = 0;
        for (final Map.Entry entry : map.entrySet()) {
            if (n == 0) {
                n = 1;
            }
            else {
                stringWriter.write(44);
            }
            stringWriter.write((String)entry.getKey());
            stringWriter.write("=\"");
            stringWriter.write((String)entry.getValue());
            stringWriter.write(34);
        }
        return stringWriter.toString();
    }
    
    static Map<String, String> splitCSL(final String s, String s2) {
        s2 = s2.trim();
        if (s2.startsWith(s)) {
            s2 = s2.substring(s.length());
        }
        return new PartLexer(s2).Parse();
    }
    
    public static String[] append(final String[] array, final String s) {
        if (array == null) {
            return new String[] { s };
        }
        final int length = array.length;
        final String[] array2 = new String[length + 1];
        System.arraycopy(array, 0, array2, 0, length);
        array2[length] = s;
        return array2;
    }
    
    static class Headers extends HashMap<String, String[]>
    {
        public Headers() {
        }
        
        public String getFirstValue(final String s) {
            final String[] values = this.getValues(s);
            if (values != null && values.length > 0) {
                return values[0];
            }
            return null;
        }
        
        public String[] getValues(String actualKey) {
            actualKey = this.actualKey(actualKey);
            if (actualKey == null) {
                return null;
            }
            return ((HashMap<K, String[]>)this).get(actualKey);
        }
        
        private String actualKey(final String s) {
            if (this.containsKey(s)) {
                return s;
            }
            for (final String s2 : ((HashMap<String, V>)this).keySet()) {
                if (s.equalsIgnoreCase(s2)) {
                    return s2;
                }
            }
            return null;
        }
        
        private boolean hasHeader(final String s) {
            return this.actualKey(s) != null;
        }
        
        public void set(final String s, final String s2) {
            this.put(s, new String[] { s2 });
        }
        
        public void add(final String s, final String s2) {
            this.put(s, HttpUtil.append(((HashMap<K, String[]>)this).get(s), s2));
        }
        
        public void ensureHeader(final String s, final String s2) {
            if (!this.containsKey(s)) {
                this.set(s, s2);
            }
        }
        
        @Override
        public Object clone() {
            final Headers headers = new Headers();
            for (final Map.Entry entry : this.entrySet()) {
                headers.put((String)entry.getKey(), this.copy((String[])entry.getValue()));
            }
            return headers;
        }
        
        private String[] copy(final String[] array) {
            final String[] array2 = new String[array.length];
            System.arraycopy(array, 0, array2, 0, array2.length);
            return array2;
        }
    }
    
    static class PartLexer
    {
        private final String src;
        int last;
        int p;
        
        PartLexer(final String src) {
            this.last = 0;
            this.p = 0;
            this.src = src;
        }
        
        Map<String, String> Parse() {
            final HashMap hashMap = new HashMap();
            while (this.p < this.src.length()) {
                this.skipWhiteSpace();
                final String consumeAlpha = this.consumeAlpha();
                if (consumeAlpha.length() == 0) {
                    throw new IllegalArgumentException("Expecting alpha label.");
                }
                this.skipWhiteSpace();
                if (!this.consumeIf('=')) {
                    throw new IllegalArgumentException("Expecting assign: '='");
                }
                this.skipWhiteSpace();
                if (!this.consumeIf('\"')) {
                    throw new IllegalArgumentException("Expecting start quote: '\"'");
                }
                this.discard();
                final String consumeUntil = this.consumeUntil('\"');
                this.discard(1);
                hashMap.put(consumeAlpha, consumeUntil);
                this.skipWhiteSpace();
                if (!this.consumeIf(',')) {
                    break;
                }
                this.discard();
            }
            return hashMap;
        }
        
        private String consumeAlpha() {
            for (char c = this.src.charAt(this.p); this.p < this.src.length() && ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')); c = this.src.charAt(this.p)) {
                ++this.p;
            }
            final String substring = this.src.substring(this.last, this.p);
            this.last = this.p;
            return substring;
        }
        
        private void skipWhiteSpace() {
            while (this.p < this.src.length() && this.src.charAt(this.p) < '!') {
                ++this.p;
            }
            this.last = this.p;
        }
        
        private boolean consumeIf(final char c) {
            if (this.p < this.src.length() && this.src.charAt(this.p) == c) {
                ++this.p;
                return true;
            }
            return false;
        }
        
        private String consumeUntil(final char c) {
            while (this.p < this.src.length() && this.src.charAt(this.p) != c) {
                ++this.p;
            }
            final String substring = this.src.substring(this.last, this.p);
            this.last = this.p;
            return substring;
        }
        
        private void discard() {
            this.last = this.p;
        }
        
        private void discard(final int n) {
            this.p += n;
            this.last = this.p;
        }
    }
}
