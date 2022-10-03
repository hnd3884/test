package sun.security.pkcs11.wrapper;

import sun.security.pkcs11.P11Util;
import java.math.BigInteger;

public class CK_MECHANISM
{
    public long mechanism;
    public Object pParameter;
    private long pHandle;
    
    public CK_MECHANISM(final long mechanism) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.mechanism = mechanism;
    }
    
    public CK_MECHANISM(final long n, final byte[] array) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, array);
    }
    
    public CK_MECHANISM(final long n, final BigInteger bigInteger) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, P11Util.getMagnitude(bigInteger));
    }
    
    public CK_MECHANISM(final long n, final CK_VERSION ck_VERSION) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_VERSION);
    }
    
    public CK_MECHANISM(final long n, final CK_SSL3_MASTER_KEY_DERIVE_PARAMS ck_SSL3_MASTER_KEY_DERIVE_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_SSL3_MASTER_KEY_DERIVE_PARAMS);
    }
    
    public CK_MECHANISM(final long n, final CK_TLS12_MASTER_KEY_DERIVE_PARAMS ck_TLS12_MASTER_KEY_DERIVE_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_TLS12_MASTER_KEY_DERIVE_PARAMS);
    }
    
    public CK_MECHANISM(final long n, final CK_SSL3_KEY_MAT_PARAMS ck_SSL3_KEY_MAT_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_SSL3_KEY_MAT_PARAMS);
    }
    
    public CK_MECHANISM(final long n, final CK_TLS12_KEY_MAT_PARAMS ck_TLS12_KEY_MAT_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_TLS12_KEY_MAT_PARAMS);
    }
    
    public CK_MECHANISM(final long n, final CK_TLS_PRF_PARAMS ck_TLS_PRF_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_TLS_PRF_PARAMS);
    }
    
    public CK_MECHANISM(final long n, final CK_TLS_MAC_PARAMS ck_TLS_MAC_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_TLS_MAC_PARAMS);
    }
    
    public CK_MECHANISM(final long n, final CK_ECDH1_DERIVE_PARAMS ck_ECDH1_DERIVE_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_ECDH1_DERIVE_PARAMS);
    }
    
    public CK_MECHANISM(final long n, final Long n2) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, n2);
    }
    
    public CK_MECHANISM(final long n, final CK_AES_CTR_PARAMS ck_AES_CTR_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_AES_CTR_PARAMS);
    }
    
    public CK_MECHANISM(final long n, final CK_GCM_PARAMS ck_GCM_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_GCM_PARAMS);
    }
    
    public CK_MECHANISM(final long n, final CK_CCM_PARAMS ck_CCM_PARAMS) {
        this.pParameter = null;
        this.pHandle = 0L;
        this.init(n, ck_CCM_PARAMS);
    }
    
    public void setParameter(final CK_RSA_PKCS_PSS_PARAMS pParameter) {
        assert this.mechanism == 13L;
        assert pParameter != null;
        if (this.pParameter != null && this.pParameter.equals(pParameter)) {
            return;
        }
        this.freeHandle();
        this.pParameter = pParameter;
    }
    
    public void freeHandle() {
        if (this.pHandle != 0L) {
            this.pHandle = PKCS11.freeMechanism(this.pHandle);
        }
    }
    
    private void init(final long mechanism, final Object pParameter) {
        this.mechanism = mechanism;
        this.pParameter = pParameter;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("mechanism: ");
        sb.append(this.mechanism);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pParameter: ");
        sb.append(this.pParameter.toString());
        sb.append(Constants.NEWLINE);
        if (this.pHandle != 0L) {
            sb.append("  ");
            sb.append("pHandle: ");
            sb.append(this.pHandle);
            sb.append(Constants.NEWLINE);
        }
        return sb.toString();
    }
}
