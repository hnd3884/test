package org.apache.lucene.codecs;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import java.util.Iterator;
import org.apache.lucene.index.Fields;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.FieldInfo;
import java.io.IOException;
import java.io.Closeable;

public abstract class TermVectorsWriter implements Closeable
{
    protected TermVectorsWriter() {
    }
    
    public abstract void startDocument(final int p0) throws IOException;
    
    public void finishDocument() throws IOException {
    }
    
    public abstract void startField(final FieldInfo p0, final int p1, final boolean p2, final boolean p3, final boolean p4) throws IOException;
    
    public void finishField() throws IOException {
    }
    
    public abstract void startTerm(final BytesRef p0, final int p1) throws IOException;
    
    public void finishTerm() throws IOException {
    }
    
    public abstract void addPosition(final int p0, final int p1, final int p2, final BytesRef p3) throws IOException;
    
    public abstract void finish(final FieldInfos p0, final int p1) throws IOException;
    
    public void addProx(final int numProx, final DataInput positions, final DataInput offsets) throws IOException {
        int position = 0;
        int lastOffset = 0;
        BytesRefBuilder payload = null;
        for (int i = 0; i < numProx; ++i) {
            BytesRef thisPayload;
            if (positions == null) {
                position = -1;
                thisPayload = null;
            }
            else {
                final int code = positions.readVInt();
                position += code >>> 1;
                if ((code & 0x1) != 0x0) {
                    final int payloadLength = positions.readVInt();
                    if (payload == null) {
                        payload = new BytesRefBuilder();
                    }
                    payload.grow(payloadLength);
                    positions.readBytes(payload.bytes(), 0, payloadLength);
                    payload.setLength(payloadLength);
                    thisPayload = payload.get();
                }
                else {
                    thisPayload = null;
                }
            }
            int startOffset;
            int endOffset;
            if (offsets == null) {
                endOffset = (startOffset = -1);
            }
            else {
                startOffset = lastOffset + offsets.readVInt();
                endOffset = (lastOffset = startOffset + offsets.readVInt());
            }
            this.addPosition(position, startOffset, endOffset, thisPayload);
        }
    }
    
    public int merge(final MergeState mergeState) throws IOException {
        int docCount = 0;
        for (int numReaders = mergeState.maxDocs.length, i = 0; i < numReaders; ++i) {
            final int maxDoc = mergeState.maxDocs[i];
            final Bits liveDocs = mergeState.liveDocs[i];
            final TermVectorsReader termVectorsReader = mergeState.termVectorsReaders[i];
            if (termVectorsReader != null) {
                termVectorsReader.checkIntegrity();
            }
            for (int docID = 0; docID < maxDoc; ++docID) {
                if (liveDocs == null || liveDocs.get(docID)) {
                    Fields vectors;
                    if (termVectorsReader == null) {
                        vectors = null;
                    }
                    else {
                        vectors = termVectorsReader.get(docID);
                    }
                    this.addAllDocVectors(vectors, mergeState);
                    ++docCount;
                }
            }
        }
        this.finish(mergeState.mergeFieldInfos, docCount);
        return docCount;
    }
    
    protected final void addAllDocVectors(final Fields vectors, final MergeState mergeState) throws IOException {
        if (vectors == null) {
            this.startDocument(0);
            this.finishDocument();
            return;
        }
        int numFields = vectors.size();
        if (numFields == -1) {
            numFields = 0;
            final Iterator<String> it = vectors.iterator();
            while (it.hasNext()) {
                it.next();
                ++numFields;
            }
        }
        this.startDocument(numFields);
        String lastFieldName = null;
        TermsEnum termsEnum = null;
        PostingsEnum docsAndPositionsEnum = null;
        int fieldCount = 0;
        for (final String fieldName : vectors) {
            ++fieldCount;
            final FieldInfo fieldInfo = mergeState.mergeFieldInfos.fieldInfo(fieldName);
            assert fieldName.compareTo(lastFieldName) > 0 : "lastFieldName=" + lastFieldName + " fieldName=" + fieldName;
            lastFieldName = fieldName;
            final Terms terms = vectors.terms(fieldName);
            if (terms == null) {
                continue;
            }
            final boolean hasPositions = terms.hasPositions();
            final boolean hasOffsets = terms.hasOffsets();
            final boolean hasPayloads = terms.hasPayloads();
            assert !(!hasPositions);
            int numTerms = (int)terms.size();
            if (numTerms == -1) {
                numTerms = 0;
                termsEnum = terms.iterator();
                while (termsEnum.next() != null) {
                    ++numTerms;
                }
            }
            this.startField(fieldInfo, numTerms, hasPositions, hasOffsets, hasPayloads);
            termsEnum = terms.iterator();
            int termCount = 0;
            while (termsEnum.next() != null) {
                ++termCount;
                final int freq = (int)termsEnum.totalTermFreq();
                this.startTerm(termsEnum.term(), freq);
                if (hasPositions || hasOffsets) {
                    docsAndPositionsEnum = termsEnum.postings(docsAndPositionsEnum, 120);
                    assert docsAndPositionsEnum != null;
                    final int docID = docsAndPositionsEnum.nextDoc();
                    assert docID != Integer.MAX_VALUE;
                    assert docsAndPositionsEnum.freq() == freq;
                    for (int posUpto = 0; posUpto < freq; ++posUpto) {
                        final int pos = docsAndPositionsEnum.nextPosition();
                        final int startOffset = docsAndPositionsEnum.startOffset();
                        final int endOffset = docsAndPositionsEnum.endOffset();
                        final BytesRef payload = docsAndPositionsEnum.getPayload();
                        assert pos >= 0;
                        this.addPosition(pos, startOffset, endOffset, payload);
                    }
                }
                this.finishTerm();
            }
            assert termCount == numTerms;
            this.finishField();
        }
        assert fieldCount == numFields;
        this.finishDocument();
    }
    
    @Override
    public abstract void close() throws IOException;
}
