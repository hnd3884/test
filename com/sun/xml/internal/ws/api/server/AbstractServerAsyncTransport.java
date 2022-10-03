package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.ws.util.Pool;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.Nullable;
import java.io.IOException;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;

public abstract class AbstractServerAsyncTransport<T>
{
    private final WSEndpoint endpoint;
    private final CodecPool codecPool;
    
    public AbstractServerAsyncTransport(final WSEndpoint endpoint) {
        this.endpoint = endpoint;
        this.codecPool = new CodecPool(endpoint);
    }
    
    protected Packet decodePacket(final T connection, @NotNull final Codec codec) throws IOException {
        final Packet packet = new Packet();
        packet.acceptableMimeTypes = this.getAcceptableMimeTypes(connection);
        packet.addSatellite(this.getPropertySet(connection));
        packet.transportBackChannel = this.getTransportBackChannel(connection);
        return packet;
    }
    
    protected abstract void encodePacket(final T p0, @NotNull final Packet p1, @NotNull final Codec p2) throws IOException;
    
    @Nullable
    protected abstract String getAcceptableMimeTypes(final T p0);
    
    @Nullable
    protected abstract TransportBackChannel getTransportBackChannel(final T p0);
    
    @NotNull
    protected abstract PropertySet getPropertySet(final T p0);
    
    @NotNull
    protected abstract WebServiceContextDelegate getWebServiceContextDelegate(final T p0);
    
    protected void handle(final T connection) throws IOException {
        final Codec codec = this.codecPool.take();
        final Packet request = this.decodePacket(connection, codec);
        if (!request.getMessage().isFault()) {
            this.endpoint.schedule(request, new WSEndpoint.CompletionCallback() {
                @Override
                public void onCompletion(@NotNull final Packet response) {
                    try {
                        AbstractServerAsyncTransport.this.encodePacket(connection, response, codec);
                    }
                    catch (final IOException ioe) {
                        ioe.printStackTrace();
                    }
                    AbstractServerAsyncTransport.this.codecPool.recycle(codec);
                }
            });
        }
    }
    
    private static final class CodecPool extends Pool<Codec>
    {
        WSEndpoint endpoint;
        
        CodecPool(final WSEndpoint endpoint) {
            this.endpoint = endpoint;
        }
        
        @Override
        protected Codec create() {
            return this.endpoint.createCodec();
        }
    }
}
