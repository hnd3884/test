package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Times;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.RandomGenerator;

abstract class AbstractTlsContext implements TlsContext
{
    private static long counter;
    private RandomGenerator nonceRandom;
    private SecureRandom secureRandom;
    private SecurityParameters securityParameters;
    private ProtocolVersion clientVersion;
    private ProtocolVersion serverVersion;
    private TlsSession session;
    private Object userObject;
    
    private static synchronized long nextCounterValue() {
        return ++AbstractTlsContext.counter;
    }
    
    AbstractTlsContext(final SecureRandom secureRandom, final SecurityParameters securityParameters) {
        this.clientVersion = null;
        this.serverVersion = null;
        this.session = null;
        this.userObject = null;
        final Digest hash = TlsUtils.createHash((short)4);
        final byte[] array = new byte[hash.getDigestSize()];
        secureRandom.nextBytes(array);
        (this.nonceRandom = new DigestRandomGenerator(hash)).addSeedMaterial(nextCounterValue());
        this.nonceRandom.addSeedMaterial(Times.nanoTime());
        this.nonceRandom.addSeedMaterial(array);
        this.secureRandom = secureRandom;
        this.securityParameters = securityParameters;
    }
    
    public RandomGenerator getNonceRandomGenerator() {
        return this.nonceRandom;
    }
    
    public SecureRandom getSecureRandom() {
        return this.secureRandom;
    }
    
    public SecurityParameters getSecurityParameters() {
        return this.securityParameters;
    }
    
    public ProtocolVersion getClientVersion() {
        return this.clientVersion;
    }
    
    void setClientVersion(final ProtocolVersion clientVersion) {
        this.clientVersion = clientVersion;
    }
    
    public ProtocolVersion getServerVersion() {
        return this.serverVersion;
    }
    
    void setServerVersion(final ProtocolVersion serverVersion) {
        this.serverVersion = serverVersion;
    }
    
    public TlsSession getResumableSession() {
        return this.session;
    }
    
    void setResumableSession(final TlsSession session) {
        this.session = session;
    }
    
    public Object getUserObject() {
        return this.userObject;
    }
    
    public void setUserObject(final Object userObject) {
        this.userObject = userObject;
    }
    
    public byte[] exportKeyingMaterial(final String s, final byte[] array, final int n) {
        if (array != null && !TlsUtils.isValidUint16(array.length)) {
            throw new IllegalArgumentException("'context_value' must have length less than 2^16 (or be null)");
        }
        final SecurityParameters securityParameters = this.getSecurityParameters();
        final byte[] clientRandom = securityParameters.getClientRandom();
        final byte[] serverRandom = securityParameters.getServerRandom();
        int n2 = clientRandom.length + serverRandom.length;
        if (array != null) {
            n2 += 2 + array.length;
        }
        final byte[] array2 = new byte[n2];
        final int n3 = 0;
        System.arraycopy(clientRandom, 0, array2, n3, clientRandom.length);
        final int n4 = n3 + clientRandom.length;
        System.arraycopy(serverRandom, 0, array2, n4, serverRandom.length);
        int n5 = n4 + serverRandom.length;
        if (array != null) {
            TlsUtils.writeUint16(array.length, array2, n5);
            n5 += 2;
            System.arraycopy(array, 0, array2, n5, array.length);
            n5 += array.length;
        }
        if (n5 != n2) {
            throw new IllegalStateException("error in calculation of seed for export");
        }
        return TlsUtils.PRF(this, securityParameters.getMasterSecret(), s, array2, n);
    }
    
    static {
        AbstractTlsContext.counter = Times.nanoTime();
    }
}
