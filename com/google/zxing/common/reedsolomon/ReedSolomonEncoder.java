package com.google.zxing.common.reedsolomon;

import java.util.ArrayList;
import java.util.List;

public final class ReedSolomonEncoder
{
    private final GenericGF field;
    private final List<GenericGFPoly> cachedGenerators;
    
    public ReedSolomonEncoder(final GenericGF field) {
        if (!GenericGF.QR_CODE_FIELD_256.equals(field)) {
            throw new IllegalArgumentException("Only QR Code is supported at this time");
        }
        this.field = field;
        (this.cachedGenerators = new ArrayList<GenericGFPoly>()).add(new GenericGFPoly(field, new int[] { 1 }));
    }
    
    private GenericGFPoly buildGenerator(final int degree) {
        if (degree >= this.cachedGenerators.size()) {
            GenericGFPoly lastGenerator = this.cachedGenerators.get(this.cachedGenerators.size() - 1);
            for (int d = this.cachedGenerators.size(); d <= degree; ++d) {
                final GenericGFPoly nextGenerator = lastGenerator.multiply(new GenericGFPoly(this.field, new int[] { 1, this.field.exp(d - 1) }));
                this.cachedGenerators.add(nextGenerator);
                lastGenerator = nextGenerator;
            }
        }
        return this.cachedGenerators.get(degree);
    }
    
    public void encode(final int[] toEncode, final int ecBytes) {
        if (ecBytes == 0) {
            throw new IllegalArgumentException("No error correction bytes");
        }
        final int dataBytes = toEncode.length - ecBytes;
        if (dataBytes <= 0) {
            throw new IllegalArgumentException("No data bytes provided");
        }
        final GenericGFPoly generator = this.buildGenerator(ecBytes);
        final int[] infoCoefficients = new int[dataBytes];
        System.arraycopy(toEncode, 0, infoCoefficients, 0, dataBytes);
        GenericGFPoly info = new GenericGFPoly(this.field, infoCoefficients);
        info = info.multiplyByMonomial(ecBytes, 1);
        final GenericGFPoly remainder = info.divide(generator)[1];
        final int[] coefficients = remainder.getCoefficients();
        final int numZeroCoefficients = ecBytes - coefficients.length;
        for (int i = 0; i < numZeroCoefficients; ++i) {
            toEncode[dataBytes + i] = 0;
        }
        System.arraycopy(coefficients, 0, toEncode, dataBytes + numZeroCoefficients, coefficients.length);
    }
}
