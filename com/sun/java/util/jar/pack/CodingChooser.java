package com.sun.java.util.jar.pack;

import java.util.List;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.util.Random;
import java.io.ByteArrayOutputStream;

class CodingChooser
{
    int verbose;
    int effort;
    boolean optUseHistogram;
    boolean optUsePopulationCoding;
    boolean optUseAdaptiveCoding;
    boolean disablePopCoding;
    boolean disableRunCoding;
    boolean topLevel;
    double fuzz;
    Coding[] allCodingChoices;
    Choice[] choices;
    ByteArrayOutputStream context;
    CodingChooser popHelper;
    CodingChooser runHelper;
    Random stress;
    private int[] values;
    private int start;
    private int end;
    private int[] deltas;
    private int min;
    private int max;
    private Histogram vHist;
    private Histogram dHist;
    private int searchOrder;
    private Choice regularChoice;
    private Choice bestChoice;
    private CodingMethod bestMethod;
    private int bestByteSize;
    private int bestZipSize;
    private int targetSize;
    public static final int MIN_EFFORT = 1;
    public static final int MID_EFFORT = 5;
    public static final int MAX_EFFORT = 9;
    public static final int POP_EFFORT = 4;
    public static final int RUN_EFFORT = 3;
    public static final int BYTE_SIZE = 0;
    public static final int ZIP_SIZE = 1;
    private Sizer zipSizer;
    private Deflater zipDef;
    private DeflaterOutputStream zipOut;
    private Sizer byteSizer;
    private Sizer byteOnlySizer;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    CodingChooser(final int effort, final Coding[] allCodingChoices) {
        this.optUseHistogram = true;
        this.optUsePopulationCoding = true;
        this.optUseAdaptiveCoding = true;
        this.topLevel = true;
        this.zipSizer = new Sizer();
        this.zipDef = new Deflater();
        this.zipOut = new DeflaterOutputStream(this.zipSizer, this.zipDef);
        this.byteSizer = new Sizer(this.zipOut);
        this.byteOnlySizer = new Sizer();
        final PropMap currentPropMap = Utils.currentPropMap();
        if (currentPropMap != null) {
            this.verbose = Math.max(currentPropMap.getInteger("com.sun.java.util.jar.pack.verbose"), currentPropMap.getInteger("com.sun.java.util.jar.pack.verbose.coding"));
            this.optUseHistogram = !currentPropMap.getBoolean("com.sun.java.util.jar.pack.no.histogram");
            this.optUsePopulationCoding = !currentPropMap.getBoolean("com.sun.java.util.jar.pack.no.population.coding");
            this.optUseAdaptiveCoding = !currentPropMap.getBoolean("com.sun.java.util.jar.pack.no.adaptive.coding");
            final int integer = currentPropMap.getInteger("com.sun.java.util.jar.pack.stress.coding");
            if (integer != 0) {
                this.stress = new Random(integer);
            }
        }
        this.effort = effort;
        this.allCodingChoices = allCodingChoices;
        this.fuzz = 1.0 + 0.0025 * (effort - 5);
        int n = 0;
        for (int i = 0; i < allCodingChoices.length; ++i) {
            if (allCodingChoices[i] != null) {
                ++n;
            }
        }
        this.choices = new Choice[n];
        int n2 = 0;
        for (int j = 0; j < allCodingChoices.length; ++j) {
            if (allCodingChoices[j] != null) {
                this.choices[n2++] = new Choice(allCodingChoices[j], j, new int[this.choices.length]);
            }
        }
        for (int k = 0; k < this.choices.length; ++k) {
            final Coding coding = this.choices[k].coding;
            assert coding.distanceFrom(coding) == 0;
            for (int l = 0; l < k; ++l) {
                final Coding coding2 = this.choices[l].coding;
                final int distance = coding.distanceFrom(coding2);
                assert distance > 0;
                assert distance == coding2.distanceFrom(coding);
                this.choices[k].distance[l] = distance;
                this.choices[l].distance[k] = distance;
            }
        }
    }
    
    Choice makeExtraChoice(final Coding coding) {
        final int[] array = new int[this.choices.length];
        for (int i = 0; i < array.length; ++i) {
            final Coding coding2 = this.choices[i].coding;
            final int distance = coding.distanceFrom(coding2);
            assert distance > 0;
            assert distance == coding2.distanceFrom(coding);
            array[i] = distance;
        }
        final Choice choice = new Choice(coding, -1, array);
        choice.reset();
        return choice;
    }
    
    ByteArrayOutputStream getContext() {
        if (this.context == null) {
            this.context = new ByteArrayOutputStream(65536);
        }
        return this.context;
    }
    
    private void reset(final int[] values, final int start, final int end) {
        this.values = values;
        this.start = start;
        this.end = end;
        this.deltas = null;
        this.min = Integer.MAX_VALUE;
        this.max = Integer.MIN_VALUE;
        this.vHist = null;
        this.dHist = null;
        this.searchOrder = 0;
        this.regularChoice = null;
        this.bestChoice = null;
        this.bestMethod = null;
        this.bestZipSize = Integer.MAX_VALUE;
        this.bestByteSize = Integer.MAX_VALUE;
        this.targetSize = Integer.MAX_VALUE;
    }
    
    CodingMethod choose(final int[] array, final int n, final int n2, final Coding coding, final int[] array2) {
        this.reset(array, n, n2);
        if (this.effort <= 1 || n >= n2) {
            if (array2 != null) {
                final int[] computeSizePrivate = this.computeSizePrivate(coding);
                array2[0] = computeSizePrivate[0];
                array2[1] = computeSizePrivate[1];
            }
            return coding;
        }
        if (this.optUseHistogram) {
            this.getValueHistogram();
            this.getDeltaHistogram();
        }
        for (int i = n; i < n2; ++i) {
            final int n3 = array[i];
            if (this.min > n3) {
                this.min = n3;
            }
            if (this.max < n3) {
                this.max = n3;
            }
        }
        final int markUsableChoices = this.markUsableChoices(coding);
        if (this.stress != null) {
            int nextInt = this.stress.nextInt(markUsableChoices * 2 + 4);
            CodingMethod codingMethod = null;
            for (int j = 0; j < this.choices.length; ++j) {
                final Choice choice = this.choices[j];
                if (choice.searchOrder >= 0 && nextInt-- == 0) {
                    codingMethod = choice.coding;
                    break;
                }
            }
            if (codingMethod == null) {
                if ((nextInt & 0x7) != 0x0) {
                    codingMethod = coding;
                }
                else {
                    codingMethod = this.stressCoding(this.min, this.max);
                }
            }
            if (!this.disablePopCoding && this.optUsePopulationCoding && this.effort >= 4) {
                codingMethod = this.stressPopCoding(codingMethod);
            }
            if (!this.disableRunCoding && this.optUseAdaptiveCoding && this.effort >= 3) {
                codingMethod = this.stressAdaptiveCoding(codingMethod);
            }
            return codingMethod;
        }
        double n4 = 1.0;
        for (int k = this.effort; k < 9; ++k) {
            n4 /= 1.414;
        }
        final int n5 = (int)Math.ceil(markUsableChoices * n4);
        this.bestChoice = this.regularChoice;
        this.evaluate(this.regularChoice);
        int updateDistances = this.updateDistances(this.regularChoice);
        final int bestZipSize = this.bestZipSize;
        final int bestByteSize = this.bestByteSize;
        if (this.regularChoice.coding == coding && this.topLevel) {
            final int encodeEscapeValue = BandStructure.encodeEscapeValue(115, coding);
            if (coding.canRepresentSigned(encodeEscapeValue)) {
                final int length = coding.getLength(encodeEscapeValue);
                final Choice regularChoice = this.regularChoice;
                regularChoice.zipSize -= length;
                this.bestByteSize = this.regularChoice.byteSize;
                this.bestZipSize = this.regularChoice.zipSize;
            }
        }
        int n6 = 1;
        while (this.searchOrder < n5) {
            if (n6 > updateDistances) {
                n6 = 1;
            }
            final Choice choiceNear = this.findChoiceNear(this.bestChoice, updateDistances / n6, updateDistances / (n6 *= 2) + 1);
            if (choiceNear == null) {
                continue;
            }
            assert choiceNear.coding.canRepresent(this.min, this.max);
            this.evaluate(choiceNear);
            final int updateDistances2 = this.updateDistances(choiceNear);
            if (choiceNear != this.bestChoice) {
                continue;
            }
            updateDistances = updateDistances2;
            if (this.verbose <= 5) {
                continue;
            }
            Utils.log.info("maxd = " + updateDistances);
        }
        final Coding coding2 = this.bestChoice.coding;
        assert coding2 == this.bestMethod;
        if (this.verbose > 2) {
            Utils.log.info("chooser: plain result=" + this.bestChoice + " after " + this.bestChoice.searchOrder + " rounds, " + (this.regularChoice.zipSize - this.bestZipSize) + " fewer bytes than regular " + coding);
        }
        this.bestChoice = null;
        if (!this.disablePopCoding && this.optUsePopulationCoding && this.effort >= 4 && this.bestMethod instanceof Coding) {
            this.tryPopulationCoding(coding2);
        }
        if (!this.disableRunCoding && this.optUseAdaptiveCoding && this.effort >= 3 && this.bestMethod instanceof Coding) {
            this.tryAdaptiveCoding(coding2);
        }
        if (array2 != null) {
            array2[0] = this.bestByteSize;
            array2[1] = this.bestZipSize;
        }
        if (this.verbose > 1) {
            Utils.log.info("chooser: result=" + this.bestMethod + " " + (bestZipSize - this.bestZipSize) + " fewer bytes than regular " + coding + "; win=" + pct(bestZipSize - this.bestZipSize, bestZipSize));
        }
        final CodingMethod bestMethod = this.bestMethod;
        this.reset(null, 0, 0);
        return bestMethod;
    }
    
    CodingMethod choose(final int[] array, final int n, final int n2, final Coding coding) {
        return this.choose(array, n, n2, coding, null);
    }
    
    CodingMethod choose(final int[] array, final Coding coding, final int[] array2) {
        return this.choose(array, 0, array.length, coding, array2);
    }
    
    CodingMethod choose(final int[] array, final Coding coding) {
        return this.choose(array, 0, array.length, coding, null);
    }
    
    private int markUsableChoices(final Coding coding) {
        int n = 0;
        for (int i = 0; i < this.choices.length; ++i) {
            final Choice regularChoice = this.choices[i];
            regularChoice.reset();
            if (!regularChoice.coding.canRepresent(this.min, this.max)) {
                regularChoice.searchOrder = -1;
                if (this.verbose > 1 && regularChoice.coding == coding) {
                    Utils.log.info("regular coding cannot represent [" + this.min + ".." + this.max + "]: " + coding);
                }
            }
            else {
                if (regularChoice.coding == coding) {
                    this.regularChoice = regularChoice;
                }
                ++n;
            }
        }
        if (this.regularChoice == null && coding.canRepresent(this.min, this.max)) {
            this.regularChoice = this.makeExtraChoice(coding);
            if (this.verbose > 1) {
                Utils.log.info("*** regular choice is extra: " + this.regularChoice.coding);
            }
        }
        if (this.regularChoice == null) {
            for (int j = 0; j < this.choices.length; ++j) {
                final Choice regularChoice2 = this.choices[j];
                if (regularChoice2.searchOrder != -1) {
                    this.regularChoice = regularChoice2;
                    break;
                }
            }
            if (this.verbose > 1) {
                Utils.log.info("*** regular choice does not apply " + coding);
                Utils.log.info("    using instead " + this.regularChoice.coding);
            }
        }
        if (this.verbose > 2) {
            Utils.log.info("chooser: #choices=" + n + " [" + this.min + ".." + this.max + "]");
            if (this.verbose > 4) {
                for (int k = 0; k < this.choices.length; ++k) {
                    final Choice choice = this.choices[k];
                    if (choice.searchOrder >= 0) {
                        Utils.log.info("  " + choice);
                    }
                }
            }
        }
        return n;
    }
    
    private Choice findChoiceNear(final Choice choice, final int n, final int n2) {
        if (this.verbose > 5) {
            Utils.log.info("findChoice " + n + ".." + n2 + " near: " + choice);
        }
        final int[] distance = choice.distance;
        Object o = null;
        for (int i = 0; i < this.choices.length; ++i) {
            final Choice choice2 = this.choices[i];
            if (choice2.searchOrder >= this.searchOrder) {
                if (distance[i] >= n2 && distance[i] <= n) {
                    if (choice2.minDistance >= n2 && choice2.minDistance <= n) {
                        if (this.verbose > 5) {
                            Utils.log.info("findChoice => good " + choice2);
                        }
                        return choice2;
                    }
                    o = choice2;
                }
            }
        }
        if (this.verbose > 5) {
            Utils.log.info("findChoice => found " + o);
        }
        return (Choice)o;
    }
    
    private void evaluate(final Choice bestChoice) {
        assert bestChoice.searchOrder == Integer.MAX_VALUE;
        bestChoice.searchOrder = this.searchOrder++;
        int n;
        if (bestChoice == this.bestChoice || bestChoice.isExtra()) {
            n = 1;
        }
        else if (this.optUseHistogram) {
            bestChoice.histSize = (int)Math.ceil(this.getHistogram(bestChoice.coding.isDelta()).getBitLength(bestChoice.coding) / 8.0);
            bestChoice.byteSize = bestChoice.histSize;
            n = ((bestChoice.byteSize <= this.targetSize) ? 1 : 0);
        }
        else {
            n = 1;
        }
        if (n != 0) {
            final int[] computeSizePrivate = this.computeSizePrivate(bestChoice.coding);
            bestChoice.byteSize = computeSizePrivate[0];
            bestChoice.zipSize = computeSizePrivate[1];
            if (this.noteSizes(bestChoice.coding, bestChoice.byteSize, bestChoice.zipSize)) {
                this.bestChoice = bestChoice;
            }
        }
        if (bestChoice.histSize >= 0 && !CodingChooser.$assertionsDisabled && bestChoice.byteSize != bestChoice.histSize) {
            throw new AssertionError();
        }
        if (this.verbose > 4) {
            Utils.log.info("evaluated " + bestChoice);
        }
    }
    
    private boolean noteSizes(final CodingMethod bestMethod, final int bestByteSize, final int bestZipSize) {
        assert bestZipSize > 0 && bestByteSize > 0;
        final boolean b = bestZipSize < this.bestZipSize;
        if (this.verbose > 3) {
            Utils.log.info("computed size " + bestMethod + " " + bestByteSize + "/zs=" + bestZipSize + ((b && this.bestMethod != null) ? (" better by " + pct(this.bestZipSize - bestZipSize, bestZipSize)) : ""));
        }
        if (b) {
            this.bestMethod = bestMethod;
            this.bestZipSize = bestZipSize;
            this.bestByteSize = bestByteSize;
            this.targetSize = (int)(bestByteSize * this.fuzz);
            return true;
        }
        return false;
    }
    
    private int updateDistances(final Choice choice) {
        final int[] distance = choice.distance;
        int n = 0;
        for (int i = 0; i < this.choices.length; ++i) {
            final Choice choice2 = this.choices[i];
            if (choice2.searchOrder >= this.searchOrder) {
                final int minDistance = distance[i];
                if (this.verbose > 5) {
                    Utils.log.info("evaluate dist " + minDistance + " to " + choice2);
                }
                if (choice2.minDistance > minDistance) {
                    choice2.minDistance = minDistance;
                }
                if (n < minDistance) {
                    n = minDistance;
                }
            }
        }
        if (this.verbose > 5) {
            Utils.log.info("evaluate maxd => " + n);
        }
        return n;
    }
    
    public void computeSize(final CodingMethod codingMethod, final int[] array, final int n, final int n2, final int[] array2) {
        if (n2 <= n) {
            array2[0] = (array2[1] = 0);
            return;
        }
        try {
            this.resetData();
            codingMethod.writeArrayTo(this.byteSizer, array, n, n2);
            array2[0] = this.getByteSize();
            array2[1] = this.getZipSize();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void computeSize(final CodingMethod codingMethod, final int[] array, final int[] array2) {
        this.computeSize(codingMethod, array, 0, array.length, array2);
    }
    
    public int[] computeSize(final CodingMethod codingMethod, final int[] array, final int n, final int n2) {
        final int[] array2 = { 0, 0 };
        this.computeSize(codingMethod, array, n, n2, array2);
        return array2;
    }
    
    public int[] computeSize(final CodingMethod codingMethod, final int[] array) {
        return this.computeSize(codingMethod, array, 0, array.length);
    }
    
    private int[] computeSizePrivate(final CodingMethod codingMethod) {
        final int[] array = { 0, 0 };
        this.computeSize(codingMethod, this.values, this.start, this.end, array);
        return array;
    }
    
    public int computeByteSize(final CodingMethod codingMethod, final int[] array, final int n, final int n2) {
        if (n2 - n < 0) {
            return 0;
        }
        if (!(codingMethod instanceof Coding)) {
            return this.countBytesToSizer(codingMethod, array, n, n2);
        }
        final int length = ((Coding)codingMethod).getLength(array, n, n2);
        final int countBytesToSizer;
        assert length == (countBytesToSizer = this.countBytesToSizer(codingMethod, array, n, n2)) : codingMethod + " : " + length + " != " + countBytesToSizer;
        return length;
    }
    
    private int countBytesToSizer(final CodingMethod codingMethod, final int[] array, final int n, final int n2) {
        try {
            this.byteOnlySizer.reset();
            codingMethod.writeArrayTo(this.byteOnlySizer, array, n, n2);
            return this.byteOnlySizer.getSize();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    int[] getDeltas(final int n, final int n2) {
        if ((n | n2) != 0x0) {
            return Coding.makeDeltas(this.values, this.start, this.end, n, n2);
        }
        if (this.deltas == null) {
            this.deltas = Coding.makeDeltas(this.values, this.start, this.end, 0, 0);
        }
        return this.deltas;
    }
    
    Histogram getValueHistogram() {
        if (this.vHist == null) {
            this.vHist = new Histogram(this.values, this.start, this.end);
            if (this.verbose > 3) {
                this.vHist.print("vHist", System.out);
            }
            else if (this.verbose > 1) {
                this.vHist.print("vHist", null, System.out);
            }
        }
        return this.vHist;
    }
    
    Histogram getDeltaHistogram() {
        if (this.dHist == null) {
            this.dHist = new Histogram(this.getDeltas(0, 0));
            if (this.verbose > 3) {
                this.dHist.print("dHist", System.out);
            }
            else if (this.verbose > 1) {
                this.dHist.print("dHist", null, System.out);
            }
        }
        return this.dHist;
    }
    
    Histogram getHistogram(final boolean b) {
        return b ? this.getDeltaHistogram() : this.getValueHistogram();
    }
    
    private void tryPopulationCoding(final Coding coding) {
        final Histogram valueHistogram = this.getValueHistogram();
        final Coding valueCoding = coding.getValueCoding();
        final Coding setL = BandStructure.UNSIGNED5.setL(64);
        final Coding valueCoding2 = coding.getValueCoding();
        int n = 4 + Math.max(valueCoding.getLength(this.min), valueCoding.getLength(this.max));
        final int length = setL.getLength(0);
        int n2 = length * (this.end - this.start);
        int n3 = (int)Math.ceil(valueHistogram.getBitLength(valueCoding2) / 8.0);
        int n4 = n + n2 + n3;
        int n5 = 0;
        final int[] array = new int[1 + valueHistogram.getTotalLength()];
        int n6 = -1;
        int n7 = -1;
        final int[][] matrix = valueHistogram.getMatrix();
        int n8 = -1;
        int length2 = 1;
        int n9 = 0;
        for (int i = 1; i <= valueHistogram.getTotalLength(); ++i) {
            if (length2 == 1) {
                ++n8;
                n9 = matrix[n8][0];
                length2 = matrix[n8].length;
            }
            final int n10 = matrix[n8][--length2];
            array[i] = n10;
            final int length3 = valueCoding.getLength(n10);
            n += length3;
            final int n11 = n9;
            n2 += (setL.getLength(i) - length) * n11;
            n3 -= length3 * n11;
            final int n12 = n + n2 + n3;
            if (n4 > n12) {
                if (n12 <= this.targetSize) {
                    n7 = i;
                    if (n6 < 0) {
                        n6 = i;
                    }
                    if (this.verbose > 4) {
                        Utils.log.info("better pop-size at fvc=" + i + " by " + pct(n4 - n12, n4));
                    }
                }
                n4 = n12;
                n5 = i;
            }
        }
        if (n6 < 0) {
            if (this.verbose > 1 && this.verbose > 1) {
                Utils.log.info("no good pop-size; best was " + n4 + " at " + n5 + " worse by " + pct(n4 - this.bestByteSize, this.bestByteSize));
            }
            return;
        }
        if (this.verbose > 1) {
            Utils.log.info("initial best pop-size at fvc=" + n5 + " in [" + n6 + ".." + n7 + "] by " + pct(this.bestByteSize - n4, this.bestByteSize));
        }
        final int bestZipSize = this.bestZipSize;
        final int[] lValuesCoded = PopulationCoding.LValuesCoded;
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        final ArrayList list3 = new ArrayList();
        if (n5 <= 255) {
            list.add(BandStructure.BYTE1);
        }
        else {
            int b = 5;
            final boolean b2 = this.effort > 4;
            if (b2) {
                list2.add(BandStructure.BYTE1.setS(1));
            }
            for (int j = lValuesCoded.length - 1; j >= 1; --j) {
                final int n13 = lValuesCoded[j];
                final Coding fitTokenCoding = PopulationCoding.fitTokenCoding(n6, n13);
                final Coding fitTokenCoding2 = PopulationCoding.fitTokenCoding(n5, n13);
                Coding fitTokenCoding3 = PopulationCoding.fitTokenCoding(n7, n13);
                if (fitTokenCoding2 != null) {
                    if (!list.contains(fitTokenCoding2)) {
                        list.add(fitTokenCoding2);
                    }
                    if (b > fitTokenCoding2.B()) {
                        b = fitTokenCoding2.B();
                    }
                }
                if (b2) {
                    if (fitTokenCoding3 == null) {
                        fitTokenCoding3 = fitTokenCoding2;
                    }
                    for (int k = fitTokenCoding.B(); k <= fitTokenCoding3.B(); ++k) {
                        if (k != fitTokenCoding2.B()) {
                            if (k != 1) {
                                final Coding setS = fitTokenCoding3.setB(k).setS(1);
                                if (!list2.contains(setS)) {
                                    list2.add(setS);
                                }
                            }
                        }
                    }
                }
            }
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                final Coding coding2 = (Coding)iterator.next();
                if (coding2.B() > b) {
                    iterator.remove();
                    list3.add(0, coding2);
                }
            }
        }
        final ArrayList list4 = new ArrayList();
        final Iterator iterator2 = list.iterator();
        final Iterator iterator3 = list2.iterator();
        final Iterator iterator4 = list3.iterator();
        while (iterator2.hasNext() || iterator3.hasNext() || iterator4.hasNext()) {
            if (iterator2.hasNext()) {
                list4.add(iterator2.next());
            }
            if (iterator3.hasNext()) {
                list4.add(iterator3.next());
            }
            if (iterator4.hasNext()) {
                list4.add(iterator4.next());
            }
        }
        list.clear();
        list2.clear();
        list3.clear();
        int size = list4.size();
        if (this.effort == 4) {
            size = 2;
        }
        else if (size > 4) {
            size -= 4;
            size = size * (this.effort - 4) / 5;
            size += 4;
        }
        if (list4.size() > size) {
            if (this.verbose > 4) {
                Utils.log.info("allFits before clip: " + list4);
            }
            list4.subList(size, list4.size()).clear();
        }
        if (this.verbose > 3) {
            Utils.log.info("allFits: " + list4);
        }
        for (Coding setS2 : list4) {
            boolean b3 = false;
            if (setS2.S() == 1) {
                b3 = true;
                setS2 = setS2.setS(0);
            }
            int min;
            if (!b3) {
                min = n5;
                assert setS2.umax() >= min;
                assert setS2.setB(setS2.B() - 1).umax() < min;
            }
            else {
                min = Math.min(setS2.umax(), n7);
                if (min < n6) {
                    continue;
                }
                if (min == n5) {
                    continue;
                }
            }
            final PopulationCoding populationCoding = new PopulationCoding();
            populationCoding.setHistogram(valueHistogram);
            populationCoding.setL(setS2.L());
            populationCoding.setFavoredValues(array, min);
            assert populationCoding.tokenCoding == setS2;
            populationCoding.resortFavoredValues();
            final int[] computePopSizePrivate = this.computePopSizePrivate(populationCoding, valueCoding, valueCoding2);
            this.noteSizes(populationCoding, computePopSizePrivate[0], 4 + computePopSizePrivate[1]);
        }
        if (this.verbose > 3) {
            Utils.log.info("measured best pop, size=" + this.bestByteSize + "/zs=" + this.bestZipSize + " better by " + pct(bestZipSize - this.bestZipSize, bestZipSize));
            if (this.bestZipSize < bestZipSize) {
                Utils.log.info(">>> POP WINS BY " + (bestZipSize - this.bestZipSize));
            }
        }
    }
    
    private int[] computePopSizePrivate(final PopulationCoding populationCoding, final Coding coding, final Coding coding2) {
        if (this.popHelper == null) {
            this.popHelper = new CodingChooser(this.effort, this.allCodingChoices);
            if (this.stress != null) {
                this.popHelper.addStressSeed(this.stress.nextInt());
            }
            this.popHelper.topLevel = false;
            final CodingChooser popHelper = this.popHelper;
            --popHelper.verbose;
            this.popHelper.disablePopCoding = true;
            this.popHelper.disableRunCoding = this.disableRunCoding;
            if (this.effort < 5) {
                this.popHelper.disableRunCoding = true;
            }
        }
        final int fVlen = populationCoding.fVlen;
        if (this.verbose > 2) {
            Utils.log.info("computePopSizePrivate fvlen=" + fVlen + " tc=" + populationCoding.tokenCoding);
            Utils.log.info("{ //BEGIN");
        }
        final int[] fValues = populationCoding.fValues;
        final int[][] encodeValues = populationCoding.encodeValues(this.values, this.start, this.end);
        final int[] array = encodeValues[0];
        final int[] array2 = encodeValues[1];
        if (this.verbose > 2) {
            Utils.log.info("-- refine on fv[" + fVlen + "] fc=" + coding);
        }
        populationCoding.setFavoredCoding(this.popHelper.choose(fValues, 1, 1 + fVlen, coding));
        if (populationCoding.tokenCoding instanceof Coding && (this.stress == null || this.stress.nextBoolean())) {
            if (this.verbose > 2) {
                Utils.log.info("-- refine on tv[" + array.length + "] tc=" + populationCoding.tokenCoding);
            }
            final CodingMethod choose = this.popHelper.choose(array, (Coding)populationCoding.tokenCoding);
            if (choose != populationCoding.tokenCoding) {
                if (this.verbose > 2) {
                    Utils.log.info(">>> refined tc=" + choose);
                }
                populationCoding.setTokenCoding(choose);
            }
        }
        if (array2.length == 0) {
            populationCoding.setUnfavoredCoding(null);
        }
        else {
            if (this.verbose > 2) {
                Utils.log.info("-- refine on uv[" + array2.length + "] uc=" + populationCoding.unfavoredCoding);
            }
            populationCoding.setUnfavoredCoding(this.popHelper.choose(array2, coding2));
        }
        if (this.verbose > 3) {
            Utils.log.info("finish computePopSizePrivate fvlen=" + fVlen + " fc=" + populationCoding.favoredCoding + " tc=" + populationCoding.tokenCoding + " uc=" + populationCoding.unfavoredCoding);
            final StringBuilder sb = new StringBuilder();
            sb.append("fv = {");
            for (int i = 1; i <= fVlen; ++i) {
                if (i % 10 == 0) {
                    sb.append('\n');
                }
                sb.append(" ").append(fValues[i]);
            }
            sb.append('\n');
            sb.append("}");
            Utils.log.info(sb.toString());
        }
        if (this.verbose > 2) {
            Utils.log.info("} //END");
        }
        if (this.stress != null) {
            return null;
        }
        int[] array3;
        try {
            this.resetData();
            populationCoding.writeSequencesTo(this.byteSizer, array, array2);
            array3 = new int[] { this.getByteSize(), this.getZipSize() };
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        int[] computeSizePrivate = null;
        assert (computeSizePrivate = this.computeSizePrivate(populationCoding)) != null;
        assert computeSizePrivate[0] == array3[0] : computeSizePrivate[0] + " != " + array3[0];
        return array3;
    }
    
    private void tryAdaptiveCoding(final Coding coding) {
        final int bestZipSize = this.bestZipSize;
        int start = this.start;
        int n = this.end;
        int[] array = this.values;
        final int n2 = n - start;
        if (coding.isDelta()) {
            array = this.getDeltas(0, 0);
            start = 0;
            n = array.length;
        }
        final int[] array2 = new int[n2 + 1];
        int n3 = 0;
        int n4 = 0;
        for (int i = start; i < n; ++i) {
            final int n5 = array[i];
            array2[n3++] = n4;
            final int length = coding.getLength(n5);
            assert length < Integer.MAX_VALUE;
            n4 += length;
        }
        array2[n3++] = n4;
        assert n3 == array2.length;
        final double n6 = n4 / (double)n2;
        double n7;
        if (this.effort >= 5) {
            if (this.effort > 6) {
                n7 = 1.001;
            }
            else {
                n7 = 1.003;
            }
        }
        else if (this.effort > 3) {
            n7 = 1.01;
        }
        else {
            n7 = 1.03;
        }
        final double n8 = n7 * n7;
        final double n9 = n8 * n8;
        final double n10 = n8 * n8 * n8;
        final double[] array3 = new double[1 + (this.effort - 3)];
        final double log = Math.log(n2);
        for (int j = 0; j < array3.length; ++j) {
            array3[j] = Math.exp(log * (j + 1) / (array3.length + 1));
        }
        final int[] array4 = new int[array3.length];
        int n11 = 0;
        for (int k = 0; k < array3.length; ++k) {
            final int nextK = AdaptiveCoding.getNextK((int)Math.round(array3[k]) - 1);
            if (nextK > 0) {
                if (nextK < n2) {
                    if (n11 <= 0 || nextK != array4[n11 - 1]) {
                        array4[n11++] = nextK;
                    }
                }
            }
        }
        final int[] realloc = BandStructure.realloc(array4, n11);
        final int[] array5 = new int[realloc.length];
        final double[] array6 = new double[realloc.length];
        for (int l = 0; l < realloc.length; ++l) {
            final int n12 = realloc[l];
            double n13;
            if (n12 < 10) {
                n13 = n10;
            }
            else if (n12 < 100) {
                n13 = n9;
            }
            else {
                n13 = n8;
            }
            array6[l] = n13;
            array5[l] = 4 + (int)Math.ceil(n12 * n6 * n13);
        }
        if (this.verbose > 1) {
            System.out.print("tryAdaptiveCoding [" + n2 + "] avgS=" + n6 + " fuzz=" + n8 + " meshes: {");
            for (int n14 = 0; n14 < realloc.length; ++n14) {
                System.out.print(" " + realloc[n14] + "(" + array5[n14] + ")");
            }
            Utils.log.info(" }");
        }
        if (this.runHelper == null) {
            this.runHelper = new CodingChooser(this.effort, this.allCodingChoices);
            if (this.stress != null) {
                this.runHelper.addStressSeed(this.stress.nextInt());
            }
            this.runHelper.topLevel = false;
            final CodingChooser runHelper = this.runHelper;
            --runHelper.verbose;
            this.runHelper.disableRunCoding = true;
            this.runHelper.disablePopCoding = this.disablePopCoding;
            if (this.effort < 5) {
                this.runHelper.disablePopCoding = true;
            }
        }
        for (int nextK2 = 0; nextK2 < n2; ++nextK2) {
            nextK2 = AdaptiveCoding.getNextK(nextK2 - 1);
            if (nextK2 > n2) {
                nextK2 = n2;
            }
            for (int n15 = realloc.length - 1; n15 >= 0; --n15) {
                final int n16 = realloc[n15];
                final int n17 = array5[n15];
                if (nextK2 + n16 <= n2) {
                    final int n18 = array2[nextK2 + n16] - array2[nextK2];
                    if (n18 >= n17) {
                        int n19 = nextK2 + n16;
                        int n20 = n18;
                        final double n21 = n6 * array6[n15];
                        while (n19 < n2 && n19 - nextK2 <= n2 / 2) {
                            final int n22 = n19;
                            final int n23 = n20;
                            n19 = nextK2 + AdaptiveCoding.getNextK(n19 + n16 - nextK2 - 1);
                            if (n19 < 0 || n19 > n2) {
                                n19 = n2;
                            }
                            n20 = array2[n19] - array2[nextK2];
                            if (n20 < 4.0 + (n19 - nextK2) * n21) {
                                n20 = n23;
                                n19 = n22;
                                break;
                            }
                        }
                        final int n24 = n19;
                        if (this.verbose > 2) {
                            Utils.log.info("bulge at " + nextK2 + "[" + (n19 - nextK2) + "] of " + pct(n20 - n6 * (n19 - nextK2), n6 * (n19 - nextK2)));
                            Utils.log.info("{ //BEGIN");
                        }
                        final CodingMethod choose = this.runHelper.choose(this.values, this.start + nextK2, this.start + n19, coding);
                        CodingMethod choose2;
                        CodingMethod choose3;
                        if (choose == coding) {
                            choose2 = coding;
                            choose3 = coding;
                        }
                        else {
                            choose2 = this.runHelper.choose(this.values, this.start, this.start + nextK2, coding);
                            choose3 = this.runHelper.choose(this.values, this.start + n19, this.start + n2, coding);
                        }
                        if (this.verbose > 2) {
                            Utils.log.info("} //END");
                        }
                        if (choose2 == choose && nextK2 > 0 && AdaptiveCoding.isCodableLength(n19)) {
                            nextK2 = 0;
                        }
                        if (choose == choose3 && n19 < n2) {
                            n19 = n2;
                        }
                        if (choose2 != coding || choose != coding || choose3 != coding) {
                            int n25 = 0;
                            CodingMethod codingMethod;
                            if (n19 == n2) {
                                codingMethod = choose;
                            }
                            else {
                                codingMethod = new AdaptiveCoding(n19 - nextK2, choose, choose3);
                                n25 += 4;
                            }
                            if (nextK2 > 0) {
                                codingMethod = new AdaptiveCoding(nextK2, choose2, codingMethod);
                                n25 += 4;
                            }
                            final int[] computeSizePrivate = this.computeSizePrivate(codingMethod);
                            this.noteSizes(codingMethod, computeSizePrivate[0], computeSizePrivate[1] + n25);
                        }
                        nextK2 = n24;
                        break;
                    }
                }
            }
        }
        if (this.verbose > 3 && this.bestZipSize < bestZipSize) {
            Utils.log.info(">>> RUN WINS BY " + (bestZipSize - this.bestZipSize));
        }
    }
    
    private static String pct(final double n, final double n2) {
        return Math.round(n / n2 * 10000.0) / 100.0 + "%";
    }
    
    private void resetData() {
        this.flushData();
        this.zipDef.reset();
        if (this.context != null) {
            try {
                this.context.writeTo(this.byteSizer);
            }
            catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        this.zipSizer.reset();
        this.byteSizer.reset();
    }
    
    private void flushData() {
        try {
            this.zipOut.finish();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private int getByteSize() {
        return this.byteSizer.getSize();
    }
    
    private int getZipSize() {
        this.flushData();
        return this.zipSizer.getSize();
    }
    
    void addStressSeed(final int n) {
        if (this.stress == null) {
            return;
        }
        this.stress.setSeed(n + ((long)this.stress.nextInt() << 32));
    }
    
    private CodingMethod stressPopCoding(final CodingMethod codingMethod) {
        assert this.stress != null;
        if (!(codingMethod instanceof Coding)) {
            return codingMethod;
        }
        final Coding valueCoding = ((Coding)codingMethod).getValueCoding();
        final Histogram valueHistogram = this.getValueHistogram();
        final int stressLen = this.stressLen(valueHistogram.getTotalLength());
        if (stressLen == 0) {
            return codingMethod;
        }
        final ArrayList list = new ArrayList();
        if (this.stress.nextBoolean()) {
            final HashSet set = new HashSet();
            for (int i = this.start; i < this.end; ++i) {
                if (set.add(this.values[i])) {
                    list.add(this.values[i]);
                }
            }
        }
        else {
            final int[][] matrix = valueHistogram.getMatrix();
            for (int j = 0; j < matrix.length; ++j) {
                final int[] array = matrix[j];
                for (int k = 1; k < array.length; ++k) {
                    list.add(array[k]);
                }
            }
        }
        int nextInt = this.stress.nextInt();
        if ((nextInt & 0x7) <= 2) {
            Collections.shuffle(list, this.stress);
        }
        else {
            final int n;
            if (((n = nextInt >>> 3) & 0x7) <= 2) {
                Collections.sort((List<Comparable>)list);
            }
            final int n2;
            if (((n2 = n >>> 3) & 0x7) <= 2) {
                Collections.reverse(list);
            }
            if (((nextInt = n2 >>> 3) & 0x7) <= 2) {
                Collections.rotate(list, this.stressLen(list.size()));
            }
        }
        if (list.size() > stressLen) {
            if ((nextInt >>> 3 & 0x7) <= 2) {
                list.subList(stressLen, list.size()).clear();
            }
            else {
                list.subList(0, list.size() - stressLen).clear();
            }
        }
        final int size = list.size();
        final int[] array2 = new int[1 + size];
        for (int l = 0; l < size; ++l) {
            array2[1 + l] = (int)list.get(l);
        }
        final PopulationCoding populationCoding = new PopulationCoding();
        populationCoding.setFavoredValues(array2, size);
        final int[] lValuesCoded = PopulationCoding.LValuesCoded;
        for (int n3 = 0; n3 < lValuesCoded.length / 2; ++n3) {
            final int m = lValuesCoded[this.stress.nextInt(lValuesCoded.length)];
            if (m >= 0) {
                if (PopulationCoding.fitTokenCoding(size, m) != null) {
                    populationCoding.setL(m);
                    break;
                }
            }
        }
        if (populationCoding.tokenCoding == null) {
            int n5;
            int n4 = n5 = array2[1];
            for (int n6 = 2; n6 <= size; ++n6) {
                final int n7 = array2[n6];
                if (n4 > n7) {
                    n4 = n7;
                }
                if (n5 < n7) {
                    n5 = n7;
                }
            }
            populationCoding.tokenCoding = this.stressCoding(n4, n5);
        }
        this.computePopSizePrivate(populationCoding, valueCoding, valueCoding);
        return populationCoding;
    }
    
    private CodingMethod stressAdaptiveCoding(final CodingMethod codingMethod) {
        assert this.stress != null;
        if (!(codingMethod instanceof Coding)) {
            return codingMethod;
        }
        final Coding coding = (Coding)codingMethod;
        final int n = this.end - this.start;
        if (n < 2) {
            return codingMethod;
        }
        final int n2 = this.stressLen(n - 1) + 1;
        if (n2 == n) {
            return codingMethod;
        }
        try {
            assert !this.disableRunCoding;
            this.disableRunCoding = true;
            final int[] array = this.values.clone();
            CodingMethod codingMethod2 = null;
            int n7;
            for (int i = this.end, start = this.start; i > start; i = n7) {
                final int n3 = (i - start < 100) ? -1 : this.stress.nextInt();
                int decodeK;
                if ((n3 & 0x7) != 0x0) {
                    decodeK = ((n2 == 1) ? n2 : (this.stressLen(n2 - 1) + 1));
                }
                else {
                    final int n5;
                    int n4 = (n5 = n3 >>> 3) & 0x3;
                    int n6 = n5 >>> 3 & 0xFF;
                    while (true) {
                        decodeK = AdaptiveCoding.decodeK(n4, n6);
                        if (decodeK <= i - start) {
                            break;
                        }
                        if (n6 != 3) {
                            n6 = 3;
                        }
                        else {
                            --n4;
                        }
                    }
                    assert AdaptiveCoding.isCodableLength(decodeK);
                }
                if (decodeK > i - start) {
                    decodeK = i - start;
                }
                while (!AdaptiveCoding.isCodableLength(decodeK)) {
                    --decodeK;
                }
                n7 = i - decodeK;
                assert n7 < i;
                assert n7 >= start;
                final CodingMethod choose = this.choose(array, n7, i, coding);
                if (codingMethod2 == null) {
                    codingMethod2 = choose;
                }
                else {
                    codingMethod2 = new AdaptiveCoding(i - n7, choose, codingMethod2);
                }
            }
            return codingMethod2;
        }
        finally {
            this.disableRunCoding = false;
        }
    }
    
    private Coding stressCoding(final int n, final int n2) {
        assert this.stress != null;
        for (int i = 0; i < 100; ++i) {
            Coding coding = Coding.of(this.stress.nextInt(5) + 1, this.stress.nextInt(256) + 1, this.stress.nextInt(3));
            if (coding.B() == 1) {
                coding = coding.setH(256);
            }
            if (coding.H() == 256 && coding.B() >= 5) {
                coding = coding.setB(4);
            }
            if (this.stress.nextBoolean()) {
                final Coding setD = coding.setD(1);
                if (setD.canRepresent(n, n2)) {
                    return setD;
                }
            }
            if (coding.canRepresent(n, n2)) {
                return coding;
            }
        }
        return BandStructure.UNSIGNED5;
    }
    
    private int stressLen(final int n) {
        assert this.stress != null;
        assert n >= 0;
        final int nextInt = this.stress.nextInt(100);
        if (nextInt < 20) {
            return Math.min(n / 5, nextInt);
        }
        if (nextInt < 40) {
            return n;
        }
        return this.stress.nextInt(n);
    }
    
    static class Choice
    {
        final Coding coding;
        final int index;
        final int[] distance;
        int searchOrder;
        int minDistance;
        int zipSize;
        int byteSize;
        int histSize;
        
        Choice(final Coding coding, final int index, final int[] distance) {
            this.coding = coding;
            this.index = index;
            this.distance = distance;
        }
        
        void reset() {
            this.searchOrder = Integer.MAX_VALUE;
            this.minDistance = Integer.MAX_VALUE;
            final int zipSize = -1;
            this.histSize = zipSize;
            this.byteSize = zipSize;
            this.zipSize = zipSize;
        }
        
        boolean isExtra() {
            return this.index < 0;
        }
        
        @Override
        public String toString() {
            return this.stringForDebug();
        }
        
        private String stringForDebug() {
            String s = "";
            if (this.searchOrder < Integer.MAX_VALUE) {
                s = s + " so: " + this.searchOrder;
            }
            if (this.minDistance < Integer.MAX_VALUE) {
                s = s + " md: " + this.minDistance;
            }
            if (this.zipSize > 0) {
                s = s + " zs: " + this.zipSize;
            }
            if (this.byteSize > 0) {
                s = s + " bs: " + this.byteSize;
            }
            if (this.histSize > 0) {
                s = s + " hs: " + this.histSize;
            }
            return "Choice[" + this.index + "] " + s + " " + this.coding;
        }
    }
    
    static class Sizer extends OutputStream
    {
        final OutputStream out;
        private int count;
        
        Sizer(final OutputStream out) {
            this.out = out;
        }
        
        Sizer() {
            this(null);
        }
        
        @Override
        public void write(final int n) throws IOException {
            ++this.count;
            if (this.out != null) {
                this.out.write(n);
            }
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.count += n2;
            if (this.out != null) {
                this.out.write(array, n, n2);
            }
        }
        
        public void reset() {
            this.count = 0;
        }
        
        public int getSize() {
            return this.count;
        }
        
        @Override
        public String toString() {
            String s = super.toString();
            assert (s = this.stringForDebug()) != null;
            return s;
        }
        
        String stringForDebug() {
            return "<Sizer " + this.getSize() + ">";
        }
    }
}
