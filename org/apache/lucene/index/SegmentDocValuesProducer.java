package org.apache.lucene.index;

import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.util.Version;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.HashMap;
import org.apache.lucene.store.Directory;
import java.util.List;
import java.util.Set;
import java.util.Map;
import org.apache.lucene.codecs.DocValuesProducer;

class SegmentDocValuesProducer extends DocValuesProducer
{
    private static final long LONG_RAM_BYTES_USED;
    private static final long BASE_RAM_BYTES_USED;
    final Map<String, DocValuesProducer> dvProducersByField;
    final Set<DocValuesProducer> dvProducers;
    final List<Long> dvGens;
    
    SegmentDocValuesProducer(final SegmentCommitInfo si, final Directory dir, final FieldInfos coreInfos, final FieldInfos allInfos, final SegmentDocValues segDocValues) throws IOException {
        this.dvProducersByField = new HashMap<String, DocValuesProducer>();
        this.dvProducers = Collections.newSetFromMap(new IdentityHashMap<DocValuesProducer, Boolean>());
        this.dvGens = new ArrayList<Long>();
        boolean success = false;
        try {
            final Version ver = si.info.getVersion();
            if (ver != null && ver.onOrAfter(Version.LUCENE_4_9_0)) {
                DocValuesProducer baseProducer = null;
                for (final FieldInfo fi : allInfos) {
                    if (fi.getDocValuesType() == DocValuesType.NONE) {
                        continue;
                    }
                    final long docValuesGen = fi.getDocValuesGen();
                    if (docValuesGen == -1L) {
                        if (baseProducer == null) {
                            baseProducer = segDocValues.getDocValuesProducer(docValuesGen, si, dir, coreInfos);
                            this.dvGens.add(docValuesGen);
                            this.dvProducers.add(baseProducer);
                        }
                        this.dvProducersByField.put(fi.name, baseProducer);
                    }
                    else {
                        assert !this.dvGens.contains(docValuesGen);
                        final DocValuesProducer dvp = segDocValues.getDocValuesProducer(docValuesGen, si, dir, new FieldInfos(new FieldInfo[] { fi }));
                        this.dvGens.add(docValuesGen);
                        this.dvProducers.add(dvp);
                        this.dvProducersByField.put(fi.name, dvp);
                    }
                }
            }
            else {
                final Map<Long, List<FieldInfo>> genInfos = new HashMap<Long, List<FieldInfo>>();
                for (final FieldInfo fi : allInfos) {
                    if (fi.getDocValuesType() == DocValuesType.NONE) {
                        continue;
                    }
                    List<FieldInfo> genFieldInfos = genInfos.get(fi.getDocValuesGen());
                    if (genFieldInfos == null) {
                        genFieldInfos = new ArrayList<FieldInfo>();
                        genInfos.put(fi.getDocValuesGen(), genFieldInfos);
                    }
                    genFieldInfos.add(fi);
                }
                for (final Map.Entry<Long, List<FieldInfo>> e : genInfos.entrySet()) {
                    final long docValuesGen = e.getKey();
                    final List<FieldInfo> infos = e.getValue();
                    DocValuesProducer dvp2;
                    if (docValuesGen == -1L) {
                        dvp2 = segDocValues.getDocValuesProducer(docValuesGen, si, dir, coreInfos);
                    }
                    else {
                        dvp2 = segDocValues.getDocValuesProducer(docValuesGen, si, dir, new FieldInfos(infos.toArray(new FieldInfo[infos.size()])));
                    }
                    this.dvGens.add(docValuesGen);
                    this.dvProducers.add(dvp2);
                    for (final FieldInfo fi2 : infos) {
                        this.dvProducersByField.put(fi2.name, dvp2);
                    }
                }
            }
            success = true;
        }
        finally {
            if (!success) {
                try {
                    segDocValues.decRef(this.dvGens);
                }
                catch (final Throwable t) {}
            }
        }
    }
    
    @Override
    public NumericDocValues getNumeric(final FieldInfo field) throws IOException {
        final DocValuesProducer dvProducer = this.dvProducersByField.get(field.name);
        assert dvProducer != null;
        return dvProducer.getNumeric(field);
    }
    
    @Override
    public BinaryDocValues getBinary(final FieldInfo field) throws IOException {
        final DocValuesProducer dvProducer = this.dvProducersByField.get(field.name);
        assert dvProducer != null;
        return dvProducer.getBinary(field);
    }
    
    @Override
    public SortedDocValues getSorted(final FieldInfo field) throws IOException {
        final DocValuesProducer dvProducer = this.dvProducersByField.get(field.name);
        assert dvProducer != null;
        return dvProducer.getSorted(field);
    }
    
    @Override
    public SortedNumericDocValues getSortedNumeric(final FieldInfo field) throws IOException {
        final DocValuesProducer dvProducer = this.dvProducersByField.get(field.name);
        assert dvProducer != null;
        return dvProducer.getSortedNumeric(field);
    }
    
    @Override
    public SortedSetDocValues getSortedSet(final FieldInfo field) throws IOException {
        final DocValuesProducer dvProducer = this.dvProducersByField.get(field.name);
        assert dvProducer != null;
        return dvProducer.getSortedSet(field);
    }
    
    @Override
    public Bits getDocsWithField(final FieldInfo field) throws IOException {
        final DocValuesProducer dvProducer = this.dvProducersByField.get(field.name);
        assert dvProducer != null;
        return dvProducer.getDocsWithField(field);
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        for (final DocValuesProducer producer : this.dvProducers) {
            producer.checkIntegrity();
        }
    }
    
    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long ramBytesUsed() {
        long ramBytesUsed = SegmentDocValuesProducer.BASE_RAM_BYTES_USED;
        ramBytesUsed += this.dvGens.size() * SegmentDocValuesProducer.LONG_RAM_BYTES_USED;
        ramBytesUsed += this.dvProducers.size() * RamUsageEstimator.NUM_BYTES_OBJECT_REF;
        ramBytesUsed += this.dvProducersByField.size() * 2 * RamUsageEstimator.NUM_BYTES_OBJECT_REF;
        for (final DocValuesProducer producer : this.dvProducers) {
            ramBytesUsed += producer.ramBytesUsed();
        }
        return ramBytesUsed;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        final List<Accountable> resources = new ArrayList<Accountable>(this.dvProducers.size());
        for (final Accountable producer : this.dvProducers) {
            resources.add(Accountables.namedAccountable("delegate", producer));
        }
        return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(producers=" + this.dvProducers.size() + ")";
    }
    
    static {
        LONG_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(Long.class);
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(SegmentDocValuesProducer.class);
    }
}
