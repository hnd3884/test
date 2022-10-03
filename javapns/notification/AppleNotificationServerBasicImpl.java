package javapns.notification;

import javapns.communication.exceptions.KeystoreException;
import javapns.communication.AppleServerBasicImpl;

public class AppleNotificationServerBasicImpl extends AppleServerBasicImpl implements AppleNotificationServer
{
    private final String host;
    private final int port;
    
    public AppleNotificationServerBasicImpl(final Object keystore, final String password, final boolean production) throws KeystoreException {
        this(keystore, password, "PKCS12", production);
    }
    
    private AppleNotificationServerBasicImpl(final Object keystore, final String password, final String type, final boolean production) throws KeystoreException {
        this(keystore, password, type, production ? AppleNotificationServerBasicImpl.PRODUCTION_HOST : AppleNotificationServerBasicImpl.DEVELOPMENT_HOST, production ? 2195 : 2195);
    }
    
    private AppleNotificationServerBasicImpl(final Object keystore, final String password, final String type, final String host, final int port) throws KeystoreException {
        super(keystore, password, type);
        this.host = host;
        this.port = port;
    }
    
    @Override
    public String getNotificationServerHost() {
        return this.host;
    }
    
    @Override
    public int getNotificationServerPort() {
        return this.port;
    }
}
