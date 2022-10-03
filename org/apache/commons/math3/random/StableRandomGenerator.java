package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NullArgumentException;

public class StableRandomGenerator implements NormalizedRandomGenerator
{
    private final RandomGenerator generator;
    private final double alpha;
    private final double beta;
    private final double zeta;
    
    public StableRandomGenerator(final RandomGenerator generator, final double alpha, final double beta) throws NullArgumentException, OutOfRangeException {
        if (generator == null) {
            throw new NullArgumentException();
        }
        if (alpha <= 0.0 || alpha > 2.0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_LEFT, alpha, 0, 2);
        }
        if (beta < -1.0 || beta > 1.0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_SIMPLE, beta, -1, 1);
        }
        this.generator = generator;
        this.alpha = alpha;
        this.beta = beta;
        if (alpha < 2.0 && beta != 0.0) {
            this.zeta = beta * FastMath.tan(3.141592653589793 * alpha / 2.0);
        }
        else {
            this.zeta = 0.0;
        }
    }
    
    public double nextNormalizedDouble() {
        final double omega = -FastMath.log(this.generator.nextDouble());
        final double phi = 3.141592653589793 * (this.generator.nextDouble() - 0.5);
        if (this.alpha == 2.0) {
            return FastMath.sqrt(2.0 * omega) * FastMath.sin(phi);
        }
        double x;
        if (this.beta == 0.0) {
            if (this.alpha == 1.0) {
                x = FastMath.tan(phi);
            }
            else {
                x = FastMath.pow(omega * FastMath.cos((1.0 - this.alpha) * phi), 1.0 / this.alpha - 1.0) * FastMath.sin(this.alpha * phi) / FastMath.pow(FastMath.cos(phi), 1.0 / this.alpha);
            }
        }
        else {
            final double cosPhi = FastMath.cos(phi);
            if (FastMath.abs(this.alpha - 1.0) > 1.0E-8) {
                final double alphaPhi = this.alpha * phi;
                final double invAlphaPhi = phi - alphaPhi;
                x = (FastMath.sin(alphaPhi) + this.zeta * FastMath.cos(alphaPhi)) / cosPhi * (FastMath.cos(invAlphaPhi) + this.zeta * FastMath.sin(invAlphaPhi)) / FastMath.pow(omega * cosPhi, (1.0 - this.alpha) / this.alpha);
            }
            else {
                final double betaPhi = 1.5707963267948966 + this.beta * phi;
                x = 0.6366197723675814 * (betaPhi * FastMath.tan(phi) - this.beta * FastMath.log(1.5707963267948966 * omega * cosPhi / betaPhi));
                if (this.alpha != 1.0) {
                    x += this.beta * FastMath.tan(3.141592653589793 * this.alpha / 2.0);
                }
            }
        }
        return x;
    }
}
