package com.zoho.mickey.server;

import com.zoho.net.handshake.HandShakePacket;
import com.adventnet.mfw.ServerInterface;
import com.zoho.net.handshake.HandShakeServerMessageHandler;

public class InformationDumpListener implements HandShakeServerMessageHandler
{
    ServerInterface server;
    
    public InformationDumpListener(final ServerInterface server) {
        this.server = server;
    }
    
    public String getResponseMessage(final HandShakePacket packetFromClient) {
        final String message = packetFromClient.getMessage();
        if (message.startsWith("InfoDump")) {
            return "INFO_DUMP " + message.substring(message.indexOf(" "), message.length()).trim();
        }
        return "UNKNOWN_PING_MESSAGE";
    }
    
    public void responsePacketSent(final HandShakePacket packetSentToClient) {
        String msg = packetSentToClient.getMessage();
        if (msg.startsWith("INFO_DUMP ")) {
            try {
                final String className = System.getProperty("InfoDumpClass", "com.zoho.mickey.server.DefaultServerInfoDump");
                final ServerInfoDump serverInfoDump = (ServerInfoDump)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
                msg = msg.replace("INFO_DUMP ", "");
                String[] options;
                if (msg.isEmpty()) {
                    options = new String[0];
                }
                else {
                    options = msg.split(" ");
                }
                serverInfoDump.repeatDump(options);
            }
            catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
