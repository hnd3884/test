package com.zoho.net.handshake;

import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.io.IOException;
import java.security.SecureRandom;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.concurrent.Callable;

public class HandShakeServer implements Callable<Boolean>
{
    private static final Properties CONF_PROP;
    private static final Logger LOGGER;
    private static boolean isConfLoaded;
    private static int serverListeningPort;
    private static long serverStartedTime;
    private static final List<HandShakeServerMessageHandler> MESSAGE_HANDLERS;
    private static AtomicBoolean hasExceptionOccurred;
    private static String serverHost;
    
    @Override
    public Boolean call() throws Exception {
        loadConfiguredMessages();
        final ExecutorService messengerServicePool = Executors.newFixedThreadPool(Integer.parseInt(HandShakeServer.CONF_PROP.getProperty("message.listen.queue.size", "3")));
        ServerSocket servSock = null;
        try {
            HandShakeServer.LOGGER.info("Starting HandShakeServer...");
            servSock = new ServerSocket();
            servSock.bind(getInetSockAddr());
            HandShakeServer.serverHost = InetAddress.getLocalHost().getHostName();
            writeToLockFile(servSock.getLocalPort());
            setServerListeningPort(servSock.getLocalPort());
            setServerStartedTime(new SecureRandom().nextLong());
            while (true) {
                HandShakeServer.LOGGER.info("Waiting for client connection...");
                messengerServicePool.submit((Callable<Object>)new RecvFromClient(servSock.accept()));
            }
        }
        catch (final Throwable t) {
            HandShakeServer.LOGGER.severe("Exception occurred while starting the server socket:: " + t.getMessage());
            t.printStackTrace();
            HandShakeServer.hasExceptionOccurred.set(true);
            throw t;
        }
        finally {
            if (servSock != null) {
                try {
                    servSock.close();
                }
                catch (final IOException ex) {}
            }
            HandShakeUtil.deleteLockFile();
        }
    }
    
    protected static boolean hasExceptionOccurred() {
        return HandShakeServer.hasExceptionOccurred.get();
    }
    
    private static InetSocketAddress getInetSockAddr() {
        final int port = 0;
        InetSocketAddress inetsktaddr = null;
        final String bindaddress = System.getProperty("bindaddress");
        if (bindaddress != null) {
            try {
                inetsktaddr = new InetSocketAddress(InetAddress.getByName(bindaddress), port);
            }
            catch (final UnknownHostException e) {
                e.printStackTrace();
            }
        }
        else {
            inetsktaddr = new InetSocketAddress(InetAddress.getLoopbackAddress().getHostName(), port);
        }
        return inetsktaddr;
    }
    
    private static void loadConfiguredMessages() throws IOException {
        final File handshakeConf = new File(System.getProperty("server.dir") + "/conf/handshake_messages.conf");
        if (handshakeConf.exists() && !HandShakeServer.isConfLoaded) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(handshakeConf);
                HandShakeServer.CONF_PROP.load(fileInputStream);
            }
            finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            HandShakeServer.isConfLoaded = Boolean.TRUE;
        }
    }
    
    private static void getConfiguredHandlers() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (!HandShakeServer.CONF_PROP.isEmpty()) {
            for (String handler : HandShakeServer.CONF_PROP.getProperty("message.handlers", "").split(",")) {
                handler = handler.trim();
                addMessageHandler((HandShakeServerMessageHandler)Class.forName(handler).newInstance());
            }
        }
    }
    
    private static void writeToLockFile(final int port) throws IOException {
        HandShakeServer.LOGGER.info("Writing HandShakeServer port [" + port + "] in .lock file");
        final File lockFile = new File(".lock");
        if (lockFile.exists()) {
            lockFile.delete();
        }
        final String value = String.valueOf(port);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(lockFile);
            final byte[] b = value.getBytes();
            fos.write(b);
        }
        finally {
            fos.close();
        }
    }
    
    static void addMessageHandler(final HandShakeServerMessageHandler handler) {
        getMessagehandlers().add(handler);
    }
    
    static String getConfiguredResponseMessage(final HandShakePacket packetFromClient) {
        final String messageFromClient = packetFromClient.getMessage();
        if (messageFromClient != null) {
            return messageFromClient.equals("PING") ? "ALIVE" : HandShakeServer.CONF_PROP.getProperty(messageFromClient, "UNKNOWN_PING_MESSAGE");
        }
        return "UNKNOWN_PING_MESSAGE";
    }
    
    static int getServerListeningPort() {
        return HandShakeServer.serverListeningPort;
    }
    
    private static void setServerListeningPort(final int serverListeningPort) {
        HandShakeServer.LOGGER.info("HandShakeServer listening port :: " + serverListeningPort);
        HandShakeServer.serverListeningPort = serverListeningPort;
    }
    
    static long getServerStartedTime() {
        return HandShakeServer.serverStartedTime;
    }
    
    static void setServerStartedTime(final long serverStartedTime) {
        HandShakeServer.serverStartedTime = serverStartedTime;
    }
    
    static String getServerHostName() {
        return HandShakeServer.serverHost;
    }
    
    static List<HandShakeServerMessageHandler> getMessagehandlers() {
        return HandShakeServer.MESSAGE_HANDLERS;
    }
    
    static {
        CONF_PROP = new Properties();
        LOGGER = Logger.getLogger(HandShakeServer.class.getName());
        HandShakeServer.isConfLoaded = Boolean.FALSE;
        HandShakeServer.serverListeningPort = -1;
        HandShakeServer.serverStartedTime = -1L;
        MESSAGE_HANDLERS = new ArrayList<HandShakeServerMessageHandler>();
        HandShakeServer.hasExceptionOccurred = new AtomicBoolean(false);
        HandShakeServer.serverHost = null;
    }
}
