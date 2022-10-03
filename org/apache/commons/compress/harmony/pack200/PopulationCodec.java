package org.apache.commons.compress.harmony.pack200;

import java.io.IOException;
import java.io.InputStream;

public class PopulationCodec extends Codec
{
    private final Codec favouredCodec;
    private Codec tokenCodec;
    private final Codec unfavouredCodec;
    private int l;
    private int[] favoured;
    
    public PopulationCodec(final Codec favouredCodec, final Codec tokenCodec, final Codec unvafouredCodec) {
        this.favouredCodec = favouredCodec;
        this.tokenCodec = tokenCodec;
        this.unfavouredCodec = unvafouredCodec;
    }
    
    public PopulationCodec(final Codec favouredCodec, final int l, final Codec unfavouredCodec) {
        if (l >= 256 || l <= 0) {
            throw new IllegalArgumentException("L must be between 1..255");
        }
        this.favouredCodec = favouredCodec;
        this.l = l;
        this.unfavouredCodec = unfavouredCodec;
    }
    
    @Override
    public int decode(final InputStream in) throws IOException, Pack200Exception {
        throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
    }
    
    @Override
    public int decode(final InputStream in, final long last) throws IOException, Pack200Exception {
        throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
    }
    
    @Override
    public int[] decodeInts(final int n, final InputStream in) throws IOException, Pack200Exception {
        this.lastBandLength = 0;
        this.favoured = new int[n];
        int smallest = Integer.MAX_VALUE;
        int last = 0;
        int value = 0;
        int k = -1;
        while (true) {
            value = this.favouredCodec.decode(in, last);
            if (k > -1 && (value == smallest || value == last)) {
                break;
            }
            this.favoured[++k] = value;
            final int absoluteSmallest = Math.abs(smallest);
            final int absoluteValue = Math.abs(value);
            if (absoluteSmallest > absoluteValue) {
                smallest = value;
            }
            else if (absoluteSmallest == absoluteValue) {
                smallest = absoluteSmallest;
            }
            last = value;
        }
        this.lastBandLength += k;
        if (this.tokenCodec == null) {
            if (k < 256) {
                this.tokenCodec = Codec.BYTE1;
            }
            else {
                int b = 1;
                BHSDCodec codec = null;
                while (++b < 5) {
                    codec = new BHSDCodec(b, 256 - this.l, 0);
                    if (codec.encodes(k)) {
                        this.tokenCodec = codec;
                        break;
                    }
                }
                if (this.tokenCodec == null) {
                    throw new Pack200Exception("Cannot calculate token codec from " + k + " and " + this.l);
                }
            }
        }
        this.lastBandLength += n;
        final int[] result = this.tokenCodec.decodeInts(n, in);
        last = 0;
        for (int i = 0; i < n; ++i) {
            final int index = result[i];
            if (index == 0) {
                ++this.lastBandLength;
                last = (result[i] = this.unfavouredCodec.decode(in, last));
            }
            else {
                result[i] = this.favoured[index - 1];
            }
        }
        return result;
    }
    
    public int[] getFavoured() {
        return this.favoured;
    }
    
    public Codec getFavouredCodec() {
        return this.favouredCodec;
    }
    
    public Codec getUnfavouredCodec() {
        return this.unfavouredCodec;
    }
    
    @Override
    public byte[] encode(final int value, final int last) throws Pack200Exception {
        throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
    }
    
    @Override
    public byte[] encode(final int value) throws Pack200Exception {
        throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
    }
    
    public byte[] encode(final int[] favoured, final int[] tokens, final int[] unfavoured) throws Pack200Exception {
        final int[] favoured2 = new int[favoured.length + 1];
        System.arraycopy(favoured, 0, favoured2, 0, favoured.length);
        favoured2[favoured2.length - 1] = favoured[favoured.length - 1];
        final byte[] favouredEncoded = this.favouredCodec.encode(favoured2);
        final byte[] tokensEncoded = this.tokenCodec.encode(tokens);
        final byte[] unfavouredEncoded = this.unfavouredCodec.encode(unfavoured);
        final byte[] band = new byte[favouredEncoded.length + tokensEncoded.length + unfavouredEncoded.length];
        System.arraycopy(favouredEncoded, 0, band, 0, favouredEncoded.length);
        System.arraycopy(tokensEncoded, 0, band, favouredEncoded.length, tokensEncoded.length);
        System.arraycopy(unfavouredEncoded, 0, band, favouredEncoded.length + tokensEncoded.length, unfavouredEncoded.length);
        return band;
    }
    
    public Codec getTokenCodec() {
        return this.tokenCodec;
    }
}
