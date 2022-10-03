package org.apache.commons.compress.harmony.pack200;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;

public abstract class BandSet
{
    protected final SegmentHeader segmentHeader;
    final int effort;
    private static final int[] effortThresholds;
    private long[] canonicalLargest;
    private long[] canonicalSmallest;
    
    public BandSet(final int effort, final SegmentHeader header) {
        this.effort = effort;
        this.segmentHeader = header;
    }
    
    public abstract void pack(final OutputStream p0) throws IOException, Pack200Exception;
    
    public byte[] encodeScalar(final int[] band, final BHSDCodec codec) throws Pack200Exception {
        return codec.encode(band);
    }
    
    public byte[] encodeScalar(final int value, final BHSDCodec codec) throws Pack200Exception {
        return codec.encode(value);
    }
    
    public byte[] encodeBandInt(final String name, final int[] ints, final BHSDCodec defaultCodec) throws Pack200Exception {
        byte[] encodedBand = null;
        if (this.effort > 1 && ints.length >= BandSet.effortThresholds[this.effort]) {
            final BandAnalysisResults results = this.analyseBand(name, ints, defaultCodec);
            final Codec betterCodec = results.betterCodec;
            encodedBand = results.encodedBand;
            if (betterCodec != null) {
                if (betterCodec instanceof BHSDCodec) {
                    final int[] specifierBand = CodecEncoding.getSpecifier(betterCodec, defaultCodec);
                    int specifier = specifierBand[0];
                    if (specifierBand.length > 1) {
                        for (int i = 1; i < specifierBand.length; ++i) {
                            this.segmentHeader.appendBandCodingSpecifier(specifierBand[i]);
                        }
                    }
                    if (defaultCodec.isSigned()) {
                        specifier = -1 - specifier;
                    }
                    else {
                        specifier += defaultCodec.getL();
                    }
                    final byte[] specifierEncoded = defaultCodec.encode(new int[] { specifier });
                    final byte[] band = new byte[specifierEncoded.length + encodedBand.length];
                    System.arraycopy(specifierEncoded, 0, band, 0, specifierEncoded.length);
                    System.arraycopy(encodedBand, 0, band, specifierEncoded.length, encodedBand.length);
                    return band;
                }
                if (betterCodec instanceof PopulationCodec) {
                    final int[] extraSpecifierInfo = results.extraMetadata;
                    for (int j = 0; j < extraSpecifierInfo.length; ++j) {
                        this.segmentHeader.appendBandCodingSpecifier(extraSpecifierInfo[j]);
                    }
                    return encodedBand;
                }
                if (betterCodec instanceof RunCodec) {}
            }
        }
        if (ints.length > 0) {
            if (encodedBand == null) {
                encodedBand = defaultCodec.encode(ints);
            }
            final int first = ints[0];
            if (defaultCodec.getB() != 1) {
                if (defaultCodec.isSigned() && first >= -256 && first <= -1) {
                    final int specifier2 = -1 - CodecEncoding.getSpecifierForDefaultCodec(defaultCodec);
                    final byte[] specifierEncoded2 = defaultCodec.encode(new int[] { specifier2 });
                    final byte[] band2 = new byte[specifierEncoded2.length + encodedBand.length];
                    System.arraycopy(specifierEncoded2, 0, band2, 0, specifierEncoded2.length);
                    System.arraycopy(encodedBand, 0, band2, specifierEncoded2.length, encodedBand.length);
                    return band2;
                }
                if (!defaultCodec.isSigned() && first >= defaultCodec.getL() && first <= defaultCodec.getL() + 255) {
                    final int specifier2 = CodecEncoding.getSpecifierForDefaultCodec(defaultCodec) + defaultCodec.getL();
                    final byte[] specifierEncoded2 = defaultCodec.encode(new int[] { specifier2 });
                    final byte[] band2 = new byte[specifierEncoded2.length + encodedBand.length];
                    System.arraycopy(specifierEncoded2, 0, band2, 0, specifierEncoded2.length);
                    System.arraycopy(encodedBand, 0, band2, specifierEncoded2.length, encodedBand.length);
                    return band2;
                }
            }
            return encodedBand;
        }
        return new byte[0];
    }
    
    private BandAnalysisResults analyseBand(final String name, final int[] band, final BHSDCodec defaultCodec) throws Pack200Exception {
        final BandAnalysisResults results = new BandAnalysisResults();
        if (this.canonicalLargest == null) {
            this.canonicalLargest = new long[116];
            this.canonicalSmallest = new long[116];
            for (int i = 1; i < this.canonicalLargest.length; ++i) {
                this.canonicalLargest[i] = CodecEncoding.getCanonicalCodec(i).largest();
                this.canonicalSmallest[i] = CodecEncoding.getCanonicalCodec(i).smallest();
            }
        }
        final BandData bandData = new BandData(band);
        final byte[] encoded = defaultCodec.encode(band);
        results.encodedBand = encoded;
        if (encoded.length <= band.length + 23 - 2 * this.effort) {
            return results;
        }
        if (!bandData.anyNegatives() && bandData.largest <= Codec.BYTE1.largest()) {
            results.encodedBand = Codec.BYTE1.encode(band);
            results.betterCodec = Codec.BYTE1;
            return results;
        }
        if (this.effort > 3 && !name.equals("POPULATION")) {
            final int numDistinctValues = bandData.numDistinctValues();
            final float distinctValuesAsProportion = numDistinctValues / (float)band.length;
            if (numDistinctValues < 100 || distinctValuesAsProportion < 0.02 || (this.effort > 6 && distinctValuesAsProportion < 0.04)) {
                this.encodeWithPopulationCodec(name, band, defaultCodec, bandData, results);
                if (this.timeToStop(results)) {
                    return results;
                }
            }
        }
        final List codecFamiliesToTry = new ArrayList();
        if (bandData.mainlyPositiveDeltas() && bandData.mainlySmallDeltas()) {
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs2);
        }
        if (bandData.wellCorrelated()) {
            if (bandData.mainlyPositiveDeltas()) {
                codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs1);
                codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs3);
                codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs4);
                codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs5);
                codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs1);
                codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs3);
                codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs4);
                codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs5);
                codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs2);
            }
            else {
                codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs1);
                codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs3);
                codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs2);
                codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs4);
                codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs5);
                codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaSignedCodecs1);
                codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaSignedCodecs2);
            }
        }
        else if (bandData.anyNegatives()) {
            codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaSignedCodecs1);
            codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaSignedCodecs2);
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs1);
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs2);
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs3);
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs4);
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs5);
        }
        else {
            codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs1);
            codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs3);
            codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs4);
            codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs5);
            codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs2);
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs1);
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs3);
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs4);
            codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs5);
        }
        if (name.equalsIgnoreCase("cpint")) {
            System.out.print("");
        }
        for (final BHSDCodec[] family : codecFamiliesToTry) {
            this.tryCodecs(name, band, defaultCodec, bandData, results, encoded, family);
            if (this.timeToStop(results)) {
                break;
            }
        }
        return results;
    }
    
    private boolean timeToStop(final BandAnalysisResults results) {
        if (this.effort > 6) {
            return results.numCodecsTried >= this.effort * 2;
        }
        return results.numCodecsTried >= this.effort;
    }
    
    private void tryCodecs(final String name, final int[] band, final BHSDCodec defaultCodec, final BandData bandData, final BandAnalysisResults results, final byte[] encoded, final BHSDCodec[] potentialCodecs) throws Pack200Exception {
        for (int i = 0; i < potentialCodecs.length; ++i) {
            final BHSDCodec potential = potentialCodecs[i];
            if (potential.equals(defaultCodec)) {
                return;
            }
            if (potential.isDelta()) {
                if (potential.largest() >= bandData.largestDelta && potential.smallest() <= bandData.smallestDelta && potential.largest() >= bandData.largest && potential.smallest() <= bandData.smallest) {
                    final byte[] encoded2 = potential.encode(band);
                    results.numCodecsTried++;
                    final byte[] specifierEncoded = defaultCodec.encode(CodecEncoding.getSpecifier(potential, null));
                    final int saved = encoded.length - encoded2.length - specifierEncoded.length;
                    if (saved > results.saved) {
                        results.betterCodec = potential;
                        results.encodedBand = encoded2;
                        results.saved = saved;
                    }
                }
            }
            else if (potential.largest() >= bandData.largest && potential.smallest() <= bandData.smallest) {
                final byte[] encoded2 = potential.encode(band);
                results.numCodecsTried++;
                final byte[] specifierEncoded = defaultCodec.encode(CodecEncoding.getSpecifier(potential, null));
                final int saved = encoded.length - encoded2.length - specifierEncoded.length;
                if (saved > results.saved) {
                    results.betterCodec = potential;
                    results.encodedBand = encoded2;
                    results.saved = saved;
                }
            }
            if (this.timeToStop(results)) {
                return;
            }
        }
    }
    
    private void encodeWithPopulationCodec(final String name, final int[] band, final BHSDCodec defaultCodec, final BandData bandData, final BandAnalysisResults results) throws Pack200Exception {
        results.numCodecsTried += 3;
        final Map distinctValues = bandData.distinctValues;
        final List favoured = new ArrayList();
        for (final Integer value : distinctValues.keySet()) {
            final Integer count = distinctValues.get(value);
            if (count > 2 || distinctValues.size() < 256) {
                favoured.add(value);
            }
        }
        if (distinctValues.size() > 255) {
            Collections.sort((List<Object>)favoured, (arg0, arg1) -> distinctValues.get(arg1).compareTo(distinctValues.get(arg0)));
        }
        final IntList unfavoured = new IntList();
        final Map favouredToIndex = new HashMap();
        for (int i = 0; i < favoured.size(); ++i) {
            final Integer value2 = favoured.get(i);
            favouredToIndex.put(value2, i);
        }
        final int[] tokens = new int[band.length];
        for (int j = 0; j < band.length; ++j) {
            final Integer favouredIndex = favouredToIndex.get(band[j]);
            if (favouredIndex == null) {
                tokens[j] = 0;
                unfavoured.add(band[j]);
            }
            else {
                tokens[j] = favouredIndex + 1;
            }
        }
        favoured.add(favoured.get(favoured.size() - 1));
        final int[] favouredBand = this.integerListToArray(favoured);
        final int[] unfavouredBand = unfavoured.toArray();
        final BandAnalysisResults favouredResults = this.analyseBand("POPULATION", favouredBand, defaultCodec);
        final BandAnalysisResults unfavouredResults = this.analyseBand("POPULATION", unfavouredBand, defaultCodec);
        int tdefL = 0;
        int l = 0;
        Codec tokenCodec = null;
        final int k = favoured.size() - 1;
        byte[] tokensEncoded;
        if (k < 256) {
            tdefL = 1;
            tokensEncoded = Codec.BYTE1.encode(tokens);
        }
        else {
            final BandAnalysisResults tokenResults = this.analyseBand("POPULATION", tokens, defaultCodec);
            tokenCodec = tokenResults.betterCodec;
            tokensEncoded = tokenResults.encodedBand;
            if (tokenCodec == null) {
                tokenCodec = defaultCodec;
            }
            l = ((BHSDCodec)tokenCodec).getL();
            final int h = ((BHSDCodec)tokenCodec).getH();
            final int s = ((BHSDCodec)tokenCodec).getS();
            final int b = ((BHSDCodec)tokenCodec).getB();
            final int d = ((BHSDCodec)tokenCodec).isDelta() ? 1 : 0;
            if (s == 0 && d == 0) {
                boolean canUseTDefL = true;
                if (b > 1) {
                    final BHSDCodec oneLowerB = new BHSDCodec(b - 1, h);
                    if (oneLowerB.largest() >= k) {
                        canUseTDefL = false;
                    }
                }
                if (canUseTDefL) {
                    switch (l) {
                        case 4: {
                            tdefL = 1;
                            break;
                        }
                        case 8: {
                            tdefL = 2;
                            break;
                        }
                        case 16: {
                            tdefL = 3;
                            break;
                        }
                        case 32: {
                            tdefL = 4;
                            break;
                        }
                        case 64: {
                            tdefL = 5;
                            break;
                        }
                        case 128: {
                            tdefL = 6;
                            break;
                        }
                        case 192: {
                            tdefL = 7;
                            break;
                        }
                        case 224: {
                            tdefL = 8;
                            break;
                        }
                        case 240: {
                            tdefL = 9;
                            break;
                        }
                        case 248: {
                            tdefL = 10;
                            break;
                        }
                        case 252: {
                            tdefL = 11;
                            break;
                        }
                    }
                }
            }
        }
        final byte[] favouredEncoded = favouredResults.encodedBand;
        final byte[] unfavouredEncoded = unfavouredResults.encodedBand;
        final Codec favouredCodec = favouredResults.betterCodec;
        final Codec unfavouredCodec = unfavouredResults.betterCodec;
        int specifier = 141 + ((favouredCodec == null) ? 1 : 0) + 4 * tdefL + ((unfavouredCodec == null) ? 2 : 0);
        final IntList extraBandMetadata = new IntList(3);
        if (favouredCodec != null) {
            final int[] specifiers = CodecEncoding.getSpecifier(favouredCodec, null);
            for (int m = 0; m < specifiers.length; ++m) {
                extraBandMetadata.add(specifiers[m]);
            }
        }
        if (tdefL == 0) {
            final int[] specifiers = CodecEncoding.getSpecifier(tokenCodec, null);
            for (int m = 0; m < specifiers.length; ++m) {
                extraBandMetadata.add(specifiers[m]);
            }
        }
        if (unfavouredCodec != null) {
            final int[] specifiers = CodecEncoding.getSpecifier(unfavouredCodec, null);
            for (int m = 0; m < specifiers.length; ++m) {
                extraBandMetadata.add(specifiers[m]);
            }
        }
        final int[] extraMetadata = extraBandMetadata.toArray();
        final byte[] extraMetadataEncoded = Codec.UNSIGNED5.encode(extraMetadata);
        if (defaultCodec.isSigned()) {
            specifier = -1 - specifier;
        }
        else {
            specifier += defaultCodec.getL();
        }
        final byte[] firstValueEncoded = defaultCodec.encode(new int[] { specifier });
        final int totalBandLength = firstValueEncoded.length + favouredEncoded.length + tokensEncoded.length + unfavouredEncoded.length;
        if (totalBandLength + extraMetadataEncoded.length < results.encodedBand.length) {
            results.saved += results.encodedBand.length - (totalBandLength + extraMetadataEncoded.length);
            final byte[] encodedBand = new byte[totalBandLength];
            System.arraycopy(firstValueEncoded, 0, encodedBand, 0, firstValueEncoded.length);
            System.arraycopy(favouredEncoded, 0, encodedBand, firstValueEncoded.length, favouredEncoded.length);
            System.arraycopy(tokensEncoded, 0, encodedBand, firstValueEncoded.length + favouredEncoded.length, tokensEncoded.length);
            System.arraycopy(unfavouredEncoded, 0, encodedBand, firstValueEncoded.length + favouredEncoded.length + tokensEncoded.length, unfavouredEncoded.length);
            results.encodedBand = encodedBand;
            results.extraMetadata = extraMetadata;
            if (l != 0) {
                results.betterCodec = new PopulationCodec(favouredCodec, l, unfavouredCodec);
            }
            else {
                results.betterCodec = new PopulationCodec(favouredCodec, tokenCodec, unfavouredCodec);
            }
        }
    }
    
    protected byte[] encodeFlags(final String name, final long[] flags, final BHSDCodec loCodec, final BHSDCodec hiCodec, final boolean haveHiFlags) throws Pack200Exception {
        if (!haveHiFlags) {
            final int[] loBits = new int[flags.length];
            for (int i = 0; i < flags.length; ++i) {
                loBits[i] = (int)flags[i];
            }
            return this.encodeBandInt(name, loBits, loCodec);
        }
        final int[] hiBits = new int[flags.length];
        final int[] loBits2 = new int[flags.length];
        for (int j = 0; j < flags.length; ++j) {
            final long l = flags[j];
            hiBits[j] = (int)(l >> 32);
            loBits2[j] = (int)l;
        }
        final byte[] hi = this.encodeBandInt(name, hiBits, hiCodec);
        final byte[] lo = this.encodeBandInt(name, loBits2, loCodec);
        final byte[] total = new byte[hi.length + lo.length];
        System.arraycopy(hi, 0, total, 0, hi.length);
        System.arraycopy(lo, 0, total, hi.length + 1, lo.length);
        return total;
    }
    
    protected int[] integerListToArray(final List integerList) {
        final int[] array = new int[integerList.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = integerList.get(i);
        }
        return array;
    }
    
    protected long[] longListToArray(final List longList) {
        final long[] array = new long[longList.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = longList.get(i);
        }
        return array;
    }
    
    protected int[] cpEntryListToArray(final List list) {
        final int[] array = new int[list.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = list.get(i).getIndex();
            if (array[i] < 0) {
                throw new RuntimeException("Index should be > 0");
            }
        }
        return array;
    }
    
    protected int[] cpEntryOrNullListToArray(final List theList) {
        final int[] array = new int[theList.size()];
        for (int j = 0; j < array.length; ++j) {
            final ConstantPoolEntry cpEntry = theList.get(j);
            array[j] = ((cpEntry == null) ? 0 : (cpEntry.getIndex() + 1));
            if (cpEntry != null && cpEntry.getIndex() < 0) {
                throw new RuntimeException("Index should be > 0");
            }
        }
        return array;
    }
    
    protected byte[] encodeFlags(final String name, final long[][] flags, final BHSDCodec loCodec, final BHSDCodec hiCodec, final boolean haveHiFlags) throws Pack200Exception {
        return this.encodeFlags(name, this.flatten(flags), loCodec, hiCodec, haveHiFlags);
    }
    
    private long[] flatten(final long[][] flags) {
        int totalSize = 0;
        for (int i = 0; i < flags.length; ++i) {
            totalSize += flags[i].length;
        }
        final long[] flatArray = new long[totalSize];
        int index = 0;
        for (int j = 0; j < flags.length; ++j) {
            for (int k = 0; k < flags[j].length; ++k) {
                flatArray[index] = flags[j][k];
                ++index;
            }
        }
        return flatArray;
    }
    
    static {
        effortThresholds = new int[] { 0, 0, 1000, 500, 100, 100, 100, 100, 100, 0 };
    }
    
    public class BandData
    {
        private final int[] band;
        private int smallest;
        private int largest;
        private int smallestDelta;
        private int largestDelta;
        private int deltaIsAscending;
        private int smallDeltaCount;
        private double averageAbsoluteDelta;
        private double averageAbsoluteValue;
        private Map distinctValues;
        
        public BandData(final int[] band) {
            this.smallest = Integer.MAX_VALUE;
            this.largest = Integer.MIN_VALUE;
            this.deltaIsAscending = 0;
            this.smallDeltaCount = 0;
            this.averageAbsoluteDelta = 0.0;
            this.averageAbsoluteValue = 0.0;
            this.band = band;
            final Integer one = 1;
            for (int i = 0; i < band.length; ++i) {
                if (band[i] < this.smallest) {
                    this.smallest = band[i];
                }
                if (band[i] > this.largest) {
                    this.largest = band[i];
                }
                if (i != 0) {
                    final int delta = band[i] - band[i - 1];
                    if (delta < this.smallestDelta) {
                        this.smallestDelta = delta;
                    }
                    if (delta > this.largestDelta) {
                        this.largestDelta = delta;
                    }
                    if (delta >= 0) {
                        ++this.deltaIsAscending;
                    }
                    this.averageAbsoluteDelta += Math.abs(delta) / (double)(band.length - 1);
                    if (Math.abs(delta) < 256) {
                        ++this.smallDeltaCount;
                    }
                }
                else {
                    this.smallestDelta = band[0];
                    this.largestDelta = band[0];
                }
                this.averageAbsoluteValue += Math.abs(band[i]) / (double)band.length;
                if (BandSet.this.effort > 3) {
                    if (this.distinctValues == null) {
                        this.distinctValues = new HashMap();
                    }
                    final Integer value = band[i];
                    Integer count = this.distinctValues.get(value);
                    if (count == null) {
                        count = one;
                    }
                    else {
                        ++count;
                    }
                    this.distinctValues.put(value, count);
                }
            }
        }
        
        public boolean mainlySmallDeltas() {
            return this.smallDeltaCount / (float)this.band.length > 0.7f;
        }
        
        public boolean wellCorrelated() {
            return this.averageAbsoluteDelta * 3.1 < this.averageAbsoluteValue;
        }
        
        public boolean mainlyPositiveDeltas() {
            return this.deltaIsAscending / (float)this.band.length > 0.95f;
        }
        
        public boolean anyNegatives() {
            return this.smallest < 0;
        }
        
        public int numDistinctValues() {
            if (this.distinctValues == null) {
                return this.band.length;
            }
            return this.distinctValues.size();
        }
    }
    
    public class BandAnalysisResults
    {
        private int numCodecsTried;
        private int saved;
        private int[] extraMetadata;
        private byte[] encodedBand;
        private Codec betterCodec;
        
        public BandAnalysisResults() {
            this.numCodecsTried = 0;
            this.saved = 0;
        }
    }
}
