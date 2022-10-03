package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.HashSet;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

class PopulationCoding implements CodingMethod
{
    Histogram vHist;
    int[] fValues;
    int fVlen;
    long[] symtab;
    CodingMethod favoredCoding;
    CodingMethod tokenCoding;
    CodingMethod unfavoredCoding;
    int L;
    static final int[] LValuesCoded;
    
    PopulationCoding() {
        this.L = -1;
    }
    
    public void setFavoredValues(final int[] fValues, final int fVlen) {
        assert fValues[0] == 0;
        assert this.fValues == null;
        this.fValues = fValues;
        this.fVlen = fVlen;
        if (this.L >= 0) {
            this.setL(this.L);
        }
    }
    
    public void setFavoredValues(final int[] array) {
        this.setFavoredValues(array, array.length - 1);
    }
    
    public void setHistogram(final Histogram vHist) {
        this.vHist = vHist;
    }
    
    public void setL(final int l) {
        this.L = l;
        if (l >= 0 && this.fValues != null && this.tokenCoding == null) {
            this.tokenCoding = fitTokenCoding(this.fVlen, l);
            assert this.tokenCoding != null;
        }
    }
    
    public static Coding fitTokenCoding(final int n, final int l) {
        if (n < 256) {
            return BandStructure.BYTE1;
        }
        final Coding setL = BandStructure.UNSIGNED5.setL(l);
        if (!setL.canRepresentUnsigned(n)) {
            return null;
        }
        Coding coding = setL;
        Coding setB = setL;
        while (true) {
            setB = setB.setB(setB.B() - 1);
            if (setB.umax() < n) {
                break;
            }
            coding = setB;
        }
        return coding;
    }
    
    public void setFavoredCoding(final CodingMethod favoredCoding) {
        this.favoredCoding = favoredCoding;
    }
    
    public void setTokenCoding(final CodingMethod tokenCoding) {
        this.tokenCoding = tokenCoding;
        this.L = -1;
        if (tokenCoding instanceof Coding && this.fValues != null) {
            final Coding coding = (Coding)tokenCoding;
            if (coding == fitTokenCoding(this.fVlen, coding.L())) {
                this.L = coding.L();
            }
        }
    }
    
    public void setUnfavoredCoding(final CodingMethod unfavoredCoding) {
        this.unfavoredCoding = unfavoredCoding;
    }
    
    public int favoredValueMaxLength() {
        if (this.L == 0) {
            return Integer.MAX_VALUE;
        }
        return BandStructure.UNSIGNED5.setL(this.L).umax();
    }
    
    public void resortFavoredValues() {
        final Coding coding = (Coding)this.tokenCoding;
        this.fValues = BandStructure.realloc(this.fValues, 1 + this.fVlen);
        int n = 1;
        for (int i = 1; i <= coding.B(); ++i) {
            int n2 = coding.byteMax(i);
            if (n2 > this.fVlen) {
                n2 = this.fVlen;
            }
            if (n2 < coding.byteMin(i)) {
                break;
            }
            final int n3 = n;
            final int n4 = n2 + 1;
            if (n4 != n3) {
                assert n4 > n3 : n4 + "!>" + n3;
                assert coding.getLength(n3) == i : i + " != len(" + n3 + ") == " + coding.getLength(n3);
                assert coding.getLength(n4 - 1) == i : i + " != len(" + (n4 - 1) + ") == " + coding.getLength(n4 - 1);
                final int n5 = n3 + (n4 - n3) / 2;
                int n6 = n3;
                int n7 = -1;
                int n8 = n3;
                for (int j = n3; j < n4; ++j) {
                    final int frequency = this.vHist.getFrequency(this.fValues[j]);
                    if (n7 != frequency) {
                        if (i == 1) {
                            Arrays.sort(this.fValues, n8, j);
                        }
                        else if (Math.abs(n6 - n5) > Math.abs(j - n5)) {
                            n6 = j;
                        }
                        n7 = frequency;
                        n8 = j;
                    }
                }
                if (i == 1) {
                    Arrays.sort(this.fValues, n8, n4);
                }
                else {
                    Arrays.sort(this.fValues, n3, n6);
                    Arrays.sort(this.fValues, n6, n4);
                }
                assert coding.getLength(n3) == coding.getLength(n6);
                assert coding.getLength(n3) == coding.getLength(n4 - 1);
                n = n2 + 1;
            }
        }
        assert n == this.fValues.length;
        this.symtab = null;
    }
    
    public int getToken(final int n) {
        if (this.symtab == null) {
            this.symtab = this.makeSymtab();
        }
        int binarySearch = Arrays.binarySearch(this.symtab, (long)n << 32);
        if (binarySearch < 0) {
            binarySearch = -binarySearch - 1;
        }
        if (binarySearch < this.symtab.length && n == (int)(this.symtab[binarySearch] >>> 32)) {
            return (int)this.symtab[binarySearch];
        }
        return 0;
    }
    
    public int[][] encodeValues(final int[] array, final int n, final int n2) {
        final int[] array2 = new int[n2 - n];
        int n3 = 0;
        for (int i = 0; i < array2.length; ++i) {
            final int token = this.getToken(array[n + i]);
            if (token != 0) {
                array2[i] = token;
            }
            else {
                ++n3;
            }
        }
        final int[] array3 = new int[n3];
        int n4 = 0;
        for (int j = 0; j < array2.length; ++j) {
            if (array2[j] == 0) {
                array3[n4++] = array[n + j];
            }
        }
        assert n4 == array3.length;
        return new int[][] { array2, array3 };
    }
    
    private long[] makeSymtab() {
        final long[] array = new long[this.fVlen];
        for (int i = 1; i <= this.fVlen; ++i) {
            array[i - 1] = ((long)this.fValues[i] << 32 | (long)i);
        }
        Arrays.sort(array);
        return array;
    }
    
    private Coding getTailCoding(CodingMethod tailCoding) {
        while (tailCoding instanceof AdaptiveCoding) {
            tailCoding = ((AdaptiveCoding)tailCoding).tailCoding;
        }
        return (Coding)tailCoding;
    }
    
    @Override
    public void writeArrayTo(final OutputStream outputStream, final int[] array, final int n, final int n2) throws IOException {
        final int[][] encodeValues = this.encodeValues(array, n, n2);
        this.writeSequencesTo(outputStream, encodeValues[0], encodeValues[1]);
    }
    
    void writeSequencesTo(final OutputStream outputStream, final int[] array, final int[] array2) throws IOException {
        this.favoredCoding.writeArrayTo(outputStream, this.fValues, 1, 1 + this.fVlen);
        this.getTailCoding(this.favoredCoding).writeTo(outputStream, this.computeSentinelValue());
        this.tokenCoding.writeArrayTo(outputStream, array, 0, array.length);
        if (array2.length > 0) {
            this.unfavoredCoding.writeArrayTo(outputStream, array2, 0, array2.length);
        }
    }
    
    int computeSentinelValue() {
        final Coding tailCoding = this.getTailCoding(this.favoredCoding);
        if (tailCoding.isDelta()) {
            return 0;
        }
        int n;
        int moreCentral = n = this.fValues[1];
        for (int i = 2; i <= this.fVlen; ++i) {
            n = this.fValues[i];
            moreCentral = moreCentral(moreCentral, n);
        }
        if (tailCoding.getLength(moreCentral) <= tailCoding.getLength(n)) {
            return moreCentral;
        }
        return n;
    }
    
    @Override
    public void readArrayFrom(final InputStream inputStream, final int[] array, final int n, final int n2) throws IOException {
        this.setFavoredValues(this.readFavoredValuesFrom(inputStream, n2 - n));
        this.tokenCoding.readArrayFrom(inputStream, array, n, n2);
        int n3 = 0;
        int n4 = -1;
        int n5 = 0;
        for (int i = n; i < n2; ++i) {
            final int n6 = array[i];
            if (n6 == 0) {
                if (n4 < 0) {
                    n3 = i;
                }
                else {
                    array[n4] = i;
                }
                n4 = i;
                ++n5;
            }
            else {
                array[i] = this.fValues[n6];
            }
        }
        final int[] array2 = new int[n5];
        if (n5 > 0) {
            this.unfavoredCoding.readArrayFrom(inputStream, array2, 0, n5);
        }
        for (int j = 0; j < n5; ++j) {
            final int n7 = array[n3];
            array[n3] = array2[j];
            n3 = n7;
        }
    }
    
    int[] readFavoredValuesFrom(final InputStream inputStream, int n) throws IOException {
        int[] array = new int[1000];
        Set set = null;
        assert (set = new HashSet()) != null;
        int i = 1;
        n += i;
        int n2 = Integer.MIN_VALUE;
        int n3 = 0;
        CodingMethod codingMethod;
        AdaptiveCoding adaptiveCoding;
        for (codingMethod = this.favoredCoding; codingMethod instanceof AdaptiveCoding; codingMethod = adaptiveCoding.tailCoding) {
            adaptiveCoding = (AdaptiveCoding)codingMethod;
            int headLength;
            for (headLength = adaptiveCoding.headLength; i + headLength > array.length; array = BandStructure.realloc(array)) {}
            final int n4 = i + headLength;
            adaptiveCoding.headCoding.readArrayFrom(inputStream, array, i, n4);
            while (i < n4) {
                final int n5 = array[i++];
                assert set.add(n5);
                assert i <= n;
                n3 = n5;
                n2 = moreCentral(n2, n5);
            }
        }
        final Coding coding = (Coding)codingMethod;
        if (coding.isDelta()) {
            long n6 = 0L;
            while (true) {
                final long n7 = n6 + coding.readFrom(inputStream);
                int reduceToUnsignedRange;
                if (coding.isSubrange()) {
                    reduceToUnsignedRange = coding.reduceToUnsignedRange(n7);
                }
                else {
                    reduceToUnsignedRange = (int)n7;
                }
                n6 = reduceToUnsignedRange;
                if (i > 1 && (reduceToUnsignedRange == n3 || reduceToUnsignedRange == n2)) {
                    break;
                }
                if (i == array.length) {
                    array = BandStructure.realloc(array);
                }
                array[i++] = reduceToUnsignedRange;
                assert set.add(reduceToUnsignedRange);
                assert i <= n;
                n3 = reduceToUnsignedRange;
                n2 = moreCentral(n2, reduceToUnsignedRange);
            }
        }
        else {
            while (true) {
                final int from = coding.readFrom(inputStream);
                if (i > 1) {
                    if (from == n3) {
                        break;
                    }
                    if (from == n2) {
                        break;
                    }
                }
                if (i == array.length) {
                    array = BandStructure.realloc(array);
                }
                array[i++] = from;
                assert set.add(from);
                assert i <= n;
                n3 = from;
                n2 = moreCentral(n2, from);
            }
        }
        return BandStructure.realloc(array, i);
    }
    
    private static int moreCentral(final int n, final int n2) {
        final int n3 = ((n >> 31 ^ n << 1) - Integer.MIN_VALUE < (n2 >> 31 ^ n2 << 1) - Integer.MIN_VALUE) ? n : n2;
        assert n3 == moreCentralSlow(n, n2);
        return n3;
    }
    
    private static int moreCentralSlow(final int n, final int n2) {
        int n3 = n;
        if (n3 < 0) {
            n3 = -n3;
        }
        if (n3 < 0) {
            return n2;
        }
        int n4 = n2;
        if (n4 < 0) {
            n4 = -n4;
        }
        if (n4 < 0) {
            return n;
        }
        if (n3 < n4) {
            return n;
        }
        if (n3 > n4) {
            return n2;
        }
        return (n < n2) ? n : n2;
    }
    
    @Override
    public byte[] getMetaCoding(final Coding coding) {
        final int fVlen = this.fVlen;
        boolean b = false;
        if (this.tokenCoding instanceof Coding) {
            final Coding coding2 = (Coding)this.tokenCoding;
            if (coding2.B() == 1) {
                b = true;
            }
            else if (this.L >= 0) {
                assert this.L == coding2.L();
                for (int i = 1; i < PopulationCoding.LValuesCoded.length; ++i) {
                    if (PopulationCoding.LValuesCoded[i] == this.L) {
                        b = (i != 0);
                        break;
                    }
                }
            }
        }
        CodingMethod tokenCoding = null;
        if ((b ? 1 : 0) != 0 && this.tokenCoding == fitTokenCoding(this.fVlen, this.L)) {
            tokenCoding = this.tokenCoding;
        }
        final int n = (this.favoredCoding == coding) ? 1 : 0;
        final int n2 = (this.unfavoredCoding == coding || this.unfavoredCoding == null) ? 1 : 0;
        final boolean b2 = this.tokenCoding == tokenCoding;
        final int n3 = b2 ? b : 0;
        assert b2 == n3 > 0;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10);
        byteArrayOutputStream.write(141 + n + 2 * n2 + 4 * n3);
        try {
            if (n == 0) {
                byteArrayOutputStream.write(this.favoredCoding.getMetaCoding(coding));
            }
            if (!b2) {
                byteArrayOutputStream.write(this.tokenCoding.getMetaCoding(coding));
            }
            if (n2 == 0) {
                byteArrayOutputStream.write(this.unfavoredCoding.getMetaCoding(coding));
            }
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static int parseMetaCoding(final byte[] array, int n, final Coding coding, final CodingMethod[] array2) {
        int n2 = array[n++] & 0xFF;
        if (n2 < 141 || n2 >= 189) {
            return n - 1;
        }
        n2 -= 141;
        final int n3 = n2 % 2;
        final int n4 = n2 / 2 % 2;
        final int n5 = n2 / 4;
        final boolean b = n5 > 0;
        final int l = PopulationCoding.LValuesCoded[n5];
        final CodingMethod[] array3 = { coding };
        final CodingMethod[] array4 = { null };
        final CodingMethod[] array5 = { coding };
        if (n3 == 0) {
            n = BandStructure.parseMetaCoding(array, n, coding, array3);
        }
        if (!b) {
            n = BandStructure.parseMetaCoding(array, n, coding, array4);
        }
        if (n4 == 0) {
            n = BandStructure.parseMetaCoding(array, n, coding, array5);
        }
        final PopulationCoding populationCoding = new PopulationCoding();
        populationCoding.L = l;
        populationCoding.favoredCoding = array3[0];
        populationCoding.tokenCoding = array4[0];
        populationCoding.unfavoredCoding = array5[0];
        array2[0] = populationCoding;
        return n;
    }
    
    private String keyString(final CodingMethod codingMethod) {
        if (codingMethod instanceof Coding) {
            return ((Coding)codingMethod).keyString();
        }
        if (codingMethod == null) {
            return "none";
        }
        return codingMethod.toString();
    }
    
    @Override
    public String toString() {
        final PropMap currentPropMap = Utils.currentPropMap();
        final boolean b = currentPropMap != null && currentPropMap.getBoolean("com.sun.java.util.jar.pack.verbose.pop");
        final StringBuilder sb = new StringBuilder(100);
        sb.append("pop(").append("fVlen=").append(this.fVlen);
        if (b && this.fValues != null) {
            sb.append(" fV=[");
            for (int i = 1; i <= this.fVlen; ++i) {
                sb.append((i == 1) ? "" : ",").append(this.fValues[i]);
            }
            sb.append(";").append(this.computeSentinelValue());
            sb.append("]");
        }
        sb.append(" fc=").append(this.keyString(this.favoredCoding));
        sb.append(" tc=").append(this.keyString(this.tokenCoding));
        sb.append(" uc=").append(this.keyString(this.unfavoredCoding));
        sb.append(")");
        return sb.toString();
    }
    
    static {
        LValuesCoded = new int[] { -1, 4, 8, 16, 32, 64, 128, 192, 224, 240, 248, 252 };
    }
}
