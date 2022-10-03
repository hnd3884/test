package org.apache.commons.compress.harmony.pack200;

import java.util.Arrays;
import java.util.HashMap;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.util.Map;

public class CodecEncoding
{
    private static final BHSDCodec[] canonicalCodec;
    private static Map canonicalCodecsToSpecifiers;
    
    public static Codec getCodec(final int value, final InputStream in, final Codec defaultCodec) throws IOException, Pack200Exception {
        if (CodecEncoding.canonicalCodec.length != 116) {
            throw new Error("Canonical encodings have been incorrectly modified");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Encoding cannot be less than zero");
        }
        if (value == 0) {
            return defaultCodec;
        }
        if (value <= 115) {
            return CodecEncoding.canonicalCodec[value];
        }
        if (value == 116) {
            int code = in.read();
            if (code == -1) {
                throw new EOFException("End of buffer read whilst trying to decode codec");
            }
            final int d = code & 0x1;
            final int s = code >> 1 & 0x3;
            final int b = (code >> 3 & 0x7) + 1;
            code = in.read();
            if (code == -1) {
                throw new EOFException("End of buffer read whilst trying to decode codec");
            }
            final int h = code + 1;
            return new BHSDCodec(b, h, s, d);
        }
        else if (value >= 117 && value <= 140) {
            final int offset = value - 117;
            final int kx = offset & 0x3;
            final boolean kbflag = (offset >> 2 & 0x1) == 0x1;
            final boolean adef = (offset >> 3 & 0x1) == 0x1;
            final boolean bdef = (offset >> 4 & 0x1) == 0x1;
            if (adef && bdef) {
                throw new Pack200Exception("ADef and BDef should never both be true");
            }
            final int kb = kbflag ? in.read() : 3;
            final int k = (kb + 1) * (int)Math.pow(16.0, kx);
            Codec aCodec;
            if (adef) {
                aCodec = defaultCodec;
            }
            else {
                aCodec = getCodec(in.read(), in, defaultCodec);
            }
            Codec bCodec;
            if (bdef) {
                bCodec = defaultCodec;
            }
            else {
                bCodec = getCodec(in.read(), in, defaultCodec);
            }
            return new RunCodec(k, aCodec, bCodec);
        }
        else {
            if (value < 141 || value > 188) {
                throw new Pack200Exception("Invalid codec encoding byte (" + value + ") found");
            }
            final int offset = value - 141;
            final boolean fdef = (offset & 0x1) == 0x1;
            final boolean udef = (offset >> 1 & 0x1) == 0x1;
            final int tdefl = offset >> 2;
            final boolean tdef = tdefl != 0;
            final int[] tdefToL = { 0, 4, 8, 16, 32, 64, 128, 192, 224, 240, 248, 252 };
            final int l = tdefToL[tdefl];
            if (tdef) {
                final Codec fCodec = fdef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
                final Codec uCodec = udef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
                return new PopulationCodec(fCodec, l, uCodec);
            }
            final Codec fCodec = fdef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
            final Codec tCodec = getCodec(in.read(), in, defaultCodec);
            final Codec uCodec2 = udef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
            return new PopulationCodec(fCodec, tCodec, uCodec2);
        }
    }
    
    public static int getSpecifierForDefaultCodec(final BHSDCodec defaultCodec) {
        return getSpecifier(defaultCodec, null)[0];
    }
    
    public static int[] getSpecifier(final Codec codec, final Codec defaultForBand) {
        if (CodecEncoding.canonicalCodecsToSpecifiers == null) {
            final HashMap reverseMap = new HashMap(CodecEncoding.canonicalCodec.length);
            for (int i = 0; i < CodecEncoding.canonicalCodec.length; ++i) {
                reverseMap.put(CodecEncoding.canonicalCodec[i], i);
            }
            CodecEncoding.canonicalCodecsToSpecifiers = reverseMap;
        }
        if (CodecEncoding.canonicalCodecsToSpecifiers.containsKey(codec)) {
            return new int[] { CodecEncoding.canonicalCodecsToSpecifiers.get(codec) };
        }
        if (codec instanceof BHSDCodec) {
            final BHSDCodec bhsdCodec = (BHSDCodec)codec;
            final int[] specifiers = { 116, (bhsdCodec.isDelta() ? 1 : 0) + 2 * bhsdCodec.getS() + 8 * (bhsdCodec.getB() - 1), bhsdCodec.getH() - 1 };
            return specifiers;
        }
        if (codec instanceof RunCodec) {
            final RunCodec runCodec = (RunCodec)codec;
            final int k = runCodec.getK();
            int kb;
            int kx;
            if (k <= 256) {
                kb = 0;
                kx = k - 1;
            }
            else if (k <= 4096) {
                kb = 1;
                kx = k / 16 - 1;
            }
            else if (k <= 65536) {
                kb = 2;
                kx = k / 256 - 1;
            }
            else {
                kb = 3;
                kx = k / 4096 - 1;
            }
            final Codec aCodec = runCodec.getACodec();
            final Codec bCodec = runCodec.getBCodec();
            int abDef = 0;
            if (aCodec.equals(defaultForBand)) {
                abDef = 1;
            }
            else if (bCodec.equals(defaultForBand)) {
                abDef = 2;
            }
            final int first = 117 + kb + ((kx == 3) ? 0 : 4) + 8 * abDef;
            final int[] aSpecifier = (abDef == 1) ? new int[0] : getSpecifier(aCodec, defaultForBand);
            final int[] bSpecifier = (abDef == 2) ? new int[0] : getSpecifier(bCodec, defaultForBand);
            final int[] specifier = new int[1 + ((kx != 3) ? 1 : 0) + aSpecifier.length + bSpecifier.length];
            specifier[0] = first;
            int index = 1;
            if (kx != 3) {
                specifier[1] = kx;
                ++index;
            }
            for (int j = 0; j < aSpecifier.length; ++j) {
                specifier[index] = aSpecifier[j];
                ++index;
            }
            for (int j = 0; j < bSpecifier.length; ++j) {
                specifier[index] = bSpecifier[j];
                ++index;
            }
            return specifier;
        }
        if (codec instanceof PopulationCodec) {
            final PopulationCodec populationCodec = (PopulationCodec)codec;
            final Codec tokenCodec = populationCodec.getTokenCodec();
            final Codec favouredCodec = populationCodec.getFavouredCodec();
            final Codec unfavouredCodec = populationCodec.getUnfavouredCodec();
            final int fDef = favouredCodec.equals(defaultForBand) ? 1 : 0;
            final int uDef = unfavouredCodec.equals(defaultForBand) ? 1 : 0;
            int tDefL = 0;
            final int[] favoured = populationCodec.getFavoured();
            if (favoured != null) {
                final int l = favoured.length;
                if (tokenCodec == Codec.BYTE1) {
                    tDefL = 1;
                }
                else if (tokenCodec instanceof BHSDCodec) {
                    final BHSDCodec tokenBHSD = (BHSDCodec)tokenCodec;
                    if (tokenBHSD.getS() == 0) {
                        final int[] possibleLValues = { 4, 8, 16, 32, 64, 128, 192, 224, 240, 248, 252 };
                        final int m = 256 - tokenBHSD.getH();
                        int index2 = Arrays.binarySearch(possibleLValues, m);
                        if (index2 != -1) {
                            tDefL = index2++;
                        }
                    }
                }
            }
            final int first2 = 141 + fDef + 2 * uDef + 4 * tDefL;
            final int[] favouredSpecifier = (fDef == 1) ? new int[0] : getSpecifier(favouredCodec, defaultForBand);
            final int[] tokenSpecifier = (tDefL != 0) ? new int[0] : getSpecifier(tokenCodec, defaultForBand);
            final int[] unfavouredSpecifier = (uDef == 1) ? new int[0] : getSpecifier(unfavouredCodec, defaultForBand);
            final int[] specifier2 = new int[1 + favouredSpecifier.length + unfavouredSpecifier.length + tokenSpecifier.length];
            specifier2[0] = first2;
            int index3 = 1;
            for (int i2 = 0; i2 < favouredSpecifier.length; ++i2) {
                specifier2[index3] = favouredSpecifier[i2];
                ++index3;
            }
            for (int i2 = 0; i2 < tokenSpecifier.length; ++i2) {
                specifier2[index3] = tokenSpecifier[i2];
                ++index3;
            }
            for (int i2 = 0; i2 < unfavouredSpecifier.length; ++i2) {
                specifier2[index3] = unfavouredSpecifier[i2];
                ++index3;
            }
            return specifier2;
        }
        return null;
    }
    
    public static BHSDCodec getCanonicalCodec(final int i) {
        return CodecEncoding.canonicalCodec[i];
    }
    
    static {
        canonicalCodec = new BHSDCodec[] { null, new BHSDCodec(1, 256), new BHSDCodec(1, 256, 1), new BHSDCodec(1, 256, 0, 1), new BHSDCodec(1, 256, 1, 1), new BHSDCodec(2, 256), new BHSDCodec(2, 256, 1), new BHSDCodec(2, 256, 0, 1), new BHSDCodec(2, 256, 1, 1), new BHSDCodec(3, 256), new BHSDCodec(3, 256, 1), new BHSDCodec(3, 256, 0, 1), new BHSDCodec(3, 256, 1, 1), new BHSDCodec(4, 256), new BHSDCodec(4, 256, 1), new BHSDCodec(4, 256, 0, 1), new BHSDCodec(4, 256, 1, 1), new BHSDCodec(5, 4), new BHSDCodec(5, 4, 1), new BHSDCodec(5, 4, 2), new BHSDCodec(5, 16), new BHSDCodec(5, 16, 1), new BHSDCodec(5, 16, 2), new BHSDCodec(5, 32), new BHSDCodec(5, 32, 1), new BHSDCodec(5, 32, 2), new BHSDCodec(5, 64), new BHSDCodec(5, 64, 1), new BHSDCodec(5, 64, 2), new BHSDCodec(5, 128), new BHSDCodec(5, 128, 1), new BHSDCodec(5, 128, 2), new BHSDCodec(5, 4, 0, 1), new BHSDCodec(5, 4, 1, 1), new BHSDCodec(5, 4, 2, 1), new BHSDCodec(5, 16, 0, 1), new BHSDCodec(5, 16, 1, 1), new BHSDCodec(5, 16, 2, 1), new BHSDCodec(5, 32, 0, 1), new BHSDCodec(5, 32, 1, 1), new BHSDCodec(5, 32, 2, 1), new BHSDCodec(5, 64, 0, 1), new BHSDCodec(5, 64, 1, 1), new BHSDCodec(5, 64, 2, 1), new BHSDCodec(5, 128, 0, 1), new BHSDCodec(5, 128, 1, 1), new BHSDCodec(5, 128, 2, 1), new BHSDCodec(2, 192), new BHSDCodec(2, 224), new BHSDCodec(2, 240), new BHSDCodec(2, 248), new BHSDCodec(2, 252), new BHSDCodec(2, 8, 0, 1), new BHSDCodec(2, 8, 1, 1), new BHSDCodec(2, 16, 0, 1), new BHSDCodec(2, 16, 1, 1), new BHSDCodec(2, 32, 0, 1), new BHSDCodec(2, 32, 1, 1), new BHSDCodec(2, 64, 0, 1), new BHSDCodec(2, 64, 1, 1), new BHSDCodec(2, 128, 0, 1), new BHSDCodec(2, 128, 1, 1), new BHSDCodec(2, 192, 0, 1), new BHSDCodec(2, 192, 1, 1), new BHSDCodec(2, 224, 0, 1), new BHSDCodec(2, 224, 1, 1), new BHSDCodec(2, 240, 0, 1), new BHSDCodec(2, 240, 1, 1), new BHSDCodec(2, 248, 0, 1), new BHSDCodec(2, 248, 1, 1), new BHSDCodec(3, 192), new BHSDCodec(3, 224), new BHSDCodec(3, 240), new BHSDCodec(3, 248), new BHSDCodec(3, 252), new BHSDCodec(3, 8, 0, 1), new BHSDCodec(3, 8, 1, 1), new BHSDCodec(3, 16, 0, 1), new BHSDCodec(3, 16, 1, 1), new BHSDCodec(3, 32, 0, 1), new BHSDCodec(3, 32, 1, 1), new BHSDCodec(3, 64, 0, 1), new BHSDCodec(3, 64, 1, 1), new BHSDCodec(3, 128, 0, 1), new BHSDCodec(3, 128, 1, 1), new BHSDCodec(3, 192, 0, 1), new BHSDCodec(3, 192, 1, 1), new BHSDCodec(3, 224, 0, 1), new BHSDCodec(3, 224, 1, 1), new BHSDCodec(3, 240, 0, 1), new BHSDCodec(3, 240, 1, 1), new BHSDCodec(3, 248, 0, 1), new BHSDCodec(3, 248, 1, 1), new BHSDCodec(4, 192), new BHSDCodec(4, 224), new BHSDCodec(4, 240), new BHSDCodec(4, 248), new BHSDCodec(4, 252), new BHSDCodec(4, 8, 0, 1), new BHSDCodec(4, 8, 1, 1), new BHSDCodec(4, 16, 0, 1), new BHSDCodec(4, 16, 1, 1), new BHSDCodec(4, 32, 0, 1), new BHSDCodec(4, 32, 1, 1), new BHSDCodec(4, 64, 0, 1), new BHSDCodec(4, 64, 1, 1), new BHSDCodec(4, 128, 0, 1), new BHSDCodec(4, 128, 1, 1), new BHSDCodec(4, 192, 0, 1), new BHSDCodec(4, 192, 1, 1), new BHSDCodec(4, 224, 0, 1), new BHSDCodec(4, 224, 1, 1), new BHSDCodec(4, 240, 0, 1), new BHSDCodec(4, 240, 1, 1), new BHSDCodec(4, 248, 0, 1), new BHSDCodec(4, 248, 1, 1) };
    }
}
