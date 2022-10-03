package com.sun.xml.internal.ws.api.pipe;

import java.util.Iterator;
import com.sun.xml.internal.ws.util.pipe.StandalonePipeAssembler;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.api.BindingID;
import java.util.logging.Logger;

public abstract class PipelineAssemblerFactory
{
    private static final Logger logger;
    
    public abstract PipelineAssembler doCreate(final BindingID p0);
    
    public static PipelineAssembler create(final ClassLoader classLoader, final BindingID bindingId) {
        for (final PipelineAssemblerFactory factory : ServiceFinder.find(PipelineAssemblerFactory.class, classLoader)) {
            final PipelineAssembler assembler = factory.doCreate(bindingId);
            if (assembler != null) {
                PipelineAssemblerFactory.logger.fine(factory.getClass() + " successfully created " + assembler);
                return assembler;
            }
        }
        return new StandalonePipeAssembler();
    }
    
    static {
        logger = Logger.getLogger(PipelineAssemblerFactory.class.getName());
    }
}
