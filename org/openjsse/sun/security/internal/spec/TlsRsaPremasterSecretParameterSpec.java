package org.openjsse.sun.security.internal.spec;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.spec.AlgorithmParameterSpec;

@Deprecated
public class TlsRsaPremasterSecretParameterSpec implements AlgorithmParameterSpec
{
    private final byte[] encodedSecret;
    private static final String PROP_NAME = "com.sun.net.ssl.rsaPreMasterSecretFix";
    private static final boolean rsaPreMasterSecretFix;
    private final int clientVersion;
    private final int serverVersion;
    
    public TlsRsaPremasterSecretParameterSpec(final int clientVersion, final int serverVersion) {
        this.clientVersion = this.checkVersion(clientVersion);
        this.serverVersion = this.checkVersion(serverVersion);
        this.encodedSecret = null;
    }
    
    public TlsRsaPremasterSecretParameterSpec(final int clientVersion, final int serverVersion, final byte[] encodedSecret) {
        this.clientVersion = this.checkVersion(clientVersion);
        this.serverVersion = this.checkVersion(serverVersion);
        if (encodedSecret == null || encodedSecret.length != 48) {
            throw new IllegalArgumentException("Encoded secret is not exactly 48 bytes");
        }
        this.encodedSecret = encodedSecret.clone();
    }
    
    public int getClientVersion() {
        return this.clientVersion;
    }
    
    public int getServerVersion() {
        return this.serverVersion;
    }
    
    public int getMajorVersion() {
        if (TlsRsaPremasterSecretParameterSpec.rsaPreMasterSecretFix || this.clientVersion >= 770) {
            return this.clientVersion >>> 8 & 0xFF;
        }
        return this.serverVersion >>> 8 & 0xFF;
    }
    
    public int getMinorVersion() {
        if (TlsRsaPremasterSecretParameterSpec.rsaPreMasterSecretFix || this.clientVersion >= 770) {
            return this.clientVersion & 0xFF;
        }
        return this.serverVersion & 0xFF;
    }
    
    private int checkVersion(final int version) {
        if (version < 0 || version > 65535) {
            throw new IllegalArgumentException("Version must be between 0 and 65,535");
        }
        return version;
    }
    
    public byte[] getEncodedSecret() {
        return (byte[])((this.encodedSecret == null) ? null : ((byte[])this.encodedSecret.clone()));
    }
    
    static {
        rsaPreMasterSecretFix = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String value = System.getProperty("com.sun.net.ssl.rsaPreMasterSecretFix");
                if (value != null && value.equalsIgnoreCase("true")) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        });
    }
}
