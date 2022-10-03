package io.netty.handler.ssl;

import java.util.HashMap;
import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import javax.net.ssl.SSLEngine;
import java.security.Principal;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.security.auth.x500.X500Principal;
import javax.net.ssl.SSLException;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import javax.net.ssl.SSLHandshakeException;
import java.util.Map;

final class OpenSslKeyMaterialManager
{
    static final String KEY_TYPE_RSA = "RSA";
    static final String KEY_TYPE_DH_RSA = "DH_RSA";
    static final String KEY_TYPE_EC = "EC";
    static final String KEY_TYPE_EC_EC = "EC_EC";
    static final String KEY_TYPE_EC_RSA = "EC_RSA";
    private static final Map<String, String> KEY_TYPES;
    private final OpenSslKeyMaterialProvider provider;
    
    OpenSslKeyMaterialManager(final OpenSslKeyMaterialProvider provider) {
        this.provider = provider;
    }
    
    void setKeyMaterialServerSide(final ReferenceCountedOpenSslEngine engine) throws SSLException {
        final String[] authMethods = engine.authMethods();
        if (authMethods.length == 0) {
            throw new SSLHandshakeException("Unable to find key material");
        }
        final Set<String> typeSet = new HashSet<String>(OpenSslKeyMaterialManager.KEY_TYPES.size());
        for (final String authMethod : authMethods) {
            final String type = OpenSslKeyMaterialManager.KEY_TYPES.get(authMethod);
            if (type != null && typeSet.add(type)) {
                final String alias = this.chooseServerAlias(engine, type);
                if (alias != null) {
                    this.setKeyMaterial(engine, alias);
                    return;
                }
            }
        }
        throw new SSLHandshakeException("Unable to find key material for auth method(s): " + Arrays.toString(authMethods));
    }
    
    void setKeyMaterialClientSide(final ReferenceCountedOpenSslEngine engine, final String[] keyTypes, final X500Principal[] issuer) throws SSLException {
        final String alias = this.chooseClientAlias(engine, keyTypes, issuer);
        if (alias != null) {
            this.setKeyMaterial(engine, alias);
        }
    }
    
    private void setKeyMaterial(final ReferenceCountedOpenSslEngine engine, final String alias) throws SSLException {
        OpenSslKeyMaterial keyMaterial = null;
        try {
            keyMaterial = this.provider.chooseKeyMaterial(engine.alloc, alias);
            if (keyMaterial == null) {
                return;
            }
            engine.setKeyMaterial(keyMaterial);
        }
        catch (final SSLException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new SSLException(e2);
        }
        finally {
            if (keyMaterial != null) {
                keyMaterial.release();
            }
        }
    }
    
    private String chooseClientAlias(final ReferenceCountedOpenSslEngine engine, final String[] keyTypes, final X500Principal[] issuer) {
        final X509KeyManager manager = this.provider.keyManager();
        if (manager instanceof X509ExtendedKeyManager) {
            return ((X509ExtendedKeyManager)manager).chooseEngineClientAlias(keyTypes, issuer, engine);
        }
        return manager.chooseClientAlias(keyTypes, issuer, null);
    }
    
    private String chooseServerAlias(final ReferenceCountedOpenSslEngine engine, final String type) {
        final X509KeyManager manager = this.provider.keyManager();
        if (manager instanceof X509ExtendedKeyManager) {
            return ((X509ExtendedKeyManager)manager).chooseEngineServerAlias(type, null, engine);
        }
        return manager.chooseServerAlias(type, null, null);
    }
    
    static {
        (KEY_TYPES = new HashMap<String, String>()).put("RSA", "RSA");
        OpenSslKeyMaterialManager.KEY_TYPES.put("DHE_RSA", "RSA");
        OpenSslKeyMaterialManager.KEY_TYPES.put("ECDHE_RSA", "RSA");
        OpenSslKeyMaterialManager.KEY_TYPES.put("ECDHE_ECDSA", "EC");
        OpenSslKeyMaterialManager.KEY_TYPES.put("ECDH_RSA", "EC_RSA");
        OpenSslKeyMaterialManager.KEY_TYPES.put("ECDH_ECDSA", "EC_EC");
        OpenSslKeyMaterialManager.KEY_TYPES.put("DH_RSA", "DH_RSA");
    }
}
