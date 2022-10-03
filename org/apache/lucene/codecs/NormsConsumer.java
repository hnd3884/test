package org.apache.lucene.codecs;

import java.util.NoSuchElementException;
import org.apache.lucene.util.Bits;
import java.util.List;
import java.util.Iterator;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.NumericDocValues;
import java.util.ArrayList;
import org.apache.lucene.index.MergeState;
import java.io.IOException;
import org.apache.lucene.index.FieldInfo;
import java.io.Closeable;

public abstract class NormsConsumer implements Closeable
{
    protected NormsConsumer() {
    }
    
    public abstract void addNormsField(final FieldInfo p0, final Iterable<Number> p1) throws IOException;
    
    public void merge(final MergeState mergeState) throws IOException {
        for (final NormsProducer normsProducer : mergeState.normsProducers) {
            if (normsProducer != null) {
                normsProducer.checkIntegrity();
            }
        }
        for (final FieldInfo mergeFieldInfo : mergeState.mergeFieldInfos) {
            if (mergeFieldInfo.hasNorms()) {
                final List<NumericDocValues> toMerge = new ArrayList<NumericDocValues>();
                for (int i = 0; i < mergeState.normsProducers.length; ++i) {
                    final NormsProducer normsProducer2 = mergeState.normsProducers[i];
                    NumericDocValues norms = null;
                    if (normsProducer2 != null) {
                        final FieldInfo fieldInfo = mergeState.fieldInfos[i].fieldInfo(mergeFieldInfo.name);
                        if (fieldInfo != null && fieldInfo.hasNorms()) {
                            norms = normsProducer2.getNorms(fieldInfo);
                        }
                    }
                    if (norms == null) {
                        norms = DocValues.emptyNumeric();
                    }
                    toMerge.add(norms);
                }
                this.mergeNormsField(mergeFieldInfo, mergeState, toMerge);
            }
        }
    }
    
    public void mergeNormsField(final FieldInfo fieldInfo, final MergeState mergeState, final List<NumericDocValues> toMerge) throws IOException {
        this.addNormsField(fieldInfo, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new Iterator<Number>() {
                    int readerUpto = -1;
                    int docIDUpto;
                    long nextValue;
                    int maxDoc;
                    NumericDocValues currentValues;
                    Bits currentLiveDocs;
                    boolean nextIsSet;
                    
                    @Override
                    public boolean hasNext() {
                        return this.nextIsSet || this.setNext();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public Number next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        assert this.nextIsSet;
                        this.nextIsSet = false;
                        return this.nextValue;
                    }
                    
                    private boolean setNext() {
                        while (this.readerUpto != toMerge.size()) {
                            if (this.currentValues == null || this.docIDUpto == this.maxDoc) {
                                ++this.readerUpto;
                                if (this.readerUpto < toMerge.size()) {
                                    this.currentValues = toMerge.get(this.readerUpto);
                                    this.currentLiveDocs = mergeState.liveDocs[this.readerUpto];
                                    this.maxDoc = mergeState.maxDocs[this.readerUpto];
                                }
                                this.docIDUpto = 0;
                            }
                            else {
                                if (this.currentLiveDocs == null || this.currentLiveDocs.get(this.docIDUpto)) {
                                    this.nextIsSet = true;
                                    this.nextValue = this.currentValues.get(this.docIDUpto);
                                    ++this.docIDUpto;
                                    return true;
                                }
                                ++this.docIDUpto;
                            }
                        }
                        return false;
                    }
                };
            }
        });
    }
}
