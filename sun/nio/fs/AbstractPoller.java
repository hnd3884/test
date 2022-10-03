package sun.nio.fs;

import java.nio.file.ClosedWatchServiceException;
import java.nio.file.StandardWatchEventKinds;
import java.util.HashSet;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.util.Set;
import java.nio.file.Path;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;

abstract class AbstractPoller implements Runnable
{
    private final LinkedList<Request> requestList;
    private boolean shutdown;
    
    protected AbstractPoller() {
        this.requestList = new LinkedList<Request>();
        this.shutdown = false;
    }
    
    public void start() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            final /* synthetic */ Runnable val$thisRunnable;
            
            @Override
            public Object run() {
                final Thread thread = new Thread(this.val$thisRunnable);
                thread.setDaemon(true);
                thread.start();
                return null;
            }
        });
    }
    
    abstract void wakeup() throws IOException;
    
    abstract Object implRegister(final Path p0, final Set<? extends WatchEvent.Kind<?>> p1, final WatchEvent.Modifier... p2);
    
    abstract void implCancelKey(final WatchKey p0);
    
    abstract void implCloseAll();
    
    final WatchKey register(final Path path, final WatchEvent.Kind<?>[] array, final WatchEvent.Modifier... array2) throws IOException {
        if (path == null) {
            throw new NullPointerException();
        }
        final HashSet set = new HashSet(array.length);
        for (final WatchEvent.Kind<?> kind : array) {
            if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY || kind == StandardWatchEventKinds.ENTRY_DELETE) {
                set.add(kind);
            }
            else if (kind != StandardWatchEventKinds.OVERFLOW) {
                if (kind == null) {
                    throw new NullPointerException("An element in event set is 'null'");
                }
                throw new UnsupportedOperationException(kind.name());
            }
        }
        if (set.isEmpty()) {
            throw new IllegalArgumentException("No events to register");
        }
        return (WatchKey)this.invoke(RequestType.REGISTER, path, set, array2);
    }
    
    final void cancel(final WatchKey watchKey) {
        try {
            this.invoke(RequestType.CANCEL, watchKey);
        }
        catch (final IOException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    final void close() throws IOException {
        this.invoke(RequestType.CLOSE, new Object[0]);
    }
    
    private Object invoke(final RequestType requestType, final Object... array) throws IOException {
        final Request request = new Request(requestType, array);
        synchronized (this.requestList) {
            if (this.shutdown) {
                throw new ClosedWatchServiceException();
            }
            this.requestList.add(request);
        }
        this.wakeup();
        final Object awaitResult = request.awaitResult();
        if (awaitResult instanceof RuntimeException) {
            throw (RuntimeException)awaitResult;
        }
        if (awaitResult instanceof IOException) {
            throw (IOException)awaitResult;
        }
        return awaitResult;
    }
    
    boolean processRequests() {
        synchronized (this.requestList) {
            Request request;
            while ((request = this.requestList.poll()) != null) {
                if (this.shutdown) {
                    request.release(new ClosedWatchServiceException());
                }
                switch (request.type()) {
                    case REGISTER: {
                        final Object[] parameters = request.parameters();
                        request.release(this.implRegister((Path)parameters[0], (Set<? extends WatchEvent.Kind<?>>)parameters[1], (WatchEvent.Modifier[])parameters[2]));
                        continue;
                    }
                    case CANCEL: {
                        this.implCancelKey((WatchKey)request.parameters()[0]);
                        request.release(null);
                        continue;
                    }
                    case CLOSE: {
                        this.implCloseAll();
                        request.release(null);
                        this.shutdown = true;
                        continue;
                    }
                    default: {
                        request.release(new IOException("request not recognized"));
                        continue;
                    }
                }
            }
        }
        return this.shutdown;
    }
    
    private enum RequestType
    {
        REGISTER, 
        CANCEL, 
        CLOSE;
    }
    
    private static class Request
    {
        private final RequestType type;
        private final Object[] params;
        private boolean completed;
        private Object result;
        
        Request(final RequestType type, final Object... params) {
            this.completed = false;
            this.result = null;
            this.type = type;
            this.params = params;
        }
        
        RequestType type() {
            return this.type;
        }
        
        Object[] parameters() {
            return this.params;
        }
        
        void release(final Object result) {
            synchronized (this) {
                this.completed = true;
                this.result = result;
                this.notifyAll();
            }
        }
        
        Object awaitResult() {
            boolean b = false;
            synchronized (this) {
                while (!this.completed) {
                    try {
                        this.wait();
                    }
                    catch (final InterruptedException ex) {
                        b = true;
                    }
                }
                if (b) {
                    Thread.currentThread().interrupt();
                }
                return this.result;
            }
        }
    }
}
