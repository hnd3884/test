package sun.security.mscapi;

import sun.security.rsa.RSAPublicKeyImpl;
import sun.security.rsa.RSAUtil;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.util.ECKeySizeParameterSpec;
import java.security.AlgorithmParameters;
import java.security.spec.ECParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.KeyFactory;
import java.security.ProviderException;
import java.math.BigInteger;
import java.util.Arrays;
import java.security.spec.ECPoint;
import java.security.interfaces.ECPublicKey;
import java.security.KeyException;
import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.security.PublicKey;

public abstract class CPublicKey extends CKey implements PublicKey
{
    private static final long serialVersionUID = -2289561342425825391L;
    protected byte[] encoding;
    
    static CPublicKey of(final String s, final long n, final long n2, final int n3) {
        return of(s, new NativeHandles(n, n2), n3);
    }
    
    public static CPublicKey of(final String s, final NativeHandles nativeHandles, final int n) {
        switch (s) {
            case "RSA": {
                return new CRSAPublicKey(nativeHandles, n);
            }
            case "EC": {
                return new CECPublicKey(nativeHandles, n);
            }
            default: {
                throw new AssertionError((Object)("Unsupported algorithm: " + s));
            }
        }
    }
    
    protected CPublicKey(final String s, final NativeHandles nativeHandles, final int n) {
        super(s, nativeHandles, n);
        this.encoding = null;
    }
    
    @Override
    public String getFormat() {
        return "X.509";
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.PUBLIC, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }
    
    native byte[] getPublicKeyBlob(final long p0, final long p1) throws KeyException;
    
    public static class CECPublicKey extends CPublicKey implements ECPublicKey
    {
        private ECPoint w;
        private static final long serialVersionUID = 12L;
        
        CECPublicKey(final NativeHandles nativeHandles, final int n) {
            super("EC", nativeHandles, n);
            this.w = null;
        }
        
        @Override
        public ECPoint getW() {
            if (this.w == null) {
                try {
                    final byte[] publicKeyBlob = this.getPublicKeyBlob(this.handles.hCryptProv, this.handles.hCryptKey);
                    final int n = publicKeyBlob[8] & 0xFF;
                    this.w = new ECPoint(new BigInteger(1, Arrays.copyOfRange(publicKeyBlob, 8, 8 + n)), new BigInteger(1, Arrays.copyOfRange(publicKeyBlob, 8 + n, 8 + n + n)));
                }
                catch (final KeyException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.w;
        }
        
        @Override
        public byte[] getEncoded() {
            if (this.encoding == null) {
                try {
                    this.encoding = KeyFactory.getInstance("EC").generatePublic(new ECPublicKeySpec(this.getW(), this.getParams())).getEncoded();
                }
                catch (final Exception ex) {}
            }
            return this.encoding;
        }
        
        @Override
        public ECParameterSpec getParams() {
            try {
                final AlgorithmParameters instance = AlgorithmParameters.getInstance("EC");
                instance.init(new ECKeySizeParameterSpec(this.keyLength));
                return instance.getParameterSpec(ECParameterSpec.class);
            }
            catch (final Exception ex) {
                throw new ProviderException(ex);
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(this.algorithm + "PublicKey [size=").append(this.keyLength).append("]\n  ECPoint: ").append(this.getW()).append("\n  params: ").append(this.getParams());
            return sb.toString();
        }
    }
    
    public static class CRSAPublicKey extends CPublicKey implements RSAPublicKey
    {
        private BigInteger modulus;
        private BigInteger exponent;
        private static final long serialVersionUID = 12L;
        
        CRSAPublicKey(final NativeHandles nativeHandles, final int n) {
            super("RSA", nativeHandles, n);
            this.modulus = null;
            this.exponent = null;
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(this.algorithm + "PublicKey [size=").append(this.keyLength).append(" bits, type=");
            if (this.handles.hCryptKey != 0L) {
                sb.append(CKey.getKeyType(this.handles.hCryptKey)).append(", container=").append(CKey.getContainerName(this.handles.hCryptProv));
            }
            else {
                sb.append("CNG");
            }
            sb.append("]\n  modulus: ").append(this.getModulus()).append("\n  public exponent: ").append(this.getPublicExponent());
            return sb.toString();
        }
        
        @Override
        public BigInteger getPublicExponent() {
            if (this.exponent == null) {
                try {
                    this.exponent = new BigInteger(1, this.getExponent(this.getPublicKeyBlob(this.handles.hCryptProv, this.handles.hCryptKey)));
                }
                catch (final KeyException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.exponent;
        }
        
        @Override
        public BigInteger getModulus() {
            if (this.modulus == null) {
                try {
                    this.modulus = new BigInteger(1, this.getModulus(this.getPublicKeyBlob(this.handles.hCryptProv, this.handles.hCryptKey)));
                }
                catch (final KeyException ex) {
                    throw new ProviderException(ex);
                }
            }
            return this.modulus;
        }
        
        @Override
        public byte[] getEncoded() {
            if (this.encoding == null) {
                try {
                    this.encoding = RSAPublicKeyImpl.newKey(RSAUtil.KeyType.RSA, (AlgorithmParameterSpec)null, this.getModulus(), this.getPublicExponent()).getEncoded();
                }
                catch (final KeyException ex) {}
            }
            return this.encoding;
        }
        
        private native byte[] getExponent(final byte[] p0) throws KeyException;
        
        private native byte[] getModulus(final byte[] p0) throws KeyException;
    }
}
