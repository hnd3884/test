package org.apache.lucene.search.suggest.tst;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.DataInput;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import org.apache.lucene.search.suggest.SortedInputIterator;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.search.suggest.Lookup;

public class TSTLookup extends Lookup
{
    TernaryTreeNode root;
    TSTAutocomplete autocomplete;
    private long count;
    private static final byte LO_KID = 1;
    private static final byte EQ_KID = 2;
    private static final byte HI_KID = 4;
    private static final byte HAS_TOKEN = 8;
    private static final byte HAS_VALUE = 16;
    
    public TSTLookup() {
        this.root = new TernaryTreeNode();
        this.autocomplete = new TSTAutocomplete();
        this.count = 0L;
    }
    
    @Override
    public void build(InputIterator iterator) throws IOException {
        if (iterator.hasPayloads()) {
            throw new IllegalArgumentException("this suggester doesn't support payloads");
        }
        if (iterator.hasContexts()) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        this.root = new TernaryTreeNode();
        iterator = new SortedInputIterator(iterator, BytesRef.getUTF8SortedAsUTF16Comparator());
        this.count = 0L;
        final ArrayList<String> tokens = new ArrayList<String>();
        final ArrayList<Number> vals = new ArrayList<Number>();
        final CharsRefBuilder charsSpare = new CharsRefBuilder();
        BytesRef spare;
        while ((spare = iterator.next()) != null) {
            charsSpare.copyUTF8Bytes(spare);
            tokens.add(charsSpare.toString());
            vals.add(iterator.weight());
            ++this.count;
        }
        this.autocomplete.balancedTree(tokens.toArray(), vals.toArray(), 0, tokens.size() - 1, this.root);
    }
    
    public boolean add(final CharSequence key, final Object value) {
        this.autocomplete.insert(this.root, key, value, 0);
        return true;
    }
    
    public Object get(final CharSequence key) {
        final List<TernaryTreeNode> list = this.autocomplete.prefixCompletion(this.root, key, 0);
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (final TernaryTreeNode n : list) {
            if (charSeqEquals(n.token, key)) {
                return n.val;
            }
        }
        return null;
    }
    
    private static boolean charSeqEquals(final CharSequence left, final CharSequence right) {
        final int len = left.length();
        if (len != right.length()) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (left.charAt(i) != right.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final boolean onlyMorePopular, final int num) {
        if (contexts != null) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        final List<TernaryTreeNode> list = this.autocomplete.prefixCompletion(this.root, key, 0);
        final List<LookupResult> res = new ArrayList<LookupResult>();
        if (list == null || list.size() == 0) {
            return res;
        }
        final int maxCnt = Math.min(num, list.size());
        if (onlyMorePopular) {
            final LookupPriorityQueue queue = new LookupPriorityQueue(num);
            for (final TernaryTreeNode ttn : list) {
                queue.insertWithOverflow((Object)new LookupResult(ttn.token, ((Number)ttn.val).longValue()));
            }
            for (final LookupResult lr : queue.getResults()) {
                res.add(lr);
            }
        }
        else {
            for (int i = 0; i < maxCnt; ++i) {
                final TernaryTreeNode ttn2 = list.get(i);
                res.add(new LookupResult(ttn2.token, ((Number)ttn2.val).longValue()));
            }
        }
        return res;
    }
    
    private void readRecursively(final DataInput in, final TernaryTreeNode node) throws IOException {
        node.splitchar = in.readString().charAt(0);
        final byte mask = in.readByte();
        if ((mask & 0x8) != 0x0) {
            node.token = in.readString();
        }
        if ((mask & 0x10) != 0x0) {
            node.val = in.readLong();
        }
        if ((mask & 0x1) != 0x0) {
            this.readRecursively(in, node.loKid = new TernaryTreeNode());
        }
        if ((mask & 0x2) != 0x0) {
            this.readRecursively(in, node.eqKid = new TernaryTreeNode());
        }
        if ((mask & 0x4) != 0x0) {
            this.readRecursively(in, node.hiKid = new TernaryTreeNode());
        }
    }
    
    private void writeRecursively(final DataOutput out, final TernaryTreeNode node) throws IOException {
        out.writeString(new String(new char[] { node.splitchar }, 0, 1));
        byte mask = 0;
        if (node.eqKid != null) {
            mask |= 0x2;
        }
        if (node.loKid != null) {
            mask |= 0x1;
        }
        if (node.hiKid != null) {
            mask |= 0x4;
        }
        if (node.token != null) {
            mask |= 0x8;
        }
        if (node.val != null) {
            mask |= 0x10;
        }
        out.writeByte(mask);
        if (node.token != null) {
            out.writeString(node.token);
        }
        if (node.val != null) {
            out.writeLong(((Number)node.val).longValue());
        }
        if (node.loKid != null) {
            this.writeRecursively(out, node.loKid);
        }
        if (node.eqKid != null) {
            this.writeRecursively(out, node.eqKid);
        }
        if (node.hiKid != null) {
            this.writeRecursively(out, node.hiKid);
        }
    }
    
    @Override
    public synchronized boolean store(final DataOutput output) throws IOException {
        output.writeVLong(this.count);
        this.writeRecursively(output, this.root);
        return true;
    }
    
    @Override
    public synchronized boolean load(final DataInput input) throws IOException {
        this.count = input.readVLong();
        this.readRecursively(input, this.root = new TernaryTreeNode());
        return true;
    }
    
    public long ramBytesUsed() {
        long mem = RamUsageEstimator.shallowSizeOf((Object)this);
        if (this.root != null) {
            mem += this.root.sizeInBytes();
        }
        return mem;
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
}
