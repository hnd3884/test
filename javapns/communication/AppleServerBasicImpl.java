package javapns.communication;

import javapns.communication.exceptions.InvalidKeystoreReferenceException;
import java.io.InputStream;
import javapns.communication.exceptions.KeystoreException;

public abstract class AppleServerBasicImpl implements AppleServer
{
    private final String password;
    private final String type;
    private Object keystore;
    private String proxyHost;
    private int proxyPort;
    private String proxyAuthorization;
    
    protected AppleServerBasicImpl(final Object keystore, final String password, final String type) throws KeystoreException {
        KeystoreManager.validateKeystoreParameter(keystore);
        this.keystore = keystore;
        this.password = password;
        this.type = type;
        this.keystore = KeystoreManager.ensureReusableKeystore(this, this.keystore);
    }
    
    @Override
    public InputStream getKeystoreStream() throws InvalidKeystoreReferenceException {
        return KeystoreManager.streamKeystore(this.keystore);
    }
    
    @Override
    public String getKeystorePassword() {
        return this.password;
    }
    
    @Override
    public String getKeystoreType() {
        return this.type;
    }
    
    @Override
    public String getProxyHost() {
        return this.proxyHost;
    }
    
    @Override
    public int getProxyPort() {
        return this.proxyPort;
    }
    
    @Override
    public void setProxy(final String proxyHost, final int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }
    
    @Override
    public void setProxyAuthorization(final String proxyAuthorization) {
        this.proxyAuthorization = proxyAuthorization;
    }
    
    @Override
    public String getProxyAuthorization() {
        return this.proxyAuthorization;
    }
}
