package org.apache.commons.math3.special;

import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;

public class BesselJ implements UnivariateFunction
{
    private static final double PI2 = 0.6366197723675814;
    private static final double TOWPI1 = 6.28125;
    private static final double TWOPI2 = 0.001935307179586477;
    private static final double TWOPI = 6.283185307179586;
    private static final double ENTEN = 1.0E308;
    private static final double ENSIG = 1.0E16;
    private static final double RTNSIG = 1.0E-4;
    private static final double ENMTEN = 8.9E-308;
    private static final double X_MIN = 0.0;
    private static final double X_MAX = 10000.0;
    private static final double[] FACT;
    private final double order;
    
    public BesselJ(final double order) {
        this.order = order;
    }
    
    public double value(final double x) throws MathIllegalArgumentException, ConvergenceException {
        return value(this.order, x);
    }
    
    public static double value(final double order, final double x) throws MathIllegalArgumentException, ConvergenceException {
        final int n = (int)order;
        final double alpha = order - n;
        final int nb = n + 1;
        final BesselJResult res = rjBesl(x, alpha, nb);
        if (res.nVals >= nb) {
            return res.vals[n];
        }
        if (res.nVals < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.BESSEL_FUNCTION_BAD_ARGUMENT, new Object[] { order, x });
        }
        if (FastMath.abs(res.vals[res.nVals - 1]) < 1.0E-100) {
            return res.vals[n];
        }
        throw new ConvergenceException(LocalizedFormats.BESSEL_FUNCTION_FAILED_CONVERGENCE, new Object[] { order, x });
    }
    
    public static BesselJResult rjBesl(final double x, final double alpha, final int nb) {
        final double[] b = new double[nb];
        int ncalc = 0;
        double alpem = 0.0;
        double alp2em = 0.0;
        final int magx = (int)x;
        if (nb > 0 && x >= 0.0 && x <= 10000.0 && alpha >= 0.0 && alpha < 1.0) {
            ncalc = nb;
            for (int i = 0; i < nb; ++i) {
                b[i] = 0.0;
            }
            if (x < 1.0E-4) {
                double tempa = 1.0;
                alpem = 1.0 + alpha;
                double halfx = 0.0;
                if (x > 8.9E-308) {
                    halfx = 0.5 * x;
                }
                if (alpha != 0.0) {
                    tempa = FastMath.pow(halfx, alpha) / (alpha * Gamma.gamma(alpha));
                }
                double tempb = 0.0;
                if (x + 1.0 > 1.0) {
                    tempb = -halfx * halfx;
                }
                b[0] = tempa + tempa * tempb / alpem;
                if (x != 0.0 && b[0] == 0.0) {
                    ncalc = 0;
                }
                if (nb != 1) {
                    if (x <= 0.0) {
                        for (int n = 1; n < nb; ++n) {
                            b[n] = 0.0;
                        }
                    }
                    else {
                        final double tempc = halfx;
                        final double tover = (tempb != 0.0) ? (8.9E-308 / tempb) : (1.78E-307 / x);
                        for (int n = 1; n < nb; ++n) {
                            tempa /= alpem;
                            ++alpem;
                            tempa *= tempc;
                            if (tempa <= tover * alpem) {
                                tempa = 0.0;
                            }
                            b[n] = tempa + tempa * tempb / alpem;
                            if (b[n] == 0.0 && ncalc > n) {
                                ncalc = n;
                            }
                        }
                    }
                }
            }
            else if (x > 25.0 && nb <= magx + 1) {
                final double xc = FastMath.sqrt(0.6366197723675814 / x);
                final double mul = 0.125 / x;
                final double xin = mul * mul;
                int m = 0;
                if (x >= 130.0) {
                    m = 4;
                }
                else if (x >= 35.0) {
                    m = 8;
                }
                else {
                    m = 11;
                }
                final double xm = 4.0 * m;
                double t = (int)(x / 6.283185307179586 + 0.5);
                final double z = x - t * 6.28125 - t * 0.001935307179586477 - (alpha + 0.5) / 0.6366197723675814;
                double vsin = FastMath.sin(z);
                double vcos = FastMath.cos(z);
                double gnu = 2.0 * alpha;
                for (int j = 1; j <= 2; ++j) {
                    double s = (xm - 1.0 - gnu) * (xm - 1.0 + gnu) * xin * 0.5;
                    t = (gnu - (xm - 3.0)) * (gnu + (xm - 3.0));
                    double capp = s * t / BesselJ.FACT[2 * m];
                    double t2 = (gnu - (xm + 1.0)) * (gnu + (xm + 1.0));
                    double capq = s * t2 / BesselJ.FACT[2 * m + 1];
                    double xk = xm;
                    int k = 2 * m;
                    t2 = t;
                    for (int l = 2; l <= m; ++l) {
                        xk -= 4.0;
                        s = (xk - 1.0 - gnu) * (xk - 1.0 + gnu);
                        t = (gnu - (xk - 3.0)) * (gnu + (xk - 3.0));
                        capp = (capp + 1.0 / BesselJ.FACT[k - 2]) * s * t * xin;
                        capq = (capq + 1.0 / BesselJ.FACT[k - 1]) * s * t2 * xin;
                        k -= 2;
                        t2 = t;
                    }
                    ++capp;
                    capq = (capq + 1.0) * (gnu * gnu - 1.0) * (0.125 / x);
                    b[j - 1] = xc * (capp * vcos - capq * vsin);
                    if (nb == 1) {
                        return new BesselJResult(MathArrays.copyOf(b, b.length), ncalc);
                    }
                    t = vsin;
                    vsin = -vcos;
                    vcos = t;
                    gnu += 2.0;
                }
                if (nb > 2) {
                    gnu = 2.0 * alpha + 2.0;
                    for (int j2 = 2; j2 < nb; ++j2) {
                        b[j2] = gnu * b[j2 - 1] / x - b[j2 - 2];
                        gnu += 2.0;
                    }
                }
            }
            else {
                final int nbmx = nb - magx;
                int n2 = magx + 1;
                int nstart = 0;
                int nend = 0;
                double en = 2.0 * (n2 + alpha);
                double plast = 1.0;
                double p = en / x;
                double test = 2.0E16;
                boolean readyToInitialize = false;
                if (nbmx >= 3) {
                    double tover = 1.0E292;
                    nstart = magx + 2;
                    nend = nb - 1;
                    en = 2.0 * (nstart - 1 + alpha);
                    for (int k2 = nstart; k2 <= nend; ++k2) {
                        n2 = k2;
                        en += 2.0;
                        double pold = plast;
                        plast = p;
                        p = en * plast / x - pold;
                        if (p > tover) {
                            tover = 1.0E308;
                            p /= tover;
                            plast /= tover;
                            double psave = p;
                            double psavel = plast;
                            nstart = n2 + 1;
                            do {
                                ++n2;
                                en += 2.0;
                                pold = plast;
                                plast = p;
                                p = en * plast / x - pold;
                            } while (p <= 1.0);
                            final double tempb = en / x;
                            test = pold * plast * (0.5 - 0.5 / (tempb * tempb));
                            test /= 1.0E16;
                            p = plast * tover;
                            --n2;
                            en -= 2.0;
                            nend = FastMath.min(nb, n2);
                            for (int l2 = nstart; l2 <= nend; ++l2) {
                                pold = psavel;
                                psavel = psave;
                                psave = en * psavel / x - pold;
                                if (psave * psavel > test) {
                                    ncalc = l2 - 1;
                                    readyToInitialize = true;
                                    break;
                                }
                            }
                            ncalc = nend;
                            readyToInitialize = true;
                            break;
                        }
                    }
                    if (!readyToInitialize) {
                        n2 = nend;
                        en = 2.0 * (n2 + alpha);
                        test = FastMath.max(test, FastMath.sqrt(plast * 1.0E16) * FastMath.sqrt(2.0 * p));
                    }
                }
                if (!readyToInitialize) {
                    do {
                        ++n2;
                        en += 2.0;
                        final double pold = plast;
                        plast = p;
                        p = en * plast / x - pold;
                    } while (p < test);
                }
                ++n2;
                en += 2.0;
                double tempb = 0.0;
                double tempa = 1.0 / p;
                int m2 = 2 * n2 - 4 * (n2 / 2);
                double sum = 0.0;
                double em = n2 / 2;
                alpem = em - 1.0 + alpha;
                alp2em = 2.0 * em + alpha;
                if (m2 != 0) {
                    sum = tempa * alpem * alp2em / em;
                }
                nend = n2 - nb;
                boolean readyToNormalize = false;
                boolean calculatedB0 = false;
                for (int l3 = 1; l3 <= nend; ++l3) {
                    --n2;
                    en -= 2.0;
                    final double tempc = tempb;
                    tempb = tempa;
                    tempa = en * tempb / x - tempc;
                    m2 = 2 - m2;
                    if (m2 != 0) {
                        --em;
                        alp2em = 2.0 * em + alpha;
                        if (n2 == 1) {
                            break;
                        }
                        alpem = em - 1.0 + alpha;
                        if (alpem == 0.0) {
                            alpem = 1.0;
                        }
                        sum = (sum + tempa * alp2em) * alpem / em;
                    }
                }
                b[n2 - 1] = tempa;
                if (nend >= 0) {
                    if (nb <= 1) {
                        alp2em = alpha;
                        if (alpha + 1.0 == 1.0) {
                            alp2em = 1.0;
                        }
                        sum += b[0] * alp2em;
                        readyToNormalize = true;
                    }
                    else {
                        --n2;
                        en -= 2.0;
                        b[n2 - 1] = en * tempa / x - tempb;
                        if (n2 == 1) {
                            calculatedB0 = true;
                        }
                        else {
                            m2 = 2 - m2;
                            if (m2 != 0) {
                                --em;
                                alp2em = 2.0 * em + alpha;
                                alpem = em - 1.0 + alpha;
                                if (alpem == 0.0) {
                                    alpem = 1.0;
                                }
                                sum = (sum + b[n2 - 1] * alp2em) * alpem / em;
                            }
                        }
                    }
                }
                if (!readyToNormalize && !calculatedB0) {
                    nend = n2 - 2;
                    if (nend != 0) {
                        for (int l3 = 1; l3 <= nend; ++l3) {
                            --n2;
                            en -= 2.0;
                            b[n2 - 1] = en * b[n2] / x - b[n2 + 1];
                            m2 = 2 - m2;
                            if (m2 != 0) {
                                --em;
                                alp2em = 2.0 * em + alpha;
                                alpem = em - 1.0 + alpha;
                                if (alpem == 0.0) {
                                    alpem = 1.0;
                                }
                                sum = (sum + b[n2 - 1] * alp2em) * alpem / em;
                            }
                        }
                    }
                }
                if (!readyToNormalize) {
                    if (!calculatedB0) {
                        b[0] = 2.0 * (alpha + 1.0) * b[1] / x - b[2];
                    }
                    --em;
                    alp2em = 2.0 * em + alpha;
                    if (alp2em == 0.0) {
                        alp2em = 1.0;
                    }
                    sum += b[0] * alp2em;
                }
                if (FastMath.abs(alpha) > 1.0E-16) {
                    sum *= Gamma.gamma(alpha) * FastMath.pow(x * 0.5, -alpha);
                }
                tempa = 8.9E-308;
                if (sum > 1.0) {
                    tempa *= sum;
                }
                for (n2 = 0; n2 < nb; ++n2) {
                    if (FastMath.abs(b[n2]) < tempa) {
                        b[n2] = 0.0;
                    }
                    final double[] array = b;
                    final int n3 = n2;
                    array[n3] /= sum;
                }
            }
        }
        else {
            if (b.length > 0) {
                b[0] = 0.0;
            }
            ncalc = FastMath.min(nb, 0) - 1;
        }
        return new BesselJResult(MathArrays.copyOf(b, b.length), ncalc);
    }
    
    static {
        FACT = new double[] { 1.0, 1.0, 2.0, 6.0, 24.0, 120.0, 720.0, 5040.0, 40320.0, 362880.0, 3628800.0, 3.99168E7, 4.790016E8, 6.2270208E9, 8.71782912E10, 1.307674368E12, 2.0922789888E13, 3.55687428096E14, 6.402373705728E15, 1.21645100408832E17, 2.43290200817664E18, 5.109094217170944E19, 1.1240007277776077E21, 2.585201673888498E22, 6.204484017332394E23 };
    }
    
    public static class BesselJResult
    {
        private final double[] vals;
        private final int nVals;
        
        public BesselJResult(final double[] b, final int n) {
            this.vals = MathArrays.copyOf(b, b.length);
            this.nVals = n;
        }
        
        public double[] getVals() {
            return MathArrays.copyOf(this.vals, this.vals.length);
        }
        
        public int getnVals() {
            return this.nVals;
        }
    }
}
