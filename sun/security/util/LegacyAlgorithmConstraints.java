package sun.security.util;

import java.security.Key;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.util.Set;
import java.util.List;

public class LegacyAlgorithmConstraints extends AbstractAlgorithmConstraints
{
    public static final String PROPERTY_TLS_LEGACY_ALGS = "jdk.tls.legacyAlgorithms";
    private final List<String> legacyAlgorithms;
    
    public LegacyAlgorithmConstraints(final String s, final AlgorithmDecomposer algorithmDecomposer) {
        super(algorithmDecomposer);
        this.legacyAlgorithms = AbstractAlgorithmConstraints.getAlgorithms(s);
    }
    
    @Override
    public final boolean permits(final Set<CryptoPrimitive> set, final String s, final AlgorithmParameters algorithmParameters) {
        return AbstractAlgorithmConstraints.checkAlgorithm(this.legacyAlgorithms, s, this.decomposer);
    }
    
    @Override
    public final boolean permits(final Set<CryptoPrimitive> set, final Key key) {
        return true;
    }
    
    @Override
    public final boolean permits(final Set<CryptoPrimitive> set, final String s, final Key key, final AlgorithmParameters algorithmParameters) {
        return AbstractAlgorithmConstraints.checkAlgorithm(this.legacyAlgorithms, s, this.decomposer);
    }
}
