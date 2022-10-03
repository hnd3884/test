package org.apache.commons.compress.harmony.unpack200;

import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPFieldRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPString;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPLong;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPFloat;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPDouble;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInteger;
import java.util.Arrays;
import org.apache.commons.compress.harmony.pack200.PopulationCodec;
import org.apache.commons.compress.harmony.pack200.Codec;
import org.apache.commons.compress.harmony.pack200.CodecEncoding;
import org.apache.commons.compress.harmony.pack200.BHSDCodec;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.io.IOException;
import java.io.InputStream;

public abstract class BandSet
{
    protected Segment segment;
    protected SegmentHeader header;
    
    public abstract void read(final InputStream p0) throws IOException, Pack200Exception;
    
    public abstract void unpack() throws IOException, Pack200Exception;
    
    public void unpack(final InputStream in) throws IOException, Pack200Exception {
        this.read(in);
        this.unpack();
    }
    
    public BandSet(final Segment segment) {
        this.segment = segment;
        this.header = segment.getSegmentHeader();
    }
    
    public int[] decodeBandInt(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        Codec codecUsed = codec;
        if (codec.getB() == 1 || count == 0) {
            return codec.decodeInts(count, in);
        }
        final int[] getFirst = codec.decodeInts(1, in);
        if (getFirst.length == 0) {
            return getFirst;
        }
        final int first = getFirst[0];
        int[] band;
        if (codec.isSigned() && first >= -256 && first <= -1) {
            codecUsed = CodecEncoding.getCodec(-1 - first, this.header.getBandHeadersInputStream(), codec);
            band = codecUsed.decodeInts(count, in);
        }
        else if (!codec.isSigned() && first >= codec.getL() && first <= codec.getL() + 255) {
            codecUsed = CodecEncoding.getCodec(first - codec.getL(), this.header.getBandHeadersInputStream(), codec);
            band = codecUsed.decodeInts(count, in);
        }
        else {
            band = codec.decodeInts(count - 1, in, first);
        }
        if (codecUsed instanceof PopulationCodec) {
            final PopulationCodec popCodec = (PopulationCodec)codecUsed;
            final int[] favoured = popCodec.getFavoured().clone();
            Arrays.sort(favoured);
            for (int i = 0; i < band.length; ++i) {
                final boolean favouredValue = Arrays.binarySearch(favoured, band[i]) > -1;
                final Codec theCodec = favouredValue ? popCodec.getFavouredCodec() : popCodec.getUnfavouredCodec();
                if (theCodec instanceof BHSDCodec && ((BHSDCodec)theCodec).isDelta()) {
                    final BHSDCodec bhsd = (BHSDCodec)theCodec;
                    final long cardinality = bhsd.cardinality();
                    while (band[i] > bhsd.largest()) {
                        final int[] array = band;
                        final int n = i;
                        array[n] -= (int)cardinality;
                    }
                    while (band[i] < bhsd.smallest()) {
                        final int[] array2 = band;
                        final int n2 = i;
                        array2[n2] += (int)cardinality;
                    }
                }
            }
        }
        return band;
    }
    
    public int[][] decodeBandInt(final String name, final InputStream in, final BHSDCodec defaultCodec, final int[] counts) throws IOException, Pack200Exception {
        final int[][] result = new int[counts.length][];
        int totalCount = 0;
        for (int i = 0; i < counts.length; ++i) {
            totalCount += counts[i];
        }
        final int[] twoDResult = this.decodeBandInt(name, in, defaultCodec, totalCount);
        int index = 0;
        for (int j = 0; j < result.length; ++j) {
            result[j] = new int[counts[j]];
            for (int k = 0; k < result[j].length; ++k) {
                result[j][k] = twoDResult[index];
                ++index;
            }
        }
        return result;
    }
    
    public long[] parseFlags(final String name, final InputStream in, final int count, final BHSDCodec codec, final boolean hasHi) throws IOException, Pack200Exception {
        return this.parseFlags(name, in, new int[] { count }, hasHi ? codec : null, codec)[0];
    }
    
    public long[][] parseFlags(final String name, final InputStream in, final int[] counts, final BHSDCodec codec, final boolean hasHi) throws IOException, Pack200Exception {
        return this.parseFlags(name, in, counts, hasHi ? codec : null, codec);
    }
    
    public long[] parseFlags(final String name, final InputStream in, final int count, final BHSDCodec hiCodec, final BHSDCodec loCodec) throws IOException, Pack200Exception {
        return this.parseFlags(name, in, new int[] { count }, hiCodec, loCodec)[0];
    }
    
    public long[][] parseFlags(final String name, final InputStream in, final int[] counts, final BHSDCodec hiCodec, final BHSDCodec loCodec) throws IOException, Pack200Exception {
        final int count = counts.length;
        if (count == 0) {
            return new long[][] { new long[0] };
        }
        int sum = 0;
        final long[][] result = new long[count][];
        for (int i = 0; i < count; ++i) {
            result[i] = new long[counts[i]];
            sum += counts[i];
        }
        int[] hi = null;
        int[] lo;
        if (hiCodec != null) {
            hi = this.decodeBandInt(name, in, hiCodec, sum);
            lo = this.decodeBandInt(name, in, loCodec, sum);
        }
        else {
            lo = this.decodeBandInt(name, in, loCodec, sum);
        }
        int index = 0;
        for (int j = 0; j < result.length; ++j) {
            for (int k = 0; k < result[j].length; ++k) {
                if (hi != null) {
                    result[j][k] = ((long)hi[index] << 32 | ((long)lo[index] & 0xFFFFFFFFL));
                }
                else {
                    result[j][k] = lo[index];
                }
                ++index;
            }
        }
        return result;
    }
    
    public String[] parseReferences(final String name, final InputStream in, final BHSDCodec codec, final int count, final String[] reference) throws IOException, Pack200Exception {
        return this.parseReferences(name, in, codec, new int[] { count }, reference)[0];
    }
    
    public String[][] parseReferences(final String name, final InputStream in, final BHSDCodec codec, final int[] counts, final String[] reference) throws IOException, Pack200Exception {
        final int count = counts.length;
        if (count == 0) {
            return new String[][] { new String[0] };
        }
        final String[][] result = new String[count][];
        int sum = 0;
        for (int i = 0; i < count; ++i) {
            result[i] = new String[counts[i]];
            sum += counts[i];
        }
        final String[] result2 = new String[sum];
        final int[] indices = this.decodeBandInt(name, in, codec, sum);
        for (int i2 = 0; i2 < sum; ++i2) {
            final int index = indices[i2];
            if (index < 0 || index >= reference.length) {
                throw new Pack200Exception("Something has gone wrong during parsing references, index = " + index + ", array size = " + reference.length);
            }
            result2[i2] = reference[index];
        }
        final String[] refs = result2;
        int pos = 0;
        for (int j = 0; j < count; ++j) {
            final int num = counts[j];
            System.arraycopy(refs, pos, result[j] = new String[num], 0, num);
            pos += num;
        }
        return result;
    }
    
    public CPInteger[] parseCPIntReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final int[] reference = this.segment.getCpBands().getCpInt();
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPInteger[] result = new CPInteger[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            if (index < 0 || index >= reference.length) {
                throw new Pack200Exception("Something has gone wrong during parsing references, index = " + index + ", array size = " + reference.length);
            }
            result[i1] = this.segment.getCpBands().cpIntegerValue(index);
        }
        return result;
    }
    
    public CPDouble[] parseCPDoubleReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPDouble[] result = new CPDouble[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = this.segment.getCpBands().cpDoubleValue(index);
        }
        return result;
    }
    
    public CPFloat[] parseCPFloatReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPFloat[] result = new CPFloat[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = this.segment.getCpBands().cpFloatValue(index);
        }
        return result;
    }
    
    public CPLong[] parseCPLongReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final long[] reference = this.segment.getCpBands().getCpLong();
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPLong[] result = new CPLong[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            if (index < 0 || index >= reference.length) {
                throw new Pack200Exception("Something has gone wrong during parsing references, index = " + index + ", array size = " + reference.length);
            }
            result[i1] = this.segment.getCpBands().cpLongValue(index);
        }
        return result;
    }
    
    public CPUTF8[] parseCPUTF8References(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPUTF8[] result = new CPUTF8[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = this.segment.getCpBands().cpUTF8Value(index);
        }
        return result;
    }
    
    public CPUTF8[][] parseCPUTF8References(final String name, final InputStream in, final BHSDCodec codec, final int[] counts) throws IOException, Pack200Exception {
        final CPUTF8[][] result = new CPUTF8[counts.length][];
        int sum = 0;
        for (int i = 0; i < counts.length; ++i) {
            result[i] = new CPUTF8[counts[i]];
            sum += counts[i];
        }
        final CPUTF8[] result2 = new CPUTF8[sum];
        final int[] indices = this.decodeBandInt(name, in, codec, sum);
        for (int i2 = 0; i2 < sum; ++i2) {
            final int index = indices[i2];
            result2[i2] = this.segment.getCpBands().cpUTF8Value(index);
        }
        final CPUTF8[] refs = result2;
        int pos = 0;
        for (int j = 0; j < counts.length; ++j) {
            final int num = counts[j];
            System.arraycopy(refs, pos, result[j] = new CPUTF8[num], 0, num);
            pos += num;
        }
        return result;
    }
    
    public CPString[] parseCPStringReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPString[] result = new CPString[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = this.segment.getCpBands().cpStringValue(index);
        }
        return result;
    }
    
    public CPInterfaceMethodRef[] parseCPInterfaceMethodRefReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final CpBands cpBands = this.segment.getCpBands();
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPInterfaceMethodRef[] result = new CPInterfaceMethodRef[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = cpBands.cpIMethodValue(index);
        }
        return result;
    }
    
    public CPMethodRef[] parseCPMethodRefReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final CpBands cpBands = this.segment.getCpBands();
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPMethodRef[] result = new CPMethodRef[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = cpBands.cpMethodValue(index);
        }
        return result;
    }
    
    public CPFieldRef[] parseCPFieldRefReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final CpBands cpBands = this.segment.getCpBands();
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPFieldRef[] result = new CPFieldRef[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = cpBands.cpFieldValue(index);
        }
        return result;
    }
    
    public CPNameAndType[] parseCPDescriptorReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final CpBands cpBands = this.segment.getCpBands();
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPNameAndType[] result = new CPNameAndType[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = cpBands.cpNameAndTypeValue(index);
        }
        return result;
    }
    
    public CPUTF8[] parseCPSignatureReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPUTF8[] result = new CPUTF8[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = this.segment.getCpBands().cpSignatureValue(index);
        }
        return result;
    }
    
    protected CPUTF8[][] parseCPSignatureReferences(final String name, final InputStream in, final BHSDCodec codec, final int[] counts) throws IOException, Pack200Exception {
        final CPUTF8[][] result = new CPUTF8[counts.length][];
        int sum = 0;
        for (int i = 0; i < counts.length; ++i) {
            result[i] = new CPUTF8[counts[i]];
            sum += counts[i];
        }
        final CPUTF8[] result2 = new CPUTF8[sum];
        final int[] indices = this.decodeBandInt(name, in, codec, sum);
        for (int i2 = 0; i2 < sum; ++i2) {
            final int index = indices[i2];
            result2[i2] = this.segment.getCpBands().cpSignatureValue(index);
        }
        final CPUTF8[] refs = result2;
        int pos = 0;
        for (int j = 0; j < counts.length; ++j) {
            final int num = counts[j];
            System.arraycopy(refs, pos, result[j] = new CPUTF8[num], 0, num);
            pos += num;
        }
        return result;
    }
    
    public CPClass[] parseCPClassReferences(final String name, final InputStream in, final BHSDCodec codec, final int count) throws IOException, Pack200Exception {
        final int[] indices = this.decodeBandInt(name, in, codec, count);
        final CPClass[] result = new CPClass[indices.length];
        for (int i1 = 0; i1 < count; ++i1) {
            final int index = indices[i1];
            result[i1] = this.segment.getCpBands().cpClassValue(index);
        }
        return result;
    }
    
    protected String[] getReferences(final int[] ints, final String[] reference) {
        final String[] result = new String[ints.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = reference[ints[i]];
        }
        return result;
    }
    
    protected String[][] getReferences(final int[][] ints, final String[] reference) {
        final String[][] result = new String[ints.length][];
        for (int i = 0; i < result.length; ++i) {
            result[i] = new String[ints[i].length];
            for (int j = 0; j < result[i].length; ++j) {
                result[i][j] = reference[ints[i][j]];
            }
        }
        return result;
    }
}
