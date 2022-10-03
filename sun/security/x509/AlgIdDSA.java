package sun.security.x509;

import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import java.security.ProviderException;
import java.io.IOException;
import sun.security.util.DerValue;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;

public final class AlgIdDSA extends AlgorithmId implements DSAParams
{
    private static final long serialVersionUID = 3437177836797504046L;
    private BigInteger p;
    private BigInteger q;
    private BigInteger g;
    
    @Override
    public BigInteger getP() {
        return this.p;
    }
    
    @Override
    public BigInteger getQ() {
        return this.q;
    }
    
    @Override
    public BigInteger getG() {
        return this.g;
    }
    
    @Deprecated
    public AlgIdDSA() {
    }
    
    AlgIdDSA(final DerValue derValue) throws IOException {
        super(derValue.getOID());
    }
    
    public AlgIdDSA(final byte[] array) throws IOException {
        super(new DerValue(array).getOID());
    }
    
    public AlgIdDSA(final byte[] array, final byte[] array2, final byte[] array3) throws IOException {
        this(new BigInteger(1, array), new BigInteger(1, array2), new BigInteger(1, array3));
    }
    
    public AlgIdDSA(final BigInteger p3, final BigInteger q, final BigInteger g) {
        super(AlgIdDSA.DSA_oid);
        if (p3 != null || q != null || g != null) {
            if (p3 == null || q == null || g == null) {
                throw new ProviderException("Invalid parameters for DSS/DSA Algorithm ID");
            }
            try {
                this.p = p3;
                this.q = q;
                this.g = g;
                this.initializeParams();
            }
            catch (final IOException ex) {
                throw new ProviderException("Construct DSS/DSA Algorithm ID");
            }
        }
    }
    
    @Override
    public String getName() {
        return "DSA";
    }
    
    private void initializeParams() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(this.p);
        derOutputStream.putInteger(this.q);
        derOutputStream.putInteger(this.g);
        this.params = new DerValue((byte)48, derOutputStream.toByteArray());
    }
    
    @Override
    protected void decodeParams() throws IOException {
        if (this.params == null) {
            throw new IOException("DSA alg params are null");
        }
        if (this.params.tag != 48) {
            throw new IOException("DSA alg parsing error");
        }
        this.params.data.reset();
        this.p = this.params.data.getBigInteger();
        this.q = this.params.data.getBigInteger();
        this.g = this.params.data.getBigInteger();
        if (this.params.data.available() != 0) {
            throw new IOException("AlgIdDSA params, extra=" + this.params.data.available());
        }
    }
    
    @Override
    public String toString() {
        return this.paramsToString();
    }
    
    @Override
    protected String paramsToString() {
        if (this.params == null) {
            return " null\n";
        }
        return "\n    p:\n" + Debug.toHexString(this.p) + "\n    q:\n" + Debug.toHexString(this.q) + "\n    g:\n" + Debug.toHexString(this.g) + "\n";
    }
}
