package org.apache.tika.pipes;

import java.util.Objects;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.emitter.EmitKey;
import org.apache.tika.pipes.fetcher.FetchKey;
import java.io.Serializable;

public class FetchEmitTuple implements Serializable
{
    public static final ON_PARSE_EXCEPTION DEFAULT_ON_PARSE_EXCEPTION;
    private final String id;
    private final FetchKey fetchKey;
    private EmitKey emitKey;
    private final Metadata metadata;
    private final ON_PARSE_EXCEPTION onParseException;
    private HandlerConfig handlerConfig;
    
    public FetchEmitTuple(final String id, final FetchKey fetchKey, final EmitKey emitKey) {
        this(id, fetchKey, emitKey, new Metadata(), HandlerConfig.DEFAULT_HANDLER_CONFIG, FetchEmitTuple.DEFAULT_ON_PARSE_EXCEPTION);
    }
    
    public FetchEmitTuple(final String id, final FetchKey fetchKey, final EmitKey emitKey, final ON_PARSE_EXCEPTION onParseException) {
        this(id, fetchKey, emitKey, new Metadata(), HandlerConfig.DEFAULT_HANDLER_CONFIG, onParseException);
    }
    
    public FetchEmitTuple(final String id, final FetchKey fetchKey, final EmitKey emitKey, final Metadata metadata) {
        this(id, fetchKey, emitKey, metadata, HandlerConfig.DEFAULT_HANDLER_CONFIG, FetchEmitTuple.DEFAULT_ON_PARSE_EXCEPTION);
    }
    
    public FetchEmitTuple(final String id, final FetchKey fetchKey, final EmitKey emitKey, final Metadata metadata, final HandlerConfig handlerConfig, final ON_PARSE_EXCEPTION onParseException) {
        this.id = id;
        this.fetchKey = fetchKey;
        this.emitKey = emitKey;
        this.metadata = metadata;
        this.handlerConfig = handlerConfig;
        this.onParseException = onParseException;
    }
    
    public String getId() {
        return this.id;
    }
    
    public FetchKey getFetchKey() {
        return this.fetchKey;
    }
    
    public EmitKey getEmitKey() {
        return this.emitKey;
    }
    
    public Metadata getMetadata() {
        return this.metadata;
    }
    
    public ON_PARSE_EXCEPTION getOnParseException() {
        return this.onParseException;
    }
    
    public void setEmitKey(final EmitKey emitKey) {
        this.emitKey = emitKey;
    }
    
    public void setHandlerConfig(final HandlerConfig handlerConfig) {
        this.handlerConfig = handlerConfig;
    }
    
    public HandlerConfig getHandlerConfig() {
        return (this.handlerConfig == null) ? HandlerConfig.DEFAULT_HANDLER_CONFIG : this.handlerConfig;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final FetchEmitTuple that = (FetchEmitTuple)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.fetchKey, that.fetchKey) && Objects.equals(this.emitKey, that.emitKey) && Objects.equals(this.metadata, that.metadata) && this.onParseException == that.onParseException && Objects.equals(this.handlerConfig, that.handlerConfig);
    }
    
    @Override
    public int hashCode() {
        int result = (this.id != null) ? this.id.hashCode() : 0;
        result = 31 * result + ((this.fetchKey != null) ? this.fetchKey.hashCode() : 0);
        result = 31 * result + ((this.emitKey != null) ? this.emitKey.hashCode() : 0);
        result = 31 * result + ((this.metadata != null) ? this.metadata.hashCode() : 0);
        result = 31 * result + ((this.onParseException != null) ? this.onParseException.hashCode() : 0);
        result = 31 * result + ((this.handlerConfig != null) ? this.handlerConfig.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "FetchEmitTuple{id='" + this.id + '\'' + ", fetchKey=" + this.fetchKey + ", emitKey=" + this.emitKey + ", metadata=" + this.metadata + ", onParseException=" + this.onParseException + ", handlerConfig=" + this.handlerConfig + '}';
    }
    
    static {
        DEFAULT_ON_PARSE_EXCEPTION = ON_PARSE_EXCEPTION.EMIT;
    }
    
    public enum ON_PARSE_EXCEPTION
    {
        SKIP, 
        EMIT;
    }
}
