package org.apache.lucene.search.suggest;

import org.apache.lucene.document.Document;
import java.util.Collections;
import java.util.HashSet;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Bits;
import java.util.Set;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.Dictionary;

public class DocumentDictionary implements Dictionary
{
    protected final IndexReader reader;
    protected final String payloadField;
    protected final String contextsField;
    private final String field;
    private final String weightField;
    
    public DocumentDictionary(final IndexReader reader, final String field, final String weightField) {
        this(reader, field, weightField, null);
    }
    
    public DocumentDictionary(final IndexReader reader, final String field, final String weightField, final String payloadField) {
        this(reader, field, weightField, payloadField, null);
    }
    
    public DocumentDictionary(final IndexReader reader, final String field, final String weightField, final String payloadField, final String contextsField) {
        this.reader = reader;
        this.field = field;
        this.weightField = weightField;
        this.payloadField = payloadField;
        this.contextsField = contextsField;
    }
    
    @Override
    public InputIterator getEntryIterator() throws IOException {
        return new DocumentInputIterator(this.payloadField != null, this.contextsField != null);
    }
    
    protected class DocumentInputIterator implements InputIterator
    {
        private final int docCount;
        private final Set<String> relevantFields;
        private final boolean hasPayloads;
        private final boolean hasContexts;
        private final Bits liveDocs;
        private int currentDocId;
        private long currentWeight;
        private BytesRef currentPayload;
        private Set<BytesRef> currentContexts;
        private final NumericDocValues weightValues;
        IndexableField[] currentDocFields;
        int nextFieldsPosition;
        
        public DocumentInputIterator(final boolean hasPayloads, final boolean hasContexts) throws IOException {
            this.currentDocId = -1;
            this.currentWeight = 0L;
            this.currentPayload = null;
            this.currentDocFields = new IndexableField[0];
            this.nextFieldsPosition = 0;
            this.hasPayloads = hasPayloads;
            this.hasContexts = hasContexts;
            this.docCount = DocumentDictionary.this.reader.maxDoc() - 1;
            this.weightValues = ((DocumentDictionary.this.weightField != null) ? MultiDocValues.getNumericValues(DocumentDictionary.this.reader, DocumentDictionary.this.weightField) : null);
            this.liveDocs = ((DocumentDictionary.this.reader.leaves().size() > 0) ? MultiFields.getLiveDocs(DocumentDictionary.this.reader) : null);
            this.relevantFields = this.getRelevantFields(DocumentDictionary.this.field, DocumentDictionary.this.weightField, DocumentDictionary.this.payloadField, DocumentDictionary.this.contextsField);
        }
        
        @Override
        public long weight() {
            return this.currentWeight;
        }
        
        public BytesRef next() throws IOException {
            Document doc;
            BytesRef tempPayload;
            Set<BytesRef> tempContexts;
            BytesRef tempTerm;
            while (true) {
                if (this.nextFieldsPosition < this.currentDocFields.length) {
                    final IndexableField fieldValue = this.currentDocFields[this.nextFieldsPosition++];
                    if (fieldValue.binaryValue() != null) {
                        return fieldValue.binaryValue();
                    }
                    if (fieldValue.stringValue() != null) {
                        return new BytesRef((CharSequence)fieldValue.stringValue());
                    }
                    continue;
                }
                else {
                    if (this.currentDocId == this.docCount) {
                        return null;
                    }
                    ++this.currentDocId;
                    if (this.liveDocs != null && !this.liveDocs.get(this.currentDocId)) {
                        continue;
                    }
                    doc = DocumentDictionary.this.reader.document(this.currentDocId, (Set)this.relevantFields);
                    tempPayload = null;
                    if (this.hasPayloads) {
                        final IndexableField payload = doc.getField(DocumentDictionary.this.payloadField);
                        if (payload != null) {
                            if (payload.binaryValue() != null) {
                                tempPayload = payload.binaryValue();
                            }
                            else if (payload.stringValue() != null) {
                                tempPayload = new BytesRef((CharSequence)payload.stringValue());
                            }
                        }
                        if (tempPayload == null) {
                            tempPayload = new BytesRef();
                        }
                    }
                    if (this.hasContexts) {
                        tempContexts = new HashSet<BytesRef>();
                        final IndexableField[] arr$;
                        final IndexableField[] contextFields = arr$ = doc.getFields(DocumentDictionary.this.contextsField);
                        for (final IndexableField contextField : arr$) {
                            if (contextField.binaryValue() != null) {
                                tempContexts.add(contextField.binaryValue());
                            }
                            else if (contextField.stringValue() != null) {
                                tempContexts.add(new BytesRef((CharSequence)contextField.stringValue()));
                            }
                        }
                    }
                    else {
                        tempContexts = Collections.emptySet();
                    }
                    this.currentDocFields = doc.getFields(DocumentDictionary.this.field);
                    this.nextFieldsPosition = 0;
                    if (this.currentDocFields.length == 0) {
                        continue;
                    }
                    final IndexableField fieldValue2 = this.currentDocFields[this.nextFieldsPosition++];
                    if (fieldValue2.binaryValue() != null) {
                        tempTerm = fieldValue2.binaryValue();
                        break;
                    }
                    if (fieldValue2.stringValue() != null) {
                        tempTerm = new BytesRef((CharSequence)fieldValue2.stringValue());
                        break;
                    }
                    continue;
                }
            }
            this.currentPayload = tempPayload;
            this.currentContexts = tempContexts;
            this.currentWeight = this.getWeight(doc, this.currentDocId);
            return tempTerm;
        }
        
        @Override
        public BytesRef payload() {
            return this.currentPayload;
        }
        
        @Override
        public boolean hasPayloads() {
            return this.hasPayloads;
        }
        
        protected long getWeight(final Document doc, final int docId) {
            final IndexableField weight = doc.getField(DocumentDictionary.this.weightField);
            if (weight != null) {
                return (weight.numericValue() != null) ? weight.numericValue().longValue() : 0L;
            }
            if (this.weightValues != null) {
                return this.weightValues.get(docId);
            }
            return 0L;
        }
        
        private Set<String> getRelevantFields(final String... fields) {
            final Set<String> relevantFields = new HashSet<String>();
            for (final String relevantField : fields) {
                if (relevantField != null) {
                    relevantFields.add(relevantField);
                }
            }
            return relevantFields;
        }
        
        @Override
        public Set<BytesRef> contexts() {
            if (this.hasContexts) {
                return this.currentContexts;
            }
            return null;
        }
        
        @Override
        public boolean hasContexts() {
            return this.hasContexts;
        }
    }
}
