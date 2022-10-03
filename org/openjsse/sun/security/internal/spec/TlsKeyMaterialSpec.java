package org.openjsse.sun.security.internal.spec;

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
    
    public TlsKeyMaterialSpec(final SecretKey clientMacKey, final SecretKey serverMacKey) {
        this(clientMacKey, serverMacKey, null, null, null, null);
    }
    
    public TlsKeyMaterialSpec(final SecretKey clientMacKey, final SecretKey serverMacKey, final SecretKey clientCipherKey, final SecretKey serverCipherKey) {
        this(clientMacKey, serverMacKey, clientCipherKey, null, serverCipherKey, null);
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
