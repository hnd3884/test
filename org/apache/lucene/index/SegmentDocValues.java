package org.apache.lucene.index;

import java.util.Iterator;
import org.apache.lucene.util.IOUtils;
import java.util.List;
import org.apache.lucene.codecs.DocValuesFormat;
import java.io.IOException;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.Directory;
import java.util.HashMap;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.util.RefCount;
import java.util.Map;

final class SegmentDocValues
{
    private final Map<Long, RefCount<DocValuesProducer>> genDVProducers;
    
    SegmentDocValues() {
        this.genDVProducers = new HashMap<Long, RefCount<DocValuesProducer>>();
    }
    
    private RefCount<DocValuesProducer> newDocValuesProducer(final SegmentCommitInfo si, final Directory dir, final Long gen, final FieldInfos infos) throws IOException {
        Directory dvDir = dir;
        String segmentSuffix = "";
        if (gen != -1L) {
            dvDir = si.info.dir;
            segmentSuffix = Long.toString(gen, 36);
        }
        final SegmentReadState srs = new SegmentReadState(dvDir, si.info, infos, IOContext.READ, segmentSuffix);
        final DocValuesFormat dvFormat = si.info.getCodec().docValuesFormat();
        return new RefCount<DocValuesProducer>(dvFormat.fieldsProducer(srs)) {
            @Override
            protected void release() throws IOException {
                ((DocValuesProducer)this.object).close();
                synchronized (SegmentDocValues.this) {
                    SegmentDocValues.this.genDVProducers.remove(gen);
                }
            }
        };
    }
    
    synchronized DocValuesProducer getDocValuesProducer(final long gen, final SegmentCommitInfo si, final Directory dir, final FieldInfos infos) throws IOException {
        RefCount<DocValuesProducer> dvp = this.genDVProducers.get(gen);
        if (dvp == null) {
            dvp = this.newDocValuesProducer(si, dir, gen, infos);
            assert dvp != null;
            this.genDVProducers.put(gen, dvp);
        }
        else {
            dvp.incRef();
        }
        return dvp.get();
    }
    
    synchronized void decRef(final List<Long> dvProducersGens) throws IOException {
        Throwable t = null;
        for (final Long gen : dvProducersGens) {
            final RefCount<DocValuesProducer> dvp = this.genDVProducers.get(gen);
            assert dvp != null : "gen=" + gen;
            try {
                dvp.decRef();
            }
            catch (final Throwable th) {
                if (t == null) {
                    continue;
                }
                t = th;
            }
        }
        if (t != null) {
            IOUtils.reThrow(t);
        }
    }
}
