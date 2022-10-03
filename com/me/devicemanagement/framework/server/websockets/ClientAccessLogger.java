package com.me.devicemanagement.framework.server.websockets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.logging.Logger;

class ClientAccessLogger
{
    private static Logger clientAccessLogger;
    private static final String ACCESS_LOGGER_LINE1;
    private static final String ACCESS_LOGGER_LINE2;
    
    private static void populateHeaderInAccessLog(final String fileName, final String line1, final String line2) {
        try {
            boolean isHeaderAvailable = true;
            final String str = System.getProperty("server.home") + File.separator + "logs" + File.separator + fileName;
            final File file = new File(str);
            if (file.exists()) {
                final FileReader fr = new FileReader(file);
                final BufferedReader br = new BufferedReader(fr);
                if (br.readLine() == null) {
                    isHeaderAvailable = false;
                }
                br.close();
                fr.close();
            }
            else {
                isHeaderAvailable = false;
            }
            if (!isHeaderAvailable) {
                ClientAccessLogger.clientAccessLogger.log(Level.INFO, line1);
                ClientAccessLogger.clientAccessLogger.log(Level.INFO, line2);
            }
        }
        catch (final Exception ex) {}
    }
    
    static void logClientAccess(final String clientId, final String clientName, final String clientType, final String action, final int connectionMode, final int socketType) {
        final String date = getCurrentDate();
        final String time = getCurrentTime();
        final String remarks = "--";
        formatLogEntry(date, time, clientId, clientName, clientType, action, getConnectionModeString(connectionMode), getSocketTypeString(socketType), remarks);
    }
    
    static void logClientAccess(final String clientId, final String clientName, final String clientType, final String action, final int connectionMode, final int socketType, final String remarks) {
        final String date = getCurrentDate();
        final String time = getCurrentTime();
        formatLogEntry(date, time, clientId, clientName, clientType, action, getConnectionModeString(connectionMode), getSocketTypeString(socketType), remarks);
    }
    
    private static void formatLogEntry(final String date, final String time, final String clientId, final String clientName, final String clientType, final String action, final String connectionMode, final String socketType, final String remarks) {
        final String printStr = String.format("%-15s", date) + String.format("%-15s", time) + String.format("%-20s", clientId) + String.format("%-30s", clientName) + String.format("%-15s", clientType) + String.format("%-25s", action) + String.format("%-20s", connectionMode) + String.format("%-15s", socketType) + remarks;
        populateHeaderInAccessLog("wsclientaccesslog0.txt", ClientAccessLogger.ACCESS_LOGGER_LINE1, ClientAccessLogger.ACCESS_LOGGER_LINE2);
        ClientAccessLogger.clientAccessLogger.log(Level.INFO, printStr);
    }
    
    private static String getCurrentDate() {
        final Date date = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        final String simpleDate = sdf.format(date);
        return simpleDate;
    }
    
    private static String getCurrentTime() {
        final Date date = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        final String time = sdf.format(date);
        return time;
    }
    
    private static String getConnectionModeString(final int connectionMode) {
        String strConnMode = "SYNCHRONOUS";
        if (connectionMode == Constants.ClientConnentionMode.ASYNC_READ.ordinal()) {
            strConnMode = "ASYNCHRONOUS";
        }
        return strConnMode;
    }
    
    private static String getSocketTypeString(final int socketType) {
        String strSocketType = "WS";
        if (socketType == Constants.ClientSocketType.TCP.ordinal()) {
            strSocketType = "TCP";
        }
        return strSocketType;
    }
    
    static {
        ClientAccessLogger.clientAccessLogger = Logger.getLogger("ClientAccessLogger");
        ACCESS_LOGGER_LINE1 = String.format("%-15s", "DATE") + String.format("%-15s", "TIME") + String.format("%-20s", "CLIENT_ID") + String.format("%-30s", "CLIENT_NAME") + String.format("%-15s", "CLIENT_TYPE") + String.format("%-25s", "ACTION") + String.format("%-20s", "CONNECTION_MODE") + String.format("%-15s", "SOCKET_TYPE") + "REMARKS";
        ACCESS_LOGGER_LINE2 = String.format("%-15s", "----") + String.format("%-15s", "----") + String.format("%-20s", "---------") + String.format("%-30s", "-----------") + String.format("%-15s", "-----------") + String.format("%-25s", "------") + String.format("%-20s", "---------------") + String.format("%-15s", "-----------") + "-------";
    }
}
