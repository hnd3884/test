package sun.security.ec;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.security.AlgorithmParameters;
import java.security.spec.InvalidParameterSpecException;
import java.io.IOException;
import java.security.InvalidKeyException;
import sun.security.util.ECUtil;
import sun.security.util.ECParameters;
import sun.security.x509.AlgorithmId;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.interfaces.ECPublicKey;
import sun.security.x509.X509Key;

public final class ECPublicKeyImpl extends X509Key implements ECPublicKey
{
    private static final long serialVersionUID = -2462037275160462289L;
    private ECPoint w;
    private ECParameterSpec params;
    
    public ECPublicKeyImpl(final ECPoint w, final ECParameterSpec params) throws InvalidKeyException {
        this.w = w;
        this.params = params;
        this.algid = new AlgorithmId(AlgorithmId.EC_oid, ECParameters.getAlgorithmParameters(params));
        this.key = ECUtil.encodePoint(w, params.getCurve());
    }
    
    public ECPublicKeyImpl(final byte[] array) throws InvalidKeyException {
        this.decode(array);
    }
    
    @Override
    public String getAlgorithm() {
        return "EC";
    }
    
    @Override
    public ECPoint getW() {
        return this.w;
    }
    
    @Override
    public ECParameterSpec getParams() {
        return this.params;
    }
    
    public byte[] getEncodedPublicValue() {
        return this.key.clone();
    }
    
    @Override
    protected void parseKeyBits() throws InvalidKeyException {
        final AlgorithmParameters parameters = this.algid.getParameters();
        if (parameters == null) {
            throw new InvalidKeyException("EC domain parameters must be encoded in the algorithm identifier");
        }
        try {
            this.params = parameters.getParameterSpec(ECParameterSpec.class);
            this.w = ECUtil.decodePoint(this.key, this.params.getCurve());
        }
        catch (final IOException ex) {
            throw new InvalidKeyException("Invalid EC key", ex);
        }
        catch (final InvalidParameterSpecException ex2) {
            throw new InvalidKeyException("Invalid EC key", ex2);
        }
    }
    
    @Override
    public String toString() {
        return "Sun EC public key, " + this.params.getCurve().getField().getFieldSize() + " bits\n  public x coord: " + this.w.getAffineX() + "\n  public y coord: " + this.w.getAffineY() + "\n  parameters: " + this.params;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.PUBLIC, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }
}
