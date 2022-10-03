package com.maverick.ssh.components.jce;

import com.maverick.ssh.SshException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import com.maverick.ssh.components.SshHmac;

public abstract class AbstractHmac implements SshHmac
{
    protected Mac mac;
    protected int macSize;
    protected int macLength;
    protected String jceAlgorithm;
    
    public AbstractHmac(final String s, final int n) {
        this(s, n, n);
    }
    
    public AbstractHmac(final String jceAlgorithm, final int macSize, final int macLength) {
        this.jceAlgorithm = jceAlgorithm;
        this.macSize = macSize;
        this.macLength = macLength;
    }
    
    public void generate(final long n, final byte[] array, final int n2, final int n3, final byte[] array2, final int n4) {
        this.mac.update(new byte[] { (byte)(n >> 24), (byte)(n >> 16), (byte)(n >> 8), (byte)(n >> 0) });
        this.mac.update(array, n2, n3);
        System.arraycopy(this.mac.doFinal(), 0, array2, n4, this.macLength);
    }
    
    public void update(final byte[] array) {
        this.mac.update(array);
    }
    
    public byte[] doFinal() {
        return this.mac.doFinal();
    }
    
    public abstract String getAlgorithm();
    
    public String getProvider() {
        return this.mac.getProvider().getName();
    }
    
    public int getMacLength() {
        return this.macLength;
    }
    
    public void init(final byte[] array) throws SshException {
        try {
            this.mac = ((JCEProvider.getProviderForAlgorithm(this.jceAlgorithm) == null) ? Mac.getInstance(this.jceAlgorithm) : Mac.getInstance(this.jceAlgorithm, JCEProvider.getProviderForAlgorithm(this.jceAlgorithm)));
            final byte[] array2 = new byte[this.macSize];
            System.arraycopy(array, 0, array2, 0, array2.length);
            this.mac.init(new SecretKeySpec(array2, this.jceAlgorithm));
        }
        catch (final Throwable t) {
            throw new SshException(t);
        }
    }
    
    public boolean verify(final long n, final byte[] array, final int n2, final int n3, final byte[] array2, final int n4) {
        final byte[] array3 = new byte[this.getMacLength()];
        this.generate(n, array, n2, n3, array3, 0);
        for (int i = 0; i < array3.length; ++i) {
            if (array2[i + n4] != array3[i]) {
                return false;
            }
        }
        return true;
    }
}
