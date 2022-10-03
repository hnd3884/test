package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.util.StringHelper;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.index.TermsEnum;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import java.util.List;

class AutoPrefixTermsWriter
{
    final List<PrefixTerm> prefixes;
    private final int minItemsInPrefix;
    private final int maxItemsInPrefix;
    private final BytesRefBuilder lastTerm;
    private int[] prefixStarts;
    private List<Object> pending;
    
    static String brToString(final BytesRef b) {
        try {
            return b.utf8ToString() + " " + b;
        }
        catch (final Throwable t) {
            return b.toString();
        }
    }
    
    public AutoPrefixTermsWriter(final Terms terms, final int minItemsInPrefix, final int maxItemsInPrefix) throws IOException {
        this.prefixes = new ArrayList<PrefixTerm>();
        this.lastTerm = new BytesRefBuilder();
        this.prefixStarts = new int[8];
        this.pending = new ArrayList<Object>();
        this.minItemsInPrefix = minItemsInPrefix;
        this.maxItemsInPrefix = maxItemsInPrefix;
        final TermsEnum termsEnum = terms.iterator();
        while (true) {
            final BytesRef term = termsEnum.next();
            if (term == null) {
                break;
            }
            this.pushTerm(term);
        }
        if (this.pending.size() > 1) {
            this.pushTerm(BlockTreeTermsWriter.EMPTY_BYTES_REF);
            while (this.pending.size() >= minItemsInPrefix) {
                this.savePrefixes(0, this.pending.size());
            }
        }
        Collections.sort(this.prefixes);
    }
    
    private void pushTerm(final BytesRef text) throws IOException {
        int limit;
        int pos;
        for (limit = Math.min(this.lastTerm.length(), text.length), pos = 0; pos < limit && this.lastTerm.byteAt(pos) == text.bytes[text.offset + pos]; ++pos) {}
        for (int i = this.lastTerm.length() - 1; i >= pos; --i) {
            for (int prefixTopSize = this.pending.size() - this.prefixStarts[i]; prefixTopSize >= this.minItemsInPrefix; prefixTopSize = this.pending.size() - this.prefixStarts[i]) {
                this.savePrefixes(i + 1, prefixTopSize);
            }
        }
        if (this.prefixStarts.length < text.length) {
            this.prefixStarts = ArrayUtil.grow(this.prefixStarts, text.length);
        }
        for (int i = pos; i < text.length; ++i) {
            this.prefixStarts[i] = this.pending.size();
        }
        this.lastTerm.copyBytes(text);
        if (text.length > 0 || this.pending.isEmpty()) {
            final byte[] termBytes = new byte[text.length];
            System.arraycopy(text.bytes, text.offset, termBytes, 0, text.length);
            this.pending.add(termBytes);
        }
    }
    
    void savePrefixes(final int prefixLength, int count) throws IOException {
        assert count > 0;
        int lastSuffixLeadLabel = -2;
        int start = this.pending.size() - count;
        assert start >= 0;
        Object o = this.pending.get(start);
        boolean skippedEmptyStringSuffix = false;
        if (o instanceof byte[]) {
            if (((byte[])o).length == prefixLength) {
                ++start;
                --count;
                skippedEmptyStringSuffix = true;
            }
        }
        else {
            final PrefixTerm prefix = (PrefixTerm)o;
            if (prefix.term.bytes.length == prefixLength) {
                ++start;
                --count;
                skippedEmptyStringSuffix = true;
            }
        }
        final int end = this.pending.size();
        int nextBlockStart = start;
        int nextFloorLeadLabel = -1;
        int prefixCount = 0;
        PrefixTerm lastPTEntry = null;
        for (int i = start; i < end; ++i) {
            o = this.pending.get(i);
            PrefixTerm ptEntry;
            byte[] termBytes;
            if (o instanceof byte[]) {
                ptEntry = null;
                termBytes = (byte[])o;
            }
            else {
                ptEntry = (PrefixTerm)o;
                termBytes = ptEntry.term.bytes;
                if (ptEntry.prefix.length != prefixLength) {
                    assert ptEntry.prefix.length > prefixLength;
                    ptEntry = null;
                }
            }
            assert termBytes.length > prefixLength;
            final int suffixLeadLabel = termBytes[prefixLength] & 0xFF;
            if (suffixLeadLabel != lastSuffixLeadLabel) {
                assert suffixLeadLabel > lastSuffixLeadLabel : "suffixLeadLabel=" + suffixLeadLabel + " vs lastSuffixLeadLabel=" + lastSuffixLeadLabel;
                final int itemsInBlock = i - nextBlockStart;
                if (itemsInBlock >= this.minItemsInPrefix && end - nextBlockStart > this.maxItemsInPrefix) {
                    if (lastPTEntry != null) {
                        lastSuffixLeadLabel = lastPTEntry.floorLeadEnd;
                    }
                    this.savePrefix(prefixLength, nextFloorLeadLabel, lastSuffixLeadLabel);
                    ++prefixCount;
                    nextFloorLeadLabel = suffixLeadLabel;
                    nextBlockStart = i;
                }
                if (nextFloorLeadLabel == -1) {
                    nextFloorLeadLabel = suffixLeadLabel;
                }
                lastSuffixLeadLabel = suffixLeadLabel;
            }
            lastPTEntry = ptEntry;
        }
        if (nextBlockStart < end) {
            if (lastPTEntry != null) {
                lastSuffixLeadLabel = lastPTEntry.floorLeadEnd;
            }
            assert lastSuffixLeadLabel >= nextFloorLeadLabel : "lastSuffixLeadLabel=" + lastSuffixLeadLabel + " nextFloorLeadLabel=" + nextFloorLeadLabel;
            if (prefixCount == 0) {
                if (prefixLength > 0) {
                    this.savePrefix(prefixLength, -2, 255);
                    ++prefixCount;
                    if (skippedEmptyStringSuffix) {
                        ++count;
                    }
                }
            }
            else {
                if (lastSuffixLeadLabel == -2) {
                    lastSuffixLeadLabel = 255;
                }
                this.savePrefix(prefixLength, nextFloorLeadLabel, lastSuffixLeadLabel);
                ++prefixCount;
            }
        }
        this.pending.subList(this.pending.size() - count, this.pending.size()).clear();
        for (int i = 0; i < prefixCount; ++i) {
            final PrefixTerm pt = this.prefixes.get(this.prefixes.size() - (prefixCount - i));
            this.pending.add(pt);
        }
    }
    
    private void savePrefix(final int prefixLength, final int floorLeadStart, final int floorLeadEnd) {
        final byte[] prefix = new byte[prefixLength];
        System.arraycopy(this.lastTerm.bytes(), 0, prefix, 0, prefixLength);
        assert floorLeadStart != -1;
        assert floorLeadEnd != -1;
        final PrefixTerm pt = new PrefixTerm(prefix, floorLeadStart, floorLeadEnd);
        this.prefixes.add(pt);
    }
    
    public static final class PrefixTerm implements Comparable<PrefixTerm>
    {
        public final byte[] prefix;
        public final int floorLeadStart;
        public final int floorLeadEnd;
        public final BytesRef term;
        
        public PrefixTerm(final byte[] prefix, final int floorLeadStart, final int floorLeadEnd) {
            this.prefix = prefix;
            this.floorLeadStart = floorLeadStart;
            this.floorLeadEnd = floorLeadEnd;
            this.term = toBytesRef(prefix, floorLeadStart);
            assert floorLeadEnd >= floorLeadStart;
            assert floorLeadEnd >= 0;
            assert floorLeadStart >= 0;
            assert floorLeadEnd != 255;
        }
        
        @Override
        public String toString() {
            String s = AutoPrefixTermsWriter.brToString(new BytesRef(this.prefix));
            if (this.floorLeadStart == -2) {
                s = s + "[-" + Integer.toHexString(this.floorLeadEnd) + "]";
            }
            else {
                s = s + "[" + Integer.toHexString(this.floorLeadStart) + "-" + Integer.toHexString(this.floorLeadEnd) + "]";
            }
            return s;
        }
        
        @Override
        public int compareTo(final PrefixTerm other) {
            int cmp = this.term.compareTo(other.term);
            if (cmp == 0) {
                if (this.prefix.length != other.prefix.length) {
                    return this.prefix.length - other.prefix.length;
                }
                cmp = other.floorLeadEnd - this.floorLeadEnd;
            }
            return cmp;
        }
        
        private static BytesRef toBytesRef(final byte[] prefix, final int floorLeadStart) {
            BytesRef br;
            if (floorLeadStart != -2) {
                assert floorLeadStart >= 0;
                br = new BytesRef(prefix.length + 1);
            }
            else {
                br = new BytesRef(prefix.length);
            }
            System.arraycopy(prefix, 0, br.bytes, 0, prefix.length);
            br.length = prefix.length;
            if (floorLeadStart != -2) {
                assert floorLeadStart >= 0;
                br.bytes[br.length++] = (byte)floorLeadStart;
            }
            return br;
        }
        
        public int compareTo(final BytesRef term) {
            return this.term.compareTo(term);
        }
        
        public TermsEnum getTermsEnum(final TermsEnum in) {
            final BytesRef prefixRef = new BytesRef(this.prefix);
            return new FilteredTermsEnum(in) {
                {
                    this.setInitialSeekTerm(PrefixTerm.this.term);
                }
                
                @Override
                protected AcceptStatus accept(final BytesRef term) {
                    if (StringHelper.startsWith(term, prefixRef) && (PrefixTerm.this.floorLeadEnd == -1 || term.length == prefixRef.length || (term.bytes[term.offset + prefixRef.length] & 0xFF) <= PrefixTerm.this.floorLeadEnd)) {
                        return AcceptStatus.YES;
                    }
                    return AcceptStatus.END;
                }
            };
        }
    }
}
