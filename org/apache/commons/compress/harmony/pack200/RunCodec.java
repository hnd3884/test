package org.apache.commons.compress.harmony.pack200;

import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;

public class RunCodec extends Codec
{
    private int k;
    private final Codec aCodec;
    private final Codec bCodec;
    private int last;
    
    public RunCodec(final int k, final Codec aCodec, final Codec bCodec) throws Pack200Exception {
        if (k <= 0) {
            throw new Pack200Exception("Cannot have a RunCodec for a negative number of numbers");
        }
        if (aCodec == null || bCodec == null) {
            throw new Pack200Exception("Must supply both codecs for a RunCodec");
        }
        this.k = k;
        this.aCodec = aCodec;
        this.bCodec = bCodec;
    }
    
    @Override
    public int decode(final InputStream in) throws IOException, Pack200Exception {
        return this.decode(in, this.last);
    }
    
    @Override
    public int decode(final InputStream in, final long last) throws IOException, Pack200Exception {
        final int k = this.k - 1;
        this.k = k;
        if (k >= 0) {
            final int value = this.aCodec.decode(in, this.last);
            this.last = ((this.k == 0) ? 0 : value);
            return this.normalise(value, this.aCodec);
        }
        this.last = this.bCodec.decode(in, this.last);
        return this.normalise(this.last, this.bCodec);
    }
    
    private int normalise(int value, final Codec codecUsed) {
        if (codecUsed instanceof BHSDCodec) {
            final BHSDCodec bhsd = (BHSDCodec)codecUsed;
            if (bhsd.isDelta()) {
                long cardinality;
                for (cardinality = bhsd.cardinality(); value > bhsd.largest(); value -= (int)cardinality) {}
                while (value < bhsd.smallest()) {
                    value += (int)cardinality;
                }
            }
        }
        return value;
    }
    
    @Override
    public int[] decodeInts(final int n, final InputStream in) throws IOException, Pack200Exception {
        final int[] band = new int[n];
        final int[] aValues = this.aCodec.decodeInts(this.k, in);
        this.normalise(aValues, this.aCodec);
        final int[] bValues = this.bCodec.decodeInts(n - this.k, in);
        this.normalise(bValues, this.bCodec);
        System.arraycopy(aValues, 0, band, 0, this.k);
        System.arraycopy(bValues, 0, band, this.k, n - this.k);
        this.lastBandLength = this.aCodec.lastBandLength + this.bCodec.lastBandLength;
        return band;
    }
    
    private void normalise(final int[] band, final Codec codecUsed) {
        if (codecUsed instanceof BHSDCodec) {
            final BHSDCodec bhsd = (BHSDCodec)codecUsed;
            if (bhsd.isDelta()) {
                final long cardinality = bhsd.cardinality();
                for (int i = 0; i < band.length; ++i) {
                    while (band[i] > bhsd.largest()) {
                        final int n = i;
                        band[n] -= (int)cardinality;
                    }
                    while (band[i] < bhsd.smallest()) {
                        final int n2 = i;
                        band[n2] += (int)cardinality;
                    }
                }
            }
        }
        else if (codecUsed instanceof PopulationCodec) {
            final PopulationCodec popCodec = (PopulationCodec)codecUsed;
            final int[] favoured = popCodec.getFavoured().clone();
            Arrays.sort(favoured);
            for (int j = 0; j < band.length; ++j) {
                final boolean favouredValue = Arrays.binarySearch(favoured, band[j]) > -1;
                final Codec theCodec = favouredValue ? popCodec.getFavouredCodec() : popCodec.getUnfavouredCodec();
                if (theCodec instanceof BHSDCodec) {
                    final BHSDCodec bhsd2 = (BHSDCodec)theCodec;
                    if (bhsd2.isDelta()) {
                        final long cardinality2 = bhsd2.cardinality();
                        while (band[j] > bhsd2.largest()) {
                            final int n3 = j;
                            band[n3] -= (int)cardinality2;
                        }
                        while (band[j] < bhsd2.smallest()) {
                            final int n4 = j;
                            band[n4] += (int)cardinality2;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return "RunCodec[k=" + this.k + ";aCodec=" + this.aCodec + "bCodec=" + this.bCodec + "]";
    }
    
    @Override
    public byte[] encode(final int value, final int last) throws Pack200Exception {
        throw new Pack200Exception("Must encode entire band at once with a RunCodec");
    }
    
    @Override
    public byte[] encode(final int value) throws Pack200Exception {
        throw new Pack200Exception("Must encode entire band at once with a RunCodec");
    }
    
    public int getK() {
        return this.k;
    }
    
    public Codec getACodec() {
        return this.aCodec;
    }
    
    public Codec getBCodec() {
        return this.bCodec;
    }
}
