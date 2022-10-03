package com.zoho.net.handshake;

import java.util.concurrent.Executors;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.io.File;
import java.util.logging.Logger;
import java.util.concurrent.ExecutorService;

public class HandShakeUtil
{
    private static final String LOCALHOST = "localhost";
    private static int lockPort;
    private static final ExecutorService SERVERPOOL;
    private static final Logger LOGGER;
    private static final File LOCK_FILE;
    
    public static void startHandShakeServer() throws IOException {
        if (!isServerListening()) {
            HandShakeUtil.SERVERPOOL.submit((Callable<Object>)new HandShakeServer());
            while (HandShakeServer.getServerStartedTime() == -1L) {
                if (HandShakeServer.hasExceptionOccurred()) {
                    throw new RuntimeException("Exception occurred while starting the handshake server.");
                }
                try {
                    Thread.sleep(100L);
                }
                catch (final InterruptedException ex) {}
            }
            HandShakeUtil.LOGGER.info("Started HandShakeServer successfully.");
            return;
        }
        HandShakeUtil.SERVERPOOL.shutdownNow();
        throw new IOException("Already another HandShakeServer seems to be running");
    }
    
    public static boolean isServerListening() {
        try {
            if (isLockFileExist()) {
                final int port = getServerListeningPort();
                HandShakeUtil.LOGGER.info("server listening port:: " + port);
                if (port != -1) {
                    return isServerListening(System.getProperty("bindaddress", "localhost"), port);
                }
                deleteLockFile();
            }
            return Boolean.FALSE;
        }
        catch (final IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }
    
    protected static void deleteLockFile() {
        HandShakeUtil.LOCK_FILE.delete();
    }
    
    public static boolean isServerListening(final String hostName, final int port) {
        final HandShakeClient hsc = null;
        try {
            final boolean isRunning = getStatus(new HandShakeClient(hostName, port));
            HandShakeUtil.LOGGER.info("is HandShakeServer running :: " + isRunning);
            return isRunning;
        }
        catch (final IOException ioe) {
            return false;
        }
        finally {
            if (hsc != null) {
                hsc.close();
            }
        }
    }
    
    public static HandShakeClient getHandShakeClient() {
        try {
            return isLockFileExist() ? getHandShakeClient(System.getProperty("bindaddress", "localhost"), getServerListeningPort()) : null;
        }
        catch (final IOException e) {
            return null;
        }
    }
    
    public static HandShakeClient getHandShakeClient(final String host) {
        try {
            return getHandShakeClient(host, getServerListeningPort());
        }
        catch (final IOException e) {
            return null;
        }
    }
    
    public static HandShakeClient getHandShakeClient(final String hostName, final int port) {
        try {
            final HandShakeClient hsClient = new HandShakeClient(hostName, port);
            final HandShakeClient hsClientNew = hsClient.validateClient(hostName, port);
            hsClient.close();
            return hsClientNew;
        }
        catch (final IOException e) {
            HandShakeUtil.LOGGER.warning("Unable to connect HandShakeServer :: " + e.getMessage() + " :: " + hostName + ":" + port);
        }
        catch (final ClassNotFoundException e2) {
            HandShakeUtil.LOGGER.warning("Unable to connect HandShakeServer :: " + e2.getMessage());
        }
        return null;
    }
    
    public static boolean isLockFileExist() {
        return HandShakeUtil.LOCK_FILE.exists();
    }
    
    public static int getServerListeningPort() throws IOException {
        HandShakeUtil.lockPort = HandShakeServer.getServerListeningPort();
        if (HandShakeUtil.lockPort == -1 && isLockFileExist()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(".lock"));
                final String port = br.readLine();
                if (port != null) {
                    try {
                        HandShakeUtil.lockPort = Integer.parseInt(port);
                    }
                    catch (final NumberFormatException nfe) {
                        throw new IOException("Problem while reading port from .lock file :: fetched value [" + port + "]");
                    }
                }
            }
            finally {
                if (br != null) {
                    br.close();
                }
            }
        }
        else if (HandShakeUtil.lockPort == -1) {
            throw new IOException(".lock file does not exist");
        }
        return HandShakeUtil.lockPort;
    }
    
    private static boolean getStatus(final HandShakeClient hsc) throws IOException {
        HandShakePacket pingMessage;
        try {
            pingMessage = hsc.getPingMessage("PING");
            HandShakeUtil.LOGGER.info("HandShake response packet received from HandShakeServer.");
            HandShakeUtil.LOGGER.info(pingMessage.toString());
        }
        catch (final IOException e) {
            HandShakeUtil.LOGGER.warning("Exception while get PING status, hence returning false. " + e.getMessage());
            return false;
        }
        catch (final ClassNotFoundException e2) {
            HandShakeUtil.LOGGER.warning("Exception while get PING status, hence returning false. " + e2.getMessage());
            return false;
        }
        HandShakeUtil.LOGGER.info("Received response message :: " + pingMessage);
        final Path userDirPath = Paths.get(pingMessage.getServerHome(), new String[0]);
        final Path serverHomePath = Paths.get(System.getProperty("server.home"), new String[0]);
        return pingMessage != null && (pingMessage.getMessage().equals("ALIVE") && Files.isSameFile(userDirPath, serverHomePath));
    }
    
    public static long getServerStartedTime() {
        return HandShakeServer.getServerStartedTime();
    }
    
    public static void addMessageHandler(final HandShakeServerMessageHandler handler) {
        HandShakeServer.addMessageHandler(handler);
    }
    
    public static long getHandShakeServerID() {
        return getServerStartedTime();
    }
    
    static {
        HandShakeUtil.lockPort = -1;
        SERVERPOOL = Executors.newFixedThreadPool(1);
        LOGGER = Logger.getLogger(HandShakeUtil.class.getName());
        LOCK_FILE = new File(".lock");
    }
}
