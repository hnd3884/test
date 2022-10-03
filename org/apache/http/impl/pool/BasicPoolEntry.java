package org.apache.http.impl.pool;

import java.io.IOException;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.pool.PoolEntry;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicPoolEntry extends PoolEntry<HttpHost, HttpClientConnection>
{
    public BasicPoolEntry(final String id, final HttpHost route, final HttpClientConnection conn) {
        super(id, route, conn);
    }
    
    @Override
    public void close() {
        try {
            final HttpClientConnection connection = ((PoolEntry<T, HttpClientConnection>)this).getConnection();
            try {
                final int socketTimeout = connection.getSocketTimeout();
                if (socketTimeout <= 0 || socketTimeout > 1000) {
                    connection.setSocketTimeout(1000);
                }
                connection.close();
            }
            catch (final IOException ex) {
                connection.shutdown();
            }
        }
        catch (final IOException ex2) {}
    }
    
    @Override
    public boolean isClosed() {
        return !((PoolEntry<T, HttpClientConnection>)this).getConnection().isOpen();
    }
}
