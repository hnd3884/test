package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import java.util.concurrent.atomic.AtomicReference;

public class DSCompiler
{
    private static AtomicReference<DSCompiler[][]> compilers;
    private final int parameters;
    private final int order;
    private final int[][] sizes;
    private final int[][] derivativesIndirection;
    private final int[] lowerIndirection;
    private final int[][][] multIndirection;
    private final int[][][] compIndirection;
    
    private DSCompiler(final int parameters, final int order, final DSCompiler valueCompiler, final DSCompiler derivativeCompiler) throws NumberIsTooLargeException {
        this.parameters = parameters;
        this.order = order;
        this.sizes = compileSizes(parameters, order, valueCompiler);
        this.derivativesIndirection = compileDerivativesIndirection(parameters, order, valueCompiler, derivativeCompiler);
        this.lowerIndirection = compileLowerIndirection(parameters, order, valueCompiler, derivativeCompiler);
        this.multIndirection = compileMultiplicationIndirection(parameters, order, valueCompiler, derivativeCompiler, this.lowerIndirection);
        this.compIndirection = compileCompositionIndirection(parameters, order, valueCompiler, derivativeCompiler, this.sizes, this.derivativesIndirection);
    }
    
    public static DSCompiler getCompiler(final int parameters, final int order) throws NumberIsTooLargeException {
        final DSCompiler[][] cache = DSCompiler.compilers.get();
        if (cache != null && cache.length > parameters && cache[parameters].length > order && cache[parameters][order] != null) {
            return cache[parameters][order];
        }
        final int maxParameters = FastMath.max(parameters, (cache == null) ? 0 : cache.length);
        final int maxOrder = FastMath.max(order, (cache == null) ? 0 : cache[0].length);
        final DSCompiler[][] newCache = new DSCompiler[maxParameters + 1][maxOrder + 1];
        if (cache != null) {
            for (int i = 0; i < cache.length; ++i) {
                System.arraycopy(cache[i], 0, newCache[i], 0, cache[i].length);
            }
        }
        for (int diag = 0; diag <= parameters + order; ++diag) {
            for (int o = FastMath.max(0, diag - parameters); o <= FastMath.min(order, diag); ++o) {
                final int p = diag - o;
                if (newCache[p][o] == null) {
                    final DSCompiler valueCompiler = (p == 0) ? null : newCache[p - 1][o];
                    final DSCompiler derivativeCompiler = (o == 0) ? null : newCache[p][o - 1];
                    newCache[p][o] = new DSCompiler(p, o, valueCompiler, derivativeCompiler);
                }
            }
        }
        DSCompiler.compilers.compareAndSet(cache, newCache);
        return newCache[parameters][order];
    }
    
    private static int[][] compileSizes(final int parameters, final int order, final DSCompiler valueCompiler) {
        final int[][] sizes = new int[parameters + 1][order + 1];
        if (parameters == 0) {
            Arrays.fill(sizes[0], 1);
        }
        else {
            System.arraycopy(valueCompiler.sizes, 0, sizes, 0, parameters);
            sizes[parameters][0] = 1;
            for (int i = 0; i < order; ++i) {
                sizes[parameters][i + 1] = sizes[parameters][i] + sizes[parameters - 1][i + 1];
            }
        }
        return sizes;
    }
    
    private static int[][] compileDerivativesIndirection(final int parameters, final int order, final DSCompiler valueCompiler, final DSCompiler derivativeCompiler) {
        if (parameters == 0 || order == 0) {
            return new int[1][parameters];
        }
        final int vSize = valueCompiler.derivativesIndirection.length;
        final int dSize = derivativeCompiler.derivativesIndirection.length;
        final int[][] derivativesIndirection = new int[vSize + dSize][parameters];
        for (int i = 0; i < vSize; ++i) {
            System.arraycopy(valueCompiler.derivativesIndirection[i], 0, derivativesIndirection[i], 0, parameters - 1);
        }
        for (int i = 0; i < dSize; ++i) {
            System.arraycopy(derivativeCompiler.derivativesIndirection[i], 0, derivativesIndirection[vSize + i], 0, parameters);
            final int[] array = derivativesIndirection[vSize + i];
            final int n = parameters - 1;
            ++array[n];
        }
        return derivativesIndirection;
    }
    
    private static int[] compileLowerIndirection(final int parameters, final int order, final DSCompiler valueCompiler, final DSCompiler derivativeCompiler) {
        if (parameters == 0 || order <= 1) {
            return new int[] { 0 };
        }
        final int vSize = valueCompiler.lowerIndirection.length;
        final int dSize = derivativeCompiler.lowerIndirection.length;
        final int[] lowerIndirection = new int[vSize + dSize];
        System.arraycopy(valueCompiler.lowerIndirection, 0, lowerIndirection, 0, vSize);
        for (int i = 0; i < dSize; ++i) {
            lowerIndirection[vSize + i] = valueCompiler.getSize() + derivativeCompiler.lowerIndirection[i];
        }
        return lowerIndirection;
    }
    
    private static int[][][] compileMultiplicationIndirection(final int parameters, final int order, final DSCompiler valueCompiler, final DSCompiler derivativeCompiler, final int[] lowerIndirection) {
        if (parameters == 0 || order == 0) {
            return new int[][][] { { { 1, 0, 0 } } };
        }
        final int vSize = valueCompiler.multIndirection.length;
        final int dSize = derivativeCompiler.multIndirection.length;
        final int[][][] multIndirection = new int[vSize + dSize][][];
        System.arraycopy(valueCompiler.multIndirection, 0, multIndirection, 0, vSize);
        for (int i = 0; i < dSize; ++i) {
            final int[][] dRow = derivativeCompiler.multIndirection[i];
            final List<int[]> row = new ArrayList<int[]>(dRow.length * 2);
            for (int j = 0; j < dRow.length; ++j) {
                row.add(new int[] { dRow[j][0], lowerIndirection[dRow[j][1]], vSize + dRow[j][2] });
                row.add(new int[] { dRow[j][0], vSize + dRow[j][1], lowerIndirection[dRow[j][2]] });
            }
            final List<int[]> combined = new ArrayList<int[]>(row.size());
            for (int k = 0; k < row.size(); ++k) {
                final int[] termJ = row.get(k);
                if (termJ[0] > 0) {
                    for (int l = k + 1; l < row.size(); ++l) {
                        final int[] termK = row.get(l);
                        if (termJ[1] == termK[1] && termJ[2] == termK[2]) {
                            final int[] array = termJ;
                            final int n = 0;
                            array[n] += termK[0];
                            termK[0] = 0;
                        }
                    }
                    combined.add(termJ);
                }
            }
            multIndirection[vSize + i] = combined.toArray(new int[combined.size()][]);
        }
        return multIndirection;
    }
    
    private static int[][][] compileCompositionIndirection(final int parameters, final int order, final DSCompiler valueCompiler, final DSCompiler derivativeCompiler, final int[][] sizes, final int[][] derivativesIndirection) throws NumberIsTooLargeException {
        if (parameters == 0 || order == 0) {
            return new int[][][] { { { 1, 0 } } };
        }
        final int vSize = valueCompiler.compIndirection.length;
        final int dSize = derivativeCompiler.compIndirection.length;
        final int[][][] compIndirection = new int[vSize + dSize][][];
        System.arraycopy(valueCompiler.compIndirection, 0, compIndirection, 0, vSize);
        for (int i = 0; i < dSize; ++i) {
            final List<int[]> row = new ArrayList<int[]>();
            for (final int[] term : derivativeCompiler.compIndirection[i]) {
                final int[] derivedTermF = new int[term.length + 1];
                derivedTermF[0] = term[0];
                derivedTermF[1] = term[1] + 1;
                final int[] orders = new int[parameters];
                orders[parameters - 1] = 1;
                derivedTermF[term.length] = getPartialDerivativeIndex(parameters, order, sizes, orders);
                for (int j = 2; j < term.length; ++j) {
                    derivedTermF[j] = convertIndex(term[j], parameters, derivativeCompiler.derivativesIndirection, parameters, order, sizes);
                }
                Arrays.sort(derivedTermF, 2, derivedTermF.length);
                row.add(derivedTermF);
                for (int l = 2; l < term.length; ++l) {
                    final int[] derivedTermG = new int[term.length];
                    derivedTermG[0] = term[0];
                    derivedTermG[1] = term[1];
                    for (int k = 2; k < term.length; ++k) {
                        derivedTermG[k] = convertIndex(term[k], parameters, derivativeCompiler.derivativesIndirection, parameters, order, sizes);
                        if (k == l) {
                            System.arraycopy(derivativesIndirection[derivedTermG[k]], 0, orders, 0, parameters);
                            final int[] array = orders;
                            final int n = parameters - 1;
                            ++array[n];
                            derivedTermG[k] = getPartialDerivativeIndex(parameters, order, sizes, orders);
                        }
                    }
                    Arrays.sort(derivedTermG, 2, derivedTermG.length);
                    row.add(derivedTermG);
                }
            }
            final List<int[]> combined = new ArrayList<int[]>(row.size());
            for (int m = 0; m < row.size(); ++m) {
                final int[] termJ = row.get(m);
                if (termJ[0] > 0) {
                    for (int k2 = m + 1; k2 < row.size(); ++k2) {
                        final int[] termK = row.get(k2);
                        boolean equals = termJ.length == termK.length;
                        for (int l = 1; equals && l < termJ.length; equals &= (termJ[l] == termK[l]), ++l) {}
                        if (equals) {
                            final int[] array2 = termJ;
                            final int n2 = 0;
                            array2[n2] += termK[0];
                            termK[0] = 0;
                        }
                    }
                    combined.add(termJ);
                }
            }
            compIndirection[vSize + i] = combined.toArray(new int[combined.size()][]);
        }
        return compIndirection;
    }
    
    public int getPartialDerivativeIndex(final int... orders) throws DimensionMismatchException, NumberIsTooLargeException {
        if (orders.length != this.getFreeParameters()) {
            throw new DimensionMismatchException(orders.length, this.getFreeParameters());
        }
        return getPartialDerivativeIndex(this.parameters, this.order, this.sizes, orders);
    }
    
    private static int getPartialDerivativeIndex(final int parameters, final int order, final int[][] sizes, final int... orders) throws NumberIsTooLargeException {
        int index = 0;
        int m = order;
        int ordersSum = 0;
        for (int i = parameters - 1; i >= 0; --i) {
            int derivativeOrder = orders[i];
            ordersSum += derivativeOrder;
            if (ordersSum > order) {
                throw new NumberIsTooLargeException(ordersSum, order, true);
            }
            while (derivativeOrder-- > 0) {
                index += sizes[i][m--];
            }
        }
        return index;
    }
    
    private static int convertIndex(final int index, final int srcP, final int[][] srcDerivativesIndirection, final int destP, final int destO, final int[][] destSizes) throws NumberIsTooLargeException {
        final int[] orders = new int[destP];
        System.arraycopy(srcDerivativesIndirection[index], 0, orders, 0, FastMath.min(srcP, destP));
        return getPartialDerivativeIndex(destP, destO, destSizes, orders);
    }
    
    public int[] getPartialDerivativeOrders(final int index) {
        return this.derivativesIndirection[index];
    }
    
    public int getFreeParameters() {
        return this.parameters;
    }
    
    public int getOrder() {
        return this.order;
    }
    
    public int getSize() {
        return this.sizes[this.parameters][this.order];
    }
    
    public void linearCombination(final double a1, final double[] c1, final int offset1, final double a2, final double[] c2, final int offset2, final double[] result, final int resultOffset) {
        for (int i = 0; i < this.getSize(); ++i) {
            result[resultOffset + i] = MathArrays.linearCombination(a1, c1[offset1 + i], a2, c2[offset2 + i]);
        }
    }
    
    public void linearCombination(final double a1, final double[] c1, final int offset1, final double a2, final double[] c2, final int offset2, final double a3, final double[] c3, final int offset3, final double[] result, final int resultOffset) {
        for (int i = 0; i < this.getSize(); ++i) {
            result[resultOffset + i] = MathArrays.linearCombination(a1, c1[offset1 + i], a2, c2[offset2 + i], a3, c3[offset3 + i]);
        }
    }
    
    public void linearCombination(final double a1, final double[] c1, final int offset1, final double a2, final double[] c2, final int offset2, final double a3, final double[] c3, final int offset3, final double a4, final double[] c4, final int offset4, final double[] result, final int resultOffset) {
        for (int i = 0; i < this.getSize(); ++i) {
            result[resultOffset + i] = MathArrays.linearCombination(a1, c1[offset1 + i], a2, c2[offset2 + i], a3, c3[offset3 + i], a4, c4[offset4 + i]);
        }
    }
    
    public void add(final double[] lhs, final int lhsOffset, final double[] rhs, final int rhsOffset, final double[] result, final int resultOffset) {
        for (int i = 0; i < this.getSize(); ++i) {
            result[resultOffset + i] = lhs[lhsOffset + i] + rhs[rhsOffset + i];
        }
    }
    
    public void subtract(final double[] lhs, final int lhsOffset, final double[] rhs, final int rhsOffset, final double[] result, final int resultOffset) {
        for (int i = 0; i < this.getSize(); ++i) {
            result[resultOffset + i] = lhs[lhsOffset + i] - rhs[rhsOffset + i];
        }
    }
    
    public void multiply(final double[] lhs, final int lhsOffset, final double[] rhs, final int rhsOffset, final double[] result, final int resultOffset) {
        for (int i = 0; i < this.multIndirection.length; ++i) {
            final int[][] mappingI = this.multIndirection[i];
            double r = 0.0;
            for (int j = 0; j < mappingI.length; ++j) {
                r += mappingI[j][0] * lhs[lhsOffset + mappingI[j][1]] * rhs[rhsOffset + mappingI[j][2]];
            }
            result[resultOffset + i] = r;
        }
    }
    
    public void divide(final double[] lhs, final int lhsOffset, final double[] rhs, final int rhsOffset, final double[] result, final int resultOffset) {
        final double[] reciprocal = new double[this.getSize()];
        this.pow(rhs, lhsOffset, -1, reciprocal, 0);
        this.multiply(lhs, lhsOffset, reciprocal, 0, result, resultOffset);
    }
    
    public void remainder(final double[] lhs, final int lhsOffset, final double[] rhs, final int rhsOffset, final double[] result, final int resultOffset) {
        final double rem = FastMath.IEEEremainder(lhs[lhsOffset], rhs[rhsOffset]);
        final double k = FastMath.rint((lhs[lhsOffset] - rem) / rhs[rhsOffset]);
        result[resultOffset] = rem;
        for (int i = 1; i < this.getSize(); ++i) {
            result[resultOffset + i] = lhs[lhsOffset + i] - k * rhs[rhsOffset + i];
        }
    }
    
    public void pow(final double a, final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        if (a == 0.0) {
            if (operand[operandOffset] == 0.0) {
                function[0] = 1.0;
                double infinity = Double.POSITIVE_INFINITY;
                for (int i = 1; i < function.length; ++i) {
                    infinity = -infinity;
                    function[i] = infinity;
                }
            }
            else if (operand[operandOffset] < 0.0) {
                Arrays.fill(function, Double.NaN);
            }
        }
        else {
            function[0] = FastMath.pow(a, operand[operandOffset]);
            final double lnA = FastMath.log(a);
            for (int i = 1; i < function.length; ++i) {
                function[i] = lnA * function[i - 1];
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void pow(final double[] operand, final int operandOffset, final double p, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        double xk = FastMath.pow(operand[operandOffset], p - this.order);
        for (int i = this.order; i > 0; --i) {
            function[i] = xk;
            xk *= operand[operandOffset];
        }
        function[0] = xk;
        double coefficient = p;
        for (int j = 1; j <= this.order; ++j) {
            final double[] array = function;
            final int n = j;
            array[n] *= coefficient;
            coefficient *= p - j;
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void pow(final double[] operand, final int operandOffset, final int n, final double[] result, final int resultOffset) {
        if (n == 0) {
            result[resultOffset] = 1.0;
            Arrays.fill(result, resultOffset + 1, resultOffset + this.getSize(), 0.0);
            return;
        }
        final double[] function = new double[1 + this.order];
        if (n > 0) {
            final int maxOrder = FastMath.min(this.order, n);
            double xk = FastMath.pow(operand[operandOffset], n - maxOrder);
            for (int i = maxOrder; i > 0; --i) {
                function[i] = xk;
                xk *= operand[operandOffset];
            }
            function[0] = xk;
        }
        else {
            final double inv = 1.0 / operand[operandOffset];
            double xk2 = FastMath.pow(inv, -n);
            for (int j = 0; j <= this.order; ++j) {
                function[j] = xk2;
                xk2 *= inv;
            }
        }
        double coefficient = n;
        for (int k = 1; k <= this.order; ++k) {
            final double[] array = function;
            final int n2 = k;
            array[n2] *= coefficient;
            coefficient *= n - k;
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void pow(final double[] x, final int xOffset, final double[] y, final int yOffset, final double[] result, final int resultOffset) {
        final double[] logX = new double[this.getSize()];
        this.log(x, xOffset, logX, 0);
        final double[] yLogX = new double[this.getSize()];
        this.multiply(logX, 0, y, yOffset, yLogX, 0);
        this.exp(yLogX, 0, result, resultOffset);
    }
    
    public void rootN(final double[] operand, final int operandOffset, final int n, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        double xk;
        if (n == 2) {
            function[0] = FastMath.sqrt(operand[operandOffset]);
            xk = 0.5 / function[0];
        }
        else if (n == 3) {
            function[0] = FastMath.cbrt(operand[operandOffset]);
            xk = 1.0 / (3.0 * function[0] * function[0]);
        }
        else {
            function[0] = FastMath.pow(operand[operandOffset], 1.0 / n);
            xk = 1.0 / (n * FastMath.pow(function[0], n - 1));
        }
        final double nReciprocal = 1.0 / n;
        final double xReciprocal = 1.0 / operand[operandOffset];
        for (int i = 1; i <= this.order; ++i) {
            function[i] = xk;
            xk *= xReciprocal * (nReciprocal - i);
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void exp(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        Arrays.fill(function, FastMath.exp(operand[operandOffset]));
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void expm1(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        function[0] = FastMath.expm1(operand[operandOffset]);
        Arrays.fill(function, 1, 1 + this.order, FastMath.exp(operand[operandOffset]));
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void log(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        function[0] = FastMath.log(operand[operandOffset]);
        if (this.order > 0) {
            double xk;
            final double inv = xk = 1.0 / operand[operandOffset];
            for (int i = 1; i <= this.order; ++i) {
                function[i] = xk;
                xk *= -i * inv;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void log1p(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        function[0] = FastMath.log1p(operand[operandOffset]);
        if (this.order > 0) {
            double xk;
            final double inv = xk = 1.0 / (1.0 + operand[operandOffset]);
            for (int i = 1; i <= this.order; ++i) {
                function[i] = xk;
                xk *= -i * inv;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void log10(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        function[0] = FastMath.log10(operand[operandOffset]);
        if (this.order > 0) {
            final double inv = 1.0 / operand[operandOffset];
            double xk = inv / FastMath.log(10.0);
            for (int i = 1; i <= this.order; ++i) {
                function[i] = xk;
                xk *= -i * inv;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void cos(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        function[0] = FastMath.cos(operand[operandOffset]);
        if (this.order > 0) {
            function[1] = -FastMath.sin(operand[operandOffset]);
            for (int i = 2; i <= this.order; ++i) {
                function[i] = -function[i - 2];
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void sin(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        function[0] = FastMath.sin(operand[operandOffset]);
        if (this.order > 0) {
            function[1] = FastMath.cos(operand[operandOffset]);
            for (int i = 2; i <= this.order; ++i) {
                function[i] = -function[i - 2];
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void tan(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        final double t = FastMath.tan(operand[operandOffset]);
        function[0] = t;
        if (this.order > 0) {
            final double[] p = new double[this.order + 2];
            p[1] = 1.0;
            final double t2 = t * t;
            for (int n = 1; n <= this.order; ++n) {
                double v = 0.0;
                p[n + 1] = n * p[n];
                for (int k = n + 1; k >= 0; k -= 2) {
                    v = v * t2 + p[k];
                    if (k > 2) {
                        p[k - 2] = (k - 1) * p[k - 1] + (k - 3) * p[k - 3];
                    }
                    else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 0x1) == 0x0) {
                    v *= t;
                }
                function[n] = v;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void acos(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        final double x = operand[operandOffset];
        function[0] = FastMath.acos(x);
        if (this.order > 0) {
            final double[] p = new double[this.order];
            p[0] = -1.0;
            final double x2 = x * x;
            final double f = 1.0 / (1.0 - x2);
            double coeff = FastMath.sqrt(f);
            function[1] = coeff * p[0];
            for (int n = 2; n <= this.order; ++n) {
                double v = 0.0;
                p[n - 1] = (n - 1) * p[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = v * x2 + p[k];
                    if (k > 2) {
                        p[k - 2] = (k - 1) * p[k - 1] + (2 * n - k) * p[k - 3];
                    }
                    else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 0x1) == 0x0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void asin(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        final double x = operand[operandOffset];
        function[0] = FastMath.asin(x);
        if (this.order > 0) {
            final double[] p = new double[this.order];
            p[0] = 1.0;
            final double x2 = x * x;
            final double f = 1.0 / (1.0 - x2);
            double coeff = FastMath.sqrt(f);
            function[1] = coeff * p[0];
            for (int n = 2; n <= this.order; ++n) {
                double v = 0.0;
                p[n - 1] = (n - 1) * p[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = v * x2 + p[k];
                    if (k > 2) {
                        p[k - 2] = (k - 1) * p[k - 1] + (2 * n - k) * p[k - 3];
                    }
                    else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 0x1) == 0x0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void atan(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        final double x = operand[operandOffset];
        function[0] = FastMath.atan(x);
        if (this.order > 0) {
            final double[] q = new double[this.order];
            q[0] = 1.0;
            final double x2 = x * x;
            double coeff;
            final double f = coeff = 1.0 / (1.0 + x2);
            function[1] = coeff * q[0];
            for (int n = 2; n <= this.order; ++n) {
                double v = 0.0;
                q[n - 1] = -n * q[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = v * x2 + q[k];
                    if (k > 2) {
                        q[k - 2] = (k - 1) * q[k - 1] + (k - 1 - 2 * n) * q[k - 3];
                    }
                    else if (k == 2) {
                        q[0] = q[1];
                    }
                }
                if ((n & 0x1) == 0x0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void atan2(final double[] y, final int yOffset, final double[] x, final int xOffset, final double[] result, final int resultOffset) {
        final double[] tmp1 = new double[this.getSize()];
        this.multiply(x, xOffset, x, xOffset, tmp1, 0);
        final double[] tmp2 = new double[this.getSize()];
        this.multiply(y, yOffset, y, yOffset, tmp2, 0);
        this.add(tmp1, 0, tmp2, 0, tmp2, 0);
        this.rootN(tmp2, 0, 2, tmp1, 0);
        if (x[xOffset] >= 0.0) {
            this.add(tmp1, 0, x, xOffset, tmp2, 0);
            this.divide(y, yOffset, tmp2, 0, tmp1, 0);
            this.atan(tmp1, 0, tmp2, 0);
            for (int i = 0; i < tmp2.length; ++i) {
                result[resultOffset + i] = 2.0 * tmp2[i];
            }
        }
        else {
            this.subtract(tmp1, 0, x, xOffset, tmp2, 0);
            this.divide(y, yOffset, tmp2, 0, tmp1, 0);
            this.atan(tmp1, 0, tmp2, 0);
            result[resultOffset] = ((tmp2[0] <= 0.0) ? -3.141592653589793 : 3.141592653589793) - 2.0 * tmp2[0];
            for (int i = 1; i < tmp2.length; ++i) {
                result[resultOffset + i] = -2.0 * tmp2[i];
            }
        }
        result[resultOffset] = FastMath.atan2(y[yOffset], x[xOffset]);
    }
    
    public void cosh(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        function[0] = FastMath.cosh(operand[operandOffset]);
        if (this.order > 0) {
            function[1] = FastMath.sinh(operand[operandOffset]);
            for (int i = 2; i <= this.order; ++i) {
                function[i] = function[i - 2];
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void sinh(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        function[0] = FastMath.sinh(operand[operandOffset]);
        if (this.order > 0) {
            function[1] = FastMath.cosh(operand[operandOffset]);
            for (int i = 2; i <= this.order; ++i) {
                function[i] = function[i - 2];
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void tanh(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        final double t = FastMath.tanh(operand[operandOffset]);
        function[0] = t;
        if (this.order > 0) {
            final double[] p = new double[this.order + 2];
            p[1] = 1.0;
            final double t2 = t * t;
            for (int n = 1; n <= this.order; ++n) {
                double v = 0.0;
                p[n + 1] = -n * p[n];
                for (int k = n + 1; k >= 0; k -= 2) {
                    v = v * t2 + p[k];
                    if (k > 2) {
                        p[k - 2] = (k - 1) * p[k - 1] - (k - 3) * p[k - 3];
                    }
                    else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 0x1) == 0x0) {
                    v *= t;
                }
                function[n] = v;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void acosh(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        final double x = operand[operandOffset];
        function[0] = FastMath.acosh(x);
        if (this.order > 0) {
            final double[] p = new double[this.order];
            p[0] = 1.0;
            final double x2 = x * x;
            final double f = 1.0 / (x2 - 1.0);
            double coeff = FastMath.sqrt(f);
            function[1] = coeff * p[0];
            for (int n = 2; n <= this.order; ++n) {
                double v = 0.0;
                p[n - 1] = (1 - n) * p[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = v * x2 + p[k];
                    if (k > 2) {
                        p[k - 2] = (1 - k) * p[k - 1] + (k - 2 * n) * p[k - 3];
                    }
                    else if (k == 2) {
                        p[0] = -p[1];
                    }
                }
                if ((n & 0x1) == 0x0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void asinh(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        final double x = operand[operandOffset];
        function[0] = FastMath.asinh(x);
        if (this.order > 0) {
            final double[] p = new double[this.order];
            p[0] = 1.0;
            final double x2 = x * x;
            final double f = 1.0 / (1.0 + x2);
            double coeff = FastMath.sqrt(f);
            function[1] = coeff * p[0];
            for (int n = 2; n <= this.order; ++n) {
                double v = 0.0;
                p[n - 1] = (1 - n) * p[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = v * x2 + p[k];
                    if (k > 2) {
                        p[k - 2] = (k - 1) * p[k - 1] + (k - 2 * n) * p[k - 3];
                    }
                    else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 0x1) == 0x0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void atanh(final double[] operand, final int operandOffset, final double[] result, final int resultOffset) {
        final double[] function = new double[1 + this.order];
        final double x = operand[operandOffset];
        function[0] = FastMath.atanh(x);
        if (this.order > 0) {
            final double[] q = new double[this.order];
            q[0] = 1.0;
            final double x2 = x * x;
            double coeff;
            final double f = coeff = 1.0 / (1.0 - x2);
            function[1] = coeff * q[0];
            for (int n = 2; n <= this.order; ++n) {
                double v = 0.0;
                q[n - 1] = n * q[n - 2];
                for (int k = n - 1; k >= 0; k -= 2) {
                    v = v * x2 + q[k];
                    if (k > 2) {
                        q[k - 2] = (k - 1) * q[k - 1] + (2 * n - k + 1) * q[k - 3];
                    }
                    else if (k == 2) {
                        q[0] = q[1];
                    }
                }
                if ((n & 0x1) == 0x0) {
                    v *= x;
                }
                coeff *= f;
                function[n] = coeff * v;
            }
        }
        this.compose(operand, operandOffset, function, result, resultOffset);
    }
    
    public void compose(final double[] operand, final int operandOffset, final double[] f, final double[] result, final int resultOffset) {
        for (int i = 0; i < this.compIndirection.length; ++i) {
            final int[][] mappingI = this.compIndirection[i];
            double r = 0.0;
            for (int j = 0; j < mappingI.length; ++j) {
                final int[] mappingIJ = mappingI[j];
                double product = mappingIJ[0] * f[mappingIJ[1]];
                for (int k = 2; k < mappingIJ.length; ++k) {
                    product *= operand[operandOffset + mappingIJ[k]];
                }
                r += product;
            }
            result[resultOffset + i] = r;
        }
    }
    
    public double taylor(final double[] ds, final int dsOffset, final double... delta) throws MathArithmeticException {
        double value = 0.0;
        for (int i = this.getSize() - 1; i >= 0; --i) {
            final int[] orders = this.getPartialDerivativeOrders(i);
            double term = ds[dsOffset + i];
            for (int k = 0; k < orders.length; ++k) {
                if (orders[k] > 0) {
                    try {
                        term *= FastMath.pow(delta[k], orders[k]) / CombinatoricsUtils.factorial(orders[k]);
                    }
                    catch (final NotPositiveException e) {
                        throw new MathInternalError(e);
                    }
                }
            }
            value += term;
        }
        return value;
    }
    
    public void checkCompatibility(final DSCompiler compiler) throws DimensionMismatchException {
        if (this.parameters != compiler.parameters) {
            throw new DimensionMismatchException(this.parameters, compiler.parameters);
        }
        if (this.order != compiler.order) {
            throw new DimensionMismatchException(this.order, compiler.order);
        }
    }
    
    static {
        DSCompiler.compilers = new AtomicReference<DSCompiler[][]>(null);
    }
}
