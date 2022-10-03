package redis.clients.jedis;

import java.io.IOException;
import java.util.Iterator;
import redis.clients.jedis.exceptions.JedisDataException;
import java.util.ArrayList;
import java.util.List;
import java.io.Closeable;

public class Transaction extends MultiKeyPipelineBase implements Closeable
{
    protected boolean inTransaction;
    
    protected Transaction() {
        this.inTransaction = true;
    }
    
    public Transaction(final Client client) {
        this.inTransaction = true;
        this.client = client;
    }
    
    @Override
    protected Client getClient(final String key) {
        return this.client;
    }
    
    @Override
    protected Client getClient(final byte[] key) {
        return this.client;
    }
    
    public void clear() {
        if (this.inTransaction) {
            this.discard();
        }
    }
    
    public List<Object> exec() {
        this.client.exec();
        this.client.getAll(1);
        this.inTransaction = false;
        final List<Object> unformatted = this.client.getObjectMultiBulkReply();
        if (unformatted == null) {
            return null;
        }
        final List<Object> formatted = new ArrayList<Object>();
        for (final Object o : unformatted) {
            try {
                formatted.add(this.generateResponse(o).get());
            }
            catch (final JedisDataException e) {
                formatted.add(e);
            }
        }
        return formatted;
    }
    
    public List<Response<?>> execGetResponse() {
        this.client.exec();
        this.client.getAll(1);
        this.inTransaction = false;
        final List<Object> unformatted = this.client.getObjectMultiBulkReply();
        if (unformatted == null) {
            return null;
        }
        final List<Response<?>> response = new ArrayList<Response<?>>();
        for (final Object o : unformatted) {
            response.add(this.generateResponse(o));
        }
        return response;
    }
    
    public String discard() {
        this.client.discard();
        this.client.getAll(1);
        this.inTransaction = false;
        this.clean();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public void close() throws IOException {
        this.clear();
    }
}
