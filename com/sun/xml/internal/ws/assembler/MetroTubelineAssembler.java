package com.sun.xml.internal.ws.assembler;

import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import java.util.Iterator;
import java.util.Collection;
import com.sun.xml.internal.ws.dump.LoggingDumpTube;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyDecorator;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;

public class MetroTubelineAssembler implements TubelineAssembler
{
    private static final String COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE = "com.sun.metro.soap.dump";
    public static final MetroConfigNameImpl JAXWS_TUBES_CONFIG_NAMES;
    private static final Logger LOGGER;
    private final BindingID bindingId;
    private final TubelineAssemblyController tubelineAssemblyController;
    
    public MetroTubelineAssembler(final BindingID bindingId, final MetroConfigName metroConfigName) {
        this.bindingId = bindingId;
        this.tubelineAssemblyController = new TubelineAssemblyController(metroConfigName);
    }
    
    TubelineAssemblyController getTubelineAssemblyController() {
        return this.tubelineAssemblyController;
    }
    
    @NotNull
    @Override
    public Tube createClient(@NotNull final ClientTubeAssemblerContext jaxwsContext) {
        if (MetroTubelineAssembler.LOGGER.isLoggable(Level.FINER)) {
            MetroTubelineAssembler.LOGGER.finer("Assembling client-side tubeline for WS endpoint: " + jaxwsContext.getAddress().getURI().toString());
        }
        final DefaultClientTubelineAssemblyContext context = this.createClientContext(jaxwsContext);
        final Collection<TubeCreator> tubeCreators = this.tubelineAssemblyController.getTubeCreators(context);
        for (final TubeCreator tubeCreator : tubeCreators) {
            tubeCreator.updateContext(context);
        }
        final TubelineAssemblyDecorator decorator = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, context.getContainer()));
        boolean first = true;
        for (final TubeCreator tubeCreator2 : tubeCreators) {
            final MessageDumpingInfo msgDumpInfo = this.setupMessageDumping(tubeCreator2.getMessageDumpPropertyBase(), Side.Client);
            final Tube oldTubelineHead = context.getTubelineHead();
            LoggingDumpTube afterDumpTube = null;
            if (msgDumpInfo.dumpAfter) {
                afterDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.After, context.getTubelineHead());
                context.setTubelineHead(afterDumpTube);
            }
            if (!context.setTubelineHead(decorator.decorateClient(tubeCreator2.createTube(context), context))) {
                if (afterDumpTube != null) {
                    context.setTubelineHead(oldTubelineHead);
                }
            }
            else {
                final String loggedTubeName = context.getTubelineHead().getClass().getName();
                if (afterDumpTube != null) {
                    afterDumpTube.setLoggedTubeName(loggedTubeName);
                }
                if (msgDumpInfo.dumpBefore) {
                    final LoggingDumpTube beforeDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.Before, context.getTubelineHead());
                    beforeDumpTube.setLoggedTubeName(loggedTubeName);
                    context.setTubelineHead(beforeDumpTube);
                }
            }
            if (first) {
                context.setTubelineHead(decorator.decorateClientTail(context.getTubelineHead(), context));
                first = false;
            }
        }
        return decorator.decorateClientHead(context.getTubelineHead(), context);
    }
    
    @NotNull
    @Override
    public Tube createServer(@NotNull final ServerTubeAssemblerContext jaxwsContext) {
        if (MetroTubelineAssembler.LOGGER.isLoggable(Level.FINER)) {
            MetroTubelineAssembler.LOGGER.finer("Assembling endpoint tubeline for WS endpoint: " + jaxwsContext.getEndpoint().getServiceName() + "::" + jaxwsContext.getEndpoint().getPortName());
        }
        final DefaultServerTubelineAssemblyContext context = this.createServerContext(jaxwsContext);
        final Collection<TubeCreator> tubeCreators = this.tubelineAssemblyController.getTubeCreators(context);
        for (final TubeCreator tubeCreator : tubeCreators) {
            tubeCreator.updateContext(context);
        }
        final TubelineAssemblyDecorator decorator = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, context.getEndpoint().getContainer()));
        boolean first = true;
        for (final TubeCreator tubeCreator2 : tubeCreators) {
            final MessageDumpingInfo msgDumpInfo = this.setupMessageDumping(tubeCreator2.getMessageDumpPropertyBase(), Side.Endpoint);
            final Tube oldTubelineHead = context.getTubelineHead();
            LoggingDumpTube afterDumpTube = null;
            if (msgDumpInfo.dumpAfter) {
                afterDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.After, context.getTubelineHead());
                context.setTubelineHead(afterDumpTube);
            }
            if (!context.setTubelineHead(decorator.decorateServer(tubeCreator2.createTube(context), context))) {
                if (afterDumpTube != null) {
                    context.setTubelineHead(oldTubelineHead);
                }
            }
            else {
                final String loggedTubeName = context.getTubelineHead().getClass().getName();
                if (afterDumpTube != null) {
                    afterDumpTube.setLoggedTubeName(loggedTubeName);
                }
                if (msgDumpInfo.dumpBefore) {
                    final LoggingDumpTube beforeDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.Before, context.getTubelineHead());
                    beforeDumpTube.setLoggedTubeName(loggedTubeName);
                    context.setTubelineHead(beforeDumpTube);
                }
            }
            if (first) {
                context.setTubelineHead(decorator.decorateServerTail(context.getTubelineHead(), context));
                first = false;
            }
        }
        return decorator.decorateServerHead(context.getTubelineHead(), context);
    }
    
    private MessageDumpingInfo setupMessageDumping(String msgDumpSystemPropertyBase, final Side side) {
        boolean dumpBefore = false;
        boolean dumpAfter = false;
        Level logLevel = Level.INFO;
        Boolean value = this.getBooleanValue("com.sun.metro.soap.dump");
        if (value != null) {
            dumpBefore = value;
            dumpAfter = value;
        }
        value = this.getBooleanValue("com.sun.metro.soap.dump.before");
        dumpBefore = ((value != null) ? value : dumpBefore);
        value = this.getBooleanValue("com.sun.metro.soap.dump.after");
        dumpAfter = ((value != null) ? value : dumpAfter);
        Level levelValue = this.getLevelValue("com.sun.metro.soap.dump.level");
        if (levelValue != null) {
            logLevel = levelValue;
        }
        value = this.getBooleanValue("com.sun.metro.soap.dump." + side.toString());
        if (value != null) {
            dumpBefore = value;
            dumpAfter = value;
        }
        value = this.getBooleanValue("com.sun.metro.soap.dump." + side.toString() + ".before");
        dumpBefore = ((value != null) ? value : dumpBefore);
        value = this.getBooleanValue("com.sun.metro.soap.dump." + side.toString() + ".after");
        dumpAfter = ((value != null) ? value : dumpAfter);
        levelValue = this.getLevelValue("com.sun.metro.soap.dump." + side.toString() + ".level");
        if (levelValue != null) {
            logLevel = levelValue;
        }
        value = this.getBooleanValue(msgDumpSystemPropertyBase);
        if (value != null) {
            dumpBefore = value;
            dumpAfter = value;
        }
        value = this.getBooleanValue(msgDumpSystemPropertyBase + ".before");
        dumpBefore = ((value != null) ? value : dumpBefore);
        value = this.getBooleanValue(msgDumpSystemPropertyBase + ".after");
        dumpAfter = ((value != null) ? value : dumpAfter);
        levelValue = this.getLevelValue(msgDumpSystemPropertyBase + ".level");
        if (levelValue != null) {
            logLevel = levelValue;
        }
        msgDumpSystemPropertyBase = msgDumpSystemPropertyBase + "." + side.toString();
        value = this.getBooleanValue(msgDumpSystemPropertyBase);
        if (value != null) {
            dumpBefore = value;
            dumpAfter = value;
        }
        value = this.getBooleanValue(msgDumpSystemPropertyBase + ".before");
        dumpBefore = ((value != null) ? value : dumpBefore);
        value = this.getBooleanValue(msgDumpSystemPropertyBase + ".after");
        dumpAfter = ((value != null) ? value : dumpAfter);
        levelValue = this.getLevelValue(msgDumpSystemPropertyBase + ".level");
        if (levelValue != null) {
            logLevel = levelValue;
        }
        return new MessageDumpingInfo(dumpBefore, dumpAfter, logLevel);
    }
    
    private Boolean getBooleanValue(final String propertyName) {
        Boolean retVal = null;
        final String stringValue = System.getProperty(propertyName);
        if (stringValue != null) {
            retVal = Boolean.valueOf(stringValue);
            MetroTubelineAssembler.LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(propertyName, retVal));
        }
        return retVal;
    }
    
    private Level getLevelValue(final String propertyName) {
        Level retVal = null;
        final String stringValue = System.getProperty(propertyName);
        if (stringValue != null) {
            MetroTubelineAssembler.LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(propertyName, stringValue));
            try {
                retVal = Level.parse(stringValue);
            }
            catch (final IllegalArgumentException ex) {
                MetroTubelineAssembler.LOGGER.warning(TubelineassemblyMessages.MASM_0019_MSG_LOGGING_SYSTEM_PROPERTY_ILLEGAL_VALUE(propertyName, stringValue), ex);
            }
        }
        return retVal;
    }
    
    protected DefaultServerTubelineAssemblyContext createServerContext(final ServerTubeAssemblerContext jaxwsContext) {
        return new DefaultServerTubelineAssemblyContext(jaxwsContext);
    }
    
    protected DefaultClientTubelineAssemblyContext createClientContext(final ClientTubeAssemblerContext jaxwsContext) {
        return new DefaultClientTubelineAssemblyContext(jaxwsContext);
    }
    
    static {
        JAXWS_TUBES_CONFIG_NAMES = new MetroConfigNameImpl("jaxws-tubes-default.xml", "jaxws-tubes.xml");
        LOGGER = Logger.getLogger(MetroTubelineAssembler.class);
    }
    
    private enum Side
    {
        Client("client"), 
        Endpoint("endpoint");
        
        private final String name;
        
        private Side(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    private static class MessageDumpingInfo
    {
        final boolean dumpBefore;
        final boolean dumpAfter;
        final Level logLevel;
        
        MessageDumpingInfo(final boolean dumpBefore, final boolean dumpAfter, final Level logLevel) {
            this.dumpBefore = dumpBefore;
            this.dumpAfter = dumpAfter;
            this.logLevel = logLevel;
        }
    }
}
