package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe;
import javax.xml.ws.WebServiceException;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import java.util.logging.Logger;

public abstract class TransportTubeFactory
{
    private static final TransportTubeFactory DEFAULT;
    private static final Logger logger;
    
    public abstract Tube doCreate(@NotNull final ClientTubeAssemblerContext p0);
    
    public static Tube create(@Nullable final ClassLoader classLoader, @NotNull final ClientTubeAssemblerContext context) {
        for (final TransportTubeFactory factory : ServiceFinder.find(TransportTubeFactory.class, classLoader, context.getContainer())) {
            final Tube tube = factory.doCreate(context);
            if (tube != null) {
                if (TransportTubeFactory.logger.isLoggable(Level.FINE)) {
                    TransportTubeFactory.logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { factory.getClass(), tube });
                }
                return tube;
            }
        }
        final ClientPipeAssemblerContext ctxt = new ClientPipeAssemblerContext(context.getAddress(), context.getWsdlModel(), context.getService(), context.getBinding(), context.getContainer());
        ctxt.setCodec(context.getCodec());
        for (final TransportPipeFactory factory2 : ServiceFinder.find(TransportPipeFactory.class, classLoader)) {
            final Pipe pipe = factory2.doCreate(ctxt);
            if (pipe != null) {
                if (TransportTubeFactory.logger.isLoggable(Level.FINE)) {
                    TransportTubeFactory.logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { factory2.getClass(), pipe });
                }
                return PipeAdapter.adapt(pipe);
            }
        }
        return TransportTubeFactory.DEFAULT.createDefault(ctxt);
    }
    
    protected Tube createDefault(final ClientTubeAssemblerContext context) {
        final String scheme = context.getAddress().getURI().getScheme();
        if (scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            return this.createHttpTransport(context);
        }
        throw new WebServiceException("Unsupported endpoint address: " + context.getAddress());
    }
    
    protected Tube createHttpTransport(final ClientTubeAssemblerContext context) {
        return new HttpTransportPipe(context.getCodec(), context.getBinding());
    }
    
    static {
        DEFAULT = new DefaultTransportTubeFactory();
        logger = Logger.getLogger(TransportTubeFactory.class.getName());
    }
    
    private static class DefaultTransportTubeFactory extends TransportTubeFactory
    {
        @Override
        public Tube doCreate(final ClientTubeAssemblerContext context) {
            return this.createDefault(context);
        }
    }
}
