package sun.net.www.http;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.Stack;

class ClientVector extends Stack<KeepAliveEntry>
{
    private static final long serialVersionUID = -8680532108106489459L;
    int nap;
    
    ClientVector(final int nap) {
        this.nap = nap;
    }
    
    synchronized HttpClient get() {
        if (this.empty()) {
            return null;
        }
        HttpClient hc = null;
        final long currentTimeMillis = System.currentTimeMillis();
        do {
            final KeepAliveEntry keepAliveEntry = this.pop();
            if (currentTimeMillis - keepAliveEntry.idleStartTime > this.nap) {
                keepAliveEntry.hc.closeServer();
            }
            else {
                hc = keepAliveEntry.hc;
            }
        } while (hc == null && !this.empty());
        return hc;
    }
    
    synchronized void put(final HttpClient httpClient) {
        if (this.size() >= KeepAliveCache.getMaxConnections()) {
            httpClient.closeServer();
        }
        else {
            this.push(new KeepAliveEntry(httpClient, System.currentTimeMillis()));
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        throw new NotSerializableException();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        throw new NotSerializableException();
    }
}
