package sun.security.internal.spec;

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
    
    public TlsRsaPremasterSecretParameterSpec(final int n, final int n2) {
        this.clientVersion = this.checkVersion(n);
        this.serverVersion = this.checkVersion(n2);
        this.encodedSecret = null;
    }
    
    public TlsRsaPremasterSecretParameterSpec(final int n, final int n2, final byte[] array) {
        this.clientVersion = this.checkVersion(n);
        this.serverVersion = this.checkVersion(n2);
        if (array == null || array.length != 48) {
            throw new IllegalArgumentException("Encoded secret is not exactly 48 bytes");
        }
        this.encodedSecret = array.clone();
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
    
    private int checkVersion(final int n) {
        if (n < 0 || n > 65535) {
            throw new IllegalArgumentException("Version must be between 0 and 65,535");
        }
        return n;
    }
    
    public byte[] getEncodedSecret() {
        return (byte[])((this.encodedSecret == null) ? null : ((byte[])this.encodedSecret.clone()));
    }
    
    static {
        rsaPreMasterSecretFix = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String property = System.getProperty("com.sun.net.ssl.rsaPreMasterSecretFix");
                if (property != null && property.equalsIgnoreCase("true")) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        });
    }
}
