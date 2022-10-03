package org.apache.lucene.index;

import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.NormsProducer;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.TermVectorsReader;
import java.util.Collections;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.store.Directory;
import java.io.IOException;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.util.Bits;

public final class SegmentReader extends CodecReader
{
    private final SegmentCommitInfo si;
    private final Bits liveDocs;
    private final int numDocs;
    final SegmentCoreReaders core;
    final SegmentDocValues segDocValues;
    final DocValuesProducer docValuesProducer;
    final FieldInfos fieldInfos;
    
    public SegmentReader(final SegmentCommitInfo si, final IOContext context) throws IOException {
        this.si = si;
        this.core = new SegmentCoreReaders(this, si.info.dir, si, context);
        this.segDocValues = new SegmentDocValues();
        boolean success = false;
        final Codec codec = si.info.getCodec();
        try {
            if (si.hasDeletions()) {
                this.liveDocs = codec.liveDocsFormat().readLiveDocs(this.directory(), si, IOContext.READONCE);
            }
            else {
                assert si.getDelCount() == 0;
                this.liveDocs = null;
            }
            this.numDocs = si.info.maxDoc() - si.getDelCount();
            this.fieldInfos = this.initFieldInfos();
            this.docValuesProducer = this.initDocValuesProducer();
            success = true;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }
    
    SegmentReader(final SegmentCommitInfo si, final SegmentReader sr) throws IOException {
        this(si, sr, si.info.getCodec().liveDocsFormat().readLiveDocs(si.info.dir, si, IOContext.READONCE), si.info.maxDoc() - si.getDelCount());
    }
    
    SegmentReader(final SegmentCommitInfo si, final SegmentReader sr, final Bits liveDocs, final int numDocs) throws IOException {
        if (numDocs > si.info.maxDoc()) {
            throw new IllegalArgumentException("numDocs=" + numDocs + " but maxDoc=" + si.info.maxDoc());
        }
        if (liveDocs != null && liveDocs.length() != si.info.maxDoc()) {
            throw new IllegalArgumentException("maxDoc=" + si.info.maxDoc() + " but liveDocs.size()=" + liveDocs.length());
        }
        this.si = si;
        this.liveDocs = liveDocs;
        this.numDocs = numDocs;
        (this.core = sr.core).incRef();
        this.segDocValues = sr.segDocValues;
        boolean success = false;
        try {
            this.fieldInfos = this.initFieldInfos();
            this.docValuesProducer = this.initDocValuesProducer();
            success = true;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }
    
    private DocValuesProducer initDocValuesProducer() throws IOException {
        final Directory dir = (this.core.cfsReader != null) ? this.core.cfsReader : this.si.info.dir;
        if (!this.fieldInfos.hasDocValues()) {
            return null;
        }
        if (this.si.hasFieldUpdates()) {
            return new SegmentDocValuesProducer(this.si, dir, this.core.coreFieldInfos, this.fieldInfos, this.segDocValues);
        }
        return this.segDocValues.getDocValuesProducer(-1L, this.si, dir, this.fieldInfos);
    }
    
    private FieldInfos initFieldInfos() throws IOException {
        if (!this.si.hasFieldUpdates()) {
            return this.core.coreFieldInfos;
        }
        final FieldInfosFormat fisFormat = this.si.info.getCodec().fieldInfosFormat();
        final String segmentSuffix = Long.toString(this.si.getFieldInfosGen(), 36);
        return fisFormat.read(this.si.info.dir, this.si.info, segmentSuffix, IOContext.READONCE);
    }
    
    @Override
    public Bits getLiveDocs() {
        this.ensureOpen();
        return this.liveDocs;
    }
    
    @Override
    protected void doClose() throws IOException {
        try {
            this.core.decRef();
        }
        finally {
            try {
                super.doClose();
            }
            finally {
                if (this.docValuesProducer instanceof SegmentDocValuesProducer) {
                    this.segDocValues.decRef(((SegmentDocValuesProducer)this.docValuesProducer).dvGens);
                }
                else if (this.docValuesProducer != null) {
                    this.segDocValues.decRef(Collections.singletonList(-1L));
                }
            }
        }
    }
    
    @Override
    public FieldInfos getFieldInfos() {
        this.ensureOpen();
        return this.fieldInfos;
    }
    
    @Override
    public int numDocs() {
        return this.numDocs;
    }
    
    @Override
    public int maxDoc() {
        return this.si.info.maxDoc();
    }
    
    @Override
    public TermVectorsReader getTermVectorsReader() {
        this.ensureOpen();
        return this.core.termVectorsLocal.get();
    }
    
    @Override
    public StoredFieldsReader getFieldsReader() {
        this.ensureOpen();
        return this.core.fieldsReaderLocal.get();
    }
    
    @Override
    public NormsProducer getNormsReader() {
        this.ensureOpen();
        return this.core.normsProducer;
    }
    
    @Override
    public DocValuesProducer getDocValuesReader() {
        this.ensureOpen();
        return this.docValuesProducer;
    }
    
    @Override
    public FieldsProducer getPostingsReader() {
        this.ensureOpen();
        return this.core.fields;
    }
    
    @Override
    public String toString() {
        return this.si.toString(this.si.info.maxDoc() - this.numDocs - this.si.getDelCount());
    }
    
    public String getSegmentName() {
        return this.si.info.name;
    }
    
    public SegmentCommitInfo getSegmentInfo() {
        return this.si;
    }
    
    public Directory directory() {
        return this.si.info.dir;
    }
    
    @Override
    public Object getCoreCacheKey() {
        return this.core;
    }
    
    @Override
    public Object getCombinedCoreAndDeletesKey() {
        return this;
    }
    
    @Override
    public void addCoreClosedListener(final CoreClosedListener listener) {
        this.ensureOpen();
        this.core.addCoreClosedListener(listener);
    }
    
    @Override
    public void removeCoreClosedListener(final CoreClosedListener listener) {
        this.ensureOpen();
        this.core.removeCoreClosedListener(listener);
    }
}
