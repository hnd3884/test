package javapns.feedback;

import org.slf4j.LoggerFactory;
import java.io.InputStream;
import javapns.devices.implementations.basic.BasicDevice;
import java.sql.Timestamp;
import java.io.ByteArrayOutputStream;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javax.net.ssl.SSLSocket;
import javapns.devices.Device;
import java.util.LinkedList;
import javapns.devices.implementations.basic.BasicDeviceFactory;
import javapns.devices.DeviceFactory;
import org.slf4j.Logger;

public class FeedbackServiceManager
{
    private static final Logger logger;
    private static final int FEEDBACK_TUPLE_SIZE = 38;
    @Deprecated
    private DeviceFactory deviceFactory;
    
    @Deprecated
    private FeedbackServiceManager(final DeviceFactory deviceFactory) {
        this.setDeviceFactory(deviceFactory);
    }
    
    public FeedbackServiceManager() {
        this.setDeviceFactory(new BasicDeviceFactory());
    }
    
    public LinkedList<Device> getDevices(final AppleFeedbackServer server) throws KeystoreException, CommunicationException {
        final ConnectionToFeedbackServer connectionHelper = new ConnectionToFeedbackServer(server);
        final SSLSocket socket = connectionHelper.getSSLSocket();
        return this.getDevices(socket);
    }
    
    private LinkedList<Device> getDevices(final SSLSocket socket) throws CommunicationException {
        LinkedList<Device> listDev = null;
        try {
            final InputStream socketStream = socket.getInputStream();
            final byte[] b = new byte[1024];
            final ByteArrayOutputStream message = new ByteArrayOutputStream();
            int nbBytes;
            while ((nbBytes = socketStream.read(b, 0, 1024)) != -1) {
                message.write(b, 0, nbBytes);
            }
            listDev = new LinkedList<Device>();
            final byte[] listOfDevices = message.toByteArray();
            final int nbTuples = listOfDevices.length / 38;
            FeedbackServiceManager.logger.debug("Found: [" + nbTuples + "]");
            for (int i = 0; i < nbTuples; ++i) {
                final int offset = i * 38;
                final int firstByte = 0xFF & listOfDevices[offset];
                final int secondByte = 0xFF & listOfDevices[offset + 1];
                final int thirdByte = 0xFF & listOfDevices[offset + 2];
                final int fourthByte = 0xFF & listOfDevices[offset + 3];
                final long anUnsignedInt = (long)(firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte) & 0xFFFFFFFFL;
                final Timestamp timestamp = new Timestamp(anUnsignedInt * 1000L);
                final int deviceTokenLength = listOfDevices[offset + 4] << 8 | listOfDevices[offset + 5];
                String deviceToken = "";
                for (int j = 0; j < 32; ++j) {
                    final int octet = 0xFF & listOfDevices[offset + 6 + j];
                    deviceToken = deviceToken.concat(String.format("%02x", octet));
                }
                final Device device = new BasicDevice();
                device.setToken(deviceToken);
                device.setLastRegister(timestamp);
                listDev.add(device);
                FeedbackServiceManager.logger.info("FeedbackManager retrieves one device :  " + timestamp + ";" + deviceTokenLength + ";" + deviceToken + ".");
            }
        }
        catch (final Exception e) {
            FeedbackServiceManager.logger.debug("Caught exception fetching devices from Feedback Service");
            throw new CommunicationException("Problem communicating with Feedback service", e);
        }
        finally {
            try {
                socket.close();
            }
            catch (final Exception ex) {}
        }
        return listDev;
    }
    
    @Deprecated
    public DeviceFactory getDeviceFactory() {
        return this.deviceFactory;
    }
    
    @Deprecated
    private void setDeviceFactory(final DeviceFactory deviceFactory) {
        this.deviceFactory = deviceFactory;
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)FeedbackServiceManager.class);
    }
}
