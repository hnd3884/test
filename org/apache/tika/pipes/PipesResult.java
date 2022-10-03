package org.apache.tika.pipes;

import org.apache.tika.pipes.emitter.EmitData;

public class PipesResult
{
    public static PipesResult CLIENT_UNAVAILABLE_WITHIN_MS;
    public static PipesResult TIMEOUT;
    public static PipesResult OOM;
    public static PipesResult UNSPECIFIED_CRASH;
    public static PipesResult EMIT_SUCCESS;
    public static PipesResult INTERRUPTED_EXCEPTION;
    public static PipesResult EMPTY_OUTPUT;
    private final STATUS status;
    private final EmitData emitData;
    private final String message;
    
    private PipesResult(final STATUS status, final EmitData emitData, final String message) {
        this.status = status;
        this.emitData = emitData;
        this.message = message;
    }
    
    public PipesResult(final STATUS status) {
        this(status, null, null);
    }
    
    public PipesResult(final STATUS status, final String message) {
        this(status, null, message);
    }
    
    public PipesResult(final EmitData emitData) {
        this(STATUS.PARSE_SUCCESS, emitData, null);
    }
    
    public PipesResult(final EmitData emitData, final String message) {
        this(STATUS.PARSE_SUCCESS_WITH_EXCEPTION, emitData, message);
    }
    
    public STATUS getStatus() {
        return this.status;
    }
    
    public EmitData getEmitData() {
        return this.emitData;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public String toString() {
        return "PipesResult{status=" + this.status + ", emitData=" + this.emitData + ", message='" + this.message + '\'' + '}';
    }
    
    static {
        PipesResult.CLIENT_UNAVAILABLE_WITHIN_MS = new PipesResult(STATUS.CLIENT_UNAVAILABLE_WITHIN_MS);
        PipesResult.TIMEOUT = new PipesResult(STATUS.TIMEOUT);
        PipesResult.OOM = new PipesResult(STATUS.OOM);
        PipesResult.UNSPECIFIED_CRASH = new PipesResult(STATUS.UNSPECIFIED_CRASH);
        PipesResult.EMIT_SUCCESS = new PipesResult(STATUS.EMIT_SUCCESS);
        PipesResult.INTERRUPTED_EXCEPTION = new PipesResult(STATUS.INTERRUPTED_EXCEPTION);
        PipesResult.EMPTY_OUTPUT = new PipesResult(STATUS.EMPTY_OUTPUT);
    }
    
    public enum STATUS
    {
        CLIENT_UNAVAILABLE_WITHIN_MS, 
        FETCHER_INITIALIZATION_EXCEPTION, 
        FETCH_EXCEPTION, 
        EMPTY_OUTPUT, 
        PARSE_EXCEPTION_NO_EMIT, 
        PARSE_EXCEPTION_EMIT, 
        PARSE_SUCCESS, 
        PARSE_SUCCESS_WITH_EXCEPTION, 
        OOM, 
        TIMEOUT, 
        UNSPECIFIED_CRASH, 
        NO_EMITTER_FOUND, 
        EMIT_SUCCESS, 
        EMIT_SUCCESS_PARSE_EXCEPTION, 
        EMIT_EXCEPTION, 
        INTERRUPTED_EXCEPTION, 
        NO_FETCHER_FOUND;
    }
}
