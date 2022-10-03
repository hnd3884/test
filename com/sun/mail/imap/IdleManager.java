package com.sun.mail.imap;

import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.util.Iterator;
import java.util.Set;
import java.nio.channels.SelectionKey;
import java.nio.channels.CancelledKeyException;
import java.io.InterruptedIOException;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import javax.mail.MessagingException;
import javax.mail.Folder;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.mail.Session;
import java.util.Queue;
import com.sun.mail.util.MailLogger;
import java.nio.channels.Selector;
import java.util.concurrent.Executor;

public class IdleManager
{
    private Executor es;
    private Selector selector;
    private MailLogger logger;
    private volatile boolean die;
    private volatile boolean running;
    private Queue<IMAPFolder> toWatch;
    private Queue<IMAPFolder> toAbort;
    
    public IdleManager(final Session session, final Executor es) throws IOException {
        this.die = false;
        this.toWatch = new ConcurrentLinkedQueue<IMAPFolder>();
        this.toAbort = new ConcurrentLinkedQueue<IMAPFolder>();
        this.es = es;
        this.logger = new MailLogger(this.getClass(), "DEBUG IMAP", session.getDebug(), session.getDebugOut());
        this.selector = Selector.open();
        es.execute(new Runnable() {
            @Override
            public void run() {
                IdleManager.this.logger.fine("IdleManager select starting");
                try {
                    IdleManager.this.running = true;
                    IdleManager.this.select();
                }
                finally {
                    IdleManager.this.running = false;
                    IdleManager.this.logger.fine("IdleManager select terminating");
                }
            }
        });
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    public void watch(final Folder folder) throws MessagingException {
        if (this.die) {
            throw new MessagingException("IdleManager is not running");
        }
        if (!(folder instanceof IMAPFolder)) {
            throw new MessagingException("Can only watch IMAP folders");
        }
        final IMAPFolder ifolder = (IMAPFolder)folder;
        final SocketChannel sc = ifolder.getChannel();
        if (sc != null) {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.log(Level.FINEST, "IdleManager watching {0}", folderName(ifolder));
            }
            int tries = 0;
            while (!ifolder.startIdle(this)) {
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.log(Level.FINEST, "IdleManager.watch startIdle failed for {0}", folderName(ifolder));
                }
                ++tries;
            }
            if (this.logger.isLoggable(Level.FINEST)) {
                if (tries > 0) {
                    this.logger.log(Level.FINEST, "IdleManager.watch startIdle succeeded for {0} after " + tries + " tries", folderName(ifolder));
                }
                else {
                    this.logger.log(Level.FINEST, "IdleManager.watch startIdle succeeded for {0}", folderName(ifolder));
                }
            }
            synchronized (this) {
                this.toWatch.add(ifolder);
                this.selector.wakeup();
            }
            return;
        }
        if (folder.isOpen()) {
            throw new MessagingException("Folder is not using SocketChannels");
        }
        throw new MessagingException("Folder is not open");
    }
    
    void requestAbort(final IMAPFolder folder) {
        this.toAbort.add(folder);
        this.selector.wakeup();
    }
    
    private void select() {
        this.die = false;
        try {
            while (!this.die) {
                this.watchAll();
                this.logger.finest("IdleManager waiting...");
                final int ns = this.selector.select();
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.log(Level.FINEST, "IdleManager selected {0} channels", (Object)ns);
                }
                if (this.die) {
                    break;
                }
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                do {
                    this.processKeys();
                } while (this.selector.selectNow() > 0 || !this.toAbort.isEmpty());
            }
        }
        catch (final InterruptedIOException ex) {
            this.logger.log(Level.FINEST, "IdleManager interrupted", ex);
        }
        catch (final IOException ex2) {
            this.logger.log(Level.FINEST, "IdleManager got I/O exception", ex2);
        }
        catch (final Exception ex3) {
            this.logger.log(Level.FINEST, "IdleManager got exception", ex3);
        }
        finally {
            this.die = true;
            this.logger.finest("IdleManager unwatchAll");
            try {
                this.unwatchAll();
                this.selector.close();
            }
            catch (final IOException ex4) {
                this.logger.log(Level.FINEST, "IdleManager unwatch exception", ex4);
            }
            this.logger.fine("IdleManager exiting");
        }
    }
    
    private void watchAll() {
        IMAPFolder folder;
        while ((folder = this.toWatch.poll()) != null) {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.log(Level.FINEST, "IdleManager adding {0} to selector", folderName(folder));
            }
            try {
                final SocketChannel sc = folder.getChannel();
                if (sc == null) {
                    continue;
                }
                sc.configureBlocking(false);
                sc.register(this.selector, 1, folder);
            }
            catch (final IOException ex) {
                this.logger.log(Level.FINEST, "IdleManager can't register folder", ex);
            }
            catch (final CancelledKeyException ex2) {
                this.logger.log(Level.FINEST, "IdleManager can't register folder", ex2);
            }
        }
    }
    
    private void processKeys() throws IOException {
        final Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
        final Iterator<SelectionKey> it = selectedKeys.iterator();
        while (it.hasNext()) {
            final SelectionKey sk = it.next();
            it.remove();
            sk.cancel();
            final IMAPFolder folder = (IMAPFolder)sk.attachment();
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.log(Level.FINEST, "IdleManager selected folder: {0}", folderName(folder));
            }
            final SelectableChannel sc = sk.channel();
            sc.configureBlocking(true);
            try {
                if (folder.handleIdle(false)) {
                    if (this.logger.isLoggable(Level.FINEST)) {
                        this.logger.log(Level.FINEST, "IdleManager continue watching folder {0}", folderName(folder));
                    }
                    this.toWatch.add(folder);
                }
                else {
                    if (!this.logger.isLoggable(Level.FINEST)) {
                        continue;
                    }
                    this.logger.log(Level.FINEST, "IdleManager done watching folder {0}", folderName(folder));
                }
            }
            catch (final MessagingException ex) {
                this.logger.log(Level.FINEST, "IdleManager got exception for folder: " + folderName(folder), ex);
            }
        }
        IMAPFolder folder;
        while ((folder = this.toAbort.poll()) != null) {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.log(Level.FINEST, "IdleManager aborting IDLE for folder: {0}", folderName(folder));
            }
            final SocketChannel sc2 = folder.getChannel();
            if (sc2 == null) {
                continue;
            }
            final SelectionKey sk2 = sc2.keyFor(this.selector);
            if (sk2 != null) {
                sk2.cancel();
            }
            sc2.configureBlocking(true);
            final Socket sock = sc2.socket();
            if (sock != null && sock.getSoTimeout() > 0) {
                this.logger.finest("IdleManager requesting DONE with timeout");
                this.toWatch.remove(folder);
                final IMAPFolder folder2 = folder;
                this.es.execute(new Runnable() {
                    @Override
                    public void run() {
                        folder2.idleAbortWait();
                    }
                });
            }
            else {
                folder.idleAbort();
                this.toWatch.add(folder);
            }
        }
    }
    
    private void unwatchAll() {
        final Set<SelectionKey> keys = this.selector.keys();
        for (final SelectionKey sk : keys) {
            sk.cancel();
            final IMAPFolder folder = (IMAPFolder)sk.attachment();
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.log(Level.FINEST, "IdleManager no longer watching folder: {0}", folderName(folder));
            }
            final SelectableChannel sc = sk.channel();
            try {
                sc.configureBlocking(true);
                folder.idleAbortWait();
            }
            catch (final IOException ex) {
                this.logger.log(Level.FINEST, "IdleManager exception while aborting idle for folder: " + folderName(folder), ex);
            }
        }
        IMAPFolder folder;
        while ((folder = this.toWatch.poll()) != null) {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.log(Level.FINEST, "IdleManager aborting IDLE for unwatched folder: {0}", folderName(folder));
            }
            final SocketChannel sc2 = folder.getChannel();
            if (sc2 == null) {
                continue;
            }
            try {
                sc2.configureBlocking(true);
                folder.idleAbortWait();
            }
            catch (final IOException ex2) {
                this.logger.log(Level.FINEST, "IdleManager exception while aborting idle for folder: " + folderName(folder), ex2);
            }
        }
    }
    
    public synchronized void stop() {
        this.die = true;
        this.logger.fine("IdleManager stopping");
        this.selector.wakeup();
    }
    
    private static String folderName(final Folder folder) {
        try {
            return folder.getURLName().toString();
        }
        catch (final MessagingException mex) {
            return folder.getStore().toString() + "/" + folder.toString();
        }
    }
}
