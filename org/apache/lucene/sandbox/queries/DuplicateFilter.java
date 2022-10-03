package org.apache.lucene.sandbox.queries;

import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.SparseFixedBitSet;
import org.apache.lucene.index.LeafReader;
import java.io.IOException;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Filter;

@Deprecated
public class DuplicateFilter extends Filter
{
    private KeepMode keepMode;
    private ProcessingMode processingMode;
    private String fieldName;
    
    public DuplicateFilter(final String fieldName) {
        this(fieldName, KeepMode.KM_USE_LAST_OCCURRENCE, ProcessingMode.PM_FULL_VALIDATION);
    }
    
    public DuplicateFilter(final String fieldName, final KeepMode keepMode, final ProcessingMode processingMode) {
        this.fieldName = fieldName;
        this.keepMode = keepMode;
        this.processingMode = processingMode;
    }
    
    public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
        if (this.processingMode == ProcessingMode.PM_FAST_INVALIDATION) {
            return this.fastBits(context.reader(), acceptDocs);
        }
        return this.correctBits(context.reader(), acceptDocs);
    }
    
    private DocIdSet correctBits(final LeafReader reader, final Bits acceptDocs) throws IOException {
        final SparseFixedBitSet bits = new SparseFixedBitSet(reader.maxDoc());
        final Terms terms = reader.fields().terms(this.fieldName);
        if (terms != null) {
            final TermsEnum termsEnum = terms.iterator();
            PostingsEnum docs = null;
            while (true) {
                final BytesRef currTerm = termsEnum.next();
                if (currTerm == null) {
                    break;
                }
                docs = termsEnum.postings(docs, 0);
                int doc = docs.nextDoc();
                if (doc == Integer.MAX_VALUE) {
                    continue;
                }
                if (this.keepMode == KeepMode.KM_USE_FIRST_OCCURRENCE) {
                    bits.set(doc);
                }
                else {
                    int lastDoc = doc;
                    do {
                        lastDoc = doc;
                        doc = docs.nextDoc();
                    } while (doc != Integer.MAX_VALUE);
                    bits.set(lastDoc);
                }
            }
        }
        return BitsFilteredDocIdSet.wrap((DocIdSet)new BitDocIdSet((BitSet)bits, (long)bits.approximateCardinality()), acceptDocs);
    }
    
    private DocIdSet fastBits(final LeafReader reader, final Bits acceptDocs) throws IOException {
        final FixedBitSet bits = new FixedBitSet(reader.maxDoc());
        bits.set(0, reader.maxDoc());
        final Terms terms = reader.fields().terms(this.fieldName);
        if (terms != null) {
            final TermsEnum termsEnum = terms.iterator();
            PostingsEnum docs = null;
            while (true) {
                final BytesRef currTerm = termsEnum.next();
                if (currTerm == null) {
                    break;
                }
                if (termsEnum.docFreq() <= 1) {
                    continue;
                }
                docs = termsEnum.postings(docs, 0);
                int doc = docs.nextDoc();
                if (doc != Integer.MAX_VALUE && this.keepMode == KeepMode.KM_USE_FIRST_OCCURRENCE) {
                    doc = docs.nextDoc();
                }
                int lastDoc = -1;
                do {
                    lastDoc = doc;
                    bits.clear(lastDoc);
                    doc = docs.nextDoc();
                } while (doc != Integer.MAX_VALUE);
                if (this.keepMode != KeepMode.KM_USE_LAST_OCCURRENCE) {
                    continue;
                }
                bits.set(lastDoc);
            }
        }
        return BitsFilteredDocIdSet.wrap((DocIdSet)new BitDocIdSet((BitSet)bits), acceptDocs);
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
    
    public KeepMode getKeepMode() {
        return this.keepMode;
    }
    
    public void setKeepMode(final KeepMode keepMode) {
        this.keepMode = keepMode;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final DuplicateFilter other = (DuplicateFilter)obj;
        return this.keepMode == other.keepMode && this.processingMode == other.processingMode && this.fieldName != null && this.fieldName.equals(other.fieldName);
    }
    
    public String toString(final String field) {
        return "DuplicateFilter(fieldName=" + this.fieldName + "," + "keepMode=" + ((this.keepMode == KeepMode.KM_USE_FIRST_OCCURRENCE) ? "first" : "last") + "," + "processingMode=" + ((this.processingMode == ProcessingMode.PM_FAST_INVALIDATION) ? "fast" : "full") + ")";
    }
    
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * hash + this.keepMode.hashCode();
        hash = 31 * hash + this.processingMode.hashCode();
        hash = 31 * hash + this.fieldName.hashCode();
        return hash;
    }
    
    public ProcessingMode getProcessingMode() {
        return this.processingMode;
    }
    
    public void setProcessingMode(final ProcessingMode processingMode) {
        this.processingMode = processingMode;
    }
    
    public enum KeepMode
    {
        KM_USE_FIRST_OCCURRENCE, 
        KM_USE_LAST_OCCURRENCE;
    }
    
    public enum ProcessingMode
    {
        PM_FULL_VALIDATION, 
        PM_FAST_INVALIDATION;
    }
}
