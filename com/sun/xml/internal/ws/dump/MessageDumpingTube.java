package com.sun.xml.internal.ws.dump;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.commons.xmlutil.Converter;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.Tube;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;

final class MessageDumpingTube extends AbstractFilterTubeImpl
{
    static final String DEFAULT_MSGDUMP_LOGGING_ROOT = "com.sun.xml.internal.ws.messagedump";
    private static final AtomicInteger ID_GENERATOR;
    private final MessageDumper messageDumper;
    private final int tubeId;
    private final MessageDumpingFeature messageDumpingFeature;
    
    MessageDumpingTube(final Tube next, final MessageDumpingFeature feature) {
        super(next);
        this.messageDumpingFeature = feature;
        this.tubeId = MessageDumpingTube.ID_GENERATOR.incrementAndGet();
        this.messageDumper = new MessageDumper("MesageDumpingTube", Logger.getLogger(feature.getMessageLoggingRoot()), feature.getMessageLoggingLevel());
    }
    
    MessageDumpingTube(final MessageDumpingTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.messageDumpingFeature = that.messageDumpingFeature;
        this.tubeId = MessageDumpingTube.ID_GENERATOR.incrementAndGet();
        this.messageDumper = that.messageDumper;
    }
    
    @Override
    public MessageDumpingTube copy(final TubeCloner cloner) {
        return new MessageDumpingTube(this, cloner);
    }
    
    @Override
    public NextAction processRequest(final Packet request) {
        this.dump(MessageDumper.MessageType.Request, Converter.toString(request), Fiber.current().owner.id);
        return super.processRequest(request);
    }
    
    @Override
    public NextAction processResponse(final Packet response) {
        this.dump(MessageDumper.MessageType.Response, Converter.toString(response), Fiber.current().owner.id);
        return super.processResponse(response);
    }
    
    @Override
    public NextAction processException(final Throwable t) {
        this.dump(MessageDumper.MessageType.Exception, Converter.toString(t), Fiber.current().owner.id);
        return super.processException(t);
    }
    
    protected final void dump(final MessageDumper.MessageType messageType, final String message, final String engineId) {
        String logMessage;
        if (this.messageDumpingFeature.getMessageLoggingStatus()) {
            this.messageDumper.setLoggingLevel(this.messageDumpingFeature.getMessageLoggingLevel());
            logMessage = this.messageDumper.dump(messageType, MessageDumper.ProcessingState.Received, message, this.tubeId, engineId);
        }
        else {
            logMessage = this.messageDumper.createLogMessage(messageType, MessageDumper.ProcessingState.Received, this.tubeId, engineId, message);
        }
        this.messageDumpingFeature.offerMessage(logMessage);
    }
    
    static {
        ID_GENERATOR = new AtomicInteger(0);
    }
}
