package org.apache.commons.compress.harmony.unpack200;

import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.ConstantPoolEntry;
import java.util.HashSet;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassConstantPool;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.io.IOException;
import org.apache.commons.compress.harmony.pack200.Codec;
import java.io.InputStream;
import java.util.Map;

public class IcBands extends BandSet
{
    private IcTuple[] icAll;
    private final String[] cpUTF8;
    private final String[] cpClass;
    private Map thisClassToTuple;
    private Map outerClassToTuples;
    
    public IcBands(final Segment segment) {
        super(segment);
        this.cpClass = segment.getCpBands().getCpClass();
        this.cpUTF8 = segment.getCpBands().getCpUTF8();
    }
    
    @Override
    public void read(final InputStream in) throws IOException, Pack200Exception {
        final int innerClassCount = this.header.getInnerClassCount();
        final int[] icThisClassInts = this.decodeBandInt("ic_this_class", in, Codec.UDELTA5, innerClassCount);
        final String[] icThisClass = this.getReferences(icThisClassInts, this.cpClass);
        final int[] icFlags = this.decodeBandInt("ic_flags", in, Codec.UNSIGNED5, innerClassCount);
        final int outerClasses = SegmentUtils.countBit16(icFlags);
        final int[] icOuterClassInts = this.decodeBandInt("ic_outer_class", in, Codec.DELTA5, outerClasses);
        final String[] icOuterClass = new String[outerClasses];
        for (int i = 0; i < icOuterClass.length; ++i) {
            if (icOuterClassInts[i] == 0) {
                icOuterClass[i] = null;
            }
            else {
                icOuterClass[i] = this.cpClass[icOuterClassInts[i] - 1];
            }
        }
        final int[] icNameInts = this.decodeBandInt("ic_name", in, Codec.DELTA5, outerClasses);
        final String[] icName = new String[outerClasses];
        for (int j = 0; j < icName.length; ++j) {
            if (icNameInts[j] == 0) {
                icName[j] = null;
            }
            else {
                icName[j] = this.cpUTF8[icNameInts[j] - 1];
            }
        }
        this.icAll = new IcTuple[icThisClass.length];
        int index = 0;
        for (int k = 0; k < icThisClass.length; ++k) {
            final String icTupleC = icThisClass[k];
            final int icTupleF = icFlags[k];
            String icTupleC2 = null;
            String icTupleN = null;
            final int cIndex = icThisClassInts[k];
            int c2Index = -1;
            int nIndex = -1;
            if ((icFlags[k] & 0x10000) != 0x0) {
                icTupleC2 = icOuterClass[index];
                icTupleN = icName[index];
                c2Index = icOuterClassInts[index] - 1;
                nIndex = icNameInts[index] - 1;
                ++index;
            }
            this.icAll[k] = new IcTuple(icTupleC, icTupleF, icTupleC2, icTupleN, cIndex, c2Index, nIndex, k);
        }
    }
    
    @Override
    public void unpack() throws IOException, Pack200Exception {
        final IcTuple[] allTuples = this.getIcTuples();
        this.thisClassToTuple = new HashMap(allTuples.length);
        this.outerClassToTuples = new HashMap(allTuples.length);
        for (int index = 0; index < allTuples.length; ++index) {
            final IcTuple tuple = allTuples[index];
            final Object result = this.thisClassToTuple.put(tuple.thisClassString(), tuple);
            if (result != null) {
                throw new Error("Collision detected in <thisClassString, IcTuple> mapping. There are at least two inner clases with the same name.");
            }
            if ((!tuple.isAnonymous() && !tuple.outerIsAnonymous()) || tuple.nestedExplicitFlagSet()) {
                final String key = tuple.outerClassString();
                List bucket = this.outerClassToTuples.get(key);
                if (bucket == null) {
                    bucket = new ArrayList();
                    this.outerClassToTuples.put(key, bucket);
                }
                bucket.add(tuple);
            }
        }
    }
    
    public IcTuple[] getIcTuples() {
        return this.icAll;
    }
    
    public IcTuple[] getRelevantIcTuples(final String className, final ClassConstantPool cp) {
        final Set relevantTuplesContains = new HashSet();
        final List relevantTuples = new ArrayList();
        final List relevantCandidates = this.outerClassToTuples.get(className);
        if (relevantCandidates != null) {
            for (int index = 0; index < relevantCandidates.size(); ++index) {
                final IcTuple tuple = relevantCandidates.get(index);
                relevantTuplesContains.add(tuple);
                relevantTuples.add(tuple);
            }
        }
        final List entries = cp.entries();
        for (int eIndex = 0; eIndex < entries.size(); ++eIndex) {
            final ConstantPoolEntry entry = entries.get(eIndex);
            if (entry instanceof CPClass) {
                final CPClass clazz = (CPClass)entry;
                final IcTuple relevant = this.thisClassToTuple.get(clazz.name);
                if (relevant != null && relevantTuplesContains.add(relevant)) {
                    relevantTuples.add(relevant);
                }
            }
        }
        final ArrayList tuplesToScan = new ArrayList(relevantTuples);
        final ArrayList tuplesToAdd = new ArrayList();
        while (tuplesToScan.size() > 0) {
            tuplesToAdd.clear();
            for (int index2 = 0; index2 < tuplesToScan.size(); ++index2) {
                final IcTuple aRelevantTuple = tuplesToScan.get(index2);
                final IcTuple relevant2 = this.thisClassToTuple.get(aRelevantTuple.outerClassString());
                if (relevant2 != null && !aRelevantTuple.outerIsAnonymous()) {
                    tuplesToAdd.add(relevant2);
                }
            }
            tuplesToScan.clear();
            for (int index2 = 0; index2 < tuplesToAdd.size(); ++index2) {
                final IcTuple tuple2 = tuplesToAdd.get(index2);
                if (relevantTuplesContains.add(tuple2)) {
                    relevantTuples.add(tuple2);
                    tuplesToScan.add(tuple2);
                }
            }
        }
        Collections.sort((List<Object>)relevantTuples, (arg0, arg1) -> {
            final Integer index3 = ((IcTuple)arg0).getTupleIndex();
            final Integer index4 = ((IcTuple)arg1).getTupleIndex();
            return index3.compareTo(index4);
        });
        final IcTuple[] relevantTuplesArray = new IcTuple[relevantTuples.size()];
        for (int i = 0; i < relevantTuplesArray.length; ++i) {
            relevantTuplesArray[i] = relevantTuples.get(i);
        }
        return relevantTuplesArray;
    }
}
