package javapns.notification;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;
import java.util.Vector;
import java.net.Socket;
import java.util.List;

class ResponsePacketReader
{
    private static final int TIMEOUT = 5000;
    
    public static int processResponses(final PushNotificationManager notificationManager) {
        final List<ResponsePacket> responses = readResponses(notificationManager.getActiveSocket());
        handleResponses(responses, notificationManager);
        return responses.size();
    }
    
    private static List<ResponsePacket> readResponses(final Socket socket) {
        final List<ResponsePacket> responses = new Vector<ResponsePacket>();
        int previousTimeout = 0;
        try {
            try {
                previousTimeout = socket.getSoTimeout();
                socket.setSoTimeout(5000);
            }
            catch (final Exception ex) {}
            final InputStream input = socket.getInputStream();
            while (true) {
                final ResponsePacket packet = readResponsePacketData(input);
                if (packet == null) {
                    break;
                }
                responses.add(packet);
            }
        }
        catch (final Exception ex2) {}
        try {
            socket.setSoTimeout(previousTimeout);
        }
        catch (final Exception ex3) {}
        return responses;
    }
    
    private static void handleResponses(final List<ResponsePacket> responses, final PushNotificationManager notificationManager) {
        final Map<Integer, PushedNotification> envelopes = notificationManager.getPushedNotifications();
        for (final ResponsePacket response : responses) {
            response.linkToPushedNotification(notificationManager);
        }
    }
    
    private static ResponsePacket readResponsePacketData(final InputStream input) throws IOException {
        final int command = input.read();
        if (command < 0) {
            return null;
        }
        final int status = input.read();
        if (status < 0) {
            return null;
        }
        final int identifier_byte1 = input.read();
        if (identifier_byte1 < 0) {
            return null;
        }
        final int identifier_byte2 = input.read();
        if (identifier_byte2 < 0) {
            return null;
        }
        final int identifier_byte3 = input.read();
        if (identifier_byte3 < 0) {
            return null;
        }
        final int identifier_byte4 = input.read();
        if (identifier_byte4 < 0) {
            return null;
        }
        final int identifier = (identifier_byte1 << 24) + (identifier_byte2 << 16) + (identifier_byte3 << 8) + identifier_byte4;
        return new ResponsePacket(command, status, identifier);
    }
}
