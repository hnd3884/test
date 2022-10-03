package org.apache.commons.math3.optim.linear;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import java.util.ArrayList;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.OptimizationData;

public class SimplexSolver extends LinearOptimizer
{
    static final int DEFAULT_ULPS = 10;
    static final double DEFAULT_CUT_OFF = 1.0E-10;
    private static final double DEFAULT_EPSILON = 1.0E-6;
    private final double epsilon;
    private final int maxUlps;
    private final double cutOff;
    private PivotSelectionRule pivotSelection;
    private SolutionCallback solutionCallback;
    
    public SimplexSolver() {
        this(1.0E-6, 10, 1.0E-10);
    }
    
    public SimplexSolver(final double epsilon) {
        this(epsilon, 10, 1.0E-10);
    }
    
    public SimplexSolver(final double epsilon, final int maxUlps) {
        this(epsilon, maxUlps, 1.0E-10);
    }
    
    public SimplexSolver(final double epsilon, final int maxUlps, final double cutOff) {
        this.epsilon = epsilon;
        this.maxUlps = maxUlps;
        this.cutOff = cutOff;
        this.pivotSelection = PivotSelectionRule.DANTZIG;
    }
    
    @Override
    public PointValuePair optimize(final OptimizationData... optData) throws TooManyIterationsException {
        return super.optimize(optData);
    }
    
    @Override
    protected void parseOptimizationData(final OptimizationData... optData) {
        super.parseOptimizationData(optData);
        this.solutionCallback = null;
        for (final OptimizationData data : optData) {
            if (data instanceof SolutionCallback) {
                this.solutionCallback = (SolutionCallback)data;
            }
            else if (data instanceof PivotSelectionRule) {
                this.pivotSelection = (PivotSelectionRule)data;
            }
        }
    }
    
    private Integer getPivotColumn(final SimplexTableau tableau) {
        double minValue = 0.0;
        Integer minPos = null;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; ++i) {
            final double entry = tableau.getEntry(0, i);
            if (entry < minValue) {
                minValue = entry;
                minPos = i;
                if (this.pivotSelection == PivotSelectionRule.BLAND && this.isValidPivotColumn(tableau, i)) {
                    break;
                }
            }
        }
        return minPos;
    }
    
    private boolean isValidPivotColumn(final SimplexTableau tableau, final int col) {
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); ++i) {
            final double entry = tableau.getEntry(i, col);
            if (Precision.compareTo(entry, 0.0, this.cutOff) > 0) {
                return true;
            }
        }
        return false;
    }
    
    private Integer getPivotRow(final SimplexTableau tableau, final int col) {
        final List<Integer> minRatioPositions = new ArrayList<Integer>();
        double minRatio = Double.MAX_VALUE;
        for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); ++i) {
            final double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
            final double entry = tableau.getEntry(i, col);
            if (Precision.compareTo(entry, 0.0, this.cutOff) > 0) {
                final double ratio = FastMath.abs(rhs / entry);
                final int cmp = Double.compare(ratio, minRatio);
                if (cmp == 0) {
                    minRatioPositions.add(i);
                }
                else if (cmp < 0) {
                    minRatio = ratio;
                    minRatioPositions.clear();
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
            Integer minRow = null;
            int minIndex = tableau.getWidth();
            for (final Integer row2 : minRatioPositions) {
                final int basicVar = tableau.getBasicVariable(row2);
                if (basicVar < minIndex) {
                    minIndex = basicVar;
                    minRow = row2;
                }
            }
            return minRow;
        }
        return minRatioPositions.get(0);
    }
    
    protected void doIteration(final SimplexTableau tableau) throws TooManyIterationsException, UnboundedSolutionException {
        this.incrementIterationCount();
        final Integer pivotCol = this.getPivotColumn(tableau);
        final Integer pivotRow = this.getPivotRow(tableau, pivotCol);
        if (pivotRow == null) {
            throw new UnboundedSolutionException();
        }
        tableau.performRowOperations(pivotCol, pivotRow);
    }
    
    protected void solvePhase1(final SimplexTableau tableau) throws TooManyIterationsException, UnboundedSolutionException, NoFeasibleSolutionException {
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
    
    public PointValuePair doOptimize() throws TooManyIterationsException, UnboundedSolutionException, NoFeasibleSolutionException {
        if (this.solutionCallback != null) {
            this.solutionCallback.setTableau(null);
        }
        final SimplexTableau tableau = new SimplexTableau(this.getFunction(), this.getConstraints(), this.getGoalType(), this.isRestrictedToNonNegative(), this.epsilon, this.maxUlps);
        this.solvePhase1(tableau);
        tableau.dropPhase1Objective();
        if (this.solutionCallback != null) {
            this.solutionCallback.setTableau(tableau);
        }
        while (!tableau.isOptimal()) {
            this.doIteration(tableau);
        }
        final PointValuePair solution = tableau.getSolution();
        if (this.isRestrictedToNonNegative()) {
            final double[] coeff = solution.getPoint();
            for (int i = 0; i < coeff.length; ++i) {
                if (Precision.compareTo(coeff[i], 0.0, this.epsilon) < 0) {
                    throw new NoFeasibleSolutionException();
                }
            }
        }
        return solution;
    }
}
