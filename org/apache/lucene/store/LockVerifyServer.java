package org.apache.lucene.store;

import org.apache.lucene.util.SuppressForbidden;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class LockVerifyServer
{
    @SuppressForbidden(reason = "System.out required: command line tool")
    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java org.apache.lucene.store.LockVerifyServer bindToIp clients\n");
            System.exit(1);
        }
        int arg = 0;
        final String hostname = args[arg++];
        final int maxClients = Integer.parseInt(args[arg++]);
        try (final ServerSocket s = new ServerSocket()) {
            s.setReuseAddress(true);
            s.setSoTimeout(30000);
            s.bind(new InetSocketAddress(hostname, 0));
            final InetSocketAddress localAddr = (InetSocketAddress)s.getLocalSocketAddress();
            System.out.println("Listening on " + localAddr + "...");
            System.setProperty("lockverifyserver.port", Integer.toString(localAddr.getPort()));
            final Object localLock = new Object();
            final int[] lockedID = { -1 };
            final CountDownLatch startingGun = new CountDownLatch(1);
            final Thread[] threads = new Thread[maxClients];
            for (int count = 0; count < maxClients; ++count) {
                final Socket cs = s.accept();
                (threads[count] = new Thread() {
                    @Override
                    public void run() {
                        try (final InputStream in = cs.getInputStream();
                             final OutputStream os = cs.getOutputStream()) {
                            final int id = in.read();
                            if (id < 0) {
                                throw new IOException("Client closed connection before communication started.");
                            }
                            startingGun.await();
                            os.write(43);
                            os.flush();
                            while (true) {
                                final int command = in.read();
                                if (command < 0) {
                                    break;
                                }
                                synchronized (localLock) {
                                    final int currentLock = lockedID[0];
                                    if (currentLock == -2) {
                                        return;
                                    }
                                    switch (command) {
                                        case 1: {
                                            if (currentLock != -1) {
                                                lockedID[0] = -2;
                                                throw new IllegalStateException("id " + id + " got lock, but " + currentLock + " already holds the lock");
                                            }
                                            lockedID[0] = id;
                                            break;
                                        }
                                        case 0: {
                                            if (currentLock != id) {
                                                lockedID[0] = -2;
                                                throw new IllegalStateException("id " + id + " released the lock, but " + currentLock + " is the one holding the lock");
                                            }
                                            lockedID[0] = -1;
                                            break;
                                        }
                                        default: {
                                            throw new RuntimeException("Unrecognized command: " + command);
                                        }
                                    }
                                    os.write(command);
                                    os.flush();
                                }
                            }
                        }
                        catch (final RuntimeException | Error e) {
                            throw e;
                        }
                        catch (final Exception ioe) {
                            throw new RuntimeException(ioe);
                        }
                        finally {
                            IOUtils.closeWhileHandlingException(cs);
                        }
                    }
                }).start();
            }
            System.out.println("All clients started, fire gun...");
            startingGun.countDown();
            for (final Thread t : threads) {
                t.join();
            }
            System.clearProperty("lockverifyserver.port");
            System.out.println("Server terminated.");
        }
    }
}
