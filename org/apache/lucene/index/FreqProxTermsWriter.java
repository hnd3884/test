package org.apache.lucene.index;

import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.util.CollectionUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;

final class FreqProxTermsWriter extends TermsHash
{
    public FreqProxTermsWriter(final DocumentsWriterPerThread docWriter, final TermsHash termVectors) {
        super(docWriter, true, termVectors);
    }
    
    private void applyDeletes(final SegmentWriteState state, final Fields fields) throws IOException {
        if (state.segUpdates != null && state.segUpdates.terms.size() > 0) {
            final Map<Term, Integer> segDeletes = state.segUpdates.terms;
            final List<Term> deleteTerms = new ArrayList<Term>(segDeletes.keySet());
            Collections.sort(deleteTerms);
            String lastField = null;
            TermsEnum termsEnum = null;
            PostingsEnum postingsEnum = null;
            for (final Term deleteTerm : deleteTerms) {
                if (!deleteTerm.field().equals(lastField)) {
                    lastField = deleteTerm.field();
                    final Terms terms = fields.terms(lastField);
                    if (terms != null) {
                        termsEnum = terms.iterator();
                    }
                    else {
                        termsEnum = null;
                    }
                }
                if (termsEnum != null && termsEnum.seekExact(deleteTerm.bytes())) {
                    postingsEnum = termsEnum.postings(postingsEnum, 0);
                    final int delDocLimit = segDeletes.get(deleteTerm);
                    assert delDocLimit < Integer.MAX_VALUE;
                    while (true) {
                        final int doc = postingsEnum.nextDoc();
                        if (doc >= delDocLimit) {
                            break;
                        }
                        if (state.liveDocs == null) {
                            state.liveDocs = state.segmentInfo.getCodec().liveDocsFormat().newLiveDocs(state.segmentInfo.maxDoc());
                        }
                        if (!state.liveDocs.get(doc)) {
                            continue;
                        }
                        ++state.delCountOnFlush;
                        state.liveDocs.clear(doc);
                    }
                }
            }
        }
    }
    
    public void flush(final Map<String, TermsHashPerField> fieldsToFlush, final SegmentWriteState state) throws IOException {
        super.flush(fieldsToFlush, state);
        final List<FreqProxTermsWriterPerField> allFields = new ArrayList<FreqProxTermsWriterPerField>();
        for (final TermsHashPerField f : fieldsToFlush.values()) {
            final FreqProxTermsWriterPerField perField = (FreqProxTermsWriterPerField)f;
            if (perField.bytesHash.size() > 0) {
                perField.sortPostings();
                assert perField.fieldInfo.getIndexOptions() != IndexOptions.NONE;
                allFields.add(perField);
            }
        }
        CollectionUtil.introSort(allFields);
        final Fields fields = new FreqProxFields(allFields);
        this.applyDeletes(state, fields);
        final FieldsConsumer consumer = state.segmentInfo.getCodec().postingsFormat().fieldsConsumer(state);
        boolean success = false;
        try {
            consumer.write(fields);
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(consumer);
            }
            else {
                IOUtils.closeWhileHandlingException(consumer);
            }
        }
    }
    
    public TermsHashPerField addField(final FieldInvertState invertState, final FieldInfo fieldInfo) {
        return new FreqProxTermsWriterPerField(invertState, this, fieldInfo, this.nextTermsHash.addField(invertState, fieldInfo));
    }
}
