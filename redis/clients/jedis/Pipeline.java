package redis.clients.jedis;

import java.io.IOException;
import java.util.Collections;
import redis.clients.jedis.exceptions.JedisDataException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.Closeable;

public class Pipeline extends MultiKeyPipelineBase implements Closeable
{
    private MultiResponseBuilder currentMulti;
    
    @Override
    protected <T> Response<T> getResponse(final Builder<T> builder) {
        if (this.currentMulti != null) {
            super.getResponse(BuilderFactory.STRING);
            final Response<T> lr = new Response<T>(builder);
            this.currentMulti.addResponse(lr);
            return lr;
        }
        return super.getResponse(builder);
    }
    
    public void setClient(final Client client) {
        this.client = client;
    }
    
    @Override
    protected Client getClient(final byte[] key) {
        return this.client;
    }
    
    @Override
    protected Client getClient(final String key) {
        return this.client;
    }
    
    public void clear() {
        if (this.isInMulti()) {
            this.discard();
        }
        this.sync();
    }
    
    public boolean isInMulti() {
        return this.currentMulti != null;
    }
    
    public void sync() {
        if (this.getPipelinedResponseLength() > 0) {
            final List<Object> unformatted = this.client.getAll();
            for (final Object o : unformatted) {
                this.generateResponse(o);
            }
        }
    }
    
    public List<Object> syncAndReturnAll() {
        if (this.getPipelinedResponseLength() > 0) {
            final List<Object> unformatted = this.client.getAll();
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
        return Collections.emptyList();
    }
    
    public Response<String> discard() {
        if (this.currentMulti == null) {
            throw new JedisDataException("DISCARD without MULTI");
        }
        this.client.discard();
        this.currentMulti = null;
        return this.getResponse(BuilderFactory.STRING);
    }
    
    public Response<List<Object>> exec() {
        if (this.currentMulti == null) {
            throw new JedisDataException("EXEC without MULTI");
        }
        this.client.exec();
        final Response<List<Object>> response = super.getResponse((Builder<List<Object>>)this.currentMulti);
        this.currentMulti.setResponseDependency(response);
        this.currentMulti = null;
        return response;
    }
    
    public Response<String> multi() {
        if (this.currentMulti != null) {
            throw new JedisDataException("MULTI calls can not be nested");
        }
        this.client.multi();
        final Response<String> response = this.getResponse(BuilderFactory.STRING);
        this.currentMulti = new MultiResponseBuilder();
        return response;
    }
    
    @Override
    public void close() throws IOException {
        this.clear();
    }
    
    private class MultiResponseBuilder extends Builder<List<Object>>
    {
        private List<Response<?>> responses;
        
        private MultiResponseBuilder() {
            this.responses = new ArrayList<Response<?>>();
        }
        
        @Override
        public List<Object> build(final Object data) {
            final List<Object> list = (List<Object>)data;
            final List<Object> values = new ArrayList<Object>();
            if (list.size() != this.responses.size()) {
                throw new JedisDataException("Expected data size " + this.responses.size() + " but was " + list.size());
            }
            for (int i = 0; i < list.size(); ++i) {
                final Response<?> response = this.responses.get(i);
                response.set(list.get(i));
                Object builtResponse;
                try {
                    builtResponse = response.get();
                }
                catch (final JedisDataException e) {
                    builtResponse = e;
                }
                values.add(builtResponse);
            }
            return values;
        }
        
        public void setResponseDependency(final Response<?> dependency) {
            for (final Response<?> response : this.responses) {
                response.setDependency(dependency);
            }
        }
        
        public void addResponse(final Response<?> response) {
            this.responses.add(response);
        }
    }
}
