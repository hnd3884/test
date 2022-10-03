package com.google.zxing.common.reedsolomon;

public final class ReedSolomonDecoder
{
    private final GenericGF field;
    
    public ReedSolomonDecoder(final GenericGF field) {
        this.field = field;
    }
    
    public void decode(final int[] received, final int twoS) throws ReedSolomonException {
        final GenericGFPoly poly = new GenericGFPoly(this.field, received);
        final int[] syndromeCoefficients = new int[twoS];
        final boolean dataMatrix = this.field.equals(GenericGF.DATA_MATRIX_FIELD_256);
        boolean noError = true;
        for (int i = 0; i < twoS; ++i) {
            final int eval = poly.evaluateAt(this.field.exp(dataMatrix ? (i + 1) : i));
            if ((syndromeCoefficients[syndromeCoefficients.length - 1 - i] = eval) != 0) {
                noError = false;
            }
        }
        if (noError) {
            return;
        }
        final GenericGFPoly syndrome = new GenericGFPoly(this.field, syndromeCoefficients);
        final GenericGFPoly[] sigmaOmega = this.runEuclideanAlgorithm(this.field.buildMonomial(twoS, 1), syndrome, twoS);
        final GenericGFPoly sigma = sigmaOmega[0];
        final GenericGFPoly omega = sigmaOmega[1];
        final int[] errorLocations = this.findErrorLocations(sigma);
        final int[] errorMagnitudes = this.findErrorMagnitudes(omega, errorLocations, dataMatrix);
        for (int j = 0; j < errorLocations.length; ++j) {
            final int position = received.length - 1 - this.field.log(errorLocations[j]);
            if (position < 0) {
                throw new ReedSolomonException("Bad error location");
            }
            received[position] = GenericGF.addOrSubtract(received[position], errorMagnitudes[j]);
        }
    }
    
    private GenericGFPoly[] runEuclideanAlgorithm(GenericGFPoly a, GenericGFPoly b, final int R) throws ReedSolomonException {
        if (a.getDegree() < b.getDegree()) {
            final GenericGFPoly temp = a;
            a = b;
            b = temp;
        }
        GenericGFPoly rLast = a;
        GenericGFPoly r = b;
        GenericGFPoly sLast = this.field.getOne();
        GenericGFPoly s = this.field.getZero();
        GenericGFPoly tLast = this.field.getZero();
        GenericGFPoly t = this.field.getOne();
        while (r.getDegree() >= R / 2) {
            final GenericGFPoly rLastLast = rLast;
            final GenericGFPoly sLastLast = sLast;
            final GenericGFPoly tLastLast = tLast;
            rLast = r;
            sLast = s;
            tLast = t;
            if (rLast.isZero()) {
                throw new ReedSolomonException("r_{i-1} was zero");
            }
            r = rLastLast;
            GenericGFPoly q = this.field.getZero();
            final int denominatorLeadingTerm = rLast.getCoefficient(rLast.getDegree());
            final int dltInverse = this.field.inverse(denominatorLeadingTerm);
            while (r.getDegree() >= rLast.getDegree() && !r.isZero()) {
                final int degreeDiff = r.getDegree() - rLast.getDegree();
                final int scale = this.field.multiply(r.getCoefficient(r.getDegree()), dltInverse);
                q = q.addOrSubtract(this.field.buildMonomial(degreeDiff, scale));
                r = r.addOrSubtract(rLast.multiplyByMonomial(degreeDiff, scale));
            }
            s = q.multiply(sLast).addOrSubtract(sLastLast);
            t = q.multiply(tLast).addOrSubtract(tLastLast);
        }
        final int sigmaTildeAtZero = t.getCoefficient(0);
        if (sigmaTildeAtZero == 0) {
            throw new ReedSolomonException("sigmaTilde(0) was zero");
        }
        final int inverse = this.field.inverse(sigmaTildeAtZero);
        final GenericGFPoly sigma = t.multiply(inverse);
        final GenericGFPoly omega = r.multiply(inverse);
        return new GenericGFPoly[] { sigma, omega };
    }
    
    private int[] findErrorLocations(final GenericGFPoly errorLocator) throws ReedSolomonException {
        final int numErrors = errorLocator.getDegree();
        if (numErrors == 1) {
            return new int[] { errorLocator.getCoefficient(1) };
        }
        final int[] result = new int[numErrors];
        int e = 0;
        for (int i = 1; i < this.field.getSize() && e < numErrors; ++i) {
            if (errorLocator.evaluateAt(i) == 0) {
                result[e] = this.field.inverse(i);
                ++e;
            }
        }
        if (e != numErrors) {
            throw new ReedSolomonException("Error locator degree does not match number of roots");
        }
        return result;
    }
    
    private int[] findErrorMagnitudes(final GenericGFPoly errorEvaluator, final int[] errorLocations, final boolean dataMatrix) {
        final int s = errorLocations.length;
        final int[] result = new int[s];
        for (int i = 0; i < s; ++i) {
            final int xiInverse = this.field.inverse(errorLocations[i]);
            int denominator = 1;
            for (int j = 0; j < s; ++j) {
                if (i != j) {
                    final int term = this.field.multiply(errorLocations[j], xiInverse);
                    final int termPlus1 = ((term & 0x1) == 0x0) ? (term | 0x1) : (term & 0xFFFFFFFE);
                    denominator = this.field.multiply(denominator, termPlus1);
                }
            }
            result[i] = this.field.multiply(errorEvaluator.evaluateAt(xiInverse), this.field.inverse(denominator));
            if (dataMatrix) {
                result[i] = this.field.multiply(result[i], xiInverse);
            }
        }
        return result;
    }
}
