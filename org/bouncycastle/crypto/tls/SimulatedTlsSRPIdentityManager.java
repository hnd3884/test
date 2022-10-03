package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Strings;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.agreement.srp.SRP6VerifierGenerator;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public class SimulatedTlsSRPIdentityManager implements TlsSRPIdentityManager
{
    private static final byte[] PREFIX_PASSWORD;
    private static final byte[] PREFIX_SALT;
    protected SRP6GroupParameters group;
    protected SRP6VerifierGenerator verifierGenerator;
    protected Mac mac;
    
    public static SimulatedTlsSRPIdentityManager getRFC5054Default(final SRP6GroupParameters srp6GroupParameters, final byte[] array) {
        final SRP6VerifierGenerator srp6VerifierGenerator = new SRP6VerifierGenerator();
        srp6VerifierGenerator.init(srp6GroupParameters, TlsUtils.createHash((short)2));
        final HMac hMac = new HMac(TlsUtils.createHash((short)2));
        hMac.init(new KeyParameter(array));
        return new SimulatedTlsSRPIdentityManager(srp6GroupParameters, srp6VerifierGenerator, hMac);
    }
    
    public SimulatedTlsSRPIdentityManager(final SRP6GroupParameters group, final SRP6VerifierGenerator verifierGenerator, final Mac mac) {
        this.group = group;
        this.verifierGenerator = verifierGenerator;
        this.mac = mac;
    }
    
    public TlsSRPLoginParameters getLoginParameters(final byte[] array) {
        this.mac.update(SimulatedTlsSRPIdentityManager.PREFIX_SALT, 0, SimulatedTlsSRPIdentityManager.PREFIX_SALT.length);
        this.mac.update(array, 0, array.length);
        final byte[] array2 = new byte[this.mac.getMacSize()];
        this.mac.doFinal(array2, 0);
        this.mac.update(SimulatedTlsSRPIdentityManager.PREFIX_PASSWORD, 0, SimulatedTlsSRPIdentityManager.PREFIX_PASSWORD.length);
        this.mac.update(array, 0, array.length);
        final byte[] array3 = new byte[this.mac.getMacSize()];
        this.mac.doFinal(array3, 0);
        return new TlsSRPLoginParameters(this.group, this.verifierGenerator.generateVerifier(array2, array, array3), array2);
    }
    
    static {
        PREFIX_PASSWORD = Strings.toByteArray("password");
        PREFIX_SALT = Strings.toByteArray("salt");
    }
}
