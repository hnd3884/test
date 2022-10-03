package org.apache.lucene.uninverting;

import java.util.Arrays;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.SortedSetDocValues;
import java.util.List;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.PagedBytes;
import java.util.ArrayList;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.TermsEnum;
import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReader;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Accountable;

public class DocTermOrds implements Accountable
{
    private static final int TNUM_OFFSET = 2;
    public static final int DEFAULT_INDEX_INTERVAL_BITS = 7;
    private int indexIntervalBits;
    private int indexIntervalMask;
    private int indexInterval;
    protected final int maxTermDocFreq;
    protected final String field;
    protected int numTermsInField;
    protected long termInstances;
    private long memsz;
    protected int total_time;
    protected int phase1_time;
    protected int[] index;
    protected byte[][] tnums;
    protected long sizeOfIndexedStrings;
    protected BytesRef[] indexedTermsArray;
    protected BytesRef prefix;
    protected int ordBase;
    protected PostingsEnum postingsEnum;
    
    public long ramBytesUsed() {
        if (this.memsz != 0L) {
            return this.memsz;
        }
        long sz = 96L;
        if (this.index != null) {
            sz += this.index.length * 4;
        }
        if (this.tnums != null) {
            for (final byte[] arr : this.tnums) {
                if (arr != null) {
                    sz += arr.length;
                }
            }
        }
        return this.memsz = sz;
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public DocTermOrds(final LeafReader reader, final Bits liveDocs, final String field) throws IOException {
        this(reader, liveDocs, field, null, Integer.MAX_VALUE);
    }
    
    public DocTermOrds(final LeafReader reader, final Bits liveDocs, final String field, final BytesRef termPrefix) throws IOException {
        this(reader, liveDocs, field, termPrefix, Integer.MAX_VALUE);
    }
    
    public DocTermOrds(final LeafReader reader, final Bits liveDocs, final String field, final BytesRef termPrefix, final int maxTermDocFreq) throws IOException {
        this(reader, liveDocs, field, termPrefix, maxTermDocFreq, 7);
    }
    
    public DocTermOrds(final LeafReader reader, final Bits liveDocs, final String field, final BytesRef termPrefix, final int maxTermDocFreq, final int indexIntervalBits) throws IOException {
        this(field, maxTermDocFreq, indexIntervalBits);
        this.uninvert(reader, liveDocs, termPrefix);
    }
    
    protected DocTermOrds(final String field, final int maxTermDocFreq, final int indexIntervalBits) {
        this.tnums = new byte[256][];
        this.indexedTermsArray = new BytesRef[0];
        this.field = field;
        this.maxTermDocFreq = maxTermDocFreq;
        this.indexIntervalBits = indexIntervalBits;
        this.indexIntervalMask = -1 >>> 32 - indexIntervalBits;
        this.indexInterval = 1 << indexIntervalBits;
    }
    
    public TermsEnum getOrdTermsEnum(final LeafReader reader) throws IOException {
        assert null != this.indexedTermsArray;
        if (0 == this.indexedTermsArray.length) {
            return null;
        }
        return new OrdWrappedTermsEnum(reader);
    }
    
    public int numTerms() {
        return this.numTermsInField;
    }
    
    public boolean isEmpty() {
        return this.index == null;
    }
    
    protected void visitTerm(final TermsEnum te, final int termNum) throws IOException {
    }
    
    protected void setActualDocFreq(final int termNum, final int df) throws IOException {
    }
    
    protected void uninvert(final LeafReader reader, final Bits liveDocs, final BytesRef termPrefix) throws IOException {
        final FieldInfo info = reader.getFieldInfos().fieldInfo(this.field);
        if (info != null && info.getDocValuesType() != DocValuesType.NONE) {
            throw new IllegalStateException("Type mismatch: " + this.field + " was indexed as " + info.getDocValuesType());
        }
        final long startTime = System.currentTimeMillis();
        this.prefix = ((termPrefix == null) ? null : BytesRef.deepCopyOf(termPrefix));
        final int maxDoc = reader.maxDoc();
        final int[] index = new int[maxDoc];
        final int[] lastTerm = new int[maxDoc];
        final byte[][] bytes = new byte[maxDoc][];
        final Terms terms = reader.terms(this.field);
        if (terms == null) {
            return;
        }
        final TermsEnum te = terms.iterator();
        final BytesRef seekStart = (termPrefix != null) ? termPrefix : new BytesRef();
        if (te.seekCeil(seekStart) == TermsEnum.SeekStatus.END) {
            return;
        }
        final List<BytesRef> indexedTerms = new ArrayList<BytesRef>();
        final PagedBytes indexedTermsBytes = new PagedBytes(15);
        byte[] tempArr = new byte[12];
        int termNum = 0;
        this.postingsEnum = null;
        do {
            final BytesRef t = te.term();
            if (t == null) {
                break;
            }
            if (termPrefix != null && !StringHelper.startsWith(t, termPrefix)) {
                break;
            }
            this.visitTerm(te, termNum);
            if ((termNum & this.indexIntervalMask) == 0x0) {
                this.sizeOfIndexedStrings += t.length;
                final BytesRef indexedTerm = new BytesRef();
                indexedTermsBytes.copy(t, indexedTerm);
                indexedTerms.add(indexedTerm);
            }
            final int df = te.docFreq();
            if (df <= this.maxTermDocFreq) {
                this.postingsEnum = te.postings(this.postingsEnum, 0);
                int actualDF = 0;
                while (true) {
                    final int doc = this.postingsEnum.nextDoc();
                    if (doc == Integer.MAX_VALUE) {
                        break;
                    }
                    ++actualDF;
                    ++this.termInstances;
                    final int delta = termNum - lastTerm[doc] + 2;
                    lastTerm[doc] = termNum;
                    int val = index[doc];
                    if ((val & 0xFF) == 0x1) {
                        int pos = val >>> 8;
                        final int ilen = vIntSize(delta);
                        byte[] arr = bytes[doc];
                        final int newend = pos + ilen;
                        if (newend > arr.length) {
                            final int newLen = newend + 3 & 0xFFFFFFFC;
                            final byte[] newarr = new byte[newLen];
                            System.arraycopy(arr, 0, newarr, 0, pos);
                            arr = newarr;
                            bytes[doc] = newarr;
                        }
                        pos = writeInt(delta, arr, pos);
                        index[doc] = (pos << 8 | 0x1);
                    }
                    else {
                        int ipos;
                        if (val == 0) {
                            ipos = 0;
                        }
                        else if ((val & 0xFF80) == 0x0) {
                            ipos = 1;
                        }
                        else if ((val & 0xFF8000) == 0x0) {
                            ipos = 2;
                        }
                        else if ((val & 0xFF800000) == 0x0) {
                            ipos = 3;
                        }
                        else {
                            ipos = 4;
                        }
                        final int endPos = writeInt(delta, tempArr, ipos);
                        if (endPos <= 4) {
                            for (int j = ipos; j < endPos; ++j) {
                                val |= (tempArr[j] & 0xFF) << (j << 3);
                            }
                            index[doc] = val;
                        }
                        else {
                            for (int j = 0; j < ipos; ++j) {
                                tempArr[j] = (byte)val;
                                val >>>= 8;
                            }
                            index[doc] = (endPos << 8 | 0x1);
                            bytes[doc] = tempArr;
                            tempArr = new byte[12];
                        }
                    }
                }
                this.setActualDocFreq(termNum, actualDF);
            }
            ++termNum;
        } while (te.next() != null);
        this.numTermsInField = termNum;
        final long midPoint = System.currentTimeMillis();
        if (this.termInstances == 0L) {
            this.tnums = null;
        }
        else {
            this.index = index;
            for (int pass = 0; pass < 256; ++pass) {
                byte[] target = this.tnums[pass];
                int pos2 = 0;
                if (target != null) {
                    pos2 = target.length;
                }
                else {
                    target = new byte[4096];
                }
                for (int docbase = pass << 16; docbase < maxDoc; docbase += 16777216) {
                    for (int lim = Math.min(docbase + 65536, maxDoc), doc2 = docbase; doc2 < lim; ++doc2) {
                        final int val2 = index[doc2];
                        if ((val2 & 0xFF) == 0x1) {
                            final int len = val2 >>> 8;
                            index[doc2] = (pos2 << 8 | 0x1);
                            if ((pos2 & 0xFF000000) != 0x0) {
                                throw new IllegalStateException("Too many values for UnInvertedField faceting on field " + this.field);
                            }
                            final byte[] arr2 = bytes[doc2];
                            bytes[doc2] = null;
                            if (target.length <= pos2 + len) {
                                int newlen;
                                for (newlen = target.length; newlen <= pos2 + len; newlen <<= 1) {}
                                final byte[] newtarget = new byte[newlen];
                                System.arraycopy(target, 0, newtarget, 0, pos2);
                                target = newtarget;
                            }
                            System.arraycopy(arr2, 0, target, pos2, len);
                            pos2 += len + 1;
                        }
                    }
                }
                if (pos2 < target.length) {
                    final byte[] newtarget2 = new byte[pos2];
                    System.arraycopy(target, 0, newtarget2, 0, pos2);
                    target = newtarget2;
                }
                this.tnums[pass] = target;
                if (pass << 16 > maxDoc) {
                    break;
                }
            }
        }
        this.indexedTermsArray = indexedTerms.toArray(new BytesRef[indexedTerms.size()]);
        final long endTime = System.currentTimeMillis();
        this.total_time = (int)(endTime - startTime);
        this.phase1_time = (int)(midPoint - startTime);
    }
    
    private static int vIntSize(final int x) {
        if ((x & 0xFFFFFF80) == 0x0) {
            return 1;
        }
        if ((x & 0xFFFFC000) == 0x0) {
            return 2;
        }
        if ((x & 0xFFE00000) == 0x0) {
            return 3;
        }
        if ((x & 0xF0000000) == 0x0) {
            return 4;
        }
        return 5;
    }
    
    private static int writeInt(final int x, final byte[] arr, int pos) {
        int a = x >>> 28;
        if (a != 0) {
            arr[pos++] = (byte)(a | 0x80);
        }
        a = x >>> 21;
        if (a != 0) {
            arr[pos++] = (byte)(a | 0x80);
        }
        a = x >>> 14;
        if (a != 0) {
            arr[pos++] = (byte)(a | 0x80);
        }
        a = x >>> 7;
        if (a != 0) {
            arr[pos++] = (byte)(a | 0x80);
        }
        arr[pos++] = (byte)(x & 0x7F);
        return pos;
    }
    
    public BytesRef lookupTerm(final TermsEnum termsEnum, final int ord) throws IOException {
        termsEnum.seekExact((long)ord);
        return termsEnum.term();
    }
    
    public SortedSetDocValues iterator(final LeafReader reader) throws IOException {
        if (this.isEmpty()) {
            return (SortedSetDocValues)DocValues.emptySortedSet();
        }
        return new Iterator(reader);
    }
    
    private final class OrdWrappedTermsEnum extends TermsEnum
    {
        private final TermsEnum termsEnum;
        private BytesRef term;
        private long ord;
        
        public OrdWrappedTermsEnum(final LeafReader reader) throws IOException {
            this.ord = -DocTermOrds.this.indexInterval - 1;
            assert DocTermOrds.this.indexedTermsArray != null;
            assert 0 != DocTermOrds.this.indexedTermsArray.length;
            this.termsEnum = reader.fields().terms(DocTermOrds.this.field).iterator();
        }
        
        public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
            return this.termsEnum.postings(reuse, flags);
        }
        
        public BytesRef term() {
            return this.term;
        }
        
        public BytesRef next() throws IOException {
            final long ord = this.ord + 1L;
            this.ord = ord;
            if (ord < 0L) {
                this.ord = 0L;
            }
            if (this.termsEnum.next() == null) {
                return this.term = null;
            }
            return this.setTerm();
        }
        
        public int docFreq() throws IOException {
            return this.termsEnum.docFreq();
        }
        
        public long totalTermFreq() throws IOException {
            return this.termsEnum.totalTermFreq();
        }
        
        public long ord() {
            return DocTermOrds.this.ordBase + this.ord;
        }
        
        public TermsEnum.SeekStatus seekCeil(final BytesRef target) throws IOException {
            if (this.term != null && this.term.equals((Object)target)) {
                return TermsEnum.SeekStatus.FOUND;
            }
            int startIdx = Arrays.binarySearch(DocTermOrds.this.indexedTermsArray, target);
            if (startIdx >= 0) {
                final TermsEnum.SeekStatus seekStatus = this.termsEnum.seekCeil(target);
                assert seekStatus == TermsEnum.SeekStatus.FOUND;
                this.ord = startIdx << DocTermOrds.this.indexIntervalBits;
                this.setTerm();
                assert this.term != null;
                return TermsEnum.SeekStatus.FOUND;
            }
            else {
                startIdx = -startIdx - 1;
                if (startIdx == 0) {
                    final TermsEnum.SeekStatus seekStatus = this.termsEnum.seekCeil(target);
                    assert seekStatus == TermsEnum.SeekStatus.NOT_FOUND;
                    this.ord = 0L;
                    this.setTerm();
                    assert this.term != null;
                    return TermsEnum.SeekStatus.NOT_FOUND;
                }
                else {
                    --startIdx;
                    if (this.ord >> DocTermOrds.this.indexIntervalBits != startIdx || this.term == null || this.term.compareTo(target) > 0) {
                        final TermsEnum.SeekStatus seekStatus = this.termsEnum.seekCeil(DocTermOrds.this.indexedTermsArray[startIdx]);
                        assert seekStatus == TermsEnum.SeekStatus.FOUND;
                        this.ord = startIdx << DocTermOrds.this.indexIntervalBits;
                        this.setTerm();
                        assert this.term != null;
                    }
                    while (this.term != null && this.term.compareTo(target) < 0) {
                        this.next();
                    }
                    if (this.term == null) {
                        return TermsEnum.SeekStatus.END;
                    }
                    if (this.term.compareTo(target) == 0) {
                        return TermsEnum.SeekStatus.FOUND;
                    }
                    return TermsEnum.SeekStatus.NOT_FOUND;
                }
            }
        }
        
        public void seekExact(final long targetOrd) throws IOException {
            int delta = (int)(targetOrd - DocTermOrds.this.ordBase - this.ord);
            if (delta < 0 || delta > DocTermOrds.this.indexInterval) {
                final int idx = (int)(targetOrd >>> DocTermOrds.this.indexIntervalBits);
                final BytesRef base = DocTermOrds.this.indexedTermsArray[idx];
                this.ord = idx << DocTermOrds.this.indexIntervalBits;
                delta = (int)(targetOrd - this.ord);
                final TermsEnum.SeekStatus seekStatus = this.termsEnum.seekCeil(base);
                assert seekStatus == TermsEnum.SeekStatus.FOUND;
            }
            while (--delta >= 0) {
                final BytesRef br = this.termsEnum.next();
                if (br == null) {
                    assert false;
                    return;
                }
                else {
                    ++this.ord;
                }
            }
            this.setTerm();
            assert this.term != null;
        }
        
        private BytesRef setTerm() throws IOException {
            this.term = this.termsEnum.term();
            if (DocTermOrds.this.prefix != null && !StringHelper.startsWith(this.term, DocTermOrds.this.prefix)) {
                this.term = null;
            }
            return this.term;
        }
    }
    
    private class Iterator extends SortedSetDocValues
    {
        final LeafReader reader;
        final TermsEnum te;
        final int[] buffer;
        int bufferUpto;
        int bufferLength;
        private int tnum;
        private int upto;
        private byte[] arr;
        
        Iterator(final LeafReader reader) throws IOException {
            this.buffer = new int[5];
            this.reader = reader;
            this.te = this.termsEnum();
        }
        
        public long nextOrd() {
            while (this.bufferUpto == this.bufferLength) {
                if (this.bufferLength < this.buffer.length) {
                    return -1L;
                }
                this.bufferLength = this.read(this.buffer);
                this.bufferUpto = 0;
            }
            return this.buffer[this.bufferUpto++];
        }
        
        int read(final int[] buffer) {
            int bufferUpto = 0;
            if (this.arr == null) {
                int code = this.upto;
                int delta = 0;
                while (true) {
                    delta = (delta << 7 | (code & 0x7F));
                    if ((code & 0x80) == 0x0) {
                        if (delta == 0) {
                            break;
                        }
                        this.tnum += delta - 2;
                        buffer[bufferUpto++] = DocTermOrds.this.ordBase + this.tnum;
                        delta = 0;
                    }
                    code >>>= 8;
                }
            }
            else {
                do {
                    int delta2 = 0;
                    byte b;
                    do {
                        b = this.arr[this.upto++];
                        delta2 = (delta2 << 7 | (b & 0x7F));
                    } while ((b & 0x80) != 0x0);
                    if (delta2 == 0) {
                        break;
                    }
                    this.tnum += delta2 - 2;
                    buffer[bufferUpto++] = DocTermOrds.this.ordBase + this.tnum;
                } while (bufferUpto != buffer.length);
            }
            return bufferUpto;
        }
        
        public void setDocument(final int docID) {
            this.tnum = 0;
            final int code = DocTermOrds.this.index[docID];
            if ((code & 0xFF) == 0x1) {
                this.upto = code >>> 8;
                final int whichArray = docID >>> 16 & 0xFF;
                this.arr = DocTermOrds.this.tnums[whichArray];
            }
            else {
                this.arr = null;
                this.upto = code;
            }
            this.bufferUpto = 0;
            this.bufferLength = this.read(this.buffer);
        }
        
        public BytesRef lookupOrd(final long ord) {
            try {
                return DocTermOrds.this.lookupTerm(this.te, (int)ord);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        public long getValueCount() {
            return DocTermOrds.this.numTerms();
        }
        
        public long lookupTerm(final BytesRef key) {
            try {
                switch (this.te.seekCeil(key)) {
                    case FOUND: {
                        assert this.te.ord() >= 0L;
                        return this.te.ord();
                    }
                    case NOT_FOUND: {
                        assert this.te.ord() >= 0L;
                        return -this.te.ord() - 1L;
                    }
                    default: {
                        return -DocTermOrds.this.numTerms() - 1;
                    }
                }
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        public TermsEnum termsEnum() {
            try {
                return DocTermOrds.this.getOrdTermsEnum(this.reader);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
