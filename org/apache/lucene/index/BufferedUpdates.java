package org.apache.lucene.index;

import org.apache.lucene.util.RamUsageEstimator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.lucene.search.Query;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class BufferedUpdates
{
    static final int BYTES_PER_DEL_TERM;
    static final int BYTES_PER_DEL_DOCID;
    static final int BYTES_PER_DEL_QUERY;
    static final int BYTES_PER_NUMERIC_FIELD_ENTRY;
    static final int BYTES_PER_NUMERIC_UPDATE_ENTRY;
    static final int BYTES_PER_BINARY_FIELD_ENTRY;
    static final int BYTES_PER_BINARY_UPDATE_ENTRY;
    final AtomicInteger numTermDeletes;
    final AtomicInteger numNumericUpdates;
    final AtomicInteger numBinaryUpdates;
    final Map<Term, Integer> terms;
    final Map<Query, Integer> queries;
    final List<Integer> docIDs;
    final Map<String, LinkedHashMap<Term, DocValuesUpdate.NumericDocValuesUpdate>> numericUpdates;
    final Map<String, LinkedHashMap<Term, DocValuesUpdate.BinaryDocValuesUpdate>> binaryUpdates;
    public static final Integer MAX_INT;
    final AtomicLong bytesUsed;
    private static final boolean VERBOSE_DELETES = false;
    long gen;
    
    public BufferedUpdates() {
        this.numTermDeletes = new AtomicInteger();
        this.numNumericUpdates = new AtomicInteger();
        this.numBinaryUpdates = new AtomicInteger();
        this.terms = new HashMap<Term, Integer>();
        this.queries = new HashMap<Query, Integer>();
        this.docIDs = new ArrayList<Integer>();
        this.numericUpdates = new HashMap<String, LinkedHashMap<Term, DocValuesUpdate.NumericDocValuesUpdate>>();
        this.binaryUpdates = new HashMap<String, LinkedHashMap<Term, DocValuesUpdate.BinaryDocValuesUpdate>>();
        this.bytesUsed = new AtomicLong();
    }
    
    @Override
    public String toString() {
        String s = "gen=" + this.gen;
        if (this.numTermDeletes.get() != 0) {
            s = s + " " + this.numTermDeletes.get() + " deleted terms (unique count=" + this.terms.size() + ")";
        }
        if (this.queries.size() != 0) {
            s = s + " " + this.queries.size() + " deleted queries";
        }
        if (this.docIDs.size() != 0) {
            s = s + " " + this.docIDs.size() + " deleted docIDs";
        }
        if (this.numNumericUpdates.get() != 0) {
            s = s + " " + this.numNumericUpdates.get() + " numeric updates (unique count=" + this.numericUpdates.size() + ")";
        }
        if (this.numBinaryUpdates.get() != 0) {
            s = s + " " + this.numBinaryUpdates.get() + " binary updates (unique count=" + this.binaryUpdates.size() + ")";
        }
        if (this.bytesUsed.get() != 0L) {
            s = s + " bytesUsed=" + this.bytesUsed.get();
        }
        return s;
    }
    
    public void addQuery(final Query query, final int docIDUpto) {
        final Integer current = this.queries.put(query, docIDUpto);
        if (current == null) {
            this.bytesUsed.addAndGet(BufferedUpdates.BYTES_PER_DEL_QUERY);
        }
    }
    
    public void addDocID(final int docID) {
        this.docIDs.add(docID);
        this.bytesUsed.addAndGet(BufferedUpdates.BYTES_PER_DEL_DOCID);
    }
    
    public void addTerm(final Term term, final int docIDUpto) {
        final Integer current = this.terms.get(term);
        if (current != null && docIDUpto < current) {
            return;
        }
        this.terms.put(term, docIDUpto);
        this.numTermDeletes.incrementAndGet();
        if (current == null) {
            this.bytesUsed.addAndGet(BufferedUpdates.BYTES_PER_DEL_TERM + term.bytes.length + 2 * term.field().length());
        }
    }
    
    public void addNumericUpdate(final DocValuesUpdate.NumericDocValuesUpdate update, final int docIDUpto) {
        LinkedHashMap<Term, DocValuesUpdate.NumericDocValuesUpdate> fieldUpdates = this.numericUpdates.get(update.field);
        if (fieldUpdates == null) {
            fieldUpdates = new LinkedHashMap<Term, DocValuesUpdate.NumericDocValuesUpdate>();
            this.numericUpdates.put(update.field, fieldUpdates);
            this.bytesUsed.addAndGet(BufferedUpdates.BYTES_PER_NUMERIC_FIELD_ENTRY);
        }
        final DocValuesUpdate.NumericDocValuesUpdate current = fieldUpdates.get(update.term);
        if (current != null && docIDUpto < current.docIDUpto) {
            return;
        }
        update.docIDUpto = docIDUpto;
        if (current != null) {
            fieldUpdates.remove(update.term);
        }
        fieldUpdates.put(update.term, update);
        this.numNumericUpdates.incrementAndGet();
        if (current == null) {
            this.bytesUsed.addAndGet(BufferedUpdates.BYTES_PER_NUMERIC_UPDATE_ENTRY + update.sizeInBytes());
        }
    }
    
    public void addBinaryUpdate(final DocValuesUpdate.BinaryDocValuesUpdate update, final int docIDUpto) {
        LinkedHashMap<Term, DocValuesUpdate.BinaryDocValuesUpdate> fieldUpdates = this.binaryUpdates.get(update.field);
        if (fieldUpdates == null) {
            fieldUpdates = new LinkedHashMap<Term, DocValuesUpdate.BinaryDocValuesUpdate>();
            this.binaryUpdates.put(update.field, fieldUpdates);
            this.bytesUsed.addAndGet(BufferedUpdates.BYTES_PER_BINARY_FIELD_ENTRY);
        }
        final DocValuesUpdate.BinaryDocValuesUpdate current = fieldUpdates.get(update.term);
        if (current != null && docIDUpto < current.docIDUpto) {
            return;
        }
        update.docIDUpto = docIDUpto;
        if (current != null) {
            fieldUpdates.remove(update.term);
        }
        fieldUpdates.put(update.term, update);
        this.numBinaryUpdates.incrementAndGet();
        if (current == null) {
            this.bytesUsed.addAndGet(BufferedUpdates.BYTES_PER_BINARY_UPDATE_ENTRY + update.sizeInBytes());
        }
    }
    
    void clear() {
        this.terms.clear();
        this.queries.clear();
        this.docIDs.clear();
        this.numericUpdates.clear();
        this.binaryUpdates.clear();
        this.numTermDeletes.set(0);
        this.numNumericUpdates.set(0);
        this.numBinaryUpdates.set(0);
        this.bytesUsed.set(0L);
    }
    
    boolean any() {
        return this.terms.size() > 0 || this.docIDs.size() > 0 || this.queries.size() > 0 || this.numericUpdates.size() > 0 || this.binaryUpdates.size() > 0;
    }
    
    static {
        BYTES_PER_DEL_TERM = 9 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 7 * RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 40;
        BYTES_PER_DEL_DOCID = 2 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4;
        BYTES_PER_DEL_QUERY = 5 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 2 * RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + 24;
        BYTES_PER_NUMERIC_FIELD_ENTRY = 7 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 3 * RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 20 + 4;
        BYTES_PER_NUMERIC_UPDATE_ENTRY = 7 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4;
        BYTES_PER_BINARY_FIELD_ENTRY = 7 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 3 * RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 20 + 4;
        BYTES_PER_BINARY_UPDATE_ENTRY = 7 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4;
        MAX_INT = Integer.MAX_VALUE;
    }
}
