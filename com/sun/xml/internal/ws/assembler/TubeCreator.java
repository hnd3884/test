package com.sun.xml.internal.ws.assembler;

import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyContextUpdater;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import com.sun.istack.internal.logging.Logger;

final class TubeCreator
{
    private static final Logger LOGGER;
    private final TubeFactory factory;
    private final String msgDumpPropertyBase;
    
    TubeCreator(final TubeFactoryConfig config, final ClassLoader tubeFactoryClassLoader) {
        final String className = config.getClassName();
        try {
            Class<?> factoryClass;
            if (this.isJDKInternal(className)) {
                factoryClass = Class.forName(className, true, null);
            }
            else {
                factoryClass = Class.forName(className, true, tubeFactoryClassLoader);
            }
            if (!TubeFactory.class.isAssignableFrom(factoryClass)) {
                throw new RuntimeException(TubelineassemblyMessages.MASM_0015_CLASS_DOES_NOT_IMPLEMENT_INTERFACE(factoryClass.getName(), TubeFactory.class.getName()));
            }
            final Class<TubeFactory> typedClass = (Class<TubeFactory>)factoryClass;
            this.factory = typedClass.newInstance();
            this.msgDumpPropertyBase = this.factory.getClass().getName() + ".dump";
        }
        catch (final InstantiationException ex) {
            throw TubeCreator.LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(className), ex), true);
        }
        catch (final IllegalAccessException ex2) {
            throw TubeCreator.LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(className), ex2), true);
        }
        catch (final ClassNotFoundException ex3) {
            throw TubeCreator.LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0017_UNABLE_TO_LOAD_TUBE_FACTORY_CLASS(className), ex3), true);
        }
    }
    
    Tube createTube(final DefaultClientTubelineAssemblyContext context) {
        return this.factory.createTube(context);
    }
    
    Tube createTube(final DefaultServerTubelineAssemblyContext context) {
        return this.factory.createTube(context);
    }
    
    void updateContext(final ClientTubelineAssemblyContext context) {
        if (this.factory instanceof TubelineAssemblyContextUpdater) {
            ((TubelineAssemblyContextUpdater)this.factory).prepareContext(context);
        }
    }
    
    void updateContext(final DefaultServerTubelineAssemblyContext context) {
        if (this.factory instanceof TubelineAssemblyContextUpdater) {
            ((TubelineAssemblyContextUpdater)this.factory).prepareContext(context);
        }
    }
    
    String getMessageDumpPropertyBase() {
        return this.msgDumpPropertyBase;
    }
    
    private boolean isJDKInternal(final String className) {
        return className.startsWith("com.sun.xml.internal.ws");
    }
    
    static {
        LOGGER = Logger.getLogger(TubeCreator.class);
    }
}
