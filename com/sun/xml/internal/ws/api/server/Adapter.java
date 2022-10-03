package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.config.management.Reconfigurable;

public abstract class Adapter<TK extends Toolkit> implements Reconfigurable, Component
{
    protected final WSEndpoint<?> endpoint;
    protected volatile Pool<TK> pool;
    
    protected Adapter(final WSEndpoint endpoint) {
        this.pool = new Pool<TK>() {
            @Override
            protected TK create() {
                return Adapter.this.createToolkit();
            }
        };
        assert endpoint != null;
        this.endpoint = endpoint;
        endpoint.getComponents().add(this.getEndpointComponent());
    }
    
    protected Component getEndpointComponent() {
        return new Component() {
            @Override
            public <S> S getSPI(final Class<S> spiType) {
                if (spiType.isAssignableFrom(Reconfigurable.class)) {
                    return spiType.cast(Adapter.this);
                }
                return null;
            }
        };
    }
    
    @Override
    public void reconfigure() {
        this.pool = new Pool<TK>() {
            @Override
            protected TK create() {
                return Adapter.this.createToolkit();
            }
        };
    }
    
    @Override
    public <S> S getSPI(final Class<S> spiType) {
        if (spiType.isAssignableFrom(Reconfigurable.class)) {
            return spiType.cast(this);
        }
        if (this.endpoint != null) {
            return this.endpoint.getSPI(spiType);
        }
        return null;
    }
    
    public WSEndpoint<?> getEndpoint() {
        return this.endpoint;
    }
    
    protected Pool<TK> getPool() {
        return this.pool;
    }
    
    protected abstract TK createToolkit();
    
    public class Toolkit
    {
        public final Codec codec;
        public final WSEndpoint.PipeHead head;
        
        public Toolkit() {
            this.codec = Adapter.this.endpoint.createCodec();
            this.head = Adapter.this.endpoint.createPipeHead();
        }
    }
}
