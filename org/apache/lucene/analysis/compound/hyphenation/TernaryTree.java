package org.apache.lucene.analysis.compound.hyphenation;

import java.util.Stack;
import java.io.PrintStream;
import java.util.Enumeration;

public class TernaryTree implements Cloneable
{
    protected char[] lo;
    protected char[] hi;
    protected char[] eq;
    protected char[] sc;
    protected CharVector kv;
    protected char root;
    protected char freenode;
    protected int length;
    protected static final int BLOCK_SIZE = 2048;
    
    TernaryTree() {
        this.init();
    }
    
    protected void init() {
        this.root = '\0';
        this.freenode = '\u0001';
        this.length = 0;
        this.lo = new char[2048];
        this.hi = new char[2048];
        this.eq = new char[2048];
        this.sc = new char[2048];
        this.kv = new CharVector();
    }
    
    public void insert(final String key, final char val) {
        int len = key.length() + 1;
        if (this.freenode + len > this.eq.length) {
            this.redimNodeArrays(this.eq.length + 2048);
        }
        final char[] strkey = new char[len--];
        key.getChars(0, len, strkey, 0);
        strkey[len] = '\0';
        this.root = this.insert(this.root, strkey, 0, val);
    }
    
    public void insert(final char[] key, final int start, final char val) {
        final int len = strlen(key) + 1;
        if (this.freenode + len > this.eq.length) {
            this.redimNodeArrays(this.eq.length + 2048);
        }
        this.root = this.insert(this.root, key, start, val);
    }
    
    private char insert(char p, final char[] key, final int start, final char val) {
        final int len = strlen(key, start);
        if (p == '\0') {
            final char freenode = this.freenode;
            this.freenode = (char)(freenode + '\u0001');
            p = freenode;
            this.eq[p] = val;
            ++this.length;
            this.hi[p] = '\0';
            if (len > 0) {
                this.sc[p] = '\uffff';
                this.lo[p] = (char)this.kv.alloc(len + 1);
                strcpy(this.kv.getArray(), this.lo[p], key, start);
            }
            else {
                this.sc[p] = '\0';
                this.lo[p] = '\0';
            }
            return p;
        }
        if (this.sc[p] == '\uffff') {
            final char freenode2 = this.freenode;
            this.freenode = (char)(freenode2 + '\u0001');
            final char pp = freenode2;
            this.lo[pp] = this.lo[p];
            this.eq[pp] = this.eq[p];
            this.lo[p] = '\0';
            if (len <= 0) {
                this.sc[pp] = '\uffff';
                this.hi[p] = pp;
                this.sc[p] = '\0';
                this.eq[p] = val;
                ++this.length;
                return p;
            }
            this.sc[p] = this.kv.get(this.lo[pp]);
            this.eq[p] = pp;
            final char[] lo = this.lo;
            final char c = pp;
            ++lo[c];
            if (this.kv.get(this.lo[pp]) == '\0') {
                this.lo[pp] = '\0';
                this.sc[pp] = '\0';
                this.hi[pp] = '\0';
            }
            else {
                this.sc[pp] = '\uffff';
            }
        }
        final char s = key[start];
        if (s < this.sc[p]) {
            this.lo[p] = this.insert(this.lo[p], key, start, val);
        }
        else if (s == this.sc[p]) {
            if (s != '\0') {
                this.eq[p] = this.insert(this.eq[p], key, start + 1, val);
            }
            else {
                this.eq[p] = val;
            }
        }
        else {
            this.hi[p] = this.insert(this.hi[p], key, start, val);
        }
        return p;
    }
    
    public static int strcmp(final char[] a, int startA, final char[] b, int startB) {
        while (a[startA] == b[startB]) {
            if (a[startA] == '\0') {
                return 0;
            }
            ++startA;
            ++startB;
        }
        return a[startA] - b[startB];
    }
    
    public static int strcmp(final String str, final char[] a, final int start) {
        int len;
        int i;
        for (len = str.length(), i = 0; i < len; ++i) {
            final int d = str.charAt(i) - a[start + i];
            if (d != 0) {
                return d;
            }
            if (a[start + i] == '\0') {
                return d;
            }
        }
        if (a[start + i] != '\0') {
            return -a[start + i];
        }
        return 0;
    }
    
    public static void strcpy(final char[] dst, int di, final char[] src, int si) {
        while (src[si] != '\0') {
            dst[di++] = src[si++];
        }
        dst[di] = '\0';
    }
    
    public static int strlen(final char[] a, final int start) {
        int len = 0;
        for (int i = start; i < a.length && a[i] != '\0'; ++i) {
            ++len;
        }
        return len;
    }
    
    public static int strlen(final char[] a) {
        return strlen(a, 0);
    }
    
    public int find(final String key) {
        final int len = key.length();
        final char[] strkey = new char[len + 1];
        key.getChars(0, len, strkey, 0);
        strkey[len] = '\0';
        return this.find(strkey, 0);
    }
    
    public int find(final char[] key, final int start) {
        char p = this.root;
        int i = start;
        while (p != '\0') {
            if (this.sc[p] == '\uffff') {
                if (strcmp(key, i, this.kv.getArray(), this.lo[p]) == 0) {
                    return this.eq[p];
                }
                return -1;
            }
            else {
                final char c = key[i];
                final int d = c - this.sc[p];
                if (d == 0) {
                    if (c == '\0') {
                        return this.eq[p];
                    }
                    ++i;
                    p = this.eq[p];
                }
                else if (d < 0) {
                    p = this.lo[p];
                }
                else {
                    p = this.hi[p];
                }
            }
        }
        return -1;
    }
    
    public boolean knows(final String key) {
        return this.find(key) >= 0;
    }
    
    private void redimNodeArrays(final int newsize) {
        final int len = (newsize < this.lo.length) ? newsize : this.lo.length;
        char[] na = new char[newsize];
        System.arraycopy(this.lo, 0, na, 0, len);
        this.lo = na;
        na = new char[newsize];
        System.arraycopy(this.hi, 0, na, 0, len);
        this.hi = na;
        na = new char[newsize];
        System.arraycopy(this.eq, 0, na, 0, len);
        this.eq = na;
        na = new char[newsize];
        System.arraycopy(this.sc, 0, na, 0, len);
        this.sc = na;
    }
    
    public int size() {
        return this.length;
    }
    
    public TernaryTree clone() {
        final TernaryTree t = new TernaryTree();
        t.lo = this.lo.clone();
        t.hi = this.hi.clone();
        t.eq = this.eq.clone();
        t.sc = this.sc.clone();
        t.kv = this.kv.clone();
        t.root = this.root;
        t.freenode = this.freenode;
        t.length = this.length;
        return t;
    }
    
    protected void insertBalanced(final String[] k, final char[] v, final int offset, final int n) {
        if (n < 1) {
            return;
        }
        final int m = n >> 1;
        this.insert(k[m + offset], v[m + offset]);
        this.insertBalanced(k, v, offset, m);
        this.insertBalanced(k, v, offset + m + 1, n - m - 1);
    }
    
    public void balance() {
        int i = 0;
        final int n = this.length;
        final String[] k = new String[n];
        final char[] v = new char[n];
        final Iterator iter = new Iterator();
        while (iter.hasMoreElements()) {
            v[i] = iter.getValue();
            k[i++] = iter.nextElement();
        }
        this.init();
        this.insertBalanced(k, v, 0, n);
    }
    
    public void trimToSize() {
        this.balance();
        this.redimNodeArrays(this.freenode);
        final CharVector kx = new CharVector();
        kx.alloc(1);
        final TernaryTree map = new TernaryTree();
        this.compact(kx, map, this.root);
        (this.kv = kx).trimToSize();
    }
    
    private void compact(final CharVector kx, final TernaryTree map, final char p) {
        if (p == '\0') {
            return;
        }
        if (this.sc[p] == '\uffff') {
            int k = map.find(this.kv.getArray(), this.lo[p]);
            if (k < 0) {
                k = kx.alloc(strlen(this.kv.getArray(), this.lo[p]) + 1);
                strcpy(kx.getArray(), k, this.kv.getArray(), this.lo[p]);
                map.insert(kx.getArray(), k, (char)k);
            }
            this.lo[p] = (char)k;
        }
        else {
            this.compact(kx, map, this.lo[p]);
            if (this.sc[p] != '\0') {
                this.compact(kx, map, this.eq[p]);
            }
            this.compact(kx, map, this.hi[p]);
        }
    }
    
    public Enumeration<String> keys() {
        return new Iterator();
    }
    
    public void printStats(final PrintStream out) {
        out.println("Number of keys = " + Integer.toString(this.length));
        out.println("Node count = " + Integer.toString(this.freenode));
        out.println("Key Array length = " + Integer.toString(this.kv.length()));
    }
    
    public class Iterator implements Enumeration<String>
    {
        int cur;
        String curkey;
        Stack<Item> ns;
        StringBuilder ks;
        
        public Iterator() {
            this.cur = -1;
            this.ns = new Stack<Item>();
            this.ks = new StringBuilder();
            this.rewind();
        }
        
        public void rewind() {
            this.ns.removeAllElements();
            this.ks.setLength(0);
            this.cur = TernaryTree.this.root;
            this.run();
        }
        
        @Override
        public String nextElement() {
            final String res = new String(this.curkey);
            this.cur = this.up();
            this.run();
            return res;
        }
        
        public char getValue() {
            if (this.cur >= 0) {
                return TernaryTree.this.eq[this.cur];
            }
            return '\0';
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.cur != -1;
        }
        
        private int up() {
            Item i = new Item();
            int res = 0;
            if (this.ns.empty()) {
                return -1;
            }
            if (this.cur != 0 && TernaryTree.this.sc[this.cur] == '\0') {
                return TernaryTree.this.lo[this.cur];
            }
            boolean climb = true;
            while (climb) {
                final Item item;
                i = (item = this.ns.pop());
                ++item.child;
                switch (i.child) {
                    case '\u0001': {
                        if (TernaryTree.this.sc[i.parent] != '\0') {
                            res = TernaryTree.this.eq[i.parent];
                            this.ns.push(i.clone());
                            this.ks.append(TernaryTree.this.sc[i.parent]);
                        }
                        else {
                            final Item item2 = i;
                            ++item2.child;
                            this.ns.push(i.clone());
                            res = TernaryTree.this.hi[i.parent];
                        }
                        climb = false;
                        continue;
                    }
                    case '\u0002': {
                        res = TernaryTree.this.hi[i.parent];
                        this.ns.push(i.clone());
                        if (this.ks.length() > 0) {
                            this.ks.setLength(this.ks.length() - 1);
                        }
                        climb = false;
                        continue;
                    }
                    default: {
                        if (this.ns.empty()) {
                            return -1;
                        }
                        climb = true;
                        continue;
                    }
                }
            }
            return res;
        }
        
        private int run() {
            if (this.cur == -1) {
                return -1;
            }
            boolean leaf = false;
            while (true) {
                if (this.cur != 0) {
                    if (TernaryTree.this.sc[this.cur] == '\uffff') {
                        leaf = true;
                    }
                    else {
                        this.ns.push(new Item((char)this.cur, '\0'));
                        if (TernaryTree.this.sc[this.cur] != '\0') {
                            this.cur = TernaryTree.this.lo[this.cur];
                            continue;
                        }
                        leaf = true;
                    }
                }
                if (leaf) {
                    final StringBuilder buf = new StringBuilder(this.ks.toString());
                    if (TernaryTree.this.sc[this.cur] == '\uffff') {
                        int p = TernaryTree.this.lo[this.cur];
                        while (TernaryTree.this.kv.get(p) != '\0') {
                            buf.append(TernaryTree.this.kv.get(p++));
                        }
                    }
                    this.curkey = buf.toString();
                    return 0;
                }
                this.cur = this.up();
                if (this.cur == -1) {
                    return -1;
                }
            }
        }
        
        private class Item implements Cloneable
        {
            char parent;
            char child;
            
            public Item() {
                this.parent = '\0';
                this.child = '\0';
            }
            
            public Item(final char p, final char c) {
                this.parent = p;
                this.child = c;
            }
            
            public Item clone() {
                return new Item(this.parent, this.child);
            }
        }
    }
}
