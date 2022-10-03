package org.apache.commons.compress.harmony.pack200;

import java.util.Iterator;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MetadataBandGroup extends BandSet
{
    public static final int CONTEXT_CLASS = 0;
    public static final int CONTEXT_FIELD = 1;
    public static final int CONTEXT_METHOD = 2;
    private final String type;
    private int numBackwardsCalls;
    public IntList param_NB;
    public IntList anno_N;
    public List type_RS;
    public IntList pair_N;
    public List name_RU;
    public List T;
    public List caseI_KI;
    public List caseD_KD;
    public List caseF_KF;
    public List caseJ_KJ;
    public List casec_RS;
    public List caseet_RS;
    public List caseec_RU;
    public List cases_RU;
    public IntList casearray_N;
    public List nesttype_RS;
    public IntList nestpair_N;
    public List nestname_RU;
    private final CpBands cpBands;
    private final int context;
    
    public MetadataBandGroup(final String type, final int context, final CpBands cpBands, final SegmentHeader segmentHeader, final int effort) {
        super(effort, segmentHeader);
        this.numBackwardsCalls = 0;
        this.param_NB = new IntList();
        this.anno_N = new IntList();
        this.type_RS = new ArrayList();
        this.pair_N = new IntList();
        this.name_RU = new ArrayList();
        this.T = new ArrayList();
        this.caseI_KI = new ArrayList();
        this.caseD_KD = new ArrayList();
        this.caseF_KF = new ArrayList();
        this.caseJ_KJ = new ArrayList();
        this.casec_RS = new ArrayList();
        this.caseet_RS = new ArrayList();
        this.caseec_RU = new ArrayList();
        this.cases_RU = new ArrayList();
        this.casearray_N = new IntList();
        this.nesttype_RS = new ArrayList();
        this.nestpair_N = new IntList();
        this.nestname_RU = new ArrayList();
        this.type = type;
        this.cpBands = cpBands;
        this.context = context;
    }
    
    @Override
    public void pack(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing metadata band group...");
        if (this.hasContent()) {
            String contextStr;
            if (this.context == 0) {
                contextStr = "Class";
            }
            else if (this.context == 1) {
                contextStr = "Field";
            }
            else {
                contextStr = "Method";
            }
            byte[] encodedBand = null;
            if (!this.type.equals("AD")) {
                if (this.type.indexOf(80) != -1) {
                    encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " param_NB", this.param_NB.toArray(), Codec.BYTE1);
                    out.write(encodedBand);
                    PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " anno_N[" + this.param_NB.size() + "]");
                }
                encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " anno_N", this.anno_N.toArray(), Codec.UNSIGNED5);
                out.write(encodedBand);
                PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " anno_N[" + this.anno_N.size() + "]");
                encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " type_RS", this.cpEntryListToArray(this.type_RS), Codec.UNSIGNED5);
                out.write(encodedBand);
                PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " type_RS[" + this.type_RS.size() + "]");
                encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " pair_N", this.pair_N.toArray(), Codec.UNSIGNED5);
                out.write(encodedBand);
                PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " pair_N[" + this.pair_N.size() + "]");
                encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " name_RU", this.cpEntryListToArray(this.name_RU), Codec.UNSIGNED5);
                out.write(encodedBand);
                PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " name_RU[" + this.name_RU.size() + "]");
            }
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " T", this.tagListToArray(this.T), Codec.BYTE1);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " T[" + this.T.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " caseI_KI", this.cpEntryListToArray(this.caseI_KI), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseI_KI[" + this.caseI_KI.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " caseD_KD", this.cpEntryListToArray(this.caseD_KD), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseD_KD[" + this.caseD_KD.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " caseF_KF", this.cpEntryListToArray(this.caseF_KF), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseF_KF[" + this.caseF_KF.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " caseJ_KJ", this.cpEntryListToArray(this.caseJ_KJ), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseJ_KJ[" + this.caseJ_KJ.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " casec_RS", this.cpEntryListToArray(this.casec_RS), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " casec_RS[" + this.casec_RS.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " caseet_RS", this.cpEntryListToArray(this.caseet_RS), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseet_RS[" + this.caseet_RS.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " caseec_RU", this.cpEntryListToArray(this.caseec_RU), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseec_RU[" + this.caseec_RU.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " cases_RU", this.cpEntryListToArray(this.cases_RU), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " cases_RU[" + this.cases_RU.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " casearray_N", this.casearray_N.toArray(), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " casearray_N[" + this.casearray_N.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " nesttype_RS", this.cpEntryListToArray(this.nesttype_RS), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " nesttype_RS[" + this.nesttype_RS.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " nestpair_N", this.nestpair_N.toArray(), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " nestpair_N[" + this.nestpair_N.size() + "]");
            encodedBand = this.encodeBandInt(contextStr + "_" + this.type + " nestname_RU", this.cpEntryListToArray(this.nestname_RU), Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " nestname_RU[" + this.nestname_RU.size() + "]");
        }
    }
    
    private int[] tagListToArray(final List t2) {
        final int[] ints = new int[t2.size()];
        for (int i = 0; i < ints.length; ++i) {
            ints[i] = t2.get(i).charAt(0);
        }
        return ints;
    }
    
    public void addParameterAnnotation(final int numParams, final int[] annoN, final IntList pairN, final List typeRS, final List nameRU, final List t, final List values, final List caseArrayN, final List nestTypeRS, final List nestNameRU, final List nestPairN) {
        this.param_NB.add(numParams);
        for (int i = 0; i < annoN.length; ++i) {
            this.anno_N.add(annoN[i]);
        }
        this.pair_N.addAll(pairN);
        for (final String desc : typeRS) {
            this.type_RS.add(this.cpBands.getCPSignature(desc));
        }
        for (final String name : nameRU) {
            this.name_RU.add(this.cpBands.getCPUtf8(name));
        }
        final Iterator valuesIterator = values.iterator();
        for (final String tag : t) {
            this.T.add(tag);
            if (tag.equals("B") || tag.equals("C") || tag.equals("I") || tag.equals("S") || tag.equals("Z")) {
                final Integer value = valuesIterator.next();
                this.caseI_KI.add(this.cpBands.getConstant(value));
            }
            else if (tag.equals("D")) {
                final Double value2 = valuesIterator.next();
                this.caseD_KD.add(this.cpBands.getConstant(value2));
            }
            else if (tag.equals("F")) {
                final Float value3 = valuesIterator.next();
                this.caseF_KF.add(this.cpBands.getConstant(value3));
            }
            else if (tag.equals("J")) {
                final Long value4 = valuesIterator.next();
                this.caseJ_KJ.add(this.cpBands.getConstant(value4));
            }
            else if (tag.equals("c")) {
                final String value5 = valuesIterator.next();
                this.casec_RS.add(this.cpBands.getCPSignature(value5));
            }
            else if (tag.equals("e")) {
                final String value5 = valuesIterator.next();
                final String value6 = valuesIterator.next();
                this.caseet_RS.add(this.cpBands.getCPSignature(value5));
                this.caseec_RU.add(this.cpBands.getCPUtf8(value6));
            }
            else {
                if (!tag.equals("s")) {
                    continue;
                }
                final String value5 = valuesIterator.next();
                this.cases_RU.add(this.cpBands.getCPUtf8(value5));
            }
        }
        for (final int arraySize : caseArrayN) {
            this.casearray_N.add(arraySize);
            this.numBackwardsCalls += arraySize;
        }
        for (final String type : nestTypeRS) {
            this.nesttype_RS.add(this.cpBands.getCPSignature(type));
        }
        for (final String name2 : nestNameRU) {
            this.nestname_RU.add(this.cpBands.getCPUtf8(name2));
        }
        for (final Integer numPairs : nestPairN) {
            this.nestpair_N.add(numPairs);
            this.numBackwardsCalls += numPairs;
        }
    }
    
    public void addAnnotation(final String desc, final List nameRU, final List t, final List values, final List caseArrayN, final List nestTypeRS, final List nestNameRU, final List nestPairN) {
        this.type_RS.add(this.cpBands.getCPSignature(desc));
        this.pair_N.add(nameRU.size());
        for (final String name : nameRU) {
            this.name_RU.add(this.cpBands.getCPUtf8(name));
        }
        final Iterator valuesIterator = values.iterator();
        for (final String tag : t) {
            this.T.add(tag);
            if (tag.equals("B") || tag.equals("C") || tag.equals("I") || tag.equals("S") || tag.equals("Z")) {
                final Integer value = valuesIterator.next();
                this.caseI_KI.add(this.cpBands.getConstant(value));
            }
            else if (tag.equals("D")) {
                final Double value2 = valuesIterator.next();
                this.caseD_KD.add(this.cpBands.getConstant(value2));
            }
            else if (tag.equals("F")) {
                final Float value3 = valuesIterator.next();
                this.caseF_KF.add(this.cpBands.getConstant(value3));
            }
            else if (tag.equals("J")) {
                final Long value4 = valuesIterator.next();
                this.caseJ_KJ.add(this.cpBands.getConstant(value4));
            }
            else if (tag.equals("c")) {
                final String value5 = valuesIterator.next();
                this.casec_RS.add(this.cpBands.getCPSignature(value5));
            }
            else if (tag.equals("e")) {
                final String value5 = valuesIterator.next();
                final String value6 = valuesIterator.next();
                this.caseet_RS.add(this.cpBands.getCPSignature(value5));
                this.caseec_RU.add(this.cpBands.getCPUtf8(value6));
            }
            else {
                if (!tag.equals("s")) {
                    continue;
                }
                final String value5 = valuesIterator.next();
                this.cases_RU.add(this.cpBands.getCPUtf8(value5));
            }
        }
        for (final int arraySize : caseArrayN) {
            this.casearray_N.add(arraySize);
            this.numBackwardsCalls += arraySize;
        }
        for (final String type : nestTypeRS) {
            this.nesttype_RS.add(this.cpBands.getCPSignature(type));
        }
        for (final String name2 : nestNameRU) {
            this.nestname_RU.add(this.cpBands.getCPUtf8(name2));
        }
        for (final Integer numPairs : nestPairN) {
            this.nestpair_N.add(numPairs);
            this.numBackwardsCalls += numPairs;
        }
    }
    
    public boolean hasContent() {
        return this.type_RS.size() > 0;
    }
    
    public int numBackwardsCalls() {
        return this.numBackwardsCalls;
    }
    
    public void incrementAnnoN() {
        this.anno_N.increment(this.anno_N.size() - 1);
    }
    
    public void newEntryInAnnoN() {
        this.anno_N.add(1);
    }
    
    public void removeLatest() {
        for (int latest = this.anno_N.remove(this.anno_N.size() - 1), i = 0; i < latest; ++i) {
            this.type_RS.remove(this.type_RS.size() - 1);
            for (int pairs = this.pair_N.remove(this.pair_N.size() - 1), j = 0; j < pairs; ++j) {
                this.removeOnePair();
            }
        }
    }
    
    private void removeOnePair() {
        final String tag = this.T.remove(this.T.size() - 1);
        if (tag.equals("B") || tag.equals("C") || tag.equals("I") || tag.equals("S") || tag.equals("Z")) {
            this.caseI_KI.remove(this.caseI_KI.size() - 1);
        }
        else if (tag.equals("D")) {
            this.caseD_KD.remove(this.caseD_KD.size() - 1);
        }
        else if (tag.equals("F")) {
            this.caseF_KF.remove(this.caseF_KF.size() - 1);
        }
        else if (tag.equals("J")) {
            this.caseJ_KJ.remove(this.caseJ_KJ.size() - 1);
        }
        else if (tag.equals("C")) {
            this.casec_RS.remove(this.casec_RS.size() - 1);
        }
        else if (tag.equals("e")) {
            this.caseet_RS.remove(this.caseet_RS.size() - 1);
            this.caseec_RU.remove(this.caseet_RS.size() - 1);
        }
        else if (tag.equals("s")) {
            this.cases_RU.remove(this.cases_RU.size() - 1);
        }
        else if (tag.equals("[")) {
            final int arraySize = this.casearray_N.remove(this.casearray_N.size() - 1);
            this.numBackwardsCalls -= arraySize;
            for (int k = 0; k < arraySize; ++k) {
                this.removeOnePair();
            }
        }
        else if (tag.equals("@")) {
            this.nesttype_RS.remove(this.nesttype_RS.size() - 1);
            final int numPairs = this.nestpair_N.remove(this.nestpair_N.size() - 1);
            this.numBackwardsCalls -= numPairs;
            for (int i = 0; i < numPairs; ++i) {
                this.removeOnePair();
            }
        }
    }
}
