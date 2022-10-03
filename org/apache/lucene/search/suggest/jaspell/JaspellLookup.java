package org.apache.lucene.search.suggest.jaspell;

import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.DataInput;
import java.util.Iterator;
import org.apache.lucene.util.CharsRef;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.search.suggest.Lookup;

@Deprecated
public class JaspellLookup extends Lookup implements Accountable
{
    JaspellTernarySearchTrie trie;
    private boolean usePrefix;
    private int editDistance;
    private long count;
    private static final byte LO_KID = 1;
    private static final byte EQ_KID = 2;
    private static final byte HI_KID = 4;
    private static final byte HAS_VALUE = 8;
    
    public JaspellLookup() {
        this.trie = new JaspellTernarySearchTrie();
        this.usePrefix = true;
        this.editDistance = 2;
        this.count = 0L;
    }
    
    @Override
    public void build(final InputIterator iterator) throws IOException {
        if (iterator.hasPayloads()) {
            throw new IllegalArgumentException("this suggester doesn't support payloads");
        }
        if (iterator.hasContexts()) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        this.count = 0L;
        (this.trie = new JaspellTernarySearchTrie()).setMatchAlmostDiff(this.editDistance);
        final CharsRefBuilder charsSpare = new CharsRefBuilder();
        BytesRef spare;
        while ((spare = iterator.next()) != null) {
            final long weight = iterator.weight();
            if (spare.length == 0) {
                continue;
            }
            charsSpare.copyUTF8Bytes(spare);
            this.trie.put(charsSpare.toString(), weight);
            ++this.count;
        }
    }
    
    public boolean add(final CharSequence key, final Object value) {
        this.trie.put(key, value);
        return false;
    }
    
    public Object get(final CharSequence key) {
        return this.trie.get(key);
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final boolean onlyMorePopular, final int num) {
        if (contexts != null) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        final List<LookupResult> res = new ArrayList<LookupResult>();
        final int count = onlyMorePopular ? (num * 2) : num;
        List<String> list;
        if (this.usePrefix) {
            list = this.trie.matchPrefix(key, count);
        }
        else {
            list = this.trie.matchAlmost(key, count);
        }
        if (list == null || list.size() == 0) {
            return res;
        }
        final int maxCnt = Math.min(num, list.size());
        if (onlyMorePopular) {
            final LookupPriorityQueue queue = new LookupPriorityQueue(num);
            for (final String s : list) {
                final long freq = ((Number)this.trie.get(s)).longValue();
                queue.insertWithOverflow((Object)new LookupResult((CharSequence)new CharsRef(s), freq));
            }
            for (final LookupResult lr : queue.getResults()) {
                res.add(lr);
            }
        }
        else {
            for (int i = 0; i < maxCnt; ++i) {
                final String s2 = list.get(i);
                final long freq2 = ((Number)this.trie.get(s2)).longValue();
                res.add(new LookupResult((CharSequence)new CharsRef(s2), freq2));
            }
        }
        return res;
    }
    
    private void readRecursively(final DataInput in, final JaspellTernarySearchTrie.TSTNode node) throws IOException {
        node.splitchar = in.readString().charAt(0);
        final byte mask = in.readByte();
        if ((mask & 0x8) != 0x0) {
            node.data = in.readLong();
        }
        if ((mask & 0x1) != 0x0) {
            final JaspellTernarySearchTrie.TSTNode kid = this.trie.new TSTNode('\0', node);
            this.readRecursively(in, node.relatives[1] = kid);
        }
        if ((mask & 0x2) != 0x0) {
            final JaspellTernarySearchTrie.TSTNode kid = this.trie.new TSTNode('\0', node);
            this.readRecursively(in, node.relatives[2] = kid);
        }
        if ((mask & 0x4) != 0x0) {
            final JaspellTernarySearchTrie.TSTNode kid = this.trie.new TSTNode('\0', node);
            this.readRecursively(in, node.relatives[3] = kid);
        }
    }
    
    private void writeRecursively(final DataOutput out, final JaspellTernarySearchTrie.TSTNode node) throws IOException {
        if (node == null) {
            return;
        }
        out.writeString(new String(new char[] { node.splitchar }, 0, 1));
        byte mask = 0;
        if (node.relatives[1] != null) {
            mask |= 0x1;
        }
        if (node.relatives[2] != null) {
            mask |= 0x2;
        }
        if (node.relatives[3] != null) {
            mask |= 0x4;
        }
        if (node.data != null) {
            mask |= 0x8;
        }
        out.writeByte(mask);
        if (node.data != null) {
            out.writeLong(((Number)node.data).longValue());
        }
        this.writeRecursively(out, node.relatives[1]);
        this.writeRecursively(out, node.relatives[2]);
        this.writeRecursively(out, node.relatives[3]);
    }
    
    @Override
    public boolean store(final DataOutput output) throws IOException {
        output.writeVLong(this.count);
        final JaspellTernarySearchTrie.TSTNode root = this.trie.getRoot();
        if (root == null) {
            return false;
        }
        this.writeRecursively(output, root);
        return true;
    }
    
    @Override
    public boolean load(final DataInput input) throws IOException {
        this.count = input.readVLong();
        final JaspellTernarySearchTrie.TSTNode root = this.trie.new TSTNode('\0', null);
        this.readRecursively(input, root);
        this.trie.setRoot(root);
        return true;
    }
    
    public long ramBytesUsed() {
        return this.trie.ramBytesUsed();
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
}
