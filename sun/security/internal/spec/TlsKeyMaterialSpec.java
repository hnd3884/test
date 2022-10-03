package sun.security.internal.spec;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;

@Deprecated
public class TlsKeyMaterialSpec implements KeySpec, SecretKey
{
    static final long serialVersionUID = 812912859129525028L;
    private final SecretKey clientMacKey;
    private final SecretKey serverMacKey;
    private final SecretKey clientCipherKey;
    private final SecretKey serverCipherKey;
    private final IvParameterSpec clientIv;
    private final IvParameterSpec serverIv;
    
    public TlsKeyMaterialSpec(final SecretKey secretKey, final SecretKey secretKey2) {
        this(secretKey, secretKey2, null, null, null, null);
    }
    
    public TlsKeyMaterialSpec(final SecretKey secretKey, final SecretKey secretKey2, final SecretKey secretKey3, final SecretKey secretKey4) {
        this(secretKey, secretKey2, secretKey3, null, secretKey4, null);
    }
    
    public TlsKeyMaterialSpec(final SecretKey clientMacKey, final SecretKey serverMacKey, final SecretKey clientCipherKey, final IvParameterSpec clientIv, final SecretKey serverCipherKey, final IvParameterSpec serverIv) {
        this.clientMacKey = clientMacKey;
        this.serverMacKey = serverMacKey;
        this.clientCipherKey = clientCipherKey;
        this.serverCipherKey = serverCipherKey;
        this.clientIv = clientIv;
        this.serverIv = serverIv;
    }
    
    @Override
    public String getAlgorithm() {
        return "TlsKeyMaterial";
    }
    
    @Override
    public String getFormat() {
        return null;
    }
    
    @Override
    public byte[] getEncoded() {
        return null;
    }
    
    public SecretKey getClientMacKey() {
        return this.clientMacKey;
    }
    
    public SecretKey getServerMacKey() {
        return this.serverMacKey;
    }
    
    public SecretKey getClientCipherKey() {
        return this.clientCipherKey;
    }
    
    public IvParameterSpec getClientIv() {
        return this.clientIv;
    }
    
    public SecretKey getServerCipherKey() {
        return this.serverCipherKey;
    }
    
    public IvParameterSpec getServerIv() {
        return this.serverIv;
    }
}
