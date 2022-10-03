package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.RealFieldUnivariateFunction;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.IntegerSequence;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public class FieldBracketingNthOrderBrentSolver<T extends RealFieldElement<T>> implements BracketedRealFieldUnivariateSolver<T>
{
    private static final int MAXIMAL_AGING = 2;
    private final Field<T> field;
    private final int maximalOrder;
    private final T functionValueAccuracy;
    private final T absoluteAccuracy;
    private final T relativeAccuracy;
    private IntegerSequence.Incrementor evaluations;
    
    public FieldBracketingNthOrderBrentSolver(final T relativeAccuracy, final T absoluteAccuracy, final T functionValueAccuracy, final int maximalOrder) throws NumberIsTooSmallException {
        if (maximalOrder < 2) {
            throw new NumberIsTooSmallException(maximalOrder, 2, true);
        }
        this.field = relativeAccuracy.getField();
        this.maximalOrder = maximalOrder;
        this.absoluteAccuracy = absoluteAccuracy;
        this.relativeAccuracy = relativeAccuracy;
        this.functionValueAccuracy = functionValueAccuracy;
        this.evaluations = IntegerSequence.Incrementor.create();
    }
    
    public int getMaximalOrder() {
        return this.maximalOrder;
    }
    
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }
    
    public int getEvaluations() {
        return this.evaluations.getCount();
    }
    
    public T getAbsoluteAccuracy() {
        return this.absoluteAccuracy;
    }
    
    public T getRelativeAccuracy() {
        return this.relativeAccuracy;
    }
    
    public T getFunctionValueAccuracy() {
        return this.functionValueAccuracy;
    }
    
    public T solve(final int maxEval, final RealFieldUnivariateFunction<T> f, final T min, final T max, final AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
        return this.solve(maxEval, f, min, max, min.add(max).divide(2.0), allowedSolution);
    }
    
    public T solve(final int maxEval, final RealFieldUnivariateFunction<T> f, final T min, final T max, final T startValue, final AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
        MathUtils.checkNotNull(f);
        this.evaluations = this.evaluations.withMaximalCount(maxEval).withStart(0);
        final T zero = this.field.getZero();
        final T nan = zero.add(Double.NaN);
        final T[] x = MathArrays.buildArray(this.field, this.maximalOrder + 1);
        final T[] y = MathArrays.buildArray(this.field, this.maximalOrder + 1);
        x[0] = min;
        x[1] = startValue;
        x[2] = max;
        this.evaluations.increment();
        y[1] = f.value(x[1]);
        if (Precision.equals(y[1].getReal(), 0.0, 1)) {
            return x[1];
        }
        this.evaluations.increment();
        y[0] = f.value(x[0]);
        if (Precision.equals(y[0].getReal(), 0.0, 1)) {
            return x[0];
        }
        int nbPoints;
        int signChangeIndex;
        if (y[0].multiply(y[1]).getReal() < 0.0) {
            nbPoints = 2;
            signChangeIndex = 1;
        }
        else {
            this.evaluations.increment();
            y[2] = f.value(x[2]);
            if (Precision.equals(y[2].getReal(), 0.0, 1)) {
                return x[2];
            }
            if (y[1].multiply(y[2]).getReal() >= 0.0) {
                throw new NoBracketingException(x[0].getReal(), x[2].getReal(), y[0].getReal(), y[2].getReal());
            }
            nbPoints = 3;
            signChangeIndex = 2;
        }
        final T[] tmpX = MathArrays.buildArray(this.field, x.length);
        T xA = x[signChangeIndex - 1];
        T yA = y[signChangeIndex - 1];
        final T absXA = xA.abs();
        T absYA = yA.abs();
        int agingA = 0;
        T xB = x[signChangeIndex];
        T yB = y[signChangeIndex];
        final T absXB = xB.abs();
        T absYB = yB.abs();
        int agingB = 0;
        while (true) {
            final T maxX = (absXA.subtract(absXB).getReal() < 0.0) ? absXB : absXA;
            final T maxY = (absYA.subtract(absYB).getReal() < 0.0) ? absYB : absYA;
            final T xTol = this.absoluteAccuracy.add(this.relativeAccuracy.multiply(maxX));
            if (xB.subtract(xA).subtract(xTol).getReal() <= 0.0 || maxY.subtract(this.functionValueAccuracy).getReal() < 0.0) {
                switch (allowedSolution) {
                    case ANY_SIDE: {
                        return (absYA.subtract(absYB).getReal() < 0.0) ? xA : xB;
                    }
                    case LEFT_SIDE: {
                        return xA;
                    }
                    case RIGHT_SIDE: {
                        return xB;
                    }
                    case BELOW_SIDE: {
                        return (yA.getReal() <= 0.0) ? xA : xB;
                    }
                    case ABOVE_SIDE: {
                        return (yA.getReal() < 0.0) ? xB : xA;
                    }
                    default: {
                        throw new MathInternalError((Throwable)null);
                    }
                }
            }
            else {
                T targetY;
                if (agingA >= 2) {
                    targetY = ((RealFieldElement<RealFieldElement<T>>)yB).divide(16.0).negate();
                }
                else if (agingB >= 2) {
                    targetY = ((RealFieldElement<RealFieldElement<T>>)yA).divide(16.0).negate();
                }
                else {
                    targetY = zero;
                }
                int start = 0;
                int end = nbPoints;
                T nextX;
                do {
                    System.arraycopy(x, start, tmpX, start, end - start);
                    nextX = this.guessX(targetY, tmpX, y, start, end);
                    if (nextX.subtract(xA).getReal() <= 0.0 || nextX.subtract(xB).getReal() >= 0.0) {
                        if (signChangeIndex - start >= end - signChangeIndex) {
                            ++start;
                        }
                        else {
                            --end;
                        }
                        nextX = nan;
                    }
                } while (Double.isNaN(nextX.getReal()) && end - start > 1);
                if (Double.isNaN(nextX.getReal())) {
                    nextX = xA.add(xB.subtract(xA).divide(2.0));
                    start = signChangeIndex - 1;
                    end = signChangeIndex;
                }
                this.evaluations.increment();
                final T nextY = f.value(nextX);
                if (Precision.equals(nextY.getReal(), 0.0, 1)) {
                    return nextX;
                }
                if (nbPoints > 2 && end - start != nbPoints) {
                    nbPoints = end - start;
                    System.arraycopy(x, start, x, 0, nbPoints);
                    System.arraycopy(y, start, y, 0, nbPoints);
                    signChangeIndex -= start;
                }
                else if (nbPoints == x.length) {
                    --nbPoints;
                    if (signChangeIndex >= (x.length + 1) / 2) {
                        System.arraycopy(x, 1, x, 0, nbPoints);
                        System.arraycopy(y, 1, y, 0, nbPoints);
                        --signChangeIndex;
                    }
                }
                System.arraycopy(x, signChangeIndex, x, signChangeIndex + 1, nbPoints - signChangeIndex);
                x[signChangeIndex] = nextX;
                System.arraycopy(y, signChangeIndex, y, signChangeIndex + 1, nbPoints - signChangeIndex);
                y[signChangeIndex] = nextY;
                ++nbPoints;
                if (nextY.multiply(yA).getReal() <= 0.0) {
                    xB = nextX;
                    yB = nextY;
                    absYB = yB.abs();
                    ++agingA;
                    agingB = 0;
                }
                else {
                    xA = nextX;
                    yA = nextY;
                    absYA = yA.abs();
                    agingA = 0;
                    ++agingB;
                    ++signChangeIndex;
                }
            }
        }
    }
    
    private T guessX(final T targetY, final T[] x, final T[] y, final int start, final int end) {
        for (int i = start; i < end - 1; ++i) {
            final int delta = i + 1 - start;
            for (int j = end - 1; j > i; --j) {
                x[j] = x[j].subtract(x[j - 1]).divide(y[j].subtract(y[j - delta]));
            }
        }
        T x2 = this.field.getZero();
        for (int k = end - 1; k >= start; --k) {
            x2 = x[k].add(x2.multiply(targetY.subtract(y[k])));
        }
        return x2;
    }
}
