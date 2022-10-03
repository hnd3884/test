package cryptix.jce.provider.elgamal;

import cryptix.jce.util.Group;
import cryptix.jce.util.Precomputed;
import cryptix.jce.ElGamalParams;

final class PrecomputedParams
{
    static ElGamalParams get(final int keysize) {
        final Group g = Precomputed.getElGamalGroup(keysize);
        if (g == null) {
            return null;
        }
        return new ElGamalParamsCryptix(g.getP(), g.getQ(), g.getG());
    }
}
