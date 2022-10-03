package javapns.notification;

import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.net.Socket;
import java.io.ByteArrayOutputStream;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.exceptions.NullIdException;
import javapns.devices.exceptions.UnknownDeviceException;
import java.io.IOException;
import javapns.communication.exceptions.InvalidCertificateChainException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.exceptions.PayloadIsEmptyException;
import javapns.devices.Device;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import javax.security.cert.X509Certificate;
import java.security.cert.Certificate;
import javax.net.ssl.SSLSession;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import java.nio.ByteBuffer;
import javapns.devices.implementations.basic.BasicDeviceFactory;
import java.util.LinkedHashMap;
import javapns.devices.DeviceFactory;
import javax.net.ssl.SSLSocket;
import javapns.communication.ConnectionToAppleServer;
import org.slf4j.Logger;

public class PushNotificationManager
{
    private static final Logger logger;
    private static final int DEFAULT_RETRIES = 3;
    private static final int SEQUENTIAL_IDENTIFIER = -1;
    private static int TESTS_SERIAL_NUMBER;
    private static boolean useEnhancedNotificationFormat;
    private static boolean heavyDebugMode;
    private int sslSocketTimeout;
    private ConnectionToAppleServer connectionToAppleServer;
    private SSLSocket socket;
    private int retryAttempts;
    private int nextMessageIdentifier;
    private boolean trustAllServerCertificates;
    @Deprecated
    private DeviceFactory deviceFactory;
    private final LinkedHashMap<Integer, PushedNotification> pushedNotifications;
    
    public PushNotificationManager() {
        this.sslSocketTimeout = 30000;
        this.retryAttempts = 3;
        this.nextMessageIdentifier = 1;
        this.trustAllServerCertificates = true;
        this.pushedNotifications = new LinkedHashMap<Integer, PushedNotification>();
        this.deviceFactory = new BasicDeviceFactory();
    }
    
    @Deprecated
    private PushNotificationManager(final DeviceFactory deviceManager) {
        this.sslSocketTimeout = 30000;
        this.retryAttempts = 3;
        this.nextMessageIdentifier = 1;
        this.trustAllServerCertificates = true;
        this.pushedNotifications = new LinkedHashMap<Integer, PushedNotification>();
        this.deviceFactory = deviceManager;
    }
    
    private static byte[] intTo4ByteArray(final int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }
    
    private static byte[] intTo2ByteArray(final int value) {
        final int s1 = (value & 0xFF00) >> 8;
        final int s2 = value & 0xFF;
        return new byte[] { (byte)s1, (byte)s2 };
    }
    
    protected static boolean isEnhancedNotificationFormatEnabled() {
        return PushNotificationManager.useEnhancedNotificationFormat;
    }
    
    public static void setEnhancedNotificationFormatEnabled(final boolean enabled) {
        PushNotificationManager.useEnhancedNotificationFormat = enabled;
    }
    
    public static void setHeavyDebugMode(final boolean enabled) {
        PushNotificationManager.heavyDebugMode = enabled;
    }
    
    public void initializeConnection(final AppleNotificationServer server) throws CommunicationException, KeystoreException {
        try {
            this.connectionToAppleServer = new ConnectionToNotificationServer(server);
            this.socket = this.connectionToAppleServer.getSSLSocket();
            if (PushNotificationManager.heavyDebugMode) {
                this.dumpCertificateChainDescription();
            }
            PushNotificationManager.logger.debug("Initialized Connection to Host: [" + server.getNotificationServerHost() + "] Port: [" + server.getNotificationServerPort() + "]: " + this.socket);
        }
        catch (final KeystoreException | CommunicationException e) {
            throw e;
        }
        catch (final Exception e) {
            throw new CommunicationException("Error creating connection with Apple server", e);
        }
    }
    
    private void dumpCertificateChainDescription() {
        try {
            final File file = new File("apns-certificatechain.txt");
            final FileOutputStream outf = new FileOutputStream(file);
            final DataOutputStream outd = new DataOutputStream(outf);
            outd.writeBytes(this.getCertificateChainDescription());
            outd.close();
        }
        catch (final Exception ex) {}
    }
    
    private String getCertificateChainDescription() {
        final StringBuilder buf = new StringBuilder();
        try {
            final SSLSession session = this.socket.getSession();
            for (final Certificate certificate : session.getLocalCertificates()) {
                buf.append(certificate.toString());
            }
            buf.append("\n--------------------------------------------------------------------------\n");
            for (final X509Certificate certificate2 : session.getPeerCertificateChain()) {
                buf.append(certificate2.toString());
            }
        }
        catch (final Exception e) {
            buf.append(e);
        }
        return buf.toString();
    }
    
    private void initializePreviousConnection() throws CommunicationException, KeystoreException {
        this.initializeConnection((AppleNotificationServer)this.connectionToAppleServer.getServer());
    }
    
    public void restartConnection(final AppleNotificationServer server) throws CommunicationException, KeystoreException {
        this.stopConnection();
        this.initializeConnection(server);
    }
    
    private void restartPreviousConnection() throws CommunicationException, KeystoreException {
        try {
            PushNotificationManager.logger.debug("Closing connection to restart previous one");
            this.socket.close();
        }
        catch (final Exception ex) {}
        this.initializePreviousConnection();
    }
    
    public void stopConnection() throws CommunicationException, KeystoreException {
        this.processedFailedNotifications();
        try {
            PushNotificationManager.logger.debug("Closing connection");
            this.socket.close();
        }
        catch (final Exception ex) {}
    }
    
    private int processedFailedNotifications() throws CommunicationException, KeystoreException {
        if (PushNotificationManager.useEnhancedNotificationFormat) {
            PushNotificationManager.logger.debug("Reading responses");
            int responsesReceived = ResponsePacketReader.processResponses(this);
            while (responsesReceived > 0) {
                final PushedNotification skippedNotification = null;
                final List<PushedNotification> notificationsToResend = new ArrayList<PushedNotification>();
                boolean foundFirstFail = false;
                for (final PushedNotification notification : this.pushedNotifications.values()) {
                    if (foundFirstFail || !notification.isSuccessful()) {
                        if (foundFirstFail) {
                            notificationsToResend.add(notification);
                        }
                        else {
                            foundFirstFail = true;
                        }
                    }
                }
                this.pushedNotifications.clear();
                final int toResend = notificationsToResend.size();
                PushNotificationManager.logger.debug("Found " + toResend + " notifications that must be re-sent");
                if (toResend > 0) {
                    PushNotificationManager.logger.debug("Restarting connection to resend notifications");
                    this.restartPreviousConnection();
                    for (final PushedNotification pushedNotification : notificationsToResend) {
                        this.sendNotification(pushedNotification, false);
                    }
                }
                final int remaining;
                responsesReceived = (remaining = ResponsePacketReader.processResponses(this));
                if (remaining == 0) {
                    PushNotificationManager.logger.debug("No notifications remaining to be resent");
                    return 0;
                }
            }
            return responsesReceived;
        }
        PushNotificationManager.logger.debug("Not reading responses because using simple notification format");
        return 0;
    }
    
    public PushedNotification sendNotification(final Device device, final Payload payload) throws CommunicationException {
        return this.sendNotification(device, payload, true);
    }
    
    public PushedNotifications sendNotifications(final Payload payload, final List<Device> devices) throws CommunicationException, KeystoreException {
        final PushedNotifications notifications = new PushedNotifications();
        for (final Device device : devices) {
            notifications.add(this.sendNotification(device, payload, false, -1));
        }
        this.stopConnection();
        return notifications;
    }
    
    public PushedNotifications sendNotifications(final Payload payload, final Device... devices) throws CommunicationException, KeystoreException {
        final PushedNotifications notifications = new PushedNotifications();
        for (final Device device : devices) {
            notifications.add(this.sendNotification(device, payload, false, -1));
        }
        this.stopConnection();
        return notifications;
    }
    
    public PushedNotification sendNotification(final Device device, final Payload payload, final boolean closeAfter) throws CommunicationException {
        return this.sendNotification(device, payload, closeAfter, -1);
    }
    
    public PushedNotification sendNotification(final Device device, final Payload payload, final int identifier) throws CommunicationException {
        return this.sendNotification(device, payload, false, identifier);
    }
    
    public PushedNotification sendNotification(final Device device, final Payload payload, final boolean closeAfter, final int identifier) throws CommunicationException {
        final PushedNotification pushedNotification = new PushedNotification(device, payload, identifier);
        this.sendNotification(pushedNotification, closeAfter);
        return pushedNotification;
    }
    
    private void sendNotification(final PushedNotification notification, final boolean closeAfter) throws CommunicationException {
        try {
            final Device device = notification.getDevice();
            final Payload payload = notification.getPayload();
            try {
                payload.verifyPayloadIsNotEmpty();
            }
            catch (final IllegalArgumentException e) {
                throw new PayloadIsEmptyException();
            }
            catch (final Exception ex2) {}
            if (notification.getIdentifier() <= 0) {
                notification.setIdentifier(this.newMessageIdentifier());
            }
            if (!this.pushedNotifications.containsKey(notification.getIdentifier())) {
                this.pushedNotifications.put(notification.getIdentifier(), notification);
            }
            final int identifier = notification.getIdentifier();
            final String token = device.getToken();
            BasicDevice.validateTokenFormat(token);
            final byte[] bytes = this.getMessage(token, payload, identifier, notification);
            final boolean simulationMode = payload.getExpiry() == 919191;
            boolean success = false;
            final BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            final int socketTimeout = this.getSslSocketTimeout();
            if (socketTimeout > 0) {
                this.socket.setSoTimeout(socketTimeout);
            }
            notification.setTransmissionAttempts(0);
            while (!success) {
                try {
                    PushNotificationManager.logger.debug("Attempting to send notification: " + payload.toString() + "");
                    PushNotificationManager.logger.debug("  to device: " + token + "");
                    notification.addTransmissionAttempt();
                    boolean streamConfirmed = false;
                    try {
                        if (!simulationMode) {
                            this.socket.getOutputStream().write(bytes);
                            streamConfirmed = true;
                        }
                        else {
                            PushNotificationManager.logger.debug("* Simulation only: would have streamed " + bytes.length + "-bytes message now..");
                        }
                    }
                    catch (final Exception e2) {
                        if (e2.toString().contains("certificate_unknown")) {
                            throw new InvalidCertificateChainException(e2.getMessage());
                        }
                        throw e2;
                    }
                    PushNotificationManager.logger.debug("Flushing");
                    this.socket.getOutputStream().flush();
                    if (streamConfirmed) {
                        PushNotificationManager.logger.debug("At this point, the entire " + bytes.length + "-bytes message has been streamed out successfully through the SSL connection");
                    }
                    success = true;
                    PushNotificationManager.logger.debug("Notification sent on " + notification.getLatestTransmissionAttempt());
                    notification.setTransmissionCompleted(true);
                }
                catch (final IOException e3) {
                    if (notification.getTransmissionAttempts() >= this.retryAttempts) {
                        PushNotificationManager.logger.error("Attempt to send Notification failed and beyond the maximum number of attempts permitted");
                        notification.setTransmissionCompleted(false);
                        notification.setException(e3);
                        PushNotificationManager.logger.error("Delivery error", (Throwable)e3);
                        throw e3;
                    }
                    PushNotificationManager.logger.info("Attempt failed (" + e3.getMessage() + ")... trying again");
                    try {
                        this.socket.close();
                    }
                    catch (final Exception ex3) {}
                    this.socket = this.connectionToAppleServer.getSSLSocket();
                    if (socketTimeout <= 0) {
                        continue;
                    }
                    this.socket.setSoTimeout(socketTimeout);
                }
            }
        }
        catch (final CommunicationException e4) {
            throw e4;
        }
        catch (final Exception ex) {
            notification.setException(ex);
            PushNotificationManager.logger.error("Delivery error: " + ex);
            try {
                if (closeAfter) {
                    PushNotificationManager.logger.error("Closing connection after error");
                    this.stopConnection();
                }
            }
            catch (final Exception ex4) {}
        }
    }
    
    @Deprecated
    public void addDevice(final String id, final String token) throws Exception {
        PushNotificationManager.logger.debug("Adding Token [" + token + "] to Device [" + id + "]");
        this.deviceFactory.addDevice(id, token);
    }
    
    @Deprecated
    public Device getDevice(final String id) throws UnknownDeviceException, NullIdException {
        PushNotificationManager.logger.debug("Getting Token from Device [" + id + "]");
        return this.deviceFactory.getDevice(id);
    }
    
    @Deprecated
    public void removeDevice(final String id) throws UnknownDeviceException, NullIdException {
        PushNotificationManager.logger.debug("Removing Token from Device [" + id + "]");
        this.deviceFactory.removeDevice(id);
    }
    
    private byte[] getMessage(String deviceToken, final Payload payload, final int identifier, final PushedNotification message) throws IOException, Exception {
        PushNotificationManager.logger.debug("Building Raw message from deviceToken and payload");
        final byte[] deviceTokenAsBytes = new byte[deviceToken.length() / 2];
        deviceToken = deviceToken.toUpperCase();
        int j = 0;
        try {
            for (int i = 0; i < deviceToken.length(); i += 2) {
                final String t = deviceToken.substring(i, i + 2);
                final int tmp = Integer.parseInt(t, 16);
                deviceTokenAsBytes[j++] = (byte)tmp;
            }
        }
        catch (final NumberFormatException e1) {
            throw new InvalidDeviceTokenFormatException(deviceToken, e1.getMessage());
        }
        this.preconfigurePayload(payload, identifier, deviceToken);
        final byte[] payloadAsBytes = payload.getPayloadAsBytes();
        final int size = 3 + deviceTokenAsBytes.length + 2 + payloadAsBytes.length;
        final ByteArrayOutputStream bao = new ByteArrayOutputStream(size);
        if (PushNotificationManager.useEnhancedNotificationFormat) {
            final byte b = 1;
            bao.write(1);
        }
        else {
            final byte b = 0;
            bao.write(0);
        }
        if (PushNotificationManager.useEnhancedNotificationFormat) {
            bao.write(intTo4ByteArray(identifier));
            message.setIdentifier(identifier);
            final int requestedExpiry = payload.getExpiry();
            if (requestedExpiry <= 0) {
                bao.write(intTo4ByteArray(requestedExpiry));
                message.setExpiry(0L);
            }
            else {
                final long ctime = System.currentTimeMillis();
                final long ttl = requestedExpiry * 1000;
                final Long expiryDateInSeconds = (ctime + ttl) / 1000L;
                bao.write(intTo4ByteArray(expiryDateInSeconds.intValue()));
                message.setExpiry(ctime + ttl);
            }
        }
        final int tl = deviceTokenAsBytes.length;
        bao.write(intTo2ByteArray(tl));
        bao.write(deviceTokenAsBytes);
        final int pl = payloadAsBytes.length;
        bao.write(intTo2ByteArray(pl));
        bao.write(payloadAsBytes);
        bao.flush();
        final byte[] bytes = bao.toByteArray();
        if (PushNotificationManager.heavyDebugMode) {
            try {
                final FileOutputStream outf = new FileOutputStream("apns-message.bytes");
                outf.write(bytes);
                outf.close();
            }
            catch (final Exception ex) {}
        }
        PushNotificationManager.logger.debug("Built raw message ID " + identifier + " of total length " + bytes.length);
        return bytes;
    }
    
    public int getRetryAttempts() {
        return this.retryAttempts;
    }
    
    public void setRetryAttempts(final int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }
    
    @Deprecated
    public DeviceFactory getDeviceFactory() {
        return this.deviceFactory;
    }
    
    @Deprecated
    public void setDeviceFactory(final DeviceFactory deviceFactory) {
        this.deviceFactory = deviceFactory;
    }
    
    private int getSslSocketTimeout() {
        return this.sslSocketTimeout;
    }
    
    public void setSslSocketTimeout(final int sslSocketTimeout) {
        this.sslSocketTimeout = sslSocketTimeout;
    }
    
    protected boolean isTrustAllServerCertificates() {
        return this.trustAllServerCertificates;
    }
    
    public void setTrustAllServerCertificates(final boolean trustAllServerCertificates) {
        this.trustAllServerCertificates = trustAllServerCertificates;
    }
    
    private int newMessageIdentifier() {
        final int id = this.nextMessageIdentifier;
        ++this.nextMessageIdentifier;
        return id;
    }
    
    Socket getActiveSocket() {
        return this.socket;
    }
    
    Map<Integer, PushedNotification> getPushedNotifications() {
        return this.pushedNotifications;
    }
    
    private void preconfigurePayload(final Payload payload, final int identifier, final String deviceToken) {
        try {
            final int config = payload.getPreSendConfiguration();
            if (payload instanceof PushNotificationPayload) {
                final PushNotificationPayload pnpayload = (PushNotificationPayload)payload;
                if (config == 1) {
                    pnpayload.getPayload().remove("alert");
                    pnpayload.addAlert(this.buildDebugAlert(payload, identifier, deviceToken));
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    private String buildDebugAlert(final Payload payload, final int identifier, final String deviceToken) {
        final StringBuilder alert = new StringBuilder();
        alert.append("JAVAPNS DEBUG ALERT ").append(PushNotificationManager.TESTS_SERIAL_NUMBER++).append("\n");
        alert.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(System.currentTimeMillis())).append("\n");
        alert.append(this.connectionToAppleServer.getServerHost()).append("\n");
        final int l = PushNotificationManager.useEnhancedNotificationFormat ? 4 : 8;
        alert.append("").append(deviceToken.substring(0, l)).append("\ufffd").append(deviceToken.substring(64 - l, 64)).append(PushNotificationManager.useEnhancedNotificationFormat ? (" [Id:" + identifier + "] " + ((payload.getExpiry() <= 0) ? "No-store" : ("Exp:T+" + payload.getExpiry()))) : "").append("\n");
        alert.append(PushNotificationManager.useEnhancedNotificationFormat ? "Enhanced" : "Simple").append(" format / ").append(payload.getCharacterEncoding()).append("").append("");
        return alert.toString();
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)PushNotificationManager.class);
        PushNotificationManager.TESTS_SERIAL_NUMBER = 1;
        PushNotificationManager.useEnhancedNotificationFormat = true;
        PushNotificationManager.heavyDebugMode = false;
    }
}
