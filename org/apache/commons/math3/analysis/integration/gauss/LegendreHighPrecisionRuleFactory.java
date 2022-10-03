package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.Pair;
import java.math.MathContext;
import java.math.BigDecimal;

public class LegendreHighPrecisionRuleFactory extends BaseRuleFactory<BigDecimal>
{
    private final MathContext mContext;
    private final BigDecimal two;
    private final BigDecimal minusOne;
    private final BigDecimal oneHalf;
    
    public LegendreHighPrecisionRuleFactory() {
        this(MathContext.DECIMAL128);
    }
    
    public LegendreHighPrecisionRuleFactory(final MathContext mContext) {
        this.mContext = mContext;
        this.two = new BigDecimal("2", mContext);
        this.minusOne = new BigDecimal("-1", mContext);
        this.oneHalf = new BigDecimal("0.5", mContext);
    }
    
    @Override
    protected Pair<BigDecimal[], BigDecimal[]> computeRule(final int numberOfPoints) throws DimensionMismatchException {
        if (numberOfPoints == 1) {
            return new Pair<BigDecimal[], BigDecimal[]>(new BigDecimal[] { BigDecimal.ZERO }, new BigDecimal[] { this.two });
        }
        final BigDecimal[] previousPoints = this.getRuleInternal(numberOfPoints - 1).getFirst();
        final BigDecimal[] points = new BigDecimal[numberOfPoints];
        final BigDecimal[] weights = new BigDecimal[numberOfPoints];
        final int iMax = numberOfPoints / 2;
        for (int i = 0; i < iMax; ++i) {
            BigDecimal a = (i == 0) ? this.minusOne : previousPoints[i - 1];
            BigDecimal b = (iMax == 1) ? BigDecimal.ONE : previousPoints[i];
            BigDecimal pma = BigDecimal.ONE;
            BigDecimal pa = a;
            BigDecimal pmb = BigDecimal.ONE;
            BigDecimal pb = b;
            for (int j = 1; j < numberOfPoints; ++j) {
                final BigDecimal b_two_j_p_1 = new BigDecimal(2 * j + 1, this.mContext);
                final BigDecimal b_j = new BigDecimal(j, this.mContext);
                final BigDecimal b_j_p_1 = new BigDecimal(j + 1, this.mContext);
                BigDecimal tmp1 = a.multiply(b_two_j_p_1, this.mContext);
                tmp1 = pa.multiply(tmp1, this.mContext);
                BigDecimal tmp2 = pma.multiply(b_j, this.mContext);
                BigDecimal ppa = tmp1.subtract(tmp2, this.mContext);
                ppa = ppa.divide(b_j_p_1, this.mContext);
                tmp1 = b.multiply(b_two_j_p_1, this.mContext);
                tmp1 = pb.multiply(tmp1, this.mContext);
                tmp2 = pmb.multiply(b_j, this.mContext);
                BigDecimal ppb = tmp1.subtract(tmp2, this.mContext);
                ppb = ppb.divide(b_j_p_1, this.mContext);
                pma = pa;
                pa = ppa;
                pmb = pb;
                pb = ppb;
            }
            BigDecimal c = a.add(b, this.mContext).multiply(this.oneHalf, this.mContext);
            BigDecimal pmc = BigDecimal.ONE;
            BigDecimal pc = c;
            boolean done = false;
            while (!done) {
                BigDecimal tmp1 = b.subtract(a, this.mContext);
                BigDecimal tmp2 = c.ulp().multiply(BigDecimal.TEN, this.mContext);
                done = (tmp1.compareTo(tmp2) <= 0);
                pmc = BigDecimal.ONE;
                pc = c;
                for (int k = 1; k < numberOfPoints; ++k) {
                    final BigDecimal b_two_j_p_2 = new BigDecimal(2 * k + 1, this.mContext);
                    final BigDecimal b_j2 = new BigDecimal(k, this.mContext);
                    final BigDecimal b_j_p_2 = new BigDecimal(k + 1, this.mContext);
                    tmp1 = c.multiply(b_two_j_p_2, this.mContext);
                    tmp1 = pc.multiply(tmp1, this.mContext);
                    tmp2 = pmc.multiply(b_j2, this.mContext);
                    BigDecimal ppc = tmp1.subtract(tmp2, this.mContext);
                    ppc = ppc.divide(b_j_p_2, this.mContext);
                    pmc = pc;
                    pc = ppc;
                }
                if (!done) {
                    if (pa.signum() * pc.signum() <= 0) {
                        b = c;
                        pmb = pmc;
                        pb = pc;
                    }
                    else {
                        a = c;
                        pma = pmc;
                        pa = pc;
                    }
                    c = a.add(b, this.mContext).multiply(this.oneHalf, this.mContext);
                }
            }
            final BigDecimal nP = new BigDecimal(numberOfPoints, this.mContext);
            BigDecimal tmp3 = pmc.subtract(c.multiply(pc, this.mContext), this.mContext);
            tmp3 = tmp3.multiply(nP);
            tmp3 = tmp3.pow(2, this.mContext);
            BigDecimal tmp4 = c.pow(2, this.mContext);
            tmp4 = BigDecimal.ONE.subtract(tmp4, this.mContext);
            tmp4 = tmp4.multiply(this.two, this.mContext);
            tmp4 = tmp4.divide(tmp3, this.mContext);
            points[i] = c;
            weights[i] = tmp4;
            final int idx = numberOfPoints - i - 1;
            points[idx] = c.negate(this.mContext);
            weights[idx] = tmp4;
        }
        if (numberOfPoints % 2 != 0) {
            BigDecimal pmc2 = BigDecimal.ONE;
            for (int l = 1; l < numberOfPoints; l += 2) {
                final BigDecimal b_j3 = new BigDecimal(l, this.mContext);
                final BigDecimal b_j_p_3 = new BigDecimal(l + 1, this.mContext);
                pmc2 = pmc2.multiply(b_j3, this.mContext);
                pmc2 = pmc2.divide(b_j_p_3, this.mContext);
                pmc2 = pmc2.negate(this.mContext);
            }
            final BigDecimal nP2 = new BigDecimal(numberOfPoints, this.mContext);
            BigDecimal tmp5 = pmc2.multiply(nP2, this.mContext);
            tmp5 = tmp5.pow(2, this.mContext);
            final BigDecimal tmp6 = this.two.divide(tmp5, this.mContext);
            points[iMax] = BigDecimal.ZERO;
            weights[iMax] = tmp6;
        }
        return new Pair<BigDecimal[], BigDecimal[]>(points, weights);
    }
}
