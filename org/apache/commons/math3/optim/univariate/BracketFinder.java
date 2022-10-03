package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.util.IntegerSequence;

public class BracketFinder
{
    private static final double EPS_MIN = 1.0E-21;
    private static final double GOLD = 1.618034;
    private final double growLimit;
    private IntegerSequence.Incrementor evaluations;
    private double lo;
    private double hi;
    private double mid;
    private double fLo;
    private double fHi;
    private double fMid;
    
    public BracketFinder() {
        this(100.0, 500);
    }
    
    public BracketFinder(final double growLimit, final int maxEvaluations) {
        if (growLimit <= 0.0) {
            throw new NotStrictlyPositiveException(growLimit);
        }
        if (maxEvaluations <= 0) {
            throw new NotStrictlyPositiveException(maxEvaluations);
        }
        this.growLimit = growLimit;
        this.evaluations = IntegerSequence.Incrementor.create().withMaximalCount(maxEvaluations);
    }
    
    public void search(final UnivariateFunction func, final GoalType goal, double xA, double xB) {
        this.evaluations = this.evaluations.withStart(0);
        final boolean isMinim = goal == GoalType.MINIMIZE;
        double fA = this.eval(func, xA);
        double fB = this.eval(func, xB);
        Label_0089: {
            if (isMinim) {
                if (fA >= fB) {
                    break Label_0089;
                }
            }
            else if (fA <= fB) {
                break Label_0089;
            }
            double tmp = xA;
            xA = xB;
            xB = tmp;
            tmp = fA;
            fA = fB;
            fB = tmp;
        }
        double xC = xB + 1.618034 * (xB - xA);
        double fC = this.eval(func, xC);
        Label_0532: {
            double w = 0.0;
            double fW = 0.0;
            Label_0323: {
                while (true) {
                    if (isMinim) {
                        if (fC >= fB) {
                            break Label_0532;
                        }
                    }
                    else if (fC <= fB) {
                        break Label_0532;
                    }
                    final double tmp2 = (xB - xA) * (fB - fC);
                    final double tmp3 = (xB - xC) * (fB - fA);
                    final double val = tmp3 - tmp2;
                    final double denom = (FastMath.abs(val) < 1.0E-21) ? 2.0E-21 : (2.0 * val);
                    w = xB - ((xB - xC) * tmp3 - (xB - xA) * tmp2) / denom;
                    final double wLim = xB + this.growLimit * (xC - xB);
                    Label_0506: {
                        if ((w - xC) * (xB - w) > 0.0) {
                            fW = this.eval(func, w);
                            if (isMinim) {
                                if (fW < fC) {
                                    break;
                                }
                            }
                            else if (fW > fC) {
                                break;
                            }
                            if (isMinim) {
                                if (fW > fB) {
                                    break Label_0323;
                                }
                            }
                            else if (fW < fB) {
                                break Label_0323;
                            }
                            w = xC + 1.618034 * (xC - xB);
                            fW = this.eval(func, w);
                        }
                        else if ((w - wLim) * (wLim - xC) >= 0.0) {
                            w = wLim;
                            fW = this.eval(func, w);
                        }
                        else if ((w - wLim) * (xC - w) > 0.0) {
                            fW = this.eval(func, w);
                            if (isMinim) {
                                if (fW >= fC) {
                                    break Label_0506;
                                }
                            }
                            else if (fW <= fC) {
                                break Label_0506;
                            }
                            xB = xC;
                            xC = w;
                            w = xC + 1.618034 * (xC - xB);
                            fB = fC;
                            fC = fW;
                            fW = this.eval(func, w);
                        }
                        else {
                            w = xC + 1.618034 * (xC - xB);
                            fW = this.eval(func, w);
                        }
                    }
                    xA = xB;
                    fA = fB;
                    xB = xC;
                    fB = fC;
                    xC = w;
                    fC = fW;
                }
                xA = xB;
                xB = w;
                fA = fB;
                fB = fW;
                break Label_0532;
            }
            xC = w;
            fC = fW;
        }
        this.lo = xA;
        this.fLo = fA;
        this.mid = xB;
        this.fMid = fB;
        this.hi = xC;
        this.fHi = fC;
        if (this.lo > this.hi) {
            double tmp4 = this.lo;
            this.lo = this.hi;
            this.hi = tmp4;
            tmp4 = this.fLo;
            this.fLo = this.fHi;
            this.fHi = tmp4;
        }
    }
    
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }
    
    public int getEvaluations() {
        return this.evaluations.getCount();
    }
    
    public double getLo() {
        return this.lo;
    }
    
    public double getFLo() {
        return this.fLo;
    }
    
    public double getHi() {
        return this.hi;
    }
    
    public double getFHi() {
        return this.fHi;
    }
    
    public double getMid() {
        return this.mid;
    }
    
    public double getFMid() {
        return this.fMid;
    }
    
    private double eval(final UnivariateFunction f, final double x) {
        try {
            this.evaluations.increment();
        }
        catch (final MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
        return f.value(x);
    }
}
