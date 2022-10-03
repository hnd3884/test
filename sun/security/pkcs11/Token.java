package sun.security.pkcs11;

import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.ObjectStreamException;
import java.io.NotSerializableException;
import java.lang.ref.WeakReference;
import java.security.ProviderException;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import sun.security.pkcs11.wrapper.CK_SESSION_INFO;
import java.security.SecureRandom;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.jca.JCAUtil;
import java.util.concurrent.ConcurrentHashMap;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.lang.ref.Reference;
import java.util.List;
import sun.security.pkcs11.wrapper.CK_MECHANISM_INFO;
import java.util.Map;
import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import java.io.Serializable;

class Token implements Serializable
{
    private static final long serialVersionUID = 2541527649100571747L;
    private static final long CHECK_INTERVAL = 50L;
    final SunPKCS11 provider;
    final PKCS11 p11;
    final Config config;
    final CK_TOKEN_INFO tokenInfo;
    final SessionManager sessionManager;
    private final TemplateManager templateManager;
    final boolean explicitCancel;
    final KeyCache secretCache;
    final KeyCache privateCache;
    private volatile P11KeyFactory rsaFactory;
    private volatile P11KeyFactory dsaFactory;
    private volatile P11KeyFactory dhFactory;
    private volatile P11KeyFactory ecFactory;
    private final Map<Long, CK_MECHANISM_INFO> mechInfoMap;
    private volatile P11SecureRandom secureRandom;
    private volatile P11KeyStore keyStore;
    private final boolean removable;
    private volatile boolean valid;
    private long lastPresentCheck;
    private byte[] tokenId;
    private boolean writeProtected;
    private volatile boolean loggedIn;
    private long lastLoginCheck;
    private static final Object CHECK_LOCK;
    private static final CK_MECHANISM_INFO INVALID_MECH;
    private Boolean supportsRawSecretKeyImport;
    private static final List<Reference<Token>> serializedTokens;
    
    Token(final SunPKCS11 provider) throws PKCS11Exception {
        this.provider = provider;
        this.removable = provider.removable;
        this.valid = true;
        this.p11 = provider.p11;
        this.config = provider.config;
        this.tokenInfo = this.p11.C_GetTokenInfo(provider.slotID);
        this.writeProtected = ((this.tokenInfo.flags & 0x2L) != 0x0L);
        SessionManager sessionManager;
        try {
            sessionManager = new SessionManager(this);
            sessionManager.releaseSession(sessionManager.getOpSession());
        }
        catch (final PKCS11Exception ex) {
            if (this.writeProtected) {
                throw ex;
            }
            this.writeProtected = true;
            sessionManager = new SessionManager(this);
            sessionManager.releaseSession(sessionManager.getOpSession());
        }
        this.sessionManager = sessionManager;
        this.secretCache = new KeyCache();
        this.privateCache = new KeyCache();
        this.templateManager = this.config.getTemplateManager();
        this.explicitCancel = this.config.getExplicitCancel();
        this.mechInfoMap = new ConcurrentHashMap<Long, CK_MECHANISM_INFO>(10);
    }
    
    boolean isWriteProtected() {
        return this.writeProtected;
    }
    
    boolean supportsRawSecretKeyImport() {
        if (this.supportsRawSecretKeyImport == null) {
            final SecureRandom secureRandom = JCAUtil.getSecureRandom();
            final byte[] array = new byte[48];
            secureRandom.nextBytes(array);
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(0L, 4L), new CK_ATTRIBUTE(256L, 16L), new CK_ATTRIBUTE(17L, array) };
            Session objSession = null;
            try {
                final CK_ATTRIBUTE[] attributes = this.getAttributes("import", 4L, 16L, array2);
                objSession = this.getObjSession();
                this.p11.C_CreateObject(objSession.id(), attributes);
                this.supportsRawSecretKeyImport = Boolean.TRUE;
            }
            catch (final PKCS11Exception ex) {
                this.supportsRawSecretKeyImport = Boolean.FALSE;
            }
            finally {
                this.releaseSession(objSession);
            }
        }
        return this.supportsRawSecretKeyImport;
    }
    
    boolean isLoggedIn(final Session session) throws PKCS11Exception {
        boolean b = this.loggedIn;
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastLoginCheck > 50L) {
            b = this.isLoggedInNow(session);
            this.lastLoginCheck = currentTimeMillis;
        }
        return b;
    }
    
    boolean isLoggedInNow(Session opSession) throws PKCS11Exception {
        final boolean b = opSession == null;
        try {
            if (b) {
                opSession = this.getOpSession();
            }
            final CK_SESSION_INFO c_GetSessionInfo = this.p11.C_GetSessionInfo(opSession.id());
            return this.loggedIn = (c_GetSessionInfo.state == 1L || c_GetSessionInfo.state == 3L);
        }
        finally {
            if (b) {
                this.releaseSession(opSession);
            }
        }
    }
    
    void ensureLoggedIn(final Session session) throws PKCS11Exception, LoginException {
        if (!this.isLoggedIn(session)) {
            this.provider.login(null, null);
        }
    }
    
    boolean isValid() {
        return !this.removable || this.valid;
    }
    
    void ensureValid() {
        if (!this.isValid()) {
            throw new ProviderException("Token has been removed");
        }
    }
    
    boolean isPresent(final long n) {
        if (!this.removable) {
            return true;
        }
        if (!this.valid) {
            return false;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastPresentCheck >= 50L) {
            synchronized (Token.CHECK_LOCK) {
                if (currentTimeMillis - this.lastPresentCheck >= 50L) {
                    boolean valid = false;
                    try {
                        if ((this.provider.p11.C_GetSlotInfo(this.provider.slotID).flags & 0x1L) != 0x0L) {
                            this.provider.p11.C_GetSessionInfo(n);
                            valid = true;
                        }
                    }
                    catch (final PKCS11Exception ex) {}
                    this.valid = valid;
                    this.lastPresentCheck = System.currentTimeMillis();
                    if (!valid) {
                        this.destroy();
                    }
                }
            }
        }
        return this.valid;
    }
    
    void destroy() {
        this.valid = false;
        this.provider.uninitToken(this);
    }
    
    Session getObjSession() throws PKCS11Exception {
        return this.sessionManager.getObjSession();
    }
    
    Session getOpSession() throws PKCS11Exception {
        return this.sessionManager.getOpSession();
    }
    
    Session releaseSession(final Session session) {
        return this.sessionManager.releaseSession(session);
    }
    
    Session killSession(final Session session) {
        return this.sessionManager.killSession(session);
    }
    
    CK_ATTRIBUTE[] getAttributes(final String s, final long n, final long n2, final CK_ATTRIBUTE[] array) throws PKCS11Exception {
        final CK_ATTRIBUTE[] attributes;
        final CK_ATTRIBUTE[] array2 = attributes = this.templateManager.getAttributes(s, n, n2, array);
        for (final CK_ATTRIBUTE ck_ATTRIBUTE : attributes) {
            if (ck_ATTRIBUTE.type == 1L) {
                if (!ck_ATTRIBUTE.getBoolean()) {
                    break;
                }
                try {
                    this.ensureLoggedIn(null);
                    break;
                }
                catch (final LoginException ex) {
                    throw new ProviderException("Login failed", ex);
                }
            }
        }
        return array2;
    }
    
    P11KeyFactory getKeyFactory(final String s) {
        P11KeyFactory p11KeyFactory;
        if (s.equals("RSA")) {
            p11KeyFactory = this.rsaFactory;
            if (p11KeyFactory == null) {
                p11KeyFactory = new P11RSAKeyFactory(this, s);
                this.rsaFactory = p11KeyFactory;
            }
        }
        else if (s.equals("DSA")) {
            p11KeyFactory = this.dsaFactory;
            if (p11KeyFactory == null) {
                p11KeyFactory = new P11DSAKeyFactory(this, s);
                this.dsaFactory = p11KeyFactory;
            }
        }
        else if (s.equals("DH")) {
            p11KeyFactory = this.dhFactory;
            if (p11KeyFactory == null) {
                p11KeyFactory = new P11DHKeyFactory(this, s);
                this.dhFactory = p11KeyFactory;
            }
        }
        else {
            if (!s.equals("EC")) {
                throw new ProviderException("Unknown algorithm " + s);
            }
            p11KeyFactory = this.ecFactory;
            if (p11KeyFactory == null) {
                p11KeyFactory = new P11ECKeyFactory(this, s);
                this.ecFactory = p11KeyFactory;
            }
        }
        return p11KeyFactory;
    }
    
    P11SecureRandom getRandom() {
        if (this.secureRandom == null) {
            this.secureRandom = new P11SecureRandom(this);
        }
        return this.secureRandom;
    }
    
    P11KeyStore getKeyStore() {
        if (this.keyStore == null) {
            this.keyStore = new P11KeyStore(this);
        }
        return this.keyStore;
    }
    
    CK_MECHANISM_INFO getMechanismInfo(final long n) throws PKCS11Exception {
        CK_MECHANISM_INFO c_GetMechanismInfo = this.mechInfoMap.get(n);
        if (c_GetMechanismInfo == null) {
            try {
                c_GetMechanismInfo = this.p11.C_GetMechanismInfo(this.provider.slotID, n);
                this.mechInfoMap.put(n, c_GetMechanismInfo);
            }
            catch (final PKCS11Exception ex) {
                if (ex.getErrorCode() != 112L) {
                    throw ex;
                }
                this.mechInfoMap.put(n, Token.INVALID_MECH);
            }
        }
        else if (c_GetMechanismInfo == Token.INVALID_MECH) {
            c_GetMechanismInfo = null;
        }
        return c_GetMechanismInfo;
    }
    
    private synchronized byte[] getTokenId() {
        if (this.tokenId == null) {
            JCAUtil.getSecureRandom().nextBytes(this.tokenId = new byte[20]);
            Token.serializedTokens.add(new WeakReference<Token>(this));
        }
        return this.tokenId;
    }
    
    private Object writeReplace() throws ObjectStreamException {
        if (!this.isValid()) {
            throw new NotSerializableException("Token has been removed");
        }
        return new TokenRep(this);
    }
    
    static {
        CHECK_LOCK = new Object();
        INVALID_MECH = new CK_MECHANISM_INFO(0L, 0L, 0L);
        serializedTokens = new ArrayList<Reference<Token>>();
    }
    
    private static class TokenRep implements Serializable
    {
        private static final long serialVersionUID = 3503721168218219807L;
        private final byte[] tokenId;
        
        TokenRep(final Token token) {
            this.tokenId = token.getTokenId();
        }
        
        private Object readResolve() throws ObjectStreamException {
            final Iterator iterator = Token.serializedTokens.iterator();
            while (iterator.hasNext()) {
                final Token token = ((Reference<Token>)iterator.next()).get();
                if (token != null && token.isValid() && Arrays.equals(token.getTokenId(), this.tokenId)) {
                    return token;
                }
            }
            throw new NotSerializableException("Could not find token");
        }
    }
}
