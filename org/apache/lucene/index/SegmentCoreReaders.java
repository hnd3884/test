package org.apache.lucene.index;

import java.util.Iterator;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.AlreadyClosedException;
import java.io.IOException;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.Codec;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.apache.lucene.store.IOContext;
import java.util.Set;
import org.apache.lucene.util.CloseableThreadLocal;
import org.apache.lucene.store.Directory;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.NormsProducer;
import org.apache.lucene.codecs.FieldsProducer;
import java.util.concurrent.atomic.AtomicInteger;

final class SegmentCoreReaders
{
    private final AtomicInteger ref;
    final FieldsProducer fields;
    final NormsProducer normsProducer;
    final StoredFieldsReader fieldsReaderOrig;
    final TermVectorsReader termVectorsReaderOrig;
    final Directory cfsReader;
    final FieldInfos coreFieldInfos;
    final CloseableThreadLocal<StoredFieldsReader> fieldsReaderLocal;
    final CloseableThreadLocal<TermVectorsReader> termVectorsLocal;
    private final Set<LeafReader.CoreClosedListener> coreClosedListeners;
    
    SegmentCoreReaders(final SegmentReader owner, final Directory dir, final SegmentCommitInfo si, final IOContext context) throws IOException {
        this.ref = new AtomicInteger(1);
        this.fieldsReaderLocal = new CloseableThreadLocal<StoredFieldsReader>() {
            @Override
            protected StoredFieldsReader initialValue() {
                return SegmentCoreReaders.this.fieldsReaderOrig.clone();
            }
        };
        this.termVectorsLocal = new CloseableThreadLocal<TermVectorsReader>() {
            @Override
            protected TermVectorsReader initialValue() {
                return (SegmentCoreReaders.this.termVectorsReaderOrig == null) ? null : SegmentCoreReaders.this.termVectorsReaderOrig.clone();
            }
        };
        this.coreClosedListeners = Collections.synchronizedSet(new LinkedHashSet<LeafReader.CoreClosedListener>());
        final Codec codec = si.info.getCodec();
        boolean success = false;
        try {
            Directory cfsDir;
            if (si.info.getUseCompoundFile()) {
                final Directory compoundReader = codec.compoundFormat().getCompoundReader(dir, si.info, context);
                this.cfsReader = compoundReader;
                cfsDir = compoundReader;
            }
            else {
                this.cfsReader = null;
                cfsDir = dir;
            }
            this.coreFieldInfos = codec.fieldInfosFormat().read(cfsDir, si.info, "", context);
            final SegmentReadState segmentReadState = new SegmentReadState(cfsDir, si.info, this.coreFieldInfos, context);
            final PostingsFormat format = codec.postingsFormat();
            this.fields = format.fieldsProducer(segmentReadState);
            assert this.fields != null;
            if (this.coreFieldInfos.hasNorms()) {
                this.normsProducer = codec.normsFormat().normsProducer(segmentReadState);
                assert this.normsProducer != null;
            }
            else {
                this.normsProducer = null;
            }
            this.fieldsReaderOrig = si.info.getCodec().storedFieldsFormat().fieldsReader(cfsDir, si.info, this.coreFieldInfos, context);
            if (this.coreFieldInfos.hasVectors()) {
                this.termVectorsReaderOrig = si.info.getCodec().termVectorsFormat().vectorsReader(cfsDir, si.info, this.coreFieldInfos, context);
            }
            else {
                this.termVectorsReaderOrig = null;
            }
            success = true;
        }
        finally {
            if (!success) {
                this.decRef();
            }
        }
    }
    
    int getRefCount() {
        return this.ref.get();
    }
    
    void incRef() {
        int count;
        while ((count = this.ref.get()) > 0) {
            if (this.ref.compareAndSet(count, count + 1)) {
                return;
            }
        }
        throw new AlreadyClosedException("SegmentCoreReaders is already closed");
    }
    
    void decRef() throws IOException {
        if (this.ref.decrementAndGet() == 0) {
            Throwable th = null;
            try {
                IOUtils.close(this.termVectorsLocal, this.fieldsReaderLocal, this.fields, this.termVectorsReaderOrig, this.fieldsReaderOrig, this.cfsReader, this.normsProducer);
            }
            catch (final Throwable throwable) {
                th = throwable;
            }
            finally {
                this.notifyCoreClosedListeners(th);
            }
        }
    }
    
    private void notifyCoreClosedListeners(Throwable th) {
        synchronized (this.coreClosedListeners) {
            for (final LeafReader.CoreClosedListener listener : this.coreClosedListeners) {
                try {
                    listener.onClose(this);
                }
                catch (final Throwable t) {
                    if (th == null) {
                        th = t;
                    }
                    else {
                        th.addSuppressed(t);
                    }
                }
            }
            IOUtils.reThrowUnchecked(th);
        }
    }
    
    void addCoreClosedListener(final LeafReader.CoreClosedListener listener) {
        this.coreClosedListeners.add(listener);
    }
    
    void removeCoreClosedListener(final LeafReader.CoreClosedListener listener) {
        this.coreClosedListeners.remove(listener);
    }
}
