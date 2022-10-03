package com.sun.xml.internal.ws.dump;

import java.util.logging.Level;
import java.util.logging.Logger;

final class MessageDumper
{
    private final String tubeName;
    private final Logger logger;
    private Level loggingLevel;
    
    public MessageDumper(final String tubeName, final Logger logger, final Level loggingLevel) {
        this.tubeName = tubeName;
        this.logger = logger;
        this.loggingLevel = loggingLevel;
    }
    
    final boolean isLoggable() {
        return this.logger.isLoggable(this.loggingLevel);
    }
    
    final void setLoggingLevel(final Level level) {
        this.loggingLevel = level;
    }
    
    final String createLogMessage(final MessageType messageType, final ProcessingState processingState, final int tubeId, final String engineId, final String message) {
        return String.format("%s %s in Tube [ %s ] Instance [ %d ] Engine [ %s ] Thread [ %s ]:%n%s", messageType, processingState, this.tubeName, tubeId, engineId, Thread.currentThread().getName(), message);
    }
    
    final String dump(final MessageType messageType, final ProcessingState processingState, final String message, final int tubeId, final String engineId) {
        final String logMessage = this.createLogMessage(messageType, processingState, tubeId, engineId, message);
        this.logger.log(this.loggingLevel, logMessage);
        return logMessage;
    }
    
    enum MessageType
    {
        Request("Request message"), 
        Response("Response message"), 
        Exception("Response exception");
        
        private final String name;
        
        private MessageType(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    enum ProcessingState
    {
        Received("received"), 
        Processed("processed");
        
        private final String name;
        
        private ProcessingState(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
