package org.apache.lucene.store;

import org.apache.lucene.util.SuppressForbidden;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Random;
import java.net.SocketAddress;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class LockStressTest
{
    static final String LOCK_FILE_NAME = "test.lock";
    
    @SuppressForbidden(reason = "System.out required: command line tool")
    public static void main(final String[] args) throws Exception {
        if (args.length != 7) {
            System.out.println("Usage: java org.apache.lucene.store.LockStressTest myID verifierHost verifierPort lockFactoryClassName lockDirName sleepTimeMS count\n\n  myID = int from 0 .. 255 (should be unique for test process)\n  verifierHost = hostname that LockVerifyServer is listening on\n  verifierPort = port that LockVerifyServer is listening on\n  lockFactoryClassName = primary FSLockFactory class that we will use\n  lockDirName = path to the lock directory\n  sleepTimeMS = milliseconds to pause betweeen each lock obtain/release\n  count = number of locking tries\n\nYou should run multiple instances of this process, each with its own\nunique ID, and each pointing to the same lock directory, to verify\nthat locking is working correctly.\n\nMake sure you are first running LockVerifyServer.");
            System.exit(1);
        }
        int arg = 0;
        final int myID = Integer.parseInt(args[arg++]);
        if (myID < 0 || myID > 255) {
            System.out.println("myID must be a unique int 0..255");
            System.exit(1);
        }
        final String verifierHost = args[arg++];
        final int verifierPort = Integer.parseInt(args[arg++]);
        final String lockFactoryClassName = args[arg++];
        final Path lockDirPath = Paths.get(args[arg++], new String[0]);
        final int sleepTimeMS = Integer.parseInt(args[arg++]);
        final int count = Integer.parseInt(args[arg++]);
        final LockFactory lockFactory = getNewLockFactory(lockFactoryClassName);
        final FSDirectory lockDir = new SimpleFSDirectory(lockDirPath, NoLockFactory.INSTANCE);
        final InetSocketAddress addr = new InetSocketAddress(verifierHost, verifierPort);
        System.out.println("Connecting to server " + addr + " and registering as client " + myID + "...");
        try (final Socket socket = new Socket()) {
            socket.setReuseAddress(true);
            socket.connect(addr, 500);
            final OutputStream out = socket.getOutputStream();
            final InputStream in = socket.getInputStream();
            out.write(myID);
            out.flush();
            LockFactory verifyLF = new VerifyingLockFactory(lockFactory, in, out);
            final Random rnd = new Random();
            if (in.read() != 43) {
                throw new IOException("Protocol violation");
            }
            for (int i = 0; i < count; ++i) {
                try (final Lock l = verifyLF.obtainLock(lockDir, "test.lock")) {
                    if (rnd.nextInt(10) == 0) {
                        if (rnd.nextBoolean()) {
                            verifyLF = new VerifyingLockFactory(getNewLockFactory(lockFactoryClassName), in, out);
                        }
                        try (final Lock secondLock = verifyLF.obtainLock(lockDir, "test.lock")) {
                            throw new IOException("Double obtain");
                        }
                        catch (final LockObtainFailedException ex) {}
                    }
                    Thread.sleep(sleepTimeMS);
                }
                catch (final LockObtainFailedException ex2) {}
                if (i % 500 == 0) {
                    System.out.println(i * 100.0 / count + "% done.");
                }
                Thread.sleep(sleepTimeMS);
            }
        }
        System.out.println("Finished " + count + " tries.");
    }
    
    private static FSLockFactory getNewLockFactory(final String lockFactoryClassName) throws IOException {
        try {
            return (FSLockFactory)Class.forName(lockFactoryClassName).getField("INSTANCE").get(null);
        }
        catch (final ReflectiveOperationException e) {
            try {
                return (FSLockFactory)Class.forName(lockFactoryClassName).asSubclass(FSLockFactory.class).newInstance();
            }
            catch (final ReflectiveOperationException | ClassCastException e2) {
                throw new IOException("Cannot get lock factory singleton of " + lockFactoryClassName);
            }
        }
    }
}
