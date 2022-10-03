package com.maverick.ssh.components;

import java.math.BigInteger;
import com.maverick.ssh.SshException;
import com.maverick.events.EventLog;

public abstract class ComponentManager
{
    private static boolean b;
    private static boolean i;
    private static ComponentManager l;
    ComponentFactory n;
    ComponentFactory g;
    ComponentFactory c;
    ComponentFactory j;
    ComponentFactory h;
    ComponentFactory e;
    ComponentFactory f;
    ComponentFactory k;
    ComponentFactory d;
    static Object m;
    
    public static boolean isEnableNoneCipher() {
        return ComponentManager.i;
    }
    
    public static void setEnableNoneCipher(final boolean i) {
        ComponentManager.i = i;
    }
    
    public static void setPerContextAlgorithmPreferences(final boolean b) {
        ComponentManager.b = b;
    }
    
    public static boolean getPerContextAlgorithmPreferences() {
        return ComponentManager.b;
    }
    
    public static ComponentManager getInstance() {
        synchronized (ComponentManager.m) {
            if (ComponentManager.l != null) {
                return ComponentManager.l;
            }
            String property = null;
            try {
                property = System.getProperty("com.maverick.ssh.components.ComponentManager.tryStandaloneCryptographyBeforeJCE", "false");
            }
            catch (final SecurityException ex) {}
            try {
                Class<?> clazz;
                if (property != null && property.equals("false")) {
                    clazz = Class.forName("com.maverick.ssh.components.jce.JCEComponentManager");
                }
                else {
                    clazz = Class.forName("com.maverick.ssh.components.standalone.StandaloneComponentManager");
                }
                (ComponentManager.l = (ComponentManager)clazz.newInstance()).init();
                return ComponentManager.l;
            }
            catch (final Throwable t) {
                try {
                    Class<?> clazz2;
                    if (property != null && property.equals("false")) {
                        clazz2 = Class.forName("com.maverick.ssh.components.standalone.StandaloneComponentManager");
                    }
                    else {
                        clazz2 = Class.forName("com.maverick.ssh.components.jce.JCEComponentManager");
                    }
                    (ComponentManager.l = (ComponentManager)clazz2.newInstance()).init();
                    return ComponentManager.l;
                }
                catch (final Throwable t2) {
                    throw new RuntimeException("Unable to locate a cryptographic provider");
                }
            }
        }
    }
    
    protected void init() throws SshException {
        EventLog.LogEvent(this, "Initializing SSH1 server->client ciphers");
        this.initializeSsh1CipherFactory(this.n = new ComponentFactory(SshCipher.class));
        EventLog.LogEvent(this, "Initializing SSH1 client-server ciphers");
        this.initializeSsh1CipherFactory(this.c = new ComponentFactory(SshCipher.class));
        EventLog.LogEvent(this, "Initializing SSH2 server->client ciphers");
        this.initializeSsh2CipherFactory(this.g = new ComponentFactory(SshCipher.class));
        if (ComponentManager.i) {
            this.g.add("none", NoneCipher.class);
            EventLog.LogEvent(this, "   none will be a supported cipher");
        }
        EventLog.LogEvent(this, "Initializing SSH2 client->server ciphers");
        this.initializeSsh2CipherFactory(this.j = new ComponentFactory(SshCipher.class));
        if (ComponentManager.i) {
            this.j.add("none", NoneCipher.class);
            EventLog.LogEvent(this, "   none will be a supported cipher");
        }
        EventLog.LogEvent(this, "Initializing SSH2 server->client HMACs");
        this.initializeHmacFactory(this.e = new ComponentFactory(SshHmac.class));
        EventLog.LogEvent(this, "Initializing SSH2 client->server HMACs");
        this.initializeHmacFactory(this.h = new ComponentFactory(SshHmac.class));
        EventLog.LogEvent(this, "Initializing public keys");
        this.initializePublicKeyFactory(this.k = new ComponentFactory(SshPublicKey.class));
        EventLog.LogEvent(this, "Initializing digests");
        this.initializeDigestFactory(this.d = new ComponentFactory(SshPublicKey.class));
        EventLog.LogEvent(this, "Initializing SSH2 key exchanges");
        this.initializeKeyExchangeFactory(this.f = new ComponentFactory(SshKeyExchange.class));
        EventLog.LogEvent(this, "Initializing Secure Random Number Generator");
        this.getRND().nextInt();
    }
    
    protected abstract void initializeSsh1CipherFactory(final ComponentFactory p0);
    
    protected abstract void initializeSsh2CipherFactory(final ComponentFactory p0);
    
    protected abstract void initializeHmacFactory(final ComponentFactory p0);
    
    protected abstract void initializePublicKeyFactory(final ComponentFactory p0);
    
    protected abstract void initializeKeyExchangeFactory(final ComponentFactory p0);
    
    protected abstract void initializeDigestFactory(final ComponentFactory p0);
    
    public static void setInstance(final ComponentManager l) {
        ComponentManager.l = l;
    }
    
    public ComponentFactory supportedSsh1CiphersSC() {
        if (ComponentManager.b) {
            return (ComponentFactory)this.n.clone();
        }
        return this.n;
    }
    
    public ComponentFactory supportedSsh1CiphersCS() {
        if (ComponentManager.b) {
            return (ComponentFactory)this.c.clone();
        }
        return this.c;
    }
    
    public ComponentFactory supportedSsh2CiphersSC() {
        if (ComponentManager.b) {
            return (ComponentFactory)this.g.clone();
        }
        return this.g;
    }
    
    public ComponentFactory supportedSsh2CiphersCS() {
        if (ComponentManager.b) {
            return (ComponentFactory)this.j.clone();
        }
        return this.j;
    }
    
    public ComponentFactory supportedHMacsSC() {
        if (ComponentManager.b) {
            return (ComponentFactory)this.e.clone();
        }
        return this.e;
    }
    
    public ComponentFactory supportedHMacsCS() {
        if (ComponentManager.b) {
            return (ComponentFactory)this.h.clone();
        }
        return this.h;
    }
    
    public ComponentFactory supportedKeyExchanges() {
        if (ComponentManager.b) {
            return (ComponentFactory)this.f.clone();
        }
        return this.f;
    }
    
    public ComponentFactory supportedPublicKeys() {
        if (ComponentManager.b) {
            return (ComponentFactory)this.k.clone();
        }
        return this.k;
    }
    
    public ComponentFactory supportedDigests() {
        if (ComponentManager.b) {
            return (ComponentFactory)this.d.clone();
        }
        return this.d;
    }
    
    public abstract SshKeyPair generateRsaKeyPair(final int p0, final int p1) throws SshException;
    
    public abstract SshRsaPublicKey createRsaPublicKey(final BigInteger p0, final BigInteger p1, final int p2) throws SshException;
    
    public abstract SshRsaPublicKey createSsh2RsaPublicKey() throws SshException;
    
    public abstract SshRsaPrivateKey createRsaPrivateKey(final BigInteger p0, final BigInteger p1) throws SshException;
    
    public abstract SshRsaPrivateCrtKey createRsaPrivateCrtKey(final BigInteger p0, final BigInteger p1, final BigInteger p2, final BigInteger p3, final BigInteger p4, final BigInteger p5) throws SshException;
    
    public abstract SshRsaPrivateCrtKey createRsaPrivateCrtKey(final BigInteger p0, final BigInteger p1, final BigInteger p2, final BigInteger p3, final BigInteger p4, final BigInteger p5, final BigInteger p6, final BigInteger p7) throws SshException;
    
    public abstract SshKeyPair generateDsaKeyPair(final int p0) throws SshException;
    
    public abstract SshDsaPublicKey createDsaPublicKey(final BigInteger p0, final BigInteger p1, final BigInteger p2, final BigInteger p3) throws SshException;
    
    public abstract SshDsaPublicKey createDsaPublicKey();
    
    public abstract SshDsaPrivateKey createDsaPrivateKey(final BigInteger p0, final BigInteger p1, final BigInteger p2, final BigInteger p3, final BigInteger p4) throws SshException;
    
    public abstract SshSecureRandomGenerator getRND() throws SshException;
    
    static {
        ComponentManager.b = false;
        ComponentManager.i = false;
        ComponentManager.m = new Object();
    }
}
