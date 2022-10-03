package com.unboundid.util.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedHashSet;
import com.unboundid.util.StaticUtils;
import java.security.Principal;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import javax.net.ssl.X509ExtendedKeyManager;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class WrapperKeyManager extends X509ExtendedKeyManager
{
    private final String certificateAlias;
    private final X509KeyManager[] keyManagers;
    
    protected WrapperKeyManager(final KeyManager[] keyManagers, final String certificateAlias) {
        this.certificateAlias = certificateAlias;
        this.keyManagers = new X509KeyManager[keyManagers.length];
        for (int i = 0; i < keyManagers.length; ++i) {
            this.keyManagers[i] = (X509KeyManager)keyManagers[i];
        }
    }
    
    protected WrapperKeyManager(final X509KeyManager[] keyManagers, final String certificateAlias) {
        this.keyManagers = keyManagers;
        this.certificateAlias = certificateAlias;
    }
    
    public String getCertificateAlias() {
        return this.certificateAlias;
    }
    
    @Override
    public final synchronized String[] getClientAliases(final String keyType, final Principal[] issuers) {
        final LinkedHashSet<String> clientAliases = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(10));
        for (final X509KeyManager m : this.keyManagers) {
            final String[] aliases = m.getClientAliases(keyType, issuers);
            if (aliases != null) {
                clientAliases.addAll((Collection<?>)Arrays.asList(aliases));
            }
        }
        if (clientAliases.isEmpty()) {
            return null;
        }
        final String[] aliases2 = new String[clientAliases.size()];
        return clientAliases.toArray(aliases2);
    }
    
    @Override
    public final synchronized String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        if (this.certificateAlias == null) {
            for (final X509KeyManager m : this.keyManagers) {
                final String alias = m.chooseClientAlias(keyType, issuers, socket);
                if (alias != null) {
                    return alias;
                }
            }
            return null;
        }
        for (final String s : keyType) {
            for (final X509KeyManager i : this.keyManagers) {
                final String[] aliases = i.getClientAliases(s, issuers);
                if (aliases != null) {
                    for (final String alias2 : aliases) {
                        if (alias2.equals(this.certificateAlias)) {
                            return this.certificateAlias;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public final synchronized String chooseEngineClientAlias(final String[] keyType, final Principal[] issuers, final SSLEngine engine) {
        if (this.certificateAlias == null) {
            for (final X509KeyManager m : this.keyManagers) {
                if (m instanceof X509ExtendedKeyManager) {
                    final X509ExtendedKeyManager em = (X509ExtendedKeyManager)m;
                    final String alias = em.chooseEngineClientAlias(keyType, issuers, engine);
                    if (alias != null) {
                        return alias;
                    }
                }
                else {
                    final String alias2 = m.chooseClientAlias(keyType, issuers, null);
                    if (alias2 != null) {
                        return alias2;
                    }
                }
            }
            return null;
        }
        for (final String s : keyType) {
            for (final X509KeyManager i : this.keyManagers) {
                final String[] aliases = i.getClientAliases(s, issuers);
                if (aliases != null) {
                    for (final String alias3 : aliases) {
                        if (alias3.equals(this.certificateAlias)) {
                            return this.certificateAlias;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public final synchronized String[] getServerAliases(final String keyType, final Principal[] issuers) {
        final LinkedHashSet<String> serverAliases = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(10));
        for (final X509KeyManager m : this.keyManagers) {
            final String[] aliases = m.getServerAliases(keyType, issuers);
            if (aliases != null) {
                serverAliases.addAll((Collection<?>)Arrays.asList(aliases));
            }
        }
        if (serverAliases.isEmpty()) {
            return null;
        }
        final String[] aliases2 = new String[serverAliases.size()];
        return serverAliases.toArray(aliases2);
    }
    
    @Override
    public final synchronized String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        if (this.certificateAlias == null) {
            for (final X509KeyManager m : this.keyManagers) {
                final String alias = m.chooseServerAlias(keyType, issuers, socket);
                if (alias != null) {
                    return alias;
                }
            }
            return null;
        }
        for (final X509KeyManager m : this.keyManagers) {
            final String[] aliases = m.getServerAliases(keyType, issuers);
            if (aliases != null) {
                for (final String alias2 : aliases) {
                    if (alias2.equals(this.certificateAlias)) {
                        return this.certificateAlias;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public final synchronized String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
        if (this.certificateAlias == null) {
            for (final X509KeyManager m : this.keyManagers) {
                if (m instanceof X509ExtendedKeyManager) {
                    final X509ExtendedKeyManager em = (X509ExtendedKeyManager)m;
                    final String alias = em.chooseEngineServerAlias(keyType, issuers, engine);
                    if (alias != null) {
                        return alias;
                    }
                }
                else {
                    final String alias2 = m.chooseServerAlias(keyType, issuers, null);
                    if (alias2 != null) {
                        return alias2;
                    }
                }
            }
            return null;
        }
        for (final X509KeyManager m : this.keyManagers) {
            final String[] aliases = m.getServerAliases(keyType, issuers);
            if (aliases != null) {
                for (final String alias3 : aliases) {
                    if (alias3.equals(this.certificateAlias)) {
                        return this.certificateAlias;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public final synchronized X509Certificate[] getCertificateChain(final String alias) {
        for (final X509KeyManager m : this.keyManagers) {
            final X509Certificate[] chain = m.getCertificateChain(alias);
            if (chain != null) {
                return chain;
            }
        }
        return null;
    }
    
    @Override
    public final synchronized PrivateKey getPrivateKey(final String alias) {
        for (final X509KeyManager m : this.keyManagers) {
            final PrivateKey key = m.getPrivateKey(alias);
            if (key != null) {
                return key;
            }
        }
        return null;
    }
}
