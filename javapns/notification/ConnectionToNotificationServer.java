package javapns.notification;

import java.security.KeyStore;
import javapns.communication.exceptions.KeystoreException;
import javapns.communication.AppleServer;
import javapns.communication.ConnectionToAppleServer;

public class ConnectionToNotificationServer extends ConnectionToAppleServer
{
    public ConnectionToNotificationServer(final AppleNotificationServer server) throws KeystoreException {
        super(server);
    }
    
    public ConnectionToNotificationServer(final AppleNotificationServer server, final KeyStore keystore) throws KeystoreException {
        super(server, keystore);
    }
    
    @Override
    public String getServerHost() {
        return ((AppleNotificationServer)this.getServer()).getNotificationServerHost();
    }
    
    public int getServerPort() {
        return ((AppleNotificationServer)this.getServer()).getNotificationServerPort();
    }
}
