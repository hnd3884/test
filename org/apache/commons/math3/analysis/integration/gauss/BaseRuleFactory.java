package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.TreeMap;
import org.apache.commons.math3.util.Pair;
import java.util.Map;

public abstract class BaseRuleFactory<T extends Number>
{
    private final Map<Integer, Pair<T[], T[]>> pointsAndWeights;
    private final Map<Integer, Pair<double[], double[]>> pointsAndWeightsDouble;
    
    public BaseRuleFactory() {
        this.pointsAndWeights = new TreeMap<Integer, Pair<T[], T[]>>();
        this.pointsAndWeightsDouble = new TreeMap<Integer, Pair<double[], double[]>>();
    }
    
    public Pair<double[], double[]> getRule(final int numberOfPoints) throws NotStrictlyPositiveException, DimensionMismatchException {
        if (numberOfPoints <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_POINTS, numberOfPoints);
        }
        Pair<double[], double[]> cached = this.pointsAndWeightsDouble.get(numberOfPoints);
        if (cached == null) {
            final Pair<T[], T[]> rule = this.getRuleInternal(numberOfPoints);
            cached = convertToDouble(rule);
            this.pointsAndWeightsDouble.put(numberOfPoints, cached);
        }
        return new Pair<double[], double[]>(cached.getFirst().clone(), cached.getSecond().clone());
    }
    
    protected synchronized Pair<T[], T[]> getRuleInternal(final int numberOfPoints) throws DimensionMismatchException {
        final Pair<T[], T[]> rule = this.pointsAndWeights.get(numberOfPoints);
        if (rule == null) {
            this.addRule(this.computeRule(numberOfPoints));
            return this.getRuleInternal(numberOfPoints);
        }
        return rule;
    }
    
    protected void addRule(final Pair<T[], T[]> rule) throws DimensionMismatchException {
        if (rule.getFirst().length != rule.getSecond().length) {
            throw new DimensionMismatchException(rule.getFirst().length, rule.getSecond().length);
        }
        this.pointsAndWeights.put(rule.getFirst().length, rule);
    }
    
    protected abstract Pair<T[], T[]> computeRule(final int p0) throws DimensionMismatchException;
    
    private static <T extends Number> Pair<double[], double[]> convertToDouble(final Pair<T[], T[]> rule) {
        final T[] pT = rule.getFirst();
        final T[] wT = rule.getSecond();
        final int len = pT.length;
        final double[] pD = new double[len];
        final double[] wD = new double[len];
        for (int i = 0; i < len; ++i) {
            pD[i] = pT[i].doubleValue();
            wD[i] = wT[i].doubleValue();
        }
        return new Pair<double[], double[]>(pD, wD);
    }
}
