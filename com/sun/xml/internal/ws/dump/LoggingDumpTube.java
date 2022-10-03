package com.sun.xml.internal.ws.dump;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.commons.xmlutil.Converter;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.Tube;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;

public class LoggingDumpTube extends AbstractFilterTubeImpl
{
    private static final AtomicInteger ID_GENERATOR;
    private MessageDumper messageDumper;
    private final Level loggingLevel;
    private final Position position;
    private final int tubeId;
    
    public LoggingDumpTube(final Level loggingLevel, final Position position, final Tube tubelineHead) {
        super(tubelineHead);
        this.position = position;
        this.loggingLevel = loggingLevel;
        this.tubeId = LoggingDumpTube.ID_GENERATOR.incrementAndGet();
    }
    
    public void setLoggedTubeName(final String loggedTubeName) {
        assert this.messageDumper == null;
        this.messageDumper = new MessageDumper(loggedTubeName, Logger.getLogger(loggedTubeName), this.loggingLevel);
    }
    
    private LoggingDumpTube(final LoggingDumpTube original, final TubeCloner cloner) {
        super(original, cloner);
        this.messageDumper = original.messageDumper;
        this.loggingLevel = original.loggingLevel;
        this.position = original.position;
        this.tubeId = LoggingDumpTube.ID_GENERATOR.incrementAndGet();
    }
    
    @Override
    public LoggingDumpTube copy(final TubeCloner cloner) {
        return new LoggingDumpTube(this, cloner);
    }
    
    @Override
    public NextAction processRequest(final Packet request) {
        if (this.messageDumper.isLoggable()) {
            final Packet dumpPacket = (request != null) ? request.copy(true) : null;
            this.messageDumper.dump(MessageDumper.MessageType.Request, this.position.requestState, Converter.toString(dumpPacket), this.tubeId, Fiber.current().owner.id);
        }
        return super.processRequest(request);
    }
    
    @Override
    public NextAction processResponse(final Packet response) {
        if (this.messageDumper.isLoggable()) {
            final Packet dumpPacket = (response != null) ? response.copy(true) : null;
            this.messageDumper.dump(MessageDumper.MessageType.Response, this.position.responseState, Converter.toString(dumpPacket), this.tubeId, Fiber.current().owner.id);
        }
        return super.processResponse(response);
    }
    
    @Override
    public NextAction processException(final Throwable t) {
        if (this.messageDumper.isLoggable()) {
            this.messageDumper.dump(MessageDumper.MessageType.Exception, this.position.responseState, Converter.toString(t), this.tubeId, Fiber.current().owner.id);
        }
        return super.processException(t);
    }
    
    @Override
    public void preDestroy() {
        super.preDestroy();
    }
    
    static {
        ID_GENERATOR = new AtomicInteger(0);
    }
    
    public enum Position
    {
        Before(MessageDumper.ProcessingState.Received, MessageDumper.ProcessingState.Processed), 
        After(MessageDumper.ProcessingState.Processed, MessageDumper.ProcessingState.Received);
        
        private final MessageDumper.ProcessingState requestState;
        private final MessageDumper.ProcessingState responseState;
        
        private Position(final MessageDumper.ProcessingState requestState, final MessageDumper.ProcessingState responseState) {
            this.requestState = requestState;
            this.responseState = responseState;
        }
    }
}
