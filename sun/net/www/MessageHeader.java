package sun.net.www;

import java.util.NoSuchElementException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.StringJoiner;
import java.io.IOException;
import java.io.InputStream;

public class MessageHeader
{
    private String[] keys;
    private String[] values;
    private int nkeys;
    
    public MessageHeader() {
        this.grow();
    }
    
    public MessageHeader(final InputStream inputStream) throws IOException {
        this.parseHeader(inputStream);
    }
    
    public synchronized String getHeaderNamesInList() {
        final StringJoiner stringJoiner = new StringJoiner(",");
        for (int i = 0; i < this.nkeys; ++i) {
            stringJoiner.add(this.keys[i]);
        }
        return stringJoiner.toString();
    }
    
    public synchronized void reset() {
        this.keys = null;
        this.values = null;
        this.nkeys = 0;
        this.grow();
    }
    
    public synchronized String findValue(final String s) {
        if (s == null) {
            int nkeys = this.nkeys;
            while (--nkeys >= 0) {
                if (this.keys[nkeys] == null) {
                    return this.values[nkeys];
                }
            }
        }
        else {
            int nkeys2 = this.nkeys;
            while (--nkeys2 >= 0) {
                if (s.equalsIgnoreCase(this.keys[nkeys2])) {
                    return this.values[nkeys2];
                }
            }
        }
        return null;
    }
    
    public synchronized int getKey(final String s) {
        int nkeys = this.nkeys;
        while (--nkeys >= 0) {
            if (this.keys[nkeys] == s || (s != null && s.equalsIgnoreCase(this.keys[nkeys]))) {
                return nkeys;
            }
        }
        return -1;
    }
    
    public synchronized String getKey(final int n) {
        if (n < 0 || n >= this.nkeys) {
            return null;
        }
        return this.keys[n];
    }
    
    public synchronized String getValue(final int n) {
        if (n < 0 || n >= this.nkeys) {
            return null;
        }
        return this.values[n];
    }
    
    public synchronized String findNextValue(final String s, final String s2) {
        int n = 0;
        if (s == null) {
            int nkeys = this.nkeys;
            while (--nkeys >= 0) {
                if (this.keys[nkeys] == null) {
                    if (n != 0) {
                        return this.values[nkeys];
                    }
                    if (this.values[nkeys] != s2) {
                        continue;
                    }
                    n = 1;
                }
            }
        }
        else {
            int nkeys2 = this.nkeys;
            while (--nkeys2 >= 0) {
                if (s.equalsIgnoreCase(this.keys[nkeys2])) {
                    if (n != 0) {
                        return this.values[nkeys2];
                    }
                    if (this.values[nkeys2] != s2) {
                        continue;
                    }
                    n = 1;
                }
            }
        }
        return null;
    }
    
    public boolean filterNTLMResponses(final String s) {
        boolean b = false;
        for (int i = 0; i < this.nkeys; ++i) {
            if (s.equalsIgnoreCase(this.keys[i]) && this.values[i] != null && this.values[i].length() > 5 && this.values[i].substring(0, 5).equalsIgnoreCase("NTLM ")) {
                b = true;
                break;
            }
        }
        if (b) {
            int nkeys = 0;
            for (int j = 0; j < this.nkeys; ++j) {
                if (s.equalsIgnoreCase(this.keys[j])) {
                    if ("Negotiate".equalsIgnoreCase(this.values[j])) {
                        continue;
                    }
                    if ("Kerberos".equalsIgnoreCase(this.values[j])) {
                        continue;
                    }
                }
                if (j != nkeys) {
                    this.keys[nkeys] = this.keys[j];
                    this.values[nkeys] = this.values[j];
                }
                ++nkeys;
            }
            if (nkeys != this.nkeys) {
                this.nkeys = nkeys;
                return true;
            }
        }
        return false;
    }
    
    public Iterator<String> multiValueIterator(final String s) {
        return new HeaderIterator(s, this);
    }
    
    public synchronized Map<String, List<String>> getHeaders() {
        return this.getHeaders(null);
    }
    
    public synchronized Map<String, List<String>> getHeaders(final String[] array) {
        return this.filterAndAddHeaders(array, null);
    }
    
    public synchronized Map<String, List<String>> filterAndAddHeaders(final String[] array, final Map<String, List<String>> map) {
        int n = 0;
        final HashMap hashMap = new HashMap();
        int nkeys = this.nkeys;
        while (--nkeys >= 0) {
            if (array != null) {
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] != null && array[i].equalsIgnoreCase(this.keys[nkeys])) {
                        n = 1;
                        break;
                    }
                }
            }
            if (n == 0) {
                Object o = hashMap.get(this.keys[nkeys]);
                if (o == null) {
                    o = new ArrayList<String>();
                    hashMap.put(this.keys[nkeys], o);
                }
                ((List<String>)o).add(this.values[nkeys]);
            }
            else {
                n = 0;
            }
        }
        if (map != null) {
            for (final Map.Entry entry : map.entrySet()) {
                Object o2 = hashMap.get(entry.getKey());
                if (o2 == null) {
                    o2 = new ArrayList();
                    hashMap.put(entry.getKey(), o2);
                }
                ((List)o2).addAll((Collection)entry.getValue());
            }
        }
        for (final String s : hashMap.keySet()) {
            hashMap.put(s, Collections.unmodifiableList((List<?>)hashMap.get(s)));
        }
        return (Map<String, List<String>>)Collections.unmodifiableMap((Map<?, ?>)hashMap);
    }
    
    private boolean isRequestline(final String s) {
        final String trim = s.trim();
        final int lastIndex = trim.lastIndexOf(32);
        if (lastIndex <= 0) {
            return false;
        }
        final int length = trim.length();
        if (length - lastIndex < 9) {
            return false;
        }
        final char char1 = trim.charAt(length - 3);
        final char char2 = trim.charAt(length - 2);
        final char char3 = trim.charAt(length - 1);
        return char1 >= '1' && char1 <= '9' && char2 == '.' && char3 >= '0' && char3 <= '9' && trim.substring(lastIndex + 1, length - 3).equalsIgnoreCase("HTTP/");
    }
    
    public synchronized void print(final PrintStream printStream) {
        for (int i = 0; i < this.nkeys; ++i) {
            if (this.keys[i] != null) {
                final StringBuilder sb = new StringBuilder(this.keys[i]);
                if (this.values[i] != null) {
                    sb.append(": " + this.values[i]);
                }
                else if (i != 0 || !this.isRequestline(this.keys[i])) {
                    sb.append(":");
                }
                printStream.print(sb.append("\r\n"));
            }
        }
        printStream.print("\r\n");
        printStream.flush();
    }
    
    public synchronized void add(final String s, final String s2) {
        this.grow();
        this.keys[this.nkeys] = s;
        this.values[this.nkeys] = s2;
        ++this.nkeys;
    }
    
    public synchronized void prepend(final String s, final String s2) {
        this.grow();
        for (int i = this.nkeys; i > 0; --i) {
            this.keys[i] = this.keys[i - 1];
            this.values[i] = this.values[i - 1];
        }
        this.keys[0] = s;
        this.values[0] = s2;
        ++this.nkeys;
    }
    
    public synchronized void set(final int n, final String s, final String s2) {
        this.grow();
        if (n < 0) {
            return;
        }
        if (n >= this.nkeys) {
            this.add(s, s2);
        }
        else {
            this.keys[n] = s;
            this.values[n] = s2;
        }
    }
    
    private void grow() {
        if (this.keys == null || this.nkeys >= this.keys.length) {
            final String[] keys = new String[this.nkeys + 4];
            final String[] values = new String[this.nkeys + 4];
            if (this.keys != null) {
                System.arraycopy(this.keys, 0, keys, 0, this.nkeys);
            }
            if (this.values != null) {
                System.arraycopy(this.values, 0, values, 0, this.nkeys);
            }
            this.keys = keys;
            this.values = values;
        }
    }
    
    public synchronized void remove(final String s) {
        if (s == null) {
            for (int i = 0; i < this.nkeys; ++i) {
                while (this.keys[i] == null && i < this.nkeys) {
                    for (int j = i; j < this.nkeys - 1; ++j) {
                        this.keys[j] = this.keys[j + 1];
                        this.values[j] = this.values[j + 1];
                    }
                    --this.nkeys;
                }
            }
        }
        else {
            for (int k = 0; k < this.nkeys; ++k) {
                while (s.equalsIgnoreCase(this.keys[k]) && k < this.nkeys) {
                    for (int l = k; l < this.nkeys - 1; ++l) {
                        this.keys[l] = this.keys[l + 1];
                        this.values[l] = this.values[l + 1];
                    }
                    --this.nkeys;
                }
            }
        }
    }
    
    public synchronized void set(final String s, final String s2) {
        int nkeys = this.nkeys;
        while (--nkeys >= 0) {
            if (s.equalsIgnoreCase(this.keys[nkeys])) {
                this.values[nkeys] = s2;
                return;
            }
        }
        this.add(s, s2);
    }
    
    public synchronized void setIfNotSet(final String s, final String s2) {
        if (this.findValue(s) == null) {
            this.add(s, s2);
        }
    }
    
    public static String canonicalID(final String s) {
        if (s == null) {
            return "";
        }
        int n = 0;
        int length = s.length();
        boolean b = false;
        char char1;
        while (n < length && ((char1 = s.charAt(n)) == '<' || char1 <= ' ')) {
            ++n;
            b = true;
        }
        char char2;
        while (n < length && ((char2 = s.charAt(length - 1)) == '>' || char2 <= ' ')) {
            --length;
            b = true;
        }
        return b ? s.substring(n, length) : s;
    }
    
    public void parseHeader(final InputStream inputStream) throws IOException {
        synchronized (this) {
            this.nkeys = 0;
        }
        this.mergeHeader(inputStream);
    }
    
    public void mergeHeader(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }
        char[] array = new char[10];
        int n = inputStream.read();
    Label_0015:
        while (n != 10 && n != 13 && n >= 0) {
            int n2 = 0;
            int n3 = -1;
            int n4 = (n > 32) ? 1 : 0;
            array[n2++] = (char)n;
            while (true) {
                int read;
                while ((read = inputStream.read()) >= 0) {
                    switch (read) {
                        case 58: {
                            if (n4 != 0 && n2 > 0) {
                                n3 = n2;
                            }
                            n4 = 0;
                            break;
                        }
                        case 9: {
                            read = 32;
                        }
                        case 32: {
                            n4 = 0;
                            break;
                        }
                        case 10:
                        case 13: {
                            n = inputStream.read();
                            if (read == 13 && n == 10) {
                                n = inputStream.read();
                                if (n == 13) {
                                    n = inputStream.read();
                                }
                            }
                            if (n != 10 && n != 13) {
                                if (n <= 32) {
                                    read = 32;
                                    break;
                                }
                            }
                            while (n2 > 0 && array[n2 - 1] <= ' ') {
                                --n2;
                            }
                            String copyValue;
                            if (n3 <= 0) {
                                copyValue = null;
                                n3 = 0;
                            }
                            else {
                                copyValue = String.copyValueOf(array, 0, n3);
                                if (n3 < n2 && array[n3] == ':') {
                                    ++n3;
                                }
                                while (n3 < n2 && array[n3] <= ' ') {
                                    ++n3;
                                }
                            }
                            String copyValue2;
                            if (n3 >= n2) {
                                copyValue2 = new String();
                            }
                            else {
                                copyValue2 = String.copyValueOf(array, n3, n2 - n3);
                            }
                            this.add(copyValue, copyValue2);
                            continue Label_0015;
                        }
                    }
                    if (n2 >= array.length) {
                        final char[] array2 = new char[array.length * 2];
                        System.arraycopy(array, 0, array2, 0, n2);
                        array = array2;
                    }
                    array[n2++] = (char)read;
                }
                n = -1;
                continue;
            }
        }
    }
    
    @Override
    public synchronized String toString() {
        String s = super.toString() + this.nkeys + " pairs: ";
        for (int n = 0; n < this.keys.length && n < this.nkeys; ++n) {
            s = s + "{" + this.keys[n] + ": " + this.values[n] + "}";
        }
        return s;
    }
    
    class HeaderIterator implements Iterator<String>
    {
        int index;
        int next;
        String key;
        boolean haveNext;
        Object lock;
        
        public HeaderIterator(final String key, final Object lock) {
            this.index = 0;
            this.next = -1;
            this.haveNext = false;
            this.key = key;
            this.lock = lock;
        }
        
        @Override
        public boolean hasNext() {
            synchronized (this.lock) {
                if (this.haveNext) {
                    return true;
                }
                while (this.index < MessageHeader.this.nkeys) {
                    if (this.key.equalsIgnoreCase(MessageHeader.this.keys[this.index])) {
                        this.haveNext = true;
                        this.next = this.index++;
                        return true;
                    }
                    ++this.index;
                }
                return false;
            }
        }
        
        @Override
        public String next() {
            synchronized (this.lock) {
                if (this.haveNext) {
                    this.haveNext = false;
                    return MessageHeader.this.values[this.next];
                }
                if (this.hasNext()) {
                    return this.next();
                }
                throw new NoSuchElementException("No more elements");
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not allowed");
        }
    }
}
