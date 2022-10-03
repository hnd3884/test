package java.security.spec;

public final class DSAGenParameterSpec implements AlgorithmParameterSpec
{
    private final int pLen;
    private final int qLen;
    private final int seedLen;
    
    public DSAGenParameterSpec(final int n, final int n2) {
        this(n, n2, n2);
    }
    
    public DSAGenParameterSpec(final int pLen, final int qLen, final int seedLen) {
        switch (pLen) {
            case 1024: {
                if (qLen != 160) {
                    throw new IllegalArgumentException("subprimeQLen must be 160 when primePLen=1024");
                }
                break;
            }
            case 2048: {
                if (qLen != 224 && qLen != 256) {
                    throw new IllegalArgumentException("subprimeQLen must be 224 or 256 when primePLen=2048");
                }
                break;
            }
            case 3072: {
                if (qLen != 256) {
                    throw new IllegalArgumentException("subprimeQLen must be 256 when primePLen=3072");
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("primePLen must be 1024, 2048, or 3072");
            }
        }
        if (seedLen < qLen) {
            throw new IllegalArgumentException("seedLen must be equal to or greater than subprimeQLen");
        }
        this.pLen = pLen;
        this.qLen = qLen;
        this.seedLen = seedLen;
    }
    
    public int getPrimePLength() {
        return this.pLen;
    }
    
    public int getSubprimeQLength() {
        return this.qLen;
    }
    
    public int getSeedLength() {
        return this.seedLen;
    }
}
