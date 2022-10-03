package org.apache.commons.math3.stat.regression;

import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class MillerUpdatingRegression implements UpdatingMultipleLinearRegression
{
    private final int nvars;
    private final double[] d;
    private final double[] rhs;
    private final double[] r;
    private final double[] tol;
    private final double[] rss;
    private final int[] vorder;
    private final double[] work_tolset;
    private long nobs;
    private double sserr;
    private boolean rss_set;
    private boolean tol_set;
    private final boolean[] lindep;
    private final double[] x_sing;
    private final double[] work_sing;
    private double sumy;
    private double sumsqy;
    private boolean hasIntercept;
    private final double epsilon;
    
    private MillerUpdatingRegression() {
        this(-1, false, Double.NaN);
    }
    
    public MillerUpdatingRegression(final int numberOfVariables, final boolean includeConstant, final double errorTolerance) throws ModelSpecificationException {
        this.nobs = 0L;
        this.sserr = 0.0;
        this.rss_set = false;
        this.tol_set = false;
        this.sumy = 0.0;
        this.sumsqy = 0.0;
        if (numberOfVariables < 1) {
            throw new ModelSpecificationException(LocalizedFormats.NO_REGRESSORS, new Object[0]);
        }
        if (includeConstant) {
            this.nvars = numberOfVariables + 1;
        }
        else {
            this.nvars = numberOfVariables;
        }
        this.hasIntercept = includeConstant;
        this.nobs = 0L;
        this.d = new double[this.nvars];
        this.rhs = new double[this.nvars];
        this.r = new double[this.nvars * (this.nvars - 1) / 2];
        this.tol = new double[this.nvars];
        this.rss = new double[this.nvars];
        this.vorder = new int[this.nvars];
        this.x_sing = new double[this.nvars];
        this.work_sing = new double[this.nvars];
        this.work_tolset = new double[this.nvars];
        this.lindep = new boolean[this.nvars];
        for (int i = 0; i < this.nvars; ++i) {
            this.vorder[i] = i;
        }
        if (errorTolerance > 0.0) {
            this.epsilon = errorTolerance;
        }
        else {
            this.epsilon = -errorTolerance;
        }
    }
    
    public MillerUpdatingRegression(final int numberOfVariables, final boolean includeConstant) throws ModelSpecificationException {
        this(numberOfVariables, includeConstant, Precision.EPSILON);
    }
    
    public boolean hasIntercept() {
        return this.hasIntercept;
    }
    
    public long getN() {
        return this.nobs;
    }
    
    public void addObservation(final double[] x, final double y) throws ModelSpecificationException {
        if ((!this.hasIntercept && x.length != this.nvars) || (this.hasIntercept && x.length + 1 != this.nvars)) {
            throw new ModelSpecificationException(LocalizedFormats.INVALID_REGRESSION_OBSERVATION, new Object[] { x.length, this.nvars });
        }
        if (!this.hasIntercept) {
            this.include(MathArrays.copyOf(x, x.length), 1.0, y);
        }
        else {
            final double[] tmp = new double[x.length + 1];
            System.arraycopy(x, 0, tmp, 1, x.length);
            this.include(tmp, tmp[0] = 1.0, y);
        }
        ++this.nobs;
    }
    
    public void addObservations(final double[][] x, final double[] y) throws ModelSpecificationException {
        if (x == null || y == null || x.length != y.length) {
            throw new ModelSpecificationException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { (x == null) ? 0 : x.length, (y == null) ? 0 : y.length });
        }
        if (x.length == 0) {
            throw new ModelSpecificationException(LocalizedFormats.NO_DATA, new Object[0]);
        }
        if (x[0].length + 1 > x.length) {
            throw new ModelSpecificationException(LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, new Object[] { x.length, x[0].length });
        }
        for (int i = 0; i < x.length; ++i) {
            this.addObservation(x[i], y[i]);
        }
    }
    
    private void include(final double[] x, final double wi, final double yi) {
        int nextr = 0;
        double w = wi;
        double y = yi;
        this.rss_set = false;
        this.sumy = this.smartAdd(yi, this.sumy);
        this.sumsqy = this.smartAdd(this.sumsqy, yi * yi);
        for (int i = 0; i < x.length; ++i) {
            if (w == 0.0) {
                return;
            }
            final double xi = x[i];
            if (xi == 0.0) {
                nextr += this.nvars - i - 1;
            }
            else {
                final double di = this.d[i];
                final double wxi = w * xi;
                final double _w = w;
                double dpi;
                if (di != 0.0) {
                    dpi = this.smartAdd(di, wxi * xi);
                    final double tmp = wxi * xi / di;
                    if (FastMath.abs(tmp) > Precision.EPSILON) {
                        w = di * w / dpi;
                    }
                }
                else {
                    dpi = wxi * xi;
                    w = 0.0;
                }
                this.d[i] = dpi;
                for (int k = i + 1; k < this.nvars; ++k) {
                    final double xk = x[k];
                    x[k] = this.smartAdd(xk, -xi * this.r[nextr]);
                    if (di != 0.0) {
                        this.r[nextr] = this.smartAdd(di * this.r[nextr], _w * xi * xk) / dpi;
                    }
                    else {
                        this.r[nextr] = xk / xi;
                    }
                    ++nextr;
                }
                final double xk = y;
                y = this.smartAdd(xk, -xi * this.rhs[i]);
                if (di != 0.0) {
                    this.rhs[i] = this.smartAdd(di * this.rhs[i], wxi * xk) / dpi;
                }
                else {
                    this.rhs[i] = xk / xi;
                }
            }
        }
        this.sserr = this.smartAdd(this.sserr, w * y * y);
    }
    
    private double smartAdd(final double a, final double b) {
        final double _a = FastMath.abs(a);
        final double _b = FastMath.abs(b);
        if (_a > _b) {
            final double eps = _a * Precision.EPSILON;
            if (_b > eps) {
                return a + b;
            }
            return a;
        }
        else {
            final double eps = _b * Precision.EPSILON;
            if (_a > eps) {
                return a + b;
            }
            return b;
        }
    }
    
    public void clear() {
        Arrays.fill(this.d, 0.0);
        Arrays.fill(this.rhs, 0.0);
        Arrays.fill(this.r, 0.0);
        Arrays.fill(this.tol, 0.0);
        Arrays.fill(this.rss, 0.0);
        Arrays.fill(this.work_tolset, 0.0);
        Arrays.fill(this.work_sing, 0.0);
        Arrays.fill(this.x_sing, 0.0);
        Arrays.fill(this.lindep, false);
        for (int i = 0; i < this.nvars; ++i) {
            this.vorder[i] = i;
        }
        this.nobs = 0L;
        this.sserr = 0.0;
        this.sumy = 0.0;
        this.sumsqy = 0.0;
        this.rss_set = false;
        this.tol_set = false;
    }
    
    private void tolset() {
        final double eps = this.epsilon;
        for (int i = 0; i < this.nvars; ++i) {
            this.work_tolset[i] = FastMath.sqrt(this.d[i]);
        }
        this.tol[0] = eps * this.work_tolset[0];
        for (int col = 1; col < this.nvars; ++col) {
            int pos = col - 1;
            double total = this.work_tolset[col];
            for (int row = 0; row < col; ++row) {
                total += FastMath.abs(this.r[pos]) * this.work_tolset[row];
                pos += this.nvars - row - 2;
            }
            this.tol[col] = eps * total;
        }
        this.tol_set = true;
    }
    
    private double[] regcf(final int nreq) throws ModelSpecificationException {
        if (nreq < 1) {
            throw new ModelSpecificationException(LocalizedFormats.NO_REGRESSORS, new Object[0]);
        }
        if (nreq > this.nvars) {
            throw new ModelSpecificationException(LocalizedFormats.TOO_MANY_REGRESSORS, new Object[] { nreq, this.nvars });
        }
        if (!this.tol_set) {
            this.tolset();
        }
        final double[] ret = new double[nreq];
        boolean rankProblem = false;
        for (int i = nreq - 1; i > -1; --i) {
            if (FastMath.sqrt(this.d[i]) < this.tol[i]) {
                ret[i] = 0.0;
                this.d[i] = 0.0;
                rankProblem = true;
            }
            else {
                ret[i] = this.rhs[i];
                int nextr = i * (this.nvars + this.nvars - i - 1) / 2;
                for (int j = i + 1; j < nreq; ++j) {
                    ret[i] = this.smartAdd(ret[i], -this.r[nextr] * ret[j]);
                    ++nextr;
                }
            }
        }
        if (rankProblem) {
            for (int i = 0; i < nreq; ++i) {
                if (this.lindep[i]) {
                    ret[i] = Double.NaN;
                }
            }
        }
        return ret;
    }
    
    private void singcheck() {
        for (int i = 0; i < this.nvars; ++i) {
            this.work_sing[i] = FastMath.sqrt(this.d[i]);
        }
        for (int col = 0; col < this.nvars; ++col) {
            final double temp = this.tol[col];
            int pos = col - 1;
            for (int row = 0; row < col - 1; ++row) {
                if (FastMath.abs(this.r[pos]) * this.work_sing[row] < temp) {
                    this.r[pos] = 0.0;
                }
                pos += this.nvars - row - 2;
            }
            this.lindep[col] = false;
            if (this.work_sing[col] < temp) {
                this.lindep[col] = true;
                if (col < this.nvars - 1) {
                    Arrays.fill(this.x_sing, 0.0);
                    for (int _pi = col * (this.nvars + this.nvars - col - 1) / 2, _xi = col + 1; _xi < this.nvars; ++_xi, ++_pi) {
                        this.x_sing[_xi] = this.r[_pi];
                        this.r[_pi] = 0.0;
                    }
                    final double y = this.rhs[col];
                    final double weight = this.d[col];
                    this.d[col] = 0.0;
                    this.rhs[col] = 0.0;
                    this.include(this.x_sing, weight, y);
                }
                else {
                    this.sserr += this.d[col] * this.rhs[col] * this.rhs[col];
                }
            }
        }
    }
    
    private void ss() {
        double total = this.sserr;
        this.rss[this.nvars - 1] = this.sserr;
        for (int i = this.nvars - 1; i > 0; --i) {
            total += this.d[i] * this.rhs[i] * this.rhs[i];
            this.rss[i - 1] = total;
        }
        this.rss_set = true;
    }
    
    private double[] cov(final int nreq) {
        if (this.nobs <= nreq) {
            return null;
        }
        double rnk = 0.0;
        for (int i = 0; i < nreq; ++i) {
            if (!this.lindep[i]) {
                ++rnk;
            }
        }
        final double var = this.rss[nreq - 1] / (this.nobs - rnk);
        final double[] rinv = new double[nreq * (nreq - 1) / 2];
        this.inverse(rinv, nreq);
        final double[] covmat = new double[nreq * (nreq + 1) / 2];
        Arrays.fill(covmat, Double.NaN);
        int start = 0;
        double total = 0.0;
        for (int row = 0; row < nreq; ++row) {
            int pos2 = start;
            if (!this.lindep[row]) {
                for (int col = row; col < nreq; ++col) {
                    if (!this.lindep[col]) {
                        int pos3 = start + col - row;
                        if (row == col) {
                            total = 1.0 / this.d[col];
                        }
                        else {
                            total = rinv[pos3 - 1] / this.d[col];
                        }
                        for (int k = col + 1; k < nreq; ++k) {
                            if (!this.lindep[k]) {
                                total += rinv[pos3] * rinv[pos2] / this.d[k];
                            }
                            ++pos3;
                            ++pos2;
                        }
                        covmat[(col + 1) * col / 2 + row] = total * var;
                    }
                    else {
                        pos2 += nreq - col - 1;
                    }
                }
            }
            start += nreq - row - 1;
        }
        return covmat;
    }
    
    private void inverse(final double[] rinv, final int nreq) {
        int pos = nreq * (nreq - 1) / 2 - 1;
        int pos2 = -1;
        int pos3 = -1;
        double total = 0.0;
        Arrays.fill(rinv, Double.NaN);
        for (int row = nreq - 1; row > 0; --row) {
            if (!this.lindep[row]) {
                final int start = (row - 1) * (this.nvars + this.nvars - row) / 2;
                for (int col = nreq; col > row; --col) {
                    pos2 = start;
                    pos3 = pos;
                    total = 0.0;
                    for (int k = row; k < col - 1; ++k) {
                        pos3 += nreq - k - 1;
                        if (!this.lindep[k]) {
                            total += -this.r[pos2] * rinv[pos3];
                        }
                        ++pos2;
                    }
                    rinv[pos] = total - this.r[pos2];
                    --pos;
                }
            }
            else {
                pos -= nreq - row;
            }
        }
    }
    
    public double[] getPartialCorrelations(final int in) {
        final double[] output = new double[(this.nvars - in + 1) * (this.nvars - in) / 2];
        final int rms_off = -in;
        final int wrk_off = -(in + 1);
        final double[] rms = new double[this.nvars - in];
        final double[] work = new double[this.nvars - in - 1];
        final int offXX = (this.nvars - in) * (this.nvars - in - 1) / 2;
        if (in < -1 || in >= this.nvars) {
            return null;
        }
        final int nvm = this.nvars - 1;
        final int base_pos = this.r.length - (nvm - in) * (nvm - in + 1) / 2;
        if (this.d[in] > 0.0) {
            rms[in + rms_off] = 1.0 / FastMath.sqrt(this.d[in]);
        }
        for (int col = in + 1; col < this.nvars; ++col) {
            int pos = base_pos + col - 1 - in;
            double sumxx = this.d[col];
            for (int row = in; row < col; ++row) {
                sumxx += this.d[row] * this.r[pos] * this.r[pos];
                pos += this.nvars - row - 2;
            }
            if (sumxx > 0.0) {
                rms[col + rms_off] = 1.0 / FastMath.sqrt(sumxx);
            }
            else {
                rms[col + rms_off] = 0.0;
            }
        }
        double sumyy = this.sserr;
        for (int row2 = in; row2 < this.nvars; ++row2) {
            sumyy += this.d[row2] * this.rhs[row2] * this.rhs[row2];
        }
        if (sumyy > 0.0) {
            sumyy = 1.0 / FastMath.sqrt(sumyy);
        }
        int pos = 0;
        for (int col2 = in; col2 < this.nvars; ++col2) {
            double sumxy = 0.0;
            Arrays.fill(work, 0.0);
            int pos2 = base_pos + col2 - in - 1;
            for (int row = in; row < col2; ++row) {
                int pos3 = pos2 + 1;
                for (int col3 = col2 + 1; col3 < this.nvars; ++col3) {
                    final double[] array = work;
                    final int n = col3 + wrk_off;
                    array[n] += this.d[row] * this.r[pos2] * this.r[pos3];
                    ++pos3;
                }
                sumxy += this.d[row] * this.r[pos2] * this.rhs[row];
                pos2 += this.nvars - row - 2;
            }
            int pos3 = pos2 + 1;
            for (int col4 = col2 + 1; col4 < this.nvars; ++col4) {
                final double[] array2 = work;
                final int n2 = col4 + wrk_off;
                array2[n2] += this.d[col2] * this.r[pos3];
                ++pos3;
                output[(col4 - 1 - in) * (col4 - in) / 2 + col2 - in] = work[col4 + wrk_off] * rms[col2 + rms_off] * rms[col4 + rms_off];
                ++pos;
            }
            sumxy += this.d[col2] * this.rhs[col2];
            output[col2 + rms_off + offXX] = sumxy * rms[col2 + rms_off] * sumyy;
        }
        return output;
    }
    
    private void vmove(final int from, final int to) {
        boolean bSkipTo40 = false;
        if (from == to) {
            return;
        }
        if (!this.rss_set) {
            this.ss();
        }
        int count = 0;
        int first;
        int inc;
        if (from < to) {
            first = from;
            inc = 1;
            count = to - from;
        }
        else {
            first = from - 1;
            inc = -1;
            count = from - to;
        }
        int m = first;
        for (int idx = 0; idx < count; ++idx) {
            int m2 = m * (this.nvars + this.nvars - m - 1) / 2;
            int m3 = m2 + this.nvars - m - 1;
            final int mp1 = m + 1;
            final double d1 = this.d[m];
            final double d2 = this.d[mp1];
            if (d1 > this.epsilon || d2 > this.epsilon) {
                double X = this.r[m2];
                if (FastMath.abs(X) * FastMath.sqrt(d1) < this.tol[mp1]) {
                    X = 0.0;
                }
                if (d1 < this.epsilon || FastMath.abs(X) < this.epsilon) {
                    this.d[m] = d2;
                    this.d[mp1] = d1;
                    this.r[m2] = 0.0;
                    for (int col = m + 2; col < this.nvars; ++col) {
                        ++m2;
                        X = this.r[m2];
                        this.r[m2] = this.r[m3];
                        this.r[m3] = X;
                        ++m3;
                    }
                    X = this.rhs[m];
                    this.rhs[m] = this.rhs[mp1];
                    this.rhs[mp1] = X;
                    bSkipTo40 = true;
                }
                else if (d2 < this.epsilon) {
                    this.d[m] = d1 * X * X;
                    this.r[m2] = 1.0 / X;
                    for (int _i = m2 + 1; _i < m2 + this.nvars - m - 1; ++_i) {
                        final double[] r = this.r;
                        final int n = _i;
                        r[n] /= X;
                    }
                    final double[] rhs = this.rhs;
                    final int n2 = m;
                    rhs[n2] /= X;
                    bSkipTo40 = true;
                }
                if (!bSkipTo40) {
                    final double d1new = d2 + d1 * X * X;
                    final double cbar = d2 / d1new;
                    final double sbar = X * d1 / d1new;
                    final double d2new = d1 * cbar;
                    this.d[m] = d1new;
                    this.d[mp1] = d2new;
                    this.r[m2] = sbar;
                    for (int col = m + 2; col < this.nvars; ++col) {
                        ++m2;
                        final double Y = this.r[m2];
                        this.r[m2] = cbar * this.r[m3] + sbar * Y;
                        this.r[m3] = Y - X * this.r[m3];
                        ++m3;
                    }
                    final double Y = this.rhs[m];
                    this.rhs[m] = cbar * this.rhs[mp1] + sbar * Y;
                    this.rhs[mp1] = Y - X * this.rhs[mp1];
                }
            }
            if (m > 0) {
                int pos = m;
                for (int row = 0; row < m; ++row) {
                    final double X = this.r[pos];
                    this.r[pos] = this.r[pos - 1];
                    this.r[pos - 1] = X;
                    pos += this.nvars - row - 2;
                }
            }
            m2 = this.vorder[m];
            this.vorder[m] = this.vorder[mp1];
            this.vorder[mp1] = m2;
            double X = this.tol[m];
            this.tol[m] = this.tol[mp1];
            this.tol[mp1] = X;
            this.rss[m] = this.rss[mp1] + this.d[mp1] * this.rhs[mp1] * this.rhs[mp1];
            m += inc;
        }
    }
    
    private int reorderRegressors(final int[] list, final int pos1) {
        if (list.length < 1 || list.length > this.nvars + 1 - pos1) {
            return -1;
        }
        int next = pos1;
        for (int i = pos1; i < this.nvars; ++i) {
            final int l = this.vorder[i];
            int j = 0;
            while (j < list.length) {
                if (l == list[j] && i > next) {
                    this.vmove(i, next);
                    if (++next >= list.length + pos1) {
                        return 0;
                    }
                    break;
                }
                else {
                    ++j;
                }
            }
        }
        return 0;
    }
    
    public double getDiagonalOfHatMatrix(final double[] row_data) {
        final double[] wk = new double[this.nvars];
        if (row_data.length > this.nvars) {
            return Double.NaN;
        }
        double[] xrow;
        if (this.hasIntercept) {
            xrow = new double[row_data.length + 1];
            xrow[0] = 1.0;
            System.arraycopy(row_data, 0, xrow, 1, row_data.length);
        }
        else {
            xrow = row_data;
        }
        double hii = 0.0;
        for (int col = 0; col < xrow.length; ++col) {
            if (FastMath.sqrt(this.d[col]) < this.tol[col]) {
                wk[col] = 0.0;
            }
            else {
                int pos = col - 1;
                double total = xrow[col];
                for (int row = 0; row < col; ++row) {
                    total = this.smartAdd(total, -wk[row] * this.r[pos]);
                    pos += this.nvars - row - 2;
                }
                wk[col] = total;
                hii = this.smartAdd(hii, total * total / this.d[col]);
            }
        }
        return hii;
    }
    
    public int[] getOrderOfRegressors() {
        return MathArrays.copyOf(this.vorder);
    }
    
    public RegressionResults regress() throws ModelSpecificationException {
        return this.regress(this.nvars);
    }
    
    public RegressionResults regress(final int numberOfRegressors) throws ModelSpecificationException {
        if (this.nobs <= numberOfRegressors) {
            throw new ModelSpecificationException(LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, new Object[] { this.nobs, numberOfRegressors });
        }
        if (numberOfRegressors > this.nvars) {
            throw new ModelSpecificationException(LocalizedFormats.TOO_MANY_REGRESSORS, new Object[] { numberOfRegressors, this.nvars });
        }
        this.tolset();
        this.singcheck();
        final double[] beta = this.regcf(numberOfRegressors);
        this.ss();
        final double[] cov = this.cov(numberOfRegressors);
        int rnk = 0;
        for (int i = 0; i < this.lindep.length; ++i) {
            if (!this.lindep[i]) {
                ++rnk;
            }
        }
        boolean needsReorder = false;
        for (int j = 0; j < numberOfRegressors; ++j) {
            if (this.vorder[j] != j) {
                needsReorder = true;
                break;
            }
        }
        if (!needsReorder) {
            return new RegressionResults(beta, new double[][] { cov }, true, this.nobs, rnk, this.sumy, this.sumsqy, this.sserr, this.hasIntercept, false);
        }
        final double[] betaNew = new double[beta.length];
        final double[] covNew = new double[cov.length];
        final int[] newIndices = new int[beta.length];
        for (int k = 0; k < this.nvars; ++k) {
            for (int l = 0; l < numberOfRegressors; ++l) {
                if (this.vorder[l] == k) {
                    betaNew[k] = beta[l];
                    newIndices[k] = l;
                }
            }
        }
        int idx1 = 0;
        for (int m = 0; m < beta.length; ++m) {
            final int _i = newIndices[m];
            for (int j2 = 0; j2 <= m; ++j2, ++idx1) {
                final int _j = newIndices[j2];
                int idx2;
                if (_i > _j) {
                    idx2 = _i * (_i + 1) / 2 + _j;
                }
                else {
                    idx2 = _j * (_j + 1) / 2 + _i;
                }
                covNew[idx1] = cov[idx2];
            }
        }
        return new RegressionResults(betaNew, new double[][] { covNew }, true, this.nobs, rnk, this.sumy, this.sumsqy, this.sserr, this.hasIntercept, false);
    }
    
    public RegressionResults regress(final int[] variablesToInclude) throws ModelSpecificationException {
        if (variablesToInclude.length > this.nvars) {
            throw new ModelSpecificationException(LocalizedFormats.TOO_MANY_REGRESSORS, new Object[] { variablesToInclude.length, this.nvars });
        }
        if (this.nobs <= this.nvars) {
            throw new ModelSpecificationException(LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, new Object[] { this.nobs, this.nvars });
        }
        Arrays.sort(variablesToInclude);
        int iExclude = 0;
        for (int i = 0; i < variablesToInclude.length; ++i) {
            if (i >= this.nvars) {
                throw new ModelSpecificationException(LocalizedFormats.INDEX_LARGER_THAN_MAX, new Object[] { i, this.nvars });
            }
            if (i > 0 && variablesToInclude[i] == variablesToInclude[i - 1]) {
                variablesToInclude[i] = -1;
                ++iExclude;
            }
        }
        int[] series;
        if (iExclude > 0) {
            int j = 0;
            series = new int[variablesToInclude.length - iExclude];
            for (int k = 0; k < variablesToInclude.length; ++k) {
                if (variablesToInclude[k] > -1) {
                    series[j] = variablesToInclude[k];
                    ++j;
                }
            }
        }
        else {
            series = variablesToInclude;
        }
        this.reorderRegressors(series, 0);
        this.tolset();
        this.singcheck();
        final double[] beta = this.regcf(series.length);
        this.ss();
        final double[] cov = this.cov(series.length);
        int rnk = 0;
        for (int l = 0; l < this.lindep.length; ++l) {
            if (!this.lindep[l]) {
                ++rnk;
            }
        }
        boolean needsReorder = false;
        for (int m = 0; m < this.nvars; ++m) {
            if (this.vorder[m] != series[m]) {
                needsReorder = true;
                break;
            }
        }
        if (!needsReorder) {
            return new RegressionResults(beta, new double[][] { cov }, true, this.nobs, rnk, this.sumy, this.sumsqy, this.sserr, this.hasIntercept, false);
        }
        final double[] betaNew = new double[beta.length];
        final int[] newIndices = new int[beta.length];
        for (int i2 = 0; i2 < series.length; ++i2) {
            for (int j2 = 0; j2 < this.vorder.length; ++j2) {
                if (this.vorder[j2] == series[i2]) {
                    betaNew[i2] = beta[j2];
                    newIndices[i2] = j2;
                }
            }
        }
        final double[] covNew = new double[cov.length];
        int idx1 = 0;
        for (int i3 = 0; i3 < beta.length; ++i3) {
            final int _i = newIndices[i3];
            for (int j3 = 0; j3 <= i3; ++j3, ++idx1) {
                final int _j = newIndices[j3];
                int idx2;
                if (_i > _j) {
                    idx2 = _i * (_i + 1) / 2 + _j;
                }
                else {
                    idx2 = _j * (_j + 1) / 2 + _i;
                }
                covNew[idx1] = cov[idx2];
            }
        }
        return new RegressionResults(betaNew, new double[][] { covNew }, true, this.nobs, rnk, this.sumy, this.sumsqy, this.sserr, this.hasIntercept, false);
    }
}
