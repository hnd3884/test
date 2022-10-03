package org.apache.lucene.index;

import java.util.HashSet;
import java.util.Collection;
import org.apache.lucene.util.MergedIterator;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public final class MultiFields extends Fields
{
    private final Fields[] subs;
    private final ReaderSlice[] subSlices;
    private final Map<String, Terms> terms;
    
    public static Fields getFields(final IndexReader reader) throws IOException {
        final List<LeafReaderContext> leaves = reader.leaves();
        switch (leaves.size()) {
            case 1: {
                return leaves.get(0).reader().fields();
            }
            default: {
                final List<Fields> fields = new ArrayList<Fields>(leaves.size());
                final List<ReaderSlice> slices = new ArrayList<ReaderSlice>(leaves.size());
                for (final LeafReaderContext ctx : leaves) {
                    final LeafReader r = ctx.reader();
                    final Fields f = r.fields();
                    fields.add(f);
                    slices.add(new ReaderSlice(ctx.docBase, r.maxDoc(), fields.size() - 1));
                }
                if (fields.size() == 1) {
                    return fields.get(0);
                }
                return new MultiFields(fields.toArray(Fields.EMPTY_ARRAY), slices.toArray(ReaderSlice.EMPTY_ARRAY));
            }
        }
    }
    
    public static Bits getLiveDocs(final IndexReader reader) {
        if (!reader.hasDeletions()) {
            return null;
        }
        final List<LeafReaderContext> leaves = reader.leaves();
        final int size = leaves.size();
        assert size > 0 : "A reader with deletions must have at least one leave";
        if (size == 1) {
            return leaves.get(0).reader().getLiveDocs();
        }
        final Bits[] liveDocs = new Bits[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext ctx = leaves.get(i);
            liveDocs[i] = ctx.reader().getLiveDocs();
            starts[i] = ctx.docBase;
        }
        starts[size] = reader.maxDoc();
        return new MultiBits(liveDocs, starts, true);
    }
    
    public static Terms getTerms(final IndexReader r, final String field) throws IOException {
        return getFields(r).terms(field);
    }
    
    public static PostingsEnum getTermDocsEnum(final IndexReader r, final String field, final BytesRef term) throws IOException {
        return getTermDocsEnum(r, field, term, 8);
    }
    
    public static PostingsEnum getTermDocsEnum(final IndexReader r, final String field, final BytesRef term, final int flags) throws IOException {
        assert field != null;
        assert term != null;
        final Terms terms = getTerms(r, field);
        if (terms != null) {
            final TermsEnum termsEnum = terms.iterator();
            if (termsEnum.seekExact(term)) {
                return termsEnum.postings(null, flags);
            }
        }
        return null;
    }
    
    public static PostingsEnum getTermPositionsEnum(final IndexReader r, final String field, final BytesRef term) throws IOException {
        return getTermPositionsEnum(r, field, term, 120);
    }
    
    public static PostingsEnum getTermPositionsEnum(final IndexReader r, final String field, final BytesRef term, final int flags) throws IOException {
        assert field != null;
        assert term != null;
        final Terms terms = getTerms(r, field);
        if (terms != null) {
            final TermsEnum termsEnum = terms.iterator();
            if (termsEnum.seekExact(term)) {
                return termsEnum.postings(null, flags);
            }
        }
        return null;
    }
    
    public MultiFields(final Fields[] subs, final ReaderSlice[] subSlices) {
        this.terms = new ConcurrentHashMap<String, Terms>();
        this.subs = subs;
        this.subSlices = subSlices;
    }
    
    @Override
    public Iterator<String> iterator() {
        final Iterator<String>[] subIterators = new Iterator[this.subs.length];
        for (int i = 0; i < this.subs.length; ++i) {
            subIterators[i] = this.subs[i].iterator();
        }
        return new MergedIterator<String>(subIterators);
    }
    
    @Override
    public Terms terms(final String field) throws IOException {
        Terms result = this.terms.get(field);
        if (result != null) {
            return result;
        }
        final List<Terms> subs2 = new ArrayList<Terms>();
        final List<ReaderSlice> slices2 = new ArrayList<ReaderSlice>();
        for (int i = 0; i < this.subs.length; ++i) {
            final Terms terms = this.subs[i].terms(field);
            if (terms != null) {
                subs2.add(terms);
                slices2.add(this.subSlices[i]);
            }
        }
        if (subs2.size() == 0) {
            result = null;
        }
        else {
            result = new MultiTerms(subs2.toArray(Terms.EMPTY_ARRAY), slices2.toArray(ReaderSlice.EMPTY_ARRAY));
            this.terms.put(field, result);
        }
        return result;
    }
    
    @Override
    public int size() {
        return -1;
    }
    
    public static FieldInfos getMergedFieldInfos(final IndexReader reader) {
        final FieldInfos.Builder builder = new FieldInfos.Builder();
        for (final LeafReaderContext ctx : reader.leaves()) {
            builder.add(ctx.reader().getFieldInfos());
        }
        return builder.finish();
    }
    
    public static Collection<String> getIndexedFields(final IndexReader reader) {
        final Collection<String> fields = new HashSet<String>();
        for (final FieldInfo fieldInfo : getMergedFieldInfos(reader)) {
            if (fieldInfo.getIndexOptions() != IndexOptions.NONE) {
                fields.add(fieldInfo.name);
            }
        }
        return fields;
    }
}
