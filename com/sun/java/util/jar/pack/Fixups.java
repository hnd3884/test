package com.sun.java.util.jar.pack;

import java.util.Objects;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractCollection;

final class Fixups extends AbstractCollection<Fixup>
{
    byte[] bytes;
    int head;
    int tail;
    int size;
    ConstantPool.Entry[] entries;
    int[] bigDescs;
    private static final int MINBIGSIZE = 1;
    private static final int[] noBigDescs;
    private static final int LOC_SHIFT = 1;
    private static final int FMT_MASK = 1;
    private static final byte UNUSED_BYTE = 0;
    private static final byte OVERFLOW_BYTE = -1;
    private static final int BIGSIZE = 0;
    private static final int U2_FORMAT = 0;
    private static final int U1_FORMAT = 1;
    private static final int SPECIAL_LOC = 0;
    private static final int SPECIAL_FMT = 0;
    
    Fixups(final byte[] bytes) {
        this.bytes = bytes;
        this.entries = new ConstantPool.Entry[3];
        this.bigDescs = Fixups.noBigDescs;
    }
    
    Fixups() {
        this((byte[])null);
    }
    
    Fixups(final byte[] array, final Collection<Fixup> collection) {
        this(array);
        this.addAll(collection);
    }
    
    Fixups(final Collection<Fixup> collection) {
        this((byte[])null);
        this.addAll(collection);
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    public void trimToSize() {
        if (this.size != this.entries.length) {
            System.arraycopy(this.entries, 0, this.entries = new ConstantPool.Entry[this.size], 0, this.size);
        }
        final int n = this.bigDescs[0];
        if (n == 1) {
            this.bigDescs = Fixups.noBigDescs;
        }
        else if (n != this.bigDescs.length) {
            System.arraycopy(this.bigDescs, 0, this.bigDescs = new int[n], 0, n);
        }
    }
    
    public void visitRefs(final Collection<ConstantPool.Entry> collection) {
        for (int i = 0; i < this.size; ++i) {
            collection.add(this.entries[i]);
        }
    }
    
    @Override
    public void clear() {
        if (this.bytes != null) {
            for (final Fixup fixup : this) {
                this.storeIndex(fixup.location(), fixup.format(), 0);
            }
        }
        this.size = 0;
        if (this.bigDescs != Fixups.noBigDescs) {
            this.bigDescs[0] = 1;
        }
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public void setBytes(final byte[] array) {
        if (this.bytes == array) {
            return;
        }
        AbstractList list = null;
        assert (list = new ArrayList(this)) != null;
        if (this.bytes == null || array == null) {
            final ArrayList<Fixup> list2 = new ArrayList<Fixup>(this);
            this.clear();
            this.bytes = array;
            this.addAll(list2);
        }
        else {
            this.bytes = array;
        }
        assert list.equals(new ArrayList(this));
    }
    
    static int fmtLen(final int n) {
        return 1 + (n - 1) / -1;
    }
    
    static int descLoc(final int n) {
        return n >>> 1;
    }
    
    static int descFmt(final int n) {
        return n & 0x1;
    }
    
    static int descEnd(final int n) {
        return descLoc(n) + fmtLen(descFmt(n));
    }
    
    static int makeDesc(final int n, final int n2) {
        final int n3 = n << 1 | n2;
        assert descLoc(n3) == n;
        assert descFmt(n3) == n2;
        return n3;
    }
    
    int fetchDesc(final int n, final int n2) {
        final byte b = this.bytes[n];
        assert b != -1;
        int n3;
        if (n2 == 0) {
            n3 = ((b & 0xFF) << 8) + (this.bytes[n + 1] & 0xFF);
        }
        else {
            n3 = (b & 0xFF);
        }
        return n3 + (n << 1);
    }
    
    boolean storeDesc(final int n, final int n2, final int n3) {
        if (this.bytes == null) {
            return false;
        }
        final int n4 = n3 - (n << 1);
        switch (n2) {
            case 0: {
                assert this.bytes[n + 0] == 0;
                assert this.bytes[n + 1] == 0;
                final byte b = (byte)(n4 >> 8);
                final byte b2 = (byte)(n4 >> 0);
                if (n4 != (n4 & 0xFFFF) || b == -1) {
                    break;
                }
                this.bytes[n + 0] = b;
                this.bytes[n + 1] = b2;
                assert this.fetchDesc(n, n2) == n3;
                return true;
            }
            case 1: {
                assert this.bytes[n] == 0;
                final byte b3 = (byte)n4;
                if (n4 != (n4 & 0xFF) || b3 == -1) {
                    break;
                }
                this.bytes[n] = b3;
                assert this.fetchDesc(n, n2) == n3;
                return true;
            }
            default: {
                assert false;
                break;
            }
        }
        this.bytes[n] = -1;
        assert (this.bytes[n + 1] = (byte)this.bigDescs[0]) != 999;
        return false;
    }
    
    void storeIndex(final int n, final int n2, final int n3) {
        storeIndex(this.bytes, n, n2, n3);
    }
    
    static void storeIndex(final byte[] array, final int n, final int n2, final int n3) {
        switch (n2) {
            case 0: {
                assert n3 == (n3 & 0xFFFF) : n3;
                array[n + 0] = (byte)(n3 >> 8);
                array[n + 1] = (byte)(n3 >> 0);
                break;
            }
            case 1: {
                assert n3 == (n3 & 0xFF) : n3;
                array[n] = (byte)n3;
                break;
            }
            default: {
                assert false;
                break;
            }
        }
    }
    
    void addU1(final int n, final ConstantPool.Entry entry) {
        this.add(n, 1, entry);
    }
    
    void addU2(final int n, final ConstantPool.Entry entry) {
        this.add(n, 0, entry);
    }
    
    @Override
    public Iterator<Fixup> iterator() {
        return new Itr();
    }
    
    public void add(final int n, final int n2, final ConstantPool.Entry entry) {
        this.addDesc(makeDesc(n, n2), entry);
    }
    
    @Override
    public boolean add(final Fixup fixup) {
        this.addDesc(fixup.desc, fixup.entry);
        return true;
    }
    
    @Override
    public boolean addAll(final Collection<? extends Fixup> collection) {
        if (!(collection instanceof Fixups)) {
            return super.addAll(collection);
        }
        final Fixups fixups = (Fixups)collection;
        if (fixups.size == 0) {
            return false;
        }
        if (this.size == 0 && this.entries.length < fixups.size) {
            this.growEntries(fixups.size);
        }
        final ConstantPool.Entry[] entries = fixups.entries;
        final Itr itr = new Itr();
        while (itr.hasNext()) {
            this.addDesc(itr.nextDesc(), entries[itr.index]);
        }
        return true;
    }
    
    private void addDesc(final int tail, final ConstantPool.Entry entry) {
        if (this.entries.length == this.size) {
            this.growEntries(this.size * 2);
        }
        this.entries[this.size] = entry;
        if (this.size == 0) {
            this.tail = tail;
            this.head = tail;
        }
        else {
            final int tail2 = this.tail;
            final int descLoc = descLoc(tail2);
            final int descFmt = descFmt(tail2);
            final int fmtLen = fmtLen(descFmt);
            final int descLoc2 = descLoc(tail);
            if (descLoc2 < descLoc + fmtLen) {
                this.badOverlap(descLoc2);
            }
            this.tail = tail;
            if (!this.storeDesc(descLoc, descFmt, tail)) {
                int n = this.bigDescs[0];
                if (this.bigDescs.length == n) {
                    this.growBigDescs();
                }
                this.bigDescs[n++] = tail;
                this.bigDescs[0] = n;
            }
        }
        ++this.size;
    }
    
    private void badOverlap(final int n) {
        throw new IllegalArgumentException("locs must be ascending and must not overlap:  " + n + " >> " + this);
    }
    
    private void growEntries(final int n) {
        final ConstantPool.Entry[] entries = this.entries;
        System.arraycopy(entries, 0, this.entries = new ConstantPool.Entry[Math.max(3, n)], 0, entries.length);
    }
    
    private void growBigDescs() {
        final int[] bigDescs = this.bigDescs;
        System.arraycopy(bigDescs, 0, this.bigDescs = new int[bigDescs.length * 2], 0, bigDescs.length);
    }
    
    static Object addRefWithBytes(final Object o, final byte[] array, final ConstantPool.Entry entry) {
        return add(o, array, 0, 0, entry);
    }
    
    static Object addRefWithLoc(final Object o, final int n, final ConstantPool.Entry entry) {
        return add(o, null, n, 0, entry);
    }
    
    private static Object add(final Object o, final byte[] array, final int n, final int n2, final ConstantPool.Entry entry) {
        Fixups fixups;
        if (o == null) {
            if (n == 0 && n2 == 0) {
                return entry;
            }
            fixups = new Fixups(array);
        }
        else if (!(o instanceof Fixups)) {
            final ConstantPool.Entry entry2 = (ConstantPool.Entry)o;
            fixups = new Fixups(array);
            fixups.add(0, 0, entry2);
        }
        else {
            fixups = (Fixups)o;
            assert fixups.bytes == array;
        }
        fixups.add(n, n2, entry);
        return fixups;
    }
    
    public static void setBytes(final Object o, final byte[] bytes) {
        if (o instanceof Fixups) {
            ((Fixups)o).setBytes(bytes);
        }
    }
    
    public static Object trimToSize(Object o) {
        if (o instanceof Fixups) {
            final Fixups fixups = (Fixups)o;
            fixups.trimToSize();
            if (fixups.size() == 0) {
                o = null;
            }
        }
        return o;
    }
    
    public static void visitRefs(final Object o, final Collection<ConstantPool.Entry> collection) {
        if (o != null) {
            if (!(o instanceof Fixups)) {
                collection.add((ConstantPool.Entry)o);
            }
            else {
                ((Fixups)o).visitRefs(collection);
            }
        }
    }
    
    public static void finishRefs(final Object o, final byte[] array, final ConstantPool.Index index) {
        if (o == null) {
            return;
        }
        if (!(o instanceof Fixups)) {
            storeIndex(array, 0, 0, index.indexOf((ConstantPool.Entry)o));
            return;
        }
        final Fixups fixups = (Fixups)o;
        assert fixups.bytes == array;
        fixups.finishRefs(index);
    }
    
    void finishRefs(final ConstantPool.Index index) {
        if (this.isEmpty()) {
            return;
        }
        for (final Fixup fixup : this) {
            this.storeIndex(fixup.location(), fixup.format(), index.indexOf(fixup.entry));
        }
        this.bytes = null;
        this.clear();
    }
    
    static {
        noBigDescs = new int[] { 1 };
    }
    
    public static class Fixup implements Comparable<Fixup>
    {
        int desc;
        ConstantPool.Entry entry;
        
        Fixup(final int desc, final ConstantPool.Entry entry) {
            this.desc = desc;
            this.entry = entry;
        }
        
        public Fixup(final int n, final int n2, final ConstantPool.Entry entry) {
            this.desc = Fixups.makeDesc(n, n2);
            this.entry = entry;
        }
        
        public int location() {
            return Fixups.descLoc(this.desc);
        }
        
        public int format() {
            return Fixups.descFmt(this.desc);
        }
        
        public ConstantPool.Entry entry() {
            return this.entry;
        }
        
        @Override
        public int compareTo(final Fixup fixup) {
            return this.location() - fixup.location();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Fixup)) {
                return false;
            }
            final Fixup fixup = (Fixup)o;
            return this.desc == fixup.desc && this.entry == fixup.entry;
        }
        
        @Override
        public int hashCode() {
            return 59 * (59 * 7 + this.desc) + Objects.hashCode(this.entry);
        }
        
        @Override
        public String toString() {
            return "@" + this.location() + ((this.format() == 1) ? ".1" : "") + "=" + this.entry;
        }
    }
    
    private class Itr implements Iterator<Fixup>
    {
        int index;
        int bigIndex;
        int next;
        
        private Itr() {
            this.index = 0;
            this.bigIndex = 1;
            this.next = Fixups.this.head;
        }
        
        @Override
        public boolean hasNext() {
            return this.index < Fixups.this.size;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Fixup next() {
            return new Fixup(this.nextDesc(), Fixups.this.entries[this.index]);
        }
        
        int nextDesc() {
            ++this.index;
            final int next = this.next;
            if (this.index < Fixups.this.size) {
                final int descLoc = Fixups.descLoc(next);
                final int descFmt = Fixups.descFmt(next);
                if (Fixups.this.bytes != null && Fixups.this.bytes[descLoc] != -1) {
                    this.next = Fixups.this.fetchDesc(descLoc, descFmt);
                }
                else {
                    assert Fixups.this.bytes[descLoc + 1] == (byte)this.bigIndex;
                    this.next = Fixups.this.bigDescs[this.bigIndex++];
                }
            }
            return next;
        }
    }
}
