package org.apache.commons.math3.optimization.linear;

import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.exception.MaxCountExceededException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.util.Precision;
import java.util.ArrayList;

@Deprecated
public class SimplexSolver extends AbstractLinearOptimizer
{
    private static final double DEFAULT_EPSILON = 1.0E-6;
    private static final int DEFAULT_ULPS = 10;
    private final double epsilon;
    private final int maxUlps;
    
    public SimplexSolver() {
        this(1.0E-6, 10);
    }
    
    public SimplexSolver(final double epsilon, final int maxUlps) {
        this.epsilon = epsilon;
        this.maxUlps = maxUlps;
    }
    
    private Integer getPivotColumn(final SimplexTableau tableau) {
        double minValue = 0.0;
        Integer minPos = null;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; ++i) {
            final double entry = tableau.getEntry(0, i);
            if (entry < minValue) {
                minValue = entry;
                minPos = i;
            }
        }
        return minPos;
    }
    
    private Integer getPivotRow(final SimplexTableau tableau, final int col) {
        List<Integer> minRatioPositions = new ArrayList<Integer>();
        double minRatio = Double.MAX_VALUE;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); ++i) {
            final double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
            final double entry = tableau.getEntry(i, col);
            if (Precision.compareTo(entry, 0.0, this.maxUlps) > 0) {
                final double ratio = rhs / entry;
                final int cmp = Double.compare(ratio, minRatio);
                if (cmp == 0) {
                    minRatioPositions.add(i);
                }
                else if (cmp < 0) {
                    minRatio = ratio;
                    minRatioPositions = new ArrayList<Integer>();
                    minRatioPositions.add(i);
                }
            }
        }
        if (minRatioPositions.size() == 0) {
            return null;
        }
        if (minRatioPositions.size() > 1) {
            if (tableau.getNumArtificialVariables() > 0) {
                for (final Integer row : minRatioPositions) {
                    for (int j = 0; j < tableau.getNumArtificialVariables(); ++j) {
                        final int column = j + tableau.getArtificialVariableOffset();
                        final double entry2 = tableau.getEntry(row, column);
                        if (Precision.equals(entry2, 1.0, this.maxUlps) && row.equals(tableau.getBasicRow(column))) {
                            return row;
                        }
                    }
                }
            }
            if (this.getIterations() < this.getMaxIterations() / 2) {
                Integer minRow = null;
                int minIndex = tableau.getWidth();
                final int varStart = tableau.getNumObjectiveFunctions();
                final int varEnd = tableau.getWidth() - 1;
                for (final Integer row2 : minRatioPositions) {
                    for (int k = varStart; k < varEnd && !row2.equals(minRow); ++k) {
                        final Integer basicRow = tableau.getBasicRow(k);
                        if (basicRow != null && basicRow.equals(row2) && k < minIndex) {
                            minIndex = k;
                            minRow = row2;
                        }
                    }
                }
                return minRow;
            }
        }
        return minRatioPositions.get(0);
    }
    
    protected void doIteration(final SimplexTableau tableau) throws MaxCountExceededException, UnboundedSolutionException {
        this.incrementIterationsCounter();
        final Integer pivotCol = this.getPivotColumn(tableau);
        final Integer pivotRow = this.getPivotRow(tableau, pivotCol);
        if (pivotRow == null) {
            throw new UnboundedSolutionException();
        }
        final double pivotVal = tableau.getEntry(pivotRow, pivotCol);
        tableau.divideRow(pivotRow, pivotVal);
        for (int i = 0; i < tableau.getHeight(); ++i) {
            if (i != pivotRow) {
                final double multiplier = tableau.getEntry(i, pivotCol);
                tableau.subtractRow(i, pivotRow, multiplier);
            }
        }
    }
    
    protected void solvePhase1(final SimplexTableau tableau) throws MaxCountExceededException, UnboundedSolutionException, NoFeasibleSolutionException {
        if (tableau.getNumArtificialVariables() == 0) {
            return;
        }
        while (!tableau.isOptimal()) {
            this.doIteration(tableau);
        }
        if (!Precision.equals(tableau.getEntry(0, tableau.getRhsOffset()), 0.0, this.epsilon)) {
            throw new NoFeasibleSolutionException();
        }
    }
    
    public PointValuePair doOptimize() throws MaxCountExceededException, UnboundedSolutionException, NoFeasibleSolutionException {
        final SimplexTableau tableau = new SimplexTableau(this.getFunction(), this.getConstraints(), this.getGoalType(), this.restrictToNonNegative(), this.epsilon, this.maxUlps);
        this.solvePhase1(tableau);
        tableau.dropPhase1Objective();
        while (!tableau.isOptimal()) {
            this.doIteration(tableau);
        }
        return tableau.getSolution();
    }
}
