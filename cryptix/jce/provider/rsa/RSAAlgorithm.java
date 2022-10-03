package cryptix.jce.provider.rsa;

import java.math.BigInteger;

final class RSAAlgorithm
{
    private static final BigInteger ONE;
    
    public static BigInteger rsa(final BigInteger X, final BigInteger n, final BigInteger exp, final BigInteger p, final BigInteger q, final BigInteger u) {
        if (p == null) {
            return rsa(X, n, exp);
        }
        final BigInteger primeExponentP = exp.mod(p.subtract(RSAAlgorithm.ONE));
        final BigInteger primeExponentQ = exp.mod(q.subtract(RSAAlgorithm.ONE));
        return rsa(X, n, exp, p, q, primeExponentP, primeExponentQ, u);
    }
    
    public static BigInteger rsa(final BigInteger X, final BigInteger modulus, final BigInteger exp, BigInteger primeP, BigInteger primeQ, BigInteger primeExponentP, BigInteger primeExponentQ, final BigInteger crtCoefficient) {
        if (!crtCoefficient.equals(primeQ.modInverse(primeP))) {
            BigInteger t = primeQ;
            primeQ = primeP;
            primeP = t;
            t = primeExponentQ;
            primeExponentQ = primeExponentP;
            primeExponentP = t;
        }
        final BigInteger p2 = X.mod(primeP).modPow(primeExponentP, primeP);
        final BigInteger q2 = X.mod(primeQ).modPow(primeExponentQ, primeQ);
        if (p2.equals(q2)) {
            return q2;
        }
        final BigInteger k = p2.subtract(q2).mod(primeP);
        final BigInteger l = k.multiply(crtCoefficient).mod(primeP);
        return primeQ.multiply(l).add(q2);
    }
    
    public static BigInteger rsa(final BigInteger X, final BigInteger n, final BigInteger exp) {
        return X.modPow(exp, n);
    }
    
    private RSAAlgorithm() {
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
