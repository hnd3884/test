package sun.net.www.http;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.net.URL;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.util.HashMap;

public class KeepAliveCache extends HashMap<KeepAliveKey, ClientVector> implements Runnable
{
    private static final long serialVersionUID = -2937172892064557949L;
    static final int MAX_CONNECTIONS = 5;
    static int result;
    static final int LIFETIME = 5000;
    private Thread keepAliveTimer;
    
    static int getMaxConnections() {
        if (KeepAliveCache.result == -1) {
            KeepAliveCache.result = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("http.maxConnections", 5));
            if (KeepAliveCache.result <= 0) {
                KeepAliveCache.result = 5;
            }
        }
        return KeepAliveCache.result;
    }
    
    public KeepAliveCache() {
        this.keepAliveTimer = null;
    }
    
    public synchronized void put(final URL url, final Object o, final HttpClient httpClient) {
        int n = (this.keepAliveTimer == null) ? 1 : 0;
        if (n == 0 && !this.keepAliveTimer.isAlive()) {
            n = 1;
        }
        if (n != 0) {
            this.clear();
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    ThreadGroup threadGroup;
                    ThreadGroup parent;
                    for (threadGroup = Thread.currentThread().getThreadGroup(); (parent = threadGroup.getParent()) != null; threadGroup = parent) {}
                    KeepAliveCache.this.keepAliveTimer = new Thread(threadGroup, KeepAliveCache.this, "Keep-Alive-Timer");
                    KeepAliveCache.this.keepAliveTimer.setDaemon(true);
                    KeepAliveCache.this.keepAliveTimer.setPriority(8);
                    KeepAliveCache.this.keepAliveTimer.setContextClassLoader(null);
                    KeepAliveCache.this.keepAliveTimer.start();
                    return null;
                }
            });
        }
        final KeepAliveKey keepAliveKey = new KeepAliveKey(url, o);
        final ClientVector clientVector = super.get(keepAliveKey);
        if (clientVector == null) {
            final int keepAliveTimeout = httpClient.getKeepAliveTimeout();
            final ClientVector clientVector2 = new ClientVector((keepAliveTimeout > 0) ? (keepAliveTimeout * 1000) : 5000);
            clientVector2.put(httpClient);
            super.put(keepAliveKey, clientVector2);
        }
        else {
            clientVector.put(httpClient);
        }
    }
    
    public synchronized void remove(final HttpClient httpClient, final Object o) {
        final KeepAliveKey keepAliveKey = new KeepAliveKey(httpClient.url, o);
        final ClientVector clientVector = super.get(keepAliveKey);
        if (clientVector != null) {
            clientVector.remove(httpClient);
            if (clientVector.empty()) {
                this.removeVector(keepAliveKey);
            }
        }
    }
    
    synchronized void removeVector(final KeepAliveKey keepAliveKey) {
        super.remove(keepAliveKey);
    }
    
    public synchronized HttpClient get(final URL url, final Object o) {
        final ClientVector clientVector = super.get(new KeepAliveKey(url, o));
        if (clientVector == null) {
            return null;
        }
        return clientVector.get();
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000L);
            }
            catch (final InterruptedException ex) {}
            synchronized (this) {
                final long currentTimeMillis = System.currentTimeMillis();
                final ArrayList<KeepAliveKey> list = new ArrayList<KeepAliveKey>();
                for (final KeepAliveKey keepAliveKey : ((HashMap<KeepAliveKey, V>)this).keySet()) {
                    final ClientVector clientVector = ((HashMap<K, ClientVector>)this).get(keepAliveKey);
                    synchronized (clientVector) {
                        int i;
                        for (i = 0; i < clientVector.size(); ++i) {
                            final KeepAliveEntry keepAliveEntry = clientVector.elementAt(i);
                            if (currentTimeMillis - keepAliveEntry.idleStartTime <= clientVector.nap) {
                                break;
                            }
                            keepAliveEntry.hc.closeServer();
                        }
                        clientVector.subList(0, i).clear();
                        if (clientVector.size() != 0) {
                            continue;
                        }
                        list.add(keepAliveKey);
                    }
                }
                final Iterator<KeepAliveKey> iterator2 = list.iterator();
                while (iterator2.hasNext()) {
                    this.removeVector(iterator2.next());
                }
            }
            if (this.size() <= 0) {
                break;
            }
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        throw new NotSerializableException();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        throw new NotSerializableException();
    }
    
    static {
        KeepAliveCache.result = -1;
    }
}
