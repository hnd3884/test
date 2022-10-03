package javapns.feedback;

import javapns.communication.exceptions.KeystoreException;
import javapns.communication.AppleServerBasicImpl;

public class AppleFeedbackServerBasicImpl extends AppleServerBasicImpl implements AppleFeedbackServer
{
    private final String host;
    private final int port;
    
    public AppleFeedbackServerBasicImpl(final Object keystore, final String password, final boolean production) throws KeystoreException {
        this(keystore, password, "PKCS12", production);
    }
    
    private AppleFeedbackServerBasicImpl(final Object keystore, final String password, final String type, final boolean production) throws KeystoreException {
        this(keystore, password, type, production ? AppleFeedbackServerBasicImpl.PRODUCTION_HOST : AppleFeedbackServerBasicImpl.DEVELOPMENT_HOST, production ? 2196 : 2196);
    }
    
    private AppleFeedbackServerBasicImpl(final Object keystore, final String password, final String type, final String host, final int port) throws KeystoreException {
        super(keystore, password, type);
        this.host = host;
        this.port = port;
    }
    
    @Override
    public String getFeedbackServerHost() {
        return this.host;
    }
    
    @Override
    public int getFeedbackServerPort() {
        return this.port;
    }
}
