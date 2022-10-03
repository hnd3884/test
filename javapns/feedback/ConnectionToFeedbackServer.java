package javapns.feedback;

import java.security.KeyStore;
import javapns.notification.AppleNotificationServer;
import javapns.communication.exceptions.KeystoreException;
import javapns.communication.AppleServer;
import javapns.communication.ConnectionToAppleServer;

public class ConnectionToFeedbackServer extends ConnectionToAppleServer
{
    public ConnectionToFeedbackServer(final AppleFeedbackServer feedbackServer) throws KeystoreException {
        super(feedbackServer);
    }
    
    public ConnectionToFeedbackServer(final AppleNotificationServer server, final KeyStore keystore) throws KeystoreException {
        super(server, keystore);
    }
    
    @Override
    public String getServerHost() {
        return ((AppleFeedbackServer)this.getServer()).getFeedbackServerHost();
    }
    
    public int getServerPort() {
        return ((AppleFeedbackServer)this.getServer()).getFeedbackServerPort();
    }
}
