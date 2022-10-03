package com.adventnet.mfw;

import com.adventnet.mfw.logging.LoggerUtil;
import com.zoho.net.handshake.HandShakePacket;
import com.zoho.net.handshake.HandShakeClient;
import java.io.File;
import java.util.logging.Logger;
import com.zoho.net.handshake.HandShakeUtil;

public class Shutdown
{
    public Shutdown(final String[] arg) throws Exception {
        final String host = arg[0];
        try {
            shutdown(host);
            if (arg.length > 2 && arg[2].equalsIgnoreCase("sync")) {
                int retries = 30;
                int interval = 1000;
                if (arg.length > 4) {
                    retries = Integer.parseInt(arg[3]);
                    interval = Integer.parseInt(arg[4]);
                }
                final Thread waitThread = new Thread(new ShutDownThread(retries, interval), "Shut down thread");
                waitThread.start();
                waitThread.join();
            }
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception occured when tried to stop server");
            e.printStackTrace();
        }
    }
    
    public static void shutdown(final String host) throws Exception {
        try {
            final HandShakeClient handShakeClient = HandShakeUtil.getHandShakeClient();
            if (handShakeClient != null) {
                final HandShakePacket pingMessage = handShakeClient.getPingMessageAndExit("ShutDownML");
                Logger.getLogger(Shutdown.class.getName()).info(pingMessage.toString());
            }
            else if (HandShakeUtil.isLockFileExist()) {
                ConsoleOut.println("Server is already been shutdown abruptly.");
                final File lock = new File(".lock");
                if (lock.exists()) {
                    lock.delete();
                }
            }
            else {
                ConsoleOut.println("Server Already Shutdown");
            }
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception occured when tried to stop server");
            e.printStackTrace();
        }
    }
    
    public static void main(final String[] arg) throws Exception {
        LoggerUtil.initLog("shutdown");
        new Shutdown(arg);
    }
    
    class ShutDownThread implements Runnable
    {
        int retries;
        int interval;
        int counter;
        
        ShutDownThread(final int retries, final int interval) {
            this.retries = 30;
            this.interval = 1000;
            this.counter = 0;
            this.retries = retries;
            this.interval = interval;
        }
        
        @Override
        public void run() {
            final File f = new File(".lock");
            while (f.exists() && this.counter < this.retries) {
                try {
                    ConsoleOut.println("Waiting for Server Shutdown ...");
                    Thread.sleep(this.interval);
                    ++this.counter;
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (f.exists()) {
                ConsoleOut.println("Server is not  Shutdown");
            }
            else {
                ConsoleOut.println("Server is   Shutdown");
            }
        }
    }
}
