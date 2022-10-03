package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.message.internal.TracingLogger;

public enum ServerTraceEvent implements TracingLogger.Event
{
    START(TracingLogger.Level.SUMMARY, "START", (String)null), 
    START_HEADERS(TracingLogger.Level.VERBOSE, "START", (String)null), 
    PRE_MATCH(TracingLogger.Level.TRACE, "PRE-MATCH", "Filter by %s"), 
    PRE_MATCH_SUMMARY(TracingLogger.Level.SUMMARY, "PRE-MATCH", "PreMatchRequest summary: %s filters"), 
    MATCH_PATH_FIND(TracingLogger.Level.TRACE, "MATCH", "Matching path [%s]"), 
    MATCH_PATH_NOT_MATCHED(TracingLogger.Level.VERBOSE, "MATCH", "Pattern [%s] is NOT matched"), 
    MATCH_PATH_SELECTED(TracingLogger.Level.TRACE, "MATCH", "Pattern [%s] IS selected"), 
    MATCH_PATH_SKIPPED(TracingLogger.Level.VERBOSE, "MATCH", "Pattern [%s] is skipped"), 
    MATCH_LOCATOR(TracingLogger.Level.TRACE, "MATCH", "Matched locator : %s"), 
    MATCH_RESOURCE_METHOD(TracingLogger.Level.TRACE, "MATCH", "Matched method  : %s"), 
    MATCH_RUNTIME_RESOURCE(TracingLogger.Level.TRACE, "MATCH", "Matched resource: template=[%s] regexp=[%s] matches=[%s] from=[%s]"), 
    MATCH_RESOURCE(TracingLogger.Level.TRACE, "MATCH", "Resource instance: %s"), 
    MATCH_SUMMARY(TracingLogger.Level.SUMMARY, "MATCH", "RequestMatching summary"), 
    REQUEST_FILTER(TracingLogger.Level.TRACE, "REQ-FILTER", "Filter by %s"), 
    REQUEST_FILTER_SUMMARY(TracingLogger.Level.SUMMARY, "REQ-FILTER", "Request summary: %s filters"), 
    METHOD_INVOKE(TracingLogger.Level.SUMMARY, "INVOKE", "Resource %s method=[%s]"), 
    DISPATCH_RESPONSE(TracingLogger.Level.TRACE, "INVOKE", "Response: %s"), 
    RESPONSE_FILTER(TracingLogger.Level.TRACE, "RESP-FILTER", "Filter by %s"), 
    RESPONSE_FILTER_SUMMARY(TracingLogger.Level.SUMMARY, "RESP-FILTER", "Response summary: %s filters"), 
    FINISHED(TracingLogger.Level.SUMMARY, "FINISHED", "Response status: %s"), 
    EXCEPTION_MAPPING(TracingLogger.Level.SUMMARY, "EXCEPTION", "Exception mapper %s maps %s ('%s') to <%s>");
    
    private final TracingLogger.Level level;
    private final String category;
    private final String messageFormat;
    
    private ServerTraceEvent(final TracingLogger.Level level, final String category, final String messageFormat) {
        this.level = level;
        this.category = category;
        this.messageFormat = messageFormat;
    }
    
    public String category() {
        return this.category;
    }
    
    public TracingLogger.Level level() {
        return this.level;
    }
    
    public String messageFormat() {
        return this.messageFormat;
    }
}
