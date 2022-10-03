package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NotPositiveException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import java.io.InputStream;
import org.apache.commons.math3.exception.MathParseException;
import java.io.IOException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.OutOfRangeException;

public class SobolSequenceGenerator implements RandomVectorGenerator
{
    private static final int BITS = 52;
    private static final double SCALE;
    private static final int MAX_DIMENSION = 1000;
    private static final String RESOURCE_NAME = "/assets/org/apache/commons/math3/random/new-joe-kuo-6.1000";
    private static final String FILE_CHARSET = "US-ASCII";
    private final int dimension;
    private int count;
    private final long[][] direction;
    private final long[] x;
    
    public SobolSequenceGenerator(final int dimension) throws OutOfRangeException {
        this.count = 0;
        if (dimension < 1 || dimension > 1000) {
            throw new OutOfRangeException(dimension, 1, 1000);
        }
        final InputStream is = this.getClass().getResourceAsStream("/assets/org/apache/commons/math3/random/new-joe-kuo-6.1000");
        if (is == null) {
            throw new MathInternalError();
        }
        this.dimension = dimension;
        this.direction = new long[dimension][53];
        this.x = new long[dimension];
        try {
            this.initFromStream(is);
        }
        catch (final IOException e) {
            throw new MathInternalError();
        }
        catch (final MathParseException e2) {
            throw new MathInternalError();
        }
        finally {
            try {
                is.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public SobolSequenceGenerator(final int dimension, final InputStream is) throws NotStrictlyPositiveException, MathParseException, IOException {
        this.count = 0;
        if (dimension < 1) {
            throw new NotStrictlyPositiveException(dimension);
        }
        this.dimension = dimension;
        this.direction = new long[dimension][53];
        this.x = new long[dimension];
        final int lastDimension = this.initFromStream(is);
        if (lastDimension < dimension) {
            throw new OutOfRangeException(dimension, 1, lastDimension);
        }
    }
    
    private int initFromStream(final InputStream is) throws MathParseException, IOException {
        for (int i = 1; i <= 52; ++i) {
            this.direction[0][i] = 1L << 52 - i;
        }
        final Charset charset = Charset.forName("US-ASCII");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
        int dim = -1;
        try {
            reader.readLine();
            int lineNumber = 2;
            int index = 1;
            String line = null;
            while ((line = reader.readLine()) != null) {
                final StringTokenizer st = new StringTokenizer(line, " ");
                try {
                    dim = Integer.parseInt(st.nextToken());
                    if (dim >= 2 && dim <= this.dimension) {
                        final int s = Integer.parseInt(st.nextToken());
                        final int a = Integer.parseInt(st.nextToken());
                        final int[] m = new int[s + 1];
                        for (int j = 1; j <= s; ++j) {
                            m[j] = Integer.parseInt(st.nextToken());
                        }
                        this.initDirectionVector(index++, a, m);
                    }
                    if (dim > this.dimension) {
                        return dim;
                    }
                }
                catch (final NoSuchElementException e) {
                    throw new MathParseException(line, lineNumber);
                }
                catch (final NumberFormatException e2) {
                    throw new MathParseException(line, lineNumber);
                }
                ++lineNumber;
            }
        }
        finally {
            reader.close();
        }
        return dim;
    }
    
    private void initDirectionVector(final int d, final int a, final int[] m) {
        final int s = m.length - 1;
        for (int i = 1; i <= s; ++i) {
            this.direction[d][i] = (long)m[i] << 52 - i;
        }
        for (int i = s + 1; i <= 52; ++i) {
            this.direction[d][i] = (this.direction[d][i - s] ^ this.direction[d][i - s] >> s);
            for (int k = 1; k <= s - 1; ++k) {
                final long[] array = this.direction[d];
                final int n = i;
                array[n] ^= (a >> s - 1 - k & 0x1) * this.direction[d][i - k];
            }
        }
    }
    
    public double[] nextVector() {
        final double[] v = new double[this.dimension];
        if (this.count == 0) {
            ++this.count;
            return v;
        }
        int c = 1;
        for (int value = this.count - 1; (value & 0x1) == 0x1; value >>= 1, ++c) {}
        for (int i = 0; i < this.dimension; ++i) {
            final long[] x = this.x;
            final int n = i;
            x[n] ^= this.direction[i][c];
            v[i] = this.x[i] / SobolSequenceGenerator.SCALE;
        }
        ++this.count;
        return v;
    }
    
    public double[] skipTo(final int index) throws NotPositiveException {
        if (index == 0) {
            Arrays.fill(this.x, 0L);
        }
        else {
            final int i = index - 1;
            final long grayCode = i ^ i >> 1;
            for (int j = 0; j < this.dimension; ++j) {
                long result = 0L;
                for (int k = 1; k <= 52; ++k) {
                    final long shift = grayCode >> k - 1;
                    if (shift == 0L) {
                        break;
                    }
                    final long ik = shift & 0x1L;
                    result ^= ik * this.direction[j][k];
                }
                this.x[j] = result;
            }
        }
        this.count = index;
        return this.nextVector();
    }
    
    public int getNextIndex() {
        return this.count;
    }
    
    static {
        SCALE = FastMath.pow(2.0, 52);
    }
}
