package com.zoho.mickey.server;

import com.zoho.net.handshake.HandShakePacket;
import com.zoho.net.handshake.HandShakeClient;
import com.adventnet.mfw.ConsoleOut;
import java.util.logging.Logger;
import com.zoho.net.handshake.HandShakeUtil;
import com.adventnet.mfw.logging.LoggerUtil;

public class InformationDumpRequest
{
    public static void threadDump(final String host, final String message) {
        try {
            LoggerUtil.initLog("infodump");
            final HandShakeClient handShakeClient = HandShakeUtil.getHandShakeClient(host);
            if (handShakeClient != null) {
                final HandShakePacket pingMessage = handShakeClient.getPingMessageAndExit("InfoDump " + ((message == null) ? "" : message));
                Logger.getLogger(InformationDumpRequest.class.getName()).info(pingMessage.toString());
                ConsoleOut.println("Information Dump has been requested");
            }
            else {
                ConsoleOut.println("Server Already Shutdown");
            }
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception occured when tried to thread dump");
            e.printStackTrace();
        }
    }
    
    public static void main(final String[] args) throws Exception {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            sb.append(args[i]).append(' ');
        }
        threadDump("localhost", sb.toString().trim());
    }
}
