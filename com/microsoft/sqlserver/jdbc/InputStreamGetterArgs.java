package com.microsoft.sqlserver.jdbc;

final class InputStreamGetterArgs
{
    final StreamType streamType;
    final boolean isAdaptive;
    final boolean isStreaming;
    final String logContext;
    static final InputStreamGetterArgs defaultArgs;
    
    static final InputStreamGetterArgs getDefaultArgs() {
        return InputStreamGetterArgs.defaultArgs;
    }
    
    InputStreamGetterArgs(final StreamType streamType, final boolean isAdaptive, final boolean isStreaming, final String logContext) {
        this.streamType = streamType;
        this.isAdaptive = isAdaptive;
        this.isStreaming = isStreaming;
        this.logContext = logContext;
    }
    
    static {
        defaultArgs = new InputStreamGetterArgs(StreamType.NONE, false, false, "");
    }
}
