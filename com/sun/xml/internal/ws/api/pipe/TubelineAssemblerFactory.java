package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.istack.internal.NotNull;
import java.util.Iterator;
import com.sun.xml.internal.ws.assembler.MetroConfigName;
import com.sun.xml.internal.ws.assembler.MetroTubelineAssembler;
import java.util.logging.Level;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.BindingID;
import java.util.logging.Logger;

public abstract class TubelineAssemblerFactory
{
    private static final Logger logger;
    
    public abstract TubelineAssembler doCreate(final BindingID p0);
    
    @Deprecated
    public static TubelineAssembler create(final ClassLoader classLoader, final BindingID bindingId) {
        return create(classLoader, bindingId, null);
    }
    
    public static TubelineAssembler create(final ClassLoader classLoader, final BindingID bindingId, @Nullable final Container container) {
        if (container != null) {
            final TubelineAssemblerFactory taf = container.getSPI(TubelineAssemblerFactory.class);
            if (taf != null) {
                final TubelineAssembler a = taf.doCreate(bindingId);
                if (a != null) {
                    return a;
                }
            }
        }
        for (final TubelineAssemblerFactory factory : ServiceFinder.find(TubelineAssemblerFactory.class, classLoader)) {
            final TubelineAssembler assembler = factory.doCreate(bindingId);
            if (assembler != null) {
                TubelineAssemblerFactory.logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { factory.getClass(), assembler });
                return assembler;
            }
        }
        for (final PipelineAssemblerFactory factory2 : ServiceFinder.find(PipelineAssemblerFactory.class, classLoader)) {
            final PipelineAssembler assembler2 = factory2.doCreate(bindingId);
            if (assembler2 != null) {
                TubelineAssemblerFactory.logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { factory2.getClass(), assembler2 });
                return new TubelineAssemblerAdapter(assembler2);
            }
        }
        return new MetroTubelineAssembler(bindingId, MetroTubelineAssembler.JAXWS_TUBES_CONFIG_NAMES);
    }
    
    static {
        logger = Logger.getLogger(TubelineAssemblerFactory.class.getName());
    }
    
    private static class TubelineAssemblerAdapter implements TubelineAssembler
    {
        private PipelineAssembler assembler;
        
        TubelineAssemblerAdapter(final PipelineAssembler assembler) {
            this.assembler = assembler;
        }
        
        @NotNull
        @Override
        public Tube createClient(@NotNull final ClientTubeAssemblerContext context) {
            final ClientPipeAssemblerContext ctxt = new ClientPipeAssemblerContext(context.getAddress(), context.getWsdlModel(), context.getService(), context.getBinding(), context.getContainer());
            return PipeAdapter.adapt(this.assembler.createClient(ctxt));
        }
        
        @NotNull
        @Override
        public Tube createServer(@NotNull final ServerTubeAssemblerContext context) {
            if (!(context instanceof ServerPipeAssemblerContext)) {
                throw new IllegalArgumentException("{0} is not instance of ServerPipeAssemblerContext");
            }
            return PipeAdapter.adapt(this.assembler.createServer((ServerPipeAssemblerContext)context));
        }
    }
}
