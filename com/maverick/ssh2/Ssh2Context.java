package com.maverick.ssh2;

import java.util.Vector;
import com.maverick.events.EventLog;
import com.maverick.ssh.SshException;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.ForwardingRequestListener;
import com.maverick.ssh.HostKeyVerification;
import com.maverick.ssh.components.ComponentFactory;
import com.maverick.ssh.SshContext;

public final class Ssh2Context implements SshContext
{
    ComponentFactory fb;
    ComponentFactory cb;
    ComponentFactory ab;
    ComponentFactory w;
    ComponentFactory o;
    ComponentFactory lb;
    ComponentFactory hb;
    ComponentFactory db;
    public static final String CIPHER_TRIPLEDES_CBC = "3des-cbc";
    public static final String CIPHER_TRIPLEDES_CTR = "3des-ctr";
    public static final String CIPHER_BLOWFISH_CBC = "blowfish-cbc";
    public static final String CIPHER_AES128_CBC = "aes128-cbc";
    public static final String CIPHER_AES192_CBC = "aes192-cbc";
    public static final String CIPHER_AES256_CBC = "aes256-cbc";
    public static final String CIPHER_AES128_CTR = "aes128-ctr";
    public static final String CIPHER_AES192_CTR = "aes192-ctr";
    public static final String CIPHER_AES256_CTR = "aes256-ctr";
    public static final String CIPHER_ARCFOUR = "arcfour";
    public static final String CIPHER_ARCFOUR_128 = "arcfour128";
    public static final String CIPHER_ARCFOUR_256 = "arcfour256";
    public static final String HMAC_SHA1 = "hmac-sha1";
    public static final String HMAC_SHA1_96 = "hmac-sha1-96";
    public static final String HMAC_MD5 = "hmac-md5";
    public static final String HMAC_MD5_96 = "hmac-md5-96";
    public static final String HMAC_SHA256 = "hmac-sha256";
    public static final String COMPRESSION_NONE = "none";
    public static final String COMPRESSION_ZLIB = "zlib";
    public static final String KEX_DIFFIE_HELLMAN_GROUP1_SHA1 = "diffie-hellman-group1-sha1";
    public static final String KEX_DIFFIE_HELLMAN_GROUP14_SHA1 = "diffie-hellman-group14-sha1";
    public static final String KEX_DIFFIE_HELLMAN_GROUP_EXCHANGE_SHA1 = "diffie-hellman-group-exchange-sha1";
    public static final String KEX_DIFFIE_HELLMAN_GROUP_EXCHANGE_SHA256 = "diffie-hellman-group-exchange-sha256";
    public static final String PUBLIC_KEY_SSHDSS = "ssh-dss";
    public static final String PUBLIC_KEY_SSHRSA = "ssh-rsa";
    String qb;
    String ob;
    String m;
    String k;
    String n;
    String l;
    String z;
    String y;
    String r;
    int u;
    BannerDisplay s;
    HostKeyVerification t;
    String mb;
    byte[] bb;
    byte[] pb;
    ForwardingRequestListener nb;
    String kb;
    int q;
    boolean gb;
    int v;
    int eb;
    int x;
    boolean jb;
    int ib;
    MaverickCallbackHandler p;
    
    public Ssh2Context() throws SshException {
        this.qb = "3des-cbc";
        this.ob = "3des-cbc";
        this.m = "hmac-sha1";
        this.k = "hmac-sha1";
        this.n = "none";
        this.l = "none";
        this.z = "diffie-hellman-group1-sha1";
        this.y = "ssh-dss";
        this.r = "/usr/libexec/sftp-server";
        this.u = 100;
        this.mb = null;
        this.bb = null;
        this.pb = null;
        this.nb = null;
        this.kb = "";
        this.q = 131072;
        this.gb = false;
        this.v = 30000;
        this.eb = 128;
        this.x = 0;
        this.jb = false;
        this.ib = 0;
        this.p = null;
        try {
            this.ab = ComponentManager.getInstance().supportedSsh2CiphersCS();
            this.w = ComponentManager.getInstance().supportedSsh2CiphersSC();
            this.o = ComponentManager.getInstance().supportedKeyExchanges();
            this.lb = ComponentManager.getInstance().supportedHMacsCS();
            this.hb = ComponentManager.getInstance().supportedHMacsSC();
            this.db = ComponentManager.getInstance().supportedPublicKeys();
            if (this.ab.contains("aes128-cbc")) {
                this.qb = "aes128-cbc";
            }
            if (this.w.contains("aes128-cbc")) {
                this.ob = "aes128-cbc";
            }
            (this.cb = new ComponentFactory(Class.forName("com.maverick.ssh.compression.SshCompression"))).add("none", Class.forName("java.lang.Object"));
            try {
                this.cb.add("zlib", Class.forName("com.sshtools.zlib.ZLibCompression"));
                this.cb.add("zlib@openssh.com", Class.forName("com.sshtools.zlib.OpenSSHZLibCompression"));
            }
            catch (final Throwable t) {}
            (this.fb = new ComponentFactory(Class.forName("com.maverick.ssh.compression.SshCompression"))).add("none", Class.forName("java.lang.Object"));
            try {
                this.fb.add("zlib", Class.forName("com.sshtools.zlib.ZLibCompression"));
                this.fb.add("zlib@openssh.com", Class.forName("com.sshtools.zlib.OpenSSHZLibCompression"));
            }
            catch (final Throwable t2) {}
        }
        catch (final Throwable t3) {
            throw new SshException((t3.getMessage() != null) ? t3.getMessage() : t3.getClass().getName(), 5);
        }
    }
    
    public int getMaximumPacketLength() {
        return this.q;
    }
    
    public void setGssCallback(final MaverickCallbackHandler p) {
        this.p = p;
    }
    
    public MaverickCallbackHandler getGssCallback() {
        return this.p;
    }
    
    public void setMaximumPacketLength(final int q) {
        if (q < 35000) {
            throw new IllegalArgumentException("The minimum packet length supported must be 35,000 bytes or greater!");
        }
        this.q = q;
    }
    
    public void setChannelLimit(final int u) {
        this.u = u;
    }
    
    public int getChannelLimit() {
        return this.u;
    }
    
    public void setX11Display(final String mb) {
        this.mb = mb;
    }
    
    public String getX11Display() {
        return this.mb;
    }
    
    public byte[] getX11AuthenticationCookie() throws SshException {
        if (this.bb == null) {
            this.bb = new byte[16];
            ComponentManager.getInstance().getRND().nextBytes(this.bb);
        }
        return this.bb;
    }
    
    public void setX11AuthenticationCookie(final byte[] bb) {
        this.bb = bb;
    }
    
    public void setX11RealCookie(final byte[] pb) {
        this.pb = pb;
    }
    
    public byte[] getX11RealCookie() throws SshException {
        if (this.pb == null) {
            this.pb = this.getX11AuthenticationCookie();
        }
        return this.pb;
    }
    
    public void setX11RequestListener(final ForwardingRequestListener nb) {
        this.nb = nb;
    }
    
    public ForwardingRequestListener getX11RequestListener() {
        return this.nb;
    }
    
    public BannerDisplay getBannerDisplay() {
        return this.s;
    }
    
    public void setBannerDisplay(final BannerDisplay s) {
        this.s = s;
    }
    
    public ComponentFactory supportedCiphersSC() {
        return this.w;
    }
    
    public ComponentFactory supportedCiphersCS() {
        return this.ab;
    }
    
    public String getPreferredCipherCS() {
        return this.qb;
    }
    
    public void setPreferredCipherCS(final String qb) throws SshException {
        if (qb == null) {
            return;
        }
        if (this.ab.contains(qb)) {
            this.setCipherPreferredPositionCS(this.qb = qb, 0);
            return;
        }
        throw new SshException(qb + " is not supported", 7);
    }
    
    public String getPreferredCipherSC() {
        return this.ob;
    }
    
    public String getCiphersCS() {
        return this.ab.list(this.qb);
    }
    
    public String getCiphersSC() {
        return this.w.list(this.ob);
    }
    
    public String getMacsCS() {
        return this.lb.list(this.m);
    }
    
    public String getMacsSC() {
        return this.hb.list(this.k);
    }
    
    public String getPublicKeys() {
        return this.db.list(this.y);
    }
    
    public String getKeyExchanges() {
        return this.o.list(this.z);
    }
    
    public void setPreferredCipherSC(final int[] array) throws SshException {
        this.ob = this.w.createNewOrdering(array);
    }
    
    public void setPreferredCipherCS(final int[] array) throws SshException {
        this.qb = this.ab.createNewOrdering(array);
    }
    
    public void setCipherPreferredPositionCS(final String s, final int n) throws SshException {
        this.qb = this.ab.changePositionofAlgorithm(s, n);
    }
    
    public void setCipherPreferredPositionSC(final String s, final int n) throws SshException {
        this.ob = this.w.changePositionofAlgorithm(s, n);
    }
    
    public void setMacPreferredPositionSC(final String s, final int n) throws SshException {
        this.k = this.hb.changePositionofAlgorithm(s, n);
    }
    
    public void setMacPreferredPositionCS(final String s, final int n) throws SshException {
        this.m = this.lb.changePositionofAlgorithm(s, n);
    }
    
    public void setPreferredMacSC(final int[] array) throws SshException {
        this.ob = this.hb.createNewOrdering(array);
    }
    
    public void setPreferredMacCS(final int[] array) throws SshException {
        this.ob = this.lb.createNewOrdering(array);
    }
    
    public void setPreferredCipherSC(final String ob) throws SshException {
        if (ob == null) {
            return;
        }
        if (this.w.contains(ob)) {
            this.setCipherPreferredPositionSC(this.ob = ob, 0);
            return;
        }
        throw new SshException(ob + " is not supported", 7);
    }
    
    public ComponentFactory supportedMacsSC() {
        return this.hb;
    }
    
    public ComponentFactory supportedMacsCS() {
        return this.lb;
    }
    
    public String getPreferredMacCS() {
        return this.m;
    }
    
    public void setPreferredMacCS(final String m) throws SshException {
        if (m == null) {
            return;
        }
        if (this.lb.contains(m)) {
            this.setMacPreferredPositionCS(this.m = m, 0);
            return;
        }
        throw new SshException(m + " is not supported", 7);
    }
    
    public String getPreferredMacSC() {
        return this.k;
    }
    
    public void setPreferredMacSC(final String k) throws SshException {
        if (k == null) {
            return;
        }
        if (this.hb.contains(k)) {
            this.setMacPreferredPositionSC(this.k = k, 0);
            return;
        }
        throw new SshException(k + " is not supported", 7);
    }
    
    public ComponentFactory supportedCompressionsSC() {
        return this.cb;
    }
    
    public ComponentFactory supportedCompressionsCS() {
        return this.fb;
    }
    
    public String getPreferredCompressionCS() {
        return this.n;
    }
    
    public void setPreferredCompressionCS(final String n) throws SshException {
        if (n == null) {
            return;
        }
        if (this.fb.contains(n)) {
            this.n = n;
            return;
        }
        throw new SshException(n + " is not supported", 7);
    }
    
    public String getPreferredCompressionSC() {
        return this.l;
    }
    
    public void setPreferredCompressionSC(final String l) throws SshException {
        if (l == null) {
            return;
        }
        if (this.cb.contains(l)) {
            this.l = l;
            return;
        }
        throw new SshException(l + " is not supported", 7);
    }
    
    public void enableCompression() throws SshException {
        this.supportedCompressionsCS().changePositionofAlgorithm("zlib", 0);
        this.supportedCompressionsCS().changePositionofAlgorithm("zlib@openssh.com", 1);
        this.n = this.supportedCompressionsCS().changePositionofAlgorithm("none", 2);
        this.supportedCompressionsSC().changePositionofAlgorithm("zlib", 0);
        this.supportedCompressionsSC().changePositionofAlgorithm("zlib@openssh.com", 1);
        this.l = this.supportedCompressionsSC().changePositionofAlgorithm("none", 2);
    }
    
    public void disableCompression() throws SshException {
        this.supportedCompressionsCS().changePositionofAlgorithm("none", 0);
        this.supportedCompressionsCS().changePositionofAlgorithm("zlib", 1);
        this.n = this.supportedCompressionsCS().changePositionofAlgorithm("zlib@openssh.com", 2);
        this.supportedCompressionsSC().changePositionofAlgorithm("none", 0);
        this.supportedCompressionsSC().changePositionofAlgorithm("zlib", 1);
        this.l = this.supportedCompressionsSC().changePositionofAlgorithm("zlib@openssh.com", 2);
    }
    
    public ComponentFactory supportedKeyExchanges() {
        return this.o;
    }
    
    public String getPreferredKeyExchange() {
        return this.z;
    }
    
    public void setPreferredKeyExchange(final String z) throws SshException {
        if (z == null) {
            return;
        }
        if (this.o.contains(z)) {
            this.setKeyExchangePreferredPosition(this.z = z, 0);
            return;
        }
        throw new SshException(z + " is not supported", 7);
    }
    
    public ComponentFactory supportedPublicKeys() {
        return this.db;
    }
    
    public String getPreferredPublicKey() {
        return this.y;
    }
    
    public void setPreferredPublicKey(final String y) throws SshException {
        if (y == null) {
            return;
        }
        if (this.db.contains(y)) {
            this.setPublicKeyPreferredPosition(this.y = y, 0);
            return;
        }
        throw new SshException(y + " is not supported", 7);
    }
    
    public void setHostKeyVerification(final HostKeyVerification t) {
        this.t = t;
    }
    
    public HostKeyVerification getHostKeyVerification() {
        return this.t;
    }
    
    public void setSFTPProvider(final String r) {
        this.r = r;
    }
    
    public String getSFTPProvider() {
        return this.r;
    }
    
    public void setPartialMessageTimeout(final int v) {
        this.v = v;
    }
    
    public int getPartialMessageTimeout() {
        return this.v;
    }
    
    public boolean isKeyReExchangeDisabled() {
        return this.gb;
    }
    
    public void setKeyReExchangeDisabled(final boolean gb) {
        this.gb = gb;
    }
    
    public void setPublicKeyPreferredPosition(final String s, final int n) throws SshException {
        this.y = this.db.changePositionofAlgorithm(s, n);
    }
    
    public void setKeyExchangePreferredPosition(final String s, final int n) throws SshException {
        this.z = this.o.changePositionofAlgorithm(s, n);
    }
    
    public int getIdleConnectionTimeoutSeconds() {
        return this.x;
    }
    
    public void setIdleConnectionTimeoutSeconds(final int x) {
        this.x = x;
    }
    
    public boolean isSendIgnorePacketOnIdle() {
        return this.jb;
    }
    
    public void setSendIgnorePacketOnIdle(final boolean jb) {
        this.jb = jb;
    }
    
    public int getKeepAliveMaxDataLength() {
        return this.eb;
    }
    
    public void setKeepAliveMaxDataLength(final int eb) {
        if (eb < 8) {
            throw new IllegalArgumentException("There must be at least 8 bytes of random data");
        }
        this.eb = eb;
    }
    
    public int getSocketTimeout() {
        return this.ib;
    }
    
    public void setSocketTimeout(final int ib) {
        this.ib = ib;
    }
    
    public void enableFIPSMode() throws SshException {
        EventLog.LogEvent(this, "Enabling FIPS mode");
        if (!this.o.contains("diffie-hellman-group14-sha1")) {
            throw new SshException("Cannot enable FIPS mode because diffie-hellman-group14-sha1 keyexchange was not supported by this configuration. Install a JCE Provider that supports a prime size of 2048 bits (for example BouncyCastle provider)", 4);
        }
        final Vector vector = new Vector();
        vector.addElement("diffie-hellman-group14-sha1");
        final String[] array = this.o.toArray();
        for (int i = 0; i < array.length; ++i) {
            if (!vector.contains(array[i])) {
                EventLog.LogEvent(this, "Removing key exchange " + array[i]);
                this.o.remove(array[i]);
            }
        }
        this.o.lockComponents();
        vector.clear();
        vector.addElement("aes128-cbc");
        vector.addElement("aes192-cbc");
        vector.addElement("aes256-cbc");
        vector.addElement("3des-cbc");
        final String[] array2 = this.ab.toArray();
        for (int j = 0; j < array2.length; ++j) {
            if (!vector.contains(array2[j])) {
                EventLog.LogEvent(this, "Removing cipher client->server " + array2[j]);
                this.ab.remove(array2[j]);
            }
        }
        this.ab.lockComponents();
        final String[] array3 = this.w.toArray();
        for (int k = 0; k < array3.length; ++k) {
            if (!vector.contains(array3[k])) {
                EventLog.LogEvent(this, "Removing cipher server->client " + array3[k]);
                this.w.remove(array3[k]);
            }
        }
        this.w.lockComponents();
        vector.clear();
        vector.addElement("ssh-rsa");
        final String[] array4 = this.db.toArray();
        for (int l = 0; l < array4.length; ++l) {
            if (!vector.contains(array4[l])) {
                EventLog.LogEvent(this, "Removing public key " + array4[l]);
                this.db.remove(array4[l]);
            }
        }
        this.db.lockComponents();
        vector.clear();
        vector.addElement("hmac-sha1");
        vector.addElement("hmac-sha256");
        vector.addElement("hmac-sha256@ssh.com");
        final String[] array5 = this.lb.toArray();
        for (int n = 0; n < array5.length; ++n) {
            if (!vector.contains(array5[n])) {
                EventLog.LogEvent(this, "Removing mac client->server " + array5[n]);
                this.lb.remove(array5[n]);
            }
        }
        this.lb.lockComponents();
        final String[] array6 = this.hb.toArray();
        for (int n2 = 0; n2 < array6.length; ++n2) {
            if (!vector.contains(array6[n2])) {
                EventLog.LogEvent(this, "Removing mac server->client " + array6[n2]);
                this.hb.remove(array6[n2]);
            }
        }
        this.lb.lockComponents();
    }
}
